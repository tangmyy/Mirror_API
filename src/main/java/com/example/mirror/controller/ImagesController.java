package com.example.mirror.controller;

import com.example.mirror.entity.Images;
import com.example.mirror.entity.Users;
import com.example.mirror.mapper.GalleriesMapper;
import com.example.mirror.mapper.ImagesMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class ImagesController {

    @Autowired
    private ImagesMapper imagesMapper;

    @Autowired
    private GalleriesMapper galleriesMapper;

    @Value("${file.upload-path}")
    private String uploadDir;

    @PutMapping("/api/images")
    public String updateImage(@RequestParam("id") int id,
                              @RequestParam("description") String description,
                              @RequestParam("tags") String tagsJson,
                              @RequestParam("isPublic") boolean isPublic,
                              HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "用户未登录";
        }

        // 通过ID查询图片
        Images image = imagesMapper.selectById(id);
        if (image == null) {
            return "图片未找到";
        }

        try {
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
            // 如果 public 状态变为 true，插入 galleries 表中
            if (isPublic) {
                saveOrUpdatePublicImageToGalleries(image);
            }
            return "更新成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "更新失败";
        }
    }

    @GetMapping("/api/images")
    public List<Images> findAllImages(){
        return imagesMapper.selectAllImages();
    }

    @GetMapping("/api/images/user")
    public List<Images> findImagesByUserId(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user != null) {
            int userId = user.getId();
            return imagesMapper.selectImagesByUserId(userId);
        } else {
            return null;
        }
    }

    @PostMapping("/api/images/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file,
                              @RequestParam("description") String description,
                              @RequestParam("isPublic") boolean isPublic,
                              @RequestParam("tags") String tagsJson,
                              HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "用户未登录";
        }

        if (file.isEmpty()) {
            return "上传失败，请选择一个文件";
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            saveFile(file, newFileName);

            Images image = new Images();
            image.setUserid(user.getId());
            image.setUrl("/images/" + newFileName);
            image.setDescription(description);
            image.setisPublic(isPublic);
            image.setCreated(LocalDateTime.now());

            // 检查点：输出 tagsJson 内容
            System.out.println("tagsJson: " + tagsJson);

            // 使用ObjectMapper将JSON数组转换为String
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> tagsList = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
            String tagsString = String.join(";", tagsList);
            image.setTags(tagsString);

            imagesMapper.insert(image);
            // 如果图片是公开的，则将其插入到 galleries 表中
            saveOrUpdatePublicImageToGalleries(image);

            return "上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
    }
    @DeleteMapping("/api/images")
    public String deleteImage(@RequestParam("id") int id) {
        // 检查图片是否在 galleries 表中存在
        if (imagesMapper.countImageInGalleries(id) > 0) {
            galleriesMapper.deleteFromGalleries(id);
        }
        // 删除 images 表中的图片记录
        imagesMapper.deleteById(id);
        return "删除成功";
    }

    private void saveFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        System.out.println(uploadPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());
    }

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
