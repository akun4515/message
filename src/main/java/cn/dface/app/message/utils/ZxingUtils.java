package cn.dface.app.message.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akun on 2017/8/24.
 */
public class ZxingUtils {

    private static final int BLACK = 0xFF000000;//用于设置图案的颜色
    private static final int WHITE = 0xFFFFFFFF; //用于背景色

    /**
     * 生成二维码<br>
     * 二维码的净宽度必然等于二维码的净高度<br>
     * 如果需要尺寸特别精准的二维码，可删除返回BufferedImage的白边并缩放图片为指定尺寸(资料很多且暂时没有场景，不再处理)
     *
     * @param content
     *            二维码内容
     * @param width
     *            整个二维码图片宽度(二维码净宽度+预留白边)
     * @param height
     *            整个二维码图片高度(二维码经高度+预留白边)
     * @param logo
     *            logo BufferedImage，为空则不添加logo
     * @param isColor
     *            是否彩色二维码(此工具类颜色固化，如要定制颜色可增加参数扩充工具类) 默认为true
     * @return
     */
    public static BufferedImage encodeQRcode(String content, int width,
                                             int height, BufferedImage logo, Boolean isColor,
                                             Integer logoWidth,Integer logoHeight) {
        try {
            // 生成二维码
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");// 编码
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 最高容错级别
            hints.put(EncodeHintType.MARGIN, 0);// 边框留白最小(无法精准控制)
            MultiFormatWriter mutiWriter = new MultiFormatWriter();
            BitMatrix matrix = mutiWriter.encode(content,
                    BarcodeFormat.QR_CODE, width, height, hints);
            // 根据左上角定位方块坐标，确定左上角定位方块范围
            int[] topLeftPoint = matrix.getTopLeftOnBit();
            int leftTopX = topLeftPoint[0];// 左上角方块左上角X坐标
            int leftTopY = topLeftPoint[1];// 左上角方块左上角Y坐标
            int rightBottomX = 0;// 左上角方块右下角X坐标
            int rightBottomY = 0;// 左上角方块右下角Y坐标
            for (int x = topLeftPoint[0]; x < matrix.getWidth(); x++) {
                if (!matrix.get(x, topLeftPoint[1])) {
                    rightBottomX = x;
                    break;
                }
            }
            for (int y = topLeftPoint[1]; y < matrix.getHeight(); y++) {
                if (!matrix.get(topLeftPoint[0], y)) {
                    rightBottomY = y;
                    break;
                }
            }
            // LOGO生成
            //int logoWidth = rightBottomX - leftTopX;// LOGO宽度
            //int logoHeight = rightBottomY - leftTopY;// LOGO高度
            int logoHalfWidth = logoWidth / 2;// LOGO宽度一半
            int logoFrameWidth = 1;// LOGO边框宽度
            BufferedImage logoImage = genLogo(logo, logoWidth, logoHeight,
                    true);
            int[][] logoPixels = new int[logoWidth][logoHeight];
            if (logoImage != null) {
                for (int i = 0; i < logoImage.getWidth(); i++) {
                    for (int j = 0; j < logoImage.getHeight(); j++) {
                        logoPixels[i][j] = logoImage.getRGB(i, j);
                    }
                }
            }
            // 二维矩阵转为一维像素数组
            int halfW = matrix.getWidth() / 2;
            int halfH = matrix.getHeight() / 2;
            int[] pixels = new int[width * height];
            for (int y = 0; y < matrix.getHeight(); y++) {
                for (int x = 0; x < matrix.getWidth(); x++) {
                    if (x > 0 && x < rightBottomX && y > 0 && y < rightBottomY) {// 左上角定位方块颜色,根据自己需要调整颜色范围和空白填充颜色
                        Color color = new Color(0, 0, 0);// 黑色
                        if (isColor == null || isColor) {// 彩色二维码
                            color = new Color(231, 144, 56);// 此处颜色固化，如果需要可扩展
                        }
                        int colorInt = color.getRGB();
                        pixels[y * width + x] = matrix.get(x, y) ? colorInt
                                : 16777215;
                    } else if (logoImage != null && x > halfW - logoHalfWidth
                            && x < halfW + logoHalfWidth
                            && y > halfH - logoHalfWidth
                            && y < halfH + logoHalfWidth) {// 添加LOGO(如果存在)
                        pixels[y * width + x] = logoPixels[x - halfW
                                + logoHalfWidth][y - halfH + logoHalfWidth];
                    } else if (logoImage != null
                            && ((x > halfW - logoHalfWidth - logoFrameWidth
                            && x < halfW - logoHalfWidth
                            + logoFrameWidth
                            && y > halfH - logoHalfWidth
                            - logoFrameWidth && y < halfH
                            + logoHalfWidth + logoFrameWidth)
                            || (x > halfW + logoHalfWidth
                            - logoFrameWidth
                            && x < halfW + logoHalfWidth
                            + logoFrameWidth
                            && y > halfH - logoHalfWidth
                            - logoFrameWidth && y < halfH
                            + logoHalfWidth + logoFrameWidth)
                            || (x > halfW - logoHalfWidth
                            - logoFrameWidth
                            && x < halfW + logoHalfWidth
                            + logoFrameWidth
                            && y > halfH - logoHalfWidth
                            - logoFrameWidth && y < halfH
                            - logoHalfWidth + logoFrameWidth) || (x > halfW
                            - logoHalfWidth - logoFrameWidth
                            && x < halfW + logoHalfWidth
                            + logoFrameWidth
                            && y > halfH + logoHalfWidth
                            - logoFrameWidth && y < halfH
                            + logoHalfWidth + logoFrameWidth))) {// 添加LOGO四周边框(如果存在)
                        Color color = new Color(0, 0, 0);// 黑色
                        if (isColor == null || isColor) {// 彩色二维码
                            // 此处固化为渐变颜色，如有需要可扩展
                            int R = (int) (50 - (50.0 - 13.0)
                                    / matrix.getHeight() * (y + 1));
                            int G = (int) (165 - (165.0 - 72.0)
                                    / matrix.getHeight() * (y + 1));
                            int B = (int) (162 - (162.0 - 107.0)
                                    / matrix.getHeight() * (y + 1));
                            color = new Color(R, G, B);
                        }
                        int colorInt = color.getRGB();
                        pixels[y * width + x] = colorInt;
                    } else {// 其他部分二维码颜色
                        Color color = new Color(0, 0, 0);// 黑色
                        if (isColor == null || isColor) {// 彩色二维码
                            // 此处固化为渐变颜色，如有需要可扩展
                            int R = (int) (50 - (50.0 - 13.0)
                                    / matrix.getHeight() * (y + 1));
                            int G = (int) (165 - (165.0 - 72.0)
                                    / matrix.getHeight() * (y + 1));
                            int B = (int) (162 - (162.0 - 107.0)
                                    / matrix.getHeight() * (y + 1));
                            color = new Color(R, G, B);
                        }
                        int colorInt = color.getRGB();
                        // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                        pixels[y * width + x] = matrix.get(x, y) ? colorInt
                                : 16777215;
                    }
                }
            }

            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            image.getRaster().setDataElements(0, 0, width, height, pixels);

            return image;
        } catch (WriterException e) {
            return null;
        }
    }

