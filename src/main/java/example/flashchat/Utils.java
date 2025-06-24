package example.flashchat;

import java.io.File;

public class Utils {
    public static String getProjectRoot() {
        return System.getProperty("user.dir");
    }

    public static String checkDirectoryExists(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
            file.setExecutable(false);
        }
        return file.getAbsolutePath();
    }
}
