package org.bouncycastle.cms.jcajce;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.util.io.StreamOverflowException;

public class ZlibExpanderProvider implements InputExpanderProvider {
  private final long limit = -1L;
  
  public ZlibExpanderProvider() {}
  
  public ZlibExpanderProvider(long paramLong) {}
  
  public InputExpander get(final AlgorithmIdentifier algorithm) {
    return new InputExpander() {
        public AlgorithmIdentifier getAlgorithmIdentifier() {
          return algorithm;
        }
        
        public InputStream getInputStream(InputStream param1InputStream) {
          ZlibExpanderProvider.LimitedInputStream limitedInputStream;
          InflaterInputStream inflaterInputStream = new InflaterInputStream(param1InputStream);
          if (ZlibExpanderProvider.this.limit >= 0L)
            limitedInputStream = new ZlibExpanderProvider.LimitedInputStream(inflaterInputStream, ZlibExpanderProvider.this.limit); 
          return limitedInputStream;
        }
      };
  }
  
  private static class LimitedInputStream extends FilterInputStream {
    private long remaining;
    
    public LimitedInputStream(InputStream param1InputStream, long param1Long) {
      super(param1InputStream);
      this.remaining = param1Long;
    }
    
    public int read() throws IOException {
      if (this.remaining >= 0L) {
        int i = this.in.read();
        if (i < 0 || --this.remaining >= 0L)
          return i; 
      } 
      throw new StreamOverflowException("expanded byte limit exceeded");
    }
    
    public int read(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      if (param1Int2 < 1)
        return super.read(param1ArrayOfbyte, param1Int1, param1Int2); 
      if (this.remaining < 1L) {
        read();
        return -1;
      } 
      int i = (this.remaining > param1Int2) ? param1Int2 : (int)this.remaining;
      int j = this.in.read(param1ArrayOfbyte, param1Int1, i);
      if (j > 0)
        this.remaining -= j; 
      return j;
    }
  }
}
