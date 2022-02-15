package org.bouncycastle.pqc.crypto.mceliece;

public class McElieceCCA2Parameters extends McElieceParameters {
  private final String digest;
  
  public McElieceCCA2Parameters() {
    this(11, 50, "SHA-256");
  }
  
  public McElieceCCA2Parameters(String paramString) {
    this(11, 50, paramString);
  }
  
  public McElieceCCA2Parameters(int paramInt) {
    this(paramInt, "SHA-256");
  }
  
  public McElieceCCA2Parameters(int paramInt, String paramString) {
    super(paramInt);
    this.digest = paramString;
  }
  
  public McElieceCCA2Parameters(int paramInt1, int paramInt2) {
    this(paramInt1, paramInt2, "SHA-256");
  }
  
  public McElieceCCA2Parameters(int paramInt1, int paramInt2, String paramString) {
    super(paramInt1, paramInt2);
    this.digest = paramString;
  }
  
  public McElieceCCA2Parameters(int paramInt1, int paramInt2, int paramInt3) {
    this(paramInt1, paramInt2, paramInt3, "SHA-256");
  }
  
  public McElieceCCA2Parameters(int paramInt1, int paramInt2, int paramInt3, String paramString) {
    super(paramInt1, paramInt2, paramInt3);
    this.digest = paramString;
  }
  
  public String getDigest() {
    return this.digest;
  }
}
