package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSMTKey;
import org.bouncycastle.util.Arrays;

public class BCXMSSMTPrivateKey implements PrivateKey, XMSSMTKey {
  private final ASN1ObjectIdentifier treeDigest;
  
  private final XMSSMTPrivateKeyParameters keyParams;
  
  public BCXMSSMTPrivateKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, XMSSMTPrivateKeyParameters paramXMSSMTPrivateKeyParameters) {
    this.treeDigest = paramASN1ObjectIdentifier;
    this.keyParams = paramXMSSMTPrivateKeyParameters;
  }
  
  public BCXMSSMTPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    XMSSMTKeyParams xMSSMTKeyParams = XMSSMTKeyParams.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
    this.treeDigest = xMSSMTKeyParams.getTreeDigest().getAlgorithm();
    XMSSPrivateKey xMSSPrivateKey = XMSSPrivateKey.getInstance(paramPrivateKeyInfo.parsePrivateKey());
    try {
      XMSSMTPrivateKeyParameters.Builder builder = (new XMSSMTPrivateKeyParameters.Builder(new XMSSMTParameters(xMSSMTKeyParams.getHeight(), xMSSMTKeyParams.getLayers(), DigestUtil.getDigest(this.treeDigest)))).withIndex(xMSSPrivateKey.getIndex()).withSecretKeySeed(xMSSPrivateKey.getSecretKeySeed()).withSecretKeyPRF(xMSSPrivateKey.getSecretKeyPRF()).withPublicSeed(xMSSPrivateKey.getPublicSeed()).withRoot(xMSSPrivateKey.getRoot());
      if (xMSSPrivateKey.getBdsState() != null)
        builder.withBDSState((BDSStateMap)XMSSUtil.deserialize(xMSSPrivateKey.getBdsState())); 
      this.keyParams = builder.build();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new IOException("ClassNotFoundException processing BDS state: " + classNotFoundException.getMessage());
    } 
  }
  
  public String getAlgorithm() {
    return "XMSSMT";
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, (ASN1Encodable)new XMSSMTKeyParams(this.keyParams.getParameters().getHeight(), this.keyParams.getParameters().getLayers(), new AlgorithmIdentifier(this.treeDigest)));
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)createKeyStructure());
      return privateKeyInfo.getEncoded();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  CipherParameters getKeyParams() {
    return (CipherParameters)this.keyParams;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof BCXMSSMTPrivateKey) {
      BCXMSSMTPrivateKey bCXMSSMTPrivateKey = (BCXMSSMTPrivateKey)paramObject;
      return (this.treeDigest.equals(bCXMSSMTPrivateKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), bCXMSSMTPrivateKey.keyParams.toByteArray()));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
  }
  
  private XMSSMTPrivateKey createKeyStructure() {
    byte[] arrayOfByte1 = this.keyParams.toByteArray();
    int i = this.keyParams.getParameters().getDigestSize();
    int j = this.keyParams.getParameters().getHeight();
    int k = (j + 7) / 8;
    int m = i;
    int n = i;
    int i1 = i;
    int i2 = i;
    int i3 = 0;
    int i4 = (int)XMSSUtil.bytesToXBigEndian(arrayOfByte1, i3, k);
    if (!XMSSUtil.isIndexValid(j, i4))
      throw new IllegalArgumentException("index out of bounds"); 
    i3 += k;
    byte[] arrayOfByte2 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i3, m);
    i3 += m;
    byte[] arrayOfByte3 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i3, n);
    i3 += n;
    byte[] arrayOfByte4 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i3, i1);
    i3 += i1;
    byte[] arrayOfByte5 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i3, i2);
    i3 += i2;
    byte[] arrayOfByte6 = XMSSUtil.extractBytesAtOffset(arrayOfByte1, i3, arrayOfByte1.length - i3);
    return new XMSSMTPrivateKey(i4, arrayOfByte2, arrayOfByte3, arrayOfByte4, arrayOfByte5, arrayOfByte6);
  }
  
  ASN1ObjectIdentifier getTreeDigestOID() {
    return this.treeDigest;
  }
  
  public int getHeight() {
    return this.keyParams.getParameters().getHeight();
  }
  
  public int getLayers() {
    return this.keyParams.getParameters().getLayers();
  }
  
  public String getTreeDigest() {
    return DigestUtil.getXMSSDigestName(this.treeDigest);
  }
}
