package org.mozilla.javascript.tools.debugger;

import org.mozilla.javascript.debug.DebuggableScript;

public interface SourceProvider {
  String getSource(DebuggableScript paramDebuggableScript);
}
