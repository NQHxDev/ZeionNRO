Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName Microsoft.VisualBasic

# ================= PATH =================
$BASE_DIR = $PSScriptRoot
$ENGINE   = Join-Path $BASE_DIR "antiddos.ps1"
$LOG_FILE = Join-Path $BASE_DIR "antiddos.log"

if (-not (Test-Path $LOG_FILE)) {
    New-Item $LOG_FILE -ItemType File | Out-Null
}

# ================= AUTO START ENGINE (FINAL) =================

# 1. Kill engine cũ (đúng tiến trình)
Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -eq "powershell.exe" -and
        $_.CommandLine -match [regex]::Escape($ENGINE)
    } |
    ForEach-Object {
        try { Stop-Process -Id $_.ProcessId -Force } catch {}
    }

Start-Sleep -Milliseconds 500

# 2. Reset log (GUI là orchestrator)
"" | Out-File $LOG_FILE -Encoding UTF8

# 3. Start engine (CHỈ 1 LẦN)
Start-Process powershell.exe `
    -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$ENGINE`"" `
    -WindowStyle Hidden

# 4. Chờ engine ghi HEARTBEAT
$engineState = "STARTING"

for ($i = 0; $i -lt 15; $i++) {

    Start-Sleep -Milliseconds 500

    try {
        $hb = Get-Content $LOG_FILE -ErrorAction Stop |
              Select-String '\[HEARTBEAT\]' |
              Select-Object -Last 1
    }
    catch {
        continue
    }

    if ($hb) {
        $engineState = "RUNNING"
        break
    }
}

# 5. Lưu trạng thái để set UI SAU KHI FORM TẠO
$Global:EngineStartupState = $engineState


# ================= COLORS =================
$BG_MAIN  = [Drawing.Color]::FromArgb(13,17,23)
$BG_PANEL = [Drawing.Color]::FromArgb(22,27,34)
$BTN_BG   = [Drawing.Color]::FromArgb(33,38,45)
$FG_GREEN = [Drawing.Color]::FromArgb(57,255,20)
$FG_GRAY  = [Drawing.Color]::FromArgb(160,160,160)

# ================= FORM =================
$form = New-Object System.Windows.Forms.Form
$form.FormBorderStyle = 'None'
$form.Size = New-Object Drawing.Size(760,480)
$form.StartPosition = 'CenterScreen'
$form.BackColor = $BG_MAIN
$form.ForeColor = $FG_GREEN
$form.Font = New-Object Drawing.Font("Consolas",10)

# ================= BUTTON FACTORY =================
function New-Button($text, $x) {
    $b = New-Object System.Windows.Forms.Button
    $b.Text = $text
    $b.Location = New-Object Drawing.Point($x,395)
    $b.Size = New-Object Drawing.Size(220,34)
    $b.BackColor = $BTN_BG
    $b.ForeColor = $FG_GREEN
    $b.FlatStyle = 'Flat'
    $b.FlatAppearance.BorderSize = 0
    $b.Font = New-Object Drawing.Font("Consolas",10)
    return $b
}

# ================= TITLE BAR =================
$titleBar = New-Object System.Windows.Forms.Panel
$titleBar.Height = 36
$titleBar.Dock = 'Top'
$titleBar.BackColor = $BG_PANEL

$titleText = New-Object System.Windows.Forms.Label
$titleText.Text = "ANTI-DDOS FIREWALL CONTROL (REALTIME)"
$titleText.ForeColor = $FG_GREEN
$titleText.Font = New-Object Drawing.Font("Consolas",11,'Bold')
$titleText.AutoSize = $true
$titleText.Location = New-Object Drawing.Point(12,9)
$led = New-Object System.Windows.Forms.Label
$led.Size = New-Object Drawing.Size(12,12)
$led.Location = New-Object Drawing.Point(700,12)
$led.BackColor = [Drawing.Color]::DarkRed
$led.BorderStyle = 'FixedSingle'
$titleBar.Controls.Add($led)

$btnClose = New-Object System.Windows.Forms.Button
$btnClose.Text = "X"
$btnClose.Size = New-Object Drawing.Size(40,30)
$btnClose.Location = New-Object Drawing.Point(715,3)
$btnClose.BackColor = $BG_PANEL
$btnClose.ForeColor = $FG_GREEN
$btnClose.FlatStyle = 'Flat'
$btnClose.FlatAppearance.BorderSize = 0
$btnClose.Font = New-Object Drawing.Font("Consolas",10,'Bold')
$btnClose.Add_Click({ $form.Close() })

$titleBar.Controls.AddRange(@($titleText,$btnClose))
$form.Controls.Add($titleBar)

# ================= DRAG WINDOW =================
$dragging = $false
$startPos = [Drawing.Point]::Empty

$titleBar.Add_MouseDown({
    $dragging = $true
    $startPos = [Windows.Forms.Cursor]::Position
})
$titleBar.Add_MouseUp({ $dragging = $false })
$titleBar.Add_MouseMove({
    if ($dragging) {
        $cur = [Windows.Forms.Cursor]::Position
        $form.Left += ($cur.X - $startPos.X)
        $form.Top  += ($cur.Y - $startPos.Y)
        $startPos = $cur
    }
})

# ================= LIST =================
$list = New-Object System.Windows.Forms.ListBox
$list.Location = New-Object Drawing.Point(20,55)
$list.Size = New-Object Drawing.Size(720,300)
$list.BackColor = $BG_PANEL
$list.ForeColor = $FG_GREEN
$list.Font = New-Object Drawing.Font("Consolas",10)

# ================= STATUS =================
$status = New-Object System.Windows.Forms.Label
$status.Text = "Waiting for events..."
$status.ForeColor = $FG_GRAY
$status.AutoSize = $true
$status.Location = New-Object Drawing.Point(20,365)

$countLabel = New-Object System.Windows.Forms.Label
$countLabel.Text = "Blocked IPs: 0"
$countLabel.ForeColor = $FG_GREEN
$countLabel.AutoSize = $true
$countLabel.Location = New-Object Drawing.Point(620,365)


# ================= BUTTONS =================
$btnUnblockAll = New-Button "UNBLOCK ALL" 20
$btnUnblockSel = New-Button "UNBLOCK SELECTED" 260
$btnRefresh    = New-Button "REFRESH" 500
$btnBlockIP    = New-Button "BLOCK IP" 500
$btnBlockIP.Location = New-Object Drawing.Point(500,435) # giữ block bên phải

# ================= FUNCTIONS =================
function Load-Firewall {
    $list.Items.Clear()
    Get-NetFirewallRule |
        Where-Object { $_.DisplayName -like "ANTI_DDOS_BLOCK_*" } |
        ForEach-Object {
            $list.Items.Add($_.DisplayName.Replace("ANTI_DDOS_BLOCK_",""))
        }
    $countLabel.Text = "Blocked IPs: $($list.Items.Count)"
}


function Set-EngineLed($state) {
    switch ($state) {
        "RUNNING" {
            $led.BackColor = [Drawing.Color]::LimeGreen
        }
        "STOPPED" {
            $led.BackColor = [Drawing.Color]::DarkRed
        }
        "UNRESPONSIVE" {
            $led.BackColor = [Drawing.Color]::Orange
        }
    }
}


# ===== APPLY ENGINE STARTUP STATE (CRITICAL) =====
switch ($Global:EngineStartupState) {
    "RUNNING" {
        $status.Text = "ENGINE STATUS: RUNNING"
        Set-EngineLed "RUNNING"
    }
    "STARTING" {
        $status.Text = "ENGINE STATUS: STARTING"
        Set-EngineLed "UNRESPONSIVE"
    }
    default {
        $status.Text = "ENGINE STATUS: FAILED TO START"
        Set-EngineLed "STOPPED"
    }
}
# ================= EVENTS =================
$btnRefresh.Add_Click({
    Load-Firewall
    $status.Text = "Manual refresh at $(Get-Date -Format HH:mm:ss)"
})

$btnBlockIP.Add_Click({
    $ip = [Microsoft.VisualBasic.Interaction]::InputBox(
        "Enter IP to block:",
        "Block IP",
        ""
    )

    if ([string]::IsNullOrWhiteSpace($ip)) { return }

    if ($ip -notmatch '^\d{1,3}(\.\d{1,3}){3}$') {
        $status.Text = "Invalid IP format."
        return
    }

    $rule = "ANTI_DDOS_BLOCK_$ip"
    if (Get-NetFirewallRule -DisplayName $rule -ErrorAction SilentlyContinue) {
        $status.Text = "IP already blocked."
        return
    }

    New-NetFirewallRule `
        -DisplayName $rule `
        -Direction Inbound `
        -Action Block `
        -RemoteAddress $ip `
        -Protocol TCP

    Add-Content $LOG_FILE "[MANUAL] IP $ip blocked"
    Load-Firewall
    $status.Text = "Manually blocked IP: $ip"
})

