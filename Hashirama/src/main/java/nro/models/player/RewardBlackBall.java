package nro.models.player;

import nro.services.Service;
import nro.utils.TimeUtil;
import nro.utils.Util;

import java.util.Date;
import nro.models.item.Item;
import nro.services.InventoryService;
import nro.services.ItemService;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class RewardBlackBall {

    private static final int TIME_REWARD = 79200000;

    public static final int R1S = 50; // +SDG
    public static final int R2S = 100; // +HP
    public static final int R3S = 100; // +KI
    public static final int R4S = 30; // CHI MANG
    public static final int R5S = 100; // GIAP
    public static final int R6S = 20; // NE DON
    public static final int R7S = 1; // thoi vang

    public static final int TIME_WAIT = 3600000;

    private Player player;

    public long[] timeOutOfDateReward;
    public long[] lastTimeGetReward;

    public RewardBlackBall(Player player) {
        this.player = player;
        this.timeOutOfDateReward = new long[7];
        this.lastTimeGetReward = new long[7];
    }

    public void reward(byte star) {
        this.timeOutOfDateReward[star - 1] = System.currentTimeMillis() + TIME_REWARD;
        Service.getInstance().point(player);
    }

    public void getRewardSelect(byte select) {
        int index = 0;
        for (int i = 0; i < timeOutOfDateReward.length; i++) {
            if (timeOutOfDateReward[i] > System.currentTimeMillis()) {
                index++;
                if (index == select + 1) {
                    getReward(i + 1);
                    break;
                }
            }
        }
    }

    private void getReward(int star) {
        if (timeOutOfDateReward[star - 1] > System.currentTimeMillis()
                && Util.canDoWithTime(lastTimeGetReward[star - 1], TIME_WAIT)) {
            switch (star) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số tự động nhận");
                    break;
                case 7:
                    if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                        Service.getInstance().sendThongBao(player, "Hành trang không đủ chổ trống");
                        return;
                    }
                    Item thoivang = ItemService.gI().createNewItem((short) 1564, R7S);
                    InventoryService.gI().addItemBag(player, thoivang, 0);
                    InventoryService.gI().sendItemBags(player);
                    lastTimeGetReward[star - 1] = System.currentTimeMillis();
                    Service.getInstance().sendThongBaoOK(player, "Nhận được 1 " + thoivang.getName());
                    break;
            }
        } else {
            Service.getInstance().sendThongBao(player, "Chưa thể nhận phần quà ngay lúc này, vui lòng đợi "
                    + TimeUtil.diffDate(new Date(lastTimeGetReward[star - 1]), new Date(lastTimeGetReward[star - 1] + TIME_WAIT),
                            TimeUtil.MINUTE) + " phút nữa");
        }
    }

    public void dispose() {
        this.player = null;
    }
}
