package org.mozilla.javascript.tools.debugger;

import org.mozilla.javascript.Scriptable;

public interface ScopeProvider {
  Scriptable getScope();
}
