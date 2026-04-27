package nro.services;

import nro.models.player.Player;
import nro.server.Client;

import nro.utils.Util;

import java.util.ArrayList;

import java.util.List;
import nro.consts.ConstPlayer;

import nro.models.map.Zone;
import nro.models.player.Inventory;
import nro.models.player.Pet;
import nro.models.skill.Skill;
import nro.server.Manager;
import nro.network.io.Message;
import nro.server.io.Session;

import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;

public class BotManager {

   private static BotManager instance;
   private static int id = 1000000;

   public final List<Player> bots = new ArrayList<>();
   public final List<Pet> pets = new ArrayList<>();

   private static final int[] BOT_ALLOWED_MAPS = {
         63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
         79, 80, 81, 82, 83,
   };

   private final String[] TenDau = { "asap", "ashy", "asks", "atom", "aunt", "auto", "avid", "away", "awry", "axis",
         "babe", "baby", "back", "bail", "bake", "bald", "ball",
         "band", "bang", "bank", "byes", "byte", "cabs", "cage", "cake", "calf", "call", "calm", "came", "camp", "cams",
         "cane", "cant", "cape", "caps", "carb", "card",
         "care", "carp", "cars", "cart", "crow", "crud", "cruel", "crux", "cube", "cubs", "cues", "cuff", "cuke",
         "cull", "cult", "cunt", "cure", "curl", "cute", "cuts", "cyan",
         "cyst", "dabs", "dace", "dada", "dads", "daff", "daft", "dais", "dale", "dame", "damn", "damp", "dams", "else",
         "emir", "emit", "ends", "envy", "epic", "eras",
         "ergo", "erst", "espy", "etch", "even", "ever", "evil", "exam", "exec", "exes", "exit", "expo", "eyed", "eyes",
         "face", "fact", "fade", "fads", "fags", "fail",
         "fair", "fake", "fall", "fame", "fang", "fans", "fare", "farm", "fast", "fate", "faux", "fawn", "faze", "gain",
         "gala", "gale", "gall", "game",
         "gamy", "gang", "gape", "gaps", "gash", "gasp", "gate", "gaud", "gave", "gawk", "gays", "gear", "geld", "gems",
         "gene", "gent", "germ", "gets", "ghee", "gibe",
         "gibs", "gift", "gigs", "gild", "gill", "gilt", "gimp", "gins", "girl", "gist", "give", "glad", "glee", "glen",
         "glow" };

   private final String[] TenSau = { "buns", "bunt", "buoy", "bush", "buss", "busy", "buts", "butt", "buys", "buzz",
         "byes", "byte", "cabs", "cage", "cake", "calf", "call",
         "calm", "came", "camp", "cams", "cane", "cant", "cape", "caps", "carb", "card", "care", "carp", "cars", "cart",
         "case", "cash", "cask", "cast", "cats", "cave",
         "cede", "cell", "cent", "cere", "cert", "cess", "chat", "chef", "chew", "chic", "chin", "chip", "chit", "chop",
         "chow", "chub", "chug", "cine", "cite", "city",
         "clad", "clam", "clap", "claw", "clay", "clef", "clew", "clip", "clod", "clog", "clot", "club", "clue", "coal",
         "coat", "coax", "cock", "coco", "code", "coed",
         "coil", "coin", "coke", "cola", "cold", "colt", "coma", "comb", "come", "comp", "cone", "conk", "cool", "coop",
         "cope", "copy", "cord", "core", "cork", "corn",
         "cost", "cosy", "cote", "cots", "cove", "cowl", "cows", "crab", "crag", "crap", "crew", "crib", "crop", "dome",
         "done", "doom", "door", "dope", "dork", "dorm",
         "dose", "dote", "dots", "dove", "down", "doze", "drag", "dram", "drat", "draw", "drew", "drip", "drop", "drug",
         "drum", "dual", "dubs", "duck", "duct", "dude",
         "duds", "dues", "duet", "duke", "dull", "duly", "dumb", "dump", "dune", "dunk", "dusk", "dust", "duty", "dyed",
         "dyer", "dyes", "each", "earl", "earn", "ears",
         "ease", "east", "easy", "eats", "echo", "edge", "edit", "eggs", "egos", "eire", "eject", "elan", "elms",
         "else", "emir", "emit", "ends", "envy", "epic", "eras",
         "ergo", "erst", "espy", "etch", "even", "ever", "evil", "exam", "exec", "exes", "exit", "expo", "eyed", "eyes",
         "face", "fact", "fade", "fads", "fags", "fail",
         "fair", "fake", "fall", "fame", "fang", "fans", "fare", "farm", "fast", "fate", "faux", "fawn", "faze", "fear",
         "feat", "feed", "feel", "fees", "feet", "fell",
         "felt", "fend", "fern", "feta", "glue", "glum", "gnat", "gnaw" };

   public static BotManager gI() {
      if (instance == null) {
         instance = new BotManager();
      }
      return instance;
   }

