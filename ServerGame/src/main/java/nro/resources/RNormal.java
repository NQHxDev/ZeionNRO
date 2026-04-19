package nro.resources;

public class RNormal extends AbsResources {

   public RNormal() {
      setFolder("normal");
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
