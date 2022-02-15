package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.util.Strings;

public final class ProtocolVersion {
  public static final ProtocolVersion SSLv3 = new ProtocolVersion(768, "SSL 3.0");
  
  public static final ProtocolVersion TLSv10 = new ProtocolVersion(769, "TLS 1.0");
  
  public static final ProtocolVersion TLSv11 = new ProtocolVersion(770, "TLS 1.1");
  
  public static final ProtocolVersion TLSv12 = new ProtocolVersion(771, "TLS 1.2");
  
  public static final ProtocolVersion DTLSv10 = new ProtocolVersion(65279, "DTLS 1.0");
  
  public static final ProtocolVersion DTLSv12 = new ProtocolVersion(65277, "DTLS 1.2");
  
  private int version;
  
  private String name;
  
  private ProtocolVersion(int paramInt, String paramString) {
    this.version = paramInt & 0xFFFF;
    this.name = paramString;
  }
  
  public int getFullVersion() {
    return this.version;
  }
  
  public int getMajorVersion() {
    return this.version >> 8;
  }
  
  public int getMinorVersion() {
    return this.version & 0xFF;
  }
  
  public boolean isDTLS() {
    return (getMajorVersion() == 254);
  }
  
  public boolean isSSL() {
    return (this == SSLv3);
  }
  
  public boolean isTLS() {
    return (getMajorVersion() == 3);
  }
  
  public ProtocolVersion getEquivalentTLSVersion() {
    return !isDTLS() ? this : ((this == DTLSv10) ? TLSv11 : TLSv12);
  }
  
  public boolean isEqualOrEarlierVersionOf(ProtocolVersion paramProtocolVersion) {
    if (getMajorVersion() != paramProtocolVersion.getMajorVersion())
      return false; 
    int i = paramProtocolVersion.getMinorVersion() - getMinorVersion();
    return isDTLS() ? ((i <= 0)) : ((i >= 0));
  }
  
  public boolean isLaterVersionOf(ProtocolVersion paramProtocolVersion) {
    if (getMajorVersion() != paramProtocolVersion.getMajorVersion())
      return false; 
    int i = paramProtocolVersion.getMinorVersion() - getMinorVersion();
    return isDTLS() ? ((i > 0)) : ((i < 0));
  }
  
  public boolean equals(Object paramObject) {
    return (this == paramObject || (paramObject instanceof ProtocolVersion && equals((ProtocolVersion)paramObject)));
  }
  
  public boolean equals(ProtocolVersion paramProtocolVersion) {
    return (paramProtocolVersion != null && this.version == paramProtocolVersion.version);
  }
  
  public int hashCode() {
    return this.version;
  }
  
  public static ProtocolVersion get(int paramInt1, int paramInt2) throws IOException {
    switch (paramInt1) {
      case 3:
        switch (paramInt2) {
          case 0:
            return SSLv3;
          case 1:
            return TLSv10;
          case 2:
            return TLSv11;
          case 3:
            return TLSv12;
        } 
        return getUnknownVersion(paramInt1, paramInt2, "TLS");
      case 254:
        switch (paramInt2) {
          case 255:
            return DTLSv10;
          case 254:
            throw new TlsFatalAlert((short)47);
          case 253:
            return DTLSv12;
        } 
        return getUnknownVersion(paramInt1, paramInt2, "DTLS");
    } 
    throw new TlsFatalAlert((short)47);
  }
  
  public String toString() {
    return this.name;
  }
  
  private static ProtocolVersion getUnknownVersion(int paramInt1, int paramInt2, String paramString) throws IOException {
    TlsUtils.checkUint8(paramInt1);
    TlsUtils.checkUint8(paramInt2);
    int i = paramInt1 << 8 | paramInt2;
    String str = Strings.toUpperCase(Integer.toHexString(0x10000 | i).substring(1));
    return new ProtocolVersion(i, paramString + " 0x" + str);
  }
}
