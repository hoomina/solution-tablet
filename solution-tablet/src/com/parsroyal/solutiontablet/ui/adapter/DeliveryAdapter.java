package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.DeliveryAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.SingleDataTransferDialogFragment;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Arash on 6/17/2018.
 */

public class DeliveryAdapter extends Adapter<ViewHolder> {

  private final long visitlineBackendId;
  private LayoutInflater inflater;
  private Context context;
  private boolean isFromReport;
  private MainActivity mainActivity;
  private List<SaleOrderListModel> orders;
  private Long visitId;

  public DeliveryAdapter(Context context, List<SaleOrderListModel> orders, boolean isFromReport,
      Long visitId, long visitlineBackendId) {
    this.context = context;
    this.visitId = visitId == null ? 0 : visitId;
    this.orders = orders;
    this.mainActivity = (MainActivity) context;
    this.isFromReport = isFromReport;
    this.visitlineBackendId = visitlineBackendId;
    inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_order_list, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    SaleOrderListModel order = orders.get(position);
    holder.setOrderData(position, order);
  }

  @Override
  public int getItemCount() {
    return orders.size();
  }

  public void update(List<SaleOrderListModel> orders) {
    this.orders = orders;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.order_code_tv)
    TextView orderCodeTv;
    @BindView(R.id.order_date_tv)
    TextView orderDateTv;
    @BindView(R.id.order_total_price)
    TextView orderTotalPrice;
    @BindView(R.id.order_count_tv)
    TextView orderCountTv;
    @BindView(R.id.order_payment_method_tv)
    TextView orderPaymentMethodTv;
    @Nullable
    @BindView(R.id.main_lay_rel)
    RelativeLayout mainLayRel;
    @Nullable
    @BindView(R.id.main_lay)
    LinearLayout mainLayLin;
    @BindView(R.id.order_status_tv)
    TextView orderStatusTv;
    @BindView(R.id.upload_img)
    ImageView uploadImg;
    @BindView(R.id.delete_img_layout)
    LinearLayout deleteImageLayout;
    @BindView(R.id.edit_img_layout)
    LinearLayout editImageLayout;
    @BindView(R.id.upload_img_layout)
    LinearLayout uploadImageLayout;
    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.customer_code_tv)
    TextView customerCodeTv;
    @Nullable
    @BindView(R.id.customer_layout)
    LinearLayout customerLayout;

    private int position;
    private SaleOrderListModel order;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void setOrderData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;

      String orderCode =
          "کد سفارش : " + NumberUtil.digitsToPersian(String.valueOf(order.getNumber()));
      orderCodeTv.setText(orderCode);
      customerNameTv.setText(order.getCustomerName());

      orderCountTv.setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderCount())));

      Date createdDate = DateUtil
          .convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);

      orderDateTv.setText(NumberUtil.digitsToPersian(dateString));
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      orderTotalPrice.setText(NumberUtil.digitsToPersian(number));
      orderPaymentMethodTv.setText(NumberUtil.digitsToPersian(order.getPaymentTypeTitle()));

      changeVisibility();
    }

    private void changeVisibility() {
      if (!MultiScreenUtility.isTablet(context)) {
        editImageLayout.setVisibility(View.GONE);
        deleteImageLayout.setVisibility(View.GONE);
        uploadImageLayout.setVisibility(View.GONE);
      } else {
        editImageLayout.setVisibility(View.INVISIBLE);
        deleteImageLayout.setVisibility(View.INVISIBLE);
        uploadImageLayout.setVisibility(View.INVISIBLE);
      }

      orderStatusTv.setVisibility(View.VISIBLE);
      Long status = order.getStatus();
      if (SaleOrderStatus.DELIVERABLE.getId().equals(status)) {
        orderStatusTv.setText(R.string.deliverable);
      } else if (SaleOrderStatus.DELIVERABLE_SENT.getId().equals(status)) {
        orderStatusTv.setText(R.string.sent);
      } else if (SaleOrderStatus.DELIVERED.getId().equals(status)) {
        orderStatusTv.setText(R.string.delivered);
      } else if (SaleOrderStatus.CANCELED.getId().equals(status)) {
        orderStatusTv.setText(R.string.canceled);
      }

      if (isFromReport) {
        customerNameTv.setVisibility(View.VISIBLE);
        customerCodeTv.setVisibility(View.VISIBLE);
        customerCodeTv
            .setText(NumberUtil.digitsToPersian(String.format("(%s)", order.getCustomerCode())));
        if (customerLayout != null) {
          customerLayout.setVisibility(View.VISIBLE);
        }
      } else {
        customerNameTv.setVisibility(View.GONE);
        customerCodeTv.setVisibility(View.GONE);
        if (customerLayout != null) {
          customerLayout.setVisibility(View.GONE);
        }
      }
    }

    @OnClick({R.id.delete_img_layout, R.id.edit_img_layout, R.id.main_lay, R.id.main_lay_rel,
        R.id.upload_img, R.id.upload_img_layout})
    @Optional
    public void onClick(View v) {

      switch (v.getId()) {
        case R.id.delete_img_layout:

          break;
        case R.id.edit_img_layout:
        case R.id.main_lay_rel:
        case R.id.main_lay:
          Bundle args = new Bundle();
          args.putLong(Constants.ORDER_ID, order.getId());
          args.putLong(Constants.VISIT_ID, visitId);
          args.putLong(Constants.VISITLINE_BACKEND_ID, visitlineBackendId);
          args.putBoolean(Constants.READ_ONLY, false);
          setPageStatus(args);
          mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, false);
          break;
        case R.id.upload_img:
        case R.id.upload_img_layout:
          if (!order.getStatus().equals(SaleOrderStatus.SENT.getId())) {
            openSendDataDialog();
          }
          break;
      }
    }

    private void setPageStatus(Bundle args) {
      if (order.getStatus().equals(SaleOrderStatus.SENT.getId())
          || order.getStatus().equals(SaleOrderStatus.CANCELED.getId())
          || order.getStatus().equals(SaleOrderStatus.SENT_INVOICE.getId())
          || order.getStatus().equals(SaleOrderStatus.DELIVERABLE_SENT.getId())
          || order.getStatus().equals(SaleOrderStatus.DELIVERED.getId())
          || order.getStatus().equals(SaleOrderStatus.REJECTED_SENT.getId())) {

        args.putString(Constants.PAGE_STATUS, Constants.VIEW);
      } else {
        args.putString(Constants.PAGE_STATUS, Constants.EDIT);
      }
    }

    private void openSendDataDialog() {
      FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
      Bundle args = new Bundle();
      args.putLong(Constants.VISIT_ID, order.getVisitId());
      SingleDataTransferDialogFragment singleDataTransferDialogFragment = SingleDataTransferDialogFragment
          .newInstance(args);

      singleDataTransferDialogFragment.show(ft, "data_transfer");
    }
  }
}
