package com.library.bookarte.global.repository;

import com.library.bookarte.global.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    List<UploadFile> findByRefIdAndRefType(Long refId, String refType);

    Optional<UploadFile> findByRefIdAndRefTypeAndFileRole(Long refId, String refType, String fileRole);
    List<UploadFile> findAllByRefIdAndRefTypeAndFileRole(Long refId, String refType, String fileRole);

    void deleteByRefIdAndRefType(Long refId, String refType);
}
