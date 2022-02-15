package com.intellij.uiDesigner.lw;

import org.jdom.Element;


















public abstract class LwIntrospectedProperty
  implements IProperty
{
  private final String myName;
  private final String myPropertyClassName;
  private String myDeclaringClassName;
  
  public LwIntrospectedProperty(String name, String propertyClassName) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    if (propertyClassName == null) {
      throw new IllegalArgumentException("propertyClassName cannot be null");
    }
    
    this.myName = name;
    this.myPropertyClassName = propertyClassName;
  }



  
  public final String getName() {
    return this.myName;
  }



  
  public final String getPropertyClassName() {
    return this.myPropertyClassName;
  }
  
  public final String getReadMethodName() {
    return "get" + Character.toUpperCase(this.myName.charAt(0)) + this.myName.substring(1);
  }
  
  public final String getWriteMethodName() {
    return "set" + Character.toUpperCase(this.myName.charAt(0)) + this.myName.substring(1);
  }
  
  public String getDeclaringClassName() {
    return this.myDeclaringClassName;
  }
  
  public void setDeclaringClassName(String definingClassName) {
    this.myDeclaringClassName = definingClassName;
  }





  
  public abstract Object read(Element paramElement) throws Exception;




  
  public Object getPropertyValue(IComponent component) {
    return ((LwComponent)component).getPropertyValue(this);
  }
  
  public String getCodeGenPropertyClassName() {
    return getPropertyClassName();
  }
}
