package org.bouncycastle.crypto.parsers;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.util.io.Streams;

public class ECIESPublicKeyParser implements KeyParser {
  private ECDomainParameters ecParams;
  
  public ECIESPublicKeyParser(ECDomainParameters paramECDomainParameters) {
    this.ecParams = paramECDomainParameters;
  }
  
  public AsymmetricKeyParameter readKey(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte;
    int i = paramInputStream.read();
    switch (i) {
      case 0:
        throw new IOException("Sender's public key invalid.");
      case 2:
      case 3:
        arrayOfByte = new byte[1 + (this.ecParams.getCurve().getFieldSize() + 7) / 8];
        arrayOfByte[0] = (byte)i;
        Streams.readFully(paramInputStream, arrayOfByte, 1, arrayOfByte.length - 1);
        return (AsymmetricKeyParameter)new ECPublicKeyParameters(this.ecParams.getCurve().decodePoint(arrayOfByte), this.ecParams);
      case 4:
      case 6:
      case 7:
        arrayOfByte = new byte[1 + 2 * (this.ecParams.getCurve().getFieldSize() + 7) / 8];
        arrayOfByte[0] = (byte)i;
        Streams.readFully(paramInputStream, arrayOfByte, 1, arrayOfByte.length - 1);
        return (AsymmetricKeyParameter)new ECPublicKeyParameters(this.ecParams.getCurve().decodePoint(arrayOfByte), this.ecParams);
    } 
    throw new IOException("Sender's public key has invalid point encoding 0x" + Integer.toString(i, 16));
  }
}
