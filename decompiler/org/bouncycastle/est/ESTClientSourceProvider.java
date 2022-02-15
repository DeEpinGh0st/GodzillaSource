package org.bouncycastle.est;

import java.io.IOException;

public interface ESTClientSourceProvider {
  Source makeSource(String paramString, int paramInt) throws IOException;
}
