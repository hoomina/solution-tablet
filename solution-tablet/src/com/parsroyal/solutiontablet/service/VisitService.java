package com.parsroyal.solutiontablet.service;

import android.location.Location;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import com.parsroyal.solutiontablet.data.searchobject.VisitInformationDetailSO;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 2017-03-08
 */
public interface VisitService extends BaseService {

  List<VisitLine> getAllVisitLines();

  List<VisitLineListModel> getAllVisitLinesListModel();

  List<VisitLineListModel> getAllVisitLinesListModel(Date from, Date to);

  List<LabelValue> getAllVisitLinesLabelValue();

  VisitLineListModel getVisitLineListModelByBackendId(long visitlineBackendId);

  Long startVisiting(Long customerBackendId, int distance);

  Long startPhoneVisiting(Long customerBackendId);

  Long startVisitingNewCustomer(Long customerId);

  void finishVisiting(Long visitId, long endDistance);

  List<VisitInformation> getAllVisitInformationForSend();

  VisitInformation getVisitInformationById(Long visitId);

  VisitInformation getVisitInformationForNewCustomer(Long customerId);

  Long saveVisit(VisitInformation visit);

  void updateVisitLocation(Long visitInformationId, Location location);

  void updateVisitLocation(Long visitInformationId, Position position);

  void saveVisitDetail(VisitInformationDetail visitDetail);

  List<VisitInformationDetail> getAllVisitDetailById(Long visitId);

  void updateVisitDetailId(VisitInformationDetailType type, long id, long backendId);

  List<VisitInformationDetail> searchVisitDetail(Long visitId, VisitInformationDetailType type,
      Long typeId);

  List<VisitInformationDetail> searchVisitDetail(Long visitId, VisitInformationDetailType type);

  List<VisitInformationDetail> searchVisitDetail(VisitInformationDetailSO visitInformationDetailSo);

  List<VisitInformationDto> getAllVisitDetailForSend(Long visitId);

  Long startAnonymousVisit();

  void deleteVisitById(Long visitId);

}
