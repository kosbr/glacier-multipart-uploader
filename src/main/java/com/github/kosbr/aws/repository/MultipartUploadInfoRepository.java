package com.github.kosbr.aws.repository;

import com.github.kosbr.aws.model.MultipartUploadInfo;
import org.springframework.data.repository.CrudRepository;

public interface MultipartUploadInfoRepository extends CrudRepository<MultipartUploadInfo, Long> {
}
