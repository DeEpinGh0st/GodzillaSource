package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class ServerDHParams {
  protected DHPublicKeyParameters publicKey;
  
  public ServerDHParams(DHPublicKeyParameters paramDHPublicKeyParameters) {
    if (paramDHPublicKeyParameters == null)
      throw new IllegalArgumentException("'publicKey' cannot be null"); 
    this.publicKey = paramDHPublicKeyParameters;
  }
  
  public DHPublicKeyParameters getPublicKey() {
    return this.publicKey;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DHParameters dHParameters = this.publicKey.getParameters();
    BigInteger bigInteger = this.publicKey.getY();
    TlsDHUtils.writeDHParameter(dHParameters.getP(), paramOutputStream);
    TlsDHUtils.writeDHParameter(dHParameters.getG(), paramOutputStream);
    TlsDHUtils.writeDHParameter(bigInteger, paramOutputStream);
  }
  
  public static ServerDHParams parse(InputStream paramInputStream) throws IOException {
    BigInteger bigInteger1 = TlsDHUtils.readDHParameter(paramInputStream);
    BigInteger bigInteger2 = TlsDHUtils.readDHParameter(paramInputStream);
    BigInteger bigInteger3 = TlsDHUtils.readDHParameter(paramInputStream);
    return new ServerDHParams(TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(bigInteger3, new DHParameters(bigInteger1, bigInteger2))));
  }
}
