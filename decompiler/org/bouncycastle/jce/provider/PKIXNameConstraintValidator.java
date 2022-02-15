package org.bouncycastle.jce.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

public class PKIXNameConstraintValidator {
  private Set excludedSubtreesDN = new HashSet();
  
  private Set excludedSubtreesDNS = new HashSet();
  
  private Set excludedSubtreesEmail = new HashSet();
  
  private Set excludedSubtreesURI = new HashSet();
  
  private Set excludedSubtreesIP = new HashSet();
  
  private Set permittedSubtreesDN;
  
  private Set permittedSubtreesDNS;
  
  private Set permittedSubtreesEmail;
  
  private Set permittedSubtreesURI;
  
  private Set permittedSubtreesIP;
  
  private static boolean withinDNSubtree(ASN1Sequence paramASN1Sequence1, ASN1Sequence paramASN1Sequence2) {
    if (paramASN1Sequence2.size() < 1)
      return false; 
    if (paramASN1Sequence2.size() > paramASN1Sequence1.size())
      return false; 
    for (int i = paramASN1Sequence2.size() - 1; i >= 0; i--) {
      if (!paramASN1Sequence2.getObjectAt(i).equals(paramASN1Sequence1.getObjectAt(i)))
        return false; 
    } 
    return true;
  }
  
  public void checkPermittedDN(ASN1Sequence paramASN1Sequence) throws PKIXNameConstraintValidatorException {
    checkPermittedDN(this.permittedSubtreesDN, paramASN1Sequence);
  }
  
  public void checkExcludedDN(ASN1Sequence paramASN1Sequence) throws PKIXNameConstraintValidatorException {
    checkExcludedDN(this.excludedSubtreesDN, paramASN1Sequence);
  }
  
  private void checkPermittedDN(Set paramSet, ASN1Sequence paramASN1Sequence) throws PKIXNameConstraintValidatorException {
    if (paramSet == null)
      return; 
    if (paramSet.isEmpty() && paramASN1Sequence.size() == 0)
      return; 
    for (ASN1Sequence aSN1Sequence : paramSet) {
      if (withinDNSubtree(paramASN1Sequence, aSN1Sequence))
        return; 
    } 
    throw new PKIXNameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
  }
  
  private void checkExcludedDN(Set paramSet, ASN1Sequence paramASN1Sequence) throws PKIXNameConstraintValidatorException {
    if (paramSet.isEmpty())
      return; 
    for (ASN1Sequence aSN1Sequence : paramSet) {
      if (withinDNSubtree(paramASN1Sequence, aSN1Sequence))
        throw new PKIXNameConstraintValidatorException("Subject distinguished name is from an excluded subtree"); 
    } 
  }
  
