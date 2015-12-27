package com.xpg.hssy.db;

import android.content.Context;

import com.easy.util.EmptyUtil;
import com.xpg.hssy.db.dao.AddressSearchRecodeDao;
import com.xpg.hssy.db.dao.ChargeOrderDao;
import com.xpg.hssy.db.dao.ChargeRecordCacheDao;
import com.xpg.hssy.db.dao.ChargeRecordDao;
import com.xpg.hssy.db.dao.DaoMaster;
import com.xpg.hssy.db.dao.DaoMaster.DevOpenHelper;
import com.xpg.hssy.db.dao.DaoSession;
import com.xpg.hssy.db.dao.DistrictDataDao;
import com.xpg.hssy.db.dao.KeyDao;
import com.xpg.hssy.db.dao.PileDao;
import com.xpg.hssy.db.dao.PileSearchRecodeDao;
import com.xpg.hssy.db.dao.ShareTimeDao;
import com.xpg.hssy.db.dao.UserDao;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.db.pojo.ChargeRecordCache;
import com.xpg.hssy.db.pojo.DistrictData;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.ShareTime;
import com.xpg.hssy.engine.LocationInfos;
import com.xpg.hssy.engine.LocationInfos.LocationInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年1月13日
 */

public class DbHelper {
    private static DbHelper instance;
    private static final String DB_NAME = "DataBase.db";
    private DaoSession daoSession;
    private UserDao userDao;
    private PileDao pileDao;
    private KeyDao keyDao;
    private ShareTimeDao shareTimeDao;
    private ChargeRecordDao chargeRecordDao;
    private ChargeRecordCacheDao chargeRecordCacheDao;
    private AddressSearchRecodeDao addressSearchRecodeDao;
    private PileSearchRecodeDao pileSearchRecodeDao;
    private ChargeOrderDao chargeOrderDao;
    private DistrictDataDao districtDataDao;

    public DbHelper(Context context) {
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME,
                null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
        pileDao = daoSession.getPileDao();
        keyDao = daoSession.getKeyDao();
        shareTimeDao = daoSession.getShareTimeDao();
        chargeRecordDao = daoSession.getChargeRecordDao();
        chargeRecordCacheDao = daoSession.getChargeRecordCacheDao();
        addressSearchRecodeDao = daoSession.getAddressSearchRecodeDao();
        pileSearchRecodeDao = daoSession.getPileSearchRecodeDao();
        chargeOrderDao = daoSession.getChargeOrderDao();
        districtDataDao = daoSession.getDistrictDataDao();
    }

