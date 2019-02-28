package com.parsroyal.storemanagement.ui.fragment.dialogFragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.impl.CanceledOrdersDataTransfer;
import com.parsroyal.storemanagement.biz.impl.InvoicedOrdersDataTransfer;
import com.parsroyal.storemanagement.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.PaymentsDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.PictureDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.QAnswersDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.SaleRejectsDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.UpdatedCustomerLocationDataTransferBizImpl;
import com.parsroyal.storemanagement.biz.impl.VisitInformationDataTransfer;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.CustomerStatus;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.entity.CustomerPic;
import com.parsroyal.storemanagement.data.entity.Payment;
import com.parsroyal.storemanagement.data.entity.VisitInformation;
import com.parsroyal.storemanagement.data.entity.VisitInformationDetail;
import com.parsroyal.storemanagement.data.event.ActionEvent;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.Event;
import com.parsroyal.storemanagement.data.event.SendOrderEvent;
import com.parsroyal.storemanagement.data.event.SuccessEvent;
import com.parsroyal.storemanagement.data.model.BaseSaleDocument;
import com.parsroyal.storemanagement.data.model.CustomerLocationDto;
import com.parsroyal.storemanagement.data.model.QAnswerDto;
import com.parsroyal.storemanagement.data.model.VisitInformationDto;
import com.parsroyal.storemanagement.data.searchobject.CustomerPictureSO;
import com.parsroyal.storemanagement.data.searchobject.VisitInformationDetailSO;
import com.parsroyal.storemanagement.service.CustomerService;
import com.parsroyal.storemanagement.service.PaymentService;
import com.parsroyal.storemanagement.service.QuestionnaireService;
import com.parsroyal.storemanagement.service.SaleOrderService;
import com.parsroyal.storemanagement.service.SettingService;
import com.parsroyal.storemanagement.service.VisitService;
import com.parsroyal.storemanagement.service.impl.CustomerServiceImpl;
import com.parsroyal.storemanagement.service.impl.PaymentServiceImpl;
import com.parsroyal.storemanagement.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.storemanagement.service.impl.SaleOrderServiceImpl;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.service.impl.VisitServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.SingleDataTransferAdapter;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.ToastUtil;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Arash
 */
