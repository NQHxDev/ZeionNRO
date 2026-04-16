package nro.jdbc.daos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import nro.card.Card;
import nro.card.CollectionBook;
import nro.consts.ConstMap;
import nro.consts.ConstPlayer;
import nro.jdbc.DBService;
import nro.manager.AchiveManager;
import nro.manager.PetFollowManager;
import nro.models.player.Enemy;
import nro.models.player.Friend;
import nro.models.player.Fusion;
import nro.models.player.MiniPet;
import nro.models.player.Pet;
import nro.models.player.PetFollow;
import nro.models.player.Player;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.item.ItemTime;
import nro.models.npc.specialnpc.MabuEgg;
import nro.models.npc.specialnpc.MagicTree;
import nro.models.skill.Skill;
import nro.models.task.Achivement;
import nro.models.task.AchivementTemplate;
import nro.models.task.TaskMain;
import nro.server.Client;
import nro.server.Manager;
import nro.server.io.Session;
import nro.server.model.AntiLogin;
import nro.services.ClanService;
import nro.services.IntrinsicService;
import nro.services.ItemService;
import nro.services.KhamNgocPlayer;
import nro.services.MapService;
import nro.services.PhongThiNghiem_Player;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.SkillUtil;
import nro.utils.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nro.models.item.ItemTimeSieuCap;
import nro.utils.Util;

