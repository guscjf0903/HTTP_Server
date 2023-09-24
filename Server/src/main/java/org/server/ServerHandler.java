package org.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.share.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ServerHandler {
    static LinkedHashMap<String, String> textMap = new LinkedHashMap<>();

    public static void GetMethod(OutputStream out, String[] urlid, String urlType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        switch (urlType) {
            case "time" -> {
                LocalDateTime now = LocalDateTime.now();
                String jsonBodyString = objectMapper.writeValueAsString(now.toString());
                String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS.getValue(), "application/json", jsonBodyString.length());
                String responseMessage = responseHeader + jsonBodyString;
                out.write(responseMessage.getBytes());
                out.flush();

            }
            case "text" -> {
                StringBuilder jsonBody = new StringBuilder();
                for (String id : urlid) {
                    if (textMap.get(id) == null) {
                        MakeFailResponseMessage(out);
                        continue;
                    }
                    jsonBody.append(textMap.get(id)).append(",");
                }
                if (!jsonBody.isEmpty()) {
                    jsonBody.deleteCharAt(jsonBody.length() - 1); // 마지막 ',' 제거
                }
                String jsonBodyString = objectMapper.writeValueAsString(jsonBody.toString());
                String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS.getValue(), "application/json", jsonBodyString.length());
                String responseMessage = responseHeader + jsonBodyString;
                out.write(responseMessage.getBytes());
                out.flush();

            }
            case "image" -> {
                for (String id : urlid) {
                    File file = new File("/Users/hyunchuljung/Desktop/ServerFolder/" + id);
                    System.out.println(id);
                    if (!file.exists()) { //만약 경로에 image가 없을때 404에러
                        System.out.println("??");
                        MakeFailResponseMessage(out);
                        continue;
                    }
                    String formatName = id.substring(id.lastIndexOf(".") + 1); //이미지 형식. ex) jpg, png
                    BufferedImage image = ImageIO.read(file);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, formatName, byteArrayOutputStream);
                    byteArrayOutputStream.flush();
                    byte[] imageInByte = byteArrayOutputStream.toByteArray();
                    String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS_CREATED.getValue(), "image/jpeg", imageInByte.length);
                    out.write(responseHeader.getBytes());
                    out.write(imageInByte);
                    out.flush();
                }
            }
            case "textall" -> {
                StringBuilder jsonBody = new StringBuilder();
                for (String id : textMap.keySet()) {
                    jsonBody.append(textMap.get(id)).append(",");
                }
                if (!jsonBody.isEmpty()) {
                    jsonBody.deleteCharAt(jsonBody.length() - 1); // 마지막 ',' 제거
                }
                String jsonBodyString = objectMapper.writeValueAsString(jsonBody.toString());
                String responseHeader = getMakeHeaderResponseMessage(StatusEnum.SUCCESS.getValue(), "application/json", jsonBodyString.length());
                String responseMessage = responseHeader + jsonBodyString;
                out.write(responseMessage.getBytes());
                out.flush();
            }
            default -> MakeFailResponseMessage(out);
        }
        lock.unlock();
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

    public static void MakeNoContentsResponseMessage(OutputStream out) throws IOException {
        String FailMessage = "No Contents";
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBodyString = objectMapper.writeValueAsString(FailMessage);
        String status = StatusEnum.SUCCESS_NOCONTENT.getValue();
        String contentsType = "Content-Type: application/json \r\n";
        String contentsLength = "Content-Length: " + jsonBodyString.length() + "\r\n\r\n";

        String responseFailMessage = status + contentsType + contentsLength + jsonBodyString;
        out.write(responseFailMessage.getBytes());
        out.flush();
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
        String contentsType = "Content-Type: " + ContentsType + "\r\n";
        String contentsLength = "Content-Length: " + ContentsLength + "\r\n\r\n";

        return reponseStatus + contentsType + contentsLength;
    }
}
