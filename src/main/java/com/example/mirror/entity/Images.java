package com.example.mirror.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("images")
public class Images {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userid;
    private String url;
    private String description;
    @TableField("isPublic")
    private Boolean isPublic;  // 确保与数据库中的列名一致
    private LocalDateTime created;
    private LocalDateTime updated;
    private String tags;

    // getters and setters
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getisPublic() {
        return isPublic;
    }

    public void setisPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Images{" +
              "id=" + id +
              ", userid=" + userid +
              ", url='" + url + '\'' +
              ", description='" + description + '\'' +
              ", isPublic=" + isPublic +
              ", created=" + created +
              ", updated=" + updated +
              ", tags='" + tags + '\'' +
              '}';
    }
}
