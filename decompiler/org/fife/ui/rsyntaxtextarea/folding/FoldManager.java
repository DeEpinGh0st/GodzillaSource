package org.fife.ui.rsyntaxtextarea.folding;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface FoldManager {
  public static final String PROPERTY_FOLDS_UPDATED = "FoldsUpdated";
  
  void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  void clear();
  
  boolean ensureOffsetNotInClosedFold(int paramInt);
  
  Fold getDeepestFoldContaining(int paramInt);
  
  Fold getDeepestOpenFoldContaining(int paramInt);
  
  Fold getFold(int paramInt);
  
  int getFoldCount();
  
  Fold getFoldForLine(int paramInt);
  
  int getHiddenLineCount();
  
  int getHiddenLineCountAbove(int paramInt);
  
  int getHiddenLineCountAbove(int paramInt, boolean paramBoolean);
  
  int getLastVisibleLine();
  
  int getVisibleLineAbove(int paramInt);
  
  int getVisibleLineBelow(int paramInt);
  
  boolean isCodeFoldingEnabled();
  
  boolean isCodeFoldingSupportedAndEnabled();
  
  boolean isFoldStartLine(int paramInt);
  
  boolean isLineHidden(int paramInt);
  
  void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  void reparse();
  
  void setCodeFoldingEnabled(boolean paramBoolean);
  
  void setFolds(List<Fold> paramList);
}
