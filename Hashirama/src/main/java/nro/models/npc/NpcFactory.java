package nro.models.npc;

import java.awt.event.FocusEvent;
import nro.attr.Attribute;
import nro.attr.AttributeManager;
import nro.consts.*;
import nro.dialog.ConfirmDialog;
import nro.dialog.MenuDialog;
import nro.jdbc.daos.PlayerDAO;
import nro.lib.RandomCollection;
import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.event.EscortedBoss;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.consignment.ConsignmentShop;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.item.ItemTemplate;
import nro.models.map.ItemMap;
import nro.models.map.Map;
import nro.models.map.SantaCity;
import nro.models.map.Zone;
import nro.models.map.challenge.MartialCongressService;
import nro.models.map.dungeon.SnakeRoad;
import nro.models.map.dungeon.zones.ZSnakeRoad;
import nro.models.map.mabu.MabuWar;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.map.phoban.DoanhTrai;
import nro.models.map.war.BlackBallWar;
import nro.models.map.war.NamekBallWar;
import nro.models.player.Inventory;
import nro.models.player.NPoint;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.noti.NotiManager;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.server.io.Message;
import nro.services.*;
import nro.services.func.*;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.TimeUtil;
import nro.utils.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import nro.manager.TopManager;
import nro.models.map.mabu.MabuWar14h;
import nro.models.phuban.DragonNamecWar.TranhNgoc;
import nro.models.phuban.DragonNamecWar.TranhNgocService;
import nro.server.Client;

import static nro.server.Manager.*;
import static nro.services.func.SummonDragon.*;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class NpcFactory {

    private static boolean nhanVang = true;
    private static boolean nhanDeTu = true;
    
    private static final int ID_XU = 1567;
    private static final int GACHA_TV =  999_999;
    private static final int GACHA_XU =  10000;
    
    // playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        Npc npc = null;
        try {
            switch (tempId) {
                case ConstNpc.FIDE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (player.iDMark.getTranhNgoc() == 1) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cút!Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
                                return;
                            }
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (select) {
                                    case 0:
                                        if (player.iDMark.getTranhNgoc() == 2 && player.isHoldNamecBallTranhDoat) {
                                            if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                                Service.getInstance().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                                return;
                                            }
                                            TranhNgocService.getInstance().dropBall(player, (byte) 2);
                                            player.zone.pointFide++;
                                            if (player.zone.pointFide > ConstTranhNgocNamek.MAX_POINT) {
                                                player.zone.pointFide = ConstTranhNgocNamek.MAX_POINT;
                                            }
                                            TranhNgocService.getInstance().sendUpdatePoint(player);
                                        }
                                        break;
                                    case 1:
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CADIC:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (player.iDMark.getTranhNgoc() == 2) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Cút!Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
                                return;
                            }
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (select) {
                                    case 0:
                                        if (player.iDMark.getTranhNgoc() == 1 && player.isHoldNamecBallTranhDoat) {
                                            if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                                Service.getInstance().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                                return;
                                            }
                                            TranhNgocService.getInstance().dropBall(player, (byte) 1);
                                            player.zone.pointCadic++;
                                            if (player.zone.pointCadic > ConstTranhNgocNamek.MAX_POINT) {
                                                player.zone.pointCadic = ConstTranhNgocNamek.MAX_POINT;
                                            }
                                            TranhNgocService.getInstance().sendUpdatePoint(player);
                                        }
                                        break;
                                    case 1:
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TORIBOT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Chào mừng bạn đến với cửa hàng đá qúy số 1 thời đại", "Cửa Hàng");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_TORIBOT, 0, -1);
                            }
                        }
                    };
                    break;
                case ConstNpc.NGO_KHONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chu mi nga", "Tặng quả\nHồng đào\nChín");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                int itemNeed = ConstItem.QUA_HONG_DAO_CHIN;
                                Item item = InventoryService.gI().findItemBagByTemp(player, itemNeed);
                                if (item != null) {
                                    RandomCollection<Integer> rc = Manager.HONG_DAO_CHIN;
                                    int itemID = rc.next();
                                    int x = cx + Util.nextInt(-50, 50);
                                    int y = player.zone.map.yPhysicInTop(x, cy - 24);
                                    int quantity = 1;
                                    if (itemID == ConstItem.HONG_NGOC) {
                                        quantity = Util.nextInt(1, 2);
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, item, 1);
                                    InventoryService.gI().sendItemBags(player);
                                    ItemMap itemMap = new ItemMap(player.zone, itemID, quantity, x, y, player.id);
                                    Service.getInstance().dropItemMap(player.zone, itemMap);
                                    npcChat(player.zone, "Xie xie");
                                } else {
                                    Service.getInstance().sendThongBao(player, "Không tìm thấy!");
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DUONG_TANG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (this.mapId == MapName.LANG_ARU) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "|7|NGŨ HÀNH SƠN"
                                        + "\n|2|A mi phò phò, thí chủ hãy giúp giải cứu đồ đệ của bần tăng đang bị phong ấn tại ngũ hành sơn."
                                        + "\n|3|Tại đây sức mạnh dưới 100 Tỷ đánh quái được x2 TNSM",
                                        "Đồng ý", "Từ chối");
                            }
                            if (this.mapId == MapName.NGU_HANH_SON_3) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "A mi phò phò, thí chủ hãy thu thập bùa 'giải khai phong ấn', mỗi chữ 10 cái.",
                                        "Về\nLàng Aru", "Từ chối");
                            }
                            if (this.mapId == MapName.NGU_HANH_SON) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "A mi phò phò, thí chủ hãy thu thập bùa 'giải khai phong ấn', mỗi chữ 10 cái.",
                                        "Đổi đào chín", "Giải phong ấn", "Từ chối");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == MapName.LANG_ARU) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:

                                                if (!Manager.gI().getGameConfig().isOpenPrisonPlanet()) {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Lối vào ngũ hành sơn chưa mở");
                                                    return;
                                                }

                                                Zone zone = MapService.gI().getZoneJoinByMapIdAndZoneId(player, 124, 0);
                                                if (zone != null) {
                                                    player.location.x = 100;
                                                    player.location.y = 384;
                                                    MapService.gI().goToMap(player, zone);
                                                    Service.getInstance().clearMap(player);
                                                    zone.mapInfo(player);
                                                    player.zone.loadAnotherToMe(player);
                                                    player.zone.load_Me_To_Another(player);
                                                }
                                                // Service.getInstance().sendThongBao(player, "Lối vào ngũ hành sơn chưa
                                                // mở");
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == MapName.NGU_HANH_SON_3) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                Zone zone = MapService.gI().getZoneJoinByMapIdAndZoneId(player, 0, 0);
                                                if (zone != null) {
                                                    player.location.x = 600;
                                                    player.location.y = 432;
                                                    MapService.gI().goToMap(player, zone);
                                                    Service.getInstance().clearMap(player);
                                                    zone.mapInfo(player);
                                                    player.zone.loadAnotherToMe(player);
                                                    player.zone.load_Me_To_Another(player);
                                                }
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == MapName.NGU_HANH_SON) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                // Đổi đào
                                                Item item = InventoryService.gI().findItemBagByTemp(player,
                                                        ConstItem.QUA_HONG_DAO);
                                                if (item == null || item.quantity < 10) {
                                                    npcChat(player,
                                                            "Cần 10 quả đào xanh để đổi lấy đào chín từ bần tăng.");
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    npcChat(player, "Túi đầy rồi kìa.");
                                                    return;
                                                }
                                                Item newItem = ItemService.gI()
                                                        .createNewItem((short) ConstItem.QUA_HONG_DAO_CHIN, 1);
                                                InventoryService.gI().subQuantityItemsBag(player, item, 10);
                                                InventoryService.gI().addItemBag(player, newItem, 0);
                                                InventoryService.gI().sendItemBags(player);
                                                npcChat(player,
                                                        "Ta đã đổi cho thí chủ rồi đó, hãy mang cho đệ tử ta đi nào.");
                                                break;

                                            case 1:
                                                // giải phong ấn
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    npcChat(player, "Túi đầy rồi kìa.");
                                                    return;
                                                }
                                                int[] itemsNeed = {ConstItem.CHU_GIAI, ConstItem.CHU_KHAI,
                                                    ConstItem.CHU_PHONG, ConstItem.CHU_AN};
                                                List<Item> items = InventoryService.gI().getListItem(player, itemsNeed)
                                                        .stream().filter(i -> i.quantity >= 10)
                                                        .collect(Collectors.toList());
                                                boolean[] flags = new boolean[4];
                                                for (Item i : items) {
                                                    switch ((int) i.template.id) {
                                                        case ConstItem.CHU_GIAI:
                                                            flags[0] = true;
                                                            break;

                                                        case ConstItem.CHU_KHAI:
                                                            flags[1] = true;
                                                            break;

                                                        case ConstItem.CHU_PHONG:
                                                            flags[2] = true;
                                                            break;

                                                        case ConstItem.CHU_AN:
                                                            flags[3] = true;
                                                            break;
                                                    }
                                                }
                                                for (int i = 0; i < flags.length; i++) {
                                                    if (!flags[i]) {
                                                        ItemTemplate template = ItemService.gI()
                                                                .getTemplate(itemsNeed[i]);
                                                        npcChat("Thí chủ còn thiếu " + template.name);
                                                        return;
                                                    }
                                                }

                                                for (Item i : items) {
                                                    InventoryService.gI().subQuantityItemsBag(player, i, 10);
                                                }

                                                RandomCollection<Integer> rc = new RandomCollection<>();
                                                rc.add(10, ConstItem.CAI_TRANG_TON_NGO_KHONG_DE_TU);
                                                rc.add(10, ConstItem.CAI_TRANG_BAT_GIOI_DE_TU);
                                                rc.add(50, ConstItem.GAY_NHU_Y);
                                                switch (player.gender) {
                                                    case ConstPlayer.TRAI_DAT:
                                                        rc.add(30, ConstItem.CAI_TRANG_TON_NGO_KHONG);
                                                        break;

                                                    case ConstPlayer.NAMEC:
                                                        rc.add(30, ConstItem.CAI_TRANG_TON_NGO_KHONG_545);
                                                        break;

                                                    case ConstPlayer.XAYDA:
                                                        rc.add(30, ConstItem.CAI_TRANG_TON_NGO_KHONG_546);
                                                        break;
                                                }
                                                int itemID = rc.next();
                                                Item nItem = ItemService.gI().createNewItem((short) itemID);
                                                boolean all = itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG_DE_TU
                                                        || itemID == ConstItem.CAI_TRANG_BAT_GIOI_DE_TU
                                                        || itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG
                                                        || itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG_545
                                                        || itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG_546;
                                                if (all) {
                                                    nItem.itemOptions.add(new ItemOption(50, Util.nextInt(20, 35)));
                                                    nItem.itemOptions.add(new ItemOption(77, Util.nextInt(20, 35)));
                                                    nItem.itemOptions.add(new ItemOption(103, Util.nextInt(20, 35)));
                                                    nItem.itemOptions.add(new ItemOption(94, Util.nextInt(5, 10)));
                                                    nItem.itemOptions.add(new ItemOption(100, Util.nextInt(10, 20)));
                                                    nItem.itemOptions.add(new ItemOption(101, Util.nextInt(10, 20)));
                                                }
                                                if (itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG
                                                        || itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG_545
                                                        || itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG_546) {
                                                    nItem.itemOptions.add(new ItemOption(80, Util.nextInt(5, 15)));
                                                    nItem.itemOptions.add(new ItemOption(81, Util.nextInt(5, 15)));
                                                    nItem.itemOptions.add(new ItemOption(106, 0));
                                                } else if (itemID == ConstItem.CAI_TRANG_TON_NGO_KHONG_DE_TU
                                                        || itemID == ConstItem.CAI_TRANG_BAT_GIOI_DE_TU) {
                                                    nItem.itemOptions.add(new ItemOption(197, 0));
                                                }
                                                if (all) {
                                                    if (Util.isTrue(499, 500)) {
                                                        nItem.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
                                                    }
                                                } else if (itemID == ConstItem.GAY_NHU_Y) {
                                                    RandomCollection<Integer> rc2 = new RandomCollection<>();
                                                    rc2.add(60, 30);
                                                    rc2.add(30, 90);
                                                    rc2.add(10, 365);
                                                    nItem.itemOptions.add(new ItemOption(93, rc2.next()));
                                                }
                                                InventoryService.gI().addItemBag(player, nItem, 0);
                                                InventoryService.gI().sendItemBags(player);
                                                npcChat(player.zone,
                                                        "A mi phò phò, đa tạ thí chủ tương trợ, xin hãy nhận món quà mọn này, bần tăng sẽ niệm chú giải thoát cho Ngộ Không");
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BANG_XEP_HANG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                            "|8|Bạn muốn xem bảng xếp hạng nào?"
                                            , "Top\nSức Đánh"
                                            , "Top\nHp"
                                            , "Top\nKi"
                                            , "Top\nSức Mạnh"
                                            , "Top\nNhiệm Vụ"
                                            , "Top\nNạp"
                                    );
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            TopService.gI().showTopSd(player);
                                            break;
                                        case 1:
                                            TopService.gI().showTopHP(player);
                                            break;
                                        case 2:
                                            TopService.gI().showTopKi(player);
                                            break;
                                        case 3:
                                            TopService.gI().showTopSucManh(player);
                                            break;
                                        case 4:
                                            TopService.gI().showTopNhiemVu(player);
                                            break;
                                        case 5:
                                            TopService.gI().showTopVnd(player);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TAPION:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 19) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ác quỷ truyền thuyết Hirudegarn\nđã thoát khỏi phong ấn ngàn năm\nHãy giúp tôi chế ngự nó",
                                            "OK", "Từ chối");
                                }
                                if (this.mapId == 126) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tôi sẽ đưa bạn về", "OK",
                                            "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 19) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
                                                if (santaCity != null) {
                                                    if (!santaCity.isOpened() || santaCity.isClosed()) {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Hẹn gặp bạn lúc 22h mỗi ngày");
                                                        return;
                                                    }
                                                    santaCity.enter(player);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
                                                }
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 126) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
                                                if (santaCity != null) {
                                                    santaCity.leave(player);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.MR_POPO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ta là người đang giữ rương quà cho ngươi, nếu có bất kì món quà nào hãy tới gặp ta để nhận."
                                            + "\n Nhớ nhận ngay để không bị mất khi có quà mới nhé!",
                                            "Rương\nQuà tặng", "Bảng\n xếp hạng", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ShopService.gI().openBoxItemReward(player);
                                                break;
                                            case 1:
                                                Service.getInstance().showTopPower(player, Service.getInstance().TOP_SUCMANH);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.LY_TIEU_NUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "\b|8|Trò chơi Tài Xỉu đang được diễn ra\n\n|6|Thử vận may của bạn với trò chơi Tài Xỉu! Đặt cược và dự đoán đúng"
                                        + "\n kết quả, bạn sẽ được nhận thưởng lớn. Hãy tham gia ngay và\n cùng trải nghiệm sự hồi hộp, thú vị trong trò chơi này!"
                                        + "\n\n|7|(Điều kiện tham gia : Nhiệm vụ 24)\n\n|2|Đặt tối thiểu: 1.000 Hồng ngọc\n Tối đa: 100.000 Hồng ngọc"
                                        + "\n\n|7| Lưu ý : Thoát game khi chốt Kết quả sẽ MẤT Tiền cược và Tiền thưởng", "Thể lệ", "Tham gia");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "|5|Có 2 nhà cái Tài và Xĩu, bạn chỉ được chọn 1 nhà để tham gia"
                                                    + "\n\n|6|Sau khi kết thúc thời gian đặt cược. Hệ thống sẽ tung xí ngầu để biết kết quả Tài Xỉu"
                                                    + "\n\nNếu Tổng số 3 con xí ngầu <=10 : XỈU\nNếu Tổng số 3 con xí ngầu >10 : TÀI\nNếu 3 Xí ngầu cùng 1 số : TAM HOA (Nhà cái lụm hết)"
                                                    + "\n\n|7|Lưu ý: Số Hồng ngọc nhận được sẽ bị nhà cái lụm đi 20%. Trong quá trình diễn ra khi đặt cược nếu thoát game sẽ bị MẤT TIỀN ĐẶT CƯỢC", "Ok");
                                            break;
                                        case 1:
                                            String time = ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                                            if (TaiXiu.gI().baotri == false) {
                                                if (player.goldTai == 0 && player.goldXiu == 0) {
                                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time, "Cập nhập", "Theo TÀI", "Theo XỈU", "Đóng");
                                                } else if (player.goldTai > 0) {
                                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Tài : " + Util.format(player.goldTai) + " Hồng ngọc", "Cập nhập", "Đóng");
                                                } else {
                                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Xỉu : " + Util.format(player.goldXiu) + " Hồng ngọc", "Cập nhập", "Đóng");
                                                }
                                            } else {
                                                if (player.goldTai == 0 && player.goldXiu == 0) {
                                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Hệ thống sắp bảo trì", "Cập nhập", "Đóng");
                                                } else if (player.goldTai > 0) {
                                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Tài : " + Util.format(player.goldTai) + " Hồng ngọc" + "\n\n|7|Hệ thống sắp bảo trì", "Cập nhập", "Đóng");
                                                } else {
                                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI-XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Xỉu : " + Util.format(player.goldXiu) + " Hồng ngọc" + "\n\n|7|Hệ thống sắp bảo trì", "Cập nhập", "Đóng");
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.SO_MAY_MAN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                String time = ((SoMayMan.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                                StringBuilder stringBuilder = new StringBuilder();
                                for (Player mem : SoMayMan.gI().TrungGiai) {
                                    if (stringBuilder.length() > 0) {
                                        stringBuilder.append(", ");
                                    }
                                    stringBuilder.append(mem.name);
                                }
                                String NamePl = stringBuilder.toString();
                                createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Kết quả giải trước: " + SoMayMan.gI().SoGiaiTruoc
                                        + "\nNgười trúng giải trước: " + NamePl
                                        + "\nTham gia: " + SoMayMan.gI().PlayerThamGia.size() + " người"
                                        + "\nĐoán trúng sẽ nhận thưởng 100.000 Hồng ngọc"
                                        + "\nThời gian quay số: " + time, "1 số\n1.000 Hồng ngọc", "Hướng dẫn", "Đóng");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            Input.gI().ChonSo(player);
                                            break;
                                        case 1:
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                    "Sau khi hết thời gian đếm ngược"
                                                    + "\nHệ thống sẽ quay số cho ra kết quả"
                                                    + "\nNgười thắng sẽ nhận được 100.000 Hồng ngọc"
                                                    + "\n(Mỗi 1 số dự đoán sẽ mất 1.000 Hồng ngọc)", "Ok");
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUY_LAO_KAME:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "|8| Tài khoản hiện đang có:"
                                            + "\n- Số Coin: " + Util.format(player.getSession().vnd)
                                            + "\n- Đã Săn: " + player.killboss + " Boss",
                                            "Đổi\nThỏi Vàng","Vào lãnh\n Địa Bang", "Bản đồ\nKho Báu", "Hồi Skill", "Giải tán\nBang");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, ConstNpc.QUY_DOI_COIN,
                                                    "|7|ĐỔI TIỀN TỆ\n"
                                                    + "\n|6|Giới hạn đổi không quá 1.000.000 Coin"
                                                    + "\n|1|Coin hiện còn : " + " " + Util.format(player.getSession().vnd) + "\n"
                                                    + "1000 Coin được 1000 thỏi vàng\n"
                                                    + "1000 Coin được 500 xu bạc\n"
                                                    + "1000 Coin được 100 xu vàng\n"
                                                    + "|3|Đổi tối thiểu 1k và số coin cần chia hết cho 1k",
                                                    "Đổi\nThỏi Vàng","Đổi\nXu Bạc","Đổi\nXu Vàng");
                                            break;
                                        case 1:
                                            if (player.clan != null) {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, -1);
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Yêu cầu có bang hội !!!");
                                            }
                                            break;
                                        case 2:
                                            if (player.clan != null) {
                                                if (player.clan.banDoKhoBau != null) {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                                            "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                                            + player.clan.banDoKhoBau.level
                                                            + "\nCon có muốn đi theo không?",
                                                            "Đồng ý", "Từ chối");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                                            "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\n"
                                                            + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                                            "Chọn\ncấp độ", "Từ chối");
                                                }
                                            } else {
                                                this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                                            }
                                            break;
                                        case 3:
                                            Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
                                            if(thoiVang == null || thoiVang.quantity < 10){
                                                this.npcChat(player, "Cần 10 thỏi vàng để hồi skill");
                                                return;
                                            }
