package nro.attr;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class AttributeTemplateManager {

   private static final AttributeTemplateManager instance = new AttributeTemplateManager();

   public static AttributeTemplateManager getInstance() {
      return instance;
   }

   @Getter
   public final List<AttributeTemplate> list = new ArrayList<>();

   public void add(AttributeTemplate at) {
      list.add(at);
   }

   public void remove(AttributeTemplate at) {
      list.remove(at);
   }

   public AttributeTemplate find(int id) {
      for (AttributeTemplate at : list) {
         if (at.id == id) {
            return at;
         }
      }
      return null;
   }

}
