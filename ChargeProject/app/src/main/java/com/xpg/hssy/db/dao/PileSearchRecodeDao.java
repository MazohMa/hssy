package com.xpg.hssy.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xpg.hssy.db.pojo.PileSearchRecode;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PILE_SEARCH_RECODE.
*/
public class PileSearchRecodeDao extends AbstractDao<PileSearchRecode, String> {

    public static final String TABLENAME = "PILE_SEARCH_RECODE";

    /**
     * Properties of entity PileSearchRecode.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property PileId = new Property(0, String.class, "pileId", true, "PILE_ID");
        public final static Property Longitude = new Property(1, Double.class, "longitude", false, "LONGITUDE");
        public final static Property Latitude = new Property(2, Double.class, "latitude", false, "LATITUDE");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Address = new Property(4, String.class, "address", false, "ADDRESS");
        public final static Property Operator = new Property(5, int.class, "operator", false, "OPERATOR");
        public final static Property GprsType = new Property(6, Integer.class, "gprsType", false, "GPRS_TYPE");
    };


    public PileSearchRecodeDao(DaoConfig config) {
        super(config);
    }
    
    public PileSearchRecodeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PILE_SEARCH_RECODE' (" + //
                "'PILE_ID' TEXT PRIMARY KEY NOT NULL ," + // 0: pileId
                "'LONGITUDE' REAL," + // 1: longitude
                "'LATITUDE' REAL," + // 2: latitude
                "'NAME' TEXT," + // 3: name
                "'ADDRESS' TEXT," + // 4: address
                "'OPERATOR' INTEGER NOT NULL ," + // 5: operator
                "'GPRS_TYPE' INTEGER);"); // 6: gprsType
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PILE_SEARCH_RECODE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PileSearchRecode entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getPileId());
 
        Double longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindDouble(2, longitude);
        }
 
        Double latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindDouble(3, latitude);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(5, address);
        }
        stmt.bindLong(6, entity.getOperator());
 
        Integer gprsType = entity.getGprsType();
        if (gprsType != null) {
            stmt.bindLong(7, gprsType);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PileSearchRecode readEntity(Cursor cursor, int offset) {
        PileSearchRecode entity = new PileSearchRecode( //
            cursor.getString(offset + 0), // pileId
            cursor.isNull(offset + 1) ? null : cursor.getDouble(offset + 1), // longitude
            cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2), // latitude
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // address
            cursor.getInt(offset + 5), // operator
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6) // gprsType
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PileSearchRecode entity, int offset) {
        entity.setPileId(cursor.getString(offset + 0));
        entity.setLongitude(cursor.isNull(offset + 1) ? null : cursor.getDouble(offset + 1));
        entity.setLatitude(cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAddress(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setOperator(cursor.getInt(offset + 5));
        entity.setGprsType(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(PileSearchRecode entity, long rowId) {
        return entity.getPileId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(PileSearchRecode entity) {
        if(entity != null) {
            return entity.getPileId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
