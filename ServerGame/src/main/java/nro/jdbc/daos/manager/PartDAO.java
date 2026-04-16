package nro.jdbc.daos.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nro.models.PartManager.PartDetail;
import nro.models.PartManager.PartPot;
import nro.utils.Log;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PartDAO {

   public static void load(Connection con) {
      String query = "SELECT * FROM part";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         List<PartPot> parts = new ArrayList<>();
         while (rs.next()) {
            PartPot part = new PartPot();
            part.id = rs.getShort("id");
            part.type = rs.getByte("type");

            String dataStr = rs.getString("data");
            if (dataStr != null && !dataStr.isEmpty()) {
               try {
                  // Using GSON for modern and safe parsing
                  // Replaces the old JSONValue.parse + replaceAll logic
                  JsonArray dataArray = JsonParser.parseString(dataStr).getAsJsonArray();

                  for (JsonElement element : dataArray) {
                     JsonArray pd = element.getAsJsonArray();
                     part.partDetails.add(new PartDetail(
                           pd.get(0).getAsShort(),
                           pd.get(1).getAsByte(),
                           pd.get(2).getAsByte()));
                  }
               } catch (Exception e) {
                  Log.error("Error parsing part JSON ID " + part.id + ": " + e.getMessage());
               }
            }
            parts.add(part);
         }

         // Write to binary file
         try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/part/part"))) {
            dos.writeShort(parts.size());
            for (PartPot part : parts) {
               dos.writeByte(part.type);
               for (PartDetail partDetail : part.partDetails) {
                  dos.writeShort(partDetail.iconId);
                  dos.writeByte(partDetail.dx);
                  dos.writeByte(partDetail.dy);
               }
            }
            dos.flush();
         }

         Log.success("Part loaded successfully (" + parts.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading part table: " + e.getMessage());
      }
   }
}
