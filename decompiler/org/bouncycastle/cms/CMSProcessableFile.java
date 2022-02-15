package org.bouncycastle.cms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;

public class CMSProcessableFile implements CMSTypedData, CMSReadable {
  private static final int DEFAULT_BUF_SIZE = 32768;
  
  private final ASN1ObjectIdentifier type;
  
  private final File file;
  
  private final byte[] buf;
  
  public CMSProcessableFile(File paramFile) {
    this(paramFile, 32768);
  }
  
  public CMSProcessableFile(File paramFile, int paramInt) {
    this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), paramFile, paramInt);
  }
  
  public CMSProcessableFile(ASN1ObjectIdentifier paramASN1ObjectIdentifier, File paramFile, int paramInt) {
    this.type = paramASN1ObjectIdentifier;
    this.file = paramFile;
    this.buf = new byte[paramInt];
  }
  
  public InputStream getInputStream() throws IOException, CMSException {
    return new BufferedInputStream(new FileInputStream(this.file), 32768);
  }
  
  public void write(OutputStream paramOutputStream) throws IOException, CMSException {
    FileInputStream fileInputStream = new FileInputStream(this.file);
    int i;
    while ((i = fileInputStream.read(this.buf, 0, this.buf.length)) > 0)
      paramOutputStream.write(this.buf, 0, i); 
    fileInputStream.close();
  }
  
  public Object getContent() {
    return this.file;
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.type;
  }
}
