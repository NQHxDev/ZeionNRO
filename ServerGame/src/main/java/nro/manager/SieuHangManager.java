package nro.manager;

import nro.core.GameScheduler;
import nro.models.player.Player;
import nro.network.io.Message;
import nro.services.Service;
import nro.utils.Log;
import nro.utils.Util;
import nro.jdbc.DBService;

import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import nro.server.Manager;
import nro.services.ItemService;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nro.server.Client;
import nro.models.boss.dhvt.SieuHangBoss;
import nro.models.map.Zone;
import nro.services.func.ChangeMapService;
import nro.services.InventoryService;
import nro.services.MapService;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;

public class SieuHangManager {

   private static SieuHangManager instance;

   private final Map<Player, SieuHangBoss> activeMatches = new HashMap<>();

   public static SieuHangManager gI() {
      if (instance == null) {
         instance = new SieuHangManager();
      }

      return instance;
   }

   public void init() {
      GameScheduler.SCHED.scheduleAtFixedRate(() -> {
         try {
            persistScores();
         } catch (Exception e) {
            Log.error(SieuHangManager.class, e, "Error in SieuHangManager persist task");
         }
      }, 10, 10, TimeUnit.MINUTES);

      GameScheduler.SCHED.scheduleAtFixedRate(() -> {
         try {
            updateMatches();
         } catch (Exception e) {
            Log.error(SieuHangManager.class, e, "Error in SieuHangManager update task");
         }
      }, 1, 1, TimeUnit.SECONDS);

      try (Connection conn = DBService.gI().getConnection();
            PreparedStatement ps = conn
                  .prepareStatement("ALTER TABLE sieu_hang ADD last_time_reset BIGINT DEFAULT 0")) {
         ps.executeUpdate();
      } catch (SQLException e) {
         if (e.getErrorCode() != 1060) { // 1060 = Duplicate column name
            Log.error(SieuHangManager.class, e, "Error adding last_time_reset column");
         }
      } catch (Exception e) {
         Log.error(SieuHangManager.class, e, "Error in SieuHangManager DB init");
      }
   }

   public void checkResetTickets(Player player) {
      if (Util.isAfterDay(player.lastTimeSieuHangReset)) {
         player.usedTicketSieuHang = 0;
         player.lastTimeSieuHangReset = System.currentTimeMillis();
      }
   }

   public void showOpponents(Player player) {
      checkResetTickets(player);
      List<Player> opponents = findOpponents(player);
      if (opponents == null || opponents.isEmpty()) {
         Service.getInstance().sendThongBao(player, "Không tìm thấy đối thủ!");
         return;
      }
      player.opponentsSieuHang = opponents;
      String npcText = "Danh sách đối thủ dành cho bạn:\n";
      int size = Math.min(opponents.size(), 5);
      String[] menuOptions = new String[size];
      for (int i = 0; i < size; i++) {
         Player opp = opponents.get(i);
         menuOptions[i] = opp.name + "\n[" + Util.powerToString(opp.nPoint.power) + "]\nĐiểm: " + opp.pointSieuHang;
      }
      NpcService.gI().createMenu(player, ConstNpc.MENU_SIEU_HANG, ConstNpc.TRONG_TAI, -1, npcText, menuOptions);
   }

