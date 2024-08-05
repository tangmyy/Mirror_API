package com.example.mirror.entity;

import java.util.Date;

public class BigModel {
   private Long id;               // 唯一标识符
   private String name;           // 模型名称
   private String description;    // 模型描述
   private Date createdDate;      // 创建时间
   private Date updatedDate;      // 更新时间
   private String data;           // 模型的数据内容，可能是JSON字符串或其他数据格式

   // 默认构造函数
   public BigModel() {
   }

   // 带参数的构造函数
   public BigModel(Long id, String name, String description, Date createdDate, Date updatedDate, String data) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.createdDate = createdDate;
      this.updatedDate = updatedDate;
      this.data = data;
   }

   // Getters and Setters
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Date getCreatedDate() {
      return createdDate;
   }

   public void setCreatedDate(Date createdDate) {
      this.createdDate = createdDate;
   }

   public Date getUpdatedDate() {
      return updatedDate;
   }

   public void setUpdatedDate(Date updatedDate) {
      this.updatedDate = updatedDate;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   @Override
   public String toString() {
      return "BigModel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", createdDate=" + createdDate +
            ", updatedDate=" + updatedDate +
            ", data='" + data + '\'' +
            '}';
   }
}
