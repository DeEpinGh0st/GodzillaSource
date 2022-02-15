package org.bouncycastle.est.jcajce;

import java.io.IOException;
import javax.net.ssl.SSLSession;

public interface JsseHostnameAuthorizer {
  boolean verified(String paramString, SSLSession paramSSLSession) throws IOException;
}
