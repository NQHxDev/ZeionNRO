package nro.resources.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Frame {

   @SerializedName("sprite_id")
   public int spriteID;

   public int dx;

   public int dy;

}
