package org.fife.rsta.ac.js.ast.parser;

import java.util.Set;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.ui.autocomplete.Completion;
import org.mozilla.javascript.ast.AstRoot;










public abstract class JavaScriptParser
{
  protected SourceCompletionProvider provider;
  protected int dot;
  protected TypeDeclarationOptions options;
  
  public JavaScriptParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options) {
    this.provider = provider;
    this.dot = dot;
    this.options = options;
  }







  
  public abstract CodeBlock convertAstNodeToCodeBlock(AstRoot paramAstRoot, Set<Completion> paramSet, String paramString);






  
  public boolean isPreProcessing() {
    return (this.options != null && this.options.isPreProcessing());
  }
}
