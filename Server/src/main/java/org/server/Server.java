package org.server;

import org.slf4j.Logger;
import org.slf4j.Logger.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int HTTPSERVER_PORT = 9351;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) {
        Server server =  new Server();
        server.start();

    }
    public void start(){
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(HTTPSERVER_PORT);
            System.out.println("[HTTPSERVER START]");
            while(true){
                System.out.println("[Client Waiting]");
                Socket socket = serverSocket.accept();
                ServerThread ServerThread = new ServerThread(socket);
                ServerThread.start();
            }
        } catch (IOException e) {
            logger.error("[IOExcption]", e);
        }
    }
}