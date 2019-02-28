package com.parsroyal.storemanagement.data.model;

import com.parsroyal.storemanagement.data.entity.Goods;
import java.util.List;

/**
 * Created by Mahyar on 7/23/2015.
 */
public class GoodsDtoList extends BaseModel {

  private List<Goods> goodsDtoList;

  public List<Goods> getGoodsDtoList() {
    return goodsDtoList;
  }

  public void setGoodsDtoList(List<Goods> goodsDtoList) {
    this.goodsDtoList = goodsDtoList;
  }
}