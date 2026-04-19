mod config;
mod stats;
mod logger;
mod firewall;
mod unblocker;
mod monitor;
mod utils;

use std::sync::Arc;
use tokio::sync::mpsc;
use tokio::time::Duration;
use tracing::{info, error};
use crate::config::AppConfig;
use crate::stats::StatsManager;
use crate::firewall::{Firewall, CurrentFirewall};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
   // Init Logger
   logger::init();
      info!(r#"
     /$$$$$$              /$$     /$$         /$$$$$$$  /$$$$$$$
    /$$__  $$            | $$    |__/        | $$__  $$| $$__  $$
   | $$  \ $$ /$$$$$$$  /$$$$$$   /$$        | $$  \ $$| $$  \ $$  /$$$$$$   /$$$$$$$
   | $$$$$$$$| $$__  $$|_  $$_/  | $$ /$$$$$$| $$  | $$| $$  | $$ /$$__  $$ /$$_____/
   | $$__  $$| $$  \ $$  | $$    | $$|______/| $$  | $$| $$  | $$| $$  \ $$|  $$$$$$
   | $$  | $$| $$  | $$  | $$ /$$| $$        | $$  | $$| $$  | $$| $$  | $$ \____  $$
   | $$  | $$| $$  | $$  |  $$$$/| $$        | $$$$$$$/| $$$$$$$/|  $$$$$$/ /$$$$$$$/
   |__/  |__/|__/  |__/   \___/  |__/        |_______/ |_______/  \______/ |_______/

   >> System: Anti-DDoS v3.1 Master
   >> Status: Initializing Security Engine...
   "#);

   // Privilege Check
   check_privileges()?;

   // Load Config
   let config = AppConfig::load().map_err(|e| {
      error!("Failed to load config: {}", e);
      e
   })?;
   info!("Configuration loaded successfully ...");

   // Enforce Log Retention
   logger::cleanup_old_logs(config.log_retention_days);

   // Init Components
   let stats = Arc::new(StatsManager::new());

   // Choose firewall based on OS (abstraction handled in mod.rs)
   let firewall: Arc<dyn Firewall> = Arc::new(CurrentFirewall::new());

   // Self-healing Init
   if let Err(e) = firewall.init().await {
      error!("Firewall initialization failed: {}", e);
      return Err(e);
   }

   // Setup Communication Channel
   let (tx, rx) = mpsc::channel(100);

   // Spawn Unblocker Task
   let unblock_stats = stats.clone();
   let unblock_firewall = firewall.clone();
   let unblock_duration = Duration::from_secs(config.unblock_after_seconds);
   tokio::spawn(async move {
      unblocker::run_unblocker(rx, unblock_firewall, unblock_stats, unblock_duration).await;
   });

   // Spawn Monitor Task
   let monitor_stats = stats.clone();
   let monitor_firewall = firewall.clone();
   let monitor_config = config.clone();
   tokio::spawn(async move {
      monitor::run_monitor(monitor_config, monitor_stats, monitor_firewall, tx).await;
   });

   info!("System fully operational. Monitoring ports: {:?}", config.ports);

   // Graceful Shutdown Handling
   #[cfg(unix)]
   {
       let mut sigterm = tokio::signal::unix::signal(tokio::signal::unix::SignalKind::terminate())?;
       tokio::select! {
           _ = tokio::signal::ctrl_c() => info!("Shutdown signal received ..."),
           _ = sigterm.recv() => info!("Shutdown signal received ..."),
       };
   }
   #[cfg(not(unix))]
   {
       tokio::signal::ctrl_c().await?;
       info!("Shutdown signal received ...");
   }

   info!("Cleaning up firewall rules ...");

   if let Err(e) = firewall.cleanup().await {
      error!("Failed to cleanup firewall: {}", e);
   }

   info!("Cleanup complete. Exiting ...");
   Ok(())
}

fn check_privileges() -> Result<(), Box<dyn std::error::Error>> {
   #[cfg(target_os = "windows")]
   {
      if !is_elevated::is_elevated() {
         error!("ERROR: Application must be run as Administrator ...");
         return Err("Not Administrator".into());
      }
   }

   #[cfg(any(target_os = "linux", target_os = "macos"))]
   {
      if nix::unistd::getuid().is_root() {
         info!("Running with root privileges ...");
      } else {
         error!("ERROR: Application must be run as root ...");
         return Err("Not root".into());
      }
   }

   Ok(())
}
