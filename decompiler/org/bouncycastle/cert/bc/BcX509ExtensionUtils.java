package org.bouncycastle.cert.bc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.operator.DigestCalculator;

public class BcX509ExtensionUtils extends X509ExtensionUtils {
  public BcX509ExtensionUtils() {
    super(new SHA1DigestCalculator(null));
  }
  
  public BcX509ExtensionUtils(DigestCalculator paramDigestCalculator) {
    super(paramDigestCalculator);
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(AsymmetricKeyParameter paramAsymmetricKeyParameter) throws IOException {
    return createAuthorityKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(paramAsymmetricKeyParameter));
  }
  
  public SubjectKeyIdentifier createSubjectKeyIdentifier(AsymmetricKeyParameter paramAsymmetricKeyParameter) throws IOException {
    return createSubjectKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(paramAsymmetricKeyParameter));
  }
  
  private static class SHA1DigestCalculator implements DigestCalculator {
    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    
    private SHA1DigestCalculator() {}
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
      return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
    }
    
    public OutputStream getOutputStream() {
      return this.bOut;
    }
    
    public byte[] getDigest() {
      byte[] arrayOfByte1 = this.bOut.toByteArray();
      this.bOut.reset();
      SHA1Digest sHA1Digest = new SHA1Digest();
      sHA1Digest.update(arrayOfByte1, 0, arrayOfByte1.length);
      byte[] arrayOfByte2 = new byte[sHA1Digest.getDigestSize()];
      sHA1Digest.doFinal(arrayOfByte2, 0);
      return arrayOfByte2;
    }
  }
}
