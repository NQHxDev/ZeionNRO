use tracing_subscriber::{fmt, prelude::*, registry};
use tracing_appender::rolling;

use std::fs;
use std::time::{SystemTime, Duration};
use tracing::{info, warn};

pub fn init() {
   let file_appender = rolling::daily("logs", "antiddos.log");
   let (non_blocking, _guard) = tracing_appender::non_blocking(file_appender);

   let file_layer = fmt::layer()
      .with_writer(non_blocking)
      .with_ansi(false);

   let stdout_layer = fmt::layer()
      .with_ansi(true);

   registry()
      .with(file_layer)
      .with(stdout_layer)
      .init();

   // Leak the guard to keep the logger alive
   Box::leak(Box::new(_guard));
}

pub fn cleanup_old_logs(retention_days: u32) {
   let logs_dir = "logs";
   let now = SystemTime::now();
   let max_age = Duration::from_secs(retention_days as u64 * 24 * 3600);

   if let Ok(entries) = fs::read_dir(logs_dir) {
      for entry in entries.flatten() {
         let path = entry.path();
         if path.is_file() {
            if let Ok(metadata) = entry.metadata() {
               if let Ok(modified) = metadata.modified() {
                  if let Ok(age) = now.duration_since(modified) {
                     if age > max_age {
                        info!("Deleting old log file: {:?}", path);
                        let _ = fs::remove_file(path);
                     }
                  }
               }
            }
         }
      }
   } else {
      warn!("Could not read logs directory for cleanup");
   }
}
