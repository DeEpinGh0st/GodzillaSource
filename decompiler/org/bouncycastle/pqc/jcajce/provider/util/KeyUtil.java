package org.bouncycastle.pqc.jcajce.provider.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class KeyUtil {
  public static byte[] getEncodedSubjectPublicKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1Encodable paramASN1Encodable) {
    try {
      return getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(paramAlgorithmIdentifier, paramASN1Encodable));
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static byte[] getEncodedSubjectPublicKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    try {
      return getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(paramAlgorithmIdentifier, paramArrayOfbyte));
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static byte[] getEncodedSubjectPublicKeyInfo(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    try {
      return paramSubjectPublicKeyInfo.getEncoded("DER");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static byte[] getEncodedPrivateKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1Encodable paramASN1Encodable) {
    try {
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(paramAlgorithmIdentifier, (ASN1Encodable)paramASN1Encodable.toASN1Primitive());
      return getEncodedPrivateKeyInfo(privateKeyInfo);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static byte[] getEncodedPrivateKeyInfo(PrivateKeyInfo paramPrivateKeyInfo) {
    try {
      return paramPrivateKeyInfo.getEncoded("DER");
    } catch (Exception exception) {
      return null;
    } 
  }
}
