package com.jgoodies.common.internal;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicGraphicsUtils;


















































public final class RenderingUtils
{
  private static final String PROP_DESKTOPHINTS = "awt.font.desktophints";
  private static final String SWING_UTILITIES2_NAME = "sun.swing.SwingUtilities2";
  private static Method drawStringMethod = null;




  
  private static Method drawStringUnderlineCharAtMethod = null;



  
  private static Method getFontMetricsMethod = null;
  
  static {
    drawStringMethod = getMethodDrawString();
    drawStringUnderlineCharAtMethod = getMethodDrawStringUnderlineCharAt();
    getFontMetricsMethod = getMethodGetFontMetrics();
  }















  
  public static void drawString(JComponent c, Graphics g, String text, int x, int y) {
    if (drawStringMethod != null) {
      try {
        drawStringMethod.invoke(null, new Object[] { c, g, text, Integer.valueOf(x), Integer.valueOf(y) });
        
        return;
      } catch (IllegalArgumentException e) {
      
      } catch (IllegalAccessException e) {
      
      } catch (InvocationTargetException e) {}
    }

    
    Graphics2D g2 = (Graphics2D)g;
    Map<?, ?> oldRenderingHints = installDesktopHints(g2);
    BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, -1, x, y);
    if (oldRenderingHints != null) {
      g2.addRenderingHints(oldRenderingHints);
    }
  }













  
  public static void drawStringUnderlineCharAt(JComponent c, Graphics g, String text, int underlinedIndex, int x, int y) {
    if (drawStringUnderlineCharAtMethod != null) {
      try {
        drawStringUnderlineCharAtMethod.invoke(null, new Object[] { c, g, text, new Integer(underlinedIndex), new Integer(x), new Integer(y) });

        
        return;
      } catch (IllegalArgumentException e) {
      
      } catch (IllegalAccessException e) {
      
      } catch (InvocationTargetException e) {}
    }

    
    Graphics2D g2 = (Graphics2D)g;
    Map<?, ?> oldRenderingHints = installDesktopHints(g2);
    BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, underlinedIndex, x, y);
    if (oldRenderingHints != null) {
      g2.addRenderingHints(oldRenderingHints);
    }
  }


















  
  public static FontMetrics getFontMetrics(JComponent c, Graphics g) {
    if (getFontMetricsMethod != null) {
      try {
        return (FontMetrics)getFontMetricsMethod.invoke(null, new Object[] { c, g });
      } catch (IllegalArgumentException e) {
      
      } catch (IllegalAccessException e) {
      
      } catch (InvocationTargetException e) {}
    }

    
    return c.getFontMetrics(g.getFont());
  }



  
  private static Method getMethodDrawString() {
    try {
      Class<?> clazz = Class.forName("sun.swing.SwingUtilities2");
      return clazz.getMethod("drawString", new Class[] { JComponent.class, Graphics.class, String.class, int.class, int.class });

    
    }
    catch (ClassNotFoundException e) {
    
    } catch (SecurityException e) {
    
    } catch (NoSuchMethodException e) {}

    
    return null;
  }

  
  private static Method getMethodDrawStringUnderlineCharAt() {
    try {
      Class<?> clazz = Class.forName("sun.swing.SwingUtilities2");
      return clazz.getMethod("drawStringUnderlineCharAt", new Class[] { JComponent.class, Graphics.class, String.class, int.class, int.class, int.class });

    
    }
    catch (ClassNotFoundException e) {
    
    } catch (SecurityException e) {
    
    } catch (NoSuchMethodException e) {}

    
    return null;
  }

  
  private static Method getMethodGetFontMetrics() {
    try {
      Class<?> clazz = Class.forName("sun.swing.SwingUtilities2");
      return clazz.getMethod("getFontMetrics", new Class[] { JComponent.class, Graphics.class });

    
    }
    catch (ClassNotFoundException e) {
    
    } catch (SecurityException e) {
    
    } catch (NoSuchMethodException e) {}

    
    return null;
  }

  
  private static Map installDesktopHints(Graphics2D g2) {
    Map<Object, Object> oldRenderingHints = null;
    Map<?, ?> desktopHints = desktopHints(g2);
    if (desktopHints != null && !desktopHints.isEmpty()) {
      oldRenderingHints = new HashMap<Object, Object>(desktopHints.size());
      
      for (Iterator<RenderingHints.Key> i = desktopHints.keySet().iterator(); i.hasNext(); ) {
        RenderingHints.Key key = i.next();
        oldRenderingHints.put(key, g2.getRenderingHint(key));
      } 
      g2.addRenderingHints(desktopHints);
    } 
    return oldRenderingHints;
  }

  
  private static Map desktopHints(Graphics2D g2) {
    if (isPrinting(g2)) {
      return null;
    }
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    GraphicsDevice device = g2.getDeviceConfiguration().getDevice();
    Map desktopHints = (Map)toolkit.getDesktopProperty("awt.font.desktophints." + device.getIDstring());
    
    if (desktopHints == null) {
      desktopHints = (Map)toolkit.getDesktopProperty("awt.font.desktophints");
    }
    
    if (desktopHints != null) {
      Object aaHint = desktopHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
      if (aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF || aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)
      {
        desktopHints = null;
      }
    } 
    return desktopHints;
  }

  
  private static boolean isPrinting(Graphics g) {
    return (g instanceof java.awt.PrintGraphics || g instanceof java.awt.print.PrinterGraphics);
  }
}
