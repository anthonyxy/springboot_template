package com.xyz.service.impl;

import com.xyz.entity.pojo.FileInfoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xyz.entity.mapper.FileInfoMapper;
import com.xyz.entity.pojo.FileInfo;
import com.xyz.service.FileService;
import com.xyz.util.dto.DataResult;

import cn.hutool.crypto.SecureUtil;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    public DataResult saveFile(String path, byte[] bytes) throws Exception {
        FileInfo fi = new FileInfo();
        String code = SecureUtil.md5(SecureUtil.md5(path) + path);
        fi.setFileCode(code);
        fi.setFileUrl(path);
        fi.setFileData(bytes);
        fileInfoMapper.insert(fi);
        return DataResult.build100(code);
    }

    @Override
    public FileInfo readFileUrl(String fileCode) throws Exception {
        FileInfoExample fiExample = new FileInfoExample();
        FileInfoExample.Criteria fiCriteria = fiExample.createCriteria();
        fiCriteria.andFileCodeEqualTo(fileCode);
        List<FileInfo> fileInfos = fileInfoMapper.selectByExample(fiExample);
        if (fileInfos.size() == 1) {
            return fileInfos.get(0);
        } else {
            return null;
        }
    }

    @Override
    public FileInfo readFileData(String fileCode) throws Exception {
        return fileInfoMapper.selectByPrimaryKey(fileCode);
    }

}
