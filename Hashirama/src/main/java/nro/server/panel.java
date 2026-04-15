package nro.server;

import com.sun.management.OperatingSystemMXBean;
import nro.core.concurrent.GameScheduler;
import nro.server.io.Message;
import nro.services.Service;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class panel extends JPanel implements ActionListener {

    /* ===== Cụm nút ===== */
    private JButton btnBaoTri, btnExp, btnSuKien, btnThongBao, btnKickAll, btnKhuyenMai, btnTiLeRoi, btnTiLeNC;

    /* ===== Cụm trạng thái ===== */
    private JLabel vCpu, vRam, vThreads, vPlayers;

    /* ===== Refresh bằng GameScheduler ===== */
    private ScheduledFuture<?> refresher;

    public panel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(24, 26, 31));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildStatusGroup(), BorderLayout.CENTER);   // Cụm 1: trạng thái
        add(buildControlsGroup(), BorderLayout.SOUTH);  // Cụm 2: nút

        // làm mới 1s/lần (không dùng Timer Swing riêng)
        refresher = GameScheduler.SCHED.scheduleAtFixedRate(
                () -> SwingUtilities.invokeLater(this::updateStatusNow), 1, 1, TimeUnit.SECONDS);
        updateStatusNow();
    }

    /* ------------------ Cụm 1: Trạng thái ------------------ */
    private JComponent buildStatusGroup() {
        JPanel group = new JPanel(new BorderLayout());
        group.setBackground(new Color(28, 30, 36));
        group.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 54, 63)),
                "Trạng thái máy chủ"));

        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 8));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 12, 12, 12));

        vCpu     = valueLabel();
        vRam     = valueLabel();
        vThreads = valueLabel();
        vPlayers = valueLabel();

        grid.add(label("CPU tiến trình:"));          grid.add(vCpu);
        grid.add(label("RAM (used/total/max):"));    grid.add(vRam);
        grid.add(label("Threads:"));                 grid.add(vThreads);
        grid.add(label("Player online:"));           grid.add(vPlayers);

        group.add(grid, BorderLayout.CENTER);
        return group;
    }

    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(new Color(176, 180, 187));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private JLabel valueLabel() {
        JLabel l = new JLabel("—");
        l.setForeground(new Color(236, 239, 244));
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    /* ------------------ Cụm 2: Nút điều khiển ------------------ */
    private JComponent buildControlsGroup() {
        JPanel group = new JPanel(new BorderLayout());
        group.setBackground(new Color(28, 30, 36));
        group.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 54, 63)),
                "Điều khiển"));

        JPanel grid = new JPanel(new GridLayout(2, 5, 8, 8));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 12, 12, 12));

        btnBaoTri    = btn("Bảo trì");
        btnExp       = btn("Đổi Exp");
        btnSuKien    = btn("Sự kiện");
        btnThongBao  = btn("Thông báo");
        btnKickAll   = btn("Đá All");
        btnKhuyenMai = btn("Khuyến mãi");
        btnTiLeRoi   = btn("Tỉ lệ rơi");
        btnTiLeNC    = btn("Tỉ lệ nâng cấp");

        grid.add(btnBaoTri);
        grid.add(btnExp);
        grid.add(btnSuKien);
        grid.add(btnThongBao);
        grid.add(btnKickAll);
        grid.add(btnKhuyenMai);
        grid.add(btnTiLeRoi);
        grid.add(btnTiLeNC);
        // ô trống để cân lưới 2x5
        grid.add(new JLabel());
        grid.add(new JLabel());

        group.add(grid, BorderLayout.CENTER);
        return group;
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.addActionListener(this);
        b.setBackground(new Color(46, 50, 58));
        b.setForeground(new Color(236, 239, 244));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton btnAccent(String text) {
        JButton b = btn(text);
        b.setBackground(new Color(220, 70, 85));
        return b;
    }

    /* ------------------ Update trạng thái ------------------ */
    private void updateStatusNow() {
        // CPU
        String cpuText = "N/A";
        try {
            OperatingSystemMXBean osb = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double load = osb.getProcessCpuLoad(); // 0..1, -1 nếu chưa có
            if (load >= 0) cpuText = String.format("%.1f%%", load * 100.0);
        } catch (Throwable ignored) {}
        vCpu.setText(cpuText);

        // RAM
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long free  = rt.freeMemory();
        long used  = total - free;
        long max   = rt.maxMemory();
        vRam.setText(bytes(used) + " / " + bytes(total) + " / " + bytes(max));

        // Threads
        ThreadMXBean tbean = ManagementFactory.getThreadMXBean();
        vThreads.setText(String.valueOf(tbean.getThreadCount()));

        // Players
        int players = 0;
        try {
            players = Client.gI().getPlayers().size();
        } catch (Throwable ignored) {}
        vPlayers.setText(String.valueOf(players));
    }

    private static String bytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format("%.1f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format("%.1f MB", mb);
        double gb = mb / 1024.0;
        return String.format("%.2f GB", gb);
    }

    /* ------------------ Actions ------------------ */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnBaoTri) {
            Maintenance.gI().start(10);

        } else if (src == btnExp) {
            String exp = JOptionPane.showInputDialog(this,
                    "Bảng Exp Server\nHiện tại: x" + Manager.RATE_EXP_SERVER);
            if (exp != null) {
                try {
                    Manager.RATE_EXP_SERVER = Integer.parseInt(exp);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } else if (src == btnSuKien) {
            String sk = JOptionPane.showInputDialog(this,
                    "Bảng Sự Kiện\nHiện tại: " + Manager.EVENT_SEVER);
            if (sk != null) {
                try {
                    Manager.EVENT_SEVER = Byte.parseByte(sk);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } else if (src == btnThongBao) {
            String chat = JOptionPane.showInputDialog(this, "Thông Báo Server");
            if (chat != null) {
                Message msg = new Message(93);
                try {
                    msg.writer().writeUTF(chat);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(panel.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                Service.getInstance().sendMessAllPlayer(msg);
                msg.cleanup();
            }

        } else if (src == btnKickAll) {
            new Thread(() -> Client.gI().close()).start();

        } else if (src == btnKhuyenMai) {
            String s = JOptionPane.showInputDialog(this,
                    "Khuyến mãi nạp (xN)\nHiện tại: x" + Manager.KHUYEN_MAI_NAP);
            if (s != null) {
                try {
                    Manager.KHUYEN_MAI_NAP = Byte.parseByte(s);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } else if (src == btnTiLeRoi) {
            new ExpInputDialog();

        } else if (src == btnTiLeNC) {
            String tile = JOptionPane.showInputDialog(this,
                    "Tỉ lệ nâng cấp hiện tại: x" + Manager.TILE_NCAP);
            if (tile != null) {
                try {
                    Manager.TILE_NCAP = Integer.parseInt(tile);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /* Hủy refresher khi panel bị remove để tránh rò rỉ */
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refresher != null) {
            refresher.cancel(false);
            refresher = null;
        }
    }

    /* ===== Dialog nhập tỉ lệ rơi ===== */
    public class ExpInputDialog extends JFrame {
        private final JTextField textField1 = new JTextField(10);
        private final JTextField textField2 = new JTextField(10);

        public ExpInputDialog() {
            setTitle("Tỉ lệ rơi (a/b)");
            setSize(320, 160);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(2, 2, 8, 8));
            add(new JLabel("a:")); add(textField1);
            add(new JLabel("b:")); add(textField2);

            int result = JOptionPane.showConfirmDialog(this, this.getContentPane(),
                    "Bảng tỉ lệ rơi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    Manager.TILE_ROI_A = Integer.parseInt(textField1.getText());
                    Manager.TILE_ROI_B = Integer.parseInt(textField2.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /* Demo chạy riêng (tuỳ chọn) */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Server Dashboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new panel());
            f.setSize(720, 440);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
