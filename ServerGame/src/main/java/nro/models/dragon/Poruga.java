package nro.models.dragon;

import nro.dialog.MenuDialog;
import nro.dialog.MenuRunable;
import nro.manager.NamekBallManager;
import nro.models.clan.Buff;
import nro.models.clan.Clan;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.network.io.Message;
import nro.services.NpcService;
import nro.services.Service;

import java.io.DataOutputStream;

public class Poruga extends AbsDragon {

   public Poruga(Player player) {
      super(player);
      this.wishes = new String[] { "Tăng 20%\nsức đánh", "Tăng 20% HP", "Tăng 20% KI", "Tăng 10%\nchí mạng" };
      this.tutorial = "";
      this.content = "Ta sẽ ban cho ngươi điều ước,ngươi có 5 phút,hãy suy nghĩ thật kĩ trước khi quyết định,tác dụng của chúc phúc sẽ có hiệu lực đến 6h AM";
      this.name = "Rồng thần Namek";
   }

   @Override
   public void openMenu() {

   }

   @Override
   public void summon() {
      appear = true;
      callDragon();
      showWishes();
      lastTimeAppear = System.currentTimeMillis();
      new Thread(this).start();
      sendNotify();
   }

   @Override
   public void reSummon() {

   }

   @Override
   public void showWishes() {
      Clan clan = summoner.clan;
      MenuDialog menu = new MenuDialog(content, wishes, new MenuRunable() {
         @Override
         public void run() {
            switch (this.indexSelected) {
               case 0:
                  clan.buff = Buff.BUFF_ATK;
                  break;
               case 1:
                  clan.buff = Buff.BUFF_HP;
                  break;
               case 2:
                  clan.buff = Buff.BUFF_KI;
                  break;
               case 3:
                  clan.buff = Buff.BUFF_CRIT;
                  break;
            }
            for (Player player : clan.membersInGame) {
               player.buff = clan.buff;
               Service.getInstance().point(player);
               Service.getInstance().sendThongBao(player, "Bạn vừa nhận được chúc phúc của rồng thần Poruga");
            }
            leave();
         }
      });
      menu.show(summoner);
   }

   @Override
   public void callDragon() {
      Message msg = Message.create(-83);
      DataOutputStream ds = msg.writer();
      try {
         ds.writeByte(appear ? (byte) 0 : (byte) 1);
         if (appear) {
            Zone z = summoner.zone;
            ds.writeShort(z.map.mapId);
            ds.writeShort(z.map.bgId);
            ds.writeByte(z.zoneId);
            ds.writeInt((int) summonerID);
            ds.writeUTF("");
            ds.writeShort(summoner.location.x);
            ds.writeShort(summoner.location.y);
            ds.writeByte(1);
         }
         ds.flush();
         Service.getInstance().sendMessAllPlayer(msg);
         msg.cleanup();
      } catch (Exception e) {
      }
   }

   @Override
   public void leave() {
      NpcService.gI().createTutorial(summoner, -1,
            "Điều ước của ngươi đã trở thành sự thật\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai");

      appear = false;
      callDragon();
      NamekBallManager.gI().initFossil();
   }

}
