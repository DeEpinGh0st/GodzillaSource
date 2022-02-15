package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
















































@Immutable
@Beta
@GwtCompatible
public final class MediaType
{
  private static final String CHARSET_ATTRIBUTE = "charset";
  private static final ImmutableListMultimap<String, String> UTF_8_CONSTANT_PARAMETERS = ImmutableListMultimap.of("charset", Ascii.toLowerCase(Charsets.UTF_8.name()));


  
  private static final CharMatcher TOKEN_MATCHER = CharMatcher.ascii()
    .and(CharMatcher.javaIsoControl().negate())
    .and(CharMatcher.isNot(' '))
    .and(CharMatcher.noneOf("()<>@,;:\\\"/[]?="));
  
  private static final CharMatcher QUOTED_TEXT_MATCHER = CharMatcher.ascii().and(CharMatcher.noneOf("\"\\\r"));




  
  private static final CharMatcher LINEAR_WHITE_SPACE = CharMatcher.anyOf(" \t\r\n");
  
  private static final String APPLICATION_TYPE = "application";
  
  private static final String AUDIO_TYPE = "audio";
  
  private static final String IMAGE_TYPE = "image";
  
  private static final String TEXT_TYPE = "text";
  private static final String VIDEO_TYPE = "video";
  private static final String WILDCARD = "*";
  private static final Map<MediaType, MediaType> KNOWN_TYPES = Maps.newHashMap();

  
  private static MediaType createConstant(String type, String subtype) {
    MediaType mediaType = addKnownType(new MediaType(type, subtype, ImmutableListMultimap.of()));
    mediaType.parsedCharset = Optional.absent();
    return mediaType;
  }
  
  private static MediaType createConstantUtf8(String type, String subtype) {
    MediaType mediaType = addKnownType(new MediaType(type, subtype, UTF_8_CONSTANT_PARAMETERS));
    mediaType.parsedCharset = Optional.of(Charsets.UTF_8);
    return mediaType;
  }
  
  private static MediaType addKnownType(MediaType mediaType) {
    KNOWN_TYPES.put(mediaType, mediaType);
    return mediaType;
  }










  
  public static final MediaType ANY_TYPE = createConstant("*", "*");
  public static final MediaType ANY_TEXT_TYPE = createConstant("text", "*");
  public static final MediaType ANY_IMAGE_TYPE = createConstant("image", "*");
  public static final MediaType ANY_AUDIO_TYPE = createConstant("audio", "*");
  public static final MediaType ANY_VIDEO_TYPE = createConstant("video", "*");
  public static final MediaType ANY_APPLICATION_TYPE = createConstant("application", "*");


  
  public static final MediaType CACHE_MANIFEST_UTF_8 = createConstantUtf8("text", "cache-manifest");
  public static final MediaType CSS_UTF_8 = createConstantUtf8("text", "css");
  public static final MediaType CSV_UTF_8 = createConstantUtf8("text", "csv");
  public static final MediaType HTML_UTF_8 = createConstantUtf8("text", "html");
  public static final MediaType I_CALENDAR_UTF_8 = createConstantUtf8("text", "calendar");
  public static final MediaType PLAIN_TEXT_UTF_8 = createConstantUtf8("text", "plain");





  
  public static final MediaType TEXT_JAVASCRIPT_UTF_8 = createConstantUtf8("text", "javascript");





  
  public static final MediaType TSV_UTF_8 = createConstantUtf8("text", "tab-separated-values");
  
  public static final MediaType VCARD_UTF_8 = createConstantUtf8("text", "vcard");






  
  public static final MediaType WML_UTF_8 = createConstantUtf8("text", "vnd.wap.wml");





  
  public static final MediaType XML_UTF_8 = createConstantUtf8("text", "xml");






  
  public static final MediaType VTT_UTF_8 = createConstantUtf8("text", "vtt");






  
  public static final MediaType BMP = createConstant("image", "bmp");









  
  public static final MediaType CRW = createConstant("image", "x-canon-crw");
  
  public static final MediaType GIF = createConstant("image", "gif");
  public static final MediaType ICO = createConstant("image", "vnd.microsoft.icon");
  public static final MediaType JPEG = createConstant("image", "jpeg");
  public static final MediaType PNG = createConstant("image", "png");

















  
  public static final MediaType PSD = createConstant("image", "vnd.adobe.photoshop");
  
