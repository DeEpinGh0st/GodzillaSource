package com.formdev.flatlaf.util;

import com.formdev.flatlaf.FlatSystemProperties;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;




















































public class UIScale
{
  private static final boolean DEBUG = false;
  private static PropertyChangeSupport changeSupport;
  private static Boolean jreHiDPI;
  
  public static void addPropertyChangeListener(PropertyChangeListener listener) {
    if (changeSupport == null)
      changeSupport = new PropertyChangeSupport(UIScale.class); 
    changeSupport.addPropertyChangeListener(listener);
  }
  
  public static void removePropertyChangeListener(PropertyChangeListener listener) {
    if (changeSupport == null)
      return; 
    changeSupport.removePropertyChangeListener(listener);
  }







  
  public static boolean isSystemScalingEnabled() {
    if (jreHiDPI != null) {
      return jreHiDPI.booleanValue();
    }
    jreHiDPI = Boolean.valueOf(false);
    
    if (SystemInfo.isJava_9_orLater) {
      
      jreHiDPI = Boolean.valueOf(true);
    } else if (SystemInfo.isJetBrainsJVM) {

      
      try {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Class<?> sunGeClass = Class.forName("sun.java2d.SunGraphicsEnvironment");
        if (sunGeClass.isInstance(ge)) {
          Method m = sunGeClass.getDeclaredMethod("isUIScaleOn", new Class[0]);
          jreHiDPI = (Boolean)m.invoke(ge, new Object[0]);
        } 
      } catch (Throwable throwable) {}
    } 


    
    return jreHiDPI.booleanValue();
  }



  
  public static double getSystemScaleFactor(Graphics2D g) {
    return isSystemScalingEnabled() ? getSystemScaleFactor(g.getDeviceConfiguration()) : 1.0D;
  }



  
  public static double getSystemScaleFactor(GraphicsConfiguration gc) {
    return (isSystemScalingEnabled() && gc != null) ? gc.getDefaultTransform().getScaleX() : 1.0D;
  }


  
  private static float scaleFactor = 1.0F;
  private static boolean initialized;
  
