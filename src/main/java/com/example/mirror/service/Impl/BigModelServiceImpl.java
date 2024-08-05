package com.example.mirror.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mirror.service.BigModelService;
import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BigModelServiceImpl extends WebSocketListener implements BigModelService {
   public static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";
   public static final String appid = "fda264df";
   public static final String apiSecret = "NDhmZjY0YzFiYjhkYTM1NWUxYzIxNzM3";
   public static final String apiKey = "52c675637d3b3d2c959abd808ca1107c";

   public static List<RoleContent> historyList = new ArrayList<>();
   public static String totalAnswer = "";
   public static String NewQuestion = "我爱我的祖国";
   public static final Gson gson = new Gson();
   private String userId;
   private Boolean wsCloseFlag;
   private static Boolean totalFlag = true;

   public BigModelServiceImpl() {
   }

   public BigModelServiceImpl(String userId, Boolean wsCloseFlag) {
      this.userId = userId;
      this.wsCloseFlag = wsCloseFlag;
   }

   public void start() throws Exception {
      while (true) {
         if (totalFlag) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("我：");
            totalFlag = false;
            NewQuestion = scanner.nextLine();
            String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            for (int i = 0; i < 1; i++) {
               totalAnswer = "";
               WebSocket webSocket = client.newWebSocket(request, new BigModelServiceImpl(i + "", false));
            }
         } else {
            Thread.sleep(200);
         }
      }
   }

   public static boolean canAddHistory() {
      int history_length = 0;
      for (RoleContent temp : historyList) {
         history_length = history_length + temp.content.length();
      }
      if (history_length > 12000) {
         for (int i = 0; i < 5; i++) {
            historyList.remove(0);
         }
         return false;
      } else {
         return true;
      }
   }

   class MyThread extends Thread {
      private WebSocket webSocket;

      public MyThread(WebSocket webSocket) {
         this.webSocket = webSocket;
      }

      public void run() {
         try {
            JSONObject requestJson = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("app_id", appid);
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

            JSONObject parameter = new JSONObject();
            JSONObject chat = new JSONObject();
            chat.put("domain", "generalv2");
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 4096);
            parameter.put("chat", chat);

            JSONObject payload = new JSONObject();
            JSONObject message = new JSONObject();
            JSONArray text = new JSONArray();

            if (historyList.size() > 0) {
               for (RoleContent tempRoleContent : historyList) {
                  text.add(JSON.toJSON(tempRoleContent));
               }
            }

            RoleContent roleContent = new RoleContent();
            roleContent.role = "user";
            roleContent.content = NewQuestion;
            text.add(JSON.toJSON(roleContent));
            historyList.add(roleContent);

            message.put("text", text);
            payload.put("message", message);

            requestJson.put("header", header);
            requestJson.put("parameter", parameter);
            requestJson.put("payload", payload);
            webSocket.send(requestJson.toString());

            while (true) {
               Thread.sleep(200);
               if (wsCloseFlag) {
                  break;
               }
            }
            webSocket.close(1000, "");
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void onOpen(WebSocket webSocket, Response response) {
      super.onOpen(webSocket, response);
      System.out.print("大模型：");
      MyThread myThread = new MyThread(webSocket);
      myThread.start();
   }

   // 现有的消息处理逻辑...(tmy)
   @Override
   public void onMessage(WebSocket webSocket, String text) {
      JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
      if (myJsonParse.header.code != 0) {
         System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
         System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
         webSocket.close(1000, "");
      }
      List<Text> textList = myJsonParse.payload.choices.text;
      for (Text temp : textList) {
         System.out.print(temp.content);
         totalAnswer = totalAnswer + temp.content;
      }
      if (myJsonParse.header.status == 2) {
         System.out.println();
         System.out.println("*************************************************************************************");
         if (canAddHistory()) {
            RoleContent roleContent = new RoleContent();
            roleContent.setRole("assistant");
            roleContent.setContent(totalAnswer);
            historyList.add(roleContent);
         } else {
            historyList.remove(0);
            RoleContent roleContent = new RoleContent();
            roleContent.setRole("assistant");
            roleContent.setContent(totalAnswer);
            historyList.add(roleContent);
         }
         wsCloseFlag = true;        // 设置wsCloseFlag为true，表示WebSocket连接可以关闭
         totalFlag = true;          // 设置totalFlag为true，表示可以接受新的用户输入
      }
   }

   @Override
   public void onFailure(WebSocket webSocket, Throwable t, Response response) {
      super.onFailure(webSocket, t, response);
      try {
         if (null != response) {
            int code = response.code();
            System.out.println("onFailure code:" + code);
            System.out.println("onFailure body:" + response.body().string());
            if (101 != code) {
               System.out.println("connection failed");
               System.exit(0);
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
      URL url = new URL(hostUrl);
      SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      format.setTimeZone(TimeZone.getTimeZone("GMT"));
      String date = format.format(new Date());
      String preStr = "host: " + url.getHost() + "\n" +
            "date: " + date + "\n" +
            "GET " + url.getPath() + " HTTP/1.1";
      Mac mac = Mac.getInstance("hmacsha256");
      SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
      mac.init(spec);
      byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
      String sha = Base64.getEncoder().encodeToString(hexDigits);
      String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
      HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder()
            .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
            .addQueryParameter("date", date)
            .addQueryParameter("host", url.getHost())
            .build();
      return httpUrl.toString();
   }

/**********************************************************************************************************************/

   public String askQuestion(String question) throws Exception {
      NewQuestion = question;
      String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
      OkHttpClient client = new OkHttpClient.Builder().build();
      String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
      Request request = new Request.Builder().url(url).build();
      totalAnswer = "";
      totalFlag = false; // 重置 totalFlag
      WebSocket webSocket = client.newWebSocket(request, new BigModelServiceImpl("1", false));

      // 等待回答完成
      while (!totalFlag) {
         Thread.sleep(100);
      }
      return totalAnswer;
   }


   /**********************************************************************************************************************/


   class JsonParse {
      Header header;
      Payload payload;
   }

   class Header {
      int code;
      int status;
      String sid;
   }

   class Payload {
      Choices choices;
   }

   class Choices {
      List<Text> text;
   }

   class Text {
      String role;
      String content;
   }

   class RoleContent {
      String role;
      String content;

      public String getRole() {
         return role;
      }

      public void setRole(String role) {
         this.role = role;
      }

      public String getContent() {
         return content;
      }

      public void setContent(String content) {
         this.content = content;
      }
   }
}

