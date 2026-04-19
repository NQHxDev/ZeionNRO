# ZeionNRO - Technical TODO

## P0 - Critical (do first)

### 1: Remove hardcoded database secrets
- [ ] Move DB host/user/password out of source code into environment variables.
- [ ] Add a small config loader for DB settings in both `ServerGame` and `ServerLogin`.
- [ ] Support local development via `.env.example` (without real secrets).
- [ ] Rotate current DB credentials after migration.
- [ ] Verify services start correctly with env-based config only.

**Files to update**
- `ServerGame/src/main/java/nro/jdbc/DBService.java`
- `ServerLogin/src/main/java/db/DbManager.java` (or equivalent config entrypoint)

**Done when**
- No DB credentials remain in repository.
- Production/staging/dev credentials are managed outside source code.

---

### 2: Secure authentication (remove plaintext password flow)
- [ ] Stop sending raw password over internal game->login protocol.
- [ ] Migrate account storage from plaintext to password hash (`bcrypt` or `Argon2`).
- [ ] Implement login verify with hash comparison.
- [ ] Add migration path for existing users (one-time upgrade on successful login or migration script).
- [ ] Remove all usage of stored `uu/pp` raw values in session sync.

**Files to update**
- `ServerGame/src/main/java/nro/login/LoginService.java`
- `ServerLogin/src/main/java/model/User.java`

**Done when**
- Passwords are never stored nor compared in plaintext.
- Login still works for old and new accounts after migration plan.

---

### 3: Replace weak transport "encryption"
- [ ] Remove default XOR key `{0}` behavior.
- [ ] Add proper secure transport (prefer TLS between components).
- [ ] If TLS cannot be done immediately, implement temporary strong key exchange + authenticated encryption as interim step.
- [ ] Disable sensitive handshake payload logging in production.

**Files to update**
- `ServerGame/src/main/java/nro/server/ServerManager.java`
- `ServerLogin/src/main/java/server/Server.java`
- `ServerCommon/src/main/java/nro/network/netty/HandshakeHandler.java`

**Done when**
- Internal auth traffic is not readable from packet capture.
- No default insecure key path is enabled.

---

### 4: Prevent race conditions in connection/account state
- [ ] Replace shared mutable `HashMap` state with thread-safe structures (`ConcurrentHashMap`) where accessed by multiple Netty threads.
- [ ] Review all increment/decrement counters related to per-IP limits for atomic behavior.
- [ ] Add defensive null checks and safe cleanup in disconnect flows.

**Files to update**
- `ServerGame/src/main/java/nro/server/ServerManager.java`
- `ServerGame/src/main/java/nro/server/io/Session.java`

**Done when**
- Concurrent connect/disconnect tests no longer produce inconsistent counters/state.

---

### 5: Fix database connection model (thread safety + correctness)
- [ ] Remove shared global `Connection[]` usage across requests.
- [ ] Standardize on one connection pool (prefer HikariCP) for each service.
- [ ] Borrow/return connection per operation using try-with-resources.
- [ ] Add pool limits/timeouts and failure logging.

**Files to update**
- `ServerGame/src/main/java/nro/jdbc/DBService.java`
- `ServerLogin/src/main/java/db/DbManager.java`
- Related DAO/service callsites

**Done when**
- No request reuses the same JDBC `Connection` concurrently.
- Under load, DB errors and stuck transactions are reduced.

---

### 6: Add packet size limits to decoder (DoS protection)
- [ ] Validate frame length before allocation in Netty decoder.
- [ ] Reject packets larger than configured max frame size.
- [ ] Close/flag abusive sessions after repeated oversized packets.
- [ ] Add server config for max packet size.

**Files to update**
- `ServerCommon/src/main/java/nro/network/netty/NettyDecoder.java`

**Done when**
- Oversized payload cannot trigger unbounded memory allocation.

## P1 - Stability and maintainability

### 7: Fix session update loop safety
- [ ] Avoid removing sessions while iterating with for-each.
- [ ] Use iterator removal or staged removal list.
- [ ] Add regression test for connect/disconnect churn.

**Files to update**
- `ServerGame/src/main/java/nro/server/Client.java`

---

### 8: Prevent CPU spin on negative sleep
- [ ] Clamp sleep duration to non-negative value.
- [ ] Log delayed tick warning only with rate limit.
- [ ] Ensure loop cannot fall into hot-spin after timing exception.

**Files to update**
- `ServerGame/src/main/java/nro/server/Client.java`

---

### 9: Correct login session connection state transitions
- [ ] Set `connected = true` only after successful socket connect/handshake.
- [ ] Reset connection flags on failure paths consistently.
- [ ] Add retries with backoff and clear error logs.

**Files to update**
- `ServerGame/src/main/java/nro/login/LoginSession.java`

---

### 10: Reduce sensitive/high-volume protocol logging
- [ ] Add log levels and environment switch (`dev` vs `prod`).
- [ ] Remove raw payload dumps in production.
- [ ] Keep only minimal metadata (msg id, size, session id, duration).

**Files to update**
- `ServerCommon/src/main/java/nro/network/netty/HandshakeHandler.java`
- `ServerCommon/src/main/java/nro/network/netty/NettyDecoder.java`
- `ServerCommon/src/main/java/nro/network/netty/NettyEncoder.java`

---

### 11: Break monolithic controller into handlers
- [ ] Split command switch into feature-based handlers (inventory, combat, map, social, etc.).
- [ ] Introduce command registry/dispatcher.
- [ ] Keep backward compatibility for current packet ids.
- [ ] Add unit tests for at least top critical commands.

**Files to update**
- `ServerGame/src/main/java/nro/server/Controller.java`
- New package for command handlers

## P2 - Cleanup and long-term quality

### 12: Remove dead/disabled security code
- [ ] Delete commented-out verification blocks or implement fully.
- [ ] Add clear feature flags for optional protections.

**Files to update**
- `ServerGame/src/main/java/nro/server/Controller.java`

---

### 13: Consolidate DB access layer
- [ ] Remove or archive legacy pool implementations not used in runtime.
- [ ] Keep one standard DB access pattern across services.
- [ ] Document how new queries should be written and where.

**Files to update**
- `ServerGame/src/main/java/nro/jdbc/ConnPool.java`
- `ServerGame/src/main/java/nro/jdbc/DBService.java`

---

### 14: Add baseline test coverage
- [ ] Add integration tests for login success/failure and session limits.
- [ ] Add decoder tests for boundary packet lengths.
- [ ] Add concurrency tests for connect/disconnect counters.
- [ ] Add smoke tests for server startup/shutdown paths.

## Suggested execution order (milestones)
- [ ] Milestone A (security patch): tasks 1, 2, 3, 6
- [ ] Milestone B (runtime safety): tasks 4, 5, 7, 8, 9
- [ ] Milestone C (maintainability): tasks 10, 11, 12, 13, 14

## Notes
- Keep each task in separate PR when possible to reduce risk.
- For high-risk migrations (auth/DB), deploy with feature flag and rollback plan.
