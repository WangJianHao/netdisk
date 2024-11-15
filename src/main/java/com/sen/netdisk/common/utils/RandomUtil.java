package com.sen.netdisk.common.utils;

import java.security.SecureRandom;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 19:27
 */
public class RandomUtil {

    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成验证码
     *
     * @param codeCount 验证码的数量
     * @return 验证码
     */
    public static String getRandomStr(Integer codeCount) {
        String str = "ABCDEFGHIGHLMNOPQRSTUVWXYZabcdefghighlmnopqrstuvwxyz0123456789";
        StringBuilder randomStr = new StringBuilder();
        int len = str.length() - 1;
        for (int i = 0; i < codeCount; i++) {
            int index = randomInt(len);
            randomStr.append(str.charAt(index));
        }
        return randomStr.toString();
    }

    /**
     * 获取 0 - maxNumber 之间的随机数 [0, maxNumber)
     *
     * @param maxNumber maxNumber
     * @return int
     */
    public static int randomInt(int maxNumber) {
        return random.nextInt(maxNumber);
    }

}
