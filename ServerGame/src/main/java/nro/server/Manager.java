package nro.server;

import lombok.Getter;
import nro.attr.Attribute;
import nro.attr.AttributeManager;
import nro.consts.ConstItem;
import nro.consts.ConstMap;
import nro.consts.ConstNpc;
import nro.data.DataGame;
import nro.jdbc.DBService;
import nro.jdbc.daos.manager.ServiceDataDAO;
import nro.jdbc.daos.ShopDAO;
import nro.jdbc.daos.manager.CaiTrangDAO;
import nro.jdbc.daos.manager.ClanDAO;
import nro.jdbc.daos.manager.FlagBagDAO;
import nro.jdbc.daos.manager.HeadAvatarDAO;
import nro.jdbc.daos.manager.IntrinsicDAO;
import nro.jdbc.daos.manager.ItemTemplateDAO;
import nro.jdbc.daos.manager.MapTemplateDAO;
import nro.jdbc.daos.manager.MobTemplateDAO;
import nro.jdbc.daos.manager.NpcTemplateDAO;
import nro.jdbc.daos.manager.PartDAO;
import nro.jdbc.daos.manager.SideTaskDAO;
import nro.jdbc.daos.manager.SkillDAO;
import nro.jdbc.daos.manager.TaskDAO;
import nro.utils.RandomCollection;
import nro.manager.NamekBallManager;
import nro.manager.SieuHangManager;
import nro.models.clan.Clan;
import nro.models.intrinsic.Intrinsic;
import nro.models.item.CaiTrang;
import nro.models.item.FlagBag;
import nro.models.item.HeadAvatar;
import nro.models.item.Item;
import nro.models.item.ItemLuckyRound;
import nro.models.item.ItemOptionTemplate;
import nro.models.item.ItemTemplate;
import nro.models.map.MapTemplate;
import nro.models.map.SantaCity;
import nro.models.mob.MobReward;
import nro.models.mob.MobTemplate;
import nro.models.npc.Npc;
import nro.models.npc.special.ConMeo;
import nro.models.npc.special.RongThieng;
import nro.models.npc.NpcTemplate;
import nro.models.player.Referee;
import nro.models.shop.Shop;
import nro.models.skill.NClass;
import nro.models.task.SideTaskTemplate;
import nro.models.task.TaskMain;
import nro.services.MapService;
import nro.utils.Log;
import nro.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import nro.models.player.TestDame;
import nro.services.PhucLoiManager;
import nro.services.func.SoMayMan;
import nro.services.func.TaiXiu;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nro.services.GameDuDoan;
import nro.services.AutoBotChatService;
import nro.services.BossFollowerService;
import nro.core.GameLoop;
import nro.core.GameScheduler;

public class Manager {

   private static Manager i;

   public static byte SERVER = 1;
   public static byte SECOND_WAIT_LOGIN = 20;
   public static byte MAX_PER_IP = 5;
   public static int MAX_PLAYER = 1000;
   public static int RATE_EXP_SERVER = 1;
   public static int TILE_ROI_A = 1;
   public static int TILE_ROI_B = 1;
   public static int TILE_NCAP = 0;
   public static byte SUKIEN = 6;
   public static int EVENT_SEVER = 6;
   public static String DOMAIN = "127.0.0.1";
   public static String SERVER_NAME = "Ngọc Rồng Zeion";
   public static int EVENT_COUNT_THAN_HUY_DIET = 0;
   public static int EVENT_COUNT_QUY_LAO_KAME = 0;
   public static int EVENT_COUNT_THAN_MEO = 0;
   public static int EVENT_COUNT_THUONG_DE = 0;
   public static int EVENT_COUNT_THAN_VU_TRU = 0;
   public static String loginHost;
   public static int loginPort;
   public static int apiPort = 8080;
   public static int bossGroup = 1;
   public static int workerGroup = 8;
   public static String apiKey = "abcdef";
   public static String executeCommand;
   public static boolean debug;
   public static boolean isBotEnabled;
   public static String NgayRunServer;

