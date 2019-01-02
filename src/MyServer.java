import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer implements Runnable{
    private String server;
    private int port;

    public MyServer(String server, int port){
        this.server = server;
        this.port = port;
    }

    @Override
    public void run() {
        while (true) {
            try (
                    ServerSocket serverSocket = new ServerSocket(port);
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(
                            clientSocket.getOutputStream(), true
                    );
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String inputLine, outputLine;

                //start wymiany komunikatow
                MyServerProtocol mServPrtcl = new MyServerProtocol();
                outputLine = mServPrtcl.processInput(null);
                out.println(outputLine);

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("FILE_TOSERVER")) { //plik zostaje przeslany na serwer
                        DataInputStream din = new DataInputStream(clientSocket.getInputStream());
                        String newFileName = inputLine.substring(13);
                        FilePuller puller = new FilePuller(din);
                        puller.pullFile(newFileName);
                        continue;
                    } else {
                        outputLine = mServPrtcl.processInput(inputLine);
                    }

                    if(inputLine.equals("WAIT") || outputLine.equals("WAIT")){
                        out.println("WAITING");
                    }

                    if (outputLine.equals("SEND_FILE")) {
                        int fileNumber = Integer.parseInt(outputLine.substring(9));

                        out.println("FILE_DOWNLOAD" + mServPrtcl.getFileName(fileNumber));

                        byte[] bytesToSend = mServPrtcl.getFileByteArray(fileNumber);
                        FilePusher pusher = new FilePusher(dos);
                        pusher.pushFile(bytesToSend);

                        out.println("Przeslano plik, wpisz cokolwiek by kontynuować...");

                    } else if (outputLine.startsWith("GET_FILE")) {
                        out.println("FILE_UPLOAD");


                    }else{
                        out.println(outputLine);
                    }

                }

                System.err.println("SERWER: KONIEC PETLI WYMIANY KOMUNIKATOW");
                break;

            } catch (IOException e) {
                System.out.println("Port niedostępny: " + port + ", Host: " + server + "\n" + "Zmieniam port serwera na " + (port + 1));
                port++;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