//                                            player.inventory.gem -= 5;
//                                            Service.getInstance().sendMoney(player);
                                            InventoryService.gI().subQuantityItemsBag(player, thoiVang, 10);
                                            Service.getInstance().releaseCooldownSkill(player);
                                            InventoryService.gI().sendItemBags(player);
                                            Service.getInstance().sendThongBao(player,
                                                        "Bạn đã tiêu 10 thỏi vàng để hồi lại full skill");
                                            break;
                                        case 4:
                                            if (player.clan != null) {
                                                ClanService.gI().RemoveClanAll(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn không có bang hội nào để giải tán.");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
                                    switch (select) {
                                        case 0:
                                            if (player.isAdmin()
                                                    || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                                ChangeMapService.gI().goToDBKB(player);
                                            } else {
                                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                            }
                                            break;

                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
                                    switch (select) {
                                        case 0:
                                            if (player.isAdmin()
                                                    || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                                Input.gI().createFormChooseLevelBDKB(player);
                                            } else {
                                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                            }
                                            break;
                                    }

                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
                                    switch (select) {
                                        case 0:
                                            BanDoKhoBauService.gI().openBanDoKhoBau(player,
                                                    Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                            break;
                                    }

                                } else if (player.iDMark.getIndexMenu() == ConstNpc.QUY_DOI_COIN) {
                                    switch (select) {
                                        case 0:
                                            Input.gI().createFormDoiThoiVang(player);
                                            break;
                                        case 1:
                                            Input.gI().createFormDoiXuBac(player);
                                            break;
                                        case 2:
                                            Input.gI().createFormDoiXuVang(player);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TRUONG_LAO_GURU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            Item mcl = InventoryService.gI().findItemBagByTemp(player, 1517);
                            int slMCL = (mcl == null) ? 0 : mcl.quantity;
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân",
                                            "Từ chối");//"Tham gia", "Đổi điểm\nThưởng\n[" + slMCL + "]", 
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
//                                            case 0:
//                                                if (TranhNgoc.gI().isTimeRegisterWar()) {
//                                                    if (player.iDMark.getTranhNgoc() == -1) {
//                                                        this.createOtherMenu(player, ConstNpc.REGISTER_TRANH_NGOC,
//                                                                "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\nPhe Cadic: " + TranhNgoc.gI().getPlayersCadic().size() + "\nPhe Fide: " + TranhNgoc.gI().getPlayersFide().size(),
//                                                                "Tham gia phe Cadic", "Tham gia phe Fide", "Đóng");
//                                                    } else {
//                                                        this.createOtherMenu(player, ConstNpc.LOG_OUT_TRANH_NGOC,
//                                                                "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\nPhe Cadic: " + TranhNgoc.gI().getPlayersCadic().size() + "\nPhe Fide: " + TranhNgoc.gI().getPlayersFide().size(),
//                                                                "Hủy\nĐăng Ký", "Đóng");
//                                                    }
//                                                    return;
//                                                }
//                                                Service.getInstance().sendPopUpMultiLine(player, 0, 7184, "Sự kiện sẽ mở đăng ký vào lúc " + TranhNgoc.HOUR_REGISTER + ":" + TranhNgoc.MIN_REGISTER + "\nSự kiện sẽ bắt đầu vào " + TranhNgoc.HOUR_OPEN + ":" + TranhNgoc.MIN_OPEN + " và kết thúc vào " + TranhNgoc.HOUR_CLOSE + ":" + TranhNgoc.MIN_CLOSE);
//                                                break;
//                                            case 1:// Shop
//                                                ShopService.gI().openShopSpecial(player, this,
//                                                        ConstNpc.SHOP_TRUONG_LAO_GURU, 0, -1);
//                                                break;
                                        }
                                        break;
                                    case ConstNpc.REGISTER_TRANH_NGOC:
                                        switch (select) {
                                            case 0:
                                                if (!player.getSession().actived) {
                                                    Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                                    return;
                                                }
                                                player.iDMark.setTranhNgoc((byte) 1);
                                                TranhNgoc.gI().addPlayersCadic(player);
                                                Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                                                break;
                                            case 1:
                                                if (!player.getSession().actived) {
                                                    Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                                    return;
                                                }
                                                player.iDMark.setTranhNgoc((byte) 2);
                                                TranhNgoc.gI().addPlayersFide(player);
                                                Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                                                break;
                                        }
                                        break;
                                    case ConstNpc.LOG_OUT_TRANH_NGOC:
                                        switch (select) {
                                            case 0:
                                                player.iDMark.setTranhNgoc((byte) -1);
                                                TranhNgoc.gI().removePlayersCadic(player);
                                                TranhNgoc.gI().removePlayersFide(player);
                                                Service.getInstance().sendThongBao(player, "Hủy đăng ký thành công");
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.VUA_VEGETA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                  Service.getInstance().sendThongBaoOK(player, "Chức năng tạm đóng");
                                  return;
//                                createOtherMenu(player, ConstNpc.BASE_MENU,
//                                        "|8|Muốn học skill gì thì lựa đi!!!",
//                                        "Học Skill 9", "Học Skill\nGồng Dame", "Học Skill\nPhân Thân");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0: //shop
                                            if (player.gender == 0) {
                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_9_TD, "|8| Skill Super Kame\n\n"
                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
                                                        + "\n Căng con mắt lên mà chọn cho đúng"
                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
                                                        "Nâng Skill\nThêm 1 Cấp");
                                            } else if (player.gender == 1) {
                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_9_NM, "|8| Skill Ma Phong Ba\n\n"
                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
                                                        + "\n Căng con mắt lên mà chọn cho đúng"
                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
                                                        "Nâng Skill\nThêm 1 Cấp");
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_9_XD, "|8| Skill Liên Hoàn Chưởng\n\n"
                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
                                                        + "\n Căng con mắt lên mà chọn cho đúng"
                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
                                                        "Nâng Skill\nThêm 1 Cấp");
                                            }
                                            break;
                                        case 1: //shop
                                            Service.getInstance().sendThongBao(player, "Skill này tạm chưa mở");
                                            return;
//                                            if (player.gender == 0) {
//                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_10_TD, "|8| Skill Gồng Cơ Đít Hít Cơ Mông\n\n"
//                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
//                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
//                                                        + "\n Căng con mắt lên mà chọn cho đúng"
//                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
//                                                        "Nâng Skill\nThêm 1 Cấp");
//                                            } else if (player.gender == 1) {
//                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_10_NM, "|8| Skill Gồng Cơ Đít Hít Cơ Mông\n\n"
//                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
//                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
//                                                        + "\n Căng con mắt lên mà chọn cho đúng"
//                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
//                                                        "Nâng Skill\nThêm 1 Cấp");
//                                            } else {
//                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_10_XD, "|8| Skill Gồng Cơ Đít Hít Cơ Mông\n\n"
//                                                        + "|7|Tổng 5 cấp độ,Chú ý chọn dúng cấp độ cần học"
//                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
//                                                        + "\n Căng con mắt lên mà chọn cho đúng"
//                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
//                                                        "Nâng Skill\nThêm 1 Cấp");
//                                            }
//                                            break;
                                        case 2: //skill 11
                                            Service.getInstance().sendThongBao(player, "Skill này tạm chưa mở");
                                            return;
//                                            if (player.gender == 0) {
//                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_11_TD, "|8| Skill Super Kame\n\n"
//                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
//                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
//                                                        + "\n Căng con mắt lên mà chọn cho đúng"
//                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
//                                                        "Nâng Skill\nThêm 1 Cấp");
//                                            } else if (player.gender == 1) {
//                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_11_NM, "|8| Skill Ma Phong Ba\n\n"
//                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
//                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
//                                                        + "\n Căng con mắt lên mà chọn cho đúng"
//                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
//                                                        "Nâng Skill\nThêm 1 Cấp");
//                                            } else {
//                                                this.createOtherMenu(player, ConstNpc.HOC_SKILL_11_XD, "|8| Skill Liên Hoàn Chưởng\n\n"
//                                                        + "|7|Chú ý chọn dúng cấp độ cần học"
//                                                        + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
//                                                        + "\n Căng con mắt lên mà chọn cho đúng"
//                                                        + "\n Admin không chịu trách nhiệm đâu nhé kkk",
//                                                        "Nâng Skill\nThêm 1 Cấp");
//                                            }
//                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == 7520042) {//Super Kamejoko
                                    switch (select) {
                                        case 0:
                                            Message msg;
                                            try {
                                                Skill curSkill = SkillUtil.getSkillbyId(player, Skill.SUPER_KAME);
                                                int level = curSkill.point;
                                                if (curSkill.point == 9) {
                                                    Service.getInstance().sendThongBao(player, "Kỹ năng đã đạt tối đa!");
                                                    return;
                                                } else if (player.nPoint.tiemNang < 999_999_999_999L * level) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ tiềm năng");
                                                    return;
                                                } else {
                                                    player.nPoint.tiemNang -= 999_999_999_999L * level;
                                                    Service.getInstance().point(player);
                                                    curSkill.point += 1;
                                                    SkillService.gI().upgradeSkillSpecial(player, 25, (byte) curSkill.point);
                                                    SkillService.gI().sendCurrLevelSpecial(player, curSkill);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nâng được skill 9 lên 1 cấp");
                                                }
                                            } catch (Exception e) {
                                                Log.error(UseItem.class, e);
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == 7520043) {//Liên hoàn chưởng
                                    switch (select) {
                                        case 0:
                                            Message msg;
                                            try {
                                                Skill curSkill = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                                                int level = curSkill.point;
                                                if (curSkill.point == 9) {
                                                    Service.getInstance().sendThongBao(player, "Kỹ năng đã đạt tối đa!");
                                                    return;
                                                } else if (player.nPoint.tiemNang < 999_999_999_999L * level) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ tiềm năng");
                                                    return;
                                                } else {
                                                    player.nPoint.tiemNang -= 999_999_999_999L * level;
                                                    Service.getInstance().point(player);
                                                    curSkill.point += 1;
                                                    SkillService.gI().upgradeSkillSpecial(player, 25, (byte) curSkill.point);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nâng được skill 9 lên 1 cấp");
                                                }
                                            } catch (Exception e) {
                                                Log.error(UseItem.class, e);
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == 7520044) {//skill liên hoàn chưởng
                                    switch (select) {
                                        case 0:
                                            try {
                                            Skill curSkill = SkillUtil.getSkillbyId(player, Skill.LIEN_HOAN_CHUONG);
                                            int level = curSkill.point;
                                            if (curSkill.point == 9) {
                                                Service.getInstance().sendThongBao(player, "Kỹ năng đã đạt tối đa!");
                                                return;
                                            } else if (player.nPoint.tiemNang < 999_999_999_999L * level) {
                                                Service.getInstance().sendThongBao(player, "Không đủ tiềm năng");
                                                return;
                                            } else {
                                                player.nPoint.tiemNang -= 999_999_999_999L * level;
                                                Service.getInstance().point(player);
                                                curSkill.point += 1;
                                                SkillService.gI().upgradeSkillSpecial(player, 25, (byte) curSkill.point);
                                                Service.getInstance().sendThongBao(player, "Mày đã nâng được skill 9 lên 1 cấp");
                                            }
                                        } catch (Exception e) {
                                            Log.error(UseItem.class, e);
                                        }
                                        break;

                                    }
                                }
                                // học cái skill nào?
                            }
                        }
                    };
                    break;
                case ConstNpc.EMI_FUKADA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "|7|TÍCH LUỸ SĂN BOSS NHẬN XU BẠC\n"
                                            + "|2|Tổng nhận từ mốc 10 đến 10000 Boss nhận về: 40.000 Xu bạc\n\n"
                                            + "|7|LÀM NHIỆM VỤ CỐT TRUYỆN NHẬN XU BẠC\n",
                                            "Quà Săn\nBoss");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                               long totalNap = player.getSession().tong_nap;
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            TaskService.gI().checkDoneAchivements(player);
                                            TaskService.gI().sendAchivement(player);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.ONG_GOHAN:// 3 thằng già
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                String mtv;
                                if (player.getSession().actived) {
                                    mtv = "Cảm ơn ae đã ghé chơi sv free của mình"
                                            + "\nChúc ae cày game vui vẻ!!!";
                                } else {
                                    mtv = "Hãy bấm mở thành viên miễn phí để!!!"
                                            + "kích hoạt đầy đủ tính năng";
                                }
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "|8|Server miễn phí, muốn có đồ thì cày\n\n"
                                            + "|3|Nếu Lười không muốn cày cũng có thể nạp tiền\n"
                                            + "|3|Để ủng hộ ad có kinh phí duy trì lâu dài nhaaa ^^"
                                                    .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                                            : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta")
                                            + "\n\n|2| ***" + mtv + "***",
                                            "Giftcode",
                                            "Nhận đệ tử",
                                            "Nhận Ngọc",
                                            "Mở Tv",
                                            "Nhập Code\nRiêng",
                                            "Đổi\nMật Khẩu");//
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            Input.gI().createFormGiftCode(player);
                                            break;
                                        case 1:
                                            if (player.pet == null) {
                                                PetService.gI().createNormalPet(player);
                                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                            } else {
                                                this.npcChat(player, "Tham Lam");
                                            }
                                            break;
                                        case 2:
                                            if (player.inventory.gem >= 100_000) {
                                                Service.getInstance().sendThongBao(player, 
                                                        "Ngọc nhận 100k ngọc là sài chán rồi\n"
                                                        + "khi nào gần hết thì quay lại nhận sau nhá");
                                                return;
                                            } else {
                                                player.inventory.gem += 100_000;
                                            }
                                            break;
                                        case 3:
                                            if (player.getSession().actived == true) {
                                                this.createOtherMenu(player, 53747,
                                                        "|7|Đã mtv không cần mở lại nữa",
                                                        "Ố kê");
                                            } else {
                                                this.createOtherMenu(player, 1456,
                                                        "|7|MỞ THÀNH VIÊN"
                                                        + "\n|5|Mở thành viên để có thể giao dịch và chat thế giới"
                                                        + "\n|3|Mtv sẽ cần 20.000 coin để mở ngay\n"
                                                        + "và free nếu đã qua nhiệm vụ fide\n"
                                                        + "\n|7|Bạn muốn mở thành viên theo cách nào?",
                                                        "Miễn Phí","Mở Ngay\n20.000\nCoin", "Đóng");
                                            }
                                            break;
                                        case 4:
                                            Input.gI().createGiftMember(player);
                                            break;
                                        case 5:
                                            Input.gI().createFormChangePassword(player);
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == 1456) {
                                    switch (select) {
                                        case 0:
                                            if (player.getSession().actived == true) {
                                                Service.getInstance().sendThongBao(player, "|4|Bạn đã mở thành viên rồi mà. Tiếp tục chơi game thui nào!!!!");
                                                return;
                                            }
                                            if (TaskService.gI().getIdTask(player) < ConstTask.TASK_22_0) {
                                                Service.getInstance().sendThongBao(player, "|7|Bạn cần hoàn thành nhiệm vụ Fide"
                                                        + "\nĐể có thể  mtv free!!"
                                                );
                                                return;
                                            } else {
                                                PlayerDAO.subActive(player, 1);
                                                player.getSession().actived = true;
                                                Service.getInstance().sendThongBao(player, "|2|Bạn đã mở thành viên Thành công."
                                                        + "\nĐã mở khóa chức năng Giao dịch và Chat thế giới !!");
                                            }
                                            break;
                                        case 1:
                                            if (player.getSession().actived == true) {
                                                Service.getInstance().sendThongBao(player, "|4|Bạn đã mở thành viên rồi mà. Tiếp tục chơi game thui nào!!!!");
                                                return;
                                            }
                                            if (player.getSession().vnd < 20_000) {
                                                Service.getInstance().sendThongBao(player, "|7|Thiếu coin\n"
                                                        + "bạn cần 20.000 coin để mở thành viên ngay\n"
                                                        + "Hãy nạp thêm tiền tại trang chủ hoặc thoát ra vào lại\n"
                                                        + "để cập nhật số coin nếu bạn đã nạp"
                                                );
                                                return;
                                            } else {
                                                PlayerDAO.subVnd(player, 20_000);
                                                PlayerDAO.subActive(player, 1);
                                                player.getSession().actived = true;
                                                Service.getInstance().sendThongBao(player, "|2|Bạn đã mở thành viên Thành công."
                                                        + "\nĐã mở khóa chức năng Giao dịch và Chat thế giới !!");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.BUNMA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ta có bán một số thứ mà có thế ngươi cũng del cần" + "\nMua thì mua ko mua thì mua", "Cửa\nhàng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:// Shop
                                            if (player.gender == ConstPlayer.TRAI_DAT) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_BUNMA_QK_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Ta có bán một số thứ mà có thế ngươi cũng del cần" + "\nNhưng ta ko thích bán cho ngươi đó", "Đóng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DENDE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    if (player.isHoldNamecBall) {
                                        this.createOtherMenu(player, ConstNpc.ORTHER_MENU,
                                                "Ô,ngọc rồng Namek,anh thật may mắn,nếu tìm đủ 7 viên ngọc có thể triệu hồi Rồng Thần Namek,",
                                                "Gọi rồng", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Ta có bán một số thứ mà có thế ngươi cũng del cần" + "\nMua thì mua ko mua thì mua", "Cửa\nhàng");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:// Shop
                                            if (player.gender == ConstPlayer.NAMEC) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_DENDE_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Ta có bán một số thứ mà có thế ngươi cũng del cần" + "\nNhưng ta ko thích bán cho ngươi đó", "Đóng");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.ORTHER_MENU) {
                                    NamekBallWar.gI().summonDragon(player, this);
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.APPULE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ta có bán một số thứ mà có thế ngươi cũng del cần" + "\nMua thì mua ko mua thì mua", "Cửa\nhàng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:// Shop
                                            if (player.gender == ConstPlayer.XAYDA) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_APPULE_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Ta có bán một số thứ mà có thế ngươi cũng del cần" + "\nNhưng ta ko thích bán cho ngươi đó",
                                                        "Đóng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DR_DRIEF:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (this.mapId == 84) {
                                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                            "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                            pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất"
                                                    : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                                } else if (this.mapId == 153) {
                                    Clan clan = pl.clan;
                                    ClanMember cm = pl.clanMember;
                                    if (cm.role == Clan.LEADER) {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Cần 1000 capsule bang [đang có " + clan.clanPoint
                                                + " capsule bang] để nâng cấp bang hội lên cấp "
                                                + (clan.level + 1) + "\n"
                                                + "+1 tối đa số lượng thành viên\n"
                                                + "+bang hội có tối đa là 50 cấp tương đương với 60 thành viên\n"
                                                + "|3|Cùng 1 thành viên trong bang đánh quái ở Lãnh địa bang hội để nhận được Capsule bang hội",
                                                "Về\nĐảoKame", "Góp " + cm.memberPoint + " capsule", "Nâng cấp",
                                                "Từ chối");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Bạn đang có " + cm.memberPoint
                                                + " capsule bang,bạn có muốn đóng góp toàn bộ cho bang hội của mình không ?",
                                                "Về\nĐảoKame", "Đồng ý", "Từ chối");
                                    }
                                } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar,
                                                "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                                "Đến\nNamếc", "Đến\nXayda", "Đến\nLasvegas");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 84) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                                } else if (mapId == 153) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMap(player, ConstMap.DAO_KAME, -1, 1059, 408);
                                            break;
                                        case 1:
                                            Clan clan = player.clan;
                                            if (clan == null) {
                                                Service.getInstance().sendThongBao(player, "Chưa có bang hội");
                                                return;
                                            }
                                            ClanMember cm = player.clanMember;
                                            player.clan.clanPoint += cm.memberPoint;
                                            cm.clanPoint += cm.memberPoint;
                                            cm.memberPoint = 0;
                                            Service.getInstance().sendThongBao(player, "Đóng góp thành công");
                                            break;
                                        case 2:
                                            Clan clan1 = player.clan;
                                            if (clan1 == null) {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn chưa có bang hội");
                                                return;
                                            }
                                            if (clan1.level >= 50) {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bang hội của bạn đã đạt cấp tối đa");
                                                return;
                                            }
                                            if (clan1.clanPoint < 1000) {
                                                Service.getInstance().sendThongBao(player, "Không đủ capsule");
                                            } else {
                                                clan1.level++;
                                                clan1.maxMember++;
                                                clan1.clanPoint -= 1000;
                                                Service.getInstance().sendThongBao(player,
                                                        "Bang hội của bạn đã được nâng cấp lên cấp " + clan1.level);
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                            break;
                                        case 1:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                            break;
                                        case 2:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CARGO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar,
                                                "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                                "Đến\nTrái Đất", "Đến\nXayda", "Đến\nLasvegas");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                            break;
                                        case 1:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                            break;
                                        case 2:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CUI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        private final int COST_FIND_BOSS = 20000000;

                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar,
                                                "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        if (this.mapId == 19) {

                                            int taskId = TaskService.gI().getIdTask(pl);
                                            switch (taskId) {
                                                case ConstTask.TASK_19_0:
                                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS)
                                                            + "  Vàng)",
                                                            "Đến Cold", "Đến\nNappa", "Từ chối");
                                                    break;
                                                case ConstTask.TASK_19_1:
                                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến chỗ\nMập đầu đinh\n("
                                                            + Util.numberToMoney(COST_FIND_BOSS) + " vàng)",
                                                            "Đến Cold", "Đến\nNappa", "Từ chối");
                                                    break;
                                                case ConstTask.TASK_19_2:
                                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS)
                                                            + " vàng)",
                                                            "Đến Cold", "Đến\nNappa", "Từ chối");
                                                    break;
                                                default:
                                                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến Cold", "Đến\nNappa", "Từ chối");

                                                    break;
                                            }
                                        } else if (this.mapId == 68) {
                                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                    "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                                        } else {
                                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                    "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                                    + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                                    "Đến\nTrái Đất", "Đến\nNamếc", "Đến\nLasvegas");

                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 26) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 19) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.KUKU);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone,
                                                                boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ  Vàng, còn thiếu "
                                                                + Util.numberToMoney(
                                                                        COST_FIND_BOSS - player.inventory.gold)
                                                                + "  Vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.MAP_DAU_DINH);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone,
                                                                boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ  Vàng, còn thiếu "
                                                                + Util.numberToMoney(
                                                                        COST_FIND_BOSS - player.inventory.gold)
                                                                + "  Vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.RAMBO);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone,
                                                                boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ  Vàng, còn thiếu "
                                                                + Util.numberToMoney(
                                                                        COST_FIND_BOSS - player.inventory.gold)
                                                                + "  Vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 68) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.SANTA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Xem con mẹ gì, có tiền không mà xem ?",
                                        "Shop\n Tạp Hóa", "Shop\n Phụ Kiện", "Item\n Hỗ Trợ", "Phụ Kiện\nĐệ Tử", "Shop\n Sự Kiện");

                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0: // shop vàng ngọc
                                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_SANTA_0, 0, -1);
                                                break;
                                            case 1: // shop thỏi vàng
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_1, 1, -1);
                                                break;
                                            case 2:// shop quy đổi
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_5, 5, -1);
                                                break;
                                            case 3:// shop vat pham
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_3, 3, -1);
                                                break;
                                            case 4: //Shop  Vật phẩm
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_4, 4, -1);
                                                break;
//                                            case 4: //tiệm hớt tóc
//                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_5, 5, -1);
//                                                break;
//                                            case 5: //tiệm hớt tóc
//                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_6, 6, -1);
//                                                break;
//                                            case 6: //tiệm hớt tóc
//                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_7, 7, -1);
//                                                break;

                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TRUONG_MY_LAN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Ngươi tìm ta có việc gì?",
                                        "Gia Hạn\nvật phẩm",
                                        "Nhận\n Chân mệnh");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.GIA_HAN_VAT_PHAM);
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                    switch (player.combineNew.typeCombine) {
                                        case CombineServiceNew.GIA_HAN_VAT_PHAM:
                                            CombineServiceNew.gI().startCombine(player);
                                            break;
                                    }
                                }

                            }
                        }
                    };
                    break;
                case ConstNpc.NGHE_CON:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "|8|Ở đây có bán phụ kiện dành cho đệ tử!!!\n"
                                        + "|3|Mình cũng có bán lại lễ bao tân thủ giá: 49k tv\n"
                                        + "|2|Và bán phụng thiên kích (hạn dùng 7 ngày) giá:1999k tv\n"
                                        + "|4|Gậy như ý FAKE giá:1999k tv\n"
                                        + "|7|Đổi 100k thỏi vàng để nhận về 99k thỏi vàng khoá",
                                        "Phụ Kiện\nĐệ Tử", "Mua Lại\nLễ Bao Tân Thủ", "Mua\nPhụng Thiên Kích", "Mua\nGậy Fake", "Đổi 100k\n Thỏi Vàng Khoá");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0: //shop
                                            ShopService.gI().openShopSpecial(player, this, ConstNpc.NGHE_CON_1, 0, -1);
                                            break;
                                        case 1: //shop 
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                Item thoivang = null;
                                                try {
                                                    thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                                                } catch (Exception e) {
                                                }
                                                if (thoivang == null || thoivang.quantity < 49999) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                                                    return;
                                                } else {
                                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 49999);
                                                    Item lebao = ItemService.gI().createNewItem((short) 1787);
                                                    InventoryService.gI().addItemBag(player, lebao, 99);
                                                    Service.getInstance().sendMoney(player);
                                                    InventoryService.gI().sendItemBags(player);
                                                    this.npcChat(player, "|1|Bạn nhận được lễ bao tân thủ");
                                                }
                                            } else {
                                                this.npcChat(player, "Hành trang không đủ chổ trống");
                                            }
                                            break;
                                        case 2: //shop 
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                Item thoivang = null;
                                                try {
                                                    thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                                                } catch (Exception e) {
                                                }
                                                if (thoivang == null || thoivang.quantity < 1999999) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                                                    return;
                                                } else {
                                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 1999999);
                                                    Item phungthienkich = ItemService.gI().createNewItem((short) ConstItem.PHUNG_THIEN_KICH);
                                                    phungthienkich.itemOptions.add(new ItemOption(0, Util.nextInt(300, 1000)));
                                                    phungthienkich.itemOptions.add(new ItemOption(3, Util.nextInt(1, 5)));
                                                    phungthienkich.itemOptions.add(new ItemOption(50, Util.nextInt(50, 150)));
                                                    phungthienkich.itemOptions.add(new ItemOption(77, Util.nextInt(50, 150)));
                                                    phungthienkich.itemOptions.add(new ItemOption(103, Util.nextInt(50, 150)));
                                                    phungthienkich.itemOptions.add(new ItemOption(101, Util.nextInt(50, 150)));
                                                    phungthienkich.itemOptions.add(new ItemOption(117, Util.nextInt(5, 15)));
                                                    phungthienkich.itemOptions.add(new ItemOption(93, 7));
                                                    phungthienkich.itemOptions.add(new ItemOption(199, 1));
                                                    InventoryService.gI().addItemBag(player, phungthienkich, 1);
                                                    Service.getInstance().sendMoney(player);
                                                    InventoryService.gI().sendItemBags(player);
                                                    this.npcChat(player, "|1|Bạn nhận được phụng thiên kích(HSD 7 ngày)");
                                                }
                                            } else {
                                                this.npcChat(player, "Hành trang không đủ chổ trống");
                                            }
                                            break;
                                        case 3: //shop 
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                Item thoivang = null;
                                                try {
                                                    thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                                                } catch (Exception e) {
                                                }
                                                if (thoivang == null || thoivang.quantity < 1999999) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                                                    return;
                                                } else {
                                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 1999999);
                                                    Item gayfake = ItemService.gI().createNewItem((short) ConstItem.GAY_FAKE);
                                                    gayfake.itemOptions.add(new ItemOption(50, Util.nextInt(40, 140)));
                                                    gayfake.itemOptions.add(new ItemOption(77, Util.nextInt(40, 140)));
                                                    gayfake.itemOptions.add(new ItemOption(103, Util.nextInt(40, 140)));
                                                    gayfake.itemOptions.add(new ItemOption(101, Util.nextInt(250, 500)));
                                                    gayfake.itemOptions.add(new ItemOption(93, 7));
                                                    gayfake.itemOptions.add(new ItemOption(199, 1));
                                                    gayfake.itemOptions.add(new ItemOption(30, 1));
                                                    InventoryService.gI().addItemBag(player, gayfake, 1);
                                                    Service.getInstance().sendMoney(player);
                                                    InventoryService.gI().sendItemBags(player);
                                                    this.npcChat(player, "|1|Bạn nhận được gậy như ý fake (HSD 7 ngày)");
                                                }
                                            } else {
                                                this.npcChat(player, "Hành trang không đủ chổ trống");
                                            }
                                            break;
                                        case 4: //shop 
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                Item thoivang = null;
                                                try {
                                                    thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                                                } catch (Exception e) {
                                                }
                                                if (thoivang == null || thoivang.quantity < 100000) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                                                    return;
                                                } else {
                                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 100000);
                                                    Item thoivangkhoa = ItemService.gI().createNewItem((short) ConstItem.THOI_VANG);
                                                    thoivangkhoa.quantity = 99000;
                                                    thoivangkhoa.itemOptions.add(new ItemOption(30, 1));
                                                    InventoryService.gI().addItemBag(player, thoivangkhoa, 99999999);
                                                    Service.getInstance().sendMoney(player);
                                                    InventoryService.gI().sendItemBags(player);
                                                    this.npcChat(player, "|1|Bạn nhận được 99k Thỏi Vàng Khoá");
                                                }
                                            } else {
                                                this.npcChat(player, "Hành trang không đủ chổ trống");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GIUMA_DAU_BO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 6 || this.mapId == 25 || this.mapId == 26) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
                                } else if (this.mapId == 139) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Người muốn trở về?", "Quay về", "Từ chối");
                                }//lãnh địa bang
                                else if (this.mapId == 153) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Theo ta, ta sẽ đưa ngươi đến Khu vực Thánh địa\nNơi đây ngươi sẽ truy tìm mảnh bông tai cấp 2 và Hồn bông tai để mở chỉ số Bông tai Cấp 3."
                                            + "\n|7|Ngươi có muốn đến đó không?", "Đến\nThánh địa", "Từ chối");
                                } else if (this.mapId == 156) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Người muốn trở về?", "Quay về", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (select == 0) {
                                            //đến potaufeu
                                            ChangeMapService.gI().goToPotaufeu(player);
                                        }
                                    }
                                } else if (this.mapId == 139) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            //về trạm vũ trụ
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 153) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            //lãnh địa bang
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 156, -1, -1);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 156) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            //về trạm vũ trụ
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 21 + player.gender, -1, -1);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.URON:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                this.openShopWithGender2(pl, ConstNpc.SHOP_URON_0, 0);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {

                            }
                        }
                    };
                    break;
                case ConstNpc.BA_HAT_MIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
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
                                            //                                            "Tẩy bỏ\nSao\npha lê",
                                            //                                            "Pháp Sư\ntrang bị",
                                            //                                            "Linh hoá\ntrang bị",
                                            //                                            "Tẩy Cs\nPháp Sư",
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
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI_X100);
                                                break;
//                                            case 2:
//                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.TAY_SAO_PHA_LE);
//                                                break;
//                                            case 3:
//                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHAP_SU_HOA);
//                                                break;
//                                            case 4:
//                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.LINH_HOA_TRANG_BI);
//                                                break;
//                                            case 5:
//                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.TAY_PHAP_SU);
//                                                break;

                                            case 2: //nâng cấp Chân mệnh
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
                                            case 3: //nâng cấp bông tai
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                                                break;
                                            case 4: //Mở chỉ số bông tai
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
                                                if ((trangSachCu != null && trangSachCu.quantity >= 9999) && (biaSach != null && biaSach.quantity >= 1)) {
                                                    createOtherMenu(player, ConstNpc.DONG_THANH_SACH_CU,
                                                            "|2|Chế tạo Cuốn sách cũ\n"
                                                            + "|1|Trang sách cũ " + trangSachCu.quantity + "/9999\n"
                                                            + "Bìa sách " + biaSach.quantity + "/1\n"
                                                            + "Tỉ lệ thành công: 60%\n"
                                                            + "Thất bại mất 99 trang sách và 1 bìa sách", "Đồng ý", "Từ chối");
                                                    break;
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
                                                    break;
                                                }
                                            case 1:
                                                Item cuonSachCu = InventoryService.gI().findItemBagByTemp(player, 1509);
                                                Item kimBam = InventoryService.gI().findItemBagByTemp(player, 1507);

                                                if ((cuonSachCu != null && cuonSachCu.quantity >= 10) && (kimBam != null && kimBam.quantity >= 1)) {
                                                    createOtherMenu(player, ConstNpc.DOI_SACH_TUYET_KY,
                                                            "|2|Đổi sách tuyệt kỹ 1\n"
                                                            + "|1|Cuốn sách cũ " + cuonSachCu.quantity + "/10\n"
                                                            + "Kìm bấm giấy " + kimBam.quantity + "/1\n"
                                                            + "Tỉ lệ thành công: 60%\n", "Đồng ý", "Từ chối");
                                                    break;
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
                                                short genderModifier = (player.gender == 0) ? -2 : ((player.gender == 2) ? 2 : (short) 0);

                                                Item sachTuyetKy = ItemService.gI().createNewItem((short) (baseValue + genderModifier));

                                                if (Util.isTrue(60, 100)) {
                                                    sachTuyetKy.itemOptions.add(new ItemOption(241, 0));
                                                    sachTuyetKy.itemOptions.add(new ItemOption(30, 0));
                                                    sachTuyetKy.itemOptions.add(new ItemOption(242, 5));
                                                    sachTuyetKy.itemOptions.add(new ItemOption(243, 1000));
                                                    try { // send effect susscess
                                                        Message msg = new Message(-81);
                                                        msg.writer().writeByte(0);
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeShort(tempId);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(1);
                                                        msg.writer().writeByte(2);
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, kimBam));
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, cuonSachCu));
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(7);
                                                        msg.writer().writeShort(sachTuyetKy.template.iconID);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                    } catch (Exception e) {
                                                        System.out.println("lỗi 4");
                                                    }
                                                    InventoryService.gI().addItemList(player.inventory.itemsBag, sachTuyetKy, 1);
                                                    InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, 10);
                                                    InventoryService.gI().subQuantityItemsBag(player, kimBam, 1);
                                                    InventoryService.gI().sendItemBags(player);
//                                                    npcChat(player, "Thành công gòi cu ơi");
                                                    return;
                                                } else {
                                                    try { // send effect faile
                                                        Message msg = new Message(-81);
                                                        msg.writer().writeByte(0);
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeShort(tempId);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(1);
                                                        msg.writer().writeByte(2);
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, kimBam));
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, cuonSachCu));
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(8);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                    } catch (Exception e) {
                                                        System.out.println("lỗi 3");
                                                    }
                                                    InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, 5);
                                                    InventoryService.gI().subQuantityItemsBag(player, kimBam, 1);
                                                    InventoryService.gI().sendItemBags(player);
