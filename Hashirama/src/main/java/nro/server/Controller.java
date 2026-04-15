package nro.server;

import nro.consts.*;
import nro.data.DataGame;
import nro.data.ItemData;
import nro.jdbc.DBService;
import nro.models.consignment.ConsignmentShop;
import nro.models.map.war.BlackBallWar;
import nro.models.npc.NpcManager;
import nro.models.player.Player;
import nro.models.skill.PlayerSkill;
import nro.noti.NotiManager;
import nro.resources.Resources;
import nro.server.io.Message;
import nro.server.io.Session;
import nro.services.*;
import nro.services.func.*;
import nro.utils.Log;
import nro.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import nro.jdbc.daos.AccountDAO;
import nro.models.boss.Boss;
import nro.models.boss.BossManager;
import nro.models.item.Item;
import nro.models.skill.Skill;
import nro.sendEff.SendEffect;

public class Controller {

    private static Controller instance;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }
    public static List<Integer> list_effect = Arrays.asList(79, 80, 81, 82, 83, 84, 85,
            86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96,
            97, 98, 99, 75, 74, 200, 201, 202, 203, 204, 205, 206, 207,
            208, 209, 210, 211, 212, 213, 214, 215, 216,
            230, 231, 232, 233, 234, 235, 236, 237, 225);

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);    
    private static final String CLIENT_SHARED_SECRET = "NRO_HASHIRAMA_NEW_14_1_2026";// Helper: hex -> bytes

    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public void onMessage(Session _session, Message _msg) {
        long st = System.currentTimeMillis();
        try {
            Player player = _session.player;
            byte cmd = _msg.command;
            if (Manager.debug) {
                System.out.println("CMD receive: " + cmd);
            }
            switch (cmd) {
                case Cmd.KIGUI:
                    ConsignmentShop.getInstance().handler(player, _msg);
                    break;
                case Cmd.ACHIEVEMENT:
                    TaskService.gI().rewardAchivement(player, _msg.reader().readByte());
                    break;
                case Cmd.RADA_CARD:
                    RadaService.getInstance().controller(player, _msg);
                    break;
                case -127:
                    if (player != null) {
                        LuckyRoundService.gI().readOpenBall(player, _msg);
                    }
                    break;
                case -125:
                    if (player != null) {
                        Input.gI().doInput(player, _msg);
                    }
                    break;
                case 112:
                    if (player != null) {
                        IntrinsicService.gI().showMenu(player);
                    }
                    break;
                case -34:
                    if (player != null) {
                        switch (_msg.reader().readByte()) {
                            case 1:
                                player.magicTree.openMenuTree();
                                break;
                            case 2:
                                player.magicTree.loadMagicTree();
                                break;
                        }
                    }
                    break;
                case -99:
                    if (player != null) {
                        FriendAndEnemyService.gI().controllerEnemy(player, _msg);
                    }
                    break;
                case 18:
                    if (player != null) {
                        FriendAndEnemyService.gI().goToPlayerWithYardrat(player, _msg);
                    }
                    break;
                case -72:
                    if (player != null) {
                        FriendAndEnemyService.gI().chatPrivate(player, _msg);
                    }
                    break;
                case -80:
                    if (player != null) {
                        FriendAndEnemyService.gI().controllerFriend(player, _msg);
                    }
                    break;
                case -59:
                    if (player != null) {
                        PVPServcice.gI().controller(player, _msg);
                    }
                    break;
                case -86:
                    if (player != null) {
                        TransactionService.gI().controller(player, _msg);
                    }
                    break;
                case -107:
                    if (player != null) {
                        Service.getInstance().showInfoPet(player);
                    }
                    break;
                case -109:
                    if (player != null && player.pet != null) {
                        Service.getInstance().InfoPetGoc(player);
                    }
                    break;
                case 103:
                    if (player != null) {
                        PhucLoi.gI().Send_PhucLoi(player);
                    }
                    break;
                case 104:
                    if (player != null) {
                        BangTin.gI().Send_BangTin(player);
                    }
                    break;
                case 105:
                    if (player != null) {
                        int id = _msg.reader().readInt();
                        PhucLoi.gI().Active_PhucLoi(player, id);
                    }
                    break;
                case 106:
                    if (player != null) {
                        TamBao.gI().Send_MocTamBao(player);
                    }
                    break;
                case 107:
                    if (player != null) {
                        byte active = _msg.reader().readByte();
                        if (active == 0) {
                            int id = _msg.reader().readInt();
                            TamBao.gI().Active_TamBao(player, id);
                        } else {
                            int solan = _msg.reader().readInt();
                            TamBao.gI().QuayTamBao(player, solan);
                        }
                    }
                    break;
                case 108:
                    if (player != null) {
                        byte active = _msg.reader().readByte();
                        byte atv = _msg.reader().readByte();
                        if (active == 0) {
                            KhamNgoc.gI().Send_KhamNgocTemplate(player);
                        } else if (active == 1) {
                            KhamNgoc.gI().Send_KhamNgoc_Player(player);
                        } else if (active == 2) {
                            KhamNgoc.gI().activeKhamNgoc(player, atv);
                        } else if (active == 3) {
                            KhamNgoc.gI().NangCapKhamNgoc(player, atv);
                        }
                    }
                    break;
                case 109:
                    if (player != null) {
                        byte active = _msg.reader().readByte();
                        if (active == 0) {
                            RuongSuuTam.gI().Send_RuongSuuTamTemplate(player);
                        } else if (active == 1) {
                            RuongSuuTam.gI().Send_RuongCaiTrang(player);
                        } else if (active == 2) {
                            RuongSuuTam.gI().Send_RuongPhuKien(player);
                        } else if (active == 3) {
                            RuongSuuTam.gI().Send_RuongPet(player);
                        } else if (active == 4) {
                            RuongSuuTam.gI().Send_RuongLinhThu(player);
                        } else if (active == 5) {
                            RuongSuuTam.gI().Send_RuongThuCuoi(player);
                        } else if (active == 6) {
                            byte acttt = _msg.reader().readByte();
                            byte type = _msg.reader().readByte();
                            int id = _msg.reader().readInt();
                            if (acttt == 0) {
                                RuongSuuTam.gI().mangItem(player, type, id);
                            } else if (acttt == 1) {
                                RuongSuuTam.gI().thaoItem(player, type, id);
                            } else if (acttt == 2) {
                                RuongSuuTam.gI().moRongRuong(player, type);
                            } else {
                                RuongSuuTam.gI().activeRuongSuuTam(player, type);
                            }
                        }
                    }
                    break;
                case 110:
                    if (player != null) {
                        byte active = _msg.reader().readByte();
                        if (active == 0) {
                            PhongThiNghiem.gI().Send_PhongThiNghiem_Template(player);
                        } else if (active == 1) {
                            PhongThiNghiem.gI().Send_PhongThiNghiem_Player(player);
                        } else if (active == 2) {
                            int vitri = _msg.reader().readInt();
                            int type = _msg.reader().readInt();
                            PhongThiNghiem.gI().dieu_che(player, vitri, type);
                        } else if (active == 3) {
                            int id = _msg.reader().readInt();
                            int vitri = _msg.reader().readInt();
                            PhongThiNghiem.gI().nhan_item(player, id, vitri);
                        } else if (active == 4) {
                            PhongThiNghiem.gI().mo_rong(player);
                        } else if (active == 5) {
                            int id = _msg.reader().readInt();
                            int vitri = _msg.reader().readInt();
                            PhongThiNghiem.gI().tangTocPtn(player, id, vitri);
                        } else if (active == 6) {
                            int id = _msg.reader().readInt();
                            int vitri = _msg.reader().readInt();
                            PhongThiNghiem.gI().huyPtn(player, id, vitri);
                        }
                    }
                    break;
                case 111:
                    if (player != null) {
                        byte active = _msg.reader().readByte();
                        if (active == 0) {
                            GameDuDoan.gI().Send_TaiXiu(player);
                        } else if (active == 1) {
                            GameDuDoan.gI().DatTai(player);
                        } else if (active == 2) {
                            GameDuDoan.gI().DatXiu(player);
                        } else if (active == 3) {
                            GameDuDoan.gI().chat(player);
                        }
                    }
                    break;
//                case -82:
//                    System.out.println("run bot");
//                    Client.gI().createBot(_session);
//                    break;
//                case -83:
//                    Client.gI().clear();
//                    break;
                case -108:
                    if (player != null && player.pet != null) {
                        player.pet.changeStatus(_msg.reader().readByte());
                    }
                    break;
                case 6: //buy item

                    if (player != null) {
                        byte typeBuy = _msg.reader().readByte();
                        int tempId = _msg.reader().readShort();
                        int quantity = 0;
                        try {
                            quantity = _msg.reader().readShort();
                        } catch (Exception e) {
                        }
                        ShopService.gI().buyItem(player, typeBuy, tempId);
                    }
                    break;
                case 7: //sell item
                    if (player != null) {
                        int action = _msg.reader().readByte();
                        int where = _msg.reader().readByte();
                        int index = _msg.reader().readShort();
                        if (action == 0) {
                            ShopService.gI().showConfirmSellItem(player, where,
                                    !player.isVersionAbove(220) ? index - 3 : index);
                        } else {
                            ShopService.gI().sellItem(player, where, index);
                        }
                    }
                    break;
                case 29:
                    if (player != null) {
                        if (player.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
                            return;
                        }
                        Service.getInstance().openZoneUI(player);
                    }
                    break;
                case 21:
                    if (player != null) {
                        if (player.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
                            Service.getInstance().sendPopUpMultiLine(player, 0, 7184, "Không thể thực hiện");
                            return;
                        }
                        int zoneId = _msg.reader().readByte();
                        ChangeMapService.gI().changeZone(player, zoneId);
                    }
                    break;
                case -71:
                    if (player != null) {
                        ChatGlobalService.gI().chat(player, _msg.reader().readUTF());
                    }
                    break;
                case -79:
                    if (player != null) {
                        Service.getInstance().getPlayerMenu(player, _msg.reader().readInt());
                    }
                    break;
                case -113:
                    if (player != null) {
                        PlayerSkill playerSkill = player.playerSkill;
                        int len = _msg.reader().available();
                        for (int i = 0; i < len; i++) {
                            byte b = _msg.reader().readByte();
                            playerSkill.skillShortCut[i] = b;
                        }
                        playerSkill.sendSkillShortCut();
                    }
                    break;
                case -101:
                    login2(_session, _msg);
                    break;
                case -118:
                    if (player != null) {
                        if (player.zone.map.mapId == 113) {
                            int pId = _msg.reader().readInt();
                        } else {
                            int id = _msg.reader().readInt();
                            Item maydo = InventoryService.gI().findItemBagByTemp(player, 1296);
                            for (Boss bosse : BossManager.gI().getBosses()) {
                                if (id != -1 && bosse != null && bosse.id == id && !bosse.isDie() && bosse.zone != null) {
                                    if (UseItem.gI().maydoboss(player) == true && maydo != null) {
                                        ChangeMapService.gI().changeMapInYard(player, bosse.zone, bosse.location.x);
                                        InventoryService.gI().subQuantityItemsBag(player, maydo, 1);
                                        InventoryService.gI().sendItemBags(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "|7|Yêu cầu có Máy dò Boss");
                                    }
                                    break;
                                } else if (id != -1 && (bosse == null || bosse.isDie() || bosse.zone == null)) {
                                    Service.getInstance().sendThongBao(player, "|7|Chưa được đâu");
                                } else {

                                }
                            }
                        }
                    }
                    break;
                case -103:
                    if (player != null) {
                        byte act = _msg.reader().readByte();
                        if (act == 0) {
                            Service.getInstance().openFlagUI(player);
                        } else if (act == 1) {
                            Service.getInstance().chooseFlag(player, _msg.reader().readByte());
                        } else {
//                        Util.log("id map" + player.map.id);
                        }
                    }
                    break;
                case -7:
                    if (player != null) {
                        int toX = player.location.x;
                        int toY = player.location.y;
                        try {
                            byte b = _msg.reader().readByte();
                            toX = _msg.reader().readShort();
                            toY = _msg.reader().readShort();
                        } catch (Exception e) {
                        }
                        PlayerService.gI().playerMove(player, toX, toY);
                    }
                    break;
                case Cmd.GET_IMAGE_SOURCE:
                    // System.out.println("-74");
                    Resources.getInstance().downloadResources(_session, _msg);
                    break;
                case -81:
                    if (player != null) {
                        _msg.reader().readByte();
                        int[] indexItem = new int[_msg.reader().readByte()];
                        for (int i = 0; i < indexItem.length; i++) {
                            indexItem[i] = _msg.reader().readByte();
                        }
//                    CombineService.gI().showInfoCombine(player, indexItem);
                        CombineServiceNew.gI().showInfoCombine(player, indexItem);
                    }
                    break;
                case -87:
                    DataGame.updateData(_session);
                    break;
                case Cmd.FINISH_UPDATE:
                    _session.finishUpdate();
                    break;
                case Cmd.REQUEST_ICON:
                    int id = _msg.reader().readInt();
                    Resources.getInstance().downloadIconData(_session, id);
                    break;
                case Cmd.GET_IMG_BY_NAME:
                    Resources.getInstance().downloadIBN(_session, _msg.reader().readUTF());
                    break;
                case -66:
                    int effId = _msg.reader().readShort();
                    int idT = effId;
                    if (list_effect.contains(idT)) {
                        Resources.effData(_session, effId, idT);
                    } else {
                        Resources.getInstance().loadEffectData(_session, effId);
                    }
                    break;

                case -62:
                    if (player != null) {
                        FlagBagService.gI().sendIconFlagChoose(player, _msg.reader().readByte());
                    }
                    break;
                case -63:
                    if (player != null) {
                        byte flagbagId = _msg.reader().readByte();
                        int flagbagIdInt = flagbagId & 0xFF; //chuyển sang byte không dấu
                        FlagBagService.gI().sendIconEffectFlag(player, flagbagIdInt);
                    }
                    break;
                case Cmd.BACKGROUND_TEMPLATE:
                    int bgId = _msg.reader().readShort();
                    Resources.getInstance().downloadBGTemplate(_session, bgId);
                    break;
                case 22:
                    if (player != null) {
                        _msg.reader().readByte();
                        NpcManager.getNpc(ConstNpc.DAU_THAN).confirmMenu(player, _msg.reader().readByte());
                    }
                    break;
                case -33:
                case -23:
                    if (player != null) {
                        player.zone.changeMapWaypoint(player);
                        Service.getInstance().hideWaitDialog(player);
                    }
                    break;
                case -45:
                    if (player != null) {
                        SkillService.gI().useSkill(player, null, null, _msg);
                    }
                    break;
                case -46:
                    if (player != null) {
                        ClanService.gI().getClan(player, _msg);
                    }
                    break;
                case -51:
                    if (player != null) {
                        ClanService.gI().clanMessage(player, _msg);
                    }
                    break;
                case -54:
                    if (player != null) {
                        ClanService.gI().clanDonate(player, _msg);
//                        Service.getInstance().sendThongBao(player, "Can not invoke clan donate");
                    }
                    break;
                case -49:
                    if (player != null) {
                        ClanService.gI().joinClan(player, _msg);
                    }
                    break;
                case -50:
                    if (player != null) {
                        ClanService.gI().sendListMemberClan(player, _msg.reader().readInt());
                    }
                    break;
                case -56:
                    if (player != null) {
                        ClanService.gI().clanRemote(player, _msg);
                    }
                    break;
                case -47:
                    if (player != null) {
                        ClanService.gI().sendListClan(player, _msg.reader().readUTF());
                    }
                    break;
                case -55:
                    if (player != null) {
                        ClanService.gI().showMenuLeaveClan(player);
                    }
                    break;
                case -57:
                    if (player != null) {
                        ClanService.gI().clanInvite(player, _msg);
                    }
                    break;
                case -40:
                    UseItem.gI().getItem(_session, _msg);
                    break;
                case -41:
                    Service.getInstance().sendCaption(_session, _msg.reader().readByte());
                    break;
                case -43:
                    if (player != null) {
                        UseItem.gI().doItem(player, _msg);
                    }
                    break;
                case -91:
                    if (player != null) {
                        switch (player.iDMark.getTypeChangeMap()) {
                            case ConstMap.CHANGE_CAPSULE:
                                UseItem.gI().choseMapCapsule(player, _msg.reader().readByte());
                                break;
                            case ConstMap.CHANGE_BLACK_BALL:
                                BlackBallWar.gI().changeMap(player, _msg.reader().readByte());
                                break;
                        }
                    }
                    break;
                case -39:
                    if (player != null) {
                        //finishLoadMap
                        ChangeMapService.gI().finishLoadMap(player);
                        if (player.zone.map.mapId == (21 + player.gender)) {
                            if (player.mabuEgg != null) {
                                player.mabuEgg.sendMabuEgg();
                            }
//                            Logger.log(Logger.PURPLE, "done load map nhà!\n");
                        }
                        EffectMapService.gI().sendEffEvent(player);
                    }
                    break;
                case 11:
                    byte modId = _msg.reader().readByte();
                    if (modId == 85 || modId == 88 || modId == 89 || modId == 94 || modId == 95 || modId == 96 || modId == 97 || modId == 98
                            || modId == 99 || modId == 100 || modId == 101 || modId == 102 || modId == 103 || modId == 104 || modId == 105 || modId == 106) {
                        Resources.getInstance().requestMobTemplate(_session, modId);
                    } else {
                        Resources.getInstance().loadMoData(_session, modId);
                    }
                    break;
                case 44:
                    if (player != null) {
                        String text = _msg.reader().readUTF();
                        Service.getInstance().chat(player, text);
                    }
                    break;
                case 32:
                    if (player != null) {
                        int npcId = _msg.reader().readShort();
                        int select = _msg.reader().readByte();
                        MenuController.getInstance().doSelectMenu(player, npcId, select);
                    }
                    break;
                case 33:
                    if (player != null) {
                        int npcId = _msg.reader().readShort();
                        MenuController.getInstance().openMenuNPC(_session, npcId, player);
                    }
                    break;
                case 34:
                    if (player != null) {
                        int selectSkill = _msg.reader().readShort();
                        SkillService.gI().selectSkill(player, selectSkill);
                    }
                    break;
                case 54:
                    if (player != null) {
                        Service.getInstance().attackMob(player, (int) (_msg.reader().readByte()));
                    }
                    break;
                case -60:
                    if (player != null) {
                        int playerId = _msg.reader().readInt();
                        Service.getInstance().attackPlayer(player, playerId);
                    }
                    break;
                case -27:
                    _session.sendSessionKey();
                    break;
                case -111:
                    System.out.println("send image version");
                    DataGame.sendDataImageVersion(_session);
                    break;
                case -20:
                    if (player != null && !player.isDie()) {
                        int itemMapId = _msg.reader().readShort();
                        ItemMapService.gI().pickItem(player, itemMapId, false);
                    }
                    break;
                case -28:
                    messageNotMap(_session, _msg);
                    break;
                case -29:
                    messageNotLogin(_session, _msg);
                    break;
                case -30:
                    messageSubCommand(_session, _msg);
                    break;
                case -15: // về nhà
                    if (player != null) {
                        player.isGoHome = true;
                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                        player.isGoHome = false;
                    }
                    break;
                case -16: // hồi sinh
                    if (player != null) {
                        PlayerService.gI().hoiSinh(player);
                    }
                    break;
                default:
//                    Util.log("CMD: " + cmd);
                    break;
            }
            if (_session.logCheck) {
//                System.out.println("Time do controller (" + cmd + "): " + (System.currentTimeMillis() - st) + " ms");
            }
        } catch (Exception e) {
            logger.error("Err controller message command: " + _msg.command, e);
//            Log.logException(Controller.class, e);
//            Log.warning("Lỗi controller message command: " + _msg.command);
        }
    }

    public void messageNotLogin(Session session, Message msg) {
        if (msg != null) {
            try {
                byte cmd = msg.reader().readByte();
                switch (cmd) {
                    case 0:
                        session.login(msg.reader().readUTF(), msg.reader().readUTF());
                        break;
                    case 2:
                        session.setClientType(msg);
                        break;
                // case 7: { // CLIENT_VERIFY: nonce + hmac
                //     String nonceHex = msg.reader().readUTF(); // nonce 16 bytes -> hex
                //     String macHex = msg.reader().readUTF(); // hmac 32 bytes -> hex

                //     try {
                //         byte[] nonce = hexToBytes(nonceHex);
                //         byte[] mac = hexToBytes(macHex);

                //         Mac h = Mac.getInstance("HmacSHA256");
                //         SecretKeySpec k = new SecretKeySpec(
                //                 CLIENT_SHARED_SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                //                 "HmacSHA256");
                //         h.init(k);
                //         h.update(nonce);
                //         byte[] expect = h.doFinal();

                //         boolean ok = java.util.Arrays.equals(expect, mac);
                //         if (!ok) {
                //             Service.getInstance().sendThongBaoOK(session,
                //                     "Phiên bản không hợp lệ. Vui lòng phiên bản mới tại trang chủ nrohashirama.online");
                //             session.disconnect();
                //         } else {
                //             session.clientVerified = true;
                //         }
                //     } catch (Exception ex) {
                //         Service.getInstance().sendThongBaoOK(session,
                //                 "Phiên bản không hợp lệ. Vui lòng phiên bản mới tại trang chủ nrohashirama.online");
                //         session.disconnect();
                //     }
                //     break;
                // }
                    default:
                        break;

                }
            } catch (IOException e) {
                Log.error(Controller.class,
                        e);
            }
        }
    }
 
    private static void sendPreloginCase5(Session session, boolean ok, String note) {
        try {
            Message rep = new Message((byte) -29);
            rep.writer().writeByte(5);                 // sub-code 5 (client đang đọc ở case 5)
            rep.writer().writeBoolean(ok);             // kết quả
            rep.writer().writeUTF(note == null ? "" : note); // ghi chú/ thông báo
            session.sendMessage(rep);
            rep.cleanup();
        } catch (Exception ex) {
            // Fallback: vẫn đảm bảo người chơi thấy thông báo
            Service.getInstance().sendThongBaoOK(session,
                note != null ? note : (ok ? "Đăng ký thành công! Vui lòng đăng nhập." : "Đăng ký thất bại."));
        }
    }
