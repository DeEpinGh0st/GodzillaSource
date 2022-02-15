package org.fife.rsta.ac.js;

import org.fife.rsta.ac.ShorthandCompletionCache;
import org.fife.rsta.ac.java.JarManager;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.mozilla.javascript.ast.AstRoot;



























public class JavaScriptCompletionProvider
  extends LanguageAwareCompletionProvider
{
  private AstRoot astRoot;
  private SourceCompletionProvider sourceProvider;
  private JavaScriptLanguageSupport languageSupport;
  
  public JavaScriptCompletionProvider(JarManager jarManager, JavaScriptLanguageSupport languageSupport) {
    this(new SourceCompletionProvider(languageSupport.isXmlAvailable()), jarManager, languageSupport);
  }



  
  public JavaScriptCompletionProvider(SourceCompletionProvider provider, JarManager jarManager, JavaScriptLanguageSupport ls) {
    super((CompletionProvider)provider);
    this.sourceProvider = (SourceCompletionProvider)getDefaultCompletionProvider();
    this.sourceProvider.setJarManager(jarManager);
    this.languageSupport = ls;
    
    setShorthandCompletionCache(new JavaScriptShorthandCompletionCache(this.sourceProvider, new DefaultCompletionProvider(), ls
          .isXmlAvailable()));
    this.sourceProvider.setParent(this);
    
    setDocCommentCompletionProvider((CompletionProvider)new JsDocCompletionProvider());
  }






  
  public synchronized AstRoot getASTRoot() {
    return this.astRoot;
  }

  
  public JarManager getJarManager() {
    return ((SourceCompletionProvider)getDefaultCompletionProvider())
      .getJarManager();
  }
  
  public JavaScriptLanguageSupport getLanguageSupport() {
    return this.languageSupport;
  }

  
  public SourceCompletionProvider getProvider() {
    return this.sourceProvider;
  }




  
  public void setShorthandCompletionCache(ShorthandCompletionCache shorthandCache) {
    this.sourceProvider.setShorthandCache(shorthandCache);
    
    setCommentCompletions(shorthandCache);
  }




  
  private void setCommentCompletions(ShorthandCompletionCache shorthandCache) {
    AbstractCompletionProvider provider = shorthandCache.getCommentProvider();
    if (provider != null) {
      for (Completion c : shorthandCache.getCommentCompletions()) {
        provider.addCompletion(c);
      }
      setCommentCompletionProvider((CompletionProvider)provider);
    } 
  }






  
  public synchronized void setASTRoot(AstRoot root) {
    this.astRoot = root;
  }

  
  protected synchronized void reparseDocument(int offset) {
    this.sourceProvider.parseDocument(offset);
  }
}
