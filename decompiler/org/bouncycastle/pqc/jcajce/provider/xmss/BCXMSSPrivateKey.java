package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSKey;
import org.bouncycastle.util.Arrays;

public class BCXMSSPrivateKey implements PrivateKey, XMSSKey {
  private final XMSSPrivateKeyParameters keyParams;
  
  private final ASN1ObjectIdentifier treeDigest;
  
  public BCXMSSPrivateKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, XMSSPrivateKeyParameters paramXMSSPrivateKeyParameters) {
    this.treeDigest = paramASN1ObjectIdentifier;
    this.keyParams = paramXMSSPrivateKeyParameters;
  }
  
  public BCXMSSPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    XMSSKeyParams xMSSKeyParams = XMSSKeyParams.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
    this.treeDigest = xMSSKeyParams.getTreeDigest().getAlgorithm();
    XMSSPrivateKey xMSSPrivateKey = XMSSPrivateKey.getInstance(paramPrivateKeyInfo.parsePrivateKey());
    try {
      XMSSPrivateKeyParameters.Builder builder = (new XMSSPrivateKeyParameters.Builder(new XMSSParameters(xMSSKeyParams.getHeight(), DigestUtil.getDigest(this.treeDigest)))).withIndex(xMSSPrivateKey.getIndex()).withSecretKeySeed(xMSSPrivateKey.getSecretKeySeed()).withSecretKeyPRF(xMSSPrivateKey.getSecretKeyPRF()).withPublicSeed(xMSSPrivateKey.getPublicSeed()).withRoot(xMSSPrivateKey.getRoot());
      if (xMSSPrivateKey.getBdsState() != null)
        builder.withBDSState((BDS)XMSSUtil.deserialize(xMSSPrivateKey.getBdsState())); 
      this.keyParams = builder.build();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new IOException("ClassNotFoundException processing BDS state: " + classNotFoundException.getMessage());
    } 
  }
  
  public String getAlgorithm() {
    return "XMSS";
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, (ASN1Encodable)new XMSSKeyParams(this.keyParams.getParameters().getHeight(), new AlgorithmIdentifier(this.treeDigest)));
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)createKeyStructure());
      return privateKeyInfo.getEncoded();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof BCXMSSPrivateKey) {
      BCXMSSPrivateKey bCXMSSPrivateKey = (BCXMSSPrivateKey)paramObject;
      return (this.treeDigest.equals(bCXMSSPrivateKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), bCXMSSPrivateKey.keyParams.toByteArray()));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
  }
  
  CipherParameters getKeyParams() {
    return (CipherParameters)this.keyParams;
  }
  
  private XMSSPrivateKey createKeyStructure() {
    byte[] arrayOfByte1 = this.keyParams.toByteArray();
    int i = this.keyParams.getParameters().getDigestSize();
    int j = this.keyParams.getParameters().getHeight();
    byte b = 4;
    int k = i;
    int m = i;
    int n = i;
    int i1 = i;
    int i2 = 0;
    int i3 = (int)XMSSUtil.bytesToXBigEndian(arrayOfByte1, i2, b);
    if (!XMSSUtil.isIndexValid(j, i3))
      throw new IllegalArgumentException("index out of bounds"); 
    i2 += b;
    byte[] arrayOfByte2 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i2, k);
    i2 += k;
    byte[] arrayOfByte3 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i2, m);
    i2 += m;
    byte[] arrayOfByte4 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i2, n);
    i2 += n;
    byte[] arrayOfByte5 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i2, i1);
    i2 += i1;
    byte[] arrayOfByte6 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i2, arrayOfByte1.length - i2);
    return new XMSSPrivateKey(i3, arrayOfByte2, arrayOfByte3, arrayOfByte4, arrayOfByte5, arrayOfByte6);
  }
  
  ASN1ObjectIdentifier getTreeDigestOID() {
    return this.treeDigest;
  }
  
  public int getHeight() {
    return this.keyParams.getParameters().getHeight();
  }
  
  public String getTreeDigest() {
    return DigestUtil.getXMSSDigestName(this.treeDigest);
  }
}