// ================== Đăng ký + Captcha (dùng sub=5 để phản hồi) ==================
    private void handleRegisterWithCaptcha(Session session, String user, String pass, String answer, String token) {
        try {
            // 1) Kiểm tra cơ bản
            if (user == null || user.length() < 5) {
                sendPreloginCase5(session, false, "Tài khoản ≥ 5 ký tự");
                return;
            }
            if (pass == null || pass.length() < 6) {
                sendPreloginCase5(session, false, "Mật khẩu ≥ 6 ký tự");
                return;
            }

            // (tuỳ chọn) ràng buộc ký tự hợp lệ
            // if (!user.matches("^[a-zA-Z0-9_\\.]{5,20}$")) {
            //     sendPreloginCase5(session, false, "Tài khoản chỉ gồm a-z, 0-9, ., _ (5-20 ký tự)");
            //     return;
            // }
            // 2) Xác thực captcha
            if (!CaptchaService.verify(token, answer, session.ipAddress)) {
                sendPreloginCase5(session, false, "Sai mã xác minh hoặc đã hết hạn.");
                return;
            }

            // 3) Tạo tài khoản (nên hash pass trong DAO)
            //    Ví dụ: int accId = AccountDAO.createAccount(user, PasswordHasher.hash(pass));
            int accId = AccountDAO.createAccount(user, pass);
            if (accId == -1) {
                sendPreloginCase5(session, false, "Tài khoản đã tồn tại.");
                return;
            }

            // 4) Thành công -> client tự quay về Login (theo case 5 ở client)
            sendPreloginCase5(session, true, "Đăng ký thành công! Vui lòng đăng nhập.");
        } catch (Exception e) {
            Log.error(Controller.class, e);
            sendPreloginCase5(session, false, "Có lỗi khi đăng ký, thử lại!");
        }
    }
        
    public void messageNotMap(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = _session.player;
                byte cmd = _msg.reader().readByte();
//                System.out.println("CMD receive -28 / " + cmd);
                switch (cmd) {
                    case 2:
                        createChar(_session, _msg);
                        break;
                    case 6:
                        DataGame.createMap(_session);
                        break;
                    case 7:
                        DataGame.updateSkill(_session);
                        break;
                    case 8:
                        ItemData.updateItem(_session);
                        break;
                    case 10:
                        DataGame.sendMapTemp(_session, _msg.reader().readUnsignedByte());
                        break;
                    case 13:
                        //client ok
                        if (player != null) {
                            Service.getInstance().player(player);
                            Service.getInstance().Send_Caitrang(player);
//                            player.zone.load_Another_To_Me(player);

                            // -64 my flag bag
                            Service.getInstance().sendFlagBag(player);

                            // -113 skill shortcut
                            player.playerSkill.sendSkillShortCut();
                            // item time
                            ItemTimeService.gI().sendAllItemTime(player);

                            // send current task
                            TaskService.gI().sendInfoCurrentTask(player);
                        }
                        break;
                    default:
                        break;

                }
            } catch (IOException e) {
                Log.error(Controller.class,
                        e);
            }
        }
    }

    public void messageSubCommand(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = _session.player;
                byte command = _msg.reader().readByte();
                switch (command) {
                    case 17: {
                        byte typee = _msg.reader().readByte();
                        short pointt = _msg.reader().readShort();
                        if (player != null && player.pet != null && player.pet.nPoint != null) {
                            player.pet.nPoint.increasePoint(typee, pointt);
                        }
                        break;
                    }
                    case 16:
                        byte type = _msg.reader().readByte();
                        short point = _msg.reader().readShort();
                        if (player != null && player.nPoint != null) {
                            player.nPoint.increasePoint(type, point);
                        }
                        break;
                    case 64:
                        int playerId = _msg.reader().readInt();
                        int menuId = _msg.reader().readShort();
                        SubMenuService.gI().controller(player, playerId, menuId);
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    public void createChar(Session session, Message msg) {
        if (Maintenance.isRuning) {
            return;
        }

        boolean created = false;

        try {
            String name = msg.reader().readUTF();
            int gender = msg.reader().readByte();
            int hair = msg.reader().readByte();

            // Check độ dài tên
            if (name.length() < 5 || name.length() > 10) {
                Service.getInstance().sendThongBaoOK(session, "Tên nhân vật tối thiểu 5 kí tự và tối đa 10 ký tự");
                return;
            }

            // Check ký tự đặc biệt
            if (Util.haveSpecialCharacter(name)) {
                Service.getInstance().sendThongBaoOK(session, "Tên nhân vật không được chứa ký tự đặc biệt");
                return;
            }

            // Check tên trong IGNORE_NAME
            for (String n : ConstIgnoreName.IGNORE_NAME) {
                if (name.equalsIgnoreCase(n)) {
                    Service.getInstance().sendThongBaoOK(session, "Tên nhân vật đã tồn tại");
                    return;
                }
            }

            final String sqlCheck
                    = "SELECT 1 FROM player WHERE name = ? OR account_id = ? LIMIT 1";

            try (Connection con = DBService.gI().getConnectionForSaveData(); PreparedStatement ps = con.prepareStatement(sqlCheck)) {

                ps.setString(1, name);
                ps.setInt(2, session.userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Service.getInstance().sendThongBaoOK(session, "Tên nhân vật đã tồn tại hoặc tài khoản đã có nhân vật");
                        return;
                    }
                }

                // Không trùng => tạo player mới
                created = PlayerService.gI().createPlayer(con,
                        session.userId,
                        name.toLowerCase(), // bạn đang dùng toLowerCase nên mình giữ nguyên
                        (byte) gender,
                        hair);
            }

        } catch (Exception e) {
            Log.error(Controller.class, e, "Lỗi createChar");
        }

        if (created) {
            session.finishUpdate();
        }
    }


    public void login2(Session session, Message msg) {
        Service.getInstance().sendThongBaoOK(session, "Vui lòng đăng ký tài khoản tại https://nrofree2025.online/");
    }

    public void sendInfo(Session session) {
        Player player = session.player;

        DataGame.sendDataItemBG(session);
        // -82 set tile map
        DataGame.sendTileSetInfo(session);

        // 112 my info intrinsic
        IntrinsicService.gI().sendInfoIntrinsic(player);

        // -42 my point
        Service.getInstance().point(player);

        // 40 task
        TaskService.gI().sendTaskMain(player);

        // -22 reset all
        Service.getInstance().clearMap(player);

        // -53 my clan
        ClanService.gI().sendMyClan(player);

        // -69 max statima
        PlayerService.gI().sendMaxStamina(player);

        // -68 cur statima
        PlayerService.gI().sendCurrentStamina(player);

        // -97 năng động
        // -107 have pet
        Service.getInstance().sendHavePet(player);

        // -119 top rank
        Service.getInstance().sendTopRank(player);

        // -50 thông tin bảng thông báo
        // -24 join map - map info
        player.zone.load_Me_To_Another(player);
        player.zone.mapInfo(player);

        // -70 thông báo bigmessage
        //check activation set
        player.setClothes.setup();
        if (player.pet != null) {
            player.pet.setClothes.setup();
        }

        if (!player.isBoss && !player.isMiniPet) {
            if (player.pet != null && player.inventory.itemsBody.get(5).isNotNullItem() && player.pet.inventory.itemsBody.get(5).isNotNullItem()) {
                if ((player.inventory.itemsBody.get(5).template.id == 1319 && player.pet.inventory.itemsBody.get(5).template.id == 619)) {
                    player.PorataVIP = true;
                } else {
                    player.PorataVIP = false;
                }
            } else {
                player.PorataVIP = false;
            }
        }

        //last time use skill
        Service.getInstance().sendTimeSkill(player);

        if (TaskService.gI().getIdTask(player) == ConstTask.TASK_0_0) {
            if (player.getSession().version == 230) {
                player.playerTask.taskMain.index = 2;
                TaskService.gI().sendTaskMain(player);
                player.zone = MapService.gI().getMapCanJoin(player, player.gender + 21);
                player.location.y = 336;
            }
            NpcService.gI().createTutorial(player, -1,
                    "Chào mừng " + player.name + " đến với " + Manager.SERVER_NAME + "\n"
                    + "Nhiệm vụ đầu tiên của bạn là di chuyển\n"
                    + "Bạn hãy di chuyển nhân vật theo mũi tên chỉ hướng");
        }
        if (player.istrain && MapService.gI().isMapTrainOff(player, player.zone.map.mapId)) {
            Service.getInstance().sendThongBao(player, "Thời gian offline của bạn là " + player.timeoff + " phút");
            player.congExpOff();
            player.timeoff = 0;
        }
        NotiManager.getInstance().sendAlert(player);
        NotiManager.getInstance().sendNoti(player);
        ConsignmentShop.getInstance().sendExpirationNotification(player);
        Util.setTimeout(() -> PlayerService.gI().sendPetFollow(player), 500);
        player.timeFixInventory = System.currentTimeMillis() + 500;

        Service.getInstance().SendThreadEff(player);
//        SendEffect.getInstance().SendThreadEffDanhHieu(player);
    }
}
