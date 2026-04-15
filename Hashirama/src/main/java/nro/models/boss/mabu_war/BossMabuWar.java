package nro.models.boss.mabu_war;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class BossMabuWar extends Boss {

    protected int mapID;

    protected int zoneId;

    public BossMabuWar(int id, BossData data) {
        super(id, data);
    }

    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null, null);
            return true;
        } else {
            return false;
        }
    }
      @Override
    public void rewards(Player pl)  {
        super.DoXungQuanh(pl, 457, Util.nextInt(1, 50), 5);// 1- 500 thỏi vàng
        TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
    }

    @Override
    public void initTalk() {

    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }
}
