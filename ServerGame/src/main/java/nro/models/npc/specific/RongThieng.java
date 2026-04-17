package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.NpcService;
import nro.services.func.SummonDragon;

import static nro.services.func.SummonDragon.*;

public class RongThieng extends Npc {

   public RongThieng(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void confirmMenu(Player player, int select) {
      switch (player.iDMark.getIndexMenu()) {
         case ConstNpc.IGNORE_MENU:
            break;
         case ConstNpc.SHENRON_CONFIRM:
            if (select == 0) {
               SummonDragon.gI().confirmWish();
            } else if (select == 1) {
               SummonDragon.gI().reOpenShenronWishes(player);
            }
            break;
         case ConstNpc.SHENRON_1_1:
            if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1
                  && select == SHENRON_1_STAR_WISHES_1.length - 1) {
               NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY,
                     SHENRON_1_STAR_WISHES_2);
               break;
            }
         case ConstNpc.SHENRON_1_2:
            if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2
                  && select == SHENRON_1_STAR_WISHES_2.length - 1) {
               NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY,
                     SHENRON_1_STAR_WISHES_1);
               break;
            }
         case ConstNpc.BLACK_SHENRON:
            if (player.iDMark.getIndexMenu() == ConstNpc.BLACK_SHENRON
                  && select == BLACK_SHENRON_WISHES.length) {
               NpcService.gI().createMenuRongThieng(player, ConstNpc.BLACK_SHENRON, BLACK_SHENRON_SAY,
                     BLACK_SHENRON_WISHES);
               break;
            }
         case ConstNpc.ICE_SHENRON:
            if (player.iDMark.getIndexMenu() == ConstNpc.ICE_SHENRON
                  && select == ICE_SHENRON_WISHES.length) {
               NpcService.gI().createMenuRongThieng(player, ConstNpc.ICE_SHENRON, ICE_SHENRON_SAY,
                     ICE_SHENRON_WISHES);
               break;
            }
         default:
            SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
            break;
      }
   }
}
