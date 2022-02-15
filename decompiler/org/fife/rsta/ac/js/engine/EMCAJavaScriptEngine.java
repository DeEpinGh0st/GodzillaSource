package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptAstParser;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.resolver.JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class EMCAJavaScriptEngine
  extends JavaScriptEngine
{
  public static final String EMCA_ENGINE = "EMCA";
  
  public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
    return (JavaScriptResolver)new JavaScriptCompletionResolver(provider);
  }

  
  public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {
    if (this.jsFactory == null) {
      this.jsFactory = JavaScriptTypesFactory.getDefaultJavaScriptTypesFactory(provider.getTypesFactory());
    }
    return this.jsFactory;
  }


  
  public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options) {
    return (JavaScriptParser)new JavaScriptAstParser(provider, dot, options);
  }
}
