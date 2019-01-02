import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class MyServerProtocol {
    // klasa ma sluzyc do obslugi żądań klienta



    public static final int PLIK_ODEBRANY = -4;
    public static final int WYSYLANIE = -3;
    public static final int POBIERANIE = -2;
    public static final int  CZEKAJ = -1;
    public static final int PRZYJMIJ_POLECENIE = 0;


    public static final int WYSWIETL_PLIKI = 1;
    public static final int POBIERZ_PLIK = 2;
    public static final int WYSLIJ_PLIK = 3;
    public static final int ZAKONCZ = 4;

    private int state = CZEKAJ;

    public MyServerProtocol(){
        fetchFilesMap();
    }



    private HashMap<Integer, File> mapOfFiles;

    public String processInput(String theInput){
        String theOutput = null;

        if(state == CZEKAJ){
            theOutput = displayInstructions();
            state = PRZYJMIJ_POLECENIE;
        }else if(state == PRZYJMIJ_POLECENIE){
            int orderNumber;
            if(theInput.matches("[0-9]+")){
                orderNumber = Integer.parseInt(theInput); // czytam numer rokzazu podanego przez klienta
            }else{
                orderNumber = -500; // przypisujemy nr rozkazu, ktory wywola blad
            }


            if(orderNumber == WYSWIETL_PLIKI){
                // wyswietlam liste plikow wraz z sumami kontrolnymi

                theOutput = displayFiles();

            }else if(orderNumber == POBIERZ_PLIK){
                theOutput = "Podaj numer pliku, ktory chcesz pobrac...";
                theOutput = theOutput + displayFiles();
                state = POBIERANIE; // zmiana stanu na pobieranie w celu przetworzenia procesu pobierania pliku

            }else if(orderNumber == WYSLIJ_PLIK){
                // klient wysyla plik do serwera
                theOutput = "GET_FILE";
                state = WYSYLANIE; //zmiana stanu, by przetworzyc wysylanie
            }else if(orderNumber ==  ZAKONCZ){
                // koncze polaczenie (urywam)
                theOutput = "konczymy polaczenie";
            }else{
                // podano zly komunikat, powtarzam operacje i zmieniam stan na czekaj
                theOutput = "Prosze podac poprawny numer polecenia. Wpisz tylko liczbe calkowita od 1 do 4! \n " +
                        "Spróbuj ponownie!";
                state = CZEKAJ;
            }

        }else if(state == POBIERANIE) {
            //przeslemy teraz plik
            int fileNumber = Integer.parseInt(theInput);

            theOutput = "SEND_FILE" + fileNumber;

            state = CZEKAJ;

        }else if(state == WYSYLANIE) {

            if(theInput.startsWith("FILE_SENT")){
                theOutput = "Serwer- Otrzymalem plik! Wpisz cokolwiek by kontynuowac.";
                state = CZEKAJ;
            }else if(theInput.equals("abort")){
                state = CZEKAJ;
            }else if(theInput.matches("[0-9]+")){
                theOutput = "WAIT";
            }




        }

        return theOutput;
    }



    public String getFileName(int fileNumber){
        return mapOfFiles.get(fileNumber).getName();
    }

    public byte[] getFileByteArray(int fileNumber) throws IOException {

        String fileName = mapOfFiles.get(fileNumber).getName();

        Path filePath = FileSystems.getDefault().getPath(Main.getAppFilesPath(), fileName);

        byte[] buffer = Files.readAllBytes(filePath);


        return buffer;
    }

    public String displayInstructions(){
        String menuInstructions = "Witaj! Wybierz numer polecenia, ktore chcesz przetworzyc!\n" +
                "1 - Wyswietl pliki polaczonego hosta\n"+
                "2 - Pobierz plik\n" +
                "3 - Wyslij plik na serwer\n" +
                "4 - Zakoncz polaczenie.";
        return menuInstructions;
    }

    public String displayFiles(){
        //fetchFilesMap();
        String message;
        message = "Oto lista plikow.\n";

        for(int i = 0; i < mapOfFiles.size(); i++){
            // theOutput = theOutput + filesMap.get(i).getName() + "\n";
            message = message + i + " ---- " + mapOfFiles.get(i).getName() + "\n";
        }
        return message;
    }

    public void fetchFilesMap(){

        this.mapOfFiles = new HashMap<>();
        String mPath = Main.getAppFilesPath();

        File dir = new File(mPath);
        File[] myFiles = dir.listFiles();
        for(int i = 0; i < myFiles.length; i++){
            if(myFiles[i].isFile()){
                this.mapOfFiles.put(i, myFiles[i]);
            }
        }
    }

    public HashMap<Integer, File> getMapOfFiles() {
        return mapOfFiles;
    }

}
