package nro.models.player;

import lombok.Getter;

public class PetFollow {

   @Getter
   public int id;

   @Getter
   public int iconID;

   @Getter
   public int width;

   @Getter
   public int height;

   @Getter
   public byte nFrame;

   public PetFollow(int id, int iconID, int width, int height, byte nFrame) {
      this.id = id;
      this.iconID = iconID;
      this.width = width;
      this.height = height;
      this.nFrame = nFrame;
   }

}