   private List<Player> findOpponents(Player player) {
      List<Player> result = new ArrayList<>();
      try (Connection conn = DBService.gI().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                  "SELECT p.id, p.name, p.gender, p.head, p.items_body, IFNULL(sh.point, 100) AS point, pp.power, pp.hp_goc, pp.dame_goc "
                        +
                        "FROM player p " +
                        "INNER JOIN player_point pp ON p.id = pp.player_id " +
                        "LEFT JOIN sieu_hang sh ON p.id = sh.player_id " +
                        "WHERE p.id != ? AND IFNULL(sh.point, 100) >= 20 " +
                        "ORDER BY ABS(IFNULL(sh.point, 100) - ?) ASC LIMIT 15")) {
         ps.setLong(1, player.id);
         ps.setInt(2, player.pointSieuHang);
         try (ResultSet rs = ps.executeQuery()) {
            List<Player> dbPlayers = new ArrayList<>();
            while (rs.next()) {
               Player p = new Player();
               p.id = rs.getInt("id");
               p.name = rs.getString("name");
               p.gender = rs.getByte("gender");
               p.head = rs.getShort("head");

               // Parse body and leg from items_body JSON
               String itemsBody = rs.getString("items_body");
               if (itemsBody != null) {
                  Pattern pattern = Pattern.compile("\"temp_id\":\\s*(-?\\d+)");
                  Matcher matcher = pattern.matcher(itemsBody);

                  short[] tempIds = new short[17];
                  for (int i = 0; i < 17; i++) {
                     if (matcher.find()) {
                        tempIds[i] = Short.parseShort(matcher.group(1));
                     } else {
                        tempIds[i] = -1;
                     }
                  }

                  // Priority 1: Cai Trang (Slot 5)
                  boolean hasCaiTrang = false;
                  if (tempIds[5] != -1) {
                     nro.models.item.CaiTrang ct = Manager.getCaiTrangByItemId(tempIds[5]);
                     if (ct != null) {
                        if (ct.getID()[0] != -1)
                           p.head = (short) ct.getID()[0];
                        if (ct.getID()[1] != -1)
                           p.body = (short) ct.getID()[1];
                        if (ct.getID()[2] != -1)
                           p.leg = (short) ct.getID()[2];
                        hasCaiTrang = true;
                     }
                  }

                  if (!hasCaiTrang) {
                     // Priority 2: Shirt (Slot 0)
                     if (tempIds[0] != -1) {
                        nro.models.item.ItemTemplate it = ItemService.gI().getTemplate(tempIds[0]);
                        if (it != null)
                           p.body = it.part;
                     }
                     // Priority 3: Pants (Slot 1)
                     if (tempIds[1] != -1) {
                        nro.models.item.ItemTemplate it = ItemService.gI().getTemplate(tempIds[1]);
                        if (it != null)
                           p.leg = it.part;
                     }
                  }
               }

               if (p.body <= 0) {
                  if (p.gender == 0) {
                     p.body = 0;
                     p.leg = 6;
                  } else if (p.gender == 1) {
                     p.body = 1;
                     p.leg = 7;
                  } else {
                     p.body = 2;
                     p.leg = 8;
                  }
               }
               if (p.leg <= 0) {
                  if (p.gender == 0)
                     p.leg = 6;
                  else if (p.gender == 1)
                     p.leg = 7;
                  else
                     p.leg = 8;
               }

               p.pointSieuHang = rs.getInt("point");
               p.nPoint.power = rs.getLong("power");
               p.nPoint.hpMax = rs.getDouble("hp_goc");
               p.nPoint.hpg = rs.getDouble("hp_goc");
               p.nPoint.hp = p.nPoint.hpMax;
               p.nPoint.dameg = rs.getDouble("dame_goc");
               dbPlayers.add(p);
            }

            // Select up to 5 closest players
            for (int i = 0; i < Math.min(dbPlayers.size(), 5); i++) {
               result.add(dbPlayers.get(i));
            }
         }
      } catch (Exception e) {
         Log.error(SieuHangManager.class, e, "Error finding opponents in DB");
      }