    public synchronized static DbHelper getInstance(Context context) {

        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    public void deleteAll() {
        userDao.deleteAll();
        pileDao.deleteAll();
        keyDao.deleteAll();
        shareTimeDao.deleteAll();
        chargeRecordDao.deleteAll();
        chargeRecordCacheDao.deleteAll();
        addressSearchRecodeDao.deleteAll();
        districtDataDao.deleteAll();
    }

    public void initDistrictData() {
        Iterator<LocationInfo> citysItr = LocationInfos.getInstance()
                .getCities().values().iterator();
        while (citysItr.hasNext()) {
            DistrictData city = new DistrictData();
            city.setByLocationInfo(citysItr.next());
            districtDataDao.insert(city);
        }
    }

    public List<DistrictData> getDistrictDataListByArg(String arg) {
        StringBuilder argb = new StringBuilder();
        argb.append("%").append(arg).append("%");
        arg = argb.toString();
        List<DistrictData> districtData = districtDataDao
                .queryBuilder()
                .whereOr(DistrictDataDao.Properties.DistrictName.like(arg), DistrictDataDao.Properties.DistrictPinyin.like(arg), DistrictDataDao.Properties
                        .DistrictFirstPinyin.like(arg)).list();
        return districtData;
    }

    public List<DistrictData> getAllDistrictDataList() {
        List<DistrictData> districtData = districtDataDao.queryBuilder()
                .orderAsc(DistrictDataDao.Properties.DistrictFirstPinyin)
                .list();
        return districtData;
    }

    /**
     * user
     */
    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * pile
     */
    public PileDao getPileDao() {
        return pileDao;
    }

    public List<Pile> getPileByUserId(String UserId) {
        List<Key> keys = getKeyByUserId(UserId);
        List<String> pileIds = new ArrayList<String>();
        for (Key key : keys) {
            if ((key.getKeyType() == Key.TYPE_OWNER || key.getKeyType() == Key.TYPE_FAMILY)
                    && key.getPileId() != null) {
                pileIds.add(key.getPileId());
            }
        }
        return pileDao.queryBuilder()
                .where(PileDao.Properties.PileId.in(pileIds)).list();
    }

    public void insertPile(Pile pile) {
        pileDao.insertOrReplace(pile);
        shareTimeDao.queryBuilder()
                .where(ShareTimeDao.Properties.PileId.eq(pile.getPileId()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        List<ShareTime> times = pile.getPileShares();
        if (times != null) {
            for (ShareTime shareTime : times) {
                shareTime.setPileId(pile.getPileId());
            }
            shareTimeDao.insertOrReplaceInTx(times);
        }
    }

    public void insertInTxPile(List<Pile> piles) {
        pileDao.insertOrReplaceInTx(piles);
        for (Pile pile : piles) {
            shareTimeDao.queryBuilder()
                    .where(ShareTimeDao.Properties.PileId.eq(pile.getPileId()))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
            List<ShareTime> times = pile.getPileShares();
            if (times != null) {
                for (ShareTime shareTime : times) {
                    shareTime.setPileId(pile.getPileId());
                }
                shareTimeDao.insertOrReplaceInTx(times);
            }
        }
    }

    /**
     * key
     */
    public KeyDao getKeyDao() {
        return keyDao;
    }

    public List<Key> getFamilyKeyByPileId(String pileId) {
        return keyDao
                .queryBuilder()
                .where(KeyDao.Properties.KeyType.eq(Key.TYPE_FAMILY), KeyDao.Properties.PileId.eq(pileId)).list();
    }

    public List<Key> getKeyByUserId(String userId) {
        return keyDao.queryBuilder().where(KeyDao.Properties.UserId.eq(userId))
                .list();
    }

    /**
     * record
     */
    public ChargeRecordDao getChargeRecordDao() {
        return chargeRecordDao;
    }

    public List<ChargeRecord> getChargeRecordByPileId(String pileId) {
        return chargeRecordDao.queryBuilder()
                .where(ChargeRecordDao.Properties.PileId.eq(pileId)).list();
    }

    public ChargeRecordCacheDao getChargeRecordCacheDao() {
        return chargeRecordCacheDao;
    }

    public List<ChargeRecordCache> getChargeRecordCacheByPileId(String pileId) {
        return chargeRecordCacheDao.queryBuilder()
                .where(ChargeRecordCacheDao.Properties.PileId.eq(pileId))
                .list();
    }

    /**
     * order
     */
    public ChargeOrderDao getChargeOrderDao() {
        return chargeOrderDao;
    }

    // 传入多个orderid，过滤掉数据库中已经存在的，返回数据库中不存在的orderid
    public List<String> getUnexistOrderId(List<String> orderIds) {
        QueryBuilder<ChargeOrder> queryBuilder = chargeOrderDao.queryBuilder()
                .where(ChargeOrderDao.Properties.OrderId.in(orderIds));
        List<ChargeOrder> orders = null;
        if (queryBuilder != null) {
            orders = queryBuilder.list();
        }
        List<String> unexistIds = new ArrayList<String>();
        for (int i = 0; i < orderIds.size(); i++) {
            unexistIds.add(orderIds.get(i));
        }
        // Collections.copy(unexistIds, orderIds);
        if (EmptyUtil.isEmpty(orders)) {
            return unexistIds;
        }

        for (ChargeOrder order : orders) {
            try {
                unexistIds.remove(order.getOrderId());
            } catch (Exception e) {
            }
        }
        return unexistIds;
    }

    public AddressSearchRecodeDao getAddressSearchRecodeDao() {
        return addressSearchRecodeDao;
    }

    public PileSearchRecodeDao getPileSearchRecodeDao() {
        return pileSearchRecodeDao;
    }

    public DistrictDataDao getDistrictDataDao() {
        return districtDataDao;
    }
}
