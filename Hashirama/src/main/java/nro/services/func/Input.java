package nro.services.func;

import nro.consts.ConstNpc;
import nro.jdbc.daos.PlayerDAO;
import nro.models.item.Item;
import nro.models.map.Zone;
import nro.models.npc.Npc;
import nro.models.npc.NpcManager;
import nro.models.player.Player;
import nro.server.Client;
import nro.server.io.Message;
import nro.services.*;
import nro.services.*;

import java.util.HashMap;
import java.util.Map;
import nro.jdbc.DBService;
import nro.models.item.ItemOption;
import nro.server.Manager;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class Input {

    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 5066;
    public static final int CHOOSE_LEVEL_CDRD = 7700;
    public static final int TANG_NGOC_HONG = 505;
    public static final int ADD_ITEM = 506;
    public static final int QUY_DOI_COIN = 508;
    public static final int QUY_DOI_COIN_1 = 509;
    public static final int BUFF_ITEM_OPTION = 510;
    public static final int XIU_taixiu = 514;
    public static final int TAI_taixiu = 515;
    public static final int BUFF_DANH_HIEU = 516;
    public static final int CHANGE_NAME_BY_ITEM = 517;
    public static final int CHON_SO_MAY_MAN = 518;
    public static final int GIFT_MEMBER = 519;
    public static final int QUY_DOI_COIN_2 = 5220;
    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;

    public static final int XIU_CLIENT = 5000;
    public static final int TAI_CLIENT = 5001;
    public static final int CHAT_TAI_XIU = 5002;

    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            Player pl = null;
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case CHANGE_PASSWORD:
                    Service.getInstance().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    GiftService.gI().use(player, text[0]);
                    break;
                case GIFT_MEMBER:
                    GiftcodePlayer.gI().use(player, text[0]);
                    break;
                case FIND_PLAYER:
                    pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên"},//, "Ban"
                                pl);
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME:
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (PlayerDAO.isExistName(text[0])) {
                            Service.getInstance().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            PlayerDAO.saveName(plChanged);
                            Service.getInstance().player(plChanged);
                            Service.getInstance().Send_Caitrang(plChanged);
                            Service.getInstance().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.getInstance().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.getInstance().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                    break;
                case CHANGE_NAME_BY_ITEM: {
                    if (PlayerDAO.isExistName(text[0])) {
                        Service.getInstance().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        createFormChangeNameByItem(player);
                    } else {
                        Item theDoiTen = InventoryService.gI().findItem(player.inventory.itemsBag, 2006);
                        if (theDoiTen == null) {
                            Service.getInstance().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                        } else {
                            InventoryService.gI().subQuantityItemsBag(player, theDoiTen, 1);
                            player.name = text[0];
                            PlayerDAO.saveName(player);
                            Service.getInstance().player(player);
                            Service.getInstance().Send_Caitrang(player);
                            Service.getInstance().sendFlagBag(player);
                            Zone zone = player.zone;
                            ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                            Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                        }
                    }
                }
                break;
                case CHOOSE_LEVEL_BDKB: {
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc chắn muốn tới bản đồ kho báu cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                break;
                case CHOOSE_LEVEL_CDRD: {
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.THAN_VU_TRU, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_CDRD,
                                    "Con có chắc chắn muốn đến con đường rắn độc cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                break;
                case TANG_NGOC_HONG:
                    pl = Client.gI().getPlayer(text[0]);
                    int numruby = Integer.parseInt((text[1]));
                    if (pl != null) {
                        if (numruby > 0 && player.inventory.ruby >= numruby) {
                            Item item = InventoryService.gI().findVeTangNgoc(player);
                            player.inventory.subRuby(numruby);
                            PlayerService.gI().sendInfoHpMpMoney(player);
                            pl.inventory.ruby += numruby;
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.getInstance().sendThongBao(player, "Tặng Hồng ngọc thành công");
                            Service.getInstance().sendThongBao(pl, "Bạn được " + player.name + " tặng " + numruby + " Hồng ngọc");
                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                            InventoryService.gI().sendItemBags(player);
                        } else {
                            Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc để tặng");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case ADD_ITEM:
                    short id = Short.parseShort((text[0]));
                    int quantity = Integer.parseInt(text[1]);
                    Item item = ItemService.gI().createNewItem(id);
                    if (item.template.type < 7) {
                        for (int i = 0; i < quantity; i++) {
                            item = ItemService.gI().createNewItem(id);
                            RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
                            InventoryService.gI().addItemBag(player, item, 0);
                        }
                    } else {
                        item.quantity = quantity;
                        InventoryService.gI().addItemBag(player, item, 0);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + item.template.name + " Số lượng: " + quantity);
                    break;
                case BUFF_ITEM_OPTION:
                    if (player.isAdmin()) {
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        int idItemBuff = Integer.parseInt(text[1]);
                        String idOptionBuff = text[2].trim();

                        int slItemBuff = Integer.parseInt(text[3]);

                        try {
                            if (pBuffItem != null) {
                                String txtBuff = player.name + " Buff cho : " + pBuffItem.name + "\b";

                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff, slItemBuff);
                                if (!idOptionBuff.isEmpty()) {
                                    String arr[] = idOptionBuff.split(";");
                                    for (int i = 0; i < arr.length; i++) {
                                        String arr2[] = arr[i].split("-");
                                        int idoption = Integer.parseInt(arr2[0].trim());
                                        int param = Integer.parseInt(arr2[1].trim());
                                        itemBuffTemplate.itemOptions.add(new ItemOption(idoption, param));
                                    }

                                }
                                for (ItemOption io : itemBuffTemplate.itemOptions) {
                                    txtBuff += io.getOptionString() + "\n";
                                }
                                txtBuff += "Số lượng: " + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryService.gI().addItemBag(pBuffItem, itemBuffTemplate, 99);
                                InventoryService.gI().sendItemBags(pBuffItem);
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                                if (player.id != pBuffItem.id) {
                                    NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Player không online");
                            }
                        } catch (Exception e) {
                            Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra vui lòng thử lại");
                        }

                    }
                    break;
                case BUFF_DANH_HIEU:
                    if (player.isAdmin()) {
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        int idDhieu = Integer.parseInt(text[1]);
                        int SoNgay = Integer.parseInt(text[2]);
                        try {
                            if (pBuffItem != null) {
                                if ((idDhieu >= 1 && idDhieu <= 3) && SoNgay > 0) {
                                    String txtBuff = player.name + " vừa Tặng Danh hiệu " + (idDhieu == 1 ? "Đại Thần " : idDhieu == 2
                                            ? "Cần Thủ " : idDhieu == 3 ? "Tuổi Thơ " : "Thợ Ngọc")
                                            + SoNgay + " Ngày cho người chơi : " + pBuffItem.name;
                                    switch (idDhieu) {
                                        case 1:
                                            if (pBuffItem.lastTimeTitle1 == 0) {
                                                pBuffItem.lastTimeTitle1 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * SoNgay);
                                            } else {
                                                pBuffItem.lastTimeTitle1 += (1000 * 60 * 60 * 24 * SoNgay);
                                            }
                                            pBuffItem.isTitleUse1 = true;
                                            Service.getInstance().point(pBuffItem);
                                            Service.getInstance().sendTitle(pBuffItem, 888);
                                            break;
                                        case 2:
                                            if (pBuffItem.lastTimeTitle2 == 0) {
                                                pBuffItem.lastTimeTitle2 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * SoNgay);
                                            } else {
                                                pBuffItem.lastTimeTitle2 += (1000 * 60 * 60 * 24 * SoNgay);
                                            }
                                            pBuffItem.isTitleUse2 = true;
                                            Service.getInstance().point(pBuffItem);
                                            Service.getInstance().sendTitle(pBuffItem, 889);
                                            break;
                                        case 3:
                                            if (pBuffItem.lastTimeTitle3 == 0) {
                                                pBuffItem.lastTimeTitle3 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * SoNgay);
                                            } else {
                                                pBuffItem.lastTimeTitle3 += (1000 * 60 * 60 * 24 * SoNgay);
                                            }
                                            pBuffItem.isTitleUse3 = true;
                                            Service.getInstance().point(pBuffItem);
                                            Service.getInstance().sendTitle(pBuffItem, 890);
                                            break;
                                        case 4:
                                            if (pBuffItem.lastTimeTitle4 == 0) {
                                                pBuffItem.lastTimeTitle4 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * SoNgay);
                                            } else {
                                                pBuffItem.lastTimeTitle4 += (1000 * 60 * 60 * 24 * SoNgay);
                                            }
                                            pBuffItem.isTitleUse4 = true;
                                            Service.getInstance().point(pBuffItem);
                                            Service.getInstance().sendTitle(pBuffItem, 891);
                                            break;
                                    }
                                    NpcService.gI().createTutorial(player, 24, txtBuff);
                                    if (player.id != pBuffItem.id) {
                                        NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Không có Danh hiệu này!!");
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Player không online");
                            }
                        } catch (Exception e) {
                            Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra vui lòng thử lại");
                        }

                    }
                    break;
                case TAI_taixiu:
                    int sotvxiu1 = Integer.valueOf(text[0]);
                    try {
                        if (sotvxiu1 >= 1000 && sotvxiu1 <= 100000) {
                            if (player.inventory.ruby >= sotvxiu1) {
                                player.inventory.ruby -= sotvxiu1;
                                player.goldTai += sotvxiu1;
                                TaiXiu.gI().goldTai += sotvxiu1;
                                Service.getInstance().sendThongBao(player, "Bạn đã đặt " + Util.format(sotvxiu1) + " Hồng ngọc vào TÀI");
                                TaiXiu.gI().addPlayerTai(player);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ Hồng ngọc để chơi.");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Cược ít nhất 10.000 Hồng ngọc.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.getInstance().sendThongBao(player, "Lỗi.");
                        System.out.println("nnnnn2  ");
                    }
                    break;

                case XIU_taixiu:
                    int sotvxiu2 = Integer.valueOf(text[0]);
                    try {
                        if (sotvxiu2 >= 1000 && sotvxiu2 <= 100000) {
                            if (player.inventory.ruby >= sotvxiu2) {
                                player.inventory.ruby -= sotvxiu2;
                                player.goldXiu += sotvxiu2;
                                TaiXiu.gI().goldXiu += sotvxiu2;
                                Service.getInstance().sendThongBao(player, "Bạn đã đặt " + Util.format(sotvxiu2) + " Hồng ngọc vào XỈU");
                                TaiXiu.gI().addPlayerXiu(player);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ Hồng ngọc để chơi.");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Cược ít nhất 20.000 - 100.000 Hồng ngọc ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.getInstance().sendThongBao(player, "Lỗi.");
                        System.out.println("nnnnn2  ");
                    }
                    break;
                case CHON_SO_MAY_MAN:
                    int sochon = Integer.parseInt(text[0]);
                    try {
                        if (sochon >= 0 && sochon <= 99) {
                            if (player.inventory.ruby >= 1000) {
                                player.inventory.ruby -= 1000;
                                player.soMayMan.add(sochon);
                                SoMayMan.gI().addPlayerSMM(player);
                                Service.getInstance().youNumber(player, sochon);
                                Service.getInstance().sendThongBao(player, "Bạn đã chọn số " + sochon + " thành công");
                                Service.getInstance().sendMoney(player);
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ Hồng ngọc để chơi.");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Chọn 1 số từ 0 đến 99");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.getInstance().sendThongBao(player, "Lỗi.");
                    }
                    break;
                case QUY_DOI_COIN:
                    int goldTrade = Integer.parseInt(text[0]);
                    int kmnap;
                    if (Manager.KHUYEN_MAI_NAP <= 0) {
                        kmnap = 1;
                    } else {
                        kmnap = Manager.KHUYEN_MAI_NAP;
                    }
                    if (goldTrade % 1000 == 0) {
                        if (goldTrade <= 0 || goldTrade >= 1000001) {
                            Service.getInstance().sendThongBao(player, "|7|Quá giới hạn mỗi lần tối đa 1.000.000");
                        } else if (player.getSession().vnd >= goldTrade) {
                            PlayerDAO.subVnd(player, goldTrade);
                            player.tongnap += goldTrade;
                            Item thoiVang = ItemService.gI().createNewItem((short) 1567, (kmnap * (goldTrade / 2)));
                            InventoryService.gI().addItemBag(player, thoiVang, -333);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format((kmnap * (goldTrade / 2)))
                                    + " " + thoiVang.template.name);
                        } else {
                            Service.getInstance().sendThongBao(player, "|7|Số Coin của bạn là " + player.getSession().vnd + " không đủ để quy "
                                    + " đổi " + (goldTrade / 2) + " Xu Bạc" + " " + "bạn cần thêm " + (player.getSession().vnd - goldTrade));
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "|7|Số Coin nhập phải là bội số của 1000");
                    }
                    break;
                case QUY_DOI_COIN_1:
                    int goldTrade1 = Integer.parseInt(text[0]);
                    int kmnap1;
                    if (Manager.KHUYEN_MAI_NAP <= 0) {
                        kmnap1 = 1;
                    } else {
                        kmnap1 = Manager.KHUYEN_MAI_NAP;
                    }
                    if (goldTrade1 % 1000 == 0) {
                        if (goldTrade1 <= 0 || goldTrade1 >= 1000001) {
                            Service.getInstance().sendThongBao(player, "|7|Quá giới hạn mỗi lần tối đa 1.000.000");
                        } else if (player.getSession().vnd >= goldTrade1) {
                            PlayerDAO.subVnd(player, goldTrade1);
                            player.tongnap += goldTrade1;
                            Item thoiVang = ItemService.gI().createNewItem((short) 1568, (kmnap1 * (goldTrade1 / 10)));
                            InventoryService.gI().addItemBag(player, thoiVang, -333);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format((kmnap1 * (goldTrade1 / 10)))
                                    + " " + thoiVang.template.name);
                        } else {
                            Service.getInstance().sendThongBao(player, "|7|Số Coin của bạn là " + player.getSession().vnd + " không đủ để quy "
                                    + " đổi " + (goldTrade1 / 10) + " Xu vàng" + " " + "bạn cần thêm " + (player.getSession().vnd - goldTrade1));
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "|7|Số Coin nhập phải là bội số của 1000");
                    }
                    break;
                case QUY_DOI_COIN_2:
                    int goldTrade2 = Integer.parseInt(text[0]);
                    int kmnap2;
                    if (Manager.KHUYEN_MAI_NAP <= 0) {
                        kmnap2 = 1;
                    } else {
                        kmnap2 = Manager.KHUYEN_MAI_NAP;
                    }
                    if (goldTrade2 % 1000 == 0) {
                        if (goldTrade2 <= 0 || goldTrade2 >= 1000001) {
                            Service.getInstance().sendThongBao(player, "|7|Quá giới hạn mỗi lần tối đa 1.000.000");
                        } else if (player.getSession().vnd >= goldTrade2) {
                            PlayerDAO.subVnd(player, goldTrade2);
                            player.tongnap += goldTrade2;
                            Item thoiVang = ItemService.gI().createNewItem((short) 457, (kmnap2 * (goldTrade2)));
                            InventoryService.gI().addItemBag(player, thoiVang, -333);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format((kmnap2 * (goldTrade2)))
                                    + " " + thoiVang.template.name);
                        } else {
                            Service.getInstance().sendThongBao(player, "|7|Số Coin của bạn là " + player.getSession().vnd + " không đủ để quy "
                                    + " đổi " + (goldTrade2) + " thỏi vàng" + " " + "bạn cần thêm " + (player.getSession().vnd - goldTrade2));
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "|7|Số Coin nhập phải là bội số của 1000");
                    }
                    break;
                case TAI_CLIENT: {
                    int sotien_tai = Integer.parseInt(text[0]);
                    try {
                        if (sotien_tai > 2_000_000) {
                            Service.getInstance().sendThongBao(player, "Tối đa đạt cược là 2 Triệu Xu");
                            return;
                        }
                        if (GameDuDoan.gI().nanKG) {
                            Service.getInstance().sendThongBao(player, "Ngoài thời gian đặt cược");
                            return;
                        }
                        Item itemCuoc = InventoryService.gI().findItemBagByTemp(player, GameDuDoan.ID_ITEM_CUOC);
                        if (itemCuoc != null && itemCuoc.quantity >= sotien_tai) {
                            player.goldTai += sotien_tai;
                            GameDuDoan.gI().goldTai += sotien_tai;
                            Service.getInstance().sendThongBao(player, "Bạn đã đặt " + Util.format(sotien_tai) + " " + GameDuDoan.NAME_ITEM_CUOC + " vào " + GameDuDoan.LON);
                            GameDuDoan.gI().addPlayerTai(player);
                            InventoryService.gI().subQuantityItemsBag(player, itemCuoc, sotien_tai);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendMoney(player);
                            GameDuDoan.gI().Send_TaiXiu(player);
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn không đủ " + GameDuDoan.NAME_ITEM_CUOC + " để chơi.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.getInstance().sendThongBao(player, "Lỗi.");
                    }
                    break;
                }
                case XIU_CLIENT: {
                    int sovang_xiu = Integer.parseInt(text[0]);
                    try {
                        if (sovang_xiu > 2_000_000) {
                            Service.getInstance().sendThongBao(player, "Tối đa đạt cược là 2 Triệu Xu");
                            return;
                        }
                        if (GameDuDoan.gI().nanKG) {
                            Service.getInstance().sendThongBao(player, "Ngoài thời gian đặt cược");
                            return;
                        }
                        Item itemCuoc = InventoryService.gI().findItemBagByTemp(player, GameDuDoan.ID_ITEM_CUOC);
                        if (itemCuoc != null && itemCuoc.quantity >= sovang_xiu) {
                            player.goldXiu += sovang_xiu;
                            GameDuDoan.gI().goldXiu += sovang_xiu;
                            Service.getInstance().sendThongBao(player, "Bạn đã đặt " + Util.format(sovang_xiu) + " " + GameDuDoan.NAME_ITEM_CUOC + " vào " + GameDuDoan.NHO);
                            GameDuDoan.gI().addPlayerXiu(player);
                            InventoryService.gI().subQuantityItemsBag(player, itemCuoc, sovang_xiu);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendMoney(player);
                            GameDuDoan.gI().Send_TaiXiu(player);
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn không đủ " + GameDuDoan.NAME_ITEM_CUOC + " để chơi.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.getInstance().sendThongBao(player, "Lỗi.");
                    }
                    break;
                }
                case CHAT_TAI_XIU: {
                    String noidung = text[0];
                    try {
                        if (noidung.length() > 30) {
                            Service.getInstance().sendThongBao(player, "Tối đa 30 ký tự");
                            return;
                        }
                        if (noidung.length() > 0) {
                            GameDuDoan.gI().noiDungChat.add("[" + player.name + "] " + noidung);
                            GameDuDoan.gI().Send_TaiXiu(player);
                        }
                    } catch (Exception e) {
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi mật khẩu", new SubInput("Mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Mã quà tặng", new SubInput("Nhập mã quà tặng", ANY));
    }
    
    public void createGiftMember(Player pl) {
        createForm(pl, GIFT_MEMBER, "Mã quà tặng", new SubInput("Nhập mã quà tặng", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormChooseLevelCDRD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_CDRD, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormTangRuby(Player pl) {
        createForm(pl, TANG_NGOC_HONG, "Tặng ngọc", new SubInput("Tên nhân vật", ANY),
                new SubInput("Số Hồng Ngọc Muốn Tặng", NUMERIC));
    }

    public void createFormAddItem(Player pl) {
        createForm(pl, ADD_ITEM, "Add Item", new SubInput("ID VẬT PHẨM", NUMERIC),
                new SubInput("SỐ LƯỢNG", NUMERIC));
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormDoiXuBac(Player pl) {
        createForm(pl, QUY_DOI_COIN, "ĐỔI XU BẠC", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormDoiXuVang(Player pl) {
        createForm(pl, QUY_DOI_COIN_1, "ĐỔI XU VÀNG", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormDoiThoiVang(Player pl) {
        createForm(pl, QUY_DOI_COIN_2, "ĐỔI THỎI VÀNG", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormBuffItemVip(Player pl) {
        createForm(pl, BUFF_ITEM_OPTION, "BUFF VIP", new SubInput("Tên người chơi", ANY), new SubInput("Id Item", ANY), new SubInput("Chuỗi option vd : 50-20;30-1", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormBuffDanhHieu(Player pl) {
        createForm(pl, BUFF_DANH_HIEU, "Tặng Danh Hiệu", new SubInput("Tên người chơi", ANY),
                 new SubInput("Danh hiệu: 1.Đại thần 2.Cần thủ 3.Tuổi thơ 4.Thợ ngọc", ANY), new SubInput("Số Ngày", ANY));
    }

    public void TAI_taixiu(Player pl) {
        createForm(pl, TAI_taixiu, "Chọn số hồng ngọc đặt Tài", new SubInput("Số Hồng ngọc cược", ANY));//????
    }

    public void XIU_taixiu(Player pl) {
        createForm(pl, XIU_taixiu, "Chọn số hồng ngọc đặt Xỉu", new SubInput("Số Hồng ngọc cược", ANY));//????
    }

    public void ChonSo(Player pl) {
        createForm(pl, CHON_SO_MAY_MAN, "Hãy chọn 1 số từ: 0 đến 99 giá 1.000 Hồng ngọc", new SubInput("Số bạn chọn", ANY));//????
    }

    public void input_Tai(Player pl) {
        createForm(pl, TAI_CLIENT, "Chọn số " + GameDuDoan.NAME_ITEM_CUOC + " đặt " + GameDuDoan.LON, new SubInput("Số " + GameDuDoan.NAME_ITEM_CUOC + " cược", ANY));//????
    }

    public void input_Xiu(Player pl) {
        createForm(pl, XIU_CLIENT, "Chọn số " + GameDuDoan.NAME_ITEM_CUOC + " đặt "  + GameDuDoan.NHO, new SubInput("Số " + GameDuDoan.NAME_ITEM_CUOC + " cược", ANY));//????
    }

    public void chat_TaiXiu(Player pl) {
        createForm(pl, CHAT_TAI_XIU, "Nhập nội dung", new SubInput("Nội dung", ANY));
    }
    public class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

}
