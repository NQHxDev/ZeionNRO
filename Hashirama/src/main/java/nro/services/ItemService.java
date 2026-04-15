package nro.services;

import nro.models.item.ItemOptionTemplate;
import nro.models.item.ItemTemplate;
import nro.models.consignment.ConsignmentItem;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.shop.ItemShop;
import nro.server.Manager;
import nro.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import nro.models.player.Player;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class ItemService {

    private static ItemService i;

    public static ItemService gI() {
        if (i == null) {
            i = new ItemService();
        }
        return i;
    }

    public Item createItemNull() {
        Item item = new Item();
        return item;
    }

    public Item createItemFromItemShop(ItemShop itemShop) {
        Item item = new Item();
        item.template = itemShop.temp;
        item.quantity = 1;
        item.content = item.getContent();
        item.info = item.getInfo();
        for (ItemOption io : itemShop.options) {
            item.itemOptions.add(new ItemOption(io));
        }
        return item;
    }

    public Item copyItem(Item item) {
        Item it = new Item();
        it.itemOptions = new ArrayList<>();
        it.template = item.template;
        it.info = item.info;
        it.content = item.content;
        it.quantity = item.quantity;
        it.createTime = item.createTime;
        for (ItemOption io : item.itemOptions) {
            it.itemOptions.add(new ItemOption(io));
        }
        return it;
    }

    public ConsignmentItem convertToConsignmentItem(Item item) {
        ConsignmentItem it = new ConsignmentItem();
        it.itemOptions = new ArrayList<>();
        it.template = item.template;
        it.info = item.info;
        it.content = item.content;
        it.quantity = item.quantity;
        it.createTime = item.createTime;
        for (ItemOption io : item.itemOptions) {
            it.itemOptions.add(new ItemOption(io));
        }
        it.setPriceGold(-1);
        it.setPriceGem(-1);
        return it;
    }


    public Item createNewItem(short tempId) {
        return createNewItem(tempId, 1);
    }

    public Item createNewItem(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();

        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item TaoMoiVatPhamVaoTuiDo(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();

        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }
    public Item DoThienSu(int itemId, int gender) {
        Item dots = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(1048, 1049, 1050);
        List<Integer> quan = Arrays.asList(1051, 1052, 1053);
        List<Integer> gang = Arrays.asList(1054, 1055, 1056);
        List<Integer> giay = Arrays.asList(1057, 1058, 1059);
        List<Integer> nhan = Arrays.asList(1060, 1061, 1062);
        //áo
        if (ao.contains(itemId)) {
            dots.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(1201) + 2800))); // áo từ 2800-4000 giáp
            if(Util.isTrue(30, 100)){
                dots.itemOptions.add(new ItemOption(108, Util.nextInt(3, 10)));
            }
        }
        //quần
        if (Util.isTrue(60, 100)) {
            if (quan.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(11) + 120))); // hp 120k-130k
            }
        } else {
            if (quan.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(51) + 130))); // hp 130-180k 15%
                if(Util.isTrue(30, 100)){
                    dots.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                }
            }
        }
        //găng
        if (Util.isTrue(60, 100)) {
            if (gang.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(7651) + 11000))); // 11000-18600
            }
        } else {
            if (gang.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(7001) + 12000))); // gang 15% 12-19k -xayda 12k1
                if(Util.isTrue(30, 100)){
                    dots.itemOptions.add(new ItemOption(50, Util.nextInt(3, 10)));
                }
            }
        }
        //giày
        if (Util.isTrue(60, 100)) {
            if (giay.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 120))); // ki 90-110k
            }
        } else {
            if (giay.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 130))); // ki 110-130k
                if(Util.isTrue(30, 100)){
                    dots.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                }
            }
        }

        if (nhan.contains(itemId)) {
            dots.itemOptions.add(new ItemOption(14, Util.highlightsItem(gender == 1, new Random().nextInt(3) + 18))); // nhẫn 18-20%
            if(Util.isTrue(30, 100)){
                    dots.itemOptions.add(new ItemOption(117, Util.nextInt(3, 10)));
                }
        }
        dots.itemOptions.add(new ItemOption(21, 120));
