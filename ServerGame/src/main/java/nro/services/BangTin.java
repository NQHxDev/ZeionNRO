package nro.services;

import lombok.Getter;
import lombok.Setter;
import nro.models.player.Player;
import java.util.ArrayList;
import java.util.List;

import nro.network.io.Message;

@Getter
@Setter
public class BangTin {

   public int id;

   public String tieude;

   public String info;

   private static final int START = 1;

   public static final List<BangTin> BANGTIN_MANAGER = new ArrayList<>();

   private static BangTin i;

   public static BangTin gI() {
      if (i == null) {
         i = new BangTin();
      }
      return i;
   }

   public void Send_BangTin(Player pl) {
      Message msg = null;
      try {
         msg = Message.create(104);
         msg.writer().writeByte(START);
         msg.writer().writeByte(BANGTIN_MANAGER.size());

         for (int j = 0; j < BANGTIN_MANAGER.size(); j++) {
            BangTin manager = BANGTIN_MANAGER.get(j);
            msg.writer().writeInt(manager.id);
            msg.writer().writeUTF(manager.tieude);
            msg.writer().writeUTF(manager.info);
         }
         pl.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
      }
   }

}
