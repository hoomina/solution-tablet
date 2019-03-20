package com.parsroyal.storemanagement.service.impl;

import android.content.Context;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.impl.CanceledOrdersDataTransfer;
import com.parsroyal.storemanagement.biz.impl.CityDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.DeliverableGoodsDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.GoodsDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.GoodsGroupDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.GoodsRequestDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.InvoicedOrdersDataTransfer;
import com.parsroyal.storemanagement.biz.impl.NewCustomerDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.PaymentsDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.PictureDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.PositionDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.ProvinceDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.QAnswersDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.QuestionnaireDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.SaleOrderForDeliveryDataTaransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.SaleRejectsDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.StockGoodDataTransfer;
import com.parsroyal.storemanagement.biz.impl.UpdatedCustomerLocationDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.VisitInformationDataTransfer;
import com.parsroyal.storemanagement.biz.impl.VisitLineDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.VisitLineForDeliveryDataTaransferBizImpl;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.impl.PaymentDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.StockGoodDaoImpl;
import com.parsroyal.storemanagement.data.entity.CustomerPic;
import com.parsroyal.storemanagement.data.entity.Payment;
import com.parsroyal.storemanagement.data.entity.SaleOrder;
import com.parsroyal.storemanagement.data.entity.VisitInformation;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.BaseSaleDocument;
import com.parsroyal.storemanagement.data.model.CustomerDto;
import com.parsroyal.storemanagement.data.model.CustomerLocationDto;
import com.parsroyal.storemanagement.data.model.PositionDto;
import com.parsroyal.storemanagement.data.model.QAnswerDto;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.data.model.VisitInformationDto;
import com.parsroyal.storemanagement.service.CustomerService;
import com.parsroyal.storemanagement.service.DataTransferService;
import com.parsroyal.storemanagement.service.PaymentService;
import com.parsroyal.storemanagement.service.PositionService;
import com.parsroyal.storemanagement.service.QuestionnaireService;
import com.parsroyal.storemanagement.service.SaleOrderService;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

/**
 * Created by Arash 29/12/2017
 */
public class DataTransferServiceImpl implements DataTransferService {

  private final GoodsServiceImpl goodsService;
  private final BaseInfoServiceImpl infoService;
  private final SaleOrderDaoImpl saleOrderDaoImpl;
  private final PaymentDaoImpl paymentDaoImpl;
  private Context context;
  private CustomerService customerService;
  private QuestionnaireService questionnaireService;
  private SaleOrderService saleOrderService;
  private PaymentService paymentService;
  private PositionService positionService;
  private VisitServiceImpl visitService;
  private StockGoodDaoImpl stockService;

  public DataTransferServiceImpl(Context context) {
    this.context = context;
    this.customerService = new CustomerServiceImpl(context);
    this.questionnaireService = new QuestionnaireServiceImpl(context);
    this.saleOrderService = new SaleOrderServiceImpl(context);
    this.saleOrderDaoImpl = new SaleOrderDaoImpl(context);
    this.paymentService = new PaymentServiceImpl(context);
    this.paymentDaoImpl = new PaymentDaoImpl(context);
    this.positionService = new PositionServiceImpl(context);
    this.visitService = new VisitServiceImpl(context);
    this.goodsService = new GoodsServiceImpl(context);
    this.infoService = new BaseInfoServiceImpl(context);
    this.stockService = new StockGoodDaoImpl(context);
  }

  @Override
  public boolean hasUnsentData() {
    List<QAnswerDto> answersForSend = questionnaireService.getAllAnswersDtoForSend();
    if (saleOrderDaoImpl.count(SaleOrder.COL_STATUS,
        SaleOrderStatus.READY_TO_SEND.getStringId()) > 0) {
      return true;
    }
    if (saleOrderDaoImpl.count(SaleOrder.COL_STATUS, SaleOrderStatus.DELIVERED.getStringId()) > 0) {
      return true;
    }
    if (saleOrderDaoImpl.count(SaleOrder.COL_STATUS, SaleOrderStatus.INVOICED.getStringId()) > 0) {
      return true;
    }
    if (saleOrderDaoImpl.count(SaleOrder.COL_STATUS,
        SaleOrderStatus.FREE_ORDER_DELIVERED.getStringId()) > 0) {
      return true;
    }

    List<CustomerDto> allNewCustomers = customerService.getAllNewCustomersForSend();
    List<VisitInformation> visits = visitService.getAllVisitInformationForSend();

    if (paymentDaoImpl.count(Payment.COL_STATUS, SendStatus.NEW.getStringId()) > 0) {
      return true;
    }
    return Empty.isNotEmpty(answersForSend) || Empty.isNotEmpty(allNewCustomers) || Empty
        .isNotEmpty(visits);
  }

