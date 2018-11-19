package cn.dface.app.message.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author akun
 * @create 2018-10-22 下午7:24
 **/
public class ImageUtils {


    public static BufferedImage generateImage(File file,BufferedImage qrcode,String avatar,String nickName){
        int width = 750;
        int height = 1334;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            Graphics2D g2 = (Graphics2D)bi.getGraphics();
            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, width, height);
            g2.setPaint(new Color(48, 123, 245));
            g2.setStroke(new BasicStroke(6.0f));
            BufferedImage image = ImageIO.read(file);
            image = ImageUtils.scaleByPercentage(image,750,1334);
            g2.drawImage(image,0,0,750,1334,null);
            g2.drawImage(qrcode,270,936,196,196,null);
            if(!StringUtils.isEmpty(avatar)){
                BufferedImage avatarImage = ImageIO.read(new URL(avatar));
                avatarImage = ImageUtils.scaleByPercentage(avatarImage,80,80);
                avatarImage = ImageUtils.convertCircular(avatarImage);
                g2.drawImage(avatarImage,30,45,80,80,null);
            }
            if(!StringUtils.isEmpty(nickName)){
                Font font = new Font("Serif",Font.BOLD,36);
                g2.setFont(font);
                g2.setPaint(Color.white);
                g2.drawString(nickName,148,94);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ImageUtils.compress(bi);
    }

    /**
     * 传入的图像必须是正方形的 才会 圆形  如果是长方形的比例则会变成椭圆的
     * @param bi1
     * @return
     * @throws IOException
     */
    public static BufferedImage convertCircular(BufferedImage bi1) throws IOException {
        //这种是黑色底的
        //BufferedImage bi2 = new BufferedImage(bi1.getWidth(),bi1.getHeight(),BufferedImage.TYPE_INT_RGB);

        //透明底的图片
        BufferedImage bi2 = new BufferedImage(bi1.getWidth(),bi1.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
        Ellipse2D.Double shape = new Ellipse2D.Double(0,0,bi1.getWidth(),bi1.getHeight());
        Graphics2D g2 = bi2.createGraphics();
        g2.setClip(shape);
        // 使用 setRenderingHint 设置抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(bi1,0,0,null);
        //设置颜色
        g2.setBackground(new Color(0,0,0));
        g2.dispose();
        return bi2;
    }

    /**
     * 缩小Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像
     * @param inputImage
     * @param newWidth：压缩后宽度
     * @param newHeight：压缩后高度
     * @throws IOException
     * return
     */
    public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight) throws Exception {
        //获取原始图像透明度类型
        int type = inputImage.getColorModel().getTransparency();
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        //开启抗锯齿
        RenderingHints renderingHints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        //使用高质量压缩
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BufferedImage img = new BufferedImage(newWidth, newHeight, type);
        Graphics2D graphics2d =img.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
        graphics2d.dispose();
        return img;
    }

    public static BufferedImage compress(BufferedImage bufferedImage){
        try {
            return Thumbnails.of(bufferedImage).scale(0.7f).outputQuality(1f).asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
            return bufferedImage;
        }
    }

    /**
     * 按照 宽高 比例压缩
     *
     * @param scale 压缩刻度
     * @return 压缩后图片数据
     * @throws IOException 压缩图片过程中出错
     */
    public static byte[] compress(byte[] srcImgData, double scale) {
        ByteArrayOutputStream bOut = null;
        byte[] destImgData = null;
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(srcImgData));
            int width = (int) (bi.getWidth() * scale); // 源图宽度
            int height = (int) (bi.getHeight() * scale); // 源图高度

            Image image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics g = tag.getGraphics();
            g.setColor(Color.RED);
            g.drawImage(image, 0, 0, null); // 绘制处理后的图
            g.dispose();

            bOut = new ByteArrayOutputStream();
            ImageIO.write(tag, "PNG", bOut);
            destImgData = bOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bOut != null){
                try {
                    bOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return destImgData;
    }

}
