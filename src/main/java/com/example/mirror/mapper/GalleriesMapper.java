// src/main/java/com/example/mirror/mapper/GalleriesMapper.java
package com.example.mirror.mapper;

import com.example.mirror.entity.Galleries;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GalleriesMapper {

    @Select("SELECT * FROM galleries")
    List<Galleries> selectAllGalleries();
    @Delete("DELETE FROM galleries WHERE imageid = #{imageid}")
    void deleteFromGalleries(@Param("imageid") int imageid);
    @Update("UPDATE galleries SET description = #{description}, tags = #{tags} WHERE imageid = #{imageid}")
    void updateInGalleries(@Param("imageid") int imageid, @Param("description") String description, @Param("tags") String tags);
    @Select("SELECT * FROM galleries WHERE description LIKE CONCAT('%', #{keyword}, '%') OR tags LIKE CONCAT('%', #{keyword}, '%')")
    List<Galleries> searchGalleriesByKeyword(@Param("keyword") String keyword);

}
