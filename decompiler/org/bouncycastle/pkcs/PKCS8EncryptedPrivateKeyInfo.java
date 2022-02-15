package org.bouncycastle.pkcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.util.io.Streams;

public class PKCS8EncryptedPrivateKeyInfo {
  private EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
  
  private static EncryptedPrivateKeyInfo parseBytes(byte[] paramArrayOfbyte) throws IOException {
    try {
      return EncryptedPrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
    } catch (ClassCastException classCastException) {
      throw new PKCSIOException("malformed data: " + classCastException.getMessage(), classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new PKCSIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  public PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo paramEncryptedPrivateKeyInfo) {
    this.encryptedPrivateKeyInfo = paramEncryptedPrivateKeyInfo;
  }
  
  public PKCS8EncryptedPrivateKeyInfo(byte[] paramArrayOfbyte) throws IOException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public EncryptedPrivateKeyInfo toASN1Structure() {
    return this.encryptedPrivateKeyInfo;
  }
  
  public byte[] getEncoded() throws IOException {
    return this.encryptedPrivateKeyInfo.getEncoded();
  }
  
  public PrivateKeyInfo decryptPrivateKeyInfo(InputDecryptorProvider paramInputDecryptorProvider) throws PKCSException {
    try {
      InputDecryptor inputDecryptor = paramInputDecryptorProvider.get(this.encryptedPrivateKeyInfo.getEncryptionAlgorithm());
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.encryptedPrivateKeyInfo.getEncryptedData());
      return PrivateKeyInfo.getInstance(Streams.readAll(inputDecryptor.getInputStream(byteArrayInputStream)));
    } catch (Exception exception) {
      throw new PKCSException("unable to read encrypted data: " + exception.getMessage(), exception);
    } 
  }
}