   public static int MAX_BAG = 109;
   public static int MAX_BOX = 119;

   public static byte KHUYEN_MAI_NAP = 1;

   public static short[][] POINT_MABU_MAP = {
         { 196, 259 },
         { 340, 259 },
         { 413, 236 },
         { 532, 259 }
   };

   private static final int MAX_THREADS = 20;
   private static final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

   public static void run(Runnable task) {
      threadPool.execute(task);
   }

   public static void shutdown() {
      threadPool.shutdown();
   }

   public static final List<String> TOP_PLAYERS = new CopyOnWriteArrayList<>();

   public static Map<Integer, MapTemplate> MAP_TEMPLATES = new ConcurrentHashMap<>();

   public static final List<nro.models.map.Map> MAPS = new CopyOnWriteArrayList<>();

   public static final Map<Integer, ItemOptionTemplate> ITEM_OPTION_TEMPLATES = new ConcurrentHashMap<>();

   public static final List<MobReward> MOB_REWARDS = new CopyOnWriteArrayList<>();

   public static final RandomCollection<ItemLuckyRound> LUCKY_ROUND_REWARDS = new RandomCollection<>();

   public static final Map<Integer, ItemTemplate> ITEM_TEMPLATES = new ConcurrentHashMap<>();

   public static final Map<Integer, MobTemplate> MOB_TEMPLATES = new ConcurrentHashMap<>();

   public static final Map<Integer, NpcTemplate> NPC_TEMPLATES = new ConcurrentHashMap<>();

   public static final List<String> CAPTIONS = new CopyOnWriteArrayList<>();

   public static final List<TaskMain> TASKS = new CopyOnWriteArrayList<>();

   public static final List<SideTaskTemplate> SIDE_TASKS_TEMPLATE = new CopyOnWriteArrayList<>();

   public static final List<Intrinsic> INTRINSICS = new CopyOnWriteArrayList<>();

   public static final List<Intrinsic> INTRINSIC_TD = new CopyOnWriteArrayList<>();

   public static final List<Intrinsic> INTRINSIC_NM = new CopyOnWriteArrayList<>();

   public static final List<Intrinsic> INTRINSIC_XD = new CopyOnWriteArrayList<>();

   public static final Map<Integer, HeadAvatar> HEAD_AVATARS = new ConcurrentHashMap<>();

   public static final List<FlagBag> FLAGS_BAGS = new CopyOnWriteArrayList<>();

   public static final Map<Integer, CaiTrang> CAI_TRANGS = new ConcurrentHashMap<>();

   public static final List<NClass> NCLASS = new CopyOnWriteArrayList<>();

   public static final List<Npc> NPCS = new CopyOnWriteArrayList<>();

   public static List<Shop> SHOPS = new CopyOnWriteArrayList<>();

   public static final List<Clan> CLANS = new CopyOnWriteArrayList<>();
   public static final ByteArrayOutputStream[] cache = new ByteArrayOutputStream[4];
   public static final RandomCollection<Integer> HONG_DAO_CHIN = new RandomCollection<>();
   public static final RandomCollection<Integer> HOP_QUA_TET = new RandomCollection<>();
   public static final List<PhucLoiManager> PHUCLOI_MANAGER = new CopyOnWriteArrayList<>();

   public static final List<Item> CT = new CopyOnWriteArrayList<>();

   public static final List<Item> FLAG = new CopyOnWriteArrayList<>();

   public static final short[] daCuongHoa = { 1791, 1792, 1793, 1794, 1795, 1796, 1563, 1564, 1565, 1559, 1560, 1561,
         1562, 1797, 1419, 1420, 1421, 1422, 1423 };
   public static final short[] radaSKHVip = { 12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561, 656, 1060,
         1061, 1062 };
   public static final short[] radaSKHThuong = { 12 };
   public static final short[][] doSKHThuong = { { 0, 6, 21, 27 }, { 1, 7, 22, 28 }, { 2, 8, 23, 29 } };

