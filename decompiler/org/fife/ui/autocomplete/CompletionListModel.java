package org.fife.ui.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractListModel;




























class CompletionListModel
  extends AbstractListModel<Completion>
{
  private List<Completion> delegate = new ArrayList<>();








  
  public void clear() {
    int end = this.delegate.size() - 1;
    this.delegate.clear();
    if (end >= 0) {
      fireIntervalRemoved(this, 0, end);
    }
  }


  
  public Completion getElementAt(int index) {
    return this.delegate.get(index);
  }


  
  public int getSize() {
    return this.delegate.size();
  }






  
  public void setContents(Collection<Completion> contents) {
    clear();
    int count = contents.size();
    if (count > 0) {
      this.delegate.addAll(contents);
      fireIntervalAdded(this, 0, count - 1);
    } 
  }
}
