package org.mozilla.javascript.commonjs.module.provider;

import java.net.URLConnection;

public interface UrlConnectionExpiryCalculator {
  long calculateExpiry(URLConnection paramURLConnection);
}
