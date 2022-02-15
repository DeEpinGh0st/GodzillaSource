package org.mozilla.javascript.commonjs.module.provider;

import java.net.URLConnection;

public interface UrlConnectionSecurityDomainProvider {
  Object getSecurityDomain(URLConnection paramURLConnection);
}
