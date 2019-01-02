import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class MyFilesManager {

    private HashMap<Integer, File> filesMap;
    private String filesDir;

    public MyFilesManager(String filesDir){
        this.filesDir = filesDir;
        this.filesMap = new HashMap<>();
    }

    public String displayFiles(){
        String message;
        message = "Oto lista plikow.\n";

        for(int i = 0; i < filesMap.size(); i++){
            // theOutput = theOutput + filesMap.get(i).getName() + "\n";
            if(i == (filesMap.size() - 1)){
                message = message + i + " ---- " + filesMap.get(i).getName();
            }else {
                message = message + i + " ---- " + filesMap.get(i).getName() + "\n";
            }
        }
        return message;
    }

    public byte[] getFileByteArray(int fileNumber) throws IOException {
        String fileName = filesMap.get(fileNumber).getName();

        Path filePath = FileSystems.getDefault().getPath(Main.getAppFilesPath(), fileName);

        return Files.readAllBytes(filePath);
    }

    public String getFileName(int fileNumber){
        return filesMap.get(fileNumber).getName();
    }

    public void fetchFiles(){

        File dir = new File(filesDir);
        File[] myFiles = dir.listFiles();
        for(int i = 0; i < myFiles.length; i++){
            if(myFiles[i].isFile()){
                this.filesMap.put(i, myFiles[i]);
            }
        }
    }






}
