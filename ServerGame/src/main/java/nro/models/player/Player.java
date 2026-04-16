package nro.models.player;

import nro.card.Card;
import nro.card.CollectionBook;
import nro.consts.ConstPlayer;
import nro.data.DataGame;
import nro.dialog.ConfirmDialog;
import nro.models.clan.Buff;
import nro.models.item.CaiTrang;
import nro.models.item.FlagBag;
import nro.models.boss.event.EscortedBoss;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.intrinsic.IntrinsicPlayer;
import nro.models.item.Item;
import nro.models.item.ItemTime;
import nro.models.map.ItemMap;
import nro.models.map.TrapMap;
import nro.models.map.Zone;
import nro.models.map.war.BlackBallWar;
import nro.models.map.mabu.MabuWar;
import nro.models.map.war.NamekBallWar;
import nro.models.mob.MobMe;
import nro.models.npc.specialnpc.MabuEgg;
import nro.models.npc.specialnpc.MagicTree;
import nro.models.pvp.PVP;
import nro.models.skill.PlayerSkill;
import nro.models.task.TaskPlayer;
import nro.server.Client;
import nro.server.Manager;
import nro.server.io.Message;
import nro.server.io.Session;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.services.func.CombineNew;
import nro.services.func.PVPServcice;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import nro.models.item.ItemOption;
import nro.models.item.ItemTimeSieuCap;
import nro.models.map.mabu.MabuWar14h;
import nro.models.mob.Mob;
import nro.models.phuban.DragonNamecWar.TranhNgoc;
import nro.models.phuban.DragonNamecWar.TranhNgocService;
import nro.models.skill.Skill;

public class Player {

   public int bongtai;
   public int thiensu;

   public long lastTimeUpdateBallWar;
   public long lastTimeNotifyTimeHoldBlackBall;
   public long lastTimeHoldBlackBall;
   public int tempIdBlackBallHold = -1;
   public boolean isHoldBlackBall;
   public boolean isHoldNamecBall;
   public boolean isHoldNamecBallTranhDoat;
   public int tempIdNamecBallHoldTranhDoat = -1;

   public long timeoff = 0;
   public boolean isdem = false;
   public byte typetrain;
   public int expoff;
   public boolean istrain;

   public int sochon;
   public List<Integer> soMayMan = new ArrayList<>();
   public int rankSieuHang;
   public long timesieuhang;
   public boolean isnhanthuong1;

   public boolean DH1 = false;
   public boolean DH2 = false;
   public boolean DH3 = false;
   public boolean DH4 = false;
   public boolean DH5 = false;

   public boolean autoDoiKhu;

   public boolean isTitleUse1;
   public long lastTimeTitle1;
   public int IdDanhHieu_1;
   public boolean isTitleUse2;
   public long lastTimeTitle2;
   public int IdDanhHieu_2;
   public boolean isTitleUse3;
   public long lastTimeTitle3;
   public int IdDanhHieu_3;
   public boolean isTitleUse4;
   public long lastTimeTitle4;
   public int IdDanhHieu_4;
   public boolean isTitleUse5;
   public long lastTimeTitle5;
   public int IdDanhHieu_5;

   public int bokhidan, tangnguyendan, bohuyetdan;

   public boolean resetdame = false;
   public boolean hitdau = false;
   public long lastTimeDame;
   public long dametong = 0;
   public int chuyensinh;
   public int MaxGoldTradeDay;
   public int chuaco2;
   public int chuaco3;
   public int chuaco4;

   public int mot;
   public int hai;
   public int ba;
   public int bon;
   public int nam;

   public int sau;
   public int bay;
   public int tam;
   public int chin;
   public int muoi;

   public int muoiMot;
   public int muoiHai;
   public int muoiBa;
   public int muoiBon;
   public int muoiLam;

   public int killboss;

   public int evenpoint;

   public int even2thang9;
   public int evenTrungThu;

   public int diemdanh;
   public int chuyencan;
   public int checkquachuyencan;
   public int hoivienvip;
   public int tongnap;
   public int naplandau;
   public int tichluynap;
   public long lastTimeUseOption;
   public int goldTai;
   public int goldXiu;

   public long vangnhat = 0;
   public long hngocnhat = 0;
   public String Hppl = "\n";

   public int server;
   public byte[] buyLimit;

   public PlayerEvent event;
   public List<String> textRuongGo = new ArrayList<>();
   public boolean receivedWoodChest;
   public int goldChallenge;
   public int levelWoodChest;
   public boolean isInvisible;
   public boolean sendMenuGotoNextFloorMabuWar;
   public long lastTimeBabiday;
   public long lastTimeChangeZone;
   public long lastTimeChatGlobal;
   public long lastTimeChatPrivate;
   public long lastTimeChangeMap;
   public Date firstTimeLogin;
   private Session session;
   public byte countSaveFail;
   public boolean beforeDispose;

   public long timeFixInventory;
   public boolean isPet;
   public boolean isBoss;
   public boolean isMiniPet;
   public boolean isBot = false;
   public long lastTimeFindMob = System.currentTimeMillis();
   public long lastTimeBotAI = 0;

   public int playerTradeId = -1;
   public Player playerTrade;

   public int mapIdBeforeLogout;
   public List<Zone> mapBlackBall;
   public Zone zone;
   public Zone mapBeforeCapsule;
   public List<Zone> mapCapsule;
   public Pet pet;
   public MiniPet minipet;

   public MobMe mobMe;
   public Location location;
   public SetClothes setClothes;
   public EffectSkill effectSkill;
   public MabuEgg mabuEgg;
   public TaskPlayer playerTask;
   public ItemTime itemTime;
   public ItemTimeSieuCap itemTimesieucap;
   public Fusion fusion;
   public MagicTree magicTree;
   public IntrinsicPlayer playerIntrinsic;
   public Inventory inventory;
   public PlayerSkill playerSkill;
   public CombineNew combineNew;
   public IDMark iDMark;
   public Charms charms;
   public EffectSkin effectSkin;
   public Gift gift;
   public NPoint nPoint;
   public RewardBlackBall rewardBlackBall;
   public EffectFlagBag effectFlagBag;

