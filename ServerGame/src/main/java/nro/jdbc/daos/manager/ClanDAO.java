package nro.jdbc.daos.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.server.Manager;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ClanDAO {

   public static void load(Connection con, List<Clan> clans) {
      String query = "SELECT * FROM clan_sv" + Manager.SERVER;
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            Clan clan = new Clan();
            clan.id = rs.getInt("id");
            clan.name = rs.getString("name");
            clan.slogan = rs.getString("slogan");
            clan.imgId = rs.getByte("img_id");
            clan.powerPoint = rs.getLong("power_point");
            clan.maxMember = rs.getByte("max_member");
            clan.clanPoint = rs.getInt("clan_point");
            clan.level = rs.getByte("level");
            clan.createTime = (int) (rs.getTimestamp("create_time").getTime() / 1000);

            String membersJson = rs.getString("members");
            if (membersJson != null && !membersJson.isEmpty()) {
               try {
                  JsonArray membersArray = JsonParser.parseString(membersJson).getAsJsonArray();
                  for (JsonElement element : membersArray) {
                     JsonObject memberObj = element.getAsJsonObject();
                     ClanMember cm = new ClanMember();
                     cm.clan = clan;
                     cm.id = memberObj.get("id").getAsInt();
                     cm.name = memberObj.get("name").getAsString();
                     cm.head = memberObj.get("head").getAsShort();
                     cm.body = memberObj.get("body").getAsShort();
                     cm.leg = memberObj.get("leg").getAsShort();
                     cm.role = memberObj.get("role").getAsByte();
                     cm.donate = memberObj.get("donate").getAsInt();
                     cm.receiveDonate = memberObj.get("receive_donate").getAsInt();
                     cm.memberPoint = memberObj.get("member_point").getAsInt();
                     cm.clanPoint = memberObj.get("clan_point").getAsInt();
                     cm.joinTime = memberObj.get("join_time").getAsInt();
                     cm.timeAskPea = memberObj.get("ask_pea_time").getAsLong();

                     if (memberObj.has("power")) {
                        cm.powerPoint = memberObj.get("power").getAsLong();
                     }
                     clan.addClanMember(cm);
                  }
               } catch (Exception e) {
                  Log.error("Error parsing members JSON for clan " + clan.name + ": " + e.getMessage());
               }
            }
            clans.add(clan);
         }

         initializeNextId(con);
         Log.success("Clans loaded successfully (" + clans.size() + "), next ID: " + Clan.NEXT_ID);

      } catch (Exception e) {
         Log.error("Error loading clan table: " + e.getMessage());
      }
   }

   private static void initializeNextId(Connection con) {
      String query = "SELECT id FROM clan_sv" + Manager.SERVER + " ORDER BY id DESC LIMIT 1";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
         if (rs.next()) {
            Clan.NEXT_ID = rs.getInt("id") + 1;
         }
      } catch (Exception e) {
         Log.error("Error initializing Clan NEXT_ID: " + e.getMessage());
      }
   }
}
