package StateSaving;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StateSaving {
    public static void saveState(String inputDirectory, String outputDirectory) {
        try {
            File f = new File(inputDirectory);
            String fileName = f.getName();
            ZipUtils.zip(inputDirectory, outputDirectory);
            CryptoUtils.encrypt("abcdefghabcdefgh", new File(outputDirectory + "/" + fileName + ".zip"), new File(outputDirectory + "/" + fileName + ".encrypt"));
            Files.delete(Paths.get(outputDirectory + "/" + fileName + ".zip"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void revertState(String inputDirectory) {
        try {
            File f = new File(inputDirectory);
            String name = f.getName();
            String parentDir = f.getParent();
            CryptoUtils.decrypt("abcdefghabcdefgh", new File("/home/tirth211/Desktop/ToolsAndPrograms/JAVA/FileIntegrityMonitor/SavedStates/" + name + ".encrypt"), new File("/home/tirth211/Desktop/ToolsAndPrograms/JAVA/FileIntegrityMonitor/SavedStates/" + name + ".zip"));
            FileUtils.deleteDirectory(f);
            ZipUtils.unzip("/home/tirth211/Desktop/ToolsAndPrograms/JAVA/FileIntegrityMonitor/SavedStates/" + name + ".zip", parentDir);
            Files.delete(Paths.get("/home/tirth211/Desktop/ToolsAndPrograms/JAVA/FileIntegrityMonitor/SavedStates/" + name + ".zip"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
