package nro.services;

import nro.models.map.Zone;
import nro.models.player.Player;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

public class BotAIService {

    // ===== CONFIG =====
    private static final long TIME_FIND_MOB = 30_000;     // 30s không có mob
    private static final long TIME_CHECK_AI = 3_000;      // 3s mới check AI 1 lần
    private static final long TIME_CHANGE_MAP = 5_000;    // 5s cooldown đổi map/khu

    private static final int[] BOT_ALLOWED_MAPS = {
//        1, 2, 3, 4, 6, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19,
//        20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
        63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
        79, 80, 81, 82, 83,
//        92, 93, 94,
//        96, 97, 98, 99, 100,
//        105, 106, 107, 108, 109, 110
    };

    public static void updateBot(Player bot) {
        if (bot == null || !bot.isBot || bot.zone == null) {
            return;
        }

        // hạn chế check AI quá dày
        if (!Util.canDoWithTime(bot.lastTimeBotAI, TIME_CHECK_AI)) {
            return;
        }
        bot.lastTimeBotAI = System.currentTimeMillis();

        // ===== 1. Zone quá đông → xử lý ngay =====
        if (bot.zone.getHumanPlayerCount() > 3) {
            tryChangeZoneOrMap(bot);
            return;
        }

        // ===== 2. Không tìm được mob quá lâu =====
        if (Util.canDoWithTime(bot.lastTimeFindMob, TIME_FIND_MOB)) {
            tryJumpMap(bot);
        }
    }

    // ===============================
    // Ưu tiên đổi ZONE, sau đó mới đổi MAP
    // ===============================
    private static void tryChangeZoneOrMap(Player bot) {
        if (!Util.canDoWithTime(bot.lastTimeChangeMap, TIME_CHANGE_MAP)) {
            return;
        }

        try {
            int mapId = bot.zone.map.mapId;
            Zone newZone = MapService.gI().getZoneForBot(bot, mapId);

            if (newZone != null && newZone != bot.zone && newZone.map != null) {
                ChangeMapService.gI().changeZone(bot, newZone);
            } else {
                tryJumpMap(bot);
            }
        } catch (Exception e) {
            tryJumpMap(bot);
        }

        bot.lastTimeChangeMap = System.currentTimeMillis();
    }

    // ===============================
    // Nhảy MAP khác khi cần
    // ===============================
    private static void tryJumpMap(Player bot) {
        if (!Util.canDoWithTime(bot.lastTimeChangeMap, TIME_CHANGE_MAP)) {
            return;
        }

        Zone z = getSafeZoneForBot(bot);
        if (z == null) {
            return; // ❌ không tìm được map an toàn → bỏ qua
        }

        try {
            ChangeMapService.gI().changeMap(
                    bot,
                    z,
                    Util.nextInt(50, z.map.mapWidth - 50),
                    z.map.mapHeight - 20,
                    200,
                    200,
                    ChangeMapService.NON_SPACE_SHIP
            );
            bot.lastTimeFindMob = System.currentTimeMillis();
            bot.lastTimeChangeMap = System.currentTimeMillis();
        } catch (Exception e) {
            // ❌ NUỐT LỖI – bot không được làm sập server
        }
    }

    private static Zone getSafeZoneForBot(Player bot) {
        for (int i = 0; i < 10; i++) { // retry tối đa 10 lần
            int mapId = BOT_ALLOWED_MAPS[Util.nextInt(BOT_ALLOWED_MAPS.length)];

            Zone z = MapService.gI().getZoneForBot(bot, mapId);
            if (z == null || z.map == null) {
                continue;
            }
            if (z.map.mapWidth <= 0 || z.map.mapHeight <= 0) {
                continue;
            }
            if (z.isFullPlayer()) {
                continue;
            }

            return z;
        }
        return null;
    }

}
