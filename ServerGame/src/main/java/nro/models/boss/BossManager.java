package nro.models.boss;

import nro.utils.Log;
import java.util.ArrayList;
import java.util.List;
import static nro.models.boss.BossFactory.*;
import static nro.models.boss.BossFactory.createBoss;
import nro.models.boss.boss_ban_do_kho_bau.BossBanDoKhoBau;
import nro.models.boss.boss_doanh_trai.BossDoanhTrai;
import nro.models.boss.cdrd.CBoss;
import nro.models.boss.dhvt.BossDHVT;
import nro.models.boss.BossMoi.Gogeta;
import nro.models.boss.BossMoi.GiamNguc;
import nro.models.boss.BossMoi.RobotHuyDiet;
//import nro.models.boss.Boss_Vy_Thu.*;
import nro.models.boss.BossMoi.*;
import nro.models.boss.BossMoiLam.*;
import nro.models.boss.traidat.*;
import nro.models.boss.BossNew.*;
import nro.models.boss.KhungLong.*;
import nro.models.boss.bosstuonglai.Itachi;
import nro.models.boss.mabu_war.BossMabuWar;
import nro.models.boss.vip.BabyVegeta;
import nro.models.boss.vip.CadicSuperSaiyan;
import nro.models.boss.vip.DraburaFrost;
import nro.models.boss.vip.EvilBuu;
import nro.models.boss.vip.GohanZombie;
import nro.models.boss.vip.GokuSuperSaiyan;
import nro.models.boss.vip.HeartsGold;
import nro.models.boss.vip.JirenCuongNo;
import nro.models.boss.vip.RongDen1Sao;
import nro.models.boss.vip.SaiyanGodTrunks;
import nro.models.boss.vip.SuperBrolyHuyenThoai;
import nro.models.boss.vip.SuperZamasuWhite;
import nro.models.boss.vip.ToppoGOD;
import nro.models.boss.vip.VegetaHakai;
import nro.models.boss.vip.ZamasuZombie;
import nro.models.map.mabu.MabuWar;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.MapService;
import nro.utils.Util;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class BossManager {

    public static final List<Boss> BOSSES_IN_GAME;
    private static BossManager intance;
    private static final List<Integer> ALLOWED_MULTI_INSTANCE_BOSS_IDS = List.of(
            BossFactory.KUKU,
            BossFactory.RAMBO,
            BossFactory.MAP_DAU_DINH,
            BossFactory.SON_TINH,
            BossFactory.THUY_TINH,
            BossFactory.GOGETA,
            BossFactory.ROBOT_HUYDIET,
            BossFactory.GIAM_NGUC,
            BossFactory.RONG_DEN,
            BossFactory.SOI_BASIL,
            BossFactory.ALONG,
            BossFactory.MIHALK,
            BossFactory.LUFFY_THAN_NIKA,
//            BossFactory.NHAT_VI,
//            BossFactory.NHI_VI,
//            BossFactory.TAM_VI,
//            BossFactory.TU_VI,
//            BossFactory.NGU_VI,
//            BossFactory.LUC_VI,
//            BossFactory.THAT_VI,
//            BossFactory.BAT_VI,
//            BossFactory.CUU_VI,
//            BossFactory.THAP_VI,
            BossFactory.KAIDO,
            BossFactory.TIEN_HAC_AM,
            BossFactory.ITACHI,
            BossFactory.SOI_3_DAU,
            BossFactory.CUMBER,
            BossFactory.GOKU_SUPER,
            BossFactory.BULMA,
            BossFactory.CHICHITHO,
            BossFactory.POCTHO,
            BossFactory.FIDEGOLD,
            BossFactory.BOSS_VIP_1,
            BossFactory.BOSS_VIP_2,
            BossFactory.BOSS_VIP_3,
            BossFactory.BOSS_VIP_4,
            BossFactory.BOSS_VIP_5,
            BossFactory.BOSS_VIP_6,
            BossFactory.BOSS_VIP_7,
            BossFactory.BOSS_VIP_8,
            BossFactory.BOSS_VIP_9,
            BossFactory.BOSS_VIP_10,
            BossFactory.BOSS_VIP_11,
            BossFactory.BOSS_VIP_12,
            BossFactory.BOSS_VIP_13,
            BossFactory.BOSS_VIP_14,
            BossFactory.BOSS_VIP_15
    );

    static {
        BOSSES_IN_GAME = new ArrayList<>();
    }

    public void updateAllBoss() {
        for (int i = BOSSES_IN_GAME.size() - 1; i >= 0; i--) {
            try {
                Boss boss = BOSSES_IN_GAME.get(i);
                if (boss != null) {
                    boss.update();
                }
            } catch (Exception e) {
                Log.error(BossManager.class, e);
            }
        }

    }

    private BossManager() {

    }

    public static BossManager gI() {
        if (intance == null) {
            intance = new BossManager();
        }
        return intance;
    }

    public Boss getBossById(int bossId) {
        for (int i = BOSSES_IN_GAME.size() - 1; i >= 0; i--) {
            if (BOSSES_IN_GAME.get(i).id == bossId) {
                return BOSSES_IN_GAME.get(i);
            }
        }
        return null;
    }
    private static int currentBossInstanceId = 100000;

    public int getNewBossInstanceId() {
        return currentBossInstanceId++;
    }

    public Boss getBossByInstanceId(int instanceId) {
        for (Boss boss : BOSSES_IN_GAME) {
            if (boss.id == instanceId) {
                return boss;
            }
        }
        return null;
    }

    public boolean isMultiInstanceBoss(int bossId) {
        return ALLOWED_MULTI_INSTANCE_BOSS_IDS.contains(bossId);
    }

    public Boss getBossByIdRandom(int bossId) {
        for (int i = BOSSES_IN_GAME.size() - 1; i >= 0; i--) {
            if (BOSSES_IN_GAME.get(i).id == bossId) {
                return BOSSES_IN_GAME.get(i);
            }
        }
        return null;
    }

    public double getBossidlist(List<Boss> BOSSES_IN_GAME) {
        for (int i = BOSSES_IN_GAME.size() - 1; i >= 0; i--) {
            return BOSSES_IN_GAME.get(i).id;
        }
        return -1;
    }

    public void addBoss(Boss boss) {
        boolean have = false;
        for (Boss b : BOSSES_IN_GAME) {
            if (boss.equals(b)) {
                have = true;
                break;
            }
        }
        if (!have) {
            BOSSES_IN_GAME.add(boss);
        }
    }

    public List<Boss> getBosses() {
        return BossManager.BOSSES_IN_GAME;
    }

    public void removeBoss(Boss boss) {
        BOSSES_IN_GAME.remove(boss);
        boss.dispose();
    }


    public void showListBoss(Player player) {
        Message msg = new Message(-96);
        try {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Danh sách BOSS");
            int count = (int) BOSSES_IN_GAME.stream()
                    .filter(boss -> !(boss instanceof CBoss) && !(boss instanceof BossMabuWar)
                    && !(boss instanceof BossDHVT)
                    && !(boss instanceof BossDoanhTrai) && !(boss instanceof BossBanDoKhoBau)
                    )
                    .count();
            msg.writer().writeByte(count > 120 ? 120 : count);
            for (int i = 0; i < BOSSES_IN_GAME.size(); i++) {
                Boss boss = BOSSES_IN_GAME.get(i);
                if ((boss instanceof CBoss) || (boss instanceof BossMabuWar)
                        || (boss instanceof BossDHVT)|| (boss instanceof BossDoanhTrai)
                        || (boss instanceof BossBanDoKhoBau)
                        
                        ) {
                    continue;
                }
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeShort(boss.data.outfit[0]);
                if (player.isVersionAbove(220)) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(boss.data.outfit[1]);
                msg.writer().writeShort(boss.data.outfit[2]);
                msg.writer().writeUTF(boss.data.name);
                msg.writer().writeUTF(boss.zone != null ? "Sống" : "Chết rồi");
                if (boss.zone != null) {
                    msg.writer().writeUTF("Map xuất hiện: " + boss.zone.map.mapName + " khu " + boss.zone.zoneId
                            + "\nMáu: " + Util.powerToStringnew(boss.nPoint.hp));
                } else {
                    msg.writer().writeUTF("Từ từ dm... boss chưa ra bấm cl");
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showListBossMember(Player player) {
        Message msg = new Message(-96);
        try {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Danh sách BOSS");
            int count = (int) BOSSES_IN_GAME.stream()
                    .filter(boss -> !(boss instanceof CBoss) && !(boss instanceof BossMabuWar)
                    && !(boss instanceof BossDHVT)
                    && !(boss instanceof BossDoanhTrai) && !(boss instanceof BossBanDoKhoBau) && !isVipBoss(boss)
                    )
                    .count();
            msg.writer().writeByte(count > 120 ? 120 : count);
            for (int i = 0; i < BOSSES_IN_GAME.size(); i++) {
                Boss boss = BOSSES_IN_GAME.get(i);
                if ((boss instanceof CBoss) || (boss instanceof BossMabuWar)
                        || (boss instanceof BossDHVT) || isVipBoss(boss)
                        ) {
                    continue;
                }
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeShort(boss.data.outfit[0]);
                if (player.isVersionAbove(220)) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(boss.data.outfit[1]);
                msg.writer().writeShort(boss.data.outfit[2]);
                msg.writer().writeUTF(boss.data.name);
                msg.writer().writeUTF(boss.zone != null ? "Sống" : "Chết rồi");
                if (boss.zone != null) {
                    msg.writer().writeUTF("Máu: " + Util.powerToStringnew(boss.nPoint.hp) + "\nĐây chỉ là con số");
                } else {
                    msg.writer().writeUTF("Từ từ dm... boss chưa ra bấm cl");
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isVipBoss(Boss boss) {
        return boss instanceof DraburaFrost
                || boss instanceof HeartsGold
                || boss instanceof SuperZamasuWhite
                || boss instanceof SuperBrolyHuyenThoai
                || boss instanceof BabyVegeta
                || boss instanceof EvilBuu
                || boss instanceof JirenCuongNo
                || boss instanceof VegetaHakai
                || boss instanceof GohanZombie
                || boss instanceof ToppoGOD
                || boss instanceof SaiyanGodTrunks
                || boss instanceof RongDen1Sao
                || boss instanceof GokuSuperSaiyan
                || boss instanceof CadicSuperSaiyan
                || boss instanceof ZamasuZombie
                ;
    }

    public void showListBossVIP(Player player) {
        Message msg = new Message(-96);
        try {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Danh sách BOSS VIP");

            int count = (int) BOSSES_IN_GAME.stream().filter(this::isVipBoss).count();
            int limit = Math.min(120, count);
            msg.writer().writeByte(limit);

            int emitted = 0;
            for (int i = 0; i < BOSSES_IN_GAME.size(); i++) {
                Boss boss = BOSSES_IN_GAME.get(i);
                if (!isVipBoss(boss)) {
                    continue;
                }

                msg.writer().writeInt((int) boss.id);
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeShort(boss.data.outfit[0]);
                if (player.isVersionAbove(220)) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(boss.data.outfit[1]);
                msg.writer().writeShort(boss.data.outfit[2]);
                msg.writer().writeUTF(boss.data.name);
                msg.writer().writeUTF(boss.zone != null ? "Sống" : "Chết rồi");
                if (boss.zone != null) {
                    msg.writer().writeUTF("Máu: " + Util.powerToStringnew(boss.nPoint.hp) + "\nĐây chỉ là con số");
                } else {
                    msg.writer().writeUTF("Từ từ dm... boss chưa ra bấm cl");
                }

                if (++emitted >= limit) {
                    break; // đảm bảo không vượt quá 120 mục
                }
            }

            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