  public void getAllProvinces() {
    try {
      new ProvinceDataTransferBizImpl(context).getAllProvinces();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllCities() {
    try {
      new CityDataTransferBizImpl(context).getAllCities();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllBaseInfos() {
    try {
      new BaseInfoDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllGoodsGroups() {
    try {
      new GoodsGroupDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllQuestionnaires() {
    try {
      new QuestionnaireDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllDeliverableGoods() {
    try {
      new DeliverableGoodsDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllVisitLinesForDelivery() {
    try {
      new VisitLineForDeliveryDataTaransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllOrdersForDelivery() {
    try {
      new SaleOrderForDeliveryDataTaransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getGoodRequest() {
    try {
      new GoodsRequestDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllGoods() {
    try {
      new GoodsDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllVisitLines() {
    try {
      new VisitLineDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void sendAllNewCustomers() {
    List<CustomerDto> allNewCustomers = customerService.getAllNewCustomersForSend();
    if (Empty.isEmpty(allNewCustomers)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_new_customer_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    }

    NewCustomerDataTransferBizImpl dataTransferBiz = new NewCustomerDataTransferBizImpl(context);

    for (int i = 0; i < allNewCustomers.size(); i++) {
      CustomerDto customerDto = allNewCustomers.get(i);
      dataTransferBiz.setCustomer(customerDto);
      boolean isSuccess = dataTransferBiz.exchangeData();

      if (isSuccess) {

        List<CustomerPic> pics = customerService
            .getAllCustomerPicForSendByCustomerId(customerDto.getId());
        if (Empty.isEmpty(pics)) {
          continue;
        }

        Timber.tag("Send Pic").d("Number of pics %s", pics.size());
        PictureDataTransferBizImpl dataTransfer = new PictureDataTransferBizImpl(context);
        for (int j = 0; j < pics.size(); j++) {
          dataTransfer.setData(pics.get(j));
          dataTransfer.exchangeData();
        }
        if (dataTransfer.getSuccess() != dataTransfer.getTotal()) {
          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }
    }

    EventBus.getDefault().post(new DataTransferSuccessEvent(
        dataTransferBiz.getSuccessfulMessage(), StatusCodes.SUCCESS));
  }

  public void sendAllUpdatedCustomersLocation() {
    List<CustomerLocationDto> allUpdatedCustomerLocation =
        customerService.getAllUpdatedCustomerLocation();
    if (Empty.isEmpty(allUpdatedCustomerLocation)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          context.getString(R.string.message_found_no_new_customer_for_send),
          StatusCodes.NO_DATA_ERROR));
      return;
    }
    boolean success = new UpdatedCustomerLocationDataTransferBizImpl(context).exchangeData();
    if (!success) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void sendAllPositions() {
    List<PositionDto> positions = positionService.getAllPositionDtoByStatus(SendStatus.NEW.getId());
    if (Empty.isNotEmpty(positions)) {
      PositionDataTransferBizImpl positionDataTransferBiz = new PositionDataTransferBizImpl(
          context);

      for (int i = 0; i < positions.size(); i++) {
        PositionDto positionDto = positions.get(i);
        positionDataTransferBiz.setPosition(positionDto);
        positionDataTransferBiz.sendAllData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          positionDataTransferBiz.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_positions_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllAnswers() {
    List<QAnswerDto> answersForSend = questionnaireService.getAllAnswersDtoForSend();

    if (Empty.isEmpty(answersForSend)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_answer_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    }

    QAnswersDataTransferBizImpl dataTransferBiz = new QAnswersDataTransferBizImpl(context);

    for (int i = 0; i < answersForSend.size(); i++) {
      QAnswerDto qAnswerDto = answersForSend.get(i);
      dataTransferBiz.setAnswer(qAnswerDto);
      dataTransferBiz.exchangeData();
    }
    EventBus.getDefault().post(new DataTransferSuccessEvent(
        dataTransferBiz.getSuccessfulMessage(), StatusCodes.SUCCESS));
  }

  public void sendAllPayments() {
    List<Payment> payments = paymentService.getAllPaymentsByStatus(SendStatus.NEW.getId());
    if (Empty.isNotEmpty(payments)) {
      PaymentsDataTransferBizImpl dataTransferBiz = new PaymentsDataTransferBizImpl(context);
      dataTransferBiz.setPayments(payments);
      boolean success = dataTransferBiz.exchangeData();
      if (!success) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
      }
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_payments_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllOrders(boolean isComplimentary) {
    List<BaseSaleDocument> saleOrders = saleOrderService.findOrderDocumentByStatus(
        isComplimentary ? SaleOrderStatus.FREE_ORDER_DELIVERED.getId()
            : SaleOrderStatus.READY_TO_SEND.getId());
    if (Empty.isNotEmpty(saleOrders)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(context,
          isComplimentary);

      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }

      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_orders_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllInvoicedOrders() {

    List<BaseSaleDocument> saleOrders;
    if (PreferenceHelper.isDistributor()) {
      saleOrders = saleOrderService.findOrderDocumentByStatus(SaleOrderStatus.DELIVERED.getId());
    } else {
      saleOrders = saleOrderService.findOrderDocumentByStatus(SaleOrderStatus.INVOICED.getId());
    }
    if (Empty.isNotEmpty(saleOrders)) {
      InvoicedOrdersDataTransfer dataTransfer = new InvoicedOrdersDataTransfer(context);
      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        /*if (ApplicationKeys.SALE_DISTRIBUTER.equals(saleType.getValue())) {
          baseSaleDocument.setStockCode(
              Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE)));
          baseSaleDocument.setOfficeCode(
              Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_CODE)));
        }*/
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_invoice_found), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllCanceledOrders() {

    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.CANCELED.getId());

    if (Empty.isNotEmpty(saleOrders)) {
      CanceledOrdersDataTransfer dataTransfer = new CanceledOrdersDataTransfer(context);
      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_invoice_found), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllSaleRejects() {
    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.REJECTED.getId());
    if (Empty.isNotEmpty(saleOrders)) {
      SaleRejectsDataTransferBizImpl dataTransfer = new SaleRejectsDataTransferBizImpl(context);

      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));

    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_sale_reject_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllCustomerPics() {

    List<CustomerPic> pics = customerService.getAllCustomerPicForSend();
    if (Empty.isEmpty(pics)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_new_customer_pic_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    } else {
      Timber.d("Send Pic lenght %s", pics.size());
    }

    PictureDataTransferBizImpl dataTransferBiz = new PictureDataTransferBizImpl(context);
    for (int i = 0; i < pics.size(); i++) {
      dataTransferBiz.setData(pics.get(i));
      dataTransferBiz.exchangeData();
    }
    if (dataTransferBiz.getTotal() == dataTransferBiz.getSuccess()) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.new_customers_pic_transferred_successfully), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferErrorEvent(context.getString(
          R.string.error_new_customers_pic_transfer), StatusCodes.SERVER_ERROR));
    }
  }

  public void sendAllVisitInformation() {

    List<VisitInformationDto> visitInformationList = visitService.getAllVisitDetailForSend(null);
    if (Empty.isEmpty(visitInformationList)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_visit_information_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    }
    VisitInformationDataTransfer dataTransfer = new VisitInformationDataTransfer(context);

    for (int i = 0; i < visitInformationList.size(); i++) {
      VisitInformationDto visitInformationDto = visitInformationList.get(i);
     /* if (visitInformationDto.getDetails() == null
          || visitInformationDto.getDetails().size() == 0) {
        visitService.deleteVisitById(visitInformationDto.getId());
        continue;
      }*/
      dataTransfer.setData(visitInformationDto);
      dataTransfer.exchangeData();
    }

    int success = dataTransfer.getSuccess();
    int total = dataTransfer.getTotal();

    if (total == success) {
      sendSuccessResult(success, total);
    } else {
      sendCancelResult(success, total);
    }
  }

  private void sendSuccessResult(int success, int total) {
    EventBus.getDefault().post(new DataTransferSuccessEvent(String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)),
        StatusCodes.SUCCESS));
  }

  private void sendCancelResult(int success, int total) {
    EventBus.getDefault().post(new DataTransferErrorEvent(String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)),
        StatusCodes.SUCCESS));
  }

  public void clearData() {
    infoService.deleteAll();
    infoService.deleteAllCities();
    infoService.deleteAllProvinces();

    customerService.deleteAll();
    customerService.deleteAllPics();

    goodsService.deleteAll();
    goodsService.deleteAllGoodsGroup();

    paymentService.deleteAll();
    visitService.deleteAll();
    questionnaireService.deleteAll();
    saleOrderService.deleteAll();
    positionService.deleteAll();
  }

  public void clearGoods() {
    GoodsServiceImpl goodsService = new GoodsServiceImpl(context);
    goodsService.deleteAll();
    goodsService.deleteAllGoodsGroup();
  }

  public void sendAllStockGoods() {
    List<StockGood> goods = stockService.getAllCountedGoods();
    if (Empty.isEmpty(goods)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_visit_information_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    }
    StockGoodDataTransfer dataTransfer = new StockGoodDataTransfer(context);

    for (int i = 0; i < goods.size(); i++) {
      StockGood stockGood = goods.get(i);

      dataTransfer.setData(stockGood);
      dataTransfer.exchangeData();
    }

    int success = dataTransfer.getSuccess();
    int total = dataTransfer.getTotal();

    if (total == success) {
      sendSuccessResult(success, total);
    } else {
      sendCancelResult(success, total);
    }
  }
}
