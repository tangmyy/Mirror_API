package com.example.mirror_realm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.mirror_realm.repository.TestImageRepository;
import com.example.mirror_realm.model.TestImage;
import java.util.List;

@Controller
public class ImageController {

   @Autowired
   private TestImageRepository testImageRepository;

   @GetMapping("/images")
   public String images(Model model) {
      List<TestImage> images = testImageRepository.findAll();
      System.out.println("Images: " + images); // 添加调试日志
      model.addAttribute("images", images);
      return "images";
   }
}
