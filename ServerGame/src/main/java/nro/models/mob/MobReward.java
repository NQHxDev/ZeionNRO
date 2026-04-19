package nro.models.mob;

import nro.models.item.ItemReward;

import java.util.ArrayList;
import java.util.List;

public class MobReward {

   public int tempId;
   public List<ItemReward> itemRewards;
   public List<ItemReward> goldRewards;
   public List<ItemReward> capsuleKyBi;
   public List<ItemReward> foods;
   public List<ItemReward> biKieps;

   public MobReward() {
      this.itemRewards = new ArrayList<>();
      this.goldRewards = new ArrayList<>();
      this.capsuleKyBi = new ArrayList<>();
      this.foods = new ArrayList<>();
      this.biKieps = new ArrayList<>();
   }

}
