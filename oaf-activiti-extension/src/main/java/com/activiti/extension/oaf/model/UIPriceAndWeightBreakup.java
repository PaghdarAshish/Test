package com.activiti.extension.oaf.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Bean class for store Price and Weight Breakup information.
 * @author Pradip Patel
 */
public class UIPriceAndWeightBreakup implements Serializable {

    private String item;
    private String areaSqMt;
    private String weightMt;
    private String bookPrice;
    private String discount;
    private String discountPrice;
    private String updated;

    @Override
    public String toString() {
        return "UIPriceAndWeightBreakup{" +
                "item='" + item + '\'' +
                ", areaSqMt='" + areaSqMt + '\'' +
                ", weightMt='" + weightMt + '\'' +
                ", bookPrice='" + bookPrice + '\'' +
                ", discount='" + discount + '\'' +
                ", discountPrice='" + discountPrice + '\'' +
                ", updated='" + updated + '\'' +
                '}';
    }

    //setters

    public void setItem(String item) {
        this.item = item;
    }

    public void setAreaSqMt(String areaSqMt) {
        this.areaSqMt = areaSqMt;
    }

    public void setWeightMt(String weightMt) {
        this.weightMt = weightMt;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    //getters

    public String getItem() {
        return item;
    }

    public String getAreaSqMt() {
        return areaSqMt;
    }

    public String getWeightMt() {
        return weightMt;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public String getUpdated() {
        return updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIPriceAndWeightBreakup that = (UIPriceAndWeightBreakup) o;
        return Objects.equals(item, that.item) &&
                Objects.equals(areaSqMt, that.areaSqMt) &&
                Objects.equals(weightMt, that.weightMt) &&
                Objects.equals(bookPrice, that.bookPrice) &&
                Objects.equals(discount, that.discount) &&
                Objects.equals(discountPrice, that.discountPrice) &&
                Objects.equals(updated, that.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, areaSqMt, weightMt, bookPrice, discount, discountPrice, updated);
    }
}
