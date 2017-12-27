package com.wutong.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OrderUtil {


    public static void main(String[] args) {
        getBidOrderId(null, null);
    }

    public static String getBidOrderId(Integer userId , Integer tenderId){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//
        Date date = new Date();
        String format = df.format(date);
//        System.out.println(format);
//        String uuid = UUID.randomUUID().toString().split("-")[0];
        String s = format + "z" + userId + "a" + tenderId;
        System.out.println(s);
        return s;
    }
}
