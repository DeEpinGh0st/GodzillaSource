package org.fife.ui.rsyntaxtextarea;

import java.util.EventListener;

public interface ActiveLineRangeListener extends EventListener {
  void activeLineRangeChanged(ActiveLineRangeEvent paramActiveLineRangeEvent);
}