//        dots.itemOptions.add(new ItemOption(30, 1));
        return dots;
    }
    
    public void settaiyoken(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1048);
        Item quan = ItemService.gI().otpts((short)1051);
        Item gang = ItemService.gI().otpts((short)1054);
        Item giay = ItemService.gI().otpts((short)1057);
        Item nhan = ItemService.gI().otpts((short)1060);
        ao.itemOptions.add(new ItemOption(127,1));
        quan.itemOptions.add(new ItemOption(127,1));
        gang.itemOptions.add(new ItemOption(127,1));
        giay.itemOptions.add(new ItemOption(127,1));
        nhan.itemOptions.add(new ItemOption(127,1));
        ao.itemOptions.add(new ItemOption(139,200));
        quan.itemOptions.add(new ItemOption(139,200));
        gang.itemOptions.add(new ItemOption(139,200));
        giay.itemOptions.add(new ItemOption(139,200));
        nhan.itemOptions.add(new ItemOption(139,200));
        ao.itemOptions.add(new ItemOption(5,15));
        quan.itemOptions.add(new ItemOption(5,15));
        gang.itemOptions.add(new ItemOption(5,15));
        giay.itemOptions.add(new ItemOption(5,15));
        nhan.itemOptions.add(new ItemOption(5,15));
        ao.itemOptions.add(new ItemOption(30,15));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
    
    public void setgenki(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1048);
        Item quan = ItemService.gI().otpts((short)1051);
        Item gang = ItemService.gI().otpts((short)1054);
        Item giay = ItemService.gI().otpts((short)1057);
        Item nhan = ItemService.gI().otpts((short)1060);
        ao.itemOptions.add(new ItemOption(128,1));
        quan.itemOptions.add(new ItemOption(128,1));
        gang.itemOptions.add(new ItemOption(128,1));
        giay.itemOptions.add(new ItemOption(128,1));
        nhan.itemOptions.add(new ItemOption(128,1));
        ao.itemOptions.add(new ItemOption(140,200));
        quan.itemOptions.add(new ItemOption(140,200));
        gang.itemOptions.add(new ItemOption(140,200));
        giay.itemOptions.add(new ItemOption(140,200));
        nhan.itemOptions.add(new ItemOption(140,200));
        ao.itemOptions.add(new ItemOption(5,15));
        quan.itemOptions.add(new ItemOption(5,15));
        gang.itemOptions.add(new ItemOption(5,15));
        giay.itemOptions.add(new ItemOption(5,15));
        nhan.itemOptions.add(new ItemOption(5,15));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
    
        public void setkamejoko(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1048);
        Item quan = ItemService.gI().otpts((short)1051);
        Item gang = ItemService.gI().otpts((short)1054);
        Item giay = ItemService.gI().otpts((short)1057);
        Item nhan = ItemService.gI().otpts((short)1060);
        ao.itemOptions.add(new ItemOption(129,1));
        quan.itemOptions.add(new ItemOption(129,1));
        gang.itemOptions.add(new ItemOption(129,1));
        giay.itemOptions.add(new ItemOption(129,1));
        nhan.itemOptions.add(new ItemOption(129,1));
        ao.itemOptions.add(new ItemOption(141,200));
        quan.itemOptions.add(new ItemOption(141,200));
        gang.itemOptions.add(new ItemOption(141,200));
        giay.itemOptions.add(new ItemOption(141,200));
        nhan.itemOptions.add(new ItemOption(141,200));
        ao.itemOptions.add(new ItemOption(5,15));
        quan.itemOptions.add(new ItemOption(5,15));
        gang.itemOptions.add(new ItemOption(5,15));
        giay.itemOptions.add(new ItemOption(5,15));
        nhan.itemOptions.add(new ItemOption(5,15));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
        
    public void setgodki(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1049);
        Item quan = ItemService.gI().otpts((short)1052);
        Item gang = ItemService.gI().otpts((short)1055);
        Item giay = ItemService.gI().otpts((short)1058);
        Item nhan = ItemService.gI().otpts((short)1061);
        ao.itemOptions.add(new ItemOption(130,1));
        quan.itemOptions.add(new ItemOption(130,1));
        gang.itemOptions.add(new ItemOption(130,1));
        giay.itemOptions.add(new ItemOption(130,1));
        nhan.itemOptions.add(new ItemOption(130,1));
        ao.itemOptions.add(new ItemOption(142,400));
        quan.itemOptions.add(new ItemOption(142,400));
        gang.itemOptions.add(new ItemOption(142,400));
        giay.itemOptions.add(new ItemOption(142,400));
        nhan.itemOptions.add(new ItemOption(142,400));
        ao.itemOptions.add(new ItemOption(5,15));
        quan.itemOptions.add(new ItemOption(5,15));
        gang.itemOptions.add(new ItemOption(5,15));
        giay.itemOptions.add(new ItemOption(5,15));
        nhan.itemOptions.add(new ItemOption(5,15));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
    
    public void setgoddam(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1049);
        Item quan = ItemService.gI().otpts((short)1052);
        Item gang = ItemService.gI().otpts((short)1055);
        Item giay = ItemService.gI().otpts((short)1058);
        Item nhan = ItemService.gI().otpts((short)1061);
        ao.itemOptions.add(new ItemOption(131,1));
        quan.itemOptions.add(new ItemOption(131,1));
        gang.itemOptions.add(new ItemOption(131,1));
        giay.itemOptions.add(new ItemOption(131,1));
        nhan.itemOptions.add(new ItemOption(131,1));
        ao.itemOptions.add(new ItemOption(143,200));
        quan.itemOptions.add(new ItemOption(143,200));
        gang.itemOptions.add(new ItemOption(143,200));
        giay.itemOptions.add(new ItemOption(143,200));
        nhan.itemOptions.add(new ItemOption(143,200));
        ao.itemOptions.add(new ItemOption(5,15));
        quan.itemOptions.add(new ItemOption(5,15));
        gang.itemOptions.add(new ItemOption(5,15));
        giay.itemOptions.add(new ItemOption(5,15));
        nhan.itemOptions.add(new ItemOption(5,15));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
    
    public void setsummon(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1049);
        Item quan = ItemService.gI().otpts((short)1052);
        Item gang = ItemService.gI().otpts((short)1055);
        Item giay = ItemService.gI().otpts((short)1058);
        Item nhan = ItemService.gI().otpts((short)1061);
        ao.itemOptions.add(new ItemOption(132,1));
        quan.itemOptions.add(new ItemOption(132,1));
        gang.itemOptions.add(new ItemOption(132,1));
        giay.itemOptions.add(new ItemOption(132,1));
        nhan.itemOptions.add(new ItemOption(132,1));
        ao.itemOptions.add(new ItemOption(144,200));
        quan.itemOptions.add(new ItemOption(144,200));
        gang.itemOptions.add(new ItemOption(144,200));
        giay.itemOptions.add(new ItemOption(144,200));
        nhan.itemOptions.add(new ItemOption(144,200));
        ao.itemOptions.add(new ItemOption(5,15));
        quan.itemOptions.add(new ItemOption(5,15));
        gang.itemOptions.add(new ItemOption(5,15));
        giay.itemOptions.add(new ItemOption(5,15));
        nhan.itemOptions.add(new ItemOption(5,15));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
    
        public void setgodgalick(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1050);
        Item quan = ItemService.gI().otpts((short)1053);
        Item gang = ItemService.gI().otpts((short)1056);
        Item giay = ItemService.gI().otpts((short)1059);
        Item nhan = ItemService.gI().otpts((short)1062);
        ao.itemOptions.add(new ItemOption(133,1));
        quan.itemOptions.add(new ItemOption(133,1));
        gang.itemOptions.add(new ItemOption(133,1));
        giay.itemOptions.add(new ItemOption(133,1));
        nhan.itemOptions.add(new ItemOption(133,1));
        ao.itemOptions.add(new ItemOption(136,200));
        quan.itemOptions.add(new ItemOption(136,200));
        gang.itemOptions.add(new ItemOption(136,200));
        giay.itemOptions.add(new ItemOption(136,200));
        nhan.itemOptions.add(new ItemOption(136,200));
        ao.itemOptions.add(new ItemOption(5,15));
        quan.itemOptions.add(new ItemOption(5,15));
        gang.itemOptions.add(new ItemOption(5,15));
        giay.itemOptions.add(new ItemOption(5,15));
        nhan.itemOptions.add(new ItemOption(5,15));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
        
        
        
    public void setmonkey(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1050);
        Item quan = ItemService.gI().otpts((short)1053);
        Item gang = ItemService.gI().otpts((short)1056);
        Item giay = ItemService.gI().otpts((short)1059);
        Item nhan = ItemService.gI().otpts((short)1062);
        ao.itemOptions.add(new ItemOption(134,1));
        quan.itemOptions.add(new ItemOption(134,1));
        gang.itemOptions.add(new ItemOption(134,1));
        giay.itemOptions.add(new ItemOption(134,1));
        nhan.itemOptions.add(new ItemOption(134,1));
        ao.itemOptions.add(new ItemOption(137,1));
        quan.itemOptions.add(new ItemOption(137,1));
        gang.itemOptions.add(new ItemOption(137,1));
        giay.itemOptions.add(new ItemOption(137,1));
        nhan.itemOptions.add(new ItemOption(137,1));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
            
    public void setgodhp(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short)1050);
        Item quan = ItemService.gI().otpts((short)1053);
        Item gang = ItemService.gI().otpts((short)1056);
        Item giay = ItemService.gI().otpts((short)1059);
        Item nhan = ItemService.gI().otpts((short)1062);
        ao.itemOptions.add(new ItemOption(135,1));
        quan.itemOptions.add(new ItemOption(135,1));
        gang.itemOptions.add(new ItemOption(135,1));
        giay.itemOptions.add(new ItemOption(135,1));
        nhan.itemOptions.add(new ItemOption(135,1));
        ao.itemOptions.add(new ItemOption(138,300));
        quan.itemOptions.add(new ItemOption(138,300));
        gang.itemOptions.add(new ItemOption(138,300));
        giay.itemOptions.add(new ItemOption(138,300));
        nhan.itemOptions.add(new ItemOption(138,300));
        ao.itemOptions.add(new ItemOption(30,0));
        quan.itemOptions.add(new ItemOption(30,0));
        gang.itemOptions.add(new ItemOption(30,0));
        giay.itemOptions.add(new ItemOption(30,0));
        nhan.itemOptions.add(new ItemOption(30,0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, nhan, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
    
    
    public Item otpts(short tempId) {
        return otpts(tempId, 1);
    }
    
    public Item otpts(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type== 0){
            item.itemOptions.add(new ItemOption(21,120));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000,2500)));
        }
        if (item.template.type== 1){
            item.itemOptions.add(new ItemOption(21,120));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(150,200)));
        }
        if (item.template.type== 2){
            item.itemOptions.add(new ItemOption(21,120));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(18000,20000)));
        }
        if (item.template.type== 3){
            item.itemOptions.add(new ItemOption(21,120));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(150,200)));
        }
        if (item.template.type== 4){
            item.itemOptions.add(new ItemOption(21,120));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(20,25)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }
    
    public int randomSKHId(byte gender) {
        if (gender == 3) gender = 2;
        int[][] options = {{128, 129, 127}, {130, 131, 132}, {133, 135, 134}};
        int skhv1 = 25;
        int skhv2 = 35;
        int skhc = 40;
        int skhId = -1;
        int rd = Util.nextInt(1, 100);
        if (rd <= skhv1) {
            skhId = 0;
        } else if (rd <= skhv1 + skhv2) {
            skhId = 1;
        } else if (rd <= skhv1 + skhv2 + skhc) {
            skhId = 2;
        }
        return options[gender][skhId];
    }
    
    public Item itemSKH(int itemId, int skhId) {
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            item.itemOptions.addAll(ItemService.gI().getListOptionItemShop((short) itemId));
            item.itemOptions.add(new ItemOption(skhId, 1));
            item.itemOptions.add(new ItemOption(optionIdSKH(skhId), 1));
            item.itemOptions.add(new ItemOption(30, 1));
        }
        return item;
    }
    public List<ItemOption> getListOptionItemShop(short id) {
        List<ItemOption> list = new ArrayList<>();
        Manager.SHOPS.forEach(shop -> shop.tabShops.forEach(tabShop -> tabShop.itemShops.forEach(itemShop -> {
            if (itemShop.temp.id == id && list.size() == 0) {
                list.addAll(itemShop.options);
            }
        })));
        return list;
    }
    
    public Item createItemSetKichHoat(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public ConsignmentItem createNewConsignmentItem(short tempId, int quantity) {
        ConsignmentItem item = new ConsignmentItem();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemFromItemMap(ItemMap itemMap) {
        Item item = createNewItem(itemMap.itemTemplate.id, itemMap.quantity);
        item.itemOptions = itemMap.options;
        return item;
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        return Manager.ITEM_OPTION_TEMPLATES.get(id);
    }

    public ItemTemplate getTemplate(int id) {
        return Manager.ITEM_TEMPLATES.get(id);
    }

    public boolean isItemActivation(Item item) {
        return false;
    }

    public int getPercentTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                    return 10;
                case 530:
                case 535:
                    return 20;
                case 531:
                case 536:
                    return 30;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    public boolean isTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                case 530:
                case 535:
                case 531:
                case 536:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
    
    public int optionIdSKH(int skhId) {
        switch (skhId) {
            case 127: //Set Viet Taiyoken
                return 139;
            case 128: //Set Viet Genki
                return 140;
            case 129: //Set Viet Kamejoko
                return 141;
            case 130: //Set Viet KI
                return 142;
            case 131: //Set Viet Dame
                return 143;
            case 132: //Set Viet Summon
                return 144;
            case 133: //Set Viet Galick
                return 136;
            case 134: //Set Viet Monkey
                return 137;
            case 135: //Set Viet HP
                return 138;
        }
        return 0;
    }

    public boolean isOutOfDateTime(Item item) {
        long now = System.currentTimeMillis();
        if (item != null) {
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 93) {
                    int dayPass = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
                    if (dayPass != 0) {
                        io.param -= dayPass;
                        if (io.param <= 0) {
                            return true;
                        } else {
                            item.createTime = System.currentTimeMillis();
                        }
                    }
                } else if (io.optionTemplate.id == 196) {
                    long e = io.param * 1000L;
                    if (e <= now) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isItemNoLimitQuantity(int id) {// item k giới hạn số lượng
        if (id >= 1066 && id <= 1070) {// mảnh trang bị thiên sứ
            return true;
        }
        return false;
    }
    public int randomSKHThanhTon(byte gender) {
        if (gender == 3) gender = 2;
        int[] options = {234, 235, 236};
        return options[gender];
    }
    
    public int optionIdSKHThanhTon(int skhId) {
        switch (skhId) {
            case 234: //Set Thánh tôn trái dất
                return 237;
            case 235: //Set Thánh tôn namec
                return 238;
            case 236: //Set Thánh tôn xayda
                return 239;
        }
        return 0;
    }
}