   public Clan clan;
   public ClanMember clanMember;

   public ListFriendEnemy<Friend> friends;
   public ListFriendEnemy<Enemy> enemies;

   protected boolean actived = false;
   public boolean loaded;

   public long id;
   public String name;
   public byte gender;
   public boolean isNewMember = true;
   public short head;

   public byte typePk;

   public long lastTimePickTranhNgoc;
   public int tempIdPickTranhNgoc = -1;
   public boolean isPickTranhNgoc;

   public byte cFlag;
   public long lastTimeChangeFlag;
   public long lastTimeTrade;

   public boolean haveTennisSpaceShip;
   private byte useSpaceShip;

   public boolean isGoHome;

   public boolean justRevived;
   public long lastTimeRevived;
   public boolean immortal;

   public long lastTimeBan;
   public long lastTimeUpdate;
   public boolean isBan;

   public SkillSpecial skillSpecial;
   public boolean isGotoFuture;
   public long lastTimeGoToFuture;
   public boolean isgotoPrimaryForest;
   public long lastTimePrimaryForest;

   public boolean isgotoHanhTinhBang;
   public long lastTimeHanhTinhBang;

   public boolean isgotoDiaNguc;
   public long lastTimeDiaNguc;

   public boolean isgotoHallowen;
   public long lastTimeHallowen;

   public boolean isGoToBDKB;
   public long lastTimeGoToBDKB;
   public long lastTimeAnXienTrapBDKB;
   private short powerPoint;
   private short percentPowerPont;
   public boolean PorataVIP;

   public long lastTimePickItem;
   @Setter
   @Getter
   private CollectionBook collectionBook;
   @Getter
   @Setter
   private boolean isSaving, isDisposed;
   @Getter
   @Setter
   private boolean interactWithKarin;
   @Getter
   @Setter
   private EscortedBoss escortedBoss;
   @Setter
   @Getter
   private ConfirmDialog confirmDialog;
   @Getter
   @Setter
   public byte[] rewardLimit;
   @Setter
   @Getter
   private PetFollow petFollow;
   @Setter
   @Getter
   private Buff buff;

   public int idPhucLoi;
   public List<Integer> listNhan = new ArrayList<>();
   public int[] checkNhan;
   public int phutOnline;
   public long lastTimeOnline = System.currentTimeMillis();
   public List<Integer> listOnline = new ArrayList<>();
   public List<Integer> listDiemDanh = new ArrayList<>();

   public Date weekTimeLogin;

   public RuongSuuTamPlayer ruongSuuTam;
   public byte typeMoRuong;
   public byte active_ruong_suu_tam;

   public List<KhamNgocPlayer> khamNgoc = new ArrayList<>();
   public byte active_kham_ngoc;
   public byte nroKhamNgoc;
   public int idTempNangCap;
   public int slItem;

   public int idTamBao;
   public List<Integer> listNhan_TamBao = new ArrayList<>();
   public int[] checkNhan_TamBao;
   public int diem_quay;
   public int[] list_id_nhan = new int[14];

   public List<PhongThiNghiem_Player> phongThiNghiem = new ArrayList<>();
   public int typeBinhDieuChe;
   public int vitriBinhDieuChe;

   public Player() {
      ruongSuuTam = new RuongSuuTamPlayer();
      lastTimeUseOption = System.currentTimeMillis();
      location = new Location();
      nPoint = new NPoint(this);
      inventory = new Inventory(this);
      playerSkill = new PlayerSkill(this);
      setClothes = new SetClothes(this);
      effectSkill = new EffectSkill(this);
      fusion = new Fusion(this);
      playerIntrinsic = new IntrinsicPlayer();
      rewardBlackBall = new RewardBlackBall(this);
      effectFlagBag = new EffectFlagBag();
      // ----------------------------------------------------------------------
      iDMark = new IDMark();
      combineNew = new CombineNew();
      playerTask = new TaskPlayer();
      friends = new ListFriendEnemy<>();
      enemies = new ListFriendEnemy<>();
      itemTime = new ItemTime(this);
      itemTimesieucap = new ItemTimeSieuCap(this);
      charms = new Charms();
      gift = new Gift();
      effectSkin = new EffectSkin(this);
      skillSpecial = new SkillSpecial(this);
      event = new PlayerEvent(this);
      buyLimit = new byte[13];
      buff = Buff.NONE;
   }

   // --------------------------------------------------------------------------
   public short getPowerPoint() {
      return powerPoint;
   }

   public void addPowerPoint(int value) {
      powerPoint += value;
   }

   public short getPercentPowerPont() {
      return percentPowerPont;
   }

   public void addPercentPowerPoint(int value) {
      percentPowerPont += value;
   }

   public void resetPowerPoint() {
      percentPowerPont = 0;
      powerPoint = 0;
   }

   public void setUseSpaceShip(byte useSpaceShip) {
      // 0 - không dùng
      // 1 - tàu vũ trụ theo hành tinh
      // 2 - dịch chuyển tức thời
      // 3 - tàu tenis
      this.useSpaceShip = useSpaceShip;
   }

   public Item getBodyItemByType(int type) {
      for (Item item : this.inventory.itemsBody) {
         if (item != null && item.getType() == type) {
            return item;
         }
      }
      return null;
   }

   public byte getUseSpaceShip() {
      return this.useSpaceShip;
   }

   public boolean isDie() {
      if (this.nPoint != null) {
         return this.nPoint.hp < 1;
      } else {
         return true;
      }
   }

