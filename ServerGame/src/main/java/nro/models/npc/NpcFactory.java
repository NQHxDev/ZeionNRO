package nro.models.npc;

import nro.attr.AttributeManager;
import nro.consts.ConstAttribute;
import nro.consts.ConstEvent;
import nro.consts.ConstItem;
import nro.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.specific.AndroidAoDai;
import nro.models.npc.specific.Appule;
import nro.models.npc.special.BaHatMit;
import nro.models.npc.specific.Babiday;
import nro.models.npc.specific.Bardock;
import nro.models.npc.specific.Berry;
import nro.models.npc.specific.BlackDragon;
import nro.models.npc.special.BoMong;
import nro.models.npc.specific.Bunma;
import nro.models.npc.specific.BunmaTL;
import nro.models.npc.specific.Cadic;
import nro.models.npc.specific.Calick;
import nro.models.npc.specific.Cargo;
import nro.models.npc.special.ConMeo;
import nro.models.npc.specific.Cui;
import nro.models.npc.specific.CuaHangKyGui;
import nro.models.npc.specific.DaiThanh;
import nro.models.npc.specific.DauThan;
import nro.models.npc.specific.Dende;
import nro.models.npc.specific.DrDrief;
import nro.models.npc.specific.DuongTang;
import nro.models.npc.specific.EmiFukada;
import nro.models.npc.specific.Fide;
import nro.models.npc.specific.GhiDanh;
import nro.models.npc.specific.GiumaDauBo;
import nro.models.npc.specific.GohanMooriParagus;
import nro.models.npc.specific.GokuSSJ;
import nro.models.npc.specific.GokuSSJ_;
import nro.models.npc.special.HangNga;
import nro.models.npc.special.HungVuong;
import nro.models.npc.specific.Itachi;
import nro.models.npc.specific.Jaco;
import nro.models.npc.specific.Kaido;
import nro.models.npc.specific.Kibit;
import nro.models.npc.specific.LinhCanh;
import nro.models.npc.specific.LongNu;
import nro.models.npc.special.LyTieuNuong;
import nro.models.npc.specific.MapFam;
import nro.models.npc.specific.MayGapThu;
import nro.models.npc.specific.MiNuong;
import nro.models.npc.specific.MrPopo;
import nro.models.npc.special.NoiBanh;
import nro.models.npc.specific.NPC_64;
import nro.models.npc.specific.NgheCon;
import nro.models.npc.specific.NgoKhong;
import nro.models.npc.specific.Osin;
import nro.models.npc.specific.QuaTrung;
import nro.models.npc.specific.QuocVuong;
import nro.models.npc.specific.QuyLaoKame;
import nro.models.npc.specific.RongOmega;
import nro.models.npc.special.RongThieng;
import nro.models.npc.specific.RuongDo;
import nro.models.npc.specific.Santa;
import nro.models.npc.specific.SoMayManNpc;
import nro.models.npc.specific.Tapion;
import nro.models.npc.specific.ThanMeoKarin;
import nro.models.npc.specific.ThoRen;
import nro.models.npc.specific.TienHacAm;
import nro.models.npc.specific.TopRanking;
import nro.models.npc.specific.ToSuKaio;
import nro.models.npc.specific.Toribot;
import nro.models.npc.specific.TruongLaoGuru;
import nro.models.npc.specific.TruongMyLan;
import nro.models.npc.specific.Uron;
import nro.models.npc.specific.Vados;
import nro.models.npc.specific.VuaVegeta;
import nro.models.npc.specific.Whis;
import nro.models.player.Player;

import nro.server.Manager;
import nro.server.ServerManager;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.func.ShopService;
import nro.utils.Log;
import nro.utils.Util;

