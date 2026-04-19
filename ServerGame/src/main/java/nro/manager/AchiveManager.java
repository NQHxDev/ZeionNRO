package nro.manager;

import lombok.Getter;
import nro.models.task.AchivementTemplate;

import java.util.ArrayList;
import java.util.List;

public class AchiveManager implements IManager<AchivementTemplate> {

   private static final AchiveManager INSTANCE = new AchiveManager();

   public static AchiveManager getInstance() {
      return INSTANCE;
   }

   @Getter
   public List<AchivementTemplate> list = new ArrayList<>();

   @Override
   public AchivementTemplate findByID(int id) {
      for (AchivementTemplate template : list) {
         if (template.id == id) {
            return template;
         }
      }
      return null;
   }

   @Override
   public void add(AchivementTemplate achivementTemplate) {

   }

   @Override
   public void remove(AchivementTemplate achivementTemplate) {

   }
}
