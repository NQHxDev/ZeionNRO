package nro.models.clan;

import lombok.Getter;
import lombok.Setter;
import nro.jdbc.DBService;
import nro.models.map.Map;
import nro.models.map.Zone;
import nro.models.map.dungeon.SnakeRoad;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.mob.Mob;
import nro.services.ClanService;
import nro.models.map.phoban.DoanhTrai;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import nro.models.player.Player;
import nro.server.Client;
import nro.server.Manager;
import nro.services.MapService;
import nro.services.Service;
import nro.network.io.Message;
import nro.utils.Log;
import nro.utils.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Clan {

   public static int NEXT_ID = 0;
   private static final Gson gson = new Gson();

   public int clanMessageId = 0;

   private final List<ClanMessage> clanMessages;

   public static final byte LEADER = 0;
   public static final byte DEPUTY = 1;
   public static final byte MEMBER = 2;

   public int id;

   public int imgId;

   public String name;

   public String slogan;

   public int createTime;

   public double powerPoint;

   public byte maxMember;

   public int level;

   /**
    * Tổng capsule bang
    */
   public int clanPoint;

   public boolean active;

   public final List<ClanMember> members;
   public final List<Player> membersInGame;

   /**
    * Đã đi doanh trại trong ngày
    */
   public boolean haveGoneDoanhTrai;
   public DoanhTrai doanhTrai;
   public Player playerOpenDoanhTrai;
   public long timeOpenDoanhTrai;

   public BanDoKhoBau banDoKhoBau;
   public SnakeRoad snakeRoad;
   public Player playerOpenBanDoKhoBau;
   public long timeOpenBanDoKhoBau;
   public boolean isLeader;
   @Setter
   @Getter
   public Buff buff;
   @Getter
   private Zone clanArea;

   public Clan() {
      this.id = NEXT_ID++;
      this.name = "";
      this.slogan = "";
      this.maxMember = 10;
      this.createTime = (int) (System.currentTimeMillis() / 1000);
      this.members = new ArrayList<>();
      this.membersInGame = new ArrayList<>();
      this.clanMessages = new ArrayList<>();
      initialize();
   }

   private void initialize() {
      Map map = MapService.gI().getMapById(153);
      this.clanArea = new Zone(map, 0, 50);
      Zone z = map.zones.get(0);
      if (z != null) {
         for (Mob m : z.mobs) {
            Mob mob = new Mob(m);
            mob.zone = clanArea;
            clanArea.addMob(mob);
         }
      }
   }

   public ClanMember getLeader() {
      for (ClanMember cm : members) {
         if (cm.role == LEADER) {
            return cm;
         }
      }
      ClanMember cm = new ClanMember();
      cm.name = "Bang chủ";
      return cm;
   }

   public byte getRole(Player player) {
      for (ClanMember cm : members) {
         if (cm.id == player.id) {
            return cm.role;
         }
      }
      return -1;
   }

   public boolean isLeader(Player player) {
      for (ClanMember cm : members) {
         if (cm.id == player.id && cm.role == LEADER) {
            return true;
         }
      }
      return false;
   }

   public boolean isDeputy(Player player) {
      for (ClanMember cm : members) {
         if (cm.id == player.id && cm.role == DEPUTY) {
            return true;
         }
      }
      return false;
   }

   public void addSMTNClan(Player plOri, double param) {
      for (Player pl : this.membersInGame) {
         if (!plOri.equals(pl) && plOri.zone.equals(pl.zone)) {
            Service.getInstance().addSMTN(pl, (byte) 1, param, false);
         }
      }
   }

   public void sendMessageClan(ClanMessage cmg) {
      Message msg;
      try {
         msg = Message.create(-51);
         msg.writer().writeByte(cmg.type);
         msg.writer().writeInt(cmg.id);
         msg.writer().writeInt(cmg.playerId);
         if (cmg.type == 2) {
            msg.writer().writeUTF(cmg.playerName + " (" + Util.powerToString(cmg.playerPower) + ")");
         } else {
            msg.writer().writeUTF(cmg.playerName);
         }
         msg.writer().writeByte(cmg.role);
         msg.writer().writeInt(cmg.time);
         if (cmg.type == 0) {
            msg.writer().writeUTF(cmg.text);
            msg.writer().writeByte(cmg.color);
         } else if (cmg.type == 1) {
            msg.writer().writeByte(cmg.receiveDonate);
            msg.writer().writeByte(cmg.maxDonate);
            msg.writer().writeByte(cmg.isNewMessage);
         }
         for (Player pl : this.membersInGame) {
            pl.sendMessage(msg);
         }
         msg.cleanup();
      } catch (Exception e) {
      }
   }

   public void addClanMessage(ClanMessage cmg) {
      this.clanMessages.add(0, cmg);
   }

   public ClanMessage getClanMessage(int clanMessageId) {
      for (ClanMessage cmg : this.clanMessages) {
         if (cmg.id == clanMessageId) {
            return cmg;
         }
      }
      return null;
   }

   public List<ClanMessage> getCurrClanMessages() {
      List<ClanMessage> list = new ArrayList<>();
      if (this.clanMessages.size() <= 20) {
         list.addAll(this.clanMessages);
      } else {
         for (int i = 0; i < 20; i++) {
            list.add(this.clanMessages.get(i));
         }
      }
      return list;
   }

   public void sendRemoveClanForAllMember() {
      for (Player pl : this.membersInGame) {
         if (pl != null) {
            ClanService.gI().sendRemoveClan(pl);
         }
      }
   }

   public void sendMyClanForAllMember() {
      for (Player pl : this.membersInGame) {
         if (pl != null) {
            ClanService.gI().sendMyClan(pl);
         }
      }
   }

   public void sendRemoveForAllMember() {
      for (Player pl : this.membersInGame) {
         if (pl != null) {
            Service.getInstance().sendThongBao(pl, "Bang Hội của bạn đã bị giải tán.");
         }
      }
   }

   public void sendFlagBagForAllMember() {
      for (Player pl : this.membersInGame) {
         if (pl != null) {
            Service.getInstance().sendFlagBag(pl);
            // SendEffect.getInstance().SendEffDanhHieu(pl);
         }
      }
   }

   public void addMemberOnline(Player player) {
      this.membersInGame.add(player);
   }

   public void removeMemberOnline(ClanMember cm, Player player) {
      if (player != null) {
         this.membersInGame.remove(player);
      }
      if (cm != null) {
         for (int i = this.membersInGame.size() - 1; i >= 0; i--) {
            if (this.membersInGame.get(i).id == cm.id) {
               this.membersInGame.remove(i);
               break;
            }
         }
      }
   }

   public Player getPlayerOnline(int playerId) {
      for (Player player : this.membersInGame) {
         if (player.id == playerId) {
            return player;
         }
      }
      return null;
   }

   // load db danh sách member
   public void addClanMember(ClanMember cm) {
      this.members.add(cm);
   }

   // thêm vào khi player tạo mới clan or mới vào clan
   public void addClanMember(Player player, byte role) {
      ClanMember cm = new ClanMember(player, this, role);
      this.members.add(cm);
      player.clanMember = cm;
   }

   // xóa khi member rời clan or bị kích
   public void removeClanMember(ClanMember cm) {
      this.members.remove(cm);
   }

   public byte getCurrMembers() {
      return (byte) this.members.size();
   }

   public List<ClanMember> getMembers() {
      return this.members;
   }

   public ClanMember getClanMember(int memberId) {
      for (ClanMember cm : members) {
         if (cm.id == memberId) {
            return cm;
         }
      }
      return null;
   }

   public void reloadClanMember() {
      for (ClanMember cm : this.members) {
         Player pl = Client.gI().getPlayer(cm.id);
         if (pl != null) {
            cm.powerPoint = pl.nPoint.power;
         }
      }
   }

   public void insert() {
      String memberJson = gson.toJson(this.members);
      int clanId = this.id;
      String clanName = this.name;
      String clanSlogan = this.slogan;
      int clanImgId = this.imgId;
      double clanPowerPoint = this.powerPoint;
      byte clanMaxMember = this.maxMember;
      int clanClanPoint = this.clanPoint;
      int clanLevel = this.level;

      Manager.run(() -> {
         try (Connection con = DBService.gI().getConnectionForClan();
               PreparedStatement ps = con.prepareStatement("insert into clan_sv" + Manager.SERVER
                     + "(id, name, slogan, img_id, power_point, max_member, clan_point, level, members) "
                     + "values (?,?,?,?,?,?,?,?,?)")) {
            ps.setInt(1, clanId);
            ps.setString(2, clanName);
            ps.setString(3, clanSlogan);
            ps.setInt(4, clanImgId);
            ps.setDouble(5, clanPowerPoint);
            ps.setByte(6, clanMaxMember);
            ps.setInt(7, clanClanPoint);
            ps.setInt(8, clanLevel);
            ps.setString(9, memberJson);
            ps.executeUpdate();
         } catch (Exception e) {
            Log.error(Clan.class, e, "Có lỗi khi insert clan vào db");
         }
      });
   }

   public void update() {
      String memberJson = gson.toJson(this.members);
      String clanSlogan = this.slogan;
      int clanImgId = this.imgId;
      double clanPowerPoint = this.powerPoint;
      byte clanMaxMember = this.maxMember;
      int clanClanPoint = this.clanPoint;
      int clanLevel = this.level;
      int clanId = this.id;

      Manager.run(() -> {
         Connection con = null;
         PreparedStatement ps = null;
         try {
            con = DBService.gI().getConnectionForClan();
            ps = con.prepareStatement("update clan_sv" + Manager.SERVER
                  + " set slogan = ?, img_id = ?, power_point = ?, max_member = ?, clan_point = ?, "
                  + "level = ?, members = ? where id = ? limit 1");

            ps.setString(1, clanSlogan);
            ps.setInt(2, clanImgId);
            ps.setDouble(3, clanPowerPoint);
            ps.setByte(4, clanMaxMember);
            ps.setInt(5, clanClanPoint);
            ps.setInt(6, clanLevel);
            ps.setString(7, memberJson);
            ps.setInt(8, clanId);
            ps.executeUpdate();
         } catch (Exception e) {
            Log.error(Clan.class, e, "Có lỗi khi update clan vào db");
         } finally {
            if (ps != null) {
               try {
                  ps.close();
               } catch (Exception e) {
               }
            }
            DBService.gI().release(con);
         }
      });
   }

}
