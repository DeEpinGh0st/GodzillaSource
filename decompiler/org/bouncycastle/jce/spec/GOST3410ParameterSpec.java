package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410NamedParameters;
import org.bouncycastle.asn1.cryptopro.GOST3410ParamSetParameters;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.jce.interfaces.GOST3410Params;

public class GOST3410ParameterSpec implements AlgorithmParameterSpec, GOST3410Params {
  private GOST3410PublicKeyParameterSetSpec keyParameters;
  
  private String keyParamSetOID;
  
  private String digestParamSetOID;
  
  private String encryptionParamSetOID;
  
  public GOST3410ParameterSpec(String paramString1, String paramString2, String paramString3) {
    GOST3410ParamSetParameters gOST3410ParamSetParameters = null;
    try {
      gOST3410ParamSetParameters = GOST3410NamedParameters.getByOID(new ASN1ObjectIdentifier(paramString1));
    } catch (IllegalArgumentException illegalArgumentException) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = GOST3410NamedParameters.getOID(paramString1);
      if (aSN1ObjectIdentifier != null) {
        paramString1 = aSN1ObjectIdentifier.getId();
        gOST3410ParamSetParameters = GOST3410NamedParameters.getByOID(aSN1ObjectIdentifier);
      } 
    } 
    if (gOST3410ParamSetParameters == null)
      throw new IllegalArgumentException("no key parameter set for passed in name/OID."); 
    this.keyParameters = new GOST3410PublicKeyParameterSetSpec(gOST3410ParamSetParameters.getP(), gOST3410ParamSetParameters.getQ(), gOST3410ParamSetParameters.getA());
    this.keyParamSetOID = paramString1;
    this.digestParamSetOID = paramString2;
    this.encryptionParamSetOID = paramString3;
  }
  
  public GOST3410ParameterSpec(String paramString1, String paramString2) {
    this(paramString1, paramString2, null);
  }
  
  public GOST3410ParameterSpec(String paramString) {
    this(paramString, CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId(), null);
  }
  
  public GOST3410ParameterSpec(GOST3410PublicKeyParameterSetSpec paramGOST3410PublicKeyParameterSetSpec) {
    this.keyParameters = paramGOST3410PublicKeyParameterSetSpec;
    this.digestParamSetOID = CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId();
    this.encryptionParamSetOID = null;
  }
  
  public String getPublicKeyParamSetOID() {
    return this.keyParamSetOID;
  }
  
  public GOST3410PublicKeyParameterSetSpec getPublicKeyParameters() {
    return this.keyParameters;
  }
  
  public String getDigestParamSetOID() {
    return this.digestParamSetOID;
  }
  
  public String getEncryptionParamSetOID() {
    return this.encryptionParamSetOID;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof GOST3410ParameterSpec) {
      GOST3410ParameterSpec gOST3410ParameterSpec = (GOST3410ParameterSpec)paramObject;
      return (this.keyParameters.equals(gOST3410ParameterSpec.keyParameters) && this.digestParamSetOID.equals(gOST3410ParameterSpec.digestParamSetOID) && (this.encryptionParamSetOID == gOST3410ParameterSpec.encryptionParamSetOID || (this.encryptionParamSetOID != null && this.encryptionParamSetOID.equals(gOST3410ParameterSpec.encryptionParamSetOID))));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.keyParameters.hashCode() ^ this.digestParamSetOID.hashCode() ^ ((this.encryptionParamSetOID != null) ? this.encryptionParamSetOID.hashCode() : 0);
  }
  
  public static GOST3410ParameterSpec fromPublicKeyAlg(GOST3410PublicKeyAlgParameters paramGOST3410PublicKeyAlgParameters) {
    return (paramGOST3410PublicKeyAlgParameters.getEncryptionParamSet() != null) ? new GOST3410ParameterSpec(paramGOST3410PublicKeyAlgParameters.getPublicKeyParamSet().getId(), paramGOST3410PublicKeyAlgParameters.getDigestParamSet().getId(), paramGOST3410PublicKeyAlgParameters.getEncryptionParamSet().getId()) : new GOST3410ParameterSpec(paramGOST3410PublicKeyAlgParameters.getPublicKeyParamSet().getId(), paramGOST3410PublicKeyAlgParameters.getDigestParamSet().getId());
  }
}
