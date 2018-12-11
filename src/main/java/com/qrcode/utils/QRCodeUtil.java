package com.qrcode.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

/**
 * 二维码工具类
 */
public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "PNG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    /**
     * 生成二维码的方法
     *
     * @param content      目标URL
     * @param imgPath      LOGO图片地址
     * @param needCompress 是否压缩LOGO
     * @param ecl 纠错等级
     * @param shape 形状
     * @param color 黑色/彩色
     * @return 二维码图片
     */
    /*private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress) throws Exception {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Random random=new Random();
        Color color= new Color(random.nextInt(255),random.nextInt(100),random.nextInt(100),255);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int trueRgb = 0xFF000000;
                if (x < width / 2 && y < height / 2) {
                    trueRgb = 0xFF0094FF;// 蓝色
                } else if (x < width / 2 && y > height / 2) {
                    trueRgb = Color.red.getRGB();// 红色
                } else if (x > width / 2 && y > height / 2) {
                    trueRgb = 0xFF5ACF00;// 绿色
                } else {
                    trueRgb = 0xFF000000;// 黑色
                }
                int[] leftTopXY = leftTopXY(bitMatrix);
                if(x <= leftTopXY[0]+leftTopXY[1]*5 && x >= leftTopXY[0]+leftTopXY[1]*2
                        && y >= leftTopXY[0]+leftTopXY[1]*2 && y <= leftTopXY[0]+leftTopXY[1]*5){
                    trueRgb = color.getRGB();
                }
            image.setRGB(x, y, bitMatrix.get(x, y) ? trueRgb
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeUtil.insertImage(image, imgPath, needCompress);
        return image;
    }*/
    private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress,String ecl,String shape,String color) throws Exception {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ecl);//ErrorCorrectionLevel.H纠错等级
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Random random=new Random();
        Color multicolor= new Color(random.nextInt(255),random.nextInt(100),random.nextInt(100),255);
        Graphics2D g2 = image.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        // 直接背景铺满整个图
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        DrawStyle drawStyle = DrawStyle.getDrawStyle(shape);
        int[] leftTopXY = leftTopXY(bitMatrix);
        for (int x = leftTopXY[0]; x < width; x=x+leftTopXY[1]) {
            for (int y = leftTopXY[0]; y < height; y=y+leftTopXY[1]) {
                int trueRgb = 0xFF000000;
                if(!StringUtils.isEmpty(color) && color.equals("colour")){//黑色或者彩色
                    if (x < width / 2 && y < height / 2) {
                        trueRgb = 0xFF0094FF;// 蓝色
                    } else if (x < width / 2 && y > height / 2) {
                        trueRgb = Color.red.getRGB();// 红色
                    } else if (x > width / 2 && y > height / 2) {
                        trueRgb = 0xFF5ACF00;// 绿色
                    } else {
                        trueRgb = 0xFF000000;// 黑色
                    }
                    if(x <= leftTopXY[0]+leftTopXY[1]*5 && x >= leftTopXY[0]+leftTopXY[1]*2
                            && y >= leftTopXY[0]+leftTopXY[1]*2 && y <= leftTopXY[0]+leftTopXY[1]*5){
                        trueRgb = multicolor.getRGB();
                    }
                }
                g2.setColor(new Color(bitMatrix.get(x, y) ? trueRgb
                        : 0xFFFFFFFF));
                drawStyle.draw(g2,x,y,leftTopXY[1],leftTopXY[1],image);

            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeUtil.insertImage(image, imgPath, needCompress);
        return image;
    }

    /**
     * 寻找左上角位置探测图形
     */
    private static int[] leftTopXY(BitMatrix bitMatrix){
        int firstTrue = -1;
        int singleBitWidth = -1;
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if(x==y){
                    if(bitMatrix.get(x, y) && firstTrue == -1){
                        firstTrue = x;
                    }else if(!bitMatrix.get(x, y) && firstTrue != -1){
                        singleBitWidth = x - firstTrue;
                        return new int[]{firstTrue,singleBitWidth};
                    }
                }
            }
        }
        return null;
    }

    /**
     * 插入LOGO
     *
     * @param source       二维码图片
     * @param imgPath      LOGO图片地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath,
                                    boolean needCompress) throws Exception {
//        File file = new File(imgPath);
        File file = ResourceUtils.getFile("classpath:static/images/"+imgPath);
        if (!file.exists()) {
            System.err.println("" + imgPath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, height, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }



    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param destPath     存放目录
     * @param needCompress 是否压缩LOGO
     * @throws Exception
     */
    public static byte[] encode(String content, String imgPath, String destPath,String filename,
                              boolean needCompress,String ecl,String shape,String color) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath,
                needCompress,ecl,shape,color);
        mkdirs(destPath);
        String file = filename+ ".png";//new Random().nextInt(99999999) + ".png";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT_NAME, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 检测路径是否存在，不存在则创建多级目录
     * @param destPath 存放目录
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码
     *
     * @param content      内容
     * @param destPath     存储地址
     * @param needCompress 是否压缩LOGO
     * @throws Exception
     */
    public static byte[] encode(String content, String destPath,String filename,
                              boolean needCompress,String ecl,String shape,String color) throws Exception {
        return QRCodeUtil.encode(content, null, destPath,filename, needCompress,ecl,shape,color);
    }

    /**
     * 解析二维码
     *
     * @param file 二维码图片
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
                image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }

    /**
     * 解析二维码
     *
     * @param path 二维码图片地址
     * @return 不是二维码的内容返回null, 是二维码直接返回识别的结果
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return QRCodeUtil.decode(new File(path));
    }

    public static java.util.List<String> traverseFolder(String path) {
        java.util.List<String> filepaths = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return null;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolder(file2.getAbsolutePath());
                    } else {
                        if(file2.getName().endsWith("png") || file2.getName().endsWith("jpg")){
                            filepaths.add(file2.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            System.out.println("文件夹不存在!");
        }
        return filepaths;
    }

}
