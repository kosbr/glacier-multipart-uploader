package com.github.kosbr.aws.service;

import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.util.BinaryUtils;
import com.github.kosbr.aws.exception.registration.UploadNotFoundException;
import com.github.kosbr.aws.model.FinishedUpload;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.repository.MultipartUploadInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UploadRegistrationServiceImpl implements UploadRegistrationService {

    @Autowired
    private MultipartUploadInfoRepository multipartUploadInfoRepository;

    @Override
    public List<MultipartUploadInfo> getAllNotFinishedUploads() {
        final List<MultipartUploadInfo> uploadInfoList = new ArrayList<>();
        multipartUploadInfoRepository.findAll().forEach(uploadInfoList::add);
        return uploadInfoList;
    }

    @Override
    public MultipartUploadInfo registerUpload(final MultipartUploadInfo multipartUploadInfo) {
        return multipartUploadInfoRepository.save(multipartUploadInfo);
    }

    @Override
    public void registerPartUpload(final long uploadInfoId, final long begin, final long end, final String checkSum)
            throws UploadNotFoundException {
        final Optional<MultipartUploadInfo> maybeUploadInfo = multipartUploadInfoRepository.findById(uploadInfoId);
        if (!maybeUploadInfo.isPresent()) {
            throwNotFoundException(uploadInfoId);
        }
        final MultipartUploadInfo multipartUploadInfo = maybeUploadInfo.get();
        final FinishedUpload finishedUpload = new FinishedUpload();
        finishedUpload.setBegin(begin);
        finishedUpload.setEnd(end);
        finishedUpload.setCheckSum(checkSum);
        if (multipartUploadInfo.getFinishedUploads() == null) {
            multipartUploadInfo.setFinishedUploads(new ArrayList<>());
        }
        multipartUploadInfo.getFinishedUploads().add(finishedUpload);
        multipartUploadInfoRepository.save(multipartUploadInfo);
    }


    @Override
    public String registerAllPartsUploaded(final long uploadInfoId) throws UploadNotFoundException {
        final Optional<MultipartUploadInfo> maybeUploadInfo = multipartUploadInfoRepository.findById(uploadInfoId);
        if (!maybeUploadInfo.isPresent()) {
            throwNotFoundException(uploadInfoId);
        }
        final List<FinishedUpload> finishedUploads = maybeUploadInfo.get().getFinishedUploads();
        final List<byte[]> checkSums = finishedUploads.stream()
                .sorted(Comparator.comparing(FinishedUpload::getBegin))
                .map(FinishedUpload::getCheckSum)
                .map(BinaryUtils::fromHex)
                .collect(Collectors.toList());
        return TreeHashGenerator.calculateTreeHash(checkSums);

    }

    @Override
    public void removeUploadInfo(final long uploadInfoId) throws UploadNotFoundException {
        final Optional<MultipartUploadInfo> maybeUploadInfo = multipartUploadInfoRepository.findById(uploadInfoId);
        if (!maybeUploadInfo.isPresent()) {
            throwNotFoundException(uploadInfoId);
        }
        multipartUploadInfoRepository.delete(maybeUploadInfo.get());
    }

    @Override
    public long getCurrentUploadPosition(final long uploadInfoId) throws UploadNotFoundException {
        final Optional<MultipartUploadInfo> maybeUploadInfo = multipartUploadInfoRepository.findById(uploadInfoId);
        if (!maybeUploadInfo.isPresent()) {
            throwNotFoundException(uploadInfoId);
        }
        final List<FinishedUpload> finishedUploads = maybeUploadInfo.get().getFinishedUploads();

        if (finishedUploads == null || finishedUploads.size() == 0) {
            return 0;
        }

        return finishedUploads.stream()
                .sorted(Comparator.comparing(FinishedUpload::getEnd).reversed())
                .findFirst()
                .get()
                .getEnd() + 1;
    }

    private void throwNotFoundException(final long uploadInfoId) throws UploadNotFoundException {
        throw new UploadNotFoundException("Upload with id " + uploadInfoId + " was not found");
    }
}
