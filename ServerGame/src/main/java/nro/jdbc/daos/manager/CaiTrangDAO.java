package nro.jdbc.daos.manager;

import nro.models.item.CaiTrang;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class CaiTrangDAO {

   public static void load(Connection con, List<CaiTrang> caiTrangs) {
      String query = "SELECT * FROM cai_trang";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            CaiTrang caiTrang = new CaiTrang(
                  rs.getInt("id_temp"),
                  rs.getInt("head"),
                  rs.getInt("body"),
                  rs.getInt("leg"),
                  rs.getInt("bag"));
            caiTrangs.add(caiTrang);
         }
         Log.success("Costumes (Cai Trang) loaded successfully (" + caiTrangs.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading cai_trang table: " + e.getMessage());
      }
   }
}
