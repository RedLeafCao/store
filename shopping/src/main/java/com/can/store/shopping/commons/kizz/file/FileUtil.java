package com.can.store.shopping.commons.kizz.file;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class FileUtil {
    public static void rename(String sourceFileName, String targetFileName) {

    }

    public static void rename(File sourceFile, File targetFile) {

    }

    public static void copy(String sourceFileName, String targetFileName) {

    }

    public static void copy(File sourceFile, File targetFile) {

    }

    public static void delete(String file) {

    }

    public static void delete(File file) {

    }

    public static File zip(List<File> files, String filename) {
        try {
            if (null == filename) {
                filename = (new Date()).getTime() + ".zip";
            }

            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(filename));
            for (int i = 0; i < files.size(); i++) {
                File f = files.get(i);
                FileInputStream fis = new FileInputStream(f);
                zout.putNextEntry(new ZipEntry(f.getName()));

                int len;
                byte[] buffer = new byte[1024];
                while ((len = fis.read(buffer)) > 0) {
                    zout.write(buffer, 0, len);
                }
                fis.close();
                f.deleteOnExit();
            }
            zout.close();

            File outFile = new File(filename);
            return outFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * zip目录或文件
     *
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @throws IOException
     */
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static int download2zip(List<File> files, String filename, HttpServletResponse response) {
        try {
            response.setContentType("application/force-download");
            response.setHeader("content-disposition", "attachment;filename="
                    + new String((filename).getBytes(), "utf-8"));

            ZipOutputStream zout = new ZipOutputStream(response.getOutputStream());
            for (int i = 0; i < files.size(); i++) {
                File f = files.get(i);
                FileInputStream fis = new FileInputStream(f);
                zout.putNextEntry(new ZipEntry(f.getName()));

                int len;
                byte[] buffer = new byte[1024];
                while ((len = fis.read(buffer)) > 0) {
                    zout.write(buffer, 0, len);
                }
                fis.close();
                f.deleteOnExit();
            }

            zout.close();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int download(InputStream zipInStream, String filename, HttpServletResponse response) {
        int returnCode = 1;
        try {
            response.reset();
            response.setContentType("application/force-download");
            response.setHeader("content-disposition", "attachment;filename="
                    + new String((filename).getBytes(), "utf-8"));

            int len;
            byte[] buffer = new byte[1024];
            while ((len = zipInStream.read(buffer)) > 0) {
                response.getOutputStream().write(buffer, 0, len);
            }
            zipInStream.close();
            returnCode = 0;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    public static int download(File zipFile, String filename, HttpServletResponse response) {
        int returnCode = 1;

        try {
            download(new FileInputStream(zipFile), filename, response);
            returnCode = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    /**
     * 获取文件内容
     *
     * @param certPath 文件路径
     * @return
     */
    public static byte[] getFileContent(String certPath) {
        byte[] certData = null;
        try {
            File file = new File(certPath);
            InputStream fileInputStream = new FileInputStream(file);
            certData = new byte[(int) file.length()];
            fileInputStream.read(certData);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return certData;
    }
}