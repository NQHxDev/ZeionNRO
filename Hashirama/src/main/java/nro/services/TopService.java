package nro.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import nro.jdbc.DBService;
import nro.models.player.Player;
import nro.server.Client;
import nro.server.io.Message;
import nro.utils.Log;
import nro.utils.Util;

/**
 * TopService - gửi các Top qua gói (-96) theo format client đang đọc.
 *
 * Hỗ trợ:
 *  - Top SD (online, không SQL)
 *  - Top Nhiệm Vụ (SQL: id, name, nv, nv1, nv2)
 *  - Top VND (SQL: id, name, vnd)
 *
 * Quy ước:
 *  - typeTop = 0 (cho phép click ở client)
 *  - headICON luôn ghi -1 để khớp stream (client có ô readShort thứ 2)
 *  - Nếu người chơi OFFLINE:
 *      + Gửi head = FALLBACK_HEAD, body=0, leg=0
 *      + info2 = "Người chơi đang offline"
 *  - Nếu ONLINE:
 *      + Lấy head/body/leg từ Player, info2 hiển thị chi tiết
 */
public final class TopService {

    // =============== CONFIG ===============

    /** head mặc định khi offline (bạn có thể đổi theo server) */
    private static final short FALLBACK_HEAD = 1806;

    /** Số lượng tối đa item mỗi top */
    private static final int LIMIT_ONLINE_TOP = 20;

    private static final int LIMIT_NV  = 20;
    private static final int LIMIT_VND = 20;
    private static final long[] TIER_TOP = {1_000_000,950_000,870_000,750_000,600_000,550_000,500_000, 470_000, 450_000};
    private static final long[] TIER_HIGH = {200_000,300_000,280_000,250_000};
    private static final long[] TIER_MID = {150_000, 120_000};
    private static final long[] TIER_LOW = {100_000, 50_000,70_000,90_000};
    private static final long[] TIER_MIN = {20_000,10_000,40_000,30_000};


    // =============== QUERIES ===============
    /**
     * Top Nhiệm Vụ – tương đương PHP đã cung cấp:
     *  SELECT name, player.id,
     *         nv  = CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(data_task, ',', 2), ',', -1) AS UNSIGNED),
     *         nv1 = CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(data_task, ',', 3), ',', -1) AS UNSIGNED),
     *         nv2 = CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(data_task, ',', 1), '[', -1) AS UNSIGNED)
     *  ORDER BY nv DESC, nv1 DESC, nv2 DESC
     */
    private static final String QUERY_TOP_NV =
        "SELECT p.id, p.name, " +
        "  CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(p.data_task, ',', 2), ',', -1) AS UNSIGNED) AS nv, " +
        "  CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(p.data_task, ',', 3), ',', -1) AS UNSIGNED) AS nv1, " +
        "  CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(p.data_task, ',', 1), '[', -1) AS UNSIGNED) AS nv2 " +
        "FROM player p " +
        "INNER JOIN account a ON a.id = p.account_id " +
        "WHERE a.is_admin = 0 AND a.ban = 0 " +
        "ORDER BY " +
        "  CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(p.data_task, ',', 2), ',', -1) AS UNSIGNED) DESC, " +
        "  CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(p.data_task, ',', 3), ',', -1) AS UNSIGNED) DESC, " +
        "  CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(p.data_task, ',', 1), '[', -1) AS UNSIGNED) DESC " +
        "LIMIT " + LIMIT_NV + ";";

    /**
     * Top VND – đơn giản: vnd nằm ở bảng player (tuỳ DB bạn).
     * Nếu cột vnd ở
     * bảng account, bạn tự sửa JOIN cho phù hợp.
     */

    private static final String QUERY_TOP_VND
            = "SELECT p.id, p.name, COALESCE(a.tongnap, 0) AS vnd "
            + "FROM player p "
            + "JOIN account a ON a.id = p.account_id "
            + "WHERE a.is_admin = 0 AND a.ban = 0 "
            + "ORDER BY vnd DESC "
            + "LIMIT ?;";


    // =============== SINGLETON ===============

    private static TopService inst;
    private TopService() {}

    public static TopService gI() {
        if (inst == null) inst = new TopService();
        return inst;
    }

