package nro.jdbc.daos.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nro.models.map.EffectMap;
import nro.models.map.MapTemplate;
import nro.models.map.WayPoint;
import nro.server.Manager;
import nro.utils.Log;

import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

public class MapTemplateDAO {

   public static Map<Integer, MapTemplate> load(Connection con) {
      Map<Integer, MapTemplate> mapTemplates = new HashMap<>();
      try {
         PreparedStatement ps = con.prepareStatement("select * from map_template");
         ResultSet rs = ps.executeQuery();
         while (rs.next()) {
            MapTemplate mapTemplate = new MapTemplate();
               int mapId = rs.getInt("id");
               String mapName = rs.getString("name");
               mapTemplate.id = mapId;
               mapTemplate.name = mapName;

               // load data
               JsonArray dataArray = parseMapData(rs.getString("data"));
               if (dataArray.size() > 0 && dataArray.get(0).isJsonArray()) {
                  dataArray = dataArray.get(0).getAsJsonArray();
               }
               if (dataArray.size() >= 5) {
                  mapTemplate.type = dataArray.get(0).getAsByte();
                  mapTemplate.planetId = dataArray.get(1).getAsByte();
                  mapTemplate.bgType = dataArray.get(2).getAsByte();
                  mapTemplate.tileId = dataArray.get(3).getAsByte();
                  mapTemplate.bgId = dataArray.get(4).getAsByte();
               }

               mapTemplate.zones = rs.getByte("zones");
               mapTemplate.maxPlayerPerZone = rs.getByte("max_player");

               // load waypoints
               JsonArray waypointsArray = parseMapData(rs.getString("waypoints"));
               for (int j = 0; j < waypointsArray.size(); j++) {
                  WayPoint wp = new WayPoint();
                  JsonArray dtwp = waypointsArray.get(j).getAsJsonArray();
                  if (dtwp.size() >= 10) {
                     wp.name = dtwp.get(0).getAsString();
                     wp.minX = dtwp.get(1).getAsShort();
                     wp.minY = dtwp.get(2).getAsShort();
                     wp.maxX = dtwp.get(3).getAsShort();
                     wp.maxY = dtwp.get(4).getAsShort();
                     wp.isEnter = dtwp.get(5).getAsByte() == 1;
                     wp.isOffline = dtwp.get(6).getAsByte() == 1;
                     wp.goMap = dtwp.get(7).getAsShort();
                     wp.goX = dtwp.get(8).getAsShort();
                     wp.goY = dtwp.get(9).getAsShort();
                     mapTemplate.wayPoints.add(wp);
                  }
               }

               // load mobs
               JsonArray mobsArray = parseMapData(rs.getString("mobs"));
               mapTemplate.mobTemp = new byte[mobsArray.size()];
               mapTemplate.mobLevel = new byte[mobsArray.size()];
               mapTemplate.mobHp = new double[mobsArray.size()];
               mapTemplate.mobX = new short[mobsArray.size()];
               mapTemplate.mobY = new short[mobsArray.size()];
               for (int j = 0; j < mobsArray.size(); j++) {
                  JsonArray dtm = mobsArray.get(j).getAsJsonArray();
                  if (dtm.size() >= 5) {
                     mapTemplate.mobTemp[j] = dtm.get(0).getAsByte();
                     mapTemplate.mobLevel[j] = dtm.get(1).getAsByte();
                     mapTemplate.mobHp[j] = dtm.get(2).getAsDouble();
                     mapTemplate.mobX[j] = dtm.get(3).getAsShort();
                     mapTemplate.mobY[j] = dtm.get(4).getAsShort();
                  }
               }

               // load npc
               String npcsStr = rs.getString("npcs");
               JsonArray npcsArray = parseMapData(npcsStr);

               mapTemplate.npcId = new byte[npcsArray.size()];
               mapTemplate.npcX = new short[npcsArray.size()];
               mapTemplate.npcY = new short[npcsArray.size()];
               mapTemplate.npcAvatar = new short[npcsArray.size()];
               for (int j = 0; j < npcsArray.size(); j++) {
                  try {
                     JsonArray dtn = npcsArray.get(j).getAsJsonArray();
                     if (dtn.size() >= 4) {
                        mapTemplate.npcId[j] = dtn.get(0).getAsByte();
                        mapTemplate.npcX[j] = dtn.get(1).getAsShort();
                        mapTemplate.npcY[j] = dtn.get(2).getAsShort();
                        mapTemplate.npcAvatar[j] = dtn.get(3).getAsShort();
                     }
                  } catch (Exception e) {
                     Log.error(MapTemplateDAO.class, e, "Error parsing NPC index " + j + " in map " + mapId);
                  }
               }

               // load effect
               JsonArray effectArray = parseMapData(rs.getString("effect"));
               if (effectArray.size() == 1 && effectArray.get(0).isJsonArray()) {
                  effectArray = effectArray.get(0).getAsJsonArray();
               }
               for (int j = 0; j < effectArray.size(); j++) {
                  EffectMap em = new EffectMap();
                  try {
                     JsonObject dataObject = effectArray.get(j).getAsJsonObject();
                     em.key = dataObject.get("key").getAsString();
                     em.value = dataObject.get("value").getAsString();
                     mapTemplate.effectMaps.add(em);
                  } catch (Exception ex) {
                     // If it's not a JsonObject, it might be a malformed string or other format
                  }
               }
               if (Manager.EVENT_SEVER == 3) {
                  EffectMap em = new EffectMap();
                  em.key = "beff";
                  em.value = "11";
                  mapTemplate.effectMaps.add(em);
               }
               mapTemplates.put(mapTemplate.id, mapTemplate);
            }
            Log.success("Map templates loaded successfully (" + mapTemplates.size() + ")");
            rs.close();
            ps.close();
         } catch (Exception e) {
         Log.error(MapTemplateDAO.class, e, "Error loading map templates");
      }
      return mapTemplates;
   }

