package net.miginfocom.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;
import java.util.StringTokenizer;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.PlatformDefaults;


























public class SwingComponentWrapper
  implements ComponentWrapper
{
  private static boolean maxSet = false;
  private static boolean vp = true;
  private static final Color DB_COMP_OUTLINE = new Color(0, 0, 200);




  
  private static final String VISUAL_PADDING_PROPERTY = PlatformDefaults.VISUAL_PADDING_PROPERTY;
  
  private final Component c;
  private int compType = -1;
  private Boolean bl = null;
  
  private boolean prefCalled = false;
  
  public SwingComponentWrapper(Component c) {
    this.c = c;
  }


  
  public final int getBaseline(int width, int height) {
    int h = height;
    int[] visPad = getVisualPadding();
    if (h < 0) {
      h = this.c.getHeight();
    } else if (visPad != null) {
      h = height + visPad[0] + visPad[2];
    } 
    int baseLine = this.c.getBaseline(Math.max(0, (width < 0) ? this.c.getWidth() : width), Math.max(0, h));
    if (baseLine != -1 && visPad != null) {
      baseLine -= visPad[0];
    }
    return baseLine;
  }


  
  public final Object getComponent() {
    return this.c;
  }


  
  private static final IdentityHashMap<FontMetrics, Point2D.Float> FM_MAP = new IdentityHashMap<>(4);
  private static final Font SUBST_FONT = new Font("sansserif", 0, 11); public final float getPixelUnitFactor(boolean isHor) { Font font; FontMetrics fm; Point2D.Float p;
    Float s;
    float scaleFactor;
    Object lafScaleFactorObj;
    float screenScale;
    switch (PlatformDefaults.getLogicalPixelBase()) {
      case 100:
        font = this.c.getFont();
        fm = this.c.getFontMetrics((font != null) ? font : SUBST_FONT);
        p = FM_MAP.get(fm);
        if (p == null) {
          Rectangle2D r = fm.getStringBounds("X", this.c.getGraphics());
          p = new Point2D.Float((float)r.getWidth() / 6.0F, (float)r.getHeight() / 13.277344F);
          FM_MAP.put(fm, p);
        } 
        return isHor ? p.x : p.y;

      
      case 101:
        s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
        scaleFactor = (s != null) ? s.floatValue() : 1.0F;


        
        lafScaleFactorObj = UIManager.get("laf.scaleFactor");
        if (lafScaleFactorObj instanceof Number) {
          float lafScaleFactor = ((Number)lafScaleFactorObj).floatValue();
          return scaleFactor * lafScaleFactor;
        } 






        
        screenScale = isJava9orLater ? 1.0F : ((isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI()) / PlatformDefaults.getDefaultDPI());
        return scaleFactor * screenScale;
    } 
    
    return 1.0F; }


  
  private static boolean isJava9orLater;
  
  static {
    try {
      StringTokenizer st = new StringTokenizer(System.getProperty("java.version"), "._-+");
      int majorVersion = Integer.parseInt(st.nextToken());
      isJava9orLater = (majorVersion >= 9);
    } catch (Exception exception) {}
  }

























  
  public final int getX() {
    return this.c.getX();
  }


  
  public final int getY() {
    return this.c.getY();
  }


  
  public final int getHeight() {
    return this.c.getHeight();
  }


  
  public final int getWidth() {
    return this.c.getWidth();
  }


  
  public final int getScreenLocationX() {
    Point p = new Point();
    SwingUtilities.convertPointToScreen(p, this.c);
    return p.x;
  }


  
  public final int getScreenLocationY() {
    Point p = new Point();
    SwingUtilities.convertPointToScreen(p, this.c);
    return p.y;
  }


  
  public final int getMinimumHeight(int sz) {
    if (!this.prefCalled) {
      this.c.getPreferredSize();
      this.prefCalled = true;
    } 
    return (this.c.getMinimumSize()).height;
  }


  
  public final int getMinimumWidth(int sz) {
    if (!this.prefCalled) {
      this.c.getPreferredSize();
      this.prefCalled = true;
    } 
    return (this.c.getMinimumSize()).width;
  }


  
  public final int getPreferredHeight(int sz) {
    if (this.c.getWidth() == 0 && this.c.getHeight() == 0 && sz != -1) {
      this.c.setBounds(this.c.getX(), this.c.getY(), sz, 1);
    }
    return (this.c.getPreferredSize()).height;
  }



  
  public final int getPreferredWidth(int sz) {
    if (this.c.getWidth() == 0 && this.c.getHeight() == 0 && sz != -1) {
      this.c.setBounds(this.c.getX(), this.c.getY(), 1, sz);
    }
    return (this.c.getPreferredSize()).width;
  }


  
  public final int getMaximumHeight(int sz) {
    if (!isMaxSet(this.c)) {
      return Integer.MAX_VALUE;
    }
    return (this.c.getMaximumSize()).height;
  }


  
  public final int getMaximumWidth(int sz) {
    if (!isMaxSet(this.c)) {
      return Integer.MAX_VALUE;
    }
    return (this.c.getMaximumSize()).width;
  }


  
  private boolean isMaxSet(Component c) {
    return c.isMaximumSizeSet();
  }


  
  public final ContainerWrapper getParent() {
    Container p = this.c.getParent();
    return (p != null) ? new SwingContainerWrapper(p) : null;
  }

  
  public final int getHorizontalScreenDPI() {
    try {
      return this.c.getToolkit().getScreenResolution();
    } catch (HeadlessException ex) {
      return PlatformDefaults.getDefaultDPI();
    } 
  }


  
  public final int getVerticalScreenDPI() {
    try {
      return this.c.getToolkit().getScreenResolution();
    } catch (HeadlessException ex) {
      return PlatformDefaults.getDefaultDPI();
    } 
  }


  
  public final int getScreenWidth() {
    try {
      return (this.c.getToolkit().getScreenSize()).width;
    } catch (HeadlessException ex) {
      return 1024;
    } 
  }


  
  public final int getScreenHeight() {
    try {
      return (this.c.getToolkit().getScreenSize()).height;
    } catch (HeadlessException ex) {
      return 768;
    } 
  }


  
  public final boolean hasBaseline() {
    if (this.bl == null) {
      try {
        if (this.c instanceof javax.swing.JLabel && ((JComponent)this.c).getClientProperty("html") != null) {
          this.bl = Boolean.FALSE;
        } else {
          this.bl = Boolean.valueOf((getBaseline(8192, 8192) > -1));


        
        }



      
      }
      catch (Throwable ex) {
        this.bl = Boolean.FALSE;
      } 
    }
    return this.bl.booleanValue();
  }


  
  public final String getLinkId() {
    return this.c.getName();
  }


  
  public final void setBounds(int x, int y, int width, int height) {
    this.c.setBounds(x, y, width, height);
  }


  
  public boolean isVisible() {
    return this.c.isVisible();
  }


  
  public final int[] getVisualPadding() {
    int[] padding = null;
    if (isVisualPaddingEnabled())
    {
      if (this.c instanceof JComponent) {
        JComponent component = (JComponent)this.c;
        Object padValue = component.getClientProperty(VISUAL_PADDING_PROPERTY);
        
        if (padValue instanceof int[]) {
          
          padding = (int[])padValue;
        } else if (padValue instanceof Insets) {
          
          Insets padInsets = (Insets)padValue;
          padding = new int[] { padInsets.top, padInsets.left, padInsets.bottom, padInsets.right };
        } 
        
        if (padding == null) {
          String classID;
          
          Border border;
          switch (getComponentType(false)) {
            case 5:
              border = component.getBorder();
              if (border != null && border.getClass().getName().startsWith("com.apple.laf.AquaButtonBorder")) {
                if (PlatformDefaults.getPlatform() == 1) {
                  String str1; Object buttonType = component.getClientProperty("JButton.buttonType");
                  if (buttonType == null) {
                    str1 = (component.getHeight() < 33) ? "Button" : "Button.bevel";
                  } else {
                    str1 = "Button." + buttonType;
                  } 
                  if (((AbstractButton)component).getIcon() != null)
                    str1 = str1 + ".icon";  break;
                } 
                String str = "Button";
                break;
              } 
              classID = "";
              break;

            
            case 16:
              border = component.getBorder();
              if (border != null && border.getClass().getName().startsWith("com.apple.laf.AquaButtonBorder")) {
                Object size = component.getClientProperty("JComponent.sizeVariant");
                if (size != null && !size.toString().equals("regular")) {
                  size = "." + size;
                } else {
                  size = "";
                } 
                
                if (component instanceof javax.swing.JRadioButton) {
                  classID = "RadioButton" + size; break;
                }  if (component instanceof javax.swing.JCheckBox) {
                  classID = "CheckBox" + size; break;
                } 
                classID = "ToggleButton" + size;
                break;
              } 
              classID = "";
              break;

            
            case 11:
              if (PlatformDefaults.getPlatform() == 1) {
                if (((JComboBox)component).isEditable()) {
                  Object object = component.getClientProperty("JComboBox.isSquare");
                  if (object != null && object.toString().equals("true")) {
                    classID = "ComboBox.editable.isSquare"; break;
                  } 
                  classID = "ComboBox.editable";
                  
                  break;
                } 
                Object isSquare = component.getClientProperty("JComboBox.isSquare");
                Object isPopDown = component.getClientProperty("JComboBox.isPopDown");
                
                if (isSquare != null && isSquare.toString().equals("true")) {
                  classID = "ComboBox.isSquare"; break;
                }  if (isPopDown != null && isPopDown.toString().equals("true")) {
                  classID = "ComboBox.isPopDown"; break;
                } 
                classID = "ComboBox";
                
                break;
              } 
              classID = "ComboBox";
              break;
            
            case 1:
              classID = "Container";
              break;
            case 9:
              classID = "Image";
              break;
            case 2:
              classID = "Label";
              break;
            case 6:
              classID = "List";
              break;
            case 10:
              classID = "Panel";
              break;
            case 14:
              classID = "ProgressBar";
              break;
            case 17:
              classID = "ScrollBar";
              break;
            case 8:
              classID = "ScrollPane";
              break;
            case 18:
              classID = "Separator";
              break;
            case 12:
              classID = "Slider";
              break;
            case 13:
              classID = "Spinner";
              break;
            case 7:
              classID = "Table";
              break;
            case 19:
              classID = "TabbedPane";
              break;
            case 4:
              classID = "TextArea";
              break;
            case 3:
              border = component.getBorder();
              if (!component.isOpaque() && border != null && border.getClass().getSimpleName().equals("AquaTextFieldBorder")) {
                classID = "TextField"; break;
              } 
              classID = "";
              break;
            
            case 15:
              classID = "Tree";
              break;
            case 0:
              classID = "Other";
              break;
            
            default:
              classID = "";
              break;
          } 
          
          padValue = PlatformDefaults.getDefaultVisualPadding(classID + "." + VISUAL_PADDING_PROPERTY);
          if (padValue instanceof int[]) {
            
            padding = (int[])padValue;
          } else if (padValue instanceof Insets) {
            
            Insets padInsets = (Insets)padValue;
            padding = new int[] { padInsets.top, padInsets.left, padInsets.bottom, padInsets.right };
          } 
        } 
      } 
    }
    return padding;
  }




  
  public static boolean isMaxSizeSetOn1_4() {
    return maxSet;
  }




  
  public static void setMaxSizeSetOn1_4(boolean b) {
    maxSet = b;
  }

  
  public static boolean isVisualPaddingEnabled() {
    return vp;
  }

  
  public static void setVisualPaddingEnabled(boolean b) {
    vp = b;
  }


  
  public final void paintDebugOutline(boolean showVisualPadding) {
    if (!this.c.isShowing()) {
      return;
    }
    Graphics2D g = (Graphics2D)this.c.getGraphics();
    if (g == null) {
      return;
    }
    g.setPaint(DB_COMP_OUTLINE);
    g.setStroke(new BasicStroke(1.0F, 2, 0, 10.0F, new float[] { 2.0F, 4.0F }, 0.0F));
    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    
    if (showVisualPadding && isVisualPaddingEnabled()) {
      int[] padding = getVisualPadding();
      if (padding != null) {
        g.setColor(Color.GREEN);
        g.drawRect(padding[1], padding[0], getWidth() - 1 - padding[1] + padding[3], getHeight() - 1 - padding[0] + padding[2]);
      } 
    } 
  }


  
  public int getComponentType(boolean disregardScrollPane) {
    if (this.compType == -1) {
      this.compType = checkType(disregardScrollPane);
    }
    return this.compType;
  }


  
  public int getLayoutHashCode() {
    Dimension d = this.c.getMaximumSize();
    int hash = d.width + (d.height << 5);
    
    d = this.c.getPreferredSize();
    hash += (d.width << 10) + (d.height << 15);
    
    d = this.c.getMinimumSize();
    hash += (d.width << 20) + (d.height << 25);
    
    if (this.c.isVisible()) {
      hash += 1324511;
    }
    String id = getLinkId();
    if (id != null) {
      hash += id.hashCode();
    }
    return hash;
  }

  
  private int checkType(boolean disregardScrollPane) {
    Component c = this.c;
    
    if (disregardScrollPane) {
      if (c instanceof JScrollPane) {
        c = ((JScrollPane)c).getViewport().getView();
      } else if (c instanceof ScrollPane) {
        c = ((ScrollPane)c).getComponent(0);
      } 
    }
    
    if (c instanceof javax.swing.JTextField || c instanceof java.awt.TextField)
      return 3; 
    if (c instanceof javax.swing.JLabel || c instanceof java.awt.Label)
      return 2; 
    if (c instanceof javax.swing.JCheckBox || c instanceof javax.swing.JRadioButton || c instanceof java.awt.Checkbox)
      return 16; 
    if (c instanceof AbstractButton || c instanceof java.awt.Button)
      return 5; 
    if (c instanceof JComboBox || c instanceof java.awt.Choice)
      return 11; 
    if (c instanceof javax.swing.text.JTextComponent || c instanceof java.awt.TextComponent)
      return 4; 
    if (c instanceof javax.swing.JPanel || c instanceof java.awt.Canvas)
      return 10; 
    if (c instanceof javax.swing.JList || c instanceof java.awt.List)
      return 6; 
    if (c instanceof javax.swing.JTable)
      return 7; 
    if (c instanceof javax.swing.JSeparator)
      return 18; 
    if (c instanceof javax.swing.JSpinner)
      return 13; 
    if (c instanceof javax.swing.JTabbedPane)
      return 19; 
    if (c instanceof javax.swing.JProgressBar)
      return 14; 
    if (c instanceof javax.swing.JSlider)
      return 12; 
    if (c instanceof JScrollPane)
      return 8; 
    if (c instanceof javax.swing.JScrollBar || c instanceof java.awt.Scrollbar)
      return 17; 
    if (c instanceof Container) {
      return 1;
    }
    return 0;
  }


  
  public final int hashCode() {
    return getComponent().hashCode();
  }


  
  public final boolean equals(Object o) {
    if (!(o instanceof ComponentWrapper)) {
      return false;
    }
    return this.c.equals(((ComponentWrapper)o).getComponent());
  }


  
  public int getContentBias() {
    return (this.c instanceof javax.swing.JTextArea || this.c instanceof javax.swing.JEditorPane || (this.c instanceof JComponent && Boolean.TRUE.equals(((JComponent)this.c).getClientProperty("migLayout.dynamicAspectRatio")))) ? 0 : -1;
  }
}