    // =============== PUBLIC APIS ===============
    /** Top Sức Đánh – chỉ online, không SQL */
    public synchronized void showTopSd(Player viewer) {
        try {
            List<Player> online = new ArrayList<>(Client.gI().getPlayers());

            online.sort(Comparator.comparingDouble(p -> p.nPoint.dame));
            Collections.reverse(online);

            online = limitOnlineTop(online); // ✅ GIỚI HẠN 30

            List<Row> rows = new ArrayList<>(online.size());
            for (Player pl : online) {
                if (pl == null) continue;

                String right = "SD: " + Util.powerToString(pl.nPoint.dame);
                String detail =
                        "SM: " + Util.powerToString(pl.nPoint.power)
                      + "\nTN: " + Util.powerToString(pl.nPoint.tiemNang)
                      + "\nHP: " + Util.powerToString(pl.nPoint.hpMax)
                      + "\nKI: " + Util.powerToString(pl.nPoint.mpMax)
                      + "\nDEF: " + Util.powerToString(pl.nPoint.def)
                      + "\nCM: " + pl.nPoint.crit + "%";

                rows.add(rowForPlayerOnline(pl, pl.name, right, detail));
            }

            sendTop(viewer,
                    "Top Sức Đánh (" + nro.utils.TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")",
                    rows);

        } catch (Exception e) {
            notifyFail(viewer, "Lỗi tải Top Sức Đánh!");
        }
    }
    public synchronized void showTopSucManh(Player viewer) {
        try {
            List<Player> online = new ArrayList<>(Client.gI().getPlayers());

            online.sort(Comparator.comparingDouble(p -> p.nPoint.power));
            Collections.reverse(online);

            online = limitOnlineTop(online); // ✅ GIỚI HẠN 30

            List<Row> rows = new ArrayList<>(online.size());
            for (Player pl : online) {
                if (pl == null) continue;

                String right = "Sức Mạnh: " + Util.powerToString(pl.nPoint.power);
                String detail =
                        "SM: " + Util.powerToString(pl.nPoint.power)
                      + "\nTN: " + Util.powerToString(pl.nPoint.tiemNang)
                      + "\nHP: " + Util.powerToString(pl.nPoint.hpMax)
                      + "\nKI: " + Util.powerToString(pl.nPoint.mpMax)
                      + "\nDEF: " + Util.powerToString(pl.nPoint.def)
                      + "\nCM: " + pl.nPoint.crit + "%";

                rows.add(rowForPlayerOnline(pl, pl.name, right, detail));
            }

            sendTop(viewer,
                    "Top Sức Mạnh (" + nro.utils.TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")",
                    rows);

        } catch (Exception e) {
            notifyFail(viewer, "Lỗi tải Top Sức Mạnh!");
        }
    }

    public synchronized void showTopHP(Player viewer) {
        try {
            List<Player> online = new ArrayList<>(Client.gI().getPlayers());

            online.sort(Comparator.comparingDouble(p -> p.nPoint.hpMax));
            Collections.reverse(online);

            online = limitOnlineTop(online); // ✅ GIỚI HẠN 30

            List<Row> rows = new ArrayList<>(online.size());
            for (Player pl : online) {
                if (pl == null) continue;

                String right = "HP: " + Util.powerToString(pl.nPoint.hpMax);
                String detail =
                        "SM: " + Util.powerToString(pl.nPoint.power)
                      + "\nTN: " + Util.powerToString(pl.nPoint.tiemNang)
                      + "\nHP: " + Util.powerToString(pl.nPoint.hpMax)
                      + "\nKI: " + Util.powerToString(pl.nPoint.mpMax)
                      + "\nDEF: " + Util.powerToString(pl.nPoint.def)
                      + "\nCM: " + pl.nPoint.crit + "%";

                rows.add(rowForPlayerOnline(pl, pl.name, right, detail));
            }

            sendTop(viewer,
                    "Top HP (" + nro.utils.TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")",
                    rows);

        } catch (Exception e) {
            notifyFail(viewer, "Lỗi tải Top HP!");
        }
    }

    public synchronized void showTopKi(Player viewer) {
        try {
            List<Player> online = new ArrayList<>(Client.gI().getPlayers());

            online.sort(Comparator.comparingDouble(p -> p.nPoint.mpMax));
            Collections.reverse(online);

            online = limitOnlineTop(online); // ✅ GIỚI HẠN 30

            List<Row> rows = new ArrayList<>(online.size());
            for (Player pl : online) {
                if (pl == null) continue;

                String right = "KI: " + Util.powerToString(pl.nPoint.mpMax);
                String detail =
                        "SM: " + Util.powerToString(pl.nPoint.power)
                      + "\nTN: " + Util.powerToString(pl.nPoint.tiemNang)
                      + "\nHP: " + Util.powerToString(pl.nPoint.hpMax)
                      + "\nKI: " + Util.powerToString(pl.nPoint.mpMax)
                      + "\nDEF: " + Util.powerToString(pl.nPoint.def)
                      + "\nCM: " + pl.nPoint.crit + "%";

                rows.add(rowForPlayerOnline(pl, pl.name, right, detail));
            }

            sendTop(viewer,
                    "Top KI (" + nro.utils.TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")",
                    rows);

        } catch (Exception e) {
            notifyFail(viewer, "Lỗi tải Top KI!");
        }
    }


