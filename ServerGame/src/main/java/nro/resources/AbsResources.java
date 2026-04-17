package nro.resources;

import com.google.gson.Gson;
import nro.resources.entity.EffectData;
import nro.resources.entity.ImageByName;
import nro.resources.entity.MobData;
import nro.utils.FileUtils;
import nro.utils.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

@Getter
@Setter
public abstract class AbsResources {

   private File folder;
   private HashMap<String, byte[]> datas;
   private int[] dataVersion;
   private byte[][] smallVersion;
   private byte[][] backgroundVersion;
   private HashMap<String, ImageByName> imageByNames;
   private List<MobData> mobDatas;
   private List<EffectData> effectDatas;

   public AbsResources() {
      this.datas = new HashMap<>();
   }

   public void init() {
      long st = System.currentTimeMillis();
      Log.log("AbsResources: Bắt đầu khởi tạo DataVersion...");
      initDataVersion();
      Log.log("AbsResources: Bắt đầu khởi tạo BGSmallVersion...");
      initBGSmallVersion();
      Log.log("AbsResources: Bắt đầu khởi tạo SmallVersion...");
      initSmallVersion();
      Log.log("AbsResources: Bắt đầu khởi tạo IBN...");
      initIBN();
      Log.log("AbsResources: Bắt đầu khởi tạo MobData...");
      initMobData();
      Log.log("AbsResources: Bắt đầu khởi tạo EffectData...");
      initEffectData();
      Log.log("AbsResources: Khởi tạo hoàn tất (" + (System.currentTimeMillis() - st) + "ms)");
   }

   public void initEffectData() {
      Gson g = new Gson();
      File folder = new File(this.folder, "effect_data");
      File[] listFiles = folder.listFiles();
      effectDatas = new ArrayList<>();
      for (File file : listFiles) {
         try {
            String json = Files.readString(file.toPath());
            if (!json.equals("")) {
               effectDatas.add(g.fromJson(json, EffectData.class));
            }
         } catch (IOException ex) {
            Logger.getLogger(AbsResources.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public void initMobData() {
      Gson g = new Gson();
      File folder = new File(this.folder, "monster_data");
      File[] listFiles = folder.listFiles();
      mobDatas = new ArrayList<>();
      for (File file : listFiles) {
         try {
            String json = Files.readString(file.toPath());
            if (!json.equals("")) {
               mobDatas.add(g.fromJson(json, MobData.class));
            }
         } catch (IOException ex) {
            Logger.getLogger(AbsResources.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      mobDatas.forEach(m -> m.setData());
   }

   public void initIBN() {
      String json = readString("ibn.json");
      if (!json.equals("")) {
         JSONArray jIBN = new JSONArray(json);
         imageByNames = new HashMap<>();
         for (int i = 0; i < jIBN.length(); i++) {
            JSONObject obj = jIBN.getJSONObject(i);
            String filename = obj.getString("filename");
            int nFame = obj.getInt("number_frame");
            imageByNames.put(filename, ImageByName.builder()
                  .filename(filename)
                  .nFame(nFame)
                  .build());
         }
      }
   }

   public void initDataVersion() {
      dataVersion = new int[4];
      for (int i = 0; i < 4; i++) {
         File folder = new File(this.folder, "data");
         int ver = (int) FileUtils.getFolderSize(folder);
         dataVersion[i] = ver;
      }
   }

   public void initBGSmallVersion() {
      try {
         backgroundVersion = new byte[4][];
         for (int i = 0; i < 4; i++) {
            Path dirPath = Paths.get(this.folder.getAbsolutePath(), "image", (i + 1) + "", "bg");
            File file = dirPath.toFile();
            if (!file.exists()) {
               Log.error("AbsResources: BG directory missing: " + dirPath.toAbsolutePath());
               continue;
            }
            File[] files = file.listFiles();
            int max = 0;
            for (File f : files) {
               String name = f.getName();
               int id = Integer.parseInt(FileUtils.cutPng(name));
               if (id > max) {
                  max = id;
               }
            }
            backgroundVersion[i] = new byte[max + 1];
            for (File f : files) {
               String name = f.getName();
               int id = Integer.parseInt(FileUtils.cutPng(name));
               backgroundVersion[i][id] = (byte) (f.length() % 127);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void initSmallVersion() {
      try {
         smallVersion = new byte[4][];
         for (int i = 0; i < 4; i++) {
            Path dirPath = Paths.get(this.folder.getAbsolutePath(), "image", (i + 1) + "", "icon");
            File file = dirPath.toFile();
            if (!file.exists()) {
                Log.error("AbsResources: Icon directory missing: " + dirPath.toAbsolutePath());
                continue;
            }
            File[] files = file.listFiles();
            int max = 0;
            for (File f : files) {
               String name = f.getName();
               name = FileUtils.cutPng(name);
               int id = Integer.parseInt(name);
               if (id > max) {
                  max = id;
               }
            }
            smallVersion[i] = new byte[max + 1];
            for (File f : files) {
               String name = f.getName();
               name = FileUtils.cutPng(name);
               int id = Integer.parseInt(name);
               smallVersion[i][id] = (byte) (f.length() % 127);
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   protected void setFolder(String folder) {
      this.folder = new File("resources", folder);
   }

   public byte[] readAllBytes(String... segments) {
      try {
         Path path = Paths.get(this.folder.getAbsolutePath(), segments);
         if (!Files.exists(path)) {
            Log.error("AbsResources: File NOT FOUND: " + path.toAbsolutePath());
            return new byte[0];
         }
         return Files.readAllBytes(path);
      } catch (IOException ex) {
         Log.error("AbsResources: Error reading file: " + ex.getMessage());
         return new byte[0];
      }
   }

   public List<String> readAllLines(String path) {
      try {
         return Files.readAllLines(new File(this.folder, path).toPath());
      } catch (IOException ex) {
         return List.of();
      }
   }

   public String readString(String path) {
      try {
         File f = new File(this.folder, path);
         if (!f.exists()) {
            return "";
         }
         return Files.readString(f.toPath());
      } catch (IOException ex) {
         ex.printStackTrace();
         return "";
      }
   }

   public byte[] getRawIconData(int zoomLevel, int iconID) {
      return readAllBytes("image", zoomLevel + "", "icon", iconID + ".png");
   }

   public byte[] getRawBGData(int zoomLevel, int bg) {
      return readAllBytes("image", zoomLevel + "", "bg", bg + ".png");
   }

   public byte[] getRawIBNData(int zoomLevel, String filename) {
      return readAllBytes("image", zoomLevel + "", "imgbyname", filename + ".png");
   }

   public byte[] getRawMobData(int zoomLevel, int id) {
      return readAllBytes("image", zoomLevel + "", "monster", id + ".png");
   }

   public byte[] getRawEffectData(int zoomLevel, int id) {
      return readAllBytes("image", zoomLevel + "", "effect", id + ".png");
   }

   public void putData(String key, byte[] data) {
      datas.put(key, data);
   }

   public byte[] getData(String key) {
      return datas.get(key);
   }

   public ImageByName getIBN(String key) {
      return imageByNames.get(key);
   }

   public MobData getMobData(int id) {
      for (MobData mob : mobDatas) {
         if (mob.getId() == id) {
            return mob;
         }
      }
      return null;
   }

   public EffectData getEffectData(int id) {
      for (EffectData eff : effectDatas) {
         if (eff.getId() == id) {
            return eff;
         }
      }
      return null;
   }

}
