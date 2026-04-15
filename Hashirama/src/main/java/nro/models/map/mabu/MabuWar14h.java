/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.map.mabu;

import java.util.ArrayList;
import java.util.List;
import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.player.Player;
import nro.services.MapService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.TimeUtil;

/**
 *
 * @author: Duy
 * @debug: Arriety
 * @tester: PutNgu
 */
public class MabuWar14h {

    private static MabuWar14h i;
    public final List<Boss> bosses = new ArrayList<>();
    public static long TIME_OPEN;

    public static long TIME_CLOSE;
    public static final byte HOUR_OPEN = 21;
    public static final byte MIN_OPEN = 0;
    public static final byte SECOND_OPEN = 0;
    public static final byte HOUR_CLOSE = 21;
    public static final byte MIN_CLOSE = 30;
    public static final byte SECOND_CLOSE = 0;
    private int day = -1;
    public boolean initBoss;
    public boolean clearBoss;

    public static MabuWar14h gI() {
        if (i == null) {
            i = new MabuWar14h();
        }
        i.setTime();
        return i;
    }

    public void setTime() {
        if (i.day == -1 || i.day != TimeUtil.getCurrDay()) {
            i.day = TimeUtil.getCurrDay();
            try {
                MabuWar14h.TIME_OPEN = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_OPEN + ":" + MIN_OPEN + ":" + SECOND_OPEN, "dd/MM/yyyy HH:mm:ss");
                MabuWar14h.TIME_CLOSE = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":" + SECOND_CLOSE, "dd/MM/yyyy HH:mm:ss");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isTimeMabuWar() {
        long now = System.currentTimeMillis();
        return now > TIME_OPEN && now < TIME_CLOSE;
    }

    public void update(Player player) {
        try {
            if (player != null && player.zone != null) {
                if (MapService.gI().isMapMabuWar14H(player.zone.map.mapId)) {
                    if (isTimeMabuWar()) {
                        if (!initBoss) {
                            BossFactory.initBossMabuWar14H();
                            initBoss = true;
                        }
                    }
                    try {
                        if (!isTimeMabuWar() && !MabuWar.gI().isTimeMabuWar()) {
                            kickOutOfMap(player);
                            removeAllBoss();
                        }
                    } catch (Exception ex) {
                        System.out.println("Log bug player: " + player.name);
                        Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra!");
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Log bug player: " + player.name);
            Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra!");
            e.printStackTrace();
        }
    }

    private void kickOutOfMap(Player player) {
        Service.getInstance().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
    }

    public void removeAllBoss() {
        if (!clearBoss) {
            for (Boss boss : bosses) {
                boss.leaveMap();
            }
            this.bosses.clear();
            clearBoss = true;
        }
    }
}
