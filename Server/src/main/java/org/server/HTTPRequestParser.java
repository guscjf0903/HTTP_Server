package org.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class HTTPRequestParser {
    public static String readHTTPRequestBody(BufferedReader bufferedReader) throws IOException {
        String line = "";
        int contentLength = 0;
        while((line = bufferedReader.readLine()) != null){
            if(line.isEmpty()){
                break;
            }
            if (line.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring("Content-Length: ".length()));
            }
        }
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
//        while(byteread < contentLength){
//            int read = bufferedReader.read(buffer,byteread, contentLength - byteread);
//            if(read == -1){
//                break;
//            }
//        }
        // StringBuilder bodyBuilder = new StringBuilder();

//        while ((line = bufferedReader.readLine()) != null) {
//            bodyBuilder.append(line);
//            bodyBuilder.append("\n"); // 각 라인을 줄 바꿈 문자와 함께 추가
//        }
//        System.out.println("test");
//        return bodyBuilder.toString().substring(0, contentLength);
        return new String(buffer);
    }
}
