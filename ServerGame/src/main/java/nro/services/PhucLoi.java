package nro.services;

import java.io.IOException;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import nro.jdbc.daos.PlayerDAO;

import lombok.Getter;
import lombok.Setter;
import nro.network.io.Message;

@Getter
@Setter
public class PhucLoi {

   public int id;
   public String name;
   public int max_count;
   private int count;
   public byte active;
   private static final byte START = 1;

   public int tab_id;
   public static final List<PhucLoiManager> PHUCLOI_MANAGER = new ArrayList<>();
   public final List<Item> PHUCLOI_LIST_ITEM = new ArrayList<>();
   public static final List<PhucLoi> PHUCLOI_TEMPLATES = new ArrayList<>();

   private static PhucLoi i;

   public static PhucLoi gI() {
      if (i == null) {
         i = new PhucLoi();
      }
      return i;
   }

   public void Send_PhucLoi(Player pl) {
      Check_active(pl);
      Message msg = null;
      try {
         msg = Message.create(103);
         msg.writer().writeByte(START);
         msg.writer().writeByte(PHUCLOI_MANAGER.size());

         for (int j = 0; j < PHUCLOI_MANAGER.size(); j++) {
            PhucLoiManager manager = PHUCLOI_MANAGER.get(j);
            msg.writer().writeUTF(manager.tab_name);
            msg.writer().writeInt(manager.max_tab);
            msg.writer().writeInt(manager.id_tab);
            msg.writer().writeUTF(manager.info_phucloi);
            msg.writer().writeInt(manager.action);
            msg.writer().writeUTF(manager.tichLuy);
            msg.writer().writeByte(PHUCLOI_TEMPLATES.size());
            for (int k = 0; k < PHUCLOI_TEMPLATES.size(); k++) {
               PhucLoi phucloi = PHUCLOI_TEMPLATES.get(k);
               msg.writer().writeInt(pl.checkNhan[k]);
               msg.writer().writeInt(phucloi.tab_id);
               if (phucloi.tab_id != manager.id_tab) {
                  continue;
               }
               msg.writer().writeInt(phucloi.tab_id);
               msg.writer().writeInt(phucloi.id);
               msg.writer().writeUTF(phucloi.name);
               msg.writer().writeInt(phucloi.max_count);
               msg.writer().writeByte(phucloi.active);
               msg.writer().writeInt(countPlayer(pl, phucloi));
               msg.writer().writeInt(phucloi.PHUCLOI_LIST_ITEM.size());
               for (int h = 0; h < phucloi.PHUCLOI_LIST_ITEM.size(); h++) {
                  Item item = phucloi.PHUCLOI_LIST_ITEM.get(h);
                  msg.writer().writeShort(item.template.id);
                  msg.writer().writeInt(item.quantity);
                  msg.writer().writeUTF(item.getInfo());
                  msg.writer().writeUTF(item.getContent());
                  List<ItemOption> itemOptions = item.getDisplayOptions();
                  msg.writer().writeByte(itemOptions.size()); // options
                  for (ItemOption o : itemOptions) {
                     msg.writer().writeByte(o.optionTemplate.id);
                     msg.writer().writeInt(o.param);
                  }
               }
            }
         }
         pl.sendMessage(msg);
         msg.cleanup();
      } catch (IOException e) {
      }
   }

   public void Check_active(Player pl) {
      try {
         pl.checkNhan = new int[PHUCLOI_TEMPLATES.size()];
         for (int a = 0; a < PHUCLOI_TEMPLATES.size(); a++) {
            PhucLoi ploi = PHUCLOI_TEMPLATES.get(a);
            for (int t = 0; t < pl.listNhan.size(); t++) {
               if (pl.listNhan.get(t).equals(ploi.id)) {
                  pl.checkNhan[a] = 1;
               }
            }
            for (int j = 0; j < pl.listOnline.size(); j++) {
               if (pl.listOnline.get(j).equals(ploi.id)) {
                  pl.checkNhan[a] = 1;
               }
            }
            for (int u = 0; u < pl.listDiemDanh.size(); u++) {
               if (pl.listDiemDanh.get(u).equals(ploi.id)) {
                  pl.checkNhan[a] = 1;
               }
            }
         }
      } catch (Exception e) {
      }
   }

