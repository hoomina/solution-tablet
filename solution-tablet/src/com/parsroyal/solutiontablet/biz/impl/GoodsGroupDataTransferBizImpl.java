package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.GoodsGroupDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsGroupDaoImpl;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 27/12/2017
 */
public class GoodsGroupDataTransferBizImpl {

  public static final String TAG = GoodsGroupDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private GoodsGroupDao goodsGroupDao;
  private SettingService settingService;

  public GoodsGroupDataTransferBizImpl(Context context) {
    this.context = context;
    this.goodsGroupDao = new GoodsGroupDaoImpl(context);
    this.settingService = new SettingServiceImpl(context);
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<String> call = restService
        .getAllGoodGroups(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID));

    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          String data = response.body();
          try {
            List<GoodsGroup> list = new Gson().fromJson(data, new TypeToken<List<GoodsGroup>>() {
            }.getType());
            if (Empty.isNotEmpty(data) && Empty.isNotEmpty(list)) {

              for (GoodsGroup group : list) {
                group.setTitle(CharacterFixUtil.fixString(group.getTitle()));
              }
              goodsGroupDao.bulkInsert(list);

              EventBus.getDefault().post(new DataTransferSuccessEvent(
                  context.getString(R.string.provinces_data_transferred_successfully)
                  , StatusCodes.SUCCESS));
            } else {
              EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.INVALID_DATA));
            }

          } catch (Exception ex) {
            Logger.sendError("Data transfer", "Error in receiving GoodsGroupData "
                + ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
            EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
          }
        } else {
          try {
            Log.d("TAG", response.errorBody().string());
          } catch (IOException e) {
            e.printStackTrace();
          }
          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

}
