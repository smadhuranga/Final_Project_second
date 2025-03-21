//package lk.ijse.back_end.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Service
//public class FileStorageService {
//
//    @Value("${file.upload-dir}")
//    private String uploadDir;
//
//    public String storeFile(MultipartFile file) throws IOException {
//        String originalFileName = file.getOriginalFilename();
//        String fileName = UUID.randomUUID() + "_" + originalFileName;
//        Path uploadPath = Paths.get(uploadDir);
//
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        Path filePath = uploadPath.resolve(fileName);
//        Files.copy(file.getInputStream(), filePath);
//        return fileName;
//    }
//}