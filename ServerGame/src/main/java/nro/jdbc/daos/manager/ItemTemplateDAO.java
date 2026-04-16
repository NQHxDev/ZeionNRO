package nro.jdbc.daos.manager;

import nro.models.item.ItemOptionTemplate;
import nro.models.item.ItemTemplate;
import nro.server.Manager;
import nro.services.ItemService;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ItemTemplateDAO {

   public static void load(Connection con, List<ItemTemplate> itemTemplates, List<ItemOptionTemplate> optionTemplates) {
      loadItemTemplates(con, itemTemplates);
      loadItemOptionTemplates(con, optionTemplates);
      initializeSpecialItems();
   }

   private static void loadItemTemplates(Connection con, List<ItemTemplate> templates) {
      String query = "SELECT * FROM item_template";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            ItemTemplate itemTemp = new ItemTemplate();
            itemTemp.id = rs.getShort("id");
            itemTemp.type = rs.getByte("type");
            itemTemp.gender = rs.getByte("gender");
            itemTemp.name = rs.getString("name");
            itemTemp.description = rs.getString("description");
            itemTemp.iconID = rs.getShort("icon_id");
            itemTemp.part = rs.getShort("part");
            itemTemp.isUpToUp = rs.getBoolean("is_up_to_up");
            itemTemp.strRequire = rs.getInt("power_require");
            templates.add(itemTemp);
         }
         Log.success("Item templates loaded successfully (" + templates.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading item_template table: " + e.getMessage());
      }
   }

   private static void loadItemOptionTemplates(Connection con, List<ItemOptionTemplate> templates) {
      String query = "SELECT id, name FROM item_option_template";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            ItemOptionTemplate optionTemp = new ItemOptionTemplate();
            optionTemp.id = rs.getInt("id");
            optionTemp.name = rs.getString("name");
            templates.add(optionTemp);
         }
         Log.success("Item option templates loaded successfully (" + templates.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading item_option_template table: " + e.getMessage());
      }
   }

   private static void initializeSpecialItems() {
      try {
         for (int a = 0; a < (Manager.CT_BOT.length); a++) {
            Manager.CT.add(ItemService.gI().createNewItem((short) Manager.CT_BOT[a]));
         }
         for (int b = 0; b < (Manager.FLAG_BOT.length); b++) {
            Manager.FLAG.add(ItemService.gI().createNewItem((short) Manager.FLAG_BOT[b]));
         }
         Log.success("Special bot items initialized successfully");
      } catch (Exception e) {
         Log.error("Error initializing special items: " + e.getMessage());
      }
   }
}
