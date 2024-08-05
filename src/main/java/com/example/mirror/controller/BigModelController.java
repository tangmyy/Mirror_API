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
      String question = request.get("question");
      Map<String, Object> response = new HashMap<>();
      try {
         // 使用 BigModelServiceImpl 发送问题并获取答案
         String answer = bigModelServiceImpl.askQuestion(question);
         response.put("success", true);
         response.put("answer", answer);
      } catch (Exception e) {
         response.put("success", false);
         response.put("error", e.getMessage());
      }
      return response;
   }




}
