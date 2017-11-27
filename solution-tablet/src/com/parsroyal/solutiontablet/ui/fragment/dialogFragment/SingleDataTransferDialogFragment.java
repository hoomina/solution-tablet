package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.NewCustomerPicDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.PaymentsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.QAnswersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.UpdatedCustomerLocationDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitInformationDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.SendOrderEvent;
import com.parsroyal.solutiontablet.data.event.SuccessEvent;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.QAnswerDto;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import com.parsroyal.solutiontablet.data.searchobject.VisitInformationDetailSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.DataTransferAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.io.File;
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
  @BindView(R.id.upload_data_btn)
  Button uploadDataBtn;
  @BindView(R.id.upload_data_btn_disabled)
  Button uploadDataBtnDisabled;
  @BindView(R.id.progress_bar)
  ProgressBar progressBar;
  @BindView(R.id.cancel_btn)
  TextView cancelBtn;

  private MainActivity mainActivity;
  private long orderId;
  private long visitId;
  private DataTransferAdapter adapter;
  private VisitService visitService;
  private boolean transferStarted;
  private boolean transferFinished = false;
  private VisitInformationDetail currentModel;
  private List<VisitInformationDetail> model;
  private int currentPosition = -1;
  private VisitInformation visit;

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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
    adapter = new DataTransferAdapter(mainActivity, this, model);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<VisitInformationDetail> getSimpleModel() {
    VisitInformationDetailSO visitInformationDetailSO = new VisitInformationDetailSO(visitId,
        VisitInformationDetail.COL_TYPE);
    return visitService.searchVisitDetail(visitInformationDetailSO);
  }

  @OnClick({R.id.close, R.id.upload_data_btn, R.id.cancel_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
      case R.id.cancel_btn:
        getDialog().dismiss();
        break;
      case R.id.upload_data_btn:
        if (transferFinished) {
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_REFRESH_DATA));
          EventBus.getDefault().post(new ActionEvent(StatusCodes.SUCCESS));
          getDialog().dismiss();
        } else {
          startTransfer();
        }
        break;
    }
  }

  private void startTransfer() {
    transferStarted = true;
    transferFinished = false;
    currentPosition = -1;
    Toast.makeText(mainActivity, "Uploading data...", Toast.LENGTH_SHORT).show();
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
    VisitInformationDetail visitDetail = model.get(currentPosition);
    adapter.setCurrent(currentPosition);
    currentModel = visitDetail;
    switch (VisitInformationDetailType.getByValue(visitDetail.getType())) {
      case CREATE_ORDER:
      case CREATE_REJECT:
      case CREATE_INVOICE:
        sendOrder(visitDetail.getTypeId());
        break;
      case TAKE_PICTURE:
        sendPicture(visitDetail.getVisitInformationId());
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
      default:
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
          mainActivity, null);

      Thread sendDataThead = new Thread(() -> {

        for (int i = 0; i < answersForSend.size(); i++) {
          QAnswerDto qAnswerDto = answersForSend.get(i);
          qAnswersDataTransferBizImpl.setAnswer(qAnswerDto);
          qAnswersDataTransferBizImpl.exchangeData();
        }
        if (qAnswersDataTransferBizImpl.getSuccess() == qAnswersDataTransferBizImpl.getTotal()) {
          sendNextDetail();
        } else {
          cancelTransfer();
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
            mainActivity, null);
        paymentsDataTransferBiz.setPayments(payments);
        paymentsDataTransferBiz.exchangeData();
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
            new UpdatedCustomerLocationDataTransferBizImpl(mainActivity, null);
        locationDataTransferBiz.setData(customerLocationDto);
        locationDataTransferBiz.exchangeData();
      } else {
        sendNextDetail();
      }
    });
    sendDataThead.start();
  }

  private void sendPicture(long visitId) {
    CustomerService customerService = new CustomerServiceImpl(mainActivity);
    File pics = customerService.getAllCustomerPicForSendByVisitId(visitId);
    Thread sendDataThead = new Thread(
        () -> new NewCustomerPicDataTransferBizImpl(mainActivity, null, pics, visitId)
            .exchangeData());
    sendDataThead.start();
  }

  //Async call
  private void sendOrder(Long orderId) {
    SaleOrderService saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(orderId);
    if (Empty.isNotEmpty(saleOrder)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(mainActivity, null);
      dataTransfer.sendSingleOrder(saleOrder);
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
    }
  }

  private void sendVisit() {

    List<VisitInformationDto> visitInformationList = visitService.getAllVisitDetailForSend(visitId);
    if (Empty.isEmpty(visitInformationList)) {
      cancelTransfer();
      return;
    }
    VisitInformationDataTransferBizImpl dataTransfer = new VisitInformationDataTransferBizImpl(
        mainActivity, null);
    Thread sendDataThead = new Thread(() -> {

      for (int i = 0; i < visitInformationList.size(); i++) {
        VisitInformationDto visitInformationDto = visitInformationList.get(i);
        if (visitInformationDto.getDetails() == null
            || visitInformationDto.getDetails().size() == 0) {
          visitService.deleteVisitById(visitInformationDto.getId());
          getActivity().runOnUiThread(this::cancelTransfer);
          return;
        }
        dataTransfer.setData(visitInformationDto);
        dataTransfer.exchangeData();
      }
      getActivity().runOnUiThread(this::finishTransfer);
    });
    sendDataThead.start();
  }

  private void finishTransfer() {
    adapter.setCurrent(++currentPosition);
    ToastUtil.toastMessage(getActivity(), "ارسال اطلاعات با موفقیت انجام شد");
    transferFinished = true;
    switchButtonState();
    uploadDataBtn.setText("تمام");
    Drawable img = getResources().getDrawable(R.drawable.ic_check_white_18_dp);
    img.setBounds(10, 0, 0, 0);
    uploadDataBtn.setCompoundDrawables(img, null, null, null);
  }

  private void cancelTransfer() {
    transferFinished = true;
    switchButtonState();
    adapter.setError(currentPosition);
    ToastUtil.toastError(getActivity(), "خطا در ارسال اطلاعات");
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