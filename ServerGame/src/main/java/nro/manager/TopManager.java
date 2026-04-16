package nro.manager;

import lombok.Getter;
import nro.jdbc.DBService;
import nro.jdbc.daos.manager.TopDAO;
import nro.models.player.Player;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class TopManager {

   @Getter
   private List<Player> listSm = new ArrayList<>();
   @Getter
   private List<Player> listDetu = new ArrayList<>();
   @Getter
   private List<Player> listNvu = new ArrayList<>();
   @Getter
   private List<Player> listNap = new ArrayList<>();
   @Getter
   private List<Player> listSieuHang = new ArrayList<>();

   private static final TopManager INSTANCE = new TopManager();

   public static TopManager getInstance() {
      return INSTANCE;
   }

   public void load() {
      try (Connection con = DBService.gI().getConnectionForGetPlayer()) {
         listSm = TopDAO.loadTopPower(con);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void load1() {
      try (Connection con = DBService.gI().getConnectionForGetPlayer()) {
         listDetu = TopDAO.loadTopPet(con);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void load2() {
      try (Connection con = DBService.gI().getConnectionForGetPlayer()) {
         listNvu = TopDAO.loadTopTask(con);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void load3() {
      try (Connection con = DBService.gI().getConnectionForGetPlayer()) {
         listNap = TopDAO.loadTopDonate(con);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void loadSieuHang() {
      try (Connection con = DBService.gI().getConnectionForGetPlayer()) {
         listSieuHang = TopDAO.loadTopSieuHang(con);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   // Descriptive aliases for future-proofing
   public void loadTopPower() { load(); }
   public void loadTopPet() { load1(); }
   public void loadTopTask() { load2(); }
   public void loadTopDonate() { load3(); }
}
