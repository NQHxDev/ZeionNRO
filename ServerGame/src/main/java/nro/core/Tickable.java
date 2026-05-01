package nro.core;

public interface Tickable {

   void tick(long nowMillis) throws Exception;

   int periodMs();

   default boolean isActive() {
      return true;
   }

}
