package nro.data;

import nro.consts.Cmd;
import nro.models.item.HeadAvatar;
import nro.models.map.MapTemplate;
import nro.models.mob.MobTemplate;
import nro.models.npc.NpcTemplate;
import nro.models.skill.NClass;
import nro.models.skill.Skill;
import nro.models.skill.SkillTemplate;
import nro.power.Caption;
import nro.power.CaptionManager;
import nro.resources.Resources;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.network.io.Message;
import nro.server.io.Session;
import nro.services.Service;
import nro.utils.FileIO;
import nro.utils.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataGame {

   // public static byte vsData = 47;
   // public static byte vsMap = 25;
   // public static byte vsSkill = 5;
   // public static byte vsItem = 90;
   public static byte vsData = 6;
   public static byte vsMap = 5;
   public static byte vsSkill = 5;
   public static byte vsItem = 6;

   public static String LINK_IP_PORT = "ServerGame:gatewayservergame.nroacademy.online:14445:0";
   private static final String MOUNT_NUM = "733:1,734:2,735:3,743:4,744:5,746:6,795:7,849:8,897:9,920:10,1092:11,1135:12,1148:13,1176:14";
   public static final Map<String, Short> MAP_MOUNT_NUM = new HashMap<>();

   private static byte[] readFileWithLog(String path) {
      Log.log("DataGame: [INFO] Đang nạp " + path);
      return FileIO.readFile(path);
   }

   private static final byte[] dart = readFileWithLog("resources/data/nro/update_data/dart");
   private static final byte[] arrow = readFileWithLog("resources/data/nro/update_data/arrow");
   private static final byte[] effect = readFileWithLog("resources/data/nro/update_data/effect");
   private static final byte[] image = readFileWithLog("resources/data/nro/update_data/image");
   private static final byte[] skill = readFileWithLog("resources/data/nro/update_data/skill");

   static {
      Log.log("DataGame: [INFO] Đang khởi tạo static block...");
      String[] array = MOUNT_NUM.split(",");
      for (String str : array) {
         String[] data = str.split(":");
         short num = (short) (Short.parseShort(data[1]) + 30000);
         MAP_MOUNT_NUM.put(data[0], num);
      }
      Log.log("DataGame: [INFO] Đang khởi tạo Resources...");
      Resources.gI().init();
      Log.log("DataGame: [INFO] Khởi tạo static block hoàn tất.");
   }

   public static void sendVersionGame(Session session) {
      Message msg;
      try {
         msg = Service.getInstance().messageNotMap((byte) 4);
         msg.writer().writeByte(vsData);
         msg.writer().writeByte(vsMap);
         msg.writer().writeByte(vsSkill);
         msg.writer().writeByte(vsItem);
         msg.writer().writeByte(0);
         List<Caption> captions = CaptionManager.getInstance().getCaptions();
         msg.writer().writeByte(captions != null ? captions.size() : 0);
         if (captions != null) {
            for (Caption caption : captions) {
               msg.writer().writeLong((long) caption.getPower());
            }
         }
         session.sendMessage(msg);
      } catch (Exception e) {
         Log.error(DataGame.class, e);
      }
   }

   // vcData
   public static void updateData(Session session) {
      byte[] part = FileIO.readFile("data/part/part");
      Message msg;
      try {
         msg = Message.create(-87);
         msg.writer().writeByte(vsData);
         msg.writer().writeInt(dart != null ? dart.length : 0);
         if (dart != null) msg.writer().write(dart);
         msg.writer().writeInt(arrow != null ? arrow.length : 0);
         if (arrow != null) msg.writer().write(arrow);
         msg.writer().writeInt(effect != null ? effect.length : 0);
         if (effect != null) msg.writer().write(effect);
         msg.writer().writeInt(image != null ? image.length : 0);
         if (image != null) msg.writer().write(image);
         msg.writer().writeInt(part != null ? part.length : 0);
         if (part != null) msg.writer().write(part);
         msg.writer().writeInt(skill != null ? skill.length : 0);
         if (skill != null) msg.writer().write(skill);

         session.doSendMessage(msg);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   // vcMap
   public static void createMap(Session session) {
      Message msg;
      try {
         msg = Service.getInstance().messageNotMap((byte) 6);
         msg.writer().writeByte(vsMap);
         msg.writer().writeByte(Manager.MAP_TEMPLATES.size());
         for (MapTemplate temp : Manager.MAP_TEMPLATES.values()) {
            msg.writer().writeUTF(temp.name);
         }
         msg.writer().writeByte(Manager.NPC_TEMPLATES.size());
         for (NpcTemplate temp : Manager.NPC_TEMPLATES.values()) {
            msg.writer().writeUTF(temp.name);
            msg.writer().writeShort(temp.head);
            msg.writer().writeShort(temp.body);
            msg.writer().writeShort(temp.leg);
            msg.writer().writeByte(0);
         }
         msg.writer().writeByte(Manager.MOB_TEMPLATES.size());
         for (MobTemplate temp : Manager.MOB_TEMPLATES.values()) {
            msg.writer().writeByte(temp.type);
            msg.writer().writeUTF(temp.name);
            msg.writer().writeDouble(temp.hp);
            msg.writer().writeByte(temp.rangeMove);
            msg.writer().writeByte(temp.speed);
            msg.writer().writeByte(temp.dartType);
         }
         session.sendMessage(msg);
      } catch (Exception e) {
         Log.error(DataGame.class, e);
      }
   }

   // vcSkill
   public static void updateSkill(Session session) {
      Message msg;
      try {
         msg = Message.create(-28);
         // msg.writer().write(FileIO.readFile("data/1632811838545_-28_7_r"));

         msg.writer().writeByte(7);
         msg.writer().writeByte(vsSkill);
         msg.writer().writeByte(0); // count skill option

         msg.writer().writeByte(Manager.NCLASS.size());
         for (NClass nClass : Manager.NCLASS) {
            msg.writer().writeUTF(nClass.name);

            msg.writer().writeByte(nClass.skillTemplatess.size());
            for (SkillTemplate skillTemp : nClass.skillTemplatess) {
               msg.writer().writeByte(skillTemp.id);
               msg.writer().writeUTF(skillTemp.name);
               msg.writer().writeByte(skillTemp.maxPoint);
               msg.writer().writeByte(skillTemp.manaUseType);
               msg.writer().writeByte(skillTemp.type);
               msg.writer().writeShort(skillTemp.iconId);
               msg.writer().writeUTF(skillTemp.damInfo);
               msg.writer().writeUTF(skillTemp.description);
               if (skillTemp.id != 0) {
                  msg.writer().writeByte(skillTemp.skillss.size());
                  for (Skill skill : skillTemp.skillss) {
                     msg.writer().writeShort(skill.skillId);
                     msg.writer().writeByte(skill.point);
                     msg.writer().writeLong(skill.powRequire);
                     msg.writer().writeShort(skill.manaUse);
                     if (skill.skillId == 1) {
                        msg.writer().writeInt(5000);
                     } else {
                        msg.writer().writeInt(skill.coolDown);
                     }
                     msg.writer().writeShort(skill.dx);
                     msg.writer().writeShort(skill.dy);
                     msg.writer().writeByte(skill.maxFight);
                     msg.writer().writeShort(skill.damage);
                     msg.writer().writeShort(skill.price);
                     msg.writer().writeUTF(skill.moreInfo);
                  }
               } else {
                  // Thêm 2 skill trống 105, 106
                  msg.writer().writeByte(skillTemp.skillss.size() + 2);
                  for (Skill skill : skillTemp.skillss) {
                     msg.writer().writeShort(skill.skillId);
                     msg.writer().writeByte(skill.point);
                     msg.writer().writeLong(skill.powRequire);
                     msg.writer().writeShort(skill.manaUse);
                     if (skill.skillId == 1) {
                        msg.writer().writeInt(5000);
                     } else {
                        msg.writer().writeInt(skill.coolDown);
                     }
                     msg.writer().writeShort(skill.dx);
                     msg.writer().writeShort(skill.dy);
                     msg.writer().writeByte(skill.maxFight);
                     msg.writer().writeShort(skill.damage);
                     msg.writer().writeShort(skill.price);
                     msg.writer().writeUTF(skill.moreInfo);
                  }
                  for (int i = 105; i <= 106; i++) {
                     msg.writer().writeShort(i);
                     msg.writer().writeByte(0);
                     msg.writer().writeLong(0);
                     msg.writer().writeShort(0);
                     msg.writer().writeInt(0);
                     msg.writer().writeShort(0);
                     msg.writer().writeShort(0);
                     msg.writer().writeByte(0);
                     msg.writer().writeShort(0);
                     msg.writer().writeShort(0);
                     msg.writer().writeUTF("");
                  }
               }
            }
         }
         session.doSendMessage(msg);
      } catch (Exception e) {
         Log.error(DataGame.class, e);
      }
   }

   public static void sendDataImageVersion(Session session) {
      Message msg;
      try {
         msg = Message.create(-111);
         msg.writer()
               .write(FileIO.readFile("resources/data/nro/data_img_version/x" + session.zoomLevel + "/img_version"));
         session.doSendMessage(msg);
      } catch (Exception e) {
         Log.error(DataGame.class, e);
      }
   }

   public static void sendDataItemBG(Session session) {
      Message msg;
      try {
         byte[] item_bg = FileIO.readFile("resources/bg_data");
         msg = Message.create(Cmd.ITEM_BACKGROUND);
         msg.writer().write(item_bg);
         session.sendMessage(msg);
      } catch (Exception e) {
      }
   }

   public static void sendTileSetInfo(Session session) {
      Message msg;
      try {
         msg = Message.create(-82);
         msg.writer().write(FileIO.readFile("resources/data/nro/map/tile_set_info"));
         session.sendMessage(msg);
      } catch (Exception e) {
      }
   }

   // data vẽ map
   public static void sendMapTemp(Session session, int id) {
      Message msg;
      try {
         msg = Service.getInstance().messageNotMap(Cmd.REQUEST_MAPTEMPLATE);
         msg.writer().write(FileIO.readFile("resources/map/" + id));
         session.sendMessage(msg);
      } catch (Exception e) {
         Log.error(DataGame.class, e);
      }
   }

   // head-avatar
   public static void sendHeadAvatar(Message msg) {
      try {
         msg.writer().writeShort(Manager.HEAD_AVATARS.size());
         for (HeadAvatar ha : Manager.HEAD_AVATARS.values()) {
            msg.writer().writeShort(ha.headId);
            msg.writer().writeShort(ha.avatarId);
         }
      } catch (Exception e) {
      }
   }

   public static void sendLinkIP(Session session) {
      Message msg;
      try {
         msg = Message.create(-29);
         msg.writer().writeByte(2);
         msg.writer().writeUTF(ServerManager.NAME + ":" + Manager.DOMAIN + ":" + ServerManager.PORT + ":0,0,0");
         msg.writer().writeByte(1);
         session.sendMessage(msg);
      } catch (Exception e) {
      }
   }

}
