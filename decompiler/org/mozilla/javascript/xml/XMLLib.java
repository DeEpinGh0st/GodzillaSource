package org.mozilla.javascript.xml;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;



public abstract class XMLLib
{
  private static final Object XML_LIB_KEY = new Object();








  
  public static abstract class Factory
  {
    public static Factory create(final String className) {
      return new Factory()
        {
          public String getImplementationClassName() {
            return className;
          }
        };
    }

    
    public abstract String getImplementationClassName();
  }
  
  public static XMLLib extractFromScopeOrNull(Scriptable scope) {
    ScriptableObject so = ScriptRuntime.getLibraryScopeOrNull(scope);
    if (so == null)
    {
      return null;
    }


    
    ScriptableObject.getProperty((Scriptable)so, "XML");
    
    return (XMLLib)so.getAssociatedValue(XML_LIB_KEY);
  }

  
  public static XMLLib extractFromScope(Scriptable scope) {
    XMLLib lib = extractFromScopeOrNull(scope);
    if (lib != null) {
      return lib;
    }
    String msg = ScriptRuntime.getMessage0("msg.XML.not.available");
    throw Context.reportRuntimeError(msg);
  }

  
  protected final XMLLib bindToScope(Scriptable scope) {
    ScriptableObject so = ScriptRuntime.getLibraryScopeOrNull(scope);
    if (so == null)
    {
      throw new IllegalStateException();
    }
    return (XMLLib)so.associateValue(XML_LIB_KEY, this);
  }



  
  public abstract boolean isXMLName(Context paramContext, Object paramObject);



  
  public abstract Ref nameRef(Context paramContext, Object paramObject, Scriptable paramScriptable, int paramInt);



  
  public abstract Ref nameRef(Context paramContext, Object paramObject1, Object paramObject2, Scriptable paramScriptable, int paramInt);



  
  public abstract String escapeAttributeValue(Object paramObject);


  
  public abstract String escapeTextValue(Object paramObject);


  
  public abstract Object toDefaultXmlNamespace(Context paramContext, Object paramObject);


  
  public void setIgnoreComments(boolean b) {
    throw new UnsupportedOperationException();
  }
  
  public void setIgnoreWhitespace(boolean b) {
    throw new UnsupportedOperationException();
  }
  
  public void setIgnoreProcessingInstructions(boolean b) {
    throw new UnsupportedOperationException();
  }
  
  public void setPrettyPrinting(boolean b) {
    throw new UnsupportedOperationException();
  }
  
  public void setPrettyIndent(int i) {
    throw new UnsupportedOperationException();
  }
  
  public boolean isIgnoreComments() {
    throw new UnsupportedOperationException();
  }
  
  public boolean isIgnoreProcessingInstructions() {
    throw new UnsupportedOperationException();
  }
  
  public boolean isIgnoreWhitespace() {
    throw new UnsupportedOperationException();
  }
  
  public boolean isPrettyPrinting() {
    throw new UnsupportedOperationException();
  }
  
  public int getPrettyIndent() {
    throw new UnsupportedOperationException();
  }
}
