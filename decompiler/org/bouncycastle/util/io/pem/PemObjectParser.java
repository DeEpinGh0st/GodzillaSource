package org.bouncycastle.util.io.pem;

import java.io.IOException;

public interface PemObjectParser {
  Object parseObject(PemObject paramPemObject) throws IOException;
}
