package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

public class TlsRSAUtils {
  public static byte[] generateEncryptedPreMasterSecret(TlsContext paramTlsContext, RSAKeyParameters paramRSAKeyParameters, OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = new byte[48];
    paramTlsContext.getSecureRandom().nextBytes(arrayOfByte);
    TlsUtils.writeVersion(paramTlsContext.getClientVersion(), arrayOfByte, 0);
    PKCS1Encoding pKCS1Encoding = new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine());
    pKCS1Encoding.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)paramRSAKeyParameters, paramTlsContext.getSecureRandom()));
    try {
      byte[] arrayOfByte1 = pKCS1Encoding.processBlock(arrayOfByte, 0, arrayOfByte.length);
      if (TlsUtils.isSSL(paramTlsContext)) {
        paramOutputStream.write(arrayOfByte1);
      } else {
        TlsUtils.writeOpaque16(arrayOfByte1, paramOutputStream);
      } 
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new TlsFatalAlert((short)80, invalidCipherTextException);
    } 
    return arrayOfByte;
  }
  
  public static byte[] safeDecryptPreMasterSecret(TlsContext paramTlsContext, RSAKeyParameters paramRSAKeyParameters, byte[] paramArrayOfbyte) {
    ProtocolVersion protocolVersion = paramTlsContext.getClientVersion();
    boolean bool = false;
    byte[] arrayOfByte1 = new byte[48];
    paramTlsContext.getSecureRandom().nextBytes(arrayOfByte1);
    byte[] arrayOfByte2 = Arrays.clone(arrayOfByte1);
    try {
      PKCS1Encoding pKCS1Encoding = new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine(), arrayOfByte1);
      pKCS1Encoding.init(false, (CipherParameters)new ParametersWithRandom((CipherParameters)paramRSAKeyParameters, paramTlsContext.getSecureRandom()));
      arrayOfByte2 = pKCS1Encoding.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    } catch (Exception exception) {}
    if (!bool || !protocolVersion.isEqualOrEarlierVersionOf(ProtocolVersion.TLSv10)) {
      int i = protocolVersion.getMajorVersion() ^ arrayOfByte2[0] & 0xFF | protocolVersion.getMinorVersion() ^ arrayOfByte2[1] & 0xFF;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      int j = (i & 0x1) - 1 ^ 0xFFFFFFFF;
      for (byte b = 0; b < 48; b++)
        arrayOfByte2[b] = (byte)(arrayOfByte2[b] & (j ^ 0xFFFFFFFF) | arrayOfByte1[b] & j); 
    } 
    return arrayOfByte2;
  }
}
