package nro.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class ChaPa extends BossDHVT {

    public ChaPa(Player player) {
        super(BossFactory.CHA_PA, BossData.CHA_PA);
        this.playerAtt = player;
    }
}