  private static void initialize() {
    if (initialized)
      return; 
    initialized = true;
    
    if (!isUserScalingEnabled()) {
      return;
    }
    
    PropertyChangeListener listener = new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent e) {
          switch (e.getPropertyName()) {
            
            case "lookAndFeel":
              if (e.getNewValue() instanceof javax.swing.LookAndFeel)
                UIManager.getLookAndFeelDefaults().addPropertyChangeListener(this); 
              UIScale.updateScaleFactor();
              break;
            
            case "defaultFont":
            case "Label.font":
              UIScale.updateScaleFactor();
              break;
          } 
        }
      };
    UIManager.addPropertyChangeListener(listener);
    UIManager.getDefaults().addPropertyChangeListener(listener);
    UIManager.getLookAndFeelDefaults().addPropertyChangeListener(listener);
    
    updateScaleFactor();
  }
  private static void updateScaleFactor() {
    float newScaleFactor;
    if (!isUserScalingEnabled()) {
      return;
    }
    
    float customScaleFactor = getCustomScaleFactor();
    if (customScaleFactor > 0.0F) {
      setUserScaleFactor(customScaleFactor);


      
      return;
    } 

    
    Font font = UIManager.getFont("defaultFont");
    if (font == null) {
      font = UIManager.getFont("Label.font");
    }
    
    if (SystemInfo.isWindows) {








      
      if (font instanceof javax.swing.plaf.UIResource) {
        if (isSystemScalingEnabled())
        {

          
          newScaleFactor = 1.0F;

        
        }
        else
        {

          
          Font winFont = (Font)Toolkit.getDefaultToolkit().getDesktopProperty("win.defaultGUI.font");
          newScaleFactor = computeScaleFactor((winFont != null) ? winFont : font);
        
        }

      
      }
      else {
        
        newScaleFactor = computeScaleFactor(font);
      } 
    } else {
      newScaleFactor = computeScaleFactor(font);
    } 
    setUserScaleFactor(newScaleFactor);
  }

  
  private static float computeScaleFactor(Font font) {
    float fontSizeDivider = 12.0F;
    
    if (SystemInfo.isWindows) {


      
      if ("Tahoma".equals(font.getFamily()))
        fontSizeDivider = 11.0F; 
    } else if (SystemInfo.isMacOS) {
      
      fontSizeDivider = 13.0F;
    } else if (SystemInfo.isLinux) {
      
      fontSizeDivider = SystemInfo.isKDE ? 13.0F : 15.0F;
    } 
    
    return font.getSize() / fontSizeDivider;
  }
  
  private static boolean isUserScalingEnabled() {
    return FlatSystemProperties.getBoolean("flatlaf.uiScale.enabled", true);
  }




  
  public static FontUIResource applyCustomScaleFactor(FontUIResource font) {
    if (!isUserScalingEnabled()) {
      return font;
    }
    float scaleFactor = getCustomScaleFactor();
    if (scaleFactor <= 0.0F) {
      return font;
    }
    float fontScaleFactor = computeScaleFactor(font);
    if (scaleFactor == fontScaleFactor) {
      return font;
    }
    int newFontSize = Math.round(font.getSize() / fontScaleFactor * scaleFactor);
    return new FontUIResource(font.deriveFont(newFontSize));
  }



  
  private static float getCustomScaleFactor() {
    return parseScaleFactor(System.getProperty("flatlaf.uiScale"));
  }



  
  private static float parseScaleFactor(String s) {
    if (s == null) {
      return -1.0F;
    }
    float units = 1.0F;
    if (s.endsWith("x")) {
      s = s.substring(0, s.length() - 1);
    } else if (s.endsWith("dpi")) {
      units = 96.0F;
      s = s.substring(0, s.length() - 3);
    } else if (s.endsWith("%")) {
      units = 100.0F;
      s = s.substring(0, s.length() - 1);
    } 
    
    try {
      float scale = Float.parseFloat(s);
      return (scale > 0.0F) ? (scale / units) : -1.0F;
    } catch (NumberFormatException ex) {
      return -1.0F;
    } 
  }



  
  public static float getUserScaleFactor() {
    initialize();
    return scaleFactor;
  }



  
  private static void setUserScaleFactor(float scaleFactor) {
    if (scaleFactor <= 1.0F) {
      scaleFactor = 1.0F;
    } else {
      scaleFactor = Math.round(scaleFactor * 4.0F) / 4.0F;
    } 
    float oldScaleFactor = UIScale.scaleFactor;
    UIScale.scaleFactor = scaleFactor;



    
    if (changeSupport != null) {
      changeSupport.firePropertyChange("userScaleFactor", Float.valueOf(oldScaleFactor), Float.valueOf(scaleFactor));
    }
  }


  
  public static float scale(float value) {
    initialize();
    return (scaleFactor == 1.0F) ? value : (value * scaleFactor);
  }



  
  public static int scale(int value) {
    initialize();
    return (scaleFactor == 1.0F) ? value : Math.round(value * scaleFactor);
  }





  
  public static int scale2(int value) {
    initialize();
    return (scaleFactor == 1.0F) ? value : (int)(value * scaleFactor);
  }



  
  public static float unscale(float value) {
    initialize();
    return (scaleFactor == 1.0F) ? value : (value / scaleFactor);
  }



  
  public static int unscale(int value) {
    initialize();
    return (scaleFactor == 1.0F) ? value : Math.round(value / scaleFactor);
  }




  
  public static void scaleGraphics(Graphics2D g) {
    initialize();
    if (scaleFactor != 1.0F) {
      g.scale(scaleFactor, scaleFactor);
    }
  }






  
  public static Dimension scale(Dimension dimension) {
    initialize();
    return (dimension == null || scaleFactor == 1.0F) ? dimension : ((dimension instanceof javax.swing.plaf.UIResource) ? new DimensionUIResource(

        
        scale(dimension.width), scale(dimension.height)) : new Dimension(
        scale(dimension.width), scale(dimension.height)));
  }







  
  public static Insets scale(Insets insets) {
    initialize();
    return (insets == null || scaleFactor == 1.0F) ? insets : ((insets instanceof javax.swing.plaf.UIResource) ? new InsetsUIResource(

        
        scale(insets.top), scale(insets.left), scale(insets.bottom), scale(insets.right)) : new Insets(
        scale(insets.top), scale(insets.left), scale(insets.bottom), scale(insets.right)));
  }
}
