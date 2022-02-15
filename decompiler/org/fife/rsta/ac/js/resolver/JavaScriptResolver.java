package org.fife.rsta.ac.js.resolver;

import java.io.IOException;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptType;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSMethodData;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;








public abstract class JavaScriptResolver
{
  protected SourceCompletionProvider provider;
  
  public JavaScriptResolver(SourceCompletionProvider provider) {
    this.provider = provider;
  }
  
  public abstract TypeDeclaration resolveNode(AstNode paramAstNode);
  
  public abstract TypeDeclaration resolveParamNode(String paramString) throws IOException;
  
  public abstract JavaScriptType compileText(String paramString) throws IOException;
  
  protected abstract TypeDeclaration resolveNativeType(AstNode paramAstNode);
  
  public abstract String getLookupText(JSMethodData paramJSMethodData, String paramString);
  
  public abstract String getFunctionNameLookup(FunctionCall paramFunctionCall, SourceCompletionProvider paramSourceCompletionProvider);
}