import static nro.server.Manager.EVENT_COUNT_QUY_LAO_KAME;
import static nro.server.Manager.EVENT_COUNT_THAN_HUY_DIET;
import static nro.server.Manager.EVENT_COUNT_THAN_MEO;
import static nro.server.Manager.EVENT_COUNT_THAN_VU_TRU;
import static nro.server.Manager.EVENT_COUNT_THUONG_DE;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NpcFactory {

   public static final Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

   private static final Map<Byte, Class<? extends Npc>> NPC_HANDLERS = new HashMap<>();

   static {
      NPC_HANDLERS.put(ConstNpc.FIDE, Fide.class);
      NPC_HANDLERS.put(ConstNpc.CADIC, Cadic.class);
      NPC_HANDLERS.put(ConstNpc.TORIBOT, Toribot.class);
      NPC_HANDLERS.put(ConstNpc.NGO_KHONG, NgoKhong.class);
      NPC_HANDLERS.put(ConstNpc.QUY_LAO_KAME, QuyLaoKame.class);
      NPC_HANDLERS.put(ConstNpc.DUONG_TANG, DuongTang.class);
      NPC_HANDLERS.put(ConstNpc.TAPION, Tapion.class);
      NPC_HANDLERS.put(ConstNpc.BANG_XEP_HANG, TopRanking.class);
      NPC_HANDLERS.put(ConstNpc.MR_POPO, MrPopo.class);
      NPC_HANDLERS.put(ConstNpc.LY_TIEU_NUONG, LyTieuNuong.class);
      NPC_HANDLERS.put(ConstNpc.SO_MAY_MAN, SoMayManNpc.class);
      NPC_HANDLERS.put(ConstNpc.TRUONG_LAO_GURU, TruongLaoGuru.class);
      NPC_HANDLERS.put(ConstNpc.VUA_VEGETA, VuaVegeta.class);
      NPC_HANDLERS.put(ConstNpc.EMI_FUKADA, EmiFukada.class);
      NPC_HANDLERS.put(ConstNpc.ONG_GOHAN, GohanMooriParagus.class);
      NPC_HANDLERS.put(ConstNpc.ONG_MOORI, GohanMooriParagus.class);
      NPC_HANDLERS.put(ConstNpc.ONG_PARAGUS, GohanMooriParagus.class);
      NPC_HANDLERS.put(ConstNpc.BUNMA, Bunma.class);
      NPC_HANDLERS.put(ConstNpc.DENDE, Dende.class);
      NPC_HANDLERS.put(ConstNpc.APPULE, Appule.class);
      NPC_HANDLERS.put(ConstNpc.DR_DRIEF, DrDrief.class);
      NPC_HANDLERS.put(ConstNpc.CARGO, Cargo.class);
      NPC_HANDLERS.put(ConstNpc.CUI, Cui.class);
      NPC_HANDLERS.put(ConstNpc.SANTA, Santa.class);
      NPC_HANDLERS.put(ConstNpc.TRUONG_MY_LAN, TruongMyLan.class);
      NPC_HANDLERS.put(ConstNpc.THAN_MEO_KARIN, ThanMeoKarin.class);
      NPC_HANDLERS.put(ConstNpc.CALICK, Calick.class);
      NPC_HANDLERS.put(ConstNpc.HANG_NGA, HangNga.class);
      NPC_HANDLERS.put(ConstNpc.NPC_64, NPC_64.class);
      NPC_HANDLERS.put(ConstNpc.THO_REN, ThoRen.class);
      NPC_HANDLERS.put(ConstNpc.DAI_THANH, DaiThanh.class);
      NPC_HANDLERS.put(ConstNpc.HUNG_VUONG, HungVuong.class);
      NPC_HANDLERS.put(ConstNpc.TO_SU_KAIO, ToSuKaio.class);
      NPC_HANDLERS.put(ConstNpc.OSIN, Osin.class);
      NPC_HANDLERS.put(ConstNpc.KIBIT, Kibit.class);
      NPC_HANDLERS.put(ConstNpc.JACO, Jaco.class);
      NPC_HANDLERS.put(ConstNpc.BABIDAY, Babiday.class);
      NPC_HANDLERS.put(ConstNpc.BUNMA_TL, BunmaTL.class);
      NPC_HANDLERS.put(ConstNpc.LINH_CANH, LinhCanh.class);
      NPC_HANDLERS.put(ConstNpc.QUA_TRUNG, QuaTrung.class);
      NPC_HANDLERS.put(ConstNpc.QUOC_VUONG, QuocVuong.class);
      NPC_HANDLERS.put(ConstNpc.BARDOCK, Bardock.class);
      NPC_HANDLERS.put(ConstNpc.BERRY, Berry.class);
      NPC_HANDLERS.put(ConstNpc.RONG_OMEGA, RongOmega.class);
      NPC_HANDLERS.put(ConstNpc.RONG_1S, BlackDragon.class);
      NPC_HANDLERS.put(ConstNpc.RONG_2S, BlackDragon.class);
      NPC_HANDLERS.put(ConstNpc.RONG_3S, BlackDragon.class);
      NPC_HANDLERS.put(ConstNpc.RONG_4S, BlackDragon.class);
      NPC_HANDLERS.put(ConstNpc.RONG_5S, BlackDragon.class);
      NPC_HANDLERS.put(ConstNpc.RONG_6S, BlackDragon.class);
      NPC_HANDLERS.put(ConstNpc.RONG_7S, BlackDragon.class);
      NPC_HANDLERS.put(ConstNpc.NGHE_CON, NgheCon.class);
      NPC_HANDLERS.put(ConstNpc.GIUMA_DAU_BO, GiumaDauBo.class);
      NPC_HANDLERS.put(ConstNpc.URON, Uron.class);
      NPC_HANDLERS.put(ConstNpc.BA_HAT_MIT, BaHatMit.class);
      NPC_HANDLERS.put(ConstNpc.DAU_THAN, DauThan.class);
      NPC_HANDLERS.put(ConstNpc.RUONG_DO, RuongDo.class);
      NPC_HANDLERS.put(ConstNpc.LONG_NU, LongNu.class);
      NPC_HANDLERS.put(ConstNpc.ITACHI, Itachi.class);
      NPC_HANDLERS.put(ConstNpc.VADOS, Vados.class);
      NPC_HANDLERS.put(ConstNpc.KAIDO, Kaido.class);
      NPC_HANDLERS.put(ConstNpc.TIEN_HAC_AM, TienHacAm.class);
      NPC_HANDLERS.put(ConstNpc.MI_NUONG, MiNuong.class);
      NPC_HANDLERS.put(ConstNpc.WHIS, Whis.class);
      NPC_HANDLERS.put(ConstNpc.BO_MONG, BoMong.class);
      NPC_HANDLERS.put(ConstNpc.GOKU_SSJ, GokuSSJ.class);
      NPC_HANDLERS.put(ConstNpc.GOKU_SSJ_, GokuSSJ_.class);
      NPC_HANDLERS.put(ConstNpc.GHI_DANH, GhiDanh.class);
      NPC_HANDLERS.put(ConstNpc.MAP_FAM, MapFam.class);
      NPC_HANDLERS.put(ConstNpc.ANDROID_AODAI, AndroidAoDai.class);
      NPC_HANDLERS.put(ConstNpc.NOI_BANH, NoiBanh.class);
      NPC_HANDLERS.put(ConstNpc.CUA_HANG_KY_GUI, CuaHangKyGui.class);
      NPC_HANDLERS.put(ConstNpc.MAY_GAP_THU, MayGapThu.class);
      NPC_HANDLERS.put(ConstNpc.CON_MEO, ConMeo.class);
      NPC_HANDLERS.put(ConstNpc.RONG_THIENG, RongThieng.class);
   }

   public static Npc createNPC(int mapId, int status, int cx, int cy, byte tempId, int avartar) {
      Npc npc = null;
      try {
         // Priority: specific handlers in Registry
         if (NPC_HANDLERS.containsKey(tempId)) {
            Class<? extends Npc> clazz = NPC_HANDLERS.get(tempId);
            return clazz.getConstructor(int.class, int.class, int.class, int.class, int.class, int.class)
                  .newInstance(mapId, status, cx, cy, tempId, avartar);
         }

         switch (tempId) {
            default:
               npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                  @Override
                  public void openBaseMenu(Player player) {
                     if (canOpenNpc(player)) {
                        super.openBaseMenu(player);
                     }
                  }

                  @Override
                  public void confirmMenu(Player player, int select) {
                     if (canOpenNpc(player)) {
                        // ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0,
                        // player.gender);
                     }
                  }
               };
         }
      } catch (Exception e) {
         Log.error(NpcFactory.class, e, "Lỗi load npc");
      }
      return npc;
   }

   public static void openMenuSuKien(Player player, Npc npc, int tempId, int select) {
      switch (Manager.EVENT_SEVER) {
         case 0:
            break;
         case 1:// hlw
            switch (select) {
               case 0:
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     Item keo = InventoryService.gI().finditemnguyenlieuKeo(player);
                     Item banh = InventoryService.gI().finditemnguyenlieuBanh(player);
                     Item bingo = InventoryService.gI().finditemnguyenlieuBingo(player);

                     if (keo != null && banh != null && bingo != null) {
                        Item GioBingo = ItemService.gI().createNewItem((short) 2016, 1);

                        // - Số item sự kiện có trong rương
                        InventoryService.gI().subQuantityItemsBag(player, keo, 10);
                        InventoryService.gI().subQuantityItemsBag(player, banh, 10);
                        InventoryService.gI().subQuantityItemsBag(player, bingo, 10);

                        GioBingo.itemOptions.add(new ItemOption(74, 0));
                        InventoryService.gI().addItemBag(player, GioBingo, 0);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Đổi quà sự kiện thành công");
                     } else {
                        Service.getInstance().sendThongBao(player,
                              "Vui lòng chuẩn bị x10 Nguyên Liệu Kẹo, Bánh Quy, Bí Ngô để đổi vật phẩm sự kiện");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                  }
                  break;
               case 1:
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
                     Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);

                     if (ve != null && giokeo != null) {
                        Item Hopmaquy = ItemService.gI().createNewItem((short) 2017, 1);
                        // - Số item sự kiện có trong rương
                        InventoryService.gI().subQuantityItemsBag(player, ve, 3);
                        InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);

                        Hopmaquy.itemOptions.add(new ItemOption(74, 0));
                        InventoryService.gI().addItemBag(player, Hopmaquy, 0);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Đổi quà sự kiện thành công");
                     } else {
                        Service.getInstance().sendThongBao(player,
                              "Vui lòng chuẩn bị x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                  }
                  break;
               case 2:
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
                     Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);
                     Item hopmaquy = InventoryService.gI().finditemnguyenlieuHopmaquy(player);

                     if (ve != null && giokeo != null && hopmaquy != null) {
                        Item HopQuaHLW = ItemService.gI().createNewItem((short) 2012, 1);
                        // - Số item sự kiện có trong rương
                        InventoryService.gI().subQuantityItemsBag(player, ve, 3);
                        InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);
                        InventoryService.gI().subQuantityItemsBag(player, hopmaquy, 3);

                        HopQuaHLW.itemOptions.add(new ItemOption(74, 0));
                        HopQuaHLW.itemOptions.add(new ItemOption(30, 0));
                        InventoryService.gI().addItemBag(player, HopQuaHLW, 0);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player,
                              "Đổi quà hộp quà sự kiện Halloween thành công");
                     } else {
                        Service.getInstance().sendThongBao(player,
                              "Vui lòng chuẩn bị x3 Hộp Ma Quỷ, x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                  }
                  break;
            }
            break;
         case 2:// 20/11
            switch (select) {
               case 3:
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     int evPoint = player.event.getEventPoint();
                     if (evPoint >= 999) {
                        Item HopQua = ItemService.gI().createNewItem((short) 2021, 1);
                        player.event.setEventPoint(evPoint - 999);

                        HopQua.itemOptions.add(new ItemOption(74, 0));
                        HopQua.itemOptions.add(new ItemOption(30, 0));
                        InventoryService.gI().addItemBag(player, HopQua, 0);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được Hộp Quà Teacher Day");
                     } else {
                        Service.getInstance().sendThongBao(player, "Cần 999 điểm tích lũy để đổi");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                  }
                  break;
               // case 4:
               // ShopService.gI().openShopSpecial(player, npc, ConstNpc.SHOP_HONG_NGOC, 0,
               // -1);
               // break;
               default:
                  int n = 0;
                  switch (select) {
                     case 0:
                        n = 1;
                        break;
                     case 1:
                        n = 10;
                        break;
                     case 2:
                        n = 99;
                        break;
                  }

                  if (n > 0) {
                     Item bonghoa = InventoryService.gI().finditemBongHoa(player, n);
                     if (bonghoa != null) {
                        int evPoint = player.event.getEventPoint();
                        player.event.setEventPoint(evPoint + n);
                        ;
                        InventoryService.gI().subQuantityItemsBag(player, bonghoa, n);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được " + n + " điểm sự kiện");
                        int pre;
                        int next;
                        String text = null;
                        AttributeManager am = ServerManager.gI().getAttributeManager();
                        switch (tempId) {
                           case ConstNpc.THAN_MEO_KARIN:
                              pre = EVENT_COUNT_THAN_MEO / 999;
                              EVENT_COUNT_THAN_MEO += n;
                              next = EVENT_COUNT_THAN_MEO / 999;
                              if (pre != next) {
                                 am.setTime(ConstAttribute.TNSM, 3600);
                                 text = "Toàn bộ máy chủ tăng được 20% TNSM cho đệ tử khi đánh quái trong 60 phút.";
                              }
                              break;

                           case ConstNpc.QUY_LAO_KAME:
                              pre = EVENT_COUNT_QUY_LAO_KAME / 999;
                              EVENT_COUNT_QUY_LAO_KAME += n;
                              next = EVENT_COUNT_QUY_LAO_KAME / 999;
                              if (pre != next) {
                                 am.setTime(ConstAttribute.VANG, 3600);
                                 text = "Toàn bộ máy chủ được tăng 100%  Vàng từ quái trong 60 phút.";
                              }
                              break;

                           case ConstNpc.THUONG_DE:
                              pre = EVENT_COUNT_THUONG_DE / 999;
                              EVENT_COUNT_THUONG_DE += n;
                              next = EVENT_COUNT_THUONG_DE / 999;
                              if (pre != next) {
                                 am.setTime(ConstAttribute.KI, 3600);
                                 text = "Toàn bộ máy chủ được tăng 20% KI trong 60 phút.";
                              }
                              break;

                           case ConstNpc.THAN_VU_TRU:
                              pre = EVENT_COUNT_THAN_VU_TRU / 999;
                              EVENT_COUNT_THAN_VU_TRU += n;
                              next = EVENT_COUNT_THAN_VU_TRU / 999;
                              if (pre != next) {
                                 am.setTime(ConstAttribute.HP, 3600);
                                 text = "Toàn bộ máy chủ được tăng 20% HP trong 60 phút.";
                              }
                              break;

                           case ConstNpc.BILL:
                              pre = EVENT_COUNT_THAN_HUY_DIET / 999;
                              EVENT_COUNT_THAN_HUY_DIET += n;
                              next = EVENT_COUNT_THAN_HUY_DIET / 999;
                              if (pre != next) {
                                 am.setTime(ConstAttribute.SUC_DANH, 3600);
                                 text = "Toàn bộ máy chủ được tăng 20% Sức đánh trong 60 phút.";
                              }
                              break;
                        }
                        if (text != null) {
                           Service.getInstance().sendThongBaoAllPlayer(text);
                        }

                     } else {
                        Service.getInstance().sendThongBao(player,
                              "Cần ít nhất " + n + " bông hoa để có thể tặng");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Cần ít nhất " + n + " bông hoa để có thể tặng");
                  }
            }
            break;
         case 3:
            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
               Item keogiangsinh = InventoryService.gI().finditemKeoGiangSinh(player);

               if (keogiangsinh != null && keogiangsinh.quantity >= 99) {
                  Item tatgiangsinh = ItemService.gI().createNewItem((short) 649, 1);
                  // - Số item sự kiện có trong rương
                  InventoryService.gI().subQuantityItemsBag(player, keogiangsinh, 99);

                  tatgiangsinh.itemOptions.add(new ItemOption(74, 0));
                  tatgiangsinh.itemOptions.add(new ItemOption(30, 0));
                  InventoryService.gI().addItemBag(player, tatgiangsinh, 0);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendThongBao(player, "Bạn nhận được Tất,vớ giáng sinh");
               } else {
                  Service.getInstance().sendThongBao(player,
                        "Vui lòng chuẩn bị x99 kẹo giáng sinh để đổi vớ tất giáng sinh");
               }
            } else {
               Service.getInstance().sendThongBao(player, "Hành trang đầy.");
            }
            break;
         case 4:
            switch (select) {
               case 0:
                  if (!player.event.isReceivedLuckyMoney()) {
                     Calendar cal = Calendar.getInstance();
                     int day = cal.get(Calendar.DAY_OF_MONTH);
                     if (day >= 22 && day <= 24) {
                        Item goldBar = ItemService.gI().createNewItem((short) ConstItem.THOI_VANG,
                              Util.nextInt(1, 3));
                        player.inventory.ruby += Util.nextInt(10, 30);
                        goldBar.quantity = Util.nextInt(1, 3);
                        InventoryService.gI().addItemBag(player, goldBar, 99999);
                        InventoryService.gI().sendItemBags(player);
                        PlayerService.gI().sendInfoHpMpMoney(player);
                        player.event.setReceivedLuckyMoney(true);
                        Service.getInstance().sendThongBao(player,
                              "Nhận lì xì thành công,chúc bạn năm mới dui dẻ");
                     } else if (day > 24) {
                        Service.getInstance().sendThongBao(player, "Hết tết rồi còn đòi lì xì");
                     } else {
                        Service.getInstance().sendThongBao(player, "Đã tết đâu mà đòi lì xì");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Bạn đã nhận lì xì rồi");
                  }
                  break;
               case 1:
                  ShopService.gI().openShopNormal(player, npc, ConstNpc.SHOP_SU_KIEN_TET, 0, -1);
                  break;
            }
            break;
         case ConstEvent.SU_KIEN_8_3:
            switch (select) {
               case 3:
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     int evPoint = player.event.getEventPoint();
                     if (evPoint >= 999) {
                        Item capsule = ItemService.gI().createNewItem((short) 2052, 1);
                        player.event.setEventPoint(evPoint - 999);

                        capsule.itemOptions.add(new ItemOption(74, 0));
                        capsule.itemOptions.add(new ItemOption(30, 0));
                        InventoryService.gI().addItemBag(player, capsule, 0);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được Capsule Hồng");
                     } else {
                        Service.getInstance().sendThongBao(player, "Cần 999 điểm tích lũy để đổi");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                  }
                  break;
               default:
                  int n = 0;
                  switch (select) {
                     case 0:
                        n = 1;
                        break;
                     case 1:
                        n = 10;
                        break;
                     case 2:
                        n = 99;
                        break;
                  }

                  if (n > 0) {
                     Item bonghoa = InventoryService.gI().finditemBongHoa(player, n);
                     if (bonghoa != null) {
                        int evPoint = player.event.getEventPoint();
                        player.event.setEventPoint(evPoint + n);
                        InventoryService.gI().subQuantityItemsBag(player, bonghoa, n);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được " + n + " điểm sự kiện");
                     } else {
                        Service.getInstance().sendThongBao(player,
                              "Cần ít nhất " + n + " bông hoa để có thể tặng");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Cần ít nhất " + n + " bông hoa để có thể tặng");
                  }
            }
            break;
      }
   }

   public static long safeMultiply(long base, long multiplier) {
      BigInteger result = BigInteger.valueOf(base).multiply(BigInteger.valueOf(multiplier));
      return result.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();
   }

   public static long calPercent(long param, long percent) {
      return (param / 100) * percent;
   }

}
