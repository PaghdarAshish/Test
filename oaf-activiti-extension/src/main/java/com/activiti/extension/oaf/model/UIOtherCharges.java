package com.activiti.extension.oaf.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Bean class for store Other Charges information.
 * @author Pradip Patel
 */
public class UIOtherCharges implements Serializable {

    private String kschl;
    private String item;
    private String percentage;
    private String amount;
    private String totalAmount;
    private String updated;

    @Override
    public String toString() {
        return "UIOtherCharges{" +
                "kschl='" + kschl + '\'' +
                ", item='" + item + '\'' +
                ", percentage='" + percentage + '\'' +
                ", amount='" + amount + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", updated='" + updated + '\'' +
                '}';
    }

    //setter


    public void setKschl(String kschl) {
        this.kschl = kschl;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    //getter


    public String getKschl() {
        return kschl;
    }

    public String getItem() {
        return item;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getAmount() {
        return amount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getUpdated() {
        return updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIOtherCharges that = (UIOtherCharges) o;
        return Objects.equals(kschl, that.kschl) &&
                Objects.equals(item, that.item) &&
                Objects.equals(percentage, that.percentage) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(totalAmount, that.totalAmount) &&
                Objects.equals(updated, that.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kschl, item, percentage, amount, totalAmount, updated);
    }
}
