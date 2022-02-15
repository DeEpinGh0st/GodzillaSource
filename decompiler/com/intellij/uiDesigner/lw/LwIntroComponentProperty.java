package com.intellij.uiDesigner.lw;

import org.jdom.Element;


















public class LwIntroComponentProperty
  extends LwIntrospectedProperty
{
  public LwIntroComponentProperty(String name, String propertyClassName) {
    super(name, propertyClassName);
  }
  
  public Object read(Element element) throws Exception {
    return LwXmlReader.getRequiredString(element, "value");
  }
}
