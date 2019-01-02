import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilePuller {
    private DataInputStream dis;

    public FilePuller(DataInputStream dis){
        this.dis = dis;
    }

    public void pullFile(String fileName) throws IOException {

        File f = new File(Main.getAppFilesPath() + "received_" + fileName);
        FileOutputStream fos = new FileOutputStream(f);


        int length = dis.readInt(); // wczytujemy "rozmiar" pliku

        if(length > 0) {
            byte[] message = new byte[length];
            dis.readFully(message);
            fos.write(message);
        }


        fos.close();



    }
}
