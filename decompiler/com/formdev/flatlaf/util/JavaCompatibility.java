package com.formdev.flatlaf.util;

import com.formdev.flatlaf.FlatLaf;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;


































public class JavaCompatibility
{
  private static Method drawStringUnderlineCharAtMethod;
  private static Method getClippedStringMethod;
  
  public static void drawStringUnderlineCharAt(JComponent c, Graphics g, String text, int underlinedIndex, int x, int y) {
    synchronized (JavaCompatibility.class) {
      if (drawStringUnderlineCharAtMethod == null) {
        try {
          Class<?> cls = Class.forName(SystemInfo.isJava_9_orLater ? "javax.swing.plaf.basic.BasicGraphicsUtils" : "sun.swing.SwingUtilities2");

          
          (new Class[6])[0] = JComponent.class; (new Class[6])[1] = Graphics2D.class; (new Class[6])[2] = String.class; (new Class[6])[3] = int.class; (new Class[6])[4] = float.class; (new Class[6])[5] = float.class; (new Class[6])[0] = JComponent.class; (new Class[6])[1] = Graphics.class; (new Class[6])[2] = String.class; (new Class[6])[3] = int.class; (new Class[6])[4] = int.class; (new Class[6])[5] = int.class; drawStringUnderlineCharAtMethod = cls.getMethod("drawStringUnderlineCharAt", SystemInfo.isJava_9_orLater ? new Class[6] : new Class[6]);
        
        }
        catch (Exception ex) {
          Logger.getLogger(FlatLaf.class.getName()).log(Level.SEVERE, (String)null, ex);
          throw new RuntimeException(ex);
        } 
      }
    } 
    
    try {
      if (SystemInfo.isJava_9_orLater)
      { drawStringUnderlineCharAtMethod.invoke(null, new Object[] { c, g, text, Integer.valueOf(underlinedIndex), Float.valueOf(x), Float.valueOf(y) }); }
      else
      { drawStringUnderlineCharAtMethod.invoke(null, new Object[] { c, g, text, Integer.valueOf(underlinedIndex), Integer.valueOf(x), Integer.valueOf(y) }); } 
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException ex) {
      Logger.getLogger(FlatLaf.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new RuntimeException(ex);
    } 
  }







  
  public static String getClippedString(JComponent c, FontMetrics fm, String string, int availTextWidth) {
    synchronized (JavaCompatibility.class) {
      if (getClippedStringMethod == null) {
        try {
          Class<?> cls = Class.forName(SystemInfo.isJava_9_orLater ? "javax.swing.plaf.basic.BasicGraphicsUtils" : "sun.swing.SwingUtilities2");

          
          getClippedStringMethod = cls.getMethod(SystemInfo.isJava_9_orLater ? "getClippedString" : "clipStringIfNecessary", new Class[] { JComponent.class, FontMetrics.class, String.class, int.class });

        
        }
        catch (Exception ex) {
          Logger.getLogger(FlatLaf.class.getName()).log(Level.SEVERE, (String)null, ex);
          throw new RuntimeException(ex);
        } 
      }
    } 
    
    try {
      return (String)getClippedStringMethod.invoke(null, new Object[] { c, fm, string, Integer.valueOf(availTextWidth) });
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException ex) {
      Logger.getLogger(FlatLaf.class.getName()).log(Level.SEVERE, (String)null, ex);
      throw new RuntimeException(ex);
    } 
  }
}
