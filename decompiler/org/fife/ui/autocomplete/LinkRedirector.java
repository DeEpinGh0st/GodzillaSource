package org.fife.ui.autocomplete;

import java.net.URL;

public interface LinkRedirector {
  URL possiblyRedirect(URL paramURL);
}
