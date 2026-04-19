package nro.resources.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EffectData {

   public int id;
   public byte type;

   public Sprite[] sprites;

   public Frame[][] frames;

   public short[] animations;

   public byte[] data;

   public byte[] getData() {
      if (data != null) {
         return data;
      }
      ByteArrayOutputStream ms = new ByteArrayOutputStream();
      DataOutputStream ds = new DataOutputStream(ms);
      try {
         ds.writeByte(sprites.length);
         for (Sprite sprite : sprites) {
            ds.writeByte(sprite.id);
            ds.writeShort(sprite.x);
            ds.writeShort(sprite.y);
            ds.writeByte(sprite.w);
            ds.writeByte(sprite.h);
         }
         ds.writeShort(frames.length);
         for (Frame[] a : frames) {
            ds.writeByte(a.length);
            for (Frame frame : a) {
               ds.writeShort(frame.dx);
               ds.writeShort(frame.dy);
               ds.writeByte(frame.spriteID);
            }
         }
         ds.writeShort(animations.length);
         for (short a : animations) {
            ds.writeShort(a);
         }
         ds.flush();
         data = ms.toByteArray();
      } catch (IOException ex) {
         Logger.getLogger(MobData.class.getName()).log(Level.SEVERE, null, ex);
      }
      return data;
   }

}
