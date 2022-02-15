package org.bouncycastle.crypto.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.util.io.Streams;

public class DHIESPublicKeyParser implements KeyParser {
  private DHParameters dhParams;
  
  public DHIESPublicKeyParser(DHParameters paramDHParameters) {
    this.dhParams = paramDHParameters;
  }
  
  public AsymmetricKeyParameter readKey(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[(this.dhParams.getP().bitLength() + 7) / 8];
    Streams.readFully(paramInputStream, arrayOfByte, 0, arrayOfByte.length);
    return (AsymmetricKeyParameter)new DHPublicKeyParameters(new BigInteger(1, arrayOfByte), this.dhParams);
  }
}
