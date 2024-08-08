package com.example.mirror.service.Impl;

import com.example.mirror.entity.Images;
import com.example.mirror.mapper.GalleriesMapper;
import com.example.mirror.mapper.ImagesMapper;
import com.example.mirror.service.ImagesService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class ImagesServiceImpl implements ImagesService {

   @Autowired
   private ImagesMapper imagesMapper;

   @Autowired
   private GalleriesMapper galleriesMapper;

   @Value("${file.upload-path}")
   private String uploadDir;

   // Post 增加 上传图片
   @Override
   public String uploadImage(MultipartFile file, String description, boolean isPublic, String tagsJson, int userId) throws IOException {
      if (file.isEmpty()) {
         return "上传失败，请选择一个文件";
      }

      String originalFileName = file.getOriginalFilename();
      String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
      String newFileName = UUID.randomUUID().toString() + fileExtension;

      saveFile(file, newFileName);

      Images image = new Images();
      image.setUserid(userId);
      image.setUrl("/images/" + newFileName);
      image.setDescription(description);
      image.setisPublic(isPublic);
      image.setCreated(LocalDateTime.now());

      System.out.println("tagsJson: " + tagsJson);          // 检查点：输出 tagsJson 内容
      ObjectMapper objectMapper = new ObjectMapper();       // 使用ObjectMapper将JSON数组转换为String

      List<String> tagsList = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
      String tagsString = String.join(";", tagsList);
      image.setTags(tagsString);
      imagesMapper.insert(image);                           // 将图片插入到  表中
      saveOrUpdatePublicImageToGalleries(image);            // 如果图片是公开的，则将其插入到 galleries 表中
      return "上传成功";
   }

   // Delete 删除 删除图片
   @Override
   public String deleteImage(int id) {
      if (imagesMapper.countImageInGalleries(id) > 0) {  // 检查图片是否在 galleries 表中存在
         galleriesMapper.deleteFromGalleries(id);
      }
      imagesMapper.deleteById(id);                       // 删除 images 表中的图片记录
      return "删除成功";
   }

   // Put 修改 修改图片属性
   @Override
   public String updateImage(int id, String description, String tagsJson, boolean isPublic, int userId) throws IOException {
      // 通过ID查询图片
      Images image = imagesMapper.selectById(id);
      if (image == null) {
         return "图片未找到";
      }
      // 初始化 ObjectMapper
      ObjectMapper objectMapper = new ObjectMapper();
      List<String> tagsList = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
      String tagsString = String.join(";", tagsList);

      image.setDescription(description);
      image.setTags(tagsString);
      image.setisPublic(isPublic);
      imagesMapper.updateImage(id, description, tagsString, isPublic);
      if (!isPublic) {
         galleriesMapper.deleteFromGalleries(id);
      }
      if (isPublic) {
         saveOrUpdatePublicImageToGalleries(image);      // 如果 public 状态变为 true，插入 galleries 表中
      }
      return "更新成功";
   }

   // Get 查询 查询所有图片
   @Override
   public List<Images> findAllImages() {
      return imagesMapper.selectAllImages();
   }

   // Get 查询 查询个人图片
   @Override
   public List<Images> findImagesByUserId(int userId) {
      return imagesMapper.selectImagesByUserId(userId);
   }

   // 保存文件到上传文件路径
   private void saveFile(MultipartFile file, String fileName) throws IOException {
      Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
      System.out.println(uploadPath);
      if (!Files.exists(uploadPath)) {
         Files.createDirectories(uploadPath);
      }
      Path filePath = uploadPath.resolve(fileName);
      file.transferTo(filePath.toFile());
   }

   // 保存 公开状态的图片 到公共相册
   private void saveOrUpdatePublicImageToGalleries(Images image) {
      if (image.getisPublic()) {
         if (imagesMapper.countImageInGalleries(image.getId()) == 0) {
            imagesMapper.insertIntoGalleries(image.getId(), image.getUserid(), image.getUrl(), image.getDescription(), image.getTags());
         } else {
            galleriesMapper.updateInGalleries(image.getId(), image.getDescription(), image.getTags());
         }
      }
   }
}
