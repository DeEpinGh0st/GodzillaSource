package org.fife.rsta.ac.js.ast.type;















public class ArrayTypeDeclaration
  extends TypeDeclaration
{
  private TypeDeclaration arrayType;
  
  public ArrayTypeDeclaration(String pkg, String apiName, String jsName, boolean staticsOnly) {
    super(pkg, apiName, jsName, staticsOnly);
  }

  
  public ArrayTypeDeclaration(String pkg, String apiName, String jsName) {
    super(pkg, apiName, jsName);
  }

  
  public TypeDeclaration getArrayType() {
    return this.arrayType;
  }

  
  public void setArrayType(TypeDeclaration containerType) {
    this.arrayType = containerType;
  }


  
  public boolean equals(Object obj) {
    boolean equals = super.equals(obj);
    
    if (equals) {
      
      ArrayTypeDeclaration objArrayType = (ArrayTypeDeclaration)obj;
      
      if (getArrayType() == null && objArrayType.getArrayType() == null) {
        return false;
      }
      
      if (getArrayType() == null && objArrayType.getArrayType() != null) {
        return false;
      }
      
      if (getArrayType() != null && objArrayType.getArrayType() == null) {
        return false;
      }
      
      return getArrayType().equals(((ArrayTypeDeclaration)obj)
          .getArrayType());
    } 
    
    return equals;
  }
}
