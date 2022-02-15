package org.bouncycastle.est;

import java.io.IOException;

public interface ESTClient {
  ESTResponse doRequest(ESTRequest paramESTRequest) throws IOException;
}
