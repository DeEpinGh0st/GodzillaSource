package org.fife.rsta.ac.js.resolver;

import java.io.IOException;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.Logger;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSMethodData;
import org.fife.ui.autocomplete.CompletionProvider;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;









public class JSR223JavaScriptCompletionResolver
  extends JavaScriptCompletionResolver
{
  public JSR223JavaScriptCompletionResolver(SourceCompletionProvider provider) {
    super(provider);
  }





  
  protected TypeDeclaration resolveNativeType(AstNode node) {
    TypeDeclaration dec = super.resolveNativeType(node);
    if (dec == null) {
      dec = testJavaStaticType(node);
    }
    
    return dec;
  }




  
  public String getLookupText(JSMethodData methodData, String name) {
    StringBuilder sb = new StringBuilder(name);
    sb.append('(');
    int count = methodData.getParameterCount();
    
    String[] parameterTypes = methodData.getMethodInfo().getParameterTypes();
    for (int i = 0; i < count; i++) {
      String paramName = methodData.getParameterType(parameterTypes, i, (CompletionProvider)this.provider);
      sb.append(paramName);
      if (i < count - 1) {
        sb.append(",");
      }
    } 
    sb.append(')');
    return sb.toString();
  }




  
  public String getFunctionNameLookup(FunctionCall call, SourceCompletionProvider provider) {
    if (call != null) {
      StringBuilder sb = new StringBuilder();
      if (call.getTarget() instanceof PropertyGet) {
        PropertyGet get = (PropertyGet)call.getTarget();
        sb.append(get.getProperty().getIdentifier());
      } 
      sb.append("(");
      int count = call.getArguments().size();
      
      for (int i = 0; i < count; i++) {
        AstNode paramNode = call.getArguments().get(i);
        JavaScriptResolver resolver = provider.getJavaScriptEngine().getJavaScriptResolver(provider);
        Logger.log("PARAM: " + JavaScriptHelper.convertNodeToSource(paramNode));
        
        try {
          TypeDeclaration type = resolver.resolveParamNode(JavaScriptHelper.convertNodeToSource(paramNode));
          String resolved = (type != null) ? type.getQualifiedName() : "any";
          sb.append(resolved);
          if (i < count - 1) {
            sb.append(",");
          }
        } catch (IOException io) {
          io.printStackTrace();
        } 
      }  sb.append(")");
      return sb.toString();
    } 
    return null;
  }








  
  protected TypeDeclaration findJavaStaticType(AstNode node) {
    String testName = null;
    if (node.getParent() != null && node
      .getParent().getType() == 33) {

      
      String name = node.toSource();
      
      try {
        String longName = node.getParent().toSource();
        
        if (longName.indexOf('[') == -1 && longName.indexOf(']') == -1 && longName
          .indexOf('(') == -1 && longName.indexOf(')') == -1)
        {
          
          int index = longName.lastIndexOf(name);
          if (index > -1) {
            testName = longName.substring(0, index + name.length());
          }
        }
      
      } catch (Exception e) {
        
        Logger.log(e.getMessage());
      } 
    } else {
      
      testName = node.toSource();
    } 
    
    if (testName != null) {
      TypeDeclaration dec = JavaScriptHelper.getTypeDeclaration(testName, this.provider);
      
      if (dec == null) {
        dec = JavaScriptHelper.createNewTypeDeclaration(testName);
      }
      ClassFile cf = this.provider.getJavaScriptTypesFactory().getClassFile(this.provider
          .getJarManager(), dec);
      if (cf != null) {
        
        TypeDeclaration returnDec = this.provider.getJavaScriptTypesFactory().createNewTypeDeclaration(cf, true, false);
        return returnDec;
      } 
    } 
    return null;
  }
}
