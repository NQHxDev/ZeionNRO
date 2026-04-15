/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.models.boss.cdrd;

import nro.consts.ConstItem;
import nro.lib.RandomCollection;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.map.ItemMap;
import nro.models.map.dungeon.SnakeRoad;
import nro.models.player.Player;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class Nadic extends CBoss {

    public Nadic(long id, short x, short y, SnakeRoad dungeon, BossData data) {
        super(id, x, y, dungeon, data);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
    }


    @Override
    public void idle() {
        
    }

    @Override
    public void checkPlayerDie(Player pl) {
        
    }

    @Override
    public void changeToAttack() {
        chat("Ha ha ha");
        super.changeToAttack(); 
    }
    
    

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{"Ốp la...Xay da da!"};
        this.textTalkAfter = new String[]{"Sếp hãy giết nó, trả thù cho em!"};
    }
    
}
