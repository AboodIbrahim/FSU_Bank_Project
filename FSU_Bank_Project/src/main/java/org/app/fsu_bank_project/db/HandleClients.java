package org.app.fsu_bank_project.db;

import org.app.fsu_bank_project.data.FSUGenBank;
import org.app.fsu_bank_project.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.app.fsu_bank_project.server.Server.dbconnection;

public class HandleClients implements  Runnable{
    private final Socket clientSocket;
    private final ServerSocket serverSocket;
    private final FSUGenBank FSUObject = new FSUGenBank();
    private String email = "";
    private boolean loggedin = false;
    public HandleClients(Socket clientSocket, ServerSocket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            while (!loggedin) {
                String email = in.readLine();
                String pass = in.readLine();
//                if(!isEmailValid(email)){
//                    out.println("invalid email");
//                    return;
//                }
                if(!dbconnection.doesUserExist(email)){
                    System.out.println(email + " habibi");
                    String pas = generatePassword();
                    out.println("Password for login is "+pas);
                    //Server.dbconnection.sendEmail(email, pas);
                    dbconnection.registerUser(email, pas);
                }else{
                    if (dbconnection.checkPassword(email, pass)) {
                        loggedin = true;
                        this.email = email;
                        out.println("Logged in");
                    } else {
                        out.println("Wrong Info");
                    }
                }

            }
            while (true) {
                String input = in.readLine();
                if (input.equals("newpass")) {
                    String newPass = in.readLine();
                    dbconnection.updatePassword(email, newPass);
                    out.println("Password changed Successfully");

                }else if (input.equals("save")) {
                    String name = in.readLine();
                    String content = in.readLine();
                    dbconnection.saveFileToDatabase(name, content);
                }else if (input.equals("dotplot")) {
                    String firstF;
                    String secondF;
                    boolean start = true;
                    do {
                        firstF = in.readLine();
                        secondF = in.readLine();
                        if (!start) out.println("Enter a valid name!:");
                        start = false;
                    } while (!dbconnection.nameExist(firstF) || !dbconnection.nameExist(secondF));

                    FSUGenBank obj1 = new FSUGenBank();
                    obj1.readFromFile(dbconnection.retrieveFile(firstF));
                    FSUGenBank obj2 = new FSUGenBank();
                    obj2.readFromFile(dbconnection.retrieveFile(secondF));

                    String dotplot = obj1.getFasta().dotPlot(obj2.getFasta());
                    out.println(dotplot);
                }else if (input.equals("update")){
                    String s = dbconnection.reviewAllFormats();
                    out.println(s);
                }

            }

         } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isEmailValid(String email){
        if( email == null) return false;
        String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static String generatePassword() {
        Random random = new Random();
        int password = 100000 + random.nextInt(900000);
        return String.valueOf(password);
    }

    private void handleUserActions(BufferedReader in, PrintWriter out) throws IOException {
        out.println("Welcome to the FSUGenBank!");
        out.println("[x]. Enter a Number between 1 - 4:");
        out.println("[1]. Save a new format");
        out.println("[2]. Review a list of existing formats");
        out.println("[3]. Make a dotplot of 2 formats");
        out.println("[4]. Exit");

        String action = in.readLine();

        switch (action) {
            case "1":
                saveNewFormat(in, out);
                break;
            case "2":
                out.println(dbconnection.reviewAllFormats());
                handleUserActions(in, out);
                break;
            case "3":
                makeDotPlot(in, out);
                break;
            case "4":
                break;
            default:
                handleUserActions(in, out);
                break;
        }
    }

    private void saveNewFormat(BufferedReader ignore, PrintWriter out) throws IOException {
        Scanner in = new Scanner(clientSocket.getInputStream());
        InputStream is = clientSocket.getInputStream();
        PrintWriter pr = new PrintWriter(clientSocket.getOutputStream(), true);
        String FileName = in.nextLine();
        int FileSize = in.nextInt();
        FileOutputStream fos = new FileOutputStream("src/main/resources/Files/Server/"+FileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] filebyte = new byte[FileSize];

        int file = is.read(filebyte, 0, filebyte.length);
        bos.write(filebyte, 0, file);

        System.out.println("Incoming File: " + FileName);
        System.out.println("Size: " + FileSize + "Byte");
        if(FileSize == file)System.out.println("File is verified");
        else System.out.println("File is corrupted. File Recieved " + file + " Byte");
        pr.println("File Recieved SUccessfully.");
        bos.close();
        boolean exist = dbconnection.nameExist(FileName);
        Random random = new Random();
        String path = "src/main/resources/Files/Server/"+FileName;
        String format = checkFormat(path);
        if(exist){
            FileName = random.nextInt(0,100000)+FileName;
        }
        if(format.equals("EMBL")){
            FSUObject.EMBLToFSUGenBank(new File(path),
                    new File("src/main/resources/Files/Server/TempTransformer.txt"));
        }else if(format.equals("GenBank")){
            FSUObject.GenBankToFSUGenBank(new File(path),
                    new File("src/main/resources/Files/Server/TempTransformer.txt"));
        } else {
            out.println(format);
            saveNewFormat(ignore, out);
        }
        dbconnection.saveFormat("src/main/resources/Files/Server/TempTransformer.txt",FileName);
        handleUserActions(ignore, out);
    }


    private String checkFormat(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String firstLine = br.readLine();
        br.close();
        if(firstLine.contains("LOCUS")){
            return "GenBank";
        } else if (firstLine.contains("XX")) {
            return "EMBL";
        }
        else return "Choose a valid format please!";
    }


    private void makeDotPlot(BufferedReader in, PrintWriter out) throws IOException {
        out.println(dbconnection.reviewAllFormats());
        out.println("Select 2 files to make a dotplot");
        String fileName;
        boolean start = true;
        do {
            if(start)out.println("Enter first file name:");
            else out.println("Enter a valid file name!:");
            start = false;
            fileName = in.readLine();
        } while (!dbconnection.nameExist(fileName));
        FSUGenBank obj1 = new FSUGenBank();
        obj1.readFromFile(dbconnection.retrieveFile(fileName));
        start = true;
        do {
            if(start)out.println("Enter second file name:");
            else out.println("Enter a valid second file name!:");
            start = false;
            fileName = in.readLine();
        } while (!dbconnection.nameExist(fileName));

        FSUGenBank obj2 = new FSUGenBank();
        obj2.readFromFile(dbconnection.retrieveFile(fileName));

        String dotplot= obj1.getFasta().dotPlot(obj2.getFasta());

        out.println(dotplot);

        handleUserActions(in, out);
    }
}