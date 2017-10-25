package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PaymentAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class PaymentAdapter extends Adapter<ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private long visitId;
  private boolean isFromReport;
  private MainActivity mainActivity;
  private List<PaymentListModel> payments;

  public PaymentAdapter(Context context, List<PaymentListModel> payments, long visitId,
      boolean isFromReport) {
    this.context = context;
    this.isFromReport = isFromReport;
    this.visitId = visitId;
    this.payments = payments;
    mainActivity = (MainActivity) context;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (isFromReport) {
      view = inflater.inflate(R.layout.item_payment_report_list, parent, false);
    } else {
      view = inflater.inflate(R.layout.item_payment_list, parent, false);
    }
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    PaymentListModel payment = payments.get(position);
    if (isFromReport) {
      holder.paymentStatusTv.setText(SendStatus.getDisplayTitle(context, payment.getStatus()));
    }
    holder.paymentMethodTv.setText(getPaymentType(payment.getType()));
    Date createDate = DateUtil
        .convertStringToDate(payment.getDate(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME,
            "FA");

    String dateString = DateUtil.getFullPersianDate(createDate);
    holder.paymentDateTv.setText(dateString);
    long amountValue = Long
        .parseLong(NumberUtil.digitsToEnglish(payment.getAmount().replaceAll(",", "")));
    String number = String.format(Locale.US, "%,d %s", amountValue / 1000, context.getString(
        R.string.common_irr_currency));
    holder.paymentTv.setText(number);
    //TODO: arash add bank and branch
//    holder.bankDetailTv.setText(payment.get);
    if (holder.mainLayLin != null) {
      holder.mainLayLin.setOnClickListener(v -> goToRegisterPaymentFragment(payment));
    } else {
      holder.mainLayRel.setOnClickListener(v -> goToRegisterPaymentFragment(payment));
    }
  }

  @Override
  public int getItemCount() {
    return payments.size();
  }

  public void update(List<PaymentListModel> payments) {
    this.payments = payments;
    notifyDataSetChanged();
  }

  private String getPaymentType(String paymentType) {
    switch (paymentType) {
      case "6":
        return mainActivity.getString(R.string.cheque);
      case "1":
        return mainActivity.getString(R.string.cash);
      case "2":
        return mainActivity.getString(R.string.e_payment);
    }
    return null;
  }

  private void goToRegisterPaymentFragment(PaymentListModel payment) {
    Bundle args = new Bundle();
    args.putLong(Constants.VISIT_ID, visitId);
    args.putLong(Constants.CUSTOMER_BACKEND_ID, payment.getCustomerBackendId());
    args.putLong(Constants.PAYMENT_ID, payment.getPrimaryKey());
    mainActivity.changeFragment(MainActivity.REGISTER_PAYMENT_FRAGMENT, args, true);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    @BindView(R.id.payment_status_tv)
    TextView paymentStatusTv;
    @BindView(R.id.payment_method_tv)
    TextView paymentMethodTv;
    @BindView(R.id.payment_date_tv)
    TextView paymentDateTv;
    @BindView(R.id.payment_tv)
    TextView paymentTv;
    @BindView(R.id.bank_detail_tv)
    TextView bankDetailTv;
    @Nullable
    @BindView(R.id.main_lay)
    RelativeLayout mainLayRel;
    @Nullable
    @BindView(R.id.main_lay_linear)
    LinearLayout mainLayLin;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}