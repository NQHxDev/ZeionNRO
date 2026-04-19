package nro.resources;

import nro.consts.Cmd;
import nro.network.io.Message;
import nro.resources.entity.EffectData;
import nro.resources.entity.ImageByName;
import nro.resources.entity.MobData;
import nro.server.io.Session;
import nro.utils.Log;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Resources {

   private final Map<Integer, AbsResources> resources;

   private static Resources instance;

   public static Resources gI() {
      if (instance == null) {
         instance = new Resources();
      }

      return instance;
   }

   private Resources() {
      resources = new HashMap<>();
      resources.put(1, new RNormal());
      resources.put(2, new RSpecial());
   }

   public void init() {
      Log.log("Loading resources...");
      File file = new File("data/resources/res1");
      if (file.exists()) {
         resources.get(1).init(file);
      }
      file = new File("data/resources/res2");
      if (file.exists()) {
         resources.get(2).init(file);
      }
      Log.success("Loading resources successfully!");
   }

   public AbsResources find(int type) {
      AbsResources res = resources.get(type);
      if (res == null) {
         res = resources.get(1);
      }
      return res;
   }

   public void sendResUpdate(Session session) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_RES_UPDATE);
         msg.writer().writeByte(1); // res version
         msg.writer().writeUTF("http://nroxanh.xyz/data/data.zip"); // link data
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void sendResUpdate2(Session session) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_RES_UPDATE);
         msg.writer().writeByte(0);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void sendResVersion(Session session) {
      try {
         AbsResources res = find(session.typeClient);
         if (res != null) {
            int[] version = res.getDataVersion();
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            DataOutputStream ds = mss.writer();
            ds.writeByte(0);
            ds.writeInt(version[session.zoomLevel - 1]);
            ds.flush();
            session.sendMessage(mss);
            mss.cleanup();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void downloadResources(Session session) {
      Message msg;
      try {
         msg = Message.create(Cmd.DOWNLOAD_RESOURCES);
         byte[] data = find(session.typeClient).getData();
         msg.writer().writeInt(data.length);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void downloadResourcesByZoom(Session session) {
      Message msg;
      try {
         msg = Message.create(Cmd.DOWNLOAD_RESOURCES);
         byte[] data = find(session.typeClient).getDataByZoom(session.zoomLevel);
         msg.writer().writeInt(data.length);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void downloadMapResources(Session session, int mapId) {
      Message msg;
      try {
         msg = Message.create(Cmd.MAP_RESOURCES);
         byte[] data = find(session.typeClient).getMapData(session.zoomLevel, mapId);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestImage(Session session, int id) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_IMAGE);
         byte[] data = find(session.typeClient).getRawImageData(session.zoomLevel, id);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestIcon(Session session, int id) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_ICON);
         byte[] data = find(session.typeClient).getRawIconData(session.zoomLevel, id);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void sendSmallVersion(Session session) {
      try {
         AbsResources res = find(session.typeClient);
         if (res != null) {
            byte[][] smallVersion = res.getSmallVersion();
            byte[] data = smallVersion[session.zoomLevel - 1];
            Message ms = new Message(Cmd.SMALLIMAGE_VERSION);
            ms.writer().writeShort(data.length);
            ms.writer().write(data);
            session.sendMessage(ms);
            ms.cleanup();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void sendBGVersion(Session session) {
      try {
         AbsResources res = find(session.typeClient);
         if (res != null) {
            byte[][] backgroundVersion = res.getBackgroundVersion();
            byte[] data = backgroundVersion[session.zoomLevel - 1];
            Message ms = new Message(Cmd.BGITEM_VERSION);
            ms.writer().writeShort(data.length);
            ms.writer().write(data);
            session.sendMessage(ms);
            ms.cleanup();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void requestBg(Session session, int id) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_BG);
         byte[] data = find(session.typeClient).getRawBgData(session.zoomLevel, id);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestMapLogo(Session session, int id) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_MAP_LOGO);
         byte[] data = find(session.typeClient).getRawMapLogoData(session.zoomLevel, id);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestSmallImage(Session session, int id) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_SMALL_IMAGE);
         byte[] data = find(session.typeClient).getRawSmallImageData(session.zoomLevel, id);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestSideIcon(Session session, int id) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_SIDE_ICON);
         byte[] data = find(session.typeClient).getRawSideIconData(session.zoomLevel, id);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestItemIcon(Session session, int id) {
      Message msg;
      try {
         msg = Message.create(Cmd.GET_ITEM_ICON);
         byte[] data = find(session.typeClient).getRawItemIconData(session.zoomLevel, id);
         msg.writer().write(data);
         session.sendMessage(msg);
         msg.cleanup();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestImgByName(Session session, String filename) {
      try {
         AbsResources res = find(session.typeClient);
         ImageByName ibn = res.getIBN(filename);
         if (ibn != null) {
            byte[] data = res.getRawIBNData(session.zoomLevel, filename);
            Message msg = Message.create(Cmd.GET_IMG_BY_NAME);
            msg.writer().writeUTF(ibn.filename);
            msg.writer().writeByte(ibn.nFame);
            msg.writer().writeInt(data.length);
            msg.writer().write(data);
            session.sendMessage(msg);
            msg.cleanup();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestMobTemplate(Session session, int id) {
      try {
         AbsResources res = find(session.typeClient);
         MobData mob = res.getMobData(id);
         if (mob != null) {
            byte[] data = mob.getMobData();
            byte[] imgData = res.getRawMobData(session.zoomLevel, id);
            Message ms = Message.create(Cmd.REQUEST_NPCTEMPLATE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeByte(mob.type);
            ds.writeInt(data.length);
            ds.write(data);
            ds.writeInt(imgData.length);
            ds.write(imgData);
            ds.writeByte(mob.typeData);
            if (mob.typeData == 1 || mob.typeData == 2) {
               byte[][] frameBoss = mob.frameBoss;
               ds.writeByte(frameBoss.length);
               for (byte[] frame : frameBoss) {
                  ds.writeByte(frame.length);
                  ds.write(frame);
               }
            }
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void requestEffData(Session session, int id) {
      try {
         AbsResources res = find(session.typeClient);
         int effId = res.getEffId(id);
         EffectData eff = res.getEffData(effId);
         if (eff != null) {
            byte[] data = eff.getData();
            byte[] imgData = res.getRawEffectData(session.zoomLevel, effId);
            Message ms = Message.create(Cmd.GET_EFFDATA);
            DataOutputStream ds = ms.writer();
            ds.writeShort(id);
            ds.writeInt(data.length);
            ds.write(data);
            if (session.isVersionAbove(220)) {
               ds.writeByte(eff.type);
            }
            ds.writeInt(imgData.length);
            ds.write(imgData);
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
