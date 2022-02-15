package org.fife.rsta.ui.search;

import java.util.Vector;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ui.UIUtil;




























public class SearchComboBox
  extends RegexAwareComboBox<String>
{
  private FindToolBar toolBar;
  
  public SearchComboBox(FindToolBar toolBar, boolean replace) {
    super(replace);
    this.toolBar = toolBar;
    UIUtil.fixComboOrientation((JComboBox)this);
    updateTextFieldKeyMap();
  }












  
  public void addItem(String item) {
    int curIndex = getIndexOf(item);
    if (curIndex == -1) {
      super.addItem(item);
    }
    else if (curIndex > 0) {
      removeItem(item);
      insertItemAt(item, 0);
    } 

    
    setSelectedIndex(0);
  }

  
  private int getIndexOf(String item) {
    for (int i = 0; i < this.dataModel.getSize(); i++) {
      if (((String)this.dataModel.getElementAt(i)).equals(item)) {
        return i;
      }
    } 
    return -1;
  }






  
  public String getSelectedString() {
    JTextComponent comp = UIUtil.getTextComponent((JComboBox)this);
    return comp.getText();
  }












  
  public Vector<String> getSearchStrings() {
    int selectedIndex = getSelectedIndex();
    if (selectedIndex == -1) {
      addItem(getSelectedString());


    
    }
    else if (selectedIndex > 0) {
      String item = (String)getSelectedItem();
      removeItem(item);
      insertItemAt(item, 0);
      setSelectedIndex(0);
    } 
    
    int itemCount = getItemCount();
    Vector<String> vector = new Vector<>(itemCount);
    for (int i = 0; i < itemCount; i++) {
      vector.add(getItemAt(i));
    }
    
    return vector;
  }





  
  private void updateTextFieldKeyMap() {
    JTextComponent comp = UIUtil.getTextComponent((JComboBox)this);

    
    InputMap im = comp.getInputMap();
    im.put(KeyStroke.getKeyStroke("ctrl H"), "none");
  }


  
  public void updateUI() {
    super.updateUI();
    if (this.toolBar != null) {
      this.toolBar.searchComboUpdateUICallback(this);
    }
    updateTextFieldKeyMap();
  }
}
