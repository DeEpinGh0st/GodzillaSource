package org.fife.ui.rsyntaxtextarea.templates;






























public abstract class AbstractCodeTemplate
  implements CodeTemplate
{
  private String id;
  
  public AbstractCodeTemplate() {}
  
  public AbstractCodeTemplate(String id) {
    setID(id);
  }









  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError("CodeTemplate implementation not Cloneable: " + 
          
          getClass().getName());
    } 
  }













  
  public int compareTo(CodeTemplate o) {
    if (o == null) {
      return -1;
    }
    return getID().compareTo(o.getID());
  }








  
  public boolean equals(Object obj) {
    if (obj instanceof CodeTemplate) {
      return (compareTo((CodeTemplate)obj) == 0);
    }
    return false;
  }








  
  public String getID() {
    return this.id;
  }







  
  public int hashCode() {
    return this.id.hashCode();
  }








  
  public void setID(String id) {
    if (id == null) {
      throw new IllegalArgumentException("id cannot be null");
    }
    this.id = id;
  }
}
