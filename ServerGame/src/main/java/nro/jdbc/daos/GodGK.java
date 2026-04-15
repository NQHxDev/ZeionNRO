package nro.jdbc.daos;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nro.card.Card;
import nro.card.CollectionBook;
import nro.consts.ConstAchive;
import nro.consts.ConstMap;
import nro.consts.ConstPlayer;
import nro.jdbc.DBService;
import nro.manager.AchiveManager;
import nro.manager.PetFollowManager;
import nro.models.player.PetFollow;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.item.ItemTime;
import nro.models.npc.specialnpc.MabuEgg;
import nro.models.npc.specialnpc.MagicTree;
import nro.models.player.*;
import nro.models.skill.Skill;
import nro.models.task.Achivement;
import nro.models.task.AchivementTemplate;
import nro.models.task.TaskMain;
import nro.server.Client;
import nro.server.Manager;
import nro.server.io.Session;
import nro.server.model.AntiLogin;
import nro.services.*;
import nro.utils.SkillUtil;
import nro.utils.TimeUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nro.models.item.ItemTimeSieuCap;
import nro.utils.Util;

/**
 * @author ❤Girlkun75❤
 * @copyright ❤Trần Lại❤
 */
public class GodGK {
//ttest da sua

    public static boolean login(Session session, AntiLogin al) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Connection conn = DBService.gI().getConnectionForLogin();
            String query = "select * from account where username = ? and password = ? limit 1";
            ps = conn.prepareStatement(query);
            ps.setString(1, session.uu);
            ps.setString(2, session.pp);
            rs = ps.executeQuery();
            if (rs.next()) {
                session.userId = rs.getInt("account.id");
                Session plInGame = Client.gI().getSession(session);
                if (plInGame != null) {
                    Service.getInstance().sendThongBaoOK(plInGame, "Máy chủ tắt hoặc mất sóng!");
                    Client.gI().kickSession(plInGame);
                    Service.getInstance().sendThongBaoOK(session, "Máy chủ tắt hoặc mất sóng!");
                    return false;
                }

                session.isAdmin = rs.getBoolean("is_admin");
                session.lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                session.actived = rs.getBoolean("active");
                session.goldBar = rs.getInt("account.thoi_vang");
                session.vnd = rs.getInt("account.vnd");
                session.dataReward = rs.getString("reward");
                session.tong_nap = rs.getInt("account.tongnap");
                if (rs.getTimestamp("last_time_login").getTime() > session.lastTimeLogout) {
                    Service.getInstance().sendThongBaoOK(session, "Tài khoản đang đăng nhập máy chủ khác!");
                    return false;
                }
                if (rs.getBoolean("ban")) {
                    Service.getInstance().sendThongBaoOK(session, "Tài khoản đã bị khóa do vi phạm điều khoản!");
                } else {
                    long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                    int secondsPass = (int) ((System.currentTimeMillis() - lastTimeLogout) / 1000);
                    if (secondsPass < Manager.SECOND_WAIT_LOGIN && !session.isAdmin) {
                        Service.getInstance().sendThongBaoOK(session, "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass) + " giây để đăng nhập lại.");
                    }
                }
                al.reset();
                return true;
            } else {
                Service.getInstance().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                al.wrong();
                // Anti login
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                }
            }
        }
        return false;
    }

    public static Player loadPlayer(Session session) {
        PreparedStatement pss = null;
        ResultSet rss = null;
        try {
            Connection conn = DBService.gI().getConnectionForLogin();
            String query = "select * from account where username = ? and password = ? limit 1";
            pss = conn.prepareStatement(query);
            pss.setString(1, session.uu);
            pss.setString(2, session.pp);
            rss = pss.executeQuery();
            if (rss.next()) {
                session.userId = rss.getInt("account.id");
                session.isAdmin = rss.getBoolean("is_admin");
                session.lastTimeLogout = rss.getTimestamp("last_time_logout").getTime();
                session.actived = rss.getBoolean("active");
                session.goldBar = rss.getInt("account.thoi_vang");
                session.vnd = rss.getInt("account.vnd");
                session.dataReward = rss.getString("reward");
                session.tong_nap = rss.getInt("tongnap");
            } else {
                Service.getInstance().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                return null;
                // Anti login
            }
        } catch (SQLException e) {
        }

        try {
            Connection connection = DBService.gI().getConnectionForLogin();
            PreparedStatement ps = connection.prepareStatement("select * from player where account_id = ? limit 1");
            ps.setInt(1, session.userId);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    double plHp = 200000000.0;
                    double plMp = 200000000.0;
                    JSONValue jv = new JSONValue();
                    JSONArray dataArray = null;
                    JSONObject dataObject = null;

                    Player player = new Player();

                    //base info
                    player.id = rs.getInt("id");
                    player.name = rs.getString("name");
                    player.head = rs.getShort("head");
                    player.gender = rs.getByte("gender");
                    player.tongnap = rs.getInt("tong_nap");
                    player.killboss = rs.getInt("kill_boss");
                    player.diemdanh = rs.getInt("diemdanh");
                    player.chuyencan = rs.getInt("chuyencan");
                    player.checkquachuyencan = rs.getInt("check_qua_chuyencan");
                    player.naplandau = rs.getInt("naplandau");
                    player.tichluynap = rs.getInt("tichluynap");
                    player.evenpoint = rs.getInt("event_point");
                    player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                    player.hoivienvip = rs.getInt("hoivien_vip");
                    player.even2thang9 = rs.getInt("sukien_2thang9");
                    player.evenTrungThu = rs.getInt("sukien_trungthu");
                    
                    player.diem_quay = rs.getInt("diem_quay");
                    player.active_kham_ngoc = rs.getByte("active_kham_ngoc");
                    player.active_ruong_suu_tam = rs.getByte("active_ruong_suu_tam");
                    
                    if (player.hoivienvip > 0) {
                        player.name = "[" + Service.getInstance().capVIP(player.hoivienvip) + "] " + rs.getString("name");
                    } else {
                        player.name = rs.getString("name");
                    }
                    long now = System.currentTimeMillis();
                    long thoiGianOffline = now - session.lastTimeLogout;
                    player.timeoff = thoiGianOffline /= 60000;

                    int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
                    if (clanId != -1) {
                        Clan clan = ClanService.gI().getClanById(clanId);
                        if (clan != null) {
                            for (ClanMember cm : clan.getMembers()) {
                                if (cm.id == player.id) {
                                    clan.addMemberOnline(player);
                                    player.clan = clan;
                                    player.clanMember = cm;
                                    player.setBuff(clan.getBuff());
                                    break;
                                }
                            }
                        }
                    }
                    // ===== SET NAME THEO 3 KÝ TỰ ĐẦU CLAN =====
                    String baseName = rs.getString("name");

                    if (player.clan != null && player.clan.name != null && !player.clan.name.isEmpty()) {
                        String clanName = player.clan.name;

                        // Lấy tối đa 3 ký tự đầu
                        String clanTag = clanName.length() >= 3
                                ? clanName.substring(0, 3)
                                : clanName;

                        player.name = "[" + clanTag + "] " + baseName;
                    } else {
                        player.name = baseName;
                    }
                    // diem su kien
                    int evPoint = rs.getInt("event_point");
                    player.event.setEventPoint(evPoint);

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("sk_tet"));
                    int timeBanhTet = Integer.parseInt(dataArray.get(0).toString());
                    int timeBanhChung = Integer.parseInt(dataArray.get(1).toString());
                    boolean isNauBanhTet = Integer.parseInt(dataArray.get(2).toString()) == 1;
                    boolean isNauBanhChung = Integer.parseInt(dataArray.get(3).toString()) == 1;
                    boolean receivedLuckMoney = Integer.parseInt(dataArray.get(4).toString()) == 1;

                    player.event.setTimeCookTetCake(timeBanhTet);
                    player.event.setTimeCookChungCake(timeBanhChung);
                    player.event.setCookingTetCake(isNauBanhTet);
                    player.event.setCookingChungCake(isNauBanhChung);
                    player.event.setReceivedLuckyMoney(receivedLuckMoney);
                    dataArray.clear();


                    
                    //////////////////////////////    

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("phong_thi_nghiem"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        PhongThiNghiem_Player ptn = new PhongThiNghiem_Player();
                        ptn.idBinh = Integer.parseInt(dataObject.get("id").toString());
                        ptn.timeCheTao = Long.parseLong(dataObject.get("time").toString());
                        player.phongThiNghiem.add(ptn);
                    }
                    dataArray.clear();
                    
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("active_phuc_loi"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.idPhucLoi = Integer.parseInt(dataArray.get(i).toString());
                        player.listNhan.add(player.idPhucLoi);
                    }
                    dataArray.clear();       
                    
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("active_vong_quay"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.idTamBao = Integer.parseInt(dataArray.get(i).toString());
                        player.listNhan_TamBao.add(player.idTamBao);
                    }
                    dataArray.clear();

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("check_online"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.listOnline.add((int) Integer.parseInt(dataArray.get(i).toString()));
                    }
                    dataArray.clear();

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("check_diem_danh"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.listDiemDanh.add((int) Integer.parseInt(dataArray.get(i).toString()));
                    }
                    dataArray.clear();

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("kham_ngoc"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        KhamNgocPlayer khamngoc = new KhamNgocPlayer();
                        khamngoc.idNro = Integer.parseInt(dataObject.get("id").toString());
                        khamngoc.levelNro = Integer.parseInt(dataObject.get("level").toString());
                        player.khamNgoc.add(khamngoc);
                    }
                    dataArray.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_cai_trang"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongCaiTrang.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_phu_kien"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongPhuKien.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_pet"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongPet.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_linh_thu"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongLinhThu.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_thu_cuoi"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongThuCuoi.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data kim lượng
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("data_inventory"));
                    player.inventory.gold = Long.parseLong(dataArray.get(0).toString());
                    player.inventory.gem = Integer.parseInt(dataArray.get(1).toString());
                    player.inventory.ruby = Integer.parseInt(dataArray.get(2).toString());
                    if (dataArray.size() >= 4) {
                        player.inventory.goldLimit = Long.parseLong(dataArray.get(3).toString());
                    }
                    dataArray.clear();
                    player.event.setDiemTichLuy(session.diemTichNap);
                    player.event.setMocNapDaNhan(rs.getInt("moc_nap"));
                    player.server = session.server;
                    //data tọa độ
                    try {
                        dataArray = (JSONArray) jv.parse(rs.getString("data_location"));
                        player.location.x = Integer.parseInt(dataArray.get(0).toString());
                        player.location.y = Integer.parseInt(dataArray.get(1).toString());
                        int mapId = Integer.parseInt(dataArray.get(2).toString());
                        if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                                || MapService.gI().isMapBanDoKhoBau(mapId) || mapId == 126 || mapId == ConstMap.CON_DUONG_RAN_DOC
                                || mapId == ConstMap.CON_DUONG_RAN_DOC_142 || mapId == ConstMap.CON_DUONG_RAN_DOC_143 || mapId == ConstMap.HOANG_MAC) {
                            mapId = player.gender + 21;
                            player.location.x = 300;
                            player.location.y = 336;
                        }
                        player.zone = MapService.gI().getMapCanJoin(player, mapId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dataArray.clear();

                    //data chỉ số
                    dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                    player.nPoint.limitPower = Byte.parseByte(dataArray.get(0).toString());
                    player.nPoint.power = Double.parseDouble(dataArray.get(1).toString());
                    player.nPoint.tiemNang = Double.parseDouble(dataArray.get(2).toString());
                    player.nPoint.stamina = Short.parseShort(dataArray.get(3).toString());
                    player.nPoint.maxStamina = Short.parseShort(dataArray.get(4).toString());
                    player.nPoint.hpg = Double.parseDouble(dataArray.get(5).toString());
                    player.nPoint.mpg = Double.parseDouble(dataArray.get(6).toString());
                    player.nPoint.dameg = Double.parseDouble(dataArray.get(7).toString());
                    player.nPoint.defg = Double.parseDouble(dataArray.get(8).toString());
                    player.nPoint.critg = Byte.parseByte(dataArray.get(9).toString());
                    dataArray.get(10); //** Năng động
                    plHp = Double.parseDouble(dataArray.get(11).toString());
                    plMp = Double.parseDouble(dataArray.get(12).toString());
                    dataArray.clear();

                    //data đậu thần
                    dataArray = (JSONArray) jv.parse(rs.getString("data_magic_tree"));
                    boolean isUpgrade = Byte.parseByte(dataArray.get(0).toString()) == 1;
                    long lastTimeUpgrade = Long.parseLong(dataArray.get(1).toString());
                    byte level = Byte.parseByte(dataArray.get(2).toString());
                    long lastTimeHarvest = Long.parseLong(dataArray.get(3).toString());
                    byte currPea = Byte.parseByte(dataArray.get(4).toString());
                    player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
                    dataArray.clear();

                    //data phần thưởng sao đen
                    dataArray = (JSONArray) jv.parse(rs.getString("data_black_ball"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray reward = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(reward.get(0).toString());
                        player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(reward.get(1).toString());
                        reward.clear();
                    }
                    dataArray.clear();

                    //data body
                    dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBody.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("items_bag"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBag.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data box
                    dataArray = (JSONArray) jv.parse(rs.getString("items_box"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBox.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data box lucky round
                    dataArray = (JSONArray) jv.parse(rs.getString("items_box_lucky_round"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBoxCrackBall.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data friends
                    dataArray = (JSONArray) jv.parse(rs.getString("friends"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) dataArray.get(i);
                        Friend friend = new Friend();
                        friend.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                        friend.name = String.valueOf(dataObject.get("name"));
                        friend.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                        friend.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                        friend.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                        friend.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                        friend.power = Double.parseDouble(String.valueOf(dataObject.get("power")));
                        player.friends.add(friend);
                        dataObject.clear();
                    }
                    dataArray.clear();

                    //data enemies
                    dataArray = (JSONArray) jv.parse(rs.getString("enemies"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) dataArray.get(i);
                        Enemy enemy = new Enemy();
                        enemy.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                        enemy.name = String.valueOf(dataObject.get("name"));
                        enemy.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                        enemy.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                        enemy.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                        enemy.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                        enemy.power = Double.parseDouble(String.valueOf(dataObject.get("power")));
                        player.enemies.add(enemy);
                        dataObject.clear();
                    }
                    dataArray.clear();

                    //data nội tại
                    dataArray = (JSONArray) jv.parse(rs.getString("data_intrinsic"));
                    byte intrinsicId = Byte.parseByte(dataArray.get(0).toString());
                    player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
                    player.playerIntrinsic.intrinsic.param1 = Short.parseShort(dataArray.get(1).toString());
                    player.playerIntrinsic.countOpen = Byte.parseByte(dataArray.get(2).toString());
                    player.playerIntrinsic.intrinsic.param2 = Short.parseShort(dataArray.get(3).toString());
                    dataArray.clear();

                    //data item time
                    dataArray = (JSONArray) jv.parse(rs.getString("data_item_time"));
                    int timeBoKhi = Integer.parseInt(dataArray.get(0).toString());
                    int timeAnDanh = Integer.parseInt(dataArray.get(1).toString());
                    int timeOpenPower = Integer.parseInt(dataArray.get(2).toString());
                    int timeCuongNo = Integer.parseInt(dataArray.get(3).toString());
                    int timeBoHuyet = Integer.parseInt(dataArray.get(5).toString());
                    int timeGiapXen = Integer.parseInt(dataArray.get(8).toString());
                    int timeMayDo = 0;
                    int timeMeal = 0;
                    int iconMeal = 0;
                    try {
                        timeMayDo = Integer.parseInt(dataArray.get(4).toString());
                        timeMeal = Integer.parseInt(dataArray.get(7).toString());
                        iconMeal = Integer.parseInt(dataArray.get(6).toString());
                    } catch (Exception e) {
                    }
                    int timeBanhChung1 = 0;
                    int timeBanhTet1 = 0;
                    int timeBoKhi2 = 0;
                    int timeGiapXen2 = 0;
                    int timeCuongNo2 = 0;
                    int timeBoHuyet2 = 0;
                    if (dataArray.size() >= 15) {
                        timeBanhChung1 = Integer.parseInt(dataArray.get(9).toString());
                        timeBanhTet1 = Integer.parseInt(dataArray.get(10).toString());
                        timeBoKhi2 = Integer.parseInt(dataArray.get(11).toString());
                        timeGiapXen2 = Integer.parseInt(dataArray.get(12).toString());
                        timeCuongNo2 = Integer.parseInt(dataArray.get(13).toString());
                        timeBoHuyet2 = Integer.parseInt(dataArray.get(14).toString());
                    }
                    player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
                    player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
                    player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
                    player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
                    player.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet2);
                    player.itemTime.lastTimeBoKhi2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi2);
                    player.itemTime.lastTimeGiapXen2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen2);
                    player.itemTime.lastTimeCuongNo2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo2);
                    player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
                    player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
                    player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
                    player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal);
                    player.itemTime.lastTimeBanhChung = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeBanhChung1);
                    player.itemTime.lastTimeBanhTet = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeBanhTet1);
                    player.itemTime.iconMeal = iconMeal;
                    player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
                    player.itemTime.isUseBoKhi = timeBoKhi != 0;
                    player.itemTime.isUseGiapXen = timeGiapXen != 0;
                    player.itemTime.isUseCuongNo = timeCuongNo != 0;
                    player.itemTime.isUseBoHuyet2 = timeBoHuyet2 != 0;
                    player.itemTime.isUseBoKhi2 = timeBoKhi2 != 0;
                    player.itemTime.isUseGiapXen2 = timeGiapXen2 != 0;
                    player.itemTime.isUseCuongNo2 = timeCuongNo2 != 0;
                    player.itemTime.isUseAnDanh = timeAnDanh != 0;
                    player.itemTime.isOpenPower = timeOpenPower != 0;
                    player.itemTime.isUseMayDo = timeMayDo != 0;
                    player.itemTime.isEatMeal = timeMeal != 0;
                    player.itemTime.isUseBanhChung = timeBanhChung1 != 0;
                    player.itemTime.isUseBanhTet = timeBanhTet1 != 0;
                    dataArray.clear();

                    //data item time
                    dataArray = (JSONArray) jv.parse(rs.getString("data_item_time_sieucap"));
                    int timeDuoiKhi = Integer.parseInt(String.valueOf(dataArray.get(0)));
                    int timeDaNgucTu = Integer.parseInt(String.valueOf(dataArray.get(1)));
                    int timeCaRot = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    int timeKeo = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    int timeXiMuoi = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    int iconBanh = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    int timeBanh = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    int timeChoido = Integer.parseInt(String.valueOf(dataArray.get(7)));
                    int timeRongSieuCap = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    int timeRongBang = Integer.parseInt(String.valueOf(dataArray.get(9)));
                    int timeDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(10)));
                    int iconDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(11)));

                    player.itemTimesieucap.lastTimeDuoikhi = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeDuoiKhi);
                    player.itemTimesieucap.lastTimeDaNgucTu = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeDaNgucTu);
                    player.itemTimesieucap.lastTimeCaRot = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeCaRot);
                    player.itemTimesieucap.lastTimeKeo = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeKeo);
                    player.itemTimesieucap.lastTimeUseXiMuoi = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeXiMuoi);
                    player.itemTimesieucap.lastTimeUseBanh = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_TRUNGTHU - timeBanh);
                    player.itemTimesieucap.lasttimeChoido = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeChoido);
                    player.itemTimesieucap.lastTimeRongSieuCap = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeRongSieuCap);
                    player.itemTimesieucap.lastTimeRongBang = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeRongBang);
                    player.itemTimesieucap.lastTimeMeal = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeDuoi3);

                    player.itemTimesieucap.isDuoikhi = timeDuoiKhi != 0;
                    player.itemTimesieucap.isDaNgucTu = timeDaNgucTu != 0;
                    player.itemTimesieucap.isUseCaRot = timeCaRot != 0;
                    player.itemTimesieucap.isKeo = timeKeo != 0;
                    player.itemTimesieucap.isUseXiMuoi = timeXiMuoi != 0;
                    player.itemTimesieucap.iconBanh = iconBanh;
                    player.itemTimesieucap.isUseTrungThu = timeBanh != 0;
                    player.itemTimesieucap.isChoido = timeChoido != 0;
                    player.itemTimesieucap.iconMeal = iconDuoi3;
                    player.itemTimesieucap.isRongSieuCap = timeRongSieuCap != 0;
                    player.itemTimesieucap.isRongBang = timeRongBang != 0;
                    player.itemTimesieucap.isEatMeal = timeDuoi3 != 0;
                    dataArray.clear();

                    //data nhiệm vụ
                    dataArray = (JSONArray) jv.parse(rs.getString("data_task"));
                    TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(dataArray.get(1).toString()));
                    taskMain.subTasks.get(Integer.parseInt(dataArray.get(2).toString())).count = Short.parseShort(dataArray.get(0).toString());
                    taskMain.index = Byte.parseByte(dataArray.get(2).toString());
                    player.playerTask.taskMain = taskMain;
                    dataArray.clear();

                    //data nhiệm vụ hàng ngày
                    try {
                        dataArray = (JSONArray) jv.parse(rs.getString("data_side_task"));
                        String format = "dd-MM-yyyy";
                        long receivedTime = Long.parseLong(String.valueOf(dataArray.get(4)));
                        Date date = new Date(receivedTime);
                        if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                            player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataArray.get(0).toString()));
                            player.playerTask.sideTask.count = Integer.parseInt(dataArray.get(1).toString());
                            player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(2).toString()));
                            player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(dataArray.get(3).toString()));
                            player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(5).toString()));
                            player.playerTask.sideTask.receivedTime = receivedTime;
                        }
                    } catch (Exception e) {
                    }

                    dataArray = (JSONArray) jv.parse(rs.getString("achivements"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        Achivement achivement = new Achivement();
                        achivement.setId(Integer.parseInt(dataObject.get("id").toString()));
                        achivement.setCount(Integer.parseInt(dataObject.get("count").toString()));
                        achivement.setFinish(Integer.parseInt(dataObject.get("finish").toString()) == 1);
                        achivement.setReceive(Integer.parseInt(dataObject.get("receive").toString()) == 1);
                        AchivementTemplate a = AchiveManager.getInstance().findByID(achivement.getId());
                        achivement.setName(a.getName());
                        achivement.setDetail(a.getDetail());
                        achivement.setMaxCount(a.getMaxCount());
                        achivement.setMoney(a.getMoney());
                        player.playerTask.achivements.add(achivement);
                    }

                    List<AchivementTemplate> listAchivements = AchiveManager.getInstance().getList();
                    if (dataArray.size() < listAchivements.size()) { //add thêm nhiệm vụ khi có nhiệm vụ mới
                        for (int i = dataArray.size(); i < listAchivements.size(); i++) {
                            AchivementTemplate a = AchiveManager.getInstance().findByID(i);
                            Achivement achivement = new Achivement();
                            if (a != null) {
                                achivement.setId(a.getId());
                                achivement.setCount(0);
                                achivement.setFinish(false);
                                achivement.setReceive(false);
                                achivement.setName(a.getName());
                                achivement.setDetail(a.getDetail());
                                achivement.setMaxCount(a.getMaxCount());
                                achivement.setMoney(a.getMoney());
                                player.playerTask.achivements.add(achivement);
                            }
                        }
                    }
                    dataArray.clear();

                    //data trứng bư
                    dataObject = (JSONObject) jv.parse(rs.getString("data_mabu_egg"));
                    Object createTime = dataObject.get("create_time");
                    if (createTime != null) {
                        player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(createTime)),
                                Long.parseLong(String.valueOf(dataObject.get("time_done"))));
                    }
                    dataObject.clear();

                    //data bùa
                    dataArray = (JSONArray) jv.parse(rs.getString("data_charm"));
                    player.charms.tdTriTue = Long.parseLong(dataArray.get(0).toString());
                    player.charms.tdManhMe = Long.parseLong(dataArray.get(1).toString());
                    player.charms.tdDaTrau = Long.parseLong(dataArray.get(2).toString());
                    player.charms.tdOaiHung = Long.parseLong(dataArray.get(3).toString());
                    player.charms.tdBatTu = Long.parseLong(dataArray.get(4).toString());
                    player.charms.tdDeoDai = Long.parseLong(dataArray.get(5).toString());
                    player.charms.tdThuHut = Long.parseLong(dataArray.get(6).toString());
                    player.charms.tdDeTu = Long.parseLong(dataArray.get(7).toString());
                    player.charms.tdTriTue3 = Long.parseLong(dataArray.get(8).toString());
                    player.charms.tdTriTue4 = Long.parseLong(dataArray.get(9).toString());
                    if (dataArray.size() >= 11) {
                        player.charms.tdDeTuMabu = Long.parseLong(dataArray.get(10).toString());
                    }
                    dataArray.clear();

                    //data drop vàng ngọc
                    dataArray = (JSONArray) jv.parse(rs.getString("drop_vang_ngoc"));
                    player.vangnhat = Long.parseLong(dataArray.get(0).toString());
                    player.hngocnhat = Long.parseLong(dataArray.get(1).toString());
                    dataArray.clear();

                    //data mốc nạp
                    dataArray = (JSONArray) jv.parse(rs.getString("nhan_moc_nap"));
                    player.mot = Integer.parseInt(dataArray.get(0).toString());
                    player.hai = Integer.parseInt(dataArray.get(1).toString());
                    player.ba = Integer.parseInt(dataArray.get(2).toString());
                    player.bon = Integer.parseInt(dataArray.get(3).toString());
                    player.nam = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();
                    
                    //data mốc nạp
                    dataArray = (JSONArray) jv.parse(rs.getString("nhan_moc_nap2"));
                    player.sau = Integer.parseInt(dataArray.get(0).toString());
                    player.bay = Integer.parseInt(dataArray.get(1).toString());
                    player.tam = Integer.parseInt(dataArray.get(2).toString());
                    player.chin = Integer.parseInt(dataArray.get(3).toString());
                    player.muoi = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();
                    
                    //data mốc nạp
                    dataArray = (JSONArray) jv.parse(rs.getString("nhan_moc_nap3"));
                    player.muoiMot = Integer.parseInt(dataArray.get(0).toString());
                    player.muoiHai = Integer.parseInt(dataArray.get(1).toString());
                    player.muoiBa = Integer.parseInt(dataArray.get(2).toString());
                    player.muoiBon = Integer.parseInt(dataArray.get(3).toString());
                    player.muoiLam = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();
                    
                    //data đan dược
                    dataArray = (JSONArray) jv.parse(rs.getString("dan_duoc"));
                    player.bohuyetdan = Integer.parseInt(dataArray.get(0).toString());
                    player.tangnguyendan = Integer.parseInt(dataArray.get(1).toString());
                    player.bokhidan = Integer.parseInt(dataArray.get(2).toString());
                    dataArray.clear();

                    //data chuyển sinh
                    dataArray = (JSONArray) jv.parse(rs.getString("chuyen_sinh"));
                    player.chuyensinh = Integer.parseInt(dataArray.get(0).toString());
                    player.MaxGoldTradeDay = Integer.parseInt(dataArray.get(1).toString());
                    player.chuaco2 = Integer.parseInt(dataArray.get(2).toString());
                    player.chuaco3 = Integer.parseInt(dataArray.get(3).toString());
                    player.chuaco4 = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();

                    //data số may mắn
                    dataArray = (JSONArray) jv.parse(rs.getString("so_may_man"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.sochon = Integer.parseInt(dataArray.get(i).toString());
                        player.soMayMan.add(player.sochon);
                    }
                    dataArray.clear();

                    //data luyện tập off
                    dataArray = (JSONArray) jv.parse(rs.getString("data_offtrain"));
                    player.typetrain = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    player.istrain = Byte.parseByte(String.valueOf(dataArray.get(1))) == 1;
                    dataArray.clear();

                    //data skill
                    dataArray = (JSONArray) jv.parse(rs.getString("skills"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        int tempId = Integer.parseInt(skillTemp.get(0).toString());
                        byte point = Byte.parseByte(skillTemp.get(2).toString());
                        Skill skill = null;
                        if (point != 0) {
                            skill = SkillUtil.createSkill(tempId, point);
                        } else {
                            skill = SkillUtil.createSkillLevel0(tempId);
                        }
                        skill.lastTimeUseThisSkill = Long.parseLong(skillTemp.get(1).toString());
                        if (skillTemp.size() > 3) {
                            skill.currLevel = Short.parseShort(String.valueOf(skillTemp.get(3)));
                        }
                        player.playerSkill.skills.add(skill);
                        skillTemp.clear();
                    }
                    dataArray.clear();

                    //data skill shortcut
                    dataArray = (JSONArray) jv.parse(rs.getString("skills_shortcut"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
                    }
                    for (int i : player.playerSkill.skillShortCut) {
                        if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                            player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                            break;
                        }
                    }
                    if (player.playerSkill.skillSelect == null) {
                        player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                                ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
                    }
                    dataArray.clear();

                    Gson gson = new Gson();
                    List<Card> cards = gson.fromJson(rs.getString("collection_book"), new TypeToken<List<Card>>() {
                    }.getType());

                    CollectionBook book = new CollectionBook(player);
                    if (cards != null) {
                        book.setCards(cards);
                    } else {
                        book.setCards(new ArrayList<>());
                    }
                    book.init();
                    player.setCollectionBook(book);
                    List<Item> itemsBody = player.inventory.itemsBody;
                    while (itemsBody.size() < 11) {
                        itemsBody.add(ItemService.gI().createItemNull());
                    }

                    if (itemsBody.get(7).isNotNullItem()) {
                        MiniPet.callMiniPet(player, (player.inventory.itemsBody.get(7).template.id));
                    }
                    if (itemsBody.get(10).isNotNullItem()) {
                        PetFollow pet = PetFollowManager.gI().findByID(itemsBody.get(10).getId());
                        player.setPetFollow(pet);
                    }
                    player.firstTimeLogin = rs.getTimestamp("firstTimeLogin");

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("buy_limit"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.buyLimit[i] = Byte.parseByte(dataArray.get(i).toString());
                    }

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("reward_limit"));

                    player.rewardLimit = new byte[dataArray.size()];
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.rewardLimit[i] = Byte.parseByte(dataArray.get(i).toString());
                    }

                    //dhvt23
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("challenge"));
                    player.goldChallenge = Integer.parseInt(dataArray.get(0).toString());
                    player.levelWoodChest = Integer.parseInt(dataArray.get(1).toString());
                    player.receivedWoodChest = Integer.parseInt(dataArray.get(2).toString()) == 1;
                    dataArray.clear();

                    //data danh hiệu
                    dataArray = (JSONArray) jv.parse(rs.getString("danh_hieu"));
                    player.isTitleUse1 = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                    player.lastTimeTitle1 = Long.parseLong(String.valueOf(dataArray.get(1)));
                    player.IdDanhHieu_1 = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    player.isTitleUse2 = Integer.parseInt(String.valueOf(dataArray.get(3))) == 1 ? true : false;
                    player.lastTimeTitle2 = Long.parseLong(String.valueOf(dataArray.get(4)));
                    player.IdDanhHieu_2 = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.isTitleUse3 = Integer.parseInt(String.valueOf(dataArray.get(6))) == 1 ? true : false;
                    player.lastTimeTitle3 = Long.parseLong(String.valueOf(dataArray.get(7)));
                    player.IdDanhHieu_3 = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    player.isTitleUse4 = Integer.parseInt(String.valueOf(dataArray.get(9))) == 1 ? true : false;
                    player.lastTimeTitle4 = Long.parseLong(String.valueOf(dataArray.get(10)));
                    player.IdDanhHieu_4 = Integer.parseInt(String.valueOf(dataArray.get(11)));
                    player.isTitleUse5 = Integer.parseInt(String.valueOf(dataArray.get(12))) == 1 ? true : false;
                    player.lastTimeTitle5 = Long.parseLong(String.valueOf(dataArray.get(13)));
                    player.IdDanhHieu_5 = Integer.parseInt(String.valueOf(dataArray.get(14)));
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle1))) {
                        player.isTitleUse1 = false;
                        player.lastTimeTitle1 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle2))) {
                        player.isTitleUse2 = false;
                        player.lastTimeTitle2 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle3))) {
                        player.isTitleUse3 = false;
                        player.lastTimeTitle3 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle4))) {
                        player.isTitleUse4 = false;
                        player.lastTimeTitle4 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle5))) {
                        player.isTitleUse5 = false;
                        player.lastTimeTitle5 = 0;
                    }
                    dataArray.clear();
                    
                    //data limit
                    dataArray = (JSONArray) jv.parse(rs.getString("reset_ngay"));
                    player.bongtai = Integer.parseInt(dataArray.get(0).toString());
                    player.thiensu = Integer.parseInt(dataArray.get(1).toString());
                    dataArray.clear();
                    

                    PlayerService.gI().dailyLogin(player);

                    //data pet
                    dataObject = (JSONObject) jv.parse(rs.getString("pet_info"));
                    if (!String.valueOf(dataObject).equals("{}")) {
                        Pet pet = new Pet(player);
                        pet.id = -player.id;
                        pet.gender = Byte.parseByte(String.valueOf(dataObject.get("gender")));
                        pet.typePet = Byte.parseByte(String.valueOf(dataObject.get("is_mabu")));
                        pet.name = String.valueOf(dataObject.get("name"));
                        player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataObject.get("type_fusion")));
                        player.fusion.lastTimeFusion = System.currentTimeMillis()
                                - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataObject.get("left_fusion"))));
                        pet.status = Byte.parseByte(String.valueOf(dataObject.get("status")));

                        //data chỉ số
                        dataObject = (JSONObject) jv.parse(rs.getString("pet_point"));
                        pet.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
                        pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
                        pet.nPoint.hpg = Double.parseDouble(String.valueOf(dataObject.get("hpg")));
                        pet.nPoint.mpg = Double.parseDouble(String.valueOf(dataObject.get("mpg")));
                        pet.nPoint.dameg = Double.parseDouble(String.valueOf(dataObject.get("damg")));
                        pet.nPoint.defg = Double.parseDouble(String.valueOf(dataObject.get("defg")));
                        pet.nPoint.critg = Integer.parseInt(String.valueOf(dataObject.get("critg")));
                        pet.nPoint.power = Double.parseDouble(String.valueOf(dataObject.get("power")));
                        pet.nPoint.tiemNang = Double.parseDouble(String.valueOf(dataObject.get("tiem_nang")));
                        pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                        double hp = Double.parseDouble(String.valueOf(dataObject.get("hp")));
                        double mp = Double.parseDouble(String.valueOf(dataObject.get("mp")));

                        //data body
                        dataArray = (JSONArray) jv.parse(rs.getString("pet_body"));
                        for (int i = 0; i < dataArray.size(); i++) {
                            dataObject = (JSONObject) dataArray.get(i);
                            Item item = null;
                            short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                            if (tempId != -1) {
                                item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                                JSONArray options = (JSONArray) dataObject.get("option");
                                for (int j = 0; j < options.size(); j++) {
                                    JSONArray opt = (JSONArray) options.get(j);
                                    item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                            Integer.parseInt(String.valueOf(opt.get(1)))));
                                }
                                item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                                if (ItemService.gI().isOutOfDateTime(item)) {
                                    item = ItemService.gI().createItemNull();
                                }
                            } else {
                                item = ItemService.gI().createItemNull();
                            }
                            pet.inventory.itemsBody.add(item);
                        }

                        //data skills
                        dataArray = (JSONArray) jv.parse(rs.getString("pet_skill"));
                        for (int i = 0; i < dataArray.size(); i++) {
                            JSONArray skillTemp = (JSONArray) dataArray.get(i);
                            int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                            byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                            Skill skill = null;
                            if (point != 0) {
                                skill = SkillUtil.createSkill(tempId, point);
                            } else {
                                skill = SkillUtil.createSkillLevel0(tempId);
                            }
                            switch (skill.template.id) {
                                case Skill.KAMEJOKO:
                                case Skill.MASENKO:
                                case Skill.ANTOMIC:
                                    skill.coolDown = 1000;
                                    break;
                            }
                            pet.playerSkill.skills.add(skill);
                        }
                        pet.nPoint.hp = hp;
                        pet.nPoint.mp = mp;
