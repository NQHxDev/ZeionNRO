package nro.server.io;

import io.netty.channel.Channel;
import nro.network.netty.NettySession;
import nro.network.io.Message;
import nro.data.DataGame;
import nro.jdbc.daos.GodGK;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.resources.Resources;
import nro.server.Client;
import nro.server.Controller;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.server.model.AntiLogin;
import nro.services.*;
import nro.utils.Util;
import lombok.Setter;
import nro.services.ItemService;
import nro.services.ItemTimeService;
import nro.services.Service;
import nro.services.TaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Session extends NettySession {

   private static final Map<String, AntiLogin> ANTILOGIN = new HashMap<>();
   private static final int TIME_WAIT_READ_MESSAGE = 180000;

   public boolean clientVerified = false;
   public boolean logCheck;

   public Player player;
   public byte timeWait = 50;

   Controller controller;

   public String ipAddress;
   public boolean isAdmin;
   public int userId;
   public String uu;
   public String pp;

   public int typeClient;
   public byte zoomLevel;
   public boolean isSetClientType;

   public long lastTimeLogout;
   public boolean loginSuccess, joinedGame, dataLoadFailed;

   public long lastTimeReadMessage;

   public boolean actived;

   public int goldBar;
   public List<Item> itemsReward;
   public String dataReward;
   public int ruby;
   public int diemTichNap;
   public int server;
   public int version;
   public int vnd;
   public int tong_nap;

   @Setter
   public boolean logging;

   public Session(Channel channel, int id) {
      super(channel, id);
      this.ipAddress = getIPString();
      this.controller = Controller.getInstance();
      Client.gI().put(this);
   }

   // Constructor for offline session simulation if needed
   public Session() {
      super(null, -1);
      this.loginSuccess = true;
      this.joinedGame = true;
      this.actived = true;
      this.itemsReward = new ArrayList<>();
      Client.gI().put(this);
   }

   public void update() {
      if (Util.canDoWithTime(lastTimeReadMessage, TIME_WAIT_READ_MESSAGE)) {
         // Client.gI().kickSession(this);
      }
   }

   public void initItemsReward() {
      try {
         this.itemsReward = new ArrayList<>();
         String[] itemsReward = dataReward.split(";");
         for (String itemInfo : itemsReward) {
            if (itemInfo == null || itemInfo.equals("")) {
               continue;
            }
            String[] subItemInfo = itemInfo.replaceAll("[{}\\[\\]]", "").split("\\|");
            String[] baseInfo = subItemInfo[0].split(":");
            int itemId = Integer.parseInt(baseInfo[0]);
            int quantity = Integer.parseInt(baseInfo[1]);
            Item item = ItemService.gI().createNewItem((short) itemId, quantity);
            if (subItemInfo.length == 2) {
               String[] options = subItemInfo[1].split(",");
               for (String opt : options) {
                  if (opt == null || opt.equals("")) {
                     continue;
                  }
                  String[] optInfo = opt.split(":");
                  int tempIdOption = Integer.parseInt(optInfo[0]);
                  int param = Integer.parseInt(optInfo[1]);
                  item.itemOptions.add(new ItemOption(tempIdOption, param));
               }
            }
            this.itemsReward.add(item);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public void sendMessage(Message msg) {
      if (isConnected()) {
         super.sendMessage(msg);
      }
   }

   public void disconnect() {
      super.disconnect();
      this.player = null;
   }

   public void setClientType(Message msg) {
      try {
         if (!isSetClientType) {
            this.typeClient = (msg.reader().readByte());
            this.zoomLevel = msg.reader().readByte();
            msg.reader().readBoolean();
            msg.reader().readInt();
            msg.reader().readInt();
            msg.reader().readBoolean();
            msg.reader().readBoolean();
            String platform = msg.reader().readUTF();
            String[] arrPlatform = platform.split("\\|");
            this.version = Integer.parseInt(arrPlatform[1].replaceAll("\\.", ""));
            isSetClientType = true;
            Resources.gI().sendResVersion(this);
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         msg.cleanup();
      }
      DataGame.sendLinkIP(this);
   }

   public boolean isVersionAbove(int version) {
      return this.version >= version;
   }

   public String getName() {
      if (this.player != null) {
         return this.player.name;
      } else {
         return ipAddress;
      }
   }

   public void login(String username, String password) {
      if (Maintenance.isRuning || !ServerManager.gI().getLogin().isConnected()) {
         Service.getInstance().sendThongBaoOK(this, "Máy chủ đang tiến hành bảo trì, vui lòng thử lại sau!");
         return;
      }
      if (!isSetClientType || logging || loginSuccess) {
         return;
      }
      logging = true;
      AntiLogin al = ANTILOGIN.get(this.ipAddress);
      if (al == null) {
         al = new AntiLogin();
         ANTILOGIN.put(this.ipAddress, al);
      }
      if (!al.canLogin()) {
         Service.getInstance().sendThongBaoOK(this, al.getNotifyCannotLogin());
         return;
      }
      if (!this.isAdmin && Client.gI().getPlayers().size() >= Manager.MAX_PLAYER) {
         Service.getInstance().sendThongBaoOK(this,
               "Máy chủ hiện đang quá tải, cư dân vui lòng di chuyển sang máy chủ khác.");
         return;
      }
      if (this.player != null) {
         return;
      } else {
         try {
            this.uu = username;
            this.pp = password;
            ServerManager.gI().getLogin().getService().login(Manager.SERVER, getId(), username, password);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public void finishUpdate() {
      if (loginSuccess && !joinedGame) {
         player = GodGK.loadPlayer(this);
         if (!dataLoadFailed) {
            if (player != null) {
               enter();
            } else {
               Service.getInstance().switchToCreateChar(this);
            }
         } else {
            Service.getInstance().sendThongBaoOK(this, "Lỗi tải dữ liệu vui lòng báo với admin.");
         }
      }
   }

   public void enter() {
      if (!joinedGame) {
         joinedGame = true;
         player.nPoint.initPowerLimit();
         if (player.pet != null) {
            player.pet.nPoint.initPowerLimit();
         }
         player.nPoint.calPoint();
         player.nPoint.setHp(player.nPoint.hp);
         player.nPoint.setMp(player.nPoint.mp);
         if (player.zone == null) {
            player.zone = MapService.gI().getMapCanJoin(player, player.gender + 21);
         }
         if (player.zone != null) {
            player.zone.addPlayer(player);
         }
         player.loaded = true;
         if (player.pet != null) {
            player.pet.nPoint.calPoint();
            player.pet.nPoint.setHp(player.pet.nPoint.hp);
            player.pet.nPoint.setMp(player.pet.nPoint.mp);
         }

         player.setSession(this);
         Client.gI().put(player);
         controller.sendInfo(this);
         Service.getInstance().player(player);
         Service.getInstance().Send_Caitrang(player);

         Service.getInstance().sendFlagBag(player);
         player.playerSkill.sendSkillShortCut();
         ItemTimeService.gI().sendAllItemTime(player);

         TaskService.gI().sendInfoCurrentTask(player);
         PhucLoi.gI().Send_PhucLoi(player);
         BangTin.gI().Send_BangTin(player);
         TamBao.gI().Send_TamBao(player);
         TamBao.gI().Send_MocTamBao(player);
         TamBao.gI().Send_QuayThuong(player);
         KhamNgoc.gI().Send_KhamNgocTemplate(player);
         KhamNgoc.gI().Send_KhamNgoc_Player(player);
         RuongSuuTam.gI().Send_RuongSuuTamTemplate(player);
         RuongSuuTam.gI().SendAllRuong(player);
         PhongThiNghiem.gI().Send_PhongThiNghiem_Template(player);
         PhongThiNghiem.gI().Send_PhongThiNghiem_Player(player);
         GameDuDoan.gI().Send_TaiXiu(player);
         GameDuDoan.gI().thongbao("");
      }
   }

   public void loadPlayer() {
      controller.sendInfo(this);
      Service.getInstance().player(player);
      Service.getInstance().Send_Caitrang(player);
      Service.getInstance().sendFlagBag(player);
      player.playerSkill.sendSkillShortCut();
   }

   public void closeMessage() {
      try {
         Client.gI().remove(this);
         disconnect();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
