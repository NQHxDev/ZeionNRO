package nro.manager;

import nro.jdbc.DBService;
import nro.jdbc.daos.manager.ServiceDataDAO;
import java.sql.Connection;

public class ConsignManager {

   private static final ConsignManager INSTANCE = new ConsignManager();

   public static ConsignManager getInstance() {
      return INSTANCE;
   }

   public void load() {
      try (Connection con = DBService.gI().getConnection()) {
         ServiceDataDAO.loadConsignmentItems(con);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void close() {
      try (Connection con = DBService.gI().getConnection()) {
         ServiceDataDAO.saveConsignmentItems(con);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
