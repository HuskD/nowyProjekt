import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Client implements Runnable {
    private String hostname;
    private int port;

    public Client(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void run() {
        Thread readThread;
        while (true) {
            try {
                Thread.sleep(4800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Socket socket = new Socket(hostname, port);

                //watek czytajacy
                new Thread(() -> {
                    String serverInput;
                    MyFilesManager flsMng = new MyFilesManager(Main.getAppFilesPath());
                    flsMng.fetchFiles();
                    try (
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            PrintWriter readThreadOut = new PrintWriter(socket.getOutputStream(), true)
                    ){
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while ((serverInput = in.readLine()) != null) {
                            if(serverInput.startsWith("FILE_DOWNLOAD")){
                                String fileName = serverInput.substring(13);

                                DataInputStream dis = new DataInputStream(socket.getInputStream());

                                FilePuller puller = new FilePuller(dis);
                                puller.pullFile(fileName);

                            }else if(serverInput.startsWith("FILE_UPLOAD")){
                                readThreadOut.println("WAIT");



                                new Thread(() -> {
                                    Scanner scanner = new Scanner(System.in);

                                    System.out.println("Ktory plik chcesz wyslac?");
                                    MyFilesManager fileManager = new MyFilesManager(Main.getAppFilesPath()); //tworzymy obiekt klasy files manager operujacy na plikach

                                    System.out.println(fileManager.displayFiles());

                                    int myFileNumber = scanner.nextInt();

                                    readThreadOut.println("FILE_TOSERVER" + fileManager.getFileName(myFileNumber));

                                    byte[] toSend;
                                    try {
                                        toSend = fileManager.getFileByteArray(myFileNumber);
                                        FilePusher pusher = new FilePusher(dos);
                                        pusher.pushFile(toSend);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    readThreadOut.println("FILE_SENT");

                                }).start();







                            }else if(serverInput.equals("WAITING")){
                                readThreadOut.println("WAIT");
                            }
                            else{
                                System.out.println("Wiadomosc serwera: " + serverInput);
                            }



                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();

                //watek piszacy

                new Thread(() -> {
                    String userInput;
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        while ((userInput = reader.readLine()) != null) {
                            out.println(userInput);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();


                break;
            } catch (SocketException se){

            } catch (IOException e) {
                System.err.println("Klient nie nawiazal polaczenia. Ponawiam probe.");
            }
        }
    }

    public static void downloadFile(InputStream is) throws IOException {





    }

}
