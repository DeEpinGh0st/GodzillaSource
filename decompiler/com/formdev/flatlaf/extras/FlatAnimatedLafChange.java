package com.formdev.flatlaf.extras;

import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.Animator;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.image.VolatileImage;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.RootPaneContainer;


































public class FlatAnimatedLafChange
{
  public static int duration = 160;



  
  public static int resolution = 40;
  
  private static Animator animator;
  private static final Map<JLayeredPane, JComponent> oldUIsnapshots = new WeakHashMap<>();
  private static final Map<JLayeredPane, JComponent> newUIsnapshots = new WeakHashMap<>();

  
  private static float alpha;
  
  private static boolean inShowSnapshot;

  
  public static void showSnapshot() {
    if (!FlatSystemProperties.getBoolean("flatlaf.animatedLafChange", true)) {
      return;
    }
    
    if (animator != null) {
      animator.stop();
    }
    alpha = 1.0F;

    
    showSnapshot(true, oldUIsnapshots);
  }
  
  private static void showSnapshot(final boolean useAlpha, Map<JLayeredPane, JComponent> map) {
    inShowSnapshot = true;

    
    Window[] windows = Window.getWindows();
    for (Window window : windows) {
      if (window instanceof RootPaneContainer && window.isShowing()) {



        
        final VolatileImage snapshot = window.createVolatileImage(window.getWidth(), window.getHeight());
        if (snapshot != null) {


          
          JLayeredPane layeredPane = ((RootPaneContainer)window).getLayeredPane();
          layeredPane.paint(snapshot.getGraphics());


          
          JComponent snapshotLayer = new JComponent()
            {
              public void paint(Graphics g) {
                if (FlatAnimatedLafChange.inShowSnapshot || snapshot.contentsLost()) {
                  return;
                }
                if (useAlpha)
                  ((Graphics2D)g).setComposite(AlphaComposite.getInstance(3, FlatAnimatedLafChange.alpha)); 
                g.drawImage(snapshot, 0, 0, null);
              }

              
              public void removeNotify() {
                super.removeNotify();

                
                snapshot.flush();
              }
            };
          if (!useAlpha)
            snapshotLayer.setOpaque(true); 
          snapshotLayer.setSize(layeredPane.getSize());

          
          layeredPane.add(snapshotLayer, Integer.valueOf(JLayeredPane.DRAG_LAYER.intValue() + (useAlpha ? 2 : 1)));
          map.put(layeredPane, snapshotLayer);
        } 
      } 
    }  inShowSnapshot = false;
  }





  
  public static void hideSnapshotWithAnimation() {
    if (!FlatSystemProperties.getBoolean("flatlaf.animatedLafChange", true)) {
      return;
    }
    if (oldUIsnapshots.isEmpty()) {
      return;
    }
    
    showSnapshot(false, newUIsnapshots);

    
    animator = new Animator(duration, fraction -> {
          if (fraction < 0.1D || fraction > 0.9D) {
            return;
          }
          
          alpha = 1.0F - fraction;
          
          for (Map.Entry<JLayeredPane, JComponent> e : oldUIsnapshots.entrySet()) {
            if (((JLayeredPane)e.getKey()).isShowing()) {
              ((JComponent)e.getValue()).repaint();
            }
          } 
        }() -> {
          hideSnapshot();
          animator = null;
        });
    animator.setResolution(resolution);
    animator.start();
  }
  
  private static void hideSnapshot() {
    hideSnapshot(oldUIsnapshots);
    hideSnapshot(newUIsnapshots);
  }

  
  private static void hideSnapshot(Map<JLayeredPane, JComponent> map) {
    for (Map.Entry<JLayeredPane, JComponent> e : map.entrySet()) {
      ((JLayeredPane)e.getKey()).remove(e.getValue());
      ((JLayeredPane)e.getKey()).repaint();
    } 
    
    map.clear();
  }



  
  public static void stop() {
    if (animator != null) {
      animator.stop();
    } else {
      hideSnapshot();
    } 
  }
}
