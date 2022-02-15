package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class BcDigestCalculatorProvider implements DigestCalculatorProvider {
  private BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;
  
  public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
    ExtendedDigest extendedDigest = this.digestProvider.get(algorithm);
    final DigestOutputStream stream = new DigestOutputStream((Digest)extendedDigest);
    return new DigestCalculator() {
        public AlgorithmIdentifier getAlgorithmIdentifier() {
          return algorithm;
        }
        
        public OutputStream getOutputStream() {
          return stream;
        }
        
        public byte[] getDigest() {
          return stream.getDigest();
        }
      };
  }
  
  private class DigestOutputStream extends OutputStream {
    private Digest dig;
    
    DigestOutputStream(Digest param1Digest) {
      this.dig = param1Digest;
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      this.dig.update(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    public void write(byte[] param1ArrayOfbyte) throws IOException {
      this.dig.update(param1ArrayOfbyte, 0, param1ArrayOfbyte.length);
    }
    
    public void write(int param1Int) throws IOException {
      this.dig.update((byte)param1Int);
    }
    
    byte[] getDigest() {
      byte[] arrayOfByte = new byte[this.dig.getDigestSize()];
      this.dig.doFinal(arrayOfByte, 0);
      return arrayOfByte;
    }
  }
}
