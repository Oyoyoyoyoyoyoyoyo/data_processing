package com.oyoyoyo.mindistance;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oyoyoyo.common.UtilTools;
import com.oyoyoyo.entity.LnBaseData;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓冲区内数据匹配类
 */
public class BufferCompute {
    private static final Logger logger = LoggerFactory.getLogger(MindistanceCompute.class);

    /**
     * 计算固定范围内属性，并匹配
     *
     * @param mainData
     * @param baseData
     * @param outputPath
     * @throws Exception
     */
    public static void computeBuffer(JSONArray mainData, JSONArray baseData, String outputPath) throws Exception {
        List<LnBaseData> lengthList = new ArrayList<LnBaseData>();
        String bufferStr = null;
        int mainTotal = mainData.size();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        double bufferLength = Double.parseDouble(UtilTools.readInputLine("请输入缓冲区范围（m）"));
        for (int i = 0; i < mainData.size(); i++) {
            logger.info("处理进度：" + ((float) i / mainTotal) * 100 + "%");
            JSONObject mainDataOroperties = (JSONObject) mainData.getJSONObject(i).get("properties");
            JSONObject mainDataGeometry = (JSONObject) mainData.getJSONObject(i).get("geometry");
            String wktPoint = "POINT("
                    + mainDataGeometry.getJSONArray("coordinates").get(0)
                    + " "
                    + mainDataGeometry.getJSONArray("coordinates").get(1)
                    + ")";
            Point mainPoint = (Point) reader.read(wktPoint);
            Polygon geoBuffer = (Polygon) mainPoint.buffer(UtilTools.mToDegrees(bufferLength));
            for (int j = 0; j < baseData.size(); j++) {
                JSONObject baseDataOroperties = (JSONObject) baseData.getJSONObject(j).get("properties");
                JSONObject baseDataGeometry = (JSONObject) baseData.getJSONObject(j).get("geometry");
                String tempPoint = "POINT("
                        + baseDataGeometry.getJSONArray("coordinates").get(0)
                        + " "
                        + baseDataGeometry.getJSONArray("coordinates").get(1)
                        + ")";
                Point basePoint = (Point) reader.read(tempPoint);
                boolean isContain = geoBuffer.contains(basePoint);
                if (isContain == true) {
                    bufferStr = bufferStr + "&" + baseDataOroperties.getString("textlabel");
                }
            }
            mainDataOroperties.put("bufferStr", bufferStr);
            bufferStr = "";
            mainData.getJSONObject(i).put("properties", mainDataOroperties);
        }
        UtilTools.outputGeoJSON(mainData, outputPath);
    }
}
