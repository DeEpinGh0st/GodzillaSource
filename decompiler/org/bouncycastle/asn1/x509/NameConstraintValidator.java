package org.bouncycastle.asn1.x509;

public interface NameConstraintValidator {
  void checkPermitted(GeneralName paramGeneralName) throws NameConstraintValidatorException;
  
  void checkExcluded(GeneralName paramGeneralName) throws NameConstraintValidatorException;
  
  void intersectPermittedSubtree(GeneralSubtree paramGeneralSubtree);
  
  void intersectPermittedSubtree(GeneralSubtree[] paramArrayOfGeneralSubtree);
  
  void intersectEmptyPermittedSubtree(int paramInt);
  
  void addExcludedSubtree(GeneralSubtree paramGeneralSubtree);
}
