package nro.server;

import nro.utils.Log;
import nro.models.pvp.PVP;
import nro.services.Service;
import nro.models.item.Item;
import nro.server.io.Session;
import nro.login.LoginSession;
import nro.services.MapService;
import nro.models.player.Player;
import nro.services.PlayerService;
import nro.services.ItemTimeService;
import nro.services.func.PVPServcice;
import nro.services.InventoryService;
import nro.services.func.SummonDragon;
import nro.models.map.war.NamekBallWar;
import nro.services.func.TransactionService;

import java.util.Map;
import lombok.Getter;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import nro.consts.ConstPlayer;

import nro.utils.Util;
import nro.models.map.Zone;
import nro.utils.SkillUtil;
import nro.models.player.Pet;
import nro.models.skill.Skill;
import nro.services.PetService;
import nro.services.ItemService;
import nro.models.player.Inventory;
import nro.models.phuban.DragonNamecWar.TranhNgoc;

public class Client implements Runnable {

   private static Client i;

   @Getter
   public final List<Session> sessions = new CopyOnWriteArrayList<>();

   private final Map<Integer, Session> sessions_id = new ConcurrentHashMap<>();

   private final Map<Long, Player> players_id = new ConcurrentHashMap<>();

   private final Map<Integer, Player> players_userId = new ConcurrentHashMap<>();

   private final Map<String, Player> players_name = new ConcurrentHashMap<>();

   private final List<Player> players = new CopyOnWriteArrayList<>();

   public final List<Player> bots = new CopyOnWriteArrayList<>();

   public final List<Pet> pets = new CopyOnWriteArrayList<>();

   public int id = 1_000_000_000;

   private Client() {
      new Thread(this).start();
   }

   public List<Player> getPlayers() {
      return new ArrayList<>(this.players);
   }

   public static Client gI() {
      if (i == null) {
         i = new Client();
      }
      return i;
   }

   public void put(Session session) {
      if (!sessions_id.containsValue(session)) {
         this.sessions_id.put(session.getId(), session);
      }
      if (!sessions.contains(session)) {
         this.sessions.add(session);
      }
   }

   public void put(Player player) {
      if (!players_id.containsKey(player.id)) {
         this.players_id.put(player.id, player);
      }
      if (!players_name.containsValue(player)) {
         this.players_name.put(player.name, player);
      }
      if (!players_userId.containsValue(player)) {
         this.players_userId.put(player.getSession().userId, player);
      }
      if (!players.contains(player)) {
         this.players.add(player);
      }

   }

   public void remove(Session session) {
      this.sessions_id.remove(session.getId());
      this.sessions.remove(session);
      LoginSession login = ServerManager.gI().login;
      if (login != null && login.connected) {
         login.service.logout(session.userId);
      }
      if (session.player != null) {
         this.remove(session.player);
         session.player.dispose();
      }
      if (session.loginSuccess && session.joinedGame) {
         session.loginSuccess = false;
         session.joinedGame = false;
         // AccountDAO.updateAccoutLogout(session);
      }
      ServerManager.gI().disconnect(session);
   }

   public void clear() {
      if (!bots.isEmpty()) {
         MapService.gI().exitMap(bots.get(0));
         MapService.gI().exitMap(bots.get(0).pet);
         pets.remove(0);
         players.remove(bots.get(0));
         this.players_id.remove(bots.get(0).id);
         this.players_name.remove(bots.get(0).name);
         // remove(bots.get(0));
         bots.remove(0);
      }
   }

   public void createBot() {
      try {
         Player pl = new Player();
         // pl.setSession(s);
         // pl.getSession().userId = id;
         System.out.println("Creat Bot:" + "[" + id + "]");
         pl.id = id;
         id++;
         pl.name = nro.services.BotManager.gI().generateRandomName();
         pl.gender = (byte) Util.nextInt(0, 2);
         pl.isBot = true;
         pl.isBoss = false;
         pl.isMiniPet = false;
         pl.isPet = false;
         pl.nPoint.power = Util.nextInt(10000, 5000000);
         pl.nPoint.power *= Util.nextInt(1, 2);
         pl.nPoint.hpg = Util.nextInt(1000, 10000);
         pl.nPoint.hpMax = Util.nextInt(1000, 10000);
         pl.nPoint.hp = pl.nPoint.hpMax / 2;
         pl.nPoint.mpMax = Util.nextInt(1000, 10000);
         pl.nPoint.dame = Util.nextInt(1000, 10000);
         pl.nPoint.stamina = 32000;
         Pet pet = new Pet(pl);
         PetService.gI().createPetIsBot(pl, (byte) 0);
         pet.changeStatus(Pet.FOLLOW);
         pet.isPet = true;
         pl.itemTime.isUseTDLT = true;
         pl.lastTimeMap = System.currentTimeMillis();
         ItemTimeService.gI().sendCanAutoPlay(pl);
         pl.typePk = ConstPlayer.NON_PK;
         // skill
         int[] skillsArr = pl.gender == 0 ? new int[] { 0, 1, 6, 20, 22 }
               : pl.gender == 1 ? new int[] { 17, 3, 7, 2, 18, 12 }
                     : new int[] { 4, 5, 8 };
         for (int j = 0; j < skillsArr.length; j++) {
            Skill skill = SkillUtil.createSkill(skillsArr[j], Util.nextInt(2, 7));
            pl.playerSkill.skills.add(skill);
         }
         pl.inventory = new Inventory(pl);
         for (int i = 0; i < 13; i++) {
            pl.inventory.itemsBody.add(ItemService.gI().createItemNull());
         }
         pl.inventory.gold = 2000000000;
         pl.inventory.itemsBody.set(5, Manager.CT.get(Util.nextInt(0, Manager.CT.size() - 1)));
         pl.inventory.itemsBody.set(14, Manager.FLAG.get(Util.nextInt(0, Manager.FLAG.size() - 1)));
         // pl.inventory.itemsBody.set(5,
         // ItemService.gI().createNewItem((short)Util.nextInt(0, Manager.CT.length
         // -1)));
         Service.getInstance().sendFlagBag(pl);
         Service.getInstance().Send_Caitrang(pl);
         pl.location.y = 50;
         pets.add(pet);
         bots.add(pl);
         Zone z = MapService.gI().getMapCanJoin(pl, Util.nextInt(150));
         while (z != null && !z.mobs.isEmpty()) {
            z = MapService.gI().getMapCanJoin(pl, Util.nextInt(150));
         }
         pl.zone = MapService.gI().getMapCanJoin(pl, Util.nextInt(150));
         if (pl.zone == null) {
            return;
         }
         if (pl.zone.map == null) {
            return;
         }
         pl.location.x = Util.nextInt(20, pl.zone.map.mapWidth - 20);// temp.location.x + Util.nextInt(-400,400);
         pl.zone.addPlayer(pl);
         pl.zone.load_Me_To_Another(pl);
         pl.zone.loadAnotherToMe(pl);
         Client.gI().put(pl);
      } catch (Exception e) {
      }
   }