//                                                    npcChat(player, "Thất bại gòi cu ơi");
                                                }
                                                return;
                                            case 1:
                                                break;
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
                                                        Message msg = new Message(-81);
                                                        msg.writer().writeByte(0);
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeShort(tempId);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();

                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(1);
                                                        msg.writer().writeByte(2);
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, trangSachCu));
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, biaSach));
                                                        player.sendMessage(msg);
                                                        msg.cleanup();

                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(7);
                                                        msg.writer().writeShort(cuonSachCu.template.iconID);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();

                                                    } catch (Exception e) {
                                                        System.out.println("lỗi 1");
                                                    }

                                                    InventoryService.gI().addItemList(player.inventory.itemsBag, cuonSachCu, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, trangSachCu, 9999);
                                                    InventoryService.gI().subQuantityItemsBag(player, biaSach, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    return;
                                                } else {
                                                    try { // send effect faile
                                                        Message msg = new Message(-81);
                                                        msg.writer().writeByte(0);
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeUTF("test");
                                                        msg.writer().writeShort(tempId);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(1);
                                                        msg.writer().writeByte(2);
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, biaSach));
                                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, trangSachCu));
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                        msg = new Message(-81);
                                                        msg.writer().writeByte(8);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        msg.writer().writeShort(-1);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                    } catch (Exception e) {
                                                        System.out.println("lỗi 2");
                                                    }
                                                    InventoryService.gI().subQuantityItemsBag(player, trangSachCu, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, biaSach, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                }
                                                return;
                                            case 1:
                                                break;
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
                                            //START _ SÁCH TUYỆT KỸ//
                                            case CombineServiceNew.GIAM_DINH_SACH:
                                            case CombineServiceNew.TAY_SACH:
                                            case CombineServiceNew.NANG_CAP_SACH_TUYET_KY:
                                            case CombineServiceNew.PHUC_HOI_SACH:
                                            case CombineServiceNew.PHAN_RA_SACH:
                                                //END _ SÁCH TUYỆT KỸ//
                                                if (select == 0) {
                                                    CombineServiceNew.gI().startCombine(player);
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.LONG_NU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Long Đế xuất hiện, Giết hắn để"
                                        + "\n Nhận được các mảnh long ấn"
                                        + "\n Và có cơ hội kí khế ước với hắn",
                                        "Kích hoạt\nẤn", "Kích hoạt\n Long Ấn");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5 || this.mapId == 13) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.AN_TRANG_BI);
                                                break;
                                            case 1:
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.LONG_AN_TRANG_BI);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                        switch (player.combineNew.typeCombine) {
                                            case CombineServiceNew.AN_TRANG_BI:
                                            case CombineServiceNew.LONG_AN_TRANG_BI:
                                                CombineServiceNew.gI().startCombine(player);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RUONG_DO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                InventoryService.gI().sendItemBox(player);
                                InventoryService.gI().openBox(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {

                            }
                        }
                    };
                    break;
                case ConstNpc.DAU_THAN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                player.magicTree.openMenuTree();
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                                        if (select == 0) {
                                            player.magicTree.harvestPea();
                                        } else if (select == 1) {
                                            if (player.magicTree.level == 10) {
                                                player.magicTree.fastRespawnPea();
                                            } else {
                                                player.magicTree.showConfirmUpgradeMagicTree();
                                            }
                                        } else if (select == 2) {
                                            player.magicTree.fastRespawnPea();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                                        if (select == 0) {
                                            player.magicTree.harvestPea();
                                        } else if (select == 1) {
                                            player.magicTree.showConfirmUpgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                                        if (select == 0) {
                                            player.magicTree.upgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_UPGRADE:
                                        if (select == 0) {
                                            player.magicTree.fastUpgradeMagicTree();
                                        } else if (select == 1) {
                                            player.magicTree.showConfirmUnuppgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                                        if (select == 0) {
                                            player.magicTree.unupgradeMagicTree();
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.ITACHI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Ngước mặt lên trời... Hận đời vô đối!!!",
                                        "Đến map\nVar itachi");//, "Triệu Hồi Boss"
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            Item thoivang = null;
                                            try {
                                                thoivang = InventoryService.gI().findItemBagByTemp(player, (short) 457);
                                            } catch (Exception e) {
                                            }
                                            if (thoivang == null || thoivang.quantity < 99) {
                                                Service.getInstance().sendThongBao(player, "|3|Chưa đủ mảnh phong ấn boss");
                                                return;
                                            }
                                            if (player.nPoint.dame < 10000000) {
                                                Service.getInstance().sendThongBao(player, "|7|Sức đánh tối thiểu 10 triệu mới đủ tuổi");
                                                return;
                                            } else {
                                                InventoryService.gI().subQuantityItemsBag(player, thoivang, 99);
                                                Service.getInstance().sendMoney(player);
                                                InventoryService.gI().sendItemBags(player);
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 213, -1, -1);
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_TRIEU_HOI_BOSS) {
                                    switch (select) {
                                        case 0:
                                            Item phonganboss = InventoryService.gI().findItemBagByTemp(player, (short) 1557);
                                            if (phonganboss == null || phonganboss.quantity < 99) {
                                                Service.getInstance().sendThongBao(player, "|3|Chưa đủ mảnh phong ấn boss");
                                                return;
                                            } else {
                                                Boss nhatvi = BossFactory.createBoss(BossFactory.BLACKGOKU);
                                                nhatvi.zone = player.zone;
                                                nhatvi.location.x = player.location.x;
                                                nhatvi.location.y = player.location.y;
                                                InventoryService.gI().subQuantityItemsBag(player, phonganboss, 99);
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendMoney(player);
                                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Nhất Vĩ");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.VADOS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Ngước mặt lên trời... Hận đời vô đối!!!",
                                        "Triệu Hồi Boss");
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS,
                                                    "|7|Muốn triệu hồi BOSS nào!!!\n"
                                                            + "|8|Heart Gold rơi thuốc tăng sức đánh gốc\n"
                                                            + "|3|Triệu hồi cần 20 Mảnh Phong Ấn Boss\n\n"
                                                            + "|8|Cooler Gold rơi thuốc tăng hp-ki gốc\n"
                                                            + "|3|Triệu hồi cần 10 Mảnh Phong Ấn Boss\n",
                                                    "HEART\nGOLD", "COOLER\nGOLD"
                                            );
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_TRIEU_HOI_BOSS) {
                                    Item phonganboss = InventoryService.gI().findItemBagByTemp(player, (short) 1557);
                                    switch (select) {
                                        case 0:
                                            if(phonganboss == null || phonganboss.quantity < 20){
                                            Service.getInstance().sendThongBaoOK(player, "Chưa đủ mảnh phong ấn boss\n"
                                                    + "Boss này triệu hồi cần có 20 mảnh phong ấn boss\n"
                                                    + "Mảnh phong ấn kiếm được từ săn các boss mới");
                                            return;
                                            }
                                            InventoryService.gI().subQuantityItemsBag(player, phonganboss, 20);
                                            InventoryService.gI().sendItemBags(player);
                                            Boss.trieuHoiBoss(player, BossFactory.HEART_GOLD);
                                            Service.getInstance().sendThongBaoAllPlayer("Người chơi " + player.name + " Đã triệu hồi boss Heart Gold");
                                            Service.getInstance().sendThongBao(player,"Triệu hồi thành công boss Heart Gold");
                                            break;
                                        case 1:
                                            if(phonganboss == null || phonganboss.quantity < 10){
                                            Service.getInstance().sendThongBaoOK(player, "Chưa đủ mảnh phong ấn boss\n"
                                                    + "Boss này triệu hồi cần có 10 mảnh phong ấn boss\n"
                                                    + "Mảnh phong ấn kiếm được từ săn các boss mới");
                                            return;
                                            }
                                            InventoryService.gI().subQuantityItemsBag(player, phonganboss, 10);
                                            InventoryService.gI().sendItemBags(player);
                                            Boss.trieuHoiBoss(player, BossFactory.COOLER_GOLD);
                                            Service.getInstance().sendThongBaoAllPlayer("Người chơi " + player.name + " Đã triệu hồi boss Cooler Gold");
                                            Service.getInstance().sendThongBao(player,"Triệu hồi thành công boss Cooler Gold");
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.KAIDO:

                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Vô địch chán voãi ò...!!!"
                                        + "Lại đây để tao gõ mày một cái nào",
                                        "Đến\nVar Kaido", "Giải tán\nbang", "Danh hiệu đệ\n 49k H.Ngọc", "Danh hiệu đệ\n 199k H.Ngọc");

                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            if (player.nPoint.dame < 100000000) {
                                                Service.getInstance().sendThongBao(player, "|7|Sức đánh tối thiểu 100 triệu mới đủ tuổi");
                                                return;
                                            } else {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 218, -1, -1);
                                            }
                                            break;
                                        case 2:
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                if (player.inventory.ruby < 49999) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ Hồng Ngọc");
                                                } else {
                                                    player.inventory.ruby -= 49999;
                                                    Item daithan = ItemService.gI().createNewItem((short) 1325);
                                                    daithan.itemOptions.add(new ItemOption(50, Util.nextInt(10, 33)));
                                                    daithan.itemOptions.add(new ItemOption(77, Util.nextInt(10, 39)));
                                                    daithan.itemOptions.add(new ItemOption(103, Util.nextInt(10, 39)));
                                                    daithan.itemOptions.add(new ItemOption(101, Util.nextInt(50, 150)));
                                                    daithan.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                                                    InventoryService.gI().addItemBag(player, daithan, 1);
                                                    Service.getInstance().sendMoney(player);
                                                    InventoryService.gI().sendItemBags(player);
                                                    this.npcChat(player, "|1|Bạn nhận được Danh Hiệu Đại Thần");
                                                }
                                            } else {
                                                this.npcChat(player, "Hành trang không đủ chổ trống");
                                            }
                                            break;
                                        case 3:
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                if (player.inventory.ruby < 199999) {
                                                    Service.getInstance().sendThongBao(player, "Không đủ Hồng Ngọc");
                                                } else {
                                                    player.inventory.ruby -= 199999;
                                                    Item thientu = ItemService.gI().createNewItem((short) 1326);
                                                    thientu.itemOptions.add(new ItemOption(50, Util.nextInt(20, 55)));
                                                    thientu.itemOptions.add(new ItemOption(77, Util.nextInt(20, 59)));
                                                    thientu.itemOptions.add(new ItemOption(103, Util.nextInt(20, 59)));
                                                    thientu.itemOptions.add(new ItemOption(101, Util.nextInt(100, 345)));
                                                    thientu.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
                                                    InventoryService.gI().addItemBag(player, thientu, 1);
                                                    Service.getInstance().sendMoney(player);
                                                    InventoryService.gI().sendItemBags(player);
                                                    this.npcChat(player, "|1|Bạn nhận được Danh Hiệu Thiên Tử");
                                                }
                                            } else {
                                                this.npcChat(player, "Hành trang không đủ chổ trống");
                                            }
                                            break;

                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TIEN_HAC_AM:

                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Lũ sâu kiến... !!!",
                                        "Đến\n Khiêu Chiến ");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 217, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.MAP_FAM:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "|7|MAP FAM\n"
                                        + "|2|Phí vào map:\n"
                                        + "- MAP FAM THỎI VÀNG - 1000 Xu bạc\n"
                                        + "|1|Các map fam mảnh vỡ bông tai, mảnh hồn vào miễn phí\n",
                                        "FAM\nMảnh\nChân Mệnh", "FAM\nThỏi Vàng", "FAM\nĐá Cường\nHoá","FAM\nMảnh Vỡ\nBt 2","FAM\nMảnh Vỡ\nBt 3","FAM\nMảnh Vỡ\nBt 4","FAM\nMảnh Hồn\nBt");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                Item xuBac = InventoryService.gI().findItemBagByTemp(player, 457);
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 215, -1, -1);
                                            break;
                                        case 1:
                                            if (xuBac == null || xuBac.quantity < 1000) {
                                                Service.getInstance().sendThongBaoOK(player, "Không đủ xu bạc, cần 1000 xu bạc làm phí vào map");
                                                return;
                                            }
                                            InventoryService.gI().subQuantityItemsBag(player, xuBac, 1000);
                                            InventoryService.gI().sendItemBags(player);
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 214, -1, -1);
                                            break;
                                        case 2:
                                            if (xuBac == null || xuBac.quantity < 1000) {
                                                Service.getInstance().sendThongBaoOK(player, "Không đủ xu bạc, cần 1000 xu bạc làm phí vào map\n"
                                                        + "map fam đá cường hoá");
                                                return;
                                            }
                                            InventoryService.gI().subQuantityItemsBag(player, xuBac, 1000);
                                            InventoryService.gI().sendItemBags(player);
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 216, -1, -1);
                                            break;
                                        case 3:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 156, -1, -1);
                                            break;
                                        case 4:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 157, -1, -1);
                                            break;
                                        case 5:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 158, -1, -1);
                                            break;
                                        case 6:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 159, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CALICK:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        private final byte COUNT_CHANGE = 50;
                        private int count;

                        private void changeMap() {
                            if (this.mapId != 102) {
                                count++;
                                if (this.count >= COUNT_CHANGE) {
                                    count = 0;
                                    this.map.npcs.remove(this);
                                    Map map = MapService.gI().getMapForCalich();
                                    this.mapId = map.mapId;
                                    this.cx = Util.nextInt(100, map.mapWidth - 100);
                                    this.cy = map.yPhysicInTop(this.cx, 0);
                                    this.map = map;
                                    this.map.npcs.add(this);
                                }
                            }
                        }

                        @Override
                        public void openBaseMenu(Player player) {
                            player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                            if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                                Service.getInstance().hideWaitDialog(player);
                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                return;
                            }
                            if (this.mapId != player.zone.map.mapId) {
                                Service.getInstance().sendThongBao(player, "Calích đã rời khỏi map!");
                                Service.getInstance().hideWaitDialog(player);
                                return;
                            }

                            if (this.mapId == 102) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
                                        "Kể\nChuyện", "Quay về\nQuá khứ");
                            } else {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
                                        "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (this.mapId == 102) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        // kể chuyện
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                                    } else if (select == 1) {
                                        // về quá khứ
                                        ChangeMapService.gI().goToQuaKhu(player);
                                    }
                                }
                            } else if (player.iDMark.isBaseMenu()) {
                                if (select == 0) {
                                    // kể chuyện
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                                } else if (select == 1) {
                                    // đến tương lai
                                    // changeMap();
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                                        ChangeMapService.gI().goToTuongLai(player);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.JACO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "|7| KHU VỰC BOSS NHÂN BẢN"
                                            + "\n\n|6|Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu"
                                            + "\nĐánh bại những kẻ giả mạo ngươi sẽ nhận được những phần thưởng hấp dẫn"
                                            + "\n|3|Hạ Boss Nhân Bản sẽ nhận được Item Siêu cấp"
                                            + "\n|2|Hãy đến đó ngay",
                                            "Đến \nPotaufeu");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Người muốn trở về?", "Quay về", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (select == 0) {
                                            ChangeMapService.gI().goToPotaufeu(player);
                                        }
                                    }
                                } else {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.THAN_MEO_KARIN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (mapId == ConstMap.THAP_KARIN) {
                                    if (player.zone instanceof ZSnakeRoad) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Hãy cầm lấy hai hạt đậu cuối cùng ở đây\nCố giữ mình nhé "
                                                + player.name,
                                                "Cảm ơn\nsư phụ");
                                    } else if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Chào con, con muốn ta giúp gì nào?", getMenuSuKien(EVENT_SEVER));
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (mapId == ConstMap.THAP_KARIN) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (player.zone instanceof ZSnakeRoad) {
                                            switch (select) {
                                                case 0:
                                                    player.setInteractWithKarin(true);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Hãy mau bay xuống chân tháp Karin");
                                                    break;
                                            }
                                        } else {
                                            switch (select) {
                                                case 0:
                                                    switch (EVENT_SEVER) {
                                                        case 2:
                                                            Attribute at = ServerManager.gI().getAttributeManager()
                                                                    .find(ConstAttribute.TNSM);
                                                            String text = "Sự kiện 20/11 chính thức tại Ngọc Rồng "
                                                                    + Manager.SERVER_NAME + "\n "
                                                                    + "Số điểm hiện tại của bạn là : "
                                                                    + player.event.getEventPoint()
                                                                    + "\nTổng số hoa đã tặng trên toàn máy chủ "
                                                                    + EVENT_COUNT_THAN_MEO % 999 + "/999";
                                                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_SUKIEN,
                                                                    at != null && !at.isExpired() ? text
                                                                    + "\nToàn bộ máy chủ được tăng 20% TNSM cho đệ tử khi đánh quái,thời gian còn lại "
                                                                    + at.getTime() / 60 + " phút."
                                                                    : text + "\nKhi tặng đủ 999 bông hoa toàn bộ máy chủ được tăng tăng 20% TNSM cho đệ tử trong 60 phút\n",
                                                                    "Tặng 1\n Bông hoa", "Tặng\n10 Bông",
                                                                    "Tặng\n99 Bông", "Đổi\nHộp quà");
                                                            break;
                                                    }
                                            }
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_SUKIEN) {
                                        openMenuSuKien(player, this, tempId, select);
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.THUONG_DE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 45) {
                                    int lanTiepTheo = (player.checkquachuyencan + 1) * 7;
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "|8|ĐIỂM DANH HÀNG NGÀY NHẬN HỒNG NGỌC\n"
                                            + "\n- Đã điểm danh: " + player.diemdanh + " Lần trong ngày"
                                            + "\n- Và đã điểm danh" + player.chuyencan + " Lần Trong Tháng\n"
                                            + "Hôm nay điểm danh nhận: " + (player.chuyencan == 0 ? 10000 : (10000 + (player.chuyencan * 1000))) + " hồng ngọc\n"
                                            + "Quà chuyên cần 7 ngày lần này: " + ((player.checkquachuyencan + 1) * 50000) + " Hồng Ngọc(" + (player.chuyencan >= lanTiepTheo ? " Có thể nhận)" :("Thiếu " + (lanTiepTheo - player.chuyencan) +" ngày)"))
                                            + "Ngoài ra có thể làm nhiệm vụ bò mộng, và fam quái tại coolder, tương lai\n"
                                            + "để kiếm được hồng ngọc",
                                            "Đến Kaio", "Điểm danh", "Quay số\nmay mắn", "Nhận Quà\nChuyên cần");

                                } else if (player.zone instanceof ZSnakeRoad) {
                                    if (mapId == ConstMap.CON_DUONG_RAN_DOC) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy lắm lấy tay ta mau",
                                                "Về thần điện");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 45) {
                                    int lanTiepTheo = (player.checkquachuyencan + 1) * 7;
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                                break;
                                            case 1:
                                                if (player.diemdanh != 0) {
                                                    Service.getInstance().sendThongBao(player, "Mỗi ngày nhận quà 1 lần thôi");
                                                    return;
                                                } else {
                                                    int soLuong = player.chuyencan == 0 ? 10000 : player.chuyencan * 10000;
                                                    player.inventory.ruby += soLuong;
                                                    InventoryService.gI().sendItemBags(player);
                                                    PlayerDAO.CongPointDiemDanh(player, 1);
                                                    PlayerDAO.ChuyenCan(player, 1);
                                                    Service.getInstance().sendMoney(player);
                                                    Service.getInstance().sendThongBao(player, "Nhận được " + soLuong + " HỒNG NGỌC");
                                                }
                                                break;
                                            case 2:
                                                this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                                        "Con muốn làm gì nào?", "Quay bằng\nvàng",
                                                        "Rương phụ\n("
                                                        + (player.inventory.itemsBoxCrackBall.size()
                                                        - InventoryService.gI().getCountEmptyListItem(
                                                                player.inventory.itemsBoxCrackBall))
                                                        + " món)",
                                                        "Xóa hết\ntrong rương", "Đóng");
                                                break;
                                            case 3: {
                                                NpcService.gI().createMenuConMeo(
                                                        player,
                                                        ConstNpc.MENU_THUONG_NGAY,
                                                        -1,
                                                        "Bạn đã điểm danh: " + player.chuyencan + " ngày"
                                                        + "Quà chuyên cần 7 ngày lần này: "
                                                        + ((player.checkquachuyencan + 1) * 50000) + " Hồng Ngọc",
                                                        "Quà 7 Ngày\n" + (player.chuyencan >= lanTiepTheo ? "(Có thể nhận)"
                                                                : ("(" + (lanTiepTheo - player.chuyencan) + " ngày nữa nhận)"))
                                                );
                                                break;
                                            }
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_TRAIN_OFFLINE) {
                                        switch (select) {
                                            case 0:
                                                Service.getInstance().sendPopUpMultiLine(player, tempId, this.avartar, ConstNpc.INFOR_TRAIN_OFFLINE);
                                                break;
                                            case 1:
                                                player.istrain = true;
                                                NpcService.gI().createTutorial(player, this.avartar, "Từ giờ, quá 30 phút Offline con sẽ tự động luyện tập");
                                                break;
                                            case 3:
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                                        switch (select) {
                                            case 0:
                                                LuckyRoundService.gI().openCrackBallUI(player,
                                                        LuckyRoundService.USING_GOLD);
                                                break;
                                            case 1:
                                                ShopService.gI().openBoxItemLuckyRound(player);
                                                break;
                                            case 2:
                                                NpcService.gI().createMenuConMeo(player,
                                                        ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                                        "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                                        + "sẽ không thể khôi phục!",
                                                        "Đồng ý", "Hủy bỏ");
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_THUONG_NGAY) {
                                        switch (select) {
                                            case 0:
                                                if (player.chuyencan < lanTiepTheo) {
                                                    Service.getInstance().sendThongBao(player, "Bạn cần điểm danh thêm " + (lanTiepTheo - player.chuyencan) + " ngày");
                                                    return;
                                                }
                                                int soLuong = (player.checkquachuyencan + 1) * 50000;
                                                player.inventory.ruby += soLuong;
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendMoney(player);
                                                Service.getInstance().sendThongBao(player, "Nhận được " + soLuong + " Hồng ngọc");
                                                PlayerDAO.CheckQuaChuyenCan(player, 1);
                                        }
                                    }
                                } else if (player.zone instanceof ZSnakeRoad) {
                                    if (mapId == ConstMap.CON_DUONG_RAN_DOC) {
                                        ZSnakeRoad zroad = (ZSnakeRoad) player.zone;
                                        if (zroad.isKilledAll()) {
                                            SnakeRoad road = (SnakeRoad) zroad.getDungeon();
                                            ZSnakeRoad egr = (ZSnakeRoad) road.find(ConstMap.THAN_DIEN);
                                            egr.enter(player, 360, 408);
                                            Service.getInstance().sendThongBao(player, "Hãy xuống gặp thần mèo Karin");
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Hãy tiêu diệt hết quái vật ở đây!");
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.THAN_VU_TRU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn làm gì nào", "Di chuyển");//,getMenuSuKien(EVENT_SEVER)
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                                        "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio",
                                                        "Con\nđường\nrắn độc", "Từ chối");//, getMenuSuKien(EVENT_SEVER)
                                                break;
//                                            case 1:
//                                                switch (EVENT_SEVER) {
//                                                    case 2:
//                                                        Attribute at = ServerManager.gI().getAttributeManager()
//                                                                .find(ConstAttribute.HP);
//                                                        String text = "Sự kiện 20/11 chính thức tại Ngọc Rồng "
//                                                                + Manager.SERVER_NAME + "\n "
//                                                                + "Số điểm hiện tại của bạn là : "
//                                                                + player.event.getEventPoint()
//                                                                + "\nTổng số hoa đã tặng trên toàn máy chủ "
//                                                                + EVENT_COUNT_THAN_VU_TRU % 999 + "/999";
//                                                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_SUKIEN,
//                                                                at != null && !at.isExpired() ? text
//                                                                + "\nToàn bộ máy chủ được tăng 20% HP,thời gian còn lại "
//                                                                + at.getTime() / 60 + " phút."
//                                                                : text + "\nKhi tặng đủ 999 bông hoa toàn bộ máy chủ được tăng 20% HP trong 60 phút\n",
//                                                                "Tặng 1\n Bông hoa", "Tặng\n10 Bông", "Tặng\n99 Bông",
//                                                                "Đổi\nHộp quà");
//                                                        break;
//                                                }
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                                break;
                                            case 2:
                                                if (player.clan != null && player.clan.maxMember > 100) {
                                                    Service.getInstance().sendThongBao(player, "Bạn không được vào khi đang ở bang hội này!!!");
                                                    return;
                                                }
                                                // con đường rắn độc
                                                // Service.getInstance().sendThongBao(player, "Comming Soon.");
                                                if (player.clan != null) {
                                                    Calendar calendar = Calendar.getInstance();
                                                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                                    if (!(dayOfWeek == Calendar.MONDAY
                                                            || dayOfWeek == Calendar.WEDNESDAY
                                                            || dayOfWeek == Calendar.FRIDAY
                                                            || dayOfWeek == Calendar.SUNDAY)) {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Chỉ mở vào thứ 2, 4, 6, CN hàng tuần!");
                                                        return;
                                                    }
                                                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 7) {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Phải tham gia bang hội ít nhất 7 ngày mới có thể tham gia!");
                                                        return;
                                                    }
                                                    if (player.clan.snakeRoad == null) {
                                                        this.createOtherMenu(player, ConstNpc.MENU_CHON_CAP_DO,
                                                                "Hãy mau trở về bằng con đường rắn độc\nbọn Xayda đã đến Trái Đất",
                                                                "Chọn\ncấp độ", "Từ chối");
                                                    } else {
                                                        if (player.clan.snakeRoad.isClosed()) {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Bang hội đã hết lượt tham gia!");
                                                        } else {
                                                            this.createOtherMenu(player,
                                                                    ConstNpc.MENU_ACCEPT_GO_TO_CDRD,
                                                                    "Con có chắc chắn muốn đến con đường rắn độc cấp độ "
                                                                    + player.clan.snakeRoad.getLevel() + "?",
                                                                    "Đồng ý", "Từ chối");
                                                        }
                                                    }
                                                } else {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Chỉ dành cho những người trong bang hội!");
                                                }
                                                break;

                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHON_CAP_DO) {
                                        switch (select) {
                                            case 0:
                                                Input.gI().createFormChooseLevelCDRD(player);
                                                break;

                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_CDRD) {
                                        switch (select) {
                                            case 0:
                                                if (player.clan != null && player.clan.maxMember > 100) {
                                                    Service.getInstance().sendThongBao(player, "Bạn không được vào khi đang ở bang hội này!!!");
                                                    return;
                                                }
                                                if (player.clan != null) {
                                                    synchronized (player.clan) {
                                                        if (player.clan.snakeRoad == null) {
                                                            int level = Byte.parseByte(
                                                                    String.valueOf(PLAYERID_OBJECT.get(player.id)));
                                                            SnakeRoad road = new SnakeRoad(level);
                                                            ServerManager.gI().getDungeonManager().addDungeon(road);
                                                            road.join(player);
                                                            player.clan.snakeRoad = road;
                                                        } else {
                                                            player.clan.snakeRoad.join(player);
                                                        }
                                                    }
                                                }
                                                break;

                                        }
                                    }
//                                    else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_SUKIEN) {
//                                        openMenuSuKien(player, this, tempId, select);
//                                    }
                                }
                            }
                        }

                    };
                    break;
                case ConstNpc.KIBIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Đến\nKaio", "Từ chối");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TO_SU_KAIO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                Service.getInstance().sendThongBaoOK(player, "Chức năng tạm chưa mở");
                                return;
