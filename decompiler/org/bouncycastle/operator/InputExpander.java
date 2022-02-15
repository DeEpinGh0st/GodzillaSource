package org.bouncycastle.operator;

import java.io.InputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface InputExpander {
  AlgorithmIdentifier getAlgorithmIdentifier();
  
  InputStream getInputStream(InputStream paramInputStream);
}
