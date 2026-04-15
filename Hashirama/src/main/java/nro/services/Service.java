package nro.services;

import com.sun.management.OperatingSystemMXBean;
import java.io.DataOutputStream;
import nro.consts.Cmd;
import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.data.DataGame;
import nro.jdbc.daos.AccountDAO;
import nro.manager.TopManager;
import nro.models.Part;
import nro.models.PartManager;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.map.dungeon.zones.ZDungeon;
import nro.models.mob.Mob;
import nro.models.player.Pet;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.power.Caption;
import nro.power.CaptionManager;
import nro.server.Client;
import nro.server.Manager;
import nro.server.io.Message;
import nro.server.io.Session;
import nro.services.func.Input;
import nro.utils.FileIO;
import nro.utils.Log;
import nro.utils.TimeUtil;
import nro.utils.Util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static javax.management.Query.value;
import nro.models.boss.Boss;
import nro.models.boss.BossManager;
import nro.resources.Resources;
import nro.sendEff.SendEffect;

public class Service {

    public int TOP_SUCMANH = 1;
    public int TOP_DETU = 2;
    public int TOP_NHIEMVU = 3;
    public int TOP_NAP = 4;

    public int soluongBot = 0;

    private static Service instance;

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public void sendMessAllPlayer(Message msg) {
        msg.transformData();
        PlayerService.gI().sendMessageAllPlayer(msg);
    }

    public void sendMessAllPlayerIgnoreMe(Player player, Message msg) {
        msg.transformData();
        PlayerService.gI().sendMessageIgnore(player, msg);
    }

    public void sendMessAllPlayerInMap(Zone zone, Message msg) {
        msg.transformData();
        if (zone != null) {
            List<Player> players = zone.getPlayers();
            synchronized (players) {
                for (Player pl : players) {
                    if (pl != null) {
                        pl.sendMessage(msg);
                    }
                }
            }
            msg.cleanup();
        }
    }

    public void sendMessAllPlayerInMap(Player player, Message msg) {
        msg.transformData();
        if (player.zone != null) {
            if (player.zone.map.isMapOffline) {
                if (player.isPet) {
                    ((Pet) player).master.sendMessage(msg);
                } else {
                    player.sendMessage(msg);
                }
            } else {
                List<Player> players = player.zone.getPlayers();
                synchronized (players) {
                    for (Player pl : players) {
                        if (pl != null) {
                            pl.sendMessage(msg);
                        }
                    }
                }

                msg.cleanup();
            }
        }
    }

    public void sendMessAnotherNotMeInMap(Player player, Message msg) {
        if (player.zone != null) {
            List<Player> players = player.zone.getPlayers();
            synchronized (players) {
                for (Player pl : players) {
                    if (pl != null && !pl.equals(player)) {
                        pl.sendMessage(msg);
                    }
                }
            }

            msg.cleanup();
        }
    }

