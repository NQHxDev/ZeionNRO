package nro.attr;

import nro.utils.Util;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class AttributeManager {

   @Getter
   public List<Attribute> attributes;
   private long lastUpdate;

   public AttributeManager() {
      this.attributes = new ArrayList<>();
   }

   public void add(Attribute at) {
      synchronized (attributes) {
         attributes.add(at);
      }
   }

   public void remove(Attribute at) {
      synchronized (attributes) {
         attributes.remove(at);
      }
   }

   public Attribute find(int templateID) {
      synchronized (attributes) {
         for (Attribute at : attributes) {
            if (at.template.id == templateID) {
               return at;
            }
         }
      }
      return null;
   }

   public void update() {
      if (Util.canDoWithTime(lastUpdate, 1000)) {
         lastUpdate = System.currentTimeMillis();
         synchronized (attributes) {
            for (Attribute at : attributes) {
               try {
                  if (!at.isExpired()) {
                     at.update();
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         }
      }
   }

   public boolean setTime(int templateID, int time) {
      Attribute attr = find(templateID);
      if (attr != null) {
         attr.setTime(time);
      }
      return false;
   }

}
