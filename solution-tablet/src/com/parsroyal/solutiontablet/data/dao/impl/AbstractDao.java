package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import com.parsroyal.solutiontablet.data.entity.BaseEntity;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 6/4/2015.
 */
public abstract class AbstractDao<T extends BaseEntity, PK extends Long> {

  protected static SQLiteDatabase writableDb;

  public PK create(T entity) {
    try {
      CommerDatabaseHelper databaseHelper = new CommerDatabaseHelper(getContext());
      SQLiteDatabase db = databaseHelper.getWritableDatabase();
      db.beginTransaction();
      Long id = db.insert(getTableName(), null, getContentValues(entity));
      db.setTransactionSuccessful();
      db.endTransaction();
      return (PK) id;
    } catch (SQLiteDatabaseLockedException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void bulkInsert(List<T> list) {
    CommerDatabaseHelper databaseHelper = new CommerDatabaseHelper(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    try {
      for (T entity : list) {
        db.insert(getTableName(), null, getContentValues(entity));
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }

  public T retrieve(PK id) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(id)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    T entity = null;
    if (cursor.moveToFirst()) {
      entity = createEntityFromCursor(cursor);
    }
    cursor.close();
    return entity;
  }

  public void update(T entity) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause = getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(entity.getPrimaryKey())};
    db.update(getTableName(), getContentValues(entity), whereClause, args);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  public void delete(PK id) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause = getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(id)};
    db.delete(getTableName(), whereClause, args);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  public List<T> retrieveAll() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    Cursor cursor = db.query(getTableName(), getProjection(), null, null, null, null, null);
    List<T> entities = new ArrayList<T>();
    while (cursor.moveToNext()) {
      entities.add(createEntityFromCursor(cursor));
    }
    cursor.close();
    return entities;
  }

  public List<T> retrieveAll(String selection, String[] args, String groupBy, String having,
      String orderBy) {
    return retrieveAll(selection, args, groupBy, having, orderBy, null);
  }

  public List<T> retrieveAll(String selection, String[] args, String groupBy, String having,
      String orderBy, String limit) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    Cursor cursor = db
        .query(getTableName(), getProjection(), selection, args, groupBy, having, orderBy, limit);
    List<T> entities = new ArrayList<T>();
    while (cursor.moveToNext()) {
      entities.add(createEntityFromCursor(cursor));
    }
    cursor.close();
    return entities;
  }

  public void deleteAll() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    db.execSQL("DELETE FROM " + getTableName());
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  public int count() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    Cursor cursor = db.rawQuery("SELECT count(*) FROM " + getTableName(), null);
    int count = 0;
    if (cursor.moveToNext()) {
      count = cursor.getInt(0);
    }
    cursor.close();
    return count;
  }

  public int count(String column, String condition) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    String sql = String.format(Locale.US,
        "SELECT count(*) FROM %s WHERE %s = %s", getTableName(), column, condition);
    Cursor cursor = db.rawQuery(sql, null);
    int count = 0;
    if (cursor.moveToNext()) {
      count = cursor.getInt(0);
    }
    cursor.close();
    return count;
  }

  public void deleteAll(String column, String condition) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    db.execSQL(String.format("DELETE FROM %s WHERE %s = %s", getTableName(), column, condition));
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  public void delete(String selection, String[] args) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    db.delete(getTableName(), selection, args);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  protected abstract Context getContext();

  protected abstract ContentValues getContentValues(T entity);

  protected abstract String getTableName();

  protected abstract String getPrimaryKeyColumnName();

  protected abstract String[] getProjection();

  protected abstract T createEntityFromCursor(Cursor cursor);
}
