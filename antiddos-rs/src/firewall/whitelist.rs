use std::net::IpAddr;
use ipnet::IpNet;
use crate::config::AppConfig;

pub struct WhitelistManager {
   cloudflare_networks: Vec<IpNet>,
}

impl WhitelistManager {
   pub fn new() -> Self {
      // Hardcoded Cloudflare ranges
      let ranges = [
         // IPv4
         "173.245.48.0/20", "103.21.244.0/22", "103.22.200.0/22", "103.31.4.0/22",
         "141.101.64.0/18", "108.162.192.0/18", "190.93.240.0/20", "188.114.96.0/20",
         "197.234.240.0/22", "198.41.128.0/17", "162.158.0.0/15", "104.16.0.0/13",
         "104.24.0.0/14", "172.64.0.0/13", "131.0.72.0/22",
         // IPv6
         "2400:cb00::/32", "2606:4700::/32", "2803:f800::/32", "2405:b500::/32",
         "2405:8100::/32", "2a06:98c0::/29", "2c0f:f248::/32"
      ];

      let cloudflare_networks = ranges.iter()
         .filter_map(|r| r.parse::<IpNet>().ok())
         .collect();

      Self { cloudflare_networks }
   }

   pub fn is_whitelisted(&self, ip_str: &str, config: &AppConfig) -> bool {
      // 1. Check manual whitelist
      if config.whitelist.iter().any(|w| w == ip_str) {
         return true;
      }

      // 2. Check Cloudflare auto-whitelist
      if config.auto_whitelist_cloudflare {
         if let Ok(ip) = ip_str.parse::<IpAddr>() {
            for net in &self.cloudflare_networks {
               if net.contains(&ip) {
                  return true;
               }
            }
         }
      }

      false
   }
}
