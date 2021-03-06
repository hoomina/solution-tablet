package com.parsroyal.solutiontablet.data.entity;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: m.sefidi Date: 4/10/13 Time: 7:57 PM To change this template
 * use File | Settings | File Templates.
 */

public class SaleOrder extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_SALE_ORDER";
  public static final String COL_ID = "_id";
  public static final String COL_NUMBER = "NUMBER";
  public static final String COL_AMOUNT = "AMOUNT";
  public static final String COL_DATE = "DATE";
  public static final String COL_PAYMENT_TYPE_BACKEND_ID = "PAYMENT_TYPE_BACKEND_ID";
  public static final String COL_SALESMAN_ID = "SALESMAN_ID";
  public static final String COL_CUSTOMER_BACKEND_ID = "CUSTOMER_BACKEND_ID";
  public static final String COL_DESCRIPTION = "DESCRIPTION";
  public static final String COL_STATUS = "STATUS";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_INVOICE_BACKEND_ID = "INVOICE_BACKEND_ID";
  public static final String COL_REJECT_TYPE_BACKEND_ID = "REJECT_TYPE_BACKEND_ID";
  public static final String COL_VISITLINE_BACKEND_ID = "VISITLINE_BACKEND_ID";
  public static final String COL_SMS_CONFIRM = "SMS_CONFIRM";

  public static final String CREATE_TABLE_SCRIPT = "CREATE TABLE " + SaleOrder.TABLE_NAME + " (" +
      " " + SaleOrder.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + SaleOrder.COL_NUMBER + " INTEGER," +
      " " + SaleOrder.COL_AMOUNT + " INTEGER," +
      " " + SaleOrder.COL_DATE + " TEXT," +
      " " + SaleOrder.COL_PAYMENT_TYPE_BACKEND_ID + " INTEGER," +
      " " + SaleOrder.COL_SALESMAN_ID + " INTEGER," +
      " " + SaleOrder.COL_CUSTOMER_BACKEND_ID + " INTEGER," +
      " " + SaleOrder.COL_DESCRIPTION + " TEXT," +
      " " + SaleOrder.COL_STATUS + " INTEGER," +
      " " + SaleOrder.COL_BACKEND_ID + " INTEGER," +
      " " + SaleOrder.COL_INVOICE_BACKEND_ID + " INTEGER," +
      " " + SaleOrder.COL_CREATE_DATE_TIME + " TEXT," +
      " " + SaleOrder.COL_UPDATE_DATE_TIME + " TEXT," +
      " " + SaleOrder.COL_REJECT_TYPE_BACKEND_ID + " INTEGER," +
      " " + SaleOrder.COL_VISITLINE_BACKEND_ID + " INTEGER," +
      " " + SaleOrder.COL_SMS_CONFIRM + " INTEGER" +
      " );";

  private Long id;
  private Long number;
  private String date;
  private Long amount;
  private Long paymentTypeBackendId;
  private Long salesmanId;
  private Long customerBackendId;
  private String description;
  private Long status;
  private Long backendId;
  private Long invoiceBackendId;
  private String exportDate;
  private Long rejectBackendId;
  private Long smsConfirm;
  private List<SaleOrderItem> orderItems;
  private Long rejectType;
  private long visitlineBackendId;

  public Long getSmsConfirm() {
    return smsConfirm;
  }

  public void setSmsConfirm(Long smsConfirm) {
    this.smsConfirm = smsConfirm;
  }

  public Long getRejectType() {
    return rejectType;
  }

  public void setRejectType(Long rejectType) {
    this.rejectType = rejectType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
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

  public Long getPaymentTypeBackendId() {
    return paymentTypeBackendId;
  }

  public void setPaymentTypeBackendId(Long paymentTypeBackendId) {
    this.paymentTypeBackendId = paymentTypeBackendId;
  }

  public Long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public Long getInvoiceBackendId() {
    return invoiceBackendId;
  }

  public void setInvoiceBackendId(Long invoiceBackendId) {
    this.invoiceBackendId = invoiceBackendId;
  }

  public List<SaleOrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<SaleOrderItem> orderItems) {
    this.orderItems = orderItems;
  }

  public String getExportDate() {
    return exportDate;
  }

  public void setExportDate(String exportDate) {
    this.exportDate = exportDate;
  }

  public Long getRejectBackendId() {
    return rejectBackendId;
  }

  public void setRejectBackendId(Long rejectBackendId) {
    this.rejectBackendId = rejectBackendId;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

  public long getVisitlineBackendId() {
    return visitlineBackendId;
  }

  public void setVisitlineBackendId(long visitlineBackendId) {
    this.visitlineBackendId = visitlineBackendId;
  }
}
