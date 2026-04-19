package nro.models.player;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerEvent {

   public boolean isUseQuanHoa;
   public boolean isUseBonTam;
   public int diemTichLuy;
   public int mocNapDaNhan;
   public int eventPoint;
   public Player player;
   public boolean cookingChungCake;
   public int timeCookChungCake;
   public boolean cookingTetCake;
   public int timeCookTetCake;
   public boolean receivedLuckyMoney;

   public PlayerEvent(Player player) {
      this.player = player;
   }

   public void setTimeCookChungCake(int sec) {
      timeCookChungCake += sec;
   }

   public void setTimeCookTetCake(int sec) {
      timeCookTetCake += sec;
   }

   public void addEventPoint(int num) {
      eventPoint += num;
   }

   public void subEventPoint(int num) {
      eventPoint -= num;
   }

   public void update() {
      if (timeCookChungCake > 0) {
         timeCookChungCake--;
      }
      if (timeCookTetCake > 0) {
         timeCookTetCake--;
      }
   }

}
