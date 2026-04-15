package nro.manager;

import lombok.Getter;
import nro.jdbc.DBService;
import nro.jdbc.daos.PlayerDAO;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.services.ItemService;
import nro.utils.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nro.models.player.Fusion;
import nro.models.player.Pet;
import nro.models.skill.Skill;
import nro.models.task.TaskMain;
import nro.server.io.Session;
import nro.services.TaskService;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class TopManager {

    @Getter
    public List<Player> listSm = new ArrayList<>();
    @Getter
    public List<Player> listDetu = new ArrayList<>();
    @Getter
    public List<Player> listNvu = new ArrayList<>();
    @Getter
    public List<Player> listNap = new ArrayList<>();
    @Getter
    public List<Player> listSieuHang = new ArrayList<>();
    private static final TopManager INSTANCE = new TopManager();

    public static TopManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        listSm.clear();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("SELECT * FROM player "
                    + "INNER JOIN account ON account.id = player.account_id "
                    + "WHERE account.is_admin = 0 AND account.ban = 0"
                    + " ORDER BY "
                    + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(chuyen_sinh, ',', 1), '[', -1) AS UNSIGNED) DESC,"
                    + "player.power DESC LIMIT 20");
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");

                dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                player.nPoint.power = Long.parseLong(dataArray.get(1).toString());
                dataArray.clear();

                //data chuyển sinh
                dataArray = (JSONArray) jv.parse(rs.getString("chuyen_sinh"));
                player.chuyensinh = Integer.parseInt(dataArray.get(0).toString());
                player.MaxGoldTradeDay = Integer.parseInt(dataArray.get(1).toString());
                player.chuaco2 = Integer.parseInt(dataArray.get(2).toString());
                player.chuaco3 = Integer.parseInt(dataArray.get(3).toString());
                player.chuaco4 = Integer.parseInt(dataArray.get(4).toString());
                dataArray.clear();

                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
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

                listSm.add(player);
            }
        } catch (Exception e) {
            Log.error(PlayerDAO.class, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void load1() {
        listDetu.clear();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("SELECT * FROM player "
                    + "INNER JOIN account ON account.id = player.account_id "
                    + "WHERE account.is_admin = 0 AND account.ban = 0"
                    + " ORDER BY CAST(JSON_EXTRACT(pet_info, '$.is_mabu') AS SIGNED) DESC,"
                    + "player.pet_power DESC LIMIT 20");
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                if (player.pet != null) {
                    player.pet.nPoint.power = rs.getByte("pet_power");
                }

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
                    pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataObject.get("hpg")));
                    pet.nPoint.mpg = Double.parseDouble(String.valueOf(dataObject.get("mpg")));
                    pet.nPoint.dameg = Double.parseDouble(String.valueOf(dataObject.get("damg")));
                    pet.nPoint.defg = Double.parseDouble(String.valueOf(dataObject.get("defg")));
                    pet.nPoint.critg = Integer.parseInt(String.valueOf(dataObject.get("critg")));
                    pet.nPoint.power = Double.parseDouble(String.valueOf(dataObject.get("power")));
                    pet.nPoint.tiemNang = Double.parseDouble(String.valueOf(dataObject.get("tiem_nang")));
                    pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                    pet.nPoint.hp = Integer.parseInt(String.valueOf(dataObject.get("hp")));
                    pet.nPoint.mp = Integer.parseInt(String.valueOf(dataObject.get("mp")));

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
//                    pet.nPoint.calPoint();
                    player.pet = pet;
                }

                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
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

                listDetu.add(player);
            }
        } catch (Exception e) {
            Log.error(PlayerDAO.class, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void load2() {
        listNvu.clear();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("SELECT * FROM player "
                    + "INNER JOIN account ON account.id = player.account_id "
                    + "WHERE account.is_admin = 0 AND account.ban = 0"
                    + " ORDER BY "
                    + " CAST(SUBSTRING_INDEX (SUBSTRING_INDEX (data_task, ',', 1), '[', 2) AS UNSIGNED) DESC,"
                    + " CAST(SUBSTRING_INDEX (data_task, ',', 2) AS UNSIGNED) DESC,"
                    + " CAST(SUBSTRING_INDEX (data_point, ',', 2) AS UNSIGNED) DESC LIMIT 20");
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");

                dataArray = (JSONArray) jv.parse(rs.getString("data_task"));
                TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(dataArray.get(1).toString()));
                taskMain.subTasks.get(Integer.parseInt(dataArray.get(2).toString())).count = Short.parseShort(dataArray.get(0).toString());
                taskMain.index = Byte.parseByte(dataArray.get(2).toString());
                player.playerTask.taskMain = taskMain;
                dataArray.clear();

                dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                player.nPoint.power = Double.parseDouble(dataArray.get(1).toString());
                dataArray.clear();

                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
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

                listNvu.add(player);
            }
        } catch (Exception e) {
            Log.error(PlayerDAO.class, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void load3() {
        listNap.clear();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("SELECT * FROM player "
                    + "INNER JOIN account ON account.id = player.account_id "
                    + "WHERE account.is_admin = 0 AND account.ban = 0"
                    + " ORDER BY player.tong_nap DESC LIMIT 20");
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.tongnap = rs.getInt("tong_nap");

                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
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

                listNap.add(player);
            }
        } catch (Exception e) {
            Log.error(PlayerDAO.class, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void loadSieuHang() {
        listSieuHang.clear();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("SELECT * FROM player "
                    + "INNER JOIN account ON account.id = player.account_id "
                    //                    + "WHERE account.is_admin = 0 AND account.ban = 0"
                    + " ORDER BY "
                    + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(rank_sieu_hang, ',', 1), '[', -1) AS UNSIGNED) ASC LIMIT 20");
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");

                //data siêu hạng
                dataArray = (JSONArray) jv.parse(rs.getString("rank_sieu_hang"));
                player.rankSieuHang = Integer.parseInt(dataArray.get(0).toString());
                player.timesieuhang = Long.parseLong(dataArray.get(1).toString());
                player.isnhanthuong1 = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                player.nPoint.hpMax = Double.parseDouble(dataArray.get(3).toString());
                player.nPoint.mpMax = Double.parseDouble(dataArray.get(4).toString());
                player.nPoint.dame = Double.parseDouble(dataArray.get(5).toString());
                dataArray.clear();

                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
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

                listSieuHang.add(player);
            }
        } catch (Exception e) {
            Log.error(PlayerDAO.class, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

}
