package org.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread{
    private static final int MAXBUFFERSIZE = 8000;
    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);
    public static Map<OutputStream, String> clientMap = Collections.synchronizedMap(new HashMap<OutputStream, String>());
    private InputStream in;
    private OutputStream out;
    private final Socket socket;

    public ServerThread(Socket socket){
        this.socket = socket;
        try{
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    public void run(){
        while(true){
            byte[] clientByteData = new byte[MAXBUFFERSIZE];



        }
    }


}
