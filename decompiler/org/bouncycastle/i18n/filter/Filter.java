package org.bouncycastle.i18n.filter;

public interface Filter {
  String doFilter(String paramString);
  
  String doFilterUrl(String paramString);
}
