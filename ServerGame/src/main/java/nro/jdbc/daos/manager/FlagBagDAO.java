package nro.jdbc.daos.manager;

import nro.models.item.FlagBag;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class FlagBagDAO {

   public static void load(Connection con, List<FlagBag> flagBags) {
      String query = "SELECT * FROM flag_bag";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            FlagBag flagBag = new FlagBag();
            flagBag.id = rs.getInt("id");
            flagBag.name = rs.getString("name");
            flagBag.gold = rs.getInt("gold");
            flagBag.gem = rs.getInt("gem");
            flagBag.iconId = rs.getShort("icon_id");

            String iconDataStr = rs.getString("icon_data");
            if (iconDataStr != null && !iconDataStr.isEmpty()) {
               String[] iconData = iconDataStr.split(",");
               flagBag.iconEffect = new short[iconData.length];
               for (int j = 0; j < iconData.length; j++) {
                  flagBag.iconEffect[j] = Short.parseShort(iconData[j].trim());
               }
            }
            flagBags.add(flagBag);
         }
         Log.success("Flag bags loaded successfully (" + flagBags.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading flag_bag table: " + e.getMessage());
      }
   }
}
