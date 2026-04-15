package nro.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class Yamcha extends BossDHVT {

    public Yamcha(Player player) {
        super(BossFactory.YAMCHA, BossData.YAMCHA);
        this.playerAtt = player;
    }
}