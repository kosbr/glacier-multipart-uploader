package com.github.kosbr.aws.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents the uploaded part of the file. Belongs to {@link MultipartUploadInfo}.
 */
@Entity
public class FinishedUpload {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * The beginning number of the byte of the uploaded part.
     */
    @Column
    private Long begin;

    /**
     * The last number of the byte of the uploaded part.
     */
    @Column
    private Long end;

    /**
     * Checksum of the uploaded part.
     */
    @Column
    private String checkSum;

    public Long getId() {
        return id;
    }

    /**
     * @return The beginning number of the byte of the uploaded part.
     */
    public Long getBegin() {
        return begin;
    }

    public void setBegin(final Long begin) {
        this.begin = begin;
    }

    /**
     * @return The last number of the byte of the uploaded part.
     */
    public Long getEnd() {
        return end;
    }

    public void setEnd(final Long end) {
        this.end = end;
    }

    /**
     * @return Checksum of the uploaded part.
     */
    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(final String checkSum) {
        this.checkSum = checkSum;
    }
}
