package nro.models.npc.special;

import nro.consts.ConstNpc;
import nro.consts.ConstTask;
import nro.dialog.ConfirmDialog;
import nro.dialog.MenuDialog;
import nro.jdbc.DBService;
import nro.jdbc.daos.manager.ServiceDataDAO;
import nro.models.item.Item;
import nro.models.map.Zone;
import nro.models.map.mabu.MabuWar;
import nro.models.npc.Npc;
import nro.models.npc.NpcFactory;
import nro.models.player.Player;
import nro.services.ClanService;
import nro.services.FriendAndEnemyService;
import nro.services.IntrinsicService;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.KhamNgoc;
import nro.services.MapService;
import nro.services.NpcService;
import nro.services.PetService;
import nro.services.PhongThiNghiem;
import nro.services.PlayerService;
import nro.services.RuongSuuTam;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;
import nro.services.func.Input;
import nro.services.func.PVPServcice;
import nro.services.func.SummonDragon;
import nro.services.func.TaiXiu;
import nro.services.func.UseItem;
import nro.utils.Log;
import nro.utils.Util;

public class ConMeo extends Npc {

   public ConMeo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void confirmMenu(Player player, int select) {
      int menuId = player.iDMark.getIndexMenu();
      if (handleEventMenus(player, select, menuId))
         return;
      if (handleItemMenus(player, select, menuId))
         return;
      if (handleSystemMenus(player, select, menuId))
         return;
      if (handleAdminMenus(player, select, menuId))
         return;
   }

   private boolean handleEventMenus(Player player, int select, int menuId) {
      switch (menuId) {
         case ConstNpc.HOP_BANH_NUONG:
            if (select == 0) {
               Item thoivang2 = InventoryService.gI().findItemBagByTemp(player, (short) 457);
               Item botbanh = InventoryService.gI().findItemBagByTemp(player, (short) 2044);
               Item thitheo = InventoryService.gI().findItemBagByTemp(player, (short) 2045);
               Item trungvit = InventoryService.gI().findItemBagByTemp(player, (short) 2046);
               Item ngucoc = InventoryService.gI().findItemBagByTemp(player, (short) 2047);
               Item hopdungbanhnuong = InventoryService.gI().findItemBagByTemp(player, (short) 2042);

               if (thoivang2 == null || thoivang2.quantity < 99
                     || botbanh == null || botbanh.quantity < 99
                     || thitheo == null || thitheo.quantity < 99
                     || trungvit == null || trungvit.quantity < 99
                     || ngucoc == null || ngucoc.quantity < 99
                     || hopdungbanhnuong == null || hopdungbanhnuong.quantity < 1) {
                  Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
                  return true;
               }
               if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                  Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
               } else {
                  Item banhnuong = ItemService.gI().createNewItem((short) 2040);
                  InventoryService.gI().subQuantityItemsBag(player, thoivang2, 99);
                  InventoryService.gI().subQuantityItemsBag(player, botbanh, 99);
                  InventoryService.gI().subQuantityItemsBag(player, thitheo, 99);
                  InventoryService.gI().subQuantityItemsBag(player, trungvit, 99);
                  InventoryService.gI().subQuantityItemsBag(player, ngucoc, 99);
                  InventoryService.gI().subQuantityItemsBag(player, hopdungbanhnuong, 1);
                  InventoryService.gI().addItemBag(player, banhnuong, 1);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendThongBao(player, "Mày đã nhận được " + banhnuong.template.name);
               }
            }
            return true;

         case ConstNpc.HOP_BANH_DEO:
            if (select == 0) {
               Item thoivang3 = InventoryService.gI().findItemBagByTemp(player, (short) 457);
               Item botbanh = InventoryService.gI().findItemBagByTemp(player, (short) 2044);
               Item ngucoc = InventoryService.gI().findItemBagByTemp(player, (short) 2047);
               Item dauxanh = InventoryService.gI().findItemBagByTemp(player, (short) 2048);
               Item duongtrang = InventoryService.gI().findItemBagByTemp(player, (short) 2049);
               Item hopdungbanhdeo = InventoryService.gI().findItemBagByTemp(player, (short) 2043);

               if (thoivang3 == null || thoivang3.quantity < 99
                     || botbanh == null || botbanh.quantity < 99
                     || ngucoc == null || ngucoc.quantity < 99
                     || dauxanh == null || dauxanh.quantity < 99
                     || duongtrang == null || duongtrang.quantity < 99
                     || hopdungbanhdeo == null || hopdungbanhdeo.quantity < 1) {
                  Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
                  return true;
               }
               if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                  Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
               } else {
                  Item banhdeo = ItemService.gI().createNewItem((short) 2041);
                  InventoryService.gI().subQuantityItemsBag(player, thoivang3, 99);
                  InventoryService.gI().subQuantityItemsBag(player, botbanh, 99);
                  InventoryService.gI().subQuantityItemsBag(player, ngucoc, 99);
                  InventoryService.gI().subQuantityItemsBag(player, dauxanh, 99);
                  InventoryService.gI().subQuantityItemsBag(player, duongtrang, 99);
                  InventoryService.gI().subQuantityItemsBag(player, hopdungbanhdeo, 1);
                  InventoryService.gI().addItemBag(player, banhdeo, 1);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendThongBao(player, "Mày đã nhận được " + banhdeo.template.name);
               }
            }
            return true;

