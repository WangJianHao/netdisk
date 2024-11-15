package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 17:45
 */
public class Constant {

    //-------------------------------文件存储路径及业务默认------------------------------//
    public static final String FILE_FOLDER_FILE_PATH = "/file/";
    public static final String FILE_FOLDER_TEMP_FILE_PATH = "/temp/";
    public static final String FILE_FOLDER_HEAD_ICON_PATH = "/headicon/";
    public static final String HEAD_ICON_SUFFIX = ".jpg";
    public static final String HEAD_ICON_DEFAULT = "default_avatar.jpg";
    public static final String TS_NAME = "index.ts";
    public static final String M3U8_NAME = "index.m3u8";
    public static final String PNG_SUFFIX = ".png";

    //-------------------------------session key------------------------------//
    public static final String SHARE_CODE_KEY_PREFIX = "share-id:";

    //-------------------------------Redis Key------------------------------//
    /**
     * 邮箱验证码redis前缀
     */
    public static final String AUTH_CODE_KEY_PREFIX = "netdisk:user:mail-code:";

    public static final String SYS_SETTING_KEY = "netdisk:sys:setting:";
    public static final String USER_SPACE_KEY = "netdisk:user:space:";
    public static final String TEMP_FILE_KEY = "netdisk:user:file:temp:";
    public static final String DOWNLOAD_CODE_KEY = "netdisk:user:file:download:";


    //-------------------------------时间------------------------------//

    /**
     * 10min
     */
    public static final Long TEN_MINUTE = 60L * 10;


    public static final Long ONE_HOUR = 60L * 60;


    //-------------------------------数字常量------------------------------//
    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;
    public static final Integer FOUR = 4;
    public static final String ZERO_STR = "0";


    //-------------------------------单位------------------------------//
    public static final Long MB = 1024 * 1024L;


}