  public static final MediaType SVG_UTF_8 = createConstantUtf8("image", "svg+xml");
  public static final MediaType TIFF = createConstant("image", "tiff");





  
  public static final MediaType WEBP = createConstant("image", "webp");

  
  public static final MediaType MP4_AUDIO = createConstant("audio", "mp4");
  public static final MediaType MPEG_AUDIO = createConstant("audio", "mpeg");
  public static final MediaType OGG_AUDIO = createConstant("audio", "ogg");
  public static final MediaType WEBM_AUDIO = createConstant("audio", "webm");





  
  public static final MediaType L16_AUDIO = createConstant("audio", "l16");





  
  public static final MediaType L24_AUDIO = createConstant("audio", "l24");






  
  public static final MediaType BASIC_AUDIO = createConstant("audio", "basic");






  
  public static final MediaType AAC_AUDIO = createConstant("audio", "aac");





  
  public static final MediaType VORBIS_AUDIO = createConstant("audio", "vorbis");







  
  public static final MediaType WMA_AUDIO = createConstant("audio", "x-ms-wma");







  
  public static final MediaType WAX_AUDIO = createConstant("audio", "x-ms-wax");






  
  public static final MediaType VND_REAL_AUDIO = createConstant("audio", "vnd.rn-realaudio");





  
  public static final MediaType VND_WAVE_AUDIO = createConstant("audio", "vnd.wave");

  
  public static final MediaType MP4_VIDEO = createConstant("video", "mp4");
  public static final MediaType MPEG_VIDEO = createConstant("video", "mpeg");
  public static final MediaType OGG_VIDEO = createConstant("video", "ogg");
  public static final MediaType QUICKTIME = createConstant("video", "quicktime");
  public static final MediaType WEBM_VIDEO = createConstant("video", "webm");
  public static final MediaType WMV = createConstant("video", "x-ms-wmv");







  
  public static final MediaType FLV_VIDEO = createConstant("video", "x-flv");







  
  public static final MediaType THREE_GPP_VIDEO = createConstant("video", "3gpp");







  
  public static final MediaType THREE_GPP2_VIDEO = createConstant("video", "3gpp2");








  
  public static final MediaType APPLICATION_XML_UTF_8 = createConstantUtf8("application", "xml");
  
  public static final MediaType ATOM_UTF_8 = createConstantUtf8("application", "atom+xml");
  public static final MediaType BZIP2 = createConstant("application", "x-bzip2");






  
  public static final MediaType DART_UTF_8 = createConstantUtf8("application", "dart");






  
  public static final MediaType APPLE_PASSBOOK = createConstant("application", "vnd.apple.pkpass");







  
  public static final MediaType EOT = createConstant("application", "vnd.ms-fontobject");









  
  public static final MediaType EPUB = createConstant("application", "epub+zip");

  
  public static final MediaType FORM_DATA = createConstant("application", "x-www-form-urlencoded");







  
  public static final MediaType KEY_ARCHIVE = createConstant("application", "pkcs12");










  
  public static final MediaType APPLICATION_BINARY = createConstant("application", "binary");
  
  public static final MediaType GZIP = createConstant("application", "x-gzip");






  
  public static final MediaType HAL_JSON = createConstant("application", "hal+json");






  
  public static final MediaType JAVASCRIPT_UTF_8 = createConstantUtf8("application", "javascript");






  
  public static final MediaType JOSE = createConstant("application", "jose");






  
  public static final MediaType JOSE_JSON = createConstant("application", "jose+json");
  
  public static final MediaType JSON_UTF_8 = createConstantUtf8("application", "json");






  
  public static final MediaType MANIFEST_JSON_UTF_8 = createConstantUtf8("application", "manifest+json");



  
  public static final MediaType KML = createConstant("application", "vnd.google-earth.kml+xml");




  
  public static final MediaType KMZ = createConstant("application", "vnd.google-earth.kmz");





  
  public static final MediaType MBOX = createConstant("application", "mbox");






  
  public static final MediaType APPLE_MOBILE_CONFIG = createConstant("application", "x-apple-aspen-config");

  
  public static final MediaType MICROSOFT_EXCEL = createConstant("application", "vnd.ms-excel");






  
  public static final MediaType MICROSOFT_OUTLOOK = createConstant("application", "vnd.ms-outlook");


  
  public static final MediaType MICROSOFT_POWERPOINT = createConstant("application", "vnd.ms-powerpoint");

  
  public static final MediaType MICROSOFT_WORD = createConstant("application", "msword");






  
  public static final MediaType WASM_APPLICATION = createConstant("application", "wasm");







  
  public static final MediaType NACL_APPLICATION = createConstant("application", "x-nacl");








  
  public static final MediaType NACL_PORTABLE_APPLICATION = createConstant("application", "x-pnacl");
  
