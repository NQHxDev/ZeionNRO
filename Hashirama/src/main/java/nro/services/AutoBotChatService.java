package nro.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import nro.models.player.Player;
import nro.server.Client;

/**
 *
 * @author Hoang Gia PC
 */
public class AutoBotChatService implements Runnable {

    private static AutoBotChatService instance;

    public static AutoBotChatService gI() {
        if (instance == null) {
            instance = new AutoBotChatService();
            new Thread(instance, "AutoBotChatService").start();
        }
        return instance;
    }

    private final List<String> texts = Arrays.asList(
            "chung may biet bo m la ai khong",
            "bo may can tat",
            "ae nao thua spl cho xin it",
            "xin it nro 3s voi ae oi",
            "ae cho xin slot bang voi",
            "super nay ae tele toi hup nay",
            "mua thoi vang sll ae nào co vao zl ib",
            "mua tat, ruby sll co ib zl",
            "bay acc 50k",
            "mua gtl xd ai co ib zl",
            "mua wtl xd ai co khong",
            "mua awj 7s ai co show zalo tui ib",
            "mua 3s vip ai co ib",
            "ban nro 3s ai mua ib",
            "ban nro 3s vip 4pt sll",
            "ban dnc , tv, do than sll",
            "mua de itachi 300k atm ai co ib",
            "ban trung itachi 400k atm ai mua ib zl",
            "ban trung ngo khong 100k atm ai mua ib",
            "mua trung zeno 50k ai ban ib",
            "xin it spl hm ae oi",
            "ai co spl tmsm cho xin it",
            "ma dong vl khong co khu vang luon",
            "sv cay thoi vang de vl",
            "ma nhieu cai trang vai lol",
            "ma dong vl khong co khu vang luon",
            "dm tui m cut het cho t fam",
            "ra dao kame cho do tl nay",
            "mua de kaido 500k atm ai co ib gap"
    );

    @Override
    public void run() {
        while (true) {
            try {
                List<Player> bots = Client.gI().getPlayers().stream()
                        .filter(p -> p != null && p.isBot)
                        .collect(Collectors.toList());

                if (bots.isEmpty()) {
                    Thread.sleep(300_000);
                    continue;
                }

                // shuffle bot
                Collections.shuffle(bots);

                // shuffle text (copy list để không ảnh hưởng list gốc)
                List<String> shuffledTexts = new ArrayList<>(texts);
                Collections.shuffle(shuffledTexts);

                int limit = Math.min(10, bots.size());
                for (int i = 0; i < limit; i++) {
                    Player bot = bots.get(i);
                    String msg = shuffledTexts.get(i % shuffledTexts.size());
                    ChatGlobalService.gI().chat(bot, msg);
                }

                Thread.sleep(300_000); // 5 phút
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
