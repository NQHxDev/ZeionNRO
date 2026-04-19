use netstat2::*;
use std::collections::HashMap;
use std::sync::Arc;
use tokio::sync::mpsc;
use tokio::time::{self, Duration, Instant};
use tracing::{info, error};
use crate::config::AppConfig;
use crate::stats::StatsManager;
use crate::firewall::Firewall;
use crate::firewall::whitelist::WhitelistManager;

pub async fn run_monitor(
   config: AppConfig,
   stats: Arc<StatsManager>,
   firewall: Arc<dyn Firewall>,
   tx: mpsc::Sender<String>,
) {
   let mut interval = time::interval(Duration::from_secs(1));
   let af_flags = AddressFamilyFlags::IPV4 | AddressFamilyFlags::IPV6;
   let proto_flags = ProtocolFlags::TCP | ProtocolFlags::UDP;
   let whitelist = WhitelistManager::new();

   let mut last_counts: HashMap<String, usize> = HashMap::new();
   let mut last_live_view = Instant::now();

   info!("Monitor thread started. Tracking TCP & UDP on ports: {:?}", config.ports);

   loop {
      interval.tick().await;

      match get_sockets_info(af_flags, proto_flags) {
         Ok(sockets) => {
            let mut current_counts: HashMap<String, usize> = HashMap::new();
            let mut per_port_counts: HashMap<(String, u16), usize> = HashMap::new();

            for s in sockets {
               let (remote_ip, local_port) = match s.protocol_socket_info {
                  ProtocolSocketInfo::Tcp(tcp) => {
                     if tcp.state == TcpState::Listen || tcp.state == TcpState::Closed {
                        continue;
                     }
                     (tcp.remote_addr.to_string(), tcp.local_port)
                  }
                  ProtocolSocketInfo::Udp(_) => {
                     continue;
                  }
               };

               let remote_ip = crate::utils::normalize_ip(&remote_ip);

               if config.ports.contains(&local_port) {
                  *current_counts.entry(remote_ip.clone()).or_insert(0) += 1;
                  *per_port_counts.entry((remote_ip, local_port)).or_insert(0) += 1;
               }
            }

            if last_live_view.elapsed() >= Duration::from_secs(5) {
               let mut sorted_ips: Vec<_> = current_counts.iter().collect();
               sorted_ips.sort_by(|a, b| b.1.cmp(a.1));
               if !sorted_ips.is_empty() {
                  let top_ips: Vec<_> = sorted_ips.iter().take(3).map(|(ip, count)| format!("{}({})", ip, count)).collect();
                  info!(">> LIVE VIEW - Top Active IPs: {}", top_ips.join(", "));
               }
               last_live_view = Instant::now();
            }

            for (ip, total_count) in current_counts.iter() {
               if whitelist.is_whitelisted(ip, &config) { continue; }

               stats.update_connection_count(ip.clone(), *total_count);

               let mut stats_entry = stats.stats.get_mut(ip).unwrap();
               if stats_entry.is_blocked { continue; }

               let mut should_block = false;
               let mut reason = "";

               // Burst Check
               let last_count = last_counts.get(ip).unwrap_or(&0);
               if *total_count > *last_count {
                  let burst = total_count - last_count;
                  if burst > config.burst_threshold {
                     should_block = true;
                     reason = "Burst threshold exceeded";
                  }
               }

               // Per-Port Thresholds
               if !should_block {
                  for port in &config.ports {
                     let count_on_port = per_port_counts.get(&(ip.clone(), *port)).unwrap_or(&0);
                     let threshold = config.thresholds.get(port).unwrap_or(&config.default_threshold);

                     if *count_on_port > *threshold {
                        should_block = true;
                        reason = "Port threshold exceeded";
                        break;
                     }
                  }
               }

               if should_block {
                  info!("Blocking {}: {}. Connections: {}", ip, reason, total_count);
                  if let Err(e) = firewall.block(ip).await {
                     error!("Failed to block {}: {}", ip, e);
                  } else {
                     stats_entry.is_blocked = true;
                     let _ = tx.send(ip.clone()).await;
                  }
               }
            }

            last_counts = current_counts;
            stats.cleanup_stale(Duration::from_secs(600));
         }
         Err(e) => {
            error!("Failed to get sockets info: {}", e);
         }
      }
   }
}
