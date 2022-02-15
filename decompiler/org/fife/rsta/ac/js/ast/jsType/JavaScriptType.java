package org.fife.rsta.ac.js.ast.jsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSCompletion;




























public class JavaScriptType
{
  protected TypeDeclaration type;
  protected HashMap<String, JSCompletion> methodFieldCompletions;
  protected HashMap<String, JSCompletion> constructors;
  protected JSCompletion classType;
  private ArrayList<JavaScriptType> extended;
  
  public JavaScriptType(TypeDeclaration type) {
    this.type = type;
    this.methodFieldCompletions = new HashMap<>();
    this.constructors = new HashMap<>();
    this.extended = new ArrayList<>();
  }







  
  public void addCompletion(JSCompletion completion) {
    this.methodFieldCompletions.put(completion.getLookupName(), completion);
  }

  
  public JSCompletion removeCompletion(String completionLookup, SourceCompletionProvider provider) {
    JSCompletion completion = getCompletion(completionLookup, provider);
    if (completion != null) {
      removeCompletion(this, completion);
    }
    return completion;
  }






  
  private void removeCompletion(JavaScriptType type, JSCompletion completion) {
    if (type.methodFieldCompletions.containsKey(completion.getLookupName())) {
      type.methodFieldCompletions.remove(completion.getLookupName());
    }
    
    for (JavaScriptType extendedType : type.extended) {
      removeCompletion(extendedType, completion);
    }
  }




  
  public void addConstructor(JSCompletion completion) {
    this.constructors.put(completion.getLookupName(), completion);
  }

  
  public void removeConstructor(JSCompletion completion) {
    this.constructors.remove(completion.getLookupName());
  }




  
  public void setClassTypeCompletion(JSCompletion classType) {
    this.classType = classType;
  }



  
  public JSCompletion getClassTypeCompletion() {
    return this.classType;
  }






  
  public JSCompletion getCompletion(String completionLookup, SourceCompletionProvider provider) {
    return getCompletion(this, completionLookup, provider);
  }






  
  protected JSCompletion _getCompletion(String completionLookup, SourceCompletionProvider provider) {
    return this.methodFieldCompletions.get(completionLookup);
  }










  
  private static JSCompletion getCompletion(JavaScriptType cachedType, String completionLookup, SourceCompletionProvider provider) {
    JSCompletion completion = cachedType._getCompletion(completionLookup, provider);
    
    if (completion == null) {
      
      Iterator<JavaScriptType> i = cachedType.getExtendedClasses().iterator();
      while (i.hasNext()) {
        completion = getCompletion(i.next(), completionLookup, provider);
        if (completion != null)
          break; 
      } 
    } 
    return completion;
  }





  
  public HashMap<String, JSCompletion> getMethodFieldCompletions() {
    return this.methodFieldCompletions;
  }
  
  public HashMap<String, JSCompletion> getConstructorCompletions() {
    return this.constructors;
  }






  
  public TypeDeclaration getType() {
    return this.type;
  }







  
  public void addExtension(JavaScriptType type) {
    this.extended.add(type);
  }





  
  public List<JavaScriptType> getExtendedClasses() {
    return this.extended;
  }


  
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof JavaScriptType) {
      JavaScriptType ct = (JavaScriptType)o;
      return ct.getType().equals(getType());
    } 
    
    if (o instanceof TypeDeclaration) {
      TypeDeclaration dec = (TypeDeclaration)o;
      return dec.equals(getType());
    } 
    
    return false;
  }







  
  public int hashCode() {
    return getType().hashCode();
  }
}
