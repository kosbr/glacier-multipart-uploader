package com.github.kosbr.aws.service.client;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UploadPartIntermediateResult {

    private long beginByte;

    private long endByte;

    private String checkSum;

    private int progressInPercents;

    public long getBeginByte() {
        return beginByte;
    }

    public void setBeginByte(final long beginByte) {
        this.beginByte = beginByte;
    }

    public long getEndByte() {
        return endByte;
    }

    public void setEndByte(final long endByte) {
        this.endByte = endByte;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(final String checkSum) {
        this.checkSum = checkSum;
    }

    public int getProgressInPercents() {
        return progressInPercents;
    }

    public void setProgressInPercents(final int progressInPercents) {
        this.progressInPercents = progressInPercents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UploadPartIntermediateResult that = (UploadPartIntermediateResult) o;

        return new EqualsBuilder()
                .append(beginByte, that.beginByte)
                .append(endByte, that.endByte)
                .append(progressInPercents, that.progressInPercents)
                .append(checkSum, that.checkSum)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(beginByte)
                .append(endByte)
                .append(checkSum)
                .append(progressInPercents)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("beginByte", beginByte)
                .append("endByte", endByte)
                .append("checkSum", checkSum)
                .append("progressInPercents", progressInPercents)
                .toString();
    }
}
