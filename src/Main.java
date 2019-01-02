import java.io.File;
import java.io.IOException;
import java.util.Scanner;
/*
   UWAGA: Założenie jest takie, że aplikacja pracuje na portach >50 000 !

   2 argumenty = <nazwa hosta, z ktorym ma polaczyc sie klient> <numer portu>

*/

public class Main {
    private static int myAppId = 1;
    private static String myFolderPath;

    public static void main(String[] args) throws InterruptedException {
        if(args.length != 2) {
            System.err.println("Podaj argumenty: <nazwa hosta> <nr portu>");
            System.exit(1);
        }
        String clientHost = args[0];
        int clientPort = Integer.parseInt(args[1]);


        System.out.println("Witaj. Czy posiadasz stworzony folder z plikami? \n" +
                "1)Tak \n2)Nie");
        Scanner scan = new Scanner(System.in);
        int hello = scan.nextInt();
        int idUpdate = 0;
        switch (hello){
            case 1:
                System.out.println("Podaj ID swojej aplikacji.");
                idUpdate = scan.nextInt();
                myAppId = idUpdate;
                break;
            case 2:
                System.out.println("Twoje id zostanie przydzielone automatycznie.");
                break;
            default:
                System.out.println("Podales niepoprawny komunikat. Id zostanie przydzielone automatycznie.");
                break;
        }

        pickFolder();

        int myServerPort = 50000 + myAppId;
        System.err.println("s port = " + myServerPort);

        MyServer server = new MyServer("localhost", myServerPort);
        new Thread(server).start();

        Client client = new Client(clientHost, clientPort);
        new Thread(client).start();


    }

    public static String getAppFilesPath(){
        return myFolderPath;
    }

    private static void pickFolder(){
        int appId = myAppId;
        String folderPath = null;
        Scanner scanner;

        boolean folderReady = false;
        boolean feedbackFlag = false;
        while (!folderReady) {
            folderPath = "TORrent_" + appId + "/";
            File folder = new File(folderPath);

            if (folder.exists()) {
                File[] list = folder.listFiles();

                if (folder.isDirectory() && list.length > 0) {

                    if(!feedbackFlag) {
                        System.out.println("Folder o nazwie " + folderPath + " juz istnieje. Czy na pewno chcesz go uzyc?");
                        System.out.println("1 - Tak, to mój folder" + "\n" +
                                "2 - Nie, utworz nowy folder lub wybierz pierwszy pusty");

                        scanner = new Scanner(System.in);
                        int whatToDo = scanner.nextInt();
                        if(whatToDo == 1){
                            folderReady = true;
                            System.out.println("Ok, twoj roboczy folder z plikami to: " + folderPath);
                        }else if(whatToDo == 2){
                            appId++;
                            feedbackFlag = true;
                        }else{
                            System.out.println("Podaj poprawny nr polecenia");
                        }
                    }else appId++;
                }else{
                    folderReady = true;
                    System.out.println("Folder z Twoimi plikami to: " + folderPath);
                    File ocucpyFile = new File(folderPath + "pusty_plik_rezerwujacy.txt");
                    try {
                        ocucpyFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                boolean success = new File(folderPath).mkdir();
                if(success){
                    System.out.println("Utworzono folder dla aplikacji. Twoj osobisty folder z plikami to: " + folderPath);
                }else{
                    System.out.println("Wystapil blad przy tworzeniu folderu aplikacji.");
                }
            }
        }
        myFolderPath = folderPath;
        myAppId = appId;

    }



}
