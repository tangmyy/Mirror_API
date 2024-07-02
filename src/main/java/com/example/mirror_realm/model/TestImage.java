package com.example.mirror_realm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "test")
public class TestImage {

   @Id
   private int id;
   private String imgpath;

   // Getters and setters
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getImgpath() {
      return imgpath;
   }

   public void setImgpath(String imgpath) {
      this.imgpath = imgpath;
   }
}
