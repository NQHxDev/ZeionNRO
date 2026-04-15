package nro.models.player;

import java.util.ArrayList;
import java.util.List;
import nro.models.item.Item;
import nro.models.item.ItemOption;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Inventory {

    public static final long LIMIT_GOLD = 10000000000L;

    private Player player;

    public Item trainArmor;

    public List<Item> itemsBody;
    public List<Item> itemsBag;
    public List<Item> itemsBox;

    public List<Item> itemsBoxCrackBall;
//    public List<Item> itemsReward;

    public long gold, goldLimit;
    public int gem;
    public int ruby;

//    public int goldBar;
    public Inventory(Player player) {
        this.player = player;
        itemsBody = new ArrayList<>();
        itemsBag = new ArrayList<>();
        itemsBox = new ArrayList<>();
        itemsBoxCrackBall = new ArrayList<>();
//        itemsReward = new ArrayList<>();
    }


    public int getGem() {
        return this.gem;
    }

    public long getGold() {
        return this.gold;
    }

    public long getGoldLimit() {
        return goldLimit + LIMIT_GOLD;
    }

    public long getGoldDisplay() {
        long amount = gold;
        if (amount > Integer.MAX_VALUE && !player.isVersionAbove(214)) {
            return Integer.MAX_VALUE;
        }
        return amount;
    }
    
    public int getParam(Item it , int id){
        for(ItemOption op : it.itemOptions){
            if(op!=null&&op.optionTemplate.id ==id){
                return op.param;
            }
        }
        return 0;
    }
    
    public boolean haveOption(List<Item> l , int index , int id){
        Item it = l.get(index);
        if(it != null && it.isNotNullItem()){
            return it.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == id);
        }
        return false;
    }

    public long getRuby() {
        return this.ruby;
    }

    public void subGem(int num) {
        this.gem -= num;
    }

    public void subGold(int num) {
        this.gold -= num;
    }

    public void subRuby(int num) {
        this.ruby -= num;
    }

    public void addGold(int gold) {
        this.gold += gold;
        long goldLimit = getGoldLimit();
        if (this.gold > goldLimit) {
            this.gold = goldLimit;
        }
    }

    public void dispose() {
        this.player = null;
        if (this.trainArmor != null) {
            this.trainArmor.dispose();
        }
        this.trainArmor = null;
        if (this.itemsBody != null) {
            for (Item it : this.itemsBody) {
                it.dispose();
            }
            this.itemsBody.clear();
        }
        if (this.itemsBag != null) {
            for (Item it : this.itemsBag) {
                it.dispose();
            }
            this.itemsBag.clear();
        }
        if (this.itemsBox != null) {
            for (Item it : this.itemsBox) {
                it.dispose();
            }
            this.itemsBox.clear();
        }
        if (this.itemsBoxCrackBall != null) {
            for (Item it : this.itemsBoxCrackBall) {
                it.dispose();
            }
            this.itemsBoxCrackBall.clear();
        }
        this.itemsBody = null;
        this.itemsBag = null;
        this.itemsBox = null;
        this.itemsBoxCrackBall = null;
    }
}