   public int countPlayer(Player pl, PhucLoi phucloi) {
      switch (phucloi.tab_id) {
         case 0: {
            phucloi.count = pl.phutOnline;
            break;
         }
         case 1: {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            phucloi.count = dayOfWeek;
            break;
         }
         case 2: {
            phucloi.count = pl.getSession().tong_nap;
            break;
         }
         case 3, 4, 5, 6, 7: {
            phucloi.count = pl.getSession().vnd;
            break;
         }
         default:
            phucloi.count = 0;
      }
      return phucloi.count;
   }

   public void Active_PhucLoi(Player pl, int tabid) {
      PhucLoi phucloi = PHUCLOI_TEMPLATES.get(tabid);
      if (phucloi.active == 0 || phucloi.active == 2) {
         if (phucloi.tab_id == 0) {
            if (pl.checkNhan[tabid] == 0) {
               if (countPlayer(pl, phucloi) >= phucloi.max_count) {
                  if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                     pl.listOnline.add(tabid);
                     for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                        Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                        InventoryService.gI().addItemBag(pl, item, -333);
                        Service.getInstance().sendThongBao(pl,
                              "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                     }
                     InventoryService.gI().sendItemBags(pl);
                     Send_PhucLoi(pl);
                  } else {
                     Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
                  }
               } else {
                  Service.getInstance().sendThongBao(pl, "|7|Không đủ điều kiện Nhận thưởng");
               }
            } else {
               Service.getInstance().sendThongBao(pl, "|7|Bạn đã nhận rồi mà !!!");
            }
         } else if (phucloi.tab_id == 1) {
            if (pl.checkNhan[tabid] == 0) {
               if (countPlayer(pl, phucloi) == phucloi.max_count) {
                  if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                     pl.listDiemDanh.add(tabid);
                     for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                        Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                        InventoryService.gI().addItemBag(pl, item, -333);
                        Service.getInstance().sendThongBao(pl,
                              "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                     }
                     InventoryService.gI().sendItemBags(pl);
                     Send_PhucLoi(pl);
                  } else {
                     Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
                  }
               } else {
                  Service.getInstance().sendThongBao(pl, "|7|Không đủ điều kiện Nhận thưởng");
               }
            } else {
               Service.getInstance().sendThongBao(pl, "|7|Bạn đã nhận rồi mà !!!");
            }
         } else {
            if (pl.checkNhan[tabid] == 0) {
               if (countPlayer(pl, phucloi) >= phucloi.max_count) {
                  if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                     pl.listNhan.add(tabid);
                     for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                        Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                        InventoryService.gI().addItemBag(pl, item, -333);
                        Service.getInstance().sendThongBao(pl,
                              "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                     }
                     InventoryService.gI().sendItemBags(pl);
                     Send_PhucLoi(pl);
                  } else {
                     Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
                  }
               } else {
                  Service.getInstance().sendThongBao(pl, "|7|Không đủ điều kiện Nhận thưởng");
               }
            } else {
               Service.getInstance().sendThongBao(pl, "|7|Bạn đã nhận rồi mà !!!");
            }
         }
      } else {
         if (phucloi.tab_id >= 3) {
            if (countPlayer(pl, phucloi) >= phucloi.max_count) {
               if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                  if (!PlayerDAO.checkVnd(pl, phucloi.max_count)) {
                     Service.getInstance().sendThongBao(pl, "|7|Không đủ Coin");
                     return;
                  }
                  PlayerDAO.subVnd(pl, phucloi.max_count);
                  for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                     Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                     InventoryService.gI().addItemBag(pl, item, -333);
                     Service.getInstance().sendThongBao(pl,
                           "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                  }
                  InventoryService.gI().sendItemBags(pl);
                  Send_PhucLoi(pl);
               } else {
                  Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
               }
            } else {
               Service.getInstance().sendThongBao(pl,
                     "|7|Còn thiếu " + (phucloi.max_count - countPlayer(pl, phucloi)) + " Coin");
            }
         }
      }
   }

}
