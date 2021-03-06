package com.parsroyal.solutiontablet.data.listmodel;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireListModel extends BaseListModel {

  private Long id;
  private Long backendId;
  private String description;
  private Integer questionsCount;
  private Long goodsGroupBackendId;
  private Long visitId;
  private String date;
  private Long answersGroupNo;
  private Long status;
  private String customerFullName;
  private String customerCode;

  public Long getVisitId() {
    return visitId;
  }

  public void setVisitId(Long visitId) {
    this.visitId = visitId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getQuestionsCount() {
    return questionsCount;
  }

  public void setQuestionsCount(Integer questionsCount) {
    this.questionsCount = questionsCount;
  }

  public Long getGoodsGroupBackendId() {
    return goodsGroupBackendId;
  }

  public void setGoodsGroupBackendId(Long goodsGroupBackendId) {
    this.goodsGroupBackendId = goodsGroupBackendId;
  }

  public Long getAnswersGroupNo() {
    return answersGroupNo;
  }

  public void setAnswersGroupNo(Long answersGroupNo) {
    this.answersGroupNo = answersGroupNo;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getCustomerFullName() {
    return customerFullName;
  }

  public void setCustomerFullName(String customerFullName) {
    this.customerFullName = customerFullName;
  }

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }
}
