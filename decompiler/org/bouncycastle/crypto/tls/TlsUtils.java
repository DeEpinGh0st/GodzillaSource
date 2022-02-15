package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Shorts;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;

public class TlsUtils {
  public static final byte[] EMPTY_BYTES = new byte[0];
  
  public static final short[] EMPTY_SHORTS = new short[0];
  
  public static final int[] EMPTY_INTS = new int[0];
  
  public static final long[] EMPTY_LONGS = new long[0];
  
  public static final Integer EXT_signature_algorithms = Integers.valueOf(13);
  
  static final byte[] SSL_CLIENT = new byte[] { 67, 76, 78, 84 };
  
  static final byte[] SSL_SERVER = new byte[] { 83, 82, 86, 82 };
  
  static final byte[][] SSL3_CONST = genSSL3Const();
  
  public static void checkUint8(short paramShort) throws IOException {
    if (!isValidUint8(paramShort))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint8(int paramInt) throws IOException {
    if (!isValidUint8(paramInt))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint8(long paramLong) throws IOException {
    if (!isValidUint8(paramLong))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint16(int paramInt) throws IOException {
    if (!isValidUint16(paramInt))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint16(long paramLong) throws IOException {
    if (!isValidUint16(paramLong))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint24(int paramInt) throws IOException {
    if (!isValidUint24(paramInt))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint24(long paramLong) throws IOException {
    if (!isValidUint24(paramLong))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint32(long paramLong) throws IOException {
    if (!isValidUint32(paramLong))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint48(long paramLong) throws IOException {
    if (!isValidUint48(paramLong))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static void checkUint64(long paramLong) throws IOException {
    if (!isValidUint64(paramLong))
      throw new TlsFatalAlert((short)80); 
  }
  
  public static boolean isValidUint8(short paramShort) {
    return ((paramShort & 0xFF) == paramShort);
  }
  
  public static boolean isValidUint8(int paramInt) {
    return ((paramInt & 0xFF) == paramInt);
  }
  
  public static boolean isValidUint8(long paramLong) {
    return ((paramLong & 0xFFL) == paramLong);
  }
  
  public static boolean isValidUint16(int paramInt) {
    return ((paramInt & 0xFFFF) == paramInt);
  }
  
  public static boolean isValidUint16(long paramLong) {
    return ((paramLong & 0xFFFFL) == paramLong);
  }
  
  public static boolean isValidUint24(int paramInt) {
    return ((paramInt & 0xFFFFFF) == paramInt);
  }
  
  public static boolean isValidUint24(long paramLong) {
    return ((paramLong & 0xFFFFFFL) == paramLong);
  }
  
  public static boolean isValidUint32(long paramLong) {
    return ((paramLong & 0xFFFFFFFFL) == paramLong);
  }
  
  public static boolean isValidUint48(long paramLong) {
    return ((paramLong & 0xFFFFFFFFFFFFL) == paramLong);
  }
  
  public static boolean isValidUint64(long paramLong) {
    return true;
  }
  
  public static boolean isSSL(TlsContext paramTlsContext) {
    return paramTlsContext.getServerVersion().isSSL();
  }
  
  public static boolean isTLSv11(ProtocolVersion paramProtocolVersion) {
    return ProtocolVersion.TLSv11.isEqualOrEarlierVersionOf(paramProtocolVersion.getEquivalentTLSVersion());
  }
  
  public static boolean isTLSv11(TlsContext paramTlsContext) {
    return isTLSv11(paramTlsContext.getServerVersion());
  }
  
  public static boolean isTLSv12(ProtocolVersion paramProtocolVersion) {
    return ProtocolVersion.TLSv12.isEqualOrEarlierVersionOf(paramProtocolVersion.getEquivalentTLSVersion());
  }
  
  public static boolean isTLSv12(TlsContext paramTlsContext) {
    return isTLSv12(paramTlsContext.getServerVersion());
  }
  
  public static void writeUint8(short paramShort, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(paramShort);
  }
  
  public static void writeUint8(int paramInt, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(paramInt);
  }
  
  public static void writeUint8(short paramShort, byte[] paramArrayOfbyte, int paramInt) {
    paramArrayOfbyte[paramInt] = (byte)paramShort;
  }
  
  public static void writeUint8(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
  
  public static void writeUint16(int paramInt, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(paramInt >>> 8);
    paramOutputStream.write(paramInt);
  }
  
  public static void writeUint16(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2 + 1] = (byte)paramInt1;
  }
  
  public static void writeUint24(int paramInt, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write((byte)(paramInt >>> 16));
    paramOutputStream.write((byte)(paramInt >>> 8));
    paramOutputStream.write((byte)paramInt);
  }
  
  public static void writeUint24(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2 + 2] = (byte)paramInt1;
  }
  
  public static void writeUint32(long paramLong, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write((byte)(int)(paramLong >>> 24L));
    paramOutputStream.write((byte)(int)(paramLong >>> 16L));
    paramOutputStream.write((byte)(int)(paramLong >>> 8L));
    paramOutputStream.write((byte)(int)paramLong);
  }
  
  public static void writeUint32(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    paramArrayOfbyte[paramInt] = (byte)(int)(paramLong >>> 24L);
    paramArrayOfbyte[paramInt + 1] = (byte)(int)(paramLong >>> 16L);
    paramArrayOfbyte[paramInt + 2] = (byte)(int)(paramLong >>> 8L);
    paramArrayOfbyte[paramInt + 3] = (byte)(int)paramLong;
  }
  
  public static void writeUint48(long paramLong, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write((byte)(int)(paramLong >>> 40L));
    paramOutputStream.write((byte)(int)(paramLong >>> 32L));
    paramOutputStream.write((byte)(int)(paramLong >>> 24L));
    paramOutputStream.write((byte)(int)(paramLong >>> 16L));
    paramOutputStream.write((byte)(int)(paramLong >>> 8L));
    paramOutputStream.write((byte)(int)paramLong);
  }
  
  public static void writeUint48(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    paramArrayOfbyte[paramInt] = (byte)(int)(paramLong >>> 40L);
    paramArrayOfbyte[paramInt + 1] = (byte)(int)(paramLong >>> 32L);
    paramArrayOfbyte[paramInt + 2] = (byte)(int)(paramLong >>> 24L);
    paramArrayOfbyte[paramInt + 3] = (byte)(int)(paramLong >>> 16L);
    paramArrayOfbyte[paramInt + 4] = (byte)(int)(paramLong >>> 8L);
    paramArrayOfbyte[paramInt + 5] = (byte)(int)paramLong;
  }
  
  public static void writeUint64(long paramLong, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write((byte)(int)(paramLong >>> 56L));
    paramOutputStream.write((byte)(int)(paramLong >>> 48L));
    paramOutputStream.write((byte)(int)(paramLong >>> 40L));
    paramOutputStream.write((byte)(int)(paramLong >>> 32L));
    paramOutputStream.write((byte)(int)(paramLong >>> 24L));
    paramOutputStream.write((byte)(int)(paramLong >>> 16L));
    paramOutputStream.write((byte)(int)(paramLong >>> 8L));
    paramOutputStream.write((byte)(int)paramLong);
  }
  
  public static void writeUint64(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    paramArrayOfbyte[paramInt] = (byte)(int)(paramLong >>> 56L);
    paramArrayOfbyte[paramInt + 1] = (byte)(int)(paramLong >>> 48L);
    paramArrayOfbyte[paramInt + 2] = (byte)(int)(paramLong >>> 40L);
    paramArrayOfbyte[paramInt + 3] = (byte)(int)(paramLong >>> 32L);
    paramArrayOfbyte[paramInt + 4] = (byte)(int)(paramLong >>> 24L);
    paramArrayOfbyte[paramInt + 5] = (byte)(int)(paramLong >>> 16L);
    paramArrayOfbyte[paramInt + 6] = (byte)(int)(paramLong >>> 8L);
    paramArrayOfbyte[paramInt + 7] = (byte)(int)paramLong;
  }
  
  public static void writeOpaque8(byte[] paramArrayOfbyte, OutputStream paramOutputStream) throws IOException {
    checkUint8(paramArrayOfbyte.length);
    writeUint8(paramArrayOfbyte.length, paramOutputStream);
    paramOutputStream.write(paramArrayOfbyte);
  }
  
  public static void writeOpaque16(byte[] paramArrayOfbyte, OutputStream paramOutputStream) throws IOException {
    checkUint16(paramArrayOfbyte.length);
    writeUint16(paramArrayOfbyte.length, paramOutputStream);
    paramOutputStream.write(paramArrayOfbyte);
  }
  
  public static void writeOpaque24(byte[] paramArrayOfbyte, OutputStream paramOutputStream) throws IOException {
    checkUint24(paramArrayOfbyte.length);
    writeUint24(paramArrayOfbyte.length, paramOutputStream);
    paramOutputStream.write(paramArrayOfbyte);
  }
  
  public static void writeUint8Array(short[] paramArrayOfshort, OutputStream paramOutputStream) throws IOException {
    for (byte b = 0; b < paramArrayOfshort.length; b++)
      writeUint8(paramArrayOfshort[b], paramOutputStream); 
  }
  
  public static void writeUint8Array(short[] paramArrayOfshort, byte[] paramArrayOfbyte, int paramInt) throws IOException {
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      writeUint8(paramArrayOfshort[b], paramArrayOfbyte, paramInt);
      paramInt++;
    } 
  }
  
  public static void writeUint8ArrayWithUint8Length(short[] paramArrayOfshort, OutputStream paramOutputStream) throws IOException {
    checkUint8(paramArrayOfshort.length);
    writeUint8(paramArrayOfshort.length, paramOutputStream);
    writeUint8Array(paramArrayOfshort, paramOutputStream);
  }
  
  public static void writeUint8ArrayWithUint8Length(short[] paramArrayOfshort, byte[] paramArrayOfbyte, int paramInt) throws IOException {
    checkUint8(paramArrayOfshort.length);
    writeUint8(paramArrayOfshort.length, paramArrayOfbyte, paramInt);
    writeUint8Array(paramArrayOfshort, paramArrayOfbyte, paramInt + 1);
  }
  
  public static void writeUint16Array(int[] paramArrayOfint, OutputStream paramOutputStream) throws IOException {
    for (byte b = 0; b < paramArrayOfint.length; b++)
      writeUint16(paramArrayOfint[b], paramOutputStream); 
  }
  
  public static void writeUint16Array(int[] paramArrayOfint, byte[] paramArrayOfbyte, int paramInt) throws IOException {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      writeUint16(paramArrayOfint[b], paramArrayOfbyte, paramInt);
      paramInt += 2;
    } 
  }
  
  public static void writeUint16ArrayWithUint16Length(int[] paramArrayOfint, OutputStream paramOutputStream) throws IOException {
    int i = 2 * paramArrayOfint.length;
    checkUint16(i);
    writeUint16(i, paramOutputStream);
    writeUint16Array(paramArrayOfint, paramOutputStream);
  }
  
  public static void writeUint16ArrayWithUint16Length(int[] paramArrayOfint, byte[] paramArrayOfbyte, int paramInt) throws IOException {
    int i = 2 * paramArrayOfint.length;
    checkUint16(i);
    writeUint16(i, paramArrayOfbyte, paramInt);
    writeUint16Array(paramArrayOfint, paramArrayOfbyte, paramInt + 2);
  }
  
  public static byte[] encodeOpaque8(byte[] paramArrayOfbyte) throws IOException {
    checkUint8(paramArrayOfbyte.length);
    return Arrays.prepend(paramArrayOfbyte, (byte)paramArrayOfbyte.length);
  }
  
  public static byte[] encodeUint8ArrayWithUint8Length(short[] paramArrayOfshort) throws IOException {
    byte[] arrayOfByte = new byte[1 + paramArrayOfshort.length];
    writeUint8ArrayWithUint8Length(paramArrayOfshort, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static byte[] encodeUint16ArrayWithUint16Length(int[] paramArrayOfint) throws IOException {
    int i = 2 * paramArrayOfint.length;
    byte[] arrayOfByte = new byte[2 + i];
    writeUint16ArrayWithUint16Length(paramArrayOfint, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static short readUint8(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    if (i < 0)
      throw new EOFException(); 
    return (short)i;
  }
  
  public static short readUint8(byte[] paramArrayOfbyte, int paramInt) {
    return (short)(paramArrayOfbyte[paramInt] & 0xFF);
  }
  
  public static int readUint16(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    int j = paramInputStream.read();
    if (j < 0)
      throw new EOFException(); 
    return i << 8 | j;
  }
  
  public static int readUint16(byte[] paramArrayOfbyte, int paramInt) {
    int i = (paramArrayOfbyte[paramInt] & 0xFF) << 8;
    i |= paramArrayOfbyte[++paramInt] & 0xFF;
    return i;
  }
  
  public static int readUint24(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    int j = paramInputStream.read();
    int k = paramInputStream.read();
    if (k < 0)
      throw new EOFException(); 
    return i << 16 | j << 8 | k;
  }
  
  public static int readUint24(byte[] paramArrayOfbyte, int paramInt) {
    int i = (paramArrayOfbyte[paramInt] & 0xFF) << 16;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 8;
    i |= paramArrayOfbyte[++paramInt] & 0xFF;
    return i;
  }
  
  public static long readUint32(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    int j = paramInputStream.read();
    int k = paramInputStream.read();
    int m = paramInputStream.read();
    if (m < 0)
      throw new EOFException(); 
    return (i << 24 | j << 16 | k << 8 | m) & 0xFFFFFFFFL;
  }
  
  public static long readUint32(byte[] paramArrayOfbyte, int paramInt) {
    int i = (paramArrayOfbyte[paramInt] & 0xFF) << 24;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 16;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 8;
    i |= paramArrayOfbyte[++paramInt] & 0xFF;
    return i & 0xFFFFFFFFL;
  }
  
  public static long readUint48(InputStream paramInputStream) throws IOException {
    int i = readUint24(paramInputStream);
    int j = readUint24(paramInputStream);
    return (i & 0xFFFFFFFFL) << 24L | j & 0xFFFFFFFFL;
  }
  
  public static long readUint48(byte[] paramArrayOfbyte, int paramInt) {
    int i = readUint24(paramArrayOfbyte, paramInt);
    int j = readUint24(paramArrayOfbyte, paramInt + 3);
    return (i & 0xFFFFFFFFL) << 24L | j & 0xFFFFFFFFL;
  }
  
  public static byte[] readAllOrNothing(int paramInt, InputStream paramInputStream) throws IOException {
    if (paramInt < 1)
      return EMPTY_BYTES; 
    byte[] arrayOfByte = new byte[paramInt];
    int i = Streams.readFully(paramInputStream, arrayOfByte);
    if (i == 0)
      return null; 
    if (i != paramInt)
      throw new EOFException(); 
    return arrayOfByte;
  }
  
  public static byte[] readFully(int paramInt, InputStream paramInputStream) throws IOException {
    if (paramInt < 1)
      return EMPTY_BYTES; 
    byte[] arrayOfByte = new byte[paramInt];
    if (paramInt != Streams.readFully(paramInputStream, arrayOfByte))
      throw new EOFException(); 
    return arrayOfByte;
  }
  
  public static void readFully(byte[] paramArrayOfbyte, InputStream paramInputStream) throws IOException {
    int i = paramArrayOfbyte.length;
    if (i > 0 && i != Streams.readFully(paramInputStream, paramArrayOfbyte))
      throw new EOFException(); 
  }
  
  public static byte[] readOpaque8(InputStream paramInputStream) throws IOException {
    short s = readUint8(paramInputStream);
    return readFully(s, paramInputStream);
  }
  
  public static byte[] readOpaque16(InputStream paramInputStream) throws IOException {
    int i = readUint16(paramInputStream);
    return readFully(i, paramInputStream);
  }
  
  public static byte[] readOpaque24(InputStream paramInputStream) throws IOException {
    int i = readUint24(paramInputStream);
    return readFully(i, paramInputStream);
  }
  
  public static short[] readUint8Array(int paramInt, InputStream paramInputStream) throws IOException {
    short[] arrayOfShort = new short[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfShort[b] = readUint8(paramInputStream); 
    return arrayOfShort;
  }
  
  public static int[] readUint16Array(int paramInt, InputStream paramInputStream) throws IOException {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = readUint16(paramInputStream); 
    return arrayOfInt;
  }
  
  public static ProtocolVersion readVersion(byte[] paramArrayOfbyte, int paramInt) throws IOException {
    return ProtocolVersion.get(paramArrayOfbyte[paramInt] & 0xFF, paramArrayOfbyte[paramInt + 1] & 0xFF);
  }
  
  public static ProtocolVersion readVersion(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    int j = paramInputStream.read();
    if (j < 0)
      throw new EOFException(); 
    return ProtocolVersion.get(i, j);
  }
  
  public static int readVersionRaw(byte[] paramArrayOfbyte, int paramInt) throws IOException {
    return paramArrayOfbyte[paramInt] << 8 | paramArrayOfbyte[paramInt + 1];
  }
  
  public static int readVersionRaw(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    int j = paramInputStream.read();
    if (j < 0)
      throw new EOFException(); 
    return i << 8 | j;
  }
  
  public static ASN1Primitive readASN1Object(byte[] paramArrayOfbyte) throws IOException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(paramArrayOfbyte);
    ASN1Primitive aSN1Primitive = aSN1InputStream.readObject();
    if (null == aSN1Primitive)
      throw new TlsFatalAlert((short)50); 
    if (null != aSN1InputStream.readObject())
      throw new TlsFatalAlert((short)50); 
    return aSN1Primitive;
  }
  
  public static ASN1Primitive readDERObject(byte[] paramArrayOfbyte) throws IOException {
    ASN1Primitive aSN1Primitive = readASN1Object(paramArrayOfbyte);
    byte[] arrayOfByte = aSN1Primitive.getEncoded("DER");
    if (!Arrays.areEqual(arrayOfByte, paramArrayOfbyte))
      throw new TlsFatalAlert((short)50); 
    return aSN1Primitive;
  }
  
  public static void writeGMTUnixTime(byte[] paramArrayOfbyte, int paramInt) {
    int i = (int)(System.currentTimeMillis() / 1000L);
    paramArrayOfbyte[paramInt] = (byte)(i >>> 24);
    paramArrayOfbyte[paramInt + 1] = (byte)(i >>> 16);
    paramArrayOfbyte[paramInt + 2] = (byte)(i >>> 8);
    paramArrayOfbyte[paramInt + 3] = (byte)i;
  }
  
  public static void writeVersion(ProtocolVersion paramProtocolVersion, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(paramProtocolVersion.getMajorVersion());
    paramOutputStream.write(paramProtocolVersion.getMinorVersion());
  }
  
  public static void writeVersion(ProtocolVersion paramProtocolVersion, byte[] paramArrayOfbyte, int paramInt) {
    paramArrayOfbyte[paramInt] = (byte)paramProtocolVersion.getMajorVersion();
    paramArrayOfbyte[paramInt + 1] = (byte)paramProtocolVersion.getMinorVersion();
  }
  
  public static Vector getAllSignatureAlgorithms() {
    Vector<Short> vector = new Vector(4);
    vector.addElement(Shorts.valueOf((short)0));
    vector.addElement(Shorts.valueOf((short)1));
    vector.addElement(Shorts.valueOf((short)2));
    vector.addElement(Shorts.valueOf((short)3));
    return vector;
  }
  
  public static Vector getDefaultDSSSignatureAlgorithms() {
    return vectorOfOne(new SignatureAndHashAlgorithm((short)2, (short)2));
  }
  
  public static Vector getDefaultECDSASignatureAlgorithms() {
    return vectorOfOne(new SignatureAndHashAlgorithm((short)2, (short)3));
  }
  
  public static Vector getDefaultRSASignatureAlgorithms() {
    return vectorOfOne(new SignatureAndHashAlgorithm((short)2, (short)1));
  }
  
  public static Vector getDefaultSupportedSignatureAlgorithms() {
    short[] arrayOfShort1 = { 2, 3, 4, 5, 6 };
    short[] arrayOfShort2 = { 1, 2, 3 };
    Vector<SignatureAndHashAlgorithm> vector = new Vector();
    for (byte b = 0; b < arrayOfShort2.length; b++) {
      for (byte b1 = 0; b1 < arrayOfShort1.length; b1++)
        vector.addElement(new SignatureAndHashAlgorithm(arrayOfShort1[b1], arrayOfShort2[b])); 
    } 
    return vector;
  }
  
  public static SignatureAndHashAlgorithm getSignatureAndHashAlgorithm(TlsContext paramTlsContext, TlsSignerCredentials paramTlsSignerCredentials) throws IOException {
    SignatureAndHashAlgorithm signatureAndHashAlgorithm = null;
    if (isTLSv12(paramTlsContext)) {
      signatureAndHashAlgorithm = paramTlsSignerCredentials.getSignatureAndHashAlgorithm();
      if (signatureAndHashAlgorithm == null)
        throw new TlsFatalAlert((short)80); 
    } 
    return signatureAndHashAlgorithm;
  }
  
  public static byte[] getExtensionData(Hashtable paramHashtable, Integer paramInteger) {
    return (paramHashtable == null) ? null : (byte[])paramHashtable.get(paramInteger);
  }
  
  public static boolean hasExpectedEmptyExtensionData(Hashtable paramHashtable, Integer paramInteger, short paramShort) throws IOException {
    byte[] arrayOfByte = getExtensionData(paramHashtable, paramInteger);
    if (arrayOfByte == null)
      return false; 
    if (arrayOfByte.length != 0)
      throw new TlsFatalAlert(paramShort); 
    return true;
  }
  
  public static TlsSession importSession(byte[] paramArrayOfbyte, SessionParameters paramSessionParameters) {
    return new TlsSessionImpl(paramArrayOfbyte, paramSessionParameters);
  }
  
  public static boolean isSignatureAlgorithmsExtensionAllowed(ProtocolVersion paramProtocolVersion) {
    return ProtocolVersion.TLSv12.isEqualOrEarlierVersionOf(paramProtocolVersion.getEquivalentTLSVersion());
  }
  
  public static void addSignatureAlgorithmsExtension(Hashtable<Integer, byte[]> paramHashtable, Vector paramVector) throws IOException {
    paramHashtable.put(EXT_signature_algorithms, createSignatureAlgorithmsExtension(paramVector));
  }
  
  public static Vector getSignatureAlgorithmsExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = getExtensionData(paramHashtable, EXT_signature_algorithms);
    return (arrayOfByte == null) ? null : readSignatureAlgorithmsExtension(arrayOfByte);
  }
  
  public static byte[] createSignatureAlgorithmsExtension(Vector paramVector) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    encodeSupportedSignatureAlgorithms(paramVector, false, byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public static Vector readSignatureAlgorithmsExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    Vector vector = parseSupportedSignatureAlgorithms(false, byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    return vector;
  }
  
  public static void encodeSupportedSignatureAlgorithms(Vector<SignatureAndHashAlgorithm> paramVector, boolean paramBoolean, OutputStream paramOutputStream) throws IOException {
    if (paramVector == null || paramVector.size() < 1 || paramVector.size() >= 32768)
      throw new IllegalArgumentException("'supportedSignatureAlgorithms' must have length from 1 to (2^15 - 1)"); 
    int i = 2 * paramVector.size();
    checkUint16(i);
    writeUint16(i, paramOutputStream);
    for (byte b = 0; b < paramVector.size(); b++) {
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = paramVector.elementAt(b);
      if (!paramBoolean && signatureAndHashAlgorithm.getSignature() == 0)
        throw new IllegalArgumentException("SignatureAlgorithm.anonymous MUST NOT appear in the signature_algorithms extension"); 
      signatureAndHashAlgorithm.encode(paramOutputStream);
    } 
  }
  
  public static Vector parseSupportedSignatureAlgorithms(boolean paramBoolean, InputStream paramInputStream) throws IOException {
    int i = readUint16(paramInputStream);
    if (i < 2 || (i & 0x1) != 0)
      throw new TlsFatalAlert((short)50); 
    int j = i / 2;
    Vector<SignatureAndHashAlgorithm> vector = new Vector(j);
    for (byte b = 0; b < j; b++) {
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = SignatureAndHashAlgorithm.parse(paramInputStream);
      if (!paramBoolean && signatureAndHashAlgorithm.getSignature() == 0)
        throw new TlsFatalAlert((short)47); 
      vector.addElement(signatureAndHashAlgorithm);
    } 
    return vector;
  }
  
  public static void verifySupportedSignatureAlgorithm(Vector<SignatureAndHashAlgorithm> paramVector, SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm) throws IOException {
    if (paramVector == null || paramVector.size() < 1 || paramVector.size() >= 32768)
      throw new IllegalArgumentException("'supportedSignatureAlgorithms' must have length from 1 to (2^15 - 1)"); 
    if (paramSignatureAndHashAlgorithm == null)
      throw new IllegalArgumentException("'signatureAlgorithm' cannot be null"); 
    if (paramSignatureAndHashAlgorithm.getSignature() != 0)
      for (byte b = 0; b < paramVector.size(); b++) {
        SignatureAndHashAlgorithm signatureAndHashAlgorithm = paramVector.elementAt(b);
        if (signatureAndHashAlgorithm.getHash() == paramSignatureAndHashAlgorithm.getHash() && signatureAndHashAlgorithm.getSignature() == paramSignatureAndHashAlgorithm.getSignature())
          return; 
      }  
    throw new TlsFatalAlert((short)47);
  }
  
  public static byte[] PRF(TlsContext paramTlsContext, byte[] paramArrayOfbyte1, String paramString, byte[] paramArrayOfbyte2, int paramInt) {
    ProtocolVersion protocolVersion = paramTlsContext.getServerVersion();
    if (protocolVersion.isSSL())
      throw new IllegalStateException("No PRF available for SSLv3 session"); 
    byte[] arrayOfByte1 = Strings.toByteArray(paramString);
    byte[] arrayOfByte2 = concat(arrayOfByte1, paramArrayOfbyte2);
    int i = paramTlsContext.getSecurityParameters().getPrfAlgorithm();
    if (i == 0)
      return PRF_legacy(paramArrayOfbyte1, arrayOfByte1, arrayOfByte2, paramInt); 
    Digest digest = createPRFHash(i);
    byte[] arrayOfByte3 = new byte[paramInt];
    hmac_hash(digest, paramArrayOfbyte1, arrayOfByte2, arrayOfByte3);
    return arrayOfByte3;
  }
  
  public static byte[] PRF_legacy(byte[] paramArrayOfbyte1, String paramString, byte[] paramArrayOfbyte2, int paramInt) {
    byte[] arrayOfByte1 = Strings.toByteArray(paramString);
    byte[] arrayOfByte2 = concat(arrayOfByte1, paramArrayOfbyte2);
    return PRF_legacy(paramArrayOfbyte1, arrayOfByte1, arrayOfByte2, paramInt);
  }
  
  static byte[] PRF_legacy(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt) {
    int i = (paramArrayOfbyte1.length + 1) / 2;
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte1, 0, i);
    System.arraycopy(paramArrayOfbyte1, paramArrayOfbyte1.length - i, arrayOfByte2, 0, i);
    byte[] arrayOfByte3 = new byte[paramInt];
    byte[] arrayOfByte4 = new byte[paramInt];
    hmac_hash(createHash((short)1), arrayOfByte1, paramArrayOfbyte3, arrayOfByte3);
    hmac_hash(createHash((short)2), arrayOfByte2, paramArrayOfbyte3, arrayOfByte4);
    for (byte b = 0; b < paramInt; b++)
      arrayOfByte3[b] = (byte)(arrayOfByte3[b] ^ arrayOfByte4[b]); 
    return arrayOfByte3;
  }
  
  static byte[] concat(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte1.length);
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, paramArrayOfbyte1.length, paramArrayOfbyte2.length);
    return arrayOfByte;
  }
  
  static void hmac_hash(Digest paramDigest, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    HMac hMac = new HMac(paramDigest);
    hMac.init((CipherParameters)new KeyParameter(paramArrayOfbyte1));
    byte[] arrayOfByte1 = paramArrayOfbyte2;
    int i = paramDigest.getDigestSize();
    int j = (paramArrayOfbyte3.length + i - 1) / i;
    byte[] arrayOfByte2 = new byte[hMac.getMacSize()];
    byte[] arrayOfByte3 = new byte[hMac.getMacSize()];
    for (byte b = 0; b < j; b++) {
      hMac.update(arrayOfByte1, 0, arrayOfByte1.length);
      hMac.doFinal(arrayOfByte2, 0);
      arrayOfByte1 = arrayOfByte2;
      hMac.update(arrayOfByte1, 0, arrayOfByte1.length);
      hMac.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
      hMac.doFinal(arrayOfByte3, 0);
      System.arraycopy(arrayOfByte3, 0, paramArrayOfbyte3, i * b, Math.min(i, paramArrayOfbyte3.length - i * b));
    } 
  }
  
  static void validateKeyUsage(Certificate paramCertificate, int paramInt) throws IOException {
    Extensions extensions = paramCertificate.getTBSCertificate().getExtensions();
    if (extensions != null) {
      KeyUsage keyUsage = KeyUsage.fromExtensions(extensions);
      if (keyUsage != null) {
        int i = keyUsage.getBytes()[0] & 0xFF;
        if ((i & paramInt) != paramInt)
          throw new TlsFatalAlert((short)46); 
      } 
    } 
  }
  
  static byte[] calculateKeyBlock(TlsContext paramTlsContext, int paramInt) {
    SecurityParameters securityParameters = paramTlsContext.getSecurityParameters();
    byte[] arrayOfByte1 = securityParameters.getMasterSecret();
    byte[] arrayOfByte2 = concat(securityParameters.getServerRandom(), securityParameters.getClientRandom());
    return isSSL(paramTlsContext) ? calculateKeyBlock_SSL(arrayOfByte1, arrayOfByte2, paramInt) : PRF(paramTlsContext, arrayOfByte1, "key expansion", arrayOfByte2, paramInt);
  }
  
  static byte[] calculateKeyBlock_SSL(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    Digest digest1 = createHash((short)1);
    Digest digest2 = createHash((short)2);
    int i = digest1.getDigestSize();
    byte[] arrayOfByte1 = new byte[digest2.getDigestSize()];
    byte[] arrayOfByte2 = new byte[paramInt + i];
    byte b = 0;
    int j = 0;
    while (j < paramInt) {
      byte[] arrayOfByte = SSL3_CONST[b];
      digest2.update(arrayOfByte, 0, arrayOfByte.length);
      digest2.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
      digest2.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
      digest2.doFinal(arrayOfByte1, 0);
      digest1.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
      digest1.update(arrayOfByte1, 0, arrayOfByte1.length);
      digest1.doFinal(arrayOfByte2, j);
      j += i;
      b++;
    } 
    return Arrays.copyOfRange(arrayOfByte2, 0, paramInt);
  }
  
  static byte[] calculateMasterSecret(TlsContext paramTlsContext, byte[] paramArrayOfbyte) {
    byte[] arrayOfByte;
    SecurityParameters securityParameters = paramTlsContext.getSecurityParameters();
    if (securityParameters.extendedMasterSecret) {
      arrayOfByte = securityParameters.getSessionHash();
    } else {
      arrayOfByte = concat(securityParameters.getClientRandom(), securityParameters.getServerRandom());
    } 
    if (isSSL(paramTlsContext))
      return calculateMasterSecret_SSL(paramArrayOfbyte, arrayOfByte); 
    String str = securityParameters.extendedMasterSecret ? "extended master secret" : "master secret";
    return PRF(paramTlsContext, paramArrayOfbyte, str, arrayOfByte, 48);
  }
  
  static byte[] calculateMasterSecret_SSL(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    Digest digest1 = createHash((short)1);
    Digest digest2 = createHash((short)2);
    int i = digest1.getDigestSize();
    byte[] arrayOfByte1 = new byte[digest2.getDigestSize()];
    byte[] arrayOfByte2 = new byte[i * 3];
    int j = 0;
    for (byte b = 0; b < 3; b++) {
      byte[] arrayOfByte = SSL3_CONST[b];
      digest2.update(arrayOfByte, 0, arrayOfByte.length);
      digest2.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
      digest2.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
      digest2.doFinal(arrayOfByte1, 0);
      digest1.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
      digest1.update(arrayOfByte1, 0, arrayOfByte1.length);
      digest1.doFinal(arrayOfByte2, j);
      j += i;
    } 
    return arrayOfByte2;
  }
  
  static byte[] calculateVerifyData(TlsContext paramTlsContext, String paramString, byte[] paramArrayOfbyte) {
    if (isSSL(paramTlsContext))
      return paramArrayOfbyte; 
    SecurityParameters securityParameters = paramTlsContext.getSecurityParameters();
    byte[] arrayOfByte = securityParameters.getMasterSecret();
    int i = securityParameters.getVerifyDataLength();
    return PRF(paramTlsContext, arrayOfByte, paramString, paramArrayOfbyte, i);
  }
  
  public static Digest createHash(short paramShort) {
    switch (paramShort) {
      case 1:
        return (Digest)new MD5Digest();
      case 2:
        return (Digest)new SHA1Digest();
      case 3:
        return (Digest)new SHA224Digest();
      case 4:
        return (Digest)new SHA256Digest();
      case 5:
        return (Digest)new SHA384Digest();
      case 6:
        return (Digest)new SHA512Digest();
    } 
    throw new IllegalArgumentException("unknown HashAlgorithm");
  }
  
  public static Digest createHash(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm) {
    return (paramSignatureAndHashAlgorithm == null) ? new CombinedHash() : createHash(paramSignatureAndHashAlgorithm.getHash());
  }
  
  public static Digest cloneHash(short paramShort, Digest paramDigest) {
    switch (paramShort) {
      case 1:
        return (Digest)new MD5Digest((MD5Digest)paramDigest);
      case 2:
        return (Digest)new SHA1Digest((SHA1Digest)paramDigest);
      case 3:
        return (Digest)new SHA224Digest((SHA224Digest)paramDigest);
      case 4:
        return (Digest)new SHA256Digest((SHA256Digest)paramDigest);
      case 5:
        return (Digest)new SHA384Digest((SHA384Digest)paramDigest);
      case 6:
        return (Digest)new SHA512Digest((SHA512Digest)paramDigest);
    } 
    throw new IllegalArgumentException("unknown HashAlgorithm");
  }
  
  public static Digest createPRFHash(int paramInt) {
    switch (paramInt) {
      case 0:
        return new CombinedHash();
    } 
    return createHash(getHashAlgorithmForPRFAlgorithm(paramInt));
  }
  
  public static Digest clonePRFHash(int paramInt, Digest paramDigest) {
    switch (paramInt) {
      case 0:
        return new CombinedHash((CombinedHash)paramDigest);
    } 
    return cloneHash(getHashAlgorithmForPRFAlgorithm(paramInt), paramDigest);
  }
  
  public static short getHashAlgorithmForPRFAlgorithm(int paramInt) {
    switch (paramInt) {
      case 0:
        throw new IllegalArgumentException("legacy PRF not a valid algorithm");
      case 1:
        return 4;
      case 2:
        return 5;
    } 
    throw new IllegalArgumentException("unknown PRFAlgorithm");
  }
  
  public static ASN1ObjectIdentifier getOIDForHashAlgorithm(short paramShort) {
    switch (paramShort) {
      case 1:
        return PKCSObjectIdentifiers.md5;
      case 2:
        return X509ObjectIdentifiers.id_SHA1;
      case 3:
        return NISTObjectIdentifiers.id_sha224;
      case 4:
        return NISTObjectIdentifiers.id_sha256;
      case 5:
        return NISTObjectIdentifiers.id_sha384;
      case 6:
        return NISTObjectIdentifiers.id_sha512;
    } 
    throw new IllegalArgumentException("unknown HashAlgorithm");
  }
  
  static short getClientCertificateType(Certificate paramCertificate1, Certificate paramCertificate2) throws IOException {
    if (paramCertificate1.isEmpty())
      return -1; 
    Certificate certificate = paramCertificate1.getCertificateAt(0);
    SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
    try {
      AsymmetricKeyParameter asymmetricKeyParameter = PublicKeyFactory.createKey(subjectPublicKeyInfo);
      if (asymmetricKeyParameter.isPrivate())
        throw new TlsFatalAlert((short)80); 
      if (asymmetricKeyParameter instanceof org.bouncycastle.crypto.params.RSAKeyParameters) {
        validateKeyUsage(certificate, 128);
        return 1;
      } 
      if (asymmetricKeyParameter instanceof org.bouncycastle.crypto.params.DSAPublicKeyParameters) {
        validateKeyUsage(certificate, 128);
        return 2;
      } 
      if (asymmetricKeyParameter instanceof org.bouncycastle.crypto.params.ECPublicKeyParameters) {
        validateKeyUsage(certificate, 128);
        return 64;
      } 
      throw new TlsFatalAlert((short)43);
    } catch (Exception exception) {
      throw new TlsFatalAlert((short)43, exception);
    } 
  }
  
  static void trackHashAlgorithms(TlsHandshakeHash paramTlsHandshakeHash, Vector<SignatureAndHashAlgorithm> paramVector) {
    if (paramVector != null)
      for (byte b = 0; b < paramVector.size(); b++) {
        SignatureAndHashAlgorithm signatureAndHashAlgorithm = paramVector.elementAt(b);
        short s = signatureAndHashAlgorithm.getHash();
        if (!HashAlgorithm.isPrivate(s))
          paramTlsHandshakeHash.trackHashAlgorithm(s); 
      }  
  }
  
  public static boolean hasSigningCapability(short paramShort) {
    switch (paramShort) {
      case 1:
      case 2:
      case 64:
        return true;
    } 
    return false;
  }
  
  public static TlsSigner createTlsSigner(short paramShort) {
    switch (paramShort) {
      case 2:
        return new TlsDSSSigner();
      case 64:
        return new TlsECDSASigner();
      case 1:
        return new TlsRSASigner();
    } 
    throw new IllegalArgumentException("'clientCertificateType' is not a type with signing capability");
  }
  
  private static byte[][] genSSL3Const() {
    byte b1 = 10;
    byte[][] arrayOfByte = new byte[b1][];
    for (byte b2 = 0; b2 < b1; b2++) {
      byte[] arrayOfByte1 = new byte[b2 + 1];
      Arrays.fill(arrayOfByte1, (byte)(65 + b2));
      arrayOfByte[b2] = arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  private static Vector vectorOfOne(Object paramObject) {
    Vector<Object> vector = new Vector(1);
    vector.addElement(paramObject);
    return vector;
  }
  
  public static int getCipherType(int paramInt) throws IOException {
    switch (getEncryptionAlgorithm(paramInt)) {
      case 10:
      case 11:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 103:
      case 104:
        return 2;
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 12:
      case 13:
      case 14:
        return 1;
      case 0:
      case 1:
      case 2:
        return 0;
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public static int getEncryptionAlgorithm(int paramInt) throws IOException {
    switch (paramInt) {
      case 10:
      case 13:
      case 16:
      case 19:
      case 22:
      case 27:
      case 139:
      case 143:
      case 147:
      case 49155:
      case 49160:
      case 49165:
      case 49170:
      case 49175:
      case 49178:
      case 49179:
      case 49180:
      case 49204:
        return 7;
      case 47:
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 60:
      case 62:
      case 63:
      case 64:
      case 103:
      case 108:
      case 140:
      case 144:
      case 148:
      case 174:
      case 178:
      case 182:
      case 49156:
      case 49161:
      case 49166:
      case 49171:
      case 49176:
      case 49181:
      case 49182:
      case 49183:
      case 49187:
      case 49189:
      case 49191:
      case 49193:
      case 49205:
      case 49207:
        return 8;
      case 49308:
      case 49310:
      case 49316:
      case 49318:
      case 49324:
        return 15;
      case 49312:
      case 49314:
      case 49320:
      case 49322:
      case 49326:
        return 16;
      case 156:
      case 158:
      case 160:
      case 162:
      case 164:
      case 166:
      case 168:
      case 170:
      case 172:
      case 49195:
      case 49197:
      case 49199:
      case 49201:
        return 10;
      case 65280:
      case 65282:
      case 65284:
      case 65296:
      case 65298:
      case 65300:
        return 103;
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 61:
      case 104:
      case 105:
      case 106:
      case 107:
      case 109:
      case 141:
      case 145:
      case 149:
      case 175:
      case 179:
      case 183:
      case 49157:
      case 49162:
      case 49167:
      case 49172:
      case 49177:
      case 49184:
      case 49185:
      case 49186:
      case 49188:
      case 49190:
      case 49192:
      case 49194:
      case 49206:
      case 49208:
        return 9;
      case 49309:
      case 49311:
      case 49317:
      case 49319:
      case 49325:
        return 17;
      case 49313:
      case 49315:
      case 49321:
      case 49323:
      case 49327:
        return 18;
      case 157:
      case 159:
      case 161:
      case 163:
      case 165:
      case 167:
      case 169:
      case 171:
      case 173:
      case 49196:
      case 49198:
      case 49200:
      case 49202:
        return 11;
      case 65281:
      case 65283:
      case 65285:
      case 65297:
      case 65299:
      case 65301:
        return 104;
      case 65:
      case 66:
      case 67:
      case 68:
      case 69:
      case 70:
      case 186:
      case 187:
      case 188:
      case 189:
      case 190:
      case 191:
      case 49266:
      case 49268:
      case 49270:
      case 49272:
      case 49300:
      case 49302:
      case 49304:
      case 49306:
        return 12;
      case 49274:
      case 49276:
      case 49278:
      case 49280:
      case 49282:
      case 49284:
      case 49286:
      case 49288:
      case 49290:
      case 49292:
      case 49294:
      case 49296:
      case 49298:
        return 19;
      case 132:
      case 133:
      case 134:
      case 135:
      case 136:
      case 137:
      case 192:
      case 193:
      case 194:
      case 195:
      case 196:
      case 197:
      case 49267:
      case 49269:
      case 49271:
      case 49273:
      case 49301:
      case 49303:
      case 49305:
      case 49307:
        return 13;
      case 49275:
      case 49277:
      case 49279:
      case 49281:
      case 49283:
      case 49285:
      case 49287:
      case 49289:
      case 49291:
      case 49293:
      case 49295:
      case 49297:
      case 49299:
        return 20;
      case 52392:
      case 52393:
      case 52394:
      case 52395:
      case 52396:
      case 52397:
      case 52398:
        return 21;
      case 1:
        return 0;
      case 2:
      case 44:
      case 45:
      case 46:
      case 49153:
      case 49158:
      case 49163:
      case 49168:
      case 49173:
      case 49209:
        return 0;
      case 59:
      case 176:
      case 180:
      case 184:
      case 49210:
        return 0;
      case 177:
      case 181:
      case 185:
      case 49211:
        return 0;
      case 4:
      case 24:
        return 2;
      case 5:
      case 138:
      case 142:
      case 146:
      case 49154:
      case 49159:
      case 49164:
      case 49169:
      case 49174:
      case 49203:
        return 2;
      case 150:
      case 151:
      case 152:
      case 153:
      case 154:
      case 155:
        return 14;
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public static int getKeyExchangeAlgorithm(int paramInt) throws IOException {
    switch (paramInt) {
      case 24:
      case 27:
      case 52:
      case 58:
      case 70:
      case 108:
      case 109:
      case 137:
      case 155:
      case 166:
      case 167:
      case 191:
      case 197:
      case 49284:
      case 49285:
        return 11;
      case 13:
      case 48:
      case 54:
      case 62:
      case 66:
      case 104:
      case 133:
      case 151:
      case 164:
      case 165:
      case 187:
      case 193:
      case 49282:
      case 49283:
        return 7;
      case 16:
      case 49:
      case 55:
      case 63:
      case 67:
      case 105:
      case 134:
      case 152:
      case 160:
      case 161:
      case 188:
      case 194:
      case 49278:
      case 49279:
        return 9;
      case 19:
      case 50:
      case 56:
      case 64:
      case 68:
      case 106:
      case 135:
      case 153:
      case 162:
      case 163:
      case 189:
      case 195:
      case 49280:
      case 49281:
        return 3;
      case 45:
      case 142:
      case 143:
      case 144:
      case 145:
      case 170:
      case 171:
      case 178:
      case 179:
      case 180:
      case 181:
      case 49296:
      case 49297:
      case 49302:
      case 49303:
      case 49318:
      case 49319:
      case 49322:
      case 49323:
      case 52397:
      case 65298:
      case 65299:
        return 14;
      case 22:
      case 51:
      case 57:
      case 69:
      case 103:
      case 107:
      case 136:
      case 154:
      case 158:
      case 159:
      case 190:
      case 196:
      case 49276:
      case 49277:
      case 49310:
      case 49311:
      case 49314:
      case 49315:
      case 52394:
      case 65280:
      case 65281:
        return 5;
      case 49173:
      case 49174:
      case 49175:
      case 49176:
      case 49177:
        return 20;
      case 49153:
      case 49154:
      case 49155:
      case 49156:
      case 49157:
      case 49189:
      case 49190:
      case 49197:
      case 49198:
      case 49268:
      case 49269:
      case 49288:
      case 49289:
        return 16;
      case 49163:
      case 49164:
      case 49165:
      case 49166:
      case 49167:
      case 49193:
      case 49194:
      case 49201:
      case 49202:
      case 49272:
      case 49273:
      case 49292:
      case 49293:
        return 18;
      case 49158:
      case 49159:
      case 49160:
      case 49161:
      case 49162:
      case 49187:
      case 49188:
      case 49195:
      case 49196:
      case 49266:
      case 49267:
      case 49286:
      case 49287:
      case 49324:
      case 49325:
      case 49326:
      case 49327:
      case 52393:
      case 65284:
      case 65285:
        return 17;
      case 49203:
      case 49204:
      case 49205:
      case 49206:
      case 49207:
      case 49208:
      case 49209:
      case 49210:
      case 49211:
      case 49306:
      case 49307:
      case 52396:
      case 65300:
      case 65301:
        return 24;
      case 49168:
      case 49169:
      case 49170:
      case 49171:
      case 49172:
      case 49191:
      case 49192:
      case 49199:
      case 49200:
      case 49270:
      case 49271:
      case 49290:
      case 49291:
      case 52392:
      case 65282:
      case 65283:
        return 19;
      case 44:
      case 138:
      case 139:
      case 140:
      case 141:
      case 168:
      case 169:
      case 174:
      case 175:
      case 176:
      case 177:
      case 49294:
      case 49295:
      case 49300:
      case 49301:
      case 49316:
      case 49317:
      case 49320:
      case 49321:
      case 52395:
      case 65296:
      case 65297:
        return 13;
      case 1:
      case 2:
      case 4:
      case 5:
      case 10:
      case 47:
      case 53:
      case 59:
      case 60:
      case 61:
      case 65:
      case 132:
      case 150:
      case 156:
      case 157:
      case 186:
      case 192:
      case 49274:
      case 49275:
      case 49308:
      case 49309:
      case 49312:
      case 49313:
        return 1;
      case 46:
      case 146:
      case 147:
      case 148:
      case 149:
      case 172:
      case 173:
      case 182:
      case 183:
      case 184:
      case 185:
      case 49298:
      case 49299:
      case 49304:
      case 49305:
      case 52398:
        return 15;
      case 49178:
      case 49181:
      case 49184:
        return 21;
      case 49180:
      case 49183:
      case 49186:
        return 22;
      case 49179:
      case 49182:
      case 49185:
        return 23;
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public static int getMACAlgorithm(int paramInt) throws IOException {
    switch (paramInt) {
      case 156:
      case 157:
      case 158:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 167:
      case 168:
      case 169:
      case 170:
      case 171:
      case 172:
      case 173:
      case 49195:
      case 49196:
      case 49197:
      case 49198:
      case 49199:
      case 49200:
      case 49201:
      case 49202:
      case 49274:
      case 49275:
      case 49276:
      case 49277:
      case 49278:
      case 49279:
      case 49280:
      case 49281:
      case 49282:
      case 49283:
      case 49284:
      case 49285:
      case 49286:
      case 49287:
      case 49288:
      case 49289:
      case 49290:
      case 49291:
      case 49292:
      case 49293:
      case 49294:
      case 49295:
      case 49296:
      case 49297:
      case 49298:
      case 49299:
      case 49308:
      case 49309:
      case 49310:
      case 49311:
      case 49312:
      case 49313:
      case 49314:
      case 49315:
      case 49316:
      case 49317:
      case 49318:
      case 49319:
      case 49320:
      case 49321:
      case 49322:
      case 49323:
      case 49324:
      case 49325:
      case 49326:
      case 49327:
      case 52392:
      case 52393:
      case 52394:
      case 52395:
      case 52396:
      case 52397:
      case 52398:
      case 65280:
      case 65281:
      case 65282:
      case 65283:
      case 65284:
      case 65285:
      case 65296:
      case 65297:
      case 65298:
      case 65299:
      case 65300:
      case 65301:
        return 0;
      case 1:
      case 4:
      case 24:
        return 1;
      case 2:
      case 5:
      case 10:
      case 13:
      case 16:
      case 19:
      case 22:
      case 27:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 65:
      case 66:
      case 67:
      case 68:
      case 69:
      case 70:
      case 132:
      case 133:
      case 134:
      case 135:
      case 136:
      case 137:
      case 138:
      case 139:
      case 140:
      case 141:
      case 142:
      case 143:
      case 144:
      case 145:
      case 146:
      case 147:
      case 148:
      case 149:
      case 150:
      case 151:
      case 152:
      case 153:
      case 154:
      case 155:
      case 49153:
      case 49154:
      case 49155:
      case 49156:
      case 49157:
      case 49158:
      case 49159:
      case 49160:
      case 49161:
      case 49162:
      case 49163:
      case 49164:
      case 49165:
      case 49166:
      case 49167:
      case 49168:
      case 49169:
      case 49170:
      case 49171:
      case 49172:
      case 49173:
      case 49174:
      case 49175:
      case 49176:
      case 49177:
      case 49178:
      case 49179:
      case 49180:
      case 49181:
      case 49182:
      case 49183:
      case 49184:
      case 49185:
      case 49186:
      case 49203:
      case 49204:
      case 49205:
      case 49206:
      case 49209:
        return 2;
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      case 64:
      case 103:
      case 104:
      case 105:
      case 106:
      case 107:
      case 108:
      case 109:
      case 174:
      case 176:
      case 178:
      case 180:
      case 182:
      case 184:
      case 186:
      case 187:
      case 188:
      case 189:
      case 190:
      case 191:
      case 192:
      case 193:
      case 194:
      case 195:
      case 196:
      case 197:
      case 49187:
      case 49189:
      case 49191:
      case 49193:
      case 49207:
      case 49210:
      case 49266:
      case 49268:
      case 49270:
      case 49272:
      case 49300:
      case 49302:
      case 49304:
      case 49306:
        return 3;
      case 175:
      case 177:
      case 179:
      case 181:
      case 183:
      case 185:
      case 49188:
      case 49190:
      case 49192:
      case 49194:
      case 49208:
      case 49211:
      case 49267:
      case 49269:
      case 49271:
      case 49273:
      case 49301:
      case 49303:
      case 49305:
      case 49307:
        return 4;
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public static ProtocolVersion getMinimumVersion(int paramInt) {
    switch (paramInt) {
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      case 64:
      case 103:
      case 104:
      case 105:
      case 106:
      case 107:
      case 108:
      case 109:
      case 156:
      case 157:
      case 158:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 167:
      case 168:
      case 169:
      case 170:
      case 171:
      case 172:
      case 173:
      case 186:
      case 187:
      case 188:
      case 189:
      case 190:
      case 191:
      case 192:
      case 193:
      case 194:
      case 195:
      case 196:
      case 197:
      case 49187:
      case 49188:
      case 49189:
      case 49190:
      case 49191:
      case 49192:
      case 49193:
      case 49194:
      case 49195:
      case 49196:
      case 49197:
      case 49198:
      case 49199:
      case 49200:
      case 49201:
      case 49202:
      case 49266:
      case 49267:
      case 49268:
      case 49269:
      case 49270:
      case 49271:
      case 49272:
      case 49273:
      case 49274:
      case 49275:
      case 49276:
      case 49277:
      case 49278:
      case 49279:
      case 49280:
      case 49281:
      case 49282:
      case 49283:
      case 49284:
      case 49285:
      case 49286:
      case 49287:
      case 49288:
      case 49289:
      case 49290:
      case 49291:
      case 49292:
      case 49293:
      case 49294:
      case 49295:
      case 49296:
      case 49297:
      case 49298:
      case 49299:
      case 49308:
      case 49309:
      case 49310:
      case 49311:
      case 49312:
      case 49313:
      case 49314:
      case 49315:
      case 49316:
      case 49317:
      case 49318:
      case 49319:
      case 49320:
      case 49321:
      case 49322:
      case 49323:
      case 49324:
      case 49325:
      case 49326:
      case 49327:
      case 52392:
      case 52393:
      case 52394:
      case 52395:
      case 52396:
      case 52397:
      case 52398:
      case 65280:
      case 65281:
      case 65282:
      case 65283:
      case 65284:
      case 65285:
      case 65296:
      case 65297:
      case 65298:
      case 65299:
      case 65300:
      case 65301:
        return ProtocolVersion.TLSv12;
    } 
    return ProtocolVersion.SSLv3;
  }
  
  public static boolean isAEADCipherSuite(int paramInt) throws IOException {
    return (2 == getCipherType(paramInt));
  }
  
  public static boolean isBlockCipherSuite(int paramInt) throws IOException {
    return (1 == getCipherType(paramInt));
  }
  
  public static boolean isStreamCipherSuite(int paramInt) throws IOException {
    return (0 == getCipherType(paramInt));
  }
  
  public static boolean isValidCipherSuiteForSignatureAlgorithms(int paramInt, Vector paramVector) {
    int i;
    try {
      i = getKeyExchangeAlgorithm(paramInt);
    } catch (IOException iOException) {
      return true;
    } 
    switch (i) {
      case 11:
      case 12:
      case 20:
        return paramVector.contains(Shorts.valueOf((short)0));
      case 5:
      case 6:
      case 19:
      case 23:
        return paramVector.contains(Shorts.valueOf((short)1));
      case 3:
      case 4:
      case 22:
        return paramVector.contains(Shorts.valueOf((short)2));
      case 17:
        return paramVector.contains(Shorts.valueOf((short)3));
    } 
    return true;
  }
  
  public static boolean isValidCipherSuiteForVersion(int paramInt, ProtocolVersion paramProtocolVersion) {
    return getMinimumVersion(paramInt).isEqualOrEarlierVersionOf(paramProtocolVersion.getEquivalentTLSVersion());
  }
  
  public static Vector getUsableSignatureAlgorithms(Vector<SignatureAndHashAlgorithm> paramVector) {
    if (paramVector == null)
      return getAllSignatureAlgorithms(); 
    Vector<Short> vector = new Vector(4);
    vector.addElement(Shorts.valueOf((short)0));
    for (byte b = 0; b < paramVector.size(); b++) {
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = paramVector.elementAt(b);
      Short short_ = Shorts.valueOf(signatureAndHashAlgorithm.getSignature());
      if (!vector.contains(short_))
        vector.addElement(short_); 
    } 
    return vector;
  }
}