   private void remove(Player player) {
      this.players_id.remove(player.id);
      this.players_name.remove(player.name);
      this.players_userId.remove(player.getSession().userId);
      this.players.remove(player);
      dispose(player);
   }

   public void dispose(Player player) {
      if (!player.beforeDispose) {
         if (player.isHoldNamecBall) {
            NamekBallWar.gI().dropBall(player);
         }
         TranhNgoc.gI().removePlayersCadic(player);
         TranhNgoc.gI().removePlayersFide(player);
         player.beforeDispose = true;
         player.mapIdBeforeLogout = player.zone.map.mapId;
         MapService.gI().exitMap(player);
         TransactionService.gI().cancelTrade(player);
         PVPServcice.gI().finishPVP(player, PVP.TYPE_LEAVE_MAP);
         if (player.clan != null) {
            player.clan.removeMemberOnline(null, player);
         }
         if (player.itemTime != null && player.itemTime.isUseTDLT) {
            Item tdlt = InventoryService.gI().findItemBagByTemp(player, 521);
            if (tdlt != null) {
               ItemTimeService.gI().turnOffTDLT(player, tdlt);
            }
         }
         if (SummonDragon.gI().playerSummonShenron != null
               && SummonDragon.gI().playerSummonShenron.id == player.id) {
            SummonDragon.gI().isPlayerDisconnect = true;
         }
         if (player.mobMe != null) {
            player.mobMe.mobMeDie();
         }
         if (player.pet != null) {
            if (player.pet.mobMe != null) {
               player.pet.mobMe.mobMeDie();
            }
            MapService.gI().exitMap(player.pet);
         }
         if (player.minipet != null) {
            MapService.gI().exitMap(player.minipet);
         }
         PlayerService.gI().savePlayer(player);
      }
   }

   public void kickSession(Session session) {
      if (session != null) {
         this.remove(session);
         session.disconnect();
      }
   }

   public Player getPlayer(long playerId) {
      return this.players_id.get(playerId);
   }

   public Player getPlayerByUser(int userId) {
      return this.players_userId.get(userId);
   }

   public Session getSession(Session session) {
      for (Session se : sessions) {
         if (se != session && se.userId == session.userId) {
            return se;
         }
      }
      return null;
   }

   public Player getPlayer(String name) {
      return this.players_name.get(name);
   }

   public Session getSession(int sessionId) {
      return this.sessions_id.get(sessionId);
   }

   public void close() {
      Log.log("Cleaning up sessions ...");
      while (!this.sessions.isEmpty()) {
         this.kickSession(this.sessions.remove(0));
      }
   }

   private void update() {
      for (Session session : sessions) {
         if (session.timeWait > 0) {
            session.timeWait--;
            if (session.timeWait == 0) {
               kickSession(session);
            }
         }
      }
   }

   @Override
   public void run() {
      while (ServerManager.isRunning) {
         try {
            long st = System.currentTimeMillis();
            update();
            long delay = 800 - (System.currentTimeMillis() - st);
            if (delay > 0) {
               Thread.sleep(delay);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public void show(Player player) {
      String txt = "";
      txt += "sessions: " + sessions.size() + "\n";
      txt += "sessions_id: " + sessions_id.size() + "\n";
      txt += "players_id: " + players_id.size() + "\n";
      txt += "players_userId: " + players_userId.size() + "\n";
      txt += "players_name: " + players_name.size() + "\n";
      txt += "players: " + players.size() + "\n";
      Service.getInstance().sendThongBao(player, txt);
   }

}
