package cn.edu.sicnu.cs.stu.baixinyuan.myapplication;

/**
 * 实验三的小工具类：秒表时间格式化。
 */
public final class Utils {

    private Utils() {
        // 工具类不允许被实例化
    }

    /**
     * 把毫秒数格式化成 {@code mm:ss.d} 形式，例如 65000 ms -> "01:05.0"。
     *
     * @param millis 已经走过的毫秒数
     * @return 格式化后的时间字符串
     */
    public static String formatStopwatch(long millis) {
        if (millis < 0) {
            millis = 0;
        }
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long tenths = (millis % 1000) / 100;
        return String.format(java.util.Locale.US, "%02d:%02d.%d", minutes, seconds, tenths);
    }
}
