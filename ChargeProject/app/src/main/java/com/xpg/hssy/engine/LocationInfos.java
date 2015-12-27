package com.xpg.hssy.engine;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年1月19日
 */

public class LocationInfos {
    public static final String tag = LocationInfos.class.getSimpleName();

    private static String FILE_PROVINCES = "provinces.csv";
    private static String FILE_CITIES = "cities.csv";
    private static String FILE_DISTRICTS = "districts.csv";

    private volatile static LocationInfos instance;

    private LocationInfos() {
    }

    public static LocationInfos getInstance() {
        if (instance == null) {
            synchronized (LocationInfos.class) {
                if (instance == null) {
                    instance = new LocationInfos();
                }
            }
        }
        return instance;
    }

    private Map<String, LocationInfo> provinces = new Hashtable<>();
    private Map<String, LocationInfo> cities = new Hashtable<>();
    private Map<String, LocationInfo> districts = new Hashtable<>();

    public void init(Context context) {
        BufferedReader br = null;
        try {
            String line = null;
            // 省
            br = new BufferedReader(new InputStreamReader(context.getAssets()
                    .open(FILE_PROVINCES)));
            while ((line = br.readLine()) != null) {
                String[] infos = line.split(",");
                LocationInfo info = new LocationInfo();
                info.code = infos[0];
                info.name = infos[1];
                info.childs = new LinkedList<LocationInfo>();
                provinces.put(info.code, info);
            }
            br.close();

            // 市
            br = new BufferedReader(new InputStreamReader(context.getAssets()
                    .open(FILE_CITIES)));
            while ((line = br.readLine()) != null) {
                String[] infos = line.split(",");
                LocationInfo info = new LocationInfo();
                info.code = infos[0];
                info.name = infos[1];
                info.parent = provinces.get(infos[2]);
                if (info.parent != null)
                    info.parent.childs.add(info);
                info.childs = new LinkedList<LocationInfo>();
                cities.put(info.code, info);
            }
            br.close();

            // 区
            br = new BufferedReader(new InputStreamReader(context.getAssets()
                    .open(FILE_DISTRICTS)));
            while ((line = br.readLine()) != null) {
                String[] infos = line.split(",");
                LocationInfo info = new LocationInfo();
                info.code = infos[0];
                info.name = infos[1];
                info.parent = cities.get(infos[2]);
                if (info.parent != null)
                    info.parent.childs.add(info);
                districts.put(info.code, info);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LocationInfo getProvinceById(String id) {
        return provinces.get(id);
    }

    public LocationInfo getProvinceByName(String str) {
        for (LocationInfo province : provinces.values()) {
            if (province.name.startsWith(str)) {
                return province;
            }
        }
        return null;
    }

    public LocationInfo getCityById(String id) {
        return cities.get(id);
    }

    public LocationInfo getCityByName(String cityStr) {
        for (LocationInfo city : cities.values()) {
            if (city.name.startsWith(cityStr)) {
                return city;
            }
        }
        return null;
    }

    public LocationInfo getCityByName(String proStr, String cityStr) {
        LocationInfo province = getProvinceByName(proStr);
        if (province == null) {
            return null;
        }

        List<LocationInfo> cities = province.getChilds();
        for (LocationInfo city : cities) {
            if (city.name.startsWith(cityStr)) {
                return city;
            }
        }
        return null;
    }

    public LocationInfo getDistrictById(String id) {
        return districts.get(id);
    }

    public LocationInfo getDistrictByName(String proStr, String cityStr,
                                          String disStr) {
        LocationInfo city = getCityByName(proStr, cityStr);
        if (city == null) {
            return null;
        }

        List<LocationInfo> districts = city.getChilds();
        for (LocationInfo district : districts) {
            if (district.name.startsWith(disStr)) {
                return district;
            }
        }
        return null;
    }

    public List<String> getStrListProvinces() {
        List<String> strProvinces = new ArrayList<String>();
        for (LocationInfo province : provinces.values()) {
            strProvinces.add(province.getName());
        }
        return strProvinces;
    }

    public List<String> getStrListCitis(String proStr) {
        List<String> strCitis = new ArrayList<String>();
        for (LocationInfo city : getProvinceByName(proStr).getChilds()) {
            strCitis.add(city.getName());
        }
        return strCitis;
    }

    public List<String> getStrListDistricts(String proStr, String cityStr) {
        List<String> strDistricts = new ArrayList<String>();
        LocationInfo li = getCityByName(proStr, cityStr);
        for (LocationInfo district : getCityByName(proStr, cityStr).getChilds()) {
            strDistricts.add(district.getName());
        }
        return strDistricts;
    }

    public static class LocationInfo {

        private String code;
        private String name;
        private LocationInfo parent;
        private List<LocationInfo> childs;

        public LocationInfo() {

        }

        public LocationInfo(String code, String name, LocationInfo parent,
                            List<LocationInfo> childs) {
            super();
            this.code = code;
            this.name = name;
            this.parent = parent;
            this.childs = childs;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocationInfo getParent() {
            return parent;
        }

        public void setParent(LocationInfo parent) {
            this.parent = parent;
        }

        public List<LocationInfo> getChilds() {
            return childs;
        }

        public void setChilds(List<LocationInfo> childs) {
            this.childs = childs;
        }

    }

    public Map<String, LocationInfo> getProvinces() {
        return provinces;
    }

    public Map<String, LocationInfo> getCities() {
        return cities;
    }

    public Map<String, LocationInfo> getDistricts() {
        return districts;
    }
}
