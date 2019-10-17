package com.bid.launcherwatch.online;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipUtils {
    public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        Enumeration<?> entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            InputStream in = zf.getInputStream(entry);
            File desFile = new File(new String((folderPath + File.separator + entry.getName()).getBytes("8859_1"), "GB2312"));
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte[] buffer = new byte[1048576];
            while (true) {
                int realLength = in.read(buffer);
                if (realLength <= 0) {
                    break;
                }
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
    }
}
