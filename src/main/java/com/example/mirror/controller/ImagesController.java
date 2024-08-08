package com.example.mirror.controller;

import com.example.mirror.entity.Images;
import com.example.mirror.entity.Users;

import com.example.mirror.service.ImagesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/images")
public class ImagesController {

   @Autowired
   private ImagesService imagesService;

   // Post 增加 上传图片
   @PostMapping("/upload")
   public String uploadImage(@RequestParam("file") MultipartFile file,
                             @RequestParam("description") String description,
                             @RequestParam("isPublic") boolean isPublic,
                             @RequestParam("tags") String tagsJson,
                             HttpSession session) {
      Users user = (Users) session.getAttribute("user");
      if (user == null) {
         return "用户未登录";
      }
      try {
         return imagesService.uploadImage(file, description, isPublic, tagsJson, user.getId());
      } catch (IOException e) {
         e.printStackTrace();
         return "上传失败";
      }
   }

   // Delete 删除 删除图片
   @DeleteMapping
   public String deleteImage(@RequestParam("id") int id) {
      return imagesService.deleteImage(id);
   }

    // Put 修改 修改图片属性
    @PutMapping
    public String updateImage(@RequestParam("id") int id,
                              @RequestParam("description") String description,
                              @RequestParam("tags") String tagsJson,
                              @RequestParam("isPublic") boolean isPublic,
                              HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "用户未登录";
        }
        try {
            return imagesService.updateImage(id, description, tagsJson, isPublic, user.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return "更新失败";
        }
    }

    // Get 查询 查询所有图片
    @GetMapping
    public List<Images> findAllImages() {
        return imagesService.findAllImages();
    }

   // Get 查询 查询个人图片
    @GetMapping("/user")
    public List<Images> findImagesByUserId(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user != null) {
            return imagesService.findImagesByUserId(user.getId());
        } else {
            return null;
        }
    }

}
