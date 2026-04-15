package nro.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class Xinbato extends BossDHVT {

    public Xinbato(Player player) {
        super(BossFactory.XINBATO, BossData.XINBATO);
        this.playerAtt = player;
    }
}