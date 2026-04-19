package nro.card;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class Card {

   @SerializedName("id")
   @Setter
   public int id;

   @SerializedName("amount")
   public int amount;

   @Getter
   @SerializedName("level")
   public int level;

   @Getter
   @SerializedName("use")
   public boolean isUse;

   @Getter
   public transient CardTemplate cardTemplate;

   public void addAmount(int amount) {
      this.amount += amount;
      if (this.amount >= cardTemplate.maxAmount) {
         levelUp();
      }
   }

   private void levelUp() {
      this.amount = 0;
      this.level++;
   }

   public void setTemplate() {
      cardTemplate = CardManager.getInstance().find(id);
   }

}
