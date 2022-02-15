package com.intellij.uiDesigner.lw;

import java.util.List;
import org.jdom.Element;




















public class LwIntroListModelProperty
  extends LwIntrospectedProperty
{
  public LwIntroListModelProperty(String name, String propertyClassName) {
    super(name, propertyClassName);
  }
  
  public Object read(Element element) throws Exception {
    List list = element.getChildren("item", element.getNamespace());
    String[] result = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {
      Element itemElement = list.get(i);
      result[i] = LwXmlReader.getRequiredString(itemElement, "value");
    } 
    return result;
  }
}
