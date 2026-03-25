package com.library.bookarte.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardUpdateRequest {
    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotBlank
    private String editor;

    @NotBlank
    private String noticeYn;

    @NotBlank
    private Long orderNum;

    private MultipartFile thumbnailFile;

    private List<MultipartFile> files;

    private List<Long> deletedFileIds;
}
