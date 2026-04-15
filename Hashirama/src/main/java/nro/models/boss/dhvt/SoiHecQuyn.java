package nro.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class SoiHecQuyn extends BossDHVT {
    public SoiHecQuyn(Player player) {
        super(BossFactory.SOI_HEC_QUYN, BossData.SOI_HEC_QUYN);
        this.playerAtt = player;
    }
}
