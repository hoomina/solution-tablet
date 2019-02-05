package com.parsroyal.solutiontablet.util;

import android.widget.Toast;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class BarcodeUtil {

  public static Map<String, String> extractCheckQR(String result) {
    if (Empty.isEmpty(result)) {
      return null;
    }
    String[] split = result.split("\n");

    if (split.length != 4) {
      return null;
    }
    Map<String, String> data = new HashMap<>();
    data.put(Constants.NATIONAL_CODE, split[0]);
    Timber.i("Check validation of national code %s", ValidationUtil.isValidNationalCode(split[0]));

    data.put(Constants.SHABA, split[1]);
    data.put(Constants.CHECK_SERIAL, split[3]);

    String[] bankDetails = split[2].split("_");//055_173
    data.put(Constants.BANK_CODE, bankDetails[0]);
    data.put(Constants.BANK_BRANCH_CODE, bankDetails[1]);

    BaseInfoService service = new BaseInfoServiceImpl(SolutionTabletApplication.getInstance());
    List<BaseInfo> bank = service
        .retrieveByTypeAndCode(BaseInfoTypes.BANK_NAME_TYPE.getId(), bankDetails[0]);
    if (bank.size() > 0) {
      Toast.makeText(SolutionTabletApplication.getInstance(), "BAnk:" + bank.get(0).getTitle(),
          Toast.LENGTH_SHORT).show();
    }
    return data;
  }
}