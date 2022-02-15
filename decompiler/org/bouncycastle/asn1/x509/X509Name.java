package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class X509Name extends ASN1Object {
  public static final ASN1ObjectIdentifier C = new ASN1ObjectIdentifier("2.5.4.6");
  
  public static final ASN1ObjectIdentifier O = new ASN1ObjectIdentifier("2.5.4.10");
  
  public static final ASN1ObjectIdentifier OU = new ASN1ObjectIdentifier("2.5.4.11");
  
  public static final ASN1ObjectIdentifier T = new ASN1ObjectIdentifier("2.5.4.12");
  
  public static final ASN1ObjectIdentifier CN = new ASN1ObjectIdentifier("2.5.4.3");
  
  public static final ASN1ObjectIdentifier SN = new ASN1ObjectIdentifier("2.5.4.5");
  
  public static final ASN1ObjectIdentifier STREET = new ASN1ObjectIdentifier("2.5.4.9");
  
  public static final ASN1ObjectIdentifier SERIALNUMBER = SN;
  
  public static final ASN1ObjectIdentifier L = new ASN1ObjectIdentifier("2.5.4.7");
  
  public static final ASN1ObjectIdentifier ST = new ASN1ObjectIdentifier("2.5.4.8");
  
  public static final ASN1ObjectIdentifier SURNAME = new ASN1ObjectIdentifier("2.5.4.4");
  
  public static final ASN1ObjectIdentifier GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");
  
  public static final ASN1ObjectIdentifier INITIALS = new ASN1ObjectIdentifier("2.5.4.43");
  
  public static final ASN1ObjectIdentifier GENERATION = new ASN1ObjectIdentifier("2.5.4.44");
  
  public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER = new ASN1ObjectIdentifier("2.5.4.45");
  
  public static final ASN1ObjectIdentifier BUSINESS_CATEGORY = new ASN1ObjectIdentifier("2.5.4.15");
  
  public static final ASN1ObjectIdentifier POSTAL_CODE = new ASN1ObjectIdentifier("2.5.4.17");
  
  public static final ASN1ObjectIdentifier DN_QUALIFIER = new ASN1ObjectIdentifier("2.5.4.46");
  
  public static final ASN1ObjectIdentifier PSEUDONYM = new ASN1ObjectIdentifier("2.5.4.65");
  
  public static final ASN1ObjectIdentifier DATE_OF_BIRTH = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.1");
  
  public static final ASN1ObjectIdentifier PLACE_OF_BIRTH = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.2");
  
  public static final ASN1ObjectIdentifier GENDER = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.3");
  
  public static final ASN1ObjectIdentifier COUNTRY_OF_CITIZENSHIP = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.4");
  
  public static final ASN1ObjectIdentifier COUNTRY_OF_RESIDENCE = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.5");
  
  public static final ASN1ObjectIdentifier NAME_AT_BIRTH = new ASN1ObjectIdentifier("1.3.36.8.3.14");
  
  public static final ASN1ObjectIdentifier POSTAL_ADDRESS = new ASN1ObjectIdentifier("2.5.4.16");
  
  public static final ASN1ObjectIdentifier DMD_NAME = new ASN1ObjectIdentifier("2.5.4.54");
  
  public static final ASN1ObjectIdentifier TELEPHONE_NUMBER = X509ObjectIdentifiers.id_at_telephoneNumber;
  
  public static final ASN1ObjectIdentifier NAME = X509ObjectIdentifiers.id_at_name;
  
  public static final ASN1ObjectIdentifier EmailAddress = PKCSObjectIdentifiers.pkcs_9_at_emailAddress;
  
  public static final ASN1ObjectIdentifier UnstructuredName = PKCSObjectIdentifiers.pkcs_9_at_unstructuredName;
  
  public static final ASN1ObjectIdentifier UnstructuredAddress = PKCSObjectIdentifiers.pkcs_9_at_unstructuredAddress;
  
  public static final ASN1ObjectIdentifier E = EmailAddress;
  
  public static final ASN1ObjectIdentifier DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
  
  public static final ASN1ObjectIdentifier UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
  
  public static boolean DefaultReverse = false;
  
  public static final Hashtable DefaultSymbols = new Hashtable<Object, Object>();
  
  public static final Hashtable RFC2253Symbols = new Hashtable<Object, Object>();
  
  public static final Hashtable RFC1779Symbols = new Hashtable<Object, Object>();
  
  public static final Hashtable DefaultLookUp = new Hashtable<Object, Object>();
  
  public static final Hashtable OIDLookUp = DefaultSymbols;
  
  public static final Hashtable SymbolLookUp = DefaultLookUp;
  
  private static final Boolean TRUE = new Boolean(true);
  
  private static final Boolean FALSE = new Boolean(false);
  
  private X509NameEntryConverter converter = null;
  
  private Vector ordering = new Vector();
  
  private Vector values = new Vector();
  
  private Vector added = new Vector();
  
  private ASN1Sequence seq;
  
  private boolean isHashCodeCalculated;
  
  private int hashCodeValue;
  
  public static X509Name getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static X509Name getInstance(Object paramObject) {
    return (paramObject == null || paramObject instanceof X509Name) ? (X509Name)paramObject : ((paramObject instanceof X500Name) ? new X509Name(ASN1Sequence.getInstance(((X500Name)paramObject).toASN1Primitive())) : ((paramObject != null) ? new X509Name(ASN1Sequence.getInstance(paramObject)) : null));
  }
  
  protected X509Name() {}
  
  public X509Name(ASN1Sequence paramASN1Sequence) {
    this.seq = paramASN1Sequence;
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Set aSN1Set = ASN1Set.getInstance(((ASN1Encodable)enumeration.nextElement()).toASN1Primitive());
      for (byte b = 0; b < aSN1Set.size(); b++) {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Set.getObjectAt(b).toASN1Primitive());
        if (aSN1Sequence.size() != 2)
          throw new IllegalArgumentException("badly sized pair"); 
        this.ordering.addElement(ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0)));
        ASN1Encodable aSN1Encodable = aSN1Sequence.getObjectAt(1);
        if (aSN1Encodable instanceof ASN1String && !(aSN1Encodable instanceof org.bouncycastle.asn1.DERUniversalString)) {
          String str = ((ASN1String)aSN1Encodable).getString();
          if (str.length() > 0 && str.charAt(0) == '#') {
            this.values.addElement("\\" + str);
          } else {
            this.values.addElement(str);
          } 
        } else {
          try {
            this.values.addElement("#" + bytesToString(Hex.encode(aSN1Encodable.toASN1Primitive().getEncoded("DER"))));
          } catch (IOException iOException) {
            throw new IllegalArgumentException("cannot encode value");
          } 
        } 
        this.added.addElement((b != 0) ? TRUE : FALSE);
      } 
    } 
  }
  
  public X509Name(Hashtable paramHashtable) {
    this((Vector)null, paramHashtable);
  }
  
  public X509Name(Vector paramVector, Hashtable paramHashtable) {
    this(paramVector, paramHashtable, new X509DefaultEntryConverter());
  }
  
  public X509Name(Vector paramVector, Hashtable paramHashtable, X509NameEntryConverter paramX509NameEntryConverter) {
    this.converter = paramX509NameEntryConverter;
    if (paramVector != null) {
      for (byte b1 = 0; b1 != paramVector.size(); b1++) {
        this.ordering.addElement(paramVector.elementAt(b1));
        this.added.addElement(FALSE);
      } 
    } else {
      Enumeration enumeration = paramHashtable.keys();
      while (enumeration.hasMoreElements()) {
        this.ordering.addElement(enumeration.nextElement());
        this.added.addElement(FALSE);
      } 
    } 
    for (byte b = 0; b != this.ordering.size(); b++) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = this.ordering.elementAt(b);
      if (paramHashtable.get(aSN1ObjectIdentifier) == null)
        throw new IllegalArgumentException("No attribute for object id - " + aSN1ObjectIdentifier.getId() + " - passed to distinguished name"); 
      this.values.addElement(paramHashtable.get(aSN1ObjectIdentifier));
    } 
  }
  
  public X509Name(Vector paramVector1, Vector paramVector2) {
    this(paramVector1, paramVector2, new X509DefaultEntryConverter());
  }
  
  public X509Name(Vector paramVector1, Vector paramVector2, X509NameEntryConverter paramX509NameEntryConverter) {
    this.converter = paramX509NameEntryConverter;
    if (paramVector1.size() != paramVector2.size())
      throw new IllegalArgumentException("oids vector must be same length as values."); 
    for (byte b = 0; b < paramVector1.size(); b++) {
      this.ordering.addElement(paramVector1.elementAt(b));
      this.values.addElement(paramVector2.elementAt(b));
      this.added.addElement(FALSE);
    } 
  }
  
  public X509Name(String paramString) {
    this(DefaultReverse, DefaultLookUp, paramString);
  }
  
  public X509Name(String paramString, X509NameEntryConverter paramX509NameEntryConverter) {
    this(DefaultReverse, DefaultLookUp, paramString, paramX509NameEntryConverter);
  }
  
  public X509Name(boolean paramBoolean, String paramString) {
    this(paramBoolean, DefaultLookUp, paramString);
  }
  
  public X509Name(boolean paramBoolean, String paramString, X509NameEntryConverter paramX509NameEntryConverter) {
    this(paramBoolean, DefaultLookUp, paramString, paramX509NameEntryConverter);
  }
  
  public X509Name(boolean paramBoolean, Hashtable paramHashtable, String paramString) {
    this(paramBoolean, paramHashtable, paramString, new X509DefaultEntryConverter());
  }
  
  private ASN1ObjectIdentifier decodeOID(String paramString, Hashtable paramHashtable) {
    paramString = paramString.trim();
    if (Strings.toUpperCase(paramString).startsWith("OID."))
      return new ASN1ObjectIdentifier(paramString.substring(4)); 
    if (paramString.charAt(0) >= '0' && paramString.charAt(0) <= '9')
      return new ASN1ObjectIdentifier(paramString); 
    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)paramHashtable.get(Strings.toLowerCase(paramString));
    if (aSN1ObjectIdentifier == null)
      throw new IllegalArgumentException("Unknown object id - " + paramString + " - passed to distinguished name"); 
    return aSN1ObjectIdentifier;
  }
  
  private String unescape(String paramString) {
    if (paramString.length() == 0 || (paramString.indexOf('\\') < 0 && paramString.indexOf('"') < 0))
      return paramString.trim(); 
    char[] arrayOfChar = paramString.toCharArray();
    boolean bool1 = false;
    boolean bool2 = false;
    StringBuffer stringBuffer = new StringBuffer(paramString.length());
    byte b1 = 0;
    if (arrayOfChar[0] == '\\' && arrayOfChar[1] == '#') {
      b1 = 2;
      stringBuffer.append("\\#");
    } 
    boolean bool3 = false;
    int i = 0;
    for (byte b2 = b1; b2 != arrayOfChar.length; b2++) {
      char c = arrayOfChar[b2];
      if (c != ' ')
        bool3 = true; 
      if (c == '"') {
        if (!bool1) {
          bool2 = !bool2 ? true : false;
        } else {
          stringBuffer.append(c);
        } 
        bool1 = false;
      } else if (c == '\\' && !bool1 && !bool2) {
        bool1 = true;
        i = stringBuffer.length();
      } else if (c != ' ' || bool1 || bool3) {
        stringBuffer.append(c);
        bool1 = false;
      } 
    } 
    if (stringBuffer.length() > 0)
      while (stringBuffer.charAt(stringBuffer.length() - 1) == ' ' && i != stringBuffer.length() - 1)
        stringBuffer.setLength(stringBuffer.length() - 1);  
    return stringBuffer.toString();
  }
  
  public X509Name(boolean paramBoolean, Hashtable paramHashtable, String paramString, X509NameEntryConverter paramX509NameEntryConverter) {
    this.converter = paramX509NameEntryConverter;
    X509NameTokenizer x509NameTokenizer = new X509NameTokenizer(paramString);
    while (x509NameTokenizer.hasMoreTokens()) {
      String str = x509NameTokenizer.nextToken();
      if (str.indexOf('+') > 0) {
        X509NameTokenizer x509NameTokenizer1 = new X509NameTokenizer(str, '+');
        addEntry(paramHashtable, x509NameTokenizer1.nextToken(), FALSE);
        while (x509NameTokenizer1.hasMoreTokens())
          addEntry(paramHashtable, x509NameTokenizer1.nextToken(), TRUE); 
        continue;
      } 
      addEntry(paramHashtable, str, FALSE);
    } 
    if (paramBoolean) {
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();
      Vector vector3 = new Vector();
      byte b1 = 1;
      for (byte b2 = 0; b2 < this.ordering.size(); b2++) {
        if (((Boolean)this.added.elementAt(b2)).booleanValue()) {
          vector1.insertElementAt(this.ordering.elementAt(b2), b1);
          vector2.insertElementAt(this.values.elementAt(b2), b1);
          vector3.insertElementAt(this.added.elementAt(b2), b1);
          b1++;
        } else {
          vector1.insertElementAt(this.ordering.elementAt(b2), 0);
          vector2.insertElementAt(this.values.elementAt(b2), 0);
          vector3.insertElementAt(this.added.elementAt(b2), 0);
          b1 = 1;
        } 
      } 
      this.ordering = vector1;
      this.values = vector2;
      this.added = vector3;
    } 
  }
  
  private void addEntry(Hashtable paramHashtable, String paramString, Boolean paramBoolean) {
    X509NameTokenizer x509NameTokenizer = new X509NameTokenizer(paramString, '=');
    String str1 = x509NameTokenizer.nextToken();
    if (!x509NameTokenizer.hasMoreTokens())
      throw new IllegalArgumentException("badly formatted directory string"); 
    String str2 = x509NameTokenizer.nextToken();
    ASN1ObjectIdentifier aSN1ObjectIdentifier = decodeOID(str1, paramHashtable);
    this.ordering.addElement(aSN1ObjectIdentifier);
    this.values.addElement(unescape(str2));
    this.added.addElement(paramBoolean);
  }
  
  public Vector getOIDs() {
    Vector vector = new Vector();
    for (byte b = 0; b != this.ordering.size(); b++)
      vector.addElement(this.ordering.elementAt(b)); 
    return vector;
  }
  
  public Vector getValues() {
    Vector vector = new Vector();
    for (byte b = 0; b != this.values.size(); b++)
      vector.addElement(this.values.elementAt(b)); 
    return vector;
  }
  
  public Vector getValues(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    Vector<String> vector = new Vector();
    for (byte b = 0; b != this.values.size(); b++) {
      if (this.ordering.elementAt(b).equals(paramASN1ObjectIdentifier)) {
        String str = this.values.elementAt(b);
        if (str.length() > 2 && str.charAt(0) == '\\' && str.charAt(1) == '#') {
          vector.addElement(str.substring(1));
        } else {
          vector.addElement(str);
        } 
      } 
    } 
    return vector;
  }
  
  public ASN1Primitive toASN1Primitive() {
    if (this.seq == null) {
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
      ASN1ObjectIdentifier aSN1ObjectIdentifier = null;
      for (byte b = 0; b != this.ordering.size(); b++) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        ASN1ObjectIdentifier aSN1ObjectIdentifier1 = this.ordering.elementAt(b);
        aSN1EncodableVector.add((ASN1Encodable)aSN1ObjectIdentifier1);
        String str = this.values.elementAt(b);
        aSN1EncodableVector.add((ASN1Encodable)this.converter.getConvertedValue(aSN1ObjectIdentifier1, str));
        if (aSN1ObjectIdentifier == null || ((Boolean)this.added.elementAt(b)).booleanValue()) {
          aSN1EncodableVector2.add((ASN1Encodable)new DERSequence(aSN1EncodableVector));
        } else {
          aSN1EncodableVector1.add((ASN1Encodable)new DERSet(aSN1EncodableVector2));
          aSN1EncodableVector2 = new ASN1EncodableVector();
          aSN1EncodableVector2.add((ASN1Encodable)new DERSequence(aSN1EncodableVector));
        } 
        aSN1ObjectIdentifier = aSN1ObjectIdentifier1;
      } 
      aSN1EncodableVector1.add((ASN1Encodable)new DERSet(aSN1EncodableVector2));
      this.seq = (ASN1Sequence)new DERSequence(aSN1EncodableVector1);
    } 
    return (ASN1Primitive)this.seq;
  }
  
  public boolean equals(Object paramObject, boolean paramBoolean) {
    X509Name x509Name;
    if (!paramBoolean)
      return equals(paramObject); 
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof X509Name) && !(paramObject instanceof ASN1Sequence))
      return false; 
    ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
    if (toASN1Primitive().equals(aSN1Primitive))
      return true; 
    try {
      x509Name = getInstance(paramObject);
    } catch (IllegalArgumentException illegalArgumentException) {
      return false;
    } 
    int i = this.ordering.size();
    if (i != x509Name.ordering.size())
      return false; 
    for (byte b = 0; b < i; b++) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier1 = this.ordering.elementAt(b);
      ASN1ObjectIdentifier aSN1ObjectIdentifier2 = x509Name.ordering.elementAt(b);
      if (aSN1ObjectIdentifier1.equals(aSN1ObjectIdentifier2)) {
        String str1 = this.values.elementAt(b);
        String str2 = x509Name.values.elementAt(b);
        if (!equivalentStrings(str1, str2))
          return false; 
      } else {
        return false;
      } 
    } 
    return true;
  }
  
  public int hashCode() {
    if (this.isHashCodeCalculated)
      return this.hashCodeValue; 
    this.isHashCodeCalculated = true;
    for (byte b = 0; b != this.ordering.size(); b++) {
      String str = this.values.elementAt(b);
      str = canonicalize(str);
      str = stripInternalSpaces(str);
      this.hashCodeValue ^= this.ordering.elementAt(b).hashCode();
      this.hashCodeValue ^= str.hashCode();
    } 
    return this.hashCodeValue;
  }
  
  public boolean equals(Object paramObject) {
    X509Name x509Name;
    int j;
    byte b1;
    byte b2;
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof X509Name) && !(paramObject instanceof ASN1Sequence))
      return false; 
    ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
    if (toASN1Primitive().equals(aSN1Primitive))
      return true; 
    try {
      x509Name = getInstance(paramObject);
    } catch (IllegalArgumentException illegalArgumentException) {
      return false;
    } 
    int i = this.ordering.size();
    if (i != x509Name.ordering.size())
      return false; 
    boolean[] arrayOfBoolean = new boolean[i];
    if (this.ordering.elementAt(0).equals(x509Name.ordering.elementAt(0))) {
      j = 0;
      b1 = i;
      b2 = 1;
    } else {
      j = i - 1;
      b1 = -1;
      b2 = -1;
    } 
    int k;
    for (k = j; k != b1; k += b2) {
      boolean bool = false;
      ASN1ObjectIdentifier aSN1ObjectIdentifier = this.ordering.elementAt(k);
      String str = this.values.elementAt(k);
      for (byte b = 0; b < i; b++) {
        if (!arrayOfBoolean[b]) {
          ASN1ObjectIdentifier aSN1ObjectIdentifier1 = x509Name.ordering.elementAt(b);
          if (aSN1ObjectIdentifier.equals(aSN1ObjectIdentifier1)) {
            String str1 = x509Name.values.elementAt(b);
            if (equivalentStrings(str, str1)) {
              arrayOfBoolean[b] = true;
              bool = true;
              break;
            } 
          } 
        } 
      } 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  private boolean equivalentStrings(String paramString1, String paramString2) {
    String str1 = canonicalize(paramString1);
    String str2 = canonicalize(paramString2);
    if (!str1.equals(str2)) {
      str1 = stripInternalSpaces(str1);
      str2 = stripInternalSpaces(str2);
      if (!str1.equals(str2))
        return false; 
    } 
    return true;
  }
  
  private String canonicalize(String paramString) {
    String str = Strings.toLowerCase(paramString.trim());
    if (str.length() > 0 && str.charAt(0) == '#') {
      ASN1Primitive aSN1Primitive = decodeObject(str);
      if (aSN1Primitive instanceof ASN1String)
        str = Strings.toLowerCase(((ASN1String)aSN1Primitive).getString().trim()); 
    } 
    return str;
  }
  
  private ASN1Primitive decodeObject(String paramString) {
    try {
      return ASN1Primitive.fromByteArray(Hex.decode(paramString.substring(1)));
    } catch (IOException iOException) {
      throw new IllegalStateException("unknown encoding in name: " + iOException);
    } 
  }
  
  private String stripInternalSpaces(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramString.length() != 0) {
      char c = paramString.charAt(0);
      stringBuffer.append(c);
      for (byte b = 1; b < paramString.length(); b++) {
        char c1 = paramString.charAt(b);
        if (c != ' ' || c1 != ' ')
          stringBuffer.append(c1); 
        c = c1;
      } 
    } 
    return stringBuffer.toString();
  }
  
  private void appendValue(StringBuffer paramStringBuffer, Hashtable paramHashtable, ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    String str = (String)paramHashtable.get(paramASN1ObjectIdentifier);
    if (str != null) {
      paramStringBuffer.append(str);
    } else {
      paramStringBuffer.append(paramASN1ObjectIdentifier.getId());
    } 
    paramStringBuffer.append('=');
    int i = paramStringBuffer.length();
    paramStringBuffer.append(paramString);
    int j = paramStringBuffer.length();
    if (paramString.length() >= 2 && paramString.charAt(0) == '\\' && paramString.charAt(1) == '#')
      i += 2; 
    while (i < j && paramStringBuffer.charAt(i) == ' ') {
      paramStringBuffer.insert(i, "\\");
      i += 2;
      j++;
    } 
    while (--j > i && paramStringBuffer.charAt(j) == ' ')
      paramStringBuffer.insert(j, '\\'); 
    while (i <= j) {
      switch (paramStringBuffer.charAt(i)) {
        case '"':
        case '+':
        case ',':
        case ';':
        case '<':
        case '=':
        case '>':
        case '\\':
          paramStringBuffer.insert(i, "\\");
          i += 2;
          j++;
          continue;
      } 
      i++;
    } 
  }
  
  public String toString(boolean paramBoolean, Hashtable paramHashtable) {
    StringBuffer stringBuffer1 = new StringBuffer();
    Vector<StringBuffer> vector = new Vector();
    boolean bool = true;
    StringBuffer stringBuffer2 = null;
    int i;
    for (i = 0; i < this.ordering.size(); i++) {
      if (((Boolean)this.added.elementAt(i)).booleanValue()) {
        stringBuffer2.append('+');
        appendValue(stringBuffer2, paramHashtable, this.ordering.elementAt(i), this.values.elementAt(i));
      } else {
        stringBuffer2 = new StringBuffer();
        appendValue(stringBuffer2, paramHashtable, this.ordering.elementAt(i), this.values.elementAt(i));
        vector.addElement(stringBuffer2);
      } 
    } 
    if (paramBoolean) {
      for (i = vector.size() - 1; i >= 0; i--) {
        if (bool) {
          bool = false;
        } else {
          stringBuffer1.append(',');
        } 
        stringBuffer1.append(vector.elementAt(i).toString());
      } 
    } else {
      for (i = 0; i < vector.size(); i++) {
        if (bool) {
          bool = false;
        } else {
          stringBuffer1.append(',');
        } 
        stringBuffer1.append(vector.elementAt(i).toString());
      } 
    } 
    return stringBuffer1.toString();
  }
  
  private String bytesToString(byte[] paramArrayOfbyte) {
    char[] arrayOfChar = new char[paramArrayOfbyte.length];
    for (byte b = 0; b != arrayOfChar.length; b++)
      arrayOfChar[b] = (char)(paramArrayOfbyte[b] & 0xFF); 
    return new String(arrayOfChar);
  }
  
  public String toString() {
    return toString(DefaultReverse, DefaultSymbols);
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
    RFC2253Symbols.put(C, "C");
    RFC2253Symbols.put(O, "O");
    RFC2253Symbols.put(OU, "OU");
    RFC2253Symbols.put(CN, "CN");
    RFC2253Symbols.put(L, "L");
    RFC2253Symbols.put(ST, "ST");
    RFC2253Symbols.put(STREET, "STREET");
    RFC2253Symbols.put(DC, "DC");
    RFC2253Symbols.put(UID, "UID");
    RFC1779Symbols.put(C, "C");
    RFC1779Symbols.put(O, "O");
    RFC1779Symbols.put(OU, "OU");
    RFC1779Symbols.put(CN, "CN");
    RFC1779Symbols.put(L, "L");
    RFC1779Symbols.put(ST, "ST");
    RFC1779Symbols.put(STREET, "STREET");
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
  }
}
