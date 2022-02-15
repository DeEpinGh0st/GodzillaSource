package org.bouncycastle.openssl;

import java.io.IOException;
import org.bouncycastle.operator.OperatorCreationException;

public class PEMEncryptedKeyPair {
  private final String dekAlgName;
  
  private final byte[] iv;
  
  private final byte[] keyBytes;
  
  private final PEMKeyPairParser parser;
  
  PEMEncryptedKeyPair(String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, PEMKeyPairParser paramPEMKeyPairParser) {
    this.dekAlgName = paramString;
    this.iv = paramArrayOfbyte1;
    this.keyBytes = paramArrayOfbyte2;
    this.parser = paramPEMKeyPairParser;
  }
  
  public PEMKeyPair decryptKeyPair(PEMDecryptorProvider paramPEMDecryptorProvider) throws IOException {
    try {
      PEMDecryptor pEMDecryptor = paramPEMDecryptorProvider.get(this.dekAlgName);
      return this.parser.parse(pEMDecryptor.decrypt(this.keyBytes, this.iv));
    } catch (IOException iOException) {
      throw iOException;
    } catch (OperatorCreationException operatorCreationException) {
      throw new PEMException("cannot create extraction operator: " + operatorCreationException.getMessage(), operatorCreationException);
    } catch (Exception exception) {
      throw new PEMException("exception processing key pair: " + exception.getMessage(), exception);
    } 
  }
}
