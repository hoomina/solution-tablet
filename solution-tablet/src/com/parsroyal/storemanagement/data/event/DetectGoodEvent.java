package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.data.model.DetectGoodDetail;
import java.util.List;

public class DetectGoodEvent extends Event {

  private List<DetectGoodDetail> detailList;

  public DetectGoodEvent(List<DetectGoodDetail> detailList) {
    this.detailList = detailList;
  }

  public List<DetectGoodDetail> getDetailList() {
    return detailList;
  }

  public void setDetailList(
      List<DetectGoodDetail> detailList) {
    this.detailList = detailList;
  }

}