   public static final short[] DoThanhTon = { 1401, 1402, 1403, 1404, 1405 };
   public static final short[] aotd = { 0, 3, 33, 34, 136, 137, 138, 139, 230, 231, 232, 233, 555, 650, 1048 };
   public static final short[] quantd = { 6, 9, 35, 36, 140, 141, 142, 143, 242, 243, 244, 245, 556, 651, 1051 };
   public static final short[] gangtd = { 21, 24, 37, 38, 144, 145, 146, 147, 254, 255, 256, 257, 562, 657, 1054 };
   public static final short[] giaytd = { 27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269, 563, 658, 1057 };
   public static final short[] aoxd = { 2, 5, 49, 50, 168, 169, 170, 171, 238, 239, 240, 241, 559, 654, 1050 };
   public static final short[] quanxd = { 8, 11, 51, 52, 172, 173, 174, 175, 250, 251, 252, 253, 560, 655, 1053 };
   public static final short[] gangxd = { 23, 26, 53, 54, 176, 177, 178, 179, 262, 263, 264, 265, 566, 661, 1056 };
   public static final short[] giayxd = { 29, 32, 55, 56, 180, 181, 182, 183, 274, 275, 276, 277, 567, 662, 1059 };
   public static final short[] aonm = { 1, 4, 41, 42, 152, 153, 154, 155, 234, 235, 236, 237, 557, 652, 1049 };
   public static final short[] quannm = { 7, 10, 43, 44, 156, 157, 158, 159, 246, 247, 248, 249, 558, 653, 1052 };
   public static final short[] gangnm = { 22, 25, 45, 46, 160, 161, 162, 163, 258, 259, 260, 261, 564, 659, 1055 };
   public static final short[] giaynm = { 28, 31, 47, 48, 164, 165, 166, 167, 270, 271, 272, 273, 565, 660, 1058 };
   public static final short[][][] doSKHVip = { { aotd, quantd, gangtd, giaytd }, { aonm, quannm, gangnm, giaynm },
         { aoxd, quanxd, gangxd, giayxd } };

   public static final short[][] doSKHTl = { { 555, 556, 562, 563, 561 }, { 557, 558, 564, 565, 561 },
         { 559, 560, 566, 567, 561 } };
   public static final short[][] doSKHHd = { { 650, 651, 657, 658, 656 }, { 652, 653, 659, 660, 656 },
         { 654, 655, 661, 662, 656 } };
   public static final short[][] doSKHTs = { { 1048, 1051, 1054, 1057, 1060 }, { 1049, 1052, 1055, 1058, 1061 },
         { 1050, 1053, 1056, 1059, 1062 } };

   public static final short[] itemIds_TL = { 555, 557, 559, 556, 558, 560, 562, 564, 566, 563, 565, 567, 561 };
   public static final short[] itemIds_HuyDiet = { 650, 651, 652, 653, 654, 655, 656, 657, 658, 659, 660, 661, 662 };
   public static final short[][] doHuyDiet = { { 650, 651, 657, 658, 656 }, { 652, 653, 659, 660, 656 },
         { 654, 655, 661, 662, 656 }, { 654, 655, 661, 662, 656 } };

