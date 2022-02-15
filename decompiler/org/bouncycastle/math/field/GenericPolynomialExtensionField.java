package org.bouncycastle.math.field;

import java.math.BigInteger;
import org.bouncycastle.util.Integers;

class GenericPolynomialExtensionField implements PolynomialExtensionField {
  protected final FiniteField subfield;
  
  protected final Polynomial minimalPolynomial;
  
  GenericPolynomialExtensionField(FiniteField paramFiniteField, Polynomial paramPolynomial) {
    this.subfield = paramFiniteField;
    this.minimalPolynomial = paramPolynomial;
  }
  
  public BigInteger getCharacteristic() {
    return this.subfield.getCharacteristic();
  }
  
  public int getDimension() {
    return this.subfield.getDimension() * this.minimalPolynomial.getDegree();
  }
  
  public FiniteField getSubfield() {
    return this.subfield;
  }
  
  public int getDegree() {
    return this.minimalPolynomial.getDegree();
  }
  
  public Polynomial getMinimalPolynomial() {
    return this.minimalPolynomial;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof GenericPolynomialExtensionField))
      return false; 
    GenericPolynomialExtensionField genericPolynomialExtensionField = (GenericPolynomialExtensionField)paramObject;
    return (this.subfield.equals(genericPolynomialExtensionField.subfield) && this.minimalPolynomial.equals(genericPolynomialExtensionField.minimalPolynomial));
  }
  
  public int hashCode() {
    return this.subfield.hashCode() ^ Integers.rotateLeft(this.minimalPolynomial.hashCode(), 16);
  }
}
