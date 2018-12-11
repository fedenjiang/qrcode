package com.qrcode;

import com.qrcode.utils.QRCodeUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018-11-30 0030.
 */
@Controller
public class QrcodeController {

    @RequestMapping("/qrcode")
    public String qrcode(){
        return "qrcode";
    }

    @RequestMapping(value = "/getqrcode",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public String getqrcode(String qrtext,String qrcodename,boolean addLogo,String ecl,String shape,String color) throws ServletException, IOException {
        System.out.println(qrtext);
        // logo图片
        String imagePath = "logo.png";
        // 生成二维码路径
        String destPath = "C:\\Users\\Administrator\\Desktop\\qrcode";
        byte[] out = null;
        try {
            if(addLogo){
                out = QRCodeUtil.encode(qrtext, imagePath,destPath,qrcodename, false,ecl,shape,color);
            }else{
                out = QRCodeUtil.encode(qrtext,destPath,qrcodename, false,ecl,shape,color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(out));
    }

    @RequestMapping(value = "/analyze")
    @ResponseBody
    public String analyze() throws ServletException, IOException {
        StringBuffer sb = new StringBuffer();
        // 二维码图片所在文件夹路径
        String destPath = "C:\\Users\\Administrator\\Desktop\\qrcode";
        //得到所有待解析的二维码图片
        List<String> filepaths = QRCodeUtil.traverseFolder(destPath);
        filepaths.forEach(f -> {
            try {
                String result = QRCodeUtil.decode(f);
                sb.append("<p style='border: 2px solid red;word-break: break-all; word-wrap:break-word;'>二维码图片："
                        + f.split("\\\\")[f.split("\\\\").length - 1] + "</p>")
                        .append("<p style='word-break: break-all; word-wrap:break-word;'>解析结果：" + result.replaceAll("\n", "<br/>") + "</p>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return sb.toString();
    }

}
