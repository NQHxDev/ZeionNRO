package nro.models.boss;

import nro.models.player.Player;

public abstract class FutureBoss extends Boss {

    public FutureBoss(int id, BossData data) {
        super(id, data);
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        damage = (damage / 100 ) * 80;
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }
}
