package com.cqupt.art.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ArticleFileService {
    String upload(MultipartFile multipartFile) throws IOException;
}
