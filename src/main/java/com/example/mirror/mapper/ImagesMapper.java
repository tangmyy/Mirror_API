package com.example.mirror.mapper;

import com.example.mirror.entity.Images;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImagesMapper extends BaseMapper<Images> {
    @Select("SELECT * FROM images")
    List<Images> selectAllImages();

    @Select("SELECT * FROM images WHERE userid = #{userId}")
    List<Images> selectImagesByUserId(int userId);

    // 查询某图片在 galleries 表中的记录数量
    @Select("SELECT COUNT(*) FROM galleries WHERE imageid = #{imageid}")
    int countImageInGalleries(@Param("imageid") int imageid);

    @Update("UPDATE images SET description = #{description}, tags = #{tags}, isPublic = #{isPublic} WHERE id = #{id}")
    int updateImage(@Param("id") int id, @Param("description") String description, @Param("tags") String tags, @Param("isPublic") boolean isPublic);

    // 将图片插入到 galleries 表中
    @Insert("INSERT INTO galleries (imageid, userid, url, description, tags) VALUES (#{imageid}, #{userid}, #{url}, #{description}, #{tags})")
    void insertIntoGalleries(@Param("imageid") int imageid, @Param("userid") int userid, @Param("url") String url, @Param("description") String description, @Param("tags") String tags);
    @Delete("DELETE FROM images WHERE id = #{id}")
    void deleteById(@Param("id") int id);
}
