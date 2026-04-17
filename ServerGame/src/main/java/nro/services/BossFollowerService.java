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

public class BossFollowerService {

   private static final Set<String> BOSS_THEO_DOI = Set.of("Broly", "Android13", "Cooler");

   public void update() {
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
      } catch (Exception e) {
         Log.error("Lỗi focut boss bot player");
      }
   }
}
