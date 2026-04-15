package nro.models.mob;

import nro.utils.Util;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class MobPoint {

    public final Mob mob;
    public double hp;
    public double maxHp;
    public double dame;

    public double clanMemHighestDame; //dame lớn nhất trong clan
    public double clanMemHighestHp; //hp lớn nhất trong clan

    public int xHpForDame = 50; //dame gốc = highesHp / xHpForDame;
    public int xDameForHp = 10; //hp gốc = xDameForHp * highestDame;

    public MobPoint(Mob mob) {
        this.mob = mob;
    }

    public double getHpFull() {
        return maxHp;
    }

    public void setHpFull(double hp) {
        maxHp = hp;
    }

    public double getHP() {
        return hp;
    }

    public void setHP(double hp) {
        if (this.hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

    public double getDameAttack() {
        return this.dame != 0 ? this.dame + Util.nextdameDouble(-(this.dame / 100.0), (this.dame / 100.0))
                : this.getHpFull() * Util.nextdameDouble(mob.pDame - 1, mob.pDame + 1) / 100.0
                + Util.nextdameDouble(-(mob.level * 10.0), mob.level * 10.0);
    }
}
