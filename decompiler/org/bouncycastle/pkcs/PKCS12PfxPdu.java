package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PKCS12PfxPdu {
  private Pfx pfx;
  
  private static Pfx parseBytes(byte[] paramArrayOfbyte) throws IOException {
    try {
      return Pfx.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
    } catch (ClassCastException classCastException) {
      throw new PKCSIOException("malformed data: " + classCastException.getMessage(), classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new PKCSIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  public PKCS12PfxPdu(Pfx paramPfx) {
    this.pfx = paramPfx;
  }
  
  public PKCS12PfxPdu(byte[] paramArrayOfbyte) throws IOException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public ContentInfo[] getContentInfos() {
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
    ContentInfo[] arrayOfContentInfo = new ContentInfo[aSN1Sequence.size()];
    for (byte b = 0; b != aSN1Sequence.size(); b++)
      arrayOfContentInfo[b] = ContentInfo.getInstance(aSN1Sequence.getObjectAt(b)); 
    return arrayOfContentInfo;
  }
  
  public boolean hasMac() {
    return (this.pfx.getMacData() != null);
  }
  
  public AlgorithmIdentifier getMacAlgorithmID() {
    MacData macData = this.pfx.getMacData();
    return (macData != null) ? macData.getMac().getAlgorithmId() : null;
  }
  
  public boolean isMacValid(PKCS12MacCalculatorBuilderProvider paramPKCS12MacCalculatorBuilderProvider, char[] paramArrayOfchar) throws PKCSException {
    if (hasMac()) {
      MacData macData = this.pfx.getMacData();
      MacDataGenerator macDataGenerator = new MacDataGenerator(paramPKCS12MacCalculatorBuilderProvider.get(new AlgorithmIdentifier(macData.getMac().getAlgorithmId().getAlgorithm(), (ASN1Encodable)new PKCS12PBEParams(macData.getSalt(), macData.getIterationCount().intValue()))));
      try {
        MacData macData1 = macDataGenerator.build(paramArrayOfchar, ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
        return Arrays.constantTimeAreEqual(macData1.getEncoded(), this.pfx.getMacData().getEncoded());
      } catch (IOException iOException) {
        throw new PKCSException("unable to process AuthSafe: " + iOException.getMessage());
      } 
    } 
    throw new IllegalStateException("no MAC present on PFX");
  }
  
  public Pfx toASN1Structure() {
    return this.pfx;
  }
  
  public byte[] getEncoded() throws IOException {
    return toASN1Structure().getEncoded();
  }
  
  public byte[] getEncoded(String paramString) throws IOException {
    return toASN1Structure().getEncoded(paramString);
  }
}