  private Set intersectDN(Set paramSet1, Set paramSet2) {
    HashSet<ASN1Sequence> hashSet = new HashSet();
    Iterator<GeneralSubtree> iterator = paramSet2.iterator();
    while (iterator.hasNext()) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(((GeneralSubtree)iterator.next()).getBase().getName().toASN1Primitive());
      if (paramSet1 == null) {
        if (aSN1Sequence != null)
          hashSet.add(aSN1Sequence); 
        continue;
      } 
      for (ASN1Sequence aSN1Sequence1 : paramSet1) {
        if (withinDNSubtree(aSN1Sequence, aSN1Sequence1)) {
          hashSet.add(aSN1Sequence);
          continue;
        } 
        if (withinDNSubtree(aSN1Sequence1, aSN1Sequence))
          hashSet.add(aSN1Sequence1); 
      } 
    } 
    return hashSet;
  }
  
  private Set unionDN(Set<ASN1Sequence> paramSet, ASN1Sequence paramASN1Sequence) {
    if (paramSet.isEmpty()) {
      if (paramASN1Sequence == null)
        return paramSet; 
      paramSet.add(paramASN1Sequence);
      return paramSet;
    } 
    HashSet<ASN1Sequence> hashSet = new HashSet();
    for (ASN1Sequence aSN1Sequence : paramSet) {
      if (withinDNSubtree(paramASN1Sequence, aSN1Sequence)) {
        hashSet.add(aSN1Sequence);
        continue;
      } 
      if (withinDNSubtree(aSN1Sequence, paramASN1Sequence)) {
        hashSet.add(paramASN1Sequence);
        continue;
      } 
      hashSet.add(aSN1Sequence);
      hashSet.add(paramASN1Sequence);
    } 
    return hashSet;
  }
  
  private Set intersectEmail(Set paramSet1, Set paramSet2) {
    HashSet<String> hashSet = new HashSet();
    Iterator<GeneralSubtree> iterator = paramSet2.iterator();
    while (iterator.hasNext()) {
      String str = extractNameAsString(((GeneralSubtree)iterator.next()).getBase());
      if (paramSet1 == null) {
        if (str != null)
          hashSet.add(str); 
        continue;
      } 
      for (String str1 : paramSet1)
        intersectEmail(str, str1, hashSet); 
    } 
    return hashSet;
  }
  
  private Set unionEmail(Set<String> paramSet, String paramString) {
    if (paramSet.isEmpty()) {
      if (paramString == null)
        return paramSet; 
      paramSet.add(paramString);
      return paramSet;
    } 
    HashSet hashSet = new HashSet();
    for (String str : paramSet)
      unionEmail(str, paramString, hashSet); 
    return hashSet;
  }
  
  private Set intersectIP(Set paramSet1, Set paramSet2) {
    HashSet<byte[]> hashSet = new HashSet();
    Iterator<GeneralSubtree> iterator = paramSet2.iterator();
    while (iterator.hasNext()) {
      byte[] arrayOfByte = ASN1OctetString.getInstance(((GeneralSubtree)iterator.next()).getBase().getName()).getOctets();
      if (paramSet1 == null) {
        if (arrayOfByte != null)
          hashSet.add(arrayOfByte); 
        continue;
      } 
      for (byte[] arrayOfByte1 : paramSet1)
        hashSet.addAll(intersectIPRange(arrayOfByte1, arrayOfByte)); 
    } 
    return hashSet;
  }
  
  private Set unionIP(Set<byte[]> paramSet, byte[] paramArrayOfbyte) {
    if (paramSet.isEmpty()) {
      if (paramArrayOfbyte == null)
        return paramSet; 
      paramSet.add(paramArrayOfbyte);
      return paramSet;
    } 
    HashSet hashSet = new HashSet();
    for (byte[] arrayOfByte : paramSet)
      hashSet.addAll(unionIPRange(arrayOfByte, paramArrayOfbyte)); 
    return hashSet;
  }
  
  private Set unionIPRange(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    HashSet<byte[]> hashSet = new HashSet();
    if (Arrays.areEqual(paramArrayOfbyte1, paramArrayOfbyte2)) {
      hashSet.add(paramArrayOfbyte1);
    } else {
      hashSet.add(paramArrayOfbyte1);
      hashSet.add(paramArrayOfbyte2);
    } 
    return hashSet;
  }
  
  private Set intersectIPRange(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != paramArrayOfbyte2.length)
      return Collections.EMPTY_SET; 
    byte[][] arrayOfByte1 = extractIPsAndSubnetMasks(paramArrayOfbyte1, paramArrayOfbyte2);
    byte[] arrayOfByte2 = arrayOfByte1[0];
    byte[] arrayOfByte3 = arrayOfByte1[1];
    byte[] arrayOfByte4 = arrayOfByte1[2];
    byte[] arrayOfByte5 = arrayOfByte1[3];
    byte[][] arrayOfByte6 = minMaxIPs(arrayOfByte2, arrayOfByte3, arrayOfByte4, arrayOfByte5);
    byte[] arrayOfByte8 = min(arrayOfByte6[1], arrayOfByte6[3]);
    byte[] arrayOfByte7 = max(arrayOfByte6[0], arrayOfByte6[2]);
    if (compareTo(arrayOfByte7, arrayOfByte8) == 1)
      return Collections.EMPTY_SET; 
    byte[] arrayOfByte9 = or(arrayOfByte6[0], arrayOfByte6[2]);
    byte[] arrayOfByte10 = or(arrayOfByte3, arrayOfByte5);
    return Collections.singleton(ipWithSubnetMask(arrayOfByte9, arrayOfByte10));
  }
  
  private byte[] ipWithSubnetMask(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = paramArrayOfbyte1.length;
    byte[] arrayOfByte = new byte[i * 2];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, i);
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, i, i);
    return arrayOfByte;
  }
  
  private byte[][] extractIPsAndSubnetMasks(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = paramArrayOfbyte1.length / 2;
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte1, 0, i);
    System.arraycopy(paramArrayOfbyte1, i, arrayOfByte2, 0, i);
    byte[] arrayOfByte3 = new byte[i];
    byte[] arrayOfByte4 = new byte[i];
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte3, 0, i);
    System.arraycopy(paramArrayOfbyte2, i, arrayOfByte4, 0, i);
    return new byte[][] { arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4 };
  }
  
  private byte[][] minMaxIPs(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) {
    int i = paramArrayOfbyte1.length;
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    byte[] arrayOfByte4 = new byte[i];
    for (byte b = 0; b < i; b++) {
      arrayOfByte1[b] = (byte)(paramArrayOfbyte1[b] & paramArrayOfbyte2[b]);
      arrayOfByte2[b] = (byte)(paramArrayOfbyte1[b] & paramArrayOfbyte2[b] | paramArrayOfbyte2[b] ^ 0xFFFFFFFF);
      arrayOfByte3[b] = (byte)(paramArrayOfbyte3[b] & paramArrayOfbyte4[b]);
      arrayOfByte4[b] = (byte)(paramArrayOfbyte3[b] & paramArrayOfbyte4[b] | paramArrayOfbyte4[b] ^ 0xFFFFFFFF);
    } 
    return new byte[][] { arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4 };
  }
  
  private void checkPermittedEmail(Set paramSet, String paramString) throws PKIXNameConstraintValidatorException {
    if (paramSet == null)
      return; 
    for (String str : paramSet) {
      if (emailIsConstrained(paramString, str))
        return; 
    } 
    if (paramString.length() == 0 && paramSet.size() == 0)
      return; 
    throw new PKIXNameConstraintValidatorException("Subject email address is not from a permitted subtree.");
  }
  
  private void checkExcludedEmail(Set paramSet, String paramString) throws PKIXNameConstraintValidatorException {
    if (paramSet.isEmpty())
      return; 
    for (String str : paramSet) {
      if (emailIsConstrained(paramString, str))
        throw new PKIXNameConstraintValidatorException("Email address is from an excluded subtree."); 
    } 
  }
  
  private void checkPermittedIP(Set paramSet, byte[] paramArrayOfbyte) throws PKIXNameConstraintValidatorException {
    if (paramSet == null)
      return; 
    for (byte[] arrayOfByte : paramSet) {
      if (isIPConstrained(paramArrayOfbyte, arrayOfByte))
        return; 
    } 
    if (paramArrayOfbyte.length == 0 && paramSet.size() == 0)
      return; 
    throw new PKIXNameConstraintValidatorException("IP is not from a permitted subtree.");
  }
  
  private void checkExcludedIP(Set paramSet, byte[] paramArrayOfbyte) throws PKIXNameConstraintValidatorException {
    if (paramSet.isEmpty())
      return; 
    for (byte[] arrayOfByte : paramSet) {
      if (isIPConstrained(paramArrayOfbyte, arrayOfByte))
        throw new PKIXNameConstraintValidatorException("IP is from an excluded subtree."); 
    } 
  }
  
  private boolean isIPConstrained(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = paramArrayOfbyte1.length;
    if (i != paramArrayOfbyte2.length / 2)
      return false; 
    byte[] arrayOfByte1 = new byte[i];
    System.arraycopy(paramArrayOfbyte2, i, arrayOfByte1, 0, i);
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    for (byte b = 0; b < i; b++) {
      arrayOfByte2[b] = (byte)(paramArrayOfbyte2[b] & arrayOfByte1[b]);
      arrayOfByte3[b] = (byte)(paramArrayOfbyte1[b] & arrayOfByte1[b]);
    } 
    return Arrays.areEqual(arrayOfByte2, arrayOfByte3);
  }
  
  private boolean emailIsConstrained(String paramString1, String paramString2) {
    String str = paramString1.substring(paramString1.indexOf('@') + 1);
    if (paramString2.indexOf('@') != -1) {
      if (paramString1.equalsIgnoreCase(paramString2))
        return true; 
      if (str.equalsIgnoreCase(paramString2.substring(1)))
        return true; 
    } else if (paramString2.charAt(0) != '.') {
      if (str.equalsIgnoreCase(paramString2))
        return true; 
    } else if (withinDomain(str, paramString2)) {
      return true;
    } 
    return false;
  }
  
  private boolean withinDomain(String paramString1, String paramString2) {
    String str = paramString2;
    if (str.startsWith("."))
      str = str.substring(1); 
    String[] arrayOfString1 = Strings.split(str, '.');
    String[] arrayOfString2 = Strings.split(paramString1, '.');
    if (arrayOfString2.length <= arrayOfString1.length)
      return false; 
    int i = arrayOfString2.length - arrayOfString1.length;
    for (byte b = -1; b < arrayOfString1.length; b++) {
      if (b == -1) {
        if (arrayOfString2[b + i].equals(""))
          return false; 
      } else if (!arrayOfString1[b].equalsIgnoreCase(arrayOfString2[b + i])) {
        return false;
      } 
    } 
    return true;
  }
  
  private void checkPermittedDNS(Set paramSet, String paramString) throws PKIXNameConstraintValidatorException {
    if (paramSet == null)
      return; 
    for (String str : paramSet) {
      if (withinDomain(paramString, str) || paramString.equalsIgnoreCase(str))
        return; 
    } 
    if (paramString.length() == 0 && paramSet.size() == 0)
      return; 
    throw new PKIXNameConstraintValidatorException("DNS is not from a permitted subtree.");
  }
  
  private void checkExcludedDNS(Set paramSet, String paramString) throws PKIXNameConstraintValidatorException {
    if (paramSet.isEmpty())
      return; 
    for (String str : paramSet) {
      if (withinDomain(paramString, str) || paramString.equalsIgnoreCase(str))
        throw new PKIXNameConstraintValidatorException("DNS is from an excluded subtree."); 
    } 
  }
  
  private void unionEmail(String paramString1, String paramString2, Set<String> paramSet) {
    if (paramString1.indexOf('@') != -1) {
      String str = paramString1.substring(paramString1.indexOf('@') + 1);
      if (paramString2.indexOf('@') != -1) {
        if (paramString1.equalsIgnoreCase(paramString2)) {
          paramSet.add(paramString1);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(str, paramString2)) {
          paramSet.add(paramString2);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (str.equalsIgnoreCase(paramString2)) {
        paramSet.add(paramString2);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString1.startsWith(".")) {
      if (paramString2.indexOf('@') != -1) {
        String str = paramString2.substring(paramString1.indexOf('@') + 1);
        if (withinDomain(str, paramString1)) {
          paramSet.add(paramString1);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(paramString1, paramString2) || paramString1.equalsIgnoreCase(paramString2)) {
          paramSet.add(paramString2);
        } else if (withinDomain(paramString2, paramString1)) {
          paramSet.add(paramString1);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (withinDomain(paramString2, paramString1)) {
        paramSet.add(paramString1);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString2.indexOf('@') != -1) {
      String str = paramString2.substring(paramString1.indexOf('@') + 1);
      if (str.equalsIgnoreCase(paramString1)) {
        paramSet.add(paramString1);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString2.startsWith(".")) {
      if (withinDomain(paramString1, paramString2)) {
        paramSet.add(paramString2);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString1.equalsIgnoreCase(paramString2)) {
      paramSet.add(paramString1);
    } else {
      paramSet.add(paramString1);
      paramSet.add(paramString2);
    } 
  }
  
  private void unionURI(String paramString1, String paramString2, Set<String> paramSet) {
    if (paramString1.indexOf('@') != -1) {
      String str = paramString1.substring(paramString1.indexOf('@') + 1);
      if (paramString2.indexOf('@') != -1) {
        if (paramString1.equalsIgnoreCase(paramString2)) {
          paramSet.add(paramString1);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(str, paramString2)) {
          paramSet.add(paramString2);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (str.equalsIgnoreCase(paramString2)) {
        paramSet.add(paramString2);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString1.startsWith(".")) {
      if (paramString2.indexOf('@') != -1) {
        String str = paramString2.substring(paramString1.indexOf('@') + 1);
        if (withinDomain(str, paramString1)) {
          paramSet.add(paramString1);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(paramString1, paramString2) || paramString1.equalsIgnoreCase(paramString2)) {
          paramSet.add(paramString2);
        } else if (withinDomain(paramString2, paramString1)) {
          paramSet.add(paramString1);
        } else {
          paramSet.add(paramString1);
          paramSet.add(paramString2);
        } 
      } else if (withinDomain(paramString2, paramString1)) {
        paramSet.add(paramString1);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString2.indexOf('@') != -1) {
      String str = paramString2.substring(paramString1.indexOf('@') + 1);
      if (str.equalsIgnoreCase(paramString1)) {
        paramSet.add(paramString1);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString2.startsWith(".")) {
      if (withinDomain(paramString1, paramString2)) {
        paramSet.add(paramString2);
      } else {
        paramSet.add(paramString1);
        paramSet.add(paramString2);
      } 
    } else if (paramString1.equalsIgnoreCase(paramString2)) {
      paramSet.add(paramString1);
    } else {
      paramSet.add(paramString1);
      paramSet.add(paramString2);
    } 
  }
  
  private Set intersectDNS(Set paramSet1, Set paramSet2) {
    HashSet<String> hashSet = new HashSet();
    Iterator<GeneralSubtree> iterator = paramSet2.iterator();
    while (iterator.hasNext()) {
      String str = extractNameAsString(((GeneralSubtree)iterator.next()).getBase());
      if (paramSet1 == null) {
        if (str != null)
          hashSet.add(str); 
        continue;
      } 
      for (String str1 : paramSet1) {
        if (withinDomain(str1, str)) {
          hashSet.add(str1);
          continue;
        } 
        if (withinDomain(str, str1))
          hashSet.add(str); 
      } 
    } 
    return hashSet;
  }
  
  protected Set unionDNS(Set<String> paramSet, String paramString) {
    if (paramSet.isEmpty()) {
      if (paramString == null)
        return paramSet; 
      paramSet.add(paramString);
      return paramSet;
    } 
    HashSet<String> hashSet = new HashSet();
    for (String str : paramSet) {
      if (withinDomain(str, paramString)) {
        hashSet.add(paramString);
        continue;
      } 
      if (withinDomain(paramString, str)) {
        hashSet.add(str);
        continue;
      } 
      hashSet.add(str);
      hashSet.add(paramString);
    } 
    return hashSet;
  }
  
  private void intersectEmail(String paramString1, String paramString2, Set<String> paramSet) {
    if (paramString1.indexOf('@') != -1) {
      String str = paramString1.substring(paramString1.indexOf('@') + 1);
      if (paramString2.indexOf('@') != -1) {
        if (paramString1.equalsIgnoreCase(paramString2))
          paramSet.add(paramString1); 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(str, paramString2))
          paramSet.add(paramString1); 
      } else if (str.equalsIgnoreCase(paramString2)) {
        paramSet.add(paramString1);
      } 
    } else if (paramString1.startsWith(".")) {
      if (paramString2.indexOf('@') != -1) {
        String str = paramString2.substring(paramString1.indexOf('@') + 1);
        if (withinDomain(str, paramString1))
          paramSet.add(paramString2); 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(paramString1, paramString2) || paramString1.equalsIgnoreCase(paramString2)) {
          paramSet.add(paramString1);
        } else if (withinDomain(paramString2, paramString1)) {
          paramSet.add(paramString2);
        } 
      } else if (withinDomain(paramString2, paramString1)) {
        paramSet.add(paramString2);
      } 
    } else if (paramString2.indexOf('@') != -1) {
      String str = paramString2.substring(paramString2.indexOf('@') + 1);
      if (str.equalsIgnoreCase(paramString1))
        paramSet.add(paramString2); 
    } else if (paramString2.startsWith(".")) {
      if (withinDomain(paramString1, paramString2))
        paramSet.add(paramString1); 
    } else if (paramString1.equalsIgnoreCase(paramString2)) {
      paramSet.add(paramString1);
    } 
  }
  
  private void checkExcludedURI(Set paramSet, String paramString) throws PKIXNameConstraintValidatorException {
    if (paramSet.isEmpty())
      return; 
    for (String str : paramSet) {
      if (isUriConstrained(paramString, str))
        throw new PKIXNameConstraintValidatorException("URI is from an excluded subtree."); 
    } 
  }
  
  private Set intersectURI(Set paramSet1, Set paramSet2) {
    HashSet<String> hashSet = new HashSet();
    Iterator<GeneralSubtree> iterator = paramSet2.iterator();
    while (iterator.hasNext()) {
      String str = extractNameAsString(((GeneralSubtree)iterator.next()).getBase());
      if (paramSet1 == null) {
        if (str != null)
          hashSet.add(str); 
        continue;
      } 
      for (String str1 : paramSet1)
        intersectURI(str1, str, hashSet); 
    } 
    return hashSet;
  }
  
  private Set unionURI(Set<String> paramSet, String paramString) {
    if (paramSet.isEmpty()) {
      if (paramString == null)
        return paramSet; 
      paramSet.add(paramString);
      return paramSet;
    } 
    HashSet hashSet = new HashSet();
    for (String str : paramSet)
      unionURI(str, paramString, hashSet); 
    return hashSet;
  }
  
  private void intersectURI(String paramString1, String paramString2, Set<String> paramSet) {
    if (paramString1.indexOf('@') != -1) {
      String str = paramString1.substring(paramString1.indexOf('@') + 1);
      if (paramString2.indexOf('@') != -1) {
        if (paramString1.equalsIgnoreCase(paramString2))
          paramSet.add(paramString1); 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(str, paramString2))
          paramSet.add(paramString1); 
      } else if (str.equalsIgnoreCase(paramString2)) {
        paramSet.add(paramString1);
      } 
    } else if (paramString1.startsWith(".")) {
      if (paramString2.indexOf('@') != -1) {
        String str = paramString2.substring(paramString1.indexOf('@') + 1);
        if (withinDomain(str, paramString1))
          paramSet.add(paramString2); 
      } else if (paramString2.startsWith(".")) {
        if (withinDomain(paramString1, paramString2) || paramString1.equalsIgnoreCase(paramString2)) {
          paramSet.add(paramString1);
        } else if (withinDomain(paramString2, paramString1)) {
          paramSet.add(paramString2);
        } 
      } else if (withinDomain(paramString2, paramString1)) {
        paramSet.add(paramString2);
      } 
    } else if (paramString2.indexOf('@') != -1) {
      String str = paramString2.substring(paramString2.indexOf('@') + 1);
      if (str.equalsIgnoreCase(paramString1))
        paramSet.add(paramString2); 
    } else if (paramString2.startsWith(".")) {
      if (withinDomain(paramString1, paramString2))
        paramSet.add(paramString1); 
    } else if (paramString1.equalsIgnoreCase(paramString2)) {
      paramSet.add(paramString1);
    } 
  }
  
  private void checkPermittedURI(Set paramSet, String paramString) throws PKIXNameConstraintValidatorException {
    if (paramSet == null)
      return; 
    for (String str : paramSet) {
      if (isUriConstrained(paramString, str))
        return; 
    } 
    if (paramString.length() == 0 && paramSet.size() == 0)
      return; 
    throw new PKIXNameConstraintValidatorException("URI is not from a permitted subtree.");
  }
  
  private boolean isUriConstrained(String paramString1, String paramString2) {
    String str = extractHostFromURL(paramString1);
    if (!paramString2.startsWith(".")) {
      if (str.equalsIgnoreCase(paramString2))
        return true; 
    } else if (withinDomain(str, paramString2)) {
      return true;
    } 
    return false;
  }
  
  private static String extractHostFromURL(String paramString) {
    String str = paramString.substring(paramString.indexOf(':') + 1);
    if (str.indexOf("//") != -1)
      str = str.substring(str.indexOf("//") + 2); 
    if (str.lastIndexOf(':') != -1)
      str = str.substring(0, str.lastIndexOf(':')); 
    str = str.substring(str.indexOf(':') + 1);
    str = str.substring(str.indexOf('@') + 1);
    if (str.indexOf('/') != -1)
      str = str.substring(0, str.indexOf('/')); 
    return str;
  }
  
  public void checkPermitted(GeneralName paramGeneralName) throws PKIXNameConstraintValidatorException {
    byte[] arrayOfByte;
    switch (paramGeneralName.getTagNo()) {
      case 1:
        checkPermittedEmail(this.permittedSubtreesEmail, extractNameAsString(paramGeneralName));
        break;
      case 2:
        checkPermittedDNS(this.permittedSubtreesDNS, DERIA5String.getInstance(paramGeneralName.getName()).getString());
        break;
      case 4:
        checkPermittedDN(ASN1Sequence.getInstance(paramGeneralName.getName().toASN1Primitive()));
        break;
      case 6:
        checkPermittedURI(this.permittedSubtreesURI, DERIA5String.getInstance(paramGeneralName.getName()).getString());
        break;
      case 7:
        arrayOfByte = ASN1OctetString.getInstance(paramGeneralName.getName()).getOctets();
        checkPermittedIP(this.permittedSubtreesIP, arrayOfByte);
        break;
    } 
  }
  
  public void checkExcluded(GeneralName paramGeneralName) throws PKIXNameConstraintValidatorException {
    byte[] arrayOfByte;
    switch (paramGeneralName.getTagNo()) {
      case 1:
        checkExcludedEmail(this.excludedSubtreesEmail, extractNameAsString(paramGeneralName));
        break;
      case 2:
        checkExcludedDNS(this.excludedSubtreesDNS, DERIA5String.getInstance(paramGeneralName.getName()).getString());
        break;
      case 4:
        checkExcludedDN(ASN1Sequence.getInstance(paramGeneralName.getName().toASN1Primitive()));
        break;
      case 6:
        checkExcludedURI(this.excludedSubtreesURI, DERIA5String.getInstance(paramGeneralName.getName()).getString());
        break;
      case 7:
        arrayOfByte = ASN1OctetString.getInstance(paramGeneralName.getName()).getOctets();
        checkExcludedIP(this.excludedSubtreesIP, arrayOfByte);
        break;
    } 
  }
  
  public void intersectPermittedSubtree(GeneralSubtree paramGeneralSubtree) {
    intersectPermittedSubtree(new GeneralSubtree[] { paramGeneralSubtree });
  }
  
  public void intersectPermittedSubtree(GeneralSubtree[] paramArrayOfGeneralSubtree) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    for (byte b = 0; b != paramArrayOfGeneralSubtree.length; b++) {
      GeneralSubtree generalSubtree = paramArrayOfGeneralSubtree[b];
      Integer integer = Integers.valueOf(generalSubtree.getBase().getTagNo());
      if (hashMap.get(integer) == null)
        hashMap.put(integer, new HashSet()); 
      ((Set<GeneralSubtree>)hashMap.get(integer)).add(generalSubtree);
    } 
    for (Map.Entry<Object, Object> entry : hashMap.entrySet()) {
      switch (((Integer)entry.getKey()).intValue()) {
        case 1:
          this.permittedSubtreesEmail = intersectEmail(this.permittedSubtreesEmail, (Set)entry.getValue());
        case 2:
          this.permittedSubtreesDNS = intersectDNS(this.permittedSubtreesDNS, (Set)entry.getValue());
        case 4:
          this.permittedSubtreesDN = intersectDN(this.permittedSubtreesDN, (Set)entry.getValue());
        case 6:
          this.permittedSubtreesURI = intersectURI(this.permittedSubtreesURI, (Set)entry.getValue());
        case 7:
          this.permittedSubtreesIP = intersectIP(this.permittedSubtreesIP, (Set)entry.getValue());
      } 
    } 
  }
  
  private String extractNameAsString(GeneralName paramGeneralName) {
    return DERIA5String.getInstance(paramGeneralName.getName()).getString();
  }
  
  public void intersectEmptyPermittedSubtree(int paramInt) {
    switch (paramInt) {
      case 1:
        this.permittedSubtreesEmail = new HashSet();
        break;
      case 2:
        this.permittedSubtreesDNS = new HashSet();
        break;
      case 4:
        this.permittedSubtreesDN = new HashSet();
        break;
      case 6:
        this.permittedSubtreesURI = new HashSet();
        break;
      case 7:
        this.permittedSubtreesIP = new HashSet();
        break;
    } 
  }
  
  public void addExcludedSubtree(GeneralSubtree paramGeneralSubtree) {
    GeneralName generalName = paramGeneralSubtree.getBase();
    switch (generalName.getTagNo()) {
      case 1:
        this.excludedSubtreesEmail = unionEmail(this.excludedSubtreesEmail, extractNameAsString(generalName));
        break;
      case 2:
        this.excludedSubtreesDNS = unionDNS(this.excludedSubtreesDNS, extractNameAsString(generalName));
        break;
      case 4:
        this.excludedSubtreesDN = unionDN(this.excludedSubtreesDN, (ASN1Sequence)generalName.getName().toASN1Primitive());
        break;
      case 6:
        this.excludedSubtreesURI = unionURI(this.excludedSubtreesURI, extractNameAsString(generalName));
        break;
      case 7:
        this.excludedSubtreesIP = unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(generalName.getName()).getOctets());
        break;
    } 
  }
  
  private static byte[] max(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    for (byte b = 0; b < paramArrayOfbyte1.length; b++) {
      if ((paramArrayOfbyte1[b] & 0xFFFF) > (paramArrayOfbyte2[b] & 0xFFFF))
        return paramArrayOfbyte1; 
    } 
    return paramArrayOfbyte2;
  }
  
  private static byte[] min(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    for (byte b = 0; b < paramArrayOfbyte1.length; b++) {
      if ((paramArrayOfbyte1[b] & 0xFFFF) < (paramArrayOfbyte2[b] & 0xFFFF))
        return paramArrayOfbyte1; 
    } 
    return paramArrayOfbyte2;
  }
  
  private static int compareTo(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return Arrays.areEqual(paramArrayOfbyte1, paramArrayOfbyte2) ? 0 : (Arrays.areEqual(max(paramArrayOfbyte1, paramArrayOfbyte2), paramArrayOfbyte1) ? 1 : -1);
  }
  
  private static byte[] or(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length];
    for (byte b = 0; b < paramArrayOfbyte1.length; b++)
      arrayOfByte[b] = (byte)(paramArrayOfbyte1[b] | paramArrayOfbyte2[b]); 
    return arrayOfByte;
  }
  
  public int hashCode() {
    return hashCollection(this.excludedSubtreesDN) + hashCollection(this.excludedSubtreesDNS) + hashCollection(this.excludedSubtreesEmail) + hashCollection(this.excludedSubtreesIP) + hashCollection(this.excludedSubtreesURI) + hashCollection(this.permittedSubtreesDN) + hashCollection(this.permittedSubtreesDNS) + hashCollection(this.permittedSubtreesEmail) + hashCollection(this.permittedSubtreesIP) + hashCollection(this.permittedSubtreesURI);
  }
  
  private int hashCollection(Collection paramCollection) {
    if (paramCollection == null)
      return 0; 
    int i = 0;
    for (byte[] arrayOfByte : paramCollection) {
      if (arrayOfByte instanceof byte[]) {
        i += Arrays.hashCode(arrayOfByte);
        continue;
      } 
      i += arrayOfByte.hashCode();
    } 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof PKIXNameConstraintValidator))
      return false; 
    PKIXNameConstraintValidator pKIXNameConstraintValidator = (PKIXNameConstraintValidator)paramObject;
    return (collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesDN, this.excludedSubtreesDN) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesDNS, this.excludedSubtreesDNS) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesEmail, this.excludedSubtreesEmail) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesIP, this.excludedSubtreesIP) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesURI, this.excludedSubtreesURI) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesDN, this.permittedSubtreesDN) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesDNS, this.permittedSubtreesDNS) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesEmail, this.permittedSubtreesEmail) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesIP, this.permittedSubtreesIP) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesURI, this.permittedSubtreesURI));
  }
  
  private boolean collectionsAreEqual(Collection paramCollection1, Collection paramCollection2) {
    if (paramCollection1 == paramCollection2)
      return true; 
    if (paramCollection1 == null || paramCollection2 == null)
      return false; 
    if (paramCollection1.size() != paramCollection2.size())
      return false; 
    for (Object object : paramCollection1) {
      Iterator<Object> iterator = paramCollection2.iterator();
      boolean bool = false;
      while (iterator.hasNext()) {
        Object object1 = iterator.next();
        if (equals(object, object1)) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  private boolean equals(Object paramObject1, Object paramObject2) {
    return (paramObject1 == paramObject2) ? true : ((paramObject1 == null || paramObject2 == null) ? false : ((paramObject1 instanceof byte[] && paramObject2 instanceof byte[]) ? Arrays.areEqual((byte[])paramObject1, (byte[])paramObject2) : paramObject1.equals(paramObject2)));
  }
  
  private String stringifyIP(byte[] paramArrayOfbyte) {
    null = "";
    int i;
    for (i = 0; i < paramArrayOfbyte.length / 2; i++)
      null = null + Integer.toString(paramArrayOfbyte[i] & 0xFF) + "."; 
    null = null.substring(0, null.length() - 1);
    null = null + "/";
    for (i = paramArrayOfbyte.length / 2; i < paramArrayOfbyte.length; i++)
      null = null + Integer.toString(paramArrayOfbyte[i] & 0xFF) + "."; 
    return null.substring(0, null.length() - 1);
  }
  
  private String stringifyIPCollection(Set paramSet) {
    null = "";
    null = null + "[";
    Iterator<byte[]> iterator = paramSet.iterator();
    while (iterator.hasNext())
      null = null + stringifyIP(iterator.next()) + ","; 
    if (null.length() > 1)
      null = null.substring(0, null.length() - 1); 
    return null + "]";
  }
  
  public String toString() {
    String str = "";
    str = str + "permitted:\n";
    if (this.permittedSubtreesDN != null) {
      str = str + "DN:\n";
      str = str + this.permittedSubtreesDN.toString() + "\n";
    } 
    if (this.permittedSubtreesDNS != null) {
      str = str + "DNS:\n";
      str = str + this.permittedSubtreesDNS.toString() + "\n";
    } 
    if (this.permittedSubtreesEmail != null) {
      str = str + "Email:\n";
      str = str + this.permittedSubtreesEmail.toString() + "\n";
    } 
    if (this.permittedSubtreesURI != null) {
      str = str + "URI:\n";
      str = str + this.permittedSubtreesURI.toString() + "\n";
    } 
    if (this.permittedSubtreesIP != null) {
      str = str + "IP:\n";
      str = str + stringifyIPCollection(this.permittedSubtreesIP) + "\n";
    } 
    str = str + "excluded:\n";
    if (!this.excludedSubtreesDN.isEmpty()) {
      str = str + "DN:\n";
      str = str + this.excludedSubtreesDN.toString() + "\n";
    } 
    if (!this.excludedSubtreesDNS.isEmpty()) {
      str = str + "DNS:\n";
      str = str + this.excludedSubtreesDNS.toString() + "\n";
    } 
    if (!this.excludedSubtreesEmail.isEmpty()) {
      str = str + "Email:\n";
      str = str + this.excludedSubtreesEmail.toString() + "\n";
    } 
    if (!this.excludedSubtreesURI.isEmpty()) {
      str = str + "URI:\n";
      str = str + this.excludedSubtreesURI.toString() + "\n";
    } 
    if (!this.excludedSubtreesIP.isEmpty()) {
      str = str + "IP:\n";
      str = str + stringifyIPCollection(this.excludedSubtreesIP) + "\n";
    } 
    return str;
  }
}