    /** Top Nhiệm Vụ – SQL chỉ lấy id/name/nv,nv1,nv2; avatar lấy từ Player nếu online, ngược lại offline */
    public synchronized void showTopNhiemVu(Player viewer) {
        try (Connection con = DBService.gI().getConnectionForGame();
             PreparedStatement ps = con.prepareStatement(QUERY_TOP_NV);
             ResultSet rs = ps.executeQuery()) {

            List<Row> rows = new ArrayList<>();

            // ===== 1. Người chơi thật từ DB =====
            while (rs.next()) {
                int id   = rs.getInt("id");
                String name = rs.getString("name");
                int nv   = rs.getInt("nv");
                int nv1  = rs.getInt("nv1");
                int nv2  = rs.getInt("nv2");

                String right  = "NV: " + nv;
                String detail = "Tiến độ: [" + nv1 + "," + nv2 + "]";

                rows.add(rowForPlayerMaybeOffline(id, name, right, detail));
            }

            // ===== 2. BOT online – nhiệm vụ giả =====
            List<Player> bots = Client.gI().getPlayers().stream()
                    .filter(p -> p != null && p.isBot)
                    .collect(Collectors.toList());

            for (Player bot : bots) {
                int nv  = Util.nextInt(20, 26);
                int nv1 = Util.nextInt(0, 5);
                int nv2 = Util.nextInt(0, 5);

                String right  = "NV: " + nv;
                String detail = "Tiến độ: [" + nv1 + "," + nv2 + "]";

                rows.add(new Row(
                        (int) bot.id,
                        bot.getHead(),
                        bot.getBody(),
                        bot.getLeg(),
                        bot.name,
                        right,
                        detail
                ));
            }

            // ===== 3. Sort lại theo NV giảm dần =====
            rows.sort((a, b) -> {
                int na = Integer.parseInt(a.infoRight.replaceAll("[^0-9]", ""));
                int nb = Integer.parseInt(b.infoRight.replaceAll("[^0-9]", ""));
                return Integer.compare(nb, na);
            });

            // ===== 4. Cắt top =====
            if (rows.size() > LIMIT_NV) {
                rows = rows.subList(0, LIMIT_NV);
            }

            sendTop(viewer,
                    "Top Nhiệm Vụ (" + nro.utils.TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")",
                    rows);

        } catch (Exception e) {
            Log.error(TopService.class, e);
            notifyFail(viewer, "Lỗi tải Top Nhiệm Vụ!");
        }
    }


    /**
     * Top VND – SQL id/name/vnd; avatar lấy từ Player nếu online, ngược lại
     * offline
     */
    public synchronized void showTopVnd(Player viewer) {
        try (Connection con = DBService.gI().getConnectionForGame(); PreparedStatement ps = con.prepareStatement(QUERY_TOP_VND)) {

            ps.setInt(1, LIMIT_VND);
            List<Row> rows = new ArrayList<>();

            // ===== 1. Người chơi thật từ DB =====
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    long vnd = rs.getLong("vnd");

                    String right = Util.format(vnd) + " VNĐ";
                    rows.add(rowForPlayerMaybeOffline(id, name, right, right));
                }
            }

            // ===== 2. BOT online – phân phối nạp giả theo tầng =====
            List<Player> bots = Client.gI().getPlayers().stream()
                    .filter(p -> p != null && p.isBot)
                    .collect(Collectors.toList());

            if (!bots.isEmpty()) {

                // Pool giá trị nạp giả (theo SLOT, không theo BOT)
                List<Long> fakeVndPool = new ArrayList<>();

                // 1–2 bot top cao
                int topCount = Util.nextInt(1, 2);
                for (int i = 0; i < topCount; i++) {
                    fakeVndPool.add(TIER_TOP[Util.nextInt(TIER_TOP.length)]);
                }

                // 2 bot cao
                for (int i = 0; i < 2; i++) {
                    fakeVndPool.add(TIER_HIGH[Util.nextInt(TIER_HIGH.length)]);
                }

                // 3 bot trung
                for (int i = 0; i < 3; i++) {
                    fakeVndPool.add(TIER_MID[Util.nextInt(TIER_MID.length)]);
                }

                // 4 bot thấp
                for (int i = 0; i < 4; i++) {
                    fakeVndPool.add(TIER_LOW[Util.nextInt(TIER_LOW.length)]);
                }

                // Còn lại → rất thấp
                while (fakeVndPool.size() < bots.size()) {
                    fakeVndPool.add(TIER_MIN[Util.nextInt(TIER_MIN.length)]);
                }

                // Shuffle để không cố định bot nào ở tier nào
                Collections.shuffle(fakeVndPool);
                Collections.shuffle(bots);

                int count = Math.min(bots.size(), fakeVndPool.size());
                for (int i = 0; i < count; i++) {
                    Player bot = bots.get(i);
                    long fakeVnd = fakeVndPool.get(i);

                    String right = Util.format(fakeVnd) + " VNĐ";

                    rows.add(new Row(
                            (int) bot.id,
                            bot.getHead(),
                            bot.getBody(),
                            bot.getLeg(),
                            bot.name,
                            right,
                            "Nạp: " + right
                    ));
                }
            }

