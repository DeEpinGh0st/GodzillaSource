package org.bouncycastle.pqc.jcajce.provider.sphincs;

import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.SPHINCSKey;
import org.bouncycastle.util.Arrays;

public class BCSphincs256PrivateKey implements PrivateKey, SPHINCSKey {
  private static final long serialVersionUID = 1L;
  
  private final ASN1ObjectIdentifier treeDigest;
  
  private final SPHINCSPrivateKeyParameters params;
  
  public BCSphincs256PrivateKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, SPHINCSPrivateKeyParameters paramSPHINCSPrivateKeyParameters) {
    this.treeDigest = paramASN1ObjectIdentifier;
    this.params = paramSPHINCSPrivateKeyParameters;
  }
  
  public BCSphincs256PrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    this.treeDigest = SPHINCS256KeyParams.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters()).getTreeDigest().getAlgorithm();
    this.params = new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(paramPrivateKeyInfo.parsePrivateKey()).getOctets());
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof BCSphincs256PrivateKey) {
      BCSphincs256PrivateKey bCSphincs256PrivateKey = (BCSphincs256PrivateKey)paramObject;
      return (this.treeDigest.equals(bCSphincs256PrivateKey.treeDigest) && Arrays.areEqual(this.params.getKeyData(), bCSphincs256PrivateKey.params.getKeyData()));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.params.getKeyData());
  }
  
  public final String getAlgorithm() {
    return "SPHINCS-256";
  }
  
  public byte[] getEncoded() {
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, (ASN1Encodable)new SPHINCS256KeyParams(new AlgorithmIdentifier(this.treeDigest)));
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)new DEROctetString(this.params.getKeyData()));
      return privateKeyInfo.getEncoded();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getKeyData() {
    return this.params.getKeyData();
  }
  
  CipherParameters getKeyParams() {
    return (CipherParameters)this.params;
  }
}
