package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.DetectGoodEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.PackerEvent;
import com.parsroyal.solutiontablet.data.model.DetectGoodDetail;
import com.parsroyal.solutiontablet.data.model.DetectGoodRequest;
import com.parsroyal.solutiontablet.data.model.Packer;
import com.parsroyal.solutiontablet.data.model.SelectOrderRequest;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.StoreRestService;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
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
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_DATA_ERROR));
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
          if (result != null) {
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

}
