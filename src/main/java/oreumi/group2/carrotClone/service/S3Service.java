package oreumi.group2.carrotClone.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface S3Service {
    public String uploadFile(MultipartFile file) throws IOException; /* 파일 업로드  */
    void deleteFile(String fileName); /* 파일 삭제 */
    List<String> listFiles(); /* 버킷의 모든 파일 목록 조회 */
}
