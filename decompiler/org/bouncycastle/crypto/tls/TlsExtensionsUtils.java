package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.util.Integers;

public class TlsExtensionsUtils {
  public static final Integer EXT_encrypt_then_mac = Integers.valueOf(22);
  
  public static final Integer EXT_extended_master_secret = Integers.valueOf(23);
  
  public static final Integer EXT_heartbeat = Integers.valueOf(15);
  
  public static final Integer EXT_max_fragment_length = Integers.valueOf(1);
  
  public static final Integer EXT_padding = Integers.valueOf(21);
  
  public static final Integer EXT_server_name = Integers.valueOf(0);
  
  public static final Integer EXT_status_request = Integers.valueOf(5);
  
  public static final Integer EXT_truncated_hmac = Integers.valueOf(4);
  
  public static Hashtable ensureExtensionsInitialised(Hashtable paramHashtable) {
    return (paramHashtable == null) ? new Hashtable<Object, Object>() : paramHashtable;
  }
  
  public static void addEncryptThenMACExtension(Hashtable<Integer, byte[]> paramHashtable) {
    paramHashtable.put(EXT_encrypt_then_mac, createEncryptThenMACExtension());
  }
  
  public static void addExtendedMasterSecretExtension(Hashtable<Integer, byte[]> paramHashtable) {
    paramHashtable.put(EXT_extended_master_secret, createExtendedMasterSecretExtension());
  }
  
  public static void addHeartbeatExtension(Hashtable<Integer, byte[]> paramHashtable, HeartbeatExtension paramHeartbeatExtension) throws IOException {
    paramHashtable.put(EXT_heartbeat, createHeartbeatExtension(paramHeartbeatExtension));
  }
  
  public static void addMaxFragmentLengthExtension(Hashtable<Integer, byte[]> paramHashtable, short paramShort) throws IOException {
    paramHashtable.put(EXT_max_fragment_length, createMaxFragmentLengthExtension(paramShort));
  }
  
  public static void addPaddingExtension(Hashtable<Integer, byte[]> paramHashtable, int paramInt) throws IOException {
    paramHashtable.put(EXT_padding, createPaddingExtension(paramInt));
  }
  
  public static void addServerNameExtension(Hashtable<Integer, byte[]> paramHashtable, ServerNameList paramServerNameList) throws IOException {
    paramHashtable.put(EXT_server_name, createServerNameExtension(paramServerNameList));
  }
  
  public static void addStatusRequestExtension(Hashtable<Integer, byte[]> paramHashtable, CertificateStatusRequest paramCertificateStatusRequest) throws IOException {
    paramHashtable.put(EXT_status_request, createStatusRequestExtension(paramCertificateStatusRequest));
  }
  
  public static void addTruncatedHMacExtension(Hashtable<Integer, byte[]> paramHashtable) {
    paramHashtable.put(EXT_truncated_hmac, createTruncatedHMacExtension());
  }
  
  public static HeartbeatExtension getHeartbeatExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_heartbeat);
    return (arrayOfByte == null) ? null : readHeartbeatExtension(arrayOfByte);
  }
  
  public static short getMaxFragmentLengthExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_max_fragment_length);
    return (arrayOfByte == null) ? -1 : readMaxFragmentLengthExtension(arrayOfByte);
  }
  
  public static int getPaddingExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_padding);
    return (arrayOfByte == null) ? -1 : readPaddingExtension(arrayOfByte);
  }
  
  public static ServerNameList getServerNameExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_server_name);
    return (arrayOfByte == null) ? null : readServerNameExtension(arrayOfByte);
  }
  
  public static CertificateStatusRequest getStatusRequestExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_status_request);
    return (arrayOfByte == null) ? null : readStatusRequestExtension(arrayOfByte);
  }
  
  public static boolean hasEncryptThenMACExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_encrypt_then_mac);
    return (arrayOfByte == null) ? false : readEncryptThenMACExtension(arrayOfByte);
  }
  
  public static boolean hasExtendedMasterSecretExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_extended_master_secret);
    return (arrayOfByte == null) ? false : readExtendedMasterSecretExtension(arrayOfByte);
  }
  
  public static boolean hasTruncatedHMacExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_truncated_hmac);
    return (arrayOfByte == null) ? false : readTruncatedHMacExtension(arrayOfByte);
  }
  
  public static byte[] createEmptyExtensionData() {
    return TlsUtils.EMPTY_BYTES;
  }
  
  public static byte[] createEncryptThenMACExtension() {
    return createEmptyExtensionData();
  }
  
  public static byte[] createExtendedMasterSecretExtension() {
    return createEmptyExtensionData();
  }
  
  public static byte[] createHeartbeatExtension(HeartbeatExtension paramHeartbeatExtension) throws IOException {
    if (paramHeartbeatExtension == null)
      throw new TlsFatalAlert((short)80); 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramHeartbeatExtension.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public static byte[] createMaxFragmentLengthExtension(short paramShort) throws IOException {
    TlsUtils.checkUint8(paramShort);
    byte[] arrayOfByte = new byte[1];
    TlsUtils.writeUint8(paramShort, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static byte[] createPaddingExtension(int paramInt) throws IOException {
    TlsUtils.checkUint16(paramInt);
    return new byte[paramInt];
  }
  
  public static byte[] createServerNameExtension(ServerNameList paramServerNameList) throws IOException {
    if (paramServerNameList == null)
      throw new TlsFatalAlert((short)80); 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramServerNameList.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public static byte[] createStatusRequestExtension(CertificateStatusRequest paramCertificateStatusRequest) throws IOException {
    if (paramCertificateStatusRequest == null)
      throw new TlsFatalAlert((short)80); 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramCertificateStatusRequest.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public static byte[] createTruncatedHMacExtension() {
    return createEmptyExtensionData();
  }
  
  private static boolean readEmptyExtensionData(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    if (paramArrayOfbyte.length != 0)
      throw new TlsFatalAlert((short)47); 
    return true;
  }
  
  public static boolean readEncryptThenMACExtension(byte[] paramArrayOfbyte) throws IOException {
    return readEmptyExtensionData(paramArrayOfbyte);
  }
  
  public static boolean readExtendedMasterSecretExtension(byte[] paramArrayOfbyte) throws IOException {
    return readEmptyExtensionData(paramArrayOfbyte);
  }
  
  public static HeartbeatExtension readHeartbeatExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    HeartbeatExtension heartbeatExtension = HeartbeatExtension.parse(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    return heartbeatExtension;
  }
  
  public static short readMaxFragmentLengthExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    if (paramArrayOfbyte.length != 1)
      throw new TlsFatalAlert((short)50); 
    return TlsUtils.readUint8(paramArrayOfbyte, 0);
  }
  
  public static int readPaddingExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      if (paramArrayOfbyte[b] != 0)
        throw new TlsFatalAlert((short)47); 
    } 
    return paramArrayOfbyte.length;
  }
  
  public static ServerNameList readServerNameExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    ServerNameList serverNameList = ServerNameList.parse(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    return serverNameList;
  }
  
  public static CertificateStatusRequest readStatusRequestExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    CertificateStatusRequest certificateStatusRequest = CertificateStatusRequest.parse(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    return certificateStatusRequest;
  }
  
  public static boolean readTruncatedHMacExtension(byte[] paramArrayOfbyte) throws IOException {
    return readEmptyExtensionData(paramArrayOfbyte);
  }
}