//                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "|7|CHỨC NĂNG KIM ĐAN\n"
//                                            + "|3|Kim đan có được từ sự kiện trung thu\n"
//                                            + "Kim đan sẽ có thể nâng cấp tương tự PTK\n"
//                                            + "|2|Chỉ số khi nâng cấp sẽ phụ thuộc vào phẩm chất\n"
//                                            + "*Chỉ số cơ bản\n"
//                                            + "-Kim Đan Hoàn Mĩ 50% sd, 60% hpki\n"
//                                            + "-Kim Đan Cao Cấp 40% sd, 50% hpki\n"
//                                            + "-Kim Đan Trung Cấp 30% sd, 40% hpki\n"
//                                            + "-Kim Đan Sơ Cấp 20% sd, 30% hpki\n"
//                                            + "|3|Khi tăng cấp chỉ số sẽ tăng thêm mỗi cấp độ\n"
//                                            + "Chỉ số sẽ cộng dồn ví dụ:\n"
//                                            + "- Kim đan hoàn mĩ khi lên cấp 2 sẽ là 100% Sd\n"
//                                            + "= 50 + 50, nhưng cấp 3 sẽ là 100 + 100, cấp 4 là 200 + 150\n"
//                                            + "Kim đan sơ cấp thì chỉ số sẽ thấp hơn nhiều\n"
//                                            + "Ae nên cân nhắc trước khi nâng cấp",
//                                            "Nâng Cấp","Tăng\nPhẩm Chất","Shop\nSự Kiện", "Từ chối");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_KIM_DAN);
                                            break;
                                        case 1:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.TANG_PHAM_CHAT_KIM_DAN);
                                            break;
                                        case 2:
                                            ShopService.gI().openShopSpecial(player, this, ConstNpc.TRUNG_THU_SHOP, 0, -1);
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                    switch (player.combineNew.typeCombine) {
                                        case CombineServiceNew.NANG_CAP_KIM_DAN:
                                        case CombineServiceNew.TANG_PHAM_CHAT_KIM_DAN:
                                            CombineServiceNew.gI().startCombine(player);
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_KIM_DAN) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_TANG_PHAM_CHAT_KIM_DAN) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.OSIN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                                } else if (this.mapId == 52) {
                                    if (MabuWar.gI().isTimeMabuWar() || MabuWar14h.gI().isTimeMabuWar()) {
                                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                    "Bây giờ tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể... \n"
                                                    + "Quý vị nào muốn đi theo thì xin mời !",
                                                    "Ok", "Từ chối");
                                        }
                                    } else {
                                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                    "Vào lúc 12h tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể... \n"
                                                    + "Quý vị nào muốn đi theo thì xin mời !",
                                                    "Ok", "Từ chối");
                                        }
                                    }
                                } else if (this.mapId == 154) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "|3|Để đến được Hành tinh ngục tù yêu cầu mang 5 món đồ Hủy diệt"
                                            + "\n|1|Ta có thể giúp gì cho ngươi ?",
                                            "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
                                } else if (this.mapId == 155 || this.mapId == 165) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Quay về", "Từ chối");
                                } else if (MapService.gI().isMapMabuWar(this.mapId) || MapService.gI().isMapMabuWar14H(this.mapId)) {
                                    if (MabuWar.gI().isTimeMabuWar()) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Đừng vội xem thường Babyđây,ngay đến cha hắn là thần ma đạo sĩ\n"
                                                + "Bibiđây khi còn sống cũng phải sợ hắn đấy",
                                                "Giải trừ\nphép thuật\n50Tr Vàng",
                                                player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
                                    } else if (MabuWar14h.gI().isTimeMabuWar()) {
                                        createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ phù hộ cho ngươi bằng nguồn sức mạnh của Thần Kaiô"
                                                + "\n+1 triệu HP, +1 triệu MP, +10k Sức đánh"
                                                + "\nLưu ý: sức mạnh sẽ biến mất khi ngươi rời khỏi đây",
                                                "Phù hộ\n55 hồng ngọc", "Từ chối", "Về\nĐại Hội\nVõ Thuật");
                                    }
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 52) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                if (MabuWar.gI().isTimeMabuWar()) {
                                                    ChangeMapService.gI().changeMap(player, 114, -1, 354, 240);
                                                } else {
                                                    ChangeMapService.gI().changeMap(player, 127, -1, 354, 240);
                                                }
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 154) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                                break;
                                            case 1:
                                                if (!Manager.gI().getGameConfig().isOpenPrisonPlanet()) {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Lối vào hành tinh ngục tù chưa mở");
                                                    return;
                                                }
                                                if (player.nPoint.power < 60000000000L) {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Yêu cầu tối thiếu 60tỷ sức mạnh");
                                                    return;
                                                }
                                                if (player.setClothes.setDHD != 5) {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Yêu cầu mang set Đồ Hủy diệt");
                                                    return;
                                                }
                                                ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 155) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (select == 0) {
                                            ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                        }
                                    }
                                } else if (this.mapId == 165) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (select == 0) {
                                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                        }
                                    }
                                } else if (MapService.gI().isMapMabuWar(this.mapId) || MapService.gI().isMapMabuWar14H(this.mapId)) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (MabuWar.gI().isTimeMabuWar()) {
                                            switch (select) {
                                                case 0:
                                                    if (player.inventory.getGold() >= 50000000) {
                                                        Service.getInstance().changeFlag(player, 9);
                                                        player.inventory.subGold(50000000);

                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Không đủ  Vàng");
                                                    }
                                                    break;
                                                case 1:
                                                    if (player.zone.map.mapId == 120) {
                                                        ChangeMapService.gI().changeMapBySpaceShip(player,
                                                                player.gender + 21, -1, 250);
                                                    }
                                                    if (player.cFlag == 9) {
                                                        if (player.getPowerPoint() >= 20) {
                                                            if (!(player.zone.map.mapId == 119)) {
                                                                int idMapNextFloor = player.zone.map.mapId == 115
                                                                        ? player.zone.map.mapId + 2
                                                                        : player.zone.map.mapId + 1;
                                                                ChangeMapService.gI().changeMap(player, idMapNextFloor, -1,
                                                                        354, 240);
                                                            } else {
                                                                Zone zone = MabuWar.gI().getMapLastFloor(120);
                                                                if (zone != null) {
                                                                    ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                                                } else {
                                                                    Service.getInstance().sendThongBao(player,
                                                                            "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                                                }
                                                            }
                                                            player.resetPowerPoint();
                                                            player.sendMenuGotoNextFloorMabuWar = false;
                                                            Service.getInstance().sendPowerInfo(player, "%",
                                                                    player.getPowerPoint());
                                                            if (Util.isTrue(1, 30)) {
                                                                player.inventory.ruby += 1;
                                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Bạn nhận được 1 Hồng Ngọc");
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Bạn đen vô cùng luôn nên không nhận được gì cả");
                                                            }
                                                        } else {
                                                            this.npcChat(player,
                                                                    "Ngươi cần có đủ điểm để xuống tầng tiếp theo");
                                                        }
                                                        break;
                                                    } else {
                                                        this.npcChat(player,
                                                                "Ngươi đang theo phe Babiđây,Hãy qua bên đó mà thể hiện");
                                                    }
                                            }
                                        } else if (MabuWar14h.gI().isTimeMabuWar()) {
                                            switch (select) {
                                                case 0:
                                                    if (player.effectSkin.isPhuHo) {
                                                        this.npcChat("Con đã mang trong mình sức mạnh của thần Kaiô!");
                                                        return;
                                                    }
                                                    if (player.inventory.ruby < 55) {
                                                        Service.getInstance().sendThongBao(player, "Bạn không đủ hồng ngọc");
                                                    } else {
                                                        player.inventory.ruby -= 55;
                                                        player.effectSkin.isPhuHo = true;
                                                        Service.getInstance().point(player);
                                                        this.npcChat("Ta đã phù hộ cho con hãy giúp ta tiêu diệt Mabư!");
                                                    }
                                                    break;
                                                case 2:
                                                    ChangeMapService.gI().changeMapBySpaceShip(player, 52, -1, 250);
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BABIDAY:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (MapService.gI().isMapMabuWar(this.mapId)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Đừng vội xem thường Babyđây,ngay đến cha hắn là thần ma đạo sĩ\n"
                                            + "Bibiđây khi còn sống cũng phải sợ hắn đấy",
                                            "Yểm bùa\n50Tr Vàng",
                                            player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (MapService.gI().isMapMabuWar(this.mapId) && MabuWar.gI().isTimeMabuWar()) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                if (player.inventory.getGold() >= 50000000) {
                                                    Service.getInstance().changeFlag(player, 10);
                                                    player.inventory.subGold(50000000);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ  Vàng");
                                                }
                                                break;
                                            case 1:
                                                if (player.zone.map.mapId == 120) {
                                                    ChangeMapService.gI().changeMapBySpaceShip(player,
                                                            player.gender + 21, -1, 250);
                                                }
                                                if (player.cFlag == 10) {
                                                    if (player.getPowerPoint() >= 20) {
                                                        if (!(player.zone.map.mapId == 119)) {
                                                            int idMapNextFloor = player.zone.map.mapId == 115
                                                                    ? player.zone.map.mapId + 2
                                                                    : player.zone.map.mapId + 1;
                                                            ChangeMapService.gI().changeMap(player, idMapNextFloor, -1,
                                                                    354, 240);
                                                        } else {
                                                            Zone zone = MabuWar.gI().getMapLastFloor(120);
                                                            if (zone != null) {
                                                                ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                                                ChangeMapService.gI().changeMapBySpaceShip(player,
                                                                        player.gender + 21, -1, 250);
                                                            }
                                                        }
                                                        player.resetPowerPoint();
                                                        player.sendMenuGotoNextFloorMabuWar = false;
                                                        Service.getInstance().sendPowerInfo(player, "TL",
                                                                player.getPowerPoint());
                                                        if (Util.isTrue(1, 30)) {
                                                            player.inventory.ruby += 1;
                                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Bạn nhận được 1 Hồng Ngọc");
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Bạn đen vô cùng luôn nên không nhận được gì cả");
                                                        }
                                                    } else {
                                                        this.npcChat(player,
                                                                "Ngươi cần có đủ điểm để xuống tầng tiếp theo");
                                                    }
                                                    break;
                                                } else {
                                                    this.npcChat(player,
                                                            "Ngươi đang theo phe Ôsin,Hãy qua bên đó mà thể hiện");
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.LINH_CANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (player.clan != null && player.clan.maxMember <= 19) {
                                    Service.getInstance().sendThongBao(player, "Bang hội của bạn chưa đủ cấp độ!!!" + "\nBang hội cần tối thiểu đạt cấp 10" + "\nHãy cùng các thành viên khác cố gắng nâng cấp bang");
                                    return;
                                }
                                if (player.clan == null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                            "Đừng chơi game 1 mình nữa, kiếm đồng đội đi" + "\n Trong box zalo có gái xinh, vào chơi kiếm bán đê", "Đóng");
                                } else if (player.clan.getMembers().size() < 5) {
                                    // } else if (player.clan.getMembers().size() < 1) {
                                    this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                            "Cần ít nhất 5 thằng đứng cùng nhau mới được tham gia", "Đóng");
                                } else {
                                    ClanMember clanMember = player.clan.getClanMember((int) player.id);
                                    int days = (int) (((System.currentTimeMillis() / 1000) - clanMember.joinTime) / 60
                                            / 60 / 24);
                                    if (days < 5) {
                                        NpcService.gI().createTutorial(player, avartar,
                                                "Chỉ những thành viên gia nhập bang hội tối thiểu 5 ngày mới có thể tham gia");
                                        return;
                                    }
                                    if (!player.clan.haveGoneDoanhTrai && player.clan.timeOpenDoanhTrai != 0) {
                                        createOtherMenu(player, ConstNpc.MENU_VAO_DT,
                                                "Có thằng lòn nào mở dt rồi kìa\n" + "Còn : "
                                                + TimeUtil.getSecondLeft(player.clan.timeOpenDoanhTrai,
                                                        DoanhTrai.TIME_DOANH_TRAI / 1000)
                                                + "Bấm tham gia nhanh không nó lại bú hết thỏi vàng",
                                                "Vào nhanh\n Còn kịp", "Chê", "Hướng\ndẫn\nthêm");
                                    } else {
                                        List<Player> plSameClans = new ArrayList<>();
                                        List<Player> playersMap = player.zone.getPlayers();
                                        synchronized (playersMap) {
                                            for (Player pl : playersMap) {
                                                if (!pl.equals(player) && pl.clan != null
                                                        && pl.clan.id == player.clan.id && pl.location.x >= 1285
                                                        && pl.location.x <= 1645) {
                                                    plSameClans.add(pl);
                                                }

                                            }
                                        }
                                        // if (plSameClans.size() >= 0) {
                                        if (plSameClans.size() >= 4) {
                                            if (!player.isAdmin() && player.clanMember
                                                    .getNumDateFromJoinTimeToToday() < DoanhTrai.DATE_WAIT_FROM_JOIN_CLAN) {
                                                createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                        "Cân có 5 thành viên đứng gần để có thể đi",
                                                        "OK", "Hướng\ndẫn\nthêm");
                                            } else if (player.clan.haveGoneDoanhTrai) {
                                                createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                        "Đã có con chó nào đi lúc sớm rồi\n "
                                                        + Util.formatTime(player.clan.timeOpenDoanhTrai)
                                                        + " hôm nay. " + "\nNgười mở" + "("
                                                        + player.clan.playerOpenDoanhTrai.name
                                                        + ")" + "Ae cái bầu đuồi",
                                                        "OK", "Hướng\ndẫn\nthêm");

                                            } else {
                                                createOtherMenu(player, ConstNpc.MENU_CHO_VAO_DT,
                                                        "Hôm nay chưa bị con lợn nào phá, mày có thể mở DT\n"
                                                        + "Một chuyến nếu kill toàn bộ boss sẽ rơi từ 1-2k thỏi vàng",
                                                        "Chiến\n thôi", "Chê", "Hướng\ndẫn\nthêm");
                                            }
                                        } else {
                                            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                    "Cần ít nhất 5 thành viên đứng gần đây mới vào được\n"
                                                    + "Gọi mấy thằng trong bang lại đây đứng đi\n"
                                                    + "Nhanh mẹ lên.....\n",
                                                    "OK", "Hướng\ndẫn\nthêm");
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 27) {
                                    switch (player.iDMark.getIndexMenu()) {
                                        case ConstNpc.MENU_KHONG_CHO_VAO_DT:
                                            if (select == 1) {
                                                NpcService.gI().createTutorial(player, this.avartar,
                                                        ConstNpc.HUONG_DAN_DOANH_TRAI);
                                            }
                                            break;
                                        case ConstNpc.MENU_CHO_VAO_DT:
                                            switch (select) {
                                                case 0:
                                                    DoanhTraiService.gI().openDoanhTrai(player);
                                                    break;
                                                case 2:
                                                    NpcService.gI().createTutorial(player, this.avartar,
                                                            ConstNpc.HUONG_DAN_DOANH_TRAI);
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.MENU_VAO_DT:
                                            switch (select) {
                                                case 0:
                                                    ChangeMapService.gI().changeMap(player, 53, 0, 35, 432);
                                                    break;
                                                case 2:
                                                    NpcService.gI().createTutorial(player, this.avartar,
                                                            ConstNpc.HUONG_DAN_DOANH_TRAI);
                                                    break;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUA_TRUNG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        private final int COST_AP_TRUNG_NHANH = 2000000000;

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                player.mabuEgg.sendMabuEgg();
                                if (player.mabuEgg.getSecondDone() != 0) {
                                    this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Muốn triệu hồi ta sao, cần có 2 tỉ vàng...",
                                            "Hủy bỏ\ntrứng",
                                            "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + "  Vàng", "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở",
                                            "Hủy bỏ\ntrứng", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.CAN_NOT_OPEN_EGG:
                                        if (select == 0) {
                                            this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                    "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                        } else if (select == 1) {
                                            if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                                player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                                player.mabuEgg.timeDone = 0;
                                                Service.getInstance().sendMoney(player);
                                                player.mabuEgg.sendMabuEgg();
                                            } else {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn không đủ  Vàng để thực hiện, còn thiếu "
                                                        + Util.numberToMoney(
                                                                (COST_AP_TRUNG_NHANH - player.inventory.gold))
                                                        + "  Vàng");
                                            }
                                        }
                                        break;
                                    case ConstNpc.CAN_OPEN_EGG:
                                        switch (select) {
                                            case 0:
                                                this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                                        "Bạn có chắc chắn cho trứng nở?\n"
                                                        + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                                        "Đệ\nTrái Đất", "Đệ\nNamếc", "Đệ\nXayda",
                                                        "Từ chối");
                                                break;
                                            case 1:
                                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                        "Bạn có chắc chắn muốn hủy bỏ trứng bí ẩn?", "Đồng ý",
                                                        "Từ chối");
                                                break;
                                        }
                                        break;
                                    case ConstNpc.CONFIRM_OPEN_EGG:
                                        switch (select) {
                                            case 0:
                                                player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                                break;
                                            case 1:
                                                player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                                break;
                                            case 2:
                                                player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case ConstNpc.CONFIRM_DESTROY_EGG:
                                        if (select == 0) {
                                            player.mabuEgg.destroyEgg();
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUOC_VUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?", "Bản thân", "Đệ tử",
                                    "Chuyển Sinh","Danh Hiệu\nCs");
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
                                                        "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + "  Vàng", "Đóng");
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
                                                            "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + "  Vàng", "Đóng");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                            "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                            "Đóng");
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                            }
                                            // giới hạn đệ tử
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
                                            int tvang = 0;
                                            if (player.chuyensinh > 0 && player.chuyensinh <= 50) {
                                                tvang = 10000;
                                            }
                                            if (player.chuyensinh > 50 && player.chuyensinh <= 100) {
                                                tvang = 30000;
                                            }
                                            if (player.chuyensinh > 100 && player.chuyensinh <= 150) {
                                                tvang = 50000;
                                            }
                                            if (player.chuyensinh > 150 && player.chuyensinh <= 200) {
                                                tvang = 100000;
                                            }
                                            if (player.chuyensinh > 200 && player.chuyensinh <= 300) {
                                                tvang = 200000;
                                            }
                                            if (player.chuyensinh > 300 && player.chuyensinh <= 400) {
                                                tvang = 300000;
                                            }
                                            if (player.chuyensinh > 400 && player.chuyensinh <= 500) {
                                                tvang = 500000;
                                            }
                                            if (player.chuyensinh > 500) {
                                                tvang = 1000000;
                                            }
                                            int percent = 0;
                                            if (player.chuyensinh <= 10) {
                                                percent = 90;
                                            } else if (player.chuyensinh > 10 && player.chuyensinh <= 20) {
                                                percent = 85;
                                            } else if (player.chuyensinh > 20 && player.chuyensinh <= 30) {
                                                percent = 80;
                                            } else if (player.chuyensinh > 30 && player.chuyensinh <= 40) {
                                                percent = 75;
                                            } else if (player.chuyensinh > 40 && player.chuyensinh <= 50) {
                                                percent = 70;
                                            } else if (player.chuyensinh > 50 && player.chuyensinh <= 60) {
                                                percent = 65;
                                            } else if (player.chuyensinh > 60 && player.chuyensinh <= 70) {
                                                percent = 60;
                                            } else if (player.chuyensinh > 70 && player.chuyensinh <= 80) {
                                                percent = 55;
                                            } else if (player.chuyensinh > 80 && player.chuyensinh <= 90) {
                                                percent = 50;
                                            } else if (player.chuyensinh > 90 && player.chuyensinh <= 100) {
                                                percent = 45;
                                            } else if (player.chuyensinh > 100 && player.chuyensinh <= 110) {
                                                percent = 40;
                                            } else if (player.chuyensinh > 110 && player.chuyensinh <= 120) {
                                                percent = 35;
                                            } else if (player.chuyensinh > 120 && player.chuyensinh <= 130) {
                                                percent = 30;
                                            } else if (player.chuyensinh > 130 && player.chuyensinh <= 140) {
                                                percent = 25;
                                            } else if (player.chuyensinh > 140 && player.chuyensinh <= 150) {
                                                percent = 20;
                                            } else if (player.chuyensinh > 150 && player.chuyensinh <= 200) {
                                                percent = 10;
                                            } else if (player.chuyensinh > 200 && player.chuyensinh <= 300) {
                                                percent = 5;
                                            } else if (player.chuyensinh > 300) {
                                                percent = 1;// Tỉ lệ thành công chuyển sinh
                                            }
                                            this.createOtherMenu(player, ConstNpc.CHUYENSINH,
                                                    "|7|CHUYỂN SINH"
                                                    + "\n\n|5|Bạn đang chuyển sinh : " + player.chuyensinh
                                                    + " \nCấp tiếp theo với tỉ lệ : " + (percent)
                                                    + "% \n Mức giá chuyển sinh : " + Util.numberToMoney(tvang) + " Thỏi vàng\n\n|7|Bạn có muốn chuyển sinh ?",
                                                    "Đồng ý", "Từ chối");
                                            break; //
                                        case 1:
                                            long hp = 0,
                                             ki = 0,
                                             dame = 0;
                                            int phantram = 0;
                                            int cs = player.chuyensinh;

                                            if (cs > 0) {
                                                if (cs < 50) {
                                                    dame = safeMultiply(2_500_000, cs);
                                                    hp = safeMultiply(5_000_000, cs);
                                                    ki = safeMultiply(5_000_000, cs);
                                                } else if (cs < 100) {
                                                    dame = safeMultiply(5_000_000, cs);
                                                    hp = safeMultiply(10_000_000, cs);
                                                    ki = safeMultiply(10_000_000, cs);
                                                    phantram = 10;
                                                } else if (cs < 150) {
                                                    dame = safeMultiply(10_000_000, cs);
                                                    hp = safeMultiply(40_000_000, cs);
                                                    ki = safeMultiply(40_000_000, cs);
                                                    phantram = 20;
                                                } else if (cs < 200) {
                                                    dame = safeMultiply(20_000_000, cs);
                                                    hp = safeMultiply(80_000_000, cs);
                                                    ki = safeMultiply(80_000_000, cs);
                                                    phantram = 30;
                                                } else if (cs < 300) {
                                                    dame = safeMultiply(30_000_000, cs);
                                                    hp = safeMultiply(120_000_000, cs);
                                                    ki = safeMultiply(120_000_000, cs);
                                                    phantram = 40;
                                                } else if (cs < 400) {
                                                    dame = safeMultiply(40_000_000, cs);
                                                    hp = safeMultiply(160_000_000, cs);
                                                    ki = safeMultiply(160_000_000, cs);
                                                    phantram = 50;
                                                } else if (cs < 500) {
                                                    dame = safeMultiply(50_000_000, cs);
                                                    hp = safeMultiply(200_000_000, cs);
                                                    ki = safeMultiply(200_000_000, cs);
                                                    phantram = 75;
                                                } else {
                                                    dame = safeMultiply(75_000_000, cs);
                                                    hp = safeMultiply(300_000_000, cs);
                                                    ki = safeMultiply(300_000_000, cs);
                                                    phantram = 100;
                                                }
                                                Service.getInstance().sendThongBaoOK(player,
                                                        "Bạn đang cấp chuyển sinh: " + cs + " Lần\n"
                                                        + "|2|Sức Đánh Cộng Thêm: " + Util.formatNew(dame) + " Sd chuyển sinh\n"
                                                        + "Hp ki Cộng Thêm: " + Util.formatNew(hp) + "\n"
                                                        + "%Sd-Hp-Ki Tăng Thêm: " + phantram + "%\n"
                                                );
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.CHUYENSINH) {
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
                                        Item thoivang = null;
                                        try {
                                            thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                                        } catch (Exception e) {
                                        }
                                        int tvang = 0;
                                        if (player.chuyensinh > 0 && player.chuyensinh <= 50) {
                                            tvang = 10000;
                                        }
                                        if (player.chuyensinh > 50 && player.chuyensinh <= 100) {
                                            tvang = 30000;
                                        }
                                        if (player.chuyensinh > 100 && player.chuyensinh <= 150) {
                                            tvang = 50000;
                                        }
                                        if (player.chuyensinh > 150 && player.chuyensinh <= 200) {
                                            tvang = 100000;
                                        }
                                        if (player.chuyensinh > 200 && player.chuyensinh <= 300) {
                                            tvang = 200000;
                                        }
                                        if (player.chuyensinh > 300 && player.chuyensinh <= 400) {
                                            tvang = 300000;
                                        }
                                        if (player.chuyensinh > 400 && player.chuyensinh <= 500) {
                                            tvang = 500000;
                                        }
                                        if (player.chuyensinh > 500) {
                                            tvang = 1000000;
                                        }
                                        if (thoivang == null || thoivang.quantity < tvang) {
                                            npcChat(player, "Bạn chưa đủ Thỏi vàng để chuyển sinh");
                                            return;
                                        }
                                        int percent = 0;
                                        if (player.chuyensinh <= 10) {
                                            percent = 90;
                                        } else if (player.chuyensinh > 10 && player.chuyensinh <= 20) {
                                            percent = 85;
                                        } else if (player.chuyensinh > 20 && player.chuyensinh <= 30) {
                                            percent = 80;
                                        } else if (player.chuyensinh > 30 && player.chuyensinh <= 40) {
                                            percent = 75;
                                        } else if (player.chuyensinh > 40 && player.chuyensinh <= 50) {
                                            percent = 70;
                                        } else if (player.chuyensinh > 50 && player.chuyensinh <= 60) {
                                            percent = 65;
                                        } else if (player.chuyensinh > 60 && player.chuyensinh <= 70) {
                                            percent = 60;
                                        } else if (player.chuyensinh > 70 && player.chuyensinh <= 80) {
                                            percent = 55;
                                        } else if (player.chuyensinh > 80 && player.chuyensinh <= 90) {
                                            percent = 50;
                                        } else if (player.chuyensinh > 90 && player.chuyensinh <= 100) {
                                            percent = 45;
                                        } else if (player.chuyensinh > 100 && player.chuyensinh <= 110) {
                                            percent = 40;
                                        } else if (player.chuyensinh > 110 && player.chuyensinh <= 120) {
                                            percent = 35;
                                        } else if (player.chuyensinh > 120 && player.chuyensinh <= 130) {
                                            percent = 30;
                                        } else if (player.chuyensinh > 130 && player.chuyensinh <= 140) {
                                            percent = 25;
                                        } else if (player.chuyensinh > 140 && player.chuyensinh <= 150) {
                                            percent = 20;
                                        } else if (player.chuyensinh > 150 && player.chuyensinh <= 200) {
                                            percent = 10;
                                        } else if (player.chuyensinh > 200 && player.chuyensinh <= 300) {
                                            percent = 5;
                                        } else if (player.chuyensinh > 300) {
                                            percent = 1;// Tỉ lệ thành công chuyển sinh
                                        }
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
                                                    short[] headtd = {30, 31, 64};
                                                    short[] headnm = {9, 29, 32};
                                                    short[] headxd = {27, 28, 6};
                                                    player.playerSkill.skills.clear();
                                                    for (Skill skill : player.playerSkill.skills) {
                                                        skill.point = 1;
                                                    }
                                                    int[] skillsArr = player.gender == 0 ? new int[]{0, 1, 6, 9, 10, 20, 22, 24, 19}
                                                            : player.gender == 1 ? new int[]{2, 3, 7, 11, 12, 17, 18, 26, 19}
                                                            : new int[]{4, 5, 8, 13, 14, 21, 23, 25, 19};
                                                    for (int i = 0; i < skillsArr.length; i++) {
                                                        if (skillsArr[i] == Skill.SUPER_KAME || skillsArr[i] == Skill.LIEN_HOAN_CHUONG || skillsArr[i] == Skill.MA_PHONG_BA) {
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
                                                        default:
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
                                                    player.nPoint.power -= 5_550_000_000_000L;
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
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT && player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    switch (select) {
                                        case 0:
                                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                                if (OpenPowerService.gI().openPowerSpeed(player)) {
                                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                                    Service.getInstance().sendMoney(player);
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player,
                                                        "Đã nghèo còn hút 3 số uống tiger \n thiếu "
                                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + "  Vàng");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET && player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    if (select == 0) {
                                        if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                            if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                                player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                                Service.getInstance().sendMoney(player);
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Đã nghèo còn hút 3 số uống tiger \n thiếu "
                                                    + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + "  Vàng");
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BARDOCK:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tôi có thể giúp gì cho bạn??", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BERRY:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tôi có thể giúp gì cho bạn??", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BUNMA_TL:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?",
                                            "Cửa hàng", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0,
                                                player.gender);
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RONG_OMEGA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                BlackBallWar.gI().setTime();
                                if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                                    try {
                                        long now = System.currentTimeMillis();
                                        if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW,
                                                    "Đường đến với ngọc rồng sao đen đã mở, "
                                                    + "ngươi có muốn tham gia không?",
                                                    "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                        } else {
                                            String[] optionRewards = new String[7];
                                            int index = 0;
                                            for (int i = 0; i < 7; i++) {
                                                if (player.rewardBlackBall.timeOutOfDateReward[i] > System
                                                        .currentTimeMillis()) {
                                                    optionRewards[index] = "Nhận thưởng\n" + (i + 1) + " sao";
                                                    index++;
                                                }
                                            }
                                            if (index != 0) {
                                                String[] options = new String[index + 1];
                                                for (int i = 0; i < index; i++) {
                                                    options[i] = optionRewards[i];
                                                }
                                                options[options.length - 1] = "Từ chối";
                                                this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW,
                                                        "Ngươi có một vài phần thưởng ngọc " + "rồng sao đen đây!",
                                                        options);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                                        "Bang chiếm được ngọc rồng đen sẽ nhận được:"
                                                        + "\n Ngọc rồng đen 1s = 30pt sức đánh"
                                                        + "\n Ngọc rồng đen 2s = 50pt hp max"
                                                        + "\n Ngọc rồng đen 3s = 50pt ki max"
                                                        + "\n Ngọc rồng đen 4s = 30pt chí mạng"
                                                        + "\n Ngọc rồng đen 5s = 20pt giáp"
                                                        + "\n Ngọc rồng đen 6s = 20pt né đòn"
                                                        + "\n Ngọc rồng đen 7s = 10 Thỏi Vàng/ giờ"
                                                        + "\n (Thỏi vàng cần nhận mỗi giờ nếu không sẽ mất)",
                                                        "Hướng dẫn", "Từ chối");
                                            }
                                        }
                                    } catch (Exception ex) {
                                        Log.error("Lỗi mở menu rồng Omega");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.MENU_REWARD_BDW:
                                        player.rewardBlackBall.getRewardSelect((byte) select);
                                        break;
                                    case ConstNpc.MENU_OPEN_BDW:
                                        if (select == 0) {
                                            NpcService.gI().createTutorial(player, this.avartar,
                                                    ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                                        } else if (select == 1) {
                                            player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                            ChangeMapService.gI().openChangeMapTab(player);
                                        }
                                        break;
                                    case ConstNpc.MENU_NOT_OPEN_BDW:
                                        if (select == 0) {
                                            NpcService.gI().createTutorial(player, this.avartar,
                                                    ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (player.isHoldBlackBall) {
                                    this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?",
                                            "Phù hộ", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME,
                                            "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                                    if (select == 0) {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                                "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                                "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                                "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                                "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                                "Từ chối");
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                                    if (select == 0) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                                    switch (select) {
                                        case 0:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                            break;
                                        case 1:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                            break;
                                        case 2:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                            break;
                                        case 3:
                                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.HANG_NGA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "|7|SHOP VIP\n"
                                        + "Cải trang, phụ kiện chỉ số cực cao",
                                        "SHOP\nVIP");//,"Tới Map\nVõ Đài","Nâng Cấp\nSKH\nThánh Tôn"
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.HANG_NGA_SHOP, 0, -1);
                                                break;
//                                            case 1:
//                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_SKH_THANH_TON);
//                                                break;
//                                            case 2:
//                                                ChangeMapService.gI().changeMapBySpaceShip(player, 145, -1, 168);
//                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                        switch (player.combineNew.typeCombine) {
                                            case CombineServiceNew.NANG_SKH_THANH_TON:
                                                if (select == 0) {
                                                    CombineServiceNew.gI().startCombine(player);
                                                }
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_THAN_LINH) {
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.NPC_64:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn xem thông tin gì?",
                                        "Top\nsức mạnh", "Đóng");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        Service.getInstance().showTopPower(player, Service.getInstance().TOP_SUCMANH);
                                    }
                                }
                            }
                        }
                    };
                    break;
//                case ConstNpc.BILL:
//                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
//
//                        @Override
//                        public void openBaseMenu(Player player) {
//                            if (canOpenNpc(player)) {
//                                if (this.mapId == 48) {
//                                    createOtherMenu(player, ConstNpc.BASE_MENU,
//                                            "|7|SHOP ĐỒ HỦY DIỆT\n|6| Mang đủ 5 món đồ Thần linh và đem 99 Thức ăn đến cho ta. Ta sẽ bán đồ Hủy diệt cho ngươi",
//                                            "SHOP HỦY DIỆT", "Đổi Phiếu\nHủy diệt", getMenuSuKien(EVENT_SEVER), "Đóng");
//                                } else {
//                                    super.openBaseMenu(player);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void confirmMenu(Player player, int select) {
//                            if (canOpenNpc(player)) {
//                                switch (this.mapId) {
//                                    case 48:
//                                        if (player.iDMark.isBaseMenu()) {
//                                            switch (select) {
//                                                case 0:
//                                                    if (player.check99ThucAnHuyDiet() == true) {
//                                                        if (player.setClothes.setDTL == 5) {
//                                                            ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BILL_HUY_DIET_0, 0, -1);
//                                                        } else {
//                                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Yêu cầu mặc 5 món Thần linh", "Đóng");
//                                                        }
//                                                    } else {
//                                                        createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ngươi chưa đủ 99 thức ăn", "Đóng");
//                                                    }
//                                                    break;
//                                                case 1:
//                                                    if (player.setClothes.setDTL == 5) {
//                                                        ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BILL_HUY_DIET_1, 1, -1);
//                                                    } else {
//                                                        createOtherMenu(player, ConstNpc.IGNORE_MENU, "Yêu cầu mặc 5 món Thần linh", "Đóng");
//                                                    }
//                                                    break;
//                                                case 2:
//                                                    switch (EVENT_SEVER) {
//                                                        case 2:
//                                                            Attribute at = ServerManager.gI().getAttributeManager()
//                                                                    .find(ConstAttribute.SUC_DANH);
//                                                            String text = "Sự kiện 20/11 chính thức tại Ngọc Rồng "
//                                                                    + Manager.SERVER_NAME + "\n "
//                                                                    + "Số điểm hiện tại của bạn là : "
//                                                                    + player.event.getEventPoint()
//                                                                    + "\nTổng số hoa đã tặng trên toàn máy chủ "
//                                                                    + EVENT_COUNT_THAN_HUY_DIET % 999 + "/999";
//                                                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_SUKIEN,
//                                                                    at != null && !at.isExpired() ? text
//                                                                    + "\nToàn bộ máy chủ được tăng 20% sức đánh,thời gian còn lại "
//                                                                    + at.getTime() / 60 + " phút."
//                                                                    : text + "\nKhi tặng đủ 999 bông toàn bộ máy chủ được tăng tăng 20% sức đánh trong 60 phút\n",
//                                                                    "Tặng 1\n Bông hoa", "Tặng\n10 Bông",
//                                                                    "Tặng\n99 Bông", "Đổi\nHộp quà");
//                                                            break;
//                                                        default:
//                                                            createOtherMenu(player, 5656,
//                                                                    "|7|Npc này không liên quan đến Sự kiện\nVui lòng tìm Npc khác !!!", "Đóng");
//                                                            break;
//                                                    }
//                                            }
//                                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_SUKIEN) {
//                                            openMenuSuKien(player, this, tempId, select);
//                                        }
//                                }
//                            }
//                        }
//                    };
//                    break;
                case ConstNpc.THO_REN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "|8|Số coin mà bạn đang có: " + player.getSession().vnd + " Coin\n"
                                        + "Số người đang onl game: " + Client.gI().getPlayers().size() + " Người\n"
                                        + "Nơi này chuyên cung cấp các dịch vụ về trang bị\n"
                                        + "Bạn cần hỗ trợ gì ?",
                                        "Nâng Đồ\nTL->HD",
                                        "Chế SKH\nVải Thô",
                                        "Cướng Hoá\nTrang Bị", "Chế Tạo\nThánh Tôn", "Shop Đá\nCường Hoá", "Gacha\nĐá Cường Hoá","Thu Mua\nTrang Bị");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_THAN_LINH);
                                            break;
                                        case 1:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VAI_THO);
                                            break;
                                        case 2:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_TRANG_BI);
                                            break;
                                        case 3:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.CHE_TAO_THANH_TON);
                                            break;
                                        case 4:
                                            ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_THOREN, 0, -1);
                                            break;
                                        case 5:
                                            this.createOtherMenu(player, ConstNpc.MENU_GACHA_DA_CUONG_HOA,
                                                    "|8|SẼ CÓ 2 LOẠI GACHA"
                                                    + "\n- GACHA 500K COIN 1 NHÁT"
                                                    + "\n- HOẶC GACHA 1M THỎI VÀNG 1 NHÁT"
                                                    + "\n GACHA COIN: TỪ ĐÁ CẤP 7 ĐẾN CẤP 12!!!"
                                                    + "\n GACHA THỎI VÀNG: TỪ ĐÁ CẤP 6 ĐẾN CẤP 11!!!"
                                                    + "\n lưu ý: chỉ có thể dùng thỏi vàng không khoá!!!",
                                                    "Coin", "Thỏi\nVàng");
                                            break;
                                        case 6:
                                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.THU_HOI_DO_TL);
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == 7520166) {
                                    switch (select) {
                                        case 0:
                                            if (player.getSession().vnd < 50000) {
                                                Service.getInstance().sendThongBao(player, "Không đủ coin");
                                                return;
                                            } else if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                                return;
                                            } else {
                                                PlayerDAO.subVnd(player, 50000);
                                                PlayerDAO.CongTongNap(player, 50000);
                                                short[] dacuonghoa7den12 = {1559, 1560, 1561, 1563, 1564, 1565};
                                                byte index = (byte) Util.nextInt(0, dacuonghoa7den12.length - 1);
                                                Item dacuonghoa = ItemService.gI().createNewItem(dacuonghoa7den12[index]);
                                                InventoryService.gI().addItemBag(player, dacuonghoa, 9999);
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendMoney(player);
                                                Service.getInstance().sendThongBao(player, "Mày đã nhận được " + dacuonghoa.template.name);
                                            }
                                            break;
                                        case 1:
                                            Item thoivang = null;
                                            try {
                                                thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                                            } catch (Exception e) {
                                            }
                                            if (thoivang == null || thoivang.quantity < 999) {
                                                Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                                            } else {
                                                InventoryService.gI().subQuantityItemsBag(player, thoivang, 999999);
                                                short[] dacuonghoa7den11 = {1559, 1560, 1561, 1563, 1564, 1565};
                                                byte index = (byte) Util.nextInt(0, dacuonghoa7den11.length - 1);
                                                Item dacuonghoa = ItemService.gI().createNewItem(dacuonghoa7den11[index]);
                                                dacuonghoa.itemOptions.add(new ItemOption(30, 1));
                                                InventoryService.gI().addItemBag(player, dacuonghoa, 9999);
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendMoney(player);
                                                this.npcChat(player, "|1|Bạn nhận được: " + dacuonghoa.getName());
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                    switch (player.combineNew.typeCombine) {
                                        case CombineServiceNew.NANG_CAP_TRANG_BI:
                                        case CombineServiceNew.NANG_CAP_SKH_VAI_THO:
                                        case CombineServiceNew.NANG_CAP_THAN_LINH:
                                        case CombineServiceNew.CHE_TAO_THANH_TON:
                                        case CombineServiceNew.THU_HOI_DO_TL:
                                            if (select == 0) {
                                                CombineServiceNew.gI().startCombine(player);
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_THAN_LINH) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_SKH_VAI_THO) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_TRANG_BI) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHE_TAO_THANH_TON) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_THU_HOI_DO_TL) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.DAI_THANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Hợp thành rương kí ức"
                                        + "\nNguyên liệu cần thiết:"
                                        + "\n1 Long Hồn"
                                        + "\n1 Quả trứng (Đệ Bư)"
                                        + "\n1 Hồn Berus (Đệ Berus)"
                                        + "\n1 Vòng Kim Cô (Đệ ngộ không)"
                                        + "\n1 Trứng Tiên (Đệ Tiên)"
                                        + "\n1 Trứng itachi (Đệ itachi)"
                                        + "\n1 Trứng Kaido (Đệ Kaido)",
                                        "Hợp thành Rương Kí Ức");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            Item quatrung = null;
                                            Item honberus = null;
                                            Item trungitachi = null;
                                            Item trungkaido = null;
                                            Item trungtien = null;
                                            Item vongkimco = null;
                                            Item longhon = null;
                                            try {
                                                quatrung = InventoryService.gI().findItemBagByTemp(player, (short) 568);
                                                honberus = InventoryService.gI().findItemBagByTemp(player, (short) 1108);
                                                trungitachi = InventoryService.gI().findItemBagByTemp(player, (short) 1538);
                                                trungkaido = InventoryService.gI().findItemBagByTemp(player, (short) 1537);
                                                trungtien = InventoryService.gI().findItemBagByTemp(player, (short) 1539);
                                                vongkimco = InventoryService.gI().findItemBagByTemp(player, (short) 1569);
                                                longhon = InventoryService.gI().findItemBagByTemp(player, (short) 1726);
                                            } catch (Exception e) {
                                            }
                                            if (quatrung == null || quatrung.quantity < 1
                                                    || honberus == null || honberus.quantity < 1
                                                    || trungitachi == null || trungitachi.quantity < 1
                                                    || trungkaido == null || trungkaido.quantity < 1
                                                    || vongkimco == null || vongkimco.quantity < 1
                                                    || longhon == null || longhon.quantity < 1
                                                    || trungtien == null || trungtien.quantity < 1) {
                                                Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
                                                return;
                                            }
                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                            } else {
                                                Item ruongkiuc = ItemService.gI().createNewItem((short) 1719);
//                                    banhdeo.itemOptions.add(new ItemOption(76, 1));
                                                InventoryService.gI().subQuantityItemsBag(player, quatrung, 1);
                                                InventoryService.gI().subQuantityItemsBag(player, honberus, 1);
                                                InventoryService.gI().subQuantityItemsBag(player, trungitachi, 1);
                                                InventoryService.gI().subQuantityItemsBag(player, trungkaido, 1);
                                                InventoryService.gI().subQuantityItemsBag(player, vongkimco, 1);
                                                InventoryService.gI().subQuantityItemsBag(player, longhon, 1);
                                                InventoryService.gI().subQuantityItemsBag(player, trungtien, 1);
                                                InventoryService.gI().addItemBag(player, ruongkiuc, 1);
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Mày đã nhận được " + ruongkiuc.template.name);
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.HUNG_VUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "|8|DÙ AI ĐI NGƯỢC VỀ XUÔI\n"
                                        + "NHỚ NGÀY GIỖ TỔ MÙNG 10 THÁNG 9\n\n"
                                        + "\nThu thập đặc sản để đổi dược hộp quà"
                                        + "\n x99 Ngà voi"
                                        + "\n x99 Cựa Gà"
                                        + "\n x99 Lmao Ngựa\n"
                                        + "|4|Đổi được hộp quà 10 tháng 3\n"
                                        + "\n x9 Ngà voi"
                                        + "\n x9 Cựa Gà"
                                        + "\n x9 Lmao Ngựa\n"
                                        + "x999 Xu vàng\n"
                                        + "x999 Xu bạc\n"
                                        + "|7|Đổi được hộp quà 10 tháng 3 VIP\n",
                                         "Đổi\nQuà", "Mua\nHộp VIP");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            Item ngavoi = null;
                                            Item cuaga = null;
                                            Item lmaongua = null;
                                            try {
                                                ngavoi = InventoryService.gI().findItemBagByTemp(player, (short) 1362);
                                                cuaga = InventoryService.gI().findItemBagByTemp(player, (short) 1363);
                                                lmaongua = InventoryService.gI().findItemBagByTemp(player, (short) 1364);
                                            } catch (Exception e) {
                                            }
                                            if (ngavoi == null || ngavoi.quantity < 99
                                                    || cuaga == null || cuaga.quantity < 99
                                                    || lmaongua == null || lmaongua.quantity < 99) {
                                                Service.getInstance().sendThongBao(player, "Nhà người chưa có đủ x99 mỗi loại.. cook");
                                                return;
                                            }
                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                            } else {
                                                Item hop10thang3 = ItemService.gI().createNewItem((short) 1370);
                                                hop10thang3.itemOptions.add(new ItemOption(30, 1));
                                                InventoryService.gI().subQuantityItemsBag(player, ngavoi, 99);
                                                InventoryService.gI().subQuantityItemsBag(player, cuaga, 99);
                                                InventoryService.gI().subQuantityItemsBag(player, lmaongua, 99);
                                                InventoryService.gI().addItemBag(player, hop10thang3, 1);
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Mày đã nhận được " + hop10thang3.template.name);
                                            }
                                            break;
                                        case 1:
                                            Item xuvang = null;
                                            Item xubac = null;
                                            Item ngavoi1 = null;
                                            Item cuaga1 = null;
                                            Item lmaongua1 = null;
                                            try {
                                                xuvang = InventoryService.gI().findItemBagByTemp(player, (short) 1567);
                                                xubac = InventoryService.gI().findItemBagByTemp(player, (short) 1568);
                                                ngavoi1 = InventoryService.gI().findItemBagByTemp(player, (short) 1362);
                                                cuaga1 = InventoryService.gI().findItemBagByTemp(player, (short) 1363);
                                                lmaongua1 = InventoryService.gI().findItemBagByTemp(player, (short) 1364);
                                            } catch (Exception e) {
                                            }
                                            if (xuvang == null || xuvang.quantity < 999
                                                    || xubac == null || xubac.quantity < 999
                                                    || ngavoi1 == null || xubac.quantity < 19
                                                    || cuaga1 == null || xubac.quantity < 19
                                                    || lmaongua1 == null || xubac.quantity < 19) {
                                                Service.getInstance().sendThongBao(player, "Nhà người chưa có đủ x999 mỗi loại.. cook");
                                                return;
                                            }
                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                            } else {
                                                Item hop10thang3vip = ItemService.gI().createNewItem((short) 1371);
                                                hop10thang3vip.itemOptions.add(new ItemOption(30, 1));
                                                InventoryService.gI().subQuantityItemsBag(player, xubac, 999);
                                                InventoryService.gI().subQuantityItemsBag(player, xuvang, 999);
                                                InventoryService.gI().subQuantityItemsBag(player, ngavoi1, 19);
                                                InventoryService.gI().subQuantityItemsBag(player, cuaga1, 19);
                                                InventoryService.gI().subQuantityItemsBag(player, lmaongua1, 19);
                                                InventoryService.gI().addItemBag(player, hop10thang3vip, 1);
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Mày đã nhận được " + hop10thang3vip.template.name);
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.MI_NUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "|7|SHOP SỰ KIỆN\n\n"
                                        + "Shop sẽ bán một số đệ tử và item even\n"
                                        + "Cải trang mị nương bán với giá 10k Xu Vàng - và Xu bạc\n"
                                        + " ***Yêu cầu tối thiểu 999 điểm sự kiện mới có thể mua\n"
                                        + "|8|Để tri ân ae đã ủng hộ sv ae đã từng nạp tiền có thể bấm nhận quà tri ân",
                                        "Shop\nMị Nương", "Mua Ct\nMị Nương", "Nhận quà\nTri ân");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0: // shop mị nương
                                            ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_MINUONG, 0, -1);
                                            break;
                                        case 1:
                                            Item xuvang1 = null;
                                            Item xubac = null;
                                            int diemsk = player.evenpoint;
                                            try {
                                                xuvang1 = InventoryService.gI().findItemBagByTemp(player, (short) 1568);
                                                xubac = InventoryService.gI().findItemBagByTemp(player, (short) 1567);
                                            } catch (Exception e) {
                                            }
                                            if (xuvang1 == null || xuvang1.quantity < 9999
                                                    || xubac == null || xubac.quantity < 9999
                                                    || diemsk < 999
                                                    ) {
                                                Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu hoặc điểm sự kiện");
                                                return;
                                            }
                                            if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                            } else {
                                                Item caitrangminuong = ItemService.gI().createNewItem((short) 994);
                                                caitrangminuong.itemOptions.add(new ItemOption(0, Util.nextInt(1234, 6666)));
                                                caitrangminuong.itemOptions.add(new ItemOption(6, Util.nextInt(6666, 34567)));
                                                caitrangminuong.itemOptions.add(new ItemOption(7, Util.nextInt(6666, 34567)));
                                                caitrangminuong.itemOptions.add(new ItemOption(50, Util.nextInt(300, 500)));
                                                caitrangminuong.itemOptions.add(new ItemOption(77, Util.nextInt(300, 500)));
                                                caitrangminuong.itemOptions.add(new ItemOption(103, Util.nextInt(300, 500)));
                                                caitrangminuong.itemOptions.add(new ItemOption(101, Util.nextInt(500, 2000)));
                                                caitrangminuong.itemOptions.add(new ItemOption(117, Util.nextInt(20, 45)));//kháng
                                                caitrangminuong.itemOptions.add(new ItemOption(5, Util.nextInt(20, 69)));//kháng
                                                caitrangminuong.itemOptions.add(new ItemOption(116, 1));//kháng
                                                caitrangminuong.itemOptions.add(new ItemOption(106, 1));//kháng
                                                caitrangminuong.itemOptions.add(new ItemOption(199, 1));//Không Thể Gia Hạn
                                                InventoryService.gI().subQuantityItemsBag(player, xuvang1, 9999);
                                                InventoryService.gI().subQuantityItemsBag(player, xubac, 9999);
                                                InventoryService.gI().addItemBag(player, caitrangminuong, 1);
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Mày đã nhận được " + caitrangminuong.template.name);
                                            }
                                            break;
                                        case 2:
                                            if (player.hoivienvip != 0) {
                                                Service.getInstance().sendThongBao(player, "Cảm ơn bạn đã ủng hộ sv"
                                                        + "Phần quà lần này đã hết và bạn đang là hội viên VIP cấp: " + player.hoivienvip + " \n"
                                                        + "Sẽ sớm cập nhật thêm quà theo cấp hội viên của bạn.");
                                                return;
                                            }
                                            if (player.tichluynap < 50000) {
                                                Service.getInstance().sendThongBao(player, "Phần quà này chỉ dành cho ae đã ủng hộ server");
                                                return;
                                            }
                                            if (InventoryService.gI().getCountEmptyBag(player) < 3) {
                                                Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                                return;
                                            } else {
                                                if (player.tichluynap >= 50000 && player.tichluynap < 500000) {
                                                    Item HopQua10thang3 = ItemService.gI().createNewItem((short) 1371);
                                                    HopQua10thang3.quantity = 1;
                                                    InventoryService.gI().addItemBag(player, HopQua10thang3, 1);
                                                    PlayerDAO.NangCapHoiVienVIP(player, 1);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nhận được " + HopQua10thang3.template.name);
                                                } else if (player.tichluynap >= 500000 && player.tichluynap < 2000000) {
                                                    Item HopQua10thang3 = ItemService.gI().createNewItem((short) 1371);
                                                    HopQua10thang3.quantity = 5;
                                                    InventoryService.gI().addItemBag(player, HopQua10thang3, 1);
                                                    PlayerDAO.NangCapHoiVienVIP(player, 2);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nhận được " + HopQua10thang3.template.name);
                                                } else if (player.tichluynap >= 2000000 && player.tichluynap < 5000000) {
                                                    Item HopQua10thang3 = ItemService.gI().createNewItem((short) 1371);
                                                    HopQua10thang3.quantity = 10;
                                                    InventoryService.gI().addItemBag(player, HopQua10thang3, 1);
                                                    PlayerDAO.NangCapHoiVienVIP(player, 3);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nhận được " + HopQua10thang3.template.name);
                                                } else if (player.tichluynap >= 5000000 && player.tichluynap < 7500000) {
                                                    Item HopQua10thang3 = ItemService.gI().createNewItem((short) 1371);
                                                    HopQua10thang3.quantity = 15;
                                                    InventoryService.gI().addItemBag(player, HopQua10thang3, 1);
                                                    PlayerDAO.NangCapHoiVienVIP(player, 4);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nhận được " + HopQua10thang3.template.name);
                                                } else if (player.tichluynap >= 7500000 && player.tichluynap < 10000000) {
                                                    Item HopQua10thang3 = ItemService.gI().createNewItem((short) 1371);
                                                    HopQua10thang3.quantity = 20;
                                                    InventoryService.gI().addItemBag(player, HopQua10thang3, 1);
                                                    PlayerDAO.NangCapHoiVienVIP(player, 5);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nhận được " + HopQua10thang3.template.name);
                                                } else {
                                                    Item HopQua10thang3 = ItemService.gI().createNewItem((short) 1371);
                                                    HopQua10thang3.quantity = 25;
                                                    InventoryService.gI().addItemBag(player, HopQua10thang3, 1);
                                                    PlayerDAO.NangCapHoiVienVIP(player, 6);
                                                    Service.getInstance().sendThongBao(player, "Mày đã nhận được " + HopQua10thang3.template.name);
                                                }
                                                InventoryService.gI().sendItemBags(player);
                                                Service.getInstance().sendMoney(player);
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.WHIS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?"
                                            + "\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!"
                                            + "\n\b|7| Điều kiện học Tuyệt kỹ"
                                            + "\b|5| -Khi lần đầu học skill cần: x999 Bí kiếp tuyệt kỹ và SM trên 60 Tỷ"
                                            + "\n -Mỗi một cấp yêu cầu: x999 Bí kiếp tuyệt kỹ và Thông thạo đạt MAX 100%", "Học\ntuyệt kĩ", "Từ Chối");
                                }
                                if (this.mapId == 154) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "|7|NÂNG CẤP ĐỒ THIÊN SỨ\n|6| Mang cho ta Công thức + Đá cầu vòng và 999 Mảnh thiên sứ ta sẽ chế tạo đồ Thiên sứ cho ngươi"
                                            + "\nĐồ Thiên sứ khi chế tạo sẽ random chỉ số 0-15%"
                                            + "\n|2|(Khi mang đủ 5 món Hủy diệt ngươi hãy theo Osin qua Hành tinh ngục tù tìm kiếm Mảnh thiên sứ và săn BOSS Thiên sứ để thu thập Đá cầu vòng)"
                                            + "\n|1| Ngươi có muốn nâng cấp không?",
                                            "Hướng dẫn", "Nâng Cấp \nĐồ Thiên Sứ", "Shop\n Thiên sứ");//, "Nâng SKH Thiên sứ"
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 154) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DO_TS);
                                                break;
                                            case 1:
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                                                break;
                                            case 2:
                                                ShopService.gI().openShopWhisThienSu(player,
                                                        ConstNpc.SHOP_WHIS_THIEN_SU, 0);
                                                break;
