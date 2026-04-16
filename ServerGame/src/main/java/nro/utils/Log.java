package nro.utils;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460 (Upgraded by AI Assistant)
 * @copyright 💖 GirlkuN 💖
 */
public class Log {

    // ANSI Color Codes
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String MAGENTA = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    public static void log(String text) {
        System.out.println(CYAN + "[INFO] " + RESET + text);
    }

    public static void success(String text) {
        System.out.println(GREEN + "[SUCCESS] " + RESET + text);
    }

    public static void warning(String text) {
        System.out.println(YELLOW + "[WARN] " + RESET + text);
    }

    public static void error(String text) {
        System.out.println(RED + "[ERROR] " + RESET + text);
    }

    public static void error(Class<?> clazz, Throwable ex, String logs) {
        System.out.println(RED + "[ERROR] " + RESET + clazz.getSimpleName() + ": " + logs);
        if (ex != null) ex.printStackTrace();
    }

    public static void error(Class<?> clazz, Throwable ex) {
        System.out.println(RED + "[ERROR] " + RESET + clazz.getSimpleName() + ": " + ex.getMessage());
        if (ex != null) ex.printStackTrace();
    }

    public static void banner() {
        System.out.println(MAGENTA + " _   _  _   ___ _   _ ___ ___ ___  _   __  __   _ " + RESET);
        System.out.println(MAGENTA + "| |_| |/ \\ / __| |_| |_ _| _ \\  _|| | |  \\/  | / \\" + RESET);
        System.out.println(MAGENTA + "|  _  | o |\\__ \\  _  || ||   / |__| |_| |\\/| |/ o |" + RESET);
        System.out.println(MAGENTA + "|_| |_|_n_||___/_| |_|___|_|_\\____|___|_|  |_|_n_|" + RESET);
        System.out.println(CYAN + "      >>> SERVERGAME GAME SERVER 2025 <<<      " + RESET);
        System.out.println(WHITE + "----------------------------------------------------" + RESET);
    }
}
