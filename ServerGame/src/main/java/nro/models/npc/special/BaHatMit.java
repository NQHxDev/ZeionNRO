package nro.models.npc.special;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.network.io.Message;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.services.func.CombineServiceNew;
import nro.services.func.ShopService;
import nro.utils.Util;

public class BaHatMit extends Npc {

   public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 5 || this.mapId == 13) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "|8|CHỨC NĂNG CHÂN MỆNH\n"
                        + "Cần 999 Mảnh chân mệnh để nhận chân mệnh cấp 1\n"
                        + "Sau đó nâng cấp sẽ cần mảnh chân mệnh\n"
                        + "|2|Mảnh chân mệnh có thể fam tại map fam",
                  "Ép sao\ntrang bị",
                  "Pha lê\nhóa\ntrang bị",
                  "Nhận\nChân Mệnh",
                  "Nâng cấp\nChân mệnh");
         } else if (this.mapId == 121) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Ngươi tìm ta có việc gì?",
                  "Về đảo\nrùa");
         } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Ngươi tìm ta có việc gì?",
                  "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                  "Nhập\nNgọc Rồng",
                  "Nâng cấp\nBông tai\nPorata",
                  "Mở chỉ số\n bông tai 2, 3, 4 và 5",
                  "Sách tuyệt kỹ");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 5 || this.mapId == 13) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                     break;
                  case 1:
                     CombineServiceNew.gI().openTabCombine(player,
                           CombineServiceNew.PHA_LE_HOA_TRANG_BI_X100);
                     break;
                  case 2: // nhận Chân mệnh
                     this.createOtherMenu(player, 5701,
                           "|7|CHÂN MỆNH"
                                 + "\n\n|5|Cần 999 Mảnh Chân Mệnh để nhận Chân Mệnh cấp 1"
                                 + "\n|3| Lưu ý: Chỉ được nhận Chân mệnh 1 lần (Hành trang chỉ tồn tại 1 Chân mệnh)"
                                 + "\nNếu đã có Chân mệnh. Ta sẽ giúp ngươi nâng cấp bậc lên với các dòng chỉ số cao hơn",
                           "Nhận");
                     break;
                  case 3:
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_CHAN_MENH);
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == 5701) {
               switch (select) {
                  case 0:
                     for (int i = 0; i < 9; i++) {
                        Item findItemBag = InventoryService.gI().findItemBagByTemp(player, 1300 + i);
                        Item findItemBody = InventoryService.gI().findItemBodyByTemp(player, 1300 + i);
                        if (findItemBag != null || findItemBody != null) {
                           Service.getInstance().sendThongBao(player, "|7|Ngươi đã có Chân mệnh rồi mà");
                           return;
                        }
                     }
                     Item thoivang = null;
                     try {
                        thoivang = InventoryService.gI().findItemBagByTemp(player, 1318);
                     } catch (Exception e) {
                     }
                     if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                        if (thoivang == null || thoivang.quantity < 999) {
                           Service.getInstance().sendThongBao(player, "Không đủ Thỏi  Vàng");
                        } else {
                           InventoryService.gI().subQuantityItemsBag(player, thoivang, 999);
                           Item chanmenh = ItemService.gI().createNewItem((short) 1300);
                           chanmenh.itemOptions.add(new ItemOption(50, 20));
                           chanmenh.itemOptions.add(new ItemOption(77, 30));
                           chanmenh.itemOptions.add(new ItemOption(103, 30));
                           chanmenh.itemOptions.add(new ItemOption(30, 1));
                           InventoryService.gI().addItemBag(player, chanmenh, 0);
                           Service.getInstance().sendMoney(player);
                           InventoryService.gI().sendItemBags(player);
                           this.npcChat(player, "|1|Bạn nhận được Chân mệnh Cấp 1");
                        }
                     } else {
                        this.npcChat(player, "Hành trang không đủ chổ trống");
                     }
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
               switch (player.combineNew.typeCombine) {
                  case CombineServiceNew.EP_SAO_TRANG_BI:
                  case CombineServiceNew.AN_TRANG_BI:
                  case CombineServiceNew.PHA_LE_HOA_TRANG_BI_X100:
                  case CombineServiceNew.TAY_SAO_PHA_LE:
                  case CombineServiceNew.PHAP_SU_HOA:
                  case CombineServiceNew.LINH_HOA_TRANG_BI:
                  case CombineServiceNew.TAY_PHAP_SU:
                  case CombineServiceNew.NANG_CAP_CHAN_MENH:
                     switch (select) {
                        case 0:
                           if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_X100) {
                              player.combineNew.quantities = 1;
                           }
                           break;
                        case 1:
                           if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_X100) {
                              player.combineNew.quantities = 10;
                           }
                           break;
                        case 2:
                           if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_X100) {
                              player.combineNew.quantities = 100;
                           }
                           break;
                     }
                     CombineServiceNew.gI().startCombine(player);
                     break;
               }
            }
         } else if (this.mapId == 112) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                     break;
               }
            }
         } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0: // shop bùa
                     createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                           "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                 + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                           "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng",
                           "Bùa\n  Đệ tử Mabư\n 1 giờ", "Đóng");
                     break;
                  case 1:
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                     break;
                  case 2:
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                     break;
                  case 3: // nâng cấp bông tai
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                     break;
                  case 4: // Mở chỉ số bông tai
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI);
                     break;
                  case 5: // Sách tuyệt kỹ
                     createOtherMenu(player, ConstNpc.SACH_TUYET_KY, "Ta có thể giúp gì cho ngươi ?",
                           "Đóng thành\nSách cũ",
                           "Đổi Sách\nTuyệt kỹ",
                           "Giám định\nSách",
                           "Tẩy\nSách",
                           "Nâng cấp\nSách\nTuyệt kỹ",
                           "Hồi phục\nSách",
                           "Phân rã\nSách");
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.SACH_TUYET_KY) {
               switch (select) {
                  case 0:
                     Item trangSachCu = InventoryService.gI().findItemBagByTemp(player, 1516);
                     Item biaSach = InventoryService.gI().findItemBagByTemp(player, 1506);
                     if ((trangSachCu != null && trangSachCu.quantity >= 9999)
                           && (biaSach != null && biaSach.quantity >= 1)) {
                        createOtherMenu(player, ConstNpc.DONG_THANH_SACH_CU,
                              "|2|Chế tạo Cuốn sách cũ\n"
                                    + "|1|Trang sách cũ " + trangSachCu.quantity + "/9999\n"
                                    + "Bìa sách " + biaSach.quantity + "/1\n"
                                    + "Tỉ lệ thành công: 60%\n"
                                    + "Thất bại mất 99 trang sách và 1 bìa sách",
                              "Đồng ý", "Từ chối");
                     } else {
                        String NpcSay = "|2|Chế tạo Cuốn sách cũ\n";
                        if (trangSachCu == null) {
                           NpcSay += "|7|Trang sách cũ " + "0/9999\n";
                        } else {
                           NpcSay += "|1|Trang sách cũ " + trangSachCu.quantity + "/9999\n";
                        }
                        if (biaSach == null) {
                           NpcSay += "|7|Bìa sách " + "0/1\n";
                        } else {
                           NpcSay += "|1|Bìa sách " + biaSach.quantity + "/1\n";
                        }
                        NpcSay += "|7|Tỉ lệ thành công: 60%\n";
                        NpcSay += "|7|Thất bại mất 99 trang sách và 1 bìa sách";
                        createOtherMenu(player, ConstNpc.DONG_THANH_SACH_CU_2,
                              NpcSay, "Từ chối");
                     }
                     break;
                  case 1:
                     Item cuonSachCu = InventoryService.gI().findItemBagByTemp(player, 1509);
                     Item kimBam = InventoryService.gI().findItemBagByTemp(player, 1507);
                     if ((cuonSachCu != null && cuonSachCu.quantity >= 10)
                           && (kimBam != null && kimBam.quantity >= 1)) {
                        createOtherMenu(player, ConstNpc.DOI_SACH_TUYET_KY,
                              "|2|Đổi sách tuyệt kỹ 1\n"
                                    + "|1|Cuốn sách cũ " + cuonSachCu.quantity + "/10\n"
                                    + "Kìm bấm giấy " + kimBam.quantity + "/1\n"
                                    + "Tỉ lệ thành công: 60%\n",
                              "Đồng ý", "Từ chối");
                     } else {
                        String NpcSay = "|2|Đổi sách Tuyệt kỹ 1\n";
                        if (cuonSachCu == null) {
                           NpcSay += "|7|Cuốn sách cũ " + "0/10\n";
                        } else {
                           NpcSay += "|1|Cuốn sách cũ " + cuonSachCu.quantity + "/10\n";
                        }
                        if (kimBam == null) {
                           NpcSay += "|7|Kìm bấm giấy " + "0/1\n";
                        } else {
                           NpcSay += "|1|Kìm bấm giấy " + kimBam.quantity + "/1\n";
                        }
                        NpcSay += "|7|Tỉ lệ thành công: 60%\n";
                        createOtherMenu(player, ConstNpc.DOI_SACH_TUYET_KY_2,
                              NpcSay, "Từ chối");
                     }
                     break;
                  case 2:// giám định sách
                     CombineServiceNew.gI().openTabCombine(player,
                           CombineServiceNew.GIAM_DINH_SACH);
                     break;
                  case 3:// tẩy sách
                     CombineServiceNew.gI().openTabCombine(player,
                           CombineServiceNew.TAY_SACH);
                     break;
                  case 4:// nâng cấp sách
                     CombineServiceNew.gI().openTabCombine(player,
                           CombineServiceNew.NANG_CAP_SACH_TUYET_KY);
                     break;
                  case 5:// phục hồi sách
                     CombineServiceNew.gI().openTabCombine(player,
                           CombineServiceNew.PHUC_HOI_SACH);
                     break;
                  case 6:// phân rã sách
                     CombineServiceNew.gI().openTabCombine(player,
                           CombineServiceNew.PHAN_RA_SACH);
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.DOI_SACH_TUYET_KY) {
               switch (select) {
                  case 0:
                     Item cuonSachCu = InventoryService.gI().findItemBagByTemp(player, 1509);
                     Item kimBam = InventoryService.gI().findItemBagByTemp(player, 1507);
                     short baseValue = 1512;
                     short genderModifier = (player.gender == 0) ? -2
                           : ((player.gender == 2) ? 2 : (short) 0);
                     Item sachTuyetKy = ItemService.gI()
                           .createNewItem((short) (baseValue + genderModifier));

                     if (Util.isTrue(60, 100)) {
                        sachTuyetKy.itemOptions.add(new ItemOption(241, 0));
                        sachTuyetKy.itemOptions.add(new ItemOption(30, 0));
                        sachTuyetKy.itemOptions.add(new ItemOption(242, 5));
                        sachTuyetKy.itemOptions.add(new ItemOption(243, 1000));
                        try { // send effect susscess
                           Message msg = Message.create(-81);
                           msg.writer().writeByte(0);
                           msg.writer().writeUTF("test");
                           msg.writer().writeUTF("test");
                           msg.writer().writeShort(tempId);
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(1);
                           msg.writer().writeByte(2);
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, kimBam));
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, cuonSachCu));
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(7);
                           msg.writer().writeShort(sachTuyetKy.template.iconID);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           player.sendMessage(msg);
                           msg.cleanup();
                        } catch (Exception e) {
                        }
                        InventoryService.gI().addItemList(player.inventory.itemsBag, sachTuyetKy, 1);
                        InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, 10);
                        InventoryService.gI().subQuantityItemsBag(player, kimBam, 1);
                        InventoryService.gI().sendItemBags(player);
                        return;
                     } else {
                        try { // send effect faile
                           Message msg = Message.create(-81);
                           msg.writer().writeByte(0);
                           msg.writer().writeUTF("test");
                           msg.writer().writeUTF("test");
                           msg.writer().writeShort(tempId);
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(1);
                           msg.writer().writeByte(2);
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, kimBam));
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, cuonSachCu));
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(8);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           player.sendMessage(msg);
                           msg.cleanup();
                        } catch (Exception e) {
                        }
                        InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, 5);
                        InventoryService.gI().subQuantityItemsBag(player, kimBam, 1);
                        InventoryService.gI().sendItemBags(player);
                     }
                     return;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.DONG_THANH_SACH_CU) {
               switch (select) {
                  case 0:
                     Item trangSachCu = InventoryService.gI().findItemBagByTemp(player, 1516);
                     Item biaSach = InventoryService.gI().findItemBagByTemp(player, 1506);
                     Item cuonSachCu = ItemService.gI().createNewItem((short) 1509);
                     if (Util.isTrue(60, 100)) {
                        cuonSachCu.itemOptions.add(new ItemOption(30, 0));
                        try { // send effect susscess
                           Message msg = Message.create(-81);
                           msg.writer().writeByte(0);
                           msg.writer().writeUTF("test");
                           msg.writer().writeUTF("test");
                           msg.writer().writeShort(tempId);
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(1);
                           msg.writer().writeByte(2);
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, trangSachCu));
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, biaSach));
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(7);
                           msg.writer().writeShort(cuonSachCu.template.iconID);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           player.sendMessage(msg);
                           msg.cleanup();
                        } catch (Exception e) {
                        }
                        InventoryService.gI().addItemList(player.inventory.itemsBag, cuonSachCu, 99);
                        InventoryService.gI().subQuantityItemsBag(player, trangSachCu, 9999);
                        InventoryService.gI().subQuantityItemsBag(player, biaSach, 1);
                        InventoryService.gI().sendItemBags(player);
                        return;
                     } else {
                        try { // send effect faile
                           Message msg = Message.create(-81);
                           msg.writer().writeByte(0);
                           msg.writer().writeUTF("test");
                           msg.writer().writeUTF("test");
                           msg.writer().writeShort(tempId);
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(1);
                           msg.writer().writeByte(2);
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, biaSach));
                           msg.writer().writeByte(InventoryService.gI().getIndexBag(player, trangSachCu));
                           player.sendMessage(msg);
                           msg.cleanup();
                           msg = Message.create(-81);
                           msg.writer().writeByte(8);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           msg.writer().writeShort(-1);
                           player.sendMessage(msg);
                           msg.cleanup();
                        } catch (Exception e) {
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trangSachCu, 99);
                        InventoryService.gI().subQuantityItemsBag(player, biaSach, 1);
                        InventoryService.gI().sendItemBags(player);
                     }
                     return;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
               switch (select) {
                  case 0:
                     ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_0, 0);
                     break;
                  case 1:
                     ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_1, 1);
                     break;
                  case 2:
                     ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_2, 2);
                     break;
                  case 3:
                     ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_3, 3);
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
               switch (player.combineNew.typeCombine) {
                  case CombineServiceNew.NANG_CAP_VAT_PHAM:
                  case CombineServiceNew.NANG_CAP_BONG_TAI:
                  case CombineServiceNew.LAM_PHEP_NHAP_DA:
                  case CombineServiceNew.NHAP_NGOC_RONG:
                  case CombineServiceNew.PHAN_RA_DO_THAN_LINH:
                  case CombineServiceNew.NANG_CAP_SKH_VAI_THO:
                  case CombineServiceNew.MO_CHI_SO_BONG_TAI:
                  case CombineServiceNew.GIAM_DINH_SACH:
                  case CombineServiceNew.TAY_SACH:
                  case CombineServiceNew.NANG_CAP_SACH_TUYET_KY:
                  case CombineServiceNew.PHUC_HOI_SACH:
                  case CombineServiceNew.PHAN_RA_SACH:
                     if (select == 0) {
                        CombineServiceNew.gI().startCombine(player);
                     }
                     break;
               }
            }
         }
      }
   }
}
