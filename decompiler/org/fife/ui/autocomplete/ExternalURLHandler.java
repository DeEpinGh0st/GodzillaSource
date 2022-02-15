package org.fife.ui.autocomplete;

import javax.swing.event.HyperlinkEvent;

public interface ExternalURLHandler {
  void urlClicked(HyperlinkEvent paramHyperlinkEvent, Completion paramCompletion, DescWindowCallback paramDescWindowCallback);
}
