use async_trait::async_trait;
use crate::firewall::Firewall;
use std::process::Command;
use tracing::{info, warn};

pub struct MacFirewall;

impl MacFirewall {
   pub fn new() -> Self {
      Self
   }
}

#[async_trait]
impl Firewall for MacFirewall {
   async fn init(&self) -> Result<(), Box<dyn std::error::Error>> {
      info!("Initializing macOS Firewall ...");

      // Reset old rules first (Self - healing)
      let _ = self.cleanup().await;

      // Ensure PF is enabled
      let output = Command::new("sudo").arg("pfctl").arg("-e").output()?;
      if !output.status.success() {
         let stderr = String::from_utf8_lossy(&output.stderr);
         if !stderr.contains("already enabled") {
            warn!("Failed to enable PF: {}", stderr.trim());
         }
      }

      // Initialize anchor and table
      let config = "table <ddos_blocks> persist\nblock drop in quick from <ddos_blocks> to any\n";
      let mut child = Command::new("sudo")
         .args(["pfctl", "-a", "com.antiddos.pro", "-f", "-"])
         .stdin(std::process::Stdio::piped())
         .spawn()?;

      use std::io::Write;
      if let Some(mut stdin) = child.stdin.take() {
         stdin.write_all(config.as_bytes())?;
      }
      child.wait()?;

      info!("macOS Firewall initialized within anchor 'com.antiddos.pro'");
      Ok(())
   }

   async fn block(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>> {
      info!("Blocking IP on macOS: {}", ip);
      let status = Command::new("sudo")
         .args(["pfctl", "-a", "com.antiddos.pro", "-t", "ddos_blocks", "-T", "add", ip])
         .status()?;

      if status.success() {
         let _ = Command::new("sudo").args(["pfctl", "-k", ip]).status();
         let _ = Command::new("sudo").args(["pfctl", "-K", ip]).status();
         Ok(())
      } else {
         Err(format!("pfctl failed to block IP: {}", ip).into())
      }
   }

   async fn unblock(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>> {
      info!("Unblocking IP on macOS: {}", ip);
      let status = Command::new("sudo")
         .args(["pfctl", "-a", "com.antiddos.pro", "-t", "ddos_blocks", "-T", "delete", ip])
         .status()?;

      if status.success() {
         Ok(())
      } else {
         Err(format!("pfctl failed to unblock IP: {}", ip).into())
      }
   }

   async fn cleanup(&self) -> Result<(), Box<dyn std::error::Error>> {
      info!("Cleaning up macOS Firewall rules ...");
      let _ = Command::new("sudo")
         .args(["pfctl", "-a", "com.antiddos.pro", "-F", "all"])
         .status();
      Ok(())
   }
}
