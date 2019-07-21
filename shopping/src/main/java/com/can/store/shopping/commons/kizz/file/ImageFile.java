package com.can.store.shopping.commons.kizz.file;

import java.io.*;
import java.util.Date;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class ImageFile {
    /**
     * 获取图片文件的真实类型
     *
     * @param filename 真实路径
     * @return
     */
    public static String getType(String filename) {
        try {
            return getType(new FileInputStream(new File(filename)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bytesToHexString(byte[] src) {
        if (null == src) {
            return null;
        }
        return bytesToHexString(src, 0, src.length);
    }

    public static String bytesToHexString(byte[] src, int start, int len) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            int v = src[start + i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 根据文件流判断图片类型
     *
     * @param fis
     * @return jpg/png/gif
     */
    public static String getType(FileInputStream fis) {
        //读取文件的前几个字节来判断图片格式
        byte[] b = new byte[4];
        try {
            fis.read(b, 0, b.length);
            String type = bytesToHexString(b).toUpperCase();
            if (type.contains("FFD8FF")) {
                return "jpeg";
            } else if (type.contains("89504E47")) {
                return "png";
            } else if (type.contains("47494638")) {
                return "gif";
            } else if (type.contains("424D")) {
                return "bmp";
            } else {
                //unsupport file
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static File save(InputStream instreams, String filename_prefix) {
        if (null == instreams) {
            return null;
        }

        File file = null;
        try {
            if (null == filename_prefix || filename_prefix.equals("")) {
                filename_prefix = "" + (new Date()).getTime();
            }
            file = File.createTempFile(filename_prefix, null);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] b = new byte[1024];
            int nRead = 0;
            while ((nRead = instreams.read(b)) != -1) {
                fos.write(b, 0, nRead);
            }

            fos.flush();
            fos.close();

            String ext_name = getType(new FileInputStream(file));
            if (null == ext_name) {
                file.deleteOnExit();
                file = null;
            } else {
                //图片格式不正确
                String filename = file.getPath();
                filename = filename.substring(0, filename.length() - 3) + ext_name;
                File finalName = new File(filename);
                file.renameTo(finalName);
                file.deleteOnExit();
                file = null;
                file = finalName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (instreams != null) {
                    instreams.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }
}