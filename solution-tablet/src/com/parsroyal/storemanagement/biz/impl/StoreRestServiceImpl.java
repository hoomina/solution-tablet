package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.impl.StockGoodDaoImpl;
import com.parsroyal.storemanagement.data.event.ActionEvent;
import com.parsroyal.storemanagement.data.event.DetectGoodEvent;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.PackerEvent;
import com.parsroyal.storemanagement.data.event.StockEvent;
import com.parsroyal.storemanagement.data.event.StockGoodsEvent;
import com.parsroyal.storemanagement.data.model.DetectGoodDetail;
import com.parsroyal.storemanagement.data.model.DetectGoodRequest;
import com.parsroyal.storemanagement.data.model.Packer;
import com.parsroyal.storemanagement.data.model.SelectOrderRequest;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.service.StoreRestService;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreRestServiceImpl {

  public void detectGood(Context context, String barcode) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    StoreRestService restService = ServiceGenerator.createService(StoreRestService.class);

    Call<List<DetectGoodDetail>> call = restService
        .detectGood(new DetectGoodRequest(barcode, PreferenceHelper.getStockKey()));

    call.enqueue(new Callback<List<DetectGoodDetail>>() {
      @Override
      public void onResponse(Call<List<DetectGoodDetail>> call,
          Response<List<DetectGoodDetail>> response) {
        if (response.isSuccessful()) {
          List<DetectGoodDetail> result = response.body();
          if (result != null && result.size() > 0) {
            EventBus.getDefault().post(new DetectGoodEvent(result));
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_DATA_ERROR));

          }
        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<List<DetectGoodDetail>> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  public void selectOrder(Context context, SelectOrderRequest request) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    StoreRestService restService = ServiceGenerator.createService(StoreRestService.class);

    Call<Packer> call = restService.selectOrder(request);

    call.enqueue(new Callback<Packer>() {
      @Override
      public void onResponse(Call<Packer> call, Response<Packer> response) {
        if (response.isSuccessful()) {
          Packer result = response.body();
          if (request.getMode() == 4) {
            EventBus.getDefault().post(new ActionEvent(StatusCodes.DELETE_ORDER_SUCCESS));
          }
          if (result != null && result.getOrdr() != 0) {
            EventBus.getDefault().post(new PackerEvent(result));
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_DATA_ERROR));

          }
        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_DATA_ERROR));
        }
      }

      @Override
      public void onFailure(Call<Packer> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  public void checkStockStatus(Context context, Long selectedStockAsn) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    StoreRestService restService = ServiceGenerator.createService(StoreRestService.class);

    Call<String> call = restService.checkStockStatus(selectedStockAsn);

    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          String result = response.body();

          EventBus.getDefault()
              .post(new StockEvent(StatusCodes.STOCK_CHECK_STATUS, "1".equals(result)));

        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_DATA_ERROR));
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  public void getStockGoods(Context context, Long selectedStockAsn) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    StoreRestService restService = ServiceGenerator.createService(StoreRestService.class);

    Call<List<StockGood>> call = restService.getStockGoods(selectedStockAsn);

    call.enqueue(new Callback<List<StockGood>>() {
      @Override
      public void onResponse(Call<List<StockGood>> call, Response<List<StockGood>> response) {
        if (response.isSuccessful()) {
          List<StockGood> result = response.body();
          if (Empty.isNotEmpty(result)) {
            new StockGoodDaoImpl(context).bulkInsert(result);
            EventBus.getDefault().post(new StockGoodsEvent(StatusCodes.SUCCESS, result));
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_DATA_ERROR));
          }

        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<List<StockGood>> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