      if (result.isEmpty()) {
         for (int i = 0; i < 3; i++) {
            result.add(createBot(player.pointSieuHang + Util.nextInt(-20, 50)));
         }
      }
      return result;
   }

   private Player createBot(int score) {
      Player bot = new Player();
      bot.id = -(Util.nextInt(1000000, 9999999));
      bot.name = "Võ sĩ " + Util.nextInt(100, 999);
      bot.pointSieuHang = Math.max(10, score);
      bot.gender = (byte) Util.nextInt(0, 2);

      // Default outfits based on gender
      if (bot.gender == 0) { // Earth
         bot.head = (short) (64 + Util.nextInt(0, 2));
         bot.body = 0;
         bot.leg = 6;
      } else if (bot.gender == 1) { // Namec
         bot.head = (short) (39 + Util.nextInt(0, 2));
         bot.body = 1;
         bot.leg = 7;
      } else { // Saiyan
         bot.head = (short) (60 + Util.nextInt(0, 2));
         bot.body = 2;
         bot.leg = 8;
      }

      bot.nPoint.power = Util.nextLong(10000000, 50000000000L);
      bot.nPoint.hpMax = Util.nextInt(100000, 5000000);
      bot.nPoint.dameg = Util.nextInt(5000, 100000);
      bot.isBot = true;
      return bot;
   }

   // Todo: Xử lý việc hiển thị top 50 thôi nếu người chơi không nằm trong top 50
   // thì chỉ hiển thị top hiện tại và 50 người
   public void sendTopSieuHang(Player player) {
      Message msg = null;
      try {
         msg = new Message(-96);
         DataOutputStream dos = msg.writer();
         dos.writeByte(1); // typeTop (1 to disable Challenge button)
         dos.writeUTF("Rank Siêu Hạng");

         List<Player> topPlayers = TopManager.getInstance().getListSieuHang();
         dos.writeByte(topPlayers.size());

         for (int i = 0; i < topPlayers.size(); i++) {
            Player p = topPlayers.get(i);
            dos.writeInt(i + 1); // rank
            dos.writeInt((int) p.id);
            dos.writeShort(p.getHead());
            dos.writeShort(-1); // headIcon
            dos.writeShort(p.getBody());
            dos.writeShort(p.getLeg());
            dos.writeUTF(p.name);
            dos.writeUTF("Sức mạnh: " + Util.powerToString(p.nPoint.power));
            dos.writeUTF("Điểm: " + p.pointSieuHang); // This will go to info2 in Panel.cs
         }
         dos.flush();
         player.sendMessage(msg);
      } catch (Exception e) {
         Log.error(SieuHangManager.class, e, "Error sending top sieu hang");
      } finally {
         if (msg != null)
            msg.cleanup();
      }
   }

   public void challenge(Player player, int opponentId) {
      checkResetTickets(player);
      if (activeMatches.containsKey(player)) {
         Service.getInstance().sendThongBao(player, "Bạn đang trong trận đấu!");
         return;
      }

      Player opponent = null;
      if (player.opponentsSieuHang != null) {
         for (Player p : player.opponentsSieuHang) {
            if (p.id == opponentId) {
               opponent = p;
               break;
            }
         }
      }

      if (opponent == null) {
         if (opponentId > 0) {
            opponent = Client.gI().getPlayer(opponentId);
         } else {
            opponent = createBot(player.pointSieuHang + Util.nextInt(-50, 50));
         }
      }

      if (opponent == null) {
         Service.getInstance().sendThongBao(player, "Đối thủ không hợp lệ!");
         return;
      }

      try {
         Zone zone = null;
         nro.models.map.Map map = MapService.gI().getMapById(113);
         if (map != null) {
            for (Zone z : map.zones) {
               if (z.zoneId >= 4 && z.getPlayers().size() == 0) {
                  zone = z;
                  break;
               }
            }
         }

         if (zone == null) {
            Service.getInstance().sendThongBao(player, "Hiện tại không có võ đài nào trống (Khu 4 trở đi)!");
            return;
         }

         if (player.usedTicketSieuHang >= 20) {
            Service.getInstance().sendThongBao(player, "Bạn đã hết lượt thi đấu hôm nay!");
            return;
         }

         if (!InventoryService.gI().findAndRemoveItemBag(player, 2025, 1)) {
            Service.getInstance().sendThongBao(player, "Bạn cần có Phiếu thi đấu để tham gia!");
            return;
         }
         player.usedTicketSieuHang++;

         SieuHangBoss boss = new SieuHangBoss(player, opponent);
         activeMatches.put(player, boss);

         ChangeMapService.gI().changeMap(player, zone, 300, 264);

         boss.zone = zone;
         boss.location.x = 450;
         boss.location.y = 264;
         boss.joinMap();

         PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_PVP);

         Service.getInstance().sendThongBao(player, "Trận đấu sẽ bắt đầu sau 5 giây!");
         for (int i = 5; i > 0; i--) {
            final int count = i;
            GameScheduler.SCHED.schedule(() -> {
               Service.getInstance().chat(player, String.valueOf(count));
            }, 5 - i, TimeUnit.SECONDS);
         }

         GameScheduler.SCHED.schedule(() -> {
            Service.getInstance().chat(player, "CHIẾN!");
            boss.changeToAttack();
         }, 5, TimeUnit.SECONDS);

         Service.getInstance().chat(player, "Tới đây đi " + opponent.name + "!");
      } catch (Exception e) {
         Log.error(SieuHangManager.class, e, "Error starting challenge");
         Service.getInstance().sendThongBao(player, "Có lỗi xảy ra khi bắt đầu trận đấu!");
      }
   }

   private void updateMatches() {
      List<Player> toRemove = new ArrayList<>();
      for (Map.Entry<Player, SieuHangBoss> entry : activeMatches.entrySet()) {
         Player p = entry.getKey();
         SieuHangBoss b = entry.getValue();

         if (p.isDie()) {
            lose(p, b);
            toRemove.add(p);
         } else if (b.isDie()) {
            win(p, b);
            toRemove.add(p);
         } else if (p.zone == null || p.zone.map.mapId != 113) {
            toRemove.add(p);
            b.leaveMap();
         }
      }
      for (Player p : toRemove)
         activeMatches.remove(p);
   }

   private void win(Player player, SieuHangBoss boss) {
      int plus = 20;
      int minus = 10;

      updateScore(player.id, plus);
      updateScore(boss.opponentId, -minus);

      Service.getInstance().sendThongBao(player, "THẮNG! Bạn nhận được " + plus + " điểm.");
      endMatch(player, boss);
   }

   private void lose(Player player, SieuHangBoss boss) {
      int minus = 10;
      int plus = 20;

      updateScore(player.id, -minus);
      updateScore(boss.opponentId, plus);

      Service.getInstance().sendThongBao(player, "BẠI! Bạn bị trừ " + minus + " điểm.");
      endMatch(player, boss);
   }

   private void updateScore(long playerId, int delta) {
      if (playerId <= 0)
         return;
      Player p = Client.gI().getPlayer((int) playerId);
      if (p != null) {
         p.pointSieuHang += delta;
         if (p.pointSieuHang < 0)
            p.pointSieuHang = 0;
         Service.getInstance().sendThongBao(p,
               "Điểm Siêu Hạng của bạn vừa " + (delta > 0 ? "tăng " : "giảm ") + Math.abs(delta) + "!");
      }
      // Always persist to DB immediately for real-time ranking
      GameScheduler.SCHED.execute(() -> {
         try (Connection conn = DBService.gI().getConnection();
               PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO sieu_hang (player_id, point) VALUES (?, 100 + ?) " +
                           "ON DUPLICATE KEY UPDATE point = GREATEST(0, point + ?)")) {
            ps.setLong(1, playerId);
            ps.setInt(2, delta);
            ps.setInt(3, delta);
            ps.executeUpdate();
         } catch (Exception e) {
            Log.error(SieuHangManager.class, e, "Error updating score for player " + playerId);
         }
      });
   }

   private void endMatch(Player player, SieuHangBoss boss) {
      boss.leaveMap();
      nro.models.boss.BossManager.gI().removeBoss(boss);
      PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
      PlayerService.gI().hoiSinh(player);
      Service.getInstance().sendThongBao(player, "Trận đấu kết thúc!");
      GameScheduler.SCHED.schedule(() -> {
         if (player.zone != null && player.zone.map.mapId == 113) {
            Zone lobby = MapService.gI().getZoneJoinByMapIdAndZoneId(player, 113, 0);
            if (lobby != null) {
               ChangeMapService.gI().changeMap(player, lobby, 335, 360);
            } else {
               ChangeMapService.gI().changeMapNonSpaceship(player, 52, 335, 360);
            }
         }
      }, 1, TimeUnit.SECONDS);
   }

   public void persistScores() {
      String sql = "INSERT INTO sieu_hang (player_id, point, used_ticket, last_time_ticket, last_time_reset) VALUES (?, ?, ?, ?, ?) "
            +
            "ON DUPLICATE KEY UPDATE point = VALUES(point), used_ticket = VALUES(used_ticket), last_time_ticket = VALUES(last_time_ticket), last_time_reset = VALUES(last_time_reset)";
      try (Connection conn = DBService.gI().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
         conn.setAutoCommit(false);
         List<Player> players = Client.gI().getPlayers();
         int count = 0;
         for (Player p : players) {
            if (p.isBot || !p.loaded)
               continue;
            ps.setLong(1, p.id);
            ps.setInt(2, p.pointSieuHang);
            ps.setInt(3, p.usedTicketSieuHang);
            ps.setLong(4, p.lastTimeReceivedTicket);
            ps.setLong(5, p.lastTimeSieuHangReset);
            ps.addBatch();
            count++;
         }
         if (count > 0) {
            ps.executeBatch();
            conn.commit();
         }
      } catch (Exception e) {
         Log.error(SieuHangManager.class, e, "Error persisting Sieu Hang scores");
      }
   }

}
