package org.fife.rsta.ac.ts;

import org.fife.rsta.ac.js.JsDocCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;


















public class TypeScriptCompletionProvider
  extends LanguageAwareCompletionProvider
{
  private TypeScriptLanguageSupport languageSupport;
  
  public TypeScriptCompletionProvider(TypeScriptLanguageSupport languageSupport) {
    super((CompletionProvider)new SourceCompletionProvider());
    
    this.languageSupport = languageSupport;
    
    setDocCommentCompletionProvider((CompletionProvider)new JsDocCompletionProvider());
  }

  
  public TypeScriptLanguageSupport getLanguageSupport() {
    return this.languageSupport;
  }
}
