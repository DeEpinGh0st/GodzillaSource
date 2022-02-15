package org.fife.ui.autocomplete;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.AccessControlException;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.UIManager;












































public final class Util
{
  public static final String PROPERTY_DONT_USE_SUBSTANCE_RENDERERS = "org.fife.ui.autocomplete.DontUseSubstanceRenderers";
  public static final String PROPERTY_ALLOW_DECORATED_AUTOCOMPLETE_WINDOWS = "org.fife.ui.autocomplete.allowDecoratedAutoCompleteWindows";
  public static final Color LIGHT_HYPERLINK_FG = new Color(14221311);
  
  private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]*>");
  
  private static final boolean USE_SUBSTANCE_RENDERERS;
  private static boolean desktopCreationAttempted;
  private static Object desktop;
  private static final Object LOCK_DESKTOP_CREATION = new Object();













  
  public static boolean browse(URI uri) {
    boolean success = false;
    
    if (uri != null) {
      Object desktop = getDesktop();
      if (desktop != null) {
        try {
          Method m = desktop.getClass().getDeclaredMethod("browse", new Class[] { URI.class });
          
          m.invoke(desktop, new Object[] { uri });
          success = true;
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception e) {}
      }
    } 


    
    return success;
  }










  
  private static Object getDesktop() {
    synchronized (LOCK_DESKTOP_CREATION) {
      
      if (!desktopCreationAttempted) {
        
        desktopCreationAttempted = true;
        
        try {
          Class<?> desktopClazz = Class.forName("java.awt.Desktop");
          
          Method m = desktopClazz.getDeclaredMethod("isDesktopSupported", new Class[0]);
          
          boolean supported = ((Boolean)m.invoke(null, new Object[0])).booleanValue();
          if (supported) {
            m = desktopClazz.getDeclaredMethod("getDesktop", new Class[0]);
            desktop = m.invoke(null, new Object[0]);
          }
        
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception e) {}
      } 
    } 




    
    return desktop;
  }










  
  public static String getHexString(Color c) {
    if (c == null) {
      return null;
    }



    
    StringBuilder sb = new StringBuilder("#");
    int r = c.getRed();
    if (r < 16) {
      sb.append('0');
    }
    sb.append(Integer.toHexString(r));
    int g = c.getGreen();
    if (g < 16) {
      sb.append('0');
    }
    sb.append(Integer.toHexString(g));
    int b = c.getBlue();
    if (b < 16) {
      sb.append('0');
    }
    sb.append(Integer.toHexString(b));
    
    return sb.toString();
  }














  
  static Color getHyperlinkForeground() {
    Color fg = UIManager.getColor("Label.foreground");
    if (fg == null) {
      fg = (new JLabel()).getForeground();
    }
    
    return isLightForeground(fg) ? LIGHT_HYPERLINK_FG : Color.blue;
  }












  
  public static Rectangle getScreenBoundsForPoint(int x, int y) {
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] devices = env.getScreenDevices();
    for (GraphicsDevice device : devices) {
      GraphicsConfiguration config = device.getDefaultConfiguration();
      Rectangle gcBounds = config.getBounds();
      if (gcBounds.contains(x, y)) {
        return gcBounds;
      }
    } 
    
    return env.getMaximumWindowBounds();
  }










  
  public static boolean getShouldAllowDecoratingMainAutoCompleteWindows() {
    try {
      return Boolean.getBoolean("org.fife.ui.autocomplete.allowDecoratedAutoCompleteWindows");
    }
    catch (AccessControlException ace) {
      return false;
    } 
  }









  
  public static boolean getUseSubstanceRenderers() {
    return USE_SUBSTANCE_RENDERERS;
  }









  
  public static boolean isLightForeground(Color fg) {
    return (fg.getRed() > 160 && fg.getGreen() > 160 && fg.getBlue() > 160);
  }









  
  public static boolean startsWithIgnoreCase(String str, String prefix) {
    int prefixLength = prefix.length();
    if (str.length() >= prefixLength) {
      return str.regionMatches(true, 0, prefix, 0, prefixLength);
    }
    return false;
  }








  
  public static String stripHtml(String text) {
    if (text == null || !text.startsWith("<html>")) {
      return text;
    }
    
    return TAG_PATTERN.matcher(text).replaceAll("");
  }



  
  static {
    try {
      bool = !Boolean.getBoolean("org.fife.ui.autocomplete.DontUseSubstanceRenderers") ? true : false;
    } catch (AccessControlException ace) {
      bool = true;
    } 
    USE_SUBSTANCE_RENDERERS = bool;
  }
  
  static {
    boolean bool;
  }
}
