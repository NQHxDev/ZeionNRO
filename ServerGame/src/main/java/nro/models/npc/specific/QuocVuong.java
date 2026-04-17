package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.npc.NpcFactory;
import nro.models.player.NPoint;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.skill.Skill;
import nro.services.InventoryService;
import nro.services.IntrinsicService;
import nro.services.OpenPowerService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.utils.SkillUtil;
import nro.utils.Util;

public class QuocVuong extends Npc {

   public QuocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      this.createOtherMenu(player, ConstNpc.BASE_MENU,
            "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?", "Bản thân", "Đệ tử",
            "Chuyển Sinh", "Danh Hiệu\nCs");
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                     this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                           "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                 + Util.powerToStringnew(player.nPoint.getPowerNextLimit()),
                           "Nâng ngay\n"
                                 + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER)
                                 + "  Vàng",
                           "Đóng");
                  } else {
                     this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                           "Sức mạnh của con đã đạt tới giới hạn",
                           "Đóng");
                  }
                  break;
               case 1:
                  if (player.pet != null) {
                     if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                              "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                    + Util.powerToStringnew(player.pet.nPoint.getPowerNextLimit()),
                              "Nâng ngay\n"
                                    + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER)
                                    + "  Vàng",
                              "Đóng");
                     } else {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                              "Sức mạnh của đệ con đã đạt tới giới hạn",
                              "Đóng");
                     }
                  } else {
                     Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                  }
                  break;
               case 2:
                  this.createOtherMenu(player, ConstNpc.MENU_CHUYENSINH,
                        "|8| -- CHUYỂN SINH --"
                              + "\n|3|Sức Mạnh Hiện Tại: \n"
                              + Util.format(player.nPoint.power)
                              + "\n|5| ----------------"
                              + "\n Bạn sẽ được tái sinh ở một hành tinh khác bất kì"
                              + "\n Các chiêu thức sẽ về cấp 1, Sức mạnh về 1 triệu 5"
                              + "\n|1| Tái sinh càng nhiều SĐ,HP,KI càng cao"
                              + "\n ----------------"
                              + "\n|7| Yêu Cầu:"
                              + "\n|2| Sức mạnh đạt 5555 Tỷ"
                              + "\n Có Skill " + player.tenskill9(player.gender)
                              + "\n ----------------"
                              + "\n|6| Có tỉ lệ thất bại !"
                              + "\n Thất bại sẽ trừ đi Thỏi  Vàng và Giảm 10 Tỷ Sức mạnh",
                        "Chuyển sinh", "Thông tin\nchuyển sinh",
                        "Đóng");
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHUYENSINH) {
            switch (select) {
               case 0:
                  int tvang = getThoiVangChuyenSinh(player.chuyensinh);
                  int percent = getPercentChuyenSinh(player.chuyensinh);
                  this.createOtherMenu(player, ConstNpc.CHUYENSINH,
                        "|7|CHUYỂN SINH"
                              + "\n\n|5|Bạn đang chuyển sinh : " + player.chuyensinh
                              + " \nCấp tiếp theo với tỉ lệ : " + (percent)
                              + "% \n Mức giá chuyển sinh : " + Util.numberToMoney(tvang)
                              + " Thỏi vàng\n\n|7|Bạn có muốn chuyển sinh ?",
                        "Đồng ý", "Từ chối");
                  break;
               case 1:
                  showInfoChuyenSinh(player);
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.CHUYENSINH) {
            handleChuyenSinh(player);
         } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT
               && player.nPoint.limitPower < NPoint.MAX_LIMIT) {
            if (select == 0) {
               if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                  if (OpenPowerService.gI().openPowerSpeed(player)) {
                     player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                     Service.getInstance().sendMoney(player);
                  }
               } else {
                  Service.getInstance().sendThongBao(player,
                        "Đã nghèo còn hút 3 số uống tiger \n thiếu "
                              + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER
                                    - player.inventory.gold))
                              + "  Vàng");
               }
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET
               && player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
            if (select == 0) {
               if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                  if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                     player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                     Service.getInstance().sendMoney(player);
                  }
               } else {
                  Service.getInstance().sendThongBao(player,
                        "Đã nghèo còn hút 3 số uống tiger \n thiếu "
                              + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER
                                    - player.inventory.gold))
                              + "  Vàng");
               }
            }
         }
      }
   }

   private int getThoiVangChuyenSinh(int cs) {
      if (cs > 0 && cs <= 50)
         return 10000;
      if (cs > 50 && cs <= 100)
         return 30000;
      if (cs > 100 && cs <= 150)
         return 50000;
      if (cs > 150 && cs <= 200)
         return 100000;
      if (cs > 200 && cs <= 300)
         return 200000;
      if (cs > 300 && cs <= 400)
         return 300000;
      if (cs > 400 && cs <= 500)
         return 500000;
      if (cs > 500)
         return 1000000;
      return 0;
   }

   private int getPercentChuyenSinh(int cs) {
      if (cs <= 10)
         return 90;
      if (cs <= 20)
         return 85;
      if (cs <= 30)
         return 80;
      if (cs <= 40)
         return 75;
      if (cs <= 50)
         return 70;
      if (cs <= 60)
         return 65;
      if (cs <= 70)
         return 60;
      if (cs <= 80)
         return 55;
      if (cs <= 90)
         return 50;
      if (cs <= 100)
         return 45;
      if (cs <= 110)
         return 40;
      if (cs <= 120)
         return 35;
      if (cs <= 130)
         return 30;
      if (cs <= 140)
         return 25;
      if (cs <= 150)
         return 20;
      if (cs <= 200)
         return 10;
      if (cs <= 300)
         return 5;
      return 1;
   }

   private void showInfoChuyenSinh(Player player) {
      long hp = 0, ki = 0, dame = 0;
      int phantram = 0;
      int cs = player.chuyensinh;
      if (cs > 0) {
         if (cs < 50) {
            dame = NpcFactory.safeMultiply(2_500_000, cs);
            hp = NpcFactory.safeMultiply(5_000_000, cs);
            ki = NpcFactory.safeMultiply(5_000_000, cs);
         } else if (cs < 100) {
            dame = NpcFactory.safeMultiply(5_000_000, cs);
            hp = NpcFactory.safeMultiply(10_000_000, cs);
            ki = NpcFactory.safeMultiply(10_000_000, cs);
            phantram = 10;
         } else if (cs < 150) {
            dame = NpcFactory.safeMultiply(10_000_000, cs);
            hp = NpcFactory.safeMultiply(40_000_000, cs);
            ki = NpcFactory.safeMultiply(40_000_000, cs);
            phantram = 20;
         } else if (cs < 200) {
            dame = NpcFactory.safeMultiply(20_000_000, cs);
            hp = NpcFactory.safeMultiply(80_000_000, cs);
            ki = NpcFactory.safeMultiply(80_000_000, cs);
            phantram = 30;
         } else if (cs < 300) {
            dame = NpcFactory.safeMultiply(30_000_000, cs);
            hp = NpcFactory.safeMultiply(120_000_000, cs);
            ki = NpcFactory.safeMultiply(120_000_000, cs);
            phantram = 40;
         } else if (cs < 400) {
            dame = NpcFactory.safeMultiply(40_000_000, cs);
            hp = NpcFactory.safeMultiply(160_000_000, cs);
            ki = NpcFactory.safeMultiply(160_000_000, cs);
            phantram = 50;
         } else if (cs < 500) {
            dame = NpcFactory.safeMultiply(50_000_000, cs);
            hp = NpcFactory.safeMultiply(200_000_000, cs);
            ki = NpcFactory.safeMultiply(200_000_000, cs);
            phantram = 75;
         } else {
            dame = NpcFactory.safeMultiply(75_000_000, cs);
            hp = NpcFactory.safeMultiply(300_000_000, cs);
            ki = NpcFactory.safeMultiply(300_000_000, cs);
            phantram = 100;
         }
         Service.getInstance().sendThongBaoOK(player,
               "Bạn đang cấp chuyển sinh: " + cs + " Lần\n"
                     + "|2|Sức Đánh Cộng Thêm: " + Util.formatNew(dame) + " Sd chuyển sinh\n"
                     + "HP & KI Cộng Thêm: " + Util.formatNew(hp) + " - " + Util.formatNew(ki)
                     + "\n"
                     + "%Sd-Hp-Ki Tăng Thêm: " + phantram + "%\n");
      }
   }

   private void handleChuyenSinh(Player player) {
      if (player.chuyensinh >= 999) {
         npcChat(player, "|7| Cấp Chuyển sinh đạt MAX là 999 Cấp");
         return;
      }
      if (player.playerSkill.skills.get(7).point == 0) {
         npcChat(player, "|7|Yêu cầu phải học kỹ năng " + player.tenskill9(player.gender));
         return;
      }
      if (player.nPoint.power < 5_555_000_000_000L) {
         npcChat(player, "|7|Bạn chưa đủ sức mạnh yêu cầu để Chuyển sinh");
      } else {
         Item thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
         int tvang = getThoiVangChuyenSinh(player.chuyensinh);
         if (thoivang == null || thoivang.quantity < tvang) {
            npcChat(player, "Bạn chưa đủ Thỏi vàng để chuyển sinh");
            return;
         }
         int percent = getPercentChuyenSinh(player.chuyensinh);
         if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.itemsBody.get(0).quantity < 1
                  && player.inventory.itemsBody.get(1).quantity < 1
                  && player.inventory.itemsBody.get(2).quantity < 1
                  && player.inventory.itemsBody.get(3).quantity < 1
                  && player.inventory.itemsBody.get(4).quantity < 1) {
               if (Util.nextInt(0, 100) < (percent)) {
                  InventoryService.gI().subQuantityItemsBag(player, thoivang, tvang);
                  player.gender += 1;
                  player.nPoint.power = 1_500_000;
                  player.chuyensinh++;
                  if (player.gender > 2) {
                     player.gender = 0;
                  }
                  short[] headtd = { 30, 31, 64 };
                  short[] headnm = { 9, 29, 32 };
                  short[] headxd = { 27, 28, 6 };
                  player.playerSkill.skills.clear();
                  int[] skillsArr = player.gender == 0
                        ? new int[] { 0, 1, 6, 9, 10, 20, 22, 24, 19 }
                        : player.gender == 1 ? new int[] { 2, 3, 7, 11, 12, 17, 18, 26, 19 }
                              : new int[] { 4, 5, 8, 13, 14, 21, 23, 25, 19 };
                  for (int i = 0; i < skillsArr.length; i++) {
                     if (skillsArr[i] == Skill.SUPER_KAME || skillsArr[i] == Skill.LIEN_HOAN_CHUONG
                           || skillsArr[i] == Skill.MA_PHONG_BA) {
                        player.playerSkill.skills.add(SkillUtil.createSkill(skillsArr[i], 9));
                     } else {
                        player.playerSkill.skills.add(SkillUtil.createSkill(skillsArr[i], 7));
                     }
                  }
                  player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(0);
                  player.playerIntrinsic.intrinsic.param1 = 0;
                  player.playerIntrinsic.intrinsic.param2 = 0;
                  player.playerIntrinsic.countOpen = 0;
                  switch (player.gender) {
                     case 0:
                        player.head = headtd[Util.nextInt(headtd.length)];
                        break;
                     case 1:
                        player.head = headnm[Util.nextInt(headnm.length)];
                        break;
                     case 2:
                        player.head = headxd[Util.nextInt(headxd.length)];
                        break;
                  }
                  npcChat(player, "|1|Chuyển sinh thành công \n cấp hiện tại :" + player.chuyensinh);
                  Service.getInstance().player(player);
                  player.zone.loadAnotherToMe(player);
                  player.zone.load_Me_To_Another(player);
                  Service.getInstance().sendFlagBag(player);
                  Service.getInstance().Send_Caitrang(player);
                  PlayerService.gI().sendInfoHpMpMoney(player);
                  Service.getInstance().point(player);
                  Service.getInstance().Send_Info_NV(player);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendMoney(player);
               } else {
                  npcChat(player, "|7|Chuyển sinh thất bại \n cấp hiện tại :" + player.chuyensinh);
                  player.nPoint.power -= 10_000_000_000L; // Legacy code was subtracting 10 billion
                  InventoryService.gI().subQuantityItemsBag(player, thoivang, tvang);
                  Service.getInstance().point(player);
                  Service.getInstance().Send_Info_NV(player);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendMoney(player);
               }
            } else {
               Service.getInstance().sendThongBao(player, "Tháo hết 5 món đầu đang mặc ra nha");
            }
         } else {
            Service.getInstance().sendThongBao(player, "Balo đầy");
         }
      }
   }
}
