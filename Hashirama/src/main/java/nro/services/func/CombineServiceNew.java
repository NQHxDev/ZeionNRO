package nro.services.func;

import nro.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.npc.NpcManager;
import nro.models.player.Player;
import nro.server.ServerNotify;
import nro.server.io.Message;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.RewardService;
import nro.services.Service;
import nro.utils.Util;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import nro.consts.ConstItem;
import nro.data.ItemData;
import nro.jdbc.daos.PlayerDAO;
import nro.server.Manager;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class CombineServiceNew {

    private static final int COST_DOI_VE_DOI_DO_HUY_DIET = 500000000;
    private static final int COST_DAP_DO_KICH_HOAT = 500000000;
    private static final int COST_DOI_MANH_KICH_HOAT = 500000000;

    private static final long COST = 100_000_000L;

    private static final byte MAX_STAR_ITEM = 10;
    private static final byte MAX_LEVEL_ITEM = 8;

    private static final byte OPEN_TAB_COMBINE = 0;
    private static final byte REOPEN_TAB_COMBINE = 1;
    private static final byte COMBINE_SUCCESS = 2;
    private static final byte COMBINE_FAIL = 3;
    private static final byte COMBINE_CHANGE_OPTION = 4;
    private static final byte COMBINE_DRAGON_BALL = 5;
    public static final byte OPEN_ITEM = 6;

    public static final int EP_SAO_TRANG_BI = 500;
    public static final int PHA_LE_HOA_TRANG_BI = 501;
    public static final int CHUYEN_HOA_TRANG_BI = 502;
    public static final int PHA_LE_HOA_TRANG_BI_X100 = 503;
//    public static final int DOI_VE_HUY_DIET = 503;
//    public static final int DAP_SET_KICH_HOAT = 504;
//    public static final int DOI_MANH_KICH_HOAT = 505;
//    public static final int DOI_CHUOI_KIEM = 506;
//    public static final int DOI_LUOI_KIEM = 507;
//    public static final int DOI_KIEM_THAN = 508;
//    public static final int OPTION_PORATA = 508;

    public static final int NANG_CAP_VAT_PHAM = 510;
    public static final int NANG_CAP_BONG_TAI = 511;
    public static final int LAM_PHEP_NHAP_DA = 512;
    public static final int NHAP_NGOC_RONG = 513;
    public static final int PHAN_RA_DO_THAN_LINH = 514;
    public static final int NANG_CAP_DO_TS = 515;
    public static final int NANG_CAP_SKH_VAI_THO = 516;
    public static final int AN_TRANG_BI = 517;
    public static final int PHAP_SU_HOA = 518;
    public static final int TAY_PHAP_SU = 519;
    public static final int MO_CHI_SO_BONG_TAI = 520;
    public static final int NANG_CAP_SKH_TS = 521;

    public static final int NANG_CAP_CHAN_MENH = 523;
    public static final int CHUYEN_HOA_DO_HUY_DIET = 524;
    public static final int NANG_CAP_THAN_LINH = 525;
    public static final int NANG_CAP_HUY_DIET = 526;
    public static final int GIA_HAN_VAT_PHAM = 527;
    public static final int PHAN_RA_DO_TS = 528;
    public static final int CHE_TAO_THANH_TON = 536;
    public static final int CHE_TAO_HUY_DIET = 537;
    public static final int CHE_TAO_TRUNG = 538;
    public static final int DOI_TRUNG = 539;
    public static final int THU_HOI_DO_TL = 540;
    public static final int NANG_CAP_TRANG_BI = 541;
    public static final int NANG_SKH_THANH_TON = 542;
    public static final int NANG_CAP_KIM_DAN = 543;
    public static final int TANG_PHAM_CHAT_KIM_DAN = 544;
    public static final int LINH_HOA_TRANG_BI = 545;

    // START _ SÁCH TUYỆT KỸ //
    public static final int GIAM_DINH_SACH = 529;
    public static final int TAY_SACH = 530;
    public static final int NANG_CAP_SACH_TUYET_KY = 531;
    public static final int PHUC_HOI_SACH = 532;
    public static final int PHAN_RA_SACH = 533;
    public static final int LONG_AN_TRANG_BI = 534;
    public static final int TAY_SAO_PHA_LE = 535;
    // END _ SÁCH TUYỆT KỸ //s

    private final Npc baHatMit;
    private final Npc npcwhists;
    private final Npc xetank;
    private final Npc thoren;
    private final Npc longnu;
    private final Npc hangNga;
    private final Npc tosu;

    private static CombineServiceNew i;

    public CombineServiceNew() {
        this.baHatMit = NpcManager.getNpc(ConstNpc.BA_HAT_MIT);
        this.npcwhists = NpcManager.getNpc(ConstNpc.WHIS);
        this.xetank = NpcManager.getNpc(ConstNpc.TRUONG_MY_LAN);
        this.thoren = NpcManager.getNpc(ConstNpc.THO_REN);
        this.longnu = NpcManager.getNpc(ConstNpc.LONG_NU);
        this.hangNga = NpcManager.getNpc(ConstNpc.HANG_NGA);
        this.tosu = NpcManager.getNpc(ConstNpc.TO_SU_KAIO);
    }

    public static CombineServiceNew gI() {
        if (i == null) {
            i = new CombineServiceNew();
        }
        return i;
    }

    /**
     * Mở tab đập đồ
     *
     * @param player
     * @param type kiểu đập đồ
     */
    public void openTabCombine(Player player, int type) {
        player.combineNew.setTypeCombine(type);
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_TAB_COMBINE);
            msg.writer().writeUTF(getTextInfoTabCombine(type));
            msg.writer().writeUTF(getTextTopTabCombine(type));
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiển thị thông tin đập đồ
     *
     * @param player
     * @param index
     */
    public void showInfoCombine(Player player, int[] index) {
        player.combineNew.clearItemCombine();
        if (index.length > 0) {
            for (int j = 0; j < index.length; j++) {
                player.combineNew.itemsCombine.add(player.inventory.itemsBag.get(index[j]));
            }
        }
        switch (player.combineNew.typeCombine) {
            case EP_SAO_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item trangBi = null;
                    Item daPhaLe = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (isTrangBiPhaLeHoa(item)) {
                            trangBi = item;
                        } else if (isDaPhaLe(item)) {
                            daPhaLe = item;
                        }
                    }
                    int star = 0; //sao pha lê đã ép
                    int starEmpty = 0; //lỗ sao pha lê
                    if (trangBi != null && daPhaLe != null) {
                        for (ItemOption io : trangBi.itemOptions) {
                            if (io.optionTemplate.id == 102) {
                                star = io.param;
                            } else if (io.optionTemplate.id == 107) {
                                starEmpty = io.param;
                            }
                        }
                        if (star < starEmpty) {
                            player.combineNew.gemCombine = getGemEpSao(star);
                            String npcSay = trangBi.template.name + "\n|2|";
                            for (ItemOption io : trangBi.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            if (daPhaLe.template.type == 30) {
                                for (ItemOption io : daPhaLe.itemOptions) {
                                    npcSay += "|7|" + io.getOptionString() + "\n";
                                }
                            } else {
                                npcSay += "|7|" + ItemService.gI().getItemOptionTemplate(getOptionDaPhaLe(daPhaLe)).name.replaceAll("#", getParamDaPhaLe(daPhaLe) + "") + "\n";
                            }
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.gemCombine) + " ngọc";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc");

                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Cần 1 trang bị có lỗ sao pha lê và 1 loại đá pha lê để ép vào", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 trang bị có lỗ sao pha lê và 1 loại đá pha lê để ép vào", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 trang bị có lỗ sao pha lê và 1 loại đá pha lê để ép vào", "Đóng");
                }
                break;
            case PHA_LE_HOA_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (isTrangBiPhaLeHoa(item)) {
                        int star = 0;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 107) {
                                star = io.param;
                                break;
                            }
                        }
                        if (star < MAX_STAR_ITEM) {
                            player.combineNew.goldCombine = getGoldPhaLeHoa(star);
                            player.combineNew.gemCombine = getGemPhaLeHoa(star);
                            player.combineNew.ratioCombine = Manager.TILE_NCAP == 0 ? getRatioPhaLeHoa(star) : Manager.TILE_NCAP;
                            String npcSay = item.template.name + "\n|2|";
                            for (ItemOption io : item.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
                            int soLuong = thoiVang.quantity;
                            npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                            if (soLuong >= player.combineNew.goldCombine) {
                                if (star < 11) {
                                    npcSay += "|1|Cần " + Util.powerToString_Long(player.combineNew.goldCombine) + " thỏi vàng";
                                } else {
                                    npcSay += "|1|Cần " + Util.powerToString_Long(player.combineNew.goldCombine) + " xu vàng";
                                }
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc");
                            } else {
                                if (star < 11) {
                                    npcSay += "Còn thiếu "
                                            + Util.powerToString_Long(player.combineNew.goldCombine - soLuong)
                                            + " thỏi vàng";
                                } else {
                                    npcSay += "Còn thiếu "
                                            + Util.powerToString_Long(player.combineNew.goldCombine - soLuong)
                                            + " xu vàng";
                                }
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Vật phẩm đã đạt tối đa sao pha lê", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể đục lỗ",
                                "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy hãy chọn 1 vật phẩm để pha lê hóa",
                            "Đóng");
                }
                break;
            case PHA_LE_HOA_TRANG_BI_X100: {
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (isTrangBiPhaLeHoa(item)) {
                        int star = 0;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 107) { // sao pha lê
                                star = io.param;
                                break;
                            }
                        }
                        if (star < MAX_STAR_ITEM) {
                            // setup chi phí & tỉ lệ như cũ
                            player.combineNew.goldCombine = getGoldPhaLeHoa(star);
                            player.combineNew.gemCombine = getGemPhaLeHoa(star);
                            player.combineNew.ratioCombine = (Manager.TILE_NCAP == 0 ? getRatioPhaLeHoa(star) : Manager.TILE_NCAP);

                            // build mô tả chỉ số item
                            String npcSay = item.template.name + "\n|2|";
                            for (ItemOption io : item.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%\n";

                            // --- Hiển thị chi phí theo mốc sao ---
                            int need = player.combineNew.goldCombine; // tái dùng 'goldCombine' làm "số lượng" thỏi/xu ở mốc đặc biệt
                            if (star <= 10) {
                                // 7 -> 8: dùng thỏi vàng (457)
                                Item tv = InventoryService.gI().findItemBagByTemp(player, 457);
                                int have = (tv == null ? 0 : tv.quantity);
                                if (have >= need) {
                                    npcSay += "|1|Cần " + Util.numberToMoney(need) + " thỏi vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                            "Nâng cấp\n1 lần\n(" + (need) + " thỏi vàng)",
                                            "Nâng cấp\n10 lần\n(" + (need * 10) + " thỏi vàng)",
                                            "Nâng cấp\n100 lần\n(" + (need * 100) + " thỏi vàng)");
                                } else {
                                    npcSay += "Còn thiếu " + Util.numberToMoney(need - have) + " thỏi vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            } else if (star > 10 || star <= 100) {
                                // 8 -> 9 -> 10: dùng xu vàng (1568)
                                Item xv = InventoryService.gI().findItemBagByTemp(player, 1568);
                                int have = (xv == null ? 0 : xv.quantity);
                                if (have >= need) {
                                    npcSay += "|1|Cần " + Util.numberToMoney(need) + " xu vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                            "Nâng cấp\n1 lần\n(" + (need) + " xu vàng)",
                                            "Nâng cấp\n10 lần\n(" + (need * 10) + " xu vàng)",
                                            "Nâng cấp\n100 lần\n(" + (need * 100) + " xu vàng)");
                                } else {
                                    npcSay += "Còn thiếu " + Util.numberToMoney(need - have) + " xu vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            } else {
                                // các mốc còn lại: dùng vàng + ngọc như cũ
                                if (player.combineNew.goldCombine <= player.inventory.gold) {
                                    npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                            "Nâng cấp\n1 lần\n(" + (player.combineNew.gemCombine) + " ngọc)",
                                            "Nâng cấp\n10 lần\n(" + (player.combineNew.gemCombine * 10) + " ngọc)",
                                            "Nâng cấp\n100 lần\n(" + (player.combineNew.gemCombine * 100) + " ngọc)");
                                } else {
                                    npcSay += "Còn thiếu " + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold) + " vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm đã đạt tối đa sao pha lê", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể đục lỗ", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 vật phẩm để pha lê hóa", "Đóng");
                }
                break;
            }
            case NHAP_NGOC_RONG:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 1) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        if (item != null) {
                            int soluong = 7;
                            if (item.isNotNullItem() && (item.template.id > 14 && item.template.id <= 20) && item.quantity >= soluong) {
                                String npcSay = "|2|Con có muốn biến " + soluong + " " + item.template.name + " thành\n"
                                        + "1 viên " + ItemService.gI().getTemplate((short) (item.template.id - 1)).name + "\n"
                                        + "|7|Cần " + soluong + " " + item.template.name;
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép", "Từ chối");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Không có ép lên được nữa !!!", "Đóng");
                            }
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần 7 viên ngọc rồng cùng sao trở lên", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }
                break;
            case AN_TRANG_BI:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 2) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        Item dangusac = player.combineNew.itemsCombine.get(1);
                        if (isTrangBiAn(item)) {
                            if (item != null && item.isNotNullItem() && dangusac != null
                                    && dangusac.isNotNullItem()
                                    && (dangusac.template.id == 1232
                                    || dangusac.template.id == 1233
                                    || dangusac.template.id == 1234) && dangusac.quantity >= 99) {
                                String npcSay = item.template.name + "\n|2|";
                                for (ItemOption io : item.itemOptions) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                                npcSay += "|1|Con có muốn biến trang bị " + item.template.name + " thành\n"
                                        + "trang bị Ấn không?\b|4|Đục là lên\n"
                                        + "|7|Cần 99 " + dangusac.template.name;
                                this.longnu.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép", "Từ chối");
                            } else {
                                this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn chưa bỏ đủ vật phẩm !!!", "Đóng");
                            }
                        } else {
                            this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể hóa ấn", "Đóng");
                        }
                    } else {
                        this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
                    }
                } else {
                    this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }
                break;
            case LONG_AN_TRANG_BI:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 2) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        Item dangusac = player.combineNew.itemsCombine.get(1);
                        if (isTrangBiAn(item)) {
                            if (item != null && item.isNotNullItem() && dangusac != null
                                    && dangusac.isNotNullItem()
                                    && (dangusac.template.id == 1689
                                    || dangusac.template.id == 1690
                                    || dangusac.template.id == 1691) && dangusac.quantity >= 555) {
                                String npcSay = item.template.name + "\n|2|";
                                for (ItemOption io : item.itemOptions) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                                npcSay += "|1|Con có muốn biến trang bị " + item.template.name + " thành\n"
                                        + "trang bị Ấn không?\b|4|Đục là lên\n"
                                        + "|7|Cần 555 " + dangusac.template.name;
                                this.longnu.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép", "Từ chối");
                            } else {
                                this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn chưa bỏ đủ vật phẩm !!!", "Đóng");
                            }
                        } else {
                            this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể hóa ấn", "Đóng");
                        }
                    } else {
                        this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
                    }
                } else {
                    this.longnu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }
                break;
            case TAY_SAO_PHA_LE:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 2) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        Item thoivang = player.combineNew.itemsCombine.get(1);
                        if (isTrangBiTaySPL(item)) {
                            if (item != null && thoivang != null && thoivang.template.id == 457 && thoivang.quantity >= 99) {
                                String npcSay = item.template.name + "\n|2|";
                                for (ItemOption io : item.itemOptions) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                                npcSay += "|1|Con có muốn tẩy trang bị " + item.template.name + " về\n"
                                        + "lúc chưa ép Sao pha lê không?\n"
                                        + "|3|Lưu ý: Không được hoàn trả lại Sao pha lê đã ép\n"
                                        + "|7|Cần 99 " + thoivang.template.name;
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép", "Từ chối");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Không đủ nguyên liệu !!!", "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể Tẩy\nVui lòng chỉ chọn 1 món (Áo, quần, giày, găng hoặc rada)", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }
                break;
            case NANG_CAP_VAT_PHAM:
                if (player.combineNew.itemsCombine.size() >= 2 && player.combineNew.itemsCombine.size() < 4) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type < 5).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ nâng cấp", "Đóng");
                        break;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đá nâng cấp", "Đóng");
                        break;
                    }
                    if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 987).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ nâng cấp", "Đóng");
                        break;
                    }
                    Item itemDo = null;
                    Item itemDNC = null;
                    Item itemDBV = null;
                    for (int j = 0; j < player.combineNew.itemsCombine.size(); j++) {
                        if (player.combineNew.itemsCombine.get(j).isNotNullItem()) {
                            if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.get(j).template.id == 987) {
                                itemDBV = player.combineNew.itemsCombine.get(j);
                                continue;
                            }
                            if (player.combineNew.itemsCombine.get(j).template.type < 5) {
                                itemDo = player.combineNew.itemsCombine.get(j);
                            } else {
                                itemDNC = player.combineNew.itemsCombine.get(j);
                            }
                        }
                    }
                    if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {
                        int level = 0;
                        for (ItemOption io : itemDo.itemOptions) {
                            if (io.optionTemplate.id == 72) {
                                level = io.param;
                                break;
                            }
                        }
                        if (level < MAX_LEVEL_ITEM) {
                            player.combineNew.goldCombine = getGoldNangCapDo(level);
                            player.combineNew.ratioCombine = Manager.TILE_NCAP == 0 ? (float) getTileNangCapDo(level) : Manager.TILE_NCAP;
                            player.combineNew.countDaNangCap = getCountDaNangCapDo(level);
                            player.combineNew.countDaBaoVe = (short) getCountDaBaoVe(level);
                            String npcSay = "|2|Hiện tại " + itemDo.template.name + " (+" + level + ")\n|0|";
                            for (ItemOption io : itemDo.itemOptions) {
                                if (io.optionTemplate.id != 72) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            String option = null;
                            int param = 0;
                            for (ItemOption io : itemDo.itemOptions) {
                                if (io.optionTemplate.id == 47
                                        || io.optionTemplate.id == 6
                                        || io.optionTemplate.id == 0
                                        || io.optionTemplate.id == 7
                                        || io.optionTemplate.id == 14
                                        || io.optionTemplate.id == 22
                                        || io.optionTemplate.id == 23) {
                                    option = io.optionTemplate.name;
                                    param = io.param + (io.param * 10 / 100);
                                    break;
                                }
                            }
                            npcSay += "|2|Sau khi nâng cấp (+" + (level + 1) + ")\n|7|"
                                    + option.replaceAll("#", String.valueOf(param))
                                    + "\n|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%\n"
                                    + (player.combineNew.countDaNangCap > itemDNC.quantity ? "|7|" : "|1|")
                                    + "Cần " + player.combineNew.countDaNangCap + " " + itemDNC.template.name
                                    + "\n" + (player.combineNew.goldCombine > player.inventory.gold ? "|7|" : "|1|")
                                    + "Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";

                            String daNPC = player.combineNew.itemsCombine.size() == 3 && itemDBV != null ? String.format("\nCần tốn %s đá bảo vệ", player.combineNew.countDaBaoVe) : "";
                            if ((level == 2 || level == 4 || level == 6) && !(player.combineNew.itemsCombine.size() == 3 && itemDBV != null)) {
                                npcSay += "\nNếu thất bại sẽ rớt xuống (+" + (level - 1) + ")";
                                npcSay += "\nVà giảm 5% chỉ số gốc";
                            }
                            if (player.combineNew.countDaNangCap > itemDNC.quantity) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn thiếu\n" + (player.combineNew.countDaNangCap - itemDNC.quantity) + " " + itemDNC.template.name);
                            } else if (player.combineNew.goldCombine > player.inventory.gold) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn thiếu\n" + Util.numberToMoney((player.combineNew.goldCombine - player.inventory.gold)) + " vàng");
                            } else if (player.combineNew.itemsCombine.size() == 3 && Objects.nonNull(itemDBV) && itemDBV.quantity < player.combineNew.countDaBaoVe) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn thiếu\n" + (player.combineNew.countDaBaoVe - itemDBV.quantity) + " đá bảo vệ");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                        npcSay, "Nâng cấp\n" + Util.numberToMoney(player.combineNew.goldCombine) + " vàng" + daNPC, "Từ chối");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Trang bị của ngươi đã đạt cấp tối đa", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị và 1 loại đá nâng cấp", "Đóng");
                    }
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cất đi con ta không thèm", "Đóng");
                        break;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị và 1 loại đá nâng cấp", "Đóng");
                }
                break;
            case NANG_CAP_CHAN_MENH:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item bongTai = null;
                    Item manhVo = null;
                    int star = 0;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id == 1318) {
                            manhVo = item;
                        } else if (item.template.id >= 1300 && item.template.id <= 1308) {
                            bongTai = item;
                            star = item.template.id - 1300;
                        }
                    }
                    if (bongTai != null && bongTai.template.id == 1308) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chân Mệnh đã đạt cấp tối đa", "Đóng");
                        return;
                    }
                    player.combineNew.DiemNangcap = getDiemNangcapChanmenh(star);
                    player.combineNew.DaNangcap = getDaNangcapChanmenh(star);
                    player.combineNew.TileNangcap = Manager.TILE_NCAP == 0 ? getTiLeNangcapChanmenh(star) : Manager.TILE_NCAP;
                    if (bongTai != null && manhVo != null && (bongTai.template.id >= 1300 && bongTai.template.id < 1308)) {
                        String npcSay = bongTai.template.name + "\n|2|";
                        for (ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.TileNangcap + "%" + "\n";
                        if (player.combineNew.DiemNangcap <= player.inventory.ruby) {
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.DiemNangcap) + " Hồng ngọc";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.DaNangcap + " Mảnh Chân Mệnh");
                        } else {
                            npcSay += "Còn thiếu " + Util.numberToMoney(player.combineNew.DiemNangcap - player.inventory.ruby) + " Hồng ngọc";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Chân Mệnh và Mảnh Chân Mệnh", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Chân Mệnh và Mảnh Chân Mệnh", "Đóng");
                }
                break;
            case NANG_CAP_BONG_TAI: {
                if (player.combineNew.itemsCombine.size() != 2) {
                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần đặt đúng 2 vật phẩm:\n- Bông tai Porata\n- Mảnh vỡ bông tai cấp tiếp theo",
                            "Đóng");
                    break;
                }

                Item bongTai = null;
                for (Item item : player.combineNew.itemsCombine) {
                    if (item == null || !item.isNotNullItem()) {
                        continue;
                    }
                    int lv = getPorataLevelByTemplateId(item.template.id);
                    if (lv > 0) {
                        bongTai = item;
                        break;
                    }
                }

                if (bongTai == null) {
                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Không tìm thấy bông tai Porata hợp lệ",
                            "Đóng");
                    break;
                }

                int currentLevel = getPorataLevelByTemplateId(bongTai.template.id);
                if (currentLevel <= 0 || currentLevel >= MAX_PORATA_LEVEL) {
                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Không thể nâng cấp bông tai cấp này",
                            "Đóng");
                    break;
                }

                int nextLevel = currentLevel + 1;

                // mảnh vỡ cấp tiếp theo
                short fragId = getFragmentTemplateIdForNextLevel(nextLevel);
                long requireFrag = getRequiredFragmentForNextLevel(nextLevel);

                if (fragId == -1 || requireFrag <= 0) {
                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Chưa cấu hình mảnh vỡ cho cấp " + nextLevel,
                            "Đóng");
                    break;
                }

                Item fragItem = null;
                for (Item item : player.combineNew.itemsCombine) {
                    if (item == null || !item.isNotNullItem()) {
                        continue;
                    }
                    if (item.template.id == fragId) {
                        fragItem = item;
                        break;
                    }
                }

                String needName = "Mảnh vỡ bông tai cấp " + nextLevel;

                if (fragItem == null) {
                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần " + needName + " x" + requireFrag,
                            "Đóng");
                    break;
                }

                if (fragItem.quantity < requireFrag) {
                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần " + needName + " x" + requireFrag
                            + "\nBạn đang có: " + fragItem.quantity,
                            "Đóng");
                    break;
                }

                // Check đã có bông tai cấp tiếp theo trong hành trang chưa (để đồng bộ với hàm nâng)
                short nextTemplateId = getPorataTemplateIdByLevel(nextLevel);
                if (nextTemplateId != -1) {
                    Item findItemBag = InventoryService.gI().findItemBagByTemp(player, nextTemplateId);
                    if (findItemBag != null) {
                        baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi đã có " + getPorataNameByLevel(nextLevel) + " trong hành trang rồi\nKhông thể nâng cấp nữa.",
                                "Đóng");
                        break;
                    }
                }

                // set cost + ratio theo config helper
                player.combineNew.goldCombine = (int)getPorataGoldCost(currentLevel);
                player.combineNew.gemCombine = getPorataGemCost(currentLevel);     // thực chất là ruby
                player.combineNew.ratioCombine = getPorataRatio(currentLevel);

                int loseOnFail = (int) Math.max(1, requireFrag / 10); // đồng bộ với nangCapBongTai()

                String npcSay = getPorataNameByLevel(nextLevel) + "\n|2|";
                for (ItemOption io : bongTai.itemOptions) {
                    npcSay += io.getOptionString() + "\n";
                }
                npcSay += "|7|Nguyên liệu: " + needName + " x" + requireFrag + "\n";
                npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%\n";
                npcSay += "|7|Thất bại mất " + loseOnFail + " mảnh\n";

                boolean enoughGold = player.inventory.gold >= player.combineNew.goldCombine;
                boolean enoughRuby = player.inventory.ruby >= player.combineNew.gemCombine;

                if (enoughGold && enoughRuby) {
                    npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng\n";
                    npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.gemCombine) + " Hồng ngọc\n";
                    baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                            "Nâng cấp");
                } else {
                    if (!enoughGold) {
                        npcSay += "|7|Thiếu " + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold) + " vàng\n";
                    }
                    if (!enoughRuby) {
                        npcSay += "|7|Thiếu " + Util.numberToMoney(player.combineNew.gemCombine - player.inventory.ruby) + " Hồng ngọc\n";
                    }
                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                }

                break;
            }
            case MO_CHI_SO_BONG_TAI:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item bongTai = null;
                    Item manhHon = null;
                    Item daXanhLam = null;

                    for (Item item : player.combineNew.itemsCombine) {
                        switch (item.template.id) {
                            // Porata các cấp
                            case 1129: // Porata 4
                            case 1165: // Porata 3
                            case 921:  // Porata 2
                            case 1976: // Porata 5
                            case 1975: // Porata 6
                                bongTai = item;
                                break;

                            // Các item đặc biệt khác nếu bạn vẫn dùng (giữ nguyên)
                            case 2098: // Mũ cối siêu cấp VIP
                            case 2099: // Hợp thể siêu nhân
                                bongTai = item;
                                break;

                            case 934: // Mảnh Hồn
                                manhHon = item;
                                break;
                            case 935: // Đá Xanh Lam
                                daXanhLam = item;
                                break;
                            default:
                                break;
                        }
                    }

                    if (bongTai != null && manhHon != null && daXanhLam != null
                            && manhHon.quantity >= 99 && daXanhLam.quantity >= 1) {

                        // Chi phí cố định như cũ
                        player.combineNew.goldCombine = 2_000_000_000;
                        player.combineNew.gemCombine = 10_000;

                        String npcSay;
                        int porataLevel = getPorataLevelByTemplateId(bongTai.template.id);

                        if (porataLevel >= 2 && porataLevel <= 6) {
                            // Bông tai Porata 2-6
                            npcSay = "Bông tai Porata cấp " + porataLevel + "\n|2|";
                            player.combineNew.ratioCombine = getMoChiSoPorataRatio(porataLevel);
                        } else {
                            // Giữ lại logic cho item đặc biệt 2098, 2099
                            switch (bongTai.template.id) {
                                case 2098:
                                    npcSay = "Mũ cối siêu cấp VIP" + "\n|2|";
                                    player.combineNew.ratioCombine = 20;
                                    break;
                                case 2099:
                                    npcSay = "Hợp thể siêu nhân" + "\n|2|";
                                    player.combineNew.ratioCombine = 30;
                                    break;
                                default:
                                    // fallback nếu là Porata 1 hoặc item lạ
                                    npcSay = "Bông tai Porata" + "\n|2|";
                                    player.combineNew.ratioCombine = 40;
                                    break;
                            }
                        }

                        // Show option cũ của bông tai
                        for (ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }

                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%\n";

                        if (player.combineNew.goldCombine <= player.inventory.gold) {
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng\n";
                            npcSay += "|1|Cần " + player.combineNew.gemCombine + " Hồng ngọc\n";
                            npcSay += "|1|Cần 99 Mảnh hồn bông tai và 1 Đá xanh lam\n";
                            npcSay += "|7|Chỉ số sẽ được random theo cấp bông tai mỗi lần mở";

                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Mở chỉ số");
                        } else {
                            npcSay += "Còn thiếu " + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold) + " vàng";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Bông tai Porata, X99 Mảnh hồn bông tai và 1 Đá xanh lam", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Bông tai Porata, X99 Mảnh hồn bông tai và 1 Đá xanh lam", "Đóng");
                }
                break;

            case CHUYEN_HOA_DO_HUY_DIET:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con hãy đưa ta đồ Hủy diệt", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 1) {
                    int huydietok = 0;
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (item.isNotNullItem()) {
                        if (item.template.id >= 650 && item.template.id <= 662) {
                            huydietok = 1;
                        }
                    }
                    if (huydietok == 0) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta chỉ có thể chuyển hóa đồ Hủy diệt thôi", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Sau khi chuyển hóa vật phẩm\n|7|"
                            + "Bạn sẽ nhận được : 1 " + " Phiếu Hủy diệt Tương ứng\n"
                            + (500000000 > player.inventory.gold ? "|7|" : "|1|")
                            + "Cần " + Util.numberToMoney(50000000000L) + " vàng";

                    if (player.inventory.gold < 50000000000L) {
                        this.thoren.npcChat(player, "Hết tiền rồi\nẢo ít thôi con");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.MENU_CHUYEN_HOA_DO_HUY_DIET,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(50000000000L) + " vàng", "Từ chối");
                } else {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta chỉ có thể chuyển hóa 1 lần 1 món đồ Hủy diệt", "Đóng");
                }
                break;
            case CHE_TAO_THANH_TON:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chế tạo cần có 9999 Nguyên Tố Bí Ẩn"
                            + "\n 1 Long Huyết Thạch"
                            + "\n 1 Sách cổ ngữ", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isSachCoNgu()).count() < 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu sách cổ ngữ", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1687).count() < 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu long huyết thạch", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isNguyenTo() && item.quantity >= 999).count() < 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu nguyên tố bí ẩn", "Đóng");
                        return;
                    }

                    String npcSay = "Đồ thánh tôn có linh hồn nên nó \nsẽ tự quyêt định hình thái ?\n|7|"
                            + "Ngươi sẽ nhận được một trang bị thánh tôn ngẫu nhiên\n"
                            + "|1|Cần " + Util.numberToMoney(COST) + " vàng";

                    if (player.inventory.gold < COST) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.MENU_CHE_TAO_THANH_TON,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST) + " vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Tính hối lộ ta hay gì, hãy gọi ta là liêm", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chưa đủ nguyên liệu chế tạo mẹ gì, cook", "Đóng");
                }
                break;
            case NANG_SKH_THANH_TON:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.hangNga.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chế tạo cần có 1 TRANG BỊ THÁNH TÔN\n"
                            + "\n 1 TRANG BỊ SKH THIÊN SỨ"
                            + "\n 1 THẦN LONG THẠCH", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isThanhTon()).count() < 1) {
                        this.hangNga.createOtherMenu(player, ConstNpc.IGNORE_MENU, "THIẾU THẦN LONG THẠCH", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isSKH() && item.isDTS()).count() < 1) {
                        this.hangNga.createOtherMenu(player, ConstNpc.IGNORE_MENU, "THIẾU TRANG BỊ SKH THIÊN SỨ", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isThanLongThach()).count() < 1) {
                        this.hangNga.createOtherMenu(player, ConstNpc.IGNORE_MENU, "THIẾU TRANG BỊ THÁNH TÔN", "Đóng");
                        return;
                    }

                    String npcSay = "Ngươi sẽ nhận được TRANG BỊ SKH THÁNH TÔN\n"
                            + "Chỉ số gấp 10 đồ thánh tôn thường, kích hoạt skh thánh tôn\n"
                            + "Trang bị sẽ chắc chắn có chỉ số ẩn"
                            + "|1|Phí nâng cấp 100 triệu thỏi vàng";
                    Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
                    if (thoiVang == null || thoiVang.quantity < COST) {
                        this.hangNga.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chưa đủ 100 triệu thỏi vàng", "Đóng");
                        return;
                    }
                    this.hangNga.createOtherMenu(player, ConstNpc.MENU_NANG_SKH_THANH_TON,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST) + "\nthỏi vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.hangNga.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chỉ cần 3 nguyên liệu", "Đóng");
                        return;
                    }
                    this.hangNga.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chưa đủ nguyên liệu", "Đóng");
                }
                break;
            case THU_HOI_DO_TL:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy bỏ trang bị con muốn thu hồi vào", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 1) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isTrangBiThuHoi() && item.quantity >= 1).count() < 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Đây không phải đồ TL, HD, TS , Thánh Tôn", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isSKH() && item.quantity >= 1).count() == 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Không thể thu hồi đồ kích hoạt", "Đóng");
                        return;
                    }
                    String npcSay = "Đồ thần 50k Coin\n Đồ HD 5k coin\n Đồ thiên sứ 25k coin\n Đồ thánh tôn 666k Coin"
                            + "Coin này bạn có thể đổi thành thỏi vàng hoặc mở thành viên\n"
                            + "|1|Phí thu hồi: " + Util.numberToMoney(5_000_000_000L) + " vàng";

                    if (player.inventory.gold < 5_000_000_000L) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.MENU_THU_HOI_DO_TL,
                            npcSay, "Chế tạo\n" + Util.numberToMoney(5_000_000_000L) + " vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta chỉ hỗ trợ đổi mỗi lần một món", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Mấy thứ vớ vẩn này thu hồi mẹ gì, cook", "Đóng");
                }
                break;
            case PHAN_RA_DO_TS:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con hãy đưa ta đồ Thiên sứ", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 1) {
                    int dothiensu = 0;
                    Item item = player.combineNew.itemsCombine.get(0);

                    if (!item.isDTS()) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ Thiên sứ", "Đóng");
                        return;
                    }
                    if (item.isNotNullItem()) {
                        if (item.isDTS()) {
                            dothiensu = 1;
                        }
                    }
                    if (dothiensu == 0) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta chỉ có thể chuyển hóa đồ Thiên sứ thôi", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Sau khi chuyển hóa vật phẩm\n|7|"
                            + "Bạn sẽ nhận được : 500 " + " Mảnh thiên sứ Tương ứng\n"
                            + (500000000 > player.inventory.gold ? "|7|" : "|1|")
                            + "Cần " + Util.numberToMoney(500000000) + " vàng";

                    if (player.inventory.gold < 500000000) {
                        this.npcwhists.npcChat(player, "Hết tiền rồi\nẢo ít thôi con");
                        return;
                    }
                    this.npcwhists.createOtherMenu(player, ConstNpc.MENU_PHAN_RA_TS,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(500000000) + " vàng", "Từ chối");
                } else {
                    this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta chỉ có thể chuyển hóa 1 lần 1 món đồ Hủy diệt", "Đóng");
                }
                break;
            case NANG_CAP_DO_TS:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 2 món Hủy Diệt bất kì và 1 món Thần Linh cùng loại", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isCongThuc()).count() < 1) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu mảnh Công thức", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1083).count() < 1) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đá cầu vòng", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isManhTS() && item.quantity >= 999).count() < 1) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu mảnh thiên sứ", "Đóng");
                        return;
                    }

                    String npcSay = "|2|Con có muốn đổi các món nguyên liệu ?\n|7|"
                            + "Và nhận được " + player.combineNew.itemsCombine.stream().filter(Item::isManhTS).findFirst().get().typeNameManh() + " thiên sứ tương ứng\n"
                            + "|1|Cần " + Util.numberToMoney(COST) + " vàng";

                    if (player.inventory.gold < COST) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.npcwhists.createOtherMenu(player, ConstNpc.MENU_NANG_CAP_DO_TS,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST) + " vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cất đi con ta không thèm", "Đóng");
                        return;
                    }
                    this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NANG_CAP_SKH_TS:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 3 món Thiên sứ", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTS()).count() < 3) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ Thiên sứ", "Đóng");
                        return;
                    }
                    Item thoivang = null;
                    try {
                        thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                    } catch (Exception e) {
                    }
                    String npcSay = "|2|Con có muốn đổi các món nguyên liệu ?\n|7|"
                            + "Và nhận được\n|0|"
                            + player.combineNew.itemsCombine.stream().filter(Item::isDTS).findFirst().get().typeName() + " kích hoạt VIP tương ứng\n"
                            + ((thoivang == null || thoivang.quantity < 50) ? "|7|" : "|1|")
                            + "Cần 50 Thỏi vàng";

                    if (player.inventory.ruby < 1000000) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.npcwhists.createOtherMenu(player, ConstNpc.MENU_NANG_DO_SKH_TS,
                            npcSay, "Nâng cấp\n" + 1000000 + " ruby", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp", "Đóng");
                        return;
                    }
                    this.npcwhists.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NANG_CAP_SKH_VAI_THO:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 1 món đồ huỷ diệt", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDHD()).count() < 3) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ huỷ diệt", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isSKH()).count() >= 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Đây là đồ skh, không thể nâng cấp", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Con có muốn đổi các món nguyên liệu ?\n|7|"
                            + "Và nhận được\n|0|"
                            + player.combineNew.itemsCombine.stream().filter(Item::isDHD).findFirst().get().typeName() + " kích hoạt tương ứng\n"
                            + "Cần 2 tỏi vàng";

                    if (player.inventory.gold < 500000000) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có đủ 500tr có thể nâng cấp", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.MENU_NANG_SKH_VAI_THO,
                            npcSay, "Nâng cấp\n" + 500000000 + " Vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NANG_CAP_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 1 món đồ SKH hoặc Phụng Thiên Kích\n"
                            + "Và đá cường hoá để có thể nâng cấp trang bị thêm 1 bậc", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 2) {
                    String npcSay = "|2|Có chắc là muốn nâng cấp hay không ?\n|7|"
                            + "Nâng cấp sẽ tăng cấp độ vật phẩm lên một bậc\n"
                            + "Cần 2 tỏi vàng";

                    if (player.inventory.gold < 200000000) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có đủ 2 tỏi vàng mới có thể nâng cấp", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.MENU_NANG_CAP_TRANG_BI,
                            npcSay, "Nâng cấp\n" + 200000000 + " Vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 2) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chỉ cần một món đồ và đá cường hoá thôi", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NANG_CAP_KIM_DAN:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đặt kim đan và đá cường hoá vào\n"
                            + "Cường hoá nâng cấp kim đan sẽ cần 99 Đá cường hoá cùng cấp", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 2) {
                    String npcSay = "|7|NÂNG CẤP KIM ĐAN"
                            + "|3|20% TỈ LỆ NÂNG CẤP THÀNH CÔNG\n"
                            + "THÀNH CÔNG SẼ TĂNG CẤP KIM ĐAN THÊM 1 CẤP\n"
                            + "MỖI LẦN NÂNG CẤP SẼ TĂNG RẤT NHIỀU CHỈ SỐ";
                    this.tosu.createOtherMenu(player, ConstNpc.MENU_NANG_CAP_KIM_DAN,
                            npcSay, "Nâng cấp", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 2) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chỉ cần một món đồ và đá cường hoá thôi", "Đóng");
                        return;
                    }
                    this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case TANG_PHAM_CHAT_KIM_DAN:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đặt kim đan và đá cường hoá vào\n"
                            + "Cường hoá nâng cấp kim đan sẽ cần 99 Đá cường hoá cùng cấp", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 2) {
                    String npcSay = "|7|TĂNG PHẨM CHẤT KIM ĐAN"
                            + "|3|50% TỈ LỆ NÂNG CẤP THÀNH CÔNG\n"
                            + "- CÓ 4 CẤP KIM ĐAN:\n"
                            + "Sơ - Trung - Cao - Hoàn Mĩ\n"
                            + "TẨY THÀNH CÔNG SẼ CHO NGẪU NHIÊN 1 TRONG CÁC PHẨM CHẤT TRÊN";
                    this.tosu.createOtherMenu(player, ConstNpc.MENU_TANG_PHAM_CHAT_KIM_DAN,
                            npcSay, "Nâng cấp", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 2) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chỉ cần một món đồ và đá cường hoá thôi", "Đóng");
                        return;
                    }
                    this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NANG_CAP_THAN_LINH:
                if (player.combineNew.itemsCombine.isEmpty()) {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 1 món Thần linh", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 1) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL()).count() < 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ Thần linh", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isSKH()).count() >= 1) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Đây là đồ skh chỉ có thể cường hoá", "Đóng");
                        return;
                    }
                    Item doThanLinh = player.combineNew.itemsCombine.get(0);
                    String npcSay = "|2|Con có muốn nâng cấp " + doThanLinh.template.name + " Thành" + "\n|7|"
                            + doThanLinh.typeName() + " Hủy diệt " + Service.getInstance().get_HanhTinh(doThanLinh.template.gender) + "\n|0|"
                            + doThanLinh.typeOption() + "+?\n"
                            + "Yêu cầu sức mạnh 80 tỉ\n"
                            + "Không thể giao dịch\n"
                            + ((player.inventory.ruby < 10000) ? "|7|" : "|1|")
                            + "Cần 2Tỷ vàng";

                    if (player.inventory.gold < 10_000_000_000L) {
                        this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.thoren.createOtherMenu(player, ConstNpc.MENU_NANG_CAP_THAN_LINH,
                            npcSay, "Nâng cấp\n10Tỷ vàng", "Từ chối");
                } else {
                    this.thoren.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case GIA_HAN_VAT_PHAM:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item thegh = null;
                    Item itemGiahan = null;
                    for (Item item_ : player.combineNew.itemsCombine) {
                        if (item_.template.id == 1346) {
                            thegh = item_;
                        } else if (item_.isTrangBiHSD()) {
                            itemGiahan = item_;
                        }
                    }
                    if (thegh == null) {
                        Service.getInstance().sendThongBaoOK(player, "Cần 1 trang bị có hạn sử dụng và 1 phiếu Gia hạn");
                        return;
                    }
                    if (itemGiahan == null) {
                        Service.getInstance().sendThongBaoOK(player, "Cần 1 trang bị có hạn sử dụng và 1 phiếu Gia hạn");
                        return;
                    }
                    for (ItemOption itopt : itemGiahan.itemOptions) {
                        if (itopt.optionTemplate.id == 93) {
                            if (itopt.param < 0 || itopt == null) {
                                Service.getInstance().sendThongBaoOK(player, "Trang bị này không phải trang bị có Hạn Sử Dụng");
                                return;
                            }
                        }
                    }
                    String npcSay = "Trang bị được gia hạn \"" + itemGiahan.template.name + "\"\n|1|";
                    npcSay += itemGiahan.template.name + "\n|2|";
                    for (ItemOption io : itemGiahan.itemOptions) {
                        npcSay += io.getOptionString() + "\n";
                    }
                    npcSay += "\n|0|Sau khi gia hạn +1 ngày\n";

                    npcSay += "|0|Tỉ lệ thành công: 100%" + "\n";
                    if (player.inventory.gold > 200000000) {
                        npcSay += "|2|Cần 200Tr vàng";
                        this.xetank.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Nâng cấp", "Từ chối");

                    } else if (player.inventory.gold < 200000000) {
                        int SoVangThieu2 = (int) (200000000 - player.inventory.gold);
                        this.xetank.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn còn thiếu " + SoVangThieu2 + " vàng");
                    } else {
                        this.xetank.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần 1 trang bị có hạn sử dụng và 1 phiếu Gia hạn");
                    }
                } else {
                    this.xetank.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống");
                }
                break;
            case PHAP_SU_HOA:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 2) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        Item dangusac = player.combineNew.itemsCombine.get(1);
                        if (isTrangBiPhapsu(item)) {
                            if (item != null && item.isNotNullItem() && dangusac != null && dangusac.isNotNullItem() && dangusac.template.id == 1235 && dangusac.quantity >= 1) {
                                String npcSay = item.template.name + "\n|2|";
                                for (ItemOption io : item.itemOptions) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                                npcSay += "|1|Con có muốn biến trang bị " + item.template.name + " thành\n"
                                        + "trang bị Pháp sư hóa không?\n"
                                        + "|7|Cần 1 " + dangusac.template.name;
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép", "Từ chối");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn chưa bỏ đủ vật phẩm !!!", "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể Pháp sư hóa", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }
                break;
            case LINH_HOA_TRANG_BI:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 2) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        Item dangusac = player.combineNew.itemsCombine.get(1);
                        int capdo = 1;
                        if (isTrangBiLinhHoa(item)) {
                            if (item != null && item.isNotNullItem() && dangusac != null && dangusac.isNotNullItem() && dangusac.template.id == 698 && dangusac.quantity >= 1) {
                                String npcSay = item.template.name + "\n|2|";
                                for (ItemOption io : item.itemOptions) {
                                    npcSay += io.getOptionString() + "\n";
                                    if (io.optionTemplate.id == 277) {
                                        capdo = io.param;
                                    }
                                }
                                npcSay += "|1|Linh để tăng chỉ số trang bị cho " + item.template.name + " \n"
                                        + "|3|Chỉ số trang bị sẽ tăng ngẫu nhiên Sức Đánh, Hp, Ki cho đồ\n"
                                        + "|2|Nâng tối đa 99 lần mỗi trang bị"
                                        + "|7|Cần " + capdo + " " + dangusac.template.name;
                                Item xuvang = InventoryService.gI().findItemBagByTemp(player, 1568);
                                if (xuvang == null || xuvang.quantity < 1000) {
                                    Service.getInstance().sendThongBaoOK(player, "Cần 1000 xu vàng phí nâng cấp");
                                    return;
                                }
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Linh Hoá", "Từ chối");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn chưa bỏ đủ vật phẩm !!!", "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể Linh Hoá\n"
                                    + "Hãy đặt vật phẩm vào trước, linh hoả đặt vào sau", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }
                break;
            case TAY_PHAP_SU:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 2) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        Item dangusac = player.combineNew.itemsCombine.get(1);
                        if (isTrangBiPhapsu(item)) {
                            if (item != null && item.isNotNullItem() && dangusac != null && dangusac.isNotNullItem() && dangusac.template.id == 1236 && dangusac.quantity >= 1) {
                                String npcSay = item.template.name + "\n|2|";
                                for (ItemOption io : item.itemOptions) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                                npcSay += "|1|Con có muốn tẩy trang bị " + item.template.name + " về\n"
                                        + "lúc chưa Pháp sư hóa không?\n"
                                        + "|7|Cần 1 " + dangusac.template.name;
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép", "Từ chối");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn chưa bỏ đủ vật phẩm !!!", "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể thực hiện", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }
                break;

            // START _ SÁCH TUYỆT KỸ //
            case GIAM_DINH_SACH:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item sachTuyetKy = null;
                    Item buaGiamDinh = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        } else if (item.template.id == 1508) {
                            buaGiamDinh = item;
                        }
                    }
                    if (sachTuyetKy != null && buaGiamDinh != null) {

                        String npcSay = "|1|" + sachTuyetKy.getName() + "\n";
                        npcSay += "|2|" + buaGiamDinh.getName() + " " + buaGiamDinh.quantity + "/1";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Giám định", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ và bùa giám định");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ và bùa giám định");
                    return;
                }
                break;
            case TAY_SACH:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item sachTuyetKy = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        }
                    }
                    if (sachTuyetKy != null) {
                        String npcSay = "|2|Tẩy Sách Tuyệt Kỹ";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ để tẩy");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ để tẩy");
                    return;
                }
                break;

            case NANG_CAP_SACH_TUYET_KY:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item sachTuyetKy = null;
                    Item kimBamGiay = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item) && (item.template.id == 1510 || item.template.id == 1512 || item.template.id == 1514)) {
                            sachTuyetKy = item;
                        } else if (item.template.id == 1507) {
                            kimBamGiay = item;
                        }
                    }
                    if (sachTuyetKy != null && kimBamGiay != null) {
                        String npcSay = "|2|Nâng cấp sách tuyệt kỹ\n";
                        npcSay += "Cần 10 Kìm bấm giấy\n"
                                + "Tỉ lệ thành công: 30%\n"
                                + "Nâng cấp thất bại sẽ mất 10 Kìm bấm giấy";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Nâng cấp", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ 1 và 10 Kìm bấm giấy.");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ 1 và 10 Kìm bấm giấy.");
                    return;
                }
                break;
            case PHUC_HOI_SACH:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item sachTuyetKy = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        }
                    }
                    if (sachTuyetKy != null) {
                        String npcSay = "|2|Phục hồi " + sachTuyetKy.getName() + "\n"
                                + "Cần 10 cuốn sách cũ\n"
                                + "Phí phục hồi 10 triệu vàng";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                    return;
                }
                break;
            case PHAN_RA_SACH:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item sachTuyetKy = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        }
                    }
                    if (sachTuyetKy != null) {
                        String npcSay = "|2|Phân rã sách\n"
                                + "Nhận lại 5 cuốn sách cũ\n"
                                + "Phí rã 10 triệu vàng";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                    return;
                }
                break;

            // END _ SÁCH TUYỆT KỸ //
        }
    }

    /**
     * Bắt đầu đập đồ - điều hướng từng loại đập đồ
     *
     * @param player
     */
    public void startCombine(Player player) {
        switch (player.combineNew.typeCombine) {
            case EP_SAO_TRANG_BI:
                epSaoTrangBi(player);
                break;
            case PHA_LE_HOA_TRANG_BI:
                phaLeHoaTrangBi(player);
                break;
            case PHA_LE_HOA_TRANG_BI_X100:
                phaLeHoaTrangBix100(player);
                break;
            case CHUYEN_HOA_TRANG_BI:

                break;
            case NHAP_NGOC_RONG:
                nhapNgocRong(player);
                break;
            case AN_TRANG_BI:
                antrangbi(player);
                break;
            case LONG_AN_TRANG_BI:
                longantrangbi(player);
                break;
            case TAY_SAO_PHA_LE:
                TaySaoPhaLe(player);
                break;
            case CHUYEN_HOA_DO_HUY_DIET:
                chuyenhoahuydiet(player);
                break;
            case PHAN_RA_DO_TS:
                PhanRaDoTS(player);
                break;
            case THU_HOI_DO_TL:
                openThuHoiTrangBi(player);
                break;
            case NANG_CAP_DO_TS:
                openDTS(player);
                break;
            case CHE_TAO_THANH_TON:
                openCheTaoThanhTon(player);
                break;
            case NANG_SKH_THANH_TON:
                openCheTaoSKHThanhTon(player);
                break;
            case NANG_CAP_SKH_TS:
                openSKHts(player);
                break;
            case NANG_CAP_SKH_VAI_THO:
                openSKHVaiTho(player);
                break;
            case NANG_CAP_TRANG_BI:
                NangCapDoSKH(player);
                break;
            case TANG_PHAM_CHAT_KIM_DAN:
                tangPhamChatKimDan(player);
                break;
            case NANG_CAP_KIM_DAN:
                NangCapKimDan(player);
                break;
            case NANG_CAP_THAN_LINH:
                NcapDoThanLinh(player);
                break;
            case NANG_CAP_VAT_PHAM:
                nangCapVatPham(player);
                break;
            case NANG_CAP_BONG_TAI:
                nangCapBongTai(player);
                break;
            case MO_CHI_SO_BONG_TAI:
                moChiSoBongTai234(player);
            case PHAP_SU_HOA:
                phapsuhoa(player);
                break;
            case LINH_HOA_TRANG_BI:
                linhHoaTrangBi(player);
                break;
            case TAY_PHAP_SU:
                tayphapsu(player);
                break;
            case NANG_CAP_CHAN_MENH:
                nangCapChanMenh(player);
                break;
            case GIA_HAN_VAT_PHAM:
                GiaHanTrangBi(player);
                break;
//            case OPTION_PORATA:
//                nangCapVatPham(player);
//                break;   
            // START _ SÁCH TUYỆT KỸ //
            case GIAM_DINH_SACH:
                giamDinhSach(player);
                break;
            case TAY_SACH:
                taySach(player);
                break;
            case NANG_CAP_SACH_TUYET_KY:
                nangCapSachTuyetKy(player);
                break;
            case PHUC_HOI_SACH:
                phucHoiSach(player);
                break;
            case PHAN_RA_SACH:
                phanRaSach(player);
                break;
            // END _ SÁCH TUYỆT KỸ //
        }

        player.iDMark.setIndexMenu(ConstNpc.IGNORE_MENU);
        player.combineNew.clearParamCombine();
        player.combineNew.lastTimeCombine = System.currentTimeMillis();

    }

    public void GetTrangBiKichHoathuydiet(Player player, int id) {
        Item item = ItemService.gI().createNewItem((short) id);
        int[][] optionNormal = {{127, 128}, {130, 132}, {133, 135}};
        int[][] paramNormal = {{139, 140}, {142, 144}, {136, 138}};
        int[][] optionVIP = {{129}, {131}, {134}};
        int[][] paramVIP = {{141}, {143}, {137}};
        int random = Util.nextInt(optionNormal.length);
        int randomSkh = Util.nextInt(100);
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(47, Util.nextInt(1500, 2000)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(22, Util.nextInt(100, 150)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(0, Util.nextInt(9000, 11000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(23, Util.nextInt(90, 150)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(14, Util.nextInt(15, 20)));
        }
        if (randomSkh <= 20) {//tile ra do kich hoat
            if (randomSkh <= 5) { // tile ra option vip
                item.itemOptions.add(new ItemOption(optionVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(paramVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            } else {// 
                item.itemOptions.add(new ItemOption(optionNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(paramNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            }
        }

        InventoryService.gI().addItemBag(player, item, 0);
        InventoryService.gI().sendItemBags(player);
    }

    public void GetTrangBiKichHoatthiensu(Player player, int id) {
        Item item = ItemService.gI().createNewItem((short) id);
        int[][] optionNormal = {{127, 128}, {130, 132}, {133, 135}};
        int[][] paramNormal = {{139, 140}, {142, 144}, {136, 138}};
        int[][] optionVIP = {{129}, {131}, {134}};
        int[][] paramVIP = {{141}, {143}, {137}};
        int random = Util.nextInt(optionNormal.length);
        int randomSkh = Util.nextInt(100);
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(22, Util.nextInt(150, 200)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(0, Util.nextInt(18000, 20000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(23, Util.nextInt(150, 200)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(14, Util.nextInt(20, 25)));
        }
        if (randomSkh <= 20) {//tile ra do kich hoat
            if (randomSkh <= 5) { // tile ra option vip
                item.itemOptions.add(new ItemOption(optionVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(paramVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            } else {// 
                item.itemOptions.add(new ItemOption(optionNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(paramNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            }
        }

        InventoryService.gI().addItemBag(player, item, 0);
        InventoryService.gI().sendItemBags(player);
    }

    private void doiManhKichHoat(Player player) {
        if (player.combineNew.itemsCombine.size() == 2 || player.combineNew.itemsCombine.size() == 3) {
            Item nr1s = null, doThan = null, buaBaoVe = null;
            for (Item it : player.combineNew.itemsCombine) {
                if (it.template.id == 14) {
                    nr1s = it;
                } else if (it.template.id == 2010) {
                    buaBaoVe = it;
                } else if (it.template.id >= 555 && it.template.id <= 567) {
                    doThan = it;
                }
            }

            if (nr1s != null && doThan != null) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_DOI_MANH_KICH_HOAT) {
                    player.inventory.gold -= COST_DOI_MANH_KICH_HOAT;
                    int tiLe = buaBaoVe != null ? 100 : 50;
                    if (Util.isTrue(tiLe, 100)) {
                        sendEffectSuccessCombine(player);
                        Item item = ItemService.gI().createNewItem((short) 2009);
                        item.itemOptions.add(new ItemOption(30, 0));
                        InventoryService.gI().addItemBag(player, item, 0);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryService.gI().subQuantityItemsBag(player, nr1s, 1);
                    InventoryService.gI().subQuantityItemsBag(player, doThan, 1);
                    if (buaBaoVe != null) {
                        InventoryService.gI().subQuantityItemsBag(player, buaBaoVe, 1);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            } else {
                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị thần linh và 1 viên ngọc rồng 1 sao", "Đóng");
            }
        }
    }

    private void chuyenhoahuydiet(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            player.inventory.gold -= 500000000;
            Item item = player.combineNew.itemsCombine.get(0);
            Item phieu = null;
            switch (item.template.id) {
                case 650:
                case 652:
                case 654:
                    phieu = ItemService.gI().createNewItem((short) 1327);
                    break;
                case 651:
                case 653:
                case 655:
                    phieu = ItemService.gI().createNewItem((short) 1328);
                    break;
                case 657:
                case 659:
                case 661:
                    phieu = ItemService.gI().createNewItem((short) 1329);
                    break;
                case 658:
                case 660:
                case 662:
                    phieu = ItemService.gI().createNewItem((short) 1330);
                    break;
                default:
                    phieu = ItemService.gI().createNewItem((short) 1331);
                    break;
            }
            sendEffectSuccessCombine(player);
            this.baHatMit.npcChat(player, "Con đã nhận được 1 " + phieu.template.name);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            player.combineNew.itemsCombine.clear();
            InventoryService.gI().addItemBag(player, phieu, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            reOpenItemCombine(player);
        }
    }

    public void openThuHoiTrangBi(Player player) {

        if (player.combineNew.itemsCombine.size() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (player.inventory.gold < 5_000_000_000L) {
            Service.getInstance().sendThongBao(player, "Cần nộp phí 5b Cho mỗi lần thu hồi");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item trangbi = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isTrangBiThuHoi() && item.quantity >= 1).findFirst().get();
            player.inventory.gold -= 5_000_000_000L;
            if (trangbi.template.id >= 555 && trangbi.template.id <= 567) {
                PlayerDAO.CongVnd(player, 100);
            } else if (trangbi.template.id >= 650 && trangbi.template.id <= 662) {
                PlayerDAO.CongVnd(player, 200);
            } else if (trangbi.template.id >= 1048 && trangbi.template.id <= 1062) {
                PlayerDAO.CongVnd(player, 300);
            } else if (trangbi.template.id >= 1401 && trangbi.template.id <= 1405) {
                PlayerDAO.CongVnd(player, 500);
            } else {
                PlayerDAO.subVnd(player, 1000);
            }
            sendEffectSuccessCombine(player);
            InventoryService.gI().subQuantityItemsBag(player, trangbi, 1);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);

            Service.getInstance().sendThongBao(player, "Ta đã chuyển khoản cho ngươi rồi\n"
                    + "Vui lòng kiểm tra lại số coin bạn đang có");
        } else {
            Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void PhanRaDoTS(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            player.inventory.gold -= 500000000;
            Item item = player.combineNew.itemsCombine.get(0);
            Item manhts = null;
            switch (item.template.id) {
                case 1048:
                case 1049:
                case 1050:
                    manhts = ItemService.gI().createNewItem((short) 1066);
                    break;
                case 1051:
                case 1052:
                case 1053:
                    manhts = ItemService.gI().createNewItem((short) 1067);
                    break;
                case 1054:
                case 1055:
                case 1056:
                    manhts = ItemService.gI().createNewItem((short) 1070);
                    break;
                case 1057:
                case 1058:
                case 1059:
                    manhts = ItemService.gI().createNewItem((short) 1068);
                    break;
                default:
                    manhts = ItemService.gI().createNewItem((short) 1069);
                    break;
            }
            sendEffectSuccessCombine(player);
            manhts.quantity = 500;
            this.npcwhists.npcChat(player, "Con đã nhận được 500 " + manhts.template.name);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            player.combineNew.itemsCombine.clear();
            InventoryService.gI().addItemBag(player, manhts, 999);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            reOpenItemCombine(player);
        }
    }

    public void openSKHts(Player player) {
        Item thoivang = null;
        try {
            thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
        } catch (Exception e) {
        }
        if (thoivang == null || thoivang.quantity < 100) {
            Service.getInstance().sendThongBao(player, "Không đủ Thỏi vàng");
            return;
        }
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTS()).count() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ Thiên sứ");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.ruby < 500000) {
                Service.getInstance().sendThongBao(player, "Con cần thêm rubi để đổi...");
                return;
            }
            player.inventory.ruby -= 500000;
            Item itemTS = player.combineNew.itemsCombine.stream().filter(Item::isDTS).findFirst().get();
            List<Item> itemDTS = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTS()).collect(Collectors.toList());
            CombineServiceNew.gI().sendEffectOpenItem(player, itemTS.template.iconID, itemTS.template.iconID);
            short itemId;
            if (Util.isTrue(5, (int) 100)) {
                itemId = Manager.doSKHTs[player.gender][itemTS.template.type];
            } else if (Util.isTrue(20, (int) 100)) {
                itemId = Manager.doSKHHd[player.gender][itemTS.template.type];
            } else {
                itemId = Manager.doSKHTl[player.gender][itemTS.template.type];
            }
            int skhId = ItemService.gI().randomSKHId(player.gender);
            Item item;
            if (new Item(itemId).isDTL()) {
                item = Util.ratiItemTL(itemId);
                item.itemOptions.add(new ItemOption(skhId, 1));
                item.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                item.itemOptions.remove(item.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21).findFirst().get());
                item.itemOptions.add(new ItemOption(21, 15));
                item.itemOptions.add(new ItemOption(30, 1));
            } else if (new Item(itemId).isDHD()) {
                item = Util.ratiItemHuyDiet(itemId);
                item.itemOptions.add(new ItemOption(skhId, 1));
                item.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                item.itemOptions.remove(item.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21).findFirst().get());
                item.itemOptions.add(new ItemOption(21, 80));
                item.itemOptions.add(new ItemOption(30, 1));
            } else {
                item = ItemService.gI().DoThienSu(itemId, player.gender);
                item.itemOptions.add(new ItemOption(skhId, 1));
                item.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                item.itemOptions.remove(item.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21).findFirst().get());
                item.itemOptions.add(new ItemOption(21, 120));
                item.itemOptions.add(new ItemOption(30, 1));
            }
            InventoryService.gI().addItemBag(player, item, 0);
            InventoryService.gI().subQuantityItemsBag(player, itemTS, 1);
            itemDTS.forEach(j -> InventoryService.gI().subQuantityItemsBag(player, j, 1));
            InventoryService.gI().subQuantityItemsBag(player, thoivang, 50);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public void openDTS(Player player) {
        //check sl đồ tl, đồ hd
        // new update 2 mon huy diet + 1 mon than linh(skh theo style) +  5 manh bat ki
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (player.inventory.gold < COST) {
            Service.getInstance().sendThongBao(player, "Ảo ít thôi con...");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            return;
        }
        Item itemTL = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isCongThuc()).findFirst().get();
        Item itemHDs = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1083).findFirst().get();
        Item itemManh = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isManhTS() && item.quantity >= 999).findFirst().get();

        player.inventory.gold -= COST;
        sendEffectSuccessCombine(player);
        short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061}, {1050, 1053, 1056, 1059, 1062}}; // thứ tự td - 0,nm - 1, xd - 2

        Item itemTS = ItemService.gI().DoThienSu(itemIds[itemTL.template.gender > 2 ? player.gender : itemTL.template.gender][itemManh.typeIdManh()], itemTL.template.gender);
        InventoryService.gI().addItemBag(player, itemTS, 0);

        InventoryService.gI().subQuantityItemsBag(player, itemTL, 1);
        InventoryService.gI().subQuantityItemsBag(player, itemManh, 999);
        InventoryService.gI().subQuantityItemsBag(player, itemHDs, 1);
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendMoney(player);
        Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + itemTS.template.name);
        player.combineNew.itemsCombine.clear();
        reOpenItemCombine(player);
    }

    public void openCheTaoThanhTon(Player player) {
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (player.inventory.gold < COST) {
            Service.getInstance().sendThongBao(player, "Ảo ít thôi con...");
            return;
        }
        Item itemTL = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isSachCoNgu()).findFirst().get();
        Item itemHDs = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1687).findFirst().get();
        Item itemManh = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isNguyenTo() && item.quantity >= 999).findFirst().get();

        player.inventory.gold -= COST;
        sendEffectSuccessCombine(player);
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            short itemId;
            itemId = Manager.DoThanhTon[Util.nextInt(0, 4)];
            int skhId = ItemService.gI().randomSKHThanhTon(player.gender);
            Item items;
            if (new Item(itemId).isThanhTon()) {
                items = Util.ratiItemThanhTon(itemId);
//                items.itemOptions.add(new ItemOption(skhId, 1));
//                items.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKHThanhTon(skhId), 1));
                items.itemOptions.remove(items.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21).findFirst().get());
                items.itemOptions.add(new ItemOption(21, 200));
                items.itemOptions.add(new ItemOption(30, 1));
            } else {
                items = ItemService.gI().itemSKH(itemId, skhId);
            }
            InventoryService.gI().subQuantityItemsBag(player, itemTL, 1);
            InventoryService.gI().subQuantityItemsBag(player, itemManh, 999);
            InventoryService.gI().subQuantityItemsBag(player, itemHDs, 1);
            InventoryService.gI().addItemBag(player, items, 1);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "|1| Bạn nhận được " + items.template.name);
        } else {
            Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    public void openCheTaoSKHThanhTon(Player player) {
        // Cần ít nhất 2 ô trống
        if (InventoryService.gI().getCountEmptyBag(player) < 2) {
            Service.getInstance().sendThongBao(player, "Cần ít nhất 2 ô trống trong hành trang");
            return;
        }

        // Check 100 triệu thỏi vàng (id 457)
        Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
        final int REQUIRE_TV = 100_000_000;
        if (thoiVang == null || thoiVang.quantity < REQUIRE_TV) {
            Service.getInstance().sendThongBao(player, "Cần 100 triệu thỏi vàng để có thể chế tạo");
            return;
        }

        // Chỉ chấp nhận đúng 3 món nguyên liệu trong ô ghép
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Vui lòng đặt đúng 3 món: 1 Thánh Tôn + 1 SKH Thiên Sứ + 1 Thần Long Thạch");
            return;
        }

        long doTT = player.combineNew.itemsCombine.stream()
                .filter(it -> it.isNotNullItem() && it.isThanhTon()).count();
        long skhTS = player.combineNew.itemsCombine.stream()
                .filter(it -> it.isNotNullItem() && it.isSKH() && it.isDTS()).count();
        long thanLongThach = player.combineNew.itemsCombine.stream()
                .filter(it -> it.isNotNullItem() && it.isThanLongThach()).count();

        if (doTT != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ Thánh Tôn");
            return;
        }
        if (skhTS != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ SKH Thiên Sứ");
            return;
        }
        if (thanLongThach != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu Thần Long Thạch");
            return;
        }

        // Gom đúng từng món
        Item doThanhTon = null;
        Item doSkhThienSu = null;
        Item thanLt = null;
        for (Item it : player.combineNew.itemsCombine) {
            if (!it.isNotNullItem()) {
                continue;
            }
            if (it.isThanhTon() && doThanhTon == null) {
                doThanhTon = it;
                continue;
            }
            if (it.isSKH() && it.isDTS() && doSkhThienSu == null) {
                doSkhThienSu = it;
                continue;
            }
            if (it.isThanLongThach() && thanLt == null) {
                thanLt = it;
            }
        }
        if (doThanhTon == null || doSkhThienSu == null || thanLt == null) {
            Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu hợp lệ");
            return;
        }

        // Xác định itemId theo type của đồ Thánh Tôn
        int type = doThanhTon.template.type;
        if (type < 0 || type >= Manager.DoThanhTon.length) {
            Service.getInstance().sendThongBao(player, "Loại trang bị không hợp lệ");
            return;
        }
        short itemId = Manager.DoThanhTon[type];

        int skhId = ItemService.gI().randomSKHThanhTon(player.gender);

        // Tạo item kết quả
        Item items;
        if (new Item(itemId).isThanhTon()) {
            items = Util.ratiItemThanhTonVIP(itemId);
            items.itemOptions.add(new ItemOption(skhId, 1));
            items.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKHThanhTon(skhId), 100));
            // remove id=21 nếu tồn tại
            ItemOption opt21 = items.itemOptions.stream()
                    .filter(op -> op.optionTemplate.id == 21)
                    .findFirst().orElse(null);
            if (opt21 != null) {
                items.itemOptions.remove(opt21);
            }
            items.itemOptions.add(new ItemOption(21, 200));
            items.itemOptions.add(new ItemOption(30, 1));
        } else {
            items = ItemService.gI().itemSKH(itemId, skhId);
        }

        InventoryService.gI().subQuantityItemsBag(player, thoiVang, REQUIRE_TV);
        InventoryService.gI().subQuantityItemsBag(player, doThanhTon, 1);
        InventoryService.gI().subQuantityItemsBag(player, doSkhThienSu, 1);
        InventoryService.gI().subQuantityItemsBag(player, thanLt, 1);

        // Hiệu ứng + thêm đồ
        sendEffectSuccessCombine(player);
        InventoryService.gI().addItemBag(player, items, 1);
        InventoryService.gI().sendItemBags(player);

        player.combineNew.itemsCombine.clear();
        reOpenItemCombine(player);
        Service.getInstance().sendThongBao(player, "|1| Bạn nhận được " + items.template.name);
    }

    public void openSKHVaiTho(Player player) {
        Item thoivang = null;
        try {
            thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
        } catch (Exception e) {
        }
        if (thoivang == null || thoivang.quantity < 30) {
            Service.getInstance().sendThongBao(player, "Không đủ Thỏi vàng");
            return;
        }
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDHD()).count() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ hủy diệt");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.gem < 1000) {
                Service.getInstance().sendThongBao(player, "Con cần thêm ngoc xanh để đổi...");
                return;
            }
            player.inventory.gem -= 1000;
            Item itemTS = player.combineNew.itemsCombine.stream().filter(Item::isDHD).findFirst().get();
            List<Item> itemDHD = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDHD()).collect(Collectors.toList());
            CombineServiceNew.gI().sendEffectOpenItem(player, itemTS.template.iconID, itemTS.template.iconID);
            short itemId;
            if (player.gender == 3 || itemTS.template.type == 4) {
                itemId = Manager.radaSKHVip[Util.nextInt(0, 5)];
                if (Util.isTrue(3, (int) 100)) {
                    itemId = Manager.radaSKHVip[6];
                }
            } else {
                itemId = Manager.doSKHVip[player.gender][itemTS.template.type][Util.nextInt(0, 5)];
                if (Util.isTrue(3, (int) 100)) {
                    itemId = Manager.doSKHVip[player.gender][itemTS.template.type][6];
                }
            }
            int skhId = ItemService.gI().randomSKHId(player.gender);
            Item item;
            if (new Item(itemId).isDTL()) {
                item = Util.ratiItemTL(itemId);
                item.itemOptions.add(new ItemOption(skhId, 1));
                item.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                item.itemOptions.remove(item.itemOptions.stream().filter(itemOption -> itemOption.optionTemplate.id == 21).findFirst().get());
                item.itemOptions.add(new ItemOption(21, 15));
                item.itemOptions.add(new ItemOption(30, 1));
            } else {
                item = ItemService.gI().itemSKH(itemId, skhId);
            }
            InventoryService.gI().addItemBag(player, item, 0);
            InventoryService.gI().subQuantityItemsBag(player, itemTS, 1);
            itemDHD.forEach(j -> InventoryService.gI().subQuantityItemsBag(player, j, 1));
            InventoryService.gI().subQuantityItemsBag(player, thoivang, 30);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    private int getItemLevel(Item item) {
        short[] upgradeArr = Manager.doSKHVip[item.template.gender][item.template.type];
        for (int i = 0; i < upgradeArr.length; i++) {
            if (upgradeArr[i] == item.getId()) {
                return i; // Trả về chỉ số xuất hiện, coi đó là "cấp độ"
            }
        }
        return -1; // Nếu không tìm thấy, trả về -1 để xử lý lỗi
    }

    private int getItemLevelRada(Item item) {
        short[] upgradeArrRada = Manager.radaSKHVip;
        for (int i1 = 0; i1 < upgradeArrRada.length; i1++) {
            if (upgradeArrRada[i1] == item.getId()) {
                return i1; // Trả về chỉ số xuất hiện, coi đó là "cấp độ"
            }
        }
        return -1; // Nếu không tìm thấy, trả về -1 để xử lý lỗi
    }

    private int getItemLevelDaCuongHoa(Item item) {
        short[] upgradeArrRada = Manager.daCuongHoa;
        for (int i1 = 0; i1 < upgradeArrRada.length; i1++) {
            if (upgradeArrRada[i1] == item.getId()) {
                return i1; // Trả về chỉ số xuất hiện, coi đó là "cấp độ"
            }
        }
        return -1; // Nếu không tìm thấy, trả về -1 để xử lý lỗi
    }

    public void NangCapDoSKH(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            return;
        }
        List<Item> items = player.combineNew.itemsCombine;
        Item itemNangCap = null;
        Item daCuongHoa = null;
        // Duyệt danh sách một lần để lấy vật phẩm
        for (Item item : items) {
            if (item.isNotNullItem()) {
                if (item.isSKH() && itemNangCap == null) {
                    itemNangCap = item;
                } else if (item.isDaCuongHoa() && daCuongHoa == null) {
                    daCuongHoa = item;
                }
            }
        }
        if (itemNangCap == null) {
            Service.getInstance().sendThongBao(player, "Trang bị này không thể cường hoá");
            return;
        }
        if (daCuongHoa == null) {
            Service.getInstance().sendThongBao(player, "Thiếu Đá Cường Hoá");
            return;
        }
        if (itemNangCap.template.type <= 3) {
            int level = getItemLevel(itemNangCap);
            int level2 = getItemLevelDaCuongHoa(daCuongHoa);
            if (level != level2) {
                Service.getInstance().sendThongBao(player, "|7|Các vật phẩm không cùng cấp độ");
                return;
            }
            if (level >= 14) {
                Service.getInstance().sendThongBao(player, "|7|Bạn đã nâng cấp tới cấp độ cao nhất");
                return;
            }
            int slda = daCuongHoa.quantity;
            if (level >= 13 && slda < 3) {
                Service.getInstance().sendThongBao(player, "Thiếu Đá Cường Hoá");
                return;
            }
            if (level >= 15 && slda < 5) {
                Service.getInstance().sendThongBao(player, "Thiếu Đá Cường Hoá");
                return;
            }
            if (player.inventory.gold < 2000000000) {
                Service.getInstance().sendThongBao(player, "Con cần có đủ 2 tỷ vàng");
                return;
            }
            // Tìm chỉ số vật phẩm trong danh sách nâng cấp
            short[] arr = Manager.doSKHVip[itemNangCap.template.gender][itemNangCap.template.type];
            int index = -1;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == itemNangCap.getId()) {
                    index = i;
                    break;
                }
            }
            if (index == -1 || index + 1 >= arr.length) {
                Service.getInstance().sendThongBao(player, "Không thể nâng cấp vật phẩm này");
                return;
            }
            player.inventory.gold -= 2000000000;
            int skhId = -1;
            for (ItemOption option : itemNangCap.itemOptions) {
                int optionId = option.optionTemplate.id;
                if (optionId >= 127 && optionId <= 135) {
                    skhId = optionId;
                    break;
                }
            }
            int csSPL = 0; // Khởi tạo giá trị mặc định
            for (ItemOption option : itemNangCap.itemOptions) {
                if (option.optionTemplate.id == 49) {
                    csSPL = option.param; // Lấy giá trị của chỉ số 49
                    break;
                }
            }
            if (csSPL == 80) {
                Item splHoanTra = ItemService.gI().createNewItem((short) 1694);
                splHoanTra.quantity = 6;
                InventoryService.gI().addItemBag(player, splHoanTra, 99999);
            }
            CombineServiceNew.gI().sendEffectOpenItem(player, itemNangCap.template.iconID, itemNangCap.template.iconID);
            short itemId = arr[index + 1];
            Item newItem;
            if (level < 11) {
                newItem = ItemService.gI().itemSKH(itemId, skhId);
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
            } else if (level == 11) {
                newItem = Util.ratiItemTL(itemId);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                newItem.itemOptions.add(new ItemOption(76, 1));
                newItem.itemOptions.add(new ItemOption(30, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
                InventoryService.gI().sendItemBags(player);
            } else if (level == 12) {
                newItem = Util.ratiItemHuyDiet(itemId);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                newItem.itemOptions.add(new ItemOption(76, 1));
                newItem.itemOptions.add(new ItemOption(30, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
                InventoryService.gI().sendItemBags(player);
            } else if (level == 13) {
                newItem = Util.ratiItemThienSu(itemId);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                newItem.itemOptions.add(new ItemOption(76, 1));
                newItem.itemOptions.add(new ItemOption(30, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 3);
                InventoryService.gI().sendItemBags(player);
            } else {
                newItem = Util.ratiItemThanhTon(itemId);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                newItem.itemOptions.add(new ItemOption(76, 1));
                newItem.itemOptions.add(new ItemOption(30, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 5);
                InventoryService.gI().sendItemBags(player);
            }
            InventoryService.gI().addItemBag(player, newItem, 999999);
            InventoryService.gI().subQuantityItemsBag(player, itemNangCap, 1);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else if (itemNangCap.template.type == 4) {
            int level = getItemLevelRada(itemNangCap);
            int level2 = getItemLevelDaCuongHoa(daCuongHoa);
            if (level != level2) {
                Service.getInstance().sendThongBao(player, "|7|Các vật phẩm không cùng cấp độ");
                return;
            }
            int slda = daCuongHoa.quantity;
            if (level >= 15) {
                Service.getInstance().sendThongBao(player, "|7|Bạn đã nâng cấp tới cấp độ cao nhất");
                return;
            }
            if (level >= 13 && slda < 3) {
                Service.getInstance().sendThongBao(player, "|7|Thiếu đá cường hoá");
                return;
            }
            if (player.inventory.gold < 2000000000) {
                Service.getInstance().sendThongBao(player, "Con cần có đủ 2 tỷ vàng");
                return;
            }
            // Tìm chỉ số vật phẩm trong danh sách nâng cấp
            short[] arr = Manager.radaSKHVip;
            int index = -1;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == itemNangCap.getId()) {
                    index = i;
                    break;
                }
            }
            if (index == -1 || index + 1 >= arr.length) {
                Service.getInstance().sendThongBao(player, "Không thể nâng cấp vật phẩm này");
                return;
            }
            player.inventory.gold -= 2000000000;
            int skhId = -1;
            for (ItemOption option : itemNangCap.itemOptions) {
                int optionId = option.optionTemplate.id;
                if (optionId >= 127 && optionId <= 135) {
                    skhId = optionId;
                    break;
                }
            }
            int csSPL = 0; // Khởi tạo giá trị mặc định
            for (ItemOption option : itemNangCap.itemOptions) {
                if (option.optionTemplate.id == 49) {
                    csSPL = option.param; // Lấy giá trị của chỉ số 49
                    break;
                }
            }
            if (csSPL == 80) {
                Item splHoanTra = ItemService.gI().createNewItem((short) 1694);
                splHoanTra.quantity = 6;
                InventoryService.gI().addItemBag(player, splHoanTra, 99999);
            }
            CombineServiceNew.gI().sendEffectOpenItem(player, itemNangCap.template.iconID, itemNangCap.template.iconID);
            short itemId = arr[index + 1];
            Item newItem;
            if (level < 11) {
                newItem = ItemService.gI().itemSKH(itemId, skhId);
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
            } else if (level == 11) {
                newItem = Util.ratiItemTL(itemId);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                newItem.itemOptions.add(new ItemOption(76, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
                InventoryService.gI().sendItemBags(player);
            } else if (level == 12) {
                newItem = Util.ratiItemHuyDiet(itemId);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                newItem.itemOptions.add(new ItemOption(76, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
                InventoryService.gI().sendItemBags(player);
            } else if (level == 13) {
                int idnhants = itemId + player.gender;
                newItem = Util.ratiItemThienSu(idnhants);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 100));
                newItem.itemOptions.add(new ItemOption(76, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 3);
                InventoryService.gI().sendItemBags(player);
            } else {
                newItem = Util.ratiItemThanhTon(itemId);
                newItem.itemOptions.add(new ItemOption(skhId, 1));
                newItem.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(skhId), 1));
                newItem.itemOptions.add(new ItemOption(76, 1));
                InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
            }
            InventoryService.gI().addItemBag(player, newItem, 999999);
            InventoryService.gI().subQuantityItemsBag(player, itemNangCap, 1);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else if (itemNangCap.template.id == 1553) {
            Item PhungThienKich = itemNangCap;
            int level = PhungThienKich.getCapDo();
            int level2 = getItemLevelDaCuongHoa(daCuongHoa);
            if (level != level2) {
                Service.getInstance().sendThongBao(player, "|7|Các vật phẩm không cùng cấp độ");
                return;
            }
            if (level2 == 18) {
                Service.getInstance().sendThongBao(player, "|7|Bạn đã nâng cấp tới cấp độ cao nhất");
                return;
            }
            if (player.inventory.gold < 2000000000) {
                Service.getInstance().sendThongBao(player, "Con cần có đủ 2 tỷ vàng");
                return;
            }
            int capdo = level2 + 1;
            CombineServiceNew.gI().sendEffectOpenItem(player, itemNangCap.template.iconID, itemNangCap.template.iconID);
            PhungThienKich.template = ItemService.gI().getTemplate(PhungThienKich.template.id);
            PhungThienKich.itemOptions.clear();
            PhungThienKich.itemOptions.add(new ItemOption(246, 1));
            PhungThienKich.itemOptions.add(new ItemOption(0, (333 + capdo * 111)));
            PhungThienKich.itemOptions.add(new ItemOption(48, (4444 + capdo * 111)));
            PhungThienKich.itemOptions.add(new ItemOption(50, (15 + capdo * 5)));
            PhungThienKich.itemOptions.add(new ItemOption(77, (15 + capdo * 6)));
            PhungThienKich.itemOptions.add(new ItemOption(103, (15 + capdo * 6)));
            PhungThienKich.itemOptions.add(new ItemOption(101, 111 * capdo));
            PhungThienKich.itemOptions.add(new ItemOption(117, 25));
            if (level2 < 13) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (5 + (capdo * 2))));
            } else if (level2 == 13) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (10 + (capdo * 2))));
                PhungThienKich.itemOptions.add(new ItemOption(189, 1));
            } else if (level2 == 14) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (15 + (capdo * 2))));
                PhungThienKich.itemOptions.add(new ItemOption(189, 1));
            } else if (level2 == 15) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (20 + (capdo * 2))));
                PhungThienKich.itemOptions.add(new ItemOption(189, 1));
            } else if (level2 == 16) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (25 + (capdo * 2))));
                PhungThienKich.itemOptions.add(new ItemOption(189, 1));
            } else if (level2 == 17) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (30 + (capdo * 2))));
                PhungThienKich.itemOptions.add(new ItemOption(189, 1));
            } else if (level2 == 18) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (35 + (capdo * 2))));
                PhungThienKich.itemOptions.add(new ItemOption(189, 1));
            } else if (level2 == 19) {
                PhungThienKich.itemOptions.add(new ItemOption(5, (40 + (capdo * 2))));
                PhungThienKich.itemOptions.add(new ItemOption(189, 1));
            }
            PhungThienKich.itemOptions.add(new ItemOption(14, 15));
            PhungThienKich.itemOptions.add(new ItemOption(191, 1));
            PhungThienKich.itemOptions.add(new ItemOption(72, capdo));
            if (level2 == 13) {
                PhungThienKich.itemOptions.add(new ItemOption(109, 5));
                PhungThienKich.itemOptions.add(new ItemOption(76, 1));
            }
            PhungThienKich.itemOptions.add(new ItemOption(30, 1));
            sendEffectSuccessCombine(player);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            reOpenItemCombine(player);
            InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else if (itemNangCap.template.id == 1772) {
            Item thanxacdaithanh = itemNangCap;
            int level = thanxacdaithanh.getCapDo();
            int level2 = getItemLevelDaCuongHoa(daCuongHoa);
            if (level != level2) {
                Service.getInstance().sendThongBao(player, "|7|Các vật phẩm không cùng cấp độ");
                return;
            }
            if (level2 == 18) {
                Service.getInstance().sendThongBao(player, "|7|Bạn đã nâng cấp tới cấp độ cao nhất");
                return;
            }
            if (player.inventory.gold < 2000000000) {
                Service.getInstance().sendThongBao(player, "Con cần có đủ 2 tỷ vàng");
                return;
            }
            int capdo = level2 + 1;
            CombineServiceNew.gI().sendEffectOpenItem(player, itemNangCap.template.iconID, itemNangCap.template.iconID);
            thanxacdaithanh.template = ItemService.gI().getTemplate(thanxacdaithanh.template.id);
            thanxacdaithanh.itemOptions.clear();
            thanxacdaithanh.itemOptions.add(new ItemOption(0, (56789 + (capdo * 2222))));
            thanxacdaithanh.itemOptions.add(new ItemOption(6, (10000000 + (capdo * 1111111))));
            thanxacdaithanh.itemOptions.add(new ItemOption(7, (10000000 + (capdo * 1111111))));
            thanxacdaithanh.itemOptions.add(new ItemOption(50, (555 + (capdo * 22))));
            thanxacdaithanh.itemOptions.add(new ItemOption(77, (555 + (capdo * 33))));
            thanxacdaithanh.itemOptions.add(new ItemOption(103, (555 + (capdo * 33))));
            thanxacdaithanh.itemOptions.add(new ItemOption(101, (555 + (capdo * 55))));
            thanxacdaithanh.itemOptions.add(new ItemOption(117, (25 + capdo * 1)));
            thanxacdaithanh.itemOptions.add(new ItemOption(76, 1));
            thanxacdaithanh.itemOptions.add(new ItemOption(30, 1));
            thanxacdaithanh.itemOptions.add(new ItemOption(197, 1));
            thanxacdaithanh.itemOptions.add(new ItemOption(72, capdo));
            sendEffectSuccessCombine(player);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            reOpenItemCombine(player);
            InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, 1);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        }
    }

    private void tangPhamChatKimDan(Player player) {
        if (player.combineNew.itemsCombine.size() != 2) {
            Service.getInstance().sendThongBao(player, "Cần đặt kim đan và hoả long thạch vào");
            return;
        }
        Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
        if (thoiVang == null && thoiVang.quantity <= 999_999) {
            Service.getInstance().sendThongBao(player, "Cần 999.999 Thỏi vàng làm phí nâng cấp");
            return;
        }
        Item kimdan = null;
        Item hoaLongThach = null;
        for (Item item : player.combineNew.itemsCombine) {
            if (item.template.id == 1687) {
                hoaLongThach = item;
            } else if (item.template.id >= 1798 && item.template.id < 1801) {
                kimdan = item;
            }
        }
        int soluongda = 9;
        if (hoaLongThach == null && hoaLongThach.quantity <= soluongda) {
            Service.getInstance().sendThongBao(player, "Cần có 9 viên Hoả Long Thạch để thực hiện");
            return;
        }
        if (kimdan != null && (kimdan.template.id >= 1798 && kimdan.template.id < 1801)) {
            if (Util.isTrue(1, 100)) {
                InventoryService.gI().subQuantityItemsBag(player, hoaLongThach, soluongda);
                kimdan.template = ItemService.gI().getTemplate(1801);
                kimdan.itemOptions.clear();
                kimdan.itemOptions.add(new ItemOption(50, 50));
                kimdan.itemOptions.add(new ItemOption(77, 60));
                kimdan.itemOptions.add(new ItemOption(103, 60));
                kimdan.itemOptions.add(new ItemOption(76, 1));
                kimdan.itemOptions.add(new ItemOption(30, 1));
                sendEffectSuccessCombine(player);
            } else if (Util.isTrue(10, 100)) {
                InventoryService.gI().subQuantityItemsBag(player, hoaLongThach, soluongda);
                kimdan.template = ItemService.gI().getTemplate(1800);
                kimdan.itemOptions.clear();
                kimdan.itemOptions.add(new ItemOption(50, 40));
                kimdan.itemOptions.add(new ItemOption(77, 50));
                kimdan.itemOptions.add(new ItemOption(103, 50));
                kimdan.itemOptions.add(new ItemOption(30, 1));
                sendEffectSuccessCombine(player);
            } else if (Util.isTrue(50, 100)) {
                InventoryService.gI().subQuantityItemsBag(player, hoaLongThach, soluongda);
                kimdan.template = ItemService.gI().getTemplate(1799);
                kimdan.itemOptions.clear();
                kimdan.itemOptions.add(new ItemOption(50, 30));
                kimdan.itemOptions.add(new ItemOption(77, 40));
                kimdan.itemOptions.add(new ItemOption(103, 40));
                kimdan.itemOptions.add(new ItemOption(30, 1));
                sendEffectSuccessCombine(player);
            } else {
                InventoryService.gI().subQuantityItemsBag(player, hoaLongThach, soluongda);
                sendEffectFailCombine(player);
            }
            InventoryService.gI().subQuantityItemsBag(player, thoiVang, 999_999);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            reOpenItemCombine(player);
        } else {
            Service.getInstance().sendThongBao(player, "Vật phẩm không đúng");
            return;
        }
    }

    public void NangCapKimDan(Player player) {
        // Cần >= 2 ô trống
        if (InventoryService.gI().getCountEmptyBag(player) < 2) {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang");
            return;
        }

        Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
        if (thoiVang == null && thoiVang.quantity <= 999_999) {
            Service.getInstance().sendThongBao(player, "Cần 999.999 Thỏi vàng làm phí nâng cấp");
            return;
        }
        // Lấy Kim Đan & Đá Cường Hóa trong khay ghép
        Item kimDan = null, daCuongHoa = null;
        for (Item it : player.combineNew.itemsCombine) {
            if (it != null && it.isNotNullItem()) {
                if (kimDan == null && it.isKimDan()) {
                    kimDan = it;
                } else if (daCuongHoa == null && it.isDaCuongHoa()) {
                    daCuongHoa = it;
                }
            }
        }
        if (kimDan == null) {
            Service.getInstance().sendThongBao(player, "Trang bị này không thể cường hoá");
            return;
        }
        if (daCuongHoa == null) {
            Service.getInstance().sendThongBao(player, "Thiếu Đá Cường Hoá");
            return;
        }
        int soLuongDaCan = 0;
        int level = kimDan.getCapDo();
        int levelDa = getItemLevelDaCuongHoa(daCuongHoa);
        if (level != levelDa) {
            Service.getInstance().sendThongBao(player, "|7|Các vật phẩm không cùng cấp độ");
            return;
        }
        if (level < 10) {
            soLuongDaCan += 99;
        } else {
            soLuongDaCan += 9;
        }
        if (daCuongHoa.quantity < soLuongDaCan) {
            Service.getInstance().sendThongBaoOK(player, "Thiếu đá cường hoá, ở cấp này bạn cần " + soLuongDaCan + " Đá Cướng Hoá");
            return;
        }
        if (levelDa >= 18) {
            Service.getInstance().sendThongBao(player, "|7|Bạn đã nâng cấp tới cấp độ cao nhất");
            return;
        }
        int capDoMoi = levelDa + 1;

        // Hiệu ứng
        CombineServiceNew.gI().sendEffectOpenItem(player, kimDan.template.iconID, kimDan.template.iconID);
        int idKimDan = kimDan.template.id;
        int chiSoHPCoBan = 0;
        int chiSoSDCoBan = 0;
        if (idKimDan == 1801) {
            chiSoHPCoBan += 60;
            chiSoSDCoBan += 50;
        }
        if (idKimDan == 1800) {
            chiSoHPCoBan += 50;
            chiSoSDCoBan += 40;
        }
        if (idKimDan == 1799) {
            chiSoHPCoBan += 40;
            chiSoSDCoBan += 30;
        }
        if (idKimDan == 1798) {
            chiSoHPCoBan += 30;
            chiSoSDCoBan += 20;
        }
        // ====== MAP HỆ SỐ NHÂN CHO CÁC CHỈ SỐ ĐANG CÓ ======
        // (thay đổi tuỳ ý: ví dụ 50/77/101/103 nhân 3 lần)
        java.util.Map<Integer, Integer> mul = new java.util.HashMap<>();
        mul.put(50, chiSoSDCoBan * capDoMoi);   // SD
        mul.put(77, chiSoHPCoBan * capDoMoi);   // HP
        mul.put(101, chiSoHPCoBan * capDoMoi);  // KI
        // Tăng các option đang có theo map; GIỮ NGUYÊN 30 & 76; 72 xử lý riêng
        for (ItemOption opt : kimDan.itemOptions) {
            int id = opt.optionTemplate.id;
            if (id == 30 || id == 76 || id == 72) {
                continue; // 30 & 76 giữ nguyên; 72 xử lý bên dưới
            }
            Integer hs = mul.get(id);
            if (hs != null) {
                long newVal = (long) opt.param + hs; // tránh tràn
                opt.param = newVal > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) newVal;
            }
        }

        // ====== XỬ LÝ CẤP (opt 72) ======
        boolean has72 = false;
        for (ItemOption o : kimDan.itemOptions) {
            if (o.optionTemplate.id == 72) {
                o.param += 1;       // đã có -> +1 mỗi lần
                has72 = true;
                break;
            }
        }
        if (!has72) {
            // chưa có -> thêm đúng cấp mới
            kimDan.itemOptions.add(new ItemOption(72, capDoMoi));
        }

        // Hoàn tất
        sendEffectSuccessCombine(player);
        InventoryService.gI().subQuantityItemsBag(player, thoiVang, 999_999);
        InventoryService.gI().subQuantityItemsBag(player, daCuongHoa, soLuongDaCan);
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendMoney(player);
        player.combineNew.itemsCombine.clear();
        reOpenItemCombine(player);
        Service.getInstance().sendThongBaoOK(player,
                "Nâng cấp " + kimDan.getName() + " thành công lên cấp " + (has72 ? (level + 1) : capDoMoi) + "\n"
                + "Hp-ki tăng thêm: " + (chiSoHPCoBan * capDoMoi) + "%\n"
                + "Sd tăng thêm: " + (chiSoSDCoBan * capDoMoi) + "%");
    }

    public void NcapDoThanLinh(Player player) {
        // 1 thiên sứ + 2 món kích hoạt -- món đầu kh làm gốc
        if (player.combineNew.itemsCombine.size() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL()).count() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ Thần linh");
            return;
        }
        Item doThanLinh = player.combineNew.itemsCombine.get(0);
        Item thoivang = null;
        try {
            thoivang = InventoryService.gI().findItemBagByTemp(player, (short) 457);
        } catch (Exception e) {
        }
        if (thoivang == null || thoivang.quantity < 199) {
            Service.getInstance().sendThongBao(player, "|7|Chưa đủ thỏi vàng");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.gold < 2_000_000_000) {
                Service.getInstance().sendThongBao(player, "Con cần thêm vàng để đổi...");
                return;
            }
            player.inventory.gold -= 2_000_000_000;
            CombineServiceNew.gI().sendEffectOpenItem(player, doThanLinh.template.iconID, doThanLinh.template.iconID);
            Item item = Util.ratiItemHuyDiet(Manager.doHuyDiet[doThanLinh.template.gender][doThanLinh.template.type]);
            item.itemOptions.add(new ItemOption(30, 1));
            InventoryService.gI().addItemBag(player, item, 0);
            InventoryService.gI().subQuantityItemsBag(player, doThanLinh, 1);
            InventoryService.gI().subQuantityItemsBag(player, thoivang, 199);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public void randomskh(Player player) {
        // 1 thiên sứ + 2 món kích hoạt -- món đầu kh làm gốc
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL()).count() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ Thần linh");
            return;
        }
        Item montldau = player.combineNew.itemsCombine.get(0);
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.gold < 1) {
                Service.getInstance().sendThongBao(player, "Con cần thêm vàng để đổi...");
                return;
            }
            if (player.inventory.gold < 1) {
                Service.getInstance().sendThongBao(player, "Con cần thêm vàng để đổi...");
                return;
            }
            player.inventory.gold -= COST;
            List<Item> itemDTL = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL()).collect(Collectors.toList());
            CombineServiceNew.gI().sendEffectOpenItem(player, montldau.template.iconID, montldau.template.iconID);
            short itemId;
            if (player.gender == 3 || montldau.template.type == 4) {
                itemId = Manager.radaSKHThuong[0];
            } else {
                itemId = Manager.doSKHThuong[player.gender][montldau.template.type];
            }
            int skhId = ItemService.gI().randomSKHId(player.gender);
            Item item = ItemService.gI().itemSKH(itemId, skhId);
            InventoryService.gI().addItemBag(player, item, 0);
            itemDTL.forEach(i -> InventoryService.gI().subQuantityItemsBag(player, i, 1));
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    private void GiaHanTrangBi(Player player) {
        if (player.combineNew.itemsCombine.size() != 2) {
            Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isTrangBiHSD()).count() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu trang bị HSD");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1346).count() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu Bùa Gia Hạn");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item thegh = player.combineNew.itemsCombine.stream().filter(item -> item.template.id == 1346).findFirst().get();
            Item tbiHSD = player.combineNew.itemsCombine.stream().filter(Item::isTrangBiHSD).findFirst().get();
            if (thegh == null) {
                Service.getInstance().sendThongBao(player, "Thiếu Bùa Gia Hạn");
                return;
            }
            if (tbiHSD == null) {
                Service.getInstance().sendThongBao(player, "Thiếu trang bị HSD");
                return;
            }
            if (tbiHSD != null) {
                for (ItemOption itopt : tbiHSD.itemOptions) {
                    if (itopt.optionTemplate.id == 93) {
                        if (itopt.param < 0 || itopt == null) {
                            Service.getInstance().sendThongBao(player, "Không Phải Trang Bị Có HSD");
                            return;
                        }
                    }
                }
            }
            if (Util.isTrue(100, 100)) {
                sendEffectSuccessCombine(player);
                for (ItemOption itopt : tbiHSD.itemOptions) {
                    if (itopt.optionTemplate.id == 93) {
                        itopt.param += 1;
                        break;
                    }
                }
            } else {
                sendEffectFailCombine(player);
            }
            InventoryService.gI().subQuantityItemsBag(player, thegh, 1);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            reOpenItemCombine(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    private void epSaoTrangBi(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gem < gem) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item trangBi = null;
            Item daPhaLe = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (isTrangBiPhaLeHoa(item)) {
                    trangBi = item;
                } else if (isDaPhaLe(item)) {
                    daPhaLe = item;
                }
            }
            int star = 0; //sao pha lê đã ép
            int starEmpty = 0; //lỗ sao pha lê
            if (trangBi != null && daPhaLe != null) {
                ItemOption optionStar = null;
                for (ItemOption io : trangBi.itemOptions) {
                    if (io.optionTemplate.id == 102) {
                        star = io.param;
                        optionStar = io;
                    } else if (io.optionTemplate.id == 107) {
                        starEmpty = io.param;
                    }
                }
                if (star < starEmpty) {
                    player.inventory.gem -= gem;
                    int optionId = getOptionDaPhaLe(daPhaLe);
                    int param = getParamDaPhaLe(daPhaLe);
                    ItemOption option = null;
                    for (ItemOption io : trangBi.itemOptions) {
                        if (io.optionTemplate.id == optionId) {
                            option = io;
                            break;
                        }
                    }
                    if (option != null) {
                        option.param += param;
                    } else {
                        trangBi.itemOptions.add(new ItemOption(optionId, param));
                    }
                    if (optionStar != null) {
                        optionStar.param++;
                    } else {
                        trangBi.itemOptions.add(new ItemOption(102, 1));
                    }

                    InventoryService.gI().subQuantityItemsBag(player, daPhaLe, 1);
                    sendEffectSuccessCombine(player);
                }
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void phaLeHoaTrangBix100(Player player) {
        boolean flag = false;
        int solandap = player.combineNew.quantities;

        while (player.combineNew.quantities > 0 && !player.combineNew.itemsCombine.isEmpty() && !flag) {
            int gold = player.combineNew.goldCombine; // dùng làm "số lượng" thỏi vàng/xu vàng khi ở mốc đặc biệt
            int gem = player.combineNew.gemCombine;

            Item item = player.combineNew.itemsCombine.get(0);
            if (!isTrangBiPhaLeHoa(item)) {
                player.combineNew.quantities -= 1;
                continue;
            }

            // đọc sao hiện tại
            int star = 0;
            ItemOption optionStar = null;
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 107) {
                    star = io.param;
                    optionStar = io;
                    break;
                }
            }

            if (star >= MAX_STAR_ITEM) {
                break;
            }

            // ---- Điều kiện tiêu hao theo mốc sao ----
            boolean useThoiVang = (star <= 10);           // 7 -> 8
            boolean useXuVang = (star > 10 && star <= 20); // 8 -> 9 -> 10

            Item thoiVang = null, xuVang = null;

            if (useThoiVang) {
                thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
                if (thoiVang == null || thoiVang.quantity < gold) {
                    Service.getInstance().sendThongBao(player, "Không đủ thỏi vàng để thực hiện");
                    break;
                }
            } else if (useXuVang) {
                xuVang = InventoryService.gI().findItemBagByTemp(player, 1568);
                if (xuVang == null || xuVang.quantity < gold) {
                    Service.getInstance().sendThongBao(player, "Không đủ xu vàng để thực hiện");
                    break;
                }
            } else {
                // mốc bình thường: yêu cầu vàng + ngọc như cũ
                if (player.inventory.gold < gold) {
                    Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                    break;
                }
                if (player.inventory.gem < gem) {
                    Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                    break;
                }
            }

            // ---- Trừ tài nguyên ----
            if (useThoiVang) {
                InventoryService.gI().subQuantityItemsBag(player, thoiVang, gold);
            } else if (useXuVang) {
                InventoryService.gI().subQuantityItemsBag(player, xuVang, gold);
            } else {
                player.inventory.gold -= gold;
                player.inventory.gem -= gem;
            }

            // ---- Tính tỉ lệ & kết quả ----
            float epint = player.combineNew.ratioCombine; // giữ nguyên tỉ lệ đang set
            flag = Util.isTrue(epint, 100);

            if (flag) {
                if (optionStar == null) {
                    item.itemOptions.add(new ItemOption(107, 1));
                } else {
                    optionStar.param++;
                }

                sendEffectSuccessCombine(player);
                Service.getInstance().sendThongBao(player,
                        "Lên cấp sau " + (solandap - player.combineNew.quantities + 1) + " lần đập");

                if (optionStar != null && optionStar.param >= 7) {
                    ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa "
                            + "thành công " + item.template.name + " lên " + optionStar.param + " sao pha lê");
                }
            } else {
                sendEffectFailCombine(player);
            }

            player.combineNew.quantities -= 1;
        }

        if (!flag) {
            sendEffectFailCombine(player);
        }
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendMoney(player);
        reOpenItemCombine(player);
    }

    private void phaLeHoaTrangBi(Player player) {
        if (!player.combineNew.itemsCombine.isEmpty()) {
            Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
            Item xuVang = InventoryService.gI().findItemBagByTemp(player, 1568);
            int gold = player.combineNew.goldCombine;
            int gem = player.combineNew.gemCombine;
            Item item = player.combineNew.itemsCombine.get(0);

            if (isTrangBiPhaLeHoa(item)) {
                int star = 0;
                ItemOption optionStar = null;
                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 107) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }
                String thongbao;
                if (star < 11) {
                    if (thoiVang == null || thoiVang.quantity < gold) {
                        thongbao = "Không đủ thỏi vàng để thực hiện";
                        Service.getInstance().sendThongBao(player, thongbao);
                        return;
                    }
                } else {
                    if (xuVang == null || xuVang.quantity < gold) {
                        thongbao = "Không đủ xu vàng để thực hiện";
                        Service.getInstance().sendThongBao(player, thongbao);
                        return;
                    }
                }
                if (player.inventory.gem < gem) {
                    Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                    return;
                }
                if (star < MAX_STAR_ITEM) {
                    player.inventory.gem -= gem;
                    if (star < 11) {
                        InventoryService.gI().subQuantityItemsBag(player, thoiVang, gold);
                    } else {
                        InventoryService.gI().subQuantityItemsBag(player, xuVang, gold);
                    }
                    byte ratio = (optionStar != null && optionStar.param > 4) ? (byte) 2 : 1;
                    if (Util.isTrue(player.combineNew.ratioCombine, 100 * ratio)) {
                        if (optionStar == null) {
                            item.itemOptions.add(new ItemOption(107, 1));
                        } else {
                            optionStar.param++;
                        }
                        sendEffectSuccessCombine(player);
                        ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa "
                                + "thành công");
                    } else {
                        sendEffectFailCombine(player);
                    }
                }
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nhapNgocRong(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                if (item != null) {
                    int soluong = 7;
                    if (item.isNotNullItem() && (item.template.id > 14 && item.template.id <= 20) && item.quantity >= soluong) {
                        Item nr = ItemService.gI().createNewItem((short) (item.template.id - 1));
                        InventoryService.gI().addItemBag(player, nr, 0);
                        InventoryService.gI().subQuantityItemsBag(player, item, soluong);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);
                        sendEffectCombineDB(player, item.template.iconID);
                    }
                }
            }
        }
    }

    private void antrangbi(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                Item dangusac = player.combineNew.itemsCombine.get(1);
                int star = 0;
                ItemOption optionStar = null;
                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 34 || io.optionTemplate.id == 35
                            || io.optionTemplate.id == 36 || io.optionTemplate.id == 37
                            || io.optionTemplate.id == 38 || io.optionTemplate.id == 39) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }
                if (item != null && item.isNotNullItem()
                        && dangusac != null
                        && dangusac.isNotNullItem()
                        && (dangusac.template.id == 1232
                        || dangusac.template.id == 1233
                        || dangusac.template.id == 1234
                        || dangusac.template.id == 1689
                        || dangusac.template.id == 1690
                        || dangusac.template.id == 1691)
                        && dangusac.quantity >= 99) {
                    if (optionStar == null) {
                        if (dangusac.template.id == 1232) {
                            item.itemOptions.add(new ItemOption(34, 1));
                            sendEffectSuccessCombine(player);
                        } else if (dangusac.template.id == 1233) {
                            item.itemOptions.add(new ItemOption(35, 1));
                            sendEffectSuccessCombine(player);
                        } else if (dangusac.template.id == 1234) {
                            item.itemOptions.add(new ItemOption(36, 1));
                            sendEffectSuccessCombine(player);
                        }
//                    InventoryService.gI().addItemBag(player, item, 0);
                        InventoryService.gI().subQuantityItemsBag(player, dangusac, 99);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);
                        sendEffectCombineDB(player, item.template.iconID);
                    } else {
                        Service.getInstance().sendThongBao(player, "Trang bị của bạn có ấn rồi mà !!!");
                    }
                }
            }
        }
    }

    private void longantrangbi(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                Item dangusac = player.combineNew.itemsCombine.get(1);
                int star = 0;
                ItemOption optionStar = null;
                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 34 || io.optionTemplate.id == 35
                            || io.optionTemplate.id == 36 || io.optionTemplate.id == 37
                            || io.optionTemplate.id == 38 || io.optionTemplate.id == 39) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }
                if (item != null && item.isNotNullItem()
                        && dangusac != null
                        && dangusac.isNotNullItem()
                        && (dangusac.template.id == 1689
                        || dangusac.template.id == 1690
                        || dangusac.template.id == 1691)
                        && dangusac.quantity >= 99) {
                    if (optionStar != null) {
                        if (dangusac.template.id == 1689) {
                            item.itemOptions.add(new ItemOption(37, 1));
                            sendEffectSuccessCombine(player);
                        } else if (dangusac.template.id == 1690) {
                            item.itemOptions.add(new ItemOption(38, 1));
                            sendEffectSuccessCombine(player);
                        } else if (dangusac.template.id == 1691) {
                            item.itemOptions.add(new ItemOption(39, 1));
                            sendEffectSuccessCombine(player);
                        }
//                    InventoryService.gI().addItemBag(player, item, 0);
                        InventoryService.gI().subQuantityItemsBag(player, dangusac, 555);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);
                        sendEffectCombineDB(player, item.template.iconID);
                    } else {
                        Service.getInstance().sendThongBao(player, "Trang bị của bạn chưa được nâng cấp ấn!!!");
                    }
                }
            }
        }
    }

    private void TaySaoPhaLe(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                Item thoivang = player.combineNew.itemsCombine.get(1);
                ItemOption optionStar = null;
                for (ItemOption io : item.itemOptions) {
                    if (ItemData.OptionSPL.contains(io.optionTemplate.id)) {
                        optionStar = io;
                        break;
                    }
                }
                if (item.isNotNullItem() && thoivang.isNotNullItem() && (thoivang.template.id == 457) && thoivang.quantity >= 50) {
                    if (optionStar == null) {
                        Service.getInstance().sendThongBao(player, "Có gì đâu mà tẩy !!!");
                    } else {

                        if (item.itemOptions != null) {

                            Iterator<ItemOption> iterator = item.itemOptions.iterator();
                            while (iterator.hasNext()) {
                                ItemOption ioo = iterator.next();
                                if (ItemData.OptionSPL.contains(ioo.optionTemplate.id)) {
                                    iterator.remove();
                                }
                            }
                        }
                        //item.itemOptions.add(new ItemOption(73 , 1));  
                        sendEffectSuccessCombine(player);
                        InventoryService.gI().subQuantityItemsBag(player, thoivang, 50);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);

                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Thiếu vật phẩm gòi !!!");
                }

            }
        }
    }

    // START _ SÁCH TUYỆT KỸ
    private void giamDinhSach(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {

            Item sachTuyetKy = null;
            Item buaGiamDinh = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                } else if (item.template.id == 1508) {
                    buaGiamDinh = item;
                }
            }
            if (sachTuyetKy != null && buaGiamDinh != null) {
                Item sachTuyetKy_2 = ItemService.gI().createNewItem((short) sachTuyetKy.template.id);
                if (checkHaveOption(sachTuyetKy, 0, 241)) {
                    int tyle = new Random().nextInt(10);
                    if (tyle >= 0 && tyle <= 33) {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(50, new Random().nextInt(5, 20)));
                    } else if (tyle > 33 && tyle <= 66) {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(77, new Random().nextInt(10, 30)));
                    } else {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(103, new Random().nextInt(10, 30)));
                    }
                    for (int i = 1; i < sachTuyetKy.itemOptions.size(); i++) {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(sachTuyetKy.itemOptions.get(i).optionTemplate.id, sachTuyetKy.itemOptions.get(i).param));
                    }
                    sendEffectSuccessCombine(player);
                    InventoryService.gI().addItemBag(player, sachTuyetKy_2, 1);
                    InventoryService.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                    InventoryService.gI().subQuantityItemsBag(player, buaGiamDinh, 1);
                    InventoryService.gI().sendItemBags(player);
                    reOpenItemCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Vui lòng tẩy sách trước khi giảm định lần nữa");
                }
            }
        }
    }

    private void nangCapSachTuyetKy(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {

            Item sachTuyetKy = null;
            Item kimBamGiay = null;

            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                } else if (item.template.id == 1507) {
                    kimBamGiay = item;
                }
            }
            Item sachTuyetKy_2 = ItemService.gI().createNewItem((short) ((short) sachTuyetKy.template.id + 1));
            if (sachTuyetKy != null && kimBamGiay != null) {
                if (kimBamGiay.quantity < 10) {
                    Service.getInstance().sendThongBao(player, "Không đủ Kìm bấm giấy mà đòi nâng cấp");
                    return;
                }
                if (checkHaveOption(sachTuyetKy, 0, 241)) {
                    Service.getInstance().sendThongBao(player, "Chưa giám định mà đòi nâng cấp");
                    return;
                }
                if (Util.isTrue(30, 100)) {
                    for (int i = 0; i < sachTuyetKy.itemOptions.size(); i++) {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(sachTuyetKy.itemOptions.get(i).optionTemplate.id, sachTuyetKy.itemOptions.get(i).param));
                    }
                    sachTuyetKy_2.itemOptions.add(new ItemOption(5, Util.nextInt(5, 15)));
                    sendEffectSuccessCombine(player);
                    InventoryService.gI().addItemBag(player, sachTuyetKy_2, 1);
                    InventoryService.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                    InventoryService.gI().subQuantityItemsBag(player, kimBamGiay, 10);
                } else {
                    InventoryService.gI().subQuantityItemsBag(player, kimBamGiay, 10);
                    sendEffectFailCombine(player);
                }
                InventoryService.gI().sendItemBags(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void phucHoiSach(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item cuonSachCu = InventoryService.gI().findItemBagByTemp(player, (short) 1509);
            int goldPhanra = 10_000_000;
            Item sachTuyetKy = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                int doBen = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 243) {
                        doBen = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (cuonSachCu == null) {
                    Service.getInstance().sendThongBaoOK(player, "Cần sách tuyệt kỹ và 10 cuốn sách cũ");
                    return;
                }
                if (cuonSachCu.quantity < 10) {
                    Service.getInstance().sendThongBaoOK(player, "Cần sách tuyệt kỹ và 10 cuốn sách cũ");
                    return;
                }
                if (player.inventory.gold < goldPhanra) {
                    Service.getInstance().sendThongBao(player, "Không có tiền mà đòi phục hồi à");
                    return;
                }
                if (doBen != 1000) {
                    for (int i = 0; i < sachTuyetKy.itemOptions.size(); i++) {
                        if (sachTuyetKy.itemOptions.get(i).optionTemplate.id == 243) {
                            sachTuyetKy.itemOptions.get(i).param = 1000;
                            break;
                        }
                    }
                    player.inventory.gold -= 10_000_000;
                    InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, 10);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    sendEffectSuccessCombine(player);
                    reOpenItemCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Còn dùng được nên không thể phục hồi");
                    return;
                }
            }
        }
    }

    private void phanRaSach(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item cuonSachCu = ItemService.gI().createNewItem((short) 1509, 5);
            int goldPhanra = 10_000_000;
            Item sachTuyetKy = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                int luotTay = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 242) {
                        luotTay = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (player.inventory.gold < goldPhanra) {
                    Service.getInstance().sendThongBao(player, "Không có tiền mà đòi phân rã à");
                    return;
                }
                if (luotTay == 0) {

                    player.inventory.gold -= goldPhanra;
                    InventoryService.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                    InventoryService.gI().addItemBag(player, cuonSachCu, 999);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    sendEffectSuccessCombine(player);
                    reOpenItemCombine(player);

                } else {
                    Service.getInstance().sendThongBao(player, "Còn dùng được phân rã ăn cứt à");
                    return;
                }
            }
        }
    }

    private void taySach(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item sachTuyetKy = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                int luotTay = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 242) {
                        luotTay = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (luotTay == 0) {
                    Service.getInstance().sendThongBao(player, "Còn cái nịt mà tẩy");
                    return;
                }
                Item sachTuyetKy_2 = ItemService.gI().createNewItem((short) sachTuyetKy.template.id);
                if (checkHaveOption(sachTuyetKy, 0, 241)) {
                    Service.getInstance().sendThongBao(player, "Còn cái nịt mà tẩy");
                    return;
                }
                int tyle = new Random().nextInt(10);
                for (int i = 1; i < sachTuyetKy.itemOptions.size(); i++) {
                    if (sachTuyetKy.itemOptions.get(i).optionTemplate.id == 242) {
                        sachTuyetKy.itemOptions.get(i).param -= 1;
                    }
                }
                sachTuyetKy_2.itemOptions.add(new ItemOption(241, 0));
                for (int i = 1; i < sachTuyetKy.itemOptions.size(); i++) {
                    sachTuyetKy_2.itemOptions.add(new ItemOption(sachTuyetKy.itemOptions.get(i).optionTemplate.id, sachTuyetKy.itemOptions.get(i).param));
                }
                sendEffectSuccessCombine(player);
                InventoryService.gI().addItemBag(player, sachTuyetKy_2, 1);
                InventoryService.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                InventoryService.gI().sendItemBags(player);
                reOpenItemCombine(player);
            }
        }
    }

    private boolean checkHaveOption(Item item, int viTriOption, int idOption) {
        if (item != null && item.isNotNullItem()) {
            if (item.itemOptions.get(viTriOption).optionTemplate.id == idOption) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // END _ SÁCH TUYỆT KỸ
    //    private void phanradothanlinh(Player player) {
//        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//            if (!player.combineNew.itemsCombine.isEmpty()) {
//                Item item = player.combineNew.itemsCombine.get(0);
//                if (item != null && item.isNotNullItem() && (item.template.id > 0 && item.template.id <= 3) && item.quantity >= 1) {
//                    Item nr = ItemService.gI().createNewItem((short) (item.template.id - 78));
//                    InventoryService.gI().addItemBag(player, nr, 0);
//                    InventoryService.gI().subQuantityItemsBag(player, item, 1);
//                    InventoryService.gI().sendItemBags(player);
//                    reOpenItemCombine(player);
//                    sendEffectCombineDB(player, item.template.iconID);
//                    Service.getInstance().sendThongBao(player, "Đã nhận được 1 điểm");
//
//                }
//            }
//        }
//    }
    private void moChiSoBongTai234(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            int gold = (int) player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int ruby = player.combineNew.gemCombine;
            if (player.inventory.ruby < ruby) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }

            Item BongTai = null;
            Item ManhHon = null;
            Item DaXanhLam = null;
            for (Item item : player.combineNew.itemsCombine) {
                switch (item.template.id) {
                    case 1129://porata cấp 4
                    case 1165://porata cấp 3
                    case 921://porata cấp 2
                    case 1975://porata cấp 6
                    case 1976://porata cấp 5
                        BongTai = item;
                        break;
                    case 934://Mảnh Hồn
                        ManhHon = item;
                        break;
                    case 935://Đá Xanh Lam
                        DaXanhLam = item;
                        break;
                    default:
                        break;
                }
            }

            if (BongTai != null && ManhHon != null && DaXanhLam != null
                    && DaXanhLam.quantity >= 1 && ManhHon.quantity >= 99) {

                player.inventory.gold -= gold;
                player.inventory.ruby -= ruby;
                InventoryService.gI().subQuantityItemsBag(player, ManhHon, 99);
                InventoryService.gI().subQuantityItemsBag(player, DaXanhLam, 1);

                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {

                    // Xác định cấp Porata từ template id
                    int porataLevel = getPorataLevelByTemplateId(BongTai.template.id);

                    // Xác định range chỉ số theo cấp
                    int minStat = 1;
                    int maxStat = 5; // fallback
                    switch (porataLevel) {
                        case 2: // Porata 2
                            minStat = 1;
                            maxStat = 25;
                            break;
                        case 3: // Porata 3
                            minStat = 5;
                            maxStat = 35;
                            break;
                        case 4: // Porata 4
                            minStat = 10;
                            maxStat = 45;
                            break;
                        case 5: // Porata 5
                            minStat = 15;
                            maxStat = 55;
                            break;
                        case 6: // Porata 6
                            minStat = 20;
                            maxStat = 60;
                            break;
                        default:
                            // nếu lỡ là Porata 1 hoặc item lạ thì cho range nhẹ
                            minStat = 1;
                            maxStat = 5;
                            break;
                    }

                    // random 1 lần giá trị, dùng chung cho option được chọn
                    int statValue = Util.nextInt(minStat, maxStat);

                    // Xoá option cũ, add option mở chỉ số mới
                    BongTai.itemOptions.clear();
                    BongTai.itemOptions.add(new ItemOption(72, 2)); // dòng cố định của bạn

                    int rdUp = Util.nextInt(0, 7);
                    switch (rdUp) {
                        case 0:
                            BongTai.itemOptions.add(new ItemOption(50, statValue));
                            break;
                        case 1:
                            BongTai.itemOptions.add(new ItemOption(77, statValue));
                            break;
                        case 2:
                            BongTai.itemOptions.add(new ItemOption(103, statValue));
                            break;
                        case 3:
                            BongTai.itemOptions.add(new ItemOption(94, statValue));
                            break;
                        case 4:
                            BongTai.itemOptions.add(new ItemOption(14, statValue));
                            break;
                        case 5:
                            BongTai.itemOptions.add(new ItemOption(80, statValue));
                            break;
                        case 6:
                            BongTai.itemOptions.add(new ItemOption(81, statValue));
                            break;
                        default:
                            break;
                    }

                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }

                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nangCapBongTai(Player player) {
        if (player.combineNew.itemsCombine.size() != 2) {
            return;
        }

        long gold = player.combineNew.goldCombine;
        int gem = player.combineNew.gemCombine;

        if (player.inventory.gold < gold) {
            Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
            return;
        }
        if (player.inventory.ruby < gem) {
            Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc để thực hiện");
            return;
        }

        Item bongTai = null;
        // mảnh vỡ các cấp
        Item manhVo = null;
        Item manhVo2 = null;
        Item manhVo3 = null;
        Item manhVo4 = null;
        Item manhVo5 = null;
        Item manhVo6 = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            switch (item.template.id) {
                case 454:
                case 921:
                case 1165:
                case 1129:
                case 1976:
                case 1975:
                    bongTai = item;
                    break;
                case 1773:
                    manhVo = item;
                    break;
                case 1774:
                    manhVo2 = item;
                    break;
                case 1775:
                    manhVo3 = item;
                    break;
                case 1776:
                    manhVo4 = item;
                    break;
                case 1777:
                    manhVo5 = item;
                    break;
            }
        }

        if (bongTai == null) {
            Service.getInstance().sendThongBao(player, "Không tìm thấy bông tai để nâng cấp");
            return;
        }

        int currentLevel = getPorataLevelByTemplateId(bongTai.template.id);
        if (currentLevel <= 0 || currentLevel >= MAX_PORATA_LEVEL) {
            Service.getInstance().sendThongBao(player, "Không thể nâng cấp bông tai cấp này");
            return;
        }

        int nextLevel = currentLevel + 1;
        short fragId = getFragmentTemplateIdForNextLevel(nextLevel);
        long requireFrag = getRequiredFragmentForNextLevel(nextLevel);
        if (fragId == -1 || requireFrag <= 0) {
            Service.getInstance().sendThongBao(player, "Chưa cấu hình nguyên liệu cho cấp " + nextLevel);
            return;
        }

        Item fragItem = null;
        switch (fragId) {
            case 1773:
                fragItem = manhVo;
                break;
            case 1774:
                fragItem = manhVo2;
                break;
            case 1775:
                fragItem = manhVo3;
                break;
            case 1776:
                fragItem = manhVo4;
                break;
            case 1777:
                fragItem = manhVo5;
                break;
        }

        if (fragItem == null || fragItem.quantity < requireFrag) {
            Service.getInstance().sendThongBao(player,
                    "Không đủ mảnh vỡ bông tai cấp " + nextLevel + " (cần " + requireFrag + ")");
            return;
        }

        // Check đã có bông tai cấp tiếp theo trong hành trang chưa
        short nextTemplateId = getPorataTemplateIdByLevel(nextLevel);
        if (nextTemplateId != -1) {
            Item findItemBag = InventoryService.gI().findItemBagByTemp(player, nextTemplateId);
            if (findItemBag != null) {
                Service.getInstance().sendThongBao(player,
                        "Ngươi đã có " + getPorataNameByLevel(nextLevel) + " trong hành trang rồi, không thể nâng cấp nữa.");
                return;
            }
        }

        // Trừ vàng, ruby
        player.inventory.gold -= gold;
        player.inventory.ruby -= gem;

        // Tỉ lệ thành công
        if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
            // Thành công: trừ full mảnh vỡ
            InventoryService.gI().subQuantityItemsBag(player, fragItem, (int) requireFrag);
            // Nâng cấp template
            if (nextTemplateId != -1) {
                bongTai.template = ItemService.gI().getTemplate(nextTemplateId);
            }
            sendEffectSuccessCombine(player);
        } else {
            // Thất bại: trừ 10% mảnh vỡ (ít nhất 1)
            int lose = (int) Math.max(1, requireFrag / 10);
            if (fragItem.quantity < lose) {
                lose = fragItem.quantity;
            }
            InventoryService.gI().subQuantityItemsBag(player, fragItem, lose);
            sendEffectFailCombine(player);
        }

        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendMoney(player);
        reOpenItemCombine(player);
    }

// ========================== CONFIG PORATA ==========================
    private static final int MAX_PORATA_LEVEL = 4; // hiện tại cho max C4, sau này sửa thành 5 hoặc 6 là xong

// Lấy cấp bông tai theo template id
    private int getPorataLevelByTemplateId(int templateId) {
        switch (templateId) {
            case 454:
                return 1;
            case 921:
                return 2;
            case 1165:
                return 3;
            case 1129:
                return 4;
            case 1976:
                return 5;
            case 1975:
                return 6;
            default:
                return 0;
        }
    }

// Tỉ lệ mở chỉ số theo cấp Porata
    private int getMoChiSoPorataRatio(int porataLevel) {
        switch (porataLevel) {
            case 2:
                return 70;
            case 3:
                return 60;
            case 4:
                return 50;
            case 5:
                return 40;
            case 6:
                return 30;
            default:
                return 50;
        }
    }
// Lấy template id bông tai theo cấp

    private short getPorataTemplateIdByLevel(int level) {
        switch (level) {
            case 1:
                return 454;
            case 2:
                return 921;
            case 3:
                return 1165;
            case 4:
                return 1129;
            case 5:
                return 1976;
            case 6:
                return 1975;
            default:
                return -1;
        }
    }

// Mảnh vỡ dùng để lên cấp nextLevel (VD: lên C2 dùng 1773, lên C3 dùng 1773 ...)
    private short getFragmentTemplateIdForNextLevel(int nextLevel) {
        switch (nextLevel) {
            case 2:
                return 1773;
            case 3:
                return 1774;
            case 4:
                return 1775;
            case 5:
                return 1776;
            case 6:
                return 1777;
            default:
                return -1;
        }
    }

// Số mảnh vỡ cần cho lần nâng lên nextLevel
    private long getRequiredFragmentForNextLevel(int nextLevel) {
        switch (nextLevel) {
            case 2:
                return 99;      // 1 -> 2
            case 3:
                return 999;     // 2 -> 3
            case 4:
                return 9999;    // 3 -> 4
            case 5:
                return 99_999;  // 4 -> 5 (tùy bạn chỉnh)
            case 6:
                return 999_999; // 5 -> 6 (tùy bạn chỉnh)
            default:
                return 0;
        }
    }

// Vàng cần theo cấp hiện tại (level hiện tại, chưa lên)
    private long getPorataGoldCost(int currentLevel) {
        switch (currentLevel) {
            case 1:
                return 100_000_000L;   // 1 -> 2
            case 2:
                return 1_000_000_000L; // 2 -> 3
            case 3:
                return 1_000_000_000L; // 3 -> 4
            case 4:
                return 2_000_000_000L; // 4 -> 5
            case 5:
                return 2_000_000_000L; // 5 -> 6
            default:
                return 0;
        }
    }

// Hồng ngọc cần theo cấp hiện tại
    private int getPorataGemCost(int currentLevel) {
        switch (currentLevel) {
            case 1:
                return 1000;   // 1 -> 2
            case 2:
                return 5000;   // 2 -> 3
            case 3:
                return 10000;  // 3 -> 4
            case 4:
                return 20000;  // 4 -> 5
            case 5:
                return 20000;  // 5 -> 6
            default:
                return 0;
        }
    }

// Tỉ lệ thành công theo cấp hiện tại
    private int getPorataRatio(int currentLevel) {
        switch (currentLevel) {
            case 1:
                return 80; // 1 -> 2
            case 2:
                return 60; // 2 -> 3
            case 3:
                return 40; // 3 -> 4
            case 4:
                return 30; // 4 -> 5
            case 5:
                return 10; // 5 -> 6
            default:
                return 0;
        }
    }

// Tên cấp mới để show cho đẹp
    private String getPorataNameByLevel(int level) {
        switch (level) {
            case 1:
                return "Bông tai Porata cấp 1";
            case 2:
                return "Bông tai Porata cấp 2";
            case 3:
                return "Bông tai Porata cấp 3";
            case 4:
                return "Bông tai Porata cấp 4";
            case 5:
                return "Bông tai Porata cấp 5";
            case 6:
                return "Bông tai Porata cấp 6";
            default:
                return "Bông tai Porata";
        }
    }

    private void nangCapChanMenh(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int diem = player.combineNew.DiemNangcap;
            if (player.inventory.ruby < diem) {
                Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc để thực hiện");
                return;
            }
            Item chanmenh = null;
            Item dahoangkim = null;
            int capbac = 0;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.template.id == 1318) {
                    dahoangkim = item;
                } else if (item.template.id >= 1300 && item.template.id < 1308) {
                    chanmenh = item;
                    capbac = item.template.id - 1299;
                }
            }
            int soluongda = player.combineNew.DaNangcap;
            if (dahoangkim != null && dahoangkim.quantity >= soluongda) {
                if (chanmenh != null && (chanmenh.template.id >= 1300 && chanmenh.template.id < 1308)) {
                    player.inventory.ruby -= diem;
                    if (Util.isTrue(player.combineNew.TileNangcap, 100)) {
                        InventoryService.gI().subQuantityItemsBag(player, dahoangkim, soluongda);
                        chanmenh.template = ItemService.gI().getTemplate(chanmenh.template.id + 1);
                        chanmenh.itemOptions.clear();
                        chanmenh.itemOptions.add(new ItemOption(50, (20 + capbac * 3)));
                        chanmenh.itemOptions.add(new ItemOption(77, (30 + capbac * 4)));
                        chanmenh.itemOptions.add(new ItemOption(103, (30 + capbac * 4)));
                        if (capbac == 9) {
                            chanmenh.itemOptions.add(new ItemOption(109, 5));
                        }
                        chanmenh.itemOptions.add(new ItemOption(30, 1));
                        sendEffectSuccessCombine(player);
                    } else {
                        InventoryService.gI().subQuantityItemsBag(player, dahoangkim, soluongda);
                        sendEffectFailCombine(player);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            } else {
                Service.getInstance().sendThongBao(player, "Không đủ mảnh chân mệnh để thực hiện");
            }
        }
    }

    private void nangCapVatPham(Player player) {
        if (player.combineNew.itemsCombine.size() >= 2 && player.combineNew.itemsCombine.size() < 4) {
            if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type < 5).count() != 1) {
                return;
            }
            if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14).count() != 1) {
                return;
            }
            if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 987).count() != 1) {
                return;//admin
            }
            Item itemDo = null;
            Item itemDNC = null;
            Item itemDBV = null;
            for (int j = 0; j < player.combineNew.itemsCombine.size(); j++) {
                if (player.combineNew.itemsCombine.get(j).isNotNullItem()) {
                    if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.get(j).template.id == 987) {
                        itemDBV = player.combineNew.itemsCombine.get(j);
                        continue;
                    }
                    if (player.combineNew.itemsCombine.get(j).template.type < 5) {
                        itemDo = player.combineNew.itemsCombine.get(j);
                    } else {
                        itemDNC = player.combineNew.itemsCombine.get(j);
                    }
                }
            }
            if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {
                int countDaNangCap = player.combineNew.countDaNangCap;
                int gold = player.combineNew.goldCombine;
                short countDaBaoVe = player.combineNew.countDaBaoVe;
                if (player.inventory.gold < gold) {
                    Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                    return;
                }

                if (itemDNC.quantity < countDaNangCap) {
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (Objects.isNull(itemDBV)) {
                        return;
                    }
                    if (itemDBV.quantity < countDaBaoVe) {
                        return;
                    }
                }

                int level = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : itemDo.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (level < MAX_LEVEL_ITEM) {
                    player.inventory.gold -= gold;
                    ItemOption option = null;
                    ItemOption option2 = null;
                    for (ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id == 47
                                || io.optionTemplate.id == 6
                                || io.optionTemplate.id == 0
                                || io.optionTemplate.id == 7
                                || io.optionTemplate.id == 14
                                || io.optionTemplate.id == 22
                                || io.optionTemplate.id == 23) {
                            option = io;
                        } else if (io.optionTemplate.id == 27
                                || io.optionTemplate.id == 28) {
                            option2 = io;
                        }
                    }
                    if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                        option.param += (option.param * 10 / 100);
                        if (option2 != null) {
                            option2.param += (option2.param * 10 / 100);
                        }
                        if (optionLevel == null) {
                            itemDo.itemOptions.add(new ItemOption(72, 1));
                        } else {
                            optionLevel.param++;
                        }
//                        if (optionLevel != null && optionLevel.param >= 5) {
//                            ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa nâng cấp "
//                                    + "thành công " + trangBi.template.name + " lên +" + optionLevel.param);
//                        }
                        sendEffectSuccessCombine(player);
                    } else {
                        if ((level == 2 || level == 4 || level == 6) && (player.combineNew.itemsCombine.size() != 3)) {
                            option.param -= (option.param * 15 / 100);
                            if (option2 != null) {
                                option2.param -= (option2.param * 15 / 100);
                            }
                            optionLevel.param--;
                        }
                        sendEffectFailCombine(player);
                    }
                    if (player.combineNew.itemsCombine.size() == 3) {
                        InventoryService.gI().subQuantityItemsBag(player, itemDBV, countDaBaoVe);
                    }
                    InventoryService.gI().subQuantityItemsBag(player, itemDNC, player.combineNew.countDaNangCap);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void phapsuhoa(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                Item dangusac = player.combineNew.itemsCombine.get(1);
                int star = 0;
                short[] chiso = {229, 230, 231, 232};
                byte randomDo = (byte) new Random().nextInt(chiso.length);
                int lvchiso = 0;
                int cap = 1;
                ItemOption optionStar = null;
                int check = chiso[randomDo];
                int run = 0;
                int lvcheck = 0;

                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 229 || io.optionTemplate.id == 230 || io.optionTemplate.id == 231 || io.optionTemplate.id == 232) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }

                for (ItemOption io2 : item.itemOptions) {
                    if (io2.optionTemplate.id == 233) {
                        lvcheck = io2.param;
                        break;
                    }
                }

                if (item != null && item.isNotNullItem() && dangusac != null && dangusac.isNotNullItem() && (dangusac.template.id == 1235) && dangusac.quantity >= 1) {
                    if (lvcheck < 12) {
                        if (optionStar == null) {
                            item.itemOptions.add(new ItemOption(233, cap));
                            if (check == 232) {
                                item.itemOptions.add(new ItemOption(check, lvchiso + 1));
                            } else {
                                item.itemOptions.add(new ItemOption(check, lvchiso + 2));
                            }
                            sendEffectSuccessCombine(player);
                            InventoryService.gI().subQuantityItemsBag(player, dangusac, 1);
                            InventoryService.gI().sendItemBags(player);
                            reOpenItemCombine(player);
                        } else {

                            for (ItemOption ioo : item.itemOptions) {
                                if (ioo.optionTemplate.id == 233) {
                                    ioo.param++;
                                }
                                if ((ioo.optionTemplate.id == 229 || ioo.optionTemplate.id == 230 || ioo.optionTemplate.id == 231 || ioo.optionTemplate.id == 232) && (ioo.optionTemplate.id == check)) {
                                    if (check == 232) {
                                        ioo.param += 1;
                                    } else {
                                        ioo.param += 2;
                                    }
                                    sendEffectSuccessCombine(player);
                                    InventoryService.gI().subQuantityItemsBag(player, dangusac, 1);
                                    InventoryService.gI().sendItemBags(player);
                                    reOpenItemCombine(player);
                                    run = 1;
                                    break;
                                } else {
                                    run = 2;
                                }
                            }

                            if (run == 2) {
                                if (check == 232) {
                                    item.itemOptions.add(new ItemOption(check, lvchiso + 1));
                                } else {
                                    item.itemOptions.add(new ItemOption(check, lvchiso + 2));
                                }
                                sendEffectSuccessCombine(player);
                                InventoryService.gI().subQuantityItemsBag(player, dangusac, 1);
                                InventoryService.gI().sendItemBags(player);
                                reOpenItemCombine(player);
                            }
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Pháp sư hóa đã đạt cấp cao nhất !!!");
                    }
                }
            }
        }
    }

    private void linhHoaTrangBi(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                Item dangusac = player.combineNew.itemsCombine.get(1);
                Item xuVang = InventoryService.gI().findItemBagByTemp(player, 1568);
                if (xuVang == null || xuVang.quantity < 1000) {
                    Service.getInstance().sendThongBaoOK(player, "hãy chuẩn bị 1000 xu vàng phí linh hoá");
                    return;
                }
                int star = 0;
                short[] chiso = {224, 225, 226};
                byte randomDo = (byte) new Random().nextInt(chiso.length);
                int lvchiso = Util.nextInt(5000, 10000);
                int cap = 1;
                ItemOption optionStar = null;
                int check = chiso[randomDo];
                int lvcheck = 0;

                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 224 || io.optionTemplate.id == 225 || io.optionTemplate.id == 226) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }

                for (ItemOption io2 : item.itemOptions) {
                    if (io2.optionTemplate.id == 227) {
                        lvcheck = io2.param + 1;
                        break;
                    }
                }

                if (item != null && item.isNotNullItem() && dangusac != null && dangusac.isNotNullItem() && (dangusac.template.id == 698) && dangusac.quantity >= lvcheck) {
                    if (lvcheck < 99) {
                        if (optionStar == null) {
                            item.itemOptions.add(new ItemOption(227, cap));
                            if (check == 224) {
                                item.itemOptions.add(new ItemOption(check, lvchiso));
                            } else {
                                item.itemOptions.add(new ItemOption(check, lvchiso * 10));
                            }
                            sendEffectSuccessCombine(player);
                            InventoryService.gI().subQuantityItemsBag(player, xuVang, 1000);
                            InventoryService.gI().subQuantityItemsBag(player, dangusac, cap);
                            InventoryService.gI().sendItemBags(player);
                            reOpenItemCombine(player);
                        } else {
                            for (ItemOption ioo : item.itemOptions) {
                                if (ioo.optionTemplate.id == 227) {
                                    ioo.param++;
                                }
                                if ((ioo.optionTemplate.id == 224 || ioo.optionTemplate.id == 225 || ioo.optionTemplate.id == 226) && (ioo.optionTemplate.id == check)) {
                                    if (check == 224) {
                                        ioo.param += lvchiso;
                                    } else {
                                        ioo.param += lvchiso * 10;
                                    }
                                    sendEffectSuccessCombine(player);
                                    InventoryService.gI().subQuantityItemsBag(player, xuVang, 1000);
                                    InventoryService.gI().subQuantityItemsBag(player, dangusac, lvcheck);
                                    InventoryService.gI().sendItemBags(player);
                                    reOpenItemCombine(player);
                                    break;
                                }
                            }
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Pháp sư hóa đã đạt cấp cao nhất !!!");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không đủ linh hoả. Nâng cấp độ này cần " + cap + " linh hoả");
                    return;
                }
            }
        }
    }

    private void tayphapsu(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                Item dangusac = player.combineNew.itemsCombine.get(1);
                ItemOption optionStar = null;

                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 229 || io.optionTemplate.id == 230 || io.optionTemplate.id == 231 || io.optionTemplate.id == 232 || io.optionTemplate.id == 233) {
                        optionStar = io;
                        break;
                    }
                }

                if (item != null && item.isNotNullItem() && dangusac != null && dangusac.isNotNullItem() && (dangusac.template.id == 1236) && dangusac.quantity >= 1) {
                    if (optionStar == null) {
                        Service.getInstance().sendThongBao(player, "Có gì đâu mà tẩy !!!");
                    } else {

                        if (item.itemOptions != null) {

                            Iterator<ItemOption> iterator = item.itemOptions.iterator();
                            while (iterator.hasNext()) {
                                ItemOption ioo = iterator.next();
                                if (ioo.optionTemplate.id == 229 || ioo.optionTemplate.id == 230 || ioo.optionTemplate.id == 231 || ioo.optionTemplate.id == 232 || ioo.optionTemplate.id == 233) {
                                    iterator.remove();
                                }
                            }

                        }
                        //item.itemOptions.add(new ItemOption(73 , 1));  
                        sendEffectSuccessCombine(player);
                        InventoryService.gI().subQuantityItemsBag(player, dangusac, 1);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);

                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Thiếu vật phẩm gòi !!!");
                }

            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * r
     * Hiệu ứng mở item
     *
     * @param player
     * @param icon1
     * @param icon2
     */
    public void sendEffectOpenItem(Player player, short icon1, short icon2) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_ITEM);
            msg.writer().writeShort(icon1);
            msg.writer().writeShort(icon2);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiệu ứng đập đồ thành công
     *
     * @param player
     */
    private void sendEffectSuccessCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_SUCCESS);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiệu ứng đập đồ thất bại
     *
     * @param player
     */
    private void sendEffectFailCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_FAIL);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Gửi lại danh sách đồ trong tab combine
     *
     * @param player
     */
    private void reOpenItemCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(REOPEN_TAB_COMBINE);
            msg.writer().writeByte(player.combineNew.itemsCombine.size());
            for (Item it : player.combineNew.itemsCombine) {
                for (int j = 0; j < player.inventory.itemsBag.size(); j++) {
                    if (it == player.inventory.itemsBag.get(j)) {
                        msg.writer().writeByte(j);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiệu ứng ghép ngọc rồng
     *
     * @param player
     * @param icon
     */
    private void sendEffectCombineDB(Player player, short icon) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_DRAGON_BALL);
            msg.writer().writeShort(icon);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //--------------------------------------------------Chân mệnh/////
    private int getDiemNangcapChanmenh(int star) {
        switch (star) {
            case 0:
                return 500;
            case 1:
                return 1000;
            case 2:
                return 2000;
            case 3:
                return 2500;
            case 4:
                return 3000;
            case 5:
                return 4000;
            case 6:
                return 5000;
            case 7:
                return 10000;
        }
        return 0;
    }

    private int getDaNangcapChanmenh(int star) {
        switch (star) {
            case 0:
                return 20;
            case 1:
                return 30;
            case 2:
                return 40;
            case 3:
                return 50;
            case 4:
                return 60;
            case 5:
                return 70;
            case 6:
                return 80;
            case 7:
                return 100;
        }
        return 0;
    }

    private float getTiLeNangcapChanmenh(int star) {
        switch (star) {
            case 0:
                return 80f;
            case 1:
                return 60f;
            case 2:
                return 40f;
            case 3:
                return 20f;
            case 4:
                return 10f;
            case 5:
                return 5f;
            case 6:
                return 2f;
            case 7:
                return 1f;
        }
        return 0;
    }

    //--------------------------------------------------------------------------Ratio, cost combine
    private int getGoldPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 100;
            case 1:
                return 200;
            case 2:
                return 400;
            case 3:
                return 600;
            case 4:
                return 800;
            case 5:
                return 1000;
            case 6:
                return 1500;
            case 7:
                return 2000;
            case 8:
                return 2500;
            case 9:
                return 3000;// mốc 10 sao
            case 10:
                return 3500;
            case 11:
                return 20000;
            case 12:
                return 30000;
            case 13:
                return 40000;
            case 14:
                return 50000;
        }
        return 0;
    }

    private float getRatioPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 80;
            case 1:
                return 70;
            case 2:
                return 60;
            case 3:
                return 50;
            case 4:
                return 40;
            case 5:
                return 30;
            case 6:
                return 20;
            case 7:
                return 10;
            case 8:
                return 5;
            case 9:
                return 3;
            case 10:
                return 10;
            case 11:
                return 100;
            case 12:
                return 100;
            case 13:
                return 100;
            case 14:
                return 100;
        }

        return 0;
    }

    private int getGemPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 1000;
            case 1:
                return 1000;
            case 2:
                return 1000;
            case 3:
                return 1000;
            case 4:
                return 1000;
            case 5:
                return 1000;
            case 6:
                return 1000;
            case 7:
                return 1000;
            case 8:
                return 1000;
            case 9:
                return 1000;
            case 10:
                return 1000;
            case 11:
                return 1000;
            case 12:
                return 1000;
            case 13:
                return 1000;
            case 14:
                return 1000;
        }
        return 0;
    }

    private int getGemEpSao(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 50;
            case 3:
                return 100;
            case 4:
                return 250;
            case 5:
                return 500;
            case 6:
                return 1000;
            case 7:
                return 2000;
        }
        return 0;
    }

    private double getTileNangCapDo(int level) {
        switch (level) {
            case 0:
                return 95;
            case 1:
                return 80;
            case 2:
                return 40;
            case 3:
                return 60;
            case 4:
                return 50;
            case 5:
                return 10;
            case 6:
                return 1;
            case 7: // 7 sao
                return 0.5f;// 0.5f;
        }
        return 0;
    }

    private int getCountDaNangCapDo(int level) {
        switch (level) {
            case 0:
                return 5;
            case 1:
                return 10;
            case 2:
                return 15;
            case 3:
                return 20;
            case 4:
                return 25;
            case 5:
                return 30;
            case 6:
                return 35;
            case 7:
                return 50;
        }
        return 0;
    }

    private int getCountDaBaoVe(int level) {
        return level + 2;
    }

    private int getGoldNangCapDo(int level) {
        switch (level) {
            case 0:
                return 10000000;
            case 1:
                return 17000000;
            case 2:
                return 30000000;
            case 3:
                return 40000000;
            case 4:
                return 70000000;
            case 5:
                return 80000000;
            case 6:
                return 100000000;
            case 7:
                return 250000000;
        }
        return 0;
    }

    //--------------------------------------------------------------------------check
    private boolean isCoupleItemNangCap(Item item1, Item item2) {
        Item trangBi = null;
        Item daNangCap = null;
        if (item1 != null && item1.isNotNullItem()) {
            if (item1.template.type < 5) {
                trangBi = item1;
            } else if (item1.template.type == 14) {
                daNangCap = item1;
            }
        }
        if (item2 != null && item2.isNotNullItem()) {
            if (item2.template.type < 5) {
                trangBi = item2;
            } else if (item2.template.type == 14) {
                daNangCap = item2;
            }
        }
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 220) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isCoupleItemNangCapCheck(Item trangBi, Item daNangCap) {
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 220) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean issachTuyetKy(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.type == 36) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDaPhaLe(Item item) {
        return item != null && (item.template.type == 30
                || (item.template.id >= 14 && item.template.id <= 20)
                || (item.template.id >= 1185 && item.template.id <= 1191)
                || (item.template.id >= 1692 && item.template.id <= 1694)
                || (item.template.id >= 1721 && item.template.id <= 1724)
                || (item.template.id >= 1737 && item.template.id <= 1748));
    }

    private boolean isTrangBiPhaLeHoa(Item item) {
        if (item != null && item.isNotNullItem()) {
            if ((item.template.type < 5 || item.template.type == 32 || item.template.type == 5 && !item.isTrangBiHSD())) {// && !item.isTrangBiHSD()
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isTrangBiTaySPL(Item item) {
        if (item != null && item.isNotNullItem()) {
            if ((item.template.type < 5 || item.template.type == 5 || item.template.id == 1558 || item.template.id == 1570 || item.template.id == 1571 || item.template.id == 1572)) {// && !item.isTrangBiHSD()
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isTrangBiAn(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id >= 1048 && item.template.id <= 1062
                    || item.template.id >= 1401 && item.template.id <= 1405) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isTrangBiPhapsu(Item item) {
        if (item != null && item.isNotNullItem()) {
            if ((item.template.type == 5 //cải trang
                    || item.template.type == 11
                    || item.template.type == 72
                    || item.template.type == 21
                    || item.template.type == 24
                    || item.template.type == 71
                    || item.template.type == 74
                    || item.template.type == 23
                    || item.template.type == 76
                    || ItemData.list_dapdo.contains((int) item.template.id)) && !item.isTrangBiHSD()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static final int IO_LINH_HOA_ID = 72;
    private static final int LINH_HOA_LEVEL_REQUIRED = 8;

    private boolean isTrangBiLinhHoa(Item item) {
        if (item == null || !item.isNotNullItem()) {
            return false;
        }
        if (item.template == null) {
            return false;
        }

        int type = item.template.type;
        if (type < 0 || type > 4) {
            return false;
        }

        if (item.isTrangBiHSD()) {
            return false;
        }

        if (item.itemOptions == null) {
            return false;
        }

        for (ItemOption io : item.itemOptions) {
            if (io != null && io.optionTemplate != null
                    && io.optionTemplate.id == IO_LINH_HOA_ID
                    && io.param == LINH_HOA_LEVEL_REQUIRED) {
                return true;
            }
        }
        return false;
    }

    private int getParamDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).param;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 5; // +5%hp
            case 19:
                return 5; // +5%ki
            case 18:
                return 5; // +5%hp/30s
            case 17:
                return 5; // +5%ki/30s
            case 16:
                return 3; // +3%sđ
            case 15:
                return 2; // +2%giáp
            case 14:
                return 2; // +2%né đòn
            case 1187:
                return 4; // +4%sđ
            case 1185:
                return 2; // +2%cm
            case 1190:
                return 7; // +7%ki
            case 1191:
                return 7; // +7%hp
            case 1480:
                return 5; // +5%hp
            case 1481:
                return 8; // +8%hp
            case 1482:
                return 8; // +8%hp
            case 1692:
                return 15; // +12%hp
            case 1693:
                return 15; // +12%Ki
            case 1694:
                return 10; // +8%SD
            case 1721:
                return 15; // +15% Gold
            case 1722:
                return 15; // +15% Tnsm
            case 1723:
                return 30; // +15% Gold
            case 1724:
                return 30; // +15% Tnsm
            case 1737:
                return 30;
            case 1738:
                return 30;
            case 1739:
                return 30;
            case 1740:
                return 50;
            case 1741:
                return 50;
            case 1742:
                return 50;
            case 1743:
                return 70;
            case 1744:
                return 70;
            case 1745:
                return 70;
            case 1746:
                return 100;
            case 1747:
                return 100;
            case 1748:
                return 100;
            default:
                return -1;
        }
    }

    private int getOptionDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).optionTemplate.id;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 210;
            case 19:
                return 211;
            case 18:
                return 212;
            case 17:
                return 213;
            case 16:
                return 49;
            case 15:
                return 214;
            case 14:
                return 215;
            case 1187:
                return 49; //sd
            case 1185:
                return 216; //chi mang
            case 1190:
                return 211; //ki
            case 1191:
                return 210; //hp
            case 1480:
                return 49; // +5%hp
            case 1481:
                return 211; // +8%hp
            case 1482:
                return 210; // +8%hp
            case 1692:
                return 210; // +12%hp
            case 1693:
                return 211; // +12%Ki
            case 1694:
                return 49; // +8%SD
            case 1721:
                return 222; // +15% Gold
            case 1722:
                return 223; // +15% Tnsm
            case 1723:
                return 222; // +15% Gold
            case 1724:
                return 223; // +15% Tnsm
            case 1737:
                return 210;//hp
            case 1738:
                return 211;//ki
            case 1739:
                return 49;//sức đánh
            case 1740:
                return 210;//hp
            case 1741:
                return 211;//ki
            case 1742:
                return 49;//sức đánh
            case 1743:
                return 210;//hp
            case 1744:
                return 211;//ki
            case 1745:
                return 49;//sức đánh
            case 1746:
                return 210;//hp
            case 1747:
                return 211;//ki
            case 1748:
                return 49;//sức đánh
            default:
                return -1;
        }
    }

    /**
     * Trả về id item c0
     *
     * @param gender
     * @param type
     * @return
     */
    private int getTempIdItemC0(int gender, int type) {
        if (type == 4) {
            return 12;
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return 0;
                    case 1:
                        return 6;
                    case 2:
                        return 21;
                    case 3:
                        return 27;
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return 1;
                    case 1:
                        return 7;
                    case 2:
                        return 22;
                    case 3:
                        return 28;
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return 2;
                    case 1:
                        return 8;
                    case 2:
                        return 23;
                    case 3:
                        return 29;
                }
                break;
        }
        return -1;
    }

    //Trả về tên đồ c0
    private String getNameItemC0(int gender, int type) {
        if (type == 4) {
            return "Rada cấp 1";
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return "Áo vải 3 lỗ";
                    case 1:
                        return "Quần vải đen";
                    case 2:
                        return "Găng thun đen";
                    case 3:
                        return "Giầy nhựa";
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return "Áo sợi len";
                    case 1:
                        return "Quần sợi len";
                    case 2:
                        return "Găng sợi len";
                    case 3:
                        return "Giầy sợi len";
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return "Áo vải thô";
                    case 1:
                        return "Quần vải thô";
                    case 2:
                        return "Găng vải thô";
                    case 3:
                        return "Giầy vải thô";
                }
                break;
        }
        return "";
    }

    //--------------------------------------------------------------------------Text tab combine
    private String getTextTopTabCombine(int type) {
        switch (type) {
            case EP_SAO_TRANG_BI:
                return "Ép SPL hoặc Ngọc rồng vào trang bị đã đục lỗ";
            case PHA_LE_HOA_TRANG_BI:
                return "Đục lỗ trang bị";
            case TAY_SAO_PHA_LE:
                return "Đục lỗ trang bị";
            case AN_TRANG_BI:
                return "Nâng cấp\trang bị kích Ấn";
            case LONG_AN_TRANG_BI:
                return "Nâng cấp\trang bị Long Ấn";
            case NHAP_NGOC_RONG:
                return "Nâng cấp\ncho viên Ngọc Rồng cấp thấp\nthành 1 viên Ngọc Rồng cấp cao";
            case NANG_CAP_VAT_PHAM:
                return "Ta bào của nhà ngươi một khúc";
            case NANG_CAP_BONG_TAI:
                return "Nâng cấp\ncho bông tai Porata của ngươi\nthành Bông tai cấp cao hơn 1 bậc";
            case MO_CHI_SO_BONG_TAI:
                return "Nâng cấp\ncho bông tai Porata cấp 2,3,4 của ngươi\ncó 1 chỉ số ngẫu nhiên";
            case PHAN_RA_DO_THAN_LINH:
                return "Ta sẽ phân rã \n  trang bị của người thành điểm!";
            case CHUYEN_HOA_DO_HUY_DIET:
                return "Ta sẽ phân rã \n  trang bị Hủy diệt của ngươi\nthành Phiếu hủy diệt!";
            case PHAN_RA_DO_TS:
                return "Ta sẽ phân rã \n  trang bị Thiên sứ của ngươi\nthành 500 mảnh thiên sứ cùng hệ!";
            case NANG_CAP_DO_TS:
                return "Ta sẽ nâng cấp \n  trang bị của người thành\n đồ thiên sứ!";
            case CHE_TAO_THANH_TON:
                return "Ta sẽ chế tạo \n  cho ngươi ngẫu nhiên một món\n đồ thánh tôn!";
            case NANG_SKH_THANH_TON:
                return "NÂNG ĐỒ THÀNH TÔN\n -> TRANG BỊ SKH THÁNH TÔN";
            case THU_HOI_DO_TL:
                return "THU MUA VẬT PHẨM";
            case NANG_CAP_SKH_TS:
                return "Thiên sứ nhờ ta nâng cấp \n  trang bị của người thành\n SKH VIP!";
            case NANG_CAP_SKH_VAI_THO:
                return "Ta sẽ giúp người nâng cấp\n  trang bị của người thành\n SKH Ngẫu nhiên!";
            case NANG_CAP_TRANG_BI:
                return "Ta sẽ giúp nhà ngươi nâng cấp \n  trang bị SKH \n Tăng thêm một cấp độ";
            case NANG_CAP_THAN_LINH:
                return "Ta sẽ nâng cấp \n trang bị Thần linh của ngươi\n thành món Hủy diệt Tương ứng!";
            case NANG_CAP_KIM_DAN:
                return "NÂNG CẤP KIM ĐAN";
            case TANG_PHAM_CHAT_KIM_DAN:
                return "TĂNG PHẨM CHẤT KIM ĐAN";
            case PHAP_SU_HOA:
                return "LINH HOÁ TRANG BỊ\n"
                        + "Tăng Mạnh Chỉ Số";
            case TAY_PHAP_SU:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở về lúc chưa 'Pháp sư hóa'";
            case NANG_CAP_CHAN_MENH:
                return "Ta sẽ Nâng cấp\nChân Mệnh của ngươi\ncao hơn một bậc";
            case GIA_HAN_VAT_PHAM:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\nthêm hạn sử dụng";
            // START_ SÁCH TUYỆT KỸ //
            case GIAM_DINH_SACH:
                return "Ta sẽ giám định\nSách Tuyệt Kỹ cho ngươi";
            case TAY_SACH:
                return "Ta sẽ phù phép\ntẩy sách đó cho ngươi";
            case NANG_CAP_SACH_TUYET_KY:
                return "Ta sẽ nâng cấp\nSách Tuyệt Kỹ cho ngươi";
            case PHUC_HOI_SACH:
                return "Ta sẽ phục hồi\nsách cho ngươi";
            case PHAN_RA_SACH:
                return "Ta sẽ phân rã\nsách cho ngươi";
            // END _ SÁCH TUYỆT KỸ //
            default:
                return "";
        }
    }

    private String getTextInfoTabCombine(int type) {
        switch (type) {
            case EP_SAO_TRANG_BI:
                return "Chọn trang bị\n"
                        + "(Áo, quần, găng, giày hoặc rađa) có ô đặt sao pha lê\n"
                        + "Chọn loại sao pha lê\n"
                        + "Sau đó chọn 'Nâng cấp'";
            case PHA_LE_HOA_TRANG_BI:
                return "Các trang bị có thể nâng cấp\n"
                        + "(Áo, quần, găng, giày\n"
                        + ", rađa hoặc Cải trang)\n"
                        + "Max 15 sao";
            case AN_TRANG_BI:
                return "Các trang bị có thể kích ấn\n"
                        + "THIÊN SỨ và THÁNH TÔN\n"
                        + "Cần có đồ và 99 mảnh Ấn\n"
                        + "-Tinh ấn (5 món +40%HP)\n"
                        + "-Nhật ấn (5 món +40%KI\n"
                        + "-Nguyệt ấn (5 món +30%SD)";
            case TAY_SAO_PHA_LE:
                return "Tẩy sẽ mất SPL và Chỉ số SPL\n"
                        + "Lỗ đục được giữ nguyên\n"
                        + "Tẩy một phát là bay màu\n"
                        + "Lưu ý:\n"
                        + "-Del trả spl cũ\n";
            case LONG_AN_TRANG_BI:
                return "Các trang bị có thể kích ấn\n"
                        + "THIÊN SỨ và THÁNH TÔN đã kích ấn\n"
                        + "Và 555 Mảnh long ấn\n"
                        + "-Huyết Long ấn (5 món +100%HP)\n"
                        + "-Thanh Long ấn (5 món +100%KI\n"
                        + "-Kim Long ấn (5 món +80%SD)";
            case NHAP_NGOC_RONG:
                return "Nạp tiền mà mua\n"
                        + "Ép làm cc gì mất công\n";
            case NANG_CAP_VAT_PHAM:
                return "Cấp tối đa là 8\n"
                        + "Mày có thể ước rồng lên 7\n"
                        + "Còn muốn đập 8 thì tìm tao\n"
                        + "Đập 8 tốn khoảng chục k đá\n"
                        + "Đi thu mua của những thằng khác"
                        + "Ít quá đập del lên đâu chứ ko phải đen";
            case NANG_CAP_BONG_TAI:
                return "Vào hành trang\n"
                        + "Chọn bông tai Porata 1, 2, 3 hoặc 4\n"
                        + "Chọn mảnh bông tai để nâng cấp(Số lượng: 9999 hoặc 19999)\n"
                        + "Sau đó chọn 'Nâng cấp'\n"
                        + "Nếu nâng cấp 2 3 4 thất bại sẽ bị trừ đi 99 Mảnh bông tai\n"
                        + "Nếu nâng Hợp thể siêu nhân (C5) thất bại trừ 999 Mảnh bông tai\n"
                        + "Sau khi thành công Bông tai của ngươi sẽ tăng 1 bậc";
            case MO_CHI_SO_BONG_TAI:
                return "Sever này có nhiều thứ giống bông tai\n"
                        + "Cứ giúp mày hợp thể được thì mở chỉ số được\n"
                        + "Mở chỉ số cần 99 Mảnh hồn và\n"
                        + "1 đá xanh lam\n"
                        + "Mảnh hồn fam ở map 4 thánh địa bang\n"
                        + "đá xanh lam mua tạp hóa\n"
                        + "Đập mỗi nhát 2 tỏi";
            case PHAN_RA_DO_THAN_LINH:
                return "Vào hành trang\n"
                        + "Chọn trang bị\n"
                        + "(Áo, quần, găng, giày hoặc rađa)\n"
                        + "Chọn loại đá để phân rã\n"
                        + "Sau đó chọn 'Phân Rã'";
            case CHUYEN_HOA_DO_HUY_DIET:
                return "Vào hành trang\n"
                        + "Chọn trang bị\n"
                        + "(Áo, quần, găng, giày hoặc rađa) Hủy diệt\n"
                        + "Sau đó chọn 'Chuyển hóa'";
            case PHAN_RA_DO_TS:
                return "Vào hành trang\n"
                        + "Chọn trang bị\n"
                        + "(Áo, quần, găng, giày hoặc nhẫn) Thiên sứ\n"
                        + "Sau đó chọn 'Chuyển hóa'";
            case NANG_CAP_DO_TS:
                return "Vào hành trang\n"
                        + "Chọn 1 Công thức theo Hành tinh + 1 Đá cầu vòng\n"
                        + " và 999 mảnh thiên sứ\n "
                        + "sẽ cho ra đồ thiên sứ từ 0-15% chỉ số\n"
                        + "(Có tỉ lệ thêm dòng chỉ số ẩn)\n"
                        + "Sau đó chọn 'Nâng Cấp'";
            case CHE_TAO_THANH_TON:
                return "Nguyên liệu cần thiết:\n"
                        + "1 Sách Thiên Tử Hoặc Ma Thần Mua shop Bằng 999 Xu Vàng\n"
                        + "1 Hoả Long thạch - Săn boss Sói Hoặc Đại Thánh\n"
                        + "999 Nguyên tố bí ẩn\n"
                        + "Săn boss Đại Thánh, Sói 3 Đầu, Hoặc boss ẩn\n"
                        + "(Có tỉ lệ thêm dòng chỉ số ẩn)\n"
                        + "Thông tin cập nhật tại TeleGram";
            case NANG_SKH_THANH_TON:
                return "Nguyên liệu cần thiết:\n"
                        + "1 TRANG BỊ THÁNH TÔN\n"
                        + "1 TRANG BỊ THIÊN SỨ KÍCH HOẠT\n"
                        + "1 THẦN LONG THẠCH\n"
                        + "(săn các boss vip có tỉ lệ nhỏ rơi)\n"
                        + "(trang bị chỉ số x10 đồ tt thường)\n"
                        + "chắc chắn nhận chỉ số ẩn";
            case THU_HOI_DO_TL:
                return "Thu mua lại các loại trang bị\n"
                        + "!!!Lưu ý mỗi lần thu hồi 1 món\n\n"
                        + "Đồ Thần Linh 100 coin\n"
                        + "Đồ Huỷ Diệt 200 coin\n"
                        + "Đồ Thiên Sứ 300 coin\n"
                        + "Đồ Thánh Tôn 500 coin\n\n"
                        + "Cân nhắc kĩ trước khi đồng ý\n"
                        + "(Phí thu hồi 5b vàng)\n";
            case NANG_CAP_SKH_TS:
                return "Nguyên liệu cần thiết:\n"
                        + "3 Món đồ thiên sứ bất kì\n"
                        + "Ngẫu nhiên ra đồ Thần Linh - Hủy diệt\n"
                        + "Thiên sứ - Và set kích hoạt ngẫu nhiên"
                        + "Đồ nhận cùng hành tinh với món đầu tiên\n"
                        + "Ví dụ: Mày đặt vào Găng TS Nm"
                        + "Thì mày sẽ nhận ngẫu nhiên"
                        + "Găng TL-HD-TS Nm với SKH ngẫu nhiên"
                        + "Chúc may mắn";
            case NANG_CAP_SKH_VAI_THO:
                return "Nguyên liệu cần thiết:\n"
                        + "3 Món Đồ Huỷ Diệt\n"
                        + "Đồ nhận về cùng hành tinh "
                        + "và loại với món đồ bỏ vào đầu tiên\n"
                        + "Đồ nhận được sẽ SKH Vải thô\n";
            case NANG_CAP_TRANG_BI:
                return "Nguyên liệu cần thiết:\n"
                        + "1 Món Đồ SKH hoặc\n"
                        + "1 Phụng Thiên Kích Hoặc\n"
                        + "1 Cải trang Thân Xác Đại Thánh (Đệ)\n"
                        + "Và một đá cường hoá cùng cấp\n"
                        + "Đồ nhận được sẽ Tăng thêm 1 cấp\n"
                        + "Phí nâng cấp 500tr vàng\n";
            case NANG_CAP_THAN_LINH:
                return "Nguyên liệu cần thiết:\n"
                        + "Cần đúng 1 món thần linh\n"
                        + "Đồ Hủy diệt sẽ cùng loại và\n"
                        + "Cùng hành tinh của món mày bỏ vào\n"
                        + "Phí là 2 Tỉ Vàng";
            case NANG_CAP_KIM_DAN:
                return "NÂNG CẤP CẦN\n"
                        + "Cần dưới cấp 10 cần x99 Đá cường hoá\n"
                        + "Trên cấp 10 cần 9 viên đá cường hoá\n"
                        + "Chỉ số tăng theo cấp độ\n"
                        + "Phẩm chất kim đan càng tốt\n"
                        + "Chỉ số phụ thuộc vào phẩm chất kim đan";
            case TANG_PHAM_CHAT_KIM_DAN:
                return "NÂNG CẤP CẦN\n"
                        + "9 HOẢ LONG THẠCH\n"
                        + "999.999 THỎI VÀNG\n"
                        + "1% Nhận kim đan hoàn mĩ\n"
                        + "10% Nhận kim đan cao cấp\n"
                        + "50% Nhận kim đan trung cấp\n"
                        + "Kim đan sơ cấp kiếm từ sự kiện trung thu";
            case PHAP_SU_HOA:
                return "Những đồ có thể pháp sư:\n"
                        + "Pet,Ngọc Bội,Danh hiệu,Linh thú,Cải trang\n"
                        + "Thú Cưỡi, Chân Mệnh\n"
                        + "Tối Đa Cấp 12\n"
                        + "Nếu chưa ưng ý có thể ra tẩy lại\n"
                        + "Chỉ số sức đánh chí mạng ngon nhất"
                        + "Good Luck";
            case LINH_HOA_TRANG_BI:
                return "Những đồ có thể linh hoá:\n"
                        + "Áo, quần, giày, găng, nhẫn, rada\n"
                        + "Tối Đa 99 lần mỗi trang bị\n"
                        + "Chỉ số ngẫu nhiên sd, hp, ki\n"
                        + "Chỉ số 5 -> 10k sd, hpki x10"
                        + "Không thể tẩy bỏ chỉ số\n"
                        + "Chúc các bạn may mắn";
            case TAY_PHAP_SU:
                return "Nếu mày chưa ưng ý với chỉ số PS\n"
                        + "Vào đây tẩy và làm lại\n"
                        + "Tẩy một phát là bay hết tất cả dòng\n"
                        + "Suy nghĩ trước khi tẩy\n"
                        + "Tẩy xong chắc gì đã ra ngon hơn"
                        + "Good Luck";
            case NANG_CAP_CHAN_MENH:
                return "Mảnh Chân Mệnh rơi ở Map fam chân mệnh\n"
                        + "Cấp chân mệnh tối đa là 9\n"
                        + "Sẽ mất khoảng 5-20k Mảnh Chân Mệnh\n"
                        + "Dùng lệnh dapdo để đập nhanh hơn\n"
                        + "Mỗi cấp sẽ tăng 3pt sức dánh và 4pt Ki-Hp";
            case GIA_HAN_VAT_PHAM:
                return "Thẻ gia hạn fam ở CDRD cấp trên 100\n"
                        + "Mỗi thẻ gia hạn tăng thêm 1 ngày sử dụng\n"
                        + "Những đồ có dòng ko thể gia hạn\n"
                        + "Thì không thể gia hạn";
            // START_ SÁCH TUYỆT KỸ //
            case GIAM_DINH_SACH:
                return "Giám định để ra một chỉ số ngẫu nhiên\n"
                        + "Cần có sách và bùa giám định"
                        + "\n Chế tạo sách cần 10 sách cũ"
                        + "\n Sách cũ mua shop nghé con ở map Lasvegas"
                        + "\n Bật capsule lên là thấy map ở cuối cùng";
            case TAY_SACH:
                return "Chỉ số chưa ưng ý\nThì cứ tẩy thôi";
            case NANG_CAP_SACH_TUYET_KY:
                return "Muốn nâng thì mày phải có sách đã\n"
                        + "\n Chế tạo sách cần 10 sách cũ"
                        + "\n Và kìm bấm giấy"
                        + "\n Sách cũ mua shop nghé con ở map Lasvegas"
                        + "\n Bật capsule lên là thấy map ở cuối cùng";
            case PHUC_HOI_SACH:
                return "Tẩy nhiều quá thì sách nó rách\n"
                        + "Mà lỡ rách rồi thì phục hồi"
                        + "\n Sách cũ mua shop nghé con ở map Lasvegas"
                        + "\n Bật capsule lên là thấy map ở cuối cùng";
            case PHAN_RA_SACH:
                return "Nếu ko dùng nữa thì đưa tao xé hộ";
            // END _ SÁCH TUYỆT KỸ //
            default:
                return "";
        }
    }
}
