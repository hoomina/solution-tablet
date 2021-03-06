package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 */
public interface BaseInfoService extends BaseService {

  KeyValue getKeyValue(String key);

  List<Province> getAllProvinces();

  List<City> getAllCities();

  List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long id);

  List<LabelValue> getAllSupplier();

  List<LabelValue> getAllAssortment();

  LabelValue getBaseInfoByBackendId(Long typeId, Long backendId);

  List<LabelValue> getAllProvincesLabelValues();

  List<LabelValue> getAllCitiesLabelsValues(Long provinceId);

  List<LabelValue> getAllPaymentType();

  void deleteAllCities();

  void deleteAllProvinces();

  List<LabelValue> search(Long activityType, String constraint);

  List<BaseInfo> retrieveByTypeAndCode(Long type, Long code);

}
