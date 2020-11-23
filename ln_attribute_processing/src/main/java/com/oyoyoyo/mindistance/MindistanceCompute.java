package com.oyoyoyo.mindistance;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oyoyoyo.common.GeoUtils;
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

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        String outputPath = null;
        JOptionPane.showMessageDialog(null, "请选择需要处理的geojson文件");
        //读取geojson文件
        File mainFile = JFileDataStoreChooser.showOpenFile("geojson", null);
        // 输出文件为同路径下同文件名
        outputPath = mainFile.getAbsolutePath().replace(".geojson", "_result_.geojson");
        JSONArray mainData = readGeoJSON(mainFile);
        JOptionPane.showMessageDialog(null, "请选择全量基础数据的geojson文件");
        File baseFile = JFileDataStoreChooser.showOpenFile("geojson", null);
        JSONArray baseData = readGeoJSON(baseFile);
        computeMinDistance(mainData, baseData, outputPath);
        long endTime = System.currentTimeMillis();
        logger.info("success");
        logger.info("当前程序耗时：" + (endTime - startTime) + "ms");
    }

    /**
     * 读取GeoJSON数据
     */
    public static JSONArray readGeoJSON(File file) {
        Map map = new HashMap();
        GeometryJSON gjson = new GeometryJSON();
        try {
            //读取文件存入sb
            Reader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            sb.toString();
            //将geojson数据转json对象
            JSONObject json = JSONObject.parseObject(sb.toString());
            JSONArray features = (JSONArray) json.get("features");
            return features;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算最短距离
     *
     * @param mainData
     * @param baseData
     */
    public static void computeMinDistance(JSONArray mainData, JSONArray baseData, String outputPath) throws Exception {
        //long startTime = System.currentTimeMillis();
        List<LnBaseData> lengthList = new ArrayList<LnBaseData>();
        double length;
        JSONArray features = new JSONArray();
        int mainTotal=mainData.size();
        for (int i = 0; i < mainData.size(); i++) {
            //logger.info("处理进度："+5/mainTotal+"%");

            JSONObject mainDataOroperties = (JSONObject) mainData.getJSONObject(i).get("properties");

            //Object feature = mainData.getJSONObject(i).put("minDistance", "test");
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
        outputGeoJSON(mainData, outputPath);
    }

    /**
     * 导出GeoJSON数据
     *
     * @param features
     */
    public static void outputGeoJSON(JSONArray features, String outputPath) throws Exception {
        StringBuffer geoJSONSb = new StringBuffer();
        geoJSONSb.append("{\"type\": \"FeatureCollection\",\"features\": ");
        geoJSONSb.append(Arrays.toString(features.toArray()));
        geoJSONSb.append("}");
        File outputfile = new File(outputPath);
        FileOutputStream fileOutputStream = new FileOutputStream(outputfile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
        outputStreamWriter.write(String.valueOf(geoJSONSb));
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }
}
