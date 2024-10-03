package com.example.mirror.controller;

import com.example.mirror.service.Impl.BigModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/bigmodel")
public class BigModelController {

   @Autowired
   private BigModelServiceImpl bigModelServiceImpl;

   @PostMapping("/ask")
   public Map<String, Object> askQuestion(@RequestBody Map<String, String> request) {
      Map<String, Object> response = new HashMap<>();
      try {
         String question = request.get("question");         // 从请求体中获取问题
         String answer = bigModelServiceImpl.askQuestion(question);         // 调用BigModelServiceImpl的askQuestion方法

         // 将结果返回给前端
         response.put("success", true);
         response.put("answer", answer);
      } catch (Exception e) {
         response.put("success", false);
         response.put("error", e.getMessage());
      }
      return response;
   }


}