public class SingleDataTransferDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.data_transfer_btn)
  Button uploadDataBtn;
  @BindView(R.id.upload_data_btn_disabled)
  Button uploadDataBtnDisabled;
  @BindView(R.id.progress_bar)
  ProgressBar progressBar;
  @BindView(R.id.cancel_btn)
  TextView cancelBtn;
  @BindView(R.id.root)
  View root;

  private MainActivity mainActivity;
  private long orderId;
  private long visitId;
  private SingleDataTransferAdapter adapter;
  private VisitService visitService;
  private boolean transferStarted;
  private boolean transferFinished = false;
  private VisitInformationDetail currentModel;
  private List<VisitInformationDetail> model;
  private int currentPosition = -1;
  private VisitInformation visit;
  private boolean transferSuccess;
  private SettingService settingService;

  public SingleDataTransferDialogFragment() {
    // Required empty public constructor
  }

  public static SingleDataTransferDialogFragment newInstance(Bundle arguments) {
    SingleDataTransferDialogFragment fragment = new SingleDataTransferDialogFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_single_data_transfer_dialog, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();

    Bundle args = getArguments();
    if (Empty.isNotEmpty(args)) {
      visitId = args.getLong(Constants.VISIT_ID, -1);
      if (visitId == -1) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }

      visitService = new VisitServiceImpl(mainActivity);
      settingService = new SettingServiceImpl();
      visit = visitService.getVisitInformationById(visitId);
      if (Empty.isEmpty(visit)) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }

      setUpRecyclerView();
      return view;
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
  }

  //set up recycler view
  private void setUpRecyclerView() {
    model = getSimpleModel();
    adapter = new SingleDataTransferAdapter(mainActivity, this, model);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<VisitInformationDetail> getSimpleModel() {
    VisitInformationDetailSO visitInformationDetailSO = new VisitInformationDetailSO(visitId,
        VisitInformationDetail.COL_TYPE);
    return visitService.searchVisitDetail(visitInformationDetailSO);
  }

  @OnClick({R.id.close, R.id.data_transfer_btn, R.id.cancel_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
      case R.id.cancel_btn:
        if (transferFinished && transferSuccess) {
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_REFRESH_DATA));
          EventBus.getDefault().post(new ActionEvent(StatusCodes.SUCCESS));
        }
        getDialog().dismiss();
        break;
      case R.id.data_transfer_btn:
        if (transferFinished) {
          if (transferSuccess) {
            EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_REFRESH_DATA));
            EventBus.getDefault().post(new ActionEvent(StatusCodes.SUCCESS));
            getDialog().dismiss();
          } else {
            setUpRecyclerView();
            startTransfer();
          }
        } else {
          startTransfer();
        }
        break;
    }
  }

  private void startTransfer() {
    transferStarted = true;
    transferFinished = false;
    transferSuccess = false;
    currentPosition = -1;
    switchButtonState();
    sendNextDetail();
    //send VisitInformationDetail
  }

  private void switchButtonState() {
    if (transferStarted && !transferFinished) {
      uploadDataBtn.setVisibility(View.INVISIBLE);
      uploadDataBtnDisabled.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.VISIBLE);
    } else {
      uploadDataBtn.setVisibility(View.VISIBLE);
      uploadDataBtnDisabled.setVisibility(View.GONE);
      progressBar.setVisibility(View.GONE);
    }
  }

  private void sendNextDetail() {
    //Uploading started
    currentPosition++;
    adapter.setCurrent(currentPosition);
    if (currentPosition == model.size()) {
      //Send Visit
      sendVisit();
      return;
    }
    if (currentPosition > model.size()) {
      return;
    }
    VisitInformationDetail visitDetail = model.get(currentPosition);
//    adapter.setCurrent(currentPosition);
    currentModel = visitDetail;
    switch (VisitInformationDetailType.getByValue(visitDetail.getType())) {
      case CREATE_ORDER:
        sendOrder(visitDetail.getTypeId());
        break;
      case CREATE_REJECT:
        sendReject(visitDetail.getTypeId());
      case CREATE_INVOICE:
        break;
      case TAKE_PICTURE:
        sendSinglePicture(visitDetail.getVisitInformationId());
        break;
      case FILL_QUESTIONNAIRE:
        sendAnswers(visitDetail.getVisitInformationId());
        break;
      case SAVE_LOCATION:
        sendSaveLocation();
        break;
      case CASH:
        sendPayments(visitDetail.getVisitInformationId());
        break;
      case DELIVER_ORDER:
        sendInvoice(visitDetail.getTypeId());
        break;
      case DELIVER_FREE_ORDER:
        sendFreeOrder(visitDetail.getTypeId());
      default:
    }
  }

  private void sendReject(long typeId) {
    SaleOrderServiceImpl saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(typeId);

    if (Empty.isNotEmpty(saleOrder)) {
      Thread dataTransfer = new Thread(() -> {

        SaleRejectsDataTransferBizImpl dataTransfer1 = new SaleRejectsDataTransferBizImpl(
            mainActivity);
        dataTransfer1.setOrder(saleOrder);
        dataTransfer1.exchangeData();
        if (dataTransfer1.getSuccess() == 1) {
          sendNextDetail();
        } else {
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
        }
      });
      dataTransfer.start();
    } else {
      sendNextDetail();
    }
  }

  //Sync call
  private void sendAnswers(long visitId) {
    QuestionnaireService questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    List<QAnswerDto> answersForSend = questionnaireService.getAllAnswersDtoForSend(visitId);

    if (Empty.isEmpty(answersForSend)) {
      sendNextDetail();
    } else {
      QAnswersDataTransferBizImpl qAnswersDataTransferBizImpl = new QAnswersDataTransferBizImpl(
          mainActivity);

      Thread sendDataThead = new Thread(() -> {

        for (int i = 0; i < answersForSend.size(); i++) {
          QAnswerDto qAnswerDto = answersForSend.get(i);
          qAnswersDataTransferBizImpl.setAnswer(qAnswerDto);
          qAnswersDataTransferBizImpl.exchangeData();
        }
        if (qAnswersDataTransferBizImpl.getSuccess() == qAnswersDataTransferBizImpl.getTotal()) {
          sendNextDetail();
        } else {
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
        }
      });
      sendDataThead.start();
    }
  }

  private void sendPayments(long visitId) {
    PaymentService paymentService = new PaymentServiceImpl(mainActivity);
    List<Payment> payments = paymentService.getAllPaymentsByVisitId(visitId);
    Thread sendDataThead = new Thread(() -> {

      if (Empty.isNotEmpty(payments)) {
        PaymentsDataTransferBizImpl paymentsDataTransferBiz = new PaymentsDataTransferBizImpl(
            mainActivity);
        paymentsDataTransferBiz.setPayments(payments);
        try {
          paymentsDataTransferBiz.exchangeData();
        } catch (Exception ex) {
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
        }
      } else {
        sendNextDetail();
      }
    });
    sendDataThead.start();
  }

  private void sendSaveLocation() {
    CustomerServiceImpl customerService = new CustomerServiceImpl(mainActivity);
    CustomerLocationDto customerLocationDto = customerService
        .findCustomerLocationDtoByCustomerBackendId(visit.getCustomerBackendId());
    Thread sendDataThead = new Thread(() -> {

      if (Empty.isNotEmpty(customerLocationDto)) {
        UpdatedCustomerLocationDataTransferBizImpl locationDataTransferBiz =
            new UpdatedCustomerLocationDataTransferBizImpl(mainActivity);
        locationDataTransferBiz.setData(customerLocationDto);
        locationDataTransferBiz.exchangeData();
      } else {
        sendNextDetail();
      }
    });
    sendDataThead.start();
  }

  private void sendSinglePicture(long visitId) {
    CustomerService customerService = new CustomerServiceImpl(mainActivity);
    List<CustomerPic> pics = customerService
        .findCustomerPic(new CustomerPictureSO(visitId, CustomerStatus.NEW.getId()));
    if (Empty.isEmpty(pics)) {
      sendNextDetail();
    }
    PictureDataTransferBizImpl dataTransfer = new PictureDataTransferBizImpl(mainActivity);

    Thread sendDataThead = new Thread(() -> {
      try {
        for (int i = 0; i < pics.size(); i++) {
          CustomerPic pic = pics.get(i);
          dataTransfer.setData(pic);
          dataTransfer.exchangeData();
        }
        if (dataTransfer.getSuccess() == dataTransfer.getTotal()) {
          sendNextDetail();
        } else {
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
        }
      } catch (Exception ex) {
        EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
      }
    });
    sendDataThead.start();
  }

  //Async call
  private void sendOrder(Long orderId) {
    SaleOrderService saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(orderId);
    if (Empty.isNotEmpty(saleOrder)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(mainActivity, false);
      dataTransfer.sendSingleOrder(saleOrder);
    } else {
      //We have sent them before
      sendNextDetail();
    }
  }//Async call

  private void sendFreeOrder(Long orderId) {
    SaleOrderService saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(orderId);
    if (Empty.isNotEmpty(saleOrder)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(mainActivity, true);
      dataTransfer.sendSingleOrder(saleOrder);
    } else {
      //We have sent them before
      sendNextDetail();
    }
  }

  private void sendInvoice(Long orderId) {
    SaleOrderService saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(orderId);
    if (Empty.isNotEmpty(saleOrder)) {
      if (saleOrder.getStatus().equals(SaleOrderStatus.CANCELED.getId())) {
        Thread sendDataThead = new Thread(() -> {
          try {
            CanceledOrdersDataTransfer ordersDataTransfer = new CanceledOrdersDataTransfer(
                mainActivity);
            ordersDataTransfer.setOrder(saleOrder);
            ordersDataTransfer.exchangeData();
            if (ordersDataTransfer.getSuccess() == 1) {
              sendNextDetail();
            } else {
              EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
            }
          } catch (Exception ex) {
            EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
          }
        });
        sendDataThead.start();
      } else {
        String stockCode = settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE);
        saleOrder.setStockCode(Empty.isEmpty(stockCode) ? 0 : Integer.valueOf(stockCode));
        String branchCode = settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_CODE);
        saleOrder.setOfficeCode(Empty.isEmpty(branchCode) ? 0 : Integer.valueOf(branchCode));
        InvoicedOrdersDataTransfer dataTransfer = new InvoicedOrdersDataTransfer(mainActivity);
        dataTransfer.sendSingleInvoice(saleOrder);
      }
    } else {
      //We have sent them before
      sendNextDetail();
    }
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof SendOrderEvent || event instanceof SuccessEvent) {
      if (event.getStatusCode().equals(StatusCodes.SUCCESS)) {
        sendNextDetail();
      } else {
        cancelTransfer();
      }
    } else if (event instanceof ErrorEvent) {
      cancelTransfer();
    } else if (event instanceof ActionEvent) {
      if (event.getStatusCode() == StatusCodes.ACTION_FINISH_TRANSFER) {
        finishTransfer();
      } else if (event.getStatusCode() == StatusCodes.ACTION_CANCEL_TRANSFER) {
        cancelTransfer();
      }
    } else if (event instanceof DataTransferSuccessEvent) {
      sendNextDetail();
    } else if (event instanceof DataTransferErrorEvent) {
      cancelTransfer();
    }
  }

  private void sendVisit() {

    List<VisitInformationDto> visitInformationList = visitService.getAllVisitDetailForSend(visitId);
    if (Empty.isEmpty(visitInformationList)) {
      EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
      return;
    }
    VisitInformationDataTransfer dataTransfer = new VisitInformationDataTransfer(mainActivity);
    Thread sendDataThead = new Thread(() -> {

      for (int i = 0; i < visitInformationList.size(); i++) {
        VisitInformationDto visitInformationDto = visitInformationList.get(i);
        if (visitInformationDto.getDetails() == null
            || visitInformationDto.getDetails().size() == 0) {
          visitService.deleteVisitById(visitInformationDto.getId());
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_CANCEL_TRANSFER));
          return;
        }
        dataTransfer.setData(visitInformationDto);
        dataTransfer.exchangeData();
      }
      if (dataTransfer.getTotal() == dataTransfer.getSuccess()) {
        finishTransfer();
      } else {
        cancelTransfer();
      }
    });
    sendDataThead.start();
  }

  private void finishTransfer() {
    mainActivity.runOnUiThread(() -> {

      adapter.setCurrent(++currentPosition);
      try {
        ToastUtil.toastMessage(root, getString(R.string.send_data_completed_successfully));
      } catch (Exception ignore) {

      }
      transferFinished = true;
      transferSuccess = true;
      cancelBtn.setVisibility(View.GONE);
      switchButtonState();
      uploadDataBtn.setText(R.string.finish);
      Drawable img = getResources().getDrawable(R.drawable.ic_check_white_18_dp);
      img.setBounds(10, 0, 0, 0);
      uploadDataBtn.setCompoundDrawables(img, null, null, null);
    });
  }

  private void cancelTransfer() {
    mainActivity.runOnUiThread(() -> {
      transferFinished = true;
      transferSuccess = false;

      switchButtonState();
      uploadDataBtn.setText(getString(R.string.retry));
      adapter.setError(currentPosition);
      ToastUtil.toastError(root, getString(R.string.error_in_sending_data));
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }
}