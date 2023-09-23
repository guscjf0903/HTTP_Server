package org.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;

import static org.server.HTTPRequestParser.*;
import static org.server.ServerHandler.*;

public class ServerThread extends Thread {
    private static final int MAXBUFFERSIZE = 8000;
    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);
    public static Map<OutputStream, String> clientMap = Collections.synchronizedMap(new HashMap<OutputStream, String>());
    public final Socket socket;
    private InputStream in;
    private OutputStream out;

    public ServerThread(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    public void run() {
        try {
            while (true) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String requestLine = bufferedReader.readLine();
                if (requestLine == null) {
                    continue;
                }
                System.out.println(requestLine);
                String[] requestLineArray = requestLineAnalysis(requestLine);// 빈칸을 기준으로 리퀘스트라인 분리
                String method = requestLineArray[0];
                String url = requestLineArray[1];
                System.out.println(url);
                String version = requestLineArray[2];

                switch (method) {
                    case "GET":
                        if(url.contains("text")){
                            String getUrlId = splitUrlId(url);
                            if(getUrlId.isEmpty()){//text id가 없을때
                                MakeFailResponseMessage(out);
                                break;
                            }
                            String[] urlIdArray = getUrlId.split(",");
                            GetMethod(out,urlIdArray,"text");

                        }else if(url.contains("image")){
                            String getUrlId = splitUrlId(url);
                            String[] urlIdArray = getUrlId.split(",");
                            if(getUrlId.isEmpty()){ //image id가 없을때
                                MakeFailResponseMessage(out);
                                break;
                            }
                            GetMethod(out, urlIdArray,"image");
                        }else if(url.contains("time")) {
                            GetMethod(out, null, "time");
                        }else{
                            MakeFailResponseMessage(out);
                        }
                        break;

                    case "POST":
                        String PostUrlId = splitUrlId(url);
                        String requestBody = readHTTPRequestBody(bufferedReader);
                        if(url.contains("text")){
                            PostMethod(out, PostUrlId,requestBody,"text");
                        }else if(url.contains("image")){
                            PostMethod(out, PostUrlId,requestBody,"image");
                        }else{
                            MakeFailResponseMessage(out);
                        }
                        break;

                    case "DELETE":
                        if(url.contains("text")){
                            String DeleteUrlId = splitUrlId(url);
                            if(DeleteUrlId.isEmpty()){//text id가 없을때
                                MakeFailResponseMessage(out);
                                break;
                            }
                            String[] DeleteIds = DeleteUrlId.split(",");
                            DeleteMethod(out, DeleteIds,"text");
                        }else if(url.contains("image")){
                            //아직 미구현
                        }else{
                            MakeFailResponseMessage(out);
                        }
                        break;

                    default: //메서드가 아무것도없을때 4xx에러
                        MakeFailResponseMessage(out);
                        break;
                }
            }
        } catch (IOException e) {
            logger.error("IOException", e);
        }


    }

    public String[] requestLineAnalysis(String requestLine) throws IOException {
        return requestLine.split(" ");
    }

    public String splitUrlId(String url) {
        if (url.startsWith("/text")) {
            return url.substring(6);
        } else if (url.startsWith("/image")) {
            return url.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }


}
