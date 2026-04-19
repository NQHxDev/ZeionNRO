use async_trait::async_trait;
use crate::firewall::Firewall;
use std::process::Command;
use tracing::{info, warn};

pub struct LinuxFirewall;

impl LinuxFirewall {
   pub fn new() -> Self {
      Self
   }
}

#[async_trait]
impl Firewall for LinuxFirewall {
   async fn init(&self) -> Result<(), Box<dyn std::error::Error>> {
      info!("Initializing Linux Firewall ...");

      // Reset old rules (Self-healing)
      let _ = self.cleanup().await;

      // Create ipset
      let status = Command::new("sudo")
         .args(["ipset", "create", "ddos_blocks", "hash:ip", "timeout", "0"])
         .status()?;

      if !status.success() {
         warn!("Failed to create ipset ddos_blocks (it might already exist)");
      }

      // Add iptables rule to drop from ipset
      let status = Command::new("sudo")
         .args(["iptables", "-I", "INPUT", "-m", "set", "--match-set", "ddos_blocks", "src", "-j", "DROP"])
         .status()?;

      if status.success() {
         info!("Linux Firewall initialized with ipset 'ddos_blocks'");
         Ok(())
      } else {
         Err("Failed to add iptables rule".into())
      }
   }

   async fn block(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>> {
      info!("Blocking IP on Linux: {}", ip);
      let status = Command::new("sudo")
         .args(["ipset", "add", "ddos_blocks", ip])
         .status()?;

      if status.success() {
         let _ = Command::new("sudo").args(["conntrack", "-D", "-s", ip]).status();
         Ok(())
      } else {
         Ok(())
      }
   }

   async fn unblock(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>> {
      info!("Unblocking IP on Linux: {}", ip);
      let status = Command::new("sudo")
         .args(["ipset", "del", "ddos_blocks", ip])
         .status()?;

      if status.success() {
         Ok(())
      } else {
         Ok(())
      }
   }

   async fn cleanup(&self) -> Result<(), Box<dyn std::error::Error>> {
      info!("Cleaning up Linux Firewall rules ...");
      let _ = Command::new("sudo")
         .args(["iptables", "-D", "INPUT", "-m", "set", "--match-set", "ddos_blocks", "src", "-j", "DROP"])
         .status();
      let _ = Command::new("sudo")
         .args(["ipset", "destroy", "ddos_blocks"])
         .status();
      Ok(())
   }
}