public class GodGK {

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
                  Service.getInstance().sendThongBaoOK(session,
                        "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass) + " giây để đăng nhập lại.");
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
      try {
         Connection conn = DBService.gI().getConnectionForLogin();
         String query = "select * from account where username = ? and password = ? limit 1";
         try (PreparedStatement pss = conn.prepareStatement(query)) {
            pss.setString(1, session.uu);
            pss.setString(2, session.pp);
            try (ResultSet rss = pss.executeQuery()) {
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
               }
            }
         }

         try (PreparedStatement ps = conn.prepareStatement("select * from player where account_id = ? limit 1")) {
            ps.setInt(1, session.userId);
            try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                  Player player = new Player();
                  loadPlayerData(player, rs);

                  player.server = session.server;
                  player.event.setDiemTichLuy(session.diemTichNap);
                  player.event.setMocNapDaNhan(rs.getInt("moc_nap"));

                  long now = System.currentTimeMillis();
                  long thoiGianOffline = now - session.lastTimeLogout;
                  player.timeoff = thoiGianOffline / 60000;

                  if (session.ruby > 0) {
                     player.inventory.ruby += session.ruby;
                     PlayerDAO.subRuby(player, session.userId, session.ruby);
                  }

                  session.player = player;

                  try (PreparedStatement ps2 = conn
                        .prepareStatement("update account set last_time_login = ?, ip_address = ? where id = ?")) {
                     ps2.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                     ps2.setString(2, session.ipAddress);
                     ps2.setInt(3, session.userId);
                     ps2.executeUpdate();
                  }

                  return player;
               }
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
         session.dataLoadFailed = true;
      }
      return null;
   }

   public static Player loadPlayerbyId(int id) {
      try {
         Connection connection = DBService.gI().getConnectionForLogin();
         try (PreparedStatement ps = connection.prepareStatement("select * from player where id = ? limit 1")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                  Player player = new Player();
                  loadPlayerData(player, rs);
                  return player;
               }
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return null;
   }

   private static final Gson gson = new Gson();

   private static void loadPlayerData(Player player, ResultSet rs) throws Exception {
      loadBaseInfo(player, rs);
      loadInventory(player, rs);
      loadLocation(player, rs);
      loadPoint(player, rs);
      loadMagicTree(player, rs);
      loadBlackBallReward(player, rs);
      loadIntrinsic(player, rs);
      loadItemTime(player, rs);
      loadTask(player, rs);
      loadMabuEgg(player, rs);
      loadOtherData(player, rs);
      loadSkill(player, rs);
      loadPet(player, rs);
   }

   private static void loadBaseInfo(Player player, ResultSet rs) throws Exception {
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
         player.name = "[" + Service.getInstance().capVIP(player.hoivienvip) + "] " + player.name;
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

      if (player.clan != null && player.clan.name != null && !player.clan.name.isEmpty()) {
         String clanTag = player.clan.name.length() >= 3 ? player.clan.name.substring(0, 3) : player.clan.name;
         player.name = "[" + clanTag + "] " + rs.getString("name");
      } else {
         player.name = rs.getString("name");
      }

      player.event.setEventPoint(rs.getInt("event_point"));

      List<Integer> sk_tet = gson.fromJson(rs.getString("sk_tet"), new TypeToken<List<Integer>>() {
      }.getType());
      if (sk_tet != null && sk_tet.size() >= 5) {
         player.event.setTimeCookTetCake(sk_tet.get(0));
         player.event.setTimeCookChungCake(sk_tet.get(1));
         player.event.setCookingTetCake(sk_tet.get(2) == 1);
         player.event.setCookingChungCake(sk_tet.get(3) == 1);
         player.event.setReceivedLuckyMoney(sk_tet.get(4) == 1);
      }

      List<PhongThiNghiem_Player> ptnList = gson.fromJson(rs.getString("phong_thi_nghiem"),
            new TypeToken<List<PhongThiNghiem_Player>>() {
            }.getType());
      if (ptnList != null) {
         player.phongThiNghiem.addAll(ptnList);
      }

      List<Integer> phucLoi = gson.fromJson(rs.getString("active_phuc_loi"), new TypeToken<List<Integer>>() {
      }.getType());
      if (phucLoi != null) {
         for (int idLoi : phucLoi) {
            player.idPhucLoi = idLoi;
            player.listNhan.add(idLoi);
         }
      }

      List<Integer> tamBao = gson.fromJson(rs.getString("active_vong_quay"), new TypeToken<List<Integer>>() {
      }.getType());
      if (tamBao != null) {
         for (int idNum : tamBao) {
            player.idTamBao = idNum;
            player.listNhan_TamBao.add(idNum);
         }
      }

      List<Integer> checkOnline = gson.fromJson(rs.getString("check_online"), new TypeToken<List<Integer>>() {
      }.getType());
      if (checkOnline != null) {
         player.listOnline.addAll(checkOnline);
      }

      List<Integer> checkDiemDanh = gson.fromJson(rs.getString("check_diem_danh"), new TypeToken<List<Integer>>() {
      }.getType());
      if (checkDiemDanh != null) {
         player.listDiemDanh.addAll(checkDiemDanh);
      }

      List<KhamNgocPlayer> khamNgoc = gson.fromJson(rs.getString("kham_ngoc"), new TypeToken<List<KhamNgocPlayer>>() {
      }.getType());
      if (khamNgoc != null) {
         player.khamNgoc.addAll(khamNgoc);
      }
   }

   private static void loadInventory(Player player, ResultSet rs) throws Exception {
      player.ruongSuuTam.RuongCaiTrang.addAll(loadRuong(rs.getString("ruong_cai_trang")));
      player.ruongSuuTam.RuongPhuKien.addAll(loadRuong(rs.getString("ruong_phu_kien")));
      player.ruongSuuTam.RuongPet.addAll(loadRuong(rs.getString("ruong_pet")));
      player.ruongSuuTam.RuongLinhThu.addAll(loadRuong(rs.getString("ruong_linh_thu")));
      player.ruongSuuTam.RuongThuCuoi.addAll(loadRuong(rs.getString("ruong_thu_cuoi")));

      List<Long> dataInventory = gson.fromJson(rs.getString("data_inventory"), new TypeToken<List<Long>>() {
      }.getType());
      if (dataInventory != null && dataInventory.size() >= 3) {
         player.inventory.gold = dataInventory.get(0);
         player.inventory.gem = dataInventory.get(1).intValue();
         player.inventory.ruby = dataInventory.get(2).intValue();
         if (dataInventory.size() >= 4) {
            player.inventory.goldLimit = dataInventory.get(3);
         }
      }

      player.inventory.itemsBody.addAll(loadRuong(rs.getString("items_body")));
      player.inventory.itemsBag.addAll(loadRuong(rs.getString("items_bag")));
      player.inventory.itemsBox.addAll(loadRuong(rs.getString("items_box")));
      player.inventory.itemsBoxCrackBall.addAll(loadRuong(rs.getString("items_box_lucky_round")));

      while (player.inventory.itemsBody.size() < 11) {
         player.inventory.itemsBody.add(ItemService.gI().createItemNull());
      }

      if (player.inventory.itemsBody.get(7).isNotNullItem()) {
         MiniPet.callMiniPet(player, player.inventory.itemsBody.get(7).template.id);
      }
      if (player.inventory.itemsBody.get(10).isNotNullItem()) {
         PetFollow pet = PetFollowManager.gI().findByID(player.inventory.itemsBody.get(10).getId());
         player.setPetFollow(pet);
      }
   }

   private static List<Item> loadRuong(String json) {
      List<Item> items = new ArrayList<>();
      JsonArray data = gson.fromJson(json, JsonArray.class);
      if (data != null) {
         for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();
            short tempId = obj.get("temp_id").getAsShort();
            if (tempId != -1) {
               Item item = ItemService.gI().createNewItem(tempId, obj.get("quantity").getAsInt());
               if (obj.has("option") && !obj.get("option").isJsonNull()) {
                  JsonArray options = obj.getAsJsonArray("option");
                  for (JsonElement optElement : options) {
                     JsonArray opt = optElement.getAsJsonArray();
                     item.itemOptions.add(new ItemOption(opt.get(0).getAsInt(), opt.get(1).getAsInt()));
                  }
               }
               if (obj.has("create_time") && !obj.get("create_time").isJsonNull()) {
                  item.createTime = obj.get("create_time").getAsLong();
               }
               if (ItemService.gI().isOutOfDateTime(item)) {
                  item = ItemService.gI().createItemNull();
               }
               items.add(item);
            } else {
               items.add(ItemService.gI().createItemNull());
            }
         }
      }
      return items;
   }

   private static void loadLocation(Player player, ResultSet rs) throws Exception {
      List<Integer> location = gson.fromJson(rs.getString("data_location"), new TypeToken<List<Integer>>() {
      }.getType());
      if (location != null && location.size() >= 3) {
         player.location.x = location.get(0);
         player.location.y = location.get(1);
         int mapId = location.get(2);
         if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
               || MapService.gI().isMapBanDoKhoBau(mapId) || mapId == 126
               || mapId == ConstMap.CON_DUONG_RAN_DOC
               || mapId == ConstMap.CON_DUONG_RAN_DOC_142 || mapId == ConstMap.CON_DUONG_RAN_DOC_143
               || mapId == ConstMap.HOANG_MAC) {
            mapId = player.gender + 21;
            player.location.x = 300;
            player.location.y = 336;
         }
         player.zone = MapService.gI().getMapCanJoin(player, mapId);
      }
   }

   private static void loadPoint(Player player, ResultSet rs) throws Exception {
      List<Object> points = gson.fromJson(rs.getString("data_point"), new TypeToken<List<Object>>() {
      }.getType());
      if (points != null && points.size() >= 13) {
         player.nPoint.limitPower = ((Double) points.get(0)).byteValue();
         player.nPoint.power = (Double) points.get(1);
         player.nPoint.tiemNang = (Double) points.get(2);
         player.nPoint.stamina = ((Double) points.get(3)).shortValue();
         player.nPoint.maxStamina = ((Double) points.get(4)).shortValue();
         player.nPoint.hpg = (Double) points.get(5);
         player.nPoint.mpg = (Double) points.get(6);
         player.nPoint.dameg = (Double) points.get(7);
         player.nPoint.defg = (Double) points.get(8);
         player.nPoint.critg = ((Double) points.get(9)).byteValue();
         player.nPoint.hp = (Double) points.get(11);
         player.nPoint.mp = (Double) points.get(12);
      }
   }

   private static void loadMagicTree(Player player, ResultSet rs) throws Exception {
      List<Object> tree = gson.fromJson(rs.getString("data_magic_tree"), new TypeToken<List<Object>>() {
      }.getType());
      if (tree != null && tree.size() >= 5) {
         boolean isUpgrade = ((Double) tree.get(0)).intValue() == 1;
         long lastTimeUpgrade = ((Double) tree.get(1)).longValue();
         byte level = ((Double) tree.get(2)).byteValue();
         long lastTimeHarvest = ((Double) tree.get(3)).longValue();
         byte currPea = ((Double) tree.get(4)).byteValue();
         player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
      }
   }

   private static void loadBlackBallReward(Player player, ResultSet rs) throws Exception {
      List<List<Long>> rewards = gson.fromJson(rs.getString("data_black_ball"), new TypeToken<List<List<Long>>>() {
      }.getType());
      if (rewards != null) {
         for (int i = 0; i < rewards.size() && i < player.rewardBlackBall.timeOutOfDateReward.length; i++) {
            List<Long> reward = rewards.get(i);
            if (reward.size() >= 2) {
               player.rewardBlackBall.timeOutOfDateReward[i] = reward.get(0);
               player.rewardBlackBall.lastTimeGetReward[i] = reward.get(1);
            }
         }
      }
   }

   private static void loadIntrinsic(Player player, ResultSet rs) throws Exception {
      List<Integer> intrinsic = gson.fromJson(rs.getString("data_intrinsic"), new TypeToken<List<Integer>>() {
      }.getType());
      if (intrinsic != null && intrinsic.size() >= 4) {
         byte intrinsicId = intrinsic.get(0).byteValue();
         player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
         player.playerIntrinsic.intrinsic.param1 = intrinsic.get(1).shortValue();
         player.playerIntrinsic.countOpen = intrinsic.get(2).byteValue();
         player.playerIntrinsic.intrinsic.param2 = intrinsic.get(3).shortValue();
      }
   }

   private static void loadItemTime(Player player, ResultSet rs) throws Exception {
      List<Integer> itemTime = gson.fromJson(rs.getString("data_item_time"), new TypeToken<List<Integer>>() {
      }.getType());
      if (itemTime != null && itemTime.size() >= 9) {
         int timeBoKhi = itemTime.get(0);
         int timeAnDanh = itemTime.get(1);
         int timeOpenPower = itemTime.get(2);
         int timeCuongNo = itemTime.get(3);
         int timeBoHuyet = itemTime.get(5);
         int timeGiapXen = itemTime.get(8);

         int timeMayDo = 0, timeMeal = 0, iconMeal = 0;
         if (itemTime.size() >= 8) {
            timeMayDo = itemTime.get(4);
            iconMeal = itemTime.get(6);
            timeMeal = itemTime.get(7);
         }

         int timeBanhChung = 0, timeBanhTet = 0, timeBoKhi2 = 0, timeGiapXen2 = 0, timeCuongNo2 = 0, timeBoHuyet2 = 0;
         if (itemTime.size() >= 15) {
            timeBanhChung = itemTime.get(9);
            timeBanhTet = itemTime.get(10);
            timeBoKhi2 = itemTime.get(11);
            timeGiapXen2 = itemTime.get(12);
            timeCuongNo2 = itemTime.get(13);
            timeBoHuyet2 = itemTime.get(14);
         }

         long now = System.currentTimeMillis();
         player.itemTime.lastTimeBoHuyet = now - (ItemTime.TIME_ITEM - timeBoHuyet);
         player.itemTime.lastTimeBoKhi = now - (ItemTime.TIME_ITEM - timeBoKhi);
         player.itemTime.lastTimeGiapXen = now - (ItemTime.TIME_ITEM - timeGiapXen);
         player.itemTime.lastTimeCuongNo = now - (ItemTime.TIME_ITEM - timeCuongNo);
         player.itemTime.lastTimeBoHuyet2 = now - (ItemTime.TIME_ITEM - timeBoHuyet2);
         player.itemTime.lastTimeBoKhi2 = now - (ItemTime.TIME_ITEM - timeBoKhi2);
         player.itemTime.lastTimeGiapXen2 = now - (ItemTime.TIME_ITEM - timeGiapXen2);
         player.itemTime.lastTimeCuongNo2 = now - (ItemTime.TIME_ITEM - timeCuongNo2);
         player.itemTime.lastTimeAnDanh = now - (ItemTime.TIME_ITEM - timeAnDanh);
         player.itemTime.lastTimeOpenPower = now - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
         player.itemTime.lastTimeUseMayDo = now - (ItemTime.TIME_MAY_DO - timeMayDo);
         player.itemTime.lastTimeEatMeal = now - (ItemTime.TIME_EAT_MEAL - timeMeal);
         player.itemTime.lastTimeBanhChung = now - (ItemTime.TIME_EAT_MEAL - timeBanhChung);
         player.itemTime.lastTimeBanhTet = now - (ItemTime.TIME_EAT_MEAL - timeBanhTet);
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
         player.itemTime.isUseBanhChung = timeBanhChung != 0;
         player.itemTime.isUseBanhTet = timeBanhTet != 0;
      }

      List<Integer> scData = gson.fromJson(rs.getString("data_item_time_sieucap"), new TypeToken<List<Integer>>() {
      }.getType());
      if (scData != null && scData.size() >= 12) {
         long now = System.currentTimeMillis();
         player.itemTimesieucap.lastTimeDuoikhi = now - (ItemTimeSieuCap.TIME_ITEM_SC_10P - scData.get(0));
         player.itemTimesieucap.lastTimeDaNgucTu = now - (ItemTimeSieuCap.TIME_ITEM_SC_10P - scData.get(1));
         player.itemTimesieucap.lastTimeCaRot = now - (ItemTimeSieuCap.TIME_ITEM_SC_10P - scData.get(2));
         player.itemTimesieucap.lastTimeKeo = now - (ItemTimeSieuCap.TIME_ITEM_SC_30P - scData.get(3));
         player.itemTimesieucap.lastTimeUseXiMuoi = now - (ItemTimeSieuCap.TIME_ITEM_SC_10P - scData.get(4));
         player.itemTimesieucap.lastTimeUseBanh = now - (ItemTimeSieuCap.TIME_TRUNGTHU - scData.get(6));
         player.itemTimesieucap.lasttimeChoido = now - (ItemTimeSieuCap.TIME_ITEM_SC_10P - scData.get(7));
         player.itemTimesieucap.lastTimeRongSieuCap = now - (ItemTimeSieuCap.TIME_ITEM_SC_30P - scData.get(8));
         player.itemTimesieucap.lastTimeRongBang = now - (ItemTimeSieuCap.TIME_ITEM_SC_30P - scData.get(9));
         player.itemTimesieucap.lastTimeMeal = now - (ItemTimeSieuCap.TIME_ITEM_SC_10P - scData.get(10));

         player.itemTimesieucap.isDuoikhi = scData.get(0) != 0;
         player.itemTimesieucap.isDaNgucTu = scData.get(1) != 0;
         player.itemTimesieucap.isUseCaRot = scData.get(2) != 0;
         player.itemTimesieucap.isKeo = scData.get(3) != 0;
         player.itemTimesieucap.isUseXiMuoi = scData.get(4) != 0;
         player.itemTimesieucap.iconBanh = scData.get(5);
         player.itemTimesieucap.isUseTrungThu = scData.get(6) != 0;
         player.itemTimesieucap.isChoido = scData.get(7) != 0;
         player.itemTimesieucap.isRongSieuCap = scData.get(8) != 0;
         player.itemTimesieucap.isRongBang = scData.get(9) != 0;
         player.itemTimesieucap.isEatMeal = scData.get(10) != 0;
         player.itemTimesieucap.iconMeal = scData.get(11);
      }
   }

   private static void loadTask(Player player, ResultSet rs) throws Exception {
      List<Integer> taskData = gson.fromJson(rs.getString("data_task"), new TypeToken<List<Integer>>() {
      }.getType());
      if (taskData != null && taskData.size() >= 3) {
         TaskMain taskMain = TaskService.gI().getTaskMainById(player, taskData.get(1).byteValue());
         if (taskMain != null) {
            taskMain.subTasks.get(taskData.get(2)).count = taskData.get(0).shortValue();
            taskMain.index = taskData.get(2).byteValue();
            player.playerTask.taskMain = taskMain;
         }
      }

      try {
         List<Object> sideTask = gson.fromJson(rs.getString("data_side_task"), new TypeToken<List<Object>>() {
         }.getType());
         if (sideTask != null && sideTask.size() >= 6) {
            long receivedTime = ((Double) sideTask.get(4)).longValue();
            String format = "dd-MM-yyyy";
            if (TimeUtil.formatTime(new Date(receivedTime), format).equals(TimeUtil.formatTime(new Date(), format))) {
               player.playerTask.sideTask.level = ((Double) sideTask.get(0)).intValue();
               player.playerTask.sideTask.count = ((Double) sideTask.get(1)).intValue();
               player.playerTask.sideTask.leftTask = ((Double) sideTask.get(2)).intValue();
               player.playerTask.sideTask.template = TaskService.gI()
                     .getSideTaskTemplateById(((Double) sideTask.get(3)).intValue());
               player.playerTask.sideTask.maxCount = ((Double) sideTask.get(5)).intValue();
               player.playerTask.sideTask.receivedTime = receivedTime;
            }
         }
      } catch (Exception e) {
      }

      List<Map<String, Object>> achivementsData = gson.fromJson(rs.getString("achivements"),
            new TypeToken<List<Map<String, Object>>>() {
            }.getType());
      if (achivementsData != null) {
         for (Map<String, Object> obj : achivementsData) {
            Achivement achive = new Achivement();
            achive.setId(((Double) obj.get("id")).intValue());
            achive.setCount(((Double) obj.get("count")).intValue());
            achive.setFinish(((Double) obj.get("finish")).intValue() == 1);
            achive.setReceive(((Double) obj.get("receive")).intValue() == 1);
            AchivementTemplate a = AchiveManager.getInstance().findByID(achive.getId());
            if (a != null) {
               achive.setName(a.getName());
               achive.setDetail(a.getDetail());
               achive.setMaxCount(a.getMaxCount());
               achive.setMoney(a.getMoney());
            }
            player.playerTask.achivements.add(achive);
         }
      }

      List<AchivementTemplate> allAchive = AchiveManager.getInstance().getList();
      if (player.playerTask.achivements.size() < allAchive.size()) {
         for (int i = player.playerTask.achivements.size(); i < allAchive.size(); i++) {
            AchivementTemplate a = AchiveManager.getInstance().findByID(i);
            if (a != null) {
               Achivement achive = new Achivement();
               achive.setId(a.getId());
               achive.setCount(0);
               achive.setFinish(false);
               achive.setReceive(false);
               achive.setName(a.getName());
               achive.setDetail(a.getDetail());
               achive.setMaxCount(a.getMaxCount());
               achive.setMoney(a.getMoney());
               player.playerTask.achivements.add(achive);
            }
         }
      }
   }

   private static void loadMabuEgg(Player player, ResultSet rs) throws Exception {
      Map<String, Object> mabu = gson.fromJson(rs.getString("data_mabu_egg"), new TypeToken<Map<String, Object>>() {
      }.getType());
      if (mabu != null && mabu.get("create_time") != null) {
         player.mabuEgg = new MabuEgg(player, ((Double) mabu.get("create_time")).longValue(),
               ((Double) mabu.get("time_done")).longValue());
      }
   }

   private static void loadOtherData(Player player, ResultSet rs) throws Exception {
      List<Long> charms = gson.fromJson(rs.getString("data_charm"), new TypeToken<List<Long>>() {
      }.getType());
      if (charms != null && charms.size() >= 10) {
         player.charms.tdTriTue = charms.get(0);
         player.charms.tdManhMe = charms.get(1);
         player.charms.tdDaTrau = charms.get(2);
         player.charms.tdOaiHung = charms.get(3);
         player.charms.tdBatTu = charms.get(4);
         player.charms.tdDeoDai = charms.get(5);
         player.charms.tdThuHut = charms.get(6);
         player.charms.tdDeTu = charms.get(7);
         player.charms.tdTriTue3 = charms.get(8);
         player.charms.tdTriTue4 = charms.get(9);
         if (charms.size() >= 11)
            player.charms.tdDeTuMabu = charms.get(10);
      }

      List<Long> dropData = gson.fromJson(rs.getString("drop_vang_ngoc"), new TypeToken<List<Long>>() {
      }.getType());
      if (dropData != null && dropData.size() >= 2) {
         player.vangnhat = dropData.get(0);
         player.hngocnhat = dropData.get(1);
      }

      for (int m = 1; m <= 3; m++) {
         String col = "nhan_moc_nap" + (m == 1 ? "" : m);
         List<Integer> moc = gson.fromJson(rs.getString(col), new TypeToken<List<Integer>>() {
         }.getType());
         if (moc != null && moc.size() >= 5) {
            if (m == 1) {
               player.mot = moc.get(0);
               player.hai = moc.get(1);
               player.ba = moc.get(2);
               player.bon = moc.get(3);
               player.nam = moc.get(4);
            } else if (m == 2) {
               player.sau = moc.get(0);
               player.bay = moc.get(1);
               player.tam = moc.get(2);
               player.chin = moc.get(3);
               player.muoi = moc.get(4);
            } else {
               player.muoiMot = moc.get(0);
               player.muoiHai = moc.get(1);
               player.muoiBa = moc.get(2);
               player.muoiBon = moc.get(3);
               player.muoiLam = moc.get(4);
            }
         }
      }

      List<Integer> danData = gson.fromJson(rs.getString("dan_duoc"), new TypeToken<List<Integer>>() {
      }.getType());
      if (danData != null && danData.size() >= 3) {
         player.bohuyetdan = danData.get(0);
         player.tangnguyendan = danData.get(1);
         player.bokhidan = danData.get(2);
      }

      List<Integer> csData = gson.fromJson(rs.getString("chuyen_sinh"), new TypeToken<List<Integer>>() {
      }.getType());
      if (csData != null && csData.size() >= 5) {
         player.chuyensinh = csData.get(0);
         player.MaxGoldTradeDay = csData.get(1);
         player.chuaco2 = csData.get(2);
         player.chuaco3 = csData.get(3);
         player.chuaco4 = csData.get(4);
      }

      List<Integer> smmData = gson.fromJson(rs.getString("so_may_man"), new TypeToken<List<Integer>>() {
      }.getType());
      if (smmData != null)
         player.soMayMan.addAll(smmData);

      List<Integer> trainData = gson.fromJson(rs.getString("data_offtrain"), new TypeToken<List<Integer>>() {
      }.getType());
      if (trainData != null && trainData.size() >= 2) {
         player.typetrain = trainData.get(0).byteValue();
         player.istrain = trainData.get(1) == 1;
      }

      List<Card> cards = gson.fromJson(rs.getString("collection_book"), new TypeToken<List<Card>>() {
      }.getType());
      CollectionBook book = new CollectionBook();
      book.setCards(cards != null ? cards : new ArrayList<>());
      book.init();
      player.setCollectionBook(book);

      player.firstTimeLogin = rs.getTimestamp("firstTimeLogin");

      byte[] buyLimit = gson.fromJson(rs.getString("buy_limit"), byte[].class);
      if (buyLimit != null)
         player.buyLimit = buyLimit;

      byte[] rewardLimit = gson.fromJson(rs.getString("reward_limit"), byte[].class);
      if (rewardLimit != null)
         player.rewardLimit = rewardLimit;

      List<Integer> challengeData = gson.fromJson(rs.getString("challenge"), new TypeToken<List<Integer>>() {
      }.getType());
      if (challengeData != null && challengeData.size() >= 3) {
         player.goldChallenge = challengeData.get(0);
         player.levelWoodChest = challengeData.get(1);
         player.receivedWoodChest = challengeData.get(2) == 1;
      }

      List<Long> dhData = gson.fromJson(rs.getString("danh_hieu"), new TypeToken<List<Long>>() {
      }.getType());
      if (dhData != null && dhData.size() >= 15) {
         player.isTitleUse1 = dhData.get(0) == 1;
         player.lastTimeTitle1 = dhData.get(1);
         player.IdDanhHieu_1 = dhData.get(2).intValue();
         player.isTitleUse2 = dhData.get(3) == 1;
         player.lastTimeTitle2 = dhData.get(4);
         player.IdDanhHieu_2 = dhData.get(5).intValue();
         player.isTitleUse3 = dhData.get(6) == 1;
         player.lastTimeTitle3 = dhData.get(7);
         player.IdDanhHieu_3 = dhData.get(8).intValue();
         player.isTitleUse4 = dhData.get(9) == 1;
         player.lastTimeTitle4 = dhData.get(10);
         player.IdDanhHieu_4 = dhData.get(11).intValue();
         player.isTitleUse5 = dhData.get(12) == 1;
         player.lastTimeTitle5 = dhData.get(13);
         player.IdDanhHieu_5 = dhData.get(14).intValue();
      }

      for (int i = 1; i <= 5; i++) {
         long lastTime = (i == 1) ? player.lastTimeTitle1
               : (i == 2) ? player.lastTimeTitle2
                     : (i == 3) ? player.lastTimeTitle3 : (i == 4) ? player.lastTimeTitle4 : player.lastTimeTitle5;
         if ("Hết hạn".equals(Util.msToTime(lastTime))) {
            if (i == 1) {
               player.isTitleUse1 = false;
               player.lastTimeTitle1 = 0;
            } else if (i == 2) {
               player.isTitleUse2 = false;
               player.lastTimeTitle2 = 0;
            } else if (i == 3) {
               player.isTitleUse3 = false;
               player.lastTimeTitle3 = 0;
            } else if (i == 4) {
               player.isTitleUse4 = false;
               player.lastTimeTitle4 = 0;
            } else {
               player.isTitleUse5 = false;
               player.lastTimeTitle5 = 0;
            }
         }
      }

      List<Integer> resetData = gson.fromJson(rs.getString("reset_ngay"), new TypeToken<List<Integer>>() {
      }.getType());
      if (resetData != null && resetData.size() >= 2) {
         player.bongtai = resetData.get(0);
         player.thiensu = resetData.get(1);
      }

      PlayerService.gI().dailyLogin(player);
   }

   private static void loadSkill(Player player, ResultSet rs) throws Exception {
      List<List<Object>> skillsList = gson.fromJson(rs.getString("skills"), new TypeToken<List<List<Object>>>() {
      }.getType());
      if (skillsList != null) {
         for (List<Object> skillTemp : skillsList) {
            int tempId = ((Double) skillTemp.get(0)).intValue();
            byte point = ((Double) skillTemp.get(2)).byteValue();
            Skill skill = point != 0 ? SkillUtil.createSkill(tempId, point) : SkillUtil.createSkillLevel0(tempId);
            skill.lastTimeUseThisSkill = ((Double) skillTemp.get(1)).longValue();
            if (skillTemp.size() > 3)
               skill.currLevel = ((Double) skillTemp.get(3)).shortValue();
            player.playerSkill.skills.add(skill);
         }
      }

      List<Integer> shortcutData = gson.fromJson(rs.getString("skills_shortcut"), new TypeToken<List<Integer>>() {
      }.getType());
      if (shortcutData != null) {
         for (int i = 0; i < shortcutData.size() && i < player.playerSkill.skillShortCut.length; i++) {
            player.playerSkill.skillShortCut[i] = shortcutData.get(i).byteValue();
         }
      }

      for (int ssc : player.playerSkill.skillShortCut) {
         if (player.playerSkill.getSkillbyId(ssc) != null && player.playerSkill.getSkillbyId(ssc).damage > 0) {
            player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(ssc);
            break;
         }
      }
      if (player.playerSkill.skillSelect == null) {
         player.playerSkill.skillSelect = player.playerSkill
               .getSkillbyId(player.gender == ConstPlayer.TRAI_DAT ? Skill.DRAGON
                     : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
      }
   }

   private static void loadPet(Player player, ResultSet rs) throws Exception {
      Map<String, Object> petInfo = gson.fromJson(rs.getString("pet_info"), new TypeToken<Map<String, Object>>() {
      }.getType());
      if (petInfo != null && !petInfo.isEmpty()) {
         Pet pet = new Pet(player);
         pet.id = -player.id;
         pet.gender = ((Double) petInfo.get("gender")).byteValue();
         pet.typePet = ((Double) petInfo.get("is_mabu")).byteValue();
         pet.name = String.valueOf(petInfo.get("name"));
         player.fusion.typeFusion = ((Double) petInfo.get("type_fusion")).byteValue();
         player.fusion.lastTimeFusion = System.currentTimeMillis()
               - (Fusion.TIME_FUSION - ((Double) petInfo.get("left_fusion")).intValue());
         pet.status = ((Double) petInfo.get("status")).byteValue();

         Map<String, Object> petPoint = gson.fromJson(rs.getString("pet_point"), new TypeToken<Map<String, Object>>() {
         }.getType());
         pet.nPoint.stamina = ((Double) petPoint.get("stamina")).shortValue();
         pet.nPoint.maxStamina = ((Double) petPoint.get("max_stamina")).shortValue();
         pet.nPoint.hpg = (Double) petPoint.get("hpg");
         pet.nPoint.mpg = (Double) petPoint.get("mpg");
         pet.nPoint.dameg = (Double) petPoint.get("damg");
         pet.nPoint.defg = (Double) petPoint.get("defg");
         pet.nPoint.critg = ((Double) petPoint.get("critg")).intValue();
         pet.nPoint.power = (Double) petPoint.get("power");
         pet.nPoint.tiemNang = (Double) petPoint.get("tiem_nang");
         pet.nPoint.limitPower = ((Double) petPoint.get("limit_power")).byteValue();
         pet.nPoint.hp = (Double) petPoint.get("hp");
         pet.nPoint.mp = (Double) petPoint.get("mp");

         pet.inventory.itemsBody.addAll(loadRuong(rs.getString("pet_body")));

         List<List<Object>> petSkillsList = gson.fromJson(rs.getString("pet_skill"),
               new TypeToken<List<List<Object>>>() {
               }.getType());
         if (petSkillsList != null) {
            for (List<Object> skillTemp : petSkillsList) {
               int tempId = ((Double) skillTemp.get(0)).intValue();
               byte point = ((Double) skillTemp.get(1)).byteValue();
               Skill skill = point != 0 ? SkillUtil.createSkill(tempId, point) : SkillUtil.createSkillLevel0(tempId);
               if (skill.template.id == Skill.KAMEJOKO || skill.template.id == Skill.MASENKO
                     || skill.template.id == Skill.ANTOMIC) {
                  skill.coolDown = 1000;
               }
               pet.playerSkill.skills.add(skill);
            }
         }
         player.pet = pet;
      }

      List<Map<String, Object>> friendsData = gson.fromJson(rs.getString("friends"),
            new TypeToken<List<Map<String, Object>>>() {
            }.getType());
      if (friendsData != null) {
         for (Map<String, Object> obj : friendsData) {
            Friend f = new Friend();
            f.id = ((Double) obj.get("id")).intValue();
            f.name = String.valueOf(obj.get("name"));
            f.head = ((Double) obj.get("head")).shortValue();
            f.body = ((Double) obj.get("body")).shortValue();
            f.leg = ((Double) obj.get("leg")).shortValue();
            f.bag = ((Double) obj.get("bag")).byteValue();
            f.power = (Double) obj.get("power");
            player.friends.add(f);
         }
      }

      List<Map<String, Object>> enemiesData = gson.fromJson(rs.getString("enemies"),
            new TypeToken<List<Map<String, Object>>>() {
            }.getType());
      if (enemiesData != null) {
         for (Map<String, Object> obj : enemiesData) {
            Enemy e = new Enemy();
            e.id = ((Double) obj.get("id")).intValue();
            e.name = String.valueOf(obj.get("name"));
            e.head = ((Double) obj.get("head")).shortValue();
            e.body = ((Double) obj.get("body")).shortValue();
            e.leg = ((Double) obj.get("leg")).shortValue();
            e.bag = ((Double) obj.get("bag")).byteValue();
            e.power = (Double) obj.get("power");
            player.enemies.add(e);
         }
      }
   }
}
