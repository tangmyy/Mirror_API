package com.example.mirror.controller;

import com.example.mirror.entity.Galleries;
import com.example.mirror.entity.Images;
import com.example.mirror.entity.Users;
import com.example.mirror.mapper.GalleriesMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class GallerController {
    @Autowired
    private GalleriesMapper galleriesMapper;

    @GetMapping("/api/images/public")
    public List<Galleries> getAllPublicImages() {
        return galleriesMapper.selectAllGalleries();
    }
    @GetMapping("/api/images/search")
    public List<Galleries> searchGalleries(@RequestParam("keyword") String keyword,HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        return galleriesMapper.searchGalleriesByKeyword(keyword);
    }
}
