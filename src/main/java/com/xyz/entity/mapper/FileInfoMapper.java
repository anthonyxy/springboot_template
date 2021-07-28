package com.xyz.entity.mapper;

import com.xyz.entity.pojo.FileInfo;
import com.xyz.entity.pojo.FileInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FileInfoMapper {
    int countByExample(FileInfoExample example);

    int deleteByExample(FileInfoExample example);

    int deleteByPrimaryKey(String fileCode);

    int insert(FileInfo record);

    int insertSelective(FileInfo record);

    List<FileInfo> selectByExampleWithBLOBs(FileInfoExample example);

    List<FileInfo> selectByExample(FileInfoExample example);

    FileInfo selectByPrimaryKey(String fileCode);

    int updateByExampleSelective(@Param("record") FileInfo record, @Param("example") FileInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") FileInfo record, @Param("example") FileInfoExample example);

    int updateByExample(@Param("record") FileInfo record, @Param("example") FileInfoExample example);

    int updateByPrimaryKeySelective(FileInfo record);

    int updateByPrimaryKeyWithBLOBs(FileInfo record);

    int updateByPrimaryKey(FileInfo record);
}