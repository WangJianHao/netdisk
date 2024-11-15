package com.sen.netdisk.dto;

import com.sen.netdisk.common.utils.RandomUtil;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 13:20
 */
@Data
public class ImageCode {

    public static final String IMAGE_CODE_KEY = "capture_code";

    public static final String IMAGE_CODE_DATE = "capture_code_create_time";

    public static final Long EXPIRE_TIME = 60 * 1000L;//超时时间，默认60s

    //图片的宽度
    private int width = 160;

    //图片的高度
    private int height = 40;

    //验证码字符个数
    private int codeCount = 4;

    //验证码干扰线数
    private int lineCount = 20;

    //验证码
    private String code = null;

    //验证码图片buffer
    private BufferedImage bufferedImage = null;

    //随机数生成
    private SecureRandom random = new SecureRandom();

    public ImageCode() {
        createImage();
    }

    public ImageCode(int width, int height) {
        this.width = width;
        this.height = height;
        createImage();
    }

    public ImageCode(int width, int height, int codeCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        createImage();
    }

    public ImageCode(int width, int height, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        createImage();
    }

    /**
     * 生成图片
     */
    public void createImage() {
        int fontWidth = width / codeCount - 5;//字体的宽度
        int fontHeight = height - 5;//字体的高度
        int codeY = height - 8;

        //图像buffer
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
//        Graphics2D g2D = bufferedImage.createGraphics();
        //设置背景色
        graphics.setColor(getRandColor(200, 250));
        graphics.fillRect(0, 0, width, height);
        //设置字体
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        graphics.setFont(font);

        //设置干扰线
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            graphics.setColor(getRandColor(1, 255));
            graphics.drawLine(xs, ys, xe, ye);
        }

        //添加噪点
        float yawpRate = 0.01f;
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            bufferedImage.setRGB(x, y, random.nextInt(255));
        }

        String ranStr = RandomUtil.getRandomStr(codeCount);
        this.code = ranStr;
        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(ranStr.charAt(i));
            graphics.setColor(getRandColor(1, 255));
            graphics.drawString(strRand, i * fontWidth + 10, codeY);
        }

    }

    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public void write(OutputStream outputStream) throws IOException {
        ImageIO.write(bufferedImage, "png", outputStream);
        outputStream.close();
    }
}
