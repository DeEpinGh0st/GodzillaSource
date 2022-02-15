package org.fife.rsta.ac.java.rjc.lang;

import java.util.ArrayList;
import java.util.List;






























public class Type
{
  private List<String> identifiers = new ArrayList<>(1);
  private List<List<TypeArgument>> typeArguments = new ArrayList<>(1);
  
  private int bracketPairCount;
  
  public Type(String identifier) {
    this();
    addIdentifier(identifier, null);
  }
  public Type() {}
  
  public Type(String identifier, int bracketPairCount) {
    this();
    addIdentifier(identifier, null);
    setBracketPairCount(bracketPairCount);
  }








  
  public void addIdentifier(String identifier, List<TypeArgument> typeArgs) {
    this.identifiers.add(identifier);
    this.typeArguments.add(typeArgs);
  }

  
  public int getIdentifierCount() {
    return this.identifiers.size();
  }










  
  public String getName(boolean fullyQualified) {
    return getName(fullyQualified, true);
  }












  
  public String getName(boolean fullyQualified, boolean addTypeArgs) {
    StringBuilder sb = new StringBuilder();
    
    int count = this.identifiers.size();
    int start = fullyQualified ? 0 : (count - 1); int i;
    for (i = start; i < count; i++) {
      sb.append(this.identifiers.get(i));
      if (addTypeArgs && this.typeArguments.get(i) != null) {
        List<TypeArgument> typeArgs = this.typeArguments.get(i);
        int typeArgCount = typeArgs.size();
        if (typeArgCount > 0) {
          sb.append('<');
          for (int j = 0; j < typeArgCount; j++) {
            TypeArgument typeArg = typeArgs.get(j);
            
            sb.append(typeArg.toString());
            if (j < typeArgCount - 1) {
              sb.append(", ");
            }
          } 
          sb.append('>');
        } 
      } 
      if (i < count - 1) {
        sb.append('.');
      }
    } 
    
    for (i = 0; i < this.bracketPairCount; i++) {
      sb.append("[]");
    }
    
    return sb.toString();
  }


  
  public List<TypeArgument> getTypeArguments(int index) {
    return this.typeArguments.get(index);
  }





  
  public void incrementBracketPairCount(int count) {
    this.bracketPairCount += count;
  }






  
  public boolean isArray() {
    return (this.bracketPairCount > 0);
  }

  
  public boolean isBasicType() {
    boolean basicType = false;
    if (!isArray() && this.identifiers.size() == 1 && this.typeArguments.get(0) == null) {
      String str = this.identifiers.get(0);





      
      basicType = ("byte".equals(str) || "float".equals(str) || "double".equals(str) || "int".equals(str) || "short".equals(str) || "long".equals(str) || "boolean".equals(str));
    } 
    return basicType;
  }

  
  public void setBracketPairCount(int count) {
    this.bracketPairCount = count;
  }









  
  public String toString() {
    return getName(true);
  }
}
