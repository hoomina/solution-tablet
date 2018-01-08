package com.parsroyal.solutiontablet.data.model;


import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants.SendOrder;
import com.parsroyal.solutiontablet.constants.Constants.TransferOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arash
 */
public class DataTransferList {

  private int id;
  private int imageId;
  private String title;
  private String result;
  private int success;
  private int failure;
  private int status;

  private DataTransferList(int id, int imageId, String title) {
    this.id = id;
    this.imageId = imageId;
    this.title = title;
  }

  public static List<DataTransferList> dataTransferGetList(Context context) {
    List<DataTransferList> featureList = new ArrayList<>();
    featureList.add(
        new DataTransferList(TransferOrder.PROVINCE, R.drawable.ic_state_24_dp,
            context.getString(R.string.provinces)));
    featureList.add(
        new DataTransferList(TransferOrder.CITY, R.drawable.ic_city_24_dp,
            context.getString(R.string.cities)));
    featureList.add(
        new DataTransferList(TransferOrder.INFO, R.drawable.ic_info_24_dp,
            context.getString(R.string.basic)));
    featureList.add(new DataTransferList(TransferOrder.GOODS_GROUP, R.drawable.ic_category_24_dp,
        context.getString(R.string.goods_category)));
    featureList.add(new DataTransferList(TransferOrder.QUESTIONNAIRE, R.drawable.ic_list_24_dp,
        context.getString(R.string.questionnaires)));
    featureList.add(new DataTransferList(TransferOrder.GOODS, R.drawable.ic_product_info_24_dp,
        context.getString(R.string.goods)));
    featureList.add(new DataTransferList(TransferOrder.VISITLINE, R.drawable.ic_path_24_dp,
        context.getString(R.string.visit_lines)));
    featureList.add(new DataTransferList(TransferOrder.GOODS_IMAGES, R.drawable.ic_image_24_dp,
        context.getString(R.string.goods_images)));

    return featureList;
  }

  public static List<DataTransferList> dataTransferSendList(Context context) {
    List<DataTransferList> featureList = new ArrayList<>();
    featureList.add(new DataTransferList(SendOrder.NEW_CUSTOMERS, R.drawable.ic_customers_24_dp,
        context.getString(R.string.new_customers)));
    featureList.add(new DataTransferList(SendOrder.ADDRESS, R.drawable.ic_address_24_dp,
        context.getString(R.string.address)));
    featureList.add(new DataTransferList(SendOrder.POSITION, R.drawable.ic_location_blue_24_dp,
        context.getString(R.string.salesman_location)));
    featureList.add(new DataTransferList(SendOrder.QUESTIONNAIRE, R.drawable.ic_list_24_dp,
        context.getString(R.string.questionnaires)));
    featureList.add(new DataTransferList(SendOrder.PAYMENT, R.drawable.ic_currency_24_dp,
        context.getString(R.string.payment)));
    featureList.add(new DataTransferList(SendOrder.ORDER, R.drawable.ic_cart_24_dp,
        context.getString(R.string.order)));
    //FACTOR HOT SALE
//    featureList.add(new DataTransferList(SendOrder.ORDER, R.drawable.ic_cart_24_dp,
//        context.getString(R.string.order)));

    featureList.add(new DataTransferList(SendOrder.RETURN_ORDER, R.drawable.ic_return_24_dp,
        context.getString(R.string.return_order)));
    featureList.add(new DataTransferList(SendOrder.VISIT_DETAIL, R.drawable.ic_visit_24_dp,
        context.getString(R.string.visit_detail)));
    //////////
    featureList.add(new DataTransferList(SendOrder.CUSTOMER_PICS, R.drawable.ic_camera_24_dp,
        context.getString(R.string.image)));

    return featureList;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public int getImageId() {
    return imageId;
  }

  public void setImageId(int imageId) {
    this.imageId = imageId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getSuccess() {
    return success;
  }

  public void setSuccess(int success) {
    this.success = success;
  }

  public int getFailure() {
    return failure;
  }

  public void setFailure(int failure) {
    this.failure = failure;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}

