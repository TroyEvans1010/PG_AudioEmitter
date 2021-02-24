package android.util;

/**
 * This allows Log to be used in the test/ source set.
 *
 * Log is an android object, so it does not exist in test/. If you try to use it in test/, you will see: java.lang.RuntimeException: Method i in android.util.Log not mocked.
 * However, this implements it, and just forwards the messages to println().
 */
public class Log {
    public static int d(String tag, String msg) {
        System.out.println("D/" + tag + ": " + msg);
        return 0;
    }

    public static int d(String tag, String msg, Throwable e) {
        System.out.println("D/" + tag + ": " + msg);
        return 0;
    }

    public static int i(String tag, String msg) {
        System.out.println("I/" + tag + ": " + msg);
        return 0;
    }

    public static int i(String tag, String msg, Throwable e) {
        System.out.println("I/" + tag + ": " + msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.println("W/" + tag + ": " + msg);
        return 0;
    }

    public static int w(String tag, String msg, Throwable e) {
        System.out.println("W/" + tag + ": " + msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.println("E/" + tag + ": " + msg);
        return 0;
    }

    public static int e(String tag, String msg, Throwable e) {
        System.out.println("E/" + tag + ": " + msg);
        return 0;
    }
}
