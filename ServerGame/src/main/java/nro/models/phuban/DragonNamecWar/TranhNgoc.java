/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.phuban.DragonNamecWar;

import java.util.ArrayList;
import java.util.List;
import nro.consts.ConstTranhNgocNamek;
import nro.models.player.Player;
import nro.services.func.ChangeMapService;
import nro.utils.TimeUtil;
import nro.utils.Util;

/**
 *
 * @Build Arriety
 */
public class TranhNgoc {

    private static TranhNgoc i;

    private static long TIME_REGISTER;
    private static long TIME_OPEN;
    private static long TIME_CLOSE;

    public static final byte HOUR_REGISTER = 17;
    public static final byte MIN_REGISTER = 0;
    public static final byte HOUR_OPEN = 17;
    public static final byte MIN_OPEN = 5;

    public static final byte HOUR_CLOSE = 17;
    public static final byte MIN_CLOSE = 10;

    private final List<Player> playersFide = new ArrayList<>();
    private final List<Player> playersCadic = new ArrayList<>();

    private int day = -1;

    public static TranhNgoc gI() {
        if (i == null) {
            i = new TranhNgoc();
        }
        i.setTime();
        return i;
    }

    public List<Player> getPlayersCadic() {
        return this.playersCadic;
    }

    public List<Player> getPlayersFide() {
        return this.playersFide;
    }

    public void addPlayersCadic(Player player) {
        synchronized (playersCadic) {
            if (!this.playersCadic.contains(player)) {
                this.playersCadic.add(player);
            }
        }
    }

    public void addPlayersFide(Player player) {
        synchronized (playersFide) {
            if (!this.playersFide.contains(player)) {
                this.playersFide.add(player);
            }
        }
    }

    public void removePlayersCadic(Player player) {
        synchronized (playersCadic) {
            if (this.playersCadic.contains(player)) {
                this.playersCadic.remove(player);
            }
        }
    }

    public void removePlayersFide(Player player) {
        synchronized (playersFide) {
            if (this.playersFide.contains(player)) {
                this.playersFide.remove(player);
            }
        }
    }

    public void setTime() {
        if (i.day == -1 || i.day != TimeUtil.getCurrDay()) {
            i.day = TimeUtil.getCurrDay();
            try {
                TranhNgoc.TIME_OPEN = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_OPEN + ":" + MIN_OPEN + ":" + 0, "dd/MM/yyyy HH:mm:ss");
                TranhNgoc.TIME_CLOSE = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":" + 0, "dd/MM/yyyy HH:mm:ss");
                TranhNgoc.TIME_REGISTER = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_REGISTER + ":" + MIN_REGISTER + ":" + 0, "dd/MM/yyyy HH:mm:ss");
            } catch (Exception e) {
            }
        }
    }

    public void update(Player player) {
        try {
            long currentTime = System.currentTimeMillis();
            if (Util.canDoWithTime(player.lastTimeUpdateBallWar, 1000)) {
                player.lastTimeUpdateBallWar = currentTime;
                if (player.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
                    try {
                        if (!isTimeStartWar() || (!player.zone.getPlayersFide().contains(player) && !player.zone.getPlayersCadic().contains(player))) {
                            kickOutOfMap(player);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    if (isTimeStartWar() && (playersFide.contains(player) || playersCadic.contains(player))) {
                        ChangeMapService.gI().changeMapInYard(player, ConstTranhNgocNamek.MAP_ID, -1, -1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void kickOutOfMap(Player player) {
        player.iDMark.setTranhNgoc((byte) -1);
        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        player.isHoldNamecBallTranhDoat = false;
        player.tempIdNamecBallHoldTranhDoat = -1;
    }

    public boolean isTimeRegisterWar() {
        long now = System.currentTimeMillis();
        return now > TIME_REGISTER && now < TIME_OPEN;
    }

    public boolean isTimeStartWar() {
        long now = System.currentTimeMillis();
        return now > TIME_OPEN && now < TIME_CLOSE;
    }
}