            // ===== 3. Sort lại theo VND giảm dần =====
            rows.sort((a, b) -> {
                long va  = Long.parseLong(a.infoRight.replaceAll("[^0-9]", ""));
                long vb = Long.parseLong(b.infoRight.replaceAll("[^0-9]", ""));
                return Long.compare(vb, va);
            });

            // ===== 4. Cắt top =====
            if (rows.size() > LIMIT_VND) {
                rows = rows.subList(0, LIMIT_VND);
            }

            sendTop(
                    viewer,
                    "Top Nạp (" + nro.utils.TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss") + ")",
                    rows
            );

        } catch (Exception e) {
            Log.error(TopService.class, e);
            notifyFail(viewer, "Lỗi tải Top Nạp!");
        }
    }




    // =============== CORE SENDER ===============

    /**
     * Gửi gói -96 cho client theo đúng format:
     *  typeTop(byte), topName(utf), count(byte),
     *  lặp từng dòng:
     *   rank(int), pId(int), headID(short), headICON(short=-1), body(short), leg(short),
     *   name(utf), info(utf), info2(utf)
     */
    private void sendTop(Player viewer, String title, List<Row> rows) throws Exception {
        if (rows == null) rows = Collections.emptyList();

        Message msg = new Message(-96);
        try {
            msg.writer().writeByte(0);           // typeTop = 0 (client cho click)
            msg.writer().writeUTF(title);
            msg.writer().writeByte((byte) Math.min(rows.size(), 127)); // client đọc sbyte

            int rank = 1;
            for (Row r : rows) {
                if (r == null) continue;

                msg.writer().writeInt(rank++);      // rank
                msg.writer().writeInt(r.pId);       // pId

                msg.writer().writeShort(r.headID);  // head
                msg.writer().writeShort((short) -1);// headICON placeholder
                msg.writer().writeShort(r.body);    // body
                msg.writer().writeShort(r.leg);     // leg

                msg.writer().writeUTF(nonNull(r.name));
                msg.writer().writeUTF(nonNull(r.infoRight));
                msg.writer().writeUTF(nonNull(r.infoDetail));
            }
            viewer.sendMessage(msg);
        } finally {
            msg.cleanup();
        }
    }

    // =============== ROW BUILDERS ===============

    /** Online: lấy đủ head/body/leg + detail đầy đủ */
    private Row rowForPlayerOnline(Player pl, String name, String rightWhenOnline, String detailWhenOnline) {
        return new Row(
            (int) pl.id,
            pl.getHead(),
            pl.getBody(),
            pl.getLeg(),
            (name != null ? name : pl.name),
            rightWhenOnline,
            detailWhenOnline
        );
    }

    /** Có thể offline: nếu không tìm thấy player online -> head fallback, body/leg=0, detail="Người chơi đang offline" */
    private Row rowForPlayerMaybeOffline(int pid, String name, String rightWhenOnline, String detailWhenOnline) {
        Player pl = Client.gI().getPlayer(pid);
        if (pl != null) {
            return rowForPlayerOnline(pl, name != null ? name : pl.name, rightWhenOnline, detailWhenOnline);
        } else {
            return new Row(
                pid,
                FALLBACK_HEAD,
                (short) 0,
                (short) 0,
                (name != null ? name : ("ID " + pid)),
                rightWhenOnline,
                "Người chơi đang offline"
            );
        }
    }

    // =============== UTILS ===============

    private static String nonNull(String s) {
        return (s == null) ? "" : s;
    }

    private void notifyFail(Player viewer, String note) {
        try {
            Service.getInstance().sendThongBaoOK(viewer.getSession(), note);
        } catch (Exception ignored) {}
    }

    // =============== DTO ===============

    /** Một dòng top (đúng format client) */
    private static final class Row {
        final int   pId;
        final short headID;
        final short body;
        final short leg;
        final String name;
        final String infoRight;
        final String infoDetail;

        Row(int pId, short headID, short body, short leg, String name, String infoRight, String infoDetail) {
            this.pId = pId;
            this.headID = headID;
            this.body = body;
            this.leg = leg;
            this.name = name;
            this.infoRight = infoRight;
            this.infoDetail = infoDetail;
        }
    }
    private List<Player> limitOnlineTop(List<Player> online) {
        if (online.size() > LIMIT_ONLINE_TOP) {
            return online.subList(0, LIMIT_ONLINE_TOP);
        }
        return online;
    }


}
