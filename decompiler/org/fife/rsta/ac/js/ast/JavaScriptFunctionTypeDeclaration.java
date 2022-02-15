package org.fife.rsta.ac.js.ast;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.mozilla.javascript.ast.AstNode;



public class JavaScriptFunctionTypeDeclaration
  extends JavaScriptVariableDeclaration
{
  private AstNode typeNode;
  
  public JavaScriptFunctionTypeDeclaration(String name, int offset, SourceCompletionProvider provider, CodeBlock block) {
    super(name, offset, provider, block);
  }

  
  public void setTypeDeclaration(AstNode typeNode) {
    this.typeNode = typeNode;
  }


  
  public TypeDeclaration getTypeDeclaration() {
    return this.provider.getJavaScriptEngine().getJavaScriptResolver(this.provider).resolveNode(this.typeNode);
  }
}
