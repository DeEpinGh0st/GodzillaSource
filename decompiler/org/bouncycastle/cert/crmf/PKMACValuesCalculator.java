package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface PKMACValuesCalculator {
  void setup(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) throws CRMFException;
  
  byte[] calculateDigest(byte[] paramArrayOfbyte) throws CRMFException;
  
  byte[] calculateMac(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws CRMFException;
}
