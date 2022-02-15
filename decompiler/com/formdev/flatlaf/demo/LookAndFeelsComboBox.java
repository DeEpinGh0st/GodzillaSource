package com.formdev.flatlaf.demo;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.MutableComboBoxModel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;





















public class LookAndFeelsComboBox
  extends JComboBox<UIManager.LookAndFeelInfo>
{
  private final PropertyChangeListener lafListener = this::lafChanged;

  
  public LookAndFeelsComboBox() {
    setRenderer(new BasicComboBoxRenderer()
        {



          
          public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
          {
            value = (value != null) ? ((UIManager.LookAndFeelInfo)value).getName() : UIManager.getLookAndFeel().getName();
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          }
        });
  }
  
  public void addLookAndFeel(String name, String className) {
    getMutableModel().addElement(new UIManager.LookAndFeelInfo(name, className));
  }
  
  public String getSelectedLookAndFeel() {
    Object sel = getSelectedItem();
    return (sel instanceof UIManager.LookAndFeelInfo) ? ((UIManager.LookAndFeelInfo)sel).getClassName() : null;
  }
  
  public void setSelectedLookAndFeel(String className) {
    setSelectedIndex(getIndexOfLookAndFeel(className));
  }
  
  public void selectedCurrentLookAndFeel() {
    setSelectedLookAndFeel(UIManager.getLookAndFeel().getClass().getName());
  }
  
  public void removeLookAndFeel(String className) {
    int index = getIndexOfLookAndFeel(className);
    if (index >= 0)
      getMutableModel().removeElementAt(index); 
  }
  
  public int getIndexOfLookAndFeel(String className) {
    ComboBoxModel<UIManager.LookAndFeelInfo> model = getModel();
    int size = model.getSize();
    for (int i = 0; i < size; i++) {
      if (className.equals(((UIManager.LookAndFeelInfo)model.getElementAt(i)).getClassName()))
        return i; 
    } 
    return -1;
  }
  
  private MutableComboBoxModel<UIManager.LookAndFeelInfo> getMutableModel() {
    return (MutableComboBoxModel<UIManager.LookAndFeelInfo>)getModel();
  }

  
  public void addNotify() {
    super.addNotify();
    
    selectedCurrentLookAndFeel();
    UIManager.addPropertyChangeListener(this.lafListener);
  }

  
  public void removeNotify() {
    super.removeNotify();
    
    UIManager.removePropertyChangeListener(this.lafListener);
  }
  
  void lafChanged(PropertyChangeEvent e) {
    if ("lookAndFeel".equals(e.getPropertyName()))
      selectedCurrentLookAndFeel(); 
  }
}
