package com.example.mirror.entity;

import java.util.Date;

public class BigModel {
   private Long id;               // 唯一标识符
   private String name;           // 模型名称
   private String description;    // 模型描述
   private Date createdDate;      // 创建时间
   private Date updatedDate;      // 更新时间
   private String data;           // 模型的数据内容，可能是JSON字符串或其他数据格式
}
