package org.bouncycastle.cms;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.util.io.Streams;

public class CMSTypedStream {
  private static final int BUF_SIZ = 32768;
  
  private final ASN1ObjectIdentifier _oid;
  
  protected InputStream _in;
  
  public CMSTypedStream(InputStream paramInputStream) {
    this(PKCSObjectIdentifiers.data.getId(), paramInputStream, 32768);
  }
  
  public CMSTypedStream(String paramString, InputStream paramInputStream) {
    this(new ASN1ObjectIdentifier(paramString), paramInputStream, 32768);
  }
  
  public CMSTypedStream(String paramString, InputStream paramInputStream, int paramInt) {
    this(new ASN1ObjectIdentifier(paramString), paramInputStream, paramInt);
  }
  
  public CMSTypedStream(ASN1ObjectIdentifier paramASN1ObjectIdentifier, InputStream paramInputStream) {
    this(paramASN1ObjectIdentifier, paramInputStream, 32768);
  }
  
  public CMSTypedStream(ASN1ObjectIdentifier paramASN1ObjectIdentifier, InputStream paramInputStream, int paramInt) {
    this._oid = paramASN1ObjectIdentifier;
    this._in = new FullReaderStream(new BufferedInputStream(paramInputStream, paramInt));
  }
  
  protected CMSTypedStream(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this._oid = paramASN1ObjectIdentifier;
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this._oid;
  }
  
  public InputStream getContentStream() {
    return this._in;
  }
  
  public void drain() throws IOException {
    Streams.drain(this._in);
    this._in.close();
  }
  
  private static class FullReaderStream extends FilterInputStream {
    FullReaderStream(InputStream param1InputStream) {
      super(param1InputStream);
    }
    
    public int read(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      int i = Streams.readFully(this.in, param1ArrayOfbyte, param1Int1, param1Int2);
      return (i > 0) ? i : -1;
    }
  }
}
