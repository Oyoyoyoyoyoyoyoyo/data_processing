package com.oyoyoyo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date:2020/11/22
 * Decription:<基础数据实体类>
 *
 * @Author:oyoyoyoyoyoyo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LnBaseData implements Comparable<LnBaseData> {
    private double length;
    private String name;
    private double index;

    @Override
    public int compareTo(LnBaseData s) {
        double num = this.length - s.length;
        return (int) num;
    }
}
