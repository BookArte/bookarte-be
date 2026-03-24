package com.library.bookarte.global.util;

import com.library.bookarte.global.entity.UploadFile;
import com.library.bookarte.global.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final UploadFileRepository uploadFileRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${file.upload.forbidden-extensions}")
    private List<String> forbiddenExtensions;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public void uploadAndSave(Long refId, String refType, MultipartFile file, String role) {
        if (file == null || file.isEmpty()) return;

        String fileUrl = uploadFile(file);

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);

        UploadFile uploadFile = UploadFile.builder()
                .refId(refId)
                .refType(refType)
                .fileUrl(fileUrl)
                .originalName(originalName)
                .fileSize(file.getSize())
                .extension(extension)
                .fileRole(role)
                .build();

        uploadFileRepository.save(uploadFile);
    }

    public String uploadFile(MultipartFile file) {

        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String s3FileName = UUID.randomUUID().toString() + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3FileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, s3FileName);

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 중 오류 발생", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("파일 용량은 10MB를 초과할 수 없습니다.");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (forbiddenExtensions.contains(extension)) {
            throw new RuntimeException("업로드가 금지된 파일 형식입니다: " + extension);
        }
    }

    private String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".")).toLowerCase();
        }
        return "";
    }
}
