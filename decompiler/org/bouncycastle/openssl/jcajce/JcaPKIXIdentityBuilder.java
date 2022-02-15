package org.bouncycastle.openssl.jcajce;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkix.jcajce.JcaPKIXIdentity;

public class JcaPKIXIdentityBuilder {
  private JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();
  
  private JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
  
  public JcaPKIXIdentityBuilder setProvider(Provider paramProvider) {
    this.keyConverter = this.keyConverter.setProvider(paramProvider);
    this.certConverter = this.certConverter.setProvider(paramProvider);
    return this;
  }
  
  public JcaPKIXIdentityBuilder setProvider(String paramString) {
    this.keyConverter = this.keyConverter.setProvider(paramString);
    this.certConverter = this.certConverter.setProvider(paramString);
    return this;
  }
  
  public JcaPKIXIdentity build(File paramFile1, File paramFile2) throws IOException, CertificateException {
    checkFile(paramFile1);
    checkFile(paramFile2);
    FileInputStream fileInputStream1 = new FileInputStream(paramFile1);
    FileInputStream fileInputStream2 = new FileInputStream(paramFile2);
    JcaPKIXIdentity jcaPKIXIdentity = build(fileInputStream1, fileInputStream2);
    fileInputStream1.close();
    fileInputStream2.close();
    return jcaPKIXIdentity;
  }
  
  public JcaPKIXIdentity build(InputStream paramInputStream1, InputStream paramInputStream2) throws IOException, CertificateException {
    PrivateKey privateKey;
    PEMParser pEMParser1 = new PEMParser(new InputStreamReader(paramInputStream1));
    Object object1 = pEMParser1.readObject();
    if (object1 instanceof PEMKeyPair) {
      PEMKeyPair pEMKeyPair = (PEMKeyPair)object1;
      privateKey = this.keyConverter.getPrivateKey(pEMKeyPair.getPrivateKeyInfo());
    } else if (object1 instanceof PrivateKeyInfo) {
      privateKey = this.keyConverter.getPrivateKey((PrivateKeyInfo)object1);
    } else {
      throw new IOException("unrecognised private key file");
    } 
    PEMParser pEMParser2 = new PEMParser(new InputStreamReader(paramInputStream2));
    ArrayList<X509Certificate> arrayList = new ArrayList();
    Object object2;
    while ((object2 = pEMParser2.readObject()) != null)
      arrayList.add(this.certConverter.getCertificate((X509CertificateHolder)object2)); 
    return new JcaPKIXIdentity(privateKey, arrayList.<X509Certificate>toArray(new X509Certificate[arrayList.size()]));
  }
  
  private void checkFile(File paramFile) throws IOException {
    if (paramFile.canRead()) {
      if (paramFile.exists())
        throw new IOException("Unable to open file " + paramFile.getPath() + " for reading."); 
      throw new FileNotFoundException("Unable to open " + paramFile.getPath() + ": it does not exist.");
    } 
  }
}
