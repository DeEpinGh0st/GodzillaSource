package org.bouncycastle.asn1.x500.style;

import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

public class BCStyle extends AbstractX500NameStyle {
  public static final ASN1ObjectIdentifier C = (new ASN1ObjectIdentifier("2.5.4.6")).intern();
  
  public static final ASN1ObjectIdentifier O = (new ASN1ObjectIdentifier("2.5.4.10")).intern();
  
  public static final ASN1ObjectIdentifier OU = (new ASN1ObjectIdentifier("2.5.4.11")).intern();
  
  public static final ASN1ObjectIdentifier T = (new ASN1ObjectIdentifier("2.5.4.12")).intern();
  
  public static final ASN1ObjectIdentifier CN = (new ASN1ObjectIdentifier("2.5.4.3")).intern();
  
  public static final ASN1ObjectIdentifier SN = (new ASN1ObjectIdentifier("2.5.4.5")).intern();
  
  public static final ASN1ObjectIdentifier STREET = (new ASN1ObjectIdentifier("2.5.4.9")).intern();
  
  public static final ASN1ObjectIdentifier SERIALNUMBER = SN;
  
  public static final ASN1ObjectIdentifier L = (new ASN1ObjectIdentifier("2.5.4.7")).intern();
  
  public static final ASN1ObjectIdentifier ST = (new ASN1ObjectIdentifier("2.5.4.8")).intern();
  
  public static final ASN1ObjectIdentifier SURNAME = (new ASN1ObjectIdentifier("2.5.4.4")).intern();
  
  public static final ASN1ObjectIdentifier GIVENNAME = (new ASN1ObjectIdentifier("2.5.4.42")).intern();
  
  public static final ASN1ObjectIdentifier INITIALS = (new ASN1ObjectIdentifier("2.5.4.43")).intern();
  
  public static final ASN1ObjectIdentifier GENERATION = (new ASN1ObjectIdentifier("2.5.4.44")).intern();
  
  public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER = (new ASN1ObjectIdentifier("2.5.4.45")).intern();
  
  public static final ASN1ObjectIdentifier BUSINESS_CATEGORY = (new ASN1ObjectIdentifier("2.5.4.15")).intern();
  
  public static final ASN1ObjectIdentifier POSTAL_CODE = (new ASN1ObjectIdentifier("2.5.4.17")).intern();
  
  public static final ASN1ObjectIdentifier DN_QUALIFIER = (new ASN1ObjectIdentifier("2.5.4.46")).intern();
  
  public static final ASN1ObjectIdentifier PSEUDONYM = (new ASN1ObjectIdentifier("2.5.4.65")).intern();
  
