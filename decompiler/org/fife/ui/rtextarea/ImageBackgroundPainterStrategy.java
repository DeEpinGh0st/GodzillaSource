package org.fife.ui.rtextarea;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;


































public abstract class ImageBackgroundPainterStrategy
  implements BackgroundPainterStrategy
{
  protected MediaTracker tracker;
  private RTextAreaBase textArea;
  private Image master;
  private int oldWidth;
  private int oldHeight;
  private int scalingHint;
  
  public ImageBackgroundPainterStrategy(RTextAreaBase textArea) {
    this.textArea = textArea;
    this.tracker = new MediaTracker(textArea);
    this.scalingHint = 2;
  }






  
  public RTextAreaBase getRTextAreaBase() {
    return this.textArea;
  }








  
  public Image getMasterImage() {
    return this.master;
  }







  
  public int getScalingHint() {
    return this.scalingHint;
  }









  
  public final void paint(Graphics g, Rectangle bounds) {
    if (bounds.width != this.oldWidth || bounds.height != this.oldHeight) {
      rescaleImage(bounds.width, bounds.height, getScalingHint());
      this.oldWidth = bounds.width;
      this.oldHeight = bounds.height;
    } 
    paintImage(g, bounds.x, bounds.y);
  }









  
  protected abstract void paintImage(Graphics paramGraphics, int paramInt1, int paramInt2);








  
  protected abstract void rescaleImage(int paramInt1, int paramInt2, int paramInt3);








  
  public void setImage(URL imageURL) {
    BufferedImage image = null;
    try {
      image = ImageIO.read(imageURL);
    } catch (Exception e) {
      e.printStackTrace();
    } 
    setImage(image);
  }





  
  public void setImage(Image image) {
    this.master = image;
    this.oldWidth = -1;
  }







  
  public void setScalingHint(int hint) {
    this.scalingHint = hint;
  }
}
