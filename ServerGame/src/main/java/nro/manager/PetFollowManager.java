package nro.manager;

import nro.models.player.PetFollow;

public class PetFollowManager extends AbsManager<PetFollow> {

   private static final PetFollowManager INSTANCE = new PetFollowManager();

   public static PetFollowManager gI() {
      return INSTANCE;
   }

   @Override
   public PetFollow findByID(int id) {
      for (PetFollow pet : list) {
         if (pet.id == id) {
            return pet;
         }
      }
      return null;
   }
}
