package com.jgoodies.common.base;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.logging.Logger;
import javax.swing.UIManager;














































public class SystemUtils
{
  protected static final String OS_NAME = getSystemProperty("os.name");







  
  protected static final String OS_VERSION = getSystemProperty("os.version");







  
  protected static final String JAVA_VERSION = getSystemProperty("java.version");






  
  public static final boolean IS_OS_LINUX = (startsWith(OS_NAME, "Linux") || startsWith(OS_NAME, "LINUX"));





  
  public static final boolean IS_OS_MAC = startsWith(OS_NAME, "Mac OS");





  
  public static final boolean IS_OS_SOLARIS = startsWith(OS_NAME, "Solaris");





  
  public static final boolean IS_OS_WINDOWS = startsWith(OS_NAME, "Windows");





  
  public static final boolean IS_OS_WINDOWS_98 = (startsWith(OS_NAME, "Windows 9") && startsWith(OS_VERSION, "4.1"));





  
  public static final boolean IS_OS_WINDOWS_ME = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "4.9"));





  
  public static final boolean IS_OS_WINDOWS_2000 = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "5.0"));





  
  public static final boolean IS_OS_WINDOWS_XP = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "5.1"));








  
  public static final boolean IS_OS_WINDOWS_XP_64_BIT_OR_SERVER_2003 = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "5.2"));





  
  public static final boolean IS_OS_WINDOWS_VISTA = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "6.0"));





  
  public static final boolean IS_OS_WINDOWS_7 = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "6.1"));





  
  public static final boolean IS_OS_WINDOWS_8 = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "6.2"));













  
  public static final boolean IS_OS_WINDOWS_6_OR_LATER = (startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "6."));







  
  public static final boolean IS_JAVA_6 = startsWith(JAVA_VERSION, "1.6");





  
  public static final boolean IS_JAVA_7 = startsWith(JAVA_VERSION, "1.7");





  
  public static final boolean IS_JAVA_7_OR_LATER = !IS_JAVA_6;







  
  public static final boolean IS_JAVA_8 = startsWith(JAVA_VERSION, "1.8");








  
  public static final boolean IS_JAVA_8_OR_LATER = (!IS_JAVA_6 && !IS_JAVA_7);









  
  public static final boolean HAS_MODERN_RASTERIZER = hasModernRasterizer();






  
  public static final boolean IS_LAF_WINDOWS_XP_ENABLED = isWindowsXPLafEnabled();







  
  public static final boolean IS_LOW_RESOLUTION = isLowResolution();




  
  private static final String AWT_UTILITIES_CLASS_NAME = "com.sun.awt.AWTUtilities";




  
  public static boolean isLafAqua() {
    return UIManager.getLookAndFeel().getID().equals("Aqua");
  }






















  
  protected static String getSystemProperty(String key) {
    try {
      return System.getProperty(key);
    } catch (SecurityException e) {
      Logger.getLogger(SystemUtils.class.getName()).warning("Can't access the System property " + key + ".");
      
      return "";
    } 
  }

  
  protected static boolean startsWith(String str, String prefix) {
    return (str != null && str.startsWith(prefix));
  }










  
  private static boolean hasModernRasterizer() {
    try {
      Class.forName("com.sun.awt.AWTUtilities");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    } 
  }
















  
  private static boolean isWindowsXPLafEnabled() {
    return (IS_OS_WINDOWS && Boolean.TRUE.equals(Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive")) && getSystemProperty("swing.noxp") == null);
  }




  
  private static boolean isLowResolution() {
    try {
      return (Toolkit.getDefaultToolkit().getScreenResolution() < 120);
    } catch (HeadlessException e) {
      return true;
    } 
  }
}
