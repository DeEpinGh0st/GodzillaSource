package org.springframework.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import org.springframework.lang.Nullable;











































public class MimeType
  implements Comparable<MimeType>, Serializable
{
  private static final long serialVersionUID = 4085923477777865903L;
  protected static final String WILDCARD_TYPE = "*";
  private static final String PARAM_CHARSET = "charset";
  
  static {
    BitSet ctl = new BitSet(128);
    for (int i = 0; i <= 31; i++) {
      ctl.set(i);
    }
    ctl.set(127);
    
    BitSet separators = new BitSet(128);
    separators.set(40);
    separators.set(41);
    separators.set(60);
    separators.set(62);
    separators.set(64);
    separators.set(44);
    separators.set(59);
    separators.set(58);
    separators.set(92);
    separators.set(34);
    separators.set(47);
    separators.set(91);
    separators.set(93);
    separators.set(63);
    separators.set(61);
    separators.set(123);
    separators.set(125);
    separators.set(32);
    separators.set(9);
  }
  private static final BitSet TOKEN = new BitSet(128); static {
    TOKEN.set(0, 128);
    TOKEN.andNot(ctl);
    TOKEN.andNot(separators);
  }



  
  private final String type;

  
  private final String subtype;

  
  private final Map<String, String> parameters;

  
  @Nullable
  private transient Charset resolvedCharset;

  
  @Nullable
  private volatile String toStringValue;


  
  public MimeType(String type) {
    this(type, "*");
  }







  
  public MimeType(String type, String subtype) {
    this(type, subtype, Collections.emptyMap());
  }







  
  public MimeType(String type, String subtype, Charset charset) {
    this(type, subtype, Collections.singletonMap("charset", charset.name()));
    this.resolvedCharset = charset;
  }








  
  public MimeType(MimeType other, Charset charset) {
    this(other.getType(), other.getSubtype(), addCharsetParameter(charset, other.getParameters()));
    this.resolvedCharset = charset;
  }







  
  public MimeType(MimeType other, @Nullable Map<String, String> parameters) {
    this(other.getType(), other.getSubtype(), parameters);
  }







  
  public MimeType(String type, String subtype, @Nullable Map<String, String> parameters) {
    Assert.hasLength(type, "'type' must not be empty");
    Assert.hasLength(subtype, "'subtype' must not be empty");
    checkToken(type);
    checkToken(subtype);
    this.type = type.toLowerCase(Locale.ENGLISH);
    this.subtype = subtype.toLowerCase(Locale.ENGLISH);
    if (!CollectionUtils.isEmpty(parameters)) {
      Map<String, String> map = new LinkedCaseInsensitiveMap<>(parameters.size(), Locale.ENGLISH);
      parameters.forEach((parameter, value) -> {
            checkParameters(parameter, value);
            map.put(parameter, value);
          });
      this.parameters = Collections.unmodifiableMap(map);
    } else {
      
      this.parameters = Collections.emptyMap();
    } 
  }






  
  protected MimeType(MimeType other) {
    this.type = other.type;
    this.subtype = other.subtype;
    this.parameters = other.parameters;
    this.resolvedCharset = other.resolvedCharset;
    this.toStringValue = other.toStringValue;
  }






  
  private void checkToken(String token) {
    for (int i = 0; i < token.length(); i++) {
      char ch = token.charAt(i);
      if (!TOKEN.get(ch)) {
        throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
      }
    } 
  }
  
  protected void checkParameters(String parameter, String value) {
    Assert.hasLength(parameter, "'parameter' must not be empty");
    Assert.hasLength(value, "'value' must not be empty");
    checkToken(parameter);
    if ("charset".equals(parameter)) {
      if (this.resolvedCharset == null) {
        this.resolvedCharset = Charset.forName(unquote(value));
      }
    }
    else if (!isQuotedString(value)) {
      checkToken(value);
    } 
  }
  
  private boolean isQuotedString(String s) {
    if (s.length() < 2) {
      return false;
    }
    
    return ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
  }

  
  protected String unquote(String s) {
    return isQuotedString(s) ? s.substring(1, s.length() - 1) : s;
  }




  
  public boolean isWildcardType() {
    return "*".equals(getType());
  }






  
  public boolean isWildcardSubtype() {
    return ("*".equals(getSubtype()) || getSubtype().startsWith("*+"));
  }





  
  public boolean isConcrete() {
    return (!isWildcardType() && !isWildcardSubtype());
  }



  
  public String getType() {
    return this.type;
  }



  
  public String getSubtype() {
    return this.subtype;
  }




  
  @Nullable
  public String getSubtypeSuffix() {
    int suffixIndex = this.subtype.lastIndexOf('+');
    if (suffixIndex != -1 && this.subtype.length() > suffixIndex) {
      return this.subtype.substring(suffixIndex + 1);
    }
    return null;
  }





  
  @Nullable
  public Charset getCharset() {
    return this.resolvedCharset;
  }





  
  @Nullable
  public String getParameter(String name) {
    return this.parameters.get(name);
  }




  
  public Map<String, String> getParameters() {
    return this.parameters;
  }









  
  public boolean includes(@Nullable MimeType other) {
    if (other == null) {
      return false;
    }
    if (isWildcardType())
    {
      return true;
    }
    if (getType().equals(other.getType())) {
      if (getSubtype().equals(other.getSubtype())) {
        return true;
      }
      if (isWildcardSubtype()) {
        
        int thisPlusIdx = getSubtype().lastIndexOf('+');
        if (thisPlusIdx == -1) {
          return true;
        }

        
        int otherPlusIdx = other.getSubtype().lastIndexOf('+');
        if (otherPlusIdx != -1) {
          String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
          String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
          String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
          if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && "*".equals(thisSubtypeNoSuffix)) {
            return true;
          }
        } 
      } 
    } 
    
    return false;
  }









  
  public boolean isCompatibleWith(@Nullable MimeType other) {
    if (other == null) {
      return false;
    }
    if (isWildcardType() || other.isWildcardType()) {
      return true;
    }
    if (getType().equals(other.getType())) {
      if (getSubtype().equals(other.getSubtype())) {
        return true;
      }
      if (isWildcardSubtype() || other.isWildcardSubtype()) {
        String thisSuffix = getSubtypeSuffix();
        String otherSuffix = other.getSubtypeSuffix();
        if (getSubtype().equals("*") || other.getSubtype().equals("*")) {
          return true;
        }
        if (isWildcardSubtype() && thisSuffix != null) {
          return (thisSuffix.equals(other.getSubtype()) || thisSuffix.equals(otherSuffix));
        }
        if (other.isWildcardSubtype() && otherSuffix != null) {
          return (getSubtype().equals(otherSuffix) || otherSuffix.equals(thisSuffix));
        }
      } 
    } 
    return false;
  }







  
  public boolean equalsTypeAndSubtype(@Nullable MimeType other) {
    if (other == null) {
      return false;
    }
    return (this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype));
  }








  
  public boolean isPresentIn(Collection<? extends MimeType> mimeTypes) {
    for (MimeType mimeType : mimeTypes) {
      if (mimeType.equalsTypeAndSubtype(this)) {
        return true;
      }
    } 
    return false;
  }


  
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof MimeType)) {
      return false;
    }
    MimeType otherType = (MimeType)other;
    return (this.type.equalsIgnoreCase(otherType.type) && this.subtype
      .equalsIgnoreCase(otherType.subtype) && 
      parametersAreEqual(otherType));
  }






  
  private boolean parametersAreEqual(MimeType other) {
    if (this.parameters.size() != other.parameters.size()) {
      return false;
    }
    
    for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
      String key = entry.getKey();
      if (!other.parameters.containsKey(key)) {
        return false;
      }
      if ("charset".equals(key)) {
        if (!ObjectUtils.nullSafeEquals(getCharset(), other.getCharset()))
          return false; 
        continue;
      } 
      if (!ObjectUtils.nullSafeEquals(entry.getValue(), other.parameters.get(key))) {
        return false;
      }
    } 
    
    return true;
  }

  
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.subtype.hashCode();
    result = 31 * result + this.parameters.hashCode();
    return result;
  }

  
  public String toString() {
    String value = this.toStringValue;
    if (value == null) {
      StringBuilder builder = new StringBuilder();
      appendTo(builder);
      value = builder.toString();
      this.toStringValue = value;
    } 
    return value;
  }
  
  protected void appendTo(StringBuilder builder) {
    builder.append(this.type);
    builder.append('/');
    builder.append(this.subtype);
    appendTo(this.parameters, builder);
  }
  
  private void appendTo(Map<String, String> map, StringBuilder builder) {
    map.forEach((key, val) -> {
          builder.append(';');
          builder.append(key);
          builder.append('=');
          builder.append(val);
        });
  }






  
  public int compareTo(MimeType other) {
    int comp = getType().compareToIgnoreCase(other.getType());
    if (comp != 0) {
      return comp;
    }
    comp = getSubtype().compareToIgnoreCase(other.getSubtype());
    if (comp != 0) {
      return comp;
    }
    comp = getParameters().size() - other.getParameters().size();
    if (comp != 0) {
      return comp;
    }
    
    TreeSet<String> thisAttributes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    thisAttributes.addAll(getParameters().keySet());
    TreeSet<String> otherAttributes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    otherAttributes.addAll(other.getParameters().keySet());
    Iterator<String> thisAttributesIterator = thisAttributes.iterator();
    Iterator<String> otherAttributesIterator = otherAttributes.iterator();
    
    while (thisAttributesIterator.hasNext()) {
      String thisAttribute = thisAttributesIterator.next();
      String otherAttribute = otherAttributesIterator.next();
      comp = thisAttribute.compareToIgnoreCase(otherAttribute);
      if (comp != 0) {
        return comp;
      }
      if ("charset".equals(thisAttribute)) {
        Charset thisCharset = getCharset();
        Charset otherCharset = other.getCharset();
        if (thisCharset != otherCharset) {
          if (thisCharset == null) {
            return -1;
          }
          if (otherCharset == null) {
            return 1;
          }
          comp = thisCharset.compareTo(otherCharset);
          if (comp != 0) {
            return comp;
          }
        } 
        continue;
      } 
      String thisValue = getParameters().get(thisAttribute);
      String otherValue = other.getParameters().get(otherAttribute);
      if (otherValue == null) {
        otherValue = "";
      }
      comp = thisValue.compareTo(otherValue);
      if (comp != 0) {
        return comp;
      }
    } 

    
    return 0;
  }

  
  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();

    
    String charsetName = getParameter("charset");
    if (charsetName != null) {
      this.resolvedCharset = Charset.forName(unquote(charsetName));
    }
  }







  
  public static MimeType valueOf(String value) {
    return MimeTypeUtils.parseMimeType(value);
  }
  
  private static Map<String, String> addCharsetParameter(Charset charset, Map<String, String> parameters) {
    Map<String, String> map = new LinkedHashMap<>(parameters);
    map.put("charset", charset.name());
    return map;
  }






  
  public static class SpecificityComparator<T extends MimeType>
    implements Comparator<T>
  {
    public int compare(T mimeType1, T mimeType2) {
      if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) {
        return 1;
      }
      if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) {
        return -1;
      }
      
      if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) {
        return 1;
      }
      if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) {
        return -1;
      }
      
      return compareParameters(mimeType1, mimeType2);
    }


    
    protected int compareParameters(T mimeType1, T mimeType2) {
      int paramsSize1 = mimeType1.getParameters().size();
      int paramsSize2 = mimeType2.getParameters().size();
      return Integer.compare(paramsSize2, paramsSize1);
    }
  }
}
