package org.bouncycastle.pkcs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.operator.OutputEncryptor;

public class PKCS8EncryptedPrivateKeyInfoBuilder {
  private PrivateKeyInfo privateKeyInfo;
  
  public PKCS8EncryptedPrivateKeyInfoBuilder(byte[] paramArrayOfbyte) {
    this(PrivateKeyInfo.getInstance(paramArrayOfbyte));
  }
  
  public PKCS8EncryptedPrivateKeyInfoBuilder(PrivateKeyInfo paramPrivateKeyInfo) {
    this.privateKeyInfo = paramPrivateKeyInfo;
  }
  
  public PKCS8EncryptedPrivateKeyInfo build(OutputEncryptor paramOutputEncryptor) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      OutputStream outputStream = paramOutputEncryptor.getOutputStream(byteArrayOutputStream);
      outputStream.write(this.privateKeyInfo.getEncoded());
      outputStream.close();
      return new PKCS8EncryptedPrivateKeyInfo(new EncryptedPrivateKeyInfo(paramOutputEncryptor.getAlgorithmIdentifier(), byteArrayOutputStream.toByteArray()));
    } catch (IOException iOException) {
      throw new IllegalStateException("cannot encode privateKeyInfo");
    } 
  }
}
