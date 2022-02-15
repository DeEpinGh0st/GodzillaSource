package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.GenericKey;

public abstract class KeyAgreeRecipientInfoGenerator implements RecipientInfoGenerator {
  private ASN1ObjectIdentifier keyAgreementOID;
  
  private ASN1ObjectIdentifier keyEncryptionOID;
  
  private SubjectPublicKeyInfo originatorKeyInfo;
  
  protected KeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier1, SubjectPublicKeyInfo paramSubjectPublicKeyInfo, ASN1ObjectIdentifier paramASN1ObjectIdentifier2) {
    this.originatorKeyInfo = paramSubjectPublicKeyInfo;
    this.keyAgreementOID = paramASN1ObjectIdentifier1;
    this.keyEncryptionOID = paramASN1ObjectIdentifier2;
  }
  
  public RecipientInfo generate(GenericKey paramGenericKey) throws CMSException {
    AlgorithmIdentifier algorithmIdentifier1;
    OriginatorIdentifierOrKey originatorIdentifierOrKey = new OriginatorIdentifierOrKey(createOriginatorPublicKey(this.originatorKeyInfo));
    if (CMSUtils.isDES(this.keyEncryptionOID.getId()) || this.keyEncryptionOID.equals(PKCSObjectIdentifiers.id_alg_CMSRC2wrap)) {
      algorithmIdentifier1 = new AlgorithmIdentifier(this.keyEncryptionOID, (ASN1Encodable)DERNull.INSTANCE);
    } else if (CMSUtils.isGOST(this.keyAgreementOID)) {
      algorithmIdentifier1 = new AlgorithmIdentifier(this.keyEncryptionOID, (ASN1Encodable)new Gost2814789KeyWrapParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet));
    } else {
      algorithmIdentifier1 = new AlgorithmIdentifier(this.keyEncryptionOID);
    } 
    AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(this.keyAgreementOID, (ASN1Encodable)algorithmIdentifier1);
    ASN1Sequence aSN1Sequence = generateRecipientEncryptedKeys(algorithmIdentifier2, algorithmIdentifier1, paramGenericKey);
    byte[] arrayOfByte = getUserKeyingMaterial(algorithmIdentifier2);
    return (arrayOfByte != null) ? new RecipientInfo(new KeyAgreeRecipientInfo(originatorIdentifierOrKey, (ASN1OctetString)new DEROctetString(arrayOfByte), algorithmIdentifier2, aSN1Sequence)) : new RecipientInfo(new KeyAgreeRecipientInfo(originatorIdentifierOrKey, null, algorithmIdentifier2, aSN1Sequence));
  }
  
  protected OriginatorPublicKey createOriginatorPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    return new OriginatorPublicKey(new AlgorithmIdentifier(paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), paramSubjectPublicKeyInfo.getPublicKeyData().getBytes());
  }
  
  protected abstract ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, GenericKey paramGenericKey) throws CMSException;
  
  protected abstract byte[] getUserKeyingMaterial(AlgorithmIdentifier paramAlgorithmIdentifier) throws CMSException;
}
