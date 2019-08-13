package com.can.store.shopping.commons;

import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 验证码自动生成
 */
public class ValidatorAutoCreate {

    private int width = 160;
    private int height = 40;
    private int codeCount = 5;
    private int lineCount = 20;
    private String code = null;
    private String codeUrl = null;
    private String realLocation = null;

    private char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
            'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};

    private BufferedImage bufferedImage;

    public static ValidatorAutoCreate getInstance() throws IOException{
        ValidatorAutoCreate va = new ValidatorAutoCreate();
        va.createCodeImage();
        va.write();
        return va;
    }

    private void createCodeImage(){
        int x_code = this.width/(this.codeCount+1); // code 的x轴位置
        int fontHeight = this.height-2; // 字符的高度
        int y_code = this.height-4;
        int red,green,blue = 0; // 三原色
        bufferedImage = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        Random random = new Random();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0,0,this.width,this.height);
        Font font = new Font("Fixedsys",Font.PLAIN,fontHeight);
        graphics2D.setFont(font);
        // 画干扰线
        for(int i = 0;i< lineCount;i++){
            int x1_line = random.nextInt(width);
            int y1_line = random.nextInt(height);
            int x2_line = random.nextInt(width-40);
            int y2_line = random.nextInt(height-10);
            red = random.nextInt(255);
            blue = random.nextInt(255);
            green = random.nextInt(255);
            graphics2D.setColor(new Color(red,green,blue));
            graphics2D.drawLine(x1_line,y1_line,x2_line,y2_line);
        }
        // 画字符
        StringBuffer randCode = new StringBuffer(codeCount);
        for(int i = 0;i<codeCount;i++){
            String ran = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            red  = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            graphics2D.setColor(new Color(red,green,blue));
            graphics2D.drawString(ran,(i+1)*x_code,y_code);
            randCode.append(ran);
        }
        code = randCode.toString();
    }

    public void write()throws IOException{
        String path = "D:/Data/store/validatorCode/";
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        int codeHash = code.hashCode();
        OutputStream outputStream = new FileOutputStream(path+codeHash+".png");
        realLocation = path+codeHash+".png";
        if (null == bufferedImage){
            createCodeImage();
        }
        ImageIO.write(bufferedImage,"png",outputStream);
        outputStream.flush();
        outputStream.close();
        StringBuffer sb = new StringBuffer("http://localhost:8081/v1/validate/"+codeHash+".png");
        codeUrl = sb.toString();
    }

    public String getCode(){
        return this.code;
    }

    public BufferedImage getBufferedImage(){
        return this.bufferedImage;
    }

    public String getCodeUrl(){
        return this.codeUrl;
    }

    public String getRealLocation(){
        return this.realLocation;
    }
}
