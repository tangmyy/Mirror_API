package com.example.mirror.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
@TableName("galleries")
public class Galleries {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userid;
    private Integer imageid;
    private String url;
    private LocalDateTime created;
    private String description; // 新增字段
    private String tags;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getImageid() {
        return imageid;
    }

    public void setImageid(Integer imageid) {
        this.imageid = imageid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    @Override
    public String toString() {
        return "Galleries{" +
                "id=" + id +
                ", userid=" + userid +
                ", url=" + url + '\'' +
                ", imageid=" + imageid    +
                ", created=" + created +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
