/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.models.map;

import nro.models.mob.Mob;
import nro.models.player.Player;
import nro.services.MapService;
import nro.services.MobService;
import nro.services.Service;
import nro.utils.Log;
import nro.utils.Util;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import nro.core.concurrent.GameScheduler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Getter
public class SantaCity extends Map {

    private boolean isOpened;
    private boolean isClosed;
    private ScheduledFuture<?> dailyOpenTask;
    private ScheduledFuture<?> closeTask;

    public SantaCity(int mapId, String mapName, byte planetId, byte tileId, byte bgId, byte bgType, byte type, int[][] tileMap, int[] tileTop, int zones, boolean isMapOffline, int maxPlayer, List<WayPoint> wayPoints, List<EffectMap> effectMaps) {
        super(mapId, mapName, planetId, tileId, bgId, bgType, type, tileMap, tileTop, zones, isMapOffline, maxPlayer, wayPoints, effectMaps);
    }

    @Override
    public void initZone(int number, int maxPlayer) {
        zones = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Zone zone = new Zone(this, i, maxPlayer);
            zones.add(zone);
        }
    }

    public Zone random() {
        synchronized (zones) {
            if (!zones.isEmpty()) {
                List<Zone> list = zones.stream().filter(t -> !t.isFullPlayer()).collect(Collectors.toList());
                if (list.isEmpty()) {
                    list = zones;
                }
                int r = Util.nextInt(list.size());
                return zones.get(r);
            }
        }
        return null;
    }

    public void enter(Player player) {
        Zone zone = random();
        if (zone != null) {
            player.location.x = 100;
            player.location.y = 360;
            MapService.gI().goToMap(player, zone);
            Service.getInstance().clearMap(player);
            zone.mapInfo(player);
            player.zone.loadAnotherToMe(player);
            player.zone.load_Me_To_Another(player);
        }
    }

    public void leave(Player player) {
        Zone zone = MapService.gI().getZoneJoinByMapIdAndZoneId(player, 19, 0);
        if (zone != null) {
            player.location.x = 1060;
            player.location.y = 360;
            MapService.gI().goToMap(player, zone);
            Service.getInstance().clearMap(player);
            zone.mapInfo(player);
            player.zone.loadAnotherToMe(player);
            player.zone.load_Me_To_Another(player);
        }
    }

    public void open() {
        if (!isOpened) {
            this.isOpened = true;
        }
    }

    public void timer(int hours, int minutes, int seconds, long time) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext = zonedNow.withHour(hours).withMinute(minutes).withSecond(seconds);
        if (zonedNow.compareTo(zonedNext) > 0) {
            zonedNext = zonedNext.plusDays(1);
        }
        long initialDelaySec = Duration.between(zonedNow, zonedNext).getSeconds();

        Runnable openCycle = () -> {
            try {
                reset();
                open();
                if (closeTask != null && !closeTask.isDone()) {
                    closeTask.cancel(false);
                }
                closeTask = GameScheduler.SCHED.schedule(this::close, time, TimeUnit.MILLISECONDS);
            } catch (Throwable ex) {
                Log.error(SantaCity.class, (Exception) ex, ex.getMessage());
            }
        };

        if (dailyOpenTask != null && !dailyOpenTask.isDone()) {
            dailyOpenTask.cancel(false);
        }
        dailyOpenTask = GameScheduler.SCHED.scheduleAtFixedRate(
                openCycle, initialDelaySec, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public void stopTimers() {
        try {
            if (dailyOpenTask != null) {
                dailyOpenTask.cancel(false);
            }
        } catch (Exception ignore) {
        }
        try {
            if (closeTask != null) {
                closeTask.cancel(false);
            }
        } catch (Exception ignore) {
        }
        dailyOpenTask = null;
        closeTask = null;
    }

    public void reset() {
        this.isOpened = false;
        this.isClosed = false;
        synchronized (zones) {
            for (Zone z : zones) {
                synchronized (z.mobs) {
                    for (Mob mob : z.mobs) {
                        MobService.gI().hoiSinhMob(mob);
                    }
                }
            }
        }
    }

    public void close() {
        if (!isClosed) {
            isClosed = true;
            synchronized (zones) {
                for (Zone z : zones) {
                    try {
                        List<Player> players = z.getPlayers().stream().collect(Collectors.toList());
                        players.forEach(t -> {
                            if (t.isDie()) {
                                Service.getInstance().hsChar(t, t.nPoint.hpMax, t.nPoint.mpMax);
                            }
                            leave(t);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

}
