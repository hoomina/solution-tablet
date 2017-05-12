package com.parsroyal.solutiontablet.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public class NCustomerDetailFragment extends BaseFragment implements View.OnFocusChangeListener {

  public static final String TAG = NCustomerDetailFragment.class.getSimpleName();

  public static final String CUSTOMER_ID_KEY = "customer_id";

  @BindView(R.id.fullNameTxt)
  EditText fullNameTxt;
  @BindView(R.id.phoneNumberTxt)
  EditText phoneNumberTxt;
  @BindView(R.id.cellPhoneTxt)
  EditText cellPhoneTxt;
  @BindView(R.id.addressTxt)
  EditText addressTxt;
  @BindView(R.id.provinceSp)
  Spinner provinceSp;
  @BindView(R.id.citySp)
  Spinner citySp;
  @BindView(R.id.activitySp)
  Spinner activitySp;
  @BindView(R.id.ownershipSp)
  Spinner ownershipSp;
  @BindView(R.id.classSp)
  Spinner classSp;
  @BindView(R.id.storeSurfaceTxt)
  EditText storeSurfaceTxt;
  @BindView(R.id.getLocationBtn)
  Button getLocationBtn;
  @BindView(R.id.saveBtn)
  Button saveBtn;
  @BindView(R.id.shopNameTxt)
  EditText shopNameTxt;
  @BindView(R.id.nationalCodeTxt)
  EditText nationalCodeTxt;
  @BindView(R.id.municipalityCodeTxt)
  EditText municipalityCodeTxt;
  @BindView(R.id.postalCodeTxt)
  EditText postalCodeTxt;
  @BindView(R.id.root_view)
  ScrollView rootView;

  private Context context;
  private MainActivity mainActivity;
  private CustomerService customerService;
  private BaseInfoService baseInfoService;
  private LocationService locationService;

  private Customer customer;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_new_customer_detail, null);

    ButterKnife.bind(this, view);

    context = getActivity();
    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(context);
    baseInfoService = new BaseInfoServiceImpl(context);
    locationService = new LocationServiceImpl(context);

    try {
      Bundle arguments = getArguments();
      if (Empty.isNotEmpty(arguments) && Empty.isNotEmpty(arguments.getLong(CUSTOMER_ID_KEY))) {
        customer = customerService.getCustomerById(arguments.getLong(CUSTOMER_ID_KEY));
      } else {
        customer = new Customer();
      }
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }

    if (Empty.isEmpty(customer)) {
      ToastUtil.toastError(getActivity(), R.string.message_error_in_loading_or_creating_customer);
      mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_FRAGMENT_ID, true);
    }

    initComponents();
    fullNameTxt.requestFocus();
    return view;
  }

  private void initComponents() {
    try {
      initializeComponents();
      loadSpinnersData();
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
    } catch (Exception ex) {
      Log.e(TAG, ex.getMessage(), ex);
    }
  }

  private void initializeComponents() {
    fullNameTxt.setText(customer.getFullName());
    phoneNumberTxt.setText(customer.getPhoneNumber());
    cellPhoneTxt.setText(customer.getCellPhone());
    shopNameTxt.setText(customer.getShopName());
    nationalCodeTxt.setText(customer.getNationalCode());
    municipalityCodeTxt.setText(customer.getMunicipalityCode());
    postalCodeTxt.setText(customer.getPostalCode());

    if (Empty.isNotEmpty(customer.getStoreSurface()) && customer.getStoreSurface() != 0) {
      storeSurfaceTxt.setText(String.valueOf(customer.getStoreSurface()));
    }

    addressTxt.setText(customer.getAddress());
  }

  private void loadSpinnersData() {
    final List<LabelValue> provinceList = baseInfoService.getAllProvincesLabelValues();
    List<LabelValue> activityList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.ACTIVITY_TYPE.getId());
    List<LabelValue> ownershipList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.OWNERSHIP_TYPE.getId());
    List<LabelValue> customerClassList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.CUSTOMER_CLASS.getId());

    if (Empty.isNotEmpty(provinceList)) {
      provinceSp.setAdapter(new LabelValueArrayAdapter(context, provinceList));
      provinceSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          LabelValue selectedProvince = provinceList.get(position);
          if (Empty.isNotEmpty(selectedProvince)) {
            loadCitySpinnerData(selectedProvince.getValue());
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
      });

      if (Empty.isNotEmpty(customer.getProvinceBackendId())) {
        int position = 0;
        for (LabelValue labelValue : provinceList) {
          if (labelValue.getValue().equals(customer.getProvinceBackendId())) {
            provinceSp.setSelection(position);
            break;
          }
          position++;
        }
      }
    }
    if (Empty.isNotEmpty(activityList)) {
      activitySp.setAdapter(new LabelValueArrayAdapter(context, activityList));
      if (Empty.isNotEmpty(customer.getActivityBackendId())) {
        int position = 0;
        for (LabelValue labelValue : activityList) {
          if (labelValue.getValue().equals(customer.getActivityBackendId())) {
            activitySp.setSelection(position);
            break;
          }
          position++;
        }
      }
    }
    if (Empty.isNotEmpty(ownershipList)) {
      ownershipSp.setAdapter(new LabelValueArrayAdapter(context, ownershipList));
      if (Empty.isNotEmpty(customer.getStoreLocationTypeBackendId())) {
        int position = 0;
        for (LabelValue labelValue : ownershipList) {
          if (labelValue.getValue().equals(customer.getStoreLocationTypeBackendId())) {
            ownershipSp.setSelection(position);
            break;
          }
          position++;
        }
      }
    }

    if (Empty.isNotEmpty(classSp)) {
      classSp.setAdapter(new LabelValueArrayAdapter(context, customerClassList));
      if (Empty.isNotEmpty(customer.getClassBackendId())) {
        int position = 0;
        for (LabelValue labelValue : customerClassList) {
          if (labelValue.getValue().equals(customer.getClassBackendId())) {
            classSp.setSelection(position);
            break;
          }
          position++;
        }
      }
    }

    LabelValue provinceLabelValue = (LabelValue) provinceSp.getSelectedItem();
    loadCitySpinnerData(provinceLabelValue.getValue());

    provinceSp.setFocusableInTouchMode(true);
    provinceSp.setOnFocusChangeListener(this);
    citySp.setFocusableInTouchMode(true);
    citySp.setOnFocusChangeListener(this);
    activitySp.setFocusableInTouchMode(true);
    activitySp.setOnFocusChangeListener(this);
    classSp.setFocusableInTouchMode(true);
    classSp.setOnFocusChangeListener(this);
    ownershipSp.setFocusableInTouchMode(true);
    ownershipSp.setOnFocusChangeListener(this);

  }

  private void loadCitySpinnerData(Long provinceId) {
    List<LabelValue> cityLabelValues = baseInfoService.getAllCitiesLabelsValues(provinceId);
    if (Empty.isNotEmpty(cityLabelValues)) {
      citySp.setAdapter(new LabelValueArrayAdapter(context, cityLabelValues));
    }

    if (Empty.isNotEmpty(customer.getCityBackendId())) {
      int position = 0;
      for (LabelValue labelValue : cityLabelValues) {
        if (labelValue.getValue().equals(customer.getCityBackendId())) {
          citySp.setSelection(position);
          break;
        }
        position++;
      }
    }

  }

  private void getLocation() {
    showProgressDialog(getString(R.string.message_finding_location));

    try {
      locationService.findCurrentLocation(new FindLocationListener() {
        @Override
        public void foundLocation(Location location) {
          customer.setyLocation(location.getLatitude());
          customer.setxLocation(location.getLongitude());
          ToastUtil.toastMessage(getActivity(), R.string.message_found_location_successfully);
          dismissProgressDialog();
        }

        @Override
        public void timeOut() {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              ToastUtil.toastError(getActivity(), R.string.message_finding_location_timeout);
              dismissProgressDialog();
            }
          });
        }
      });
    } catch (BusinessException ex) {
      ToastUtil.toastError(getActivity(), ex);
      dismissProgressDialog();
    } catch (Exception e) {
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
      Log.e(TAG, e.getMessage(), e);
      dismissProgressDialog();
    }
  }

  private void save() {
    try {
      customer.setFullName(CharacterFixUtil.fixString(fullNameTxt.getText().toString()));
      customer.setPhoneNumber(NumberUtil.digitsToEnglish(phoneNumberTxt.getText().toString()));
      customer.setCellPhone(NumberUtil.digitsToEnglish(cellPhoneTxt.getText().toString()));
      customer.setAddress(CharacterFixUtil.fixString(addressTxt.getText().toString()));
      if (Empty.isNotEmpty(storeSurfaceTxt.getText()) && !storeSurfaceTxt.getText().toString()
          .equals("")) {
        customer.setStoreSurface(
            Integer.parseInt(NumberUtil.digitsToEnglish(storeSurfaceTxt.getText().toString())));
      }
      customer.setProvinceBackendId(provinceSp.getSelectedItemId());
      customer.setCityBackendId(citySp.getSelectedItemId());
      customer.setActivityBackendId(activitySp.getSelectedItemId());
      customer.setStoreLocationTypeBackendId(ownershipSp.getSelectedItemId());
      customer.setClassBackendId(classSp.getSelectedItemId());
      customer.setShopName(CharacterFixUtil.fixString(shopNameTxt.getText().toString()));
      customer.setNationalCode(NumberUtil.digitsToEnglish(nationalCodeTxt.getText().toString()));
      customer.setMunicipalityCode(
          NumberUtil.digitsToEnglish(municipalityCodeTxt.getText().toString()));
      customer.setPostalCode(NumberUtil.digitsToEnglish(postalCodeTxt.getText().toString()));
      customer.setApproved(false);

      if (validate()) {
        customerService.saveCustomer(customer);
        ToastUtil.toastSuccess(getActivity(), R.string.message_customer_save_successfully);
        mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_FRAGMENT_ID, false);
      }
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
    }
  }

  private boolean validate() {
    if (Empty.isEmpty(customer)) {
      return false;
    }
    if (Empty.isEmpty(customer.getFullName())) {
      ToastUtil.toastError(getActivity(), R.string.message_customer_name_is_required);
      fullNameTxt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getPhoneNumber())) {
      ToastUtil.toastError(getActivity(), R.string.message_phone_is_required);
      phoneNumberTxt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getCellPhone())) {
      ToastUtil.toastError(getActivity(), R.string.message_cell_phone_is_required);
      cellPhoneTxt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getShopName())) {
      ToastUtil.toastError(getActivity(), R.string.message_shop_name_is_required);
      shopNameTxt.requestFocus();
      return false;
    }

    String nationalCode = NumberUtil.digitsToEnglish(customer.getNationalCode());
    if (!Empty.isEmpty(nationalCode) && (!isValidNationalCode(nationalCode))) {
      ToastUtil.toastError(getActivity(), R.string.message_national_code_is_not_valid);
      nationalCodeTxt.requestFocus();
      return false;
    }

    String postalCode = customer.getPostalCode().trim();
    if (!Empty.isEmpty(postalCode) && postalCode.length() != 10) {
      ToastUtil.toastError(getActivity(), R.string.message_postal_code_is_not_valid);
      postalCodeTxt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getAddress())) {
      ToastUtil.toastError(getActivity(), R.string.message_address_is_required);
      addressTxt.requestFocus();
      return false;
    }

    return true;
  }

  private boolean isValidNationalCode(String nationalCode) {
    if (nationalCode.length() != 10) {
      return false;
    } else {
      //Check for equal numbers
      String[] allDigitEqual = {"0000000000", "1111111111", "2222222222", "3333333333",
          "4444444444", "5555555555", "6666666666", "7777777777", "8888888888", "9999999999"};
      if (Arrays.asList(allDigitEqual).contains(nationalCode)) {
        return false;
      } else {
        int sum = 0;
        int lenght = 10;
        for (int i = 0; i < lenght - 1; i++) {
          sum += Integer.parseInt(String.valueOf(nationalCode.charAt(i))) * (lenght - i);
        }

        int r = Integer.parseInt(String.valueOf(nationalCode.charAt(9)));

        int c = sum % 11;

        return (((c < 2) && (r == c)) || ((c >= 2) && ((11 - c) == r)));
      }

    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID;
  }

  @OnClick({R.id.getLocationBtn, R.id.saveBtn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.getLocationBtn:
        getLocation();
        break;
      case R.id.saveBtn:
        save();
        break;
    }
  }

  /**
   * Called when the focus state of a view has changed.
   *
   * @param v The view whose state has changed.
   * @param hasFocus The new focus state of v.
   */
  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    if (hasFocus) {
      v.performClick();
    }
  }
}
