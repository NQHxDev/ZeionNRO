package nro.data.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

   private int id;

   private String username;

   private String password;

   private int serverLogin;

   private boolean isAdmin;

   private boolean active;

   private int goldBar;

   private Timestamp lastTimeLogin;

   private Timestamp lastTimeLogout;

   private String reward;

   private int ruby;

   private int countCard;

   private boolean ban;

}
