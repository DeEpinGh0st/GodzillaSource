package org.fife.rsta.ui.search;

import java.util.EventListener;

public interface SearchListener extends EventListener {
  void searchEvent(SearchEvent paramSearchEvent);
  
  String getSelectedText();
}
