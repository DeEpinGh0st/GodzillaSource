package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.util.Arrays;

public final class SessionParameters {
  private int cipherSuite;
  
  private short compressionAlgorithm;
  
  private byte[] masterSecret;
  
  private Certificate peerCertificate;
  
  private byte[] pskIdentity = null;
  
  private byte[] srpIdentity = null;
  
  private byte[] encodedServerExtensions;
  
  private SessionParameters(int paramInt, short paramShort, byte[] paramArrayOfbyte1, Certificate paramCertificate, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) {
    this.cipherSuite = paramInt;
    this.compressionAlgorithm = paramShort;
    this.masterSecret = Arrays.clone(paramArrayOfbyte1);
    this.peerCertificate = paramCertificate;
    this.pskIdentity = Arrays.clone(paramArrayOfbyte2);
    this.srpIdentity = Arrays.clone(paramArrayOfbyte3);
    this.encodedServerExtensions = paramArrayOfbyte4;
  }
  
  public void clear() {
    if (this.masterSecret != null)
      Arrays.fill(this.masterSecret, (byte)0); 
  }
  
  public SessionParameters copy() {
    return new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions);
  }
  
  public int getCipherSuite() {
    return this.cipherSuite;
  }
  
  public short getCompressionAlgorithm() {
    return this.compressionAlgorithm;
  }
  
  public byte[] getMasterSecret() {
    return this.masterSecret;
  }
  
  public Certificate getPeerCertificate() {
    return this.peerCertificate;
  }
  
  public byte[] getPskIdentity() {
    return this.pskIdentity;
  }
  
  public byte[] getPSKIdentity() {
    return this.pskIdentity;
  }
  
  public byte[] getSRPIdentity() {
    return this.srpIdentity;
  }
  
  public Hashtable readServerExtensions() throws IOException {
    if (this.encodedServerExtensions == null)
      return null; 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.encodedServerExtensions);
    return TlsProtocol.readExtensions(byteArrayInputStream);
  }
  
  public static final class Builder {
    private int cipherSuite = -1;
    
    private short compressionAlgorithm = -1;
    
    private byte[] masterSecret = null;
    
    private Certificate peerCertificate = null;
    
    private byte[] pskIdentity = null;
    
    private byte[] srpIdentity = null;
    
    private byte[] encodedServerExtensions = null;
    
    public SessionParameters build() {
      validate((this.cipherSuite >= 0), "cipherSuite");
      validate((this.compressionAlgorithm >= 0), "compressionAlgorithm");
      validate((this.masterSecret != null), "masterSecret");
      return new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions);
    }
    
    public Builder setCipherSuite(int param1Int) {
      this.cipherSuite = param1Int;
      return this;
    }
    
    public Builder setCompressionAlgorithm(short param1Short) {
      this.compressionAlgorithm = param1Short;
      return this;
    }
    
    public Builder setMasterSecret(byte[] param1ArrayOfbyte) {
      this.masterSecret = param1ArrayOfbyte;
      return this;
    }
    
    public Builder setPeerCertificate(Certificate param1Certificate) {
      this.peerCertificate = param1Certificate;
      return this;
    }
    
    public Builder setPskIdentity(byte[] param1ArrayOfbyte) {
      this.pskIdentity = param1ArrayOfbyte;
      return this;
    }
    
    public Builder setPSKIdentity(byte[] param1ArrayOfbyte) {
      this.pskIdentity = param1ArrayOfbyte;
      return this;
    }
    
    public Builder setSRPIdentity(byte[] param1ArrayOfbyte) {
      this.srpIdentity = param1ArrayOfbyte;
      return this;
    }
    
    public Builder setServerExtensions(Hashtable param1Hashtable) throws IOException {
      if (param1Hashtable == null) {
        this.encodedServerExtensions = null;
      } else {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TlsProtocol.writeExtensions(byteArrayOutputStream, param1Hashtable);
        this.encodedServerExtensions = byteArrayOutputStream.toByteArray();
      } 
      return this;
    }
    
    private void validate(boolean param1Boolean, String param1String) {
      if (!param1Boolean)
        throw new IllegalStateException("Required session parameter '" + param1String + "' not configured"); 
    }
  }
}