//                    pet.nPoint.calPoint();
                        player.pet = pet;
                    }
                    if (session.ruby > 0) {
                        player.inventory.ruby += session.ruby;
                        PlayerDAO.subRuby(player, session.userId, session.ruby);
                    }
                    player.nPoint.hp = plHp;
                    player.nPoint.mp = plMp;
                    session.player = player;
                    PreparedStatement ps2 = connection.prepareStatement("update account set last_time_login = ?, ip_address = ? where id = ?");
                    ps2.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps2.setString(2, session.ipAddress);
                    ps2.setInt(3, session.userId);
                    ps2.executeUpdate();
                    ps2.close();
                    return player;
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            session.dataLoadFailed = true;
        }
        return null;
    }

    public static Player loadPlayerbyId(int id) {
        PreparedStatement pss = null;
        try {
            Connection connection = DBService.gI().getConnectionForLogin();
            PreparedStatement ps = connection.prepareStatement("select * from player where id = ? limit 1");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    double plHp = 200000000;
                    double plMp = 200000000;
                    JSONValue jv = new JSONValue();
                    JSONArray dataArray = null;
                    JSONObject dataObject = null;

                    Player player = new Player();

                    //base info
                    player.id = rs.getInt("id");
                    player.name = rs.getString("name");
                    player.head = rs.getShort("head");
                    player.gender = rs.getByte("gender");
                    player.tongnap = rs.getInt("tong_nap");
                    player.killboss = rs.getInt("kill_boss");
                    player.diemdanh = rs.getInt("diemdanh");
                    player.chuyencan = rs.getInt("chuyencan");
                    player.checkquachuyencan = rs.getInt("check_qua_chuyencan");
                    player.naplandau = rs.getInt("naplandau");
                    player.tichluynap = rs.getInt("tichluynap");
                    player.evenpoint = rs.getInt("event_point");
                    player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                    player.hoivienvip = rs.getInt("hoivien_vip");
                    player.even2thang9 = rs.getInt("sukien_2thang9");
                    player.evenTrungThu = rs.getInt("sukien_trungthu");
                    
                    player.diem_quay = rs.getInt("diem_quay");
                    player.active_kham_ngoc = rs.getByte("active_kham_ngoc");
                    player.active_ruong_suu_tam = rs.getByte("active_ruong_suu_tam");
                    
                    if (player.hoivienvip > 0) {
                        player.name = "[" + Service.getInstance().capVIP(player.hoivienvip) + "] " + rs.getString("name");
                    } else {
                        player.name = rs.getString("name");
                    }
                    int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
                    if (clanId != -1) {
                        Clan clan = ClanService.gI().getClanById(clanId);
                        if (clan != null) {
                            for (ClanMember cm : clan.getMembers()) {
                                if (cm.id == player.id) {
                                    clan.addMemberOnline(player);
                                    player.clan = clan;
                                    player.clanMember = cm;
                                    player.setBuff(clan.getBuff());
                                    break;
                                }
                            }
                        }
                    }
                    // ===== SET NAME THEO 3 KÝ TỰ ĐẦU CLAN =====
                    String baseName = rs.getString("name");

                    if (player.clan != null && player.clan.name != null && !player.clan.name.isEmpty()) {
                        String clanName = player.clan.name;

                        // Lấy tối đa 3 ký tự đầu
                        String clanTag = clanName.length() >= 3
                                ? clanName.substring(0, 3)
                                : clanName;

                        player.name = "[" + clanTag + "] " + baseName;
                    } else {
                        player.name = baseName;
                    }
                    // diem su kien
                    int evPoint = rs.getInt("event_point");
                    player.event.setEventPoint(evPoint);

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("sk_tet"));
                    int timeBanhTet = Integer.parseInt(dataArray.get(0).toString());
                    int timeBanhChung = Integer.parseInt(dataArray.get(1).toString());
                    boolean isNauBanhTet = Integer.parseInt(dataArray.get(2).toString()) == 1;
                    boolean isNauBanhChung = Integer.parseInt(dataArray.get(3).toString()) == 1;
                    boolean receivedLuckMoney = Integer.parseInt(dataArray.get(4).toString()) == 1;

                    player.event.setTimeCookTetCake(timeBanhTet);
                    player.event.setTimeCookChungCake(timeBanhChung);
                    player.event.setCookingTetCake(isNauBanhTet);
                    player.event.setCookingChungCake(isNauBanhChung);
                    player.event.setReceivedLuckyMoney(receivedLuckMoney);
                    dataArray.clear();

                    
                    //////////////////////////////    
                    
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("phong_thi_nghiem"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        PhongThiNghiem_Player ptn = new PhongThiNghiem_Player();
                        ptn.idBinh = Integer.parseInt(dataObject.get("id").toString());
                        ptn.timeCheTao = Long.parseLong(dataObject.get("time").toString());
                        player.phongThiNghiem.add(ptn);
                    }
                    dataArray.clear();
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("active_phuc_loi"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.idPhucLoi = Integer.parseInt(dataArray.get(i).toString());
                        player.listNhan.add(player.idPhucLoi);
                    }
                    dataArray.clear();
                    
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("active_vong_quay"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.idTamBao = Integer.parseInt(dataArray.get(i).toString());
                        player.listNhan_TamBao.add(player.idTamBao);
                    }
                    dataArray.clear();

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("check_online"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.listOnline.add((int) Integer.parseInt(dataArray.get(i).toString()));
                    }
                    dataArray.clear();

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("check_diem_danh"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.listDiemDanh.add((int) Integer.parseInt(dataArray.get(i).toString()));
                    }
                    dataArray.clear();

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("kham_ngoc"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        KhamNgocPlayer khamngoc = new KhamNgocPlayer();
                        khamngoc.idNro = Integer.parseInt(dataObject.get("id").toString());
                        khamngoc.levelNro = Integer.parseInt(dataObject.get("level").toString());
                        player.khamNgoc.add(khamngoc);
                    }
                    dataArray.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_cai_trang"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongCaiTrang.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_phu_kien"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongPhuKien.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_pet"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongPet.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_linh_thu"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongLinhThu.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("ruong_thu_cuoi"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.ruongSuuTam.RuongThuCuoi.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();
                    
                    //data kim lượng
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("data_inventory"));
                    player.inventory.gold = Long.parseLong(dataArray.get(0).toString());
                    player.inventory.gem = Integer.parseInt(dataArray.get(1).toString());
                    player.inventory.ruby = Integer.parseInt(dataArray.get(2).toString());
                    if (dataArray.size() >= 4) {
                        player.inventory.goldLimit = Long.parseLong(dataArray.get(3).toString());
                    }
                    dataArray.clear();
                    //data tọa độ
                    try {
                        dataArray = (JSONArray) jv.parse(rs.getString("data_location"));
                        player.location.x = Integer.parseInt(dataArray.get(0).toString());
                        player.location.y = Integer.parseInt(dataArray.get(1).toString());
                        int mapId = Integer.parseInt(dataArray.get(2).toString());
                        if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                                || MapService.gI().isMapBanDoKhoBau(mapId) || mapId == 126 || mapId == ConstMap.CON_DUONG_RAN_DOC
                                || mapId == ConstMap.CON_DUONG_RAN_DOC_142 || mapId == ConstMap.CON_DUONG_RAN_DOC_143 || mapId == ConstMap.HOANG_MAC) {
                            mapId = player.gender + 21;
                            player.location.x = 300;
                            player.location.y = 336;
                        }
                        player.zone = MapService.gI().getMapCanJoin(player, mapId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dataArray.clear();

                    //data chỉ số
                    dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                    player.nPoint.limitPower = Byte.parseByte(dataArray.get(0).toString());
                    player.nPoint.power = Double.parseDouble(dataArray.get(1).toString());
                    player.nPoint.tiemNang = Double.parseDouble(dataArray.get(2).toString());
                    player.nPoint.stamina = Short.parseShort(dataArray.get(3).toString());
                    player.nPoint.maxStamina = Short.parseShort(dataArray.get(4).toString());
                    player.nPoint.hpg = Double.parseDouble(dataArray.get(5).toString());
                    player.nPoint.mpg = Double.parseDouble(dataArray.get(6).toString());
                    player.nPoint.dameg = Double.parseDouble(dataArray.get(7).toString());
                    player.nPoint.defg = Double.parseDouble(dataArray.get(8).toString());
                    player.nPoint.critg = Byte.parseByte(dataArray.get(9).toString());
                    dataArray.get(10); //** Năng động
                    plHp = Double.parseDouble(dataArray.get(11).toString());
                    plMp = Double.parseDouble(dataArray.get(12).toString());
                    dataArray.clear();

                    //data đậu thần
                    dataArray = (JSONArray) jv.parse(rs.getString("data_magic_tree"));
                    boolean isUpgrade = Byte.parseByte(dataArray.get(0).toString()) == 1;
                    long lastTimeUpgrade = Long.parseLong(dataArray.get(1).toString());
                    byte level = Byte.parseByte(dataArray.get(2).toString());
                    long lastTimeHarvest = Long.parseLong(dataArray.get(3).toString());
                    byte currPea = Byte.parseByte(dataArray.get(4).toString());
                    player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
                    dataArray.clear();

                    //data phần thưởng sao đen
                    dataArray = (JSONArray) jv.parse(rs.getString("data_black_ball"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray reward = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(reward.get(0).toString());
                        player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(reward.get(1).toString());
                        reward.clear();
                    }
                    dataArray.clear();

                    //data body
                    dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBody.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data bag
                    dataArray = (JSONArray) jv.parse(rs.getString("items_bag"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBag.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data box
                    dataArray = (JSONArray) jv.parse(rs.getString("items_box"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBox.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data box lucky round
                    dataArray = (JSONArray) jv.parse(rs.getString("items_box_lucky_round"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBoxCrackBall.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data friends
                    dataArray = (JSONArray) jv.parse(rs.getString("friends"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) dataArray.get(i);
                        Friend friend = new Friend();
                        friend.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                        friend.name = String.valueOf(dataObject.get("name"));
                        friend.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                        friend.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                        friend.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                        friend.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                        friend.power = Double.parseDouble(String.valueOf(dataObject.get("power")));
                        player.friends.add(friend);
                        dataObject.clear();
                    }
                    dataArray.clear();

                    //data enemies
                    dataArray = (JSONArray) jv.parse(rs.getString("enemies"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) dataArray.get(i);
                        Enemy enemy = new Enemy();
                        enemy.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                        enemy.name = String.valueOf(dataObject.get("name"));
                        enemy.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                        enemy.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                        enemy.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                        enemy.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                        enemy.power = Double.parseDouble(String.valueOf(dataObject.get("power")));
                        player.enemies.add(enemy);
                        dataObject.clear();
                    }
                    dataArray.clear();

                    //data nội tại
                    dataArray = (JSONArray) jv.parse(rs.getString("data_intrinsic"));
                    byte intrinsicId = Byte.parseByte(dataArray.get(0).toString());
                    player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
                    player.playerIntrinsic.intrinsic.param1 = Short.parseShort(dataArray.get(1).toString());
                    player.playerIntrinsic.countOpen = Byte.parseByte(dataArray.get(2).toString());
                    player.playerIntrinsic.intrinsic.param2 = Short.parseShort(dataArray.get(3).toString());
                    dataArray.clear();

                    //data item time
                    dataArray = (JSONArray) jv.parse(rs.getString("data_item_time"));
                    int timeBoKhi = Integer.parseInt(dataArray.get(0).toString());
                    int timeAnDanh = Integer.parseInt(dataArray.get(1).toString());
                    int timeOpenPower = Integer.parseInt(dataArray.get(2).toString());
                    int timeCuongNo = Integer.parseInt(dataArray.get(3).toString());
                    int timeBoHuyet = Integer.parseInt(dataArray.get(5).toString());
                    int timeGiapXen = Integer.parseInt(dataArray.get(8).toString());
                    int timeMayDo = 0;
                    int timeMeal = 0;
                    int iconMeal = 0;
                    try {
                        timeMayDo = Integer.parseInt(dataArray.get(4).toString());
                        timeMeal = Integer.parseInt(dataArray.get(7).toString());
                        iconMeal = Integer.parseInt(dataArray.get(6).toString());
                    } catch (Exception e) {
                    }
                    int timeBanhChung1 = 0;
                    int timeBanhTet1 = 0;
                    int timeBoKhi2 = 0;
                    int timeGiapXen2 = 0;
                    int timeCuongNo2 = 0;
                    int timeBoHuyet2 = 0;
                    if (dataArray.size() >= 15) {
                        timeBanhChung1 = Integer.parseInt(dataArray.get(9).toString());
                        timeBanhTet1 = Integer.parseInt(dataArray.get(10).toString());
                        timeBoKhi2 = Integer.parseInt(dataArray.get(11).toString());
                        timeGiapXen2 = Integer.parseInt(dataArray.get(12).toString());
                        timeCuongNo2 = Integer.parseInt(dataArray.get(13).toString());
                        timeBoHuyet2 = Integer.parseInt(dataArray.get(14).toString());
                    }
                    player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
                    player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
                    player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
                    player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
                    player.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet2);
                    player.itemTime.lastTimeBoKhi2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi2);
                    player.itemTime.lastTimeGiapXen2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen2);
                    player.itemTime.lastTimeCuongNo2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo2);
                    player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
                    player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
                    player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
                    player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal);
                    player.itemTime.lastTimeBanhChung = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeBanhChung1);
                    player.itemTime.lastTimeBanhTet = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeBanhTet1);
                    player.itemTime.iconMeal = iconMeal;
                    player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
                    player.itemTime.isUseBoKhi = timeBoKhi != 0;
                    player.itemTime.isUseGiapXen = timeGiapXen != 0;
                    player.itemTime.isUseCuongNo = timeCuongNo != 0;
                    player.itemTime.isUseBoHuyet2 = timeBoHuyet2 != 0;
                    player.itemTime.isUseBoKhi2 = timeBoKhi2 != 0;
                    player.itemTime.isUseGiapXen2 = timeGiapXen2 != 0;
                    player.itemTime.isUseCuongNo2 = timeCuongNo2 != 0;
                    player.itemTime.isUseAnDanh = timeAnDanh != 0;
                    player.itemTime.isOpenPower = timeOpenPower != 0;
                    player.itemTime.isUseMayDo = timeMayDo != 0;
                    player.itemTime.isEatMeal = timeMeal != 0;
                    player.itemTime.isUseBanhChung = timeBanhChung1 != 0;
                    player.itemTime.isUseBanhTet = timeBanhTet1 != 0;
                    dataArray.clear();

                    //data item time
                    dataArray = (JSONArray) jv.parse(rs.getString("data_item_time_sieucap"));
                    int timeDuoiKhi = Integer.parseInt(String.valueOf(dataArray.get(0)));
                    int timeDaNgucTu = Integer.parseInt(String.valueOf(dataArray.get(1)));
                    int timeCaRot = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    int timeKeo = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    int timeXiMuoi = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    int iconBanh = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    int timeBanh = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    int timeChoido = Integer.parseInt(String.valueOf(dataArray.get(7)));
                    int timeRongSieuCap = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    int timeRongBang = Integer.parseInt(String.valueOf(dataArray.get(9)));
                    int timeDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(10)));
                    int iconDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(11)));

                    player.itemTimesieucap.lastTimeDuoikhi = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeDuoiKhi);
                    player.itemTimesieucap.lastTimeDaNgucTu = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeDaNgucTu);
                    player.itemTimesieucap.lastTimeCaRot = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeCaRot);
                    player.itemTimesieucap.lastTimeKeo = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeKeo);
                    player.itemTimesieucap.lastTimeUseXiMuoi = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeXiMuoi);
                    player.itemTimesieucap.lastTimeUseBanh = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_TRUNGTHU - timeBanh);
                    player.itemTimesieucap.lasttimeChoido = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeChoido);
                    player.itemTimesieucap.lastTimeRongSieuCap = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeRongSieuCap);
                    player.itemTimesieucap.lastTimeRongBang = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeRongBang);
                    player.itemTimesieucap.lastTimeMeal = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_10P - timeDuoi3);

                    player.itemTimesieucap.isDuoikhi = timeDuoiKhi != 0;
                    player.itemTimesieucap.isDaNgucTu = timeDaNgucTu != 0;
                    player.itemTimesieucap.isUseCaRot = timeCaRot != 0;
                    player.itemTimesieucap.isKeo = timeKeo != 0;
                    player.itemTimesieucap.isUseXiMuoi = timeXiMuoi != 0;
                    player.itemTimesieucap.iconBanh = iconBanh;
                    player.itemTimesieucap.isUseTrungThu = timeBanh != 0;
                    player.itemTimesieucap.isChoido = timeChoido != 0;
                    player.itemTimesieucap.iconMeal = iconDuoi3;
                    player.itemTimesieucap.isRongSieuCap = timeRongSieuCap != 0;
                    player.itemTimesieucap.isRongBang = timeRongBang != 0;
                    player.itemTimesieucap.isEatMeal = timeDuoi3 != 0;
                    dataArray.clear();

                    //data nhiệm vụ
                    dataArray = (JSONArray) jv.parse(rs.getString("data_task"));
                    TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(dataArray.get(1).toString()));
                    taskMain.subTasks.get(Integer.parseInt(dataArray.get(2).toString())).count = Short.parseShort(dataArray.get(0).toString());
                    taskMain.index = Byte.parseByte(dataArray.get(2).toString());
                    player.playerTask.taskMain = taskMain;
                    dataArray.clear();

                    //data nhiệm vụ hàng ngày
                    try {
                        dataArray = (JSONArray) jv.parse(rs.getString("data_side_task"));
                        String format = "dd-MM-yyyy";
                        long receivedTime = Long.parseLong(String.valueOf(dataArray.get(4)));
                        Date date = new Date(receivedTime);
                        if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                            player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataArray.get(0).toString()));
                            player.playerTask.sideTask.count = Integer.parseInt(dataArray.get(1).toString());
                            player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(2).toString()));
                            player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(dataArray.get(3).toString()));
                            player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(5).toString()));
                            player.playerTask.sideTask.receivedTime = receivedTime;
                        }
                    } catch (Exception e) {
                    }

                    dataArray = (JSONArray) jv.parse(rs.getString("achivements"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        Achivement achivement = new Achivement();
                        achivement.setId(Integer.parseInt(dataObject.get("id").toString()));
                        achivement.setCount(Integer.parseInt(dataObject.get("count").toString()));
                        achivement.setFinish(Integer.parseInt(dataObject.get("finish").toString()) == 1);
                        achivement.setReceive(Integer.parseInt(dataObject.get("receive").toString()) == 1);
                        AchivementTemplate a = AchiveManager.getInstance().findByID(achivement.getId());
                        achivement.setName(a.getName());
                        achivement.setDetail(a.getDetail());
                        achivement.setMaxCount(a.getMaxCount());
                        achivement.setMoney(a.getMoney());
                        player.playerTask.achivements.add(achivement);
                    }

                    List<AchivementTemplate> listAchive = AchiveManager.getInstance().getList();
                    if (dataArray.size() < listAchive.size()) { //add thêm nhiệm vụ khi có nhiệm vụ mới
                        for (int i = dataArray.size(); i < listAchive.size(); i++) {
                            AchivementTemplate a = AchiveManager.getInstance().findByID(i);
                            Achivement achivement = new Achivement();
                            if (a != null) {
                                achivement.setId(a.getId());
                                achivement.setCount(0);
                                achivement.setFinish(false);
                                achivement.setReceive(false);
                                achivement.setName(a.getName());
                                achivement.setDetail(a.getDetail());
                                achivement.setMaxCount(a.getMaxCount());
                                achivement.setMoney(a.getMoney());
                                player.playerTask.achivements.add(achivement);
                            }
                        }
                    }
                    dataArray.clear();

                    //data trứng bư
                    dataObject = (JSONObject) jv.parse(rs.getString("data_mabu_egg"));
                    Object createTime = dataObject.get("create_time");
                    if (createTime != null) {
                        player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(createTime)),
                                Long.parseLong(String.valueOf(dataObject.get("time_done"))));
                    }
                    dataObject.clear();

                    //data bùa
                    dataArray = (JSONArray) jv.parse(rs.getString("data_charm"));
                    player.charms.tdTriTue = Long.parseLong(dataArray.get(0).toString());
                    player.charms.tdManhMe = Long.parseLong(dataArray.get(1).toString());
                    player.charms.tdDaTrau = Long.parseLong(dataArray.get(2).toString());
                    player.charms.tdOaiHung = Long.parseLong(dataArray.get(3).toString());
                    player.charms.tdBatTu = Long.parseLong(dataArray.get(4).toString());
                    player.charms.tdDeoDai = Long.parseLong(dataArray.get(5).toString());
                    player.charms.tdThuHut = Long.parseLong(dataArray.get(6).toString());
                    player.charms.tdDeTu = Long.parseLong(dataArray.get(7).toString());
                    player.charms.tdTriTue3 = Long.parseLong(dataArray.get(8).toString());
                    player.charms.tdTriTue4 = Long.parseLong(dataArray.get(9).toString());
                    if (dataArray.size() >= 11) {
                        player.charms.tdDeTuMabu = Long.parseLong(dataArray.get(10).toString());
                    }
                    dataArray.clear();

                    //data drop vàng ngọc
                    dataArray = (JSONArray) jv.parse(rs.getString("drop_vang_ngoc"));
                    player.vangnhat = Long.parseLong(dataArray.get(0).toString());
                    player.hngocnhat = Long.parseLong(dataArray.get(1).toString());
                    dataArray.clear();

                    //data mốc nạp
                    dataArray = (JSONArray) jv.parse(rs.getString("nhan_moc_nap"));
                    player.mot = Integer.parseInt(dataArray.get(0).toString());
                    player.hai = Integer.parseInt(dataArray.get(1).toString());
                    player.ba = Integer.parseInt(dataArray.get(2).toString());
                    player.bon = Integer.parseInt(dataArray.get(3).toString());
                    player.nam = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();
                    
                    //data mốc nạp
                    dataArray = (JSONArray) jv.parse(rs.getString("nhan_moc_nap2"));
                    player.sau = Integer.parseInt(dataArray.get(0).toString());
                    player.bay = Integer.parseInt(dataArray.get(1).toString());
                    player.tam = Integer.parseInt(dataArray.get(2).toString());
                    player.chin = Integer.parseInt(dataArray.get(3).toString());
                    player.muoi = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();
                    
                    //data mốc nạp
                    dataArray = (JSONArray) jv.parse(rs.getString("nhan_moc_nap3"));
                    player.muoiMot = Integer.parseInt(dataArray.get(0).toString());
                    player.muoiHai = Integer.parseInt(dataArray.get(1).toString());
                    player.muoiBa = Integer.parseInt(dataArray.get(2).toString());
                    player.muoiBon = Integer.parseInt(dataArray.get(3).toString());
                    player.muoiLam = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();
                    
                    //data đan dược
                    dataArray = (JSONArray) jv.parse(rs.getString("dan_duoc"));
                    player.bohuyetdan = Integer.parseInt(dataArray.get(0).toString());
                    player.tangnguyendan = Integer.parseInt(dataArray.get(1).toString());
                    player.bokhidan = Integer.parseInt(dataArray.get(2).toString());
                    dataArray.clear();

                    //data chuyển sinh
                    dataArray = (JSONArray) jv.parse(rs.getString("chuyen_sinh"));
                    player.chuyensinh = Integer.parseInt(dataArray.get(0).toString());
                    player.MaxGoldTradeDay = Integer.parseInt(dataArray.get(1).toString());
                    player.chuaco2 = Integer.parseInt(dataArray.get(2).toString());
                    player.chuaco3 = Integer.parseInt(dataArray.get(3).toString());
                    player.chuaco4 = Integer.parseInt(dataArray.get(4).toString());
                    dataArray.clear();

                    //data số may mắn
                    dataArray = (JSONArray) jv.parse(rs.getString("so_may_man"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.sochon = Integer.parseInt(dataArray.get(i).toString());
                        player.soMayMan.add(player.sochon);
                    }
                    dataArray.clear();

                    //data luyện tập off
                    dataArray = (JSONArray) jv.parse(rs.getString("data_offtrain"));
                    player.typetrain = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    player.istrain = Byte.parseByte(String.valueOf(dataArray.get(1))) == 1;
                    dataArray.clear();

                    //data skill
                    dataArray = (JSONArray) jv.parse(rs.getString("skills"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        int tempId = Integer.parseInt(skillTemp.get(0).toString());
                        byte point = Byte.parseByte(skillTemp.get(2).toString());
                        Skill skill = null;
                        if (point != 0) {
                            skill = SkillUtil.createSkill(tempId, point);
                        } else {
                            skill = SkillUtil.createSkillLevel0(tempId);
                        }
                        skill.lastTimeUseThisSkill = Long.parseLong(skillTemp.get(1).toString());
                        if (skillTemp.size() > 3) {
                            skill.currLevel = Short.parseShort(String.valueOf(skillTemp.get(3)));
                        }
                        player.playerSkill.skills.add(skill);
                        skillTemp.clear();
                    }
                    dataArray.clear();

                    //data skill shortcut
                    dataArray = (JSONArray) jv.parse(rs.getString("skills_shortcut"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
                    }
                    for (int i : player.playerSkill.skillShortCut) {
                        if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                            player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                            break;
                        }
                    }
                    if (player.playerSkill.skillSelect == null) {
                        player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                                ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
                    }
                    dataArray.clear();

                    Gson gson = new Gson();
                    List<Card> cards = gson.fromJson(rs.getString("collection_book"), new TypeToken<List<Card>>() {
                    }.getType());

                    CollectionBook book = new CollectionBook(player);
                    if (cards != null) {
                        book.setCards(cards);
                    } else {
                        book.setCards(new ArrayList<>());
                    }
                    book.init();
                    player.setCollectionBook(book);
                    List<Item> itemsBody = player.inventory.itemsBody;
                    while (itemsBody.size() < 11) {
                        itemsBody.add(ItemService.gI().createItemNull());
                    }

                    if (itemsBody.get(7).isNotNullItem()) {
                        MiniPet.callMiniPet(player, (player.inventory.itemsBody.get(7).template.id));
                    }
                    if (itemsBody.get(10).isNotNullItem()) {
                        PetFollow pet = PetFollowManager.gI().findByID(itemsBody.get(10).getId());
                        player.setPetFollow(pet);
                    }
                    player.firstTimeLogin = rs.getTimestamp("firstTimeLogin");

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("buy_limit"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.buyLimit[i] = Byte.parseByte(dataArray.get(i).toString());
                    }

                    dataArray = (JSONArray) JSONValue.parse(rs.getString("reward_limit"));

                    player.rewardLimit = new byte[dataArray.size()];
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.rewardLimit[i] = Byte.parseByte(dataArray.get(i).toString());
                    }

                    //dhvt23
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("challenge"));
                    player.goldChallenge = Integer.parseInt(dataArray.get(0).toString());
                    player.levelWoodChest = Integer.parseInt(dataArray.get(1).toString());
                    player.receivedWoodChest = Integer.parseInt(dataArray.get(2).toString()) == 1;
                    dataArray.clear();

                    //data danh hiệu
                    dataArray = (JSONArray) jv.parse(rs.getString("danh_hieu"));
                    player.isTitleUse1 = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                    player.lastTimeTitle1 = Long.parseLong(String.valueOf(dataArray.get(1)));
                    player.IdDanhHieu_1 = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    player.isTitleUse2 = Integer.parseInt(String.valueOf(dataArray.get(3))) == 1 ? true : false;
                    player.lastTimeTitle2 = Long.parseLong(String.valueOf(dataArray.get(4)));
                    player.IdDanhHieu_2 = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.isTitleUse3 = Integer.parseInt(String.valueOf(dataArray.get(6))) == 1 ? true : false;
                    player.lastTimeTitle3 = Long.parseLong(String.valueOf(dataArray.get(7)));
                    player.IdDanhHieu_3 = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    player.isTitleUse4 = Integer.parseInt(String.valueOf(dataArray.get(9))) == 1 ? true : false;
                    player.lastTimeTitle4 = Long.parseLong(String.valueOf(dataArray.get(10)));
                    player.IdDanhHieu_4 = Integer.parseInt(String.valueOf(dataArray.get(11)));
                    player.isTitleUse5 = Integer.parseInt(String.valueOf(dataArray.get(12))) == 1 ? true : false;
                    player.lastTimeTitle5 = Long.parseLong(String.valueOf(dataArray.get(13)));
                    player.IdDanhHieu_5 = Integer.parseInt(String.valueOf(dataArray.get(14)));
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle1))) {
                        player.isTitleUse1 = false;
                        player.lastTimeTitle1 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle2))) {
                        player.isTitleUse2 = false;
                        player.lastTimeTitle2 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle3))) {
                        player.isTitleUse3 = false;
                        player.lastTimeTitle3 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle4))) {
                        player.isTitleUse4 = false;
                        player.lastTimeTitle4 = 0;
                    }
                    if ("Hết hạn".equals(Util.msToTime(player.lastTimeTitle5))) {
                        player.isTitleUse5 = false;
                        player.lastTimeTitle5 = 0;
                    }
                    dataArray.clear();
                    
                    //data limit
                    dataArray = (JSONArray) jv.parse(rs.getString("reset_ngay"));
                    player.bongtai = Integer.parseInt(dataArray.get(0).toString());
                    player.thiensu = Integer.parseInt(dataArray.get(1).toString());
                    dataArray.clear();

                    PlayerService.gI().dailyLogin(player);

                    //data pet
                    dataObject = (JSONObject) jv.parse(rs.getString("pet_info"));
                    if (!String.valueOf(dataObject).equals("{}")) {
                        Pet pet = new Pet(player);
                        pet.id = -player.id;
                        pet.gender = Byte.parseByte(String.valueOf(dataObject.get("gender")));
                        pet.typePet = Byte.parseByte(String.valueOf(dataObject.get("is_mabu")));
                        pet.name = String.valueOf(dataObject.get("name"));
                        player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataObject.get("type_fusion")));
                        player.fusion.lastTimeFusion = System.currentTimeMillis()
                                - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataObject.get("left_fusion"))));
                        pet.status = Byte.parseByte(String.valueOf(dataObject.get("status")));

                        //data chỉ số
                        dataObject = (JSONObject) jv.parse(rs.getString("pet_point"));
                        pet.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
                        pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
                        pet.nPoint.hpg = Double.parseDouble(String.valueOf(dataObject.get("hpg")));
                        pet.nPoint.mpg = Double.parseDouble(String.valueOf(dataObject.get("mpg")));
                        pet.nPoint.dameg = Double.parseDouble(String.valueOf(dataObject.get("damg")));
                        pet.nPoint.defg = Double.parseDouble(String.valueOf(dataObject.get("defg")));
                        pet.nPoint.critg = Integer.parseInt(String.valueOf(dataObject.get("critg")));
                        pet.nPoint.power = Double.parseDouble(String.valueOf(dataObject.get("power")));
                        pet.nPoint.tiemNang = Double.parseDouble(String.valueOf(dataObject.get("tiem_nang")));
                        pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                        double hp = Double.parseDouble(String.valueOf(dataObject.get("hp")));
                        double mp = Double.parseDouble(String.valueOf(dataObject.get("mp")));

                        //data body
                        dataArray = (JSONArray) jv.parse(rs.getString("pet_body"));
                        for (int i = 0; i < dataArray.size(); i++) {
                            dataObject = (JSONObject) dataArray.get(i);
                            Item item = null;
                            short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                            if (tempId != -1) {
                                item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                                JSONArray options = (JSONArray) dataObject.get("option");
                                for (int j = 0; j < options.size(); j++) {
                                    JSONArray opt = (JSONArray) options.get(j);
                                    item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                            Integer.parseInt(String.valueOf(opt.get(1)))));
                                }
                                item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                                if (ItemService.gI().isOutOfDateTime(item)) {
                                    item = ItemService.gI().createItemNull();
                                }
                            } else {
                                item = ItemService.gI().createItemNull();
                            }
                            pet.inventory.itemsBody.add(item);
                        }

                        //data skills
                        dataArray = (JSONArray) jv.parse(rs.getString("pet_skill"));
                        for (int i = 0; i < dataArray.size(); i++) {
                            JSONArray skillTemp = (JSONArray) dataArray.get(i);
                            int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                            byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                            Skill skill = null;
                            if (point != 0) {
                                skill = SkillUtil.createSkill(tempId, point);
                            } else {
                                skill = SkillUtil.createSkillLevel0(tempId);
                            }
                            switch (skill.template.id) {
                                case Skill.KAMEJOKO:
                                case Skill.MASENKO:
                                case Skill.ANTOMIC:
                                    skill.coolDown = 1000;
                                    break;
                            }
                            pet.playerSkill.skills.add(skill);
                        }
                        pet.nPoint.hp = hp;
                        pet.nPoint.mp = mp;
//                    pet.nPoint.calPoint();
                        player.pet = pet;
                    }
                    player.nPoint.hp = plHp;
                    player.nPoint.mp = plMp;
                    return player;
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
