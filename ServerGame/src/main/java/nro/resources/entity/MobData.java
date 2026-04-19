package nro.resources.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MobData {

   public int id;

   public byte type;
    public byte typeData;
    public byte[][] frameBoss;

   public Sprite[] sprites;

   public Frame[][] frames;

   public short[] animations;

   public byte[] dataMob;

   public void dispose() {
      dataMob = null;
   }

   public byte[] getMobData() {
      if (dataMob != null) {
         return dataMob;
      }
      ByteArrayOutputStream ms = new ByteArrayOutputStream();
      DataOutputStream ds = new DataOutputStream(ms);
      try {
         ds.writeByte(sprites.length);
         for (Sprite sprite : sprites) {
            ds.writeByte(sprite.id);
            if (type == 0 || type == 1) {
               ds.writeByte(sprite.x);
               ds.writeByte(sprite.y);
            } else {
               ds.writeShort(sprite.x);
               ds.writeShort(sprite.y);
            }
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
         dataMob = ms.toByteArray();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return dataMob;
   }

}
