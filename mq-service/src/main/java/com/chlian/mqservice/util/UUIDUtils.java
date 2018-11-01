package com.chlian.mqservice.util;

import java.util.UUID;

/**
 *
 * @作者: liuj
 * @项目: mai8年10月19日下午7:05:14
 * @TODO： 生成随机字符串的工具类 uuid
 */
public class UUIDUtils {

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /*public static void main(String[] args) {
        System.out.println("格式前的UUID ： " + UUID.randomUUID().toString());
        System.out.println("格式化后的UUID ：" + getUUID());
    }*/
}
