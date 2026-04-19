use dashmap::DashSet;
use tokio::sync::Mutex;
use async_trait::async_trait;
use crate::firewall::Firewall;
use std::process::Command;
use tracing::{info};

pub struct WindowsFirewall {
   rule_name: String,
   blocked_ips: DashSet<String>,
   update_lock: Mutex<()>,
}

impl WindowsFirewall {
   pub fn new() -> Self {
      Self {
         rule_name: "ANTI_DDOS_PRO".to_string(),
         blocked_ips: DashSet::new(),
         update_lock: Mutex::new(()),
      }
   }

   async fn sync_firewall(&self) -> Result<(), Box<dyn std::error::Error>> {
      let _lock = self.update_lock.lock().await;

      let mut ips: Vec<String> = self.blocked_ips.iter().map(|ip| ip.clone()).collect();

      // We need at least one IP or netsh might fail or block everything
      if ips.is_empty() {
         ips.push("127.0.0.1".to_string());
      }

      let ip_list = ips.join(",");
      let status = Command::new("netsh")
         .args(["advfirewall", "firewall", "set", "rule",
                  &format!("name={}", self.rule_name),
                  "new", &format!("remoteip={}", ip_list)])
         .status()?;

      if status.success() {
         Ok(())
      } else {
         Err("Failed to sync Windows Firewall rule".into())
      }
   }
}

#[async_trait]
impl Firewall for WindowsFirewall {
   async fn init(&self) -> Result<(), Box<dyn std::error::Error>> {
      info!("Initializing Optimized Windows Firewall (netsh)...");

      // Reset old rule (Self-healing)
      let _ = self.cleanup().await;

      let status = Command::new("netsh")
         .args(["advfirewall", "firewall", "add", "rule",
                  &format!("name={}", self.rule_name),
                  "dir=in", "action=block", "remoteip=127.0.0.1", "enable=yes"])
         .status()?;

      if status.success() {
         Ok(())
      } else {
         Err("Failed to initialize Windows Firewall rule".into())
      }
   }

   async fn block(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>> {
      if self.blocked_ips.contains(ip) {
         return Ok(());
      }

      info!("Tagging IP for block on Windows: {}", ip);
      self.blocked_ips.insert(ip.to_string());

      let _ = Command::new("powershell")
         .args(["-Command", &format!("Get-NetTCPConnection -RemoteAddress {} | Remove-NetTCPConnection -Confirm:$false", ip)])
         .status();

      self.sync_firewall().await
   }

   async fn unblock(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>> {
      if !self.blocked_ips.contains(ip) {
         return Ok(());
      }

      info!("Removing IP from Windows block list: {}", ip);
      self.blocked_ips.remove(ip);
      self.sync_firewall().await
   }

   async fn cleanup(&self) -> Result<(), Box<dyn std::error::Error>> {
      info!("Cleaning up Windows Firewall rules ...");
      let _ = Command::new("netsh")
         .args(["advfirewall", "firewall", "delete", "rule", &format!("name={}", self.rule_name)])
         .status();
      self.blocked_ips.clear();
      Ok(())
   }
}