  public static final MediaType OCTET_STREAM = createConstant("application", "octet-stream");
  
  public static final MediaType OGG_CONTAINER = createConstant("application", "ogg");
  
  public static final MediaType OOXML_DOCUMENT = createConstant("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");

  
  public static final MediaType OOXML_PRESENTATION = createConstant("application", "vnd.openxmlformats-officedocument.presentationml.presentation");

  
  public static final MediaType OOXML_SHEET = createConstant("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
  
  public static final MediaType OPENDOCUMENT_GRAPHICS = createConstant("application", "vnd.oasis.opendocument.graphics");
  
  public static final MediaType OPENDOCUMENT_PRESENTATION = createConstant("application", "vnd.oasis.opendocument.presentation");
  
  public static final MediaType OPENDOCUMENT_SPREADSHEET = createConstant("application", "vnd.oasis.opendocument.spreadsheet");
  
  public static final MediaType OPENDOCUMENT_TEXT = createConstant("application", "vnd.oasis.opendocument.text");
  public static final MediaType PDF = createConstant("application", "pdf");
  public static final MediaType POSTSCRIPT = createConstant("application", "postscript");





  
  public static final MediaType PROTOBUF = createConstant("application", "protobuf");








  
  public static final MediaType RDF_XML_UTF_8 = createConstantUtf8("application", "rdf+xml");
  
  public static final MediaType RTF_UTF_8 = createConstantUtf8("application", "rtf");








  
  public static final MediaType SFNT = createConstant("application", "font-sfnt");

  
  public static final MediaType SHOCKWAVE_FLASH = createConstant("application", "x-shockwave-flash");






  
  public static final MediaType SKETCHUP = createConstant("application", "vnd.sketchup.skp");











  
  public static final MediaType SOAP_XML_UTF_8 = createConstantUtf8("application", "soap+xml");
  
  public static final MediaType TAR = createConstant("application", "x-tar");








  
  public static final MediaType WOFF = createConstant("application", "font-woff");






  
  public static final MediaType WOFF2 = createConstant("application", "font-woff2");
  
  public static final MediaType XHTML_UTF_8 = createConstantUtf8("application", "xhtml+xml");








  
  public static final MediaType XRD_UTF_8 = createConstantUtf8("application", "xrd+xml");
  
  public static final MediaType ZIP = createConstant("application", "zip");
  
  private final String type;
  private final String subtype;
  private final ImmutableListMultimap<String, String> parameters;
  @LazyInit
  private String toString;
  @LazyInit
  private int hashCode;
  @LazyInit
  private Optional<Charset> parsedCharset;
  
  private MediaType(String type, String subtype, ImmutableListMultimap<String, String> parameters) {
    this.type = type;
    this.subtype = subtype;
    this.parameters = parameters;
  }

  
  public String type() {
    return this.type;
  }

  
  public String subtype() {
    return this.subtype;
  }

  
  public ImmutableListMultimap<String, String> parameters() {
    return this.parameters;
  }
  
  private Map<String, ImmutableMultiset<String>> parametersAsMap() {
    return Maps.transformValues((Map)this.parameters
        .asMap(), new Function<Collection<String>, ImmutableMultiset<String>>()
        {
          public ImmutableMultiset<String> apply(Collection<String> input)
          {
            return ImmutableMultiset.copyOf(input);
          }
        });
  }









  
  public Optional<Charset> charset() {
    Optional<Charset> local = this.parsedCharset;
    if (local == null) {
      String value = null;
      local = Optional.absent();
      for (UnmodifiableIterator<String> unmodifiableIterator = this.parameters.get("charset").iterator(); unmodifiableIterator.hasNext(); ) { String currentValue = unmodifiableIterator.next();
        if (value == null) {
          value = currentValue;
          local = Optional.of(Charset.forName(value)); continue;
        }  if (!value.equals(currentValue)) {
          throw new IllegalStateException("Multiple charset values defined: " + value + ", " + currentValue);
        } }

      
      this.parsedCharset = local;
    } 
    return local;
  }




  
  public MediaType withoutParameters() {
    return this.parameters.isEmpty() ? this : create(this.type, this.subtype);
  }





  
  public MediaType withParameters(Multimap<String, String> parameters) {
    return create(this.type, this.subtype, parameters);
  }







  
  public MediaType withParameters(String attribute, Iterable<String> values) {
    Preconditions.checkNotNull(attribute);
    Preconditions.checkNotNull(values);
    String normalizedAttribute = normalizeToken(attribute);
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
    for (UnmodifiableIterator<Map.Entry<String, String>> unmodifiableIterator = this.parameters.entries().iterator(); unmodifiableIterator.hasNext(); ) { Map.Entry<String, String> entry = unmodifiableIterator.next();
      String key = entry.getKey();
      if (!normalizedAttribute.equals(key)) {
        builder.put(key, entry.getValue());
      } }
    
    for (String value : values) {
      builder.put(normalizedAttribute, normalizeParameterValue(normalizedAttribute, value));
    }
    MediaType mediaType = new MediaType(this.type, this.subtype, builder.build());
    
    if (!normalizedAttribute.equals("charset")) {
      mediaType.parsedCharset = this.parsedCharset;
    }
    
    return (MediaType)MoreObjects.firstNonNull(KNOWN_TYPES.get(mediaType), mediaType);
  }








  
  public MediaType withParameter(String attribute, String value) {
    return withParameters(attribute, (Iterable<String>)ImmutableSet.of(value));
  }









  
  public MediaType withCharset(Charset charset) {
    Preconditions.checkNotNull(charset);
    MediaType withCharset = withParameter("charset", charset.name());
    
    withCharset.parsedCharset = Optional.of(charset);
    return withCharset;
  }

  
  public boolean hasWildcard() {
    return ("*".equals(this.type) || "*".equals(this.subtype));
  }





























  
  public boolean is(MediaType mediaTypeRange) {
    return ((mediaTypeRange.type.equals("*") || mediaTypeRange.type.equals(this.type)) && (mediaTypeRange.subtype
      .equals("*") || mediaTypeRange.subtype.equals(this.subtype)) && this.parameters
      .entries().containsAll((Collection)mediaTypeRange.parameters.entries()));
  }






  
  public static MediaType create(String type, String subtype) {
    MediaType mediaType = create(type, subtype, (Multimap<String, String>)ImmutableListMultimap.of());
    mediaType.parsedCharset = Optional.absent();
    return mediaType;
  }

  
  private static MediaType create(String type, String subtype, Multimap<String, String> parameters) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(subtype);
    Preconditions.checkNotNull(parameters);
    String normalizedType = normalizeToken(type);
    String normalizedSubtype = normalizeToken(subtype);
    Preconditions.checkArgument((
        !"*".equals(normalizedType) || "*".equals(normalizedSubtype)), "A wildcard type cannot be used with a non-wildcard subtype");
    
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
    for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)parameters.entries()) {
      String attribute = normalizeToken(entry.getKey());
      builder.put(attribute, normalizeParameterValue(attribute, entry.getValue()));
    } 
    MediaType mediaType = new MediaType(normalizedType, normalizedSubtype, builder.build());
    
    return (MediaType)MoreObjects.firstNonNull(KNOWN_TYPES.get(mediaType), mediaType);
  }





  
  static MediaType createApplicationType(String subtype) {
    return create("application", subtype);
  }





  
  static MediaType createAudioType(String subtype) {
    return create("audio", subtype);
  }





  
  static MediaType createImageType(String subtype) {
    return create("image", subtype);
  }





  
  static MediaType createTextType(String subtype) {
    return create("text", subtype);
  }





  
  static MediaType createVideoType(String subtype) {
    return create("video", subtype);
  }
  
  private static String normalizeToken(String token) {
    Preconditions.checkArgument(TOKEN_MATCHER.matchesAllOf(token));
    return Ascii.toLowerCase(token);
  }
  
  private static String normalizeParameterValue(String attribute, String value) {
    return "charset".equals(attribute) ? Ascii.toLowerCase(value) : value;
  }





  
  public static MediaType parse(String input) {
    Preconditions.checkNotNull(input);
    Tokenizer tokenizer = new Tokenizer(input);
    try {
      String type = tokenizer.consumeToken(TOKEN_MATCHER);
      tokenizer.consumeCharacter('/');
      String subtype = tokenizer.consumeToken(TOKEN_MATCHER);
      ImmutableListMultimap.Builder<String, String> parameters = ImmutableListMultimap.builder();
      while (tokenizer.hasMore()) {
        String value; tokenizer.consumeTokenIfPresent(LINEAR_WHITE_SPACE);
        tokenizer.consumeCharacter(';');
        tokenizer.consumeTokenIfPresent(LINEAR_WHITE_SPACE);
        String attribute = tokenizer.consumeToken(TOKEN_MATCHER);
        tokenizer.consumeCharacter('=');
        
        if ('"' == tokenizer.previewChar()) {
          tokenizer.consumeCharacter('"');
          StringBuilder valueBuilder = new StringBuilder();
          while ('"' != tokenizer.previewChar()) {
            if ('\\' == tokenizer.previewChar()) {
              tokenizer.consumeCharacter('\\');
              valueBuilder.append(tokenizer.consumeCharacter(CharMatcher.ascii())); continue;
            } 
            valueBuilder.append(tokenizer.consumeToken(QUOTED_TEXT_MATCHER));
          } 
          
          value = valueBuilder.toString();
          tokenizer.consumeCharacter('"');
        } else {
          value = tokenizer.consumeToken(TOKEN_MATCHER);
        } 
        parameters.put(attribute, value);
      } 
      return create(type, subtype, (Multimap<String, String>)parameters.build());
    } catch (IllegalStateException e) {
      throw new IllegalArgumentException("Could not parse '" + input + "'", e);
    } 
  }
  
  private static final class Tokenizer {
    final String input;
    int position = 0;
    
    Tokenizer(String input) {
      this.input = input;
    }
    
    String consumeTokenIfPresent(CharMatcher matcher) {
      Preconditions.checkState(hasMore());
      int startPosition = this.position;
      this.position = matcher.negate().indexIn(this.input, startPosition);
      return hasMore() ? this.input.substring(startPosition, this.position) : this.input.substring(startPosition);
    }
    
    String consumeToken(CharMatcher matcher) {
      int startPosition = this.position;
      String token = consumeTokenIfPresent(matcher);
      Preconditions.checkState((this.position != startPosition));
      return token;
    }
    
    char consumeCharacter(CharMatcher matcher) {
      Preconditions.checkState(hasMore());
      char c = previewChar();
      Preconditions.checkState(matcher.matches(c));
      this.position++;
      return c;
    }
    
    char consumeCharacter(char c) {
      Preconditions.checkState(hasMore());
      Preconditions.checkState((previewChar() == c));
      this.position++;
      return c;
    }
    
    char previewChar() {
      Preconditions.checkState(hasMore());
      return this.input.charAt(this.position);
    }
    
    boolean hasMore() {
      return (this.position >= 0 && this.position < this.input.length());
    }
  }

  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj instanceof MediaType) {
      MediaType that = (MediaType)obj;
      return (this.type.equals(that.type) && this.subtype
        .equals(that.subtype) && 
        
        parametersAsMap().equals(that.parametersAsMap()));
    } 
    return false;
  }



  
  public int hashCode() {
    int h = this.hashCode;
    if (h == 0) {
      h = Objects.hashCode(new Object[] { this.type, this.subtype, parametersAsMap() });
      this.hashCode = h;
    } 
    return h;
  }
  
  private static final Joiner.MapJoiner PARAMETER_JOINER = Joiner.on("; ").withKeyValueSeparator("=");






  
  public String toString() {
    String result = this.toString;
    if (result == null) {
      result = computeToString();
      this.toString = result;
    } 
    return result;
  }
  
  private String computeToString() {
    StringBuilder builder = (new StringBuilder()).append(this.type).append('/').append(this.subtype);
    if (!this.parameters.isEmpty()) {
      builder.append("; ");
      
      ListMultimap listMultimap = Multimaps.transformValues((ListMultimap)this.parameters, new Function<String, String>()
          {
            
            public String apply(String value)
            {
              return MediaType.TOKEN_MATCHER.matchesAllOf(value) ? value : MediaType.escapeAndQuote(value);
            }
          });
      PARAMETER_JOINER.appendTo(builder, listMultimap.entries());
    } 
    return builder.toString();
  }
  
  private static String escapeAndQuote(String value) {
    StringBuilder escaped = (new StringBuilder(value.length() + 16)).append('"');
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (ch == '\r' || ch == '\\' || ch == '"') {
        escaped.append('\\');
      }
      escaped.append(ch);
    } 
    return escaped.append('"').toString();
  }
}
