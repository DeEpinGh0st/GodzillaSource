package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

public class ServerSRPParams {
  protected BigInteger N;
  
  protected BigInteger g;
  
  protected BigInteger B;
  
  protected byte[] s;
  
  public ServerSRPParams(BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte, BigInteger paramBigInteger3) {
    this.N = paramBigInteger1;
    this.g = paramBigInteger2;
    this.s = Arrays.clone(paramArrayOfbyte);
    this.B = paramBigInteger3;
  }
  
  public BigInteger getB() {
    return this.B;
  }
  
  public BigInteger getG() {
    return this.g;
  }
  
  public BigInteger getN() {
    return this.N;
  }
  
  public byte[] getS() {
    return this.s;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    TlsSRPUtils.writeSRPParameter(this.N, paramOutputStream);
    TlsSRPUtils.writeSRPParameter(this.g, paramOutputStream);
    TlsUtils.writeOpaque8(this.s, paramOutputStream);
    TlsSRPUtils.writeSRPParameter(this.B, paramOutputStream);
  }
  
  public static ServerSRPParams parse(InputStream paramInputStream) throws IOException {
    BigInteger bigInteger1 = TlsSRPUtils.readSRPParameter(paramInputStream);
    BigInteger bigInteger2 = TlsSRPUtils.readSRPParameter(paramInputStream);
    byte[] arrayOfByte = TlsUtils.readOpaque8(paramInputStream);
    BigInteger bigInteger3 = TlsSRPUtils.readSRPParameter(paramInputStream);
    return new ServerSRPParams(bigInteger1, bigInteger2, arrayOfByte, bigInteger3);
  }
}