$btnUnblockSel.Add_Click({
    if (-not $list.SelectedItem) {
        $status.Text = "No IP selected."
        return
    }

    $ip = $list.SelectedItem
    $rule = "ANTI_DDOS_BLOCK_$ip"

    Get-NetFirewallRule -DisplayName $rule -ErrorAction SilentlyContinue |
        Remove-NetFirewallRule

    $list.Items.Remove($ip)
    $countLabel.Text = "Blocked IPs: $($list.Items.Count)"
    $status.Text = "Unblocked IP: $ip"
})

$btnUnblockAll.Add_Click({
    Get-NetFirewallRule |
        Where-Object { $_.DisplayName -like "ANTI_DDOS_BLOCK_*" } |
        Remove-NetFirewallRule

    $list.Items.Clear()
    $countLabel.Text = "Blocked IPs: 0"
    $status.Text = "All IPs unblocked."
})

$engineTimer = New-Object System.Windows.Forms.Timer
$engineTimer.Interval = 5000

$engineTimer.Add_Tick({

    try {
        $hb = Get-Content $LOG_FILE -ErrorAction Stop |
              Select-String '\[HEARTBEAT\]' |
              Select-Object -Last 1
    }
    catch {
        $status.Text = "ENGINE STATUS: STOPPED"
        Set-EngineLed "STOPPED"
        return
    }

    if (-not $hb) {
        $status.Text = "ENGINE STATUS: STOPPED"
        Set-EngineLed "STOPPED"
        return
    }

    $time = [datetime]::ParseExact(
        $hb.Line.Substring(0,19),
        'yyyy-MM-dd HH:mm:ss',
        $null
    )

    if ((Get-Date) - $time -lt (New-TimeSpan -Seconds 20)) {
        $status.Text = "ENGINE STATUS: RUNNING"
        Set-EngineLed "RUNNING"
    }
    else {
        $status.Text = "ENGINE STATUS: UNRESPONSIVE"
        Set-EngineLed "UNRESPONSIVE"
    }
})

