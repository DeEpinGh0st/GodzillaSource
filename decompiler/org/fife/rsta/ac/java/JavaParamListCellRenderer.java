package org.fife.rsta.ac.java;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JList;























public class JavaParamListCellRenderer
  extends JavaCellRenderer
{
  public JavaParamListCellRenderer() {
    setSimpleText(true);
  }








  
  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.width += 32;
    return d;
  }












  
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
    super.getListCellRendererComponent(list, value, index, selected, hasFocus);
    
    JavaSourceCompletion ajsc = (JavaSourceCompletion)value;
    setIcon(ajsc.getIcon());
    return this;
  }
}
