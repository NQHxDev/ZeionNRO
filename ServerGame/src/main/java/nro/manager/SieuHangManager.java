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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import nro.server.Client;
import nro.models.boss.dhvt.SieuHangBoss;
import nro.models.map.Zone;
import nro.services.func.ChangeMapService;
import nro.services.InventoryService;
import nro.services.MapService;
import nro.services.PlayerService;
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
   }

   public void checkResetTickets(Player player) {
      if (player.lastTimeReceivedTicket == 0 || Util.isAfterDay(player.lastTimeReceivedTicket)) {
         player.usedTicketSieuHang = 0;
         player.lastTimeReceivedTicket = System.currentTimeMillis();
      }
   }

   public void showOpponents(Player player) {
      System.out.println("[DEBUG_SIEU_HANG] showOpponents for " + player.name);
      checkResetTickets(player);
      List<Player> opponents = findOpponents(player);
      System.out.println("[DEBUG_SIEU_HANG] opponents found: " + (opponents != null ? opponents.size() : "NULL"));
      sendOpponentList(player, opponents);
   }

   private List<Player> findOpponents(Player player) {
      List<Player> allPlayers = new ArrayList<>(Client.gI().getPlayers());
      allPlayers.remove(player);

      List<Player> result = new ArrayList<>();
      Player lower = null, equal = null, higher = null;

      for (Player p : allPlayers) {
         if (p.isBot || p.isBoss)
            continue;
         if (p.pointSieuHang < player.pointSieuHang && (lower == null || p.pointSieuHang > lower.pointSieuHang))
            lower = p;
         else if (Math.abs(p.pointSieuHang - player.pointSieuHang) <= 50 && (equal == null || p != lower))
            equal = p;
         else if (p.pointSieuHang > player.pointSieuHang && (higher == null || p.pointSieuHang < higher.pointSieuHang))
            higher = p;
      }

      // Generate bots if missing
      if (lower == null)
         lower = createBot(player.pointSieuHang - Util.nextInt(20, 50));
      if (equal == null)
         equal = createBot(player.pointSieuHang + Util.nextInt(-10, 10));
      if (higher == null)
         higher = createBot(player.pointSieuHang + Util.nextInt(30, 80));

      result.add(lower);
      result.add(equal);
      result.add(higher);
      return result;
   }

   private Player createBot(int score) {
      Player bot = new Player();
      bot.id = -(Util.nextInt(1000000, 9999999));
      bot.name = "Võ sĩ " + Util.nextInt(100, 999);
      bot.pointSieuHang = Math.max(10, score);
      bot.head = (short) Util.nextInt(50, 100);
      bot.body = (short) Util.nextInt(50, 100);
      bot.leg = (short) Util.nextInt(50, 100);
      bot.gender = (byte) Util.nextInt(0, 2);
      bot.nPoint.power = Util.nextLong(1000000, 50000000000L);
      bot.nPoint.hpMax = Util.nextInt(50000, 2000000);
      bot.nPoint.dameg = Util.nextInt(1000, 50000);
      return bot;
   }

   private void sendOpponentList(Player player, List<Player> opponents) {
      if (opponents == null || opponents.isEmpty()) {
         System.out.println("[DEBUG_SIEU_HANG] No opponents to send for " + player.name);
         Service.getInstance().sendThongBao(player, "Không tìm thấy đối thủ!");
         return;
      }
      Message msg = null;
      try {
         System.out
               .println("[DEBUG_SIEU_HANG] Sending opponent list to " + player.name + ", size: " + opponents.size());
         msg = new Message(-115);
         DataOutputStream dos = msg.writer();
         dos.writeByte(0); // action 0: List
         dos.writeByte(opponents.size());
         for (Player opp : opponents) {
            dos.writeInt((int) opp.id);
            dos.writeUTF(opp.name);
            dos.writeUTF(Util.powerToString(opp.nPoint.power));
            dos.writeInt(opp.pointSieuHang);
            dos.writeShort(opp.getHead());
            dos.writeShort(opp.getBody());
            dos.writeShort(opp.getLeg());
         }
         dos.flush();
         player.sendMessage(msg);
         System.out.println("[DEBUG_SIEU_HANG] Packet -115 sent to " + player.name);
      } catch (Exception e) {
         System.out.println("[DEBUG_SIEU_HANG] ERROR in sendOpponentList: " + e.getMessage());
         Log.error(SieuHangManager.class, e, "Error sending opponent list");
      } finally {
         if (msg != null)
            msg.cleanup();
      }
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
         System.out.println("[DEBUG_SIEU_HANG] Top list sent to " + player.name + " (size: " + topPlayers.size() + ")");
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
      if (opponentId > 0) {
         opponent = Client.gI().getPlayer(opponentId);
      } else {
         opponent = createBot(player.pointSieuHang + Util.nextInt(-50, 50));
      }

      if (opponent == null) {
         Service.getInstance().sendThongBao(player, "Đối thủ không hợp lệ!");
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

      try {
         Zone zone = MapService.gI().getMapWithRandZone(113);
         if (zone == null) {
            Service.getInstance().sendThongBao(player, "Đường truyền võ đài đang bận!");
            return;
         }

         SieuHangBoss boss = new SieuHangBoss(player, opponent);
         activeMatches.put(player, boss);

         ChangeMapService.gI().changeMapNonSpaceship(player, zone.map.mapId, 335, 264);
         boss.zone = zone;
         boss.location.x = 500;
         boss.location.y = 336;
         boss.joinMap();

         PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_PVP);
         boss.changeToAttack();

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
      int plus = 150;
      int minus = 100;

      player.pointSieuHang += plus;
      updateOpponentScore(boss.opponentId, -minus);

      Service.getInstance().sendThongBao(player, "THẮNG! Bạn nhận được " + plus + " điểm.");
      endMatch(player, boss);
   }

   private void lose(Player player, SieuHangBoss boss) {
      int minus = 100;
      int plus = 150;

      player.pointSieuHang -= minus;
      if (player.pointSieuHang < 0)
         player.pointSieuHang = 0;
      updateOpponentScore(boss.opponentId, plus);

      Service.getInstance().sendThongBao(player, "BẠI! Bạn bị trừ " + minus + " điểm.");
      endMatch(player, boss);
   }

   private void updateOpponentScore(long opponentId, int delta) {
      if (opponentId <= 0)
         return;
      Player opp = Client.gI().getPlayer((int) opponentId);
      if (opp != null) {
         opp.pointSieuHang += delta;
         if (opp.pointSieuHang < 0)
            opp.pointSieuHang = 0;
         Service.getInstance().sendThongBao(opp,
               "Điểm Siêu Hạng của bạn vừa " + (delta > 0 ? "tăng " : "giảm ") + Math.abs(delta) + "!");
      } else {
         GameScheduler.SCHED.execute(() -> {
            try (Connection conn = DBService.gI().getConnection();
                  PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO sieu_hang (player_id, point) VALUES (?, 100 + ?) " +
                              "ON DUPLICATE KEY UPDATE point = GREATEST(0, point + ?)")) {
               ps.setLong(1, opponentId);
               ps.setInt(2, delta);
               ps.setInt(3, delta);
               ps.executeUpdate();
            } catch (Exception e) {
               Log.error(SieuHangManager.class, e, "Error updating offline opponent score");
            }
         });
      }
   }

   private void endMatch(Player player, SieuHangBoss boss) {
      boss.leaveMap();
      PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
      PlayerService.gI().hoiSinh(player);
      Service.getInstance().sendThongBao(player, "Trận đấu kết thúc!");
      GameScheduler.SCHED.schedule(() -> {
         if (player.zone != null && player.zone.map.mapId == 113) {
            ChangeMapService.gI().changeMapNonSpaceship(player, 52, 335, 264);
         }
      }, 2, TimeUnit.SECONDS);
   }

   public void persistScores() {
      Log.log("Persisting Sieu Hang scores to DB...");
      String sql = "INSERT INTO sieu_hang (player_id, point, used_ticket, last_time_ticket) VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE point = VALUES(point), used_ticket = VALUES(used_ticket), last_time_ticket = VALUES(last_time_ticket)";
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
            ps.addBatch();
            count++;
         }
         if (count > 0) {
            ps.executeBatch();
            conn.commit();
            Log.success("Persisted " + count + " Sieu Hang scores to sieu_hang table successfully.");
         }
      } catch (Exception e) {
         Log.error(SieuHangManager.class, e, "Error persisting Sieu Hang scores");
      }
   }

}
