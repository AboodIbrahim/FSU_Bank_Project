package org.app.fsu_bank_project.server;

import org.app.fsu_bank_project.db.FSUGenBankDB;
import org.app.fsu_bank_project.db.HandleClients;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class Server extends Thread{
    private InetAddress addr;
    public static FSUGenBankDB dbconnection;

    static {
        try {
            dbconnection = new FSUGenBankDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    int clientNumber = 0;
    ServerSocket serverSocket;
    public Server(int port) throws UnknownHostException, SQLException {
        dbconnection = new FSUGenBankDB();
        this.addr = InetAddress.getByName("0.0.0.0");
        System.out.println();
    }

    public void start() {

        try {
            serverSocket = new ServerSocket(1234, 0, addr);
            System.out.println("Server is open!");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client " + (++clientNumber) + " connected.");
                HandleClients clientHandler = new HandleClients(clientSocket,serverSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws UnknownHostException, SQLException {
        Server server = new Server(1234);
        server.start();
    }
}
