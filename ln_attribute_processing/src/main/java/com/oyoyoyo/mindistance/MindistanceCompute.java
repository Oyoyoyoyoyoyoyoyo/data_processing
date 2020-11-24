package com.oyoyoyo.mindistance;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oyoyoyo.common.GeoUtils;
import com.oyoyoyo.common.UtilTools;
import com.oyoyoyo.entity.LnBaseData;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * 两属性间最短距离属性匹配
 */

public class MindistanceCompute {
    private static final Logger logger = LoggerFactory.getLogger(MindistanceCompute.class);

    /**
     * 计算最短距离
     *
     * @param mainData
     * @param baseData
     */
    public static void computeMinDistance(JSONArray mainData, JSONArray baseData, String outputPath) throws Exception {
        List<LnBaseData> lengthList = new ArrayList<LnBaseData>();
        double length;
        JSONArray features = new JSONArray();
        int mainTotal = mainData.size();
        for (int i = 0; i < mainData.size(); i++) {
            logger.info("处理进度：" + ((float) i / mainTotal) * 100 + "%");
            JSONObject mainDataOroperties = (JSONObject) mainData.getJSONObject(i).get("properties");
            for (int j = 0; j < baseData.size(); j++) {
                JSONObject baseDataOroperties = (JSONObject) baseData.getJSONObject(j).get("properties");
                GeoUtils geoUtils = new GeoUtils();
                length = geoUtils.DistanceOfTwoPoints(
                        Double.parseDouble(mainDataOroperties.getString("lon"))
                        , Double.parseDouble(mainDataOroperties.getString("lat"))
                        , Double.parseDouble(baseDataOroperties.getString("lon"))
                        , Double.parseDouble(baseDataOroperties.getString("lat"))
                        , GeoUtils.GaussSphere.WGS84);
                LnBaseData lnBaseData = new LnBaseData();
                lnBaseData.setLength(length);
                lnBaseData.setName(baseDataOroperties.get("textlabel").toString());
                lnBaseData.setIndex(j);
                lengthList.add(lnBaseData);
            }
            LnBaseData lnBaseDataMin = Collections.min(lengthList);
            lengthList.clear();
            mainDataOroperties.put("minDistance", lnBaseDataMin.getName());

            mainData.getJSONObject(i).put("properties", mainDataOroperties);
        }
        UtilTools.outputGeoJSON(mainData, outputPath);
    }


}