//                                            case 3: //Mở chỉ số bông tai
////                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHAN_RA_DO_TS);
////                                                break;
//                                            case 3:
//                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_TS);
//                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                        switch (player.combineNew.typeCombine) {
                                            case CombineServiceNew.NANG_CAP_DO_TS:
                                            case CombineServiceNew.PHAN_RA_DO_TS:
                                            case CombineServiceNew.NANG_CAP_SKH_TS:
                                                if (select == 0) {
                                                    CombineServiceNew.gI().startCombine(player);
                                                }
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS) {
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DO_SKH_TS) {
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_RA_TS) {
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                    }
                                }

                                if (player.iDMark.isBaseMenu() && this.mapId == 48) {
                                    if (select == 0) {
                                        Message msg;
                                        try {
                                            Item sachTuyetki = null;
                                            try {
                                                sachTuyetki = InventoryService.gI().findItemBagByTemp(player, 1215);
                                            } catch (Exception e) {
                                            }
                                            if (player.gender == 0) {
                                                Skill curSkill = SkillUtil.getSkillbyId(player, Skill.SUPER_KAME);
                                                if (curSkill.point == 0) {
                                                    if (player.nPoint.power >= 60000000000L) {
                                                        if (sachTuyetki == null || sachTuyetki.quantity < 999) {
                                                            this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                                            return;
                                                        }
                                                        if (sachTuyetki.quantity >= 999) {
                                                            InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
                                                            InventoryService.gI().sendItemBags(player);
                                                            curSkill = SkillUtil.createSkill(Skill.SUPER_KAME, 1);
                                                            SkillUtil.setSkill(player, curSkill);
                                                            msg = Service.getInstance().messageSubCommand((byte) 23);
                                                            msg.writer().writeShort(curSkill.skillId);
                                                            player.sendMessage(msg);
                                                            msg.cleanup();
                                                        } else {
                                                            Service.getInstance().sendThongBao(player, "Không đủ bí kíp tuyệt kĩ");
                                                        }
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 60 Tỷ");
                                                    }
                                                } else if (curSkill.point > 0 && curSkill.point < 9) {
                                                    if (sachTuyetki == null || sachTuyetki.quantity < 999) {
                                                        this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                                        return;
                                                    }
                                                    if (sachTuyetki.quantity >= 999 && curSkill.currLevel == 100) {
                                                        InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
                                                        InventoryService.gI().sendItemBags(player);
                                                        curSkill = SkillUtil.createSkill(Skill.SUPER_KAME, curSkill.point + 1);
                                                        SkillUtil.setSkill(player, curSkill);
                                                        msg = Service.getInstance().messageSubCommand((byte) 62);
                                                        msg.writer().writeShort(curSkill.skillId);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Thông thạo của bạn chưa đủ 100%");
                                                    }
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Tuyệt kĩ của bạn đã đạt cấp tối đa");
                                                }
                                            }
                                            if (player.gender == 1) {
                                                Skill curSkill = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                                                if (curSkill.point == 0) {
                                                    if (player.nPoint.power >= 60000000000L) {
                                                        if (sachTuyetki == null || sachTuyetki.quantity < 999) {
                                                            this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                                            return;
                                                        }
                                                        if (sachTuyetki.quantity >= 999) {
                                                            InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
                                                            InventoryService.gI().sendItemBags(player);
                                                            curSkill = SkillUtil.createSkill(Skill.MA_PHONG_BA, 1);
                                                            SkillUtil.setSkill(player, curSkill);
                                                            msg = Service.getInstance().messageSubCommand((byte) 23);
                                                            msg.writer().writeShort(curSkill.skillId);
                                                            player.sendMessage(msg);
                                                            msg.cleanup();
                                                        } else {
                                                            Service.getInstance().sendThongBao(player, "Không đủ bí kíp tuyệt kĩ");
                                                        }
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 60 Tỷ");
                                                    }
                                                } else if (curSkill.point > 0 && curSkill.point < 9) {
                                                    if (sachTuyetki == null || sachTuyetki.quantity < 999) {
                                                        this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                                        return;
                                                    }
                                                    if (sachTuyetki.quantity >= 999 && curSkill.currLevel == 100) {
                                                        InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
                                                        InventoryService.gI().sendItemBags(player);
                                                        curSkill = SkillUtil.createSkill(Skill.MA_PHONG_BA, curSkill.point + 1);
                                                        SkillUtil.setSkill(player, curSkill);
                                                        msg = Service.getInstance().messageSubCommand((byte) 62);
                                                        msg.writer().writeShort(curSkill.skillId);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Thông thạo của bạn chưa đủ 100%");
                                                    }
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Tuyệt kĩ của bạn đã đạt cấp tối đa");
                                                }
                                            }
                                            if (player.gender == 2) {
                                                Skill curSkill = SkillUtil.getSkillbyId(player, Skill.LIEN_HOAN_CHUONG);
                                                if (curSkill.point == 0) {
                                                    if (player.nPoint.power >= 60000000000L) {
                                                        if (sachTuyetki == null || sachTuyetki.quantity < 999) {
                                                            this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                                            return;
                                                        }
                                                        if (sachTuyetki.quantity >= 999) {
                                                            InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
                                                            InventoryService.gI().sendItemBags(player);
                                                            curSkill = SkillUtil.createSkill(Skill.LIEN_HOAN_CHUONG, 1);
                                                            SkillUtil.setSkill(player, curSkill);
                                                            msg = Service.getInstance().messageSubCommand((byte) 23);
                                                            msg.writer().writeShort(curSkill.skillId);
                                                            player.sendMessage(msg);
                                                            msg.cleanup();
                                                        } else {
                                                            Service.getInstance().sendThongBao(player, "Không đủ bí kíp tuyệt kĩ");
                                                        }
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 60 Tỷ");
                                                    }
                                                } else if (curSkill.point > 0 && curSkill.point < 9) {
                                                    if (sachTuyetki == null || sachTuyetki.quantity < 999) {
                                                        this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                                        return;
                                                    }
                                                    if (sachTuyetki.quantity >= 999 && curSkill.currLevel == 100) {
                                                        InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
                                                        InventoryService.gI().sendItemBags(player);
                                                        curSkill = SkillUtil.createSkill(Skill.LIEN_HOAN_CHUONG, curSkill.point + 1);
                                                        SkillUtil.setSkill(player, curSkill);
                                                        msg = Service.getInstance().messageSubCommand((byte) 62);
                                                        msg.writer().writeShort(curSkill.skillId);
                                                        player.sendMessage(msg);
                                                        msg.cleanup();
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Thông thạo của bạn chưa đủ 100%");
                                                    }
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Tuyệt kĩ của bạn đã đạt cấp tối đa");
                                                }
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.BO_MONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 47 || this.mapId == 84) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "|8|LÀM NHIỆM VỤ HÀNG NGÀY\n"
                                            + "|3|Tối đa " + ConstTask.MAX_SIDE_TASK + " nhiệm vụ mỗi ngày\n"
                                            + "|2|Nv dễ: " + ConstTask.GOLD_EASY + "  Thỏi vàng"// và " + ConstTask.RUBY_EASY + " Hồng Ngọc mỗi nhiệm vụ\n"
                                            + "Nv trung bình: " + ConstTask.GOLD_NORMAL + "  Thỏi vàng"// và " + ConstTask.RUBY_NORMAL + " Hồng Ngọc mỗi nhiệm vụ\n"
                                            + "Nv khó: " + ConstTask.GOLD_HARD + "  Thỏi vàng"// và " + ConstTask.RUBY_HARD + " Hồng Ngọc mỗi nhiệm vụ\n"
                                            + "Nv siêu khó: " + ConstTask.GOLD_VERY_HARD + "  Thỏi vàng"// và " + ConstTask.RUBY_VERY_HARD + " Hồng Ngọc mỗi nhiệm vụ\n"
                                            + "Nv địa ngục: " + ConstTask.GOLD_HELL + "  Thỏi vàng"// và " + ConstTask.RUBY_HELL + " Hồng Ngọc mỗi nhiệm vụ\n"
                                            + "|1|Tích cực quay tay vận may sẽ tới kkkk",
                                             "Nhiệm vụ\nhàng ngày");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 47 || this.mapId == 84) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                if (player.playerTask.sideTask.template != null) {
                                                    String npcSay = "Nhiệm vụ hiện tại: "
                                                            + player.playerTask.sideTask.getName() + " ("
                                                            + player.playerTask.sideTask.getLevel() + ")"
                                                            + "\nHiện tại đã hoàn thành: "
                                                            + player.playerTask.sideTask.count + "/"
                                                            + player.playerTask.sideTask.maxCount + " ("
                                                            + player.playerTask.sideTask.getPercentProcess()
                                                            + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                                            + player.playerTask.sideTask.leftTask + "/"
                                                            + ConstTask.MAX_SIDE_TASK;
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                            npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                            "|7|Tối đa " + ConstTask.MAX_SIDE_TASK + " nhiệm vụ mỗi ngày\n"
                                                            + "|2|Nv dễ: " + ConstTask.GOLD_EASY + "  Thỏi vàng"// và " + ConstTask.RUBY_EASY + " Hồng Ngọc mỗi nhiệm vụ\n"
                                                            + "Nv trung bình: " + ConstTask.GOLD_NORMAL + "  Thỏi vàng"// và " + ConstTask.RUBY_NORMAL + " Hồng Ngọc mỗi nhiệm vụ\n"
                                                            + "Nv khó: " + ConstTask.GOLD_HARD + "  Thỏi vàng"// và " + ConstTask.RUBY_HARD + " Hồng Ngọc mỗi nhiệm vụ\n"
                                                            + "Nv siêu khó: " + ConstTask.GOLD_VERY_HARD + "  Thỏi vàng"// và " + ConstTask.RUBY_VERY_HARD + " Hồng Ngọc mỗi nhiệm vụ\n"
                                                            + "Nv địa ngục: " + ConstTask.GOLD_HELL + "  Thỏi vàng"// và " + ConstTask.RUBY_HELL + " Hồng Ngọc mỗi nhiệm vụ\n"
                                                            + "\n|1|Chọn loại nhiệm vụ nào?",
                                                            "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa Ngục", "Từ chối");
                                                }
                                                break;
                                            case 1:
                                                TaskService.gI().checkDoneAchivements(player);
                                                TaskService.gI().sendAchivement(player);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                                        switch (select) {
                                            case 0:
                                            case 1:
                                            case 2:
                                            case 3:
                                            case 4:
                                                TaskService.gI().changeSideTask(player, (byte) select);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                                        switch (select) {
                                            case 0:
                                                TaskService.gI().paySideTask(player);
                                                break;
                                            case 1:
                                                TaskService.gI().removeSideTask(player);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GOKU_SSJ:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 80) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Xin chào, tôi có thể giúp gì cho cậu?", "Tới hành tinh\nYardart",
                                            "Từ chối");
                                } else if (this.mapId == 131) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Xin chào, tôi có thể giúp gì cho cậu?", "Quay về", "Từ chối");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        if (this.mapId == 80) {
                                            // if (select == 0) {
                                            // if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
                                            // ChangeMapService.gI().changeMapBySpaceShip(player, 160, -1, 168);
                                            // } else {
                                            // this.npcChat(player, "Xin lỗi, tôi chưa thể đưa cậu tới nơi đó lúc
                                            // này...");
                                            // }
                                            // } else
                                            if (select == 0) {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 940);
                                            }
                                        } else if (this.mapId == 131) {
                                            if (select == 0) {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GOKU_SSJ_:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 133) {
                                    Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
                                    int soLuong = 0;
                                    if (biKiep != null) {
                                        soLuong = biKiep.quantity;
                                    }
                                    if (soLuong >= 9999) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Mày có " + soLuong
                                                + " bí kiếp.\n"
                                                + "Hãy kiếm đủ 9999 bí kiếp tao sẽ đổi cho mày cải trang yadart",
                                                "Đổi", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Mày có " + soLuong
                                                + " bí kiếp.\n"
                                                + "Hãy kiếm đủ 9999 bí kíp rồi tìm tao\n"
                                                + "Còn giờ thì cook =))",
                                                "Đóng");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 133) {
                                    Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
                                    int soLuong = 0;
                                    if (biKiep != null) {
                                        soLuong = biKiep.quantity;
                                    }
                                    if (soLuong >= 9999 && InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                        yardart.itemOptions.add(new ItemOption(50, 100));
                                        yardart.itemOptions.add(new ItemOption(77, 120));
                                        yardart.itemOptions.add(new ItemOption(103, 120));
                                        yardart.itemOptions.add(new ItemOption(101, 1000));
                                        yardart.itemOptions.add(new ItemOption(108, 10));
                                        yardart.itemOptions.add(new ItemOption(33, 1));
                                        yardart.itemOptions.add(new ItemOption(30, 1));
                                        InventoryService.gI().addItemBag(player, yardart, 0);
                                        InventoryService.gI().subQuantityItemsBag(player, biKiep, 9999);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player,
                                                "Bạn vừa nhận được trang phục tộc Yardart");
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GHI_DANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        String[] menuselect = new String[]{};

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Chào mừng bạn đến với đại hội võ thuật", "Đại Hội\nVõ Thuật\nLần Thứ\n23", "Giải siêu hạng");
                                } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
                                    int goldchallenge = player.goldChallenge;
                                    if (player.levelWoodChest == 0) {
                                        menuselect = new String[]{
                                            "Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng",
                                            "Về\nĐại Hội\nVõ Thuật"};
                                    } else {
                                        menuselect = new String[]{
                                            "Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng",
                                            "Nhận thưởng\nRương cấp\n" + player.levelWoodChest,
                                            "Về\nĐại Hội\nVõ Thuật"};
                                    }
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm,ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào",
                                            menuselect, "Từ chối");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
                                            switch (select) {
                                                case 0:
                                                    ChangeMapService.gI().changeMapNonSpaceship(player,
                                                            ConstMap.DAI_HOI_VO_THUAT_129, player.location.x, 360);
                                                    break;
                                                case 1:
                                                    ChangeMapService.gI().changeMapNonSpaceship(player, 113, player.location.x, 360);
                                                    break;
                                            }
                                        } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
                                            int goldchallenge = player.goldChallenge;
                                            if (player.levelWoodChest == 0) {
                                                switch (select) {
                                                    case 0:
                                                        if (InventoryService.gI().finditemWoodChest(player)) {
                                                            if (player.inventory.getGold() >= goldchallenge) {
                                                                MartialCongressService.gI().startChallenge(player);
                                                                player.inventory.subGold(goldchallenge);
                                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                                player.goldChallenge += 2000000000;
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Không đủ vàng, còn thiếu "
                                                                        + Util.numberToMoney(goldchallenge
                                                                                - player.inventory.gold)
                                                                        + " vàng kìa thằng lòn");
                                                            }
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Hãy mở rương báu vật trước");
                                                        }
                                                        break;
                                                    case 1:
                                                        ChangeMapService.gI().changeMapNonSpaceship(player,
                                                                ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
                                                        break;
                                                }
                                            } else {
                                                switch (select) {
                                                    case 0:
                                                        if (InventoryService.gI().finditemWoodChest(player)) {
                                                            if (player.inventory.getGold() >= goldchallenge) {
                                                                MartialCongressService.gI().startChallenge(player);
                                                                player.inventory.subGold(goldchallenge);
                                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                                player.goldChallenge += 2000000000;
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Không đủ vàng , còn thiếu "
                                                                        + Util.numberToMoney(goldchallenge
                                                                                - player.inventory.gold)
                                                                        + " vàng kìa thằng lòn");
                                                            }
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Hãy mở rương báu vật trước");
                                                        }
                                                        break;
                                                    case 1:
                                                        if (!player.receivedWoodChest) {
                                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                                Item it = ItemService.gI()
                                                                        .createNewItem((short) ConstItem.RUONG_GO);
                                                                it.itemOptions.add(new ItemOption(72, player.levelWoodChest));
                                                                it.itemOptions.add(new ItemOption(30, 0));
                                                                it.createTime = System.currentTimeMillis();
                                                                InventoryService.gI().addItemBag(player, it, 0);
                                                                InventoryService.gI().sendItemBags(player);

                                                                player.receivedWoodChest = true;
                                                                player.levelWoodChest = 0;
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Bạn nhận được rương gỗ");
                                                            } else {
                                                                this.npcChat(player, "Hành trang đã đầy");
                                                            }
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Mỗi ngày chỉ có thể nhận quà một lần");
                                                        }
                                                        break;
                                                    case 2:
                                                        ChangeMapService.gI().changeMapNonSpaceship(player,
                                                                ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
                                                        break;
                                                }
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.ANDROID_AODAI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Xin chào " + player.name + "\nTôi có thể giúp gì cho bạn"
                                        + "\n|5|-Đổi Capsule Bạc : Cần 99 chữ Vạn Sự Như Ý 2024 và 2 Thiệp chúc tết"
                                        + "\n-Đổi Túi  Vàng :  Cần 50 chữ Vạn Sự Như Ý 2024 và 1 Thiệp chúc tết"
                                        + "\n-Danh hiệu Thiên tử 1 Ngày (Chỉ số ngẫu nhiên) :  Cần 99 chữ Vạn Sự Như Ý 2024 và 10 Thiệp chúc tết"
                                        + "\n-Danh hiệu Thiên tử Vĩnh viễn (Chỉ số Max):  Cần 99 chữ Vạn Sự Như Ý 2024 và 99 Thiệp chúc tết + 2000 Thỏi  Vàng",
                                        "Capsule 2024", "Túi  Vàng", "Danh hiệu 1 Ngày", "Danh hiệu Vĩnh viễn");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
                                            case 0:
                                                Item chuvan = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_VAN, 99);
                                                Item chusu = InventoryService.gI().findItem(player, ConstItem.CHU_SU,
                                                        99);
                                                Item chunhu = InventoryService.gI().findItem(player, ConstItem.CHU_NHU,
                                                        99);
                                                Item chuy = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_Y, 99);
                                                Item chu2024 = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_2024, 99);
                                                Item thiep = InventoryService.gI().findItem(player,
                                                        ConstItem.THIEP_CHUC_TET_2024, 2);
                                                if (chuvan != null && chusu != null && chunhu != null
                                                        && chuy != null && chu2024 != null && thiep != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, chuvan, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chusu, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chunhu, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chuy, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chu2024, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, thiep, 2);

                                                    Item capsule2024 = ItemService.gI().createNewItem((short) ConstItem.CAPSULE_BAC);
                                                    capsule2024.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, capsule2024, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được " + capsule2024.template.name);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                            case 1:
                                                Item chuvana = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_VAN, 50);
                                                Item chusua = InventoryService.gI().findItem(player, ConstItem.CHU_SU,
                                                        50);
                                                Item chunhua = InventoryService.gI().findItem(player, ConstItem.CHU_NHU,
                                                        50);
                                                Item chuya = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_Y, 50);
                                                Item chu2024a = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_2024, 50);
                                                Item thiepa = InventoryService.gI().findItem(player,
                                                        ConstItem.THIEP_CHUC_TET_2024, 1);
                                                if (chuvana != null && chusua != null && chunhua != null
                                                        && chuya != null && chu2024a != null && thiepa != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, chuvana, 50);
                                                    InventoryService.gI().subQuantityItemsBag(player, chusua, 50);
                                                    InventoryService.gI().subQuantityItemsBag(player, chunhua, 50);
                                                    InventoryService.gI().subQuantityItemsBag(player, chuya, 50);
                                                    InventoryService.gI().subQuantityItemsBag(player, chu2024a, 50);
                                                    InventoryService.gI().subQuantityItemsBag(player, thiepa, 1);

                                                    Item tuivang = ItemService.gI().createNewItem((short) ConstItem.TUI_VANG);
                                                    tuivang.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, tuivang, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được " + tuivang.template.name);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                            case 2:
                                                Item chuvanb = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_VAN, 99);
                                                Item chusub = InventoryService.gI().findItem(player, ConstItem.CHU_SU,
                                                        99);
                                                Item chunhub = InventoryService.gI().findItem(player, ConstItem.CHU_NHU,
                                                        99);
                                                Item chuyb = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_Y, 99);
                                                Item chu2024b = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_2024, 99);
                                                Item thiepb = InventoryService.gI().findItem(player,
                                                        ConstItem.THIEP_CHUC_TET_2024, 10);
                                                if (chuvanb != null && chusub != null && chunhub != null
                                                        && chuyb != null && chu2024b != null && thiepb != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, chuvanb, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chusub, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chunhub, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chuyb, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chu2024b, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, thiepb, 10);

                                                    Item thientu = ItemService.gI().createNewItem((short) ConstItem.THIEN_TU);
                                                    thientu.itemOptions.add(new ItemOption(50, Util.nextInt(20, 40)));
                                                    thientu.itemOptions.add(new ItemOption(77, Util.nextInt(20, 55)));
                                                    thientu.itemOptions.add(new ItemOption(103, Util.nextInt(20, 55)));
                                                    thientu.itemOptions.add(new ItemOption(14, Util.nextInt(5, 15)));
                                                    thientu.itemOptions.add(new ItemOption(101, Util.nextInt(200, 1200)));
                                                    thientu.itemOptions.add(new ItemOption(30, 1));
                                                    thientu.itemOptions.add(new ItemOption(93, 1));
                                                    InventoryService.gI().addItemBag(player, thientu, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được " + thientu.template.name);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                            case 3:
                                                Item chuvanc = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_VAN, 99);
                                                Item chusuc = InventoryService.gI().findItem(player, ConstItem.CHU_SU,
                                                        99);
                                                Item chunhuc = InventoryService.gI().findItem(player, ConstItem.CHU_NHU,
                                                        99);
                                                Item chuyc = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_Y, 99);
                                                Item chu2024c = InventoryService.gI().findItem(player,
                                                        ConstItem.CHU_2024, 99);
                                                Item thiepc = InventoryService.gI().findItem(player,
                                                        ConstItem.THIEP_CHUC_TET_2024, 99);
                                                Item thoivang = InventoryService.gI().findItem(player,
                                                        ConstItem.THOI_VANG, 2000);
                                                if (chuvanc != null && chusuc != null && chunhuc != null
                                                        && chuyc != null && chu2024c != null && thiepc != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, chuvanc, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chusuc, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chunhuc, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chuyc, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, chu2024c, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, thiepc, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 2000);

                                                    Item thientuu = ItemService.gI().createNewItem((short) ConstItem.THIEN_TU);
                                                    thientuu.itemOptions.add(new ItemOption(50, 40));
                                                    thientuu.itemOptions.add(new ItemOption(77, 55));
                                                    thientuu.itemOptions.add(new ItemOption(103, 55));
                                                    thientuu.itemOptions.add(new ItemOption(14, 15));
                                                    thientuu.itemOptions.add(new ItemOption(101, 1200));
                                                    thientuu.itemOptions.add(new ItemOption(30, 1));
                                                    InventoryService.gI().addItemBag(player, thientuu, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được " + thientuu.template.name);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.NOI_BANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Xin chào " + player.name + "\nTôi là nồi nấu bánh\nTôi có thể giúp gì cho bạn"
                                        + "\n|5|-Làm Bánh Tét: (99)Thịt ba chỉ, Gạo nếp, Đỗ xanh, Lá chuối"
                                        + "\n-Làm Bánh Chưng: (99)Thịt Heo, Gạo nếp, Đỗ xanh, Lá dong"
                                        + "\n-Nấu Bánh Tét: (1)Bánh tét, Phụ gia tạo màu, Gia vị tổng hộp"
                                        + "\n-Nấu Bánh Chưng: (1)Bánh chưng, Phụ gia tạo màu, Gia vị tổng hộp"
                                        + "\n-Đổi Hộp quà: (5)Bánh chưng Chín, Bánh tét Chín"
                                        + "\n|3|(Làm bánh Thành công sẽ nhận 1 Điểm Sự kiện)",
                                        "Làm\nBánh Tét", "Làm\nBánh Chưng", getMenuLamBanh(player, 0),
                                        getMenuLamBanh(player, 1), "Đổi Hộp\nQuà Tết");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
                                            case 0:
                                                Item thitBaChi = InventoryService.gI().findItem(player,
                                                        ConstItem.THIT_BA_CHI, 99);
                                                Item gaoNep = InventoryService.gI().findItem(player, ConstItem.GAO_NEP,
                                                        99);
                                                Item doXanh = InventoryService.gI().findItem(player, ConstItem.DO_XANH,
                                                        99);
                                                Item laChuoi = InventoryService.gI().findItem(player,
                                                        ConstItem.LA_CHUOI, 99);
                                                if (thitBaChi != null && gaoNep != null && doXanh != null
                                                        && laChuoi != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, thitBaChi, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, gaoNep, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, doXanh, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, laChuoi, 99);
                                                    Item banhtet = ItemService.gI()
                                                            .createNewItem((short) ConstItem.BANH_TET_2023);
                                                    banhtet.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, banhtet, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được Bánh Tét");
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                            case 1:
                                                Item thitHeo1 = InventoryService.gI().findItem(player,
                                                        ConstItem.THIT_HEO_2023, 99);
                                                Item gaoNep1 = InventoryService.gI().findItem(player, ConstItem.GAO_NEP,
                                                        99);
                                                Item doXanh1 = InventoryService.gI().findItem(player, ConstItem.DO_XANH,
                                                        99);
                                                Item laDong1 = InventoryService.gI().findItem(player,
                                                        ConstItem.LA_DONG_2023, 99);
                                                if (thitHeo1 != null && gaoNep1 != null && doXanh1 != null
                                                        && laDong1 != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, thitHeo1, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, gaoNep1, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, doXanh1, 99);
                                                    InventoryService.gI().subQuantityItemsBag(player, laDong1, 99);
                                                    Item banhChung = ItemService.gI()
                                                            .createNewItem((short) ConstItem.BANH_CHUNG_2023);
                                                    banhChung.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, banhChung, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được Bánh Chưng");
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                            case 2:
                                                if (!player.event.isCookingTetCake()) {
                                                    Item banhTet2 = InventoryService.gI().findItem(player,
                                                            ConstItem.BANH_TET_2023, 1);
                                                    Item phuGiaTaoMau2 = InventoryService.gI().findItem(player,
                                                            ConstItem.PHU_GIA_TAO_MAU, 1);
                                                    Item giaVi2 = InventoryService.gI().findItem(player,
                                                            ConstItem.GIA_VI_TONG_HOP, 1);

                                                    if (banhTet2 != null && phuGiaTaoMau2 != null && giaVi2 != null) {
                                                        InventoryService.gI().subQuantityItemsBag(player, banhTet2, 1);
                                                        InventoryService.gI().subQuantityItemsBag(player, phuGiaTaoMau2,
                                                                1);
                                                        InventoryService.gI().subQuantityItemsBag(player, giaVi2, 1);
                                                        InventoryService.gI().sendItemBags(player);
                                                        player.event.setTimeCookTetCake(300);
                                                        player.event.setCookingTetCake(true);
                                                        Service.getInstance().sendThongBao(player,
                                                                "Bắt đầu nấu bánh,thời gian nấu bánh là 5 phút");
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ nguyên liệu");
                                                    }
                                                } else if (player.event.isCookingTetCake()
                                                        && player.event.getTimeCookTetCake() == 0) {
                                                    Item cake = ItemService.gI()
                                                            .createNewItem((short) ConstItem.BANH_TET_CHIN, 1);
                                                    cake.itemOptions.add(new ItemOption(77, 20));
                                                    cake.itemOptions.add(new ItemOption(103, 20));
                                                    cake.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, cake, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    player.event.setCookingTetCake(false);
                                                    player.event.addEventPoint(1);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được Bánh Tét (đã chính) và 1 điểm sự kiện");
                                                }
                                                break;
                                            case 3:
                                                if (!player.event.isCookingChungCake()) {
                                                    Item banhChung3 = InventoryService.gI().findItem(player,
                                                            ConstItem.BANH_CHUNG_2023, 1);
                                                    Item phuGiaTaoMau3 = InventoryService.gI().findItem(player,
                                                            ConstItem.PHU_GIA_TAO_MAU, 1);
                                                    Item giaVi3 = InventoryService.gI().findItem(player,
                                                            ConstItem.GIA_VI_TONG_HOP, 1);

                                                    if (banhChung3 != null && phuGiaTaoMau3 != null && giaVi3 != null) {
                                                        InventoryService.gI().subQuantityItemsBag(player, banhChung3,
                                                                1);
                                                        InventoryService.gI().subQuantityItemsBag(player, phuGiaTaoMau3,
                                                                1);
                                                        InventoryService.gI().subQuantityItemsBag(player, giaVi3, 1);
                                                        InventoryService.gI().sendItemBags(player);
                                                        player.event.setTimeCookChungCake(300);
                                                        player.event.setCookingChungCake(true);
                                                        Service.getInstance().sendThongBao(player,
                                                                "Bắt đầu nấu bánh,thời gian nấu bánh là 5 phút");
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ nguyên liệu");
                                                    }
                                                } else if (player.event.isCookingChungCake()
                                                        && player.event.getTimeCookChungCake() == 0) {
                                                    Item cake = ItemService.gI()
                                                            .createNewItem((short) ConstItem.BANH_CHUNG_CHIN, 1);
                                                    cake.itemOptions.add(new ItemOption(50, 20));
                                                    cake.itemOptions.add(new ItemOption(5, 15));
                                                    cake.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, cake, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    player.event.setCookingChungCake(false);
                                                    player.event.addEventPoint(1);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được Bánh Chưng (đã chín) và 1 điểm sự kiện");
                                                }
                                                break;
                                            case 4:
                                                Item tetCake = InventoryService.gI().findItem(player,
                                                        ConstItem.BANH_TET_CHIN, 5);
                                                Item chungCake = InventoryService.gI().findItem(player,
                                                        ConstItem.BANH_CHUNG_CHIN, 5);
                                                if (chungCake != null && tetCake != null) {
                                                    Item hopQua = ItemService.gI()
                                                            .createNewItem((short) ConstItem.HOP_QUA_TET_2023, 1);
                                                    hopQua.itemOptions.add(new ItemOption(30, 0));
                                                    hopQua.itemOptions.add(new ItemOption(74, 0));

                                                    InventoryService.gI().subQuantityItemsBag(player, tetCake, 5);
                                                    InventoryService.gI().subQuantityItemsBag(player, chungCake, 5);
                                                    InventoryService.gI().addItemBag(player, hopQua, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn nhận được Hộp quà tết");
                                                } else {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Không đủ nguyên liệu để đổi");
                                                }
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CUA_HANG_KY_GUI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Của hàng chúng tôi chuyên bán hàng hiệu,hàng độc,nếu bạn không chê thì mại đzô",
                                        "Không có\nHướng dẫn", "Mua bán", "Danh sách\nHết Hạn", "Hủy");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
                                            case 1:
                                                if (!Manager.gI().getGameConfig().isOpenSuperMarket()) {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Chức năng kí gửi chưa mở,vui lòng quay lại sau");
                                                    return;
                                                }
                                                ConsignmentShop.getInstance().show(player);
                                                break;
                                            case 2:
                                                ConsignmentShop.getInstance().showExpiringItems(player);
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.MAY_GAP_THU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "|8|MÁY GẮP THÚ\n"
                                        + "|7|TRANG BỊ SIÊU CẤP VIP PRO\n"
                                        + "|3|Chỉ số đồ sẽ cao hơn đồ shop rất nhiều\n"
                                        + "gắp bằng xu sẽ có thể nhận về trang bị có chỉ số cao hơn\n\n"
                                        + "|2|CHÚC BẠN MAY MẮN",
                                        "Thỏi\nVàng", "XU");
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, 123457, "|8|GACHA BẰNG THỎI VÀNG\n"
                                                    + "|3|CHỈ SỐ ĐỒ NHẬN VỀ SẼ NGẪU NHIÊN\n"
                                                    + "|2|- Sức Đánh - Hp Ki trong khoảng 10-20%\n"
                                                    + "- Sdcm, Chí mạng trong khoảng 1-5%\n"
                                                    + "- Đẹp trong khoảng 30-45%\n"
                                                    + "- Tiềm năng sức mạnh trong khoảng 50-100%\n"
                                                    + "|3|*** Mỗi lần gacha tốn " + GACHA_TV + " Thỏi vàng",
                                                    "Cải\nTrang", "Linh\nThú", "Pet", "Thú Cưỡi");//
                                            break;
                                        case 1:
                                            this.createOtherMenu(player, 123456, "|8|GACHA BẰNG XU\n"
                                                    + "|3|CHỈ SỐ ĐỒ NHẬN VỀ SẼ NGẪU NHIÊN\n"
                                                    + "|2|- Sức Đánh - Hp Ki trong khoảng 20-30%\n"
                                                    + "- Sdcm, Chí mạng trong khoảng 5-9%\n"
                                                    + "- Tiềm năng sức mạnh trong khoảng 100-200%\n"
                                                    + "|3|*** Mỗi lần gacha tốn " + GACHA_XU + " Xu",
                                                    "Cải\nTrang", "Linh\nThú", "Pet", "Thú Cưỡi");//
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == 123457) {
                                        Item thoiVang = null;
                                        try {
                                            thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
                                        } catch (Exception e) {
                                        }
                                        switch (select) {
                                            case 0:
                                                if (thoiVang == null || thoiVang.quantity < GACHA_TV) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ " + GACHA_TV + " thỏi vàng");
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    int[] ct = new int[]{1251, 1252, 1253, 1625, 1627, 1641, 1642, 1643, 1631, 1620, 1632, 1626,
                                                        1411, 1410, 1409, 1416, 1600, 1624,1628, 1412, 1638, 1629, 1686, 1214, 1384, 1636, 1639,
                                                        1634, 1635, 1319, 1618, 1619
                                                    };
                                                    int randomCt = new Random().nextInt(ct.length);
                                                    Item caiTrang = ItemService.gI().createNewItem((short) ct[randomCt]);
                                                    caiTrang.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                                                    caiTrang.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                                                    caiTrang.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                                                    caiTrang.itemOptions.add(new ItemOption(5, Util.nextInt(1, 5)));
                                                    caiTrang.itemOptions.add(new ItemOption(101, Util.nextInt(50, 100)));
                                                    caiTrang.itemOptions.add(new ItemOption(117, Util.nextInt(1, 5)));
                                                    caiTrang.itemOptions.add(new ItemOption(94, Util.nextInt(10, 20)));
                                                    caiTrang.itemOptions.add(new ItemOption(106, 1));
                                                    caiTrang.itemOptions.add(new ItemOption(116, 1));
                                                    caiTrang.itemOptions.add(new ItemOption(33, 1));
                                                    caiTrang.itemOptions.add(new ItemOption(76, 1));
                                                    caiTrang.itemOptions.add(new ItemOption(73, 1));
                                                    InventoryService.gI().subQuantityItemsBag(player, thoiVang, GACHA_TV);
                                                    InventoryService.gI().addItemBag(player, caiTrang, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + caiTrang.template.name);
                                                }
                                                break;
                                            case 1:
                                                if (thoiVang == null || thoiVang.quantity < GACHA_TV) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ " + GACHA_TV + " thỏi vàng");
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    int[] linhthu = new int[]{
                                                        1295, 1344, 1374, 1375, 1376, 1377, 1378, 1379, 1380, 1381,
                                                        1382, 1540, 1541, 1542, 1543, 1544, 1545, 1546, 1547, 1548,
                                                        1589, 1598, 1599, 1601, 1614, 1615, 1616
                                                    };
                                                    int randomLinhthu = new Random().nextInt(linhthu.length);
                                                    Item linhThu = ItemService.gI().createNewItem((short) linhthu[randomLinhthu]);
                                                    linhThu.itemOptions.add(new ItemOption(50, Util.nextInt(10, 400)));
                                                    linhThu.itemOptions.add(new ItemOption(77, Util.nextInt(10, 400)));
                                                    linhThu.itemOptions.add(new ItemOption(103, Util.nextInt(300, 400)));
                                                    linhThu.itemOptions.add(new ItemOption(101, Util.nextInt(100, 150)));
                                                    linhThu.itemOptions.add(new ItemOption(5, Util.nextInt(35, 45)));
                                                    linhThu.itemOptions.add(new ItemOption(173, Util.nextInt(3, 5)));
                                                    linhThu.itemOptions.add(new ItemOption(209, Util.nextInt(3, 5)));
                                                    linhThu.itemOptions.add(new ItemOption(76, 1));
                                                    linhThu.itemOptions.add(new ItemOption(73, 1));
                                                    InventoryService.gI().subQuantityItemsBag(player, thoiVang, GACHA_TV);
                                                    InventoryService.gI().addItemBag(player, linhThu, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + linhThu.template.name);
                                                }
                                                break;
                                            case 2://random pet
                                                if (thoiVang == null || thoiVang.quantity < GACHA_TV) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ " + GACHA_TV + " thỏi vàng");
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    short[] pet = {1644, 1645, 1646, 1647, 1648, 2036, 1196, 1197, 1198, 1221, 1222 , 1223};
                                                    byte randompet = (byte) Util.nextInt(0, pet.length - 1);
                                                    Item petRandom = ItemService.gI().createNewItem(pet[randompet]);
                                                    petRandom.itemOptions.add(new ItemOption(50, Util.nextInt(300, 400)));
                                                    petRandom.itemOptions.add(new ItemOption(77, Util.nextInt(300, 400)));
                                                    petRandom.itemOptions.add(new ItemOption(103, Util.nextInt(300, 400)));
                                                    petRandom.itemOptions.add(new ItemOption(14, Util.nextInt(25, 35)));
                                                    petRandom.itemOptions.add(new ItemOption(5, Util.nextInt(35, 45)));
                                                    petRandom.itemOptions.add(new ItemOption(97, Util.nextInt(25, 35)));
                                                    petRandom.itemOptions.add(new ItemOption(80, Util.nextInt(25, 35)));
                                                    petRandom.itemOptions.add(new ItemOption(81, Util.nextInt(25, 35)));
                                                    petRandom.itemOptions.add(new ItemOption(76, 1));
                                                    petRandom.itemOptions.add(new ItemOption(73, 1));
                                                    InventoryService.gI().subQuantityItemsBag(player, thoiVang, GACHA_TV);
                                                    InventoryService.gI().addItemBag(player, petRandom, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendMoney(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + petRandom.template.name);
                                                }
                                                break;
                                            case 3:
                                                if (thoiVang == null || thoiVang.quantity < GACHA_TV) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ " + GACHA_TV + " thỏi vàng");
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    int[] thucuoigold = new int[]{1502, 1503, 1142, 1092, 1227};
                                                    int randomThuCuoi = new Random().nextInt(thucuoigold.length);
                                                    Item thucuoiGold = ItemService.gI().createNewItem((short) thucuoigold[randomThuCuoi]);
                                                    thucuoiGold.itemOptions.add(new ItemOption(50, Util.nextInt(300, 400)));
                                                    thucuoiGold.itemOptions.add(new ItemOption(77, Util.nextInt(300, 400)));
                                                    thucuoiGold.itemOptions.add(new ItemOption(103, Util.nextInt(300, 400)));
                                                    thucuoiGold.itemOptions.add(new ItemOption(5, Util.nextInt(35, 45)));
                                                    thucuoiGold.itemOptions.add(new ItemOption(95, Util.nextInt(30, 45)));
                                                    thucuoiGold.itemOptions.add(new ItemOption(96, Util.nextInt(30, 45)));
                                                    thucuoiGold.itemOptions.add(new ItemOption(16, 50));
                                                    thucuoiGold.itemOptions.add(new ItemOption(76, 0));
                                                    thucuoiGold.itemOptions.add(new ItemOption(73, 0));
                                                    InventoryService.gI().addItemBag(player, thucuoiGold, 1);
                                                    InventoryService.gI().subQuantityItemsBag(player, thoiVang, GACHA_TV);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + thucuoiGold.template.name);
                                                }
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == 123456) {
                                        Item xubac = InventoryService.gI().findItemBagByTemp(player, ID_XU);
                                        switch (select) {
                                            case 0:
                                                if (xubac == null || xubac.quantity < 10000) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ tiền cần : " + GACHA_XU + " Xu bạc");
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    int[] ct = new int[]{1251, 1252, 1253, 1625, 1627, 1641, 1642, 1643, 1631, 1620, 1632, 1626,
                                                        1628, 1412, 1638, 1629, 1686, 1214, 1384, 1636, 1639, 1634, 1635, 1319, 1618, 1619
                                                    };
                                                    int randomCt = new Random().nextInt(ct.length);
                                                    Item caiTrang = ItemService.gI().createNewItem((short) ct[randomCt]);
                                                    caiTrang.itemOptions.add(new ItemOption(50, Util.nextInt(400, 500)));
                                                    caiTrang.itemOptions.add(new ItemOption(77, Util.nextInt(400, 500)));
                                                    caiTrang.itemOptions.add(new ItemOption(103, Util.nextInt(400, 500)));
                                                    caiTrang.itemOptions.add(new ItemOption(5, Util.nextInt(60, 75)));
                                                    caiTrang.itemOptions.add(new ItemOption(101, Util.nextInt(200, 300)));
                                                    caiTrang.itemOptions.add(new ItemOption(117, Util.nextInt(35, 55)));
                                                    caiTrang.itemOptions.add(new ItemOption(94, Util.nextInt(170, 200)));
                                                    caiTrang.itemOptions.add(new ItemOption(106, 1));
                                                    caiTrang.itemOptions.add(new ItemOption(116, 1));
                                                    caiTrang.itemOptions.add(new ItemOption(73, 1));
                                                    caiTrang.itemOptions.add(new ItemOption(30, 1));
                                                    InventoryService.gI().subQuantityItemsBag(player, xubac, GACHA_XU);
                                                    InventoryService.gI().addItemBag(player, caiTrang, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + caiTrang.template.name);
                                                }
                                                break;
                                            case 1:
                                                if (xubac == null || xubac.quantity < 10000) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ tiền cần : " + GACHA_XU + " " + xubac.getName());
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    int[] linhthu = new int[]{
                                                        1295, 1344, 1374, 1375, 1376, 1377, 1378, 1379, 1380, 1381,
                                                        1382, 1540, 1541, 1542, 1543, 1544, 1545, 1546, 1547, 1548,
                                                        1549, 1550, 1551, 1552, 1573, 1574, 1575, 1576, 1577, 1578,
                                                        1589, 1598, 1599, 1601,1614, 1615, 1616
                                                    };
                                                    int randomLinhthu = new Random().nextInt(linhthu.length);
                                                    Item linhThu = ItemService.gI().createNewItem((short) linhthu[randomLinhthu]);
                                                    linhThu.itemOptions.add(new ItemOption(50, Util.nextInt(320, 450)));
                                                    linhThu.itemOptions.add(new ItemOption(77, Util.nextInt(320, 450)));
                                                    linhThu.itemOptions.add(new ItemOption(103, Util.nextInt(320, 450)));
                                                    linhThu.itemOptions.add(new ItemOption(101, Util.nextInt(200, 300)));
                                                    linhThu.itemOptions.add(new ItemOption(5, Util.nextInt(40, 50)));
                                                    linhThu.itemOptions.add(new ItemOption(173, Util.nextInt(4, 7)));
                                                    linhThu.itemOptions.add(new ItemOption(209, Util.nextInt(4, 7)));
                                                    linhThu.itemOptions.add(new ItemOption(73, 1));
                                                    InventoryService.gI().subQuantityItemsBag(player, xubac, GACHA_XU);
                                                    InventoryService.gI().addItemBag(player, linhThu, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + linhThu.template.name);
                                                }
                                                break;
                                            case 2:
                                                if (xubac == null || xubac.quantity < 10000) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ tiền cần : " + GACHA_XU + " " + xubac.getName());
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    short[] pet = {1644, 1645, 1646, 1647, 1648, 2036, 1196, 1197, 1198, 1221, 1222, 1223};
                                                    byte randompet = (byte) Util.nextInt(0, pet.length - 1);
                                                    Item petRandom = ItemService.gI().createNewItem(pet[randompet]);
                                                    petRandom.itemOptions.add(new ItemOption(50, Util.nextInt(320, 450)));
                                                    petRandom.itemOptions.add(new ItemOption(77, Util.nextInt(320, 450)));
                                                    petRandom.itemOptions.add(new ItemOption(103, Util.nextInt(320, 450)));
                                                    petRandom.itemOptions.add(new ItemOption(14, Util.nextInt(35, 55)));
                                                    petRandom.itemOptions.add(new ItemOption(5, Util.nextInt(40, 50)));
                                                    petRandom.itemOptions.add(new ItemOption(97, Util.nextInt(35, 55)));
                                                    petRandom.itemOptions.add(new ItemOption(80, Util.nextInt(35, 55)));
                                                    petRandom.itemOptions.add(new ItemOption(81, Util.nextInt(35, 55)));
                                                    petRandom.itemOptions.add(new ItemOption(73, 1));
                                                    InventoryService.gI().subQuantityItemsBag(player, xubac, GACHA_XU);
                                                    InventoryService.gI().addItemBag(player, petRandom, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendMoney(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + petRandom.template.name);
                                                }
                                                break;
                                            case 3:
                                                if (xubac == null || xubac.quantity < 10000) {
                                                    Service.getInstance().sendThongBaoOK(player, "Không đủ tiền cần : " + GACHA_XU + " " + xubac.getName());
                                                    return;
                                                }
                                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                                    Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
                                                    return;
                                                } else {
                                                    int[] thucuoiVip = new int[]{1502, 1503, 1142, 1227};
                                                    int randomThuCuoi = new Random().nextInt(thucuoiVip.length);
                                                    Item thucuoiCoin = ItemService.gI().createNewItem((short) thucuoiVip[randomThuCuoi]);
                                                    thucuoiCoin.itemOptions.add(new ItemOption(50, Util.nextInt(320, 450)));
                                                    thucuoiCoin.itemOptions.add(new ItemOption(77, Util.nextInt(320, 450)));
                                                    thucuoiCoin.itemOptions.add(new ItemOption(103, Util.nextInt(320, 450)));
                                                    thucuoiCoin.itemOptions.add(new ItemOption(5, Util.nextInt(40, 55)));
                                                    thucuoiCoin.itemOptions.add(new ItemOption(95, Util.nextInt(35, 55)));
                                                    thucuoiCoin.itemOptions.add(new ItemOption(96, Util.nextInt(35, 55)));
                                                    thucuoiCoin.itemOptions.add(new ItemOption(16, 50));
                                                    thucuoiCoin.itemOptions.add(new ItemOption(73, 0));
                                                    InventoryService.gI().subQuantityItemsBag(player, xubac, GACHA_XU);
                                                    InventoryService.gI().addItemBag(player, thucuoiCoin, 1);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + thucuoiCoin.template.name);
                                                }
                                                break;
                                        }
                                    }
                            }
                        }
                    };
                    break;
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

