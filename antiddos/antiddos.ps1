# =========================================================
# ANTI-DDoS FIREWALL ENGINE (STANDALONE)
# =========================================================

# ================== CONFIG ==================
$PORTS          = @(14445, 14446, 3306)
$MAX_CONN       = 30
$WINDOW_SEC     = 2
$HEARTBEAT_SEC  = 15

$BASE_DIR = $PSScriptRoot
$LOG_FILE = Join-Path $BASE_DIR "antiddos.log"

$WHITELIST = @(
    "127.0.0.1",
    "::1"
)

# ================== SINGLE INSTANCE GUARD ==================
$mutexName = "Global\ANTI_DDOS_ENGINE"
$mutex = New-Object System.Threading.Mutex($false, $mutexName)

if (-not $mutex.WaitOne(0)) {
    exit
}

# ================== INIT LOG ==================
"" | Out-File $LOG_FILE -Encoding UTF8

$ENGINE_PID = $PID
$ENGINE_ID  = [Guid]::NewGuid().ToString().Substring(0,8)

function Log {
    param([string]$Message)

    $time = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    "$time [$ENGINE_ID|PID:$ENGINE_PID] $Message" |
        Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
}

Log "===== ENGINE BOOT ====="
Log "[INIT] Engine PID=$ENGINE_PID"
Log "[INIT] Ports=$($PORTS -join ',') MAX_CONN=$MAX_CONN"

# ================== FUNCTIONS ==================

function Block-IP {
    param([string]$IP)

    if ([string]::IsNullOrWhiteSpace($IP)) { return }
    if ($WHITELIST -contains $IP) { return }

    $ruleName = "ANTI_DDOS_BLOCK_$IP"

    if (Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue) {
        return
    }

    New-NetFirewallRule `
        -DisplayName $ruleName `
        -Direction Inbound `
        -Action Block `
        -RemoteAddress $IP `
        -Protocol TCP `
        -ErrorAction SilentlyContinue | Out-Null

    Log "[BLOCK] IP=$IP"
}

# ================== FIREWALL INIT ==================

if (-not (Get-NetFirewallRule -DisplayName "BLOCK_ALL_UDP" -ErrorAction SilentlyContinue)) {
    New-NetFirewallRule `
        -DisplayName "BLOCK_ALL_UDP" `
        -Direction Inbound `
        -Action Block `
        -Protocol UDP `
        -ErrorAction SilentlyContinue | Out-Null

    Log "[INIT] UDP_BLOCK_ENABLED"
}

# ================== START ==================
Log "[START] ENGINE_STARTED"

$lastHeartbeat = Get-Date

# ================== MAIN LOOP ==================
while ($true) {

    foreach ($port in $PORTS) {

        try {
            $connections = Get-NetTCPConnection `
                -LocalPort $port `
                -State SynReceived,Established `
                -ErrorAction Stop
        }
        catch {
            continue
        }

        if (-not $connections) { continue }

        $grouped = $connections | Group-Object -Property RemoteAddress

        foreach ($group in $grouped) {
            if ($group.Count -gt $MAX_CONN) {
                Block-IP $group.Name
            }
        }
    }

    # ---------------- HEARTBEAT ----------------
    if ((Get-Date) - $lastHeartbeat -ge (New-TimeSpan -Seconds $HEARTBEAT_SEC)) {
        Log "[HEARTBEAT]"
        $lastHeartbeat = Get-Date
    }

    Start-Sleep -Seconds $WINDOW_SEC
}
