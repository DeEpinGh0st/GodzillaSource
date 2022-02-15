package com.jgoodies.forms.factories;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

public interface ComponentFactory {
  JButton createButton(Action paramAction);
  
  JLabel createLabel(String paramString);
  
  JLabel createReadOnlyLabel(String paramString);
  
  JLabel createTitle(String paramString);
  
  JLabel createHeaderLabel(String paramString);
  
  JComponent createSeparator(String paramString, int paramInt);
}
