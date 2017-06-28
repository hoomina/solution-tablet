package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.biz.impl.KeyValueBizImpl;
import com.parsroyal.solutiontablet.biz.impl.UserInformationDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.exception.InvalidServerAddressException;
import com.parsroyal.solutiontablet.exception.PasswordNotProvidedForConnectingToServerException;
import com.parsroyal.solutiontablet.exception.UsernameNotProvidedForConnectingToServerException;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class SettingServiceImpl implements SettingService {

  private Context context;
  private KeyValueBiz keyValueBiz;
  private KeyValueDao keyValueDao;

  private KeyValue serverAddress1;
  private KeyValue username;
  private KeyValue password;

  public SettingServiceImpl(Context context) {
    this.context = context;
    this.keyValueBiz = new KeyValueBizImpl(context);
    this.keyValueDao = new KeyValueDaoImpl(context);
  }

  @Override
  public void saveSetting(String settingKey, String settingValue) {
    keyValueBiz.save(new KeyValue(settingKey, settingValue));
  }

  @Override
  public String getSettingValue(String key) {
    KeyValue keyValue = keyValueBiz.findByKey(key);
    if (Empty.isNotEmpty(keyValue)) {
      return keyValue.getValue();
    }
    return null;
  }

  @Override
  public void getUserInformation(ResultObserver observer) {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    if (Empty.isEmpty(serverAddress1)) {
      throw new InvalidServerAddressException();
    }

    if (Empty.isEmpty(username)) {
      throw new UsernameNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(password)) {
      throw new PasswordNotProvidedForConnectingToServerException();
    }

    UserInformationDataTransferBizImpl userInformationBiz = new UserInformationDataTransferBizImpl(
        context, observer);
    userInformationBiz.exchangeData();
  }
}
