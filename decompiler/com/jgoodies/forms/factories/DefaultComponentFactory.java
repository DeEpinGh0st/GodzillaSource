package com.jgoodies.forms.factories;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.forms.layout.Sizes;
import com.jgoodies.forms.util.FormUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;























































public class DefaultComponentFactory
  implements ComponentFactory
{
  private static final DefaultComponentFactory INSTANCE = new DefaultComponentFactory();









  
  public static DefaultComponentFactory getInstance() {
    return INSTANCE;
  }


















  
  public JLabel createLabel(String textWithMnemonic) {
    JLabel label = new FormsLabel();
    MnemonicUtils.configure(label, textWithMnemonic);
    return label;
  }




















  
  public JLabel createReadOnlyLabel(String textWithMnemonic) {
    JLabel label = new ReadOnlyLabel();
    MnemonicUtils.configure(label, textWithMnemonic);
    return label;
  }














  
  public JButton createButton(Action action) {
    return new JButton(action);
  }

















  
  public JLabel createTitle(String textWithMnemonic) {
    JLabel label = new TitleLabel();
    MnemonicUtils.configure(label, textWithMnemonic);
    label.setVerticalAlignment(0);
    return label;
  }


  
  public JLabel createHeaderLabel(String markedText) {
    return createTitle(markedText);
  }

















  
  public JComponent createSeparator(String textWithMnemonic) {
    return createSeparator(textWithMnemonic, 2);
  }





















  
  public JComponent createSeparator(String textWithMnemonic, int alignment) {
    if (Strings.isBlank(textWithMnemonic)) {
      return new JSeparator();
    }
    JLabel title = createTitle(textWithMnemonic);
    title.setHorizontalAlignment(alignment);
    return createSeparator(title);
  }





























  
  public JComponent createSeparator(JLabel label) {
    Preconditions.checkNotNull(label, "The label must not be null.");
    int horizontalAlignment = label.getHorizontalAlignment();
    Preconditions.checkArgument((horizontalAlignment == 2 || horizontalAlignment == 0 || horizontalAlignment == 4), "The label's horizontal alignment must be one of: LEFT, CENTER, RIGHT.");



    
    JPanel panel = new JPanel(new TitledSeparatorLayout(!FormUtils.isLafAqua()));
    panel.setOpaque(false);
    panel.add(label);
    panel.add(new JSeparator());
    if (horizontalAlignment == 0) {
      panel.add(new JSeparator());
    }
    return panel;
  }








  
  private static class FormsLabel
    extends JLabel
  {
    private FormsLabel() {}








    
    public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
        this.accessibleContext = new AccessibleFormsLabel();
      }
      return this.accessibleContext;
    }






    
    private final class AccessibleFormsLabel
      extends JLabel.AccessibleJLabel
    {
      private AccessibleFormsLabel() {}






      
      public String getAccessibleName() {
        if (this.accessibleName != null) {
          return this.accessibleName;
        }
        String text = DefaultComponentFactory.FormsLabel.this.getText();
        if (text == null) {
          return super.getAccessibleName();
        }
        return text.endsWith(":") ? text.substring(0, text.length() - 1) : text;
      }
    }
  }



  
  private static final class ReadOnlyLabel
    extends FormsLabel
  {
    private ReadOnlyLabel() {}



    
    private static final String[] UIMANAGER_KEYS = new String[] { "Label.disabledForeground", "Label.disabledText", "Label[Disabled].textForeground", "textInactiveText" };





    
    public void updateUI() {
      super.updateUI();
      setForeground(getDisabledForeground());
    }

    
    private static Color getDisabledForeground() {
      for (String key : UIMANAGER_KEYS) {
        Color foreground = UIManager.getColor(key);
        if (foreground != null)
        {
          return foreground;
        }
      } 
      return null;
    }
  }






  
  private static final class TitleLabel
    extends FormsLabel
  {
    private TitleLabel() {}





    
    public void updateUI() {
      super.updateUI();
      Color foreground = getTitleColor();
      if (foreground != null) {
        setForeground(foreground);
      }
      setFont(getTitleFont());
    }
    
    private static Color getTitleColor() {
      return UIManager.getColor("TitledBorder.titleColor");
    }










    
    private static Font getTitleFont() {
      return FormUtils.isLafAqua() ? UIManager.getFont("Label.font").deriveFont(1) : UIManager.getFont("TitledBorder.font");
    }
  }







  
  private static final class TitledSeparatorLayout
    implements LayoutManager
  {
    private final boolean centerSeparators;






    
    private TitledSeparatorLayout(boolean centerSeparators) {
      this.centerSeparators = centerSeparators;
    }











    
    public void addLayoutComponent(String name, Component comp) {}










    
    public void removeLayoutComponent(Component comp) {}










    
    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }










    
    public Dimension preferredLayoutSize(Container parent) {
      Component label = getLabel(parent);
      Dimension labelSize = label.getPreferredSize();
      Insets insets = parent.getInsets();
      int width = labelSize.width + insets.left + insets.right;
      int height = labelSize.height + insets.top + insets.bottom;
      return new Dimension(width, height);
    }






    
    public void layoutContainer(Container parent) {
      synchronized (parent.getTreeLock()) {
        
        Dimension size = parent.getSize();
        Insets insets = parent.getInsets();
        int width = size.width - insets.left - insets.right;

        
        JLabel label = getLabel(parent);
        Dimension labelSize = label.getPreferredSize();
        int labelWidth = labelSize.width;
        int labelHeight = labelSize.height;
        Component separator1 = parent.getComponent(1);
        int separatorHeight = (separator1.getPreferredSize()).height;
        
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        int ascent = metrics.getMaxAscent();
        int hGapDlu = this.centerSeparators ? 3 : 1;
        int hGap = Sizes.dialogUnitXAsPixel(hGapDlu, label);
        int vOffset = this.centerSeparators ? (1 + (labelHeight - separatorHeight) / 2) : (ascent - separatorHeight / 2);


        
        int alignment = label.getHorizontalAlignment();
        int y = insets.top;
        if (alignment == 2) {
          int x = insets.left;
          label.setBounds(x, y, labelWidth, labelHeight);
          x += labelWidth;
          x += hGap;
          int separatorWidth = size.width - insets.right - x;
          separator1.setBounds(x, y + vOffset, separatorWidth, separatorHeight);
        } else if (alignment == 4) {
          int x = insets.left + width - labelWidth;
          label.setBounds(x, y, labelWidth, labelHeight);
          x -= hGap;
          x--;
          int separatorWidth = x - insets.left;
          separator1.setBounds(insets.left, y + vOffset, separatorWidth, separatorHeight);
        } else {
          int xOffset = (width - labelWidth - 2 * hGap) / 2;
          int x = insets.left;
          separator1.setBounds(x, y + vOffset, xOffset - 1, separatorHeight);
          x += xOffset;
          x += hGap;
          label.setBounds(x, y, labelWidth, labelHeight);
          x += labelWidth;
          x += hGap;
          Component separator2 = parent.getComponent(2);
          int separatorWidth = size.width - insets.right - x;
          separator2.setBounds(x, y + vOffset, separatorWidth, separatorHeight);
        } 
      } 
    }
    
    private static JLabel getLabel(Container parent) {
      return (JLabel)parent.getComponent(0);
    }
  }
}
