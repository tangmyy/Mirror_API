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
import java.util.concurrent.CompletableFuture;

@Service
public class BigModelServiceImpl extends WebSocketListener implements BigModelService {
   public static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";      // 接口的基础URL，用于连接Spark大模型的API
   public static final String appid = "fda264df";                                      // 应用的App ID，用于身份验证
   public static final String apiSecret = "NDhmZjY0YzFiYjhkYTM1NWUxYzIxNzM3";          // 应用的API密钥，用于身份验证中的签名生成
   public static final String apiKey = "52c675637d3b3d2c959abd808ca1107c";             // 应用的API Key，用于身份验证

   public static List<RoleContent> historyList = new ArrayList<>();                    // 用于存储对话历史记录的列表，每条记录包括角色和内容

   public static String totalAnswer = "";                                              // 用于存储从大模型接收到的完整回答内容
   public static String NewQuestion = "我爱我的祖国";                                   // 用于存储新的用户问题，默认初始值为“我爱我的祖国”

   public static final Gson gson = new Gson();                                         // Gson实例，用于在JSON和Java对象之间进行转换
   private String userId;
   private Boolean wsCloseFlag;                                                        // WebSocket关闭标志，用于指示WebSocket连接是否应关闭
   private static Boolean totalFlag = true;                                            // 标志是否可以接受新的用户输入，控制主循环中的输入和处理逻辑

   // 一个内部线程类，用于处理 WebSocket 的发送请求和接收响应。
   // 这个类中的 run() 方法负责构建请求 JSON 并通过 WebSocket 发送，同时等待响应并处理响应数据
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
            // todo
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

   public void start() throws Exception {
      while (true) {
         if (totalFlag) {
            Scanner scanner = new Scanner(System.in);    // 创建一个 Scanner 对象，用于从标准输入（控制台）读取用户输入 ?:和scanf有什么区别
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

   // 在 WebSocket 连接打开时调用，启动 MyThread 线程来处理请求
   @Override
   public void onOpen(WebSocket webSocket, Response response) {
      super.onOpen(webSocket, response);
      System.out.print("大模型：");
      MyThread myThread = new MyThread(webSocket);
      myThread.start();
   }

   // 现有的消息处理逻辑...(tmy)
   // 处理从 WebSocket 接收到的消息 解析消息内容并输出，同时更新历史记录列表和 WebSocket 关闭标志
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

   // 在 WebSocket 连接失败时调用，输出错误信息并尝试关闭连接
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

   // 生成经过身份验证的 URL，用于 WebSocket 连接。该方法使用 HMAC-SHA256 算法生成签名并构建完整的请求 URL
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

   // 检查是否可以添加新的历史记录 如果历史记录的总长度超过 12000 个字符，则删除最早的五条记录
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
/**********************************************************************************************************************/

   //发送一个新的问题并返回答案。此方法首先设置新问题的内容，然后通过 WebSocket 发送请求，等待响应并返回答案
   public String askQuestion(String question) throws Exception {
      NewQuestion = question;
      String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
      OkHttpClient client = new OkHttpClient.Builder().build();
      String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
      Request request = new Request.Builder().url(url).build();
      totalAnswer = "";
      totalFlag = false; // 重置 totalFlag
      WebSocket webSocket = client.newWebSocket(request, new BigModelServiceImpl("1", false));

      while (!totalFlag) {          // 等待回答完成
         Thread.sleep(100);
      }
      return totalAnswer;
   }

   /**********************************************************************************************************************/

   public BigModelServiceImpl() {
   }

   public BigModelServiceImpl(String userId, Boolean wsCloseFlag) {
      this.userId = userId;
      this.wsCloseFlag = wsCloseFlag;
   }

   class JsonParse {                // 内部类，用于解析 JSON 响应的数据结构。包含 Header 和 Payload 子类
      Header header;
      Payload payload;
   }

   class Header {                   // 内部类，用于表示 JSON 响应中的头部信息，包含响应代码、状态和会话 ID
      int code;
      int status;
      String sid;
   }

   class Payload {                  // 内部类，用于表示 JSON 响应中的有效负载，包含选择列表
      Choices choices;
   }

   class Choices {                  // 内部类，用于表示 JSON 响应中的有效负载，包含选择列表
      List<Text> text;
   }

   class Text {                     // 内部类，用于表示文本内容，包含角色和文本内容
      String role;
      String content;
   }

   class RoleContent {              // 内部类，用于表示角色内容，包含角色和内容的 getter 和 setter 方法
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

