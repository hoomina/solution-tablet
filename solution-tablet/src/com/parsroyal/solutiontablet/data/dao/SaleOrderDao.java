package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public interface SaleOrderDao extends BaseDao<SaleOrder, Long> {

  List<SaleOrder> retrieveSaleOrderByStatus(Long statusId);

  List<SaleOrderListModel> searchForOrders(SaleOrderSO saleOrderSO);

  SaleOrderDto getOrderDtoById(Long orderId);

  SaleOrderDto getOrderDtoByCustomerBackendIdAndStatus(Long customerBackendId, Long statusId);

  List<BaseSaleDocument> findOrderDocumentsByStatusId(Long statusId);

  BaseSaleDocument findOrderDocumentByOrderId( Long orderId);

  void deleteByCustomerBackendIdAndStatus(Long customerBackendId, Long statusId);
}
