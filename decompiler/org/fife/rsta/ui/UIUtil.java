package org.fife.rsta.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;















public final class UIUtil
{
  private static boolean desktopCreationAttempted;
  private static Object desktop;
  private static final Object LOCK_DESKTOP_CREATION = new Object();




  
  private static final Border EMPTY_5_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

















  
  public static boolean browse(String uri) {
    if (uri == null) {
      return false;
    }
    try {
      return browse(new URI(uri));
    } catch (URISyntaxException e) {
      return false;
    } 
  }











  
  public static boolean browse(URI uri) {
    boolean success = false;
    
    if (uri != null) {
      Object desktop = getDesktop();
      if (desktop != null) {
        try {
          Method m = desktop.getClass().getDeclaredMethod("browse", new Class[] { URI.class });
          
          m.invoke(desktop, new Object[] { uri });
          success = true;
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception exception) {}
      }
    } 


    
    return success;
  }








  
  public static void fixComboOrientation(JComboBox<?> combo) {
    ListCellRenderer<?> r = combo.getRenderer();
    if (r instanceof Component) {
      
      ComponentOrientation o = ComponentOrientation.getOrientation(Locale.getDefault());
      ((Component)r).setComponentOrientation(o);
    } 
  }












  
  private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
    SpringLayout layout = (SpringLayout)parent.getLayout();
    Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }









  
  private static Object getDesktop() {
    synchronized (LOCK_DESKTOP_CREATION) {
      
      if (!desktopCreationAttempted) {
        
        desktopCreationAttempted = true;
        
        try {
          Class<?> desktopClazz = Class.forName("java.awt.Desktop");
          
          Method m = desktopClazz.getDeclaredMethod("isDesktopSupported", new Class[0]);
          
          boolean supported = ((Boolean)m.invoke(null, new Object[0])).booleanValue();
          if (supported) {
            m = desktopClazz.getDeclaredMethod("getDesktop", new Class[0]);
            desktop = m.invoke(null, new Object[0]);
          }
        
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception exception) {}
      } 
    } 




    
    return desktop;
  }








  
  public static Border getEmpty5Border() {
    return EMPTY_5_BORDER;
  }








  
  public static Color getErrorTextForeground() {
    Color defaultFG = UIManager.getColor("TextField.foreground");
    if (defaultFG.getRed() >= 160 && defaultFG.getGreen() >= 160 && defaultFG
      .getBlue() >= 160) {
      return new Color(255, 160, 160);
    }
    return Color.red;
  }








  
  public static int getMnemonic(ResourceBundle msg, String key) {
    int mnemonic = 0;
    try {
      Object value = msg.getObject(key);
      if (value instanceof String) {
        mnemonic = ((String)value).charAt(0);
      }
    } catch (MissingResourceException missingResourceException) {}

    
    return mnemonic;
  }







  
  public static JTextComponent getTextComponent(JComboBox<?> combo) {
    return (JTextComponent)combo.getEditor().getEditorComponent();
  }























  
  public static void makeSpringCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {
    SpringLayout layout;
    try {
      layout = (SpringLayout)parent.getLayout();
    } catch (ClassCastException cce) {
      System.err.println("The first argument to makeCompactGrid must use SpringLayout.");

      
      return;
    } 
    
    Spring x = Spring.constant(initialX);
    for (int c = 0; c < cols; c++) {
      Spring width = Spring.constant(0); int i;
      for (i = 0; i < rows; i++) {
        width = Spring.max(width, 
            getConstraintsForCell(i, c, parent, cols)
            .getWidth());
      }
      for (i = 0; i < rows; i++) {
        
        SpringLayout.Constraints constraints = getConstraintsForCell(i, c, parent, cols);
        constraints.setX(x);
        constraints.setWidth(width);
      } 
      x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    } 

    
    Spring y = Spring.constant(initialY);
    for (int r = 0; r < rows; r++) {
      Spring height = Spring.constant(0); int i;
      for (i = 0; i < cols; i++) {
        height = Spring.max(height, 
            getConstraintsForCell(r, i, parent, cols).getHeight());
      }
      for (i = 0; i < cols; i++) {
        
        SpringLayout.Constraints constraints = getConstraintsForCell(r, i, parent, cols);
        constraints.setY(y);
        constraints.setHeight(height);
      } 
      y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    } 

    
    SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint("South", y);
    pCons.setConstraint("East", x);
  }











  
  public static JButton newButton(ResourceBundle bundle, String key) {
    JButton b = new JButton(bundle.getString(key));
    b.setMnemonic(getMnemonic(bundle, key + ".Mnemonic"));
    return b;
  }












  
  public static JLabel newLabel(ResourceBundle msg, String key, Component labelFor) {
    JLabel label = new JLabel(msg.getString(key));
    String mnemonicKey = key + ".Mnemonic";
    label.setDisplayedMnemonic(getMnemonic(msg, mnemonicKey));
    if (labelFor != null) {
      label.setLabelFor(labelFor);
    }
    return label;
  }
}