// girlkun75-mark
    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
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
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 29028) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.HOP_BANH_NUONG:
                        switch (select) {
                            case 0:
                                Item thoivang2 = null;
                                Item botbanh = null;
                                Item thitheo = null;
                                Item trungvit = null;
                                Item ngucoc = null;
                                Item hopdungbanhnuong = null;
                                try {
                                    thoivang2 = InventoryService.gI().findItemBagByTemp(player, (short) 457);
                                    botbanh = InventoryService.gI().findItemBagByTemp(player, (short) 2044);
                                    thitheo = InventoryService.gI().findItemBagByTemp(player, (short) 2045);
                                    trungvit = InventoryService.gI().findItemBagByTemp(player, (short) 2046);
                                    ngucoc = InventoryService.gI().findItemBagByTemp(player, (short) 2047);
                                    hopdungbanhnuong = InventoryService.gI().findItemBagByTemp(player, (short) 2042);
                                } catch (Exception e) {
                                }
                                if (thoivang2 == null || thoivang2.quantity < 99
                                        || botbanh == null || botbanh.quantity < 99
                                        || thitheo == null || thitheo.quantity < 99
                                        || trungvit == null || trungvit.quantity < 99
                                        || ngucoc == null || trungvit.quantity < 99
                                        || hopdungbanhnuong == null || hopdungbanhnuong.quantity < 1) {
                                    Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                } else {
                                    Item banhnuong = ItemService.gI().createNewItem((short) 2040);
//                                    banhnuong.itemOptions.add(new ItemOption(76, 1));
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
                                break;
                        }
                        break;
                    case ConstNpc.HOP_BANH_DEO:
                        switch (select) {
                            case 0:
                                Item thoivang3 = null;
                                Item botbanh = null;
                                Item ngucoc = null;
                                Item dauxanh = null;
                                Item duongtrang = null;
                                Item hopdungbanhdeo = null;
                                try {
                                    thoivang3 = InventoryService.gI().findItemBagByTemp(player, (short) 457);
                                    botbanh = InventoryService.gI().findItemBagByTemp(player, (short) 2044);
                                    ngucoc = InventoryService.gI().findItemBagByTemp(player, (short) 2047);
                                    dauxanh = InventoryService.gI().findItemBagByTemp(player, (short) 2048);
                                    duongtrang = InventoryService.gI().findItemBagByTemp(player, (short) 2049);
                                    hopdungbanhdeo = InventoryService.gI().findItemBagByTemp(player, (short) 2043);
                                } catch (Exception e) {
                                }
                                if (thoivang3 == null || thoivang3.quantity < 99
                                        || botbanh == null || botbanh.quantity < 99
                                        || ngucoc == null || ngucoc.quantity < 99
                                        || dauxanh == null || dauxanh.quantity < 99
                                        || duongtrang == null || duongtrang.quantity < 99
                                        || hopdungbanhdeo == null || hopdungbanhdeo.quantity < 1) {
                                    Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                } else {
                                    Item banhdeo = ItemService.gI().createNewItem((short) 2041);
//                                    banhdeo.itemOptions.add(new ItemOption(76, 1));
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
                                break;
                        }
                        break;
                    case ConstNpc.MENU_TO_500K:
                        switch (select) {
                            case 0:
                                Item thoivang3 = null;
                                Item to500k = null;
                                try {
                                    thoivang3 = InventoryService.gI().findItemBagByTemp(player, (short) 457);
                                    to500k = InventoryService.gI().findItemBagByTemp(player, (short) 1707);
                                } catch (Exception e) {
                                }
                                if (thoivang3 == null || thoivang3.quantity < 99000
                                        || to500k == null || to500k.quantity < 1) {
                                    Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                                } else {
                                    Item to500kkokhoa = ItemService.gI().createNewItem((short) 1707);
//                                    banhdeo.itemOptions.add(new ItemOption(76, 1));
                                    InventoryService.gI().subQuantityItemsBag(player, thoivang3, 99000);
                                    InventoryService.gI().subQuantityItemsBag(player, to500k, 1);
                                    InventoryService.gI().addItemBag(player, to500kkokhoa, 1);
                                    InventoryService.gI().sendItemBags(player);
                                    Service.getInstance().sendThongBao(player, "Mày đã nhận được " + to500kkokhoa.template.name);
                                }
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_DIALOG:
                        ConfirmDialog confirmDialog = player.getConfirmDialog();
                        if (confirmDialog != null) {
                            if (confirmDialog instanceof MenuDialog menu) {
                                menu.getRunable().setIndexSelected(select);
                                menu.run();
                                return;
                            }
                            if (select == 0) {
                                confirmDialog.run();
                            } else {
                                confirmDialog.cancel();
                            }
                            player.setConfirmDialog(null);
                        }
                        break;
                    case ConstNpc.UP_TOP_ITEM:

                        break;
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
                        break;

                    case ConstNpc.SKH_THANH_TON:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhVip(player, 0, -1, (short) 1996, 1);
                                break;
                            case 1:
                                UseItem.gI().openskhVip(player, 1, -1, (short) 1996, 1);
                                break;
                            case 2:
                                UseItem.gI().openskhVip(player, 2, -1, (short) 1996, 1);
                                break;
                        }
                        break;
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
                        break;
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
                        break;
                    case ConstNpc.SKH_TD:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhNomal(player, 0, 0, (short) 2000);
                                break;
                            case 1:
                                UseItem.gI().openskhNomal(player, 0, 1, (short) 2000);
                                break;
                            case 2:
                                UseItem.gI().openskhNomal(player, 0, 2, (short) 2000);
                                break;
                        }
                        break;
                    case ConstNpc.SKH_NM:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhNomal(player, 1, 0, (short) 2001);
                                break;
                            case 1:
                                UseItem.gI().openskhNomal(player, 1, 1, (short) 2001);
                                break;
                            case 2:
                                UseItem.gI().openskhNomal(player, 1, 2, (short) 2001);
                                break;
                        }
                        break;
                    case ConstNpc.SKH_XD:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhNomal(player, 2, 0, (short) 2002);
                                break;
                            case 1:
                                UseItem.gI().openskhNomal(player, 2, 1, (short) 2002);
                                break;
                            case 2:
                                UseItem.gI().openskhNomal(player, 2, 2, (short) 2002);
                                break;
                        }
                        break;
                    case ConstNpc.SET_TD_TL:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhVip(player, 0, 0, (short) 1997, 2);
                                break;
                            case 1:
                                UseItem.gI().openskhVip(player, 0, 1, (short) 1997, 2);
                                break;
                            case 2:
                                UseItem.gI().openskhVip(player, 0, 2, (short) 1997, 2);
                                break;
                        }
                        break;
                    case ConstNpc.SET_NM_TL:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhVip(player, 1, 0, (short) 1997, 2);
                                break;
                            case 1:
                                UseItem.gI().openskhVip(player, 1, 1, (short) 1997, 2);
                                break;
                            case 2:
                                UseItem.gI().openskhVip(player, 1, 2, (short) 1997, 2);
                                break;
                        }
                        break;
                    case ConstNpc.SET_XD_TL:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhVip(player, 2, 0, (short) 1997, 2);
                                break;
                            case 1:
                                UseItem.gI().openskhVip(player, 2, 1, (short) 1997, 2);
                                break;
                            case 2:
                                UseItem.gI().openskhVip(player, 2, 2, (short) 1997, 2);
                                break;
                        }
                        break;
                    case ConstNpc.SET_TD_HD:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhVip(player, 0, 0, (short) 1998, 3);
                                break;
                            case 1:
                                UseItem.gI().openskhVip(player, 0, 1, (short) 1998, 3);
                                break;
                            case 2:
                                UseItem.gI().openskhVip(player, 0, 2, (short) 1998, 3);
                                break;
                        }
                        break;
                    case ConstNpc.SET_NM_HD:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhVip(player, 1, 0, (short) 1998, 3);
                                break;
                            case 1:
                                UseItem.gI().openskhVip(player, 1, 1, (short) 1998, 3);
                                break;
                            case 2:
                                UseItem.gI().openskhVip(player, 1, 2, (short) 1998, 3);
                                break;
                        }
                        break;
                    case ConstNpc.SET_XD_HD:
                        switch (select) {
                            case 0:
                                UseItem.gI().openskhVip(player, 2, 0, (short) 1998, 3);
                                break;
                            case 1:
                                UseItem.gI().openskhVip(player, 2, 1, (short) 1998, 3);
                                break;
                            case 2:
                                UseItem.gI().openskhVip(player, 2, 2, (short) 1998, 3);
                                break;
                        }
                        break;
                    case ConstNpc.MENU_TD:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settaiyoken(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setkamejoko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.TAIXIU:
                        String time = ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                        if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldTai == 0 && player.goldXiu == 0 && TaiXiu.gI().baotri == false) {
                            switch (select) {
                                case 0:
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time, "Cập nhập", "Theo TÀI", "Theo XỈU", "Đóng");
                                    break;
                                case 1:
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
                                        Input.gI().TAI_taixiu(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Bạn chưa đủ điều kiện để chơi");
                                    }
                                    break;
                                case 2:
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
                                        Input.gI().XIU_taixiu(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Bạn chưa đủ điều kiện để chơi");
                                    }
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldTai > 0 && TaiXiu.gI().baotri == false) {
                            switch (select) {
                                case 0:
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Tài : " + Util.format(player.goldTai) + " Hồng ngọc", "Cập nhập", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldXiu > 0 && TaiXiu.gI().baotri == false) {
                            switch (select) {
                                case 0:
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Xỉu : " + Util.format(player.goldXiu) + " Hồng ngọc", "Cập nhập", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldTai > 0 && TaiXiu.gI().baotri == true) {
                            switch (select) {
                                case 0:
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Tài : " + Util.format(player.goldTai) + " Hồng ngọc" + "\n\n|7|Hệ thống sắp bảo trì", "Cập nhập", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldXiu > 0 && TaiXiu.gI().baotri == true) {
                            switch (select) {
                                case 0:
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Bạn đã cược Xỉu : " + Util.format(player.goldXiu) + " Hồng ngọc" + "\n\n|7|Hệ thống sắp bảo trì", "Cập nhập", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldXiu == 0 && player.goldTai == 0 && TaiXiu.gI().baotri == true) {
                            switch (select) {
                                case 0:
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time + "\n\n|7|Hệ thống sắp bảo trì", "Cập nhập", "Đóng");
                                    break;
                            }
                        }
                        break;

                    case ConstNpc.MENU_NM:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgoddam(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setsummon(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.MENU_XD:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodgalick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setmonkey(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setgodhp(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.RUONG_GO:
                        int size = player.textRuongGo.size();
                        if (size > 0) {
                            String menuselect = "OK [" + (size - 1) + "]";
                            if (size == 1) {
                                menuselect = "OK";
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1,
                                    player.textRuongGo.get(size - 1), menuselect);
                            player.textRuongGo.remove(size - 1);
                        }
                        break;
                    case ConstNpc.MENU_MABU_WAR:
                        if (select == 0) {
                            if (player.zone.finishMabuWar) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                            } else if (player.zone.map.mapId == 119) {
                                Zone zone = MabuWar.gI().getMapLastFloor(120);
                                if (zone != null) {
                                    ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                }
                            } else {
                                int idMapNextFloor = player.zone.map.mapId == 115 ? player.zone.map.mapId + 2
                                        : player.zone.map.mapId + 1;
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
                                Service.getInstance().sendThongBao(player,
                                        "Bạn đen vô cùng luôn nên không nhận được gì cả");
                            }
                        }
                        break;
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP:
                        // PVP_old.gI().sendInvitePVP(player, (byte) select);
                        PVPServcice.gI().sendInvitePVP(player, (byte) select);
                        break;
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPServcice.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.SUMMON_BLACK_SHENRON:
                        if (select == 0) {
                            SummonDragon.gI().summonBlackShenron(player);
                        }
                        break;
                    case ConstNpc.SUMMON_ICE_SHENRON:
                        if (select == 0) {
                            SummonDragon.gI().summonIceShenron(player);
                        }
                        break;
                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
//                    case ConstNpc.BAN_PLAYER:
//                        if (select == 0) {
//                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
//                            Service.getInstance().sendThongBao(player,
//                                    "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
//                        }
//                        break;
                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.getInstance().sendThongBao(player, "Phát đệ tử cho "
                                        + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.DUNG_NHIEU_TV:
                        Item thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                        switch (select) {
                            case 0:
                                if (thoivang == null || thoivang.quantity < 1) {
                                    Service.getInstance().sendThongBao(player, "Cần có đủ 1 Thỏi  Vàng để thực hiện");
                                    return;
                                }
                                if (player.inventory.gold + 500_000_000 > player.inventory.getGoldLimit()) {
                                    Service.getInstance().sendThongBao(player, "Vàng sau khi nhận vượt quá giới hạn");
                                } else {
                                    player.inventory.gold += 500_000_000;
                                    Service.getInstance().sendThongBao(player, "|4|Bạn nhận được 500 Triệu Vàng");
                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 1);
                                    InventoryService.gI().sendItemBags(player);
                                    Service.getInstance().sendMoney(player);
                                    break;
                                }
                                break;
                            case 1:
                                if (thoivang == null || thoivang.quantity < 5) {
                                    Service.getInstance().sendThongBao(player, "Cần có đủ 5 Thỏi  Vàng để thực hiện");
                                    return;
                                }
                                if (player.inventory.gold + 2_500_000_000L > player.inventory.getGoldLimit()) {
                                    Service.getInstance().sendThongBao(player, "Vàng sau khi nhận vượt quá giới hạn");
                                } else {
                                    player.inventory.gold += 2_500_000_000L;
                                    Service.getInstance().sendThongBao(player, "|4|Bạn nhận được 2,5 Tỷ Vàng");
                                    Service.getInstance().sendMoney(player);
                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 5);
                                    InventoryService.gI().sendItemBags(player);
                                    break;
                                }
                                break;
                            case 2:
                                if (thoivang == null || thoivang.quantity < 10) {
                                    Service.getInstance().sendThongBao(player, "Cần có đủ 10 Thỏi  Vàng để thực hiện");
                                    return;
                                }
                                if (player.inventory.gold + 5_000_000_000L > player.inventory.getGoldLimit()) {
                                    Service.getInstance().sendThongBao(player, "Vàng sau khi nhận vượt quá giới hạn");
                                } else {
                                    player.inventory.gold += 5_000_000_000L;
                                    Service.getInstance().sendThongBao(player, "|4|Bạn nhận được 5 Tỷ Vàng");
                                    Service.getInstance().sendMoney(player);
                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 10);
                                    InventoryService.gI().sendItemBags(player);
                                    break;
                                }
                                break;
                            case 3:
                                if (thoivang == null || thoivang.quantity < 100) {
                                    Service.getInstance().sendThongBao(player, "Cần có đủ 100 Thỏi  Vàng để thực hiện");
                                    return;
                                }
                                if (player.inventory.gold + 50_000_000_000L > player.inventory.getGoldLimit()) {
                                    Service.getInstance().sendThongBao(player, "Vàng sau khi nhận vượt quá giới hạn");
                                } else {
                                    player.inventory.gold += 50_000_000_000L;
                                    Service.getInstance().sendThongBao(player, "|4|Bạn nhận được 50 Tỷ Vàng");
                                    Service.getInstance().sendMoney(player);
                                    InventoryService.gI().subQuantityItemsBag(player, thoivang, 100);
                                    InventoryService.gI().sendItemBags(player);
                                    break;
                                }
                        }
                        break;
                    case ConstNpc.MENU_TRIEU_HOI_BOSS:
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS_VI_THU,
                                        "|7|Muốn triệu hồi con nào!!!",
                                        "Nhất Vĩ", "Nhị Vĩ", "Tam Vĩ",
                                        "Tứ Vĩ", "Ngũ Vĩ", "Lục Vĩ",
                                        "Thất Vĩ", "Bát Vĩ", "Cửu Vĩ",
                                        "Thập Vĩ"
                                );
                                break;
                            case 1:
                                this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS_VI_THU,
                                        "|7|Muốn triệu hồi con nào!!!",
                                        "Nhất Vĩ", "Nhị Vĩ", "Tam Vĩ",
                                        "Tứ Vĩ", "Ngũ Vĩ", "Lục Vĩ",
                                        "Thất Vĩ", "Bát Vĩ", "Cửu Vĩ",
                                        "Thập Vĩ"
                                );
                                break;
                            case 2:
                                this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS_VI_THU,
                                        "|7|Muốn triệu hồi con nào!!!",
                                        "Nhất Vĩ", "Nhị Vĩ", "Tam Vĩ",
                                        "Tứ Vĩ", "Ngũ Vĩ", "Lục Vĩ",
                                        "Thất Vĩ", "Bát Vĩ", "Cửu Vĩ",
                                        "Thập Vĩ"
                                );
                                break;
                            case 3:
                                this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS_VI_THU,
                                        "|7|Muốn triệu hồi con nào!!!",
                                        "Nhất Vĩ", "Nhị Vĩ", "Tam Vĩ",
                                        "Tứ Vĩ", "Ngũ Vĩ", "Lục Vĩ",
                                        "Thất Vĩ", "Bát Vĩ", "Cửu Vĩ",
                                        "Thập Vĩ"
                                );
                                break;
                            case 4:
                                this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS_VI_THU,
                                        "|7|Muốn triệu hồi con nào!!!",
                                        "Nhất Vĩ", "Nhị Vĩ", "Tam Vĩ",
                                        "Tứ Vĩ", "Ngũ Vĩ", "Lục Vĩ",
                                        "Thất Vĩ", "Bát Vĩ", "Cửu Vĩ",
                                        "Thập Vĩ"
                                );
                                break;
                        }
                        break;

