package nro.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class JackyChun extends BossDHVT {

    public JackyChun(Player player) {
        super(BossFactory.JACKY_CHUN, BossData.JACKY_CHUN);
        this.playerAtt = player;
    }
}