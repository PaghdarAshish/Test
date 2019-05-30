package com.activiti.extension.oaf.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Bean class for store/parse 'manageOAF' request payload.
 * @author Pradip Patel
 */
public class UIQuotationDetail implements Serializable {

    private UIHeaderSegment headerSegment;
    private List<UIPriceAndWeightBreakup> priceAndWeightBreakups;
    private List<UIOtherCharges> otherCharges;
    private List<UIAttachment> attachments;

    //
    @Override
    public String toString() {
        return "UIQuotationDetail{" +
                "headerSegment=" + headerSegment +
                ", priceAndWeightBreakups=" + priceAndWeightBreakups +
                ", otherCharges=" + otherCharges +
                ", attachments=" + attachments +
                '}';
    }


    //getter

    public UIHeaderSegment getHeaderSegment() {
        return headerSegment;
    }

    public List<UIPriceAndWeightBreakup> getPriceAndWeightBreakups() {
        return priceAndWeightBreakups;
    }

    public List<UIOtherCharges> getOtherCharges() {
        return otherCharges;
    }

    public List<UIAttachment> getAttachments() {
        return attachments;
    }


    //setter

    public void setHeaderSegment(UIHeaderSegment headerSegment) {
        this.headerSegment = headerSegment;
    }

    public void setPriceAndWeightBreakups(List<UIPriceAndWeightBreakup> priceAndWeightBreakups) {
        this.priceAndWeightBreakups = priceAndWeightBreakups;
    }

    public void setOtherCharges(List<UIOtherCharges> otherCharges) {
        this.otherCharges = otherCharges;
    }

    public void setAttachments(List<UIAttachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIQuotationDetail that = (UIQuotationDetail) o;
        return Objects.equals(headerSegment, that.headerSegment) &&
                Objects.equals(priceAndWeightBreakups, that.priceAndWeightBreakups) &&
                Objects.equals(otherCharges, that.otherCharges) &&
                Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerSegment, priceAndWeightBreakups, otherCharges, attachments);
    }
}
