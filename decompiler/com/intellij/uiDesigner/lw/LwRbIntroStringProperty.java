package com.intellij.uiDesigner.lw;

import org.jdom.Element;


















public final class LwRbIntroStringProperty
  extends LwIntrospectedProperty
{
  public LwRbIntroStringProperty(String name) {
    super(name, String.class.getName());
  }



  
  public Object read(Element element) throws Exception {
    StringDescriptor descriptor = LwXmlReader.getStringDescriptor(element, "value", "resource-bundle", "key");


    
    if (descriptor == null) {
      throw new IllegalArgumentException("String descriptor value required");
    }
    return descriptor;
  }
}
