package com.github.kosbr.aws.repository;

import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.model.UploaderConfiguration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MultipartUploadInfoRepository extends CrudRepository<MultipartUploadInfo, Long> {

    List<MultipartUploadInfo> findAllByUploaderConfiguration(UploaderConfiguration configuration);
}
