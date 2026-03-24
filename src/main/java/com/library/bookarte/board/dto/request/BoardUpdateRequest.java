package com.library.bookarte.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardUpdateRequest {
    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    @NotBlank
    private String noticeYn;

    @NotBlank
    private Long orderNum;

    private MultipartFile thumbnailFile;

    private List<MultipartFile> files;
}
