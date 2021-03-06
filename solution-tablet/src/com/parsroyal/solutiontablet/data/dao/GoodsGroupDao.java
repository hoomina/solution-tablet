package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import java.util.List;

/**
 * Created by Arash on 7/24/2015.
 */
public interface GoodsGroupDao extends BaseDao<GoodsGroup, Long> {

  List<GoodsGroup> getCategories();

  List<GoodsGroup> getChilds(Long goodsGroupBackendId);

  GoodsGroup retrieveByBackendId(Long goodsGroupBackendId);
}
