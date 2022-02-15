package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;

public class CMSAbsentContent implements CMSTypedData, CMSReadable {
  private final ASN1ObjectIdentifier type;
  
  public CMSAbsentContent() {
    this(CMSObjectIdentifiers.data);
  }
  
  public CMSAbsentContent(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.type = paramASN1ObjectIdentifier;
  }
  
  public InputStream getInputStream() {
    return null;
  }
  
  public void write(OutputStream paramOutputStream) throws IOException, CMSException {}
  
  public Object getContent() {
    return null;
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.type;
  }
}
