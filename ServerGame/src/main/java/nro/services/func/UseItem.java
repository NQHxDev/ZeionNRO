package nro.services.func;

import nro.consts.*;
import nro.dialog.MenuDialog;
import nro.dialog.MenuRunable;
import nro.event.Event;
import nro.lib.RandomCollection;
import nro.manager.MiniPetManager;
import nro.manager.NamekBallManager;
import nro.manager.PetFollowManager;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.item.MinipetTemplate;
import nro.models.map.*;
import nro.models.map.dungeon.zones.ZSnakeRoad;
import nro.models.map.war.NamekBallWar;
import nro.models.player.Inventory;
import nro.models.player.MiniPet;
import nro.models.player.PetFollow;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.Manager;
import nro.server.io.Message;
import nro.server.io.Session;
import nro.services.*;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.TimeUtil;
import nro.utils.Util;

import java.util.Date;
import java.util.List;
import java.util.Random;

import nro.data.ItemData;
import nro.jdbc.daos.PlayerDAO;
import nro.models.boss.BossManager;
import nro.models.npc.specialnpc.MabuEgg;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class UseItem {

   private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
   private static final int ITEM_BAG_TO_BOX = 1;
   private static final int ITEM_BODY_TO_BOX = 3;
   private static final int ITEM_BAG_TO_BODY = 4;
   private static final int ITEM_BODY_TO_BAG = 5;
   private static final int ITEM_BAG_TO_PET_BODY = 6;
   private static final int ITEM_BODY_PET_TO_BAG = 7;

   private static final byte DO_USE_ITEM = 0;
   private static final byte DO_THROW_ITEM = 1;
   private static final byte ACCEPT_THROW_ITEM = 2;
   private static final byte ACCEPT_USE_ITEM = 3;

   private static UseItem instance;

   private UseItem() {

   }

   public static UseItem gI() {
      if (instance == null) {
         instance = new UseItem();
      }
      return instance;
   }

   public void getItem(Session session, Message msg) {
      Player player = session.player;
      TransactionService.gI().cancelTrade(player);
      try {
         int type = msg.reader().readByte();
         int index = msg.reader().readByte();
         if (index == -1) {
            return;
         }
         switch (type) {
            case ITEM_BOX_TO_BODY_OR_BAG:
               InventoryService.gI().itemBoxToBodyOrBag(player, index);
               TaskService.gI().checkDoneTaskGetItemBox(player);
               break;
            case ITEM_BAG_TO_BOX:
               InventoryService.gI().itemBagToBox(player, index);
               break;
            case ITEM_BODY_TO_BOX:
               InventoryService.gI().itemBodyToBox(player, index);
               break;
            case ITEM_BAG_TO_BODY:
               InventoryService.gI().itemBagToBody(player, index);
               break;
            case ITEM_BODY_TO_BAG:
               InventoryService.gI().itemBodyToBag(player, index);
               break;
            case ITEM_BAG_TO_PET_BODY:
               InventoryService.gI().itemBagToPetBody(player, index);
               break;
            case ITEM_BODY_PET_TO_BAG:
               InventoryService.gI().itemPetBodyToBag(player, index);
               break;
         }
         player.setClothes.setup();
         if (player.pet != null) {
            player.pet.setClothes.setup();
         }
         player.setClanMember();
         PlayerService.gI().sendPetFollow(player);
         Service.getInstance().point(player);
      } catch (Exception e) {
         Log.error(UseItem.class, e);

      }
   }

   public void doItem(Player player, Message _msg) {
      TransactionService.gI().cancelTrade(player);
      Message msg;
      try {
         byte type = _msg.reader().readByte();
         int where = _msg.reader().readByte();
         int index = _msg.reader().readByte();
         switch (type) {
            case DO_USE_ITEM:
               if (player != null && player.inventory != null) {
                  if (index != -1) {
                     if (index >= 0 && index < player.inventory.itemsBag.size()) {
                        Item item = player.inventory.itemsBag.get(index);
                        if (item.isNotNullItem()) {
                           if (ItemData.IdMiniPet.contains((int) item.template.id)) {
                              MinipetTemplate temp = MiniPetManager.gI().findByID(item.getId());
                              if (temp == null) {
                                 System.err.println("khong tim thay minipet id: " + item.getId());
                              }
                              MiniPet.callMiniPet(player, item.template.id);
                              InventoryService.gI().itemBagToBody(player, index);
                              return;
                           }
                           if (item.template.type == 22) {
                              msg = new Message(-43);
                              msg.writer().writeByte(type);
                              msg.writer().writeByte(where);
                              msg.writer().writeByte(index);
                              msg.writer().writeUTF(
                                    "Bạn có muốn dùng " + player.inventory.itemsBag.get(index).template.name + "?");
                              player.sendMessage(msg);
                              msg.cleanup();
                           } else if (item.template.type == 7) {
                              msg = new Message(-43);
                              msg.writer().writeByte(type);
                              msg.writer().writeByte(where);
                              msg.writer().writeByte(index);
                              msg.writer().writeUTF(
                                    "Bạn chắc chắn học " + player.inventory.itemsBag.get(index).template.name + "?");
                              player.sendMessage(msg);
                           } else if (player.isVersionAbove(220) && item.template.type == 23 || item.template.type == 24
                                 || item.template.type == 11) {
                              InventoryService.gI().itemBagToBody(player, index);
                           } else if (item.template.id == 401) {
                              msg = new Message(-43);
                              msg.writer().writeByte(type);
                              msg.writer().writeByte(where);
                              msg.writer().writeByte(index);
                              msg.writer()
                                    .writeUTF("Sau khi đổi đệ sẽ mất toàn bộ trang bị trên người đệ tử nếu chưa tháo");
                              player.sendMessage(msg);
                           } else if (item.getType() == 72) {
                              PetFollow pet = PetFollowManager.gI().findByID(item.getId());
                              player.setPetFollow(pet);
                              InventoryService.gI().itemBagToBody(player, index);
                              PlayerService.gI().sendPetFollow(player);
                           } else if (item.getType() == 76) {
                              InventoryService.gI().itemBagToBody(player, index);
                           } else if (item.getType() == 79) {
                              InventoryService.gI().itemBagToBody(player, index);
                           } else if (item.getType() == 80) {
                              InventoryService.gI().itemBagToBody(player, index);
                           } else if (item.template.type == 74) {
                              Service.getInstance().sendFoot(player, item.template.id);
                              InventoryService.gI().itemBagToBody(player, index);
                           } else if (item.template.type == 35) {
                              InventoryService.gI().itemBagToBody(player, index);
                           } else if (item.template.type == 36) {
                              InventoryService.gI().itemBagToBody(player, index);
                           } else if (item.template.type == 11) {
                              InventoryService.gI().itemBagToBody(player, index);
                           } else {
                              useItem(player, item);
                           }
                        }
                     }
                  } else {
                     InventoryService.gI().eatPea(player);
                  }
               }
               break;
            case DO_THROW_ITEM:
               if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                  Item item = null;
                  if (where == 0) {
                     if (index >= 0 && index < player.inventory.itemsBody.size()) {
                        item = player.inventory.itemsBody.get(index);
                     }
                  } else {
                     if (index >= 0 && index < player.inventory.itemsBag.size()) {
                        item = player.inventory.itemsBag.get(index);
                     }
                  }
                  if (item != null && item.isNotNullItem()) {
                     msg = new Message(-43);
                     msg.writer().writeByte(type);
                     msg.writer().writeByte(where);
                     msg.writer().writeByte(index);
                     msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                     player.sendMessage(msg);
                  }
               } else {
                  Service.getInstance().sendThongBao(player, "Không thể thực hiện");
               }
               break;
            case ACCEPT_THROW_ITEM:
               InventoryService.gI().throwItem(player, where, index);
               break;
            case ACCEPT_USE_ITEM:
               if (index >= 0 && index < player.inventory.itemsBag.size()) {
                  Item item = player.inventory.itemsBag.get(index);
                  if (item.isNotNullItem()) {
                     useItem(player, item);
                  }
               }
               break;
         }
      } catch (Exception e) {
         Log.error(UseItem.class, e);
      }
   }

   public void useSatellite(Player player, Item item) {
      Satellite satellite = null;
      if (player.zone != null) {
         int count = player.zone.getSatellites().size();
         if (count < 3) {
            switch (item.template.id) {
               case ConstItem.VE_TINH_TRI_LUC:
                  satellite = new SatelliteMP(player.zone, ConstItem.VE_TINH_TRI_LUC, player.location.x,
                        player.location.y, player);
                  break;

               case ConstItem.VE_TINH_TRI_TUE:
                  satellite = new SatelliteExp(player.zone, ConstItem.VE_TINH_TRI_TUE, player.location.x,
                        player.location.y, player);
                  break;

               case ConstItem.VE_TINH_PHONG_THU:
                  satellite = new SatelliteDefense(player.zone, ConstItem.VE_TINH_PHONG_THU, player.location.x,
                        player.location.y, player);
                  break;

               case ConstItem.VE_TINH_SINH_LUC:
                  satellite = new SatelliteHP(player.zone, ConstItem.VE_TINH_SINH_LUC, player.location.x,
                        player.location.y, player);
                  break;
            }
            if (satellite != null) {
               InventoryService.gI().subQuantityItemsBag(player, item, 1);
               Service.getInstance().dropItemMapForMe(player, satellite);
               Service.getInstance().dropItemMap(player.zone, satellite);
            }
         } else {
            Service.getInstance().sendThongBaoOK(player,
                  "Số lượng vệ tinh có thể đặt trong khu vực đã đạt mức tối đa.");
         }
      }
   }

   private void useItem(Player pl, Item item) {
      if (Event.isEvent() && Event.getInstance().useItem(pl, item)) {
         return;
      }
      if (item.template.strRequire <= pl.nPoint.power) {
         int type = item.getType();
         if (type == 6) {
            InventoryService.gI().eatPea(pl);
         } else if (type == 33) {
            RadaService.getInstance().useItemCard(pl, item);
         } else if (type == 5) {
            Service.getInstance().Send_Caitrang(pl);
         } else if (type == 22) {
            useSatellite(pl, item);
         } else if (type == 72) {
            PlayerService.gI().sendPetFollow(pl);
         } else if (type == 74) {
            Service.getInstance().sendFoot(pl, item.template.id);
         } else {
            switch (item.template.id) {
               case ConstItem.GOI_10_RADA_DO_NGOC:
                  findNamekBall(pl, item);
                  break;
               case 627:
                  OpenHopTriAn(pl, item);
                  break;
               case ConstItem.CAPSULE_THOI_TRANG_30_NGAY:
                  capsuleThoiTrang(pl, item);
                  break;
               case 1445:
                  openboxsukien(pl, item, ConstEvent.SU_KIEN_TET);
                  break;
               case 570:
                  openWoodChest(pl, item);
                  break;
               case 648:
                  openboxsukien(pl, item, 3);
                  break;
               case 668:
                  hopQuaTanThu(pl, item);
                  break;
               case 992:
                  if (TaskService.gI().getIdTask(pl) == ConstTask.TASK_32_0) {
                     TaskService.gI().doneTask(pl, ConstTask.TASK_32_0);
                  }
                  ChangeMapService.gI().goToPrimaryForest(pl);
                  break;
               case 1483:
                  NpcService.gI().createMenuConMeo(pl, ConstNpc.VAO_MAP_NGOAI_VUC, -1,
                        "Bạn muốn Di chuyển đến mơi nào???",
                        "Map Hallowen", "Hành tinh\nBăng giá", "Map Địa Ngục");
                  break;
               case 1444:
                  if (!pl.getSession().actived) {
                     Service.getInstance().sendThongBao(pl, "Vui lòng kích hoạt tài khoản để có thể sử dụng");
                     return;
                  }
                  Input.gI().createFormTangRuby(pl);
                  break;
               case 628: // phiếu cải trang hải tặc
                  openPhieuCaiTrangHaiTac(pl, item);
                  break;
               case 1433: // Hop Qua Kich Hoat
                  openboxsukien(pl, item, 1);
                  break;
               case 1441: // phiếu cải trang 20/10
                  openbox2010(pl, item);
                  break;
               case 1442:
                  openboxsukien(pl, item, 2);
                  break;
               case 457:
                  NpcService.gI().createMenuConMeo(pl, ConstNpc.DUNG_NHIEU_TV, 7710,
                        "|7|THỎI VÀNG\n"
                              + "|-1|Theo nguyện vọng góp ý từ các chiến binh, ta được Admin\n"
                              + "giao cho trọng trách hỗ trợ Sử dụng Thỏi vàng số lượng nhiều\n"
                              + "|0|Ngươi muốn Sử dụng bao nhiêu Thỏi vàng?",
                        "X1 Thỏi\n(500 Triệu)", "X5 Thỏi\n(2,5 Tỷ)", "X10 Thỏi\n(5 Tỷ)",
                        "X100 Thỏi\n(50 Tỷ)");
                  return;
               case 211: // nho tím
               case 212: // nho xanh
                  eatGrapes(pl, item);
                  break;
               case 1459, 1108, 1537, 1538, 1539, 1569, 1695, 1699, 568: // đổi đệ tử
                  chonHtDeTu(pl, item);
                  break;
               case 380: // cskb
                  openCSKB(pl, item);
                  break;
               case 573: // capsule bạc
                  openCapsuleBac(pl, item);
                  break;
               case ConstItem.TUI_VANG: // capsule bạc
                  tuivang(pl, item);
                  break;
               case 574: // capsule vàng
                  openCapsuleVang(pl, item);
                  break;
               case 381: // cuồng nộ
               case 382: // bổ huyết
               case 383: // bổ khí
               case 384: // giáp xên
               case 385: // ẩn danh
               case 379: // máy dò
               case 663: // bánh pudding
               case 664: // xúc xíc
               case 665: // kem dâu
               case 666: // mì ly
               case 667: // sushi
               case ConstItem.BANH_CHUNG_CHIN:
               case ConstItem.BANH_TET_CHIN:
               case ConstItem.CUONG_NO_2:
               case ConstItem.BO_HUYET_2:
               case ConstItem.GIAP_XEN_BO_HUNG_2:
               case ConstItem.BO_KHI_2:
               case 579:
               case 1385:
               case 899:
               case 1317:
               case 1201:
               case 1386:
               case 465:
               case 466:
               case 472:
               case 473:
                  useItemTime(pl, item);
                  break;
               case 521: // tdlt
                  useTDLT(pl, item);
                  break;
               case 1105:
                  UseItem.gI().Hopts(pl, item);
                  break;
               case 1996:
                  skh_thanh_ton(pl);
                  break;
               case 1997:
                  skh_than_linh(pl);
                  break;
               case 1998:
                  skh_huy_diet(pl);
                  break;
               case 2000:
                  skhtd(pl);
                  break;
               case 2001:
                  skhnm(pl);
                  break;
               case 2002:
                  skhxd(pl);
                  break;
               case 454: // bông tai
                  usePorata(pl);
                  break;
               case 921: // bông tai
                  UseItem.gI().usePorata2(pl);
                  break;
               case 1165: // bông tai
                  UseItem.gI().usePorata3(pl);
                  break;
               case 1129: // bông tai
                  UseItem.gI().usePorata4(pl);
                  break;
               case 2098: // bông tai
                  UseItem.gI().usePorata6(pl);
                  break;
               case 2099: // bông tai
                  UseItem.gI().usePorata5(pl);
                  break;
               case 699, 700, 701: // Bổ huyết đan
                  anDanDuoc(pl, item);
                  break;
               case 193: // gói 10 viên capsule
                  InventoryService.gI().subQuantityItemsBag(pl, item, 1);
               case 194: // capsule đặc biệt
                  openCapsuleUI(pl);
                  break;
               case 401: // đổi đệ tử
                  changePet(pl, item);
                  break;
               case 402: // sách nâng chiêu 1 đệ tử
               case 403: // sách nâng chiêu 2 đệ tử
               case 404: // sách nâng chiêu 3 đệ tử
               case 759: // sách nâng chiêu 4 đệ tử
                  upSkillPet(pl, item);
                  break;
               case 1241: // đổi skill
                  doiskill4(pl, item);
                  break;
               case 1237: // pháp sư
                  openphapsu(pl, item);
                  break;
               case 1525: // hộp sách tuyệt kĩ
                  OpenNguyenLieu(pl, item);
                  break;
               case 1526: // hộp cải trang Hit
                  OpenHit(pl, item);
                  break;
               case 1785: // hộp bé 3 100k
                  HopBeBa100k(pl, item);
                  break;
               case 1786: // hộp bé 3 1000k
                  HopBeBa1000k(pl, item);
                  break;
               case 1787: // hộp tân thủ
                  OpenTanThu(pl, item);
                  break;
               case 1788: // hộp tân thủ VIP
                  OpenTanThuVIP(pl, item);
                  break;
               case 1780: // hộp cải trang Hit
                  OpenPhongAnBoss(pl, item);
                  break;
               case 2040: // Bánh Nướng
                  OpenHopBanhNuong(pl, item);
                  break;
               case 2041: // Bánh Dẻo
                  OpenHopBanhDeo(pl, item);
                  break;
               case 1703: // Thùng 333
                  OpenThung333(pl, item);
                  break;
               case 1704: // Thùng Ken
                  OpenThungKen(pl, item);
                  break;
               case 1705: // Thùng Tiger
                  OpenThungTiger(pl, item);
                  break;
               case 2006:
                  Input.gI().createFormChangeNameByItem(pl);
                  break;
               case 1479: // rương spl vip
                  RuongSaoPhaLe(pl, item);
                  break;
               case 1370: // Hộp Sự kiện 10 tháng 3
                  HopQua10Thang3(pl, item);
                  break;
               case 1371: // Hộp Sự kiện 10 tháng 3 VIP
                  HopQua10Thang3VIP(pl, item);
                  break;
               case 1368: // Trứng pet Khủng Long
                  TrungKhungLong(pl, item);
                  break;
               case 1334: // hộp đồ thần linh
                  hopthanlinh(pl, item);
                  break;
               case 2042: // cai nit
                  NpcService.gI().createMenuConMeo(pl, ConstNpc.HOP_BANH_NUONG, 29028,
                        "Làm bánh trung thu black - gold phiên bản li mít tịt loại 1"
                              + "\nCần:"
                              + "\n x99 Thỏi Vàng"
                              + "\n x99 Bột Mì"
                              + "\n x99 Ngũ Cốc"
                              + "\n x99 Thịt Lợn"
                              + "\n x99 Trứng Vịt"
                              + "\n x1 Vỏ Hộp Bánh Fake"
                              + "\n\n Ăn bánh này chắc chắn tiêu chảy!!!",
                        "Bắt đầu\n Làm Bánh");
                  break;
               case 2043: // cai nit
                  NpcService.gI().createMenuConMeo(pl, ConstNpc.HOP_BANH_DEO, 29028,
                        "Làm bánh trung thu black - gold phiên bản li mít tịt loại 2"
                              + "\nCần:"
                              + "\n x99 Thỏi Vàng"
                              + "\n x99 Bột Mì"
                              + "\n x99 Ngũ Cốc"
                              + "\n x99 Đậu Xanh"
                              + "\n x99 Đường Trắng"
                              + "\n x1 Vỏ Hộp Bánh Fake"
                              + "\n\n Ăn bánh này chắc chắn tiêu chảy!!!",
                        "Bắt đầu\n Làm Bánh");
                  break;
               case 1707: // Tờ 500k
                  NpcService.gI().createMenuConMeo(pl, ConstNpc.MENU_TO_500K, 30054,
                        "Có thể sử dụng để đổi tờ 500k Khoá Thành Không Khoá"
                              + "\nCần:"
                              + "\n x99k Thỏi Vàng"
                              + "\n x1 Tờ 500k khoá"
                              + "\n|7|Lưu Ý: Nếu có tờ 500k không khoá \nNên cất tiền không khoá vào rương trước khi đổi",
                        "Đổi\nLuôn");
                  break;
               case 1460: // hộp đồ thần linh
                  hopHuyDiet(pl, item);
                  break;
               case 1399: // rương skh vip
                  ruongskhVIP(pl, item);
                  break;
               case 1407: // rương skh vip
                  RuongSkhThanhTon(pl, item);
                  break;
               case 1296: // cskb
                  NpcService.gI().createMenuConMeo(pl, ConstNpc.MENU_MAY_DO_BOSS, -1,
                        "|7|MÁY DÒ BOSS\n"
                              + "|1|Tele trực tiếp tới bos\n"
                              + "|2|Các boss thường sẽ chỉ tốn máy dò:\n"
                              + "Các boss vip muốn di chuyển tới sẽ cần thêm 20 xu bạc\n"
                              + "xu bạc săn từ boss hoặc đổi từ coin ở quy lão kame\n"
                              + "|3|cân nhắc kĩ trước khi click vào mày dò boss VIP\n"
                              + "Khi click mở máy dò boss vip sẽ bị trừ trực tiếp 20 xu bạc",
                        "Boss\nThường", "Boss\nVIP");
                  break;
               case ConstItem.CAPSULE_TET_2022:
                  openCapsuleTet2022(pl, item);
                  break;
               default:
                  switch (item.template.type) {
                     case 7: // sách học, nâng skill
                        learnSkill(pl, item);
                        break;
                     case 12: // ngọc rồng các loại
                        // Service.getInstance().sendThongBaoOK(pl, "Bảo trì tính năng.");
                        controllerCallRongThan(pl, item);
                        break;
                     case 11: // item flag bag
                        useItemChangeFlagBag(pl, item);
                        break;
                  }
            }
         }
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
      }
   }

   private static final int[][][] ACTIVATION_SET_NO = { { { 129, 141 }, { 127, 139 }, { 128, 140 } }, // songoku - thien
                                                                                                      // xin hang -
                                                                                                      // kirin
         { { 131, 143 }, { 132, 144 }, { 130, 142 } }, // oc tieu - pikkoro daimao - picolo
         { { 135, 138 }, { 133, 136 }, { 134, 137 } } // kakarot - cadic - nappa
   };

   public void addOptionSkhStt(int gender, Item item, int stt) {
      int[] idOption = ACTIVATION_SET_NO[gender][stt];
      item.itemOptions.add(new ItemOption(idOption[0], 1)); // tên set
      item.itemOptions.add(new ItemOption(idOption[1], 100)); // hiệu ứng set
      item.itemOptions.add(new ItemOption(30, 1)); // không thể giao dịch
   }

   public void addOptionSkhHd(int gender, Item item, int stt) {
      int[] idOption = ACTIVATION_SET_NO[gender][stt];
      item.itemOptions.add(new ItemOption(idOption[0], 1)); // tên set
      item.itemOptions.add(new ItemOption(idOption[1], 175)); // hiệu ứng set
      item.itemOptions.add(new ItemOption(30, 1)); // không thể giao dịch
   }

   public void addOptionSkhTL(int gender, Item item, int stt) {
      int[] idOption = ACTIVATION_SET_NO[gender][stt];
      item.itemOptions.add(new ItemOption(idOption[0], 1)); // tên set
      item.itemOptions.add(new ItemOption(idOption[1], 150)); // hiệu ứng set
      item.itemOptions.add(new ItemOption(30, 1)); // không thể giao dịch
   }

   public void addOptionSkhTS(int gender, Item item, int stt) {
      int[] idOption = ACTIVATION_SET_NO[gender][stt];
      item.itemOptions.add(new ItemOption(idOption[0], 1)); // tên set
      item.itemOptions.add(new ItemOption(idOption[1], 200)); // hiệu ứng set
      item.itemOptions.add(new ItemOption(30, 1)); // không thể giao dịch
   }

   public void openskhNomal(Player pl, int gender, int stt, short idItem) {
      try {
         Item item = InventoryService.gI().findItemBagByTemp(pl, (short) idItem);
         if (item == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
         }
         if (InventoryService.gI().getCountEmptyBag(pl) >= 5) {
            short[] dotd = { 0, 6, 21, 27, 12 };
            short[] donm = { 1, 7, 22, 28, 12 };
            short[] doxd = { 2, 8, 23, 29, 12 };
            switch (gender) {
               case 0: {
                  for (int i = 0; i < dotd.length; i++) {
                     Item it = ItemService.gI().createNewItem(dotd[i]);
                     RewardService.gI().initBaseOptionClothes(it.template.id, it.template.type, it.itemOptions);
                     addOptionSkhStt(gender, it, stt);
                     it.itemOptions.add(new ItemOption(107, 5));
                     InventoryService.gI().addItemBag(pl, it, 0);
                  }
                  break;
               }
               case 1: {
                  for (int i = 0; i < donm.length; i++) {
                     Item it = ItemService.gI().createNewItem(donm[i]);
                     RewardService.gI().initBaseOptionClothes(it.template.id, it.template.type, it.itemOptions);
                     addOptionSkhStt(gender, it, stt);
                     it.itemOptions.add(new ItemOption(107, 5));
                     InventoryService.gI().addItemBag(pl, it, 0);
                  }
                  break;
               }
               case 2: {
                  for (int i = 0; i < doxd.length; i++) {
                     Item it = ItemService.gI().createNewItem(doxd[i]);
                     RewardService.gI().initBaseOptionClothes(it.template.id, it.template.type, it.itemOptions);
                     addOptionSkhStt(gender, it, stt);
                     it.itemOptions.add(new ItemOption(107, 5));
                     InventoryService.gI().addItemBag(pl, it, 0);
                  }
                  break;
               }
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
         } else {
            Service.getInstance().sendThongBao(pl, "Hành trang không đủ chổ trống");
         }

      } catch (Exception e) {
      }
   }

   public void openskhVip(Player pl, int gender, int stt, short idItem, int typeCheck) {
      try {
         Item item = InventoryService.gI().findItemBagByTemp(pl, (short) idItem);
         if (item == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
         }
         if (InventoryService.gI().getCountEmptyBag(pl) >= 5) {
            if (typeCheck == 1) {
               for (int i = 0; i < Manager.DoThanhTon.length; i++) {
                  int skhId = ItemService.gI().randomSKHThanhTon((byte) gender);
                  Item it = ItemService.gI().createNewItem(Manager.DoThanhTon[i]);
                  it = Util.ratiItemThanhTon(it.template.id);
                  it.itemOptions.add(new ItemOption(skhId, 1));
                  it.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKHThanhTon(skhId), 1));
                  it.itemOptions.remove(it.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21)
                        .findFirst().get());
                  it.itemOptions.add(new ItemOption(5, 30));
                  it.itemOptions.add(new ItemOption(76, 1));
                  it.itemOptions.add(new ItemOption(21, 200));
                  it.itemOptions.add(new ItemOption(30, 1));
                  InventoryService.gI().addItemBag(pl, it, 1);
               }
            } else if (typeCheck == 2) {
               short[] dotd = { 555, 556, 562, 563, 561 };
               short[] donm = { 557, 558, 564, 565, 561 };
               short[] doxd = { 559, 560, 566, 567, 561 };
               switch (gender) {
                  case 0: {
                     for (int i = 0; i < dotd.length; i++) {
                        Item it = ItemService.gI().createNewItem(dotd[i]);
                        it = Util.ratiItemTL(it.template.id);
                        addOptionSkhTL(gender, it, stt);
                        InventoryService.gI().addItemBag(pl, it, 0);
                     }
                     break;
                  }
                  case 1: {
                     for (int i = 0; i < donm.length; i++) {
                        Item it = ItemService.gI().createNewItem(donm[i]);
                        it = Util.ratiItemTL(it.template.id);
                        addOptionSkhTL(gender, it, stt);
                        InventoryService.gI().addItemBag(pl, it, 0);
                     }
                     break;
                  }
                  case 2: {
                     for (int i = 0; i < doxd.length; i++) {
                        Item it = ItemService.gI().createNewItem(doxd[i]);
                        it = Util.ratiItemTL(it.template.id);
                        addOptionSkhTL(gender, it, stt);
                        InventoryService.gI().addItemBag(pl, it, 0);
                     }
                     break;
                  }
               }
            } else if (typeCheck == 3) {
               short[] dotd = { 650, 651, 657, 658, 656 };
               short[] donm = { 652, 653, 659, 660, 656 };
               short[] doxd = { 654, 655, 661, 662, 656 };
               switch (gender) {
                  case 0: {
                     for (int i = 0; i < dotd.length; i++) {
                        Item it = ItemService.gI().createNewItem(dotd[i]);
                        it = Util.ratiItemHuyDiet(it.template.id);
                        addOptionSkhHd(gender, it, stt);
                        InventoryService.gI().addItemBag(pl, it, 0);
                     }
                     break;
                  }
                  case 1: {
                     for (int i = 0; i < donm.length; i++) {
                        Item it = ItemService.gI().createNewItem(donm[i]);
                        it = Util.ratiItemHuyDiet(it.template.id);
                        addOptionSkhHd(gender, it, stt);
                        InventoryService.gI().addItemBag(pl, it, 0);
                     }
                     break;
                  }
                  case 2: {
                     for (int i = 0; i < doxd.length; i++) {
                        Item it = ItemService.gI().createNewItem(doxd[i]);
                        it = Util.ratiItemHuyDiet(it.template.id);
                        addOptionSkhHd(gender, it, stt);
                        InventoryService.gI().addItemBag(pl, it, 0);
                     }
                     break;
                  }
               }
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
         } else {
            Service.getInstance().sendThongBao(pl, "Hành trang không đủ chổ trống");
         }
      } catch (Exception e) {
      }
   }

   private void skhtd(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SKH_TD, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Songoku", "Kaioken", "Kirin");
   }

   private void skhnm(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SKH_NM, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Ốc tiêu", "Đẻ trứng", "Picolo");
   }

   private void skhxd(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SKH_XD, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Nappa", "Kakarot", "Cadic");
   }

   private void Hopts(Player pl, Item item) {// hop qua do huy diet
      NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi",
            "Set trái đất", "Set namec", "Set xayda", "Từ chổi");
   }

   public void skh_thanh_ton(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SKH_THANH_TON, -1, "Chọn hành tinh của mày đi",
            "Set trái đất", "Set namec", "Set xayda");
   }

   public void skh_than_linh(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SKH_THAN_LINH, -1, "Chọn hành tinh của mày đi",
            "Set trái đất", "Set namec", "Set xayda");
   }

   public void skh_huy_diet(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SKH_HUY_DIET, -1, "Chọn hành tinh của mày đi",
            "Set trái đất", "Set namec", "Set xayda");
   }

   public void Set_TraiDat_TL(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SET_TD_TL, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Songoku", "Kaioken", "Kirin");
   }

   public void Set_Namec_TL(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SET_NM_TL, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Ốc tiêu", "Đẻ trứng", "Picolo");
   }

   public void Set_Xayda_TL(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SET_XD_TL, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Nappa", "Kakarot", "Cadic");
   }

   public void Set_TraiDat_HD(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SET_TD_HD, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Songoku", "Kaioken", "Kirin");
   }

   public void Set_Namec_HD(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SET_NM_HD, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Ốc tiêu", "Đẻ trứng", "Picolo");
   }

   public void Set_Xayda_HD(Player pl) {
      NpcService.gI().createMenuConMeo(pl, ConstNpc.SET_XD_HD, -1, "Chọn Set kích hoạt bạn muốn!!!",
            "Nappa", "Kakarot", "Cadic");
   }

   private void changePet(Player player, Item item) {
      if (InventoryService.gI().getCountEmptyBody(player.pet) == 10) {
         if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
               gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
         } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
         }
      } else {
         Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử");
      }
   }

   private void chonHtDeTu(Player player, Item item) {
      if (player.pet == null) {
         Service.getInstance().sendThongBao(player, "Hãy về nhà nhận để từ trước khi sử dụng");
         return;
      }
      if (InventoryService.gI().getCountEmptyBody(player.pet) != 10) {
         Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử ra để tránh mất đồ");
         return;
      }
      NpcService.gI().createMenuConMeo(player, item.template.id, item.template.iconID,
            "Chọn hành tinh đệ tử bạn muốn",
            "Trái Đất", "Namec", "Xayda");
   }

   private void OpenNguyenLieu(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 1506, 1507 };
         short trangsach = 1516;
         short bua = 1508;
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(60, 100)) {
            Item it = ItemService.gI().createNewItem(trangsach, 50);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else if (Util.isTrue(15, 100)) {
            Item it = ItemService.gI().createNewItem(bua);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenHit(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 16, 15 };
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(50, 100)) {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else {
            Item it = ItemService.gI().createNewItem((short) ConstItem.CAI_TRANG_HIT);
            if (Util.isTrue(10, 100)) {
               it.itemOptions.add(new ItemOption(5, Util.nextInt(120, 150)));
            } else {
               if (Util.isTrue(20, 100)) {
                  it.itemOptions.add(new ItemOption(5, Util.nextInt(90, 120)));
               } else {
                  it.itemOptions.add(new ItemOption(5, Util.nextInt(70, 120)));
               }
            }
            it.itemOptions.add(new ItemOption(93, Util.nextInt(2, 5)));
            it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 50)));
            it.itemOptions.add(new ItemOption(14, Util.nextInt(5, 15)));
            // if (Util.isTrue(90, 100)) {
            // } else {
            // it.itemOptions.add(new ItemOption(73, 1));
            // }
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenPhongAnBoss(Player pl, Item item) {
      InventoryService.gI().subQuantityItemsBag(pl, item, 1);
      InventoryService.gI().sendItemBags(pl);
      NpcService.gI().createMenuConMeo(pl, ConstNpc.MENU_TRIEU_HOI_BOSS, 30689,
            "|8|Triệu Hồi Boss\n"
                  + "Muốn triệu hồi con nào!!!",
            "Boss\nVĩ Thú", "Boss\nTương Lai", "Boss\nNgoại Vực",
            "Boss\nTrạm Tàu", "Boss\nNew");
   }

   private void OpenTanThu(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 10) {
         Item tanthu13 = ItemService.gI().createNewItem((short) ConstItem.VIEN_CAPSULE_DAC_BIET);
         tanthu13.itemOptions.add(new ItemOption(30, 1));
         InventoryService.gI().addItemBag(pl, tanthu13, 1);

         int[] listlinhthu = { 1283, 1284, 1285, 1286, 1287 };
         Item tanthu1 = ItemService.gI().createNewItem((short) listlinhthu[Util.nextInt(0, listlinhthu.length - 1)]);
         tanthu1.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
         tanthu1.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
         tanthu1.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
         tanthu1.itemOptions.add(new ItemOption(117, Util.nextInt(1, 10)));
         tanthu1.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
         tanthu1.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu1, 1);

         Item tanthu2 = ItemService.gI().createNewItem((short) ConstItem.PET_CABIBARA);
         tanthu2.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
         tanthu2.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
         tanthu2.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
         tanthu2.itemOptions.add(new ItemOption(5, Util.nextInt(1, 5)));
         tanthu2.itemOptions.add(new ItemOption(93, Util.nextInt(2, 4)));
         tanthu2.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu2, 1);

         int[] listct = { 1813, 1814, 1815, 1816 };
         Item tanthu3 = ItemService.gI().createNewItem((short) listct[Util.nextInt(0, listct.length - 1)]);
         tanthu3.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
         tanthu3.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
         tanthu3.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
         tanthu3.itemOptions.add(new ItemOption(101, Util.nextInt(50, 150)));
         tanthu3.itemOptions.add(new ItemOption(93, Util.nextInt(2, 4)));
         tanthu3.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu3, 1);

         Item tanthu4 = ItemService.gI().createNewItem((short) 733);
         tanthu4.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
         tanthu4.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
         tanthu4.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
         tanthu4.itemOptions.add(new ItemOption(16, Util.nextInt(1, 30)));
         tanthu4.itemOptions.add(new ItemOption(95, Util.nextInt(1, 7)));
         tanthu4.itemOptions.add(new ItemOption(96, Util.nextInt(1, 7)));
         tanthu4.itemOptions.add(new ItemOption(93, Util.nextInt(2, 4)));
         tanthu4.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu4, 1);

         Item tanthu6 = ItemService.gI().createNewItem((short) ConstItem.NGOC_BOI_1);
         tanthu6.itemOptions.add(new ItemOption(50, Util.nextInt(15, 30)));
         tanthu6.itemOptions.add(new ItemOption(77, Util.nextInt(15, 30)));
         tanthu6.itemOptions.add(new ItemOption(103, Util.nextInt(15, 30)));
         tanthu6.itemOptions.add(new ItemOption(101, Util.nextInt(10, 100)));
         tanthu6.itemOptions.add(new ItemOption(93, Util.nextInt(2, 4)));
         tanthu6.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu6, 1);

         // ==================Sách
         int idSach;
         if (pl.gender == 0) {
            idSach = ConstItem.SACH_TD;
         } else if (pl.gender == 1) {
            idSach = ConstItem.SACH_NM;
         } else {
            idSach = ConstItem.SACH_XD;
         }
         Item tanthu9 = ItemService.gI().createNewItem((short) idSach);
         tanthu9.itemOptions.add(new ItemOption(50, Util.nextInt(5, 20)));
         tanthu9.itemOptions.add(new ItemOption(93, 2));
         tanthu9.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu9, 1);

         // =======================Chân mệnh
         Item tanthu10 = ItemService.gI().createNewItem((short) ConstItem.CHAN_MENH_6);
         tanthu10.itemOptions.add(new ItemOption(50, Util.nextInt(5, 30)));
         tanthu10.itemOptions.add(new ItemOption(77, Util.nextInt(5, 30)));
         tanthu10.itemOptions.add(new ItemOption(103, Util.nextInt(5, 30)));
         tanthu10.itemOptions.add(new ItemOption(93, 2));
         tanthu10.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu10, 1);

         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenTanThuVIP(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 10) {
         Item tanthu1 = ItemService.gI().createNewItem((short) ConstItem.LA_CO_VIETNAM);
         tanthu1.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
         tanthu1.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
         tanthu1.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
         tanthu1.itemOptions.add(new ItemOption(117, Util.nextInt(1, 10)));
         tanthu1.itemOptions.add(new ItemOption(93, 7));
         tanthu1.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu1, 1);

         Item tanthu2 = ItemService.gI().createNewItem((short) 1006);
         tanthu2.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
         tanthu2.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
         tanthu2.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
         tanthu2.itemOptions.add(new ItemOption(5, Util.nextInt(1, 5)));
         tanthu2.itemOptions.add(new ItemOption(93, 7));
         tanthu2.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu2, 1);

         Item tanthu3 = ItemService.gI().createNewItem((short) (604 + pl.gender));
         tanthu3.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
         tanthu3.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
         tanthu3.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
         tanthu3.itemOptions.add(new ItemOption(101, Util.nextInt(50, 150)));
         tanthu3.itemOptions.add(new ItemOption(93, 7));
         tanthu3.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu3, 1);

         Item tanthu4 = ItemService.gI().createNewItem((short) 1503);
         tanthu4.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
         tanthu4.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
         tanthu4.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
         tanthu4.itemOptions.add(new ItemOption(16, Util.nextInt(1, 30)));
         tanthu4.itemOptions.add(new ItemOption(95, Util.nextInt(1, 7)));
         tanthu4.itemOptions.add(new ItemOption(96, Util.nextInt(1, 7)));
         tanthu4.itemOptions.add(new ItemOption(93, 7));
         tanthu4.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu4, 1);

         Item tanthu6 = ItemService.gI().createNewItem((short) ConstItem.NGOC_BOI_1);
         tanthu6.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
         tanthu6.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
         tanthu6.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
         tanthu6.itemOptions.add(new ItemOption(101, Util.nextInt(50, 100)));
         tanthu6.itemOptions.add(new ItemOption(93, 7));
         tanthu6.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu6, 1);

         // ==================Sách
         int idSach;
         if (pl.gender == 0) {
            idSach = ConstItem.SACH_TD;
         } else if (pl.gender == 1) {
            idSach = ConstItem.SACH_NM;
         } else {
            idSach = ConstItem.SACH_XD;
         }
         Item tanthu9 = ItemService.gI().createNewItem((short) idSach);
         tanthu9.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
         tanthu9.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
         tanthu9.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
         tanthu9.itemOptions.add(new ItemOption(93, 7));
         tanthu9.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu9, 1);

         // =======================Chân mệnh
         Item tanthu10 = ItemService.gI().createNewItem((short) ConstItem.CHAN_MENH_6);
         tanthu10.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
         tanthu10.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
         tanthu10.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
         tanthu10.itemOptions.add(new ItemOption(93, 7));
         tanthu10.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().addItemBag(pl, tanthu10, 1);

         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void HopBeBa100k(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] petbeba = { 1781, 1782, 1783, 1784 };
         byte index = (byte) Util.nextInt(0, petbeba.length - 1);
         Item it = ItemService.gI().createNewItem(petbeba[index]);
         it.itemOptions.add(new ItemOption(50, Util.nextInt(70, 220)));
         it.itemOptions.add(new ItemOption(77, Util.nextInt(70, 220)));
         it.itemOptions.add(new ItemOption(103, Util.nextInt(70, 220)));
         it.itemOptions.add(new ItemOption(14, Util.nextInt(3, 15)));
         it.itemOptions.add(new ItemOption(94, Util.nextInt(50, 150)));
         if (Util.isTrue(30, 100)) {
            it.itemOptions.add(new ItemOption(5, Util.nextInt(10, 25)));
         }
         if (Util.isTrue(30, 100)) {
            it.itemOptions.add(new ItemOption(101, Util.nextInt(50, 500)));
         }
         it.itemOptions.add(new ItemOption(80, Util.nextInt(5, 30)));
         it.itemOptions.add(new ItemOption(81, Util.nextInt(5, 30)));
         it.itemOptions.add(new ItemOption(76, 1));
         it.itemOptions.add(new ItemOption(73, 1));
         InventoryService.gI().addItemBag(pl, it, 1);
         Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void HopBeBa1000k(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] petbeba = { 1781, 1782, 1783, 1784 };
         byte index = (byte) Util.nextInt(0, petbeba.length - 1);
         Item it = ItemService.gI().createNewItem(petbeba[index]);
         it.itemOptions.add(new ItemOption(50, Util.nextInt(150, 300)));
         it.itemOptions.add(new ItemOption(77, Util.nextInt(150, 300)));
         it.itemOptions.add(new ItemOption(103, Util.nextInt(150, 300)));
         it.itemOptions.add(new ItemOption(14, Util.nextInt(5, 20)));
         it.itemOptions.add(new ItemOption(94, Util.nextInt(30, 250)));
         if (Util.isTrue(30, 100)) {
            it.itemOptions.add(new ItemOption(5, Util.nextInt(10, 40)));
         }
         if (Util.isTrue(30, 100)) {
            it.itemOptions.add(new ItemOption(101, Util.nextInt(100, 1000)));
         }
         it.itemOptions.add(new ItemOption(80, Util.nextInt(15, 40)));
         it.itemOptions.add(new ItemOption(81, Util.nextInt(15, 40)));
         it.itemOptions.add(new ItemOption(76, 1));
         it.itemOptions.add(new ItemOption(73, 1));
         InventoryService.gI().addItemBag(pl, it, 1);
         Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenThungKen(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 1791, 1792, 1793, 1794, 1795, 1796, 1559, 1560, 1563, 1564, 1565 };
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(90, 100)) {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, Util.nextInt(1, 3));
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else {
            Item it = ItemService.gI().createNewItem((short) ConstItem.CT_LINH_VIETNAM);
            it.itemOptions.add(new ItemOption(50, Util.nextInt(300, 690)));
            it.itemOptions.add(new ItemOption(77, Util.nextInt(300, 690)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(300, 690)));
            it.itemOptions.add(new ItemOption(101, Util.nextInt(300, 990)));
            it.itemOptions.add(new ItemOption(117, Util.nextInt(5, 25)));
            it.itemOptions.add(new ItemOption(116, 1));
            it.itemOptions.add(new ItemOption(106, 1));
            it.itemOptions.add(new ItemOption(18, Util.nextInt(30, 66)));
            it.itemOptions.add(new ItemOption(76, 1));
            it.itemOptions.add(new ItemOption(30, 1));
            if (Util.isTrue(1, 100)) {
               it.itemOptions.add(new ItemOption(5, Util.nextInt(69, 99)));
               it.itemOptions.add(new ItemOption(73, 1));
               it.itemOptions.add(new ItemOption(33, 1));
            } else {
               it.itemOptions.add(new ItemOption(5, Util.nextInt(69, 99)));
               it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
               it.itemOptions.add(new ItemOption(199, 1));
            }
            PlayerDAO.congDiemEven2thang9(pl, 1);
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenThungTiger(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 1791, 1792, 1793, 1794, 1795, 1796, 1559, 1560, 1563, 1564, 1565 };
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(90, 100)) {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, Util.nextInt(1, 2));
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else {
            Item it = ItemService.gI().createNewItem((short) ConstItem.PET_XETANK);
            it.itemOptions.add(new ItemOption(50, Util.nextInt(300, 600)));
            it.itemOptions.add(new ItemOption(77, Util.nextInt(300, 600)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(300, 600)));
            it.itemOptions.add(new ItemOption(14, Util.nextInt(15, 30)));
            it.itemOptions.add(new ItemOption(94, Util.nextInt(15, 45)));
            it.itemOptions.add(new ItemOption(80, Util.nextInt(10, 25)));
            it.itemOptions.add(new ItemOption(81, Util.nextInt(10, 25)));
            it.itemOptions.add(new ItemOption(76, 1));
            it.itemOptions.add(new ItemOption(30, 1));
            if (Util.isTrue(1, 100)) {
               it.itemOptions.add(new ItemOption(5, Util.nextInt(49, 69)));
               it.itemOptions.add(new ItemOption(73, 1));
            } else {
               it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 2)));
               it.itemOptions.add(new ItemOption(199, 1));
               it.itemOptions.add(new ItemOption(5, Util.nextInt(49, 69)));
            }
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         PlayerDAO.congDiemEven2thang9(pl, 1);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void anDanDuoc(Player pl, Item item) {
      long csTangThem = Util.nextInt(500, 1000);
      String thongBao;
      if (pl.nPoint.dameg < 29900D) {
         Service.getInstance().sendThongBaoOK(pl, "Chưa đủ ngưỡng sức đánh gốc\n"
               + "không thể sử dụng đan dược lúc này\n"
               + "sức đánh gốc yêu cầu 29k9");
         return;
      }
      if (pl.nPoint.hpg < 999000D) {
         Service.getInstance().sendThongBaoOK(pl, "Chưa đủ ngưỡng sức đánh gốc\n"
               + "không thể sử dụng đan dược lúc này\n"
               + "hp gốc yêu cầu 999k");
         return;
      }
      if (pl.nPoint.mpg < 999000D) {
         Service.getInstance().sendThongBaoOK(pl, "Chưa đủ ngưỡng sức đánh gốc\n"
               + "không thể sử dụng đan dược lúc này\n"
               + "ki gốc yêu cầu 999k");
         return;
      }
      if (item.template.id == 699) { // Bổ Huyết Đan
         if (pl.bohuyetdan >= 1000) {
            Service.getInstance().sendThongBaoOK(pl, "Ăn tối đa 1000 viên đan loại này");
            return;
         }
         pl.nPoint.hpg += csTangThem;
         pl.bohuyetdan++;
         thongBao = "Sử dụng Bổ Huyết Đan:\n"
               + "Tăng thêm " + Util.format(csTangThem) + " HP gốc";
      } else if (item.template.id == 700) { // Cường Lực Đan
         if (pl.tangnguyendan >= 1000) {
            Service.getInstance().sendThongBaoOK(pl, "Ăn tối đa 1000 viên đan loại này");
            return;
         }
         pl.nPoint.dameg += csTangThem;
         pl.tangnguyendan++;
         thongBao = "Sử dụng Cường Lực Đan:\n"
               + "Tăng thêm " + Util.format(csTangThem) + " sát thương gốc";
      } else if (item.template.id == 701) { // Bổ Khí Đan
         if (pl.bokhidan >= 1000) {
            Service.getInstance().sendThongBaoOK(pl, "Ăn tối đa 1000 viên đan loại này");
            return;
         }
         pl.nPoint.mpg += csTangThem;
         pl.bokhidan++;
         thongBao = "Sử dụng Bổ Khí Đan:\n"
               + "Tăng thêm " + Util.format(csTangThem) + " KI gốc";
      } else {
         Service.getInstance().sendThongBao(pl, "Vật phẩm không hợp lệ!");
         return;
      }

      InventoryService.gI().subQuantityItemsBag(pl, item, 1);
      InventoryService.gI().sendItemBags(pl);
      Service.getInstance().sendThongBaoOK(pl, thongBao);
   }

   private void OpenThung333(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 1791, 1792, 1793, 1794, 1795, 1796, 1559, 1560, 1563, 1564, 1565 };
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(90, 100)) {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, Util.nextInt(1, 2));
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else {
            Item it = ItemService.gI().createNewItem((short) ConstItem.LA_CO_VIETNAM);
            it.itemOptions.add(new ItemOption(50, Util.nextInt(300, 600)));
            it.itemOptions.add(new ItemOption(77, Util.nextInt(300, 600)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(300, 600)));
            it.itemOptions.add(new ItemOption(117, Util.nextInt(45, 89)));
            it.itemOptions.add(new ItemOption(19, Util.nextInt(40, 89)));
            it.itemOptions.add(new ItemOption(76, 1));
            it.itemOptions.add(new ItemOption(30, 1));
            if (Util.isTrue(1, 100)) {
               it.itemOptions.add(new ItemOption(209, 6));
               it.itemOptions.add(new ItemOption(162, 6));
               it.itemOptions.add(new ItemOption(73, 1));
               it.itemOptions.add(new ItemOption(5, Util.nextInt(49, 69)));
            } else {
               it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
               it.itemOptions.add(new ItemOption(199, 1));
               it.itemOptions.add(new ItemOption(5, Util.nextInt(49, 69)));
            }
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         PlayerDAO.congDiemEven2thang9(pl, 1);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenHopTriAn(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         Item qua = null;
         int soLuong = 1;
         if (Util.isTrue(1, 100000)) {
            qua = ItemService.gI().createNewItem((short) ConstItem.TRUNG_SOI);// TRỨNG ĐỆ SÓI
            qua.quantity = soLuong;
         } else if (Util.isTrue(5, 10000)) {
            qua = ItemService.gI().createNewItem((short) ConstItem.TRUNG_TIEN); // TRỨNG ĐỆ TIÊN
            qua.quantity = soLuong;
         } else if (Util.isTrue(1, 1000)) {
            qua = ItemService.gI().createNewItem((short) ConstItem.TRUNG_KAIDO); // TRỨNG ĐỆ KAIDO
            qua.quantity = soLuong;
         } else if (Util.isTrue(5, 1000)) {
            qua = ItemService.gI().createNewItem((short) ConstItem.TRUNG_ITACHI); // TRỨNG ĐỆ ITACHI
            qua.quantity = soLuong;
         } else if (Util.isTrue(5, 100)) {
            int[] dacuonghoa = { 1565, 1559, 1560, 1561 };
            int randomIndex = Util.nextInt(0, dacuonghoa.length - 1);
            qua = ItemService.gI().createNewItem((short) dacuonghoa[randomIndex]); // Lấy ngẫu nhiên 1 viên đá cường hoá
                                                                                   // trong list
            soLuong = 1;
            qua.quantity = soLuong;
         } else if (Util.isTrue(5, 100)) {
            qua = ItemService.gI().createNewItem((short) ConstItem.HOP_SPL_VIP);// HỘP SAO PHA LÊ VIP
            soLuong += Util.nextInt(1, 3);
            qua.quantity = soLuong;
         } else if (Util.isTrue(3, 10)) {
            qua = ItemService.gI().createNewItem((short) ConstItem.XU_VANG); // XU VÀNG
            soLuong += Util.nextInt(10, 30);
            qua.quantity = soLuong;
         } else if (Util.isTrue(5, 10)) {
            qua = ItemService.gI().createNewItem((short) ConstItem.XU_BAC); // XU BẠC
            soLuong += Util.nextInt(20, 200);
            qua.quantity = soLuong;
         } else {
            qua = ItemService.gI().createNewItem((short) ConstItem.THOI_VANG);// THỎI VÀNG
            soLuong += Util.nextInt(1000000, 2000000);
            qua.quantity = soLuong;
         }
         qua.itemOptions.add(new ItemOption(73, 1));
         InventoryService.gI().addItemBag(pl, qua, soLuong);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
         Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + soLuong + " " + qua.template.name);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenHopBanhDeo(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 1015, 1687, 1559, 1560, 1561, 1563, 1564, 1565, 1792, 1793, 1794, 1795, 1796, 1737, 1738,
               1739 };
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(1, 100)) {
            Item kimDan = ItemService.gI().createNewItem((short) ConstItem.KIM_DAN_SO_CAP);
            kimDan.itemOptions.add(new ItemOption(50, 20));
            kimDan.itemOptions.add(new ItemOption(77, 30));
            kimDan.itemOptions.add(new ItemOption(103, 30));
            InventoryService.gI().addItemBag(pl, kimDan, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + kimDan.template.name);
         } else {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, Util.nextInt(1, 2));
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         pl.evenTrungThu++;
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenHopBanhNuong(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 1015, 1687, 1559, 1560, 1561, 1563, 1564, 1565, 1792, 1793, 1794, 1795, 1796, 1737, 1738,
               1739 };
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(1, 100)) {
            Item kimDan = ItemService.gI().createNewItem((short) ConstItem.KIM_DAN_SO_CAP);
            kimDan.itemOptions.add(new ItemOption(50, 20));
            kimDan.itemOptions.add(new ItemOption(77, 30));
            kimDan.itemOptions.add(new ItemOption(103, 30));
            InventoryService.gI().addItemBag(pl, kimDan, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + kimDan.template.name);
         } else {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, Util.nextInt(1, 2));
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         pl.evenTrungThu += 2;
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void OpenPokemon(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] thuong = { 16, 15 };
         byte index = (byte) Util.nextInt(0, thuong.length - 1);
         if (Util.isTrue(50, 100)) {
            Item it = ItemService.gI().createNewItem(thuong[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else {
            Item it = ItemService.gI().createNewItem((short) ConstItem.POKEMON);
            it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 20)));
            it.itemOptions.add(new ItemOption(14, Util.nextInt(5, 10)));
            if (Util.isTrue(90, 100)) {
               it.itemOptions.add(new ItemOption(93, Util.nextInt(2, 5)));
            } else {
               it.itemOptions.add(new ItemOption(73, 1));
            }
            InventoryService.gI().addItemBag(pl, it, 1);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void openphapsu(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] manh = { 1232, 1233, 1234, 1689, 1690, 1691 };
         short da = 1235;
         short bua = 1236;
         short[] rac = { 579, 1201, 15 };
         byte index = (byte) Util.nextInt(0, manh.length - 1);
         byte index2 = (byte) Util.nextInt(0, rac.length - 1);
         short[] icon = new short[2];
         icon[0] = item.template.iconID;
         if (Util.isTrue(35, 100)) {
            Item it = ItemService.gI().createNewItem(rac[index2]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else if (Util.isTrue(13, 100)) {
            Item it = ItemService.gI().createNewItem(da);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else if (Util.isTrue(3, 100)) {
            Item it = ItemService.gI().createNewItem(bua);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         } else {
            Item it = ItemService.gI().createNewItem(manh[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 1);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
         CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void RuongSaoPhaLe(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] manh = { 1480, 1481, 1482 };
         byte index = (byte) Util.nextInt(0, manh.length - 1);
         short[] icon = new short[2];
         icon[0] = item.template.iconID;
         Item it = ItemService.gI().createNewItem(manh[index]);
         switch (it.template.id) {
            case 1480:
               it.itemOptions.add(new ItemOption(210, 8));
               break;
            case 1481:
               it.itemOptions.add(new ItemOption(211, 8));
               break;
            case 1482:
               it.itemOptions.add(new ItemOption(49, 5));
               break;
            default:
               break;
         }
         InventoryService.gI().addItemBag(pl, it, 1);
         icon[1] = it.template.iconID;
         Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
         CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   public void hopthanlinh(Player player, Item item) {
      byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length);
      Item thanlinh = Util.ratiItemTL(Manager.itemIds_TL[randomDo]);
      short[] icon = new short[2];
      icon[0] = item.template.iconID;
      if (InventoryService.gI().getCountEmptyBag(player) > 1) {
         thanlinh.itemOptions.add(new ItemOption(30, 1));
         InventoryService.gI().addItemBag(player, thanlinh, 1);
         Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + thanlinh.template.name);
         InventoryService.gI().subQuantityItemsBag(player, item, 1);
         icon[1] = thanlinh.template.iconID;
         InventoryService.gI().sendItemBags(player);
         CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
      }
   }

   public void cainit(Player player, Item item) {
      byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length);
      Item thanlinh = Util.ratiItemTL(Manager.itemIds_TL[randomDo]);
      short[] icon = new short[2];
      icon[0] = item.template.iconID;
      if (InventoryService.gI().getCountEmptyBag(player) > 1) {
         thanlinh.itemOptions.add(new ItemOption(30, 1));
         InventoryService.gI().addItemBag(player, thanlinh, 1);
         Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + thanlinh.template.name);
         InventoryService.gI().subQuantityItemsBag(player, item, 1);
         icon[1] = thanlinh.template.iconID;
         InventoryService.gI().sendItemBags(player);
         CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
      }
   }

   public void hopHuyDiet(Player player, Item item) {
      byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length);
      Item thanlinh = Util.ratiItemHuyDiet(Manager.itemIds_HuyDiet[randomDo]);
      short[] icon = new short[2];
      icon[0] = item.template.iconID;
      if (InventoryService.gI().getCountEmptyBag(player) > 1) {
         thanlinh.itemOptions.add(new ItemOption(30, 1));
         InventoryService.gI().addItemBag(player, thanlinh, 1);
         Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + thanlinh.template.name);
         InventoryService.gI().subQuantityItemsBag(player, item, 1);
         icon[1] = thanlinh.template.iconID;
         InventoryService.gI().sendItemBags(player);
         CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
      }
   }

   private void ruongskhVIP(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short itemId;
         int[] idhender = new int[] { 0, 1, 2, 3, 4 };
         int idr = new Random().nextInt(idhender.length);
         if (pl.gender == 3 || idr == 4) {
            itemId = Manager.radaSKHVip[Util.nextInt(0, 5)];
            if (Util.isTrue(1, (int) 100)) {
               itemId = Manager.radaSKHVip[6];
            }
         } else {
            itemId = Manager.doSKHVip[pl.gender][idr][Util.nextInt(0, 5)];
            if (Util.isTrue(1, 100)) {
               itemId = Manager.doSKHVip[pl.gender][idr][6];
            }
         }
         int skhId = ItemService.gI().randomSKHId(pl.gender);
         Item items;
         if (new Item(itemId).isDTL()) {
            items = Util.ratiItemTL(itemId);
            items.itemOptions.add(new ItemOption(skhId, 1));
            items.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 1));
            items.itemOptions.remove(items.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21)
                  .findFirst().get());
            items.itemOptions.add(new ItemOption(21, 15));
            items.itemOptions.add(new ItemOption(30, 1));
         } else {
            items = ItemService.gI().itemSKH(itemId, skhId);
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().addItemBag(pl, items, 1);
         InventoryService.gI().sendItemBags(pl);
         Service.getInstance().sendThongBao(pl, "|1| Bạn nhận được " + items.template.name);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void RuongSkhThanhTon(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short itemId;
         itemId = Manager.DoThanhTon[Util.nextInt(0, 4)];
         int skhId = ItemService.gI().randomSKHThanhTon(pl.gender);
         Item items;
         if (new Item(itemId).isThanhTon()) {
            items = Util.ratiItemThanhTon(itemId);
            items.itemOptions.add(new ItemOption(skhId, 1));
            items.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKHThanhTon(skhId), 1));
            items.itemOptions.remove(items.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21)
                  .findFirst().get());
            items.itemOptions.add(new ItemOption(21, 200));
            items.itemOptions.add(new ItemOption(30, 1));
         } else {
            items = ItemService.gI().itemSKH(itemId, skhId);
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().addItemBag(pl, items, 1);
         InventoryService.gI().sendItemBags(pl);
         Service.getInstance().sendThongBao(pl, "|1| Bạn nhận được " + items.template.name);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   public boolean maydoboss(Player pl) {
      try {
         BossManager.gI().showListBossMember(pl);
         return true;
      } catch (Exception e) {
      }
      return false;
   }

   public boolean maydobossVIP(Player pl) {
      try {
         BossManager.gI().showListBossVIP(pl);
         return true;
      } catch (Exception e) {
      }
      return false;
   }

   private void findNamekBall(Player pl, Item item) {
      List<NamekBall> balls = NamekBallManager.gI().getList();
      StringBuffer sb = new StringBuffer();
      for (NamekBall namekBall : balls) {
         Map m = namekBall.zone.map;
         sb.append(namekBall.getIndex() + 1).append(" Sao: ").append(m.mapName)
               .append(namekBall.getHolderName() == null ? "" : " - " + namekBall.getHolderName()).append("\n");
      }
      final int star = Util.nextInt(0, 6);
      final NamekBall ball = NamekBallManager.gI().findByIndex(star);
      final Inventory inventory = pl.inventory;
      MenuDialog menu = new MenuDialog(sb.toString(),
            new String[] { "Đến ngay\nViên " + (star + 1) + " Sao\n 50tr Vàng",
                  "Đến ngay\nViên " + (star + 1) + " Sao\n 5 Hồng ngọc" },
            new MenuRunable() {
               @Override
               public void run() {
                  switch (getIndexSelected()) {
                     case 0:
                        if (inventory.gold < 50000000) {
                           Service.getInstance().sendThongBao(pl, "Không đủ tiền");
                           return;
                        }
                        inventory.subGold(50000000);
                        ChangeMapService.gI().changeMap(pl, ball.zone, ball.x, ball.y);
                        break;
                     case 1:
                        if (inventory.ruby < 5) {
                           Service.getInstance().sendThongBao(pl, "Không đủ tiền");
                           return;
                        }
                        inventory.subRuby(5);
                        ChangeMapService.gI().changeMap(pl, ball.zone, ball.x, ball.y);
                        break;
                  }
                  if (pl.isHoldNamecBall) {
                     NamekBallWar.gI().dropBall(pl);
                  }
                  Service.getInstance().sendMoney(pl);
               }
            });
      menu.show(pl);
      InventoryService.gI().sendItemBags(pl);
      InventoryService.gI().subQuantityItemsBag(pl, item, 1);
   }

   private void capsuleThoiTrang(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         Item it = ItemService.gI().createNewItem(
               (short) Util.nextInt(ConstItem.CAI_TRANG_GOKU_THOI_TRANG, ConstItem.CAI_TRANG_CA_DIC_THOI_TRANG));
         it.itemOptions.add(new ItemOption(50, 30));
         it.itemOptions.add(new ItemOption(77, 30));
         it.itemOptions.add(new ItemOption(103, 30));
         it.itemOptions.add(new ItemOption(106, 0));
         InventoryService.gI().addItemBag(pl, it, 0);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
         short icon1 = item.template.iconID;
         short icon2 = it.template.iconID;
         CombineServiceNew.gI().sendEffectOpenItem(pl, icon1, icon2);
      } else {
         Service.getInstance().sendThongBao(pl, "Hãy chừa 1 ô trống để mở.");
      }

   }

   private void openCapsuleTet2022(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
         Service.getInstance().sendThongBao(pl, "Hãy chừa 1 ô trống để mở.");
         return;
      }
      RandomCollection<Integer> rdItemID = new RandomCollection<>();
      rdItemID.add(1, ConstItem.PHAO_HOA);
      rdItemID.add(1, ConstItem.CAY_TRUC);
      rdItemID.add(1, ConstItem.NON_HO_VANG);
      if (pl.gender == 0) {
         rdItemID.add(1, ConstItem.NON_TRAU_MAY_MAN);
         rdItemID.add(1, ConstItem.NON_CHUOT_MAY_MAN);
      } else if (pl.gender == 1) {
         rdItemID.add(1, ConstItem.NON_TRAU_MAY_MAN_847);
         rdItemID.add(1, ConstItem.NON_CHUOT_MAY_MAN_755);
      } else {
         rdItemID.add(1, ConstItem.NON_TRAU_MAY_MAN_848);
         rdItemID.add(1, ConstItem.NON_CHUOT_MAY_MAN_756);
      }
      rdItemID.add(1, ConstItem.CAI_TRANG_HO_VANG);
      rdItemID.add(1, ConstItem.HO_MAP_VANG);
      // rdItemID.add(2, ConstItem.SAO_PHA_LE);
      // rdItemID.add(2, ConstItem.SAO_PHA_LE_442);
      // rdItemID.add(2, ConstItem.SAO_PHA_LE_443);
      // rdItemID.add(2, ConstItem.SAO_PHA_LE_444);
      // rdItemID.add(2, ConstItem.SAO_PHA_LE_445);
      // rdItemID.add(2, ConstItem.SAO_PHA_LE_446);
      // rdItemID.add(2, ConstItem.SAO_PHA_LE_447);
      rdItemID.add(2, ConstItem.DA_LUC_BAO);
      rdItemID.add(2, ConstItem.DA_SAPHIA);
      rdItemID.add(2, ConstItem.DA_TITAN);
      rdItemID.add(2, ConstItem.DA_THACH_ANH_TIM);
      rdItemID.add(2, ConstItem.DA_RUBY);
      rdItemID.add(3, ConstItem.VANG_190);
      int itemID = rdItemID.next();
      Item newItem = ItemService.gI().createNewItem((short) itemID);
      if (newItem.template.type == 9) {
         newItem.quantity = Util.nextInt(10, 50) * 1000000;
      } else if (newItem.template.type == 14 || newItem.template.type == 30) {
         newItem.quantity = 10;
      } else {
         switch (itemID) {
            case ConstItem.CAY_TRUC: {
               RandomCollection<ItemOption> rdOption = new RandomCollection<>();
               rdOption.add(2, new ItemOption(77, 15));// %hp
               rdOption.add(2, new ItemOption(103, 15));// %hp
               rdOption.add(1, new ItemOption(50, 15));// %hp
               newItem.itemOptions.add(rdOption.next());
            }
               break;

            case ConstItem.HO_MAP_VANG: {
               newItem.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
               newItem.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
               newItem.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
            }
               break;

            case ConstItem.NON_HO_VANG:
            case ConstItem.CAI_TRANG_HO_VANG:
            case ConstItem.NON_TRAU_MAY_MAN:
            case ConstItem.NON_TRAU_MAY_MAN_847:
            case ConstItem.NON_TRAU_MAY_MAN_848:
            case ConstItem.NON_CHUOT_MAY_MAN:
            case ConstItem.NON_CHUOT_MAY_MAN_755:
            case ConstItem.NON_CHUOT_MAY_MAN_756:
               newItem.itemOptions.add(new ItemOption(77, 30));
               newItem.itemOptions.add(new ItemOption(103, 30));
               newItem.itemOptions.add(new ItemOption(50, 30));
               break;
         }
         RandomCollection<Integer> rdDay = new RandomCollection<>();
         rdDay.add(6, 3);
         rdDay.add(3, 7);
         rdDay.add(1, 15);
         int day = rdDay.next();
         newItem.itemOptions.add(new ItemOption(93, day));
      }
      short icon1 = item.template.iconID;
      short icon2 = newItem.template.iconID;
      if (newItem.template.type == 9) {
         Service.getInstance().sendThongBao(pl,
               "Bạn nhận được " + Util.numberToMoney(newItem.quantity) + " " + newItem.template.name);
      } else if (newItem.quantity == 1) {
         Service.getInstance().sendThongBao(pl, "Bạn nhận được " + newItem.template.name);
      } else {
         Service.getInstance().sendThongBao(pl, "Bạn nhận được x" + newItem.quantity + " " + newItem.template.name);
      }
      CombineServiceNew.gI().sendEffectOpenItem(pl, icon1, icon2);
      InventoryService.gI().subQuantityItemsBag(pl, item, 1);
      InventoryService.gI().addItemBag(pl, newItem, 99);
      InventoryService.gI().sendItemBags(pl);
   }

   private int randClothes(int level) {
      return ConstItem.LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][level - 1];
   }

   private void openWoodChest(Player pl, Item item) {
      int time = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
      if (time != 0) {
         Item itemReward = null;
         int param = item.itemOptions.get(0).param;
         int gold = 0;
         int[] listItem = { 457, 457, 457, 457, 457, 457, 457, 457, 457, 457, 457, 457, 457 };
         int[] listClothesReward;
         int[] listItemReward;
         String text = "Bạn nhận được\n";
         if (param < 8) {
            gold = 10000000 * param;
            listClothesReward = new int[] { randClothes(param) };
            listItemReward = Util.pickNRandInArr(listItem, 3);
         } else if (param < 10) {
            gold = 25000000 * param;
            listClothesReward = new int[] { randClothes(param), randClothes(param) };
            listItemReward = Util.pickNRandInArr(listItem, 4);
         } else {
            gold = 50000000 * param;
            listClothesReward = new int[] { randClothes(param), randClothes(param), randClothes(param) };
            listItemReward = Util.pickNRandInArr(listItem, 5);
            int ruby = Util.nextInt(10000, 99999);
            pl.inventory.ruby += ruby;
            pl.textRuongGo.add(text + "|1| " + ruby + " Hồng Ngọc");
         }
         for (var i : listClothesReward) {
            itemReward = ItemService.gI().createNewItem((short) i);
            RewardService.gI().initBaseOptionClothes(itemReward.template.id, itemReward.template.type,
                  itemReward.itemOptions);
            RewardService.gI().initStarOption(itemReward,
                  new RewardService.RatioStar[] { new RewardService.RatioStar((byte) 1, 1, 2),
                        new RewardService.RatioStar((byte) 2, 1, 3), new RewardService.RatioStar((byte) 3, 1, 4),
                        new RewardService.RatioStar((byte) 4, 1, 5), });
            InventoryService.gI().addItemBag(pl, itemReward, 0);
            pl.textRuongGo.add(text + itemReward.getInfoItem());
         }
         for (var i : listItemReward) {
            itemReward = ItemService.gI().createNewItem((short) i);
            RewardService.gI().initBaseOptionSaoPhaLe(itemReward);
            itemReward.quantity = Util.nextInt(1, 5);
            InventoryService.gI().addItemBag(pl, itemReward, 0);
            pl.textRuongGo.add(text + itemReward.getInfoItem());
         }
         if (param == 11) {
            itemReward = ItemService.gI().createNewItem((short) ConstItem.MANH_NHAN);
            itemReward.quantity = Util.nextInt(1, 3);
            InventoryService.gI().addItemBag(pl, itemReward, 0);
            pl.textRuongGo.add(text + itemReward.getInfoItem());
         }
         NpcService.gI().createMenuConMeo(pl, ConstNpc.RUONG_GO, -1,
               "Bạn nhận được\n|1|+" + Util.numberToMoney(gold) + " vàng", "OK [" + pl.textRuongGo.size() + "]");
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         pl.inventory.addGold(gold);
         InventoryService.gI().sendItemBags(pl);
         PlayerService.gI().sendInfoHpMpMoney(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Vì bạn quên không lấy chìa nên cần đợi 24h để bẻ khóa");
      }
   }

   private void useItemChangeFlagBag(Player player, Item item) {
      switch (item.template.id) {
         case 865: // kiem Z
            if (!player.effectFlagBag.useKiemz) {
               player.effectFlagBag.reset();
               player.effectFlagBag.useKiemz = !player.effectFlagBag.useKiemz;
            } else {
               player.effectFlagBag.reset();
            }
            break;
         case 994: // vỏ ốc
            break;
         case 995: // cây kem
            break;
         case 996: // cá heo
            break;
         case 997: // con diều
            break;
         case 998: // diều rồng
            if (!player.effectFlagBag.useDieuRong) {
               player.effectFlagBag.reset();
               player.effectFlagBag.useDieuRong = !player.effectFlagBag.useDieuRong;
            } else {
               player.effectFlagBag.reset();
            }
            break;
         case 999: // mèo mun
            if (!player.effectFlagBag.useMeoMun) {
               player.effectFlagBag.reset();
               player.effectFlagBag.useMeoMun = !player.effectFlagBag.useMeoMun;
            } else {
               player.effectFlagBag.reset();
            }
            break;
         case 1000: // xiên cá
            if (!player.effectFlagBag.useXienCa) {
               player.effectFlagBag.reset();
               player.effectFlagBag.useXienCa = !player.effectFlagBag.useXienCa;
            } else {
               player.effectFlagBag.reset();
            }
            break;
         case 1001: // phóng heo
            if (!player.effectFlagBag.usePhongHeo) {
               player.effectFlagBag.reset();
               player.effectFlagBag.usePhongHeo = !player.effectFlagBag.usePhongHeo;
            } else {
               player.effectFlagBag.reset();
            }
            break;
         case 954:
            if (!player.effectFlagBag.useHoaVang) {
               player.effectFlagBag.reset();
               player.effectFlagBag.useHoaVang = !player.effectFlagBag.useHoaVang;
            } else {
               player.effectFlagBag.reset();
            }
            break;
         case 955:
            if (!player.effectFlagBag.useHoaHong) {
               player.effectFlagBag.reset();
               player.effectFlagBag.useHoaHong = !player.effectFlagBag.useHoaHong;
            } else {
               player.effectFlagBag.reset();
            }
            break;
         case 852:
            if (!player.effectFlagBag.useGayTre) {
               player.effectFlagBag.reset();
               player.effectFlagBag.useGayTre = !player.effectFlagBag.useGayTre;
            } else {
               player.effectFlagBag.reset();
            }
            break;
      }
      Service.getInstance().point(player);
      Service.getInstance().sendFlagBag(player);
   }

   public void HopQua10Thang3(Player pl, Item it) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 3) {
         int[] id = { 1794, 1795, 1796, 1563, 1564, 1565, 1559, 1560 };
         int[] idpet = { 778, 779 };
         int[] idvongco = { 1359, 1360 };
         if (Util.isTrue(1, 200)) {
            Item vongco = ItemService.gI().createNewItem((short) idvongco[Util.nextInt(0, idvongco.length - 1)]);
            vongco.itemOptions.add(new ItemOption(50, Util.nextInt(20, 69)));
            if (vongco.template.id == 1359) {
               vongco.itemOptions.add(new ItemOption(77, Util.nextInt(70, 200)));
               vongco.itemOptions.add(new ItemOption(103, Util.nextInt(200, 500)));
            } else {
               vongco.itemOptions.add(new ItemOption(77, Util.nextInt(200, 500)));
               vongco.itemOptions.add(new ItemOption(103, Util.nextInt(70, 200)));
            }
            vongco.itemOptions.add(new ItemOption(30, 1));
            InventoryService.gI().addItemBag(pl, vongco, 0);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được " + vongco.getName());
         }
         if (Util.isTrue(1, 100)) {
            Item item = ItemService.gI().createNewItem((short) idpet[Util.nextInt(0, idpet.length - 1)]);
            item.itemOptions.add(new ItemOption(73, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được trứng đệ sự kiện");
         } else if (Util.isTrue(3, 100)) {
            Item item = ItemService.gI().createNewItem((short) 1562);
            item.itemOptions.add(new ItemOption(73, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được đá cường hoá 13");
         } else if (Util.isTrue(10, 100)) {
            Item item = ItemService.gI().createNewItem((short) 1561);
            item.itemOptions.add(new ItemOption(73, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được đá cường hoá 12");
         } else {
            Item item = ItemService.gI().createNewItem((short) id[Util.nextInt(0, idpet.length - 1)]);
            if (item.template.id == 1794) {
               item.quantity = 49;
            } else if (item.template.id == 1795) {
               item.quantity = 5;
            } else if (item.template.id == 1796) {
               item.quantity = 3;
            } else if (item.template.id == 1563) {
               item.quantity = 2;
            } else {
               item.quantity = 1;
            }
            item.itemOptions.add(new ItemOption(30, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được " + item.getName() + "");
         }
         InventoryService.gI().subQuantityItemsBag(pl, it, 1);
         InventoryService.gI().sendItemBags(pl);
         Service.getInstance().sendThongBao(pl, "Chúc bạn chơi game vui vẻ");
      } else {
         Service.getInstance().sendThongBao(pl, "Cần tối thiểu 3 ô trống để nhận thưởng");
      }
   }

   public void HopQua10Thang3VIP(Player pl, Item it) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 3) {
         int[] id = { 1794, 1795, 1796, 1563, 1564, 1565, 1559, 1560 };
         int[] idpet = { 778, 779 };
         if (Util.isTrue(1, 200)) {
            Item vongco = ItemService.gI().createNewItem((short) 1361);
            vongco.itemOptions.add(new ItemOption(50, Util.nextInt(100, 234)));
            vongco.itemOptions.add(new ItemOption(77, Util.nextInt(234, 456)));
            vongco.itemOptions.add(new ItemOption(103, Util.nextInt(234, 456)));
            vongco.itemOptions.add(new ItemOption(30, 1));
            InventoryService.gI().addItemBag(pl, vongco, 0);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được " + vongco.getName());
         }
         if (Util.isTrue(1, 100)) {
            Item item = ItemService.gI().createNewItem((short) idpet[Util.nextInt(0, idpet.length - 1)]);
            item.itemOptions.add(new ItemOption(73, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được trứng đệ sự kiện");
         } else if (Util.isTrue(7, 100)) {
            Item item = ItemService.gI().createNewItem((short) 1562);
            item.itemOptions.add(new ItemOption(73, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được đá cường hoá 13");
         } else if (Util.isTrue(15, 100)) {
            Item item = ItemService.gI().createNewItem((short) 1561);
            item.itemOptions.add(new ItemOption(73, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được đá cường hoá 12");
         } else {
            Item item = ItemService.gI().createNewItem((short) id[Util.nextInt(0, idpet.length - 1)]);
            if (item.template.id == 1794) {
               item.quantity = 69;
            } else if (item.template.id == 1795) {
               item.quantity = 5;
            } else if (item.template.id == 1796) {
               item.quantity = 3;
            } else if (item.template.id == 1563) {
               item.quantity = 2;
            } else {
               item.quantity = 1;
            }
            item.itemOptions.add(new ItemOption(30, 1));
            InventoryService.gI().addItemBag(pl, item, 9);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được " + item.getName() + "");
         }
         InventoryService.gI().subQuantityItemsBag(pl, it, 1);
         InventoryService.gI().sendItemBags(pl);
         Service.getInstance().sendThongBao(pl, "Chúc bạn chơi game vui vẻ");
      } else {
         Service.getInstance().sendThongBao(pl, "Cần tối thiểu 3 ô trống để nhận thưởng");
      }
   }

   public void TrungKhungLong(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] petbeba = { 999, 1000, 1001, 1002, 1003, 1004, 1005, 1006 };
         byte index = (byte) Util.nextInt(0, petbeba.length - 1);
         Item petkhunglong = ItemService.gI().createNewItem(petbeba[index]);
         petkhunglong.itemOptions.add(new ItemOption(50, Util.nextInt(150, 450)));
         petkhunglong.itemOptions.add(new ItemOption(77, Util.nextInt(150, 450)));
         petkhunglong.itemOptions.add(new ItemOption(103, Util.nextInt(150, 450)));
         petkhunglong.itemOptions.add(new ItemOption(14, Util.nextInt(5, 25)));
         petkhunglong.itemOptions.add(new ItemOption(94, Util.nextInt(30, 300)));
         if (Util.isTrue(50, 100)) {
            petkhunglong.itemOptions.add(new ItemOption(5, Util.nextInt(10, 69)));
         }
         if (Util.isTrue(50, 100)) {
            petkhunglong.itemOptions.add(new ItemOption(101, Util.nextInt(100, 1000)));
         }
         if (Util.isTrue(10, 100)) {
            petkhunglong.itemOptions.add(new ItemOption(73, 1));
         } else {
            petkhunglong.itemOptions.add(new ItemOption(93, 3));
            petkhunglong.itemOptions.add(new ItemOption(199, 1));
         }
         petkhunglong.itemOptions.add(new ItemOption(80, Util.nextInt(15, 40)));
         petkhunglong.itemOptions.add(new ItemOption(81, Util.nextInt(15, 40)));
         petkhunglong.itemOptions.add(new ItemOption(76, 1));
         petkhunglong.itemOptions.add(new ItemOption(73, 1));
         InventoryService.gI().addItemBag(pl, petkhunglong, 1);
         Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + petkhunglong.template.name);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   public void hopQuaTanThu(Player pl, Item it) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 14) {
         int gender = pl.gender;
         int[] id = { gender, 6 + gender, 21 + gender, 27 + gender, 12, 194, 441, 442, 443, 444, 445, 446, 447 };
         int[] soluong = { 1, 1, 1, 1, 1, 1, 10, 10, 10, 10, 10, 10, 10 };
         int[] option = { 0, 0, 0, 0, 0, 73, 95, 96, 97, 98, 99, 100, 101 };
         int[] param = { 0, 0, 0, 0, 0, 0, 5, 5, 5, 3, 3, 5, 5 };
         int arrLength = id.length - 1;

         for (int i = 0; i < arrLength; i++) {
            if (i < 5) {
               Item item = ItemService.gI().createNewItem((short) id[i]);
               RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
               item.itemOptions.add(new ItemOption(107, 3));
               InventoryService.gI().addItemBag(pl, item, 0);
            } else {
               Item item = ItemService.gI().createNewItem((short) id[i]);
               item.quantity = soluong[i];
               item.itemOptions.add(new ItemOption(option[i], param[i]));
               InventoryService.gI().addItemBag(pl, item, 0);
            }
         }

         int[] idpet = { 916, 917, 918, 942, 943, 944, 1046, 1039, 1040 };

         Item item = ItemService.gI().createNewItem((short) idpet[Util.nextInt(0, idpet.length - 1)]);
         item.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
         item.itemOptions.add(new ItemOption(93, 3));
         InventoryService.gI().addItemBag(pl, item, 0);

         InventoryService.gI().subQuantityItemsBag(pl, it, 1);
         InventoryService.gI().sendItemBags(pl);
         Service.getInstance().sendThongBao(pl, "Chúc bạn chơi game vui vẻ");
      } else {
         Service.getInstance().sendThongBao(pl, "Cần tối thiểu 14 ô trống để nhận thưởng");
      }
   }

   private void openbox2010(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] temp = { 17, 16, 15, 675, 676, 677, 678, 679, 680, 681, 580, 581, 582 };
         byte index = (byte) Util.nextInt(0, temp.length - 1);
         short[] icon = new short[2];
         icon[0] = item.template.iconID;

         Item it = ItemService.gI().createNewItem(temp[index]);

         if (temp[index] >= 15 && temp[index] <= 17) {
            it.itemOptions.add(new ItemOption(73, 0));

         } else if (temp[index] >= 580 && temp[index] <= 582 || temp[index] >= 675 && temp[index] <= 681) { // cải trang

            it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
            it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
            it.itemOptions.add(new ItemOption(95, Util.nextInt(5, 15)));
            it.itemOptions.add(new ItemOption(96, Util.nextInt(5, 15)));

            if (Util.isTrue(1, 200)) {
               it.itemOptions.add(new ItemOption(74, 0));
            } else {
               it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
            }

         } else {
            it.itemOptions.add(new ItemOption(73, 0));
         }
         InventoryService.gI().addItemBag(pl, it, 0);
         icon[1] = it.template.iconID;

         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);

         CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   public void openboxsukien(Player pl, Item item, int idsukien) {
      try {
         switch (idsukien) {
            case 1:
               if (Manager.EVENT_SEVER == idsukien) {
                  if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                     short[] temp = { 16, 15, 865, 999, 1000, 1001, 739, 742, 743 };
                     byte index = (byte) Util.nextInt(0, temp.length - 1);
                     short[] icon = new short[2];
                     icon[0] = item.template.iconID;

                     Item it = ItemService.gI().createNewItem(temp[index]);

                     if (temp[index] >= 15 && temp[index] <= 16) {
                        it.itemOptions.add(new ItemOption(73, 0));

                     } else if (temp[index] == 865) {

                        it.itemOptions.add(new ItemOption(30, 0));

                        if (Util.isTrue(1, 30)) {
                           it.itemOptions.add(new ItemOption(93, 365));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 999) { // mèo mun
                        it.itemOptions.add(new ItemOption(77, 15));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(1, 50)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 1000) { // xiên cá
                        it.itemOptions.add(new ItemOption(103, 15));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(1, 50)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 1001) { // Phóng heo
                        it.itemOptions.add(new ItemOption(50, 15));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(1, 50)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }

                     } else if (temp[index] == 739) { // cải trang Billes

                        it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                        it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                        it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 45)));

                        if (Util.isTrue(1, 100)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }

                     } else if (temp[index] == 742) { // cải trang Caufila

                        it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                        it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                        it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 45)));

                        if (Util.isTrue(1, 100)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 743) { // chổi bay
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(1, 50)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }

                     } else {
                        it.itemOptions.add(new ItemOption(73, 0));
                     }
                     InventoryService.gI().addItemBag(pl, it, 0);
                     icon[1] = it.template.iconID;

                     InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                     InventoryService.gI().sendItemBags(pl);

                     CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                  } else {
                     Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                  }
                  break;
               } else {
                  Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
               }
            case ConstEvent.SU_KIEN_20_11:
               if (Manager.EVENT_SEVER == idsukien) {
                  if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                     short[] temp = { 16, 15, 1039, 954, 955, 710, 711, 1040, 2023, 999, 1000, 1001 };
                     byte index = (byte) Util.nextInt(0, temp.length - 1);
                     short[] icon = new short[2];
                     icon[0] = item.template.iconID;
                     Item it = ItemService.gI().createNewItem(temp[index]);
                     if (temp[index] >= 15 && temp[index] <= 16) {
                        it.itemOptions.add(new ItemOption(73, 0));
                     } else if (temp[index] == 1039) {
                        it.itemOptions.add(new ItemOption(50, 10));
                        it.itemOptions.add(new ItemOption(77, 10));
                        it.itemOptions.add(new ItemOption(103, 10));
                        it.itemOptions.add(new ItemOption(30, 0));
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                     } else if (temp[index] == 954) {
                        it.itemOptions.add(new ItemOption(50, 15));
                        it.itemOptions.add(new ItemOption(77, 15));
                        it.itemOptions.add(new ItemOption(103, 15));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(79, 80)) {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 955) {
                        it.itemOptions.add(new ItemOption(50, 20));
                        it.itemOptions.add(new ItemOption(77, 20));
                        it.itemOptions.add(new ItemOption(103, 20));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(79, 80)) {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 710) {// cải trang quy lão kame
                        it.itemOptions.add(new ItemOption(50, 22));
                        it.itemOptions.add(new ItemOption(77, 20));
                        it.itemOptions.add(new ItemOption(103, 20));
                        it.itemOptions.add(new ItemOption(194, 0));
                        it.itemOptions.add(new ItemOption(160, 35));
                        if (Util.isTrue(99, 100)) {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 711) { // cải trang jacky chun
                        it.itemOptions.add(new ItemOption(50, 23));
                        it.itemOptions.add(new ItemOption(77, 21));
                        it.itemOptions.add(new ItemOption(103, 21));
                        it.itemOptions.add(new ItemOption(195, 0));
                        it.itemOptions.add(new ItemOption(160, 50));
                        if (Util.isTrue(99, 100)) {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 1040) {
                        it.itemOptions.add(new ItemOption(50, 10));
                        it.itemOptions.add(new ItemOption(77, 10));
                        it.itemOptions.add(new ItemOption(103, 10));
                        it.itemOptions.add(new ItemOption(30, 0));
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                     } else if (temp[index] == 2023) {
                        it.itemOptions.add(new ItemOption(30, 0));
                     } else if (temp[index] == 999) { // mèo mun
                        it.itemOptions.add(new ItemOption(77, 15));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(1, 50)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 1000) { // xiên cá
                        it.itemOptions.add(new ItemOption(103, 15));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(1, 50)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else if (temp[index] == 1001) { // Phóng heo
                        it.itemOptions.add(new ItemOption(50, 15));
                        it.itemOptions.add(new ItemOption(30, 0));
                        if (Util.isTrue(1, 50)) {
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                     } else {
                        it.itemOptions.add(new ItemOption(73, 0));
                     }
                     InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                     icon[1] = it.template.iconID;
                     CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                     InventoryService.gI().addItemBag(pl, it, 0);
                     int ruby = Util.nextInt(1, 5);
                     pl.inventory.ruby += ruby;
                     InventoryService.gI().sendItemBags(pl);
                     PlayerService.gI().sendInfoHpMpMoney(pl);
                     Service.getInstance().sendThongBao(pl, "Bạn được tặng kèm " + ruby + " Hồng Ngọc");
                  } else {
                     Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                  }
               } else {
                  Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
               }
               break;
            case ConstEvent.SU_KIEN_NOEL:
               if (Manager.EVENT_SEVER == idsukien) {
                  if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                     int spl = Util.nextInt(441, 445);
                     int dnc = Util.nextInt(220, 224);
                     int nr = Util.nextInt(16, 18);
                     int nrBang = Util.nextInt(926, 931);

                     if (Util.isTrue(5, 90)) {
                        int ruby = Util.nextInt(1, 3);
                        pl.inventory.ruby += ruby;
                        CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, (short) 7743);
                        PlayerService.gI().sendInfoHpMpMoney(pl);
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        InventoryService.gI().sendItemBags(pl);
                        Service.getInstance().sendThongBao(pl, "Bạn nhận được " + ruby + " Hồng Ngọc");
                     } else {
                        int[] temp = { spl, dnc, nr, nrBang, 387, 390, 393, 821, 822, 746, 380, 999, 1000, 1001, 936,
                              2022 };
                        byte index = (byte) Util.nextInt(0, temp.length - 1);
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        Item it = ItemService.gI().createNewItem((short) temp[index]);

                        if (temp[index] >= 441 && temp[index] <= 443) {// sao pha le
                           it.itemOptions.add(new ItemOption(temp[index] - 346, 5));
                           it.quantity = 10;
                        } else if (temp[index] >= 444 && temp[index] <= 445) {
                           it.itemOptions.add(new ItemOption(temp[index] - 346, 3));
                           it.quantity = 10;
                        } else if (temp[index] >= 220 && temp[index] <= 224) { // da nang cap
                           it.quantity = 10;
                        } else if (temp[index] >= 387 && temp[index] <= 393) { // mu noel do
                           it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
                           it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                           it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                           it.itemOptions.add(new ItemOption(80, Util.nextInt(10, 20)));
                           it.itemOptions.add(new ItemOption(106, 0));
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                           it.itemOptions.add(new ItemOption(199, 0));
                        } else if (temp[index] == 936) { // tuan loc
                           it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
                           it.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
                           it.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
                        } else if (temp[index] == 822) { // cay thong noel
                           it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                           it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                           it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
                           it.itemOptions.add(new ItemOption(30, 0));
                           it.itemOptions.add(new ItemOption(74, 0));
                        } else if (temp[index] == 746) { // xe truot tuyet
                           it.itemOptions.add(new ItemOption(74, 0));
                           it.itemOptions.add(new ItemOption(30, 0));
                           if (Util.isTrue(99, 100)) {
                              it.itemOptions.add(new ItemOption(93, Util.nextInt(30, 360)));
                           }
                        } else if (temp[index] == 999) { // mèo mun
                           it.itemOptions.add(new ItemOption(77, 15));
                           it.itemOptions.add(new ItemOption(74, 0));
                           it.itemOptions.add(new ItemOption(30, 0));
                           if (Util.isTrue(99, 100)) {
                              it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                           }
                        } else if (temp[index] == 1000) { // xiên cá
                           it.itemOptions.add(new ItemOption(103, 15));
                           it.itemOptions.add(new ItemOption(74, 0));
                           it.itemOptions.add(new ItemOption(30, 0));
                           if (Util.isTrue(99, 100)) {
                              it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                           }
                        } else if (temp[index] == 1001) { // Phóng heo
                           it.itemOptions.add(new ItemOption(50, 15));
                           it.itemOptions.add(new ItemOption(74, 0));
                           it.itemOptions.add(new ItemOption(30, 0));
                           if (Util.isTrue(99, 100)) {
                              it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                           }
                        } else if (temp[index] == 2022 || temp[index] == 821) {
                           it.itemOptions.add(new ItemOption(30, 0));
                        } else {
                           it.itemOptions.add(new ItemOption(73, 0));
                        }
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        icon[1] = it.template.iconID;
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                     }
                  } else {
                     Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                  }
               } else {
                  Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
               }
               break;
            case ConstEvent.SU_KIEN_TET:
               if (Manager.EVENT_SEVER == idsukien) {
                  if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                     short[] icon = new short[2];
                     icon[0] = item.template.iconID;
                     RandomCollection<Integer> rd = Manager.HOP_QUA_TET;
                     int tempID = rd.next();
                     Item it = ItemService.gI().createNewItem((short) tempID);
                     if (it.template.type == 11) {// FLAGBAG
                        it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 20)));
                        it.itemOptions.add(new ItemOption(77, Util.nextInt(5, 20)));
                        it.itemOptions.add(new ItemOption(103, Util.nextInt(5, 20)));
                     } else if (tempID >= 1159 && tempID <= 1161) {
                        it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
                        it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
                        it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
                        it.itemOptions.add(new ItemOption(106, 0));
                     } else if (tempID == ConstItem.CAI_TRANG_SSJ_3_WHITE) {
                        it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
                        it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                        it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                        it.itemOptions.add(new ItemOption(5, Util.nextInt(10, 25)));
                        it.itemOptions.add(new ItemOption(104, Util.nextInt(5, 15)));
                     }
                     int type = it.template.type;
                     if (type == 5 || type == 11) {// cải trang & flagbag
                        if (Util.isTrue(199, 200)) {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                        }
                        it.itemOptions.add(new ItemOption(199, 0));// KHÔNG THỂ GIA HẠN
                     } else if (type == 23) {// thú cưỡi
                        if (Util.isTrue(199, 200)) {
                           it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
                        }
                     }
                     if (tempID >= ConstItem.MANH_AO && tempID <= ConstItem.MANH_GANG_TAY) {
                        it.quantity = Util.nextInt(5, 15);
                     } else {
                        it.itemOptions.add(new ItemOption(74, 0));
                     }
                     icon[1] = it.template.iconID;
                     InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                     CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                     InventoryService.gI().addItemBag(pl, it, 0);
                     InventoryService.gI().sendItemBags(pl);
                     break;
                  } else {
                     Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                  }
               } else {
                  Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
               }
               break;
         }
      } catch (Exception e) {
         Log.error("Lỗi mở hộp quà: " + e.getMessage());
      }
   }

   private void openboxkichhoat(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] temp = { 76, 188, 189, 190, 441, 442, 447, 2010, 2009, 865, 938, 939, 940, 16, 17, 18, 19, 20, 946,
               947, 948, 382, 383, 384, 385 };
         int[][] gold = { { 5000, 20000 } };
         byte index = (byte) Util.nextInt(0, temp.length - 1);
         short[] icon = new short[2];
         icon[0] = item.template.iconID;
         if (index <= 3 && index >= 0) {
            pl.inventory.addGold(Util.nextInt(gold[0][0], gold[0][1]));
            PlayerService.gI().sendInfoHpMpMoney(pl);
            icon[1] = 930;
         } else {

            Item it = ItemService.gI().createNewItem(temp[index]);
            if (temp[index] == 441) {
               it.itemOptions.add(new ItemOption(95, 5));
            } else if (temp[index] == 442) {
               it.itemOptions.add(new ItemOption(96, 5));
            } else if (temp[index] == 447) {
               it.itemOptions.add(new ItemOption(101, 5));
            } else if (temp[index] >= 2009 && temp[index] <= 2010) {
               it.itemOptions.add(new ItemOption(30, 0));
            } else if (temp[index] == 865) {
               it.itemOptions.add(new ItemOption(30, 0));
               if (Util.isTrue(1, 20)) {
                  it.itemOptions.add(new ItemOption(93, 365));
               } else {
                  it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
               }
            } else if (temp[index] >= 938 && temp[index] <= 940) {
               it.itemOptions.add(new ItemOption(77, 35));
               it.itemOptions.add(new ItemOption(103, 35));
               it.itemOptions.add(new ItemOption(50, 35));
               if (Util.isTrue(1, 50)) {
                  it.itemOptions.add(new ItemOption(116, 0));
               } else {
                  it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
               }
            } else if (temp[index] >= 946 && temp[index] <= 948) {
               it.itemOptions.add(new ItemOption(77, 35));
               it.itemOptions.add(new ItemOption(103, 35));
               it.itemOptions.add(new ItemOption(50, 35));
               if (Util.isTrue(1, 20)) {
                  it.itemOptions.add(new ItemOption(93, 365));
               } else {
                  it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
               }
            } else {
               it.itemOptions.add(new ItemOption(73, 0));
            }
            InventoryService.gI().addItemBag(pl, it, 0);
            icon[1] = it.template.iconID;

         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);

         CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void openPhieuCaiTrangHaiTac(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         Item ct = ItemService.gI().createNewItem((short) Util.nextInt(618, 626));
         ct.itemOptions.add(new ItemOption(147, 3));
         ct.itemOptions.add(new ItemOption(77, 3));
         ct.itemOptions.add(new ItemOption(103, 3));
         ct.itemOptions.add(new ItemOption(149, 0));
         if (item.template.id == 2006) {
            ct.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
         } else if (item.template.id == 2007) {
            ct.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30)));
         }
         InventoryService.gI().addItemBag(pl, ct, 0);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
         CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, ct.template.iconID);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void eatGrapes(Player pl, Item item) {
      int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
      if (percentCurrentStatima > 50) {
         Service.getInstance().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
         return;
      } else if (item.template.id == 211) {
         pl.nPoint.stamina = pl.nPoint.maxStamina;
         Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
      } else if (item.template.id == 212) {
         pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
         Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
      }
      InventoryService.gI().subQuantityItemsBag(pl, item, 1);
      InventoryService.gI().sendItemBags(pl);
      PlayerService.gI().sendCurrentStamina(pl);
   }

   private void openCapsuleVang(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] temp = { 1066, 1067, 1068, 1069, 1070 };
         byte index = (byte) Util.nextInt(0, temp.length - 1);
         short[] icon = new short[2];
         icon[0] = item.template.iconID;
         if (Util.isTrue(8, 100)) {
            Item it = ItemService.gI().createNewItem((short) 934, 99);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 0);
            PlayerService.gI().sendInfoHpMpMoney(pl);
            icon[1] = it.template.iconID;
         } else {
            Item it = ItemService.gI().createNewItem(temp[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 0);
            icon[1] = it.template.iconID;
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);

         CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void openCapsuleBac(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] temp = { 18, 17, 16, 15, 1346, 1099, 1100, 1101, 1102 };
         int[][] gold = { { 20000000, 400000000 } };
         byte index = (byte) Util.nextInt(0, temp.length - 1);
         short[] icon = new short[2];
         icon[0] = item.template.iconID;
         if (Util.isTrue(20, 100)) {
            pl.inventory.addGold(Util.nextInt(gold[0][0], gold[0][1]));
            PlayerService.gI().sendInfoHpMpMoney(pl);
            icon[1] = 930;
         } else {
            Item it = ItemService.gI().createNewItem(temp[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 0);
            icon[1] = it.template.iconID;
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);

         CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void tuivang(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         int[][] gold = { { 300000000, 500000000 } };
         pl.inventory.addGold(Util.nextInt(gold[0][0], gold[0][1]));
         PlayerService.gI().sendInfoHpMpMoney(pl);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void openCSKB(Player pl, Item item) {
      if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
         short[] temp = { 76, 188, 189, 190, 381, 382, 383, 384, 385 };
         int[][] gold = { { 5000, 20000 } };
         byte index = (byte) Util.nextInt(0, temp.length - 1);
         short[] icon = new short[2];
         icon[0] = item.template.iconID;
         if (index <= 3) {
            pl.inventory.addGold(Util.nextInt(gold[0][0], gold[0][1]));
            PlayerService.gI().sendInfoHpMpMoney(pl);
            icon[1] = 930;
         } else {
            Item it = ItemService.gI().createNewItem(temp[index]);
            it.itemOptions.add(new ItemOption(73, 0));
            InventoryService.gI().addItemBag(pl, it, 0);
            icon[1] = it.template.iconID;
         }
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);

         CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
      } else {
         Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
      }
   }

   private void openMabuEgg(Player pl, Item item) {
      if (pl.mabuEgg == null) {
         MabuEgg.createMabuEgg(pl);
         InventoryService.gI().subQuantityItemsBag(pl, item, 1);
         InventoryService.gI().sendItemBags(pl);
         Service.getInstance().sendThongBao(pl, "Bạn đã bắt đầu ấp Trứng Mabu");
      } else {
         Service.getInstance().sendThongBao(pl, "Vui lòng Hủy hoặc Nở trứng Mabu ở nhà");
      }
   }

   private void useItemTime(Player pl, Item item) {
      boolean updatePoint = false;
      switch (item.template.id) {
         case 382: // bổ huyết
            if (pl.itemTime.isUseBoHuyet2) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
            pl.itemTime.isUseBoHuyet = true;
            updatePoint = true;
            break;
         case 383: // bổ khí
            if (pl.itemTime.isUseBoKhi2) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
            pl.itemTime.isUseBoKhi = true;
            updatePoint = true;
            break;
         case 384: // giáp xên
            if (pl.itemTime.isUseGiapXen2) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
            pl.itemTime.isUseGiapXen = true;
            updatePoint = true;
            break;
         case 381: // cuồng nộ
            if (pl.itemTime.isUseCuongNo2) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
            pl.itemTime.isUseCuongNo = true;
            updatePoint = true;
            break;
         case 385: // ẩn danh
            pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
            pl.itemTime.isUseAnDanh = true;
            break;
         case ConstItem.BO_HUYET_2: // bổ huyết 2
            if (pl.itemTime.isUseBoHuyet) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
            pl.itemTime.isUseBoHuyet2 = true;
            updatePoint = true;
            break;
         case ConstItem.BO_KHI_2: // bổ khí 2
            if (pl.itemTime.isUseBoKhi) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
            pl.itemTime.isUseBoKhi2 = true;
            updatePoint = true;
            break;
         case ConstItem.GIAP_XEN_BO_HUNG_2: // giáp xên 2
            if (pl.itemTime.isUseGiapXen) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
            pl.itemTime.isUseGiapXen2 = true;
            updatePoint = true;
            break;
         case ConstItem.CUONG_NO_2: // cuồng nộ 2
            if (pl.itemTime.isUseCuongNo) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
            pl.itemTime.isUseCuongNo2 = true;
            updatePoint = true;
            break;
         case 379: // máy dò
            pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
            pl.itemTime.isUseMayDo = true;
            break;
         case 663: // bánh pudding
         case 664: // xúc xíc
         case 665: // kem dâu
         case 666: // mì ly
         case 667: // sushi
            pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
            pl.itemTime.isEatMeal = true;
            ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
            pl.itemTime.iconMeal = item.template.iconID;
            updatePoint = true;
            break;
         case ConstItem.BANH_CHUNG_CHIN:
            pl.itemTime.lastTimeBanhChung = System.currentTimeMillis();
            pl.itemTime.isUseBanhChung = true;
            updatePoint = true;
            break;
         case ConstItem.BANH_TET_CHIN:
            pl.itemTime.lastTimeBanhTet = System.currentTimeMillis();
            pl.itemTime.isUseBanhTet = true;
            updatePoint = true;
            break;
         case 1317:// cn
            pl.itemTimesieucap.lastTimeUseXiMuoi = System.currentTimeMillis();
            pl.itemTimesieucap.isUseXiMuoi = true;
            updatePoint = true;
            break;
         case 1385:
            pl.itemTimesieucap.lastTimeCaRot = System.currentTimeMillis();
            pl.itemTimesieucap.isUseCaRot = true;
            updatePoint = true;
            break;
         case 899:
            pl.itemTimesieucap.lastTimeKeo = System.currentTimeMillis();
            pl.itemTimesieucap.isKeo = true;
            updatePoint = true;
            break;
         case 1386: // đồ ngon
            if (pl.itemTimesieucap.isChoido) {
               Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
               return;
            }
            pl.itemTimesieucap.lasttimeChoido = System.currentTimeMillis();
            pl.itemTimesieucap.isChoido = true;
            updatePoint = true;
            // Service.getInstance().player(pl);
            break;
         case 579: // đuôi khỉ
            pl.itemTimesieucap.lastTimeDuoikhi = System.currentTimeMillis();
            pl.itemTimesieucap.isDuoikhi = true;
            updatePoint = true;
            break;
         case 1201: // Đá ngục tù
            pl.itemTimesieucap.lastTimeDaNgucTu = System.currentTimeMillis();
            pl.itemTimesieucap.isDaNgucTu = true;
            updatePoint = true;
            break;
         // bánh trung thu
         case 465:
         case 466:
         case 472:
         case 473:
            pl.itemTimesieucap.lastTimeUseBanh = System.currentTimeMillis();
            pl.itemTimesieucap.isUseTrungThu = true;
            ItemTimeService.gI().removeItemTime(pl, pl.itemTimesieucap.iconBanh);
            pl.itemTimesieucap.iconBanh = item.template.iconID;
            updatePoint = true;
            break;
      }
      if (updatePoint) {
         Service.getInstance().point(pl);
      }
      ItemTimeService.gI().sendAllItemTime(pl);
      InventoryService.gI().subQuantityItemsBag(pl, item, 1);
      InventoryService.gI().sendItemBags(pl);
   }

   private void controllerCallRongThan(Player pl, Item item) {
      int tempId = item.template.id;
      if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
         switch (tempId) {
            case SummonDragon.NGOC_RONG_1_SAO:
            case SummonDragon.NGOC_RONG_2_SAO:
            case SummonDragon.NGOC_RONG_3_SAO:
               SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13), SummonDragon.DRAGON_SHENRON);
               break;
            default:
               NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON, -1,
                     "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
               break;
         }
      } else if (tempId == SummonDragon.NGOC_RONG_SIEU_CAP) {
         SummonDragon.gI().openMenuSummonShenron(pl, (byte) 1015, SummonDragon.DRAGON_BLACK_SHENRON);
      } else if (tempId >= SummonDragon.NGOC_RONG_BANG[0] && tempId <= SummonDragon.NGOC_RONG_BANG[6]) {
         switch (tempId) {
            case 925:
               SummonDragon.gI().openMenuSummonShenron(pl, (byte) 925, SummonDragon.DRAGON_ICE_SHENRON);
               break;
            default:
               Service.getInstance().sendThongBao(pl, "Bạn chỉ có thể gọi rồng băng từ ngọc 1 sao");
               break;
         }
      }
   }

   public void learnSkill(Player pl, Item item) {
      Message msg;
      try {
         if (item.template.gender == pl.gender || item.template.gender == 3) {
            String[] subName = item.template.name.split("");
            byte level = Byte.parseByte(subName[subName.length - 1]);
            Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
            if (curSkill.point == 7) {
               Service.getInstance().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
            } else {
               if (curSkill.point == 0) {
                  if (level == 1) {
                     curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                     SkillUtil.setSkill(pl, curSkill);
                     InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                     msg = Service.getInstance().messageSubCommand((byte) 23);
                     msg.writer().writeShort(curSkill.skillId);
                     pl.sendMessage(msg);
                     msg.cleanup();
                  } else {
                     Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                           level);
                     Service.getInstance().sendThongBao(pl,
                           "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                  }
               } else {
                  if (curSkill.point + 1 == level) {
                     curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                     // System.out.println(curSkill.template.name + " - " + curSkill.point);
                     SkillUtil.setSkill(pl, curSkill);
                     InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                     msg = Service.getInstance().messageSubCommand((byte) 62);
                     msg.writer().writeShort(curSkill.skillId);
                     pl.sendMessage(msg);
                     msg.cleanup();
                  } else {
                     Service.getInstance().sendThongBao(pl,
                           "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                  }
               }
               InventoryService.gI().sendItemBags(pl);
            }
         } else {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");

         }
      } catch (Exception e) {
         Log.error(UseItem.class, e);
      }
   }

   private void useTDLT(Player pl, Item item) {
      if (pl.itemTime.isUseTDLT) {
         ItemTimeService.gI().turnOffTDLT(pl, item);
      } else {
         ItemTimeService.gI().turnOnTDLT(pl, item);
      }
   }

   private void usePorata(Player pl) {
      if (pl.pet == null || pl.fusion.typeFusion == 4) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
      } else {
         if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.fusion(true);
         } else {
            pl.pet.unFusion();
         }
      }
   }

   private void usePorata2(Player pl) {
      if (pl.pet == null || pl.fusion.typeFusion == 4) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
      } else {
         if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.fusion2(true);
         } else {
            pl.pet.unFusion();
         }
      }
   }

   private void usePorata3(Player pl) {
      if (pl.pet == null || pl.fusion.typeFusion == 4) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
      } else {
         if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.fusion3(true);
         } else {
            pl.pet.unFusion();
         }
      }
   }

   private void usePorata4(Player pl) {
      if (pl.pet == null || pl.fusion.typeFusion == 4) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
      } else {
         if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.fusion4(true);
         } else {
            pl.pet.unFusion();
         }
      }
   }

   private void usePorata5(Player pl) {
      if (pl.pet == null || pl.fusion.typeFusion == 5) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
      } else {
         if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.fusion5(true);
         } else {
            pl.pet.unFusion();
         }
      }
   }

   private void usePorata6(Player pl) {
      if (pl.pet == null || pl.fusion.typeFusion == 6) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
      } else {
         if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.fusion6(true);
         } else {
            pl.pet.unFusion();
         }
      }
   }

   private void openCapsuleUI(Player pl) {
      if (pl.isHoldNamecBall) {
         NamekBallWar.gI().dropBall(pl);
         Service.getInstance().sendFlagBag(pl);
      }
      pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
      ChangeMapService.gI().openChangeMapTab(pl);
   }

   public void choseMapCapsule(Player pl, int index) {
      int zoneId = -1;
      if (index < 0 || index >= pl.mapCapsule.size()) {
         return;
      }
      Zone zoneChose = pl.mapCapsule.get(index);
      if (index != 0 || zoneChose.map.mapId == 21 || zoneChose.map.mapId == 22 || zoneChose.map.mapId == 23) {
         if (!(pl.zone != null && pl.zone instanceof ZSnakeRoad)) {
            pl.mapBeforeCapsule = pl.zone;
         } else {
            pl.mapBeforeCapsule = null;
         }
      } else {
         zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
         pl.mapBeforeCapsule = null;
      }
      ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
   }

   private void doiskill4(Player pl, Item item) {
      if (pl.pet.nPoint.power > 20000000000L) {
         if (pl.pet != null) {
            if (pl.pet.playerSkill.skills.get(2).skillId != -1) {
               pl.pet.openSkill4();
               Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
               InventoryService.gI().subQuantityItemsBag(pl, item, 1);
               InventoryService.gI().sendItemBags(pl);
            } else {
               Service.getInstance().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 3 chứ!");
            }
         } else {
            Service.getInstance().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
         }
      } else {
         Service.getInstance().sendThongBao(pl, "Yêu cầu đệ tử có skill 4");
      }
   }

   private void upSkillPet(Player pl, Item item) {
      if (pl.pet == null) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
         return;
      }
      try {
         switch (item.template.id) {
            case 402: // skill 1
               if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                  Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                  InventoryService.gI().subQuantityItemsBag(pl, item, 1);
               } else {
                  Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
               }
               break;
            case 403: // skill 2
               if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                  Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                  InventoryService.gI().subQuantityItemsBag(pl, item, 1);
               } else {
                  Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
               }
               break;
            case 404: // skill 3
               if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                  Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                  InventoryService.gI().subQuantityItemsBag(pl, item, 1);
               } else {
                  Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
               }
               break;
            case 759: // skill 4
               if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                  Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                  InventoryService.gI().subQuantityItemsBag(pl, item, 1);
               } else {
                  Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
               }
               break;
         }
      } catch (Exception e) {
         Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
      }
   }
}
