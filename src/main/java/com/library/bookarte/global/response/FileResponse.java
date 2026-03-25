package com.library.bookarte.global.response;

import com.library.bookarte.global.entity.UploadFile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileResponse {
    private Long fileId;
    private String originalName;
    private String fileUrl;
    private Long fileSize;

    public static FileResponse from(UploadFile file) {
        return FileResponse.builder()
                .fileId(file.getFileId())
                .originalName(file.getOriginalName())
                .fileUrl(file.getFileUrl())
                .fileSize(file.getFileSize())
                .build();
    }
}
