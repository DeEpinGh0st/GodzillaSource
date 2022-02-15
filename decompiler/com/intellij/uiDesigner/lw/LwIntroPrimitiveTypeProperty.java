package com.intellij.uiDesigner.lw;

import org.jdom.Element;



public final class LwIntroPrimitiveTypeProperty
  extends LwIntrospectedProperty
{
  private final Class myValueClass;
  
  public LwIntroPrimitiveTypeProperty(String name, Class valueClass) {
    super(name, valueClass.getName());
    this.myValueClass = valueClass;
  }
  
  public Object read(Element element) throws Exception {
    return LwXmlReader.getRequiredPrimitiveTypeValue(element, "value", this.myValueClass);
  }
}
