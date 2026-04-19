pub mod whitelist;
use async_trait::async_trait;

#[async_trait]
pub trait Firewall: Send + Sync {
   async fn init(&self) -> Result<(), Box<dyn std::error::Error>>;
   async fn block(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>>;
   async fn unblock(&self, ip: &str) -> Result<(), Box<dyn std::error::Error>>;
   async fn cleanup(&self) -> Result<(), Box<dyn std::error::Error>>;
}

#[cfg(target_os = "windows")]
pub mod windows;
#[cfg(target_os = "linux")]
pub mod linux;
#[cfg(target_os = "macos")]
pub mod macos;

#[cfg(target_os = "windows")]
pub use windows::WindowsFirewall as CurrentFirewall;

#[cfg(target_os = "linux")]
pub use linux::LinuxFirewall as CurrentFirewall;

#[cfg(target_os = "macos")]
pub use macos::MacFirewall as CurrentFirewall;
