package nro.models.mob;

public class MobFactory {

   public static Mob newMob(Mob mob) {
      int templateID = mob.tempId;
      switch (templateID) {
         case 72:
            return new GuardRobot(mob);
         case 71:
            return new Octopus(mob);
         case 70:
            return new Hirudegarn(mob);
         default:
            return new Mob(mob);
      }
   }

}
