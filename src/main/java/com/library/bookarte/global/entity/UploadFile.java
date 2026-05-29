package com.library.bookarte.global.entity;

import com.library.bookarte.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "upload_file")
public class UploadFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String originalName;

    private Long fileSize;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String refType;

    @Column(nullable = false)
    private Long refId;

    private String fileRole;
}
