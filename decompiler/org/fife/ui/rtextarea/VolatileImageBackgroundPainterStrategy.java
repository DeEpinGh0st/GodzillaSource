package org.fife.ui.rtextarea;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.VolatileImage;





































public class VolatileImageBackgroundPainterStrategy
  extends ImageBackgroundPainterStrategy
{
  private VolatileImage bgImage;
  
  public VolatileImageBackgroundPainterStrategy(RTextAreaBase ta) {
    super(ta);
  }











  
  protected void paintImage(Graphics g, int x, int y) {
    if (this.bgImage != null) {
      do {
        int rc = this.bgImage.validate(null);
        if (rc == 1)
        {

          
          renderImage(this.bgImage.getWidth(), this.bgImage.getHeight(), 
              getScalingHint());
        }
        g.drawImage(this.bgImage, x, y, null);
      } while (this.bgImage.contentsLost());
    }
  }










  
  private void renderImage(int width, int height, int hint) {
    Image master = getMasterImage();
    if (master != null) {
      do {
        Image i = master.getScaledInstance(width, height, hint);
        this.tracker.addImage(i, 1);
        try {
          this.tracker.waitForID(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
          this.bgImage = null;
          return;
        } finally {
          this.tracker.removeImage(i, 1);
        } 
        this.bgImage.getGraphics().drawImage(i, 0, 0, null);
        this.tracker.addImage(this.bgImage, 0);
        try {
          this.tracker.waitForID(0);
        } catch (InterruptedException e) {
          e.printStackTrace();
          this.bgImage = null;
          return;
        } finally {
          this.tracker.removeImage(this.bgImage, 0);
        } 
      } while (this.bgImage.contentsLost());
    } else {
      
      this.bgImage = null;
    } 
  }









  
  protected void rescaleImage(int width, int height, int hint) {
    this.bgImage = getRTextAreaBase().createVolatileImage(width, height);
    if (this.bgImage != null)
      renderImage(width, height, hint); 
  }
}
