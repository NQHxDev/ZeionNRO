use std::net::IpAddr;

pub fn normalize_ip(ip_str: &str) -> String {
   if let Ok(ip) = ip_str.parse::<IpAddr>() {
      match ip {
         IpAddr::V6(v6) => {
               if let Some(v4) = v6.to_ipv4_mapped() {
                  return v4.to_string();
               }

               if let Some(v4) = v6.to_ipv4() {
                  if !v6.is_loopback() && !v6.is_unspecified() {
                     return v4.to_string();
                  }
               }

               ip.to_string()
         }
         IpAddr::V4(v4) => v4.to_string(),
      }
   } else {
      ip_str.to_string()
   }
}

#[cfg(test)]
mod tests {
   use super::*;

   #[test]
   fn test_normalization() {
      // Test IPv4-mapped
      assert_eq!(normalize_ip("::ffff:192.168.1.1"), "192.168.1.1");
      // Test IPv4-compatible hex
      assert_eq!(normalize_ip("::c0a8:0142"), "192.168.1.66");
      // Test standard IPv4
      assert_eq!(normalize_ip("192.168.1.1"), "192.168.1.1");
      // Test standard IPv6 (loopback) should not be converted to 0.0.0.1
      assert_eq!(normalize_ip("::1"), "::1");
   }
}
