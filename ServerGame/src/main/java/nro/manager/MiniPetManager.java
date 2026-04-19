package nro.manager;

import nro.models.item.MinipetTemplate;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MiniPetManager implements IManager<MinipetTemplate> {

   private static final MiniPetManager INSTANCE = new MiniPetManager();

   @Getter
   public List<MinipetTemplate> list = new ArrayList<>();

   public static MiniPetManager gI() {
      return INSTANCE;
   }

   @Override
   public void add(MinipetTemplate minipetTemplate) {
      list.add(minipetTemplate);
   }

   @Override
   public void remove(MinipetTemplate minipetTemplate) {
      list.remove(minipetTemplate);
   }

   @Override
   public MinipetTemplate findByID(int id) {
      for (MinipetTemplate temp : list) {
         if (temp.id == id) {
            return temp;
         }
      }
      return null;
   }

}
