package nro.models.map.phoban;

import nro.models.boss.boss_ban_do_kho_bau.BossBanDoKhoBau;
import nro.models.boss.boss_ban_do_kho_bau.TrungUyXanhLo;
import nro.models.clan.Clan;
import nro.models.map.TrapMap;
import nro.models.map.Zone;
import nro.models.mob.GuardRobot;
import nro.models.mob.Mob;
import nro.models.mob.Octopus;
import nro.models.player.Player;
import nro.services.ItemTimeService;
import nro.services.MobService;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class BanDoKhoBau {

    public static final long POWER_CAN_GO_TO_DBKB = 200000000;

    public static final List<BanDoKhoBau> BAN_DO_KHO_BAUS;
    public static final int MAX_AVAILABLE = 50;
    public static final int TIME_BAN_DO_KHO_BAU = 600000;

    static {
        BAN_DO_KHO_BAUS = new ArrayList<>();
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            BAN_DO_KHO_BAUS.add(new BanDoKhoBau(i));
        }
    }

    public int id;
    public byte level;
    public final List<Zone> zones;
    public final List<BossBanDoKhoBau> bosses;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;

    public BanDoKhoBau(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
        this.bosses = new ArrayList<>();
    }

    public void update() {
        if (this.isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_BAN_DO_KHO_BAU)) {
                finish();
            }
        }
    }

    public void openBanDoKhoBau(Player plOpen, Clan clan, byte level) {
//        if (true) {
//            Service.getInstance().sendThongBao(plOpen, "Chuc nang tam dong");
//            return;
//        }
        this.level = level;
        this.lastTimeOpen = System.currentTimeMillis();
        this.isOpened = true;
        this.clan = clan;
        this.clan.timeOpenBanDoKhoBau = this.lastTimeOpen;
        this.clan.playerOpenBanDoKhoBau = plOpen;
        this.clan.banDoKhoBau = this;
        resetBanDo();

        ChangeMapService.gI().goToDBKB(plOpen);

        sendTextBanDoKhoBau();
    }

    private void resetBanDo() {
        for (Zone zone : zones) {
            for (TrapMap trap : zone.trapMaps) {
                trap.dame = this.level * 100000;
            }
        }
        for (Zone zone : zones) {
            for (Mob m : zone.mobs) {
                if (m instanceof Octopus) {
                    m.location.x = 740;
                    m.location.y = 576;
                }
                if (m instanceof GuardRobot) {
                    m.location.x = 550;
                    m.location.y = 336;
                }
                MobService.gI().initMobBanDoKhoBau(m, this.level);
                MobService.gI().hoiSinhMob(m);
            }
        }
        for (BossBanDoKhoBau boss : bosses) {
            boss.leaveMap();
        }
        this.bosses.clear();
        initBoss();
    }

    private void initBoss() {
        this.bosses.add(new TrungUyXanhLo(this));
    }

    //kết thúc bản đồ kho báu
    private void finish() {
        List<Player> plOutDT = new ArrayList();
        for (Zone zone : zones) {
            List<Player> players = zone.getPlayers();
            synchronized (players) {
                for (Player pl : players) {
                    plOutDT.add(pl);
                }
            }

        }
        for (Player pl : plOutDT) {
            ChangeMapService.gI().changeMapBySpaceShip(pl, 5, -1, 64);
        }
        this.clan.banDoKhoBau = null;
        this.clan = null;
        this.isOpened = false;
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public static void addZone(int idBanDo, Zone zone) {
        BAN_DO_KHO_BAUS.get(idBanDo).zones.add(zone);
    }

    private void sendTextBanDoKhoBau() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        }
    }
}
