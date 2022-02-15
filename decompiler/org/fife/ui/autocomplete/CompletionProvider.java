package org.fife.ui.autocomplete;

import java.awt.Point;
import java.util.List;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;

public interface CompletionProvider {
  void clearParameterizedCompletionParams();
  
  String getAlreadyEnteredText(JTextComponent paramJTextComponent);
  
  List<Completion> getCompletions(JTextComponent paramJTextComponent);
  
  List<Completion> getCompletionsAt(JTextComponent paramJTextComponent, Point paramPoint);
  
  ListCellRenderer<Object> getListCellRenderer();
  
  ParameterChoicesProvider getParameterChoicesProvider();
  
  List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent paramJTextComponent);
  
  char getParameterListEnd();
  
  String getParameterListSeparator();
  
  char getParameterListStart();
  
  CompletionProvider getParent();
  
  boolean isAutoActivateOkay(JTextComponent paramJTextComponent);
  
  void setListCellRenderer(ListCellRenderer<Object> paramListCellRenderer);
  
  void setParameterizedCompletionParams(char paramChar1, String paramString, char paramChar2);
  
  void setParent(CompletionProvider paramCompletionProvider);
}