    /**
     * 解析条形码或二维码
     *
     * @param filePath
     *            图片路径
     * @return
     */
    public static Map<String, String> decode(String filePath) {
        try {
            if (filePath == null || filePath.equals("")) {
                return null;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            BufferedImage image = ImageIO.read(new File(filePath));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);// 对图像进行解码
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("encode", result.getBarcodeFormat().name());
            resultMap.put("text", result.getText());
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 把传入的原始LOGO图像按高度和宽度进行缩放，生成符合要求的图标
     *
     * @param srcImage
     *            logo BufferedImage
     * @param height
     *            目标高度
     * @param width
     *            目标宽度
     * @param hasFiller
     *            比例不对时是否需要补白：true为补白; false为不补白;
     */
    private static BufferedImage genLogo(BufferedImage srcImage, int height,
                                         int width, boolean hasFiller) {

        try {
            double ratio = 0.0; // 缩放比例
            Image destImage = srcImage.getScaledInstance(width, height,
                    BufferedImage.SCALE_SMOOTH);
            // 计算比例
            if ((srcImage.getHeight() > height)
                    || (srcImage.getWidth() > width)) {
                if (srcImage.getHeight() > srcImage.getWidth()) {
                    ratio = (new Integer(height)).doubleValue()
                            / srcImage.getHeight();
                } else {
                    ratio = (new Integer(width)).doubleValue()
                            / srcImage.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(
                        AffineTransform.getScaleInstance(ratio, ratio), null);
                destImage = op.filter(srcImage, null);
            }
            if (hasFiller) {// 补白
                BufferedImage image = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D graphic = image.createGraphics();
                graphic.setColor(Color.white);
                graphic.fillRect(0, 0, width, height);
                if (width == destImage.getWidth(null))
                    graphic.drawImage(destImage, 0,
                            (height - destImage.getHeight(null)) / 2,
                            destImage.getWidth(null),
                            destImage.getHeight(null), Color.white, null);
                else
                    graphic.drawImage(destImage,
                            (width - destImage.getWidth(null)) / 2, 0,
                            destImage.getWidth(null),
                            destImage.getHeight(null), Color.white, null);
                graphic.dispose();
                destImage = image;
            }
            return (BufferedImage) destImage;
        } catch (Exception e) {
            return null;
        }
    }

    public static BufferedImage generateQrcode(String content,int width,int height){
        String format="png";    //图片的格式

        /**
         * 定义二维码的参数
         */
        HashMap hints=new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");    //指定字符编码为“utf-8”
        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.M);  //指定二维码的纠错等级为中级
        hints.put(EncodeHintType.MARGIN, 2);    //设置图片的边距

        /**
         * 生成二维码
         */
        try {
            BitMatrix bitMatrix=new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,hints);
            return toBufferedImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y,  (matrix.get(x, y) ? BLACK : WHITE));
//              image.setRGB(x, y,  (matrix.get(x, y) ? Color.YELLOW.getRGB() : Color.CYAN.getRGB()));
            }
        }
        return image;
    }
}

