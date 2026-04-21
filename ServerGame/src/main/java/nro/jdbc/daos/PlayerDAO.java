package nro.jdbc.daos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nro.consts.ConstMap;
import nro.jdbc.DBService;
import nro.manager.AchiveManager;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.item.ItemTime;
import nro.models.skill.Skill;
import nro.models.task.Achivement;
import nro.models.task.AchivementTemplate;
import nro.server.Manager;
import nro.services.MapService;
import nro.utils.Log;
import nro.utils.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import nro.models.item.ItemTimeSieuCap;
import nro.models.player.Friend;
import nro.models.player.Fusion;
import nro.models.player.Player;
import nro.services.PhongThiNghiem;
import nro.services.RuongSuuTam;

public class PlayerDAO {

   public static boolean updateTimeLogout;

   private static final Gson gson = new Gson();

   private static String saveItems(List<Item> items) {
      JsonArray dataBody = new JsonArray();
      for (Item item : items) {
         JsonObject dataItem = new JsonObject();
         if (item.isNotNullItem()) {
            JsonArray options = new JsonArray();
            dataItem.addProperty("temp_id", item.template.id);
            dataItem.addProperty("quantity", item.quantity);
            dataItem.addProperty("create_time", item.createTime);
            for (ItemOption io : item.itemOptions) {
               JsonArray option = new JsonArray();
               option.add(io.optionTemplate.id);
               option.add(io.param);
               options.add(option);
            }
            dataItem.add("option", options);
         } else {
            dataItem.addProperty("temp_id", -1);
            dataItem.addProperty("quantity", 0);
            dataItem.addProperty("create_time", 0L);
            dataItem.add("option", new JsonArray());
         }
         dataBody.add(dataItem);
      }
      return gson.toJson(dataBody);
   }

   private static String saveInventory(Player player) {
      JsonArray dataInventory = new JsonArray();
      dataInventory.add(player.inventory.gold);
      dataInventory.add(player.inventory.gem);
      dataInventory.add(player.inventory.ruby);
      dataInventory.add(player.inventory.goldLimit);
      return gson.toJson(dataInventory);
   }

