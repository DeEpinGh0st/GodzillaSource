package org.bouncycastle.cms;

import java.io.InputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.io.TeeInputStream;

public class RecipientOperator {
  private final AlgorithmIdentifier algorithmIdentifier;
  
  private final Object operator;
  
  public RecipientOperator(InputDecryptor paramInputDecryptor) {
    this.algorithmIdentifier = paramInputDecryptor.getAlgorithmIdentifier();
    this.operator = paramInputDecryptor;
  }
  
  public RecipientOperator(MacCalculator paramMacCalculator) {
    this.algorithmIdentifier = paramMacCalculator.getAlgorithmIdentifier();
    this.operator = paramMacCalculator;
  }
  
  public InputStream getInputStream(InputStream paramInputStream) {
    return (InputStream)((this.operator instanceof InputDecryptor) ? ((InputDecryptor)this.operator).getInputStream(paramInputStream) : new TeeInputStream(paramInputStream, ((MacCalculator)this.operator).getOutputStream()));
  }
  
  public boolean isMacBased() {
    return this.operator instanceof MacCalculator;
  }
  
  public byte[] getMac() {
    return ((MacCalculator)this.operator).getMac();
  }
}
