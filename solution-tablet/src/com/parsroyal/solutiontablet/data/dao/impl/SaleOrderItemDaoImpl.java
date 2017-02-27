package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.parsroyal.solutiontablet.data.dao.SaleOrderItemDao;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.model.SaleOrderItemDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public class SaleOrderItemDaoImpl extends AbstractDao<SaleOrderItem, Long> implements SaleOrderItemDao
{

    private Context context;

    public SaleOrderItemDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(SaleOrderItem entity)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SaleOrderItem.COL_ID, entity.getId());
        contentValues.put(SaleOrderItem.COL_GOODS_BACKEND_ID, entity.getGoodsBackendId());
        contentValues.put(SaleOrderItem.COL_GOODS_COUNT, entity.getGoodsCount());
        contentValues.put(SaleOrderItem.COL_AMOUNT, entity.getAmount());
        contentValues.put(SaleOrderItem.COL_SALE_ORDER_ID, entity.getSaleOrderId());
        contentValues.put(SaleOrderItem.COL_SALE_ORDER_BACKEND_ID, entity.getSaleOrderBackendId());
        contentValues.put(SaleOrderItem.COL_SELECTED_UNIT, entity.getSelectedUnit());
        contentValues.put(SaleOrderItem.COL_BACKEND_ID, entity.getBackendId());
        contentValues.put(SaleOrderItem.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
        contentValues.put(SaleOrderItem.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
        contentValues.put(SaleOrderItem.COL_INVOICE_BACKEND_ID, entity.getInvoiceBackendId());
        return contentValues;
    }

    @Override
    protected String getTableName()
    {
        return SaleOrderItem.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return SaleOrderItem.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection = {
                SaleOrderItem.COL_ID,
                SaleOrderItem.COL_GOODS_BACKEND_ID,
                SaleOrderItem.COL_GOODS_COUNT,
                SaleOrderItem.COL_AMOUNT,
                SaleOrderItem.COL_SALE_ORDER_ID,
                SaleOrderItem.COL_SALE_ORDER_BACKEND_ID,
                SaleOrderItem.COL_SELECTED_UNIT,
                SaleOrderItem.COL_BACKEND_ID,
                SaleOrderItem.COL_CREATE_DATE_TIME,
                SaleOrderItem.COL_UPDATE_DATE_TIME,
                SaleOrderItem.COL_INVOICE_BACKEND_ID
        };
        return projection;
    }

    @Override
    protected SaleOrderItem createEntityFromCursor(Cursor cursor)
    {
        SaleOrderItem saleOrderItem = new SaleOrderItem();
        saleOrderItem.setId(cursor.getLong(0));
        saleOrderItem.setGoodsBackendId(cursor.getLong(1));
        saleOrderItem.setGoodsCount(cursor.getLong(2));
        saleOrderItem.setAmount(cursor.getLong(3));
        saleOrderItem.setSaleOrderId(cursor.getLong(4));
        saleOrderItem.setSaleOrderBackendId(cursor.getLong(5));
        saleOrderItem.setSelectedUnit(cursor.getLong(6));
        saleOrderItem.setBackendId(cursor.getLong(7));
        saleOrderItem.setCreateDateTime(cursor.getString(8));
        saleOrderItem.setUpdateDateTime(cursor.getString(9));
        saleOrderItem.setInvoiceBackendId(cursor.getLong(10));
        return saleOrderItem;
    }

    @Override
    public void deleteAllItemsBySaleOrderId(Long saleOrderId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String sql = "DELETE FROM " + SaleOrderItem.TABLE_NAME + " WHERE " + SaleOrderItem.COL_SALE_ORDER_ID + " = ?";
        String[] args = {String.valueOf(saleOrderId)};
        db.beginTransaction();
        db.rawQuery(sql, args);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public List<SaleOrderItemDto> getAllOrderItemsDtoByOrderId(Long orderId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = " " + SaleOrderItem.COL_SALE_ORDER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, selectionArgs, null, null, null);
        List<SaleOrderItemDto> items = new ArrayList<>();
        while (cursor.moveToNext())
        {
            items.add(createOrderItemDtoFromCursor(cursor));
        }
        cursor.close();
        return items;
    }

    @Override
    public SaleOrderItem getOrderItemByOrderIdAndGoodsId(Long orderId, Long goodsBackendId, long invoiceBackendId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = SaleOrderItem.COL_SALE_ORDER_ID + " = ? AND " +
                SaleOrderItem.COL_GOODS_BACKEND_ID + " = ? AND " +
                SaleOrderItem.COL_INVOICE_BACKEND_ID + " = ?";
        String[] args = {String.valueOf(orderId), String.valueOf(goodsBackendId),String.valueOf(invoiceBackendId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
        SaleOrderItem entity = null;
        if (cursor.moveToFirst())
        {
            entity = createEntityFromCursor(cursor);
        }
        cursor.close();
        return entity;
    }

    private SaleOrderItemDto createOrderItemDtoFromCursor(Cursor cursor)
    {
        SaleOrderItemDto saleOrderItem = new SaleOrderItemDto();
        saleOrderItem.setId(cursor.getLong(0));
        saleOrderItem.setGoodsBackendId(cursor.getLong(1));
        saleOrderItem.setGoodsCount(cursor.getLong(2));
        saleOrderItem.setAmount(cursor.getLong(3));
        saleOrderItem.setSaleOrderId(cursor.getLong(4));
        saleOrderItem.setSaleOrderBackendId(cursor.getLong(5));
        saleOrderItem.setSelectedUnit(cursor.getLong(6));
        saleOrderItem.setBackendId(cursor.getLong(7));
        saleOrderItem.setCreateDateTime(cursor.getString(8));
        saleOrderItem.setUpdateDateTime(cursor.getString(9));
        saleOrderItem.setInvoiceBackendId(cursor.getLong(10));
        return saleOrderItem;
    }
}
