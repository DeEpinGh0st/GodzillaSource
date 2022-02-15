package org.fife.rsta.ui;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;




































public class RComboBoxModel<E>
  extends DefaultComboBoxModel<E>
{
  private static final long serialVersionUID = 1L;
  private int maxNumElements;
  
  public RComboBoxModel() {
    setMaxNumElements(8);
  }







  
  public RComboBoxModel(E[] items) {
    super(items);
    setMaxNumElements(8);
  }







  
  public RComboBoxModel(Vector<E> v) {
    super(v);
    setMaxNumElements(8);
  }








  
  public void addElement(E anObject) {
    insertElementAt(anObject, 0);
  }




  
  private void ensureValidItemCount() {
    while (getSize() > this.maxNumElements) {
      removeElementAt(getSize() - 1);
    }
  }






  
  public int getMaxNumElements() {
    return this.maxNumElements;
  }











  
  public void insertElementAt(E anObject, int index) {
    int oldPos = getIndexOf(anObject);
    if (oldPos == index) {
      return;
    }
    if (oldPos > -1) {
      removeElement(anObject);
    }
    
    super.insertElementAt(anObject, index);
    ensureValidItemCount();
  }









  
  public void setMaxNumElements(int numElements) {
    this.maxNumElements = (numElements <= 0) ? 4 : numElements;
    ensureValidItemCount();
  }
}
