package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Hashtable;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;

public class TlsSRPUtils {
  public static final Integer EXT_SRP = Integers.valueOf(12);
  
  public static void addSRPExtension(Hashtable<Integer, byte[]> paramHashtable, byte[] paramArrayOfbyte) throws IOException {
    paramHashtable.put(EXT_SRP, createSRPExtension(paramArrayOfbyte));
  }
  
  public static byte[] getSRPExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_SRP);
    return (arrayOfByte == null) ? null : readSRPExtension(arrayOfByte);
  }
  
  public static byte[] createSRPExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new TlsFatalAlert((short)80); 
    return TlsUtils.encodeOpaque8(paramArrayOfbyte);
  }
  
  public static byte[] readSRPExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    byte[] arrayOfByte = TlsUtils.readOpaque8(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    return arrayOfByte;
  }
  
  public static BigInteger readSRPParameter(InputStream paramInputStream) throws IOException {
    return new BigInteger(1, TlsUtils.readOpaque16(paramInputStream));
  }
  
  public static void writeSRPParameter(BigInteger paramBigInteger, OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeOpaque16(BigIntegers.asUnsignedByteArray(paramBigInteger), paramOutputStream);
  }
  
  public static boolean isSRPCipherSuite(int paramInt) {
    switch (paramInt) {
      case 49178:
      case 49179:
      case 49180:
      case 49181:
      case 49182:
      case 49183:
      case 49184:
      case 49185:
      case 49186:
        return true;
    } 
    return false;
  }
}
