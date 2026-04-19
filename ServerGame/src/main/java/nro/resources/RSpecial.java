package nro.resources;

public class RSpecial extends AbsResources {

   public RSpecial() {
      setFolder("special");
   }

   @Override
   public byte[] getData() {
      return readAllBytes("data.zip");
   }

   @Override
   public byte[] getDataByZoom(int zoomLevel) {
      return readAllBytes("image", zoomLevel + "", "data", "data.zip");
   }

   @Override
   public byte[] getMapData(int zoomLevel, int mapId) {
      return readAllBytes("image", zoomLevel + "", "map", mapId + ".png");
   }

}
