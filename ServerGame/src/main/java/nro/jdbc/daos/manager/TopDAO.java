package nro.jdbc.daos.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import nro.models.item.Item;
import nro.models.player.Fusion;
import nro.models.player.Pet;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.models.task.TaskMain;
import nro.services.ItemService;
import nro.services.TaskService;
import nro.utils.Log;
import nro.utils.SkillUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopDAO {

   private static final Gson gson = new Gson();

   public static List<Player> loadTopPower(Connection con) {
      List<Player> list = new ArrayList<>();
      String sql = "SELECT * FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.is_admin = 0 AND account.ban = 0"
            + " ORDER BY "
            + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(chuyen_sinh, ',', 1), '[', -1) AS UNSIGNED) DESC,"
            + "player.power DESC LIMIT 20";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            Player player = parseBasePlayer(rs);

            // Power from data_point
            JsonArray dataPoint = gson.fromJson(rs.getString("data_point"), JsonArray.class);
            if (dataPoint != null && dataPoint.size() > 1) {
               player.nPoint.power = dataPoint.get(1).getAsLong();
            }

            // Chuyen sinh data
            JsonArray chuyenSinh = gson.fromJson(rs.getString("chuyen_sinh"), JsonArray.class);
            if (chuyenSinh != null && chuyenSinh.size() >= 5) {
               player.chuyensinh = chuyenSinh.get(0).getAsInt();
               player.MaxGoldTradeDay = chuyenSinh.get(1).getAsInt();
               player.chuaco2 = chuyenSinh.get(2).getAsInt();
               player.chuaco3 = chuyenSinh.get(3).getAsInt();
               player.chuaco4 = chuyenSinh.get(4).getAsInt();
            }

            loadItemsBody(player, rs.getString("items_body"));
            list.add(player);
         }
      } catch (SQLException e) {
         Log.error(TopDAO.class, e);
      }
      return list;
   }

   public static List<Player> loadTopPet(Connection con) {
      List<Player> list = new ArrayList<>();
      String sql = "SELECT * FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.is_admin = 0 AND account.ban = 0"
            + " ORDER BY CAST(JSON_EXTRACT(pet_info, '$.is_mabu') AS SIGNED) DESC,"
            + "player.pet_power DESC LIMIT 20";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            Player player = parseBasePlayer(rs);
            player.pet_power = rs.getDouble("pet_power");

            // Pet Info
            JsonObject petInfo = gson.fromJson(rs.getString("pet_info"), JsonObject.class);
            if (petInfo != null && petInfo.size() > 0) {
               Pet pet = new Pet(player);
               pet.id = -player.id;
               pet.gender = petInfo.get("gender").getAsByte();
               pet.typePet = petInfo.get("is_mabu").getAsByte();
               pet.name = petInfo.get("name").getAsString();
               player.fusion.typeFusion = petInfo.get("type_fusion").getAsByte();
               player.fusion.lastTimeFusion = System.currentTimeMillis()
                     - (Fusion.TIME_FUSION - petInfo.get("left_fusion").getAsInt());
               pet.status = petInfo.get("status").getAsByte();

               // Pet Point
               JsonObject petPoint = gson.fromJson(rs.getString("pet_point"), JsonObject.class);
               if (petPoint != null) {
                  pet.nPoint.stamina = petPoint.get("stamina").getAsShort();
                  pet.nPoint.maxStamina = petPoint.get("max_stamina").getAsShort();
                  pet.nPoint.hpg = petPoint.get("hpg").getAsInt();
                  pet.nPoint.mpg = petPoint.get("mpg").getAsDouble();
                  pet.nPoint.dameg = petPoint.get("damg").getAsDouble();
                  pet.nPoint.defg = petPoint.get("defg").getAsDouble();
                  pet.nPoint.critg = petPoint.get("critg").getAsInt();
                  pet.nPoint.power = petPoint.get("power").getAsDouble();
                  pet.nPoint.tiemNang = petPoint.get("tiem_nang").getAsDouble();
                  pet.nPoint.limitPower = petPoint.get("limit_power").getAsByte();
                  pet.nPoint.hp = petPoint.get("hp").getAsInt();
                  pet.nPoint.mp = petPoint.get("mp").getAsInt();
               }

               // Pet Body
               loadItemsBody(pet, rs.getString("pet_body"));

               // Pet Skill
               JsonArray petSkills = gson.fromJson(rs.getString("pet_skill"), JsonArray.class);
               if (petSkills != null) {
                  for (JsonElement e : petSkills) {
                     JsonArray s = e.getAsJsonArray();
                     int tempId = s.get(0).getAsInt();
                     byte point = s.get(1).getAsByte();
                     Skill skill = (point != 0) ? SkillUtil.createSkill(tempId, point)
                           : SkillUtil.createSkillLevel0(tempId);
                     if (skill.template.id == Skill.KAMEJOKO || skill.template.id == Skill.MASENKO
                           || skill.template.id == Skill.ANTOMIC) {
                        skill.coolDown = 1000;
                     }
                     pet.playerSkill.skills.add(skill);
                  }
               }
               player.pet = pet;
            }

            loadItemsBody(player, rs.getString("items_body"));
            list.add(player);
         }
      } catch (SQLException e) {
         Log.error(TopDAO.class, e);
      }
      return list;
   }

   public static List<Player> loadTopTask(Connection con) {
      List<Player> list = new ArrayList<>();
      String sql = "SELECT * FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.is_admin = 0 AND account.ban = 0"
            + " ORDER BY "
            + " CAST(SUBSTRING_INDEX (SUBSTRING_INDEX (data_task, ',', 1), '[', 2) AS UNSIGNED) DESC,"
            + " CAST(SUBSTRING_INDEX (data_task, ',', 2) AS UNSIGNED) DESC,"
            + " CAST(SUBSTRING_INDEX (data_point, ',', 2) AS UNSIGNED) DESC LIMIT 20";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            Player player = parseBasePlayer(rs);

            // Task Data
            JsonArray taskData = gson.fromJson(rs.getString("data_task"), JsonArray.class);
            if (taskData != null && taskData.size() >= 3) {
               TaskMain taskMain = TaskService.gI().getTaskMainById(player, taskData.get(1).getAsByte());
               taskMain.subTasks.get(taskData.get(2).getAsInt()).count = taskData.get(0).getAsShort();
               taskMain.index = taskData.get(2).getAsByte();
               player.playerTask.taskMain = taskMain;
            }

            // Power
            JsonArray dataPoint = gson.fromJson(rs.getString("data_point"), JsonArray.class);
            if (dataPoint != null && dataPoint.size() > 1) {
               player.nPoint.power = dataPoint.get(1).getAsDouble();
            }

            loadItemsBody(player, rs.getString("items_body"));
            list.add(player);
         }
      } catch (SQLException e) {
         Log.error(TopDAO.class, e);
      }
      return list;
   }

   public static List<Player> loadTopDonate(Connection con) {
      List<Player> list = new ArrayList<>();
      String sql = "SELECT * FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + "WHERE account.is_admin = 0 AND account.ban = 0"
            + " ORDER BY player.tong_nap DESC LIMIT 20";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            Player player = parseBasePlayer(rs);
            player.tongnap = rs.getInt("tong_nap");
            loadItemsBody(player, rs.getString("items_body"));
            list.add(player);
         }
      } catch (SQLException e) {
         Log.error(TopDAO.class, e);
      }
      return list;
   }

   public static List<Player> loadTopSieuHang(Connection con) {
      List<Player> list = new ArrayList<>();
      String sql = "SELECT * FROM player "
            + "INNER JOIN account ON account.id = player.account_id "
            + " ORDER BY "
            + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(rank_sieu_hang, ',', 1), '[', -1) AS UNSIGNED) ASC LIMIT 20";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            Player player = parseBasePlayer(rs);

            // Rank Sieu Hang
            JsonArray rankData = gson.fromJson(rs.getString("rank_sieu_hang"), JsonArray.class);
            if (rankData != null && rankData.size() >= 6) {
               player.rankSieuHang = rankData.get(0).getAsInt();
               player.timesieuhang = rankData.get(1).getAsLong();
               player.isnhanthuong1 = (player.rankSieuHang == 1);
               player.nPoint.hpMax = rankData.get(3).getAsDouble();
               player.nPoint.mpMax = rankData.get(4).getAsDouble();
               player.nPoint.dame = rankData.get(5).getAsDouble();
            }

            loadItemsBody(player, rs.getString("items_body"));
            list.add(player);
         }
      } catch (SQLException e) {
         Log.error(TopDAO.class, e);
      }
      return list;
   }

   private static Player parseBasePlayer(ResultSet rs) throws SQLException {
      Player player = new Player();
      player.id = rs.getInt("id");
      player.name = rs.getString("name");
      player.head = rs.getShort("head");
      player.gender = rs.getByte("gender");
      return player;
   }

   private static void loadItemsBody(Player player, String json) {
      if (json == null || json.isEmpty() || json.equals("[]"))
         return;
      try {
         List<ItemData> itemsData = gson.fromJson(json, new TypeToken<List<ItemData>>() {
         }.getType());
         for (ItemData d : itemsData) {
            Item item;
            if (d.temp_id != -1) {
               item = ItemService.gI().createNewItem(d.temp_id, d.quantity);
               item.itemOptions.addAll(ServiceDataDAO.parseItemOptions(d.option));
               item.createTime = d.create_time;
               if (ItemService.gI().isOutOfDateTime(item)) {
                  item = ItemService.gI().createItemNull();
               }
            } else {
               item = ItemService.gI().createItemNull();
            }
            player.inventory.itemsBody.add(item);
         }
      } catch (Exception e) {
         Log.error(TopDAO.class, e, "Lỗi load items body Top: " + json);
      }
   }

   private static class ItemData {
      short temp_id;
      int quantity;
      String option;
      long create_time;
   }
}
