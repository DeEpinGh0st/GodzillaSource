package core;

import core.ui.component.dialog.GOptionPane;
import core.ui.component.dialog.ImageShowDialog;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import util.Log;
import util.functions;







public class ApplicationConfig
{
  private static final String GITEE_CONFIG_URL = "https://gitee.com/beichendram/Godzilla/raw/master/%s";
  private static final String GIT_CONFIG_URL = "https://raw.githubusercontent.com/BeichenDream/Godzilla/master/%s";
  private static String ACCESS_URL = "https://gitee.com/beichendram/Godzilla/raw/master/%s";
  public static final String GIT = "https://github.com/BeichenDream/Godzilla";
  private static final HashMap<String, String> headers = new HashMap<>();

  
  static {
    headers.put("Accept", "*/*");
    headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
  }



  
  public static void invoke() {
    if (functions.getCurrentJarFile() == null) {
      return;
    }
    
    HashMap<String, String> configMap = null;
    
    try {
      configMap = getAppConfig(String.format("https://gitee.com/beichendram/Godzilla/raw/master/%s", new Object[] { "application.config" }));
      ACCESS_URL = "https://gitee.com/beichendram/Godzilla/raw/master/%s";
    } catch (Exception e) {
      try {
        configMap = getAppConfig(String.format("https://raw.githubusercontent.com/BeichenDream/Godzilla/master/%s", new Object[] { "application.config" }));
        ACCESS_URL = "https://raw.githubusercontent.com/BeichenDream/Godzilla/master/%s";
      } catch (Exception e2) {
        Log.error("Network connection failure");
      } 
    } 
    try {
      HashMap<String, String> md5SumMap = getAppConfig(String.format(ACCESS_URL, new Object[] { "hashsumJar" }));
      String hashString = md5SumMap.get("4.01");
      File jarFile = functions.getCurrentJarFile();
      String jarHashString = new String();
      if (jarFile != null) {
        FileInputStream inputStream = new FileInputStream(jarFile);
        byte[] jar = functions.readInputStream(inputStream);
        inputStream.close();
        jarHashString = functions.SHA(jar, "SHA-512");
      } 
      if (hashString != null) {
        if (jarFile != null) {
          if (!jarHashString.equals(hashString)) {
            String tipString = EasyI18N.getI18nString("??????????????????????????????????????????   ????????????????????????\r\n??????Jar??????:%s\r\n??????Jar??????:%s", new Object[] { hashString, jarHashString });
            GOptionPane.showMessageDialog(null, tipString, EasyI18N.getI18nString("??????\t????????????:", new Object[] { "4.01" }), 2);
            Log.error(String.format(tipString, new Object[] { hashString, jarHashString }));
            System.exit(0);
          } else {
            Log.error(EasyI18N.getI18nString("??????Hash??????   Hash Url:%s\r\n??????Jar??????:%s\r\n??????Jar??????:%s", new Object[] { String.format(ACCESS_URL, new Object[] { "hashsumJar" }), hashString, jarHashString }));
          } 
        } else {
          String tipString = EasyI18N.getI18nString("?????????Jar??????\r\n??????????????????????????????????????????   ????????????????????????");
          GOptionPane.showMessageDialog(null, tipString, EasyI18N.getI18nString("??????\t????????????:%s", new Object[] { "4.01", hashString }), 2);
          Log.error(tipString);
          System.exit(0);
        } 
      } else {
        String tipString = EasyI18N.getI18nString("?????????????????????(%s)???Hash\r\n??????Hash:%s\r\n??????????????????????????????????????????   ????????????????????????", new Object[] { "4.01", jarHashString });
        JOptionPane.showMessageDialog(null, tipString, EasyI18N.getI18nString("??????\t????????????:%s", new Object[] { "4.01" }), 2);
        Log.error(String.format(tipString, new Object[] { "4.01" }));
        System.exit(0);
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    
    if (configMap != null && configMap.size() > 0) {

      
      String version = configMap.get("currentVersion");
      boolean isShowGroup = Boolean.valueOf(configMap.get("isShowGroup")).booleanValue();
      
      String wxGroupImageUrl = configMap.get("wxGroupImageUrl");
      
      String showGroupTitle = configMap.get("showGroupTitle");
      
      String gitUrl = configMap.get("gitUrl");
      
      boolean isShowAppTip = Boolean.valueOf(configMap.get("isShowAppTip")).booleanValue();
      
      String appTip = configMap.get("appTip");
      
      if (version != null && wxGroupImageUrl != null && appTip != null && gitUrl != null) {
        
        if (functions.stringToint(version.replace(".", "")) > functions.stringToint("4.01".replace(".", ""))) {
          GOptionPane.showMessageDialog(null, EasyI18N.getI18nString("?????????????????????\n????????????:%s\n????????????:%s", new Object[] { "4.01", version }), "message", 2);
          functions.openBrowseUrl(gitUrl);
        } 
        
        if (isShowAppTip) {
          JOptionPane.showMessageDialog(null, appTip, "message", 1);
        }
        
        if (isShowGroup) {
          try {
            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new ByteArrayInputStream(functions.httpReqest(wxGroupImageUrl, "GET", headers, null))));
            ImageShowDialog.showImageDiaolog(imageIcon, showGroupTitle);
          } catch (IOException e) {
            Log.error(e);
            Log.error("showGroup fail!");
          } 
        }
      } 
    } 
  }


  
  private static HashMap<String, String> getAppConfig(String configUrl) throws Exception {
    String configString;
    byte[] result = functions.httpReqest(configUrl, "GET", headers, null);
    if (result == null) {
      throw new Exception("readApplication Fail!");
    }
    
    try {
      configString = new String(result, "utf-8");
    } catch (UnsupportedEncodingException e) {
      configString = new String(result);
    } 
    HashMap<String, String> hashMap = new HashMap<>();
    String[] lines = configString.split("\n");
    for (String line : lines) {
      int index = line.indexOf(':');
      if (index != -1) {
        hashMap.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
      }
    } 
    return hashMap;
  }
}
