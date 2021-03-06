package com.parsroyal.solutiontablet.data.dao;

import com.google.android.gms.maps.model.LatLng;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.model.PositionDto;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 10/9/2016.
 */
public interface PositionDao extends BaseDao<Position, Long> {

  Position getPositionById(Long positionId);

  List<PositionDto> findPositionDtoByStatusId(Long statusId);

  List<Position> findPositionByDate(Date from, Date to);

  List<LatLng> findPositionLatLngByDate(Date from, Date to);

  List<LatLng> findPositionLatLng();

  Position getLastPosition();
}
