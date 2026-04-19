use dashmap::DashMap;
use std::sync::Arc;
use std::time::{Duration, Instant};

#[derive(Debug)]
pub struct IpStats {
   pub connections: usize,
   pub last_seen: Instant,
   pub is_blocked: bool,
}

pub struct StatsManager {
   pub stats: Arc<DashMap<String, IpStats>>,
}

impl StatsManager {
   pub fn new() -> Self {
      Self {
         stats: Arc::new(DashMap::new()),
      }
   }

   pub fn update_connection_count(&self, ip: String, count: usize) {
      self.stats.entry(ip).and_modify(|s| {
         s.connections = count;
         s.last_seen = Instant::now();
      }).or_insert(IpStats {
         connections: count,
         last_seen: Instant::now(),
         is_blocked: false,
      });
   }

   #[allow(dead_code)]
   pub fn mark_as_blocked(&self, ip: &str) {
      if let Some(mut entry) = self.stats.get_mut(ip) {
         entry.is_blocked = true;
      }
   }

   pub fn mark_as_unblocked(&self, ip: &str) {
      if let Some(mut entry) = self.stats.get_mut(ip) {
         entry.is_blocked = false;
      }
   }

   pub fn cleanup_stale(&self, ttl: Duration) {
      self.stats.retain(|_, v| {
         v.last_seen.elapsed() < ttl || v.is_blocked
      });
   }
}
