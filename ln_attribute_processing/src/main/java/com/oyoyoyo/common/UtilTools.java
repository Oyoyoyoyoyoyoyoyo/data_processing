package com.oyoyoyo.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.geotools.data.DataUtilities;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.io.*;
import java.util.*;

/**
 * 常用工具方法类
 */
public class UtilTools {
    public static final double DISTANCE = 0.000008983153;
    public static Scanner scan = null;

    /**
     * 获取SimpleFeatureType(读取geojson/wkt数据用)
     *
     * @param file
     * @param attrs0
     * @param strType geom数据类型
     * @return
     * @throws Exception
     */
    public static SimpleFeatureType getSimpleTypeByAttr0(File file, JSONObject attrs0, String strType) throws Exception {

        String outPutFileName = file.getName();
        //数据集合类型
        String geomType = "the_geom:" + strType + ":srid=4326,";
        //属性字段
        String geomAttrs = "";
        for (String attr : attrs0.keySet()) {
            geomAttrs = attr + ":String," + geomAttrs;
        }
        geomAttrs = geomAttrs.substring(0, geomAttrs.length() - 1);
        SimpleFeatureType TYPE =
                DataUtilities.createType(
                        outPutFileName,
                        //输出文件名称
                        geomType + geomAttrs
                );
        return TYPE;
    }

    /**
     * 获取SimpleFeatureType(读取shp数据用)
     *
     * @param file
     * @param attrs
     * @param strType geom数据类型
     * @return
     * @throws Exception
     */
    public static SimpleFeatureType getSimpleTypeByAttrs(File file, List<AttributeDescriptor> attrs, String strType) throws Exception {

        String outPutFileName = file.getName();
        //数据集合类型
        String geomType = "the_geom:" + strType + ":srid=4326,";
        //属性字段
        String geomAttrs = "";
        for (int i = 0; i < attrs.size(); i++) {
            AttributeDescriptor attr = attrs.get(i);
            String fieldName = attr.getName().toString();
            if (fieldName == "the_geom") {
                continue;
            }
            geomAttrs = fieldName + ":String," + geomAttrs;
        }
        geomAttrs = geomAttrs.substring(0, geomAttrs.length() - 1);
        SimpleFeatureType TYPE =
                DataUtilities.createType(
                        outPutFileName,
                        //输出文件名称
                        geomType + geomAttrs
                );
        return TYPE;
    }

    /**
     * 距离(m)到度的转换
     * 转换算法：degree=length/(2*Math.PI*6371004)*360;
     *
     * @param length 缓冲区距离 单位m
     * @return
     */
    public static double mToDegrees(double length) {
        //100米=0.0008983153 Degrees
        //degree = meter / (2 * Math.PI * 6371004) * 360
        double degree = length / (2 * Math.PI * 6371004) * 360;
        return degree;
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
     * 获取控制台输入内容
     *
     * @return
     */
    public static String readInputLine(String msg) throws Exception {
        String str = "";
        scan = new Scanner(System.in);
        System.out.println(msg);
        // 判断是否还有输入
        str = scan.nextLine();
        System.out.println(str);
        return str;
    }

    /**
     * 关闭控制台输入
     */
    public static void closeScanner() {
        scan.close();
    }
}
