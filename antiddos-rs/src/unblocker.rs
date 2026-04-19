use std::collections::BinaryHeap;
use std::cmp::Ordering;
use tokio::sync::mpsc;
use tokio::time::{self, Duration, Instant as TokioInstant};
use tracing::{info, error};
use std::sync::Arc;
use crate::firewall::Firewall;
use crate::stats::StatsManager;

#[derive(Debug, Eq, PartialEq)]
struct ScheduledUnblock {
   time: TokioInstant,
   ip: String,
}

impl Ord for ScheduledUnblock {
   fn cmp(&self, other: &Self) -> Ordering {
      other.time.cmp(&self.time) // Min-heap for earliest time
   }
}

impl PartialOrd for ScheduledUnblock {
   fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
      Some(self.cmp(other))
   }
}

pub async fn run_unblocker(
    mut rx: mpsc::Receiver<String>,
   firewall: Arc<dyn Firewall>,
   stats: Arc<StatsManager>,
   unblock_duration: Duration,
) {
   let mut heap: BinaryHeap<ScheduledUnblock> = BinaryHeap::new();

   info!("Unblocker thread started.");

   loop {
      let now = TokioInstant::now();
      let next_unblock_time = heap.peek().map(|u| u.time);

      let sleep_duration = match next_unblock_time {
         Some(time) if time > now => time - now,
         Some(_) => Duration::ZERO, // Past due
         None => Duration::from_secs(3600), // Idle wait
      };

      tokio::select! {
         // Wait for next scheduled unblock or idle timeout
         _ = time::sleep(sleep_duration) => {
            while let Some(u) = heap.peek() {
               if u.time <= TokioInstant::now() {
                  let u = heap.pop().unwrap();
                  info!("Auto-unblocking IP: {}", u.ip);
                  if let Err(e) = firewall.unblock(&u.ip).await {
                     error!("Failed to unblock {}: {}", u.ip, e);
                  }
                  stats.mark_as_unblocked(&u.ip);
               } else {
                  break;
               }
            }
         }
         // Receive new block notification
         Some(ip) = rx.recv() => {
            info!("Scheduling unblock for {} in {:?}", ip, unblock_duration);
            heap.push(ScheduledUnblock {
               time: TokioInstant::now() + unblock_duration,
               ip,
            });
         }
      }
   }
}