$engineTimer.Start()



# ================= REALTIME FILE WATCHER =================
$watcher = New-Object IO.FileSystemWatcher
$watcher.Path = $BASE_DIR
$watcher.Filter = "antiddos.log"
$watcher.NotifyFilter = 'LastWrite'
$watcher.EnableRaisingEvents = $true

Register-ObjectEvent $watcher Changed -Action {

    Start-Sleep -Milliseconds 80

    try {
        $lines = Get-Content $LOG_FILE -Tail 5 -ErrorAction Stop
    }
    catch {
        return
    }

    $form.Invoke([Action]{

        foreach ($line in $lines) {

            if ($line -match '\[BLOCK\] IP=([0-9\.]+)') {

                $ip = $matches[1]

                if (-not $list.Items.Contains($ip)) {
                    $list.Items.Add($ip)
                    $countLabel.Text = "Blocked IPs: $($list.Items.Count)"
                }

                $status.Text = "Blocked IP detected: $ip"
                return
            }

            if ($line -match '\[START\]') {
                $status.Text = "ENGINE STARTED"
                return
            }
        }
    })
}


# ================= ADD CONTROLS =================
$form.Controls.AddRange(@(
    $list,
    $status,
    $countLabel,
    $btnUnblockAll,
    $btnUnblockSel,
    $btnRefresh,
    $btnBlockIP
))

Load-Firewall
[void]$form.ShowDialog()

