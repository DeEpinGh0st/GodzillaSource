package com.formdev.flatlaf.util;

import com.formdev.flatlaf.FlatSystemProperties;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import javax.swing.JComponent;























public class HiDPIUtils
{
  private static Boolean useTextYCorrection;
  
  public static void paintAtScale1x(Graphics2D g, JComponent c, Painter painter) {
    paintAtScale1x(g, 0, 0, c.getWidth(), c.getHeight(), painter);
  }









  
  public static void paintAtScale1x(Graphics2D g, int x, int y, int width, int height, Painter painter) {
    AffineTransform transform = g.getTransform();

    
    if (transform.getScaleX() == 1.0D && transform.getScaleY() == 1.0D) {
      painter.paint(g, x, y, width, height, 1.0D);
      
      return;
    } 
    
    Rectangle2D.Double scaledRect = scale(transform, x, y, width, height);

    
    try {
      g.setTransform(new AffineTransform(1.0D, 0.0D, 0.0D, 1.0D, 
            Math.floor(scaledRect.x), Math.floor(scaledRect.y)));
      
      int swidth = (int)scaledRect.width;
      int sheight = (int)scaledRect.height;

      
      painter.paint(g, 0, 0, swidth, sheight, transform.getScaleX());
    } finally {
      
      g.setTransform(transform);
    } 
  }





  
  private static Rectangle2D.Double scale(AffineTransform transform, int x, int y, int width, int height) {
    double dx1 = transform.getScaleX();
    double dy2 = transform.getScaleY();
    double px = x * dx1 + transform.getTranslateX();
    double py = y * dy2 + transform.getTranslateY();
    dx1 *= width;
    dy2 *= height;
    
    double newx = normalize(px);
    double newy = normalize(py);
    dx1 = normalize(px + dx1) - newx;
    dy2 = normalize(py + dy2) - newy;
    
    return new Rectangle2D.Double(newx, newy, dx1, dy2);
  }
  
  private static double normalize(double value) {
    return Math.floor(value + 0.25D) + 0.25D;
  }



  
  private static boolean useTextYCorrection() {
    if (useTextYCorrection == null)
      useTextYCorrection = Boolean.valueOf(FlatSystemProperties.getBoolean("flatlaf.useTextYCorrection", true)); 
    return useTextYCorrection.booleanValue();
  }







  
  public static float computeTextYCorrection(Graphics2D g) {
    if (!useTextYCorrection() || !SystemInfo.isWindows) {
      return 0.0F;
    }
    if (!SystemInfo.isJava_9_orLater) {
      return (UIScale.getUserScaleFactor() > 1.0F) ? -UIScale.scale(0.625F) : 0.0F;
    }
    AffineTransform t = g.getTransform();
    double scaleY = t.getScaleY();
    if (scaleY < 1.25D) {
      return 0.0F;
    }









    
    if (scaleY <= 1.25D)
      return -0.875F; 
    if (scaleY <= 1.5D)
      return -0.625F; 
    if (scaleY <= 1.75D)
      return -0.875F; 
    if (scaleY <= 2.0D)
      return -0.75F; 
    if (scaleY <= 2.25D)
      return -0.875F; 
    if (scaleY <= 3.5D)
      return -0.75F; 
    return -0.875F;
  }








  
  public static void drawStringWithYCorrection(JComponent c, Graphics2D g, String text, int x, int y) {
    drawStringUnderlineCharAtWithYCorrection(c, g, text, -1, x, y);
  }








  
  public static void drawStringUnderlineCharAtWithYCorrection(JComponent c, Graphics2D g, String text, int underlinedIndex, int x, int y) {
    float yCorrection = computeTextYCorrection(g);
    if (yCorrection != 0.0F) {
      g.translate(0.0D, yCorrection);
      JavaCompatibility.drawStringUnderlineCharAt(c, g, text, underlinedIndex, x, y);
      g.translate(0.0D, -yCorrection);
    } else {
      JavaCompatibility.drawStringUnderlineCharAt(c, g, text, underlinedIndex, x, y);
    } 
  }



  
  public static Graphics2D createGraphicsTextYCorrection(Graphics2D g) {
    final float yCorrection = computeTextYCorrection(g);
    if (yCorrection == 0.0F) {
      return g;
    }
    return new Graphics2DProxy(g)
      {
        public void drawString(String str, int x, int y) {
          super.drawString(str, x, y + yCorrection);
        }

        
        public void drawString(String str, float x, float y) {
          super.drawString(str, x, y + yCorrection);
        }

        
        public void drawString(AttributedCharacterIterator iterator, int x, int y) {
          super.drawString(iterator, x, y + yCorrection);
        }

        
        public void drawString(AttributedCharacterIterator iterator, float x, float y) {
          super.drawString(iterator, x, y + yCorrection);
        }

        
        public void drawChars(char[] data, int offset, int length, int x, int y) {
          super.drawChars(data, offset, length, x, Math.round(y + yCorrection));
        }

        
        public void drawBytes(byte[] data, int offset, int length, int x, int y) {
          super.drawBytes(data, offset, length, x, Math.round(y + yCorrection));
        }

        
        public void drawGlyphVector(GlyphVector g, float x, float y) {
          super.drawGlyphVector(g, x, y + yCorrection);
        }
      };
  }
  
  public static interface Painter {
    void paint(Graphics2D param1Graphics2D, int param1Int1, int param1Int2, int param1Int3, int param1Int4, double param1Double);
  }
}
