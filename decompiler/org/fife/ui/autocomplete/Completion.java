package org.fife.ui.autocomplete;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

public interface Completion extends Comparable<Completion> {
  int compareTo(Completion paramCompletion);
  
  String getAlreadyEntered(JTextComponent paramJTextComponent);
  
  Icon getIcon();
  
  String getInputText();
  
  CompletionProvider getProvider();
  
  int getRelevance();
  
  String getReplacementText();
  
  String getSummary();
  
  String getToolTipText();
}
