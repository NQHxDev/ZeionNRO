package nro.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileIO {

   private static final Map<String, byte[]> CACHE = new HashMap<>();

   public static byte[] readFile(String url) {
      try {
         byte[] ab = CACHE.get(url);
         if (ab == null) {
            FileInputStream fis = new FileInputStream(url);
            ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            // CACHE.put(url, ab);
         }
         return ab;
      } catch (IOException e) {
         System.out.println("khong tim thay file: " + url);
      }
      return null;
   }

   public static ByteArrayOutputStream loadFile(String url) {
      try {
         FileInputStream openFileInput = new FileInputStream(url);
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         byte[] bArr = new byte[1024];
         while (true) {
            int read = openFileInput.read(bArr);
            if (read == -1) {
               break;
            }
            byteArrayOutputStream.write(bArr, 0, read);
         }
         byteArrayOutputStream.flush();
         byteArrayOutputStream.close();
         openFileInput.close();
         return byteArrayOutputStream;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }

   public static void writeFile(String url, byte[] ab) {
      try {
         File f = new File(url);
         if (f.exists()) {
            f.delete();
         }
         f.createNewFile();
         FileOutputStream fos = new FileOutputStream(url);
         fos.write(ab);
         fos.flush();
         fos.close();
      } catch (IOException e) {
      }
   }

}
