package nro.server;

import nro.attr.AttributeManager;
import nro.jdbc.DBService;
import nro.jdbc.daos.AccountDAO;
import nro.jdbc.daos.HistoryTransactionDAO;
import nro.jdbc.daos.PlayerDAO;
import nro.login.LoginSession;
import nro.network.netty.CommonHandler;
import nro.network.netty.NettyServer;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.manager.SieuHangManager;
import nro.models.map.challenge.MartialCongressManager;
import nro.models.map.dungeon.DungeonManager;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.map.phoban.DoanhTrai;
import nro.models.player.Player;
import nro.server.io.Session;
import nro.services.ClanService;
import nro.utils.Log;
import nro.utils.TimeUtil;
import nro.core.GameLoop;
import nro.core.GameScheduler;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerManager {

   public static String timeStart;

   public static final Map<String, Integer> CLIENTS = new ConcurrentHashMap<>();

   public static String NAME = "";

   public static int PORT = 14445;

   private Controller controller;

   private static ServerManager instance;

   private NettyServer nettyServer;

   public static boolean isRunning;

   @Getter
   public LoginSession login;

   public static boolean updateTimeLogin;

   @Getter
   @Setter
   public AttributeManager attributeManager;

   @Getter
   public DungeonManager dungeonManager;

   public void init() {
      Log.log("Đang khởi tạo Manager...");
      Manager.gI();
      Log.log("Đang dọn dẹp lịch sử giao dịch...");
      HistoryTransactionDAO.deleteHistory();
      Log.log("Đang khởi tạo Boss...");
      BossFactory.initBoss();
      this.controller = Controller.getInstance();
      if (updateTimeLogin) {
         AccountDAO.updateLastTimeLoginAllAccount();
      }
   }

   public static ServerManager gI() {
      if (instance == null) {
         instance = new ServerManager();
         instance.init();
      }
      return instance;
   }

   public static void main(String[] args) {
      Log.banner();
      Log.log("Hệ điều hành: " + System.getProperty("os.name"));
      Log.log("Phiên bản Java: " + System.getProperty("java.version"));
      Log.log("Đang kiểm tra môi trường...");
      timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
      ServerManager.gI().run();
   }

   public void run() {
      isRunning = true;

      activeCommandLine();
      GameLoop.gI().start();
      activeGame();
      activeLogin();
      SieuHangManager.gI().init();
      autoTask();
      (new AutoMaintenance(23, 58, 59)).start();
      activeNettyServer();

   }

   private void activeNettyServer() {
      try {
         // Sử dụng Key XOR {0} mặc định cho ServerGame
         byte[] key = { 0 };
         CommonHandler handler = new CommonHandler(controller);
         nettyServer = new NettyServer(PORT, key, handler);
         nettyServer.setPublicConfig(Manager.DOMAIN, PORT);
         nettyServer.setRedirect(false);

         // Cấu hình Session Factory để tạo nro.server.io.Session
         nettyServer.setSessionFactory((channel, id) -> {
            String ip = channel.remoteAddress().toString().replace("/", "").split(":")[0];
            if (canConnectWithIp(ip)) {
               Session session = new Session(channel, id);
               session.ipAddress = ip;
               return session;
            } else {
               channel.close();
               return null;
            }
         });

         Log.success("Netty ServerGame đang lắng nghe tại cổng: " + PORT);
         nettyServer.start();
      } catch (Exception e) {
         Log.error(ServerManager.class, e, "Lỗi khi khởi động Netty Server tại port " + PORT);
         System.exit(0);
      }
   }

   public void activeLogin() {
      login = new LoginSession();
      login.connect(Manager.loginHost, Manager.loginPort);
   }

   private boolean canConnectWithIp(String ipAddress) {
      Object o = CLIENTS.get(ipAddress);
      if (o == null) {
         CLIENTS.put(ipAddress, 1);
         return true;
      } else {
         int n = Integer.parseInt(String.valueOf(o));
         if (n < Manager.MAX_PER_IP) {
            n++;
            CLIENTS.put(ipAddress, n);
            return true;
         } else {
            return false;
         }
      }
   }

   public void disconnect(Session session) {
      Object o = CLIENTS.get(session.ipAddress);
      if (o != null) {
         int n = Integer.parseInt(String.valueOf(o));
         n--;
         if (n < 0) {
            n = 0;
         }
         CLIENTS.put(session.ipAddress, n);
      }
   }

   private void activeCommandLine() {
      new Thread(() -> {
         try (Scanner sc = new Scanner(System.in)) {
            while (isRunning) {
               if (sc.hasNextLine()) {
                  String line = sc.nextLine();
                  if (line.equals("baotri")) {
                     new Thread(() -> {
                        Maintenance.gI().start(1);
                     }).start();
                  } else if (line.equals("athread")) {
                     Log.log("Thread Server: " + Thread.activeCount());
                  } else if (line.equals("nplayer")) {
                     Log.log("Player in game: " + Client.gI().getPlayers().size());
                  } else if (line.equals("a")) {
                     new Thread(() -> {
                        Client.gI().close();
                     }).start();
                  }
               }
            }
         } catch (Exception e) {
            Log.error(ServerManager.class, e, "Lỗi command line");
         }
      }).start();
   }

   private void activeGame() {
      GameLoop.gI().register(BossManager.gI());
      if (attributeManager != null) {
         GameLoop.gI().register(attributeManager);
      }
      dungeonManager = new DungeonManager();
      GameLoop.gI().register(dungeonManager);
      GameLoop.gI().register(MartialCongressManager.gI());

      GameScheduler.SCHED.scheduleAtFixedRate(() -> {
         try {
            for (DoanhTrai dt : DoanhTrai.DOANH_TRAIS) {
               dt.update();
            }
            for (BanDoKhoBau bdkb : BanDoKhoBau.BAN_DO_KHO_BAUS) {
               bdkb.update();
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }, 0, 500, TimeUnit.MILLISECONDS);
   }

   public void close(long delay) {
      try {
         Manager.gI().updateEventCount();
      } catch (Exception e) {
         Log.error(ServerManager.class, e);
      }
      try {
         Manager.gI().updateAttributeServer();
      } catch (Exception e) {
         Log.error(ServerManager.class, e);
      }
      try {
         Client.gI().close();
      } catch (Exception e) {
         Log.error(ServerManager.class, e);
      }
      try {
         ClanService.gI().close();
      } catch (Exception e) {
         Log.error(ServerManager.class, e);
      }
      try {
         nro.manager.ConsignManager.getInstance().close();
      } catch (Exception e) {
         Log.error(ServerManager.class, e);
      }

      if (nettyServer != null) {
         nettyServer.stop();
      }

      Log.success("-----------------------------------------------------");
      Log.success("     >>> MAINTENANCE COMPLETED SUCCESSFULLY <<<      ");
      Log.success("-----------------------------------------------------");
      System.exit(0);
   }

   public void saveAll(boolean updateTimeLogout) {
      List<Player> list = Client.gI().getPlayers();
      // Tối ưu hóa: Lưu song song sử dụng Parallel Stream và Connection Pool
      list.parallelStream().forEach(player -> {
         try (Connection conn = DBService.gI().getConnection()) {
            PlayerDAO.updatePlayer(player, conn, updateTimeLogout);
         } catch (Exception e) {
            Log.error(ServerManager.class, e, "Lỗi khi lưu người chơi: " + player.name);
         }
      });
   }

   public void autoTask() {
      GameScheduler.SCHED.scheduleWithFixedDelay(() -> {
         saveAll(false);
      }, 300000, 300000, TimeUnit.MILLISECONDS);
   }

}
