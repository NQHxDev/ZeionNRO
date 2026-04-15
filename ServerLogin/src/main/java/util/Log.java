package util;

public class Log {

   public static final String RESET = "\033[0m";
   public static final String RED = "\033[0;31m";
   public static final String GREEN = "\033[0;32m";
   public static final String YELLOW = "\033[0;33m";
   public static final String BLUE = "\033[0;34m";
   public static final String MAGENTA = "\033[0;35m";
   public static final String CYAN = "\033[0;36m";
   public static final String WHITE = "\033[0;37m";

   public static void info(String message) {
      System.out.println(CYAN + "[INFO] " + RESET + message);
   }

   public static void success(String message) {
      System.out.println(GREEN + "[SUCCESS] " + RESET + message);
   }

   public static void warning(String message) {
      System.out.println(YELLOW + "[WARN] " + RESET + message);
   }

   public static void error(String message) {
      System.out.println(RED + "[ERROR] " + RESET + message);
   }

   public static void banner() {
      System.out.println(MAGENTA + "  ____  ____  __  __   __ _ " + RESET);
      System.out.println(MAGENTA + " (_  _)(  __)(  )/  \\ (  ( \\" + RESET);
      System.out.println(MAGENTA + "   )(   ) _)  )((  O )/    /" + RESET);
      System.out.println(MAGENTA + "  (__) (____)(__)\\__/ \\_)__)" + RESET);
      System.out.println(CYAN + "  >>> ZEION LOGIN SERVER <<<");
      System.out.println(WHITE + "----------------------------------------" + RESET);
   }
}