   private static JsonArray parseMapData(String json) {
      if (json == null || json.isEmpty()) {
         return new JsonArray();
      }

      JsonArray result = new JsonArray();
      // Use regex to find all inner array patterns: [...]
      // This is the most robust way for the mixed unescaped format found in this DB.
      java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\[([^\\[\\]]*)\\]");
      java.util.regex.Matcher m = p.matcher(json);

      boolean foundRegex = false;
      while (m.find()) {
         foundRegex = true;
         String content = m.group(1);
         JsonArray innerArray = new JsonArray();
         // Simple comma split
         String[] parts = content.split(",");
         for (String part : parts) {
            String pTrim = part.trim();
            if (pTrim.startsWith("\"") && pTrim.endsWith("\"") && pTrim.length() > 1) {
               innerArray.add(pTrim.substring(1, pTrim.length() - 1));
            } else if (pTrim.startsWith("'") && pTrim.endsWith("'") && pTrim.length() > 1) {
               innerArray.add(pTrim.substring(1, pTrim.length() - 1));
            } else {
               try {
                  if (pTrim.contains(".")) {
                     innerArray.add(Double.parseDouble(pTrim));
                  } else {
                     innerArray.add(Long.parseLong(pTrim));
                  }
               } catch (Exception nfe) {
                  innerArray.add(pTrim.replace("\"", ""));
               }
            }
         }
         result.add(innerArray);
      }

      // If regex found nothing, try standard GSON (e.g. for simple effect objects or
      // well-formed JSON)
      if (!foundRegex) {
         try {
            JsonElement element = JsonParser.parseString(json);
            if (element.isJsonArray()) {
               return element.getAsJsonArray();
            } else {
               result.add(element);
            }
         } catch (Exception e) {
            // Last resort: if it's not even a single object, wrap it as a string
            result.add(json.replace("\"", ""));
         }
      }

      return result;
   }
}
