package com.example.mirror.service;

import com.example.mirror.entity.Images;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImagesService {
   String uploadImage(MultipartFile file, String description, boolean isPublic, String tagsJson, int userId) throws IOException;
   String deleteImage(int id);
   String updateImage(int id, String description, String tagsJson, boolean isPublic, int userId) throws IOException;
   List<Images> findAllImages();
   List<Images> findImagesByUserId(int userId);

}
