package org.bouncycastle.est;

import java.io.IOException;

public interface ESTHijacker {
  ESTResponse hijack(ESTRequest paramESTRequest, Source paramSource) throws IOException;
}
