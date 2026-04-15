package nro.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import nro.models.boss.Boss;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.server.Client;
import nro.services.func.ChangeMapService;
import nro.utils.Log;

/**
 *
 * @author Hoang Gia PC
 */
public class BossFollowerService implements Runnable {

    private static final Set<String> BOSS_THEO_DOI = Set.of("Broly", "Android13", "Cooler");
    private static final int[] MAP_BOSS_IDS = {45, 46, 47}; // mapId các boss xuất hiện

    public static void start() {
        new Thread(new BossFollowerService(), "BossFollowerService").start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Player> bots = Client.gI().getPlayers().stream()
                        .filter(p -> p.isBot && !p.isDie())
                        .collect(Collectors.toList());

                for (Player bot : bots) {
                    for (Boss boss : BossManager.gI().getBosses()) {
                        if (BOSS_THEO_DOI.contains(boss.name)) {
                            if (bot.zone.map.mapId != boss.zone.map.mapId) {
                                ChangeMapService.gI().changeMap(bot, boss.zone.map.mapId, -1, 200, 100);
                                Service.getInstance().sendThongBao(bot, "Tìm thấy boss " + boss.name + ", đang di chuyển...");
                            }
                        }
                    }
                }

                Thread.sleep(60_000); // kiểm tra mỗi phút
            } catch (Exception e) {
                Log.error("Lỗi focut boss bot player");
            }
        }
    }
}

