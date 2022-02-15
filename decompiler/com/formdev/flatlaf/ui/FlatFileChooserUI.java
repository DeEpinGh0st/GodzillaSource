package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

















































































































public class FlatFileChooserUI
  extends MetalFileChooserUI
{
  private final FlatFileView fileView = new FlatFileView();
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatFileChooserUI((JFileChooser)c);
  }
  
  public FlatFileChooserUI(JFileChooser filechooser) {
    super(filechooser);
  }

  
  public void installComponents(JFileChooser fc) {
    super.installComponents(fc);
    
    patchUI(fc);
  }

  
  private void patchUI(JFileChooser fc) {
    Component topPanel = fc.getComponent(0);
    if (topPanel instanceof JPanel && ((JPanel)topPanel)
      .getLayout() instanceof java.awt.BorderLayout) {
      
      Component topButtonPanel = ((JPanel)topPanel).getComponent(0);
      if (topButtonPanel instanceof JPanel && ((JPanel)topButtonPanel)
        .getLayout() instanceof javax.swing.BoxLayout) {
        
        Insets margin = UIManager.getInsets("Button.margin");
        Component[] comps = ((JPanel)topButtonPanel).getComponents();
        for (int i = comps.length - 1; i >= 0; i--) {
          Component c = comps[i];
          if (c instanceof javax.swing.JButton || c instanceof javax.swing.JToggleButton) {
            AbstractButton b = (AbstractButton)c;
            b.putClientProperty("JButton.buttonType", "toolBarButton");
            
            b.setMargin(margin);
            b.setFocusable(false);
          } else if (c instanceof javax.swing.Box.Filler) {
            ((JPanel)topButtonPanel).remove(i);
          } 
        } 
      } 
    } 
    
    try {
      Component directoryComboBox = ((JPanel)topPanel).getComponent(2);
      if (directoryComboBox instanceof JComboBox) {
        int maximumRowCount = UIManager.getInt("ComboBox.maximumRowCount");
        if (maximumRowCount > 0)
          ((JComboBox)directoryComboBox).setMaximumRowCount(maximumRowCount); 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
  }



  
  public Dimension getPreferredSize(JComponent c) {
    return UIScale.scale(super.getPreferredSize(c));
  }

  
  public Dimension getMinimumSize(JComponent c) {
    return UIScale.scale(super.getMinimumSize(c));
  }

  
  public FileView getFileView(JFileChooser fc) {
    return this.fileView;
  }

  
  public void clearIconCache() {
    this.fileView.clearIconCache();
  }

  
  private class FlatFileView
    extends BasicFileChooserUI.BasicFileView
  {
    private FlatFileView() {}
    
    public Icon getIcon(File f) {
      ScaledImageIcon scaledImageIcon;
      Icon icon = getCachedIcon(f);
      if (icon != null) {
        return icon;
      }
      
      if (f != null) {
        icon = FlatFileChooserUI.this.getFileChooser().getFileSystemView().getSystemIcon(f);
        
        if (icon != null) {
          if (icon instanceof ImageIcon)
            scaledImageIcon = new ScaledImageIcon((ImageIcon)icon); 
          cacheIcon(f, (Icon)scaledImageIcon);
          return (Icon)scaledImageIcon;
        } 
      } 

      
      icon = super.getIcon(f);
      
      if (icon instanceof ImageIcon) {
        scaledImageIcon = new ScaledImageIcon((ImageIcon)icon);
        cacheIcon(f, (Icon)scaledImageIcon);
      } 
      
      return (Icon)scaledImageIcon;
    }
  }
}
