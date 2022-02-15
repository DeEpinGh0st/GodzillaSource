package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.util.Integers;

public class TlsSRTPUtils {
  public static final Integer EXT_use_srtp = Integers.valueOf(14);
  
  public static void addUseSRTPExtension(Hashtable<Integer, byte[]> paramHashtable, UseSRTPData paramUseSRTPData) throws IOException {
    paramHashtable.put(EXT_use_srtp, createUseSRTPExtension(paramUseSRTPData));
  }
  
  public static UseSRTPData getUseSRTPExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_use_srtp);
    return (arrayOfByte == null) ? null : readUseSRTPExtension(arrayOfByte);
  }
  
  public static byte[] createUseSRTPExtension(UseSRTPData paramUseSRTPData) throws IOException {
    if (paramUseSRTPData == null)
      throw new IllegalArgumentException("'useSRTPData' cannot be null"); 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    TlsUtils.writeUint16ArrayWithUint16Length(paramUseSRTPData.getProtectionProfiles(), byteArrayOutputStream);
    TlsUtils.writeOpaque8(paramUseSRTPData.getMki(), byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public static UseSRTPData readUseSRTPExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    int i = TlsUtils.readUint16(byteArrayInputStream);
    if (i < 2 || (i & 0x1) != 0)
      throw new TlsFatalAlert((short)50); 
    int[] arrayOfInt = TlsUtils.readUint16Array(i / 2, byteArrayInputStream);
    byte[] arrayOfByte = TlsUtils.readOpaque8(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    return new UseSRTPData(arrayOfInt, arrayOfByte);
  }
}
