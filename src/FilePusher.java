import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FilePusher {
    private DataOutputStream dos;

    public FilePusher(DataOutputStream outputStream){
        this.dos = outputStream;
    }

    public void pushFile(byte[] bytes) throws IOException {
        try {
            dos.writeInt(bytes.length); // wysylam "rozmiar" pliku
            dos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
