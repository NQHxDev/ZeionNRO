package nro.services.func;

import java.util.Arrays;
import nro.consts.ConstAchive;
import nro.consts.ConstItem;
import nro.consts.ConstNpc;
import nro.jdbc.daos.PlayerDAO;
import nro.models.item.CaiTrang;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.shop.ItemShop;
import nro.models.shop.Shop;
import nro.models.shop.TabShop;
import nro.server.Manager;
import nro.server.io.Message;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.utils.Log;
import nro.utils.Util;

import java.util.List;
import nro.data.ItemData;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class ShopService {

    private static final int COST_GOLD_BAR = 500000000;
    private static final int COST_LOCK_GOLD_BAR = 300000000;

    private static final byte NORMAL_SHOP = 0;
    private static final byte SPEC_SHOP = 3;

    private static ShopService i;

    public static ShopService gI() {
        if (i == null) {
            i = new ShopService();
        }
        return i;
    }

    //Lấy ra itemshop khi mua
    private ItemShop getItemShop(int shopId, int tempId) {
        ItemShop itemShop = null;
        Shop shop = null;
        switch (shopId) {
            case ConstNpc.SHOP_BUNMA_QK_0:
                shop = getShop(ConstNpc.BUNMA, 0, -1);
                break;
            case ConstNpc.SHOP_DENDE_0:
                shop = getShop(ConstNpc.DENDE, 0, -1);
                break;
            case ConstNpc.SHOP_APPULE_0:
                shop = getShop(ConstNpc.APPULE, 0, -1);
                break;
            case ConstNpc.SHOP_URON_0:
                shop = getShop(ConstNpc.URON, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_0:
                shop = getShop(ConstNpc.SANTA, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_1:
                shop = getShop(ConstNpc.SANTA, 1, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_0:
                shop = getShop(ConstNpc.BA_HAT_MIT, 0, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_1:
                shop = getShop(ConstNpc.BA_HAT_MIT, 1, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_2:
                shop = getShop(ConstNpc.BA_HAT_MIT, 2, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_3:
                shop = getShop(ConstNpc.BA_HAT_MIT, 3, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_4:
                shop = getShop(ConstNpc.BA_HAT_MIT, 4, -1);
                break;
            case ConstNpc.SHOP_BUNMA_TL_0:
                shop = getShop(ConstNpc.BUNMA_TL, 0, -1);
                break;
            case ConstNpc.SHOP_BILL_HUY_DIET_0:
                shop = getShop(ConstNpc.BILL, 0, -1);
                break;
            case ConstNpc.SHOP_BILL_HUY_DIET_1:
                shop = getShop(ConstNpc.BILL, 1, -1);
                break;
            case ConstNpc.SHOP_WHIS_THIEN_SU:
                shop = getShop(ConstNpc.WHIS, 0, -1);
                break;
//            case ConstNpc.SHOP_HONG_NGOC:
//                shop = getShop(ConstNpc.QUY_LAO_KAME, 0, -1);
//                break;
            case ConstNpc.SHOP_SU_KIEN_TET:
                shop = getShop(ConstNpc.QUY_LAO_KAME, 0, -1);
                break;
            case ConstNpc.SHOP_TORIBOT:
                shop = getShop(ConstNpc.TORIBOT, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_2:
                shop = getShop(ConstNpc.SANTA, 2, -1);
                break;
            case ConstNpc.SHOP_SANTA_3:
                shop = getShop(ConstNpc.SANTA, 3, -1);
                break;
            case ConstNpc.SHOP_SANTA_4:
                shop = getShop(ConstNpc.SANTA, 4, -1);
                break;
            case ConstNpc.SHOP_SANTA_5:
                shop = getShop(ConstNpc.SANTA, 5, -1);
                break;
            case ConstNpc.NGHE_CON_1:
                shop = getShop(ConstNpc.NGHE_CON, 0, -1);
                break;
            case ConstNpc.NGHE_CON_2:
                shop = getShop(ConstNpc.NGHE_CON, 1, -1);
                break;
            case ConstNpc.TRUONG_MY_LAN_1:
                shop = getShop(ConstNpc.TRUONG_MY_LAN, 0, -1);
                break;
            case ConstNpc.TRUONG_MY_LAN_2:
                shop = getShop(ConstNpc.TRUONG_MY_LAN, 1, -1);
                break;
            case ConstNpc.SHOP_TRUONG_LAO_GURU:
                shop = getShop(ConstNpc.TRUONG_LAO_GURU, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_6:
                shop = getShop(ConstNpc.SANTA, 6, -1);
                break;
            case ConstNpc.SHOP_SANTA_7:
                shop = getShop(ConstNpc.SANTA, 7, -1);
                break;
            case ConstNpc.SHOP_THOREN:
                shop = getShop(ConstNpc.THO_REN, 0, -1);
                break;
            case ConstNpc.SHOP_THOREN_1:
                shop = getShop(ConstNpc.THO_REN, 1, -1);
                break;
            case ConstNpc.SHOP_MINUONG:
                shop = getShop(ConstNpc.MI_NUONG, 0, -1);
                break;
            case ConstNpc.HANG_NGA_SHOP:
                shop = getShop(ConstNpc.HANG_NGA, 0, -1);
                break;
            case ConstNpc.HANG_NGA_SHOP2:
                shop = getShop(ConstNpc.HANG_NGA, 1, -1);
                break;
            case ConstNpc.TRUNG_THU_SHOP:
                shop = getShop(ConstNpc.TO_SU_KAIO, 0, -1);
                break;
        }
        if (shop != null) {
            for (TabShop tab : shop.tabShops) {
                for (ItemShop is : tab.itemShops) {
                    if (is.temp.id == tempId) {
                        itemShop = is;
                        break;
                    }
                }
                if (itemShop != null) {
                    break;
                }
            }
        }
        return itemShop;
    }

    private Shop getShop(int npcId, int order, int gender) {
        for (Shop shop : Manager.SHOPS) {
            if (shop.npcId == npcId && shop.shopOrder == order) {
                if (gender != -1) {
                    return new Shop(shop, gender);
                } else {
                    return shop;
                }
            }
        }
        return null;
    }

    private Shop getShopBua(Player player, Shop s) {
        Shop shop = new Shop(s);
        for (TabShop tabShop : shop.tabShops) {
            for (ItemShop item : tabShop.itemShops) {

                long min = 0;
                switch (item.temp.id) {
                    case 213:
                        long timeTriTue = player.charms.tdTriTue;
                        long current = System.currentTimeMillis();
                        min = (timeTriTue - current) / 60000;

                        break;
                    case 214:
                        min = (player.charms.tdManhMe - System.currentTimeMillis()) / 60000;
                        break;
                    case 215:
                        min = (player.charms.tdDaTrau - System.currentTimeMillis()) / 60000;
                        break;
                    case 216:
                        min = (player.charms.tdOaiHung - System.currentTimeMillis()) / 60000;
                        break;
                    case 217:
                        min = (player.charms.tdBatTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 218:
                        min = (player.charms.tdDeoDai - System.currentTimeMillis()) / 60000;
                        break;
                    case 219:
                        min = (player.charms.tdThuHut - System.currentTimeMillis()) / 60000;
                        break;
                    case 522:
                        min = (player.charms.tdDeTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 671:
                        min = (player.charms.tdTriTue3 - System.currentTimeMillis()) / 60000;
                        break;
                    case 672:
                        min = (player.charms.tdTriTue4 - System.currentTimeMillis()) / 60000;
                        break;
                    case 2025:
                        min = (player.charms.tdDeTuMabu - System.currentTimeMillis()) / 60000;
                        break;
                }
                if (min > 0) {
                    item.options.clear();
                    if (min >= 1440) {
                        item.options.add(new ItemOption(63, (int) min / 1440));
                    } else if (min >= 60) {
                        item.options.add(new ItemOption(64, (int) min / 60));
                    } else {
                        item.options.add(new ItemOption(65, (int) min));
                    }
                }
            }
        }
        return shop;
    }

    public void openShopWhisThienSu(Player player, int shopId, int order) {
        Shop shop = getShop(ConstNpc.WHIS, order, -1);
        openShopType3(player, shop, shopId);
    }

    //shop bùa
    public void openShopBua(Player player, int shopId, int order) {
//        player.iDMark.setShopId(shopId);
        Shop shop = getShopBua(player, getShop(ConstNpc.BA_HAT_MIT, order, -1));
        openShopType0(player, shop, shopId);
    }

    //shop normal
    public void openShopNormal(Player player, Npc npc, int shopId, int order, int gender) {
        Shop shop = getShop(npc.tempId, order, gender);
        openShopType0(player, shop, shopId);
    }

    public void openShopSpecial(Player player, Npc npc, int shopId, int order, int gender) {
        Shop shop = getShop(npc.tempId, order, gender);
        openShopType3(player, shop, shopId);
    }

    private void openShopType0(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(NORMAL_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeInt(itemShop.gold);
                        msg.writer().writeInt(itemShop.gem);
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeInt(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        CaiTrang caiTrang = Manager.gI().getCaiTrangByItemId(itemShop.temp.id);
                        msg.writer().writeByte(caiTrang != null ? 1 : 0);
                        if (caiTrang != null) {
                            msg.writer().writeShort(caiTrang.getID()[0]);
                            msg.writer().writeShort(caiTrang.getID()[1]);
                            msg.writer().writeShort(caiTrang.getID()[2]);
                            msg.writer().writeShort(caiTrang.getID()[3]);
                        }
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(ShopService.class, e);
            }
        }
    }

    private void openShopType3(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(SPEC_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    //System.out.println(tab.name);
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeShort(itemShop.iconSpec);
                        msg.writer().writeInt(itemShop.costSpec);
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeInt(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        CaiTrang caiTrang = Manager.gI().getCaiTrangByItemId(itemShop.temp.id);
                        msg.writer().writeByte(caiTrang != null ? 1 : 0);
                        if (caiTrang != null) {
                            msg.writer().writeShort(caiTrang.getID()[0]);
                            msg.writer().writeShort(caiTrang.getID()[1]);
                            msg.writer().writeShort(caiTrang.getID()[2]);
                            msg.writer().writeShort(caiTrang.getID()[3]);
                        }
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
                //System.out.println("sent");
            } catch (Exception e) {
                Log.error(ShopService.class, e);
            }
        }
    }

    private void buyItemShopNormal(Player player, ItemShop is) {
        if (is != null) {
            int itemShopID = is.temp.id;
            if (is.temp.id == 517 && player.inventory.itemsBag.size() >= Manager.MAX_BAG) {
                Service.getInstance().sendThongBao(player, "Hành trang đã đạt tới số lượng tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (is.temp.id == 518 && player.inventory.itemsBox.size() >= Manager.MAX_BOX) {
                Service.getInstance().sendThongBao(player, "rương đồ đã đạt tới số lượng tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (is.temp.id == 988 && player.inventory.getGoldLimit() >= 500000000000L) {
                Service.getInstance().sendThongBao(player, "Giới hạn vàng của bạn đã đạt tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                int gold = is.gold;
                int gem = is.gem;
                int itemExchange = is.itemExchange;
                if (gold != 0) {
                    if (player.inventory.gold >= gold) {
                        player.inventory.gold -= gold;
                    } else {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ vàng, còn thiếu "
                                + (Util.numberToMoney(gold - player.inventory.gold) + " vàng"));
                        Service.getInstance().sendMoney(player);
                        return;
                    }
                }
                if (gem != 0) {
                    if (player.inventory.getGem() >= gem) {
                        player.inventory.subGem(gem);
                    } else {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu "
                                + (gem - player.inventory.getGem()) + " ngọc");
                        Service.getInstance().sendMoney(player);
                        return;
                    }
                }
                if (itemExchange >= 0) {
                    Item itm = InventoryService.gI().findItemBagByTemp(player, itemExchange);
                    if (isLimitItem(itemShopID)) {
                        if (player.buyLimit[itemShopID - 1074] < getBuyLimit(itemShopID)) {
                            player.buyLimit[itemShopID - 1074]++;
                        } else {
                            Service.getInstance().sendThongBao(player, "Số lượt mua trong ngày đã đạt giới hạn");
                            return;
                        }
                    }
                    if (itemExchange == 861 && player.inventory.getRuby() >= is.costSpec) {
                        player.inventory.subRuby(is.costSpec);
                    } else if (itm != null && itm.isNotNullItem() && itm.quantity >= is.costSpec) {
                        InventoryService.gI().subQuantityItemsBag(player, itm, is.costSpec);
                    } else {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ vật phẩm để trao đổi.");
                        return;
                    }
                }
                switch (player.iDMark.getShopId()) {
                    case ConstNpc.SHOP_BA_HAT_MIT_0:
                        player.charms.addTimeCharms(is.temp.id, 60);
                        openShopBua(player, player.iDMark.getShopId(), 0);
                        break;
                    case ConstNpc.SHOP_BA_HAT_MIT_1:
                        player.charms.addTimeCharms(is.temp.id, 60 * 8);
                        openShopBua(player, player.iDMark.getShopId(), 1);
                        break;
                    case ConstNpc.SHOP_BA_HAT_MIT_2:
                        player.charms.addTimeCharms(is.temp.id, 60 * 24 * 30);
                        openShopBua(player, player.iDMark.getShopId(), 2);
                        break;
                    case ConstNpc.SHOP_BA_HAT_MIT_3:
                        player.charms.addTimeCharms(is.temp.id, 60);
                        openShopBua(player, player.iDMark.getShopId(), 3);
                        break;
                    case ConstNpc.SHOP_BILL_HUY_DIET_0:
                        if (player.setClothes.setDTL == 5) {
                            Item meal = InventoryService.gI().findMealChangeDestroyClothes(player);
                            if (meal != null) {
                                Item item = ItemService.gI().createItemFromItemShop(is);
                                int param = 0;
                                if (Util.isTrue(2, 10)) {
                                    param = Util.nextInt(10, 15);
                                } else if (Util.isTrue(3, 10)) {
                                    param = Util.nextInt(0, 10);
                                }
                                for (ItemOption io : item.itemOptions) {
                                    int optId = io.optionTemplate.id;
                                    switch (optId) {
                                        case 47: //giáp
                                        case 6: //hp
                                        case 26: //hp/30s
                                        case 22: //hp k
                                        case 0: //sức đánh
                                        case 7: //ki
                                        case 28: //ki/30s
                                        case 23: //ki k
                                        case 14: //crit
                                            io.param += ((long) io.param * param / 100);
                                            break;
                                    }
                                }
                                InventoryService.gI().subQuantityItemsBag(player, meal, 99);
                                InventoryService.gI().addItemBag(player, item, 99);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Đổi thành công " + is.temp.name);
                            } else {
                                Service.getInstance().sendThongBao(player, "Yêu cầu có 99 thức ăn");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Yêu cầu có đủ trang bị thần linh");
                        }
                        break;
                    case ConstNpc.SHOP_BILL_HUY_DIET_1:
                        if (player.setClothes.setDTL == 5) {
                            Item item = ItemService.gI().createItemFromItemShop(is);
                            int param = 0;
                            if (Util.isTrue(2, 10)) {
                                param = Util.nextInt(10, 15);
                            } else if (Util.isTrue(3, 10)) {
                                param = Util.nextInt(0, 10);
                            }
                            for (ItemOption io : item.itemOptions) {
                                int optId = io.optionTemplate.id;
                                switch (optId) {
                                    case 47: //giáp
                                    case 6: //hp
                                    case 26: //hp/30s
                                    case 22: //hp k
                                    case 0: //sức đánh
                                    case 7: //ki
                                    case 28: //ki/30s
                                    case 23: //ki k
                                    case 14: //crit
                                        io.param += ((long) io.param * param / 100);
                                        break;
                                }
                            }
                            InventoryService.gI().addItemBag(player, item, 99);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendThongBao(player, "Đổi thành công " + is.temp.name);
                        } else {
                            Service.getInstance().sendThongBao(player, "Yêu cầu có đủ trang bị thần linh");
                        }
                        break;
                    case ConstNpc.SHOP_WHIS_THIEN_SU:
                        int param = 0;
//                        if (Util.isTrue(2, 10)) {
//                            param = Util.nextInt(10, 15);
//                        } else if (Util.isTrue(3, 10)) {
//                            param = Util.nextInt(0, 10);
//                        }
                        Item item = ItemService.gI().createItemFromItemShop(is);
//                        for (ItemOption io : item.itemOptions) {
//                            int optId = io.optionTemplate.id;
//                            switch (optId) {
//                                case 47: //giáp
//                                case 6: //hp
//                                case 26: //hp/30s
//                                case 22: //hp k
//                                case 0: //sức đánh
//                                case 7: //ki
//                                case 28: //ki/30s
//                                case 23: //ki k
//                                case 14: //crit
////                                    io.param += ((long) io.param * param / 100);
//                                    break;
//                            }
//                        }
//                        item.itemOptions.add(new ItemOption(41, 1));
                        InventoryService.gI().addItemBag(player, item, 99);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Đổi thành công " + is.temp.name);
                        break;
                    case ConstNpc.SHOP_SU_KIEN_TET:
                        int pointExchange = 0;
                        int evPoint = player.event.getEventPoint();
                        for (ItemOption io : is.options) {
                            if (io.optionTemplate.id == 200) {
                                pointExchange = io.param;
                            }
                        }
                        if (pointExchange > 0) {
                            if (evPoint >= pointExchange) {
                                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is), 99);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + is.temp.name);
                                player.event.subEventPoint(pointExchange);
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ điểm sự kiện");
                            }
                        }
                        break;
                    default:
                        InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is), 99);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Mua thành công " + is.temp.name);
                        break;
                }
            } else {
                Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
            }
            Service.getInstance().sendMoney(player);
        }
    }

    private boolean isLimitItem(int id) {
        return id >= ConstItem.DA_NANG_CAP_CAP_1 && id <= ConstItem.CONG_THUC_VIP_1086;
    }

    private int getBuyLimit(int id) {
        switch (id) {
            case ConstItem.DA_NANG_CAP_CAP_1:
            case ConstItem.DA_NANG_CAP_CAP_2:
            case ConstItem.DA_MAY_MAN_CAP_1:
            case ConstItem.DA_MAY_MAN_CAP_2:
            case ConstItem.CONG_THUC_VIP:
            case ConstItem.CONG_THUC_VIP_1085:
            case ConstItem.CONG_THUC_VIP_1086:
                return 10;
            case ConstItem.DA_NANG_CAP_CAP_3:
            case ConstItem.DA_MAY_MAN_CAP_3:
                return 5;
            case ConstItem.DA_NANG_CAP_CAP_4:
            case ConstItem.DA_MAY_MAN_CAP_4:
                return 2;
            case ConstItem.DA_NANG_CAP_CAP_5:
            case ConstItem.DA_MAY_MAN_CAP_5:
                return 1;
        }
        return -1;
    }

    //item reward lucky round---------------------------------------------------
    public void openBoxItemLuckyRound(Player player) {
        player.iDMark.setShopId(ConstNpc.SIDE_BOX_LUCKY_ROUND);
        InventoryService.gI().arrangeItems(player.inventory.itemsBoxCrackBall);
        Message msg;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Rương đồ");
            int n = player.inventory.itemsBoxCrackBall.size()
                    - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall);
            msg.writer().writeByte(n);
            for (int i = 0; i < n; i++) {
                Item item = player.inventory.itemsBoxCrackBall.get(i);
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("\n|7|Kiếp đỏ đen");
                List<ItemOption> itemOptions = item.getDisplayOptions();
                msg.writer().writeByte(itemOptions.size());
                for (ItemOption io : itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeInt(io.param);
                }
                msg.writer().writeByte(1);
                CaiTrang ct = Manager.gI().getCaiTrangByItemId(item.template.id);
                msg.writer().writeByte(ct != null ? 1 : 0);
                if (ct != null) {
                    msg.writer().writeShort(ct.getID()[0]);
                    msg.writer().writeShort(ct.getID()[1]);
                    msg.writer().writeShort(ct.getID()[2]);
                    msg.writer().writeShort(ct.getID()[3]);
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void getItemSideBoxLuckyRound(Player player, byte type, int index) {
        if (index < 0 || index >= player.inventory.itemsBoxCrackBall.size()) {
            return;
        }
        Item item = player.inventory.itemsBoxCrackBall.get(index);
        switch (type) {
            case 0: //nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item, 0);
//                        Service.getInstance().sendThongBao(player,
//                                "Bạn nhận được " + (item.template.id == 189
//                                        ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        InventoryService.gI().sendItemBags(player);
                        InventoryService.gI().removeItem(player.inventory.itemsBoxCrackBall, index);
                        openBoxItemLuckyRound(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: //xóa
                InventoryService.gI().subQuantityItem(player.inventory.itemsBoxCrackBall, item, item.quantity);
                openBoxItemLuckyRound(player);
                Service.getInstance().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: //nhận hết
                for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                    item = player.inventory.itemsBoxCrackBall.get(i);
                    if (item.isNotNullItem()) {
                        if (InventoryService.gI().addItemBag(player, item, 0)) {
                            player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
//                            Service.getInstance().sendThongBao(player,
//                                    "Bạn nhận được " + (item.template.id == 189
//                                            ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        }
                    } else {
                        break;
                    }
                }
                InventoryService.gI().sendItemBags(player);
                openBoxItemLuckyRound(player);
                break;
        }
    }
    //item reward---------------------------------------------------------------

    public void openBoxItemReward(Player player) {
        if (player.getSession().itemsReward == null) {
            player.getSession().initItemsReward();
        }
        player.iDMark.setShopId(ConstNpc.SIDE_BOX_ITEM_REWARD);
        Message msg;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Phần\nthưởng");
            msg.writer().writeByte(player.getSession().itemsReward.size());
            for (Item item : player.getSession().itemsReward) {
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("\n|7|Đồ ngẫu nhiên");
                List<ItemOption> itemOptions = item.getDisplayOptions();
                msg.writer().writeByte(itemOptions.size() + 1);
                for (ItemOption io : itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeInt(io.param);
                }
                //số lượng
                msg.writer().writeByte(31);
                msg.writer().writeShort(item.quantity);
                //
                msg.writer().writeByte(1);
                CaiTrang ct = Manager.gI().getCaiTrangByItemId(item.template.id);
                msg.writer().writeByte(ct != null ? 1 : 0);
                if (ct != null) {
                    msg.writer().writeShort(ct.getID()[0]);
                    msg.writer().writeShort(ct.getID()[1]);
                    msg.writer().writeShort(ct.getID()[2]);
                    msg.writer().writeShort(ct.getID()[3]);
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void getItemSideBoxReward(Player player, byte type, int index) {
        if (index < 0 || index >= player.getSession().itemsReward.size()) {
            return;
        }
        Item item = player.getSession().itemsReward.get(index);
        switch (type) {
            case 0: //nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item, 0);
//                        Service.getInstance().sendThongBao(player,
//                                "Bạn nhận được " + (item.template.id == 189
//                                        ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
//                        InventoryService.gI().sendItemBags(player);
                        player.getSession().itemsReward.remove(index);
                        openBoxItemReward(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: //xóa
                player.getSession().itemsReward.remove(index);
                openBoxItemReward(player);
                Service.getInstance().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: //nhận hết
                for (int i = player.getSession().itemsReward.size() - 1; i >= 0; i--) {
                    item = player.getSession().itemsReward.get(i);
                    if (item.isNotNullItem()) {
                        if (InventoryService.gI().addItemBag(player, item, 0)) {
                            player.getSession().itemsReward.remove(i);
//                            Service.getInstance().sendThongBao(player,
//                                    "Bạn nhận được " + (item.template.id == 189
//                                            ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        }
                    } else {
                        break;
                    }
                }
                InventoryService.gI().sendItemBags(player);
                openBoxItemReward(player);
                break;
        }
        PlayerDAO.updateItemReward(player);

    }

    //--------------------------------------------------------------------------
    //điều hướng mua
    public void buyItem(Player player, byte type, int tempId) {
        switch (player.iDMark.getShopId()) {
            case ConstNpc.SIDE_BOX_LUCKY_ROUND:
                getItemSideBoxLuckyRound(player, type, tempId);
                break;
            case ConstNpc.SIDE_BOX_ITEM_REWARD:
                getItemSideBoxReward(player, type, tempId);
                break;
            default:
                buyItemShopNormal(player, getItemShop(player.iDMark.getShopId(), tempId));
                break;
        }
    }

    //ID Item bán vào shop hoàn trả vật phẩm
    public static List<Integer> IdItemSell = Arrays.asList(
            //====================
            //Máy gắp thú vip
            1540, 1541, 1542, 1543, 1544, 1545, 1546, 1549, 1550, 1551, 1552,
            1547, 1548, 1277, 1278, 1279, 1281, 1282, 1284, 1285, 1286, 1287, 1288, 1289, 1290, 1291, 1280, 1178,
            //====================
            //Máy gắp thú thường
            2019, 2020, 2021, 2022, 2023, 1344, 1254, 1242, 1140, 1143, 1144, 1145, 1146,
            1147, 1148, 1149, 1150, 1151, 1152, 1153, 1154, 1425, 1426, 1427, 1428, 1429, 1430,
            //====================
            //Linh thú shop
            1573, 1574, 1575, 1576, 1577, 1578, 1579, 1580, 1581, 1582, 1583, 1584, 1293, 1608, 1609, 1610, 1611, 1612,
            1613, 1615, 1598, 1599, 1601, 1585, 1586, 1587, 1588, 1589, 1292, 1374, 1375, 1376, 1377, 1378, 1379,
            1380, 1381, 1382, 1273, 1274, 1275, 1276, 1616,
            //====================
            //Bội Shop
            1716, 1714, 1713, 1712, 1711, 1710, 1709, 1708, 1715, 1717,
            //====================
            //Danh hiệu shop

            //====================
            //Pet shop
            1648, 1222, 1223, 942, 943, 944, 1196, 1198, 1197, 1644, 1645,
            1646, 1221, 1596, 1684, 1685, 1683, 1593, 1595, 1603, 1606, 2036, 1602,
            //====================
            //Cải trang shop
            1251, 1252, 1253, 1625, 1621, 1627, 1641, 1642, 1643, 1631, 1620, 1632, 1626, 1411, 1410, 1409, 1112, 1111, 1633,
            1192, 860, 845, 843, 844, 1416, 1600, 1624, 1628, 1412, 1638, 1629, 1686, 1214, 1384, 1636, 1639, 1634, 1635,
            //====================
            //Thú cưỡi shop
            1297, 1298, 1247, 1345, 1200, 2015, 2016, 1502, 1503, 1142, 897,
            920, 746, 1092, 743, 849, 733, 1227, 1238, 1239, 1226, 1225, 1383,
            //====================   
            //Đồ thần linh
            565, 566, 567, 650, 651, 652, 653, 654, 655, 656, 657, 658, 659,
            //====================
            //Đồ huỷ diệt            
            660, 661, 662,
            //====================  
            //Đồ thiên sứ
            1048, 1049, 1050, 1051, 1052, 1053, 1054, 1055, 1056, 1057, 1058, 1059, 1060, 1061, 1062,
            //====================      
            // Đồ vòng quay thần điện
            879, 884, 729,
            // Đá quý rương gỗ
            1559, 1560, 1561, 1562, 1563, 1564, 1565, 1356
    );

    public int IdItemNhan(int idCheck) {
        switch (idCheck) {
//======================
// Đá Quý
//======================
//Linh_Thú
//====================   
//Cải_trang
//====================             
//Danh hiệu
//====================               
//Bội Shop
//====================
//Thú_Cưỡi
//====================                
//Pet
//====================                
//Đồ rác TL-HD
            case 555:
            case 556:
            case 557:
            case 558:
            case 559:
            case 560:
            case 561:
            case 562:
            case 563:
            case 564:
            case 565:
            case 566:
            case 567:
            case 650:
            case 651:
            case 652:
            case 653:
            case 654:
            case 655:
            case 656:
            case 657:
            case 658:
            case 659:
            case 660:
            case 661:
            case 662:
//====================                
//Đồ thiên sứ
            case 1048:
            case 1049:
            case 1050:
            case 1051:
            case 1052:
            case 1053:
            case 1054:
            case 1055:
            case 1056:
            case 1057:
            case 1058:
            case 1059:
            case 1060:
            case 1061:
            case 1062:
//====================                                         
//Đồ vòng quay thượng đế
                return 457;//id vật phẩm nhận
            default:
                return 190;
        }
    }

    public int GiaBan(int idCheck) {
        switch (idCheck) {
//==================== 
//Linh_Thú_Shop

//Máy Gắp thú thường
//====================  
//====================  
//Máy gắp thú VIP
//====================
//Cải_trang
//====================                
//Thú_Cưỡi
//====================
//Pet
//====================                
//Danh hiệu - Bội
//====================                
//Bán đồ thần linh
            case 555:
            case 556:
            case 557:
            case 558:
            case 559:
            case 560:
            case 561:
            case 562:
            case 563:
            case 564:
            case 565:
            case 566:
            case 567:
                return 1000;
//====================                
//Đồ huỷ diệt
            case 650:
            case 651:
            case 652:
            case 653:
            case 654:
            case 655:
            case 656:
            case 657:
            case 658:
            case 659:
            case 660:
            case 661:
            case 662:
                return 2000;
//====================                
//Đồ thiên sứ
            case 1048:
            case 1049:
            case 1050:
            case 1051:
            case 1052:
            case 1053:
            case 1054:
            case 1055:
            case 1056:
            case 1057:
            case 1058:
            case 1059:
            case 1060:
            case 1061:
            case 1062:
                return 5000;
//====================                
//Đồ vòng quay thượng đế
            default:
                return 1;
        }
    }

    public String NameItemNhan(int idCheck) {//ko sửa
        Item item = ItemService.gI().createNewItem((short) IdItemNhan(idCheck));
        return item.template.name;
    }

    public void showConfirmSellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            if (index < 0 || index >= pl.inventory.itemsBody.size()) {
                return;
            }
            item = pl.inventory.itemsBody.get(index);
        } else {
            if (index < 0 || index >= pl.inventory.itemsBag.size()) {
                return;
            }
            item = pl.inventory.itemsBag.get(index);
        }
        if (item.isNotNullItem()) {
            int goldReceive = 0;
            if (item.template.id == 457) {
                goldReceive = COST_GOLD_BAR;
            } else if (item.template.id == 2011) {
                goldReceive = COST_LOCK_GOLD_BAR;
            } else {
                goldReceive = item.quantity;
            }
            Message msg = new Message(7);
            try {
                msg.writer().writeByte(where);
                msg.writer().writeShort(index);
                if (IdItemSell.contains((int) item.template.id)) {
                    int soluong = GiaBan(item.template.id);
                    msg.writer().writeUTF("Bạn có muốn bán\n x1 " + item.template.name
                            + "\nvới giá là " + Util.numberToMoney(soluong) + " " + NameItemNhan(item.template.id));
                } else {
                    msg.writer().writeUTF("Bạn có muốn bán\n x" + (item.template.id == 457 || item.template.id == 2011 ? 1 : item.quantity) + " " + item.template.name
                            + "\nvới giá là " + Util.numberToMoney(goldReceive) + " vàng?");
                }
                pl.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            if (index < 0 || index >= pl.inventory.itemsBody.size()) {
                return;
            }
            item = pl.inventory.itemsBody.get(index);
            if (item.template.id == 1444) {
                Service.getInstance().sendThongBao(pl, "Không thể bán vật phẩm này");
                return;
            }
        } else {
            if (index < 0 || index >= pl.inventory.itemsBag.size()) {
                return;
            }
            item = pl.inventory.itemsBag.get(index);
            if (item.template.id == 1444) {
                Service.getInstance().sendThongBao(pl, "Không thể bán vật phẩm này");
                return;
            }
        }
        if (item != null && item.isNotNullItem()) {
            if (IdItemSell.contains((int) item.template.id)) {

                Item itemNhan = ItemService.gI().createNewItem((short) IdItemNhan(item.template.id));
                int goldReceive = GiaBan(item.template.id);
                if (where == 0) {
                    InventoryService.gI().subQuantityItemsBody(pl, item, 1);
                    InventoryService.gI().sendItemBody(pl);
                    Service.getInstance().Send_Caitrang(pl);
                    InventoryService.gI().sendItemBags(pl);
                } else {
                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    InventoryService.gI().sendItemBags(pl);
                }
                itemNhan.quantity = goldReceive;
                InventoryService.gI().addItemBag(pl, itemNhan, 0);
                PlayerService.gI().sendInfoHpMpMoney(pl);
                InventoryService.gI().sendItemBags(pl);
                Service.getInstance().sendThongBao(pl, "Đã bán " + item.template.name
                        + " thu được " + Util.numberToMoney(goldReceive) + " " + itemNhan.template.name);
            } else {
                int goldReceive = 0;
                if (item.template.id == 457) {
                    goldReceive = COST_GOLD_BAR;
                } else if (item.template.id == 2011) {
                    goldReceive = COST_LOCK_GOLD_BAR;
                } else {
                    goldReceive = item.quantity;
                }
                if (pl.inventory.gold + goldReceive <= pl.inventory.getGoldLimit()) {
                    if (where == 0) {
                        InventoryService.gI().subQuantityItemsBody(pl, item, item.quantity);
                        InventoryService.gI().sendItemBody(pl);
                        Service.getInstance().Send_Caitrang(pl);
                    } else {
                        if (item.template.id == 457 || item.template.id == 2011) {
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        } else {
                            InventoryService.gI().subQuantityItemsBag(pl, item, item.quantity);
                        }
                        InventoryService.gI().sendItemBags(pl);
                    }
                    pl.inventory.gold += goldReceive;
//                    pl.playerTask.achivements.get(ConstAchive.TRUM_NHAT_VE_CHAI).count++;
                    PlayerService.gI().sendInfoHpMpMoney(pl);
                    Service.getInstance().sendThongBao(pl, "Đã bán " + item.template.name
                            + " thu được " + Util.numberToMoney(goldReceive) + " vàng");
                } else {
                    Service.getInstance().sendThongBao(pl, "Vàng sau khi bán vượt quá giới hạn");
                }
            }
        } else {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        }
    }

}
