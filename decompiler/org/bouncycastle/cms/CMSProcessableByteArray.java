package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class CMSProcessableByteArray implements CMSTypedData, CMSReadable {
  private final ASN1ObjectIdentifier type;
  
  private final byte[] bytes;
  
  public CMSProcessableByteArray(byte[] paramArrayOfbyte) {
    this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), paramArrayOfbyte);
  }
  
  public CMSProcessableByteArray(ASN1ObjectIdentifier paramASN1ObjectIdentifier, byte[] paramArrayOfbyte) {
    this.type = paramASN1ObjectIdentifier;
    this.bytes = paramArrayOfbyte;
  }
  
  public InputStream getInputStream() {
    return new ByteArrayInputStream(this.bytes);
  }
  
  public void write(OutputStream paramOutputStream) throws IOException, CMSException {
    paramOutputStream.write(this.bytes);
  }
  
  public Object getContent() {
    return Arrays.clone(this.bytes);
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.type;
  }
}
