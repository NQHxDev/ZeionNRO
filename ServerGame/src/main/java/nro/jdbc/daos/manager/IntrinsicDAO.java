package nro.jdbc.daos.manager;

import nro.consts.ConstPlayer;
import nro.models.intrinsic.Intrinsic;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class IntrinsicDAO {

   public static void load(Connection con, List<Intrinsic> allIntrinsics, List<Intrinsic> td, List<Intrinsic> nm,
         List<Intrinsic> xd) {
      String query = "SELECT * FROM intrinsic";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            Intrinsic intrinsic = new Intrinsic();
            intrinsic.id = rs.getByte("id");
            intrinsic.name = rs.getString("name");
            intrinsic.paramFrom1 = rs.getShort("param_from_1");
            intrinsic.paramTo1 = rs.getShort("param_to_1");
            intrinsic.paramFrom2 = rs.getShort("param_from_2");
            intrinsic.paramTo2 = rs.getShort("param_to_2");
            intrinsic.icon = rs.getShort("icon");
            intrinsic.gender = rs.getByte("gender");

            switch (intrinsic.gender) {
               case ConstPlayer.TRAI_DAT:
                  td.add(intrinsic);
                  break;
               case ConstPlayer.NAMEC:
                  nm.add(intrinsic);
                  break;
               case ConstPlayer.XAYDA:
                  xd.add(intrinsic);
                  break;
               default:
                  td.add(intrinsic);
                  nm.add(intrinsic);
                  xd.add(intrinsic);
                  break;
            }
            allIntrinsics.add(intrinsic);
         }
         Log.success("Intrinsics loaded successfully (" + allIntrinsics.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading intrinsic table: " + e.getMessage());
      }
   }
}
