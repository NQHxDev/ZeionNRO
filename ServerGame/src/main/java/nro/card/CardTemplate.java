package nro.card;

import nro.models.item.ItemOption;
import java.util.ArrayList;

import lombok.Getter;

public class CardTemplate {

   public int id;

   @Getter
   public int itemID;

   public byte rank;

   public byte maxAmount;

   public byte type;

   public int icon;

   public String name;

   public String info;

   @Getter
   public short mobID;

   public short head;

   public short body;

   public short leg;

   public short bag;

   @Getter
   public short aura;

   @Getter
   public ArrayList<ItemOption> options;

   public CardTemplate() {
      this.options = new ArrayList<>();
   }

   public CardTemplate(int id, int itemID, byte rank, byte maxAmount, byte type, int icon, String name, String info,
         short mobID, short head, short body, short leg, short bag, short aura, ArrayList<ItemOption> options) {
      this.id = id;
      this.itemID = itemID;
      this.rank = rank;
      this.maxAmount = maxAmount;
      this.type = type;
      this.icon = icon;
      this.name = name;
      this.info = info;
      this.mobID = mobID;
      this.head = head;
      this.body = body;
      this.leg = leg;
      this.bag = bag;
      this.aura = aura;
      this.options = options;
   }
}
