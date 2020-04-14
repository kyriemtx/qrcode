package com.kyrie.qrcode.utis;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

/**
 * @ClassName QRCodeUtils
 * @Description
 * @Author tengxiao.ma
 * @Date 2020/4/14 19:13
 **/
@Slf4j
public class QRCodeUtils {

    private static final String CHARSET = "UTF-8";
    private static final int CODE_WIDTH = 360;
    private static final int CODE_HEIGHT = 360;
    //前景色  黑色
    private static final int FRONT_COLOR = 0x000000;
    //背景色 白色
    private static final int BACKGROUND_COLOR = 0xFFFFFF;

    /**
     * 二维码生成方法
     * @param codeContent  二维码内容
     * @param imgSaveDir   本地保存地址
     * @param fileNme      生成二维码的文件名
     */
    public static void createQRCode(String codeContent, File imgSaveDir, String fileNme){
        try{
            if(StringUtils.isBlank(codeContent)){
                log.info("二维码文本内容为空，不可生成二维码，请重试");
                return;
            }
            if(imgSaveDir == null || imgSaveDir.isFile()){
                imgSaveDir = FileSystemView.getFileSystemView().getHomeDirectory();
                log.info("二维码存放地址默认存放路径在桌面");
            }
            if(!imgSaveDir.exists()){
                imgSaveDir.mkdirs();
                log.info("创建二维码存放的目录");
            }
            if(StringUtils.isBlank(fileNme)){
                fileNme = System.currentTimeMillis() + ".jpg";
                log.info("未传入二维码文件名，随机生成一个二维码文件名");
            }
            codeContent = codeContent.trim();

            /**
             * com.google.zxing.EncodeHintType：编码提示类型,枚举类型
             * EncodeHintType.CHARACTER_SET：设置字符编码类型
             * EncodeHintType.ERROR_CORRECTION：设置误差校正
             * 不设置时，默认为 L 等级，等级不一样，生成的图案不同，但扫描的结果是一样的
             * EncodeHintType.MARGIN：设置二维码边距，单位像素，值越小，二维码距离四周越近
             */
            Map<EncodeHintType, Object> hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            /**
             * codeContent  二维码内容 如果是一个网页地址，如 https://www.kyriemtx.com/ 则 微信扫一扫会直接进入此地址
             * BarcodeFormat.QR_CODE  format：编码类型，如 条形码，二维码 等
             * width： 二维码的宽度
             * height：二维码的高度
             * hints： 二维码内容的编码类型
             */
            BitMatrix bitMatrix = multiFormatWriter.encode(codeContent, BarcodeFormat.QR_CODE,CODE_WIDTH,CODE_HEIGHT,hints);
            BufferedImage bufferedImage = new BufferedImage(CODE_WIDTH,CODE_HEIGHT,BufferedImage.TYPE_INT_RGB);
            for(int x = 0;x<CODE_WIDTH;x++){
                for(int y = 0;y<CODE_HEIGHT;y++){
                    bufferedImage.setRGB(x,y,bitMatrix.get(x,y) ? FRONT_COLOR:BACKGROUND_COLOR);
                }
            }
            File codeImgFile = new File(imgSaveDir,fileNme);
            ImageIO.write(bufferedImage,"jpg",codeImgFile);
            log.info("二维码生成成功，文件名：{},存放路径：{}",fileNme,codeImgFile.getAbsolutePath());
        }catch (Exception e){
            log.info("生成二维码失败");
            return;
        }
    }


    /**
     * 解析二维码
     * @param filePath  二维码文件存放路径
     * @return
     * @throws Exception
     */
    public static String parseQRCode(String filePath) throws Exception{
        if(StringUtils.isBlank(filePath)){
            log.info("二维码存放路径不能为空");
            return null;
        }
        File codeFile = new File(filePath);
        BufferedImage image;
        image = ImageIO.read(codeFile);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }



    public static void createCodeToOutputStream(String codeContext, OutputStream outputStream){
        try {
            if(StringUtils.isBlank(codeContext)){
                log.info("二维码内容为空，生成一个随默认的二维码");
                codeContext = "http://kyriemtx.com";
            }
            codeContext = codeContext.trim();
            Map<EncodeHintType, Object> hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(codeContext, BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_HEIGHT, hints);
            BufferedImage bufferedImage = new BufferedImage(CODE_WIDTH, CODE_HEIGHT, BufferedImage.TYPE_INT_BGR);
            for (int x = 0; x < CODE_WIDTH; x++) {
                for (int y = 0; y < CODE_HEIGHT; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? FRONT_COLOR : BACKGROUND_COLOR);
                }
            }
            ImageIO.write(bufferedImage, "png", outputStream);
        }catch (Exception e){
            log.info("生成二维码失败");
        }
    }

    public static void main(String[] args) throws Exception{
        String filePath = "C:\\Users\\tengxiao.ma\\Desktop\\1586863308057.jpg";
        String result = parseQRCode(filePath);
        System.err.println("解析结果："+result);
    }
}
