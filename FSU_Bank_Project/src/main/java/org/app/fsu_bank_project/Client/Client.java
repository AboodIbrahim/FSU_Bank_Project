package org.app.fsu_bank_project.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket clientSocket;

    public Client() {
        try {
            clientSocket = new Socket("localhost", 1234);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            handleRegistration(in, out);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRegistration(BufferedReader in, PrintWriter out) throws IOException {
        System.out.println(in.readLine()); // print ==> "Enter email:"
        Scanner scanner = new Scanner(System.in);
        String sc = scanner.nextLine();
        out.println(sc);
        String response = in.readLine();
        while (response.equalsIgnoreCase("Enter email:")) {
            System.out.println("Enter a vaild Email!!"); // print ==> "Enter email:"
            out.println(scanner.nextLine());
            response = in.readLine();
        }

        if (response.equalsIgnoreCase("Password sent to your email.")) {
            System.out.println(response); //Password sent to your email.
            System.out.println(in.readLine()); //Enter password:
            out.println(scanner.nextLine());
        } else if (response.equalsIgnoreCase("Enter password:")) {
            System.out.println(response); //Enter password:
            out.println(scanner.nextLine());
        }
        System.out.println(in.readLine()); // Login successful. Do you want to change your password? (yes/no)
        String yes_no = scanner.nextLine();
        out.println(yes_no);    // yes/no
        if (yes_no.equals("yes") || yes_no.equals("y")) {
            System.out.println(in.readLine()); // enter new password
            out.println(scanner.nextLine());
            System.out.println(in.readLine()); //Password has been changed successfully.
        }
        handleServer(in, out);
    }


    private void handleServer(BufferedReader in, PrintWriter out) throws IOException {
        String list;
        Scanner scanner = new Scanner(System.in);
        do {
            list = in.readLine();
            System.out.println(list); // format list Where is the Error
        } while (!list.equals("[4]. Exit"));
        String input = scanner.nextLine();
        out.println(input);
        switch (input) {
            case "1":
                saveNewFormat(in,out);
                break;
            case "2":
                do {
                    list = in.readLine();
                    System.out.println(list); // format list
                } while (!list.equals("finished"));
                handleServer(in, out);
                break;
            case "3":
                viewDotPlot(in, out);
                break;
            case "4":
                System.out.println("Goodbye!");
                clientSocket.close();
                break;
            default:
                System.out.println("Enter a valid number please!");
                handleServer(in, out);
                break;
        }
    }

    private void saveNewFormat(BufferedReader ignore, PrintWriter out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input the file Path:");
        String path = scanner.nextLine();
        System.out.println("Please input the file Name:");
        String FileName = scanner.nextLine();
        File MyFile = new File(path);
        int FileSize = (int) MyFile.length();
        OutputStream os =clientSocket.getOutputStream();
        PrintWriter pr = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(MyFile));
        Scanner in = new Scanner(clientSocket.getInputStream());
        pr.println(FileName);
        pr.println(FileSize);
        byte[] fileByte = new byte[FileSize];
        bis.read(fileByte, 0, fileByte.length);
        os.write(fileByte, 0, fileByte.length);
        System.out.println(in.nextLine());
        os.flush();

        String check= ignore.readLine();
        if (check.equals("Choose a valid format please!")){
            System.out.println(check);
            saveNewFormat(ignore, out);
        }
        handleServer(ignore, out);
    }


    private void viewDotPlot(BufferedReader in, PrintWriter out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String list = in.readLine();
        System.out.println(list);
        while (!list.equalsIgnoreCase("finished")) {
            list = in.readLine();
            System.out.println(list); // format list
            System.out.println("...");
        }
        System.out.println(in.readLine());//Select 2 files to make a dotplot


        System.out.println("......");
        String response = in.readLine();
        while (response.equalsIgnoreCase("Enter first file name:") || response.equalsIgnoreCase("Enter a valid file name!:")){
            System.out.println(response);
            out.println(scanner.nextLine());
            response = in.readLine();
        }

        while (response.equalsIgnoreCase("Enter second file name:") || response.equalsIgnoreCase("Enter a valid second file name!:")){
            System.out.println(response);
            out.println(scanner.nextLine());
            response = in.readLine();
        }
        do {
            System.out.println(response); // dotplot
            response=in.readLine();
        } while (!response.endsWith("finished"));
        System.out.println(response);
        System.out.println("...............");
        handleServer(in, out);
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client();

    }

}
