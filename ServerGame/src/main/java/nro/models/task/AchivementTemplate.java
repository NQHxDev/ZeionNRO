package nro.models.task;

public class AchivementTemplate {

   public int id;

   public String name;

   public String detail;

   public int money;

   public int maxCount;

   public AchivementTemplate() {
   }

   public AchivementTemplate(int id, String name, String detail, int money, int maxCount) {
      this.id = id;
      this.name = name;
      this.detail = detail;
      this.money = money;
      this.maxCount = maxCount;
   }

}