         case ConstNpc.MENU_TO_500K:
            if (select == 0) {
               Item thoivang3 = InventoryService.gI().findItemBagByTemp(player, (short) 457);
               Item to500k = InventoryService.gI().findItemBagByTemp(player, (short) 1707);
               if (thoivang3 == null || thoivang3.quantity < 99000
                     || to500k == null || to500k.quantity < 1) {
                  Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
                  return true;
               }
               if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                  Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
               } else {
                  Item to500kkokhoa = ItemService.gI().createNewItem((short) 1707);
                  InventoryService.gI().subQuantityItemsBag(player, thoivang3, 99000);
                  InventoryService.gI().subQuantityItemsBag(player, to500k, 1);
                  InventoryService.gI().addItemBag(player, to500kkokhoa, 1);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendThongBao(player, "Mày đã nhận được " + to500kkokhoa.template.name);
               }
            }
            return true;
         default:
            return false;
      }
   }

   private boolean handleItemMenus(Player player, int select, int menuId) {
      switch (menuId) {
         case ConstNpc.MENU_OPTION_USE_ITEM1105:
            switch (select) {
               case 0:
                  IntrinsicService.gI().sattd(player);
                  break;
               case 1:
                  IntrinsicService.gI().satnm(player);
                  break;
               case 2:
                  IntrinsicService.gI().setxd(player);
                  break;
            }
            return true;
         case ConstNpc.SKH_THANH_TON:
            UseItem.gI().openskhVip(player, select, -1, (short) 1996, 1);
            return true;
         case ConstNpc.SKH_THAN_LINH:
            switch (select) {
               case 0:
                  UseItem.gI().Set_TraiDat_TL(player);
                  break;
               case 1:
                  UseItem.gI().Set_Namec_TL(player);
                  break;
               case 2:
                  UseItem.gI().Set_Xayda_TL(player);
                  break;
            }
            return true;
         case ConstNpc.SKH_HUY_DIET:
            switch (select) {
               case 0:
                  UseItem.gI().Set_TraiDat_HD(player);
                  break;
               case 1:
                  UseItem.gI().Set_Namec_HD(player);
                  break;
               case 2:
                  UseItem.gI().Set_Xayda_HD(player);
                  break;
            }
            return true;
         case ConstNpc.SKH_TD:
            UseItem.gI().openskhNomal(player, 0, select, (short) 2000);
            return true;
         case ConstNpc.SKH_NM:
            UseItem.gI().openskhNomal(player, 1, select, (short) 2001);
            return true;
         case ConstNpc.SKH_XD:
            UseItem.gI().openskhNomal(player, 2, select, (short) 2002);
            return true;
         case ConstNpc.SET_TD_TL:
            UseItem.gI().openskhVip(player, 0, select, (short) 1997, 2);
            return true;
         case ConstNpc.SET_NM_TL:
            UseItem.gI().openskhVip(player, 1, select, (short) 1997, 2);
            return true;
         case ConstNpc.SET_XD_TL:
            UseItem.gI().openskhVip(player, 2, select, (short) 1997, 2);
            return true;
         case ConstNpc.SET_TD_HD:
            UseItem.gI().openskhVip(player, 0, select, (short) 1998, 3);
            return true;
         case ConstNpc.SET_NM_HD:
            UseItem.gI().openskhVip(player, 1, select, (short) 1998, 3);
            return true;
         case ConstNpc.SET_XD_HD:
            UseItem.gI().openskhVip(player, 2, select, (short) 1998, 3);
            return true;
         case ConstNpc.MENU_TD:
            try {
               switch (select) {
                  case 0:
                     ItemService.gI().settaiyoken(player);
                     break;
                  case 1:
                     ItemService.gI().setgenki(player);
                     break;
                  case 2:
                     ItemService.gI().setkamejoko(player);
                     break;
               }
            } catch (Exception e) {
               Log.error(ConMeo.class, e, "Lỗi set đồ TD");
            }
            return true;
         case ConstNpc.MENU_NM:
            try {
               switch (select) {
                  case 0:
                     ItemService.gI().setgodki(player);
                     break;
                  case 1:
                     ItemService.gI().setgoddam(player);
                     break;
                  case 2:
                     ItemService.gI().setsummon(player);
                     break;
               }
            } catch (Exception e) {
               Log.error(ConMeo.class, e, "Lỗi set đồ NM");
            }
            return true;
         case ConstNpc.MENU_XD:
            try {
               switch (select) {
                  case 0:
                     ItemService.gI().setgodgalick(player);
                     break;
                  case 1:
                     ItemService.gI().setmonkey(player);
                     break;
                  case 2:
                     ItemService.gI().setgodhp(player);
                     break;
               }
            } catch (Exception e) {
               Log.error(ConMeo.class, e, "Lỗi set đồ XD");
            }
            return true;
         case ConstNpc.INTRINSIC:
            if (select == 0)
               IntrinsicService.gI().showAllIntrinsic(player);
            else if (select == 1)
               IntrinsicService.gI().showConfirmOpen(player);
            else if (select == 2)
               IntrinsicService.gI().showConfirmOpenVip(player);
            return true;
         case ConstNpc.CONFIRM_OPEN_INTRINSIC:
            if (select == 0)
               IntrinsicService.gI().open(player);
            return true;
         case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
            if (select == 0)
               IntrinsicService.gI().openVip(player);
            return true;
         case ConstNpc.RUONG_GO:
            int size = player.textRuongGo.size();
            if (size > 0) {
               String menuselect = size == 1 ? "OK" : "OK [" + (size - 1) + "]";
               NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, player.textRuongGo.get(size - 1),
                     menuselect);
               player.textRuongGo.remove(size - 1);
            }
            return true;
         default:
            return false;
      }
   }

   private boolean handleSystemMenus(Player player, int select, int menuId) {
      switch (menuId) {
         case ConstNpc.CONFIRM_DIALOG:
            ConfirmDialog confirmDialog = player.getConfirmDialog();
            if (confirmDialog != null) {
               if (confirmDialog instanceof MenuDialog menu) {
                  menu.getRunable().setIndexSelected(select);
                  menu.run();
                  return true;
               }
               if (select == 0)
                  confirmDialog.run();
               else
                  confirmDialog.cancel();
               player.setConfirmDialog(null);
            }
            return true;

         case ConstNpc.TAIXIU:
            handleTaiXiu(player, select);
            return true;

         case ConstNpc.MENU_MABU_WAR:
            if (select == 0) {
               if (MapService.gI().isMapMabuWar(player.zone.map.mapId)) {
                  handleMabuWar(player);
               } else {
                  ChangeMapService.gI().changeMapInYard(player, 114, -1, 300);
               }
            }
            return true;

         case ConstNpc.MAKE_MATCH_PVP:
            PVPServcice.gI().sendInvitePVP(player, (byte) select);
            return true;

         case ConstNpc.MAKE_FRIEND:
            if (select == 0) {
               Object playerId = NpcFactory.PLAYERID_OBJECT.get(player.id);
               if (playerId != null)
                  FriendAndEnemyService.gI().acceptMakeFriend(player, Integer.parseInt(String.valueOf(playerId)));
            }
            return true;

         case ConstNpc.REVENGE:
            if (select == 0)
               PVPServcice.gI().acceptRevenge(player);
            return true;

         case ConstNpc.TUTORIAL_SUMMON_DRAGON:
            if (select == 0)
               NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
            return true;

         case ConstNpc.SUMMON_SHENRON:
            if (select == 0)
               NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
            else if (select == 1)
               SummonDragon.gI().summonShenron(player);
            return true;

         case ConstNpc.SUMMON_BLACK_SHENRON:
            if (select == 0)
               SummonDragon.gI().summonBlackShenron(player);
            return true;

         case ConstNpc.SUMMON_ICE_SHENRON:
            if (select == 0)
               SummonDragon.gI().summonIceShenron(player);
            return true;

         case ConstNpc.CONFIRM_LEAVE_CLAN:
            if (select == 0)
               ClanService.gI().leaveClan(player);
            return true;

         case ConstNpc.CONFIRM_NHUONG_PC:
            if (select == 0) {
               ClanService.gI().phongPc(player, (int) NpcFactory.PLAYERID_OBJECT.get(player.id));
            }
            return true;

         case ConstNpc.RUONG_SUU_TAM:
            if (select == 0) {
               RuongSuuTam.gI().activeRuongSuuTam(player, (byte) 1);
            }
            return true;

         case ConstNpc.KHAM_NGOC:
            if (select == 0) {
               KhamNgoc.gI().activeKhamNgoc(player, (byte) 1);
            }
            return true;

         case ConstNpc.MO_RONG_RUONG_SUU_TAM:
            if (select == 0) {
               RuongSuuTam.gI().upgradeRuong(player);
            }
            return true;

         case ConstNpc.NANG_CAP_KHAM_NGOC:
            if (select == 0) {
               KhamNgoc.gI().upgrade(player);
            }
            return true;

         case ConstNpc.DIEU_CHE:
            if (select == 0) {
               PhongThiNghiem.gI().confirmDieuChe(player);
            }
            return true;

         case ConstNpc.HUY_PTN:
            if (select == 0) {
               PhongThiNghiem.gI().confirmHuy(player);
            }
            return true;

         case ConstNpc.TANG_TOC:
            if (select == 0) {
               PhongThiNghiem.gI().confirmTangToc(player);
            }
            return true;

         case ConstNpc.MO_RONG_PHONG_THI_NGHIEM:
            if (select == 0) {
               PhongThiNghiem.gI().confirmMoRong(player);
            }
            return true;

         default:
            return false;
      }
   }

   private void handleTaiXiu(Player player, int select) {
      String time = ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
      boolean canBet = ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && !TaiXiu.gI().baotri;
      String status = TaiXiu.gI().baotri ? "\n\n|7|Hệ thống sắp bảo trì" : "";

      if (canBet) {
         if (player.goldTai == 0 && player.goldXiu == 0) {
            switch (select) {
               case 0:
                  NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                        "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y
                              + " : " + TaiXiu.gI().z
                              + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                              + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                              + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + status,
                        "Cập nhập", "Theo TÀI", "Theo XỈU", "Đóng");
                  break;
               case 1:
                  if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0)
                     Input.gI().TAI_taixiu(player);
                  else
                     Service.getInstance().sendThongBao(player, "Bạn chưa đủ điều kiện để chơi");
                  break;
               case 2:
                  if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0)
                     Input.gI().XIU_taixiu(player);
                  else
                     Service.getInstance().sendThongBao(player, "Bạn chưa đủ điều kiện để chơi");
                  break;
            }
         } else {
            String betType = player.goldTai > 0 ? "Tài" : "Xỉu";
            long betGold = player.goldTai > 0 ? player.goldTai : player.goldXiu;
            if (select == 0) {
               NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                     "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y
                           + " : " + TaiXiu.gI().z
                           + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                           + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                           + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược " + betType + " : "
                           + Util.format(betGold) + " Hồng ngọc" + status,
                     "Cập nhập", "Đóng");
            }
         }
      } else if (TaiXiu.gI().baotri) {
         if (select == 0) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                  "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : "
                        + TaiXiu.gI().z
                        + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                        + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                        + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + status,
                  "Cập nhập", "Đóng");
         }
      }
   }

   private void handleMabuWar(Player player) {
      if (player.zone.finishMabuWar) {
         ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
      } else if (player.zone.map.mapId == 119) {
         Zone zone = MabuWar.gI().getMapLastFloor(120);
         if (zone != null)
            ChangeMapService.gI().changeMap(player, zone, 354, 240);
         else {
            Service.getInstance().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
         }
      } else {
         int idMapNextFloor = player.zone.map.mapId == 115 ? player.zone.map.mapId + 2 : player.zone.map.mapId + 1;
         ChangeMapService.gI().changeMap(player, idMapNextFloor, -1, 354, 240);
      }
      player.resetPowerPoint();
      player.sendMenuGotoNextFloorMabuWar = false;
      Service.getInstance().sendPowerInfo(player, "TL", player.getPowerPoint());
      if (Util.isTrue(1, 30)) {
         player.inventory.ruby += 1;
         PlayerService.gI().sendInfoHpMpMoney(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được 1 Hồng Ngọc");
      } else {
         Service.getInstance().sendThongBao(player, "Bạn đen vô cùng luôn nên không nhận được gì cả");
      }
   }

   private boolean handleAdminMenus(Player player, int select, int menuId) {
      switch (menuId) {
         case ConstNpc.BUFF_PET:
            if (select == 0) {
               Player pl = (Player) NpcFactory.PLAYERID_OBJECT.get(player.id);
               if (pl.pet == null) {
                  PetService.gI().createNormalPet(pl);
                  Service.getInstance().sendThongBao(player, "Phát đệ tử cho " + pl.name + " thành công");
               }
            }
            return true;

         case ConstNpc.DUNG_NHIEU_TV:
            handleExchangeGold(player, select);
            return true;

         case ConstNpc.MENU_TRIEU_HOI_BOSS:
            this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS_VI_THU, "|7|Muốn triệu hồi con nào!!!",
                  "Nhất Vĩ", "Nhị Vĩ", "Tam Vĩ", "Tứ Vĩ", "Ngũ Vĩ", "Lục Vĩ", "Thất Vĩ", "Bát Vĩ", "Cửu Vĩ", "Thập Vĩ");
            return true;

         case ConstNpc.CHON_HT_DE_MABU, ConstNpc.CHON_HT_DE_BERUS, ConstNpc.CHON_HT_DE_ITACHI,
               ConstNpc.CHON_HT_DE_KAIDO, ConstNpc.CHON_HT_DE_TIEN, ConstNpc.CHON_HT_DE_SOI3DAU,
               ConstNpc.CHON_HT_DE_NGO_KHONG, ConstNpc.CHON_HT_DE_DAITHANH:
            handlePetCreation(player, select, menuId);
            return true;

         case ConstNpc.INFO_ALL:
            handleInfoAll(player, select);
            return true;

         case ConstNpc.CHISODO:
            handleChiSoDo(player, select);
            return true;

         case ConstNpc.ADMIN_DANH_HIEU:
            handleAdminTitle(player, select);
            return true;

         case ConstNpc.MENU_MAY_DO_BOSS:
            if (select == 0) {
               UseItem.gI().maydoboss(player);
            } else if (select == 1) {
               UseItem.gI().maydobossVIP(player);
            }
            return true;

         case ConstNpc.MENU_LOAD_DATA:
            handleLoadData(player, select);
            return true;

         case ConstNpc.MENU_DANHHIEU:
            handleUserTitle(player, select);
            return true;

         case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
            if (select == 0) {
               for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                  player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
               }
               Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
            }
            return true;

         case ConstNpc.MENU_FIND_PLAYER:
            handleFindPlayer(player, select);
            return true;

         default:
            return false;
      }
   }

   private void handleExchangeGold(Player player, int select) {
      Item thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
      long[] goldGains = { 500_000_000L, 2_500_000_000L, 5_000_000_000L, 50_000_000_000L };
      int[] costs = { 1, 5, 10, 100 };

      if (select < costs.length) {
         if (thoivang == null || thoivang.quantity < costs[select]) {
            Service.getInstance().sendThongBao(player, "Cần có đủ " + costs[select] + " Thỏi Vàng để thực hiện");
            return;
         }
         if (player.inventory.gold + goldGains[select] > player.inventory.getGoldLimit()) {
            Service.getInstance().sendThongBao(player, "Vàng sau khi nhận vượt quá giới hạn");
         } else {
            player.inventory.gold += goldGains[select];
            Service.getInstance().sendThongBao(player, "|4|Bạn nhận được " + Util.format(goldGains[select]) + " Vàng");
            InventoryService.gI().subQuantityItemsBag(player, thoivang, costs[select]);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
         }
      }
   }

   private void handlePetCreation(Player player, int select, int menuId) {
      int tempItem = player.iDMark.getIndexMenu();
      Item item = InventoryService.gI().findItemBagByTemp(player, (short) tempItem);
      if (item == null || item.quantity < 1) {
         Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại");
         return;
      }

      switch (menuId) {
         case ConstNpc.CHON_HT_DE_MABU:
            if (player.pet != null) {
               PetService.gI().changePetNew4(player, select);
            } else {
               PetService.gI().createPetNew4(player, select);
            }
            break;
         case ConstNpc.CHON_HT_DE_BERUS:
            if (player.pet != null) {
               PetService.gI().changeBerusPet(player, select);
            } else {
               PetService.gI().createBerusPet(player, select);
            }
            break;
         case ConstNpc.CHON_HT_DE_ITACHI:
            if (player.pet != null) {
               PetService.gI().changeItachiPet(player, select);
            } else {
               PetService.gI().createItachiPet(player, select);
            }
            break;
         case ConstNpc.CHON_HT_DE_KAIDO:
            if (player.pet != null) {
               PetService.gI().changeKaidoPet(player, select);
            } else {
               PetService.gI().createKaidoPet(player, select);
            }
            break;
         case ConstNpc.CHON_HT_DE_TIEN:
            if (player.pet != null) {
               PetService.gI().changeAndroidPet(player, select);
            } else {
               PetService.gI().createAndroidPet(player, select);
            }
            break;
         case ConstNpc.CHON_HT_DE_SOI3DAU:
            if (player.pet != null) {
               PetService.gI().changeSoi3DauPet(player, select);
            } else {
               PetService.gI().createSoi3DauPet(player, select);
            }
            break;
         case ConstNpc.CHON_HT_DE_NGO_KHONG:
            if (player.pet != null) {
               PetService.gI().changeNgoKoPet(player, select);
            } else {
               PetService.gI().createNgoKoPet(player, select);
            }
            break;
         case ConstNpc.CHON_HT_DE_DAITHANH:
            if (player.pet != null) {
               PetService.gI().changePetDaiThanh(player, select);
            } else {
               PetService.gI().createDaiThanhPet(player, select);
            }
            break;
      }
      InventoryService.gI().subQuantityItemsBag(player, item, 1);
      InventoryService.gI().sendItemBags(player);
   }

   private void handleInfoAll(Player player, int select) {
      if (select == 0) {
         String info = "|7|THÔNG TIN NHÂN VẬT"
               + "\b|5|HP bản thân: " + Util.format(player.nPoint.hp) + "/" + Util.powerToString(player.nPoint.hpMax)
               + "\bKI bản thân: " + Util.format(player.nPoint.mp) + "/" + Util.powerToString(player.nPoint.mpMax)
               + "\bSức đánh: " + Util.format(player.nPoint.dame) + "\bGiáp: " + Util.format(player.nPoint.def)
               + "\b|8|-Vàng: " + Util.format(player.inventory.gold) + "   -Ngọc: " + Util.format(player.inventory.gem)
               + "   -H.Ngọc: " + Util.format(player.inventory.ruby);
         NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12713, info, "Thông tin\n nhân vật",
               "Thông tin\nđệ tử", "Thông tin\nđồ mặc");
      } else if (select == 1 && player.pet != null) {
         String info = "|7|THÔNG TIN ĐỆ TỬ"
               + "\b\b|7|Hành tinh: " + Service.getInstance().get_HanhTinh(player.pet.gender)
               + "\b|5|HP ĐỆ TỬ: " + Util.format(player.pet.nPoint.hp) + "/"
               + Util.powerToString(player.pet.nPoint.hpMax)
               + "\bKI ĐỆ TỬ: " + Util.format(player.pet.nPoint.mp) + "/" + Util.powerToString(player.pet.nPoint.mpMax)
               + "\bSức đánh: " + Util.format(player.pet.nPoint.dame) + "\bGiáp: " + Util.format(player.pet.nPoint.def);
         NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12713, info, "Thông tin\n nhân vật",
               "Thông tin\nđệ tử", "Thông tin\nđồ mặc");
      } else if (select == 2) {
         NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
               "|1|Bạn muốn xem chỉ số đồ bị giới hạn hiện thị:", "Chỉ số\nô 1", "Chỉ số\nô 2", "Chỉ số\nô 3",
               "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6", "Chỉ số\nô 7", "Chỉ số\nô 8", "Chỉ số\nô 9", "Chỉ số\nô 10",
               "Chỉ số\nô 11", "Chỉ số\nô 12");
      }
   }

   private void handleChiSoDo(Player player, int select) {
      if (select < player.inventory.itemsBody.size()) {
         Item it = player.inventory.itemsBody.get(select);
         if (it.quantity < 1) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                  "|7|Ô này không có đồ!!!\n|2|Bạn muốn xem chỉ số đồ bị giới hạn hiện thị:", "Chỉ số\nô 1",
                  "Chỉ số\nô 2", "Chỉ số\nô 3", "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6", "Chỉ số\nô 7",
                  "Chỉ số\nô 8", "Chỉ số\nô 9", "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
         } else {
            NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                  "|2|Tên Vật phẩm: " + it.template.name + "\n|7|Chỉ số:\n|6|" + it.getInfo(), "Chỉ số\nô 1",
                  "Chỉ số\nô 2", "Chỉ số\nô 3", "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6", "Chỉ số\nô 7",
                  "Chỉ số\nô 8", "Chỉ số\nô 9", "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
         }
      }
   }

   private void handleAdminTitle(Player player, int select) {
      int[] titles = { 888, 889, 890, 891 };
      String[] names = { "Đại Thần", "Cần Thủ", "Tuổi Thơ", "Thợ ngọc" };
      if (select < titles.length) {
         long duration = 1000L * 60 * 60 * 24 * 7;
         if (select == 0) {
            player.lastTimeTitle1 = (player.lastTimeTitle1 == 0 ? System.currentTimeMillis() : player.lastTimeTitle1)
                  + duration;
            player.isTitleUse1 = true;
         } else if (select == 1) {
            player.lastTimeTitle2 = (player.lastTimeTitle2 == 0 ? System.currentTimeMillis() : player.lastTimeTitle2)
                  + duration;
            player.isTitleUse2 = true;
         } else if (select == 2) {
            player.lastTimeTitle3 = (player.lastTimeTitle3 == 0 ? System.currentTimeMillis() : player.lastTimeTitle3)
                  + duration;
            player.isTitleUse3 = true;
         } else if (select == 3) {
            player.lastTimeTitle4 = (player.lastTimeTitle4 == 0 ? System.currentTimeMillis() : player.lastTimeTitle4)
                  + duration;
            player.isTitleUse4 = true;
         }

         Service.getInstance().point(player);
         Service.getInstance().sendTitle(player, titles[select]);
         Service.getInstance().sendThongBao(player, "Bạn nhận được 7 ngày Danh hiệu " + names[select]);
      }
   }

   private void handleLoadData(Player player, int select) {
      try (java.sql.Connection con = DBService.gI().getConnectionForGame()) {
         switch (select) {
            case 0:
               ServiceDataDAO.loadBangTin(con);
               Service.getInstance().sendThongBao(player, "Reloaded Bulletins successfully");
               break;
            case 1:
               ServiceDataDAO.loadPhucLoi(con);
               ServiceDataDAO.loadPhucLoiTab(con);
               Service.getInstance().sendThongBao(player, "Reloaded Welfare successfully");
               break;
            case 2:
               ServiceDataDAO.loadMocTamBao(con);
               Service.getInstance().sendThongBao(player, "Reloaded Milestones successfully");
               break;
            case 3:
               ServiceDataDAO.loadKhamNgoc(con);
               Service.getInstance().sendThongBao(player, "Reloaded Gem Sockets successfully");
               break;
            case 4:
               ServiceDataDAO.loadRuongSuuTam(con);
               Service.getInstance().sendThongBao(player, "Reloaded Collection Book successfully");
               break;
         }
      } catch (Exception e) {
         Log.error(ConMeo.class, e, "Error reloading data");
         Service.getInstance().sendThongBao(player, "Error reloading data. Check logs.");
      }
   }

   private void handleUserTitle(Player player, int select) {
      boolean[] uses = { player.isTitleUse1, player.isTitleUse2, player.isTitleUse3, player.isTitleUse4,
            player.isTitleUse5 };
      long[] times = { player.lastTimeTitle1, player.lastTimeTitle2, player.lastTimeTitle3, player.lastTimeTitle4,
            player.lastTimeTitle5 };

      if (select < times.length && times[select] > 0) {
         Service.getInstance().removeTitle(player);
         if (select == 0)
            player.isTitleUse1 = !player.isTitleUse1;
         else if (select == 1)
            player.isTitleUse2 = !player.isTitleUse2;
         else if (select == 2)
            player.isTitleUse3 = !player.isTitleUse3;
         else if (select == 3)
            player.isTitleUse4 = !player.isTitleUse4;
         else if (select == 4)
            player.isTitleUse5 = !player.isTitleUse5;

         Service.getInstance().point(player);
         Service.getInstance().sendThongBao(player, "Đã " + (uses[select] ? "Tắt" : "Bật") + " Danh Hiệu!");
         for (int i = 888; i <= 892; i++)
            Service.getInstance().sendTitle(player, i);
         Service.getInstance().removeTitle(player);
      }
   }

   private void handleFindPlayer(Player player, int select) {
      Player p = (Player) NpcFactory.PLAYERID_OBJECT.get(player.id);
      if (p != null) {
         switch (select) {
            case 0:
               if (p.zone != null) {
                  ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
               }
               break;
            case 1:
               if (p.zone != null) {
                  ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
               }
               break;
            case 2:
               Input.gI().createFormChangeName(player, p);
               break;
         }
      }
   }
}
