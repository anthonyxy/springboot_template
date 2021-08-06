package com.xyz.contorller;

import java.io.*;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.xyz.entity.pojo.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.xyz.service.FileService;
import com.xyz.util.AnthonyUtil;
import com.xyz.util.ResponseUtil;
import com.xyz.util.dto.DataResult;

import cn.hutool.core.date.DateUtil;

/**
 * 文件处理接口
 * CREATE TABLE `file_info` (
 * `file_code` varchar(32) NOT NULL COMMENT '文件码',
 * `file_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件路径',
 * `file_data` longblob NULL COMMENT '文件二进制内容',
 * PRIMARY KEY (`file_code`) USING BTREE
 */
//
// CREATE TABLE `file_info` (
// `file_code` varchar(32) NOT NULL COMMENT '文件码',
// `file_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件路径',
// `file_data` longblob NULL COMMENT '文件二进制内容',
// PRIMARY KEY (`file_code`) USING BTREE
// )
@RestController
public class FileContorller {

    private static final Logger logger = LoggerFactory.getLogger(FileContorller.class);

    @Autowired
    private FileService fileService;

    private String FILE_PATH = null;

    // 初始化FILE_PATH
    @PostConstruct
    public void init() {
        String path = this.getClass().getResource("/").getPath();
        if (path.indexOf("target") > 0) {
            FILE_PATH = path.replace("target/classes/", "file/");
        } else {
            FILE_PATH = path.replace("resources/", "file/");
        }
        File dist = new File(FILE_PATH);
        // 如果文件夹不存在则创建
        if (!dist.exists() && !dist.isDirectory()) {
            dist.mkdir();
        }
        logger.info("FILE_PATH初始化成功:" + FILE_PATH);
    }

    // 上传文件
    @PostMapping("uploadFile")
    public DataResult uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return DataResult.build9250("请选择上传文件");
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        StringBuilder newpath = new StringBuilder();
        newpath.append(DateUtil.format(new Date(), "yyyy-MM-dd"));
        newpath.append("/");
        newpath.append(System.currentTimeMillis());
        newpath.append(AnthonyUtil.random(8));
        newpath.append(suffixName);
        File newFile = new File(FILE_PATH + newpath);
        // 检测是否存在目录
        if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }
        try {
            // 文件上传
            file.transferTo(newFile);
            // 获取二进制数据
            byte[] bytes = FileContorller.FileToByte(newFile);
            // 保存路径到数据库
            return fileService.saveFile(newpath.toString(), bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return DataResult.build9500();
        }
    }

    // 读取文件
    @GetMapping(value = {"/readFile/{hashCode}", "readImg/{hashCode}"})
    public void readFile(HttpServletResponse response, @PathVariable String hashCode) {
        // 文件读取策略：
        // 1：优先本地读取
        // 2：未找到去数据库获取二进制数据到本地保存，再本地获取
        // 3：本地和数据库都没有，不返回任何
        try {
            FileInfo fi = fileService.readFileUrl(hashCode); // 获取File的文件码、路径，保证读取速度，先不获取二进制数据
            if (fi == null || StrUtil.isBlankIfStr(fi.getFileUrl())) { // 文件码错误或路径不存在
                return;
            }
            String path = FILE_PATH + fi.getFileUrl(); // 获取文件绝对路径
            File file = new File(path); // 创建File对象
            if (!file.exists()) { // 本地不存在目标文件
                fi = fileService.readFileData(hashCode); // 重新获取File的文件码、路径、二进制数据
                byte[] fileData = fi.getFileData(); // 文件的二进制数据
                if (fileData != null && fileData.length > 0) { // 二进制数据存在
                    // 文件目录是否存在，不存在就创建
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    FileContorller.ByteToFile(fileData, path); // 下载文件
                } else { // 二进制数据不存在（该文件本地和数据库都没有）
                    return;
                }
            }
            ResponseUtil.readFileResponse(response, file); // 返回图片
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将文件放在FILE_PATH下载
    @GetMapping("download")
    public void download(HttpServletResponse response) {
        try {
            ResponseUtil.download(FILE_PATH + "mwxz_banner.gif", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // File转二进制
    private static byte[] FileToByte(File file) {
        byte[] by = new byte[(int) file.length()];
        try (
                InputStream is = new FileInputStream(file);
                ByteArrayOutputStream bs = new ByteArrayOutputStream()
        ) {
            byte[] bb = new byte[2048];
            int ch = is.read(bb);
            while (ch != -1) {
                bs.write(bb, 0, ch);
                ch = is.read(bb);
            }
            by = bs.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return by;
    }

    // 二进制转File
    private static void ByteToFile(byte[] byteArray, String filePath) {
        File file = new File(filePath);
        try (
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            bos.write(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
