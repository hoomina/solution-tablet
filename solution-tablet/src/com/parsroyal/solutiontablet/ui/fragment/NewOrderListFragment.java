package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderAdapter;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;

public class NewOrderListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_order)
  FloatingActionButton fabAddOrder;

  private Long visitId;
  private OrderAdapter adapter;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;
  private SaleOrderSO saleOrderSO = new SaleOrderSO();
  private NewVisitDetailFragment parent;
  private SettingServiceImpl settingService;
  private String saleType;

  public NewOrderListFragment() {
    // Required empty public constructor
  }


  public static NewOrderListFragment newInstance(Bundle arguments,
      NewVisitDetailFragment newVisitDetailFragment) {
    NewOrderListFragment newOrderListFragment = new NewOrderListFragment();
    newOrderListFragment.parent = newVisitDetailFragment;
    newOrderListFragment.setArguments(arguments);
    return newOrderListFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_order_list, container, false);
    mainActivity = (MainActivity) getActivity();
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    this.saleOrderService = new SaleOrderServiceImpl(mainActivity);
    settingService = new SettingServiceImpl(mainActivity);

    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    if (args != null) {
      visitId = args.getLong(Constants.VISIT_ID, -1);
    }
    if (parent == null) {
      fabAddOrder.setVisibility(View.GONE);
    }
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new OrderAdapter(mainActivity, getOrderList(), parent == null, visitId, saleType);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
          fabAddOrder.setVisibility(View.GONE);
        } else {
          fabAddOrder.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private List<SaleOrderListModel> getOrderList() {
    if (parent != null) {
      saleOrderSO.setCustomerBackendId(parent.getCustomer().getBackendId());
    }
    saleOrderSO.setIgnoreDraft(true);
    return saleOrderService.findOrders(saleOrderSO);
  }

  @Override
  public int getFragmentId() {
    return 0;
  }

  @OnClick(R.id.fab_add_order)
  public void onClick() {
    parent.openOrderDetailFragment(SaleOrderStatus.DRAFT.getId());
  }
}
