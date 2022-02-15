package org.bouncycastle.est;

import java.io.IOException;

public interface ESTSourceConnectionListener<T, I> {
  ESTRequest onConnection(Source<T> paramSource, ESTRequest paramESTRequest) throws IOException;
}
