package org.fife.ui.rsyntaxtextarea.parser;

import java.net.URL;






























public abstract class AbstractParser
  implements Parser
{
  private boolean enabled;
  private ExtendedHyperlinkListener linkListener;
  
  protected AbstractParser() {
    setEnabled(true);
  }


  
  public ExtendedHyperlinkListener getHyperlinkListener() {
    return this.linkListener;
  }








  
  public URL getImageBase() {
    return null;
  }


  
  public boolean isEnabled() {
    return this.enabled;
  }







  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }







  
  public void setHyperlinkListener(ExtendedHyperlinkListener listener) {
    this.linkListener = listener;
  }
}