   public void createBot() {
      try {
         String[] name1 = TenDau;
         String[] name2 = TenSau;

         Player pl = new Player();
         pl.id = id++;
         pl.name = name1[Util.nextInt(name1.length)] + name2[Util.nextInt(name2.length)];
         pl.gender = (byte) Util.nextInt(0, 2);
         pl.isBot = true;

         pl.nPoint.power = Util.nextLong(5_000_000_000L, 60_000_000_000L);
         pl.nPoint.hpg = Util.nextInt(1_000_000, 30_000_000);
         pl.nPoint.hpMax = Util.nextInt(1_000_000, 30_000_000);
         pl.nPoint.hp = pl.nPoint.hpMax / 2;
         pl.nPoint.mpMax = Util.nextInt(1_000_000, 30_000_000);
         pl.nPoint.dame = Util.nextInt(100_000, 1_000_000);
         pl.nPoint.stamina = 32000;

         PetService.gI().createPetIsBot(pl, (byte) 0);
         pl.pet.changeStatus(Pet.ATTACK);

         pl.lastTimeMap = System.currentTimeMillis();
         pl.typePk = ConstPlayer.NON_PK;

         // Kỹ năng
         int[] skillsArr = pl.gender == 0 ? new int[] { 0, 1, 6, 20, 22 }
               : pl.gender == 1 ? new int[] { 17, 3, 7, 2, 18, 12 }
                     : new int[] { 4, 5, 8 };
         for (int skillId : skillsArr) {
            Skill skill = SkillUtil.createSkill(skillId, Util.nextInt(2, 7));
            pl.playerSkill.skills.add(skill);
         }

         // Đồ
         pl.inventory = new Inventory(pl);
         for (int i = 0; i < 15; i++) {
            pl.inventory.itemsBody.add(ItemService.gI().createItemNull());
         }
         pl.inventory.gold = 2_000_000_000;
         safeSetCostume(pl);
         pl.inventory.itemsBody.set(14, Manager.FLAG.get(Util.nextInt(Manager.FLAG.size())));

         // Map & zone
         Zone z = getSafeStartZone(pl);
         if (z == null) {
            return;
         }

         pl.zone = z;
         pl.location.x = Util.nextInt(50, z.map.mapWidth - 50);
         pl.location.y = z.map.mapHeight - 20;

         pl.autoDoiKhu = true;
         ChangeMapService.gI().startAutoZoneChange(pl);

         Session fakeSession = new Session();
         fakeSession.player = pl;
         fakeSession.userId = (int) pl.id;
         fakeSession.actived = true;
         fakeSession.joinedGame = true;

         pl.setSession(fakeSession);
         Client.gI().put(fakeSession);
         Client.gI().put(pl);

         z.addPlayer(pl);
         z.load_Me_To_Another(pl);
         z.loadAnotherToMe(pl);
         randomFusionForBot(pl);

         ItemTimeService.gI().sendCanAutoPlay(pl);
         bots.add(pl);
         pets.add(pl.pet);

         Log.log("Bot " + pl.name + " [" + pl.id + "] đã tạo!");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void safeSetCostume(Player pl) {
      for (int i = 0; i < 5; i++) {
         try {
            if (!Manager.CT.isEmpty()) {
               pl.inventory.itemsBody.set(5, Manager.CT.get(Util.nextInt(Manager.CT.size())));
               return;
            }
         } catch (Exception ignored) {
         }
      }
      pl.inventory.itemsBody.set(5, ItemService.gI().createItemNull());
   }

   private Zone getSafeStartZone(Player pl) {
      for (int i = 0; i < 15; i++) {
         int mapId = BOT_ALLOWED_MAPS[Util.nextInt(BOT_ALLOWED_MAPS.length)];
         Zone z = MapService.gI().getZoneForBot(pl, mapId);
         if (z != null && z.map != null && !z.isFullPlayer()) {
            return z;
         }
      }
      return null;
   }

   private void randomFusionForBot(Player pl) {
      if (!Util.isTrue(25, 100))
         return;

      int[] fusionTypes = {
            ConstPlayer.LUONG_LONG_NHAT_THE,
            ConstPlayer.HOP_THE_PORATA,
            ConstPlayer.HOP_THE_PORATA2,
            ConstPlayer.HOP_THE_PORATA3,
            ConstPlayer.HOP_THE_PORATA4
      };
      int fusionType = fusionTypes[Util.nextInt(fusionTypes.length)];
      pl.fusion.typeFusion = (byte) fusionType;
      pl.fusion.lastTimeFusion = System.currentTimeMillis();

      if (pl.pet != null) {
         pl.pet.changeStatus(Pet.GOHOME);
      }

      try {
         Message msg = Message.create(125);
         msg.writer().writeByte(fusionType);
         msg.writer().writeInt((int) pl.id);
         Service.getInstance().sendMessAllPlayerInMap(pl, msg);
         msg.cleanup();
      } catch (Exception ignored) {
      }

      pl.nPoint.setDirty();
      pl.nPoint.calPoint();
      pl.nPoint.setFullHpMp();
      Service.getInstance().point(pl);
      Service.getInstance().Send_Caitrang(pl);
   }
}