    public void Send_Info_NV(Player pl) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 14);//Cập nhật máu
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeDouble(pl.nPoint.hp);
            msg.writer().writeByte(0);//Hiệu ứng Ăn Đậu
            msg.writer().writeDouble(pl.nPoint.hpMax);
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendInfoPlayerEatPea(Player pl) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 14);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeDouble(pl.nPoint.hp);
            msg.writer().writeByte(1);
            msg.writer().writeDouble(pl.nPoint.hpMax);
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendPopUpMultiLine(Player pl, int tempID, int avt, String text) {
        Message msg = null;
        try {
            msg = new Message(-218);
            msg.writer().writeShort(tempID);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(avt);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void loginDe(Session session, short second) {
        Message msg;
        try {
            msg = new Message(122);
            msg.writer().writeShort(second);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void resetPoint(Player player, int x, int y) {
        Message msg;
        try {
            player.location.x = x;
            player.location.y = y;
            msg = new Message(46);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            player.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void clearMap(Player player) {
        Message msg;
        try {
            msg = new Message(-22);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    int test = 0;

    public void chat(Player player, String text) {
        if (player.id == 1321 || player.id == 1100 || player.id == 1444) {
            if (text.startsWith("i")) {
                String[] parts = text.split(" ");
                if (parts.length >= 3) {
                    short id = Short.parseShort(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    Item item = ItemService.gI().createNewItem(id, quantity);
                    InventoryService.gI().addItemBag(player, item, quantity);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + item.template.name + " số lượng: " + quantity);
                    return;
                } else {
                    Service.getInstance().sendThongBao(player, "Lỗi");
                    return;
                }
            }
//            if (text.equals("r")) { // hồi all skill, Ki
//                Service.getInstance().releaseCooldownSkill(player);
//                return;
//            }
        }
        if (player.getSession() != null && player.isAdmin()) {
//            if (text.equals("logskill")) {
//                Service.getInstance().sendThongBao(player, player.playerSkill.skillSelect.coolDown + "");
//                return;
//            }
            if (text.startsWith("i")) {
                String[] parts = text.split(" ");
                if (parts.length >= 3) {
                    short id = Short.parseShort(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    Item item = ItemService.gI().createNewItem(id, quantity);
                    InventoryService.gI().addItemBag(player, item, quantity);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + item.template.name + " số lượng: " + quantity);
                    return;
                } else {
                    Service.getInstance().sendThongBao(player, "Lỗi");
                    return;
                }
            }
//            if (text.equals("client")) {
//                Client.gI().show(player);
//                return;
//            }

            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double cpuUsage = osBean.getSystemCpuLoad();
            long totalMemory = osBean.getTotalPhysicalMemorySize();
            long usedMemory = totalMemory - osBean.getFreePhysicalMemorySize();
            double memoryUsage = (double) usedMemory / totalMemory;

            DecimalFormat decimalFormat = new DecimalFormat("0.0%");
            String cpu = decimalFormat.format(cpuUsage);
            String memory = decimalFormat.format(memoryUsage);

            if (text.equals("admin")) {
                String str = "|7|--Ngọc Rồng Hashirama--" + "\n"
                        + "|4|Sessions: " + (Client.gI().getSessions().size() + soluongBot) + "\n"
                        + "|5|Tổng Thread: " + Thread.activeCount() + "\n"
                        + "|6|Số người Online:  " + (Client.gI().getPlayers().size() + soluongBot) + "\n"
                        + "|8|Thời gian chạy Server: " + Manager.NgayRunServer + "\n"
                        + "|4|Memory usage: " + memory + "\n"
                        + "Current CPU usage: " + cpu + "\n"
                        + "|3|[ Vui lòng chọn quyền ]";
                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_ADMIN, -1, str,
                        "Ngọc rồng", "Gọi Boss", "Tìm kiếm\nngười chơi",
                        "Cập Nhật\nThông Báo", "Đổi hành tinh");//, "Bảo trì", "Buff Item Option", "Tặng\nDanh hiệu"
                return;
            } else if (text.equals("vt")) {
                sendThongBao(player, "Tọa độ\n" + player.location.x + " - " + player.location.y);
                return;
            }  else if (text.equals("load")) {
                String str = "|7|--Ngọc Rồng Hashirama--" + "\n"
                        + "|4|Sessions: " + (Client.gI().getSessions().size() + soluongBot) + "\n"
                        + "|5|Tổng Thread: " + Thread.activeCount() + "\n"
                        + "|6|Số người Online:  " + (Client.gI().getPlayers().size() + soluongBot) + "\n"
                        + "|8|Thời gian chạy Server: " + Manager.NgayRunServer + "\n"
                        + "|4|Memory usage: " + memory + "\n"
                        + "Current CPU usage: " + cpu + "\n"
                        + "|3|[ Vui lòng chọn chức năng cần load lại]";
                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LOAD_DATA, -1, str,
                        "Load\nBảng Tin","Load\nPhúc Lợi","Load\nVòng Quay","Load\nKhảm Ngọc","Load\nRương\nXưu Tầm");
                return;
            } 

//            else if (text.equals("danhhieu")) {
            //                BuffDanhHieu(player);
            //                return;
            //            } else if (text.equals("bang")) {
            //                new Thread(()-> {
            //                    try {
            //                        ClanService.gI().close();
            //                        System.out.println("Save " + Manager.CLANS.size() + " bang");
            //                    } catch (Exception e) {
            //                        System.out.println("Lỗi save clan!...................................\n");
            //                    }
            //                })
            //            }
            //            else if (text.equals("a")) {
            //                BossManager.gI().showListBoss(player);
            //                return;
            //            } else if (text.equals("r")) { // hồi all skill, Ki
            //                Service.getInstance().releaseCooldownSkill(player);
            //                return;
            //            } else if (text.equals("toado")) {
            //                Service.getInstance().sendThongBao(player, player.location.x + " - " + player.location.y);
            //                return;
            //            } else if (text.equals("tn")) {
            //                Input.gI().createFormTangRuby(player);
            //                return;
            //            } else if (text.equals("it")) {
            //                Input.gI().createFormAddItem(player);
            //                return;
            //            }
            // tạo bot
            else if (text.startsWith("bot")) {
                try {
                    int soluong = Integer.parseInt(text.replaceAll("bot", ""));
                    soluongBot += soluong;
                    for (int a = 0; a < soluong; a++) {
                        BotManager.gI().createBot();
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            else if (text.startsWith("f")) {
            //                int id = Integer.parseInt(text.replaceAll("f", ""));
            //                Resources.effData(player.getSession(), id);
            //                return;
            //            }
            //            else if (text.startsWith("xoa")) {
            //                try {
            //                    int soluong = Integer.parseInt(text.replaceAll("xoa", ""));
            //                    for (int a = 0; a < soluong; a++) {
            //                        if (!Client.gI().bots.isEmpty()) {
            //                            soluongBot--;
            //                            Client.gI().clear();
            //                        } else {
            //                            break;
            //                        }
            //                    }
            //                    System.out.println("CON LAI: " + Client.gI().bots.size() + " BOT");
            //                    return;
            //                } catch (Exception e) {
            //                    e.printStackTrace();
            //                }
            //            }
            //            else if (text.startsWith("up")) {
            //                try {
            //                    long power = Long.parseLong(text.replaceAll("up", ""));
            //                    addSMTN(player, (byte) 2, power, false);
            //                    return;
            //                } catch (Exception e) {
            //                }
            //            } 
            else if (text.startsWith("chat")) {
                try {
                    String chat = text.replaceAll("chat ", "");
                    HeThongChatGlobal(chat);
                    return;
                } catch (Exception e) {
                }
            } else if (text.startsWith("m ")) {
                try {
                    int mapId = Integer.parseInt(text.replace("m ", ""));
                    Zone zone = MapService.gI().getZoneJoinByMapIdAndZoneId(player, mapId, 0);
                    if (zone != null) {
                        player.location.x = 500;
                        player.location.y = zone.map.yPhysicInTop(500, 100);
                        MapService.gI().goToMap(player, zone);
                        Service.getInstance().clearMap(player);
                        zone.mapInfo(player);
                        player.zone.loadAnotherToMe(player);
                        player.zone.load_Me_To_Another(player);
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            else if (text.startsWith("s")) {
//                int so = Integer.parseInt(text.replace("s", ""));
//                player.soMayMan.add(so);
//                youNumber(player, so);
//                return;
//            } else if (text.equals("xs")) {
//                sendsoxo(player, 18, false);
//                return;
//            }
        }
        if (text.equals("tt")) {
            infoall(player);
            return;
        }

//        if (text.equals("diemdanh")) {
//            String str = "|7|--Nro free 2025--" + "\n"
//                    + "|8|Số người Online:  " + (Client.gI().getPlayers().size()) + "\n"
//                    + "|3|[ Vui lòng chọn quyền ]"
//                    + "Bạn đã điểm danh: " + "Ngày";
//            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_THUONG_NGAY, -1, str,
//                    "Nhận thưởng\nHàng Ngày", "Nhận Thưởng\n7 Ngày", "Nhận thưởng\n30 Ngày");
//            return;
//        }

//        if (text.equals("dh")) {
//            MenuDanhHieu(player);
//            return;
//        }
//        if (text.startsWith("danhhieu ")) {
//            int sodanhhieu = Integer.parseInt(text.replace("danhhieu ", ""));
//            if (player.lastTimeTitle1 == 0 && player.IdDanhHieu_1 != sodanhhieu) {
//                if (player.lastTimeTitle1 == 0) {
//                    player.lastTimeTitle1 += System.currentTimeMillis() + (1000 * 60 * 60);
//                }
//                player.isTitleUse1 = true;
//                player.IdDanhHieu_1 = sodanhhieu;
//                Service.getInstance().point(player);
//                SendEffect.getInstance().removeTitle(player);
//                Service.getInstance().sendMoney(player);
//                return;
//            } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 == 0 && player.IdDanhHieu_1 != sodanhhieu && player.IdDanhHieu_2 != sodanhhieu) {
//                if (player.lastTimeTitle2 == 0) {
//                    player.lastTimeTitle2 += System.currentTimeMillis() + (1000 * 60 * 60);
//                }
//                player.isTitleUse2 = true;
//                player.IdDanhHieu_2 = sodanhhieu;
//                Service.getInstance().point(player);
//                SendEffect.getInstance().removeTitle(player);
//                Service.getInstance().sendMoney(player);
//                return;
//            } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 != 0 && player.lastTimeTitle3 == 0
//                    && player.IdDanhHieu_1 != sodanhhieu && player.IdDanhHieu_2 != sodanhhieu && player.IdDanhHieu_3 != sodanhhieu) {
//                if (player.lastTimeTitle3 == 0) {
//                    player.lastTimeTitle3 += System.currentTimeMillis() + (1000 * 60 * 60);
//                }
//                player.isTitleUse3 = true;
//                player.IdDanhHieu_3 = sodanhhieu;
//                Service.getInstance().point(player);
//                SendEffect.getInstance().removeTitle(player);
//                Service.getInstance().sendMoney(player);
//                return;
//            } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 != 0 && player.lastTimeTitle3 != 0
//                    && player.lastTimeTitle4 == 0 && player.IdDanhHieu_1 != sodanhhieu
//                    && player.IdDanhHieu_2 != sodanhhieu && player.IdDanhHieu_3 != sodanhhieu && player.IdDanhHieu_4 != sodanhhieu) {
//
//                if (player.lastTimeTitle4 == 0) {
//                    player.lastTimeTitle4 += System.currentTimeMillis() + (1000 * 60 * 60);
//                }
//                player.IdDanhHieu_4 = sodanhhieu;
//                player.isTitleUse4 = true;
//                Service.getInstance().point(player);
//                SendEffect.getInstance().removeTitle(player);
//                Service.getInstance().sendMoney(player);
//                return;
//            } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 != 0
//                    && player.lastTimeTitle3 != 0 && player.lastTimeTitle4 != 0 && player.lastTimeTitle5 == 0
//                    && player.IdDanhHieu_1 != sodanhhieu && player.IdDanhHieu_2 != sodanhhieu && player.IdDanhHieu_3 != sodanhhieu
//                    && player.IdDanhHieu_4 != sodanhhieu && player.IdDanhHieu_5 != sodanhhieu) {
//                if (player.lastTimeTitle5 == 0) {
//                    player.lastTimeTitle5 += System.currentTimeMillis() + (1000 * 60 * 60);
//                }
//                player.IdDanhHieu_5 = sodanhhieu;
//                player.isTitleUse5 = true;
//                Service.getInstance().point(player);
//                SendEffect.getInstance().removeTitle(player);
//                Service.getInstance().sendMoney(player);
//                return;
//            }
//            return;
//        }
//        if (text.startsWith("d1")) {
//            if (player.lastTimeTitle4 == 0) {
//                player.lastTimeTitle4 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
//            } else {
//                player.lastTimeTitle4 += (1000 * 60 * 60 * 24 * 7);
//            }
//            player.isTitleUse4 = true;
//            Service.getInstance().point(player);
//            Service.getInstance().sendTitle(player, 891);
//            return;
//        }
//        if (text.startsWith("d2")) {
//            if (player.lastTimeTitle5 == 0) {
//                player.lastTimeTitle5 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
//            } else {
//                player.lastTimeTitle5 += (1000 * 60 * 60 * 24 * 7);
//            }
//            player.isTitleUse5 = true;
//            Service.getInstance().point(player);
//            Service.getInstance().sendTitle(player, 892);
//            return;
//        }
        if (text.startsWith("ten con la ")) {
            PetService.gI().changeNamePet(player, text.replaceAll("ten con la ", ""));
        }
        if (text.equals("fix")) {
            Service.getInstance().player(player);
            Service.getInstance().Send_Caitrang(player);
        }
        if (player.pet != null) {
            if (text.equals("di theo") || text.equals("follow")) {
                player.pet.changeStatus(Pet.FOLLOW);
            } else if (text.equals("bao ve") || text.equals("protect")) {
                player.pet.changeStatus(Pet.PROTECT);
            } else if (text.equals("tan cong") || text.equals("attack")) {
                player.pet.changeStatus(Pet.ATTACK);
            } else if (text.equals("ve nha") || text.equals("go home")) {
                player.pet.changeStatus(Pet.GOHOME);
            } else if (text.equals("bien hinh")) {
                player.pet.transform();
            }
        }

        if (text.length() > 100) {
            text = text.substring(0, 100);
        }
        chatMap(player, text);
    }

    public void chatMap(Player player, String text) {
        Message msg;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeUTF(text);
            sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void infoall(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12713,
                "BẢNG CHỨC NĂNG NGƯỜI CHƠI",
                "Thông tin\n nhân vật", "Thông tin\nđệ tử", "Thông tin\nđồ mặc");
    }

    public void MenuDanhHieu(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DANHHIEU, 0,
                "|7|DANH HIỆU THỜI HẠN"
                + "\n|2|Đây là danh hiệu mà ngươi có"
                + (player.lastTimeTitle1 > 0 ? "\n|4|Đại Thần: " + Util.msToTime(player.lastTimeTitle1) : "\n|4|Đại Thần: Chưa có")
                + (player.lastTimeTitle2 > 0 ? "\nCần Thủ: " + Util.msToTime(player.lastTimeTitle2) : "\nCần Thủ: Chưa có")
                + (player.lastTimeTitle3 > 0 ? "\nTuổi Thơ: " + Util.msToTime(player.lastTimeTitle3) : "\nTuổi Thơ: Chưa có")
                + (player.lastTimeTitle4 > 0 ? "\nThợ ngọc: " + Util.msToTime(player.lastTimeTitle4) : "\nThợ ngọc: Chưa có")
                + (player.lastTimeTitle5 > 0 ? "\nTuổi Thơ: " + Util.msToTime(player.lastTimeTitle5) : "\nTuổi Thơ: Chưa có")
                + "\n|3|(Mỗi Danh hiệu còn thời hạn sẽ tăng 20% HP,KI,SD cho bản thân)",
                ("Đại Thần\n" + (player.isTitleUse1 == true ? "'ON'" : "'OFF'")),
                ("Cần Thủ\n" + (player.isTitleUse2 == true ? "'ON'" : "'OFF'") + "\n"),
                ("Tuổi Thơ\n" + (player.isTitleUse3 == true ? "'ON'" : "'OFF'") + "\n"),
                ("Thợ ngọc\n" + (player.isTitleUse4 == true ? "'ON'" : "'OFF'") + "\n"),
                ("Chịch gái\n" + (player.isTitleUse5 == true ? "'ON'" : "'OFF'") + "\n"));
    }
    

    public void BuffDanhHieu(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.ADMIN_DANH_HIEU, 0,
                "|7|CHỌN DANH HIỆU"
                + "\n|2|Chọn danh hiệu 7 ngày dưới đây !!!",
                ("Đại Thần"),
                ("Cần Thủ"),
                ("Tuổi Thơ"),
                ("Thợ ngọc"));
    }

    public String capVIP(int capbac) {
        if (capbac == 0) {
            return "Dân Đen";
        } else if (capbac == 1) {
            return "ĐỒNG";
        } else if (capbac == 2) {
            return "BẠC";
        } else if (capbac == 3) {
            return "GOLD";
        } else if (capbac == 4) {
            return "LỤC BẢO";
        } else if (capbac == 5) {
            return "BẠCH KIM";
        } else if (capbac == 6) {
            return "KIM CƯƠNG";
        }else{
            return "VƯƠNG GIẢ";
        }
    }

    public void chatJustForMe(Player me, Player plChat, String text) {
        Message msg;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeUTF(text);
            me.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void point(Player player) {
        player.nPoint.calPoint();
        Send_Info_NV(player);
        if (!player.isPet && !player.isBoss && !player.isBot) {
            Message msg;
            try {
                msg = new Message(-42);
                msg.writer().writeDouble(player.nPoint.hpg);
                msg.writer().writeDouble(player.nPoint.mpg);
                msg.writer().writeDouble(player.nPoint.dameg);
                msg.writer().writeDouble(player.nPoint.hpMax);// hp full
                msg.writer().writeDouble(player.nPoint.mpMax);// mp full
                msg.writer().writeDouble(player.nPoint.hp);// hp
                msg.writer().writeDouble(player.nPoint.mp);// mp
                msg.writer().writeByte(player.nPoint.speed);// speed
                msg.writer().writeByte(20);
                msg.writer().writeByte(20);
                msg.writer().writeByte(1);
                msg.writer().writeDouble(player.nPoint.dame);// dam base
                msg.writer().writeDouble(player.nPoint.def);// def full
                msg.writer().writeByte(player.nPoint.crit);// crit full
                msg.writer().writeDouble(player.nPoint.tiemNang);
                msg.writer().writeShort(100);
                msg.writer().writeDouble(player.nPoint.defg);
                msg.writer().writeByte(player.nPoint.critg);
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(Service.class, e);
            }
        }
    }

    public void player(Player pl) {
        if (pl == null) {
            return;
        }
        Message msg;
        try {
            msg = messageSubCommand((byte) 0);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(pl.playerTask.taskMain.id);
            msg.writer().writeByte(pl.gender);
            msg.writer().writeShort(pl.head);
            msg.writer().writeUTF(pl.name);
            msg.writer().writeByte(0); //cPK
            msg.writer().writeByte(pl.typePk);
            msg.writer().writeDouble(pl.nPoint.power);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(pl.gender);
            //--------skill---------

            ArrayList<Skill> skills = (ArrayList<Skill>) pl.playerSkill.skills;

            msg.writer().writeByte(pl.playerSkill.getSizeSkill());

            for (Skill skill : skills) {
                if (skill.skillId != -1) {
                    msg.writer().writeShort(skill.skillId);
                }
            }

            //---vang---luong--luongKhoa
            long gold = pl.inventory.getGoldDisplay();
            if (pl.isVersionAbove(214)) {
                msg.writer().writeLong(gold);
            } else {
                msg.writer().writeInt((int) gold);
            }
            msg.writer().writeInt(pl.inventory.ruby);
            msg.writer().writeInt(pl.inventory.gem);

            //--------itemBody---------
            ArrayList<Item> itemsBody = (ArrayList<Item>) pl.inventory.itemsBody;
            msg.writer().writeByte(itemsBody.size());
            for (Item item : itemsBody) {
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.getDisplayOptions();
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeInt(itemOption.param);
                    }
                }

            }

            //--------itemBag---------
            ArrayList<Item> itemsBag = (ArrayList<Item>) pl.inventory.itemsBag;
            msg.writer().writeByte(itemsBag.size());
            for (int i = 0; i < itemsBag.size(); i++) {
                Item item = itemsBag.get(i);
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.getDisplayOptions();
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeInt(itemOption.param);
                    }
                }

            }

            //--------itemBox---------
            ArrayList<Item> itemsBox = (ArrayList<Item>) pl.inventory.itemsBox;
            msg.writer().writeByte(itemsBox.size());
            for (int i = 0; i < itemsBox.size(); i++) {
                Item item = itemsBox.get(i);
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.getDisplayOptions();
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeInt(itemOption.param);
                    }
                }
            }
            //-----------------
            DataGame.sendHeadAvatar(msg);
            //-----------------
            msg.writer().writeShort(514); //char info id - con chim thông báo
            msg.writer().writeShort(515); //char info id
            msg.writer().writeShort(537); //char info id
            msg.writer().writeByte(pl.fusion.typeFusion != ConstPlayer.NON_FUSION ? 1 : 0); //nhập thể
//            msg.writer().writeInt(1632811835); //deltatime
            msg.writer().writeInt(333); //deltatime
            msg.writer().writeByte(pl.isNewMember ? 1 : 0); //is new member

//            if (pl.isAdmin()) {
            msg.writer().writeShort(pl.getAura()); //idauraeff
            msg.writer().writeByte(-1);
//            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public Message messageNotLogin(byte command) throws IOException {
        Message ms = new Message(-29);
        ms.writer().writeByte(command);
        return ms;
    }

    public Message messageNotMap(byte command) throws IOException {
        Message ms = new Message(-28);
        ms.writer().writeByte(command);
        return ms;
    }

    public Message messageSubCommand(byte command) throws IOException {
        Message ms = new Message(-30);
        ms.writer().writeByte(command);
        return ms;
    }

    public void addSMTN(Player player, byte type, double param, boolean isOri) {
        if (player.isPet) {
            if (player.nPoint.power > player.nPoint.getPowerLimit()) {
                return;
            }
            player.nPoint.powerUp((long)param);
            player.nPoint.tiemNangUp(param);
            Player master = ((Pet) player).master;

            param = master.nPoint.calSubTNSM(param);
            if (master.nPoint.power < master.nPoint.getPowerLimit()) {
                master.nPoint.powerUp((long)param);
            }
            master.nPoint.tiemNangUp(param);
            addSMTN(master, type, param, true);
        } else {
            if (player.nPoint.power > player.nPoint.getPowerLimit()) {
                return;
            }
            switch (type) {
                case 1:
                    player.nPoint.tiemNangUp(param);
                    break;
                case 2:
                    player.nPoint.powerUp((long)param);
                    player.nPoint.tiemNangUp(param);
                    break;
                default:
                    player.nPoint.powerUp((long)param);
                    break;
            }
            PlayerService.gI().sendTNSM(player, type, param);
            if (isOri) {
                if (player.clan != null) {
                    player.clan.addSMTNClan(player, param);
                }
            }
        }
    }

    //    public void congTiemNang(Player pl, byte type, int tiemnang) {
//        Message msg;
//        try {
//            msg = new Message(-3);
//            msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
//            msg.writer().writeInt(tiemnang);// số tn cần cộng
//            if (!pl.isPet) {
//                pl.sendMessage(msg);
//            } else {
//                ((Pet) pl).master.nPoint.powerUp(tiemnang);
//                ((Pet) pl).master.nPoint.tiemNangUp(tiemnang);
//                ((Pet) pl).master.sendMessage(msg);
//            }
//            msg.cleanup();
//            switch (type) {
//                case 1:
//                    pl.nPoint.tiemNangUp(tiemnang);
//                    break;
//                case 2:
//                    pl.nPoint.powerUp(tiemnang);
//                    pl.nPoint.tiemNangUp(tiemnang);
//                    break;
//                default:
//                    pl.nPoint.powerUp(tiemnang);
//                    break;
//            }
//        } catch (Exception e) {
//
//        }
//    }
    public String get_HanhTinh(int hanhtinh) {
        switch (hanhtinh) {
            case 0:
                return "Trái Đất";
            case 1:
                return "Namếc";
            case 2:
                return "Xayda";
            default:
                return "";
        }
    }

    public String getCurrStrLevel(Player pl) {
        double sucmanh = pl.nPoint.power;
        if (sucmanh < 3000) {
            return "1 Con Gà";
        } else if (sucmanh < 15000) {
            return "1 Con Gà";
        } else if (sucmanh < 40000) {
            return "1 Con Gà";
        } else if (sucmanh < 90000) {
            return "1 Con Gà";
        } else if (sucmanh < 170000) {
            return "1 Con Gà";
        } else if (sucmanh < 340000) {
            return "1 Con Gà";
        } else if (sucmanh < 700000) {
            return "1 Con Gà";
        } else if (sucmanh < 1500000) {
            return "Tập chơi";
        } else if (sucmanh < 15000000) {
            return "Gỗ đoàn";
        } else if (sucmanh < 150000000) {
            return "Nhôm Nhựa";
        } else if (sucmanh < 1500000000) {
            return "Sắt vụn";
        } else if (sucmanh < 5000000000L) {
            return "Đồng nát";
        } else if (sucmanh < 10000000000L) {
            return "Bạc Ráck";
        } else if (sucmanh < 40000000000L) {
            return "Vàng Ráck";
        } else if (sucmanh < 50010000000L) {
            return "Bạch Kim";
        } else if (sucmanh < 60010000000L) {
            return "Lục Bảo";
        } else if (sucmanh < 70010000000L) {
            return "Kim Cương";
        } else if (sucmanh < 80010000000L) {
            return "Tinh Anh";
        } else if (sucmanh < 100010000000L) {
            return "Cao Thủ";
        } else if (sucmanh < 11100010000000L) {
            return "Đại Cao Thủ";
        }
        return "Thách Đấu";
    }

    public void sendsoxo(Player pl, int winNum, boolean kqua) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        String lose = "Con số trúng thưởng là " + winNum + "\nChúc bạn may mắn lần sau!!";
        String win = "Chúc mừng bạn đã đoán trúng số " + winNum + " của giải lần này";
        Message msg;
        try {
            msg = new Message(-126);
            msg.writer().writeByte(1);
            msg.writer().writeByte(0);
            msg.writer().writeUTF(num.format(winNum));
            msg.writer().writeUTF((kqua == true) ? win : lose);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void youNumber(Player pl, int number) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int num : pl.soMayMan) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(num);
        }
        String text = stringBuilder.toString();

        Message msg;
        try {
            msg = new Message(-126);
            msg.writer().writeByte(0);
            msg.writer().writeUTF(text);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void hsChar(Player pl, double hp, double mp) {
        Message msg;
        try {
            pl.setJustRevivaled();
            pl.nPoint.setHp(hp);
            pl.nPoint.setMp(mp);
            if (!pl.isPet) {
                msg = new Message(-16);
                pl.sendMessage(msg);
                msg.cleanup();
                PlayerService.gI().sendInfoHpMpMoney(pl);
            }

            msg = messageSubCommand((byte) 15);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeDouble(hp);
            msg.writer().writeDouble(mp);
            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);
            sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();

            Send_Info_NV(pl);
            PlayerService.gI().sendInfoHpMp(pl);
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void charDie(Player pl) {
        Message msg;
        try {
            if (!pl.isPet) {
                msg = new Message(-17);
                msg.writer().writeByte((int) pl.id);
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                pl.sendMessage(msg);
                msg.cleanup();
            } else {
                ((Pet) pl).lastTimeDie = System.currentTimeMillis();
            }

            msg = new Message(-8);
            msg.writer().writeShort((int) pl.id);
            msg.writer().writeByte(0); //cpk
            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();

//            Send_Info_NV(pl);
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void attackMob(Player pl, int mobId) {
        if (pl != null && pl.zone != null) {
            for (Mob mob : pl.zone.mobs) {
                if (mob.id == mobId) {
                    SkillService.gI().useSkill(pl, null, mob, null);
                    break;
                }
            }
        }
    }

    public void sendTitleRv(Player player, Player p2, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            if (id == 888) {
                if (player.lastTimeTitle1 > 0 && player.isTitleUse1) {
                    me.writer().writeShort(93);
                }
            }
            if (id == 889) {
                if (player.lastTimeTitle2 > 0 && player.isTitleUse2) {
                    me.writer().writeShort(95);
                }
            }
            if (id == 890) {
                if (player.lastTimeTitle3 > 0 && player.isTitleUse3) {
                    me.writer().writeShort(94);
                }
            }
            if (id == 891) {
                if (player.lastTimeTitle4 > 0 && player.isTitleUse4) {
                    me.writer().writeShort(236);
                }
            }
            if (id == 892) {
                if (player.lastTimeTitle5 > 0 && player.isTitleUse5) {
                    me.writer().writeShort(235);
                }
            }
            if (id == 900) {
                if (player.gender == 0 && player.nPoint.power >= 9_990_000_000_000L) {
                    me.writer().writeShort(89);
                } else if (player.gender == 1 && player.nPoint.power >= 9_990_000_000_000L) {
                    me.writer().writeShort(89);
                } else if (player.gender == 2 && player.nPoint.power >= 9_990_000_000_000L) {
                    me.writer().writeShort(89);
                }
            }
            me.writer().writeByte(1);
            me.writer().writeByte(-1);
            me.writer().writeShort(50);
            me.writer().writeByte(-1);
            me.writer().writeByte(-1);
            p2.sendMessage(me);
            me.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTitle(Player player, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            for (int i = 0; i < 9; i++) {
                if (id == 1470 + i) {
                    me.writer().writeShort(91 + i);
                }
            }
            if (id == 1622) {
                me.writer().writeShort(216);
            }
            if (id == 888) {
                if (player.lastTimeTitle1 > 0 && player.isTitleUse1) {
                    me.writer().writeShort(93);
                }
            }
            if (id == 889) {
                if (player.lastTimeTitle2 > 0 && player.isTitleUse2) {
                    me.writer().writeShort(95);
                }
            }
            if (id == 890) {
                if (player.lastTimeTitle3 > 0 && player.isTitleUse3) {
                    me.writer().writeShort(94);
                }
            }
            if (id == 891) {
                if (player.lastTimeTitle4 > 0 && player.isTitleUse4) {
                    me.writer().writeShort(236);
                }
            }
            if (id == 892) {
                if (player.lastTimeTitle5 > 0 && player.isTitleUse5) {
                    me.writer().writeShort(235);
                }
            }
            if (id == 900) {
                if (player.gender == 0 && player.nPoint.power >= 9_990_000_000_000L) {
                    me.writer().writeShort(89);
                } else if (player.gender == 1 && player.nPoint.power >= 9_990_000_000_000L) {
                    me.writer().writeShort(89);
                } else if (player.gender == 2 && player.nPoint.power >= 9_990_000_000_000L) {
                    me.writer().writeShort(89);
                }
            }
            me.writer().writeByte(1);
            me.writer().writeByte(-1);
            me.writer().writeShort(50);
            me.writer().writeByte(-1);
            me.writer().writeByte(-1);
            this.sendMessAllPlayerInMap(player, me);
            me.cleanup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeTitle(Player player) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(2);
            me.writer().writeInt((int) player.id);
            player.getSession().sendMessage(me);
            this.sendMessAllPlayerInMap(player, me);
            me.cleanup();
            if (player.inventory.itemsBody.get(11).isNotNullItem()) {
                Service.getInstance().sendFoot(player, (short) player.inventory.itemsBody.get(11).template.id);
            }
            if (player.inventory.itemsBody.get(14).isNotNullItem()) {
                Service.getInstance().sendTitle(player, (short) player.inventory.itemsBody.get(14).template.id);
            }
            if (player.isTitleUse1 == true && player.lastTimeTitle1 > 0) {
                Service.getInstance().sendTitle(player, (short) 888);
            }
            if (player.isTitleUse2 == true && player.lastTimeTitle2 > 0) {
                Service.getInstance().sendTitle(player, (short) 889);
            }
            if (player.isTitleUse3 == true && player.lastTimeTitle3 > 0) {
                Service.getInstance().sendTitle(player, (short) 890);
            }
            if (player.isTitleUse4 == true && player.lastTimeTitle4 > 0) {
                Service.getInstance().sendTitle(player, (short) 891);
            }
            if (player.isTitleUse5 == true && player.lastTimeTitle5 > 0) {
                Service.getInstance().sendTitle(player, (short) 892);
            }
//            if (player.nPoint.power >= 500_000_000_000L) {
//                Service.getInstance().sendTitle(player, (short) 900);
//            }
        } catch (IOException e) {
        }
    }

    public void SendThreadEff(Player player) {

        new Thread(() -> {

            try {
                Thread.sleep(1000);
                if (player.inventory.itemsBody.get(11).isNotNullItem()) {
                    Service.getInstance().sendFoot(player, (short) player.inventory.itemsBody.get(11).template.id);
                }
                if (player.inventory.itemsBody.get(14).isNotNullItem()) {
                    Service.getInstance().sendTitle(player, (short) player.inventory.itemsBody.get(14).template.id);
                }
                if (player.lastTimeTitle1 > 0) {
                    Service.getInstance().sendTitle(player, (short) 888);
                }
                if (player.lastTimeTitle2 > 0) {
                    Service.getInstance().sendTitle(player, (short) 889);
                }
                if (player.lastTimeTitle3 > 0) {
                    Service.getInstance().sendTitle(player, (short) 890);
                }
                if (player.lastTimeTitle4 > 0) {
                    Service.getInstance().sendTitle(player, (short) 891);
                }
                if (player.lastTimeTitle5 > 0) {
                    Service.getInstance().sendTitle(player, (short) 892);
                }
//                if (player.nPoint.power >= 500_000_000_000L) {
//                    Service.getInstance().sendTitle(player, (short) 900);
//                }
                if (player.inventory.itemsBody.get(14).isNotNullItem()) {
                    Service.getInstance().sendTitle(player, (short) player.inventory.itemsBody.get(14).template.id);
                }
            } catch (Exception e) {

            }

        }).start();
    }


    public void sendFoot(Player player, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            switch (id) {
                case 1300:
                    me.writer().writeShort(79);
                    break;
                case 1301:
                    me.writer().writeShort(80);
                    break;
                case 1302:
                    me.writer().writeShort(81);
                    break;
                case 1303:
                    me.writer().writeShort(82);
                    break;
                case 1304:
                    me.writer().writeShort(83);
                    break;
                case 1305:
                    me.writer().writeShort(84);
                    break;
                case 1306:
                    me.writer().writeShort(85);
                    break;
                case 1307:
                    me.writer().writeShort(86);
                    break;
                case 1308:
                    me.writer().writeShort(87);
                    break;
                default:
                    break;
            }
            me.writer().writeByte(0);
            me.writer().writeByte(-1);
            me.writer().writeShort(1);
            me.writer().writeByte(-1);
            this.sendMessAllPlayerInMap(player, me);
            me.cleanup();

        } catch (IOException e) {
        }
    }

    public void sendFootRv(Player player, Player p2, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            switch (id) {
                case 1300:
                    me.writer().writeShort(79);
                    break;
                case 1301:
                    me.writer().writeShort(80);
                    break;
                case 1302:
                    me.writer().writeShort(81);
                    break;
                case 1303:
                    me.writer().writeShort(82);
                    break;
                case 1304:
                    me.writer().writeShort(83);
                    break;
                case 1305:
                    me.writer().writeShort(84);
                    break;
                case 1306:
                    me.writer().writeShort(85);
                    break;
                case 1307:
                    me.writer().writeShort(86);
                    break;
                case 1308:
                    me.writer().writeShort(87);
                    break;
                default:
                    break;
            }

            me.writer().writeByte(0);
            me.writer().writeByte(-1);
            me.writer().writeShort(1);
            me.writer().writeByte(-1);
            p2.sendMessage(me);
            me.cleanup();
        } catch (IOException e) {
        }
    }

    public void loadLaiEff(Player player) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(2);
            me.writer().writeInt((int) player.id);
            player.getSession().sendMessage(me);
            this.sendMessAllPlayerInMap(player, me);
            me.cleanup();
        } catch (IOException e) {
        }
    }

    public void Send_Caitrang(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); //id player
                short head = player.getHead();
                short body = player.getBody();
                short leg = player.getLeg();

                msg.writer().writeShort(head);//set head
                msg.writer().writeShort(body);//setbody
                msg.writer().writeShort(leg);//set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);//set khỉ
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(Service.class, e);
            }
        }
    }

    public void setNotMonkey(Player player) {
        Message msg;
        try {
            msg = new Message(-90);
            msg.writer().writeByte(-1);
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void sendFlagBag(Player pl) {
        Message msg;
        try {
            msg = new Message(-64);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(pl.getFlagBag());
            sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendThongBaoOK(Player pl, String text) {
        if (pl.isPet) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void sendThongBaoOK(Session session, String text) {
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendThongBaoAllPlayer(String thongBao) {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(thongBao);
            this.sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendBigMessage(Player player, int iconId, String text) {
        try {
            Message msg;
            msg = new Message(-70);
            msg.writer().writeShort(iconId);
            msg.writer().writeUTF(text);
            msg.writer().writeByte(0);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendThongBaoFromAdmin(Player player, String text) {
        sendBigMessage(player, 1139, text);
    }

    public void sendBigMessAllPlayer(int iconId, String text) {
        try {
            Message msg;
            msg = new Message(-70);
            msg.writer().writeShort(iconId);
            msg.writer().writeUTF(text);
            msg.writer().writeByte(0);
            this.sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendThongBao(Player pl, String thongBao) {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(thongBao);
            pl.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void sendMoney(Player pl) {
        Message msg;
        try {
            msg = new Message(6);
            long gold = pl.inventory.getGoldDisplay();
            if (pl.isVersionAbove(214)) {
                msg.writer().writeLong(gold);
            } else {
                msg.writer().writeInt((int) gold);
            }
            msg.writer().writeInt(pl.inventory.gem);
            msg.writer().writeInt(pl.inventory.ruby);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendToAntherMePickItem(Player player, int itemMapId) {
        Message msg;
        try {
            msg = new Message(-19);
            msg.writer().writeShort(itemMapId);
            msg.writer().writeInt((int) player.id);
            sendMessAllPlayerIgnoreMe(player, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public boolean isItemMoney(int type) {
        return type == 9 || type == 10 || type == 34;
    }

    public void SendImgSkill9(short SkillId, int IdAnhSKill) {
        Message msg = new Message(62);
        DataOutputStream ds = msg.writer();
        try {
            ds.writeShort(SkillId);
            ds.writeByte(1);
            ds.writeByte(IdAnhSKill);
            ds.flush();
            Service.getInstance().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

//    public void useSkillNotFocus(Player pl, Message m) throws IOException {
//        byte status = m.reader().readByte();
//        if (status == 20) {
//            SkillService.gI().userSkillSpecial(pl, m);
//        } else {
//            SkillService.gI().useSkill(pl, null, null);
//        }
//    }
    public void chatGlobal(Player pl, String text) {
        if (pl.inventory.getGold() >= 2000000000) {
            if (pl.isAdmin() || Util.canDoWithTime(pl.lastTimeChatGlobal, 18000)) {
                if (pl.isAdmin() || pl.nPoint.power > 2000000000) {
                    pl.inventory.subGold(2000000000);
                    sendMoney(pl);
                    pl.lastTimeChatGlobal = System.currentTimeMillis();
                    Message msg;
                    try {
                        msg = new Message(92);
                        msg.writer().writeUTF(pl.name);
                        msg.writer().writeUTF("|5|" + text);
                        msg.writer().writeInt((int) pl.id);
                        msg.writer().writeShort(pl.getHead());
                        msg.writer().writeShort(pl.getBody());
                        msg.writer().writeShort(pl.getFlagBag()); //bag
                        msg.writer().writeShort(pl.getLeg());
                        msg.writer().writeByte(0);
                        sendMessAllPlayer(msg);
                        msg.cleanup();
                    } catch (Exception e) {
                    }
                } else {
                    sendThongBao(pl, "Sức mạnh phải ít nhất 2tỷ mới có thể chat thế giới");
                }
            } else {
                sendThongBao(pl, "Không thể chat thế giới lúc này, vui lòng đợi " + TimeUtil.getTimeLeft(pl.lastTimeChatGlobal, 120));
            }
        } else {
            sendThongBao(pl, "Không đủ vàng để chat thế giới");
        }
    }

    public void HeThongChatGlobal(String text) {
        List<Player> list = Client.gI().getPlayers();
        for (Player pl : list) {
            if (pl != null) {
                try {
                    Message ms = new Message(Cmd.CHAT_THEGIOI_SERVER);
                    ms.writer().writeUTF("THÔNG BÁO SERVER");
                    ms.writer().writeUTF("|5|" + text);
                    ms.writer().writeInt(-1);
                    ms.writer().writeShort(1460);
                    if (pl.isVersionAbove(220)) {
                        ms.writer().writeShort(-1);
                    }
                    ms.writer().writeShort(1461);
                    ms.writer().writeShort(-1); //bag
                    ms.writer().writeShort(1462);
                    ms.writer().writeByte(0);
                    pl.sendMessage(ms);
                    ms.cleanup();
                } catch (Exception e) {
                }
            }
        }
    }

    private int tiLeXanhDo = 3;

    public int xanhToDo(int n) {
        return n * tiLeXanhDo;
    }

    public int doToXanh(int n) {
        return (int) n / tiLeXanhDo;
    }

    public static final int[] flagTempId = {363, 364, 365, 366, 367, 368, 369, 370, 371, 519, 520, 747};
    public static final int[] flagIconId = {2761, 2330, 2323, 2327, 2326, 2324, 2329, 2328, 2331, 4386, 4385, 2325};

    public void openFlagUI(Player pl) {
        Message msg;
        try {
            msg = new Message(-103);
            msg.writer().writeByte(0);
            msg.writer().writeByte(flagTempId.length);
            for (int i = 0; i < flagTempId.length; i++) {
                msg.writer().writeShort(flagTempId[i]);
                msg.writer().writeByte(1);
                switch (flagTempId[i]) {
                    case 363:
                        msg.writer().writeByte(73);
                        msg.writer().writeInt(0);
                        break;
                    case 371:
                        msg.writer().writeByte(88);
                        msg.writer().writeInt(10);
                        break;
                    default:
                        msg.writer().writeByte(88);
                        msg.writer().writeInt(5);
                        break;
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void changeFlag(Player pl, int index) {
        Message msg;
        try {
            pl.cFlag = (byte) index;
            msg = new Message(-103);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(index);
            Service.getInstance().sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();

            msg = new Message(-103);
            msg.writer().writeByte(2);
            msg.writer().writeByte(index);
            msg.writer().writeShort(flagIconId[index]);
            Service.getInstance().sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();

            if (pl.pet != null) {
                pl.pet.cFlag = (byte) index;
                msg = new Message(-103);
                msg.writer().writeByte(1);
                msg.writer().writeInt((int) pl.pet.id);
                msg.writer().writeByte(index);
                Service.getInstance().sendMessAllPlayerInMap(pl.pet, msg);
                msg.cleanup();

                msg = new Message(-103);
                msg.writer().writeByte(2);
                msg.writer().writeByte(index);
                msg.writer().writeShort(flagIconId[index]);
                Service.getInstance().sendMessAllPlayerInMap(pl.pet, msg);
                msg.cleanup();
            }
            pl.lastTimeChangeFlag = System.currentTimeMillis();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void sendFlagPlayerToMe(Player me, Player pl) {
        Message msg;
        try {
            msg = new Message(-103);
            msg.writer().writeByte(2);
            msg.writer().writeByte(pl.cFlag);
            msg.writer().writeShort(flagIconId[pl.cFlag]);
            me.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void chooseFlag(Player pl, int index) {
        if (Util.canDoWithTime(pl.lastTimeChangeFlag, 60000)) {
            if (!MapService.gI().isMapBlackBallWar(pl.zone.map.mapId) && !MapService.gI().isMapMabuWar(pl.zone.map.mapId) && !pl.isHoldBlackBall) {
                changeFlag(pl, index);
            } else {
                sendThongBao(pl, "Không thể đổi cờ ở khu vực này");
            }
        } else {
            sendThongBao(pl, "Không thể đổi cờ lúc này! Vui lòng đợi " + TimeUtil.getTimeLeft(pl.lastTimeChangeFlag, 60) + " nữa!");
        }
    }

    public void attackPlayer(Player pl, int idPlAnPem) {
        SkillService.gI().useSkill(pl, pl.zone.getPlayerInMap(idPlAnPem), null, null);
    }

//    public void logingame(String status) {
//        if (status.equals("logingame")) {
//            Runtime.getRuntime().exit(0);
//        }
//    }
    public void openZoneUI(Player pl) {
        if (pl.zone == null || pl.zone.map.isMapOffline) {
            sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
            return;
        }
        int mapid = pl.zone.map.mapId;
        if (!pl.isAdmin() && (MapService.gI().isMapDoanhTrai(mapid)
                || MapService.gI().isMapBanDoKhoBau(mapid)
                || mapid == 120
                || mapid == 114
                || mapid == 115
                || mapid == 117
                || mapid == 118
                || mapid == 119
                || MapService.gI().isMapVS(mapid)
                || mapid == 126
                || pl.zone instanceof ZDungeon)) {
            sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
            return;
        }
        Message msg;
        try {
            msg = new Message(29);
            msg.writer().writeByte(pl.zone.map.zones.size());
            for (Zone zone : pl.zone.map.zones) {
                msg.writer().writeByte(zone.zoneId);
                int numPlayers = zone.getNumOfPlayers();
                msg.writer().writeByte((numPlayers < 5 ? 0 : (numPlayers < 8 ? 1 : 2)));
                msg.writer().writeByte(numPlayers);
                msg.writer().writeByte(zone.maxPlayer);
                msg.writer().writeByte(0);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }


    public void releaseCooldownSkill(Player pl) {
        Message msg;
        try {
            msg = new Message(-94);
            long now = System.currentTimeMillis();

            for (Skill skill : pl.playerSkill.skills) {

                skill.lastTimeUseThisSkill = 0; // hoặc now - skill.coolDown

                msg.writer().writeShort(skill.skillId);
                msg.writer().writeInt(0);
            }

            pl.sendMessage(msg);
            pl.nPoint.setMp(pl.nPoint.mpMax);
            PlayerService.gI().sendInfoHpMpMoney(pl);
            msg.cleanup();
        } catch (Exception e) {
            Log.error("lỗi service hồi skill");
        }
    }


    public void sendTimeSkill(Player pl) {
        Message msg;
        try {
            msg = new Message(-94);
            for (Skill skill : pl.playerSkill.skills) {
                msg.writer().writeShort(skill.skillId);

                int timeLeft = (int) (skill.lastTimeUseThisSkill + skill.coolDown - System.currentTimeMillis());
                if (timeLeft < 0) {
                    timeLeft = 0;
                }
                msg.writer().writeInt(timeLeft);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void dropItemMap(Zone zone, ItemMap item) {
        Message msg;
        try {
            msg = new Message(68);
            msg.writer().writeShort(item.itemMapId);
            msg.writer().writeShort(item.itemTemplate.id);
            msg.writer().writeShort(item.x);
            msg.writer().writeShort(item.y);
            msg.writer().writeInt((int) item.playerId);//
            if (item.playerId == -2) {
                msg.writer().writeShort(item.range);
            }
            sendMessAllPlayerInMap(zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropItemMapForMe(Player player, ItemMap item) {
        Message msg;
        try {
            msg = new Message(68);
            msg.writer().writeShort(item.itemMapId);
            msg.writer().writeShort(item.itemTemplate.id);
            msg.writer().writeShort(item.x);
            msg.writer().writeShort(item.y);
            msg.writer().writeInt((int) item.playerId);//
            if (item.playerId == -2) {
                msg.writer().writeShort(item.range);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void InfoPetGoc(Player pl) {
        if (pl != null && pl.pet != null) {
            Message msg;
            try {
                msg = new Message(-109);
                msg.writer().writeDouble(pl.pet.nPoint.hpg); //hp
                msg.writer().writeDouble(pl.pet.nPoint.mpg); //hpfull
                msg.writer().writeDouble(pl.pet.nPoint.dameg); //mp
                msg.writer().writeDouble(pl.pet.nPoint.defg); //mpfull
                msg.writer().writeInt(pl.pet.nPoint.critg); //mpfull

                pl.sendMessage(msg);
                msg.cleanup();

            } catch (Exception e) {
                Log.error(Service.class, e);
            }
        }
    }

    public void showInfoPet(Player pl) {
        if (pl != null && pl.pet != null) {
            Message msg;
            try {
                msg = new Message(-107);
                msg.writer().writeByte(2);
                msg.writer().writeShort(pl.pet.getAvatar());
                msg.writer().writeByte(pl.pet.inventory.itemsBody.size());

                for (Item item : pl.pet.inventory.itemsBody) {
                    if (!item.isNotNullItem()) {
                        msg.writer().writeShort(-1);
                    } else {
                        msg.writer().writeShort(item.template.id);
                        msg.writer().writeInt(item.quantity);
                        msg.writer().writeUTF(item.getInfo());
                        msg.writer().writeUTF(item.getContent());

                        List<ItemOption> itemOptions = item.getDisplayOptions();
                        int countOption = itemOptions.size();
                        msg.writer().writeByte(countOption);
                        for (ItemOption iop : itemOptions) {
                            msg.writer().writeByte(iop.optionTemplate.id);
                            msg.writer().writeInt(iop.param);
                        }
                    }
                }

//                msg.writer().writeDouble(pl.pet.nPoint.hpg); //hp
//                msg.writer().writeDouble(pl.pet.nPoint.mpg); //hpfull
//                msg.writer().writeDouble(pl.pet.nPoint.dameg); //mp
//                msg.writer().writeDouble(pl.pet.nPoint.defg); //mpfull
//                msg.writer().writeInt(pl.pet.nPoint.critg); //mpfull
                msg.writer().writeDouble(pl.pet.nPoint.hp); //hp
                msg.writer().writeDouble(pl.pet.nPoint.hpMax); //hpfull
                msg.writer().writeDouble(pl.pet.nPoint.mp); //mp
                msg.writer().writeDouble(pl.pet.nPoint.mpMax); //mpfull
                msg.writer().writeDouble(pl.pet.nPoint.dame); //damefull
                msg.writer().writeUTF(pl.pet.name); //name
                msg.writer().writeUTF(getCurrStrLevel(pl.pet)); //curr level
                msg.writer().writeDouble(pl.pet.nPoint.power); //power
                msg.writer().writeDouble(pl.pet.nPoint.tiemNang); //tiềm năng
                msg.writer().writeByte(pl.pet.getStatus()); //status
                msg.writer().writeShort(pl.pet.nPoint.stamina); //stamina
                msg.writer().writeShort(pl.pet.nPoint.maxStamina); //stamina full
                msg.writer().writeByte(pl.pet.nPoint.crit); //crit
                msg.writer().writeDouble(pl.pet.nPoint.def); //def
                int sizeSkill = pl.pet.playerSkill.skills.size();
                msg.writer().writeByte(4); //counnt pet skill
                for (int i = 0; i < pl.pet.playerSkill.skills.size(); i++) {
                    if (pl.pet.playerSkill.skills.get(i).skillId != -1) {
                        msg.writer().writeShort(pl.pet.playerSkill.skills.get(i).skillId);
                    } else {
                        if (i == 1) {
                            msg.writer().writeShort(-1);
                            msg.writer().writeUTF("Cần đạt sức mạnh 150tr để mở");
                        } else if (i == 2) {
                            msg.writer().writeShort(-1);
                            msg.writer().writeUTF("Cần đạt sức mạnh 1tỷ5 để mở");
                        } else {
                            msg.writer().writeShort(-1);
                            msg.writer().writeUTF("Cần đạt sức mạnh tối thượng\nđể mở");
                        }
                    }
                }

                pl.sendMessage(msg);
                msg.cleanup();

            } catch (Exception e) {
                Log.error(Service.class, e);
            }
        }
    }

    //    public void sendItemTime(Player pl, int itemId, int time) {
//        Message msg;
//        try {
//            msg = new Message(-106);
//            msg.writer().writeShort(itemId);
//            msg.writer().writeShort(time);
//            pl.sendMessage(msg);
//        } catch (Exception e) {
//        }
//    }
//    public void removeItemTime(Player pl, int itemTime) {
//        sendItemTime(pl, itemTime, 0);
//    }
    public void sendSpeedPlayer(Player pl, int speed) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 8);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(speed != -1 ? speed : pl.nPoint.speed);
            pl.sendMessage(msg);
//            Service.getInstance().sendMessAllPlayerInMap(pl.map, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void setPos(Player player, int x, int y) {
        player.location.x = x;
        player.location.y = y;
        Message msg;
        try {
            msg = new Message(123);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeByte(1);
            sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void getPlayerMenu(Player player, int playerId) {
        Message msg;
        try {
            msg = new Message(-79);
            Player pl = player.zone.getPlayerInMap(playerId);
            if (pl != null) {
                msg.writer().writeInt(playerId);
                msg.writer().writeDouble(pl.nPoint.power);
                msg.writer().writeUTF(Service.getInstance().getCurrStrLevel(pl));
                player.sendMessage(msg);
            }
            msg.cleanup();
            if (player.isAdmin()) {
                SubMenuService.gI().showMenuForAdmin(player);
            }
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void subMenuPlayer(Player player) {
        Message msg;
        try {
            msg = messageSubCommand((byte) 63);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("String 1");
            msg.writer().writeUTF("String 2");
            msg.writer().writeShort(550);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hideWaitDialog(Player pl) {
        Message msg;
        try {
            msg = new Message(-99);
            msg.writer().writeByte(-1);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void chatPrivate(Player plChat, Player plReceive, String text) {
        Message msg;
        try {
            msg = new Message(92);
            msg.writer().writeUTF(plChat.name);
            msg.writer().writeUTF("|5|" + text);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeShort(plChat.getHead());
            msg.writer().writeShort(plChat.getBody());
            msg.writer().writeShort(plChat.getFlagBag()); //bag
            msg.writer().writeShort(plChat.getLeg());
            msg.writer().writeByte(1);
            plChat.sendMessage(msg);
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void changePassword(Player player, String oldPass, String newPass, String rePass) {
        if (player.getSession().pp.equals(oldPass)) {
            if (newPass.length() >= 6) {
                if (newPass.equals(rePass)) {
                    player.getSession().pp = newPass;
                    AccountDAO.updateAccount(player.getSession());
                    Service.getInstance().sendThongBao(player, "Đổi mật khẩu thành công!");
                } else {
                    Service.getInstance().sendThongBao(player, "Mật khẩu nhập lại không đúng!");
                }
            } else {
                Service.getInstance().sendThongBao(player, "Mật khẩu ít nhất 6 ký tự!");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Mật khẩu cũ không đúng!");
        }
    }

    public void switchToCreateChar(Session session) {
        Message msg;
        try {
            msg = new Message(2);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendCaption(Session session, byte gender) {
        Message msg;
        try {
            List<Caption> captions = CaptionManager.getInstance().getCaptions();
            msg = new Message(-41);
            msg.writer().writeByte(captions.size());
            for (Caption caption : captions) {
                msg.writer().writeUTF(caption.getCaption(gender));
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendHavePet(Player player) {
        Message msg;
        try {
            msg = new Message(-107);
            msg.writer().writeByte(player.pet == null ? 0 : 1);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendWaitToLogin(Session session, int secondsWait) {
        Message msg;
        try {
            msg = new Message(122);
            msg.writer().writeShort(secondsWait);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void sendMessage(Session session, int cmd, String path) {
        Message msg;
        try {
            msg = new Message(cmd);
            msg.writer().write(FileIO.readFile(path));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendTopRank(Player pl) {
        Message msg;
        try {
            msg = new Message(Cmd.THELUC);
            msg.writer().writeInt(1);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createItemMap(Player player, int tempId) {
        ItemMap itemMap = new ItemMap(player.zone, tempId, 1, player.location.x, player.location.y, player.id);
        dropItemMap(player.zone, itemMap);
    }

    public void sendNangDong(Player player) {
        Message msg;
        try {
            msg = new Message(-97);
            msg.writer().writeInt(100);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendPowerInfo(Player pl, String info, short point) {
        Message m = null;
        try {
            m = new Message(-115);
            m.writer().writeUTF(info);
            m.writer().writeShort(point);
            m.writer().writeShort(20);
            m.writer().writeShort(10);
            m.writer().flush();
            if (pl != null && pl.getSession() != null) {
                pl.sendMessage(m);
            }
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public void setMabuHold(Player pl, byte type) {
        Message m = null;
        try {
            m = new Message(52);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public void sendPercentMabuEgg(Player player, byte percent) {
        try {
            Message msg = new Message(-117);
            msg.writer().writeByte(percent);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendPlayerInfo(Player player) {
        try {
            Message msg = messageSubCommand((byte) 7);
            msg.writer().writeInt((int) player.id);
            if (player.clan != null) {
                msg.writer().writeInt(player.clan.id);
            } else {
                msg.writer().writeInt(-1);
            }
            int level = CaptionManager.getInstance().getLevel(player);
            level = player.isInvisible ? 0 : level;
            msg.writer().writeByte(level);
            msg.writer().writeBoolean(player.isInvisible);
            msg.writer().writeByte(player.typePk);
            msg.writer().writeByte(player.gender);
            msg.writer().writeByte(player.gender);
            msg.writer().writeShort(player.getHead());
            msg.writer().writeUTF(player.name);
            msg.writer().writeDouble(player.nPoint.hp);
            msg.writer().writeDouble(player.nPoint.hpMax);
            msg.writer().writeShort(player.getBody());
            msg.writer().writeShort(player.getLeg());
            msg.writer().writeByte(player.getFlagBag());
            msg.writer().writeByte(-1);
            msg.writer().writeShort(player.location.x);
            msg.writer().writeShort(player.location.y);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(0);

//            msg.writer().writeShort(0);
//            msg.writer().writeByte(0);
//            msg.writer().writeShort(0);
            sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCurrLevel(Player pl) {

    }

    public int getWidthHeightImgPetFollow(int id) {
        if (id == 15067) {
            return 65;
        }
        return 75;
    }

    public void showTopPower(Player player, int top) {
        List<Player> list = null;
        switch (top) {
            case 1:
                list = TopManager.getInstance().getListSm();
                break;
            case 2:
                list = TopManager.getInstance().getListDetu();
                break;
            case 3:
                list = TopManager.getInstance().getListNvu();
                break;
            case 4:
                list = TopManager.getInstance().getListNap();
                break;
        }
        Message msg = new Message(Cmd.TOP);
        try {
            msg.writer().writeByte(0);
            msg.writer().writeUTF(top == TOP_SUCMANH ? "Top Sức Mạnh" : top == TOP_DETU ? "Top Đệ tử" : top == TOP_NHIEMVU ? "Top Nhiệm vụ" : "Top Donate");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                Player pl = list.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (player.isVersionAbove(220)) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                if (top == TOP_DETU && pl.pet != null) {
                    msg.writer().writeUTF("Loại: " + (pl.pet.typePet == 0 ? "Đệ tử Thường" : pl.pet.typePet == 1 ? "Mabu" : pl.pet.typePet == 2 ? "Berus" : pl.pet.typePet == 3 ? "Zeno"
                            : pl.pet.typePet == 4 ? "Kaido" : pl.pet.typePet == 5 ? "Itachi" : pl.pet.typePet == 6 ? "Tiên hắc ám" : pl.pet.typePet == 7 ? "Ngộ Không" : "Long Đế"));
                } else {
                    msg.writer().writeUTF(Client.gI().getPlayer(pl.id) != null ? "Online" : "Chưa Online");
                }
                msg.writer().writeUTF(top == TOP_SUCMANH ? "Sức mạnh: " + Util.powerToString(pl.nPoint.power) + "\n Chuyển sinh: " + pl.chuyensinh
                        : top == TOP_DETU ? "SM Đệ: " + (pl.pet != null ? Util.format(pl.pet.nPoint.power) : "Chưa có Đệ tử")
                                : top == TOP_NHIEMVU ? "Nhiệm vụ: " + pl.playerTask.taskMain.name
                                        : "Đã nạp: " + Util.format(pl.tongnap));
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTopSieuHang(Player player) {
        List<Player> list = null;
        list = TopManager.getInstance().getListSieuHang();
        Message msg = new Message(Cmd.TOP);
        try {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Siêu hạng");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                Player pl = list.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (player.isVersionAbove(220)) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(pl.rankSieuHang == 1 ? ("+500Tr Vàng/ngày") : (pl.rankSieuHang > 1 && pl.rankSieuHang < 10) ? ("+50Tr Vàng/ngày") : "");
                msg.writer().writeUTF("HP: " + Util.format(pl.nPoint.hpMax)
                        + "\n" + "KI: " + Util.format(pl.nPoint.mpMax)
                        + "\n" + "Sức Đánh: " + Util.format(pl.nPoint.dame)
                        + "\n" + "Hạng: " + pl.rankSieuHang);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNotBienhinh(Player player) {
        Message msg;
        try {
            msg = new Message(-90);
            msg.writer().writeByte(-1);
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void Mabu14hAttack(Boss mabu, Player plAttack, int x, int y, byte skillId) {
        mabu.isUseSpeacialSkill = true;
        mabu.lastTimeUseSpeacialSkill = System.currentTimeMillis();
        try {
            Message msg = new Message(51);
            msg.writer().writeInt((int) mabu.id);
            msg.writer().writeByte(skillId);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            if (skillId == 1) {
                msg.writer().writeByte(1);
                double dame = Util.DoubleGioihan(plAttack.injured(mabu, (int) (mabu.nPoint.getDameAttack(false) * (skillId == 1 ? 1.5 : 1)), false, false));
                msg.writer().writeInt((int) plAttack.id);
                msg.writer().writeDouble(dame);
            } else if (skillId == 0) {
                List<Player> listAttack = mabu.getListPlayerAttack(70);
                msg.writer().writeByte(listAttack.size());
                for (int i = 0; i < listAttack.size(); i++) {
                    Player pl = listAttack.get(i);
                    double dame = pl.injured(mabu, mabu.nPoint.getDameAttack(false), false, false);
                    msg.writer().writeInt((int) pl.id);
                    msg.writer().writeDouble(Util.DoubleGioihan(dame));
                }
                listAttack.clear();
            }
            sendMessAllPlayerInMap(mabu.zone, msg);
            mabu.isUseSpeacialSkill = false;
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendMabuEat(Player plHold, short... point) {
        Message msg;
        try {
            msg = new Message(52);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) plHold.id);
            msg.writer().writeShort(point[0]);
            msg.writer().writeShort(point[1]);
            sendMessAllPlayerInMap(plHold.zone, msg);
            plHold.location.x = point[0];
            plHold.location.y = point[1];
            MapService.gI().sendPlayerMove(plHold);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void removeMabuEat(Player plHold) {
        PlayerService.gI().changeAndSendTypePK(plHold, ConstPlayer.NON_PK);
        plHold.effectSkill.isHoldMabu = false;
        plHold.effectSkill.isTaskHoldMabu = -1;
        Message msg;
        try {
            msg = new Message(52);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) plHold.id);
            sendMessAllPlayerInMap(plHold.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void eatPlayer(Boss mabu, Player plHold) {
        mabu.isUseSpeacialSkill = true;
        mabu.lastTimeUseSpeacialSkill = System.currentTimeMillis();
        plHold.effectSkill.isTaskHoldMabu = 1;
        plHold.effectSkill.lastTimeHoldMabu = System.currentTimeMillis();
        try {
            Message msg = new Message(52);
            msg.writer().writeByte(2);
            msg.writer().writeInt((int) mabu.id);
            msg.writer().writeInt((int) plHold.id);
            sendMessAllPlayerInMap(mabu.zone, msg);
            mabu.isUseSpeacialSkill = false;
            msg.cleanup();
        } catch (IOException e) {
        }
    }
}
