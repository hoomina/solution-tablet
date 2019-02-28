package com.parsroyal.storemanagement.constants;

/**
 * Created by Arash on 2018-01-26
 */
public enum SaleOrderStatus {
  GIFT(10L, "GIFT"),
  INVOICE_GIFT(11L, "INVOICE_GIFT"),
  DRAFT(90001L, "DRAFT"),
  SENT(90002L, "SENT"),
  DELIVERABLE(90003L, "DELIVERABLE"),
  INVOICED(90004L, "INVOICED"),
  SENT_INVOICE(90005L, "SENT_INVOICE"),
  READY_TO_SEND(90006L, "READY_TO_SEND"),
  CANCELED(90007L, "CANCELED"),
  REJECTED_DRAFT(90008L, "REJECTED_DRAFT"),
  REJECTED(90009L, "REJECTED"),
  REJECTED_SENT(90010L, "REJECTED_SENT"),
  DELIVERED(90011L, "DELIVERED"),
  DELIVERABLE_SENT(90012L, "DELIVERABLE_SENT"),
  FREE_ORDER_DRAFT(90013L, "FREE_ORDER_DRAFT"),
  FREE_ORDER_DELIVERED(90014L, "FREE_ORDER_DELIVERED"),
  FREE_ORDER_SENT(90015L, "FREE_ORDER_SENT");

  private Long id;
  private String title;

  SaleOrderStatus(Long id, String title) {
    this.id = id;
    this.title = title;
  }

  public static SaleOrderStatus findById(Long statusId) {
    for (SaleOrderStatus saleOrderStatus : SaleOrderStatus.values()) {
      if (saleOrderStatus.getId().equals(statusId)) {
        return saleOrderStatus;
      }
    }
    return null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStringId() {
    return String.valueOf(id);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}