   public boolean isAlive() {
      if (this.nPoint != null) {
         return this.nPoint.hp > 1;
      } else {
         return true;
      }
   }

   public boolean checkdanhhieutop1() {
      for (Item item : this.inventory.itemsBody) {
         if (item != null && item.template != null && item.template.id == 1900) {
            return true;
         }
      }
      return false;
   }

   public boolean checkdanhhieutop2() {
      for (Item item : this.inventory.itemsBody) {
         if (item != null && item.template != null && item.template.id == 1901) {
            return true;
         }
      }
      return false;
   }

   public boolean checkdanhhieutop3() {
      for (Item item : this.inventory.itemsBody) {
         if (item != null && item.template != null && item.template.id == 1902) {
            return true;
         }
      }
      return false;
   }

   public boolean check99ThucAnHuyDiet() {
      for (Item item : this.inventory.itemsBag) {
         if (item != null && item.template != null && item.template.id >= 663 && item.template.id <= 667
               && item.quantity >= 99) {
            return true;
         }
      }
      return false;
   }

   // --------------------------------------------------------------------------
   public void setSession(Session session) {
      this.session = session;
   }

   public void sendMessage(Message msg) {
      if (this.session != null) {
         session.sendMessage(msg);
      }
   }

   public Session getSession() {
      return this.session;
   }

   public int version() {
      return session.version;
   }

   public boolean isVersionAbove(int version) {
      return version() >= version;
   }

   public void update() {
      // final Calendar rightNow = Calendar.getInstance();
      // int hour = rightNow.get(11);
      if (this.isBot) {
         // chỉ lo AI cấp cao
         BotAIService.updateBot(this);
         active();
         return; // ⛔ QUAN TRỌNG: KHÔNG CHẠY CODE PLAYER
      }

      if (this != null && this.name != null && !this.beforeDispose && !isBot) {
         try {
            if (this.istrain && !MapService.gI().isMapTrainOff(this, this.zone.map.mapId)) {
               ChangeMapService.gI().changeMapBySpaceShip(this, MapService.gI().getMapTrainOff(this), -1, 250);
               congExpOff();
               this.timeoff = 0;
            }
            // if (!isdem && (hour >= 18 || hour < 5)) {
            // SummonDragon.gI().activeNight(this);
            // isdem = true;
            // Service.getInstance().sendThongBao(this, "Chúc bạn buổi tối vui vẻ");
            // } else if (isdem && (hour >= 5 && hour < 18)) {
            // SummonDragon.gI().activeDay(this);
            // isdem = false;
            // Service.getInstance().sendThongBao(this, "Chúc bạn buổi sáng tốt lành");
            // }
            if (!isBan) {
               // SendEffect.getInstance().update(this);
               if (nPoint != null) {
                  nPoint.update();
               }
               if (fusion != null) {
                  fusion.update();
               }
               if (effectSkin != null) {
                  effectSkill.update();
               }
               if (mobMe != null) {
                  mobMe.update();
               }
               if (effectSkin != null) {
                  effectSkin.update();
               }
               if (pet != null) {
                  pet.update();
               }
               if (minipet != null) {
                  minipet.update();
               }
               if (magicTree != null) {
                  magicTree.update();
               }
               if (itemTime != null) {
                  itemTime.update();
               }
               if (itemTimesieucap != null) {
                  itemTimesieucap.update();
               }
               if (event != null) {
                  event.update();
               }
               if (this.isPl()) {
                  MabuWar.gI().update(this);
                  MabuWar14h.gI().update(this);
                  TranhNgoc.gI().update(this);
               }
               if (!this.isBoss && !this.isMiniPet) {
                  if (pet != null && this.inventory.itemsBody.get(5).isNotNullItem()
                        && this.pet.inventory.itemsBody.get(5).isNotNullItem()) {
                     if ((this.inventory.itemsBody.get(5).template.id == 1319
                           && this.pet.inventory.itemsBody.get(5).template.id == 619)) {
                        this.PorataVIP = true;
                     } else {
                        this.PorataVIP = false;
                     }
                  } else {
                     this.PorataVIP = false;
                  }
               }
               BlackBallWar.gI().update(this);
               if (!this.isBoss && !this.isPet && !this.isMiniPet) {
                  if (this.server != Manager.SERVER) {
                     PlayerService.gI().banPlayer(this);
                  }
                  // checkLocation();
                  // if (Util.canDoWithTime(lastTimeUpdate, 60000)) {//ONL ĐỦ 1 TIẾNG
                  //// this.playerTask.achivements.get(ConstAchive.HOAT_DONG_CHAM_CHI).count++;
                  // }
                  if (Util.canDoWithTime(lastTimeOnline, 60000)) {
                     this.phutOnline++;
                     this.lastTimeOnline = System.currentTimeMillis();
                  }
               }
               if (isGotoFuture && Util.canDoWithTime(lastTimeGoToFuture, 3000)) {
                  ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                  this.isGotoFuture = false;
               }
               if (isGoToBDKB && Util.canDoWithTime(lastTimeGoToBDKB, 3000)) {
                  ChangeMapService.gI().changeMapBySpaceShip(this, 135, -1, 35);
                  this.isGoToBDKB = false;
               }
               if (isgotoPrimaryForest && Util.canDoWithTime(lastTimePrimaryForest, 3000)) {
                  ChangeMapService.gI().changeMap(this, 161, -1, 169, 312);
                  this.isgotoPrimaryForest = false;
               }
               if (isgotoHanhTinhBang && Util.canDoWithTime(lastTimeHanhTinhBang, 3000)) {
                  ChangeMapService.gI().changeMap(this, 211, -1, 290, 768);
                  this.isgotoHanhTinhBang = false;
               }
               if (isgotoDiaNguc && Util.canDoWithTime(lastTimeDiaNguc, 3000)) {
                  ChangeMapService.gI().changeMap(this, 212, -1, 110, 480);
                  this.isgotoDiaNguc = false;
               }
               if (isgotoHallowen && Util.canDoWithTime(lastTimeHallowen, 3000)) {
                  ChangeMapService.gI().changeMap(this, 210, -1, 258, 192);
                  this.isgotoHallowen = false;
               }
               if (this.zone != null) {
                  TrapMap trap = this.zone.isInTrap(this);
                  if (trap != null) {
                     trap.doPlayer(this);
                  }
               }
               if (Util.canDoWithTime(this.lastTimeDame, 5000) && this.dametong != 0 && this.hitdau == true) {
                  Service.getInstance().sendThongBao(this,
                        "\n|1|DAME/5giây: \b|7|" + Util.powerToStringnew(this.dametong));
                  this.dametong = 0;
                  this.resetdame = true;
                  this.hitdau = false;
               }
            } else {
               if (Util.canDoWithTime(lastTimeBan, 5000)) {
                  Client.gI().kickSession(session);
               }
            }
         } catch (Exception e) {
            Log.error(Player.class, e, "Lỗi tại player: " + this.name);
         }
      }
   }

