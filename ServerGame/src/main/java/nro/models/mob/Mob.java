package nro.models.mob;

import nro.consts.ConstMap;
import nro.consts.ConstMob;
import java.util.List;
import nro.models.map.Zone;
import nro.models.map.dungeon.zones.ZSnakeRoad;
import nro.models.player.Location;
import nro.models.player.Player;
import nro.power.CaptionManager;
import nro.services.MapService;
import nro.services.Service;
import nro.utils.Util;
import nro.services.MobService;
import nro.services.TaskService;

public class Mob {

   private static final double[] POW_095 = new double[201];
   private static final double[] POW_115 = new double[201];

   static {
      for (int i = 0; i <= 200; i++) {
         POW_095[i] = Math.pow(0.95, i);
         POW_115[i] = Math.pow(1.15, i);
      }
   }

   public int id;
   public Zone zone;
   public int tempId;
   public String name;
   public byte level;
   public boolean isDie;

   public MobPoint point;
   public MobEffectSkill effectSkill;
   public Location location;

   public byte pDame;
   public int pTiemNang;
   private double maxTiemNang;

   public long lastTimeDie;
   public int sieuquai = 0;

   public boolean actived;

   // private long targetID;

   public Mob(Mob mob) {
      this.point = new MobPoint(this);
      this.effectSkill = new MobEffectSkill(this);
      this.location = new Location();
      this.id = mob.id;
      this.tempId = mob.tempId;
      this.level = mob.level;
      this.point.setHpFull(mob.point.getHpFull());
      this.point.setHP(this.point.getHpFull());
      this.location.x = mob.location.x;
      this.location.y = mob.location.y;
      this.pDame = mob.pDame;
      this.pTiemNang = mob.pTiemNang;
      this.setTiemNang();
      this.status = 5;
   }

   public Mob() {
      this.point = new MobPoint(this);
      this.effectSkill = new MobEffectSkill(this);
      this.location = new Location();
   }

   public int getSys() {
      return 0;
   }

   public void setTiemNang() {
      this.maxTiemNang = (this.point.getHpFull() * (1.0 + (this.level * 0.15))) / 10.0;
   }

   public byte status;

   protected long lastTimeAttackPlayer;

   public boolean isDie() {
      return this.isDie;
   }

   public synchronized void injured(Player plAtt, double damage, boolean dieWhenHpFull) {
      if (!this.isDie()) {
         // if (plAtt != null) {
         // this.targetID = plAtt.id;
         // }
         this.addPlayerAttack(plAtt);

         if (damage >= this.point.hp) {
            damage = this.point.hp;
         }
         if (!dieWhenHpFull) {
            if (this.point.hp >= this.point.maxHp && damage >= this.point.hp) {
               damage = this.point.hp - 1;
            }
            if (this.tempId == 0 && damage > 10.0) {
               damage = 10.0;
            }
         }
         this.point.hp -= damage;
         if (this.point.hp <= 0) {
            MobService.gI().dropItemTask(plAtt, this);
            MobService.gI().sendMobDieAffterAttacked(this, plAtt, damage);
            TaskService.gI().checkDoneTaskKillMob(plAtt, this);
            TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
            this.isDie = true;
            setDie();
         } else {
            MobService.gI().sendMobStillAliveAffterAttacked(this, damage, plAtt != null ? plAtt.nPoint.isCrit : false);
         }
         if (plAtt != null) {
            Service.getInstance().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
         }

         // if (this.isDie() && plAtt != null) {
         // if (!plAtt.isPet) {
         // if (plAtt.charms.tdThuHut > System.currentTimeMillis()) {
         // for (int i = this.zone.items.size() - 1; i >= 0; i--) {
         // ItemMap itemMap = this.zone.items.get(i);
         // if (itemMap.playerId == plAtt.id) {
         // ItemMapService.gI().pickItem(plAtt, itemMap.itemMapId);
         // }
         // }
         // }
         // } else {
         // if (((Pet) plAtt).master.charms.tdThuHut > System.currentTimeMillis()) {
         // for (int i = this.zone.items.size() - 1; i >= 0; i--) {
         // ItemMap itemMap = this.zone.items.get(i);
         // if (itemMap.playerId == ((Pet) plAtt).master.id) {
         // ItemMapService.gI().pickItem(((Pet) plAtt).master, itemMap.itemMapId);
         // }
         // }
         // }
         // }
         // }
      }
   }