//                    case ConstNpc.MENU_TRIEU_HOI_BOSS_VI_THU:
//                        switch (select) {
//                            case 0:
//                                Boss nhatvi = BossFactory.createBoss(BossFactory.NHAT_VI);
//                                nhatvi.zone = player.zone;
//                                nhatvi.location.x = player.location.x;
//                                nhatvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Nhất Vĩ");
//                                break;
//                            case 1:
//                                Boss nhivi = BossFactory.createBoss(BossFactory.NHI_VI);
//                                nhivi.zone = player.zone;
//                                nhivi.location.x = player.location.x;
//                                nhivi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Nhị Vĩ");
//                                break;
//                            case 2:
//                                Boss tamvi = BossFactory.createBoss(BossFactory.TAM_VI);
//                                tamvi.zone = player.zone;
//                                tamvi.location.x = player.location.x;
//                                tamvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Tam Vĩ");
//                                break;
//                            case 3:
//                                Boss tuvi = BossFactory.createBoss(BossFactory.TU_VI);
//                                tuvi.zone = player.zone;
//                                tuvi.location.x = player.location.x;
//                                tuvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Tứ Vĩ");
//                                break;
//                            case 4:
//                                Boss nguvi = BossFactory.createBoss(BossFactory.NGU_VI);
//                                nguvi.zone = player.zone;
//                                nguvi.location.x = player.location.x;
//                                nguvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Ngũ Vĩ");
//                                break;
//                            case 5:
//                                Boss lucvi = BossFactory.createBoss(BossFactory.LUC_VI);
//                                lucvi.zone = player.zone;
//                                lucvi.location.x = player.location.x;
//                                lucvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Lục Vĩ");
//                                break;
//                            case 6:
//                                Boss thatvi = BossFactory.createBoss(BossFactory.THAT_VI);
//                                thatvi.zone = player.zone;
//                                thatvi.location.x = player.location.x;
//                                thatvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Thất Vĩ");
//                                break;
//                            case 7:
//                                Boss batvi = BossFactory.createBoss(BossFactory.BAT_VI);
//                                batvi.zone = player.zone;
//                                batvi.location.x = player.location.x;
//                                batvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Bát Vĩ");
//                                break;
//                            case 8:
//                                Boss cuuvi = BossFactory.createBoss(BossFactory.CUU_VI);
//                                cuuvi.zone = player.zone;
//                                cuuvi.location.x = player.location.x;
//                                cuuvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Cửu Vĩ");
//                                break;
//                            case 9:
//                                Boss thapvi = BossFactory.createBoss(BossFactory.THAP_VI);
//                                thapvi.zone = player.zone;
//                                thapvi.location.x = player.location.x;
//                                thapvi.location.y = player.location.y;
//                                Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Thập Vĩ");
//                                break;
////                        }
//                        break;
                    case ConstNpc.VAO_MAP_NGOAI_VUC:
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().goToHallowen(player);
                                break;
                            case 1:
                                ChangeMapService.gI().goToHanhTinhBangGia(player);
                                break;
                            case 2:
                                ChangeMapService.gI().goToDiaNguc(player);
                                break;
                        }
                        break;
                    case ConstNpc.CHON_HT_DE_MABU: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changePetNew4(player, 0);
                                case 1 ->
                                    PetService.gI().changePetNew4(player, 1);
                                case 2 ->
                                    PetService.gI().changePetNew4(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createPetNew4(player, 0);
                                case 1 ->
                                    PetService.gI().createPetNew4(player, 1);
                                case 2 ->
                                    PetService.gI().createPetNew4(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                  
                    case ConstNpc.CHON_HT_DE_BERUS: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changeBerusPet(player, 0);
                                case 1 ->
                                    PetService.gI().changeBerusPet(player, 1);
                                case 2 ->
                                    PetService.gI().changeBerusPet(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createBerusPet(player, 0);
                                case 1 ->
                                    PetService.gI().createBerusPet(player, 1);
                                case 2 ->
                                    PetService.gI().createBerusPet(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                    case ConstNpc.CHON_HT_DE_ITACHI: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changeItachiPet(player, 0);
                                case 1 ->
                                    PetService.gI().changeItachiPet(player, 1);
                                case 2 ->
                                    PetService.gI().changeItachiPet(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createItachiPet(player, 0);
                                case 1 ->
                                    PetService.gI().createItachiPet(player, 1);
                                case 2 ->
                                    PetService.gI().createItachiPet(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                    case ConstNpc.CHON_HT_DE_KAIDO: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changeKaidoPet(player, 0);
                                case 1 ->
                                    PetService.gI().changeKaidoPet(player, 1);
                                case 2 ->
                                    PetService.gI().changeKaidoPet(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createKaidoPet(player, 0);
                                case 1 ->
                                    PetService.gI().createKaidoPet(player, 1);
                                case 2 ->
                                    PetService.gI().createKaidoPet(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                    case ConstNpc.CHON_HT_DE_TIEN: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changeAndroidPet(player, 0);
                                case 1 ->
                                    PetService.gI().changeAndroidPet(player, 1);
                                case 2 ->
                                    PetService.gI().changeAndroidPet(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createAndroidPet(player, 0);
                                case 1 ->
                                    PetService.gI().createAndroidPet(player, 1);
                                case 2 ->
                                    PetService.gI().createAndroidPet(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                    case ConstNpc.CHON_HT_DE_SOI3DAU: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changeSoi3DauPet(player, 0);
                                case 1 ->
                                    PetService.gI().changeSoi3DauPet(player, 1);
                                case 2 ->
                                    PetService.gI().changeSoi3DauPet(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createSoi3DauPet(player, 0);
                                case 1 ->
                                    PetService.gI().createSoi3DauPet(player, 1);
                                case 2 ->
                                    PetService.gI().createSoi3DauPet(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                    case ConstNpc.CHON_HT_DE_NGO_KHONG: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changeNgoKoPet(player, 0);
                                case 1 ->
                                    PetService.gI().changeNgoKoPet(player, 1);
                                case 2 ->
                                    PetService.gI().changeNgoKoPet(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createNgoKoPet(player, 0);
                                case 1 ->
                                    PetService.gI().createNgoKoPet(player, 1);
                                case 2 ->
                                    PetService.gI().createNgoKoPet(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                    case ConstNpc.CHON_HT_DE_DAITHANH: {
                        int id = player.iDMark.getIndexMenu();
                        Item trungDt = InventoryService.gI().findItemBagByTemp(player, id);
                        if (trungDt == null || trungDt.quantity < 1) {
                            Service.getInstance().sendThongBaoOK(player, "Vật phẩm không tồn tại, hoặc có lỗi hiển thị vật phẩm\n"
                                    + "vui lòng thoát game vào lại rồi thử thao tác lại lần nữa");
                            return;
                        }
                        if (player.pet != null) {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().changePetDaiThanh(player, 0);
                                case 1 ->
                                    PetService.gI().changePetDaiThanh(player, 1);
                                case 2 ->
                                    PetService.gI().changePetDaiThanh(player, 2);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    PetService.gI().createDaiThanhPet(player, 0);
                                case 1 ->
                                    PetService.gI().createDaiThanhPet(player, 1);
                                case 2 ->
                                    PetService.gI().createDaiThanhPet(player, 2);
                            }
                        }
                        InventoryService.gI().subQuantityItemsBag(player, trungDt, 1);
                        InventoryService.gI().sendItemBags(player);
                        System.out.println("Nguoi choi :" + player.name + " vua doi de tu berus\n");
                        break;
                    }
                    case ConstNpc.INFO_ALL:
                        switch (select) {
                            case 0:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12713,
                                        "|7|THÔNG TIN NHÂN VẬT"
                                        + "\b|5|HP bản thân: " + Util.format(player.nPoint.hp) + "/" + Util.powerToString(player.nPoint.hpMax)
                                        + "\bKI bản thân: " + Util.format(player.nPoint.mp) + "/" + Util.powerToString(player.nPoint.mpMax)
                                        + "\bSức đánh: " + Util.format(player.nPoint.dame)
                                        + "\bGiáp: " + Util.format(player.nPoint.def)
                                        //                                        + "\b|4|HP Gốc: " + Util.format(player.nPoint.hpg)
                                        //                                        + "\bKI Gốc: " + Util.format(player.nPoint.mpg)
                                        //                                        + "\bSức đánh Gốc: " + Util.format(player.nPoint.dameg)
                                        //                                        + "\bGiáp Gốc: " + Util.format(player.nPoint.defg)
                                        + "\b|8|-Vàng: " + Util.format(player.inventory.gold)
                                        + "   -Ngọc: " + Util.format(player.inventory.gem)
                                        + "   -H.Ngọc: " + Util.format(player.inventory.ruby)
                                        + "\b|5|Tổng  Vàng nhặt: " + Util.format(player.vangnhat)
                                        + "\b|3|Tổng Hồng ngọc nhặt: " + Util.format(player.hngocnhat),
                                        "Thông tin\n nhân vật", "Thông tin\nđệ tử", "Thông tin\nđồ mặc");
                                break;
                            case 1:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12713,
                                        "|7|THÔNG TIN ĐỆ TỬ"
                                        + "\b\b|7|Hành tinh: " + Service.getInstance().get_HanhTinh(player.pet.gender)
                                        + "\b|5|HP ĐỆ TỬ: " + Util.format(player.pet.nPoint.hp) + "/" + Util.powerToString(player.pet.nPoint.hpMax)
                                        + "\bKI ĐỆ TỬ: " + Util.format(player.pet.nPoint.mp) + "/" + Util.powerToString(player.pet.nPoint.mpMax)
                                        + "\bSức đánh: " + Util.format(player.pet.nPoint.dame)
                                        + "\bGiáp: " + Util.format(player.pet.nPoint.def)
                                        + "\b|4|HP Gốc: " + Util.format(player.pet.nPoint.hpg)
                                        + "\bKI Gốc: " + Util.format(player.pet.nPoint.mpg)
                                        + "\bSức đánh Gốc: " + Util.format(player.pet.nPoint.dameg)
                                        + "\bGiáp Gốc: " + Util.format(player.pet.nPoint.defg),
                                        "Thông tin\n nhân vật", "Thông tin\nđệ tử", "Thông tin\nđồ mặc");
                                break;
                            case 2:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                                        "|1|Bạn muốn xem chỉ số đồ bị giới hạn hiện thị:",
                                        "Chỉ số\nô 1", "Chỉ số\nô 2", "Chỉ số\nô 3",
                                        "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6",
                                        "Chỉ số\nô 7", "Chỉ số\nô 8", "Chỉ số\nô 9",
                                        "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
                                break;
                        }
                        break;
                    case ConstNpc.CHISODO: {
                        Item it = player.inventory.itemsBody.get(select);
                        if (it.quantity < 1) {
                            NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                                    "|7|Ô này không có đồ!!!"
                                    + "\n|2|Bạn muốn xem chỉ số đồ bị giới hạn hiện thị:",
                                    "Chỉ số\nô 1", "Chỉ số\nô 2", "Chỉ số\nô 3",
                                    "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6",
                                    "Chỉ số\nô 7", "Chỉ số\nô 8", "Chỉ số\nô 9",
                                    "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
                            return;
                        }
                        NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                                "|2|Tên Vật phẩm: " + it.template.name
                                + "\n|7|Chỉ số:"
                                + "\n|6|" + it.getInfo(),
                                "Chỉ số\nô 1", "Chỉ số\nô 2", "Chỉ số\nô 3",
                                "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6",
                                "Chỉ số\nô 7", "Chỉ số\nô 8", "Chỉ số\nô 9",
                                "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
                    }
                    break;
                    case ConstNpc.ADMIN_DANH_HIEU: {
                        switch (select) {
                            case 0:
                                if (player.lastTimeTitle1 == 0) {
                                    player.lastTimeTitle1 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
                                } else {
                                    player.lastTimeTitle1 += (1000 * 60 * 60 * 24 * 7);
                                }
                                player.isTitleUse1 = true;
                                Service.getInstance().point(player);
                                Service.getInstance().sendTitle(player, 888);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 7 ngày Danh hiệu Đại Thần");
                                break;
                            case 1:
                                if (player.lastTimeTitle2 == 0) {
                                    player.lastTimeTitle2 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
                                } else {
                                    player.lastTimeTitle2 += (1000 * 60 * 60 * 24 * 7);
                                }
                                player.isTitleUse2 = true;
                                Service.getInstance().point(player);
                                Service.getInstance().sendTitle(player, 889);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 7 ngày Danh hiệu Cần Thủ");
                                break;
                            case 2:
                                if (player.lastTimeTitle3 == 0) {
                                    player.lastTimeTitle3 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
                                } else {
                                    player.lastTimeTitle3 += (1000 * 60 * 60 * 24 * 7);
                                }
                                player.isTitleUse3 = true;
                                Service.getInstance().point(player);
                                Service.getInstance().sendTitle(player, 890);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 7 ngày Danh hiệu Tuổi Thơ");
                                break;
                            case 3:
                                if (player.lastTimeTitle4 == 0) {
                                    player.lastTimeTitle4 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
                                } else {
                                    player.lastTimeTitle4 += (1000 * 60 * 60 * 24 * 7);
                                }
                                player.isTitleUse4 = true;
                                Service.getInstance().point(player);
                                Service.getInstance().sendTitle(player, 891);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 7 ngày Danh hiệu Thợ ngọc");
                                break;
                        }
                    }
                    break;
                    case ConstNpc.MO_RONG_RUONG_SUU_TAM:
                        if (select == 0) {
                            Item thoiVang = InventoryService.gI().findItemBagByTemp(player, (short) RuongSuuTam.ID_TEMP);

                            List<Item> listItem = new ArrayList<>();
                            if (player.typeMoRuong == 0) {
                                listItem = player.ruongSuuTam.RuongCaiTrang;
                            } else if (player.typeMoRuong == 1) {
                                listItem = player.ruongSuuTam.RuongPhuKien;
                            } else if (player.typeMoRuong == 2) {
                                listItem = player.ruongSuuTam.RuongPet;
                            } else if (player.typeMoRuong == 3) {
                                listItem = player.ruongSuuTam.RuongLinhThu;
                            } else if (player.typeMoRuong == 4) {
                                listItem = player.ruongSuuTam.RuongThuCuoi;
                            }
                            if (listItem == null) {
                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                return;
                            }
                            if (listItem.size() >= RuongSuuTam.MAX_SIZE) {
                                Service.getInstance().sendThongBao(player, "Rương Sưu Tầm đã đạt giới hạn tối đa");
                                return;
                            }
                            if (thoiVang != null && thoiVang.quantity >= RuongSuuTam.QUATITY) {
                                listItem.add(ItemService.gI().createItemNull());
                                InventoryService.gI().subQuantityItemsBag(player, thoiVang, RuongSuuTam.QUATITY);
                                InventoryService.gI().sendItemBags(player);
                                RuongSuuTam.gI().SendAllRuong(player);
                                Service.getInstance().sendThongBao(player, "Mở thêm 1 ô " + RuongSuuTam.gI().nameRuong(player, player.typeMoRuong) + " Thành công");
                            } else {
                                Service.getInstance().sendThongBao(player, "Không đủ 100 Thỏi vàng");
                            }
                        }
                        break;
                    case ConstNpc.NANG_CAP_KHAM_NGOC:
                        if (select == 0) {
                            int nro = player.nroKhamNgoc;
                            int max_quatity = player.slItem;
                            int idItem = player.idTempNangCap;
                            KhamNgocPlayer manager = player.khamNgoc.get(nro);
                            Item item = InventoryService.gI().findItemBagByTemp(player, (short) idItem);
                            Item it = ItemService.gI().createNewItem((short) idItem);
                            if (item == null || item.quantity < max_quatity) {
                                Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu. Còn thiếu " + (item == null ? max_quatity : (max_quatity - item.quantity)) + " " + it.template.name);
                                return;
                            }
                            InventoryService.gI().subQuantityItemsBag(player, item, max_quatity);
                            manager.levelNro++;
                            InventoryService.gI().sendItemBags(player);
                            player.nPoint.calPoint();
                            Service.getInstance().point(player);
                            KhamNgoc.gI().Send_KhamNgoc_Player(player);
                            if (manager.levelNro == 0) {
                                Service.getInstance().sendThongBao(player, "|2|Kích hoạt thành công Ngọc rồng " + (nro + 1) + " sao");
                            } else {
                                Service.getInstance().sendThongBao(player, "|2|Nâng thành công Ngọc rồng " + (nro + 1) + " sao lên Cấp " + manager.levelNro);
                            }
                        }
                        break;
                    case ConstNpc.DIEU_CHE:
                        if (select == 0) {
                            PhongThiNghiem ptn = PhongThiNghiem.PHONG_THI_NGHIEM.get(player.typeBinhDieuChe);
                            for (int i = 0; i < ptn.items.size(); i++) {
                                Item item = InventoryService.gI().findItemBagByTemp(player, (short) ptn.items.get(i).tempId);
                                InventoryService.gI().subQuantityItemsBag(player, item, ptn.items.get(i).quantity);
                            }
                            InventoryService.gI().sendItemBags(player);
                            player.phongThiNghiem.get(player.vitriBinhDieuChe).idBinh = player.typeBinhDieuChe;
                            player.phongThiNghiem.get(player.vitriBinhDieuChe).timeCheTao = System.currentTimeMillis() + (ptn.thoi_gian * 1000 * 60);
                            PhongThiNghiem.gI().Send_PhongThiNghiem_Player(player);
                        }
                        break;
                    case ConstNpc.MO_RONG_PHONG_THI_NGHIEM:
                        if (select == 0) {
                            Item thoiVang = InventoryService.gI().findItemBagByTemp(player, (short) PhongThiNghiem.ID_ITEM_MO_RONG);
                            if (player.phongThiNghiem.size() >= PhongThiNghiem.MAX_SIZE) {
                                Service.getInstance().sendThongBao(player, "Phòng Thí Nghiệm đã đạt giới hạn tối đa");
                                return;
                            }
                            if (thoiVang != null && thoiVang.quantity >= PhongThiNghiem.SO_LUONG) {
                                PhongThiNghiem_Player ptnPl = new PhongThiNghiem_Player();ptnPl.idBinh = -1;
                                ptnPl.timeCheTao = 0;
                                player.phongThiNghiem.add(ptnPl);
                                InventoryService.gI().subQuantityItemsBag(player, thoiVang, PhongThiNghiem.SO_LUONG);
                                InventoryService.gI().sendItemBags(player);
                                PhongThiNghiem.gI().Send_PhongThiNghiem_Player(player);
                                Service.getInstance().sendThongBao(player, "Mở thêm 1 ô Thành công");
                            } else {
                                Service.getInstance().sendThongBao(player, "Không đủ " + PhongThiNghiem.SO_LUONG + " Thỏi vàng");
                            }
                        }
                        break;
                    case ConstNpc.HUY_PTN:
                        if (select == 0) {
                            PhongThiNghiem ptn = PhongThiNghiem.PHONG_THI_NGHIEM.get(player.typeBinhDieuChe);
                            String text = "";
                            for (int i = 0; i < ptn.items.size(); i++) {
                                Item it = ItemService.gI().createNewItem((short) ptn.items.get(i).tempId);
                                it.quantity = ptn.items.get(i).quantity;
                                text += "|5|-" + it.quantity + " " + it.template.name + (i == (ptn.items.size() - 1) ? "" : "\n");
                                InventoryService.gI().addItemBag(player, it,0);
                            }
                            InventoryService.gI().sendItemBags(player);
                            player.phongThiNghiem.get(player.vitriBinhDieuChe).idBinh = -1;
                            player.phongThiNghiem.get(player.vitriBinhDieuChe).timeCheTao = 0;
                            PhongThiNghiem.gI().Send_PhongThiNghiem_Player(player);
                            Service.getInstance().sendThongBao(player, "Hủy Thành công " + ptn.name_binh
                                    + "\n|3|Nhận lại vật phẩm:"
                                    + "\n" + text);
                        }
                        break;
                    case ConstNpc.TANG_TOC:
                        if (select == 0) {
                            Item item = InventoryService.gI().findItemBagByTemp(player, (short) PhongThiNghiem.ID_ITEM_TANG_TOC);
                            if (item == null || item.quantity < PhongThiNghiem.SO_LUONG_TANG_TOC) {
                                Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                return;
                            }
                            InventoryService.gI().subQuantityItemsBag(player, item, PhongThiNghiem.SO_LUONG_TANG_TOC);
                            InventoryService.gI().sendItemBags(player);
                            PhongThiNghiem ptn = PhongThiNghiem.PHONG_THI_NGHIEM.get(player.typeBinhDieuChe);
                            player.phongThiNghiem.get(player.vitriBinhDieuChe).timeCheTao -= PhongThiNghiem.TIME_TANG_TOC;
                            PhongThiNghiem.gI().Send_PhongThiNghiem_Player(player);
                            Service.getInstance().sendThongBao(player, "Tăng tốc Thành công " + ptn.name_binh + ". Giảm " + Util.msToTime(PhongThiNghiem.TIME_TANG_TOC) + " thởi gian điều chế");
                        }
                        break;
                    case ConstNpc.MENU_MAY_DO_BOSS:
                        Item xuBac = InventoryService.gI().findItemBagByTemp(player, 1567);
                        switch (select){
                            case 0:{
                                UseItem.gI().maydoboss(player);
                                break;
                            }
                            case 1: {
                                if (xuBac == null || xuBac.quantity < 20) {
                                    Service.getInstance().sendThongBaoOK(player, "Cần 20 xu bạc để dịch chuyển tới boss vip");
                                    return;
                                }
                                UseItem.gI().maydobossVIP(player);
                                InventoryService.gI().subQuantityItemsBag(player, xuBac, 20);
                                InventoryService.gI().sendItemBags(player);
                                break;
                            }

                        }
                        break;
                    case ConstNpc.MENU_LOAD_DATA: {
                        switch (select) {
                            case 0:
                                BangTin.BANGTIN_MANAGER.clear();
                                BangTin.gI().load_BangTin();
                                Service.getInstance().sendThongBao(player, "Load Bảng Tin thành công");
                                break;
                            case 1:
                                PhucLoi.PHUCLOI_MANAGER.clear();
                                PhucLoi.PHUCLOI_TEMPLATES.clear();
                                PhucLoi.gI().load_PhucLoi();
                                PhucLoi.gI().load_PhucLoiTab();
                                Service.getInstance().sendThongBao(player, "Load Phúc Lợi thành công");
                                break;
                            case 2:
                                TamBao.MOC_TAMBAO.clear();
                                TamBao.gI().load_mocTamBao();
                                Service.getInstance().sendThongBao(player, "Load Vòng Quay thành công");
                                break;
                            case 3:
                                KhamNgoc.KHAM_NGOC.clear();
                                KhamNgoc.gI().loadKhamNgoc();
                                Service.getInstance().sendThongBao(player, "Load Khảm Ngọc thành công");
                                break;
                            case 4:
                                RuongSuuTam.listRuong.clear();
                                RuongSuuTam.listCaiTrang.clear();
                                RuongSuuTam.listPhuKien.clear();
                                RuongSuuTam.listPet.clear();
                                RuongSuuTam.listLinhThu.clear();
                                RuongSuuTam.listThuCuoi.clear();
                                RuongSuuTam.gI().loadRuongSuuTam();
                                Service.getInstance().sendThongBao(player, "Load Rương Sưu Tầm thành công");
                                break;
                        }
                    }
                    case ConstNpc.MENU_DANHHIEU: {
                        switch (select) {
                            case 0:
                                if (player.lastTimeTitle1 > 0) {
                                    Service.getInstance().removeTitle(player);
                                    player.isTitleUse1 = !player.isTitleUse1;
                                    Service.getInstance().point(player);
                                    Service.getInstance().sendThongBao(player, "Đã " + (player.isTitleUse1 == true ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    Service.getInstance().sendTitle(player, 892);
                                    Service.getInstance().sendTitle(player, 891);
                                    Service.getInstance().sendTitle(player, 890);
                                    Service.getInstance().sendTitle(player, 889);
                                    Service.getInstance().sendTitle(player, 888);
                                    Service.getInstance().removeTitle(player);
                                    break;
                                }
                                break;
                            case 1:
                                if (player.lastTimeTitle2 > 0) {
                                    Service.getInstance().removeTitle(player);
                                    player.isTitleUse2 = !player.isTitleUse2;
                                    Service.getInstance().point(player);
                                    Service.getInstance().sendThongBao(player, "Đã " + (player.isTitleUse2 == true ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    Service.getInstance().sendTitle(player, 892);
                                    Service.getInstance().sendTitle(player, 891);
                                    Service.getInstance().sendTitle(player, 890);
                                    Service.getInstance().sendTitle(player, 889);
                                    Service.getInstance().sendTitle(player, 888);
                                    Service.getInstance().removeTitle(player);
                                    break;
                                }
                                break;
                            case 2:
                                if (player.lastTimeTitle3 > 0) {
                                    Service.getInstance().removeTitle(player);
                                    player.isTitleUse3 = !player.isTitleUse3;
                                    Service.getInstance().point(player);
                                    Service.getInstance().sendThongBao(player, "Đã " + (player.isTitleUse3 == true ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    Service.getInstance().sendTitle(player, 892);
                                    Service.getInstance().sendTitle(player, 891);
                                    Service.getInstance().sendTitle(player, 890);
                                    Service.getInstance().sendTitle(player, 889);
                                    Service.getInstance().sendTitle(player, 888);
                                    Service.getInstance().removeTitle(player);
                                    break;
                                }
                                break;
                            case 3:
                                if (player.lastTimeTitle4 > 0) {
                                    Service.getInstance().removeTitle(player);
                                    player.isTitleUse4 = !player.isTitleUse4;
                                    Service.getInstance().point(player);
                                    Service.getInstance().sendThongBao(player, "Đã " + (player.isTitleUse4 == true ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    Service.getInstance().sendTitle(player, 892);
                                    Service.getInstance().sendTitle(player, 891);
                                    Service.getInstance().sendTitle(player, 890);
                                    Service.getInstance().sendTitle(player, 889);
                                    Service.getInstance().sendTitle(player, 888);
                                    Service.getInstance().removeTitle(player);
                                    break;
                                }
                                break;
                            case 4:
                                if (player.lastTimeTitle5 > 0) {
                                    Service.getInstance().removeTitle(player);
                                    player.isTitleUse5 = !player.isTitleUse5;
                                    Service.getInstance().point(player);
                                    Service.getInstance().sendThongBao(player, "Đã " + (player.isTitleUse5 == true ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    Service.getInstance().sendTitle(player, 892);
                                    Service.getInstance().sendTitle(player, 891);
                                    Service.getInstance().sendTitle(player, 890);
                                    Service.getInstance().sendTitle(player, 889);
                                    Service.getInstance().sendTitle(player, 888);
                                    Service.getInstance().removeTitle(player);
                                    break;
                                }
                                break;
                        }
                    }
                    break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x,
                                                p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x,
                                                player.location.y);
                                    }
                                    break;
                                case 2:
                                    if (p != null) {
                                        Input.gI().createFormChangeName(player, p);
                                    }
                                    break;
//                                case 3:
//                                    if (p != null) {
//                                        String[] selects = new String[]{"Đồng ý", "Hủy"};
//                                        NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
//                                                "Bạn có chắc chắn muốn ban " + p.name, selects, p);
//                                    }
//                                    break;
                            }
                        }
                        break;
                }
            }
        };
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

    public static String getMenuSuKien(int id) {
        switch (id) {
            case ConstEvent.KHONG_CO_SU_KIEN:
                return "Chưa có\n Sự Kiện";
            case ConstEvent.SU_KIEN_HALLOWEEN:
                return "Sự Kiện\nHaloween";
            case ConstEvent.SU_KIEN_20_11:
                return "Sự Kiện\n 20/11";
            case ConstEvent.SU_KIEN_NOEL:
                return "Sự Kiện\n Giáng Sinh";
            case ConstEvent.SU_KIEN_TET:
                return "Sự Kiện\n Tết Nguyên\nĐán 2024";
            case ConstEvent.SU_KIEN_8_3:
                return "Sự Kiện\n 8/3";
        }
        return "Chưa có\n Sự Kiện";
    }

    public static String getMenuLamBanh(Player player, int type) {
        switch (type) {
            case 0:// bánh tét
                if (player.event.isCookingTetCake()) {
                    int timeCookTetCake = player.event.getTimeCookTetCake();
                    if (timeCookTetCake == 0) {
                        return "Lấy bánh";
                    } else if (timeCookTetCake > 0) {
                        return "Đang nấu\nBánh Tét\nCòn " + TimeUtil.secToTime(timeCookTetCake);
                    }
                } else {
                    return "Nấu\nBánh Tét";
                }
                break;
            case 1:// bánh chưng
                if (player.event.isCookingChungCake()) {
                    int timeCookChungCake = player.event.getTimeCookChungCake();
                    if (timeCookChungCake == 0) {
                        return "Lấy bánh";
                    } else if (timeCookChungCake > 0) {
                        return "Đang nấu\nBánh Chưng\nCòn " + TimeUtil.secToTime(timeCookChungCake);
                    }
                } else {
                    return "Nấu\nBánh Chưng";
                }
                break;
        }
        return "";
    }
    

    public static long safeMultiply(long base, long multiplier) {
        BigInteger result = BigInteger.valueOf(base).multiply(BigInteger.valueOf(multiplier));
        return result.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();
    }
    public static long calPercent(long param, long percent) {
        return (param / 100) * percent;
    }

}
