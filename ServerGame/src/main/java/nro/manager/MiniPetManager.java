package nro.manager;

import nro.jdbc.DBService;
import nro.models.item.MinipetTemplate;

import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MiniPetManager implements IManager<MinipetTemplate> {

   private static final MiniPetManager INSTANCE = new MiniPetManager();

   @Getter
   private List<MinipetTemplate> list = new ArrayList<>();

   public static MiniPetManager gI() {
      return INSTANCE;
   }

   public void load() {
      try {
         PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT * FROM mini_pet");
         ResultSet rs = ps.executeQuery();
         while (rs.next()) {
            int id = rs.getInt("id_temp");
            short head = rs.getShort("head");
            short body = rs.getShort("body");
            short leg = rs.getShort("leg");
            add(new MinipetTemplate(id, head, body, leg));
         }
         ps.close();
         rs.close();
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
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
         if (temp.getId() == id) {
            return temp;
         }
      }
      return null;
   }
}
