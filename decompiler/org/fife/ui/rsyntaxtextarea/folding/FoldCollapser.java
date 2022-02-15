package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;






















public class FoldCollapser
{
  private List<Integer> typesToCollapse;
  
  public FoldCollapser() {
    this(1);
  }








  
  public FoldCollapser(int typeToCollapse) {
    this.typesToCollapse = new ArrayList<>(3);
    addTypeToCollapse(typeToCollapse);
  }






  
  public void addTypeToCollapse(int typeToCollapse) {
    this.typesToCollapse.add(Integer.valueOf(typeToCollapse));
  }






  
  public void collapseFolds(FoldManager fm) {
    for (int i = 0; i < fm.getFoldCount(); i++) {
      Fold fold = fm.getFold(i);
      collapseImpl(fold);
    } 
  }








  
  protected void collapseImpl(Fold fold) {
    if (getShouldCollapse(fold)) {
      fold.setCollapsed(true);
    }
    for (int i = 0; i < fold.getChildCount(); i++) {
      collapseImpl(fold.getChild(i));
    }
  }







  
  public boolean getShouldCollapse(Fold fold) {
    int type = fold.getFoldType();
    for (Integer typeToCollapse : this.typesToCollapse) {
      if (type == typeToCollapse.intValue()) {
        return true;
      }
    } 
    return false;
  }
}
