package com.oyoyoyo;

import com.alibaba.fastjson.JSONArray;
import com.oyoyoyo.common.UtilTools;
import com.oyoyoyo.compute.BufferCompute;
import com.oyoyoyo.compute.MindistanceCompute;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;
import java.io.File;

@SpringBootApplication
public class LnAttributeProcessingApplication {
    private static final Logger logger = LoggerFactory.getLogger(MindistanceCompute.class);

    public static void main(String[] args) throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(LnAttributeProcessingApplication.class);
        builder.headless(false).run(args);
        int type;
        long startTime = System.currentTimeMillis();
        String outputPath = null;
        type = Integer.parseInt(UtilTools.readInputLine("请输入数据处理类型：(1-领近分析；2-相交分析)"));
        if (type != 2 && type != 1) {
            logger.error("输入类型错误");
            return;
        }
        //读取文件
        JOptionPane.showMessageDialog(null, "请选择待处理数据(geojson)");
        File mainFile = JFileDataStoreChooser.showOpenFile("geojson", null);
        outputPath = mainFile.getAbsolutePath().replace(".geojson", "_result_.geojson");
        JSONArray mainData = UtilTools.readGeoJSON(mainFile);
        JOptionPane.showMessageDialog(null, "请选择基础数据(geojson)");
        File baseFile = JFileDataStoreChooser.showOpenFile("geojson", null);
        JSONArray baseData = UtilTools.readGeoJSON(baseFile);

        if (type == 1) {
            //领近分析
            MindistanceCompute.computeMinDistance(mainData, baseData, outputPath);
            long endTime = System.currentTimeMillis();
            logger.info("success");
            logger.info("当前程序耗时：" + (endTime - startTime) + "ms");
        } else if (type == 2) {
            //相交分析
            BufferCompute.computeBuffer(mainData, baseData, outputPath);
            UtilTools.closeScanner();
            long endTime = System.currentTimeMillis();
            logger.info("success");
            logger.info("当前程序耗时：" + (endTime - startTime) + "ms");
        }
    }


}