   // private void checkLocation() {
   // if (this.location.x > this.zone.map.mapWidth || this.location.x < 0
   // || this.location.y > this.zone.map.mapHeight || this.location.y < 0) {
   // if (this.inventory.gold >= 500000000) {
   // this.inventory.subGold(500000000);
   // } else {
   // this.inventory.gold = 0;
   // }
   // PlayerService.gI().sendInfoHpMpMoney(this);
   // ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400,
   // 336);
   // Service.getInstance().sendBigMessage(this, 1139, "|1|Do phát hiện có hành vi
   // bất thường nên\n "
   // + "chúng tôi đã đưa bạn về nhà và xử phạt 500Tr vàng\n"
   // + "|7|nếu còn tiếp tục tái phạm sẽ khóa vĩnh viễn");
   // }
   // }

   public boolean isPl() {
      return !isPet && !isBoss && !isMiniPet;
   }

   // --------------------------------------------------------------------------
   /*
    * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất
    * {383, 384, 385}: ht porata xayda trái đất
    * {391, 392, 393}: ht namếc
    * {870, 871, 872}: ht c2 trái đất
    * {873, 874, 875}: ht c2 namếc
    * {867, 878, 869}: ht c2 xayda
    */
   private static final short[][] idOutfitFusion = {
         // {873, 874, 875},{380, 381, 382}, {383, 384, 385}, {391, 392, 393},// btc1
         { 1210, 1211, 1212 }, { 566, 567, 568 }, { 873, 874, 875 }, // btc2
         { 1375, 1376, 1377 }, { 1372, 1373, 1374 }, { 1369, 1370, 1371 }, // btc2
         // {1255, 1256, 1257}, {1249, 1250, 1251}, {1089, 1090, 1091},//btc3 - đúng
         { 1602, 1595, 1596 }, { 1597, 1600, 1601 }, { 1592, 1595, 1596 },
         { 1252, 1253, 1254 }, { 1360, 1361, 1362 }, { 1246, 1247, 1248 }, // btc4 -
         { 1795, 1796, 1797 }, { 1822, 1823, 1824 }, { 1758, 1761, 1762 }, // btc5//btc5
         { 1288, 1289, 1290 }, { 1804, 1805, 1806 }, { 1092, 1093, 1094 },
   };

   public byte getAura() {
      // if (this.itemTimesieucap.isChoido) {
      // return 13;
      // }
      Item item = this.inventory.itemsBody.get(16);
      if (!item.isNotNullItem()) {
         return -1;
      }
      switch (item.template.id) {
         case 1798:
            return 11;
         case 1799:
            return 12;
         case 1800:
            return 22;
         case 1801:
            return 54;
      }
      Item caitrang = this.inventory.itemsBody.get(5);
      if (!caitrang.isNotNullItem()) {
         return -1;
      }
      switch (caitrang.template.id) {
         case 994:
            return 54;
      }
      if (Manager.TOP_PLAYERS.contains(this.name)) {
         return 1;
      }
      CollectionBook book = getCollectionBook();
      if (book != null) {
         Card card = book.getCards().stream().filter(t -> t.isUse() && t.getCardTemplate().getAura() != -1).findAny()
               .orElse(null);
         if (card != null) {
            return (byte) card.getCardTemplate().getAura();
         }
      }
      return -1;
   }

