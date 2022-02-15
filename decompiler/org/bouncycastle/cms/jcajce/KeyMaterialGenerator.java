package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

interface KeyMaterialGenerator {
  byte[] generateKDFMaterial(AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt, byte[] paramArrayOfbyte);
}
