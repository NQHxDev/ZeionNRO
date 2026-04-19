use serde::{Deserialize, Serialize};
use std::fs;
use std::path::Path;
use std::collections::HashMap;

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct AppConfig {
   pub ports: Vec<u16>,
   pub thresholds: HashMap<u16, usize>,
   pub default_threshold: usize,
   pub burst_threshold: usize,
   pub unblock_after_seconds: u64,
   pub whitelist: Vec<String>,
   pub auto_whitelist_cloudflare: bool,
   pub log_retention_days: u32,
}

impl AppConfig {
   pub fn load() -> Result<Self, Box<dyn std::error::Error>> {
      let config_path = Path::new("config.json");

      let content = if config_path.exists() {
         fs::read_to_string(config_path)?
      } else {
         include_str!("../config.json").to_string()
      };

      let config: AppConfig = serde_json::from_str(&content)?;
      Ok(config)
   }
}
