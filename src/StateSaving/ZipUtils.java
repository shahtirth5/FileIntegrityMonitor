package StateSaving;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void zip(String sourceDirectory, String destinationDirectory)  {
        File sourceDir = new File(sourceDirectory);
        ZipOutputStream zip = null;
        try {
            zip = new ZipOutputStream(new FileOutputStream(destinationDirectory + "/" + sourceDir.getName() + ".zip"));
            String basePath = sourceDir.getParent() + "/";
            addDir(sourceDir,basePath ,zip);
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addDir(File sourceDir, String basePath, ZipOutputStream zip) throws IOException {
        File[] contents = sourceDir.listFiles();
        for(File file : contents) {
            if(file.isDirectory()){
                addDir(file, basePath, zip);
            } else {
                zip.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(basePath,"")));
                Path rn_demo = Paths.get(String.valueOf(file));
                Files.copy(rn_demo, zip);
            }
        }
        zip.closeEntry();
    }

    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        byte[] buffer = new byte[1024];
        try{
            //create output directory is not exists
            File folder = new File(destDirectory);
            if(!folder.exists()){
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFilePath));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){
                String fileName = ze.getName();
                File newFile = new File(destDirectory + File.separator + fileName);
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

//try(ZipFile file = new ZipFile(zipFilePath))
//        {
//            FileSystem fileSystem = FileSystems.getDefault();
//            //Get file entries
//            Enumeration<? extends ZipEntry> entries = file.entries();
//
//            //We will unzip files in this folder
//            String uncompressedDirectory = destDirectory;
////            Files.createDirectory(fileSystem.getPath(uncompressedDirectory));
//
//            //Iterate over entries
//            while (entries.hasMoreElements())
//            {
//                ZipEntry entry = entries.nextElement();
//                //If directory then create a new directory in uncompressed folder
//                if (entry.isDirectory())
//                {
//                    System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
//                    Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
//                }
//                //Else create the file
//                else
//                {
//                    InputStream is = file.getInputStream(entry);
//                    BufferedInputStream bis = new BufferedInputStream(is);
//                    String uncompressedFileName = uncompressedDirectory + entry.getName();
//                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
//                    Files.createFile(uncompressedFilePath);
//                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
//                    while (bis.available() > 0)
//                    {
//                        fileOutput.write(bis.read());
//                    }
//                    fileOutput.close();
//                    System.out.println("Written :" + entry.getName());
//                }
//            }
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }

