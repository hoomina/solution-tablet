package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.listmodel.CustomerListModel;
import com.parsroyal.storemanagement.exception.UnknownSystemException;
import com.parsroyal.storemanagement.service.CustomerService;
import com.parsroyal.storemanagement.service.impl.CustomerServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.fragment.bottomsheet.CustomerInfoBottomSheet;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.CustomerInfoDialogFragment;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.CustomerSearchDialogFragment;
import com.parsroyal.storemanagement.util.CharacterFixUtil;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.ToastUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Shakib
 */
public class PathDetailAdapter extends RecyclerView.Adapter<PathDetailAdapter.ViewHolder> {

  private final CustomerService customerService;

  private Long visitlineBackendId;
  private LayoutInflater inflater;
  private MainActivity mainActivity;
  private List<CustomerListModel> customers;
  private CustomerSearchDialogFragment customerSearchDialogFragment;

  public PathDetailAdapter(Context mainActivity, List<CustomerListModel> customers,
      Long visitLineBackendId) {
    this.mainActivity = (MainActivity) mainActivity;
    this.customers = customers;
    inflater = LayoutInflater.from(mainActivity);
    customerService = new CustomerServiceImpl(mainActivity);

    this.visitlineBackendId = visitLineBackendId;
  }

  public void setSearchCallback(CustomerSearchDialogFragment dialogFragment) {
    this.customerSearchDialogFragment = dialogFragment;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_customers_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    CustomerListModel model = customers.get(position);
    setMargin(position == customers.size() - 1, holder.customerLay);
    holder.setData(model, position);
    holder.setListeners();
  }

  private void setMargin(boolean isLastItem, CardView cardView) {
    CardView.LayoutParams parameter = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    float scale = mainActivity.getResources().getDisplayMetrics().density;
    int paddingInPx = (int) (8 * scale + 0.5f);
    if (isLastItem) {
      parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    } else {
      if (MultiScreenUtility.isTablet(mainActivity)) {
        parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
      } else {
        parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, 0);
      }
    }
    cardView.setLayoutParams(parameter);
  }

  @Override
  public int getItemCount() {
    return customers.size();
  }

  public List<CustomerListModel> getDataModel() {
    return customers;
  }

  public void setDataModel(List<CustomerListModel> dataModel) {
    this.customers = dataModel;
  }

  public List<CustomerListModel> getFilteredData(CharSequence constraint) {
    try {
      String keyword = "";
      if (constraint.length() != 0 && !constraint.toString().equals("")) {
        keyword = CharacterFixUtil.fixString(constraint.toString());
      }
      return customerService.getFilteredCustomerList(visitlineBackendId, keyword, false);

    } catch (final Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Filter data", "Error in filtering customer list" + ex.getMessage());
      mainActivity
          .runOnUiThread(() -> ToastUtil.toastError(mainActivity, new UnknownSystemException(ex)));
      return Collections.emptyList();
    }
  }

  public void notifyItemHasRejection(int position) {
    customers.get(position).setHasRejection(true);
    notifyItemChanged(position);
  }

  public void update(List<CustomerListModel> customerListModels) {
    this.customers = customerListModels;
    notifyDataSetChanged();
  }

  /**
   * @author Arash
   */
  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.customer_id_tv)
    TextView customerIdTv;
    @BindView(R.id.customer_shop_name_tv)
    TextView customerShopNameTv;
    @BindView(R.id.has_location_img)
    ImageView hasLocationImg;
    @BindView(R.id.visit_today_img)
    ImageView visitTodayImg;
    @BindView(R.id.has_order_img)
    ImageView hasOrderImg;
    @BindView(R.id.counter_img)
    TextView counterImg;
    @BindView(R.id.has_answer_img)
    ImageView hasAnswerImg;
    @BindView(R.id.customer_lay)
    CardView customerLay;
    private CustomerListModel model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(CustomerListModel model, int position) {
      this.model = model;
      this.position = position;
      customerShopNameTv.setText(model.getShopName());
      customerNameTv.setText(model.getTitle());
      customerIdTv.setText(String.format(mainActivity.getString(R.string.code_x),
          NumberUtil.digitsToPersian(model.getCode())));
      //set location icon
      if (model.hasLocation()) {
        hasLocationImg.setImageResource(R.drawable.ic_gps_fixed_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.primary));
      } else {
        hasLocationImg.setImageResource(R.drawable.ic_gps_off_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      }
      //set visit icon
      if (model.isVisited()) {
        if (model.isPhoneVisit()) {
          visitTodayImg.setImageResource(R.drawable.ic_call);
          visitTodayImg
              .setColorFilter(ContextCompat.getColor(mainActivity, R.color.log_in_enter_bg));
        }else {
          visitTodayImg
              .setColorFilter(ContextCompat.getColor(mainActivity, R.color.log_in_enter_bg));
        }
      } else {
        visitTodayImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      }
      if (model.hasRejection()) {
        visitTodayImg
            .setColorFilter(ContextCompat.getColor(mainActivity, R.color.badger_background));
      }
      if (model.hasNoOrder()) {
        visitTodayImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.orange));
      }

      ArrayList<VisitInformationDetailType> list = new ArrayList<>();
      list.addAll(model.getDetails());

      if (list.size() > 0) {
        hasOrderImg.setVisibility(View.VISIBLE);
        hasOrderImg.setImageResource(list.get(0).getDrawable());
      } else {
        hasOrderImg.setVisibility(View.GONE);
      }
      if (list.size() > 1) {
        hasAnswerImg.setVisibility(View.VISIBLE);
        hasAnswerImg.setImageResource(list.get(1).getDrawable());
      } else {
        hasAnswerImg.setVisibility(View.GONE);
      }

      if (list.size() > 2) {
        counterImg.setVisibility(View.VISIBLE);
        counterImg
            .setText(NumberUtil.digitsToPersian(String.format(Locale.US, "+%d", list.size() - 2)));
      } else {
        counterImg.setVisibility(View.GONE);
      }
    }

    void setListeners() {
      customerLay.setOnClickListener(v -> showCustomerDetailDialog());
    }

    private void showCustomerDetailDialog() {
      FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();

      CustomerInfoDialogFragment customerInfoDialogFragment;
      if (MultiScreenUtility.isTablet(mainActivity)) {
        customerInfoDialogFragment = CustomerInfoBottomSheet
            .newInstance(PathDetailAdapter.this, model, position);
      } else {
        customerInfoDialogFragment = CustomerInfoDialogFragment
            .newInstance(PathDetailAdapter.this, model, position);
      }
      if (customerSearchDialogFragment != null) {
        customerSearchDialogFragment.getDialog().dismiss();
      }
      customerInfoDialogFragment.show(ft, "customer info");
    }
  }
}