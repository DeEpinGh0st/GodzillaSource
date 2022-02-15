package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Function;
import javax.swing.JComponent;































public class MigLayoutVisualPadding
{
  public static String VISUAL_PADDING_PROPERTY = "visualPadding";
  
  private static final FlatMigInsets ZERO = new FlatMigInsets(0, 0, 0, 0);
  
  private static final boolean migLayoutAvailable;
  
  static {
    boolean available = false;
    try {
      Class.forName("net.miginfocom.swing.MigLayout");
      available = true;
    } catch (ClassNotFoundException classNotFoundException) {}

    
    migLayoutAvailable = available;
  }



  
  public static void install(JComponent c, Insets insets) {
    if (!migLayoutAvailable) {
      return;
    }
    setVisualPadding(c, insets);
  }



  
  public static void install(JComponent c) {
    if (!migLayoutAvailable) {
      return;
    }
    install(c, c2 -> { FlatBorder border = FlatUIUtils.getOutsideFlatBorder(c2); if (border != null) { int focusWidth = border.getFocusWidth(c2); return new Insets(focusWidth, focusWidth, focusWidth, focusWidth); }  return null; }new String[] { "border" });
  }












  
  public static void install(JComponent c, Function<JComponent, Insets> getPaddingFunction, String... propertyNames) {
    if (!migLayoutAvailable) {
      return;
    }
    
    setVisualPadding(c, getPaddingFunction.apply(c));

    
    c.addPropertyChangeListener(e -> {
          String propertyName = e.getPropertyName();
          for (String name : propertyNames) {
            if (name == propertyName) {
              setVisualPadding(c, getPaddingFunction.apply(c));
              break;
            } 
          } 
        });
  }
  
  private static void setVisualPadding(JComponent c, Insets visualPadding) {
    Object oldPadding = c.getClientProperty(VISUAL_PADDING_PROPERTY);
    if (oldPadding == null || oldPadding instanceof FlatMigInsets) {

      
      FlatMigInsets flatVisualPadding = (visualPadding != null) ? new FlatMigInsets(UIScale.scale2(visualPadding.top), UIScale.scale2(visualPadding.left), UIScale.scale2(visualPadding.bottom), UIScale.scale2(visualPadding.right)) : ZERO;

      
      c.putClientProperty(VISUAL_PADDING_PROPERTY, flatVisualPadding);
    } 
  }



  
  public static void uninstall(JComponent c) {
    if (!migLayoutAvailable) {
      return;
    }
    
    for (PropertyChangeListener l : c.getPropertyChangeListeners()) {
      if (l instanceof FlatMigListener) {
        c.removePropertyChangeListener(l);
        
        break;
      } 
    } 
    
    if (c.getClientProperty(VISUAL_PADDING_PROPERTY) instanceof FlatMigInsets) {
      c.putClientProperty(VISUAL_PADDING_PROPERTY, (Object)null);
    }
  }

  
  private static interface FlatMigListener
    extends PropertyChangeListener {}

  
  private static class FlatMigInsets
    extends Insets
  {
    FlatMigInsets(int top, int left, int bottom, int right) {
      super(top, left, bottom, right);
    }
  }
}