   public long getTiemNangForPlayer(Player pl, double dame) {
      if (this.tempId == 0) { // Mộc nhân
         return 10;
      }
      int levelPlayer = CaptionManager.getInstance().getLevel(pl);
      int levelDiff = levelPlayer - this.level;

      double ratio = dame / point.getHpFull();
      if (ratio > 1.0) {
         ratio = 1.0 + (ratio - 1.0) / 10.0;
      }

      // Tăng hệ số nhân cơ bản từ 5.0 lên 6.5
      long tiemNang = (long) (ratio * maxTiemNang * 6.5);

      // Xử lý chênh lệch Level bằng bảng tra cứu đã tính sẵn
      if (levelDiff > 0) {
         int idx = Math.min(200, levelDiff);
         tiemNang = (long) (tiemNang * POW_095[idx]); // Giảm 5% mỗi cấp khi mạnh hơn quái
         if (levelDiff > 15) {
            tiemNang = 1;
         }
      } else if (levelDiff < 0) {
         int idx = Math.min(200, -levelDiff);
         tiemNang = (long) (tiemNang * POW_115[idx]); // Tăng 15% mỗi cấp khi đánh quái vượt cấp
      }

      tiemNang = Math.max(1, tiemNang);

      // Thưởng TN/SM theo Map
      if (pl.nPoint.power < 100_000_000_000L && MapService.gI().isMapNguHanhSon(pl.zone.map.mapId)) {
         tiemNang *= 2;
      }

      // Gom nhóm các Map thưởng x4
      int mapId = pl.zone.map.mapId;
      boolean isBonusMap = (mapId >= 53 && mapId <= 62) || (mapId >= 135 && mapId <= 138)
            || (mapId >= 141 && mapId <= 143) || (mapId >= 216 && mapId <= 218)
            || MapService.gI().isMapBanDoKhoBau(mapId);

      if (isBonusMap) {
         tiemNang *= 4;
      }

      return pl.nPoint.calSucManhTiemNang(tiemNang);
   }

   public void update() {
      if (this.isDie()) {
         if (!(zone instanceof ZSnakeRoad)) {
            boolean canRespawn = (zone.map.type == ConstMap.MAP_NORMAL
                  || zone.map.type == ConstMap.MAP_OFFLINE
                  || zone.map.type == ConstMap.MAP_BLACK_BALL_WAR
                  || (zone.map.mapId >= 0 && zone.map.mapId <= 44) || tempId == 0) // Mộc nhân luôn hồi sinh
                  && (tempId != ConstMob.HIRUDEGARN);

            if (canRespawn && Util.canDoWithTime(lastTimeDie, 2000)) {
               MobService.gI().hoiSinhMob(this);
            } else if (this.zone.map.type == ConstMap.MAP_DOANH_TRAI && Util.canDoWithTime(lastTimeDie, 10000)) {
               MobService.gI().hoiSinhMobDoanhTrai(this);
            }
         }
         return;
      }
      if (zone != null) {
         effectSkill.update();
         if (!zone.getPlayers().isEmpty() && Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            attackPlayer();
         }
      }
   }

   public void attackPlayer() {
      if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0)) {
         Player pl = getPlayerCanAttack();
         if (pl != null) {
            double damage = MobService.gI().mobAttackPlayer(this, pl);
            MobService.gI().sendMobAttackMe(this, pl, damage);
            MobService.gI().sendMobAttackPlayer(this, pl);
         }
         this.lastTimeAttackPlayer = System.currentTimeMillis();
      }
   }

   public Player getPlayerCanAttack() {
      int distance = 500;
      Player plAttack = null;
      // Player plAttack = zone.findPlayerByID(targetID);
      // int dis = Util.getDistance(plAttack, this);
      // if (plAttack != null && dis <= distance) {
      // return plAttack;
      // }
      distance = 100;
      try {
         List<Player> players = this.zone.getNotBosses();
         for (Player pl : players) {
            if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isMiniPet
                  && !pl.nPoint.buffDefenseSatellite) {
               int dis = Util.getDistance(pl, this);
               if (dis <= distance) {
                  plAttack = pl;
                  distance = dis;
               }
            }
         }
      } catch (Exception e) {

      }
      return plAttack;
   }

   private void addPlayerAttack(Player pl) {
   }

   public void setDie() {
      this.lastTimeDie = System.currentTimeMillis();
   }
}
