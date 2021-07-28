package com.xyz.service;

import com.xyz.entity.pojo.FileInfo;
import com.xyz.util.dto.DataResult;

import java.util.List;

public interface FileService {

    DataResult saveFile(String path, byte[] bytes) throws Exception;

    FileInfo readFileUrl(String fileCode) throws Exception;

    FileInfo readFileData(String fileCode) throws Exception;

}
