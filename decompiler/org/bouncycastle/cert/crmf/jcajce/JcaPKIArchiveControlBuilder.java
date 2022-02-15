package org.bouncycastle.cert.crmf.jcajce;

import java.security.PrivateKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.crmf.PKIArchiveControlBuilder;

public class JcaPKIArchiveControlBuilder extends PKIArchiveControlBuilder {
  public JcaPKIArchiveControlBuilder(PrivateKey paramPrivateKey, X500Name paramX500Name) {
    this(paramPrivateKey, new GeneralName(paramX500Name));
  }
  
  public JcaPKIArchiveControlBuilder(PrivateKey paramPrivateKey, X500Principal paramX500Principal) {
    this(paramPrivateKey, X500Name.getInstance(paramX500Principal.getEncoded()));
  }
  
  public JcaPKIArchiveControlBuilder(PrivateKey paramPrivateKey, GeneralName paramGeneralName) {
    super(PrivateKeyInfo.getInstance(paramPrivateKey.getEncoded()), paramGeneralName);
  }
}