  public static final ASN1ObjectIdentifier DATE_OF_BIRTH = (new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.1")).intern();
  
  public static final ASN1ObjectIdentifier PLACE_OF_BIRTH = (new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.2")).intern();
  
  public static final ASN1ObjectIdentifier GENDER = (new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.3")).intern();
  
  public static final ASN1ObjectIdentifier COUNTRY_OF_CITIZENSHIP = (new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.4")).intern();
  
  public static final ASN1ObjectIdentifier COUNTRY_OF_RESIDENCE = (new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.5")).intern();
  
  public static final ASN1ObjectIdentifier NAME_AT_BIRTH = (new ASN1ObjectIdentifier("1.3.36.8.3.14")).intern();
  
  public static final ASN1ObjectIdentifier POSTAL_ADDRESS = (new ASN1ObjectIdentifier("2.5.4.16")).intern();
  
  public static final ASN1ObjectIdentifier DMD_NAME = (new ASN1ObjectIdentifier("2.5.4.54")).intern();
  
  public static final ASN1ObjectIdentifier TELEPHONE_NUMBER = X509ObjectIdentifiers.id_at_telephoneNumber;
  
  public static final ASN1ObjectIdentifier NAME = X509ObjectIdentifiers.id_at_name;
  
  public static final ASN1ObjectIdentifier ORGANIZATION_IDENTIFIER = X509ObjectIdentifiers.id_at_organizationIdentifier;
  
  public static final ASN1ObjectIdentifier EmailAddress = PKCSObjectIdentifiers.pkcs_9_at_emailAddress;
  
  public static final ASN1ObjectIdentifier UnstructuredName = PKCSObjectIdentifiers.pkcs_9_at_unstructuredName;
  
  public static final ASN1ObjectIdentifier UnstructuredAddress = PKCSObjectIdentifiers.pkcs_9_at_unstructuredAddress;
  
  public static final ASN1ObjectIdentifier E = EmailAddress;
  
  public static final ASN1ObjectIdentifier DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
  
  public static final ASN1ObjectIdentifier UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
  
  private static final Hashtable DefaultSymbols = new Hashtable<Object, Object>();
  
  private static final Hashtable DefaultLookUp = new Hashtable<Object, Object>();
  
  public static final X500NameStyle INSTANCE = new BCStyle();
  
  protected final Hashtable defaultLookUp = copyHashTable(DefaultLookUp);
  
  protected final Hashtable defaultSymbols = copyHashTable(DefaultSymbols);
  
  protected ASN1Encodable encodeStringValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    return (ASN1Encodable)((paramASN1ObjectIdentifier.equals(EmailAddress) || paramASN1ObjectIdentifier.equals(DC)) ? new DERIA5String(paramString) : (paramASN1ObjectIdentifier.equals(DATE_OF_BIRTH) ? new ASN1GeneralizedTime(paramString) : ((paramASN1ObjectIdentifier.equals(C) || paramASN1ObjectIdentifier.equals(SN) || paramASN1ObjectIdentifier.equals(DN_QUALIFIER) || paramASN1ObjectIdentifier.equals(TELEPHONE_NUMBER)) ? new DERPrintableString(paramString) : super.encodeStringValue(paramASN1ObjectIdentifier, paramString))));
  }
  
  public String oidToDisplayName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (String)DefaultSymbols.get(paramASN1ObjectIdentifier);
  }
  
  public String[] oidToAttrNames(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return IETFUtils.findAttrNamesForOID(paramASN1ObjectIdentifier, this.defaultLookUp);
  }
  
  public ASN1ObjectIdentifier attrNameToOID(String paramString) {
    return IETFUtils.decodeAttrName(paramString, this.defaultLookUp);
  }
  
  public RDN[] fromString(String paramString) {
    return IETFUtils.rDNsFromString(paramString, this);
  }
  
  public String toString(X500Name paramX500Name) {
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool = true;
    RDN[] arrayOfRDN = paramX500Name.getRDNs();
    for (byte b = 0; b < arrayOfRDN.length; b++) {
      if (bool) {
        bool = false;
      } else {
        stringBuffer.append(',');
      } 
      IETFUtils.appendRDN(stringBuffer, arrayOfRDN[b], this.defaultSymbols);
    } 
    return stringBuffer.toString();
  }
  
  static {
    DefaultSymbols.put(C, "C");
    DefaultSymbols.put(O, "O");
    DefaultSymbols.put(T, "T");
    DefaultSymbols.put(OU, "OU");
    DefaultSymbols.put(CN, "CN");
    DefaultSymbols.put(L, "L");
    DefaultSymbols.put(ST, "ST");
    DefaultSymbols.put(SN, "SERIALNUMBER");
    DefaultSymbols.put(EmailAddress, "E");
    DefaultSymbols.put(DC, "DC");
    DefaultSymbols.put(UID, "UID");
    DefaultSymbols.put(STREET, "STREET");
    DefaultSymbols.put(SURNAME, "SURNAME");
    DefaultSymbols.put(GIVENNAME, "GIVENNAME");
    DefaultSymbols.put(INITIALS, "INITIALS");
    DefaultSymbols.put(GENERATION, "GENERATION");
    DefaultSymbols.put(UnstructuredAddress, "unstructuredAddress");
    DefaultSymbols.put(UnstructuredName, "unstructuredName");
    DefaultSymbols.put(UNIQUE_IDENTIFIER, "UniqueIdentifier");
    DefaultSymbols.put(DN_QUALIFIER, "DN");
    DefaultSymbols.put(PSEUDONYM, "Pseudonym");
    DefaultSymbols.put(POSTAL_ADDRESS, "PostalAddress");
    DefaultSymbols.put(NAME_AT_BIRTH, "NameAtBirth");
    DefaultSymbols.put(COUNTRY_OF_CITIZENSHIP, "CountryOfCitizenship");
    DefaultSymbols.put(COUNTRY_OF_RESIDENCE, "CountryOfResidence");
    DefaultSymbols.put(GENDER, "Gender");
    DefaultSymbols.put(PLACE_OF_BIRTH, "PlaceOfBirth");
    DefaultSymbols.put(DATE_OF_BIRTH, "DateOfBirth");
    DefaultSymbols.put(POSTAL_CODE, "PostalCode");
    DefaultSymbols.put(BUSINESS_CATEGORY, "BusinessCategory");
    DefaultSymbols.put(TELEPHONE_NUMBER, "TelephoneNumber");
    DefaultSymbols.put(NAME, "Name");
    DefaultSymbols.put(ORGANIZATION_IDENTIFIER, "organizationIdentifier");
    DefaultLookUp.put("c", C);
    DefaultLookUp.put("o", O);
    DefaultLookUp.put("t", T);
    DefaultLookUp.put("ou", OU);
    DefaultLookUp.put("cn", CN);
    DefaultLookUp.put("l", L);
    DefaultLookUp.put("st", ST);
    DefaultLookUp.put("sn", SN);
    DefaultLookUp.put("serialnumber", SN);
    DefaultLookUp.put("street", STREET);
    DefaultLookUp.put("emailaddress", E);
    DefaultLookUp.put("dc", DC);
    DefaultLookUp.put("e", E);
    DefaultLookUp.put("uid", UID);
    DefaultLookUp.put("surname", SURNAME);
    DefaultLookUp.put("givenname", GIVENNAME);
    DefaultLookUp.put("initials", INITIALS);
    DefaultLookUp.put("generation", GENERATION);
    DefaultLookUp.put("unstructuredaddress", UnstructuredAddress);
    DefaultLookUp.put("unstructuredname", UnstructuredName);
    DefaultLookUp.put("uniqueidentifier", UNIQUE_IDENTIFIER);
    DefaultLookUp.put("dn", DN_QUALIFIER);
    DefaultLookUp.put("pseudonym", PSEUDONYM);
    DefaultLookUp.put("postaladdress", POSTAL_ADDRESS);
    DefaultLookUp.put("nameofbirth", NAME_AT_BIRTH);
    DefaultLookUp.put("countryofcitizenship", COUNTRY_OF_CITIZENSHIP);
    DefaultLookUp.put("countryofresidence", COUNTRY_OF_RESIDENCE);
    DefaultLookUp.put("gender", GENDER);
    DefaultLookUp.put("placeofbirth", PLACE_OF_BIRTH);
    DefaultLookUp.put("dateofbirth", DATE_OF_BIRTH);
    DefaultLookUp.put("postalcode", POSTAL_CODE);
    DefaultLookUp.put("businesscategory", BUSINESS_CATEGORY);
    DefaultLookUp.put("telephonenumber", TELEPHONE_NUMBER);
    DefaultLookUp.put("name", NAME);
    DefaultLookUp.put("organizationidentifier", ORGANIZATION_IDENTIFIER);
  }
}
