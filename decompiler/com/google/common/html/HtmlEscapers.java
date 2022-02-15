package com.google.common.html;

import com.google.common.annotations.GwtCompatible;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;










































@GwtCompatible
public final class HtmlEscapers
{
  public static Escaper htmlEscaper() {
    return HTML_ESCAPER;
  }




  
  private static final Escaper HTML_ESCAPER = Escapers.builder()
    .addEscape('"', "&quot;")
    
    .addEscape('\'', "&#39;")
    .addEscape('&', "&amp;")
    .addEscape('<', "&lt;")
    .addEscape('>', "&gt;")
    .build();
}
