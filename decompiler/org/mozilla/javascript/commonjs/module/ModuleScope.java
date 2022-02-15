package org.mozilla.javascript.commonjs.module;

import java.net.URI;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;











public class ModuleScope
  extends TopLevel
{
  private static final long serialVersionUID = 1L;
  private final URI uri;
  private final URI base;
  
  public ModuleScope(Scriptable prototype, URI uri, URI base) {
    this.uri = uri;
    this.base = base;
    setPrototype(prototype);
    cacheBuiltins();
  }
  
  public URI getUri() {
    return this.uri;
  }
  
  public URI getBase() {
    return this.base;
  }
}
