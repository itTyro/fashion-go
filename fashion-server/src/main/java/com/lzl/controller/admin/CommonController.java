package com.lzl.controller.admin;

import com.lzl.result.Result;
import com.lzl.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传，{}", file);

        String originalFilename = file.getOriginalFilename();
        String endName = originalFilename.substring(originalFilename.lastIndexOf("."));

        String name = UUID.randomUUID().toString() + endName;
        String url = aliOssUtil.upload(file.getBytes(), name);
        return Result.success(url);
    }
}
