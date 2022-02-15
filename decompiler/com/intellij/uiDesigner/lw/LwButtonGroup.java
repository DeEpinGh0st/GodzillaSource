package com.intellij.uiDesigner.lw;

import java.util.ArrayList;
import java.util.Iterator;
import org.jdom.Element;



















public class LwButtonGroup
  implements IButtonGroup
{
  private String myName;
  private ArrayList myComponentIds = new ArrayList();
  private boolean myBound;
  
  public void read(Element element) {
    this.myName = element.getAttributeValue("name");
    this.myBound = LwXmlReader.getOptionalBoolean(element, "bound", false);
    for (Iterator i = element.getChildren().iterator(); i.hasNext(); ) {
      Element child = i.next();
      this.myComponentIds.add(child.getAttributeValue("id"));
    } 
  }
  
  public String getName() {
    return this.myName;
  }
  
  public String[] getComponentIds() {
    return (String[])this.myComponentIds.toArray((Object[])new String[this.myComponentIds.size()]);
  }
  
  public boolean isBound() {
    return this.myBound;
  }
}
