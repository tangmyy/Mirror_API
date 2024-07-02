package com.example.mirror_realm.controller;

import com.example.mirror_realm.Image;
import com.example.mirror_realm.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

   @Autowired
   private ImageRepository imageRepository;

   @GetMapping("/")
   public String home(Model model) {
      List<Image> images = imageRepository.findAll();
      images.forEach(image -> System.out.println("Image path: " + image.getImgpath())); // Debugging line
      model.addAttribute("images", images);
      return "index"; // this will resolve to /WEB-INF/jsp/index.jsp
   }

}
