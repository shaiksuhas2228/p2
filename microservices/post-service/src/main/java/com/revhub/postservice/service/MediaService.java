package com.revhub.postservice.service;

import com.revhub.postservice.entity.Media;
import com.revhub.postservice.entity.Post;
import com.revhub.postservice.repository.MediaRepository;
import com.revhub.postservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private PostRepository postRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public void uploadMedia(Long postId, MultipartFile[] files) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path uploadPath = Paths.get(uploadDir);
                    
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath);

                    Media media = new Media();
                    media.setFileName(file.getOriginalFilename());
                    media.setFilePath(filePath.toString());
                    media.setFileType(file.getContentType());
                    media.setFileSize(file.getSize());
                    media.setPost(post);
                    
                    mediaRepository.save(media);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
                }
            }
        }
    }
}