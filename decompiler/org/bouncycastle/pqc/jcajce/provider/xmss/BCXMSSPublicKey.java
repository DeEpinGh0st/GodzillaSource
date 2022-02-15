package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSKey;
import org.bouncycastle.util.Arrays;

public class BCXMSSPublicKey implements PublicKey, XMSSKey {
  private final XMSSPublicKeyParameters keyParams;
  
  private final ASN1ObjectIdentifier treeDigest;
  
  public BCXMSSPublicKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, XMSSPublicKeyParameters paramXMSSPublicKeyParameters) {
    this.treeDigest = paramASN1ObjectIdentifier;
    this.keyParams = paramXMSSPublicKeyParameters;
  }
  
  public BCXMSSPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    XMSSKeyParams xMSSKeyParams = XMSSKeyParams.getInstance(paramSubjectPublicKeyInfo.getAlgorithm().getParameters());
    this.treeDigest = xMSSKeyParams.getTreeDigest().getAlgorithm();
    XMSSPublicKey xMSSPublicKey = XMSSPublicKey.getInstance(paramSubjectPublicKeyInfo.parsePublicKey());
    this.keyParams = (new XMSSPublicKeyParameters.Builder(new XMSSParameters(xMSSKeyParams.getHeight(), DigestUtil.getDigest(this.treeDigest)))).withPublicSeed(xMSSPublicKey.getPublicSeed()).withRoot(xMSSPublicKey.getRoot()).build();
  }
  
  public final String getAlgorithm() {
    return "XMSS";
  }
  
  public byte[] getEncoded() {
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, (ASN1Encodable)new XMSSKeyParams(this.keyParams.getParameters().getHeight(), new AlgorithmIdentifier(this.treeDigest)));
      SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, (ASN1Encodable)new XMSSPublicKey(this.keyParams.getPublicSeed(), this.keyParams.getRoot()));
      return subjectPublicKeyInfo.getEncoded();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  CipherParameters getKeyParams() {
    return (CipherParameters)this.keyParams;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof BCXMSSPublicKey) {
      BCXMSSPublicKey bCXMSSPublicKey = (BCXMSSPublicKey)paramObject;
      return (this.treeDigest.equals(bCXMSSPublicKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), bCXMSSPublicKey.keyParams.toByteArray()));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
  }
  
  public int getHeight() {
    return this.keyParams.getParameters().getHeight();
  }
  
  public String getTreeDigest() {
    return DigestUtil.getXMSSDigestName(this.treeDigest);
  }
}