   public boolean checkSkinFusion() {
      if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
         Short idct = inventory.itemsBody.get(5).template.id;
         if (idct == 601 || idct == 603 || idct == 602) {
            return true;
         }
      }
      return false;
   }

   public short getHead() {
      if (effectSkill != null && effectSkill.isMonkey) {
         return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
      } else if (effectSkill != null && effectSkill.isSocola) {
         return 412;
      } else if (effectSkill != null && effectSkill.isBinh) {
         return 1321;
      } else if (effectSkin != null && effectSkin.isHoaDa) {
         return 454;
      }

      // Fusion
      if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
         if (checkSkinFusion()) {
            Item ctItem = inventory != null ? inventory.itemsBody.get(5) : null;
            if (ctItem != null && ctItem.template != null) {
               CaiTrang ct = Manager.getCaiTrangByItemId(ctItem.template.id);
               if (ct != null && ct.getID()[0] != -1) {
                  return (short) ct.getID()[0];
               }
               return ctItem.template.part;
            }
            return this.head;
         }

         if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE
               || fusion.typeFusion == ConstPlayer.HOP_THE_PORATA && !PorataVIP) {
            return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2 && !PorataVIP) {
            return idOutfitFusion[3 + this.gender][0];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3 && !PorataVIP) {
            return idOutfitFusion[6 + this.gender][0];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4 && !PorataVIP) {
            return idOutfitFusion[9 + this.gender][0];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5 && !PorataVIP) {
            return idOutfitFusion[12 + this.gender][0];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6 && !PorataVIP) {
            return idOutfitFusion[15 + this.gender][0];
         } else if (PorataVIP) {
            return 1853;
         }
      }

      // Costume
      Item ctItem = inventory != null ? inventory.itemsBody.get(5) : null;
      if (ctItem != null && ctItem.template != null) {
         CaiTrang ct = Manager.getCaiTrangByItemId(ctItem.template.id);
         if (!checkSkinFusion() && ct != null && ct.getID()[0] != -1) {
            return (short) ct.getID()[0];
         }
      }

      return this.head;
   }

   public short getBody() {
      if (effectSkill != null && effectSkill.isMonkey) {
         return 193;
      } else if (effectSkill != null && effectSkill.isSocola) {
         return 413;
      } else if (effectSkill != null && effectSkill.isBinh) {
         return 1322;
      } else if (effectSkin != null && effectSkin.isHoaDa) {
         return 455;
      }

      // Fusion
      if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
         if (checkSkinFusion()) {
            Item ctItem = inventory != null ? inventory.itemsBody.get(5) : null;
            if (ctItem != null && ctItem.template != null) {
               CaiTrang ct = Manager.getCaiTrangByItemId(ctItem.template.id);
               if (ct != null && ct.getID()[1] != -1) {
                  return (short) ct.getID()[1];
               }
               return ctItem.template.part;
            }
            return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
         }

         if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE
               || fusion.typeFusion == ConstPlayer.HOP_THE_PORATA && !PorataVIP) {
            return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2 && !PorataVIP) {
            return idOutfitFusion[3 + this.gender][1];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3 && !PorataVIP) {
            return idOutfitFusion[6 + this.gender][1];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4 && !PorataVIP) {
            return idOutfitFusion[9 + this.gender][1];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5 && !PorataVIP) {
            return idOutfitFusion[12 + this.gender][1];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6 && !PorataVIP) {
            return idOutfitFusion[15 + this.gender][1];
         } else if (PorataVIP) {
            return 1854;
         }
      }

      // Costume
      Item ctItem = inventory != null ? inventory.itemsBody.get(5) : null;
      if (ctItem != null && ctItem.template != null && !checkSkinFusion()) {
         CaiTrang ct = Manager.getCaiTrangByItemId(ctItem.template.id);
         if (ct != null && ct.getID()[1] != -1) {
            return (short) ct.getID()[1];
         }
      }

      // Normal armor slot 0
      Item body = inventory != null ? inventory.itemsBody.get(0) : null;
      if (body != null && body.template != null) {
         return body.template.part;
      }

      return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
   }

   public short getLeg() {
      if (effectSkill != null && effectSkill.isMonkey) {
         return 194;
      } else if (effectSkill != null && effectSkill.isSocola) {
         return 414;
      } else if (effectSkill != null && effectSkill.isBinh) {
         return 1323;
      } else if (effectSkin != null && effectSkin.isHoaDa) {
         return 456;
      }

      // Fusion
      if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
         if (checkSkinFusion()) {
            Item ctItem = inventory != null ? inventory.itemsBody.get(5) : null;
            if (ctItem != null && ctItem.template != null) {
               CaiTrang ct = Manager.getCaiTrangByItemId(ctItem.template.id);
               if (ct != null && ct.getID()[2] != -1) {
                  return (short) ct.getID()[2];
               }
               return ctItem.template.part;
            }
            return (short) (gender == ConstPlayer.NAMEC ? 60 : 58);
         }

         if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE
               || fusion.typeFusion == ConstPlayer.HOP_THE_PORATA && !PorataVIP) {
            return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2 && !PorataVIP) {
            return idOutfitFusion[3 + this.gender][2];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3 && !PorataVIP) {
            return idOutfitFusion[6 + this.gender][2];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4 && !PorataVIP) {
            return idOutfitFusion[9 + this.gender][2];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5 && !PorataVIP) {
            return idOutfitFusion[12 + this.gender][2];
         } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6 && !PorataVIP) {
            return idOutfitFusion[15 + this.gender][2];
         } else if (PorataVIP) {
            return 1855;
         }
      }

      // Costume
      Item ctItem = inventory != null ? inventory.itemsBody.get(5) : null;
      if (ctItem != null && ctItem.template != null && !checkSkinFusion()) {
         CaiTrang ct = Manager.getCaiTrangByItemId(ctItem.template.id);
         if (ct != null && ct.getID()[2] != -1) {
            return (short) ct.getID()[2];
         }
      }

      // Normal leg slot 1
      Item leg = inventory != null ? inventory.itemsBody.get(1) : null;
      if (leg != null && leg.template != null) {
         return leg.template.part;
      }

      return (short) (gender == ConstPlayer.NAMEC ? 60 : 58);
   }

   public short getFlagBag() {
      if (this.isHoldBlackBall) {
         return 31;
      } else if (this.isHoldNamecBall) {
         return 30;
      }
      if (this.inventory.itemsBody.size() > 10) {
         if (this.inventory.itemsBody.get(14).isNotNullItem()) {
            FlagBag f = FlagBagService.gI().getFlagBagByName(this.inventory.itemsBody.get(14).template.name);
            if (f != null) {
               return (short) f.id;
            }
         }
      }
      if (this.isPet) {
         if (this.inventory.itemsBody.get(7).isNotNullItem()) {
            FlagBag ff = FlagBagService.gI().getFlagBagByName(this.inventory.itemsBody.get(7).template.name);
            if (ff != null) {
               return (short) ff.id;
            }
         }
      }
      // if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
      // return 28;
      // }
      if (this.clan != null) {
         return (short) this.clan.imgId;
      }
      return -1;
   }

   public short getMount() {
      if (!this.isBot && this.isVersionAbove(220)) {
         for (Item item : inventory.itemsBody) {
            if (item.isNotNullItem()) {
               if (item.template.type == 24) {
                  if (item.template.gender == 3 || item.template.gender == this.gender) {
                     return item.template.id;
                  } else {
                     return -1;
                  }
               }
               if (item.template.type == 23) {
                  if (item.template.id < 500) {
                     return item.template.id;
                  } else {
                     Object mount = DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
                     if (mount == null) {
                        return -1;
                     }
                     return (short) mount;
                  }
               }
            }
         }
      } else {
         for (Item item : inventory.itemsBag) {
            if (item.isNotNullItem()) {
               if (item.template.type == 24) {
                  if (item.template.gender == 3 || item.template.gender == this.gender) {
                     return item.template.id;
                  } else {
                     return -1;
                  }
               }
               if (item.template.type == 23) {
                  if (item.template.id < 500) {
                     return item.template.id;
                  } else {
                     Object mount = DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
                     if (mount == null) {
                        return -1;
                     }
                     return (short) mount;
                  }
               }
            }
         }
      }
      return -1;
   }

   public Mob mobTarget;

   public long lastTimeTargetMob;

   public long timeTargetMob;

   public long lastTimeAttack;
   public long lastTimeMap;
   public Player isBotAttckBoss;
   public PVP pvp;
   private long lastTimeTargetPlayer;
   private int timeTargetPlayer;

   public void moveTo(int targetX, int targetY) {
      if (this.zone == null || this.location == null)
         return;

      int curX = this.location.x;
      int curY = this.location.y;

      int dx = targetX - curX;
      int dy = targetY - curY;

      int distance = (int) Math.sqrt(dx * dx + dy * dy);

      // ✅ Nếu đã đủ gần thì không cần move nữa
      if (distance < 20)
         return;

      // ===== TÍNH BƯỚC DI CHUYỂN MƯỢT =====
      int step;

      if (distance > 300) {
         step = 120; // xa → đi nhanh
      } else if (distance > 150) {
         step = 80;
      } else if (distance > 60) {
         step = 45;
      } else {
         step = 25; // gần → đi chậm
      }

      // Bot nhanh hơn người chút nhưng KHÔNG x2 thô
      if (isBot) {
         step += Util.nextInt(5, 15);
      }

      // ===== CHUẨN HOÁ VECTOR =====
      double ratio = step / (double) distance;
      int nextX = curX + (int) (dx * ratio);
      int nextY = curY + (int) (dy * ratio);

      // Ngẫu nhiên nhẹ theo trục Y cho tự nhiên
      if (isBot && Util.isTrue(2, 10)) {
         nextY += Util.nextInt(-15, 15);
      }

      PlayerService.gI().playerMove(this, nextX, nextY);
   }

   public Mob getMobAttack() {
      if (this.zone == null) {
         return null;
      }

      // 1️⃣ Giữ mob cũ nếu còn sống & cùng zone
      if (this.mobTarget != null
            && !this.mobTarget.isDie()
            && this.mobTarget.zone == this.zone) {
         return this.mobTarget;
      }

      // 2️⃣ Giãn thời gian tìm mob mới (tránh spam)
      if (!Util.canDoWithTime(lastTimeFindMob, 300)) {
         return this.mobTarget;
      }
      lastTimeFindMob = System.currentTimeMillis();

      // 3️⃣ Tìm mob sống gần nhất
      Mob nearest = null;
      int minDistance = Integer.MAX_VALUE;

      for (Mob m : this.zone.mobs) {
         if (m == null || m.isDie()) {
            continue;
         }

         int dis = Util.getDistance(this, m);
         if (dis < minDistance) {
            minDistance = dis;
            nearest = m;
         }
      }

      this.mobTarget = nearest;
      return nearest;
   }

   public void active() {
      if (this.isBot) {
         if (this.pet != null) {
            this.pet.update();
         }
         if (this.isDie()) {
            Service.getInstance().sendMoney(this);
            PlayerService.gI().hoiSinh(this);
            Service.getInstance().hsChar(this, this.nPoint.hpMax, this.nPoint.mpMax);
            PlayerService.gI().sendInfoHpMp(this);
         }
         if (this.nPoint.mp <= this.nPoint.mpMax * 30 / 100) {
            this.nPoint.mp = this.nPoint.mpMax;
         }
         this.BayCs();
         this.AttackBoss();
         this.attack();
      }
   }

   public int getRangeCanAttackWithSkillSelect() {
      int skillId = this.playerSkill.skillSelect.template.id;
      if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
         return Skill.RANGE_ATTACK_CHIEU_CHUONG;
      } else {
         return Skill.RANGE_ATTACK_CHIEU_DAM;
      }
   }

   public void BayCs() {
      if (!this.isBot || this.zone == null) {
         return;
      }

      // 1️⃣ Đổi KHU nếu quá đông
      int playerCount = this.zone.getHumanoids().size();
      if (playerCount >= 18 && Util.canDoWithTime(lastTimeChangeZone, 30_000)) {
         this.lastTimeChangeZone = System.currentTimeMillis();
         ChangeMapService.gI().changeZone(this, this.zone.map.mapId);
         return;
      }

      // 2️⃣ Đổi MAP nếu không tìm thấy mob quá lâu
      if (this.getMobAttack() == null
            && Util.canDoWithTime(lastTimeFindMob, 20_000)
            && Util.canDoWithTime(lastTimeMap, 60_000)) {

         this.lastTimeMap = System.currentTimeMillis();
         byte randomMap = (byte) Util.nextInt(0, Manager.IdMapSpam.length - 1);
         ChangeMapService.gI().changeMap(
               this,
               Manager.IdMapSpam[randomMap],
               -1,
               Util.nextInt(100, 300),
               Util.nextInt(100, 300));
      }
   }

   public void AttackBoss() {
      try {
         if (this.isBot) {
            List<Player> playersMap = null;
            playersMap = new ArrayList<>(this.zone.getHumanoids()); // Tạo một bản sao của danh sách playersMap

            if (!this.zone.map.isMapOffline) {
               Iterator<Player> iterator = playersMap.iterator();
               while (iterator.hasNext()) {
                  Player pl = iterator.next();
                  if (pl != null && pl != this && pl.isBoss && pl.location != null) {
                     this.playerSkill.skillSelect = this.playerSkill.skills.get(1);
                     if (SkillService.gI().canUseSkillWithCooldown(this)) {
                        PlayerService.gI().playerMove(this, pl.location.x + Util.nextInt(-60, 60),
                              pl.location.y);
                        SkillService.gI().useSkill(this, pl, null, null);
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
      }
   }

   public Player getBotAttackBOSS() {
      if (this.isBotAttckBoss != null && (this.isBotAttckBoss.isDie() || !this.zone.equals(this.isBotAttckBoss.zone)
            || (this.isBotAttckBoss.pvp == null || this.pvp == null)
            || (this.isBotAttckBoss.typePk != ConstPlayer.PK_ALL || this.typePk != ConstPlayer.PK_ALL)
            || ((this.isBotAttckBoss.cFlag == 0 && this.cFlag == 0)
                  && (this.isBotAttckBoss.cFlag != 8 || this.cFlag == this.isBotAttckBoss.cFlag)))
            || this.isBotAttckBoss == this) {
         this.isBotAttckBoss = null;
      }
      if (this.zone != null
            && (this.isBotAttckBoss == null || this.isBotAttckBoss == this)
            || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
         this.isBotAttckBoss = this.zone.PlayerPKinmap();
         this.lastTimeTargetPlayer = System.currentTimeMillis();
         this.timeTargetPlayer = Util.nextInt(40000, 45000);
      }
      return this.isBotAttckBoss;
   }

   public void attack() {
      try {
         if (!this.isBot)
            return;

         if (!Util.canDoWithTime(lastTimeAttack, 400))
            return;
         lastTimeAttack = System.currentTimeMillis();

         Mob m = getMobAttack();
         if (m == null || m.isDie())
            return;

         // ===== TELE THẲNG TỚI MOB =====
         PlayerService.gI().playerMove(
               this,
               m.location.x + Util.nextInt(-10, 10),
               m.location.y);

         // ===== CHỌN SKILL =====
         if (Util.isTrue(80, 100)) {
            this.playerSkill.skillSelect = this.playerSkill.skills.get(0); // skill cơ bản
         } else {
            this.playerSkill.skillSelect = this.playerSkill.skills.get(
                  Util.nextInt(0, this.playerSkill.skills.size() - 1));
         }

         // ===== ĐÁNH NGAY =====
         SkillService.gI().useSkill(this, null, m, null);

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   // --------------------------------------------------------------------------
   public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
      int mstChuong = this.nPoint.mstChuong;
      int giamst = this.nPoint.tlGiamst;
      if (!this.isDie()) {
         if (this.isMiniPet) {
            return 0;
         }
         if (plAtt != null) {
            // if (this.pet != null && this.pet.status < 3) {
            // this.pet.angry(plAtt);
            // }
            if (!this.isBoss && plAtt.nPoint.xDameChuong && SkillUtil.isUseSkillChuong(plAtt)) {
               damage = (double) plAtt.nPoint.tlDameChuong * damage;
               plAtt.nPoint.xDameChuong = false;
            }
            if (mstChuong > 0 && SkillUtil.isUseSkillChuong(plAtt)) {
               PlayerService.gI().hoiPhuc(this, 0, damage / 100.0 * (double) mstChuong);
               damage = 0;
            }
         }
         if (!SkillUtil.isUseSkillBoom(plAtt)) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
               return 0;
            }
         }
         damage = this.nPoint.subDameInjureWithDeff(damage);
         if (!piercing && effectSkill.isShielding) {
            if (damage > nPoint.hpMax) {
               EffectSkillService.gI().breakShield(this);
            }
            damage = 1;
         }
         if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
            damage = this.nPoint.hp - 1;
         }
         if (giamst > 0) {
            damage -= nPoint.calPercent(damage, giamst);
         }
         if (this.effectSkill.isHoldMabu) {
            damage = 1;
         }
         if (this.effectSkill.isHoldMabu && Util.isTrue(30, 150)) {
            Service.getInstance().removeMabuEat(this);
         }
         this.nPoint.subHP(damage);
         if (isDie()) {
            if (plAtt != null) {
               if (MapService.gI().isMapMabuWar(plAtt.zone.map.mapId)) {
                  plAtt.addPowerPoint(5);
                  Service.getInstance().sendPowerInfo(plAtt, "TL", plAtt.getPowerPoint());
               }
            }
            setDie(plAtt);
         }
         return damage;
      } else {
         return 0;
      }
   }

   private void setDie(Player plAtt) {
      // xóa phù
      if (this.effectSkin.xHPKI > 1) {
         this.effectSkin.xHPKI = 1;
         Service.getInstance().point(this);
      }
      // xóa tụ skill đặc biệt
      this.playerSkill.prepareQCKK = false;
      this.playerSkill.prepareLaze = false;
      this.playerSkill.prepareTuSat = false;
      // xóa hiệu ứng skill
      this.effectSkill.removeSkillEffectWhenDie();
      //
      nPoint.setHp(0);
      nPoint.setMp(0);
      // xóa trứng
      if (this.mobMe != null) {
         this.mobMe.mobMeDie();
      }
      Service.getInstance().charDie(this);
      // add kẻ thù
      if (!this.isPet && !this.isBoss && plAtt != null && !plAtt.isPet && !plAtt.isBoss) {
         if (!plAtt.itemTime.isUseAnDanh) {
            FriendAndEnemyService.gI().addEnemy(this, plAtt);
         }
      }
      if (this.effectSkin.isSocola) {
         reward(plAtt);
      }
      if (MapService.gI().isMapMabuWar(this.zone.map.mapId)) {
         if (this.powerPoint < 20) {
            this.powerPoint = 0;
         }
         if (this.percentPowerPont < 100) {
            this.percentPowerPont = 0;
         }
      }
      // kết thúc pk
      PVPServcice.gI().finishPVP(this, PVP.TYPE_DIE);
      BlackBallWar.gI().dropBlackBall(this);
      if (isHoldNamecBall) {
         NamekBallWar.gI().dropBall(this);
      }
      if (isHoldNamecBallTranhDoat) {
         TranhNgocService.getInstance().dropBall(this, (byte) -1);
         TranhNgocService.getInstance().sendUpdateLift(this);
      }
   }

   public void reward(Player pl) {
      if (pl != null) {
         int x = this.location.x;
         int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
         ItemMap itemMap = new ItemMap(this.zone, 516, 1, x, y, pl.id);
         RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
         if (itemMap != null) {
            Service.getInstance().dropItemMap(zone, itemMap);
         }
      }
   }

   public String tenskill9(int gender) {
      switch (gender) {
         case 2:
            return "Cadic Liên hoàn chưởng";
         case 1:
            return "Ma Phong Ba";
         case 0:
            return "Super Kamejoko";
         default:
            return "";
      }
   }

   // --------------------------------------------------------------------------
   public void setClanMember() {
      if (this.clanMember != null) {
         this.clanMember.powerPoint = this.nPoint.power;
         this.clanMember.head = this.getHead();
         this.clanMember.body = this.getBody();
         this.clanMember.leg = this.getLeg();
      }
   }

   public void congExpOff() {
      long exp = this.getexp() * this.timeoff;
      Service.getInstance().addSMTN(this, (byte) 2, exp, false);
      NpcService.gI().createTutorial(this, 536,
            "Bạn tăng được " + exp + " sức mạnh trong thời gian " + this.timeoff + " phút tập luyện Offline");
   }

   public boolean isAdmin() {
      return this.session.isAdmin;
   }

   public void setJustRevivaled() {
      this.justRevived = true;
      this.lastTimeRevived = System.currentTimeMillis();
      this.immortal = true;
   }

   public void dispose() {
      if (escortedBoss != null) {
         escortedBoss.stopEscorting();
      }
      if (skillSpecial != null) {
         skillSpecial.dispose();
         skillSpecial = null;
      }
      isDisposed = true;
      // if (pet != null) {
      // pet.dispose();
      // pet = null;
      // }
      // playerTrade = null;
      // if (mapBlackBall != null) {
      // mapBlackBall.clear();
      // mapBlackBall = null;
      // }
      // zone = null;
      // mapBeforeCapsule = null;
      // if (mapCapsule != null) {
      // mapCapsule.clear();
      // mapCapsule = null;
      // }
      // if (mobMe != null) {
      // mobMe.dispose();
      // mobMe = null;
      // }
      // location = null;
      // if (setClothes != null) {
      // setClothes.dispose();
      // setClothes = null;
      // }
      // if (effectSkill != null) {
      // effectSkill.dispose();
      // effectSkill = null;
      // }
      // if (mabuEgg != null) {
      // mabuEgg.dispose();
      // mabuEgg = null;
      // }
      // if (playerTask != null) {
      // playerTask.dispose();
      // playerTask = null;
      // }
      // if (itemTime != null) {
      // itemTime.dispose();
      // itemTime = null;
      // }
      // if (fusion != null) {
      // fusion.dispose();
      // fusion = null;
      // }
      // if (magicTree != null) {
      // magicTree.dispose();
      // magicTree = null;
      // }
      // if (playerIntrinsic != null) {
      // playerIntrinsic.dispose();
      // playerIntrinsic = null;
      // }
      // if (inventory != null) {
      // inventory.dispose();
      // inventory = null;
      // }
      // if (playerSkill != null) {
      // playerSkill.dispose();
      // playerSkill = null;
      // }
      // if (combineNew != null) {
      // combineNew.dispose();
      // combineNew = null;
      // }
      // iDMark = null;
      // if (charms != null) {
      // charms.dispose();
      // charms = null;
      // }
      // if (effectSkin != null) {
      // effectSkin.dispose();
      // effectSkin = null;
      // }
      // if (gift != null) {
      // gift.dispose();
      // gift = null;
      // }
      // if (nPoint != null) {
      // nPoint.dispose();
      // nPoint = null;
      // }
      // if (rewardBlackBall != null) {
      // rewardBlackBall.dispose();
      //
      // rewardBlackBall = null;
      // }
      // if (effectFlagBag != null) {
      // effectFlagBag.dispose();
      // effectFlagBag = null;
      // }
      // effectFlagBag = null;
      // clan = null;
      // clanMember = null;
      // friends = null;
      // enemies = null;
      // session = null;
      // name = null;
   }

   public void TangDiem(Player player) {
      try {
         Item item = InventoryService.gI().findItemBagByTemp(player, 1459);
         if (item == null) {
            return;
         }
         for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 240 && io.param < 10) {
               io.param++;
               break;
            }
         }
      } catch (Exception e) {
         System.out.println("Loi tang diem boss");
      }
   }

   public int getexp() {
      int[] expTable = { 5000000 };
      if (this.typetrain >= 0 && this.typetrain < expTable.length) {
         return expTable[this.typetrain];
      } else {
         return 0;
      }
   }
}