   private static String saveLocation(Player player) {
      int mapId = player.mapIdBeforeLogout;
      int x = player.location.x;
      int y = player.location.y;
      if (player.isDie()) {
         mapId = player.gender + 21;
         x = 300;
         y = 336;
      } else {
         if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId) || mapId == 126
               || mapId == ConstMap.CON_DUONG_RAN_DOC
               || mapId == ConstMap.CON_DUONG_RAN_DOC_142 || mapId == ConstMap.CON_DUONG_RAN_DOC_143
               || mapId == ConstMap.HOANG_MAC) {
            mapId = player.gender + 21;
            x = 300;
            y = 336;
         }
      }
      JsonArray dataLocation = new JsonArray();
      dataLocation.add(x);
      dataLocation.add(y);
      dataLocation.add(mapId);
      return gson.toJson(dataLocation);
   }

   private static String savePoint(Player player) {
      JsonArray dataPoint = new JsonArray();
      dataPoint.add(player.nPoint.limitPower);
      dataPoint.add(player.nPoint.power);
      dataPoint.add(player.nPoint.tiemNang);
      dataPoint.add(player.nPoint.stamina);
      dataPoint.add(player.nPoint.maxStamina);
      dataPoint.add(player.nPoint.hpg);
      dataPoint.add(player.nPoint.mpg);
      dataPoint.add(player.nPoint.dameg);
      dataPoint.add(player.nPoint.defg);
      dataPoint.add(player.nPoint.critg);
      dataPoint.add(0);
      dataPoint.add(player.nPoint.hp);
      dataPoint.add(player.nPoint.mp);
      return gson.toJson(dataPoint);
   }

   private static String saveMagicTree(Player player) {
      JsonArray dataMagicTree = new JsonArray();
      dataMagicTree.add(player.magicTree.isUpgrade ? 1 : 0);
      dataMagicTree.add(player.magicTree.lastTimeUpgrade);
      dataMagicTree.add(player.magicTree.level);
      dataMagicTree.add(player.magicTree.lastTimeHarvest);
      dataMagicTree.add(player.magicTree.currPeas);
      return gson.toJson(dataMagicTree);
   }

   private static String saveFriends(List<? extends Friend> friends) {
      JsonArray dataFriends = new JsonArray();
      for (Friend f : friends) {
         JsonObject friend = new JsonObject();
         friend.addProperty("id", f.id);
         friend.addProperty("name", f.name);
         friend.addProperty("power", f.power);
         friend.addProperty("head", f.head);
         friend.addProperty("body", f.body);
         friend.addProperty("leg", f.leg);
         friend.addProperty("bag", f.bag);
         dataFriends.add(friend);
      }
      return gson.toJson(dataFriends);
   }

   private static String saveIntrinsic(Player player) {
      JsonArray dataIntrinsic = new JsonArray();
      dataIntrinsic.add(player.playerIntrinsic.intrinsic.id);
      dataIntrinsic.add(player.playerIntrinsic.intrinsic.param1);
      dataIntrinsic.add(player.playerIntrinsic.countOpen);
      dataIntrinsic.add(player.playerIntrinsic.intrinsic.param2);
      return gson.toJson(dataIntrinsic);
   }

   private static String saveItemTime(Player player) {
      JsonArray dataItemTime = new JsonArray();
      long now = System.currentTimeMillis();
      dataItemTime.add(player.itemTime.isUseBoKhi ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeBoKhi)) : 0);
      dataItemTime.add(player.itemTime.isUseAnDanh ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeAnDanh)) : 0);
      dataItemTime.add(
            player.itemTime.isOpenPower ? (ItemTime.TIME_OPEN_POWER - (now - player.itemTime.lastTimeOpenPower)) : 0);
      dataItemTime
            .add(player.itemTime.isUseCuongNo ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeCuongNo)) : 0);
      dataItemTime
            .add(player.itemTime.isUseMayDo ? (ItemTime.TIME_MAY_DO - (now - player.itemTime.lastTimeUseMayDo)) : 0);
      dataItemTime
            .add(player.itemTime.isUseBoHuyet ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeBoHuyet)) : 0);
      dataItemTime.add(player.itemTime.iconMeal);
      dataItemTime
            .add(player.itemTime.isEatMeal ? (ItemTime.TIME_EAT_MEAL - (now - player.itemTime.lastTimeEatMeal)) : 0);
      dataItemTime
            .add(player.itemTime.isUseGiapXen ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeGiapXen)) : 0);
      dataItemTime
            .add(player.itemTime.isUseBanhChung ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeBanhChung)) : 0);
      dataItemTime
            .add(player.itemTime.isUseBanhTet ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeBanhTet)) : 0);
      dataItemTime.add(player.itemTime.isUseBoKhi2 ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeBoKhi2)) : 0);
      dataItemTime
            .add(player.itemTime.isUseGiapXen2 ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeGiapXen2)) : 0);
      dataItemTime
            .add(player.itemTime.isUseCuongNo2 ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeCuongNo2)) : 0);
      dataItemTime
            .add(player.itemTime.isUseBoHuyet2 ? (ItemTime.TIME_ITEM - (now - player.itemTime.lastTimeBoHuyet2)) : 0);
      return gson.toJson(dataItemTime);
   }

   private static String saveItemTimeSC(Player player) {
      JsonArray dataItemTimeSC = new JsonArray();
      long now = System.currentTimeMillis();
      dataItemTimeSC.add(player.itemTimesieucap.isDuoikhi
            ? (ItemTimeSieuCap.TIME_ITEM_SC_10P - (now - player.itemTimesieucap.lastTimeDuoikhi))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isDaNgucTu
            ? (ItemTimeSieuCap.TIME_ITEM_SC_10P - (now - player.itemTimesieucap.lastTimeDaNgucTu))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isUseCaRot
            ? (ItemTimeSieuCap.TIME_ITEM_SC_10P - (now - player.itemTimesieucap.lastTimeCaRot))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isKeo
            ? (ItemTimeSieuCap.TIME_ITEM_SC_30P - (now - player.itemTimesieucap.lastTimeKeo))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isUseXiMuoi
            ? (ItemTimeSieuCap.TIME_ITEM_SC_10P - (now - player.itemTimesieucap.lastTimeUseXiMuoi))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.iconBanh);
      dataItemTimeSC.add(player.itemTimesieucap.isUseTrungThu
            ? (ItemTimeSieuCap.TIME_TRUNGTHU - (now - player.itemTimesieucap.lastTimeUseBanh))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isChoido
            ? (ItemTimeSieuCap.TIME_ITEM_SC_10P - (now - player.itemTimesieucap.lasttimeChoido))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isRongSieuCap
            ? (ItemTimeSieuCap.TIME_ITEM_SC_30P - (now - player.itemTimesieucap.lastTimeRongSieuCap))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isRongBang
            ? (ItemTimeSieuCap.TIME_ITEM_SC_30P - (now - player.itemTimesieucap.lastTimeRongBang))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.isEatMeal
            ? (ItemTimeSieuCap.TIME_ITEM_SC_10P - (now - player.itemTimesieucap.lastTimeMeal))
            : 0);
      dataItemTimeSC.add(player.itemTimesieucap.iconMeal);
      dataItemTimeSC.add(player.itemTimesieucap.isBienhinhSc
            ? (ItemTimeSieuCap.TIME_ITEM_SC_10P - (now - player.itemTimesieucap.lastTimeBienhinhSc))
            : 0);
      return gson.toJson(dataItemTimeSC);
   }

   private static String saveTask(Player player) {
      JsonArray dataTask = new JsonArray();
      dataTask.add(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
      dataTask.add(player.playerTask.taskMain.id);
      dataTask.add(player.playerTask.taskMain.index);
      return gson.toJson(dataTask);
   }

   private static String saveSideTask(Player player) {
      JsonArray dataSideTask = new JsonArray();
      dataSideTask.add(player.playerTask.sideTask.level);
      dataSideTask.add(player.playerTask.sideTask.count);
      dataSideTask.add(player.playerTask.sideTask.leftTask);
      dataSideTask.add(player.playerTask.sideTask.template != null ? player.playerTask.sideTask.template.id : -1);
      dataSideTask.add(player.playerTask.sideTask.receivedTime);
      dataSideTask.add(player.playerTask.sideTask.maxCount);
      return gson.toJson(dataSideTask);
   }

   private static String saveAchivements(Player player) {
      JsonArray dataAchive = new JsonArray();
      for (Achivement a : player.playerTask.achivements) {
         JsonObject jobj = new JsonObject();
         jobj.addProperty("id", a.id);
         jobj.addProperty("count", a.getCount());
         jobj.addProperty("finish", a.isFinish ? 1 : 0);
         jobj.addProperty("receive", a.isReceive ? 1 : 0);
         dataAchive.add(jobj);
      }
      return gson.toJson(dataAchive);
   }

   private static String saveMabuEgg(Player player) {
      JsonObject dataMaBu = new JsonObject();
      if (player.mabuEgg != null) {
         dataMaBu.addProperty("create_time", player.mabuEgg.lastTimeCreate);
         dataMaBu.addProperty("time_done", player.mabuEgg.timeDone);
      }
      return gson.toJson(dataMaBu);
   }

   private static String saveCharms(Player player) {
      JsonArray dataCharms = new JsonArray();
      dataCharms.add(player.charms.tdTriTue);
      dataCharms.add(player.charms.tdManhMe);
      dataCharms.add(player.charms.tdDaTrau);
      dataCharms.add(player.charms.tdOaiHung);
      dataCharms.add(player.charms.tdBatTu);
      dataCharms.add(player.charms.tdDeoDai);
      dataCharms.add(player.charms.tdThuHut);
      dataCharms.add(player.charms.tdDeTu);
      dataCharms.add(player.charms.tdTriTue3);
      dataCharms.add(player.charms.tdTriTue4);
      dataCharms.add(player.charms.tdDeTuMabu);
      return gson.toJson(dataCharms);
   }

   private static String saveSkills(Player player) {
      JsonArray dataSkills = new JsonArray();
      for (Skill skill : player.playerSkill.skills) {
         JsonArray dataskill = new JsonArray();
         dataskill.add(skill.template.id);
         dataskill.add(skill.lastTimeUseThisSkill);
         dataskill.add(skill.point);
         dataskill.add(skill.currLevel);
         dataSkills.add(dataskill);
      }
      return gson.toJson(dataSkills);
   }

   private static String saveSkillShortcut(Player player) {
      JsonArray dataSkillShortcut = new JsonArray();
      for (int skillId : player.playerSkill.skillShortCut) {
         dataSkillShortcut.add(skillId);
      }
      return gson.toJson(dataSkillShortcut);
   }

   private static String savePetInfo(Player player) {
      JsonObject jPetInfo = new JsonObject();
      if (player.pet != null) {
         jPetInfo.addProperty("name", player.pet.name);
         jPetInfo.addProperty("gender", player.pet.gender);
         jPetInfo.addProperty("is_mabu", player.pet.typePet);
         jPetInfo.addProperty("status", player.pet.status);
         jPetInfo.addProperty("type_fusion", player.fusion.typeFusion);
         int timeLeftFusion = (int) (Fusion.TIME_FUSION - (System.currentTimeMillis() - player.fusion.lastTimeFusion));
         jPetInfo.addProperty("left_fusion", Math.max(0, timeLeftFusion));
      }
      return gson.toJson(jPetInfo);
   }

   private static String savePetPoint(Player player) {
      JsonObject jPetPoint = new JsonObject();
      if (player.pet != null) {
         jPetPoint.addProperty("power", player.pet.nPoint.power);
         jPetPoint.addProperty("tiem_nang", player.pet.nPoint.tiemNang);
         jPetPoint.addProperty("stamina", player.pet.nPoint.stamina);
         jPetPoint.addProperty("max_stamina", player.pet.nPoint.maxStamina);
         jPetPoint.addProperty("hpg", player.pet.nPoint.hpg);
         jPetPoint.addProperty("mpg", player.pet.nPoint.mpg);
         jPetPoint.addProperty("damg", player.pet.nPoint.dameg);
         jPetPoint.addProperty("defg", player.pet.nPoint.defg);
         jPetPoint.addProperty("critg", player.pet.nPoint.critg);
         jPetPoint.addProperty("limit_power", player.pet.nPoint.limitPower);
         jPetPoint.addProperty("hp", player.pet.nPoint.hp);
         jPetPoint.addProperty("mp", player.pet.nPoint.mp);
      }
      return gson.toJson(jPetPoint);
   }

   private static String savePetSkills(Player player) {
      JsonArray jPetSkills = new JsonArray();
      if (player.pet != null) {
         for (Skill s : player.pet.playerSkill.skills) {
            JsonArray pskill = new JsonArray();
            if (s.skillId != -1) {
               pskill.add(s.template.id);
               pskill.add(s.point);
            } else {
               pskill.add(-1);
               pskill.add(0);
            }
            jPetSkills.add(pskill);
         }
      }
      return gson.toJson(jPetSkills);
   }

   private static String saveBlackBall(Player player) {
      JsonArray dataBlackBall = new JsonArray();
      for (int i = 0; i < 7; i++) {
         JsonArray data = new JsonArray();
         data.add(player.rewardBlackBall.timeOutOfDateReward[i]);
         data.add(player.rewardBlackBall.lastTimeGetReward[i]);
         dataBlackBall.add(data);
      }
      return gson.toJson(dataBlackBall);
   }

   private static String saveDanhHieu(Player player) {
      JsonArray dataDanhhieu = new JsonArray();
      dataDanhhieu.add(player.isTitleUse1 ? 1 : 0);
      dataDanhhieu.add(player.lastTimeTitle1);
      dataDanhhieu.add(player.IdDanhHieu_1);
      dataDanhhieu.add(player.isTitleUse2 ? 1 : 0);
      dataDanhhieu.add(player.lastTimeTitle2);
      dataDanhhieu.add(player.IdDanhHieu_2);
      dataDanhhieu.add(player.isTitleUse3 ? 1 : 0);
      dataDanhhieu.add(player.lastTimeTitle3);
      dataDanhhieu.add(player.IdDanhHieu_3);
      dataDanhhieu.add(player.isTitleUse4 ? 1 : 0);
      dataDanhhieu.add(player.lastTimeTitle4);
      dataDanhhieu.add(player.IdDanhHieu_4);
      dataDanhhieu.add(player.isTitleUse5 ? 1 : 0);
      dataDanhhieu.add(player.lastTimeTitle5);
      dataDanhhieu.add(player.IdDanhHieu_5);
      return gson.toJson(dataDanhhieu);
   }

   private static String saveKhamNgoc(Player player) {
      return gson.toJson(player.khamNgoc);
   }

   private static String savePhongThiNghiem(Player player) {
      return gson.toJson(player.phongThiNghiem);
   }

   private static String createDefaultInventory() {
      JsonArray dataInventory = new JsonArray();
      dataInventory.add(2000000000); // gold
      dataInventory.add(100000000); // gem
      dataInventory.add(0); // ruby
      return gson.toJson(dataInventory);
   }

   private static String createDefaultLocation(byte gender) {
      JsonArray dataLocation = new JsonArray();
      dataLocation.add(100);
      dataLocation.add(384);
      dataLocation.add(39 + gender);
      return gson.toJson(dataLocation);
   }

   private static String createDefaultPoint(byte gender) {
      JsonArray dataPoint = new JsonArray();
      dataPoint.add(0); // giới hạn sức mạnh
      dataPoint.add(15000000); // sức mạnh
      dataPoint.add(150000000); // tiềm năng
      dataPoint.add(1000); // thể lực
      dataPoint.add(1000); // thể lực đầy
      dataPoint.add(2000); // hp gốc
      dataPoint.add(2000); // ki gốc
      dataPoint.add(333); // sức đánh gốc
      dataPoint.add(0); // giáp gốc
      dataPoint.add(0); // chí mạng gốc
      dataPoint.add(0); // năng động
      dataPoint.add(2000); // hp hiện tại
      dataPoint.add(2000); // ki hiện tại
      return gson.toJson(dataPoint);
   }

   private static String createDefaultMagicTree() {
      JsonArray dataMagicTree = new JsonArray();
      dataMagicTree.add(0); // isUpgrade
      dataMagicTree.add(System.currentTimeMillis());
      dataMagicTree.add(10); // LV
      dataMagicTree.add(System.currentTimeMillis());
      dataMagicTree.add(23); // curr Peas
      return gson.toJson(dataMagicTree);
   }

   private static String createDefaultBody(byte gender) {
      JsonArray dataBody = new JsonArray();
      int idAo = gender == 0 ? 0 : gender == 1 ? 1 : 2;
      int idQuan = gender == 0 ? 6 : gender == 1 ? 7 : 8;
      int def = gender == 2 ? 3 : 2;
      int hp = gender == 0 ? 30 : 20;

      for (int i = 0; i < 17; i++) {
         JsonObject item = new JsonObject();
         JsonArray options = new JsonArray();
         if (i == 0) {
            JsonArray option = new JsonArray();
            option.add(47);
            option.add(def);
            options.add(option);
            item.addProperty("temp_id", idAo);
            item.addProperty("create_time", System.currentTimeMillis());
            item.addProperty("quantity", 1);
         } else if (i == 1) {
            JsonArray option = new JsonArray();
            option.add(6);
            option.add(hp);
            options.add(option);
            item.addProperty("temp_id", idQuan);
            item.addProperty("create_time", System.currentTimeMillis());
            item.addProperty("quantity", 1);
         } else {
            item.addProperty("temp_id", -1);
            item.addProperty("create_time", 0L);
            item.addProperty("quantity", 1);
         }
         item.add("option", options);
         dataBody.add(item);
      }
      return gson.toJson(dataBody);
   }

   private static String createDefaultBag() {
      JsonArray dataBag = new JsonArray();
      for (int i = 0; i < 20; i++) {
         JsonObject item = new JsonObject();
         JsonArray options = new JsonArray();
         if (i == 0) {
            JsonArray option = new JsonArray();
            option.add(30);
            option.add(1);
            options.add(option);
            item.addProperty("temp_id", 1787);
            item.addProperty("create_time", System.currentTimeMillis());
            item.addProperty("quantity", 1);
         } else if (i == 1) {
            JsonArray option = new JsonArray();
            option.add(30);
            option.add(1);
            options.add(option);
            item.addProperty("temp_id", 457);
            item.addProperty("create_time", System.currentTimeMillis());
            item.addProperty("quantity", 999);
         } else {
            item.addProperty("temp_id", -1);
            item.addProperty("create_time", 0L);
            item.addProperty("quantity", 1);
         }
         item.add("option", options);
         dataBag.add(item);
      }
      return gson.toJson(dataBag);
   }

   private static String createDefaultBox() {
      JsonArray dataBox = new JsonArray();
      for (int i = 0; i < 20; i++) {
         JsonObject item = new JsonObject();
         JsonArray options = new JsonArray();
         if (i == 0) {
            item.addProperty("temp_id", 12);
            JsonArray option = new JsonArray();
            option.add(14);
            option.add(1);
            options.add(option);
            item.addProperty("create_time", System.currentTimeMillis());
         } else {
            item.addProperty("temp_id", -1);
            item.addProperty("create_time", 0L);
         }
         item.add("option", options);
         item.addProperty("quantity", 1);
         dataBox.add(item);
      }
      return gson.toJson(dataBox);
   }

   private static String createDefaultLuckyRound() {
      JsonArray dataLuckyRound = new JsonArray();
      for (int i = 0; i < 110; i++) {
         JsonObject item = new JsonObject();
         item.addProperty("temp_id", -1);
         item.add("option", new JsonArray());
         item.addProperty("create_time", 0L);
         item.addProperty("quantity", 1);
         dataLuckyRound.add(item);
      }
      return gson.toJson(dataLuckyRound);
   }

   private static String createDefaultIntrinsic() {
      JsonArray dataIntrinsic = new JsonArray();
      for (int i = 0; i < 4; i++)
         dataIntrinsic.add(0);
      return gson.toJson(dataIntrinsic);
   }

   private static String createDefaultItemTime() {
      JsonArray dataItemTime = new JsonArray();
      for (int i = 0; i < 15; i++)
         dataItemTime.add(0);
      return gson.toJson(dataItemTime);
   }

   private static String createDefaultItemTimeSC() {
      JsonArray dataItemTimeSC = new JsonArray();
      for (int i = 0; i < 12; i++)
         dataItemTimeSC.add(0);
      return gson.toJson(dataItemTimeSC);
   }

   private static String createDefaultTask() {
      JsonArray dataTask = new JsonArray();
      dataTask.add(0); // Task ID
      dataTask.add(0); // Task Sub ID
      dataTask.add(0); // Task Count
      return gson.toJson(dataTask);
   }

   private static String createDefaultAchivements() {
      JsonArray dataAchive = new JsonArray();
      for (AchivementTemplate a : AchiveManager.getInstance().list) {
         JsonObject jobj = new JsonObject();
         jobj.addProperty("id", a.id);
         jobj.addProperty("count", 0);
         jobj.addProperty("finish", 0);
         jobj.addProperty("receive", 0);
         dataAchive.add(jobj);
      }
      return gson.toJson(dataAchive);
   }

   private static String createDefaultCharms() {
      JsonArray dataCharms = new JsonArray();
      for (int i = 0; i < 10; i++)
         dataCharms.add(0);
      return gson.toJson(dataCharms);
   }

   private static String createDefaultSkills(byte gender) {
      int[] skillsArr = gender == 0 ? new int[] { 0, 1, 6, 9, 10, 20, 22, 24, 19, 27 }
            : gender == 1 ? new int[] { 2, 3, 7, 11, 12, 17, 18, 26, 19, 28 }
                  : new int[] { 4, 5, 8, 13, 14, 21, 23, 25, 19, 29 };
      JsonArray dataSkills = new JsonArray();
      for (int i = 0; i < skillsArr.length; i++) {
         JsonArray skill = new JsonArray();
         skill.add(skillsArr[i]);
         skill.add(0);
         skill.add(i <= 1 ? 1 : 0);
         dataSkills.add(skill);
      }
      return gson.toJson(dataSkills);
   }

   private static String createDefaultSkillShortcut(byte gender) {
      JsonArray dataSkillShortcut = new JsonArray();
      dataSkillShortcut.add(gender == 0 ? 0 : gender == 1 ? 2 : 4);
      dataSkillShortcut.add(gender == 0 ? 1 : gender == 1 ? 3 : 5);
      dataSkillShortcut.add(gender == 0 ? 6 : gender == 1 ? 7 : 8);
      for (int i = 0; i < 7; i++)
         dataSkillShortcut.add(-1);
      return gson.toJson(dataSkillShortcut);
   }

   private static String createDefaultBlackBall() {
      JsonArray dataBlackBall = new JsonArray();
      for (int i = 0; i < 7; i++) {
         JsonArray arr = new JsonArray();
         arr.add(0);
         arr.add(0);
         dataBlackBall.add(arr);
      }
      return gson.toJson(dataBlackBall);
   }

   private static String createDefaultKhamNgoc() {
      JsonArray dataKhamNgoc = new JsonArray();
      for (nro.services.KhamNgoc a : nro.services.KhamNgoc.KHAM_NGOC) {
         JsonObject jobjk = new JsonObject();
         jobjk.addProperty("id", a.id);
         jobjk.addProperty("level", -1);
         dataKhamNgoc.add(jobjk);
      }
      return gson.toJson(dataKhamNgoc);
   }

   private static String createDefaultRuongSuuTam() {
      JsonArray dataRuong = new JsonArray();
      for (int i = 0; i < RuongSuuTam.size_ruong; i++) {
         JsonObject item = new JsonObject();
         item.addProperty("temp_id", -1);
         item.addProperty("create_time", 0L);
         item.addProperty("quantity", 0);
         item.add("option", new JsonArray());
         dataRuong.add(item);
      }
      return gson.toJson(dataRuong);
   }

   private static String createDefaultPhongThiNghiem() {
      JsonArray dataPTN = new JsonArray();
      for (int i = 0; i < PhongThiNghiem.SIZE; i++) {
         JsonObject ptn = new JsonObject();
         ptn.addProperty("id", -1);
         ptn.addProperty("time", 0L);
         dataPTN.add(ptn);
      }
      return gson.toJson(dataPTN);
   }

   private static int[] countItems(Player player) {
      int[] counts = new int[4];
      for (Item item : player.inventory.itemsBag) {
         if (item.isNotNullItem()) {
            if (item.template.id == 14)
               counts[0]++;
            else if (item.template.id == 15)
               counts[1]++;
            else if (item.template.id == 16)
               counts[2]++;
            else if (item.template.id == 457)
               counts[3] += item.quantity;
         }
      }
      return counts;
   }

   private static String saveMocNap(int... values) {
      JsonArray array = new JsonArray();
      for (int v : values) {
         array.add(v);
      }
      return gson.toJson(array);
   }

   private static String saveChallenge(Player player) {
      JsonArray array = new JsonArray();
      array.add(player.goldChallenge);
      return gson.toJson(array);
   }

   private static String saveSkTet(Player player) {
      JsonArray array = new JsonArray();
      array.add(player.event.timeCookTetCake);
      array.add(player.event.timeCookChungCake);
      array.add(player.event.cookingTetCake ? 1 : 0);
      array.add(player.event.cookingChungCake ? 1 : 0);
      array.add(player.event.receivedLuckyMoney ? 1 : 0);
      return gson.toJson(array);
   }

   private static String saveBuyLimit(Player player) {
      JsonArray array = new JsonArray();
      for (byte b : player.buyLimit) {
         array.add(b);
      }
      return gson.toJson(array);
   }

   private static String saveRewardLimit(Player player) {
      JsonArray array = new JsonArray();
      for (byte b : player.rewardLimit) {
         array.add(b);
      }
      return gson.toJson(array);
   }

   private static String saveVangNgoc(Player player) {
      JsonArray array = new JsonArray();
      array.add(player.vangnhat);
      array.add(player.hngocnhat);
      return gson.toJson(array);
   }

   private static String saveOnline(Player player) {
      JsonArray array = new JsonArray();
      for (int i : player.listOnline) {
         array.add(i);
      }
      return gson.toJson(array);
   }

   private static String saveDiemDanh(Player player) {
      JsonArray array = new JsonArray();
      for (int i : player.listDiemDanh) {
         array.add(i);
      }
      return gson.toJson(array);
   }

   private static String saveLuyentap(Player player) {
      JsonArray array = new JsonArray();
      array.add((int) player.typetrain);
      array.add(player.istrain ? 1 : 0);
      return gson.toJson(array);
   }

   private static String saveResetDay(Player player) {
      JsonArray array = new JsonArray();
      array.add(player.bongtai);
      array.add(player.thiensu);
      return gson.toJson(array);
   }

   public static void createNewPlayer(Connection con, int userId, String name, byte gender, int hair) {
      PreparedStatement ps = null;
      try {
         ps = con.prepareStatement("insert into player"
               + "(account_id, name, head, gender, have_tennis_space_ship, clan_id_sv" + Manager.SERVER + ", "
               + "data_inventory, data_location, data_point, data_magic_tree, items_body, "
               + "items_bag, items_box, items_box_lucky_round, friends, enemies, data_intrinsic, data_item_time,"
               + "data_task, data_mabu_egg, data_charm, skills, skills_shortcut, pet_info, pet_point, pet_body, pet_skill,"
               + "data_black_ball, thoi_vang, data_side_task, achivements, data_item_time_sieucap, "
               + "kham_ngoc, ruong_cai_trang, ruong_phu_kien, ruong_pet, ruong_linh_thu, ruong_thu_cuoi, phong_thi_nghiem, reward_limit, buy_limit, "
               + "data_item_noel, challenge, sk_tet, moc_nap, drop_vang_ngoc, tong_nap, danh_hieu, so_may_man, active_phuc_loi, check_online, check_diem_danh, phut_online, weekTimeLogin, "
               + "power, pet_power, 1sao, 2sao, 3sao, collection_book, event_point, firstTimeLogin, nhan_moc_nap, chuyen_sinh, data_offtrain, reset_ngay, nhan_moc_nap2,"
               + "kill_boss, diemdanh, chuyencan, hoivien_vip, check_qua_chuyencan, naplandau, tichluynap, nhan_moc_nap3, sukien_2thang9, sukien_trungthu, diem_quay, active_vong_quay,"
               + "active_kham_ngoc, active_ruong_suu_tam, dan_duoc) "
               + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

         ps.setInt(1, userId);
         ps.setString(2, name);
         ps.setInt(3, hair);
         ps.setByte(4, gender);
         ps.setBoolean(5, false);
         ps.setInt(6, -1);
         ps.setString(7, createDefaultInventory());
         ps.setString(8, createDefaultLocation(gender));
         ps.setString(9, createDefaultPoint(gender));
         ps.setString(10, createDefaultMagicTree());
         ps.setString(11, createDefaultBody(gender));
         ps.setString(12, createDefaultBag());
         ps.setString(13, createDefaultBox());
         ps.setString(14, createDefaultLuckyRound());
         ps.setString(15, "[]"); // friends
         ps.setString(16, "[]"); // enemies
         ps.setString(17, createDefaultIntrinsic());
         ps.setString(18, createDefaultItemTime());
         ps.setString(19, createDefaultTask());
         ps.setString(20, "{}"); // mabu egg
         ps.setString(21, createDefaultCharms());
         ps.setString(22, createDefaultSkills(gender));
         ps.setString(23, createDefaultSkillShortcut(gender));
         ps.setString(24, "{}"); // pet info
         ps.setString(25, "{}"); // pet point
         ps.setString(26, "[]"); // pet body
         ps.setString(27, "[]"); // pet skill
         ps.setString(28, createDefaultBlackBall());
         ps.setInt(29, 10); // gold bar
         ps.setString(30, "{}"); // side task
         ps.setString(31, createDefaultAchivements());
         ps.setString(32, createDefaultItemTimeSC());
         ps.setString(33, createDefaultKhamNgoc());
         ps.setString(34, createDefaultRuongSuuTam());
         ps.setString(35, createDefaultRuongSuuTam());
         ps.setString(36, createDefaultRuongSuuTam());
         ps.setString(37, createDefaultRuongSuuTam());
         ps.setString(38, createDefaultRuongSuuTam());
         ps.setString(39, createDefaultPhongThiNghiem());
         ps.setString(40, "[]");
         ps.setString(41, "[]");
         ps.setString(42, "[]"); // data_item_noel
         ps.setString(43, "[]"); // challenge
         ps.setString(44, "[0,0,0,0,0]"); // sk_tet
         ps.setInt(45, 0); // moc_nap
         ps.setString(46, "[0,0]"); // drop_vang_ngoc
         ps.setInt(47, 0); // tong_nap
         ps.setString(48, "[]"); // danh_hieu
         ps.setString(49, "[]"); // so_may_man
         ps.setString(50, "[]"); // active_phuc_loi
         ps.setString(51, "[]"); // check_online
         ps.setString(52, "[]"); // check_diem_danh
         ps.setInt(53, 0); // phut_online
         ps.setTimestamp(54, new Timestamp(System.currentTimeMillis())); // weekTimeLogin
         ps.setDouble(55, 2000); // power
         ps.setDouble(56, 0); // pet_power
         ps.setInt(57, 0); // 1sao
         ps.setInt(58, 0); // 2sao
         ps.setInt(59, 0); // 3sao
         ps.setString(60, "[]"); // collection_book
         ps.setInt(61, 0); // event_point
         ps.setTimestamp(62, new Timestamp(System.currentTimeMillis())); // firstTimeLogin
         ps.setString(63, "[]"); // nhan_moc_nap
         ps.setString(64, "[]"); // chuyen_sinh
         ps.setString(65, "[]"); // data_offtrain
         ps.setString(66, "[]"); // reset_ngay
         ps.setString(67, "[]"); // nhan_moc_nap2
         ps.setInt(68, 0); // kill_boss
         ps.setInt(69, 0); // diemdanh
         ps.setInt(70, 0); // chuyencan
         ps.setInt(71, 0); // hoivien_vip
         ps.setInt(72, 0); // check_qua_chuyencan
         ps.setInt(73, 0); // naplandau
         ps.setInt(74, 0); // tichluynap
         ps.setString(75, "[]"); // nhan_moc_nap3
         ps.setInt(76, 0); // sukien_2thang9
         ps.setInt(77, 0); // sukien_trungthu
         ps.setInt(78, 0); // diem_quay
         ps.setString(79, "[]"); // active_vong_quay
         ps.setInt(80, 0); // active_kham_ngoc
         ps.setInt(81, 0); // active_ruong_suu_tam
         ps.setString(82, "[]"); // dan_duoc
         ps.executeUpdate();
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi tạo player mới");
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException e) {
            }
         }
      }
   }

   public static void updatePlayer(Player player, Connection connection) {
      if (player.isDisposed || player.isSaving || !player.loaded) {
         return;
      }
      player.isSaving = true;
      try {
         int[] itemCounts = countItems(player);
         int n1s = itemCounts[0], n2s = itemCounts[1], n3s = itemCounts[2], tv = itemCounts[3];

         PreparedStatement ps = null;
         try {
            ps = connection.prepareStatement("update player set head = ?, gender = ?, have_tennis_space_ship = ?,"
                  + "clan_id_sv" + Manager.SERVER
                  + " = ?, data_inventory = ?, data_location = ?, data_point = ?, data_magic_tree = ?,"
                  + "items_body = ?, items_bag = ?, items_box = ?, items_box_lucky_round = ?, friends = ?,"
                  + "enemies = ?, data_intrinsic = ?, data_item_time = ?, data_task = ?, data_mabu_egg = ?,"
                  + "pet_info = ?, pet_point = ?, pet_body = ?, pet_skill = ? , power = ?, pet_power = ?, "
                  + "data_black_ball = ?, data_side_task = ?, data_charm = ?, skills = ?, skills_shortcut = ?,"
                  + "thoi_vang = ?, 1sao = ?, 2sao = ?, 3sao = ?, collection_book = ?, event_point = ?, firstTimeLogin = ?,"
                  + " challenge = ?, sk_tet = ?, buy_limit = ?, moc_nap = ?,achivements = ? , reward_limit = ?, drop_vang_ngoc = ?"
                  + ", nhan_moc_nap = ?, data_item_time_sieucap = ?, chuyen_sinh = ?, tong_nap = ?, danh_hieu = ?"
                  + ", so_may_man = ?, data_offtrain = ?, reset_ngay = ?, nhan_moc_nap2 = ?, active_phuc_loi = ?, kill_boss = ?"
                  + ", diemdanh = ?, chuyencan = ?, hoivien_vip = ?, check_qua_chuyencan = ?, naplandau = ?"
                  + ", tichluynap = ?, nhan_moc_nap3 = ?, sukien_2thang9 = ?, sukien_trungthu = ?"
                  + ", diem_quay = ?, active_vong_quay = ?"
                  + ", kham_ngoc = ?, active_kham_ngoc = ?, ruong_cai_trang = ?"
                  + ", ruong_phu_kien = ?, ruong_pet = ?, ruong_linh_thu = ?"
                  + ", ruong_thu_cuoi = ?, active_ruong_suu_tam = ?"
                  + ", check_online = ?, phut_online = ?, check_diem_danh = ?, weekTimeLogin = ?, dan_duoc = ?,phong_thi_nghiem = ? where id = ?");

            ps.setShort(1, player.head);
            ps.setInt(2, player.gender);
            ps.setBoolean(3, player.haveTennisSpaceShip);
            ps.setShort(4, (short) (player.clan != null ? player.clan.id : -1));
            ps.setString(5, saveInventory(player));
            ps.setString(6, saveLocation(player));
            ps.setString(7, savePoint(player));
            ps.setString(8, saveMagicTree(player));
            ps.setString(9, saveItems(player.inventory.itemsBody));
            ps.setString(10, saveItems(player.inventory.itemsBag));
            ps.setString(11, saveItems(player.inventory.itemsBox));
            ps.setString(12, saveItems(player.inventory.itemsBoxCrackBall));
            ps.setString(13, saveFriends(player.friends));
            ps.setString(14, saveFriends(player.enemies));
            ps.setString(15, saveIntrinsic(player));
            ps.setString(16, saveItemTime(player));
            ps.setString(17, saveTask(player));
            ps.setString(18, saveMabuEgg(player));
            ps.setString(19, savePetInfo(player));
            ps.setString(20, savePetPoint(player));
            ps.setString(21, player.pet != null ? saveItems(player.pet.inventory.itemsBody) : "[]");
            ps.setString(22, savePetSkills(player));
            ps.setDouble(23, player.nPoint.power);
            ps.setDouble(24, player.pet != null ? player.pet.nPoint.power : 0);
            ps.setString(25, saveBlackBall(player));
            ps.setString(26, saveSideTask(player));
            ps.setString(27, saveCharms(player));
            ps.setString(28, saveSkills(player));
            ps.setString(29, saveSkillShortcut(player));
            ps.setInt(30, tv);
            ps.setInt(31, n1s);
            ps.setInt(32, n2s);
            ps.setInt(33, n3s);
            ps.setString(34, gson.toJson(player.collectionBook.cards));
            ps.setInt(35, player.evenpoint);
            ps.setString(36, Util.toDateString(player.firstTimeLogin));
            ps.setString(37, saveChallenge(player));
            ps.setString(38, saveSkTet(player));
            ps.setString(39, saveBuyLimit(player));
            ps.setInt(40, player.event.mocNapDaNhan);
            ps.setString(41, saveAchivements(player));
            ps.setString(42, saveRewardLimit(player));
            ps.setString(43, saveVangNgoc(player));
            ps.setString(44, saveMocNap(player.mot, player.hai, player.ba, player.bon, player.nam));
            ps.setString(45, saveItemTimeSC(player));
            ps.setString(46, saveMocNap(player.chuyensinh, player.MaxGoldTradeDay, player.chuaco2, player.chuaco3,
                  player.chuaco4));
            ps.setInt(47, player.tongnap);
            ps.setString(48, saveDanhHieu(player));
            ps.setString(49, gson.toJson(player.soMayMan));
            ps.setString(50, saveLuyentap(player));
            ps.setString(51, saveResetDay(player));
            ps.setString(52, saveMocNap(player.sau, player.bay, player.tam, player.chin, player.muoi));
            ps.setString(53, saveMocNap(player.listNhan.stream().mapToInt(Integer::intValue).toArray()));
            ps.setInt(54, player.killboss);
            ps.setInt(55, player.diemdanh);
            ps.setInt(56, player.chuyencan);
            ps.setInt(57, player.hoivienvip);
            ps.setInt(58, player.checkquachuyencan);
            ps.setInt(59, player.naplandau);
            ps.setInt(60, player.tichluynap);
            ps.setString(61, saveMocNap(player.muoiMot, player.muoiHai, player.muoiBa, player.muoiBon, player.muoiLam));
            ps.setInt(62, player.even2thang9);
            ps.setInt(63, player.evenTrungThu);
            ps.setInt(64, player.diem_quay);
            ps.setString(65, saveMocNap(player.listNhan_TamBao.stream().mapToInt(Integer::intValue).toArray()));
            ps.setString(66, saveKhamNgoc(player));
            ps.setInt(67, player.active_kham_ngoc);
            ps.setString(68, saveItems(player.ruongSuuTam.RuongCaiTrang));
            ps.setString(69, saveItems(player.ruongSuuTam.RuongPhuKien));
            ps.setString(70, saveItems(player.ruongSuuTam.RuongPet));
            ps.setString(71, saveItems(player.ruongSuuTam.RuongLinhThu));
            ps.setString(72, saveItems(player.ruongSuuTam.RuongThuCuoi));
            ps.setInt(73, player.active_ruong_suu_tam);
            ps.setString(74, saveOnline(player));
            ps.setInt(75, player.phutOnline);
            ps.setString(76, saveDiemDanh(player));
            ps.setString(77, Util.toDateString(player.weekTimeLogin));
            ps.setString(78, saveMocNap(player.bohuyetdan, player.tangnguyendan, player.bokhidan));
            ps.setString(79, savePhongThiNghiem(player));
            ps.setInt(80, (int) player.id);
            ps.executeUpdate();
            if (updateTimeLogout) {
               AccountDAO.updateAccoutLogout(player.getSession());
            }
         } catch (Exception e) {
            Log.error(PlayerDAO.class, e, "Lỗi save player " + player.name);
         } finally {
            if (ps != null) {
               try {
                  ps.close();
               } catch (SQLException e) {
               }
            }
         }
      } finally {
         player.isSaving = false;
      }
   }

   public static void saveName(Player player) {
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set name = ? where id = ?");
         ps.setString(1, player.name);
         ps.setInt(2, (int) player.id);
         ps.executeUpdate();
      } catch (Exception e) {
      } finally {
         try {
            ps.close();
         } catch (Exception e) {
         }
      }
   }

   public static boolean isExistName(String name) {
      boolean exist = false;
      PreparedStatement ps = null;
      ResultSet rs = null;
      try (Connection con = DBService.gI().getConnectionForGame();) {
         ps = con.prepareStatement("select * from player where name = ?");
         ps.setString(1, name);
         rs = ps.executeQuery();
         if (rs.next()) {
            exist = true;
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            ps.close();
            rs.close();
         } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      return exist;
   }

   public static boolean checkVnd(Player player, int giatri) {
      if (giatri <= 0) {
         return false;
      }
      PreparedStatement ps = null;
      ResultSet rs = null;
      try (Connection con = DBService.gI().getConnectionForGame();) {
         ps = con.prepareStatement("select vnd from account where id = ?");
         ps.setInt(1, player.getSession().userId);
         rs = ps.executeQuery();
         if (rs.next()) {
            int vnd = rs.getInt("vnd");
            if (vnd < giatri) {
               return false;
            }
         }
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi check vnd " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean subVnd(Player player, int vnd) {
      if (vnd <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update account set vnd = (vnd - ?) where id = ?");
         ps.setInt(1, vnd);
         ps.setInt(2, player.getSession().userId);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.getSession().vnd -= vnd; // Cập nhật số dư `vnd` của người chơi trong bộ nhớ.
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update vnd " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean CongVnd(Player player, int vnd) {
      if (vnd <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update account set vnd = (vnd + ?) where id = ?");
         ps.setInt(1, vnd);
         ps.setInt(2, player.getSession().userId);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.getSession().vnd += vnd; // Cập nhật số dư `vnd` của người chơi trong bộ nhớ.
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update vnd " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean CongKillBoss(Player player, int kill) {
      if (kill <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set kill_boss = (kill_boss + ?) where id = ?");
         ps.setInt(1, kill);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.killboss += kill; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update kill boss " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean congDiemEven2thang9(Player player, int diem) {
      if (diem <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set sukien_2thang9 = (sukien_2thang9 + ?) where id = ?");
         ps.setInt(1, diem);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.even2thang9 += diem; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update kill boss " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean CongDiemSuKien(Player player, int kill) {
      if (kill <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set event_point = (event_point + ?) where id = ?");
         ps.setInt(1, kill);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.evenpoint += kill; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update even point " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean CongTongNap(Player player, int coin) {
      if (coin <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set kill_boss = (kill_boss + ?) where id = ?");
         ps.setInt(1, coin);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.tongnap += coin; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update tongnap " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean NangCapHoiVienVIP(Player player, int capdo) {
      if (capdo <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set hoivien_vip = (hoivien_vip + ?) where id = ?");
         ps.setInt(1, capdo);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.hoivienvip += capdo; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update cap hoi vien vip " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean ChuyenCan(Player player, int day) {
      if (day <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set chuyencan = (chuyencan + ?) where id = ?");
         ps.setInt(1, day);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.chuyencan += day; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update chuyen can" + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean CheckQuaChuyenCan(Player player, int daNhan) {
      if (daNhan <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set check_qua_chuyencan = (check_qua_chuyencan + ?) where id = ?");
         ps.setInt(1, daNhan);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.checkquachuyencan += daNhan; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update chuyen can" + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean CongPointDiemDanh(Player player, int point) {
      if (point <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set diemdanh = (diemdanh + ?) where id = ?");
         ps.setInt(1, point);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.diemdanh += point; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update kill boss " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean NapLanDau(Player player, int danhan) {
      if (danhan <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update player set naplandau = (naplandau + ?) where id = ?");
         ps.setInt(1, danhan);
         ps.setLong(2, player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         player.naplandau += danhan; // Cập nhật số điểm giết boss trong cột kill_boss
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update diem nap lan dau " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean PhatThuongOfline(Player player, int ngoc) {
      if (ngoc <= 0) {
         return false; // Giá trị `num` không hợp lệ.
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement(
               "UPDATE player SET data_inventory = JSON_REPLACE(data_inventory, '$[2]', ?) WHERE id = ?");
         ps.setInt(1, player.inventory.ruby + ngoc);
         ps.setInt(2, (int) player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update phat thuong " + player.name);
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu: " + player.name);
         }
      }
   }

   public static boolean XoaSoMayMan(Player player) {
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("UPDATE player SET so_may_man = ? WHERE id = ?");
         ps.setString(1, "[]");
         ps.setInt(2, (int) player.id);
         // ps.executeUpdate();
         int check = ps.executeUpdate();
         if (check == 0) {
            return false; // Không cập nhật bất kỳ hàng nào trong cơ sở dữ liệu.
         }
         return true;
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi XoaSoMayMan");
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException e) {
            Log.error(PlayerDAO.class, e, "Lỗi đóng kết nối cơ sở dữ liệu");
         }
      }
   }

   public static void subRuby(Player player, int userId, int ruby) {
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update account set ruby = ruby - ? where id = ?");
         ps.setInt(1, ruby);
         ps.setInt(2, userId);
         ps.executeUpdate();
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update ruby " + player.name);
      } finally {
         try {
            ps.close();
         } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public static void subGoldBar(Player player, int num) {
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?) where id = ?");
         ps.setInt(1, num);
         ps.setInt(2, player.getSession().userId);
         ps.executeUpdate();
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update thỏi vàng " + player.name);
      } finally {
         try {
            ps.close();
         } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public static void subActive(Player player, int num) {
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("update account set active = ? where id = ?");
         ps.setInt(1, num);
         ps.setInt(2, player.getSession().userId);
         ps.executeUpdate();
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update active " + player.name);
      } finally {
         try {
            ps.close();
         } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public static void addHistoryReceiveGoldBar(Player player, int goldBefore, int goldAfter,
         int goldBagBefore, int goldBagAfter, int goldBoxBefore, int goldBoxAfter) {
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForSaveData();) {
         ps = con.prepareStatement("insert into history_receive_goldbar(player_id,player_name,gold_before_receive,"
               + "gold_after_receive,gold_bag_before,gold_bag_after,gold_box_before,gold_box_after) values (?,?,?,?,?,?,?,?)");
         ps.setInt(1, (int) player.id);
         ps.setString(2, player.name);
         ps.setInt(3, goldBefore);
         ps.setInt(4, goldAfter);
         ps.setInt(5, goldBagBefore);
         ps.setInt(6, goldBagAfter);
         ps.setInt(7, goldBoxBefore);
         ps.setInt(8, goldBoxAfter);
         ps.executeUpdate();
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update thỏi vàng " + player.name);
      } finally {
         try {
            ps.close();
         } catch (Exception e) {
         }
      }
   }

   public static void updateItemReward(Player player) {
      String dataItemReward = "";
      for (Item item : player.getSession().itemsReward) {
         if (item.isNotNullItem()) {
            dataItemReward += "{" + item.template.id + ":" + item.quantity;
            if (!item.itemOptions.isEmpty()) {
               dataItemReward += "|";
               for (ItemOption io : item.itemOptions) {
                  dataItemReward += "[" + io.optionTemplate.id + ":" + io.param + "],";
               }
               dataItemReward = dataItemReward.substring(0, dataItemReward.length() - 1) + "};";
            }
         }
      }
      PreparedStatement ps = null;
      try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
         ps = con.prepareStatement("update account set reward = ? where id = ?");
         ps.setString(1, dataItemReward);
         ps.setInt(2, player.getSession().userId);
         ps.executeUpdate();
      } catch (Exception e) {
         Log.error(PlayerDAO.class, e, "Lỗi update phần thưởng " + player.name);
      } finally {
         try {
            ps.close();
         } catch (Exception e) {
         }
      }
   }

   public static void saveBag(Connection con, Player player) {
      if (player.loaded) {
         PreparedStatement ps = null;
         try {
            String itemsBag = saveItems(player.inventory.itemsBag);
            ps = con.prepareStatement("update player set items_bag = ? where id = ?");
            ps.setString(1, itemsBag);
            ps.setInt(2, (int) player.id);
            ps.executeUpdate();
         } catch (Exception e) {
            Log.error(PlayerDAO.class, e, "Lỗi save bag player " + player.name);
         } finally {
            if (ps != null) {
               try {
                  ps.close();
               } catch (SQLException ex) {
                  java.util.logging.Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
               }
            }
         }
      }
   }

}