   public static final int[] IdMapSpam = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
         16, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34,
         35, 36, 37, 38, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75,
         76, 77, 79, 80, 81, 82, 83, 92, 93, 94, 96, 97,
         98, 99, 100, 105, 106, 107, 108, 109, 110, 155 };
   // public static final short[] CT_BOT = {608, 612, 613, 614, 615, 616, 607, 605,
   // 604, 583, 578, 577, 617, 630, 609, 606, 289, 288, 550
   // , 724, 647, 634, 633, 632, 631, 406, 427, 286, 285, 284, 283, 282, 428, 429,
   // 424, 287, 407, 423, 425, 426, 405, 292, 291, 290
   // , 430, 431, 432, 525, 526, 527, 528, 549, 551, 552, 575, 524, 461, 458, 433,
   // 448, 449, 450, 576, 451, 452, 455};
   public static final short[] CT_BOT = { 1536, 1600, 1618, 1619, 1620, 1621, 1623, 1624, 1625, 1626,
         1627, 1628, 1411, 1412, 1413, 1416, 1251, 1252, 1253, 989, 990, 991, 1134, 1208, 1320
   };
   public static final short[] FLAG_BOT = {
         // 1650, 1651, 1652, 1653, 1654, 1655, 1656, 1657, 1658, 1659, 1660, 1661, 1662,
         // 1663, 1664,
         // 1665, 1666, 1667, 1668, 1669, 1670, 1671, 1672, 1673, 1674, 1675, 1676, 1677,
         // 1678,
         1159, 1160, 1161, 1162, 1163
   };
   // public static short[] CT = {1220, 1224, 1251, 1252, 1253, 1267, 1268, 1270,
   // 1271, 1272, 1299, 1310, 1311, 1319, 1320
   // , 1321, 1353, 1354, 1355, 1357, 1358, 1384, 1387, 1388, 1389, 1390, 1396,
   // 1398, 1406, 1408, 1467, 1468, 1469};
   @Getter
   public GameConfig gameConfig;

   public static Manager gI() {
      if (i == null) {
         i = new Manager();
      }
      return i;
   }

   private Manager() {
      try {
         Log.log("Đang bắt đầu load server.properties...");
         loadProperties();
         Log.log("Đang bắt đầu load game.properties...");
         gameConfig = new GameConfig();
      } catch (Exception ex) {
         Log.error(Manager.class, ex, "Lỗi load properties");
         System.exit(0);
      }
      Log.log("Đang bắt đầu load database...");
      loadDatabase();
      new ConMeo(-1, -1, -1, -1, ConstNpc.CON_MEO, 29028);
      new RongThieng(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1);
      SieuHangManager.gI().init();
      // Event.initEvent(gameConfig.getEvent());
      // if (Event.isEvent()) {
      // Event.getInstance().init();
      // }
      initRandomItem();
      NamekBallManager.gI().initBall();
   }

   private void initRandomItem() {
      HONG_DAO_CHIN.add(50, ConstItem.CHU_GIAI);
      HONG_DAO_CHIN.add(50, ConstItem.HONG_NGOC);

      HOP_QUA_TET.add(10, ConstItem.DIEU_RONG);
      HOP_QUA_TET.add(10, ConstItem.DAO_RANG_CUA);
      HOP_QUA_TET.add(10, ConstItem.QUAT_BA_TIEU);
      HOP_QUA_TET.add(10, ConstItem.BUA_MJOLNIR);
      HOP_QUA_TET.add(10, ConstItem.BUA_STORMBREAKER);
      HOP_QUA_TET.add(10, ConstItem.DINH_BA_SATAN);
      HOP_QUA_TET.add(10, ConstItem.CHOI_PHU_THUY);
      HOP_QUA_TET.add(10, ConstItem.MANH_AO);
      HOP_QUA_TET.add(10, ConstItem.MANH_QUAN);
      HOP_QUA_TET.add(10, ConstItem.MANH_GIAY);
      HOP_QUA_TET.add(10, ConstItem.MANH_NHAN);
      HOP_QUA_TET.add(10, ConstItem.MANH_GANG_TAY);
      HOP_QUA_TET.add(8, ConstItem.PHUONG_HOANG_LUA);
      // HOP_QUA_TET.add(7, ConstItem.CAI_TRANG_SSJ_3_WHITE);
      HOP_QUA_TET.add(7, ConstItem.NOEL_2022_GOKU);
      HOP_QUA_TET.add(7, ConstItem.NOEL_2022_CADIC);
      HOP_QUA_TET.add(7, ConstItem.NOEL_2022_POCOLO);
      HOP_QUA_TET.add(20, ConstItem.CUONG_NO_2);
      HOP_QUA_TET.add(20, ConstItem.BO_HUYET_2);
      HOP_QUA_TET.add(20, ConstItem.BO_KHI_2);
   }

   private void initMap() {
      int[][] tileTyleTop = readTileIndexTileType(ConstMap.TILE_TOP);
      for (MapTemplate mapTemp : MAP_TEMPLATES.values()) {
         int[][] tileMap = readTileMap(mapTemp.id);
         int[] tileTop = tileTyleTop[mapTemp.tileId - 1];
         nro.models.map.Map map = null;
         if (mapTemp.id == 126) {
            map = new SantaCity(mapTemp.id,
                  mapTemp.name, mapTemp.planetId, mapTemp.tileId, mapTemp.bgId,
                  mapTemp.bgType, mapTemp.type, tileMap, tileTop,
                  mapTemp.zones, mapTemp.isMapOffline(),
                  mapTemp.maxPlayerPerZone, mapTemp.wayPoints, mapTemp.effectMaps);
            SantaCity santaCity = (SantaCity) map;
            santaCity.timer(22, 0, 0, 3600000);
         } else {
            map = new nro.models.map.Map(mapTemp.id,
                  mapTemp.name, mapTemp.planetId, mapTemp.tileId, mapTemp.bgId,
                  mapTemp.bgType, mapTemp.type, tileMap, tileTop,
                  mapTemp.zones, mapTemp.isMapOffline(),
                  mapTemp.maxPlayerPerZone, mapTemp.wayPoints, mapTemp.effectMaps);
         }
         if (map != null) {
            MAPS.add(map);
            map.initMob(mapTemp.mobTemp, mapTemp.mobLevel, mapTemp.mobHp, mapTemp.mobX, mapTemp.mobY);
            map.initNpc(mapTemp.npcId, mapTemp.npcX, mapTemp.npcY, mapTemp.npcAvatar);
            GameLoop.gI().register(map);
         }
      }

      Referee r = new Referee();
      r.initReferee();

      TestDame r2 = new TestDame();
      r2.initTestDame();

      Log.success("Initialization of map successful!");
   }

   private void loadDatabase() {
      long st = System.currentTimeMillis();

      try (Connection con = DBService.gI().getConnection();) {
         // Load Part
         PartDAO.load(con);

         // Load Map Template
         MAP_TEMPLATES = MapTemplateDAO.load(con);

         // Load Skill
         SkillDAO.load(con, NCLASS);

         // Load Head Avatar
         HeadAvatarDAO.load(con, HEAD_AVATARS);

         // Load Flag Bag
         FlagBagDAO.load(con, FLAGS_BAGS);

         // Load Costume (Cai Trang)
         CaiTrangDAO.load(con, CAI_TRANGS);

         // Load Intrinsic
         IntrinsicDAO.load(con, INTRINSICS, INTRINSIC_TD, INTRINSIC_NM, INTRINSIC_XD);

         // Load Task
         TaskDAO.load(con, TASKS);

         // Load Side Task
         SideTaskDAO.load(con, SIDE_TASKS_TEMPLATE);

         // Load Items
         ItemTemplateDAO.load(con, ITEM_TEMPLATES, ITEM_OPTION_TEMPLATES);

         // Load Shop
         SHOPS = ShopDAO.getShops(con);
         Log.success("Shop loaded successfully (" + SHOPS.size() + ")");

         // Load Mob Template
         MobTemplateDAO.load(con, MOB_TEMPLATES);

         // Load NPC Template
         NpcTemplateDAO.load(con, NPC_TEMPLATES);

         initMap();

         // Load Clan
         ClanDAO.load(con, CLANS);

         // Load Miscellaneous Templates & Services (Consolidated DAO)
         ServiceDataDAO.loadTemplates(con);

      } catch (Exception e) {
         Log.error(Manager.class, e, "Error loading database");
         System.exit(0);
      }

      LocalDateTime localNow = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      DateTimeFormatter giophutgiay = DateTimeFormatter.ofPattern("HH:mm:ss");
      String timeString = localNow.format(giophutgiay);
      String dateString = localNow.format(formatter);
      NgayRunServer = timeString + " Date: " + dateString;
      TaiXiu.gI().lastTimeEnd = System.currentTimeMillis() + 50000;
      SoMayMan.gI().lastTimeEnd = System.currentTimeMillis() + 60000;

      GameScheduler.SCHED.scheduleAtFixedRate(TaiXiu.gI(), 0, 1000, TimeUnit.MILLISECONDS);
      GameScheduler.SCHED.scheduleAtFixedRate(SoMayMan.gI(), 0, 1000, TimeUnit.MILLISECONDS);

      GameDuDoan.gI().lastTimeEnd = System.currentTimeMillis() + GameDuDoan.TIME_TAI_XIU;
      GameScheduler.SCHED.scheduleAtFixedRate(GameDuDoan.gI(), 0, 1000, TimeUnit.MILLISECONDS);
      Log.log(
            "Total database loading time: " + (System.currentTimeMillis() - st) + "(ms)");
      if (isBotEnabled) {
         GameScheduler.SCHED.schedule(() -> {
            try {
               for (int a = 0; a < 199; a++) {
                  nro.services.BotManager.gI().createBot();
                  Thread.sleep(80);
               }
               // BossFollowerService.gI().update(); // Scheduled below
               // AutoBotChatService.gI().update(); // Scheduled below
               Log.success("Created 199 bots successfully");
            } catch (Exception e) {
               e.printStackTrace();
            }
         }, 30, TimeUnit.SECONDS);

         GameScheduler.SCHED.scheduleAtFixedRate(() -> AutoBotChatService.gI().update(), 1, 5, TimeUnit.MINUTES);
         GameScheduler.SCHED.scheduleAtFixedRate(() -> new BossFollowerService().update(), 1, 1, TimeUnit.MINUTES);
      }
   }

   public static MapTemplate getMapTemplate(int mapID) {
      return MAP_TEMPLATES.get(mapID);
   }

   public static void loadEventCount() {
      try (Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from event where server =" + SERVER)) {
         try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
               EVENT_COUNT_QUY_LAO_KAME = rs.getInt("kame");
               EVENT_COUNT_THAN_HUY_DIET = rs.getInt("bill");
               EVENT_COUNT_THAN_MEO = rs.getInt("karin");
               EVENT_COUNT_THUONG_DE = rs.getInt("thuongde");
               EVENT_COUNT_THAN_VU_TRU = rs.getInt("thanvutru");
            }
         }
      } catch (Exception e) {
         Logger.getLogger(Manager.class
               .getName()).log(Level.SEVERE, null, e);
      }
   }

   public void updateEventCount() {
      try (Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement(
                  "UPDATE event SET kame = ?, bill = ?, karin = ?, thuongde = ?, thanvutru = ? WHERE `server` = ?")) {
         ps.setInt(1, EVENT_COUNT_QUY_LAO_KAME);
         ps.setInt(3, EVENT_COUNT_THAN_HUY_DIET);
         ps.setInt(2, EVENT_COUNT_THAN_MEO);
         ps.setInt(4, EVENT_COUNT_THUONG_DE);
         ps.setInt(5, EVENT_COUNT_THAN_VU_TRU);
         ps.setInt(6, SERVER);
         ps.executeUpdate();
      } catch (SQLException ex) {
         Logger.getLogger(Manager.class
               .getName()).log(Level.SEVERE, null, ex);
      }
   }

   public static void removeClan(Clan clan) {
      CLANS.remove(clan);
   }

   public void updateAttributeServer() {
      AttributeManager am = ServerManager.gI().getAttributeManager();
      List<Attribute> attributes = am.getAttributes();
      try (Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement(
                  "UPDATE `attribute_server` SET `attribute_template_id` = ?, `value` = ?, `time` = ? WHERE `id` = ?;")) {
         synchronized (attributes) {
            for (Attribute at : attributes) {
               try {
                  if (at.isChanged()) {
                     ps.setInt(1, at.getTemplate().id);
                     ps.setInt(2, at.getValue());
                     ps.setInt(3, at.getTime());
                     ps.setInt(4, at.id);
                     ps.addBatch();
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         }
         ps.executeBatch();
      } catch (SQLException ex) {
         Logger.getLogger(Manager.class
               .getName()).log(Level.SEVERE, null, ex);
      }
   }

   public void loadProperties() throws IOException {
      Properties properties = new Properties();
      String fileName = "config/server.properties";
      File file = new File(fileName);
      Log.log("Kiểm tra file: " + file.getAbsolutePath());
      if (!file.exists()) {
         Log.error("Không tìm thấy file: " + fileName);
      } else {
         Log.log("File tồn tại. Đang mở FileInputStream...");
      }

      try (FileInputStream fis = new FileInputStream(file)) {
         Log.log("Đã mở FileInputStream. Đang nạp Properties (Giới hạn 30s)...");
         try {
            Util.runWithTimeout(() -> {
               try {
                  properties.load(fis);
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }, 30, TimeUnit.SECONDS);
            Log.log("Nạp Properties thành công...");
         } catch (Exception e) {
            Log.error("Lỗi nạp properties (có thể do treo): " + e.getMessage());
            throw new IOException("Timeout hoặc lỗi khi nạp properties", e);
         }
      }

      Log.log("Đang bắt đầu gán Properties từ server.properties...");
      Object value = null;
      // ###Config db
      if ((value = properties.get("server.db.driver")) != null) {
         DBService.DRIVER = String.valueOf(value);
      }
      if ((value = properties.get("server.db.ip")) != null) {
         DBService.DB_HOST = String.valueOf(value);
      }
      if ((value = properties.get("server.db.port")) != null) {
         DBService.DB_PORT = Integer.parseInt(String.valueOf(value));
      }
      if ((value = properties.get("server.db.name")) != null) {
         DBService.DB_NAME = String.valueOf(value);
      }
      if ((value = properties.get("server.db.us")) != null) {
         DBService.DB_USER = String.valueOf(value);
      }
      if ((value = properties.get("server.db.pw")) != null) {
         DBService.DB_PASSWORD = String.valueOf(value);
      }
      if ((value = properties.get("server.db.max")) != null) {
         DBService.MAX_CONN = Integer.parseInt(String.valueOf(value));
      }
      if (properties.containsKey("login.host")) {
         loginHost = properties.getProperty("login.host");
      } else {
         loginHost = "127.0.0.1";
      }
      if (properties.containsKey("login.port")) {
         loginPort = Integer.parseInt(properties.getProperty("login.port"));
      } else {
         loginPort = 8888;
      }
      if (properties.containsKey("update.timelogin")) {
         ServerManager.updateTimeLogin = Boolean.parseBoolean(properties.getProperty("update.timelogin"));
      }

      if (properties.containsKey("execute.command")) {
         executeCommand = properties.getProperty("execute.command");
      }

      Log.log("Đang gán Server Config...");
      // ###Config sv
      if ((value = properties.get("server.port")) != null) {
         ServerManager.PORT = Integer.parseInt(String.valueOf(value));
      }
      if ((value = properties.get("server.name")) != null) {
         ServerManager.NAME = String.valueOf(value);
      }
      if ((value = properties.get("server.sv")) != null) {
         SERVER = Byte.parseByte(String.valueOf(value));
      }
      if (properties.containsKey("server.debug")) {
         debug = Boolean.parseBoolean(properties.getProperty("server.debug"));
      } else {
         debug = false;
      }
      if (properties.containsKey("server.isBotEnabled")) {
         isBotEnabled = Boolean.parseBoolean(properties.getProperty("server.isBotEnabled"));
      } else {
         isBotEnabled = false;
      }
      if ((value = properties.get("api.key")) != null) {
         Manager.apiKey = String.valueOf(value);
      }
      if ((value = properties.get("api.port")) != null) {
         Manager.apiPort = Integer.parseInt(String.valueOf(value));
      }
      String linkServer = "";
      for (int i = 1; i <= 10; i++) {
         value = properties.get("server.sv" + i);
         if (value != null) {
            linkServer += String.valueOf(value) + ":0,";
         }
      }
      if (!linkServer.isEmpty()) {
         DataGame.LINK_IP_PORT = linkServer.substring(0, linkServer.length() - 1);
      } else {
         Log.warning("Không tìm thấy bất kỳ server.svX nào trong properties!");
      }
      if ((value = properties.get("server.waitlogin")) != null) {
         SECOND_WAIT_LOGIN = Byte.parseByte(String.valueOf(value));
      }
      if ((value = properties.get("server.maxperip")) != null) {
         MAX_PER_IP = Byte.parseByte(String.valueOf(value));
      }
      if ((value = properties.get("server.maxplayer")) != null) {
         MAX_PLAYER = Integer.parseInt(String.valueOf(value));
      }
      if ((value = properties.get("server.expserver")) != null) {
         RATE_EXP_SERVER = Integer.parseInt(String.valueOf(value));
      }
      if ((value = properties.get("server.event")) != null) {
         EVENT_SEVER = Byte.parseByte(String.valueOf(value));
      }
      if ((value = properties.get("server.name")) != null) {
         SERVER_NAME = String.valueOf(value);
      }
      if ((value = properties.get("server.domain")) != null) {
         DOMAIN = String.valueOf(value);
      }
   }

   /**
    * @param tileTypeFocus tile type: top, bot, left, right...
    *
    * @return [tileMapId][tileType]
    */
   private int[][] readTileIndexTileType(int tileTypeFocus) {
      int[][] tileIndexTileType = null;
      try (DataInputStream dis = new DataInputStream(new FileInputStream("resources/data/nro/map/tile_set_info"))) {
         int numTileMap = dis.readByte();
         tileIndexTileType = new int[numTileMap][];
         for (int i = 0; i < numTileMap; i++) {
            int numTileOfMap = dis.readByte();
            for (int j = 0; j < numTileOfMap; j++) {
               int tileType = dis.readInt();
               int numIndex = dis.readByte();
               if (tileType == tileTypeFocus) {
                  tileIndexTileType[i] = new int[numIndex];
               }
               for (int k = 0; k < numIndex; k++) {
                  int typeIndex = dis.readByte();
                  if (tileType == tileTypeFocus) {
                     tileIndexTileType[i][k] = typeIndex;
                  }
               }
            }
         }
      } catch (Exception e) {
         Log.error(MapService.class,
               e);
      }
      return tileIndexTileType;
   }

   /**
    * @param mapId mapId
    *
    * @return tile map for paint
    */
   private int[][] readTileMap(int mapId) {
      int[][] tileMap = null;
      try (DataInputStream dis = new DataInputStream(new FileInputStream("resources/map/" + mapId))) {
         int w = dis.readByte();
         int h = dis.readByte();
         tileMap = new int[h][w];
         for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[i].length; j++) {
               tileMap[i][j] = dis.readByte();
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return tileMap;
   }

   // service*******************************************************************
   public static Clan getClanById(int id) throws Exception {
      for (Clan clan : CLANS) {
         if (clan.id == id) {
            return clan;
         }
      }
      throw new Exception("Kh么ng t矛m th岷 clan id: " + id);
   }

   public static void addClan(Clan clan) {
      CLANS.add(clan);
   }

   public static int getNumClan() {
      return CLANS.size();

   }

   public static CaiTrang getCaiTrangByItemId(int itemId) {
      return CAI_TRANGS.get(itemId);
   }

   public static MobTemplate getMobTemplateByTemp(int mobTempId) {
      return MOB_TEMPLATES.get(mobTempId);
   }
}
