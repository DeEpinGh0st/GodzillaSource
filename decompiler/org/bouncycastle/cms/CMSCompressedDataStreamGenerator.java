package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.operator.OutputCompressor;

public class CMSCompressedDataStreamGenerator {
  public static final String ZLIB = "1.2.840.113549.1.9.16.3.8";
  
  private int _bufferSize;
  
  public void setBufferSize(int paramInt) {
    this._bufferSize = paramInt;
  }
  
  public OutputStream open(OutputStream paramOutputStream, OutputCompressor paramOutputCompressor) throws IOException {
    return open(CMSObjectIdentifiers.data, paramOutputStream, paramOutputCompressor);
  }
  
  public OutputStream open(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OutputStream paramOutputStream, OutputCompressor paramOutputCompressor) throws IOException {
    BERSequenceGenerator bERSequenceGenerator1 = new BERSequenceGenerator(paramOutputStream);
    bERSequenceGenerator1.addObject((ASN1Encodable)CMSObjectIdentifiers.compressedData);
    BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator1.getRawOutputStream(), 0, true);
    bERSequenceGenerator2.addObject((ASN1Encodable)new ASN1Integer(0L));
    bERSequenceGenerator2.addObject((ASN1Encodable)paramOutputCompressor.getAlgorithmIdentifier());
    BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
    bERSequenceGenerator3.addObject((ASN1Encodable)paramASN1ObjectIdentifier);
    OutputStream outputStream = CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, true, this._bufferSize);
    return new CmsCompressedOutputStream(paramOutputCompressor.getOutputStream(outputStream), bERSequenceGenerator1, bERSequenceGenerator2, bERSequenceGenerator3);
  }
  
  private class CmsCompressedOutputStream extends OutputStream {
    private OutputStream _out;
    
    private BERSequenceGenerator _sGen;
    
    private BERSequenceGenerator _cGen;
    
    private BERSequenceGenerator _eiGen;
    
    CmsCompressedOutputStream(OutputStream param1OutputStream, BERSequenceGenerator param1BERSequenceGenerator1, BERSequenceGenerator param1BERSequenceGenerator2, BERSequenceGenerator param1BERSequenceGenerator3) {
      this._out = param1OutputStream;
      this._sGen = param1BERSequenceGenerator1;
      this._cGen = param1BERSequenceGenerator2;
      this._eiGen = param1BERSequenceGenerator3;
    }
    
    public void write(int param1Int) throws IOException {
      this._out.write(param1Int);
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      this._out.write(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    public void write(byte[] param1ArrayOfbyte) throws IOException {
      this._out.write(param1ArrayOfbyte);
    }
    
    public void close() throws IOException {
      this._out.close();
      this._eiGen.close();
      this._cGen.close();
      this._sGen.close();
    }
  }
}
