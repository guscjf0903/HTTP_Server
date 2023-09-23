package org.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.share.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ServerHandler {
    static HashMap<String, String> textMap = new HashMap<>();

    public static void GetMethod(OutputStream out, String[] urlid, String urlType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ReentrantLock lock = new ReentrantLock();

        if (urlType.equals("time")) {
            LocalDateTime now = LocalDateTime.now();
            String jsonBodyString = objectMapper.writeValueAsString(now.toString()); // " " 빠져있는상황 기억
            String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS.getValue(), "application/json", jsonBodyString.length());
            String responseMessage = responseHeader + jsonBodyString;
            out.write(responseMessage.getBytes());
            out.flush();

        }else if(urlType.startsWith("text")){
            StringBuilder jsonBody = new StringBuilder();
            for(String id: urlid){
                jsonBody.append(textMap.get(id)).append(",");
            }
            String jsonBodyString = objectMapper.writeValueAsString(jsonBody.toString());
            String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS.getValue(), "application/json", jsonBodyString.length());
            String responseMessage = responseHeader + jsonBodyString;
            out.write(responseMessage.getBytes());
            out.flush();

        }else if(urlType.startsWith("image")){
            for(String id: urlid){
                File file = new File("/Users/hyunchuljung/Desktop/ServerFolder/" + id);
                BufferedImage image = ImageIO.read(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpeg", byteArrayOutputStream);
                byteArrayOutputStream.flush();
                byte[] imageInByte = byteArrayOutputStream.toByteArray();
                String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS_CREATED.getValue(), "image/jpeg", imageInByte.length);
                out.write(responseHeader.getBytes());
                out.write(imageInByte);
                out.flush();
            }
        }
        else{
            MakeFailResponseMessage(out);
        }

    }

    public static void PostMethod(OutputStream out, String url, String requestBody, String urlType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if(urlType.equals("text")){
            textMap.put(url, objectMapper.readValue(requestBody, String.class));
        } else if(url.startsWith("image")){
            //아직 미구현
        } else{
            MakeFailResponseMessage(out);
        }
        String jsonBodyString = objectMapper.writeValueAsString("post success");
        String jsonHeaderString =  getMakeHeaderResponseMessage(StatusEnum.SUCCESS_CREATED.getValue(), "application/json", jsonBodyString.length());
        String responseMessage = jsonHeaderString + jsonBodyString;
        out.write(responseMessage.getBytes());
        out.flush();
    }

    public static void DeleteMethod(OutputStream out, String[] DeleteIds, String urlType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if(urlType.equals("text")){
            for(String id: DeleteIds){
                textMap.remove(id);
            }
            String deleteMessage = "delete success";
            String jsonBodyString = objectMapper.writeValueAsString(deleteMessage);
            String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS.getValue(), "application/json", jsonBodyString.length());
            String responseMessage = responseHeader + jsonBodyString;
            out.write(responseMessage.getBytes());
            out.flush();
        } else if(urlType.equals("image")){
            //아직 미구현
        } else{
            MakeFailResponseMessage(out);
        }

    }



        public static void MakeFailResponseMessage(OutputStream out) throws IOException {
        String FailMessage = "404 Error";
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBodyString = objectMapper.writeValueAsString(FailMessage);
        String status = StatusEnum.FAIL.getValue();
        String contentsType = "Content-Type: application/json \r\n";
        String contentsLength = "Content-Length: " + jsonBodyString.length() + "\r\n\r\n";

        String responseFailMessage = status + contentsType + contentsLength + jsonBodyString;
        out.write(responseFailMessage.getBytes());
        out.flush();
    }


    public static String getMakeHeaderResponseMessage(String reponseStatus, String ContentsType, int ContentsLength) {
        String status = reponseStatus;
        String contentsType = "Content-Type: " + ContentsType + "\r\n";
        String contentsLength = "Content-Length: " + ContentsLength + "\r\n\r\n";

        return status + contentsType + contentsLength;
    }
}
