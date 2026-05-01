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
import net.datafaker.Faker;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class BotManager {

   private static BotManager instance;
   private static int id = 1000000;
   private static final Faker faker = new Faker();

   public final List<Player> bots = new ArrayList<>();
   public final List<Pet> pets = new ArrayList<>();

   private static final int[] BOT_ALLOWED_MAPS = {
         63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
         79, 80, 81, 82, 83,
   };

   public static BotManager gI() {
      if (instance == null) {
         instance = new BotManager();
      }
      return instance;
   }

   public void createBot() {
      try {
         Player pl = new Player();
         pl.id = id++;
         pl.name = generateRandomName();
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

   public String generateRandomName() {
      String name = faker.name().firstName().toLowerCase() + Util.nextInt(10, 99);
      return removeAccent(name);
   }

   public String removeAccent(String s) {
      String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
      Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
      return pattern.matcher(temp).replaceAll("").replace("đ", "d").replace("Đ", "D");
   }

}
