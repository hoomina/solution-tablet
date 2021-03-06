package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.SmsReasonAdapter;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderInfoFragment;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;

/**
 * Created by shkbhbb on 9/17/18.
 */

public class SmsDialogFragment extends DialogFragment {

  @BindView(R.id.sms_title_tv)
  TextView smsTitleTv;
  @BindView(R.id.sms_code_edt)
  EditText smsCodeEdt;
  @BindView(R.id.main_lay)
  LinearLayout mainLay;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.reason_lay)
  LinearLayout reasonLay;
  @BindView(R.id.code_lay)
  LinearLayout codeLay;
  @BindView(R.id.back_iv)
  ImageView backView;
  @BindView(R.id.spacer)
  View spacer;

  private MainActivity mainActivity;
  private int rand;
  private OrderInfoFragment parent;
  private String phoneNum;
  private boolean smsFailedMode;
  private SmsReasonAdapter smsReasonAdapter;

  public SmsDialogFragment() {

  }

  public static SmsDialogFragment newInstance(MainActivity mainActivity,
      OrderInfoFragment orderInfoFragment, int rand, String cellPhone, boolean smsFailedMode) {
    SmsDialogFragment smsDialogFragment = new SmsDialogFragment();
    smsDialogFragment.mainActivity = mainActivity;
    smsDialogFragment.parent = orderInfoFragment;
    smsDialogFragment.rand = rand;
    smsDialogFragment.phoneNum = cellPhone;
    smsDialogFragment.smsFailedMode = smsFailedMode;
    return smsDialogFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_dialog_sms, container, false);
    ButterKnife.bind(this, view);
    smsCodeEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() == 5 && Integer.parseInt(s.toString()) == rand) {
          parent.updateOrderSmsConfirmed(-1);
          getDialog().dismiss();
          mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
        } else if (s.length() == 5) {
          ToastUtil.toastError(mainLay, getString(R.string.invalid_code));
        }
      }
    });
    smsTitleTv.setText(String
        .format("همکار گرامی! کد اعتبار سنجی ثبت سفارش، به شماره همراه %s ارسال گردید!",
            NumberUtil.digitsToPersian(phoneNum)));
    if (smsFailedMode) {
      smsFailed();
    }
    return view;
  }

  private void smsFailed() {
    codeLay.setVisibility(View.GONE);
    reasonLay.setVisibility(View.VISIBLE);
    setUpRecyclerView();
    backView.setVisibility(View.GONE);
    spacer.setVisibility(View.VISIBLE);
  }

  private void setUpRecyclerView() {
    BaseInfoServiceImpl baseInfoService = new BaseInfoServiceImpl(mainActivity);
    List<LabelValue> list = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.SMS_CONFIRM.getId());

    smsReasonAdapter = new SmsReasonAdapter(getActivity(), list);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(smsReasonAdapter);
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }

  @OnClick({R.id.back_iv, R.id.send_btn, R.id.close_iv, R.id.resend_tv, R.id.no_code_tv})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.close_iv:
        getDialog().dismiss();
        mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
        break;
      case R.id.resend_tv:
        rand = parent.calculateRand();
        parent.sendSMS();
        break;
      case R.id.no_code_tv:
        codeLay.setVisibility(View.GONE);
        reasonLay.setVisibility(View.VISIBLE);
        setUpRecyclerView();
        break;
      case R.id.back_iv:
        reasonLay.setVisibility(View.GONE);
        codeLay.setVisibility(View.VISIBLE);
        break;
      case R.id.send_btn:
        getDialog().dismiss();
        parent.updateOrderSmsConfirmed(
            smsReasonAdapter != null ? smsReasonAdapter.getSelectedItem() : 0);
        mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
        break;
    }
  }
}
