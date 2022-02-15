package com.formdev.flatlaf.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.Icon;
import javax.swing.ImageIcon;

























public class ScaledImageIcon
  implements Icon
{
  private final ImageIcon imageIcon;
  private final int iconWidth;
  private final int iconHeight;
  private double lastSystemScaleFactor;
  private float lastUserScaleFactor;
  private Image lastImage;
  
  public ScaledImageIcon(ImageIcon imageIcon) {
    this(imageIcon, imageIcon.getIconWidth(), imageIcon.getIconHeight());
  }
  
  public ScaledImageIcon(ImageIcon imageIcon, int iconWidth, int iconHeight) {
    this.imageIcon = imageIcon;
    this.iconWidth = iconWidth;
    this.iconHeight = iconHeight;
  }

  
  public int getIconWidth() {
    return UIScale.scale(this.iconWidth);
  }

  
  public int getIconHeight() {
    return UIScale.scale(this.iconHeight);
  }







  
  public void paintIcon(Component c, Graphics g, int x, int y) {
    double systemScaleFactor = UIScale.getSystemScaleFactor((Graphics2D)g);
    float userScaleFactor = UIScale.getUserScaleFactor();
    double scaleFactor = systemScaleFactor * userScaleFactor;

    
    if (scaleFactor == 1.0D && this.iconWidth == this.imageIcon.getIconWidth() && this.iconHeight == this.imageIcon.getIconHeight()) {
      this.imageIcon.paintIcon(c, g, x, y);
      
      return;
    } 
    
    if (systemScaleFactor == this.lastSystemScaleFactor && userScaleFactor == this.lastUserScaleFactor && this.lastImage != null) {


      
      paintLastImage(g, x, y);
      
      return;
    } 
    
    int destImageWidth = (int)Math.round(this.iconWidth * scaleFactor);
    int destImageHeight = (int)Math.round(this.iconHeight * scaleFactor);

    
    Image image = getResolutionVariant(destImageWidth, destImageHeight);

    
    int imageWidth = image.getWidth(null);
    int imageHeight = image.getHeight(null);

    
    if (imageWidth != destImageWidth || imageHeight != destImageHeight) {
      
      Object scalingInterpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
      float imageScaleFactor = destImageWidth / imageWidth;
      if ((int)imageScaleFactor == imageScaleFactor && imageScaleFactor > 1.0F && imageWidth <= 16 && imageHeight <= 16)
      {




        
        scalingInterpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      }

      
      BufferedImage bufferedImage = image2bufferedImage(image);
      image = scaleImage(bufferedImage, destImageWidth, destImageHeight, scalingInterpolation);
    } 

    
    this.lastSystemScaleFactor = systemScaleFactor;
    this.lastUserScaleFactor = userScaleFactor;
    this.lastImage = image;

    
    paintLastImage(g, x, y);
  }
  
  protected Image getResolutionVariant(int destImageWidth, int destImageHeight) {
    return MultiResolutionImageSupport.getResolutionVariant(this.imageIcon
        .getImage(), destImageWidth, destImageHeight);
  }
  
  private void paintLastImage(Graphics g, int x, int y) {
    if (this.lastSystemScaleFactor > 1.0D) {
      HiDPIUtils.paintAtScale1x((Graphics2D)g, x, y, 100, 100, (g2, x2, y2, width2, height2, scaleFactor2) -> g2.drawImage(this.lastImage, x2, y2, (ImageObserver)null));
    
    }
    else {
      
      g.drawImage(this.lastImage, x, y, null);
    } 
  }







  
  private BufferedImage scaleImage(BufferedImage image, int targetWidth, int targetHeight, Object scalingInterpolation) {
    BufferedImage bufferedImage = new BufferedImage(targetWidth, targetHeight, 2);
    Graphics2D g = bufferedImage.createGraphics();
    try {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, scalingInterpolation);
      g.drawImage(image, 0, 0, targetWidth, targetHeight, null);
    } finally {
      g.dispose();
    } 
    return bufferedImage;
  }

  
  private BufferedImage image2bufferedImage(Image image) {
    if (image instanceof BufferedImage) {
      return (BufferedImage)image;
    }
    
    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
    Graphics2D g = bufferedImage.createGraphics();
    try {
      g.drawImage(image, 0, 0, (ImageObserver)null);
    } finally {
      g.dispose();
    } 
    return bufferedImage;
  }
}
