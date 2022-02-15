package org.fife.rsta.ac.js.engine;

import java.util.List;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;












public abstract class JavaScriptEngine
{
  private TypeDeclarationFactory typesFactory = new TypeDeclarationFactory();

  
  protected JavaScriptTypesFactory jsFactory;

  
  public List<String> setTypeDeclarationVersion(String ecmaVersion, boolean xmlSupported, boolean client) {
    return this.typesFactory.setTypeDeclarationVersion(ecmaVersion, xmlSupported, client);
  }
  
  public TypeDeclarationFactory getTypesFactory() {
    return this.typesFactory;
  }
  
  public abstract JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider paramSourceCompletionProvider);
  
  public abstract JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider paramSourceCompletionProvider);
  
  public abstract JavaScriptParser getParser(SourceCompletionProvider paramSourceCompletionProvider, int paramInt, TypeDeclarationOptions paramTypeDeclarationOptions);
}
