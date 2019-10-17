package com.bid.launcherwatch.online;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class NetHelper {
    private static Handler sHandler = null;
    private static String sNetLastUpdateDate = null;
    private static List<OnlineClockSkinXMLNode> sOnlineThemeList = null;

    public static void initHandler(Handler handler) {
        sHandler = handler;
    }

    public static InputStream fetchOnlineFile(String file) {
        InputStream input = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://www.weitetech.cn/clockskin/" + file).openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            if (connection.getReadTimeout() == 1) {
                Log.e("net", " in  fetchOnlineFile()  timeout ");
                sendMsg(1);
                return null;
            }
            input = connection.getInputStream();
            return input;
        } catch (MalformedURLException e) {
            Log.e("net", " in  fetchOnlineFile()  exception : " + e.toString());
            e.printStackTrace();
        } catch (IOException e2) {
            Log.e("net", " in  fetchOnlineFile()  exception : " + e2.toString());
            e2.printStackTrace();
        }
        return input;
    }

    public static Long getOnlineFileLastModifiedTime(String file) {
        Long time = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://www.weitetech.cn/clockskin/" + file).openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            if (connection.getReadTimeout() == 1) {
                Log.e("net", " in  fetchOnlineFile()  timeout ");
                sendMsg(1);
                return null;
            }
            time = Long.valueOf(connection.getLastModified());
            return time;
        } catch (MalformedURLException e) {
            Log.e("net", " in  getOnlineFileLastModifiedTime()  exception : " + e.toString());
            e.printStackTrace();
        } catch (IOException e2) {
            Log.e("net", " in  getOnlineFileLastModifiedTime()  exception : " + e2.toString());
            e2.printStackTrace();
        }
        return time;
    }

    public static int getOnlineFileSize(String file) {
        int size = 0;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://www.weitetech.cn/clockskin/" + file).openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            if (connection.getReadTimeout() == 1) {
                Log.e("net", " in  fetchOnlineFile()  timeout ");
                sendMsg(1);
                return 0;
            }
            size = connection.getContentLength();
            return size;
        } catch (MalformedURLException e) {
            Log.e("net", " in  getOnlineFileSize()  exception : " + e.toString());
            e.printStackTrace();
        } catch (IOException e2) {
            Log.e("net", " in  getOnlineFileLastModifiedTime()  exception : " + e2.toString());
            e2.printStackTrace();
        }
        return size;
    }

    public static String getLastUpdateDate() {
        return sNetLastUpdateDate;
    }

    public static List<OnlineClockSkinXMLNode> getOnlineThemeList() {
        return sOnlineThemeList;
    }

    public static int getOnlineThemeListSize() {
        if (sOnlineThemeList == null) {
            return 0;
        }
        Log.i("TAG", "getOnlineThemeListSize=" + sOnlineThemeList.size());
        return sOnlineThemeList.size();
    }

    public static boolean checkNetIsOk(Context con) {
        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netinfo = cm.getAllNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        for (NetworkInfo isConnected : netinfo) {
            if (isConnected.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIsServerOk() {
        Log.i("net", "in netHelper checkIsServerOk()    ");
        try {
            URL url = new URL("http://www.weitetech.cn/clockskin/clockskin.xml");
            URLConnection myurlcon = url.openConnection();
            myurlcon.setConnectTimeout(3000);
            myurlcon.setReadTimeout(3000);
            url.openStream();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("net", "in checkIsServerOk()  " + e.toString());
            return false;
        }
    }

    public static void sendMsg(int what) {
        Message msg = sHandler.obtainMessage();
        msg.what = what;
        sHandler.sendMessage(msg);
    }

    public static boolean checkFontIsNeedUpdate(Context con) {
        Log.i("net", "in netHelper checkIsNeedUpdate()    ");
        return checkFontIsNeedUpdate2(con);
    }

    public static boolean checkFontIsNeedUpdate2(Context con) {
        Log.i("net", "in netHelper checkIsNeedUpdate()\t");
        if (!checkNetIsOk(con)) {
            Log.e("net", "in netHelper checkNetIsOk()   error!  ");
            sendMsg(0);
            return false;
        }
        boolean bNeedUpdate = false;
        File cacheDir = con.getCacheDir();
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        File file = new File(cacheDir, "clockskin.xml");
        if (!file.exists()) {
            bNeedUpdate = true;
        } else {
            Long currModifiedTime = getOnlineFileLastModifiedTime("clockskin.xml");
            Log.e("xxxx", " time: " + currModifiedTime + " " + file.lastModified());
            if (currModifiedTime != null) {
                Log.e("xxxx", " time-: " + (currModifiedTime.longValue() - file.lastModified()));
                if (currModifiedTime.longValue() > file.lastModified()) {
                    bNeedUpdate = true;
                }
            } else {
                sendMsg(0);
                return false;
            }
        }
        Log.e("xxxx", " need update??" + bNeedUpdate);
        if (bNeedUpdate) {
            InputStream input = fetchOnlineFile("clockskin.xml");
            if (input != null) {
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    while (true) {
                        int count = input.read();
                        if (count == -1) {
                            break;
                        }
                        outputStream.write(count);
                    }
                    outputStream.close();
                    input.close();
                    try {
                        sOnlineThemeList = XMLParserHelperBySAXGBK.parseXML(new FileInputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("net", "IOException");
                        sendMsg(0);
                        return false;
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                    sendMsg(0);
                    return false;
                }
            } else {
                Log.e("net", "in netHelper checkIsNeedUpdate()   fetchOnlineFile == null\t!!!!!  ");
                sendMsg(0);
                return false;
            }
        } else {
            try {
                sOnlineThemeList = XMLParserHelperBySAXGBK.parseXML(new FileInputStream(file));
            } catch (IOException e3) {
                e3.printStackTrace();
                Log.i("net", "IOException");
                sendMsg(0);
                return false;
            }
        }
        Log.i("net", "in netHelper checkIsNeedUpdate()  needHelper ? " + bNeedUpdate);
        return bNeedUpdate;
    }
}
