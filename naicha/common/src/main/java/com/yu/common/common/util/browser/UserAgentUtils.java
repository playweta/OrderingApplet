package com.yu.common.common.util.browser;


import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;

import java.io.IOException;

/**
 * 解析游览器信息
 */
public class UserAgentUtils {

    private static UASparser uasParser = null;
    // 初始化uasParser对象
    static {
        try {
            uasParser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserAgentInfo parse(String userAgent){
        try {
            return uasParser.parse(userAgent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)
    {
        String str = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36";
        System.out.println(str);
        try {
            UserAgentInfo userAgentInfo = UserAgentUtils.uasParser.parse(str);
            System.out.println("操作系统名称："+userAgentInfo.getOsFamily());// Windows
            System.out.println("操作系统："+userAgentInfo.getOsName());//
            System.out.println("浏览器名称："+userAgentInfo.getUaFamily());// Chrome
            System.out.println("浏览器版本："+userAgentInfo.getBrowserVersionInfo());//
            System.out.println("设备类型："+userAgentInfo.getDeviceType());
            System.out.println("浏览器:"+userAgentInfo.getUaName());
            System.out.println("类型："+userAgentInfo.getType());
            System.out.println(userAgentInfo.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
