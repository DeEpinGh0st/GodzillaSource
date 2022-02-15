package org.fife.ui.autocomplete;

import java.util.EventListener;

public interface AutoCompletionListener extends EventListener {
  void autoCompleteUpdate(AutoCompletionEvent paramAutoCompletionEvent);
}
