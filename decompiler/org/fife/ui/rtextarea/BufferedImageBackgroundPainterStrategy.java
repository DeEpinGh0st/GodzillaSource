package org.fife.ui.rtextarea;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;








































public class BufferedImageBackgroundPainterStrategy
  extends ImageBackgroundPainterStrategy
{
  private BufferedImage bgImage;
  
  public BufferedImageBackgroundPainterStrategy(RTextAreaBase ta) {
    super(ta);
  }











  
  protected void paintImage(Graphics g, int x, int y) {
    if (this.bgImage != null) {
      g.drawImage(this.bgImage, x, y, null);
    }
  }










  
  protected void rescaleImage(int width, int height, int hint) {
    Image master = getMasterImage();
    if (master != null) {
      
      Map<RenderingHints.Key, Object> hints = new HashMap<>();
      
      switch (hint) {
      
      } 
      
      hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      
      hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      
      hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


      
      this.bgImage = createAcceleratedImage(width, height);
      Graphics2D g = this.bgImage.createGraphics();
      g.addRenderingHints(hints);
      g.drawImage(master, 0, 0, width, height, null);
      g.dispose();
    }
    else {
      
      this.bgImage = null;
    } 
  }

  
  private BufferedImage createAcceleratedImage(int width, int height) {
    GraphicsConfiguration gc = getRTextAreaBase().getGraphicsConfiguration();
    BufferedImage image = gc.createCompatibleImage(width, height);
    return image;
  }
}
