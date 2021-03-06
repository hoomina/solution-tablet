package com.parsroyal.solutiontablet.data.listmodel;

import androidx.annotation.NonNull;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class SaleOrderListModel extends BaseListModel implements Comparable<SaleOrderListModel> {

  private Long id;
  private Long backendId;
  private String date;
  private Long amount;
  private String paymentTypeTitle;
  private String customerName;
  private Long status;
  private Long customerBackendId;
  private String description;
  private String createdDateTime;
  private Long visitId;
  private int orderCount;
  private Long number;
  private String customerCode;

  public SaleOrderListModel(Long id, Long backendId, String date, Long amount,
      String paymentTypeTitle, String customerName, Long status, Long customerBackendId) {
    this.id = id;
    this.backendId = backendId;
    this.date = date;
    this.amount = amount;
    this.paymentTypeTitle = paymentTypeTitle;
    this.customerName = customerName;
    this.status = status;
    this.customerBackendId = customerBackendId;
  }

  public SaleOrderListModel() {
  }

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public Long getVisitId() {
    return visitId;
  }

  public void setVisitId(Long visitId) {
    this.visitId = visitId;
  }

  public String getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(String createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public String getPaymentTypeTitle() {
    return paymentTypeTitle;
  }

  public void setPaymentTypeTitle(String paymentTypeTitle) {
    this.paymentTypeTitle = paymentTypeTitle;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  @Override
  public Long getPrimaryKey() {
    return getId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SaleOrderListModel that = (SaleOrderListModel) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int orderCount) {
    this.orderCount = orderCount;
  }

  @Override
  public int compareTo(@NonNull SaleOrderListModel o) {
    return id.compareTo(o.id);
  }
}
