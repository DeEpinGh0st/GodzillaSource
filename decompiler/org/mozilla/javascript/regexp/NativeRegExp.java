package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
























public class NativeRegExp
  extends IdScriptableObject
  implements Function
{
  static final long serialVersionUID = 4965263491464903264L;
  private static final Object REGEXP_TAG = new Object();
  public static final int JSREG_GLOB = 1;
  public static final int JSREG_FOLD = 2;
  public static final int JSREG_MULTILINE = 4;
  public static final int TEST = 0;
  public static final int MATCH = 1;
  public static final int PREFIX = 2;
  private static final boolean debug = false;
  private static final byte REOP_SIMPLE_START = 1;
  private static final byte REOP_EMPTY = 1;
  private static final byte REOP_BOL = 2;
  private static final byte REOP_EOL = 3;
  private static final byte REOP_WBDRY = 4;
  private static final byte REOP_WNONBDRY = 5;
  private static final byte REOP_DOT = 6;
  private static final byte REOP_DIGIT = 7;
  private static final byte REOP_NONDIGIT = 8;
  private static final byte REOP_ALNUM = 9;
  private static final byte REOP_NONALNUM = 10;
  private static final byte REOP_SPACE = 11;
  private static final byte REOP_NONSPACE = 12;
  private static final byte REOP_BACKREF = 13;
  private static final byte REOP_FLAT = 14;
  private static final byte REOP_FLAT1 = 15;
  private static final byte REOP_FLATi = 16;
  private static final byte REOP_FLAT1i = 17;
  private static final byte REOP_UCFLAT1 = 18;
  private static final byte REOP_UCFLAT1i = 19;
  private static final byte REOP_CLASS = 22;
  private static final byte REOP_NCLASS = 23;
  private static final byte REOP_SIMPLE_END = 23;
  private static final byte REOP_QUANT = 25;
  private static final byte REOP_STAR = 26;
  private static final byte REOP_PLUS = 27;
  private static final byte REOP_OPT = 28;
  private static final byte REOP_LPAREN = 29;
  private static final byte REOP_RPAREN = 30;
  private static final byte REOP_ALT = 31;
  private static final byte REOP_JUMP = 32;
  private static final byte REOP_ASSERT = 41;
  private static final byte REOP_ASSERT_NOT = 42;
  private static final byte REOP_ASSERTTEST = 43;
  private static final byte REOP_ASSERTNOTTEST = 44;
  private static final byte REOP_MINIMALSTAR = 45;
  private static final byte REOP_MINIMALPLUS = 46;
  private static final byte REOP_MINIMALOPT = 47;
  private static final byte REOP_MINIMALQUANT = 48;
  private static final byte REOP_ENDCHILD = 49;
  private static final byte REOP_REPEAT = 51;
  private static final byte REOP_MINIMALREPEAT = 52;
  private static final byte REOP_ALTPREREQ = 53;
  private static final byte REOP_ALTPREREQi = 54;
  private static final byte REOP_ALTPREREQ2 = 55;
  private static final byte REOP_END = 57;
  private static final int ANCHOR_BOL = -2;
  private static final int INDEX_LEN = 2;
  private static final int Id_lastIndex = 1;
  private static final int Id_source = 2;
  private static final int Id_global = 3;
  private static final int Id_ignoreCase = 4;
  private static final int Id_multiline = 5;
  private static final int MAX_INSTANCE_ID = 5;
  private static final int Id_compile = 1;
  private static final int Id_toString = 2;
  private static final int Id_toSource = 3;
  private static final int Id_exec = 4;
  private static final int Id_test = 5;
  private static final int Id_prefix = 6;
  private static final int MAX_PROTOTYPE_ID = 6;
  private RECompiled re;
  Object lastIndex;
  private int lastIndexAttr;
  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeRegExp proto = new NativeRegExp();
    proto.re = compileRE(cx, "", (String)null, false);
    proto.activatePrototypeMap(6);
    proto.setParentScope(scope);
    proto.setPrototype(getObjectPrototype(scope));
    
    NativeRegExpCtor ctor = new NativeRegExpCtor();

    
    proto.defineProperty("constructor", ctor, 2);
    
    ScriptRuntime.setFunctionProtoAndParent(ctor, scope);
    
    ctor.setImmunePrototypeProperty(proto);
    
    if (sealed) {
      proto.sealObject();
      ctor.sealObject();
    } 
    
    defineProperty(scope, "RegExp", ctor, 2);
  }













































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































  
  NativeRegExp(Scriptable scope, RECompiled regexpCompiled)
  {
    this.lastIndex = Double.valueOf(0.0D);
    this.lastIndexAttr = 6; this.re = regexpCompiled; this.lastIndex = Double.valueOf(0.0D); ScriptRuntime.setBuiltinProtoAndParent((ScriptableObject)this, scope, TopLevel.Builtins.RegExp); } NativeRegExp() { this.lastIndex = Double.valueOf(0.0D); this.lastIndexAttr = 6; }

  
  public String getClassName() {
    return "RegExp";
  }
  
  public String getTypeOf() {
    return "object";
  }
  
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    return execSub(cx, scope, args, 1);
  }
  
  public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
    return (Scriptable)execSub(cx, scope, args, 1);
  }
  
  Scriptable compile(Context cx, Scriptable scope, Object[] args) {
    if (args.length > 0 && args[0] instanceof NativeRegExp) {
      if (args.length > 1 && args[1] != Undefined.instance)
        throw ScriptRuntime.typeError0("msg.bad.regexp.compile"); 
      NativeRegExp thatObj = (NativeRegExp)args[0];
      this.re = thatObj.re;
      this.lastIndex = thatObj.lastIndex;
      return (Scriptable)this;
    } 
    String s = (args.length == 0 || args[0] instanceof Undefined) ? "" : escapeRegExp(args[0]);
    String global = (args.length > 1 && args[1] != Undefined.instance) ? ScriptRuntime.toString(args[1]) : null;
    this.re = compileRE(cx, s, global, false);
    this.lastIndex = Double.valueOf(0.0D);
    return (Scriptable)this;
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('/');
    if (this.re.source.length != 0) {
      buf.append(this.re.source);
    } else {
      buf.append("(?:)");
    } 
    buf.append('/');
    if ((this.re.flags & 0x1) != 0)
      buf.append('g'); 
    if ((this.re.flags & 0x2) != 0)
      buf.append('i'); 
    if ((this.re.flags & 0x4) != 0)
      buf.append('m'); 
    return buf.toString();
  }
  
  private static RegExpImpl getImpl(Context cx) {
    return (RegExpImpl)ScriptRuntime.getRegExpProxy(cx);
  }
  
  private static String escapeRegExp(Object src) {
    String s = ScriptRuntime.toString(src);
    StringBuilder sb = null;
    int start = 0;
    int slash = s.indexOf('/');
    while (slash > -1) {
      if (slash == start || s.charAt(slash - 1) != '\\') {
        if (sb == null)
          sb = new StringBuilder(); 
        sb.append(s, start, slash);
        sb.append("\\/");
        start = slash + 1;
      } 
      slash = s.indexOf('/', slash + 1);
    } 
    if (sb != null) {
      sb.append(s, start, s.length());
      s = sb.toString();
    } 
    return s;
  }
  
  private Object execSub(Context cx, Scriptable scopeObj, Object[] args, int matchType) {
    String str;
    Object rval;
    RegExpImpl reImpl = getImpl(cx);
    if (args.length == 0) {
      str = reImpl.input;
      if (str == null)
        str = ScriptRuntime.toString(Undefined.instance); 
    } else {
      str = ScriptRuntime.toString(args[0]);
    } 
    double d = 0.0D;
    if ((this.re.flags & 0x1) != 0)
      d = ScriptRuntime.toInteger(this.lastIndex); 
    if (d < 0.0D || str.length() < d) {
      this.lastIndex = Double.valueOf(0.0D);
      rval = null;
    } else {
      int[] indexp = { (int)d };
      rval = executeRegExp(cx, scopeObj, reImpl, str, indexp, matchType);
      if ((this.re.flags & 0x1) != 0)
        this.lastIndex = Double.valueOf((rval == null || rval == Undefined.instance) ? 0.0D : indexp[0]); 
    } 
    return rval;
  }
  
  static RECompiled compileRE(Context cx, String str, String global, boolean flat) {
    int k;
    RENode n;
    RECompiled regexp = new RECompiled(str);
    int length = str.length();
    int flags = 0;
    if (global != null)
      for (int i = 0; i < global.length(); i++) {
        char c = global.charAt(i);
        int f = 0;
        if (c == 'g') {
          f = 1;
        } else if (c == 'i') {
          f = 2;
        } else if (c == 'm') {
          f = 4;
        } else {
          reportError("msg.invalid.re.flag", String.valueOf(c));
        } 
        if ((flags & f) != 0)
          reportError("msg.invalid.re.flag", String.valueOf(c)); 
        flags |= f;
      }  
    regexp.flags = flags;
    CompilerState state = new CompilerState(cx, regexp.source, length, flags);
    if (flat && length > 0) {
      state.result = new RENode((byte)14);
      state.result.chr = state.cpbegin[0];
      state.result.length = length;
      state.result.flatIndex = 0;
      state.progLength += 5;
    } else {
      if (!parseDisjunction(state))
        return null; 
      if (state.maxBackReference > state.parenCount) {
        state = new CompilerState(cx, regexp.source, length, flags);
        state.backReferenceLimit = state.parenCount;
        if (!parseDisjunction(state))
          return null; 
      } 
    } 
    regexp.program = new byte[state.progLength + 1];
    if (state.classCount != 0) {
      regexp.classList = new RECharSet[state.classCount];
      regexp.classCount = state.classCount;
    } 
    int endPC = emitREBytecode(state, regexp, 0, state.result);
    regexp.program[endPC++] = 57;
    regexp.parenCount = state.parenCount;
    switch (regexp.program[0]) {
      case 18:
      case 19:
        regexp.anchorCh = (char)getIndex(regexp.program, 1);
        break;
      case 15:
      case 17:
        regexp.anchorCh = (char)(regexp.program[1] & 0xFF);
        break;
      case 14:
      case 16:
        k = getIndex(regexp.program, 1);
        regexp.anchorCh = regexp.source[k];
        break;
      case 2:
        regexp.anchorCh = -2;
        break;
      case 31:
        n = state.result;
        if (n.kid.op == 2 && n.kid2.op == 2)
          regexp.anchorCh = -2; 
        break;
    } 
    return regexp;
  }
  
  static boolean isDigit(char c) {
    return ('0' <= c && c <= '9');
  }
  
  private static boolean isWord(char c) {
    return (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || isDigit(c) || c == '_');
  }
  
  private static boolean isControlLetter(char c) {
    return (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z'));
  }
  
  private static boolean isLineTerm(char c) {
    return ScriptRuntime.isJSLineTerminator(c);
  }
  
  private static boolean isREWhiteSpace(int c) {
    return ScriptRuntime.isJSWhitespaceOrLineTerminator(c);
  }
  
  private static char upcase(char ch) {
    if (ch < '') {
      if ('a' <= ch && ch <= 'z')
        return (char)(ch + -32); 
      return ch;
    } 
    char cu = Character.toUpperCase(ch);
    return (cu < '') ? ch : cu;
  }
  
  private static char downcase(char ch) {
    if (ch < '') {
      if ('A' <= ch && ch <= 'Z')
        return (char)(ch + 32); 
      return ch;
    } 
    char cl = Character.toLowerCase(ch);
    return (cl < '') ? ch : cl;
  }
  
  private static int toASCIIHexDigit(int c) {
    if (c < 48)
      return -1; 
    if (c <= 57)
      return c - 48; 
    c |= 0x20;
    if (97 <= c && c <= 102)
      return c - 97 + 10; 
    return -1;
  }
  
  private static boolean parseDisjunction(CompilerState state) {
    if (!parseAlternative(state))
      return false; 
    char[] source = state.cpbegin;
    int index = state.cp;
    if (index != source.length && source[index] == '|') {
      state.cp++;
      RENode result = new RENode((byte)31);
      result.kid = state.result;
      if (!parseDisjunction(state))
        return false; 
      result.kid2 = state.result;
      state.result = result;
      if (result.kid.op == 14 && result.kid2.op == 14) {
        result.op = ((state.flags & 0x2) == 0) ? 53 : 54;
        result.chr = result.kid.chr;
        result.index = result.kid2.chr;
        state.progLength += 13;
      } else if (result.kid.op == 22 && result.kid.index < 256 && result.kid2.op == 14 && (state.flags & 0x2) == 0) {
        result.op = 55;
        result.chr = result.kid2.chr;
        result.index = result.kid.index;
        state.progLength += 13;
      } else if (result.kid.op == 14 && result.kid2.op == 22 && result.kid2.index < 256 && (state.flags & 0x2) == 0) {
        result.op = 55;
        result.chr = result.kid.chr;
        result.index = result.kid2.index;
        state.progLength += 13;
      } else {
        state.progLength += 9;
      } 
    } 
    return true;
  }
  
  private static boolean parseAlternative(CompilerState state) {
    RENode headTerm = null;
    RENode tailTerm = null;
    char[] source = state.cpbegin;
    while (true) {
      if (state.cp == state.cpend || source[state.cp] == '|' || (state.parenNesting != 0 && source[state.cp] == ')')) {
        if (headTerm == null) {
          state.result = new RENode((byte)1);
        } else {
          state.result = headTerm;
        } 
        return true;
      } 
      if (!parseTerm(state))
        return false; 
      if (headTerm == null) {
        headTerm = state.result;
        tailTerm = headTerm;
      } else {
        tailTerm.next = state.result;
      } 
      for (; tailTerm.next != null; tailTerm = tailTerm.next);
    } 
  }
  
  private static boolean calculateBitmapSize(CompilerState state, RENode target, char[] src, int index, int end) {
    char rangeStart = Character.MIN_VALUE;
    int max = 0;
    boolean inRange = false;
    target.bmsize = 0;
    target.sense = true;
    if (index == end)
      return true; 
    if (src[index] == '^') {
      index++;
      target.sense = false;
    } 
    while (index != end) {
      char c;
      int n, i, localMax = 0;
      int nDigits = 2;
      switch (src[index]) {
        case '\\':
          index++;
          c = src[index++];
          switch (c) {
            case 'b':
              localMax = 8;
              break;
            case 'f':
              localMax = 12;
              break;
            case 'n':
              localMax = 10;
              break;
            case 'r':
              localMax = 13;
              break;
            case 't':
              localMax = 9;
              break;
            case 'v':
              localMax = 11;
              break;
            case 'c':
              if (index < end && isControlLetter(src[index])) {
                localMax = (char)(src[index++] & 0x1F);
              } else {
                index--;
              } 
              localMax = 92;
              break;
            case 'u':
              nDigits += 2;
            case 'x':
              n = 0;
              for (i = 0; i < nDigits && index < end; i++) {
                c = src[index++];
                n = Kit.xDigitToInt(c, n);
                if (n < 0) {
                  index -= i + 1;
                  n = 92;
                  break;
                } 
              } 
              localMax = n;
              break;
            case 'd':
              if (inRange) {
                reportError("msg.bad.range", "");
                return false;
              } 
              localMax = 57;
              break;
            case 'D':
            case 'S':
            case 'W':
            case 's':
            case 'w':
              if (inRange) {
                reportError("msg.bad.range", "");
                return false;
              } 
              target.bmsize = 65536;
              return true;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
              n = c - 48;
              c = src[index];
              if ('0' <= c && c <= '7') {
                index++;
                n = 8 * n + c - 48;
                c = src[index];
                if ('0' <= c && c <= '7') {
                  index++;
                  i = 8 * n + c - 48;
                  if (i <= 255) {
                    n = i;
                  } else {
                    index--;
                  } 
                } 
              } 
              localMax = n;
              break;
          } 
          localMax = c;
          break;
        default:
          localMax = src[index++];
          break;
      } 
      if (inRange) {
        if (rangeStart > localMax) {
          reportError("msg.bad.range", "");
          return false;
        } 
        inRange = false;
      } else if (index < end - 1 && src[index] == '-') {
        index++;
        inRange = true;
        rangeStart = (char)localMax;
        continue;
      } 
      if ((state.flags & 0x2) != 0) {
        char cu = upcase((char)localMax);
        char cd = downcase((char)localMax);
        localMax = (cu >= cd) ? cu : cd;
      } 
      if (localMax > max)
        max = localMax; 
    } 
    target.bmsize = max + 1;
    return true;
  }
  
  private static void doFlat(CompilerState state, char c) {
    state.result = new RENode((byte)14);
    state.result.chr = c;
    state.result.length = 1;
    state.result.flatIndex = -1;
    state.progLength += 3;
  }
  
  private static int getDecimalValue(char c, CompilerState state, int maxValue, String overflowMessageId) {
    boolean overflow = false;
    int start = state.cp;
    char[] src = state.cpbegin;
    int value = c - 48;
    for (; state.cp != state.cpend; state.cp++) {
      c = src[state.cp];
      if (!isDigit(c))
        break; 
      if (!overflow) {
        int v = value * 10 + c - 48;
        if (v < maxValue) {
          value = v;
        } else {
          overflow = true;
          value = maxValue;
        } 
      } 
    } 
    if (overflow)
      reportError(overflowMessageId, String.valueOf(src, start, state.cp - start)); 
    return value;
  }
  
  private static boolean parseTerm(CompilerState state) {
    int i;
    RENode result;
    int min, max, leftCurl;
    char[] src = state.cpbegin;
    char c = src[state.cp++];
    int nDigits = 2;
    int parenBaseCount = state.parenCount;
    switch (c) {
      case '^':
        state.result = new RENode((byte)2);
        state.progLength++;
        return true;
      case '$':
        state.result = new RENode((byte)3);
        state.progLength++;
        return true;
      case '\\':
        if (state.cp < state.cpend) {
          int num, termStart, n, j;
          c = src[state.cp++];
          switch (c) {
            case 'b':
              state.result = new RENode((byte)4);
              state.progLength++;
              return true;
            case 'B':
              state.result = new RENode((byte)5);
              state.progLength++;
              return true;
            case '0':
              reportWarning(state.cx, "msg.bad.backref", "");
              num = 0;
              while (num < 32 && state.cp < state.cpend) {
                c = src[state.cp];
                if (c >= '0' && c <= '7') {
                  state.cp++;
                  num = 8 * num + c - 48;
                } 
              } 
              c = (char)num;
              doFlat(state, c);
              break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
              termStart = state.cp - 1;
              num = getDecimalValue(c, state, 65535, "msg.overlarge.backref");
              if (num > state.backReferenceLimit)
                reportWarning(state.cx, "msg.bad.backref", ""); 
              if (num > state.backReferenceLimit) {
                state.cp = termStart;
                if (c >= '8') {
                  c = '\\';
                  doFlat(state, c);
                  break;
                } 
                state.cp++;
                num = c - 48;
                while (num < 32 && state.cp < state.cpend) {
                  c = src[state.cp];
                  if (c >= '0' && c <= '7') {
                    state.cp++;
                    num = 8 * num + c - 48;
                  } 
                } 
                c = (char)num;
                doFlat(state, c);
                break;
              } 
              state.result = new RENode((byte)13);
              state.result.parenIndex = num - 1;
              state.progLength += 3;
              if (state.maxBackReference < num)
                state.maxBackReference = num; 
              break;
            case 'f':
              c = '\f';
              doFlat(state, c);
              break;
            case 'n':
              c = '\n';
              doFlat(state, c);
              break;
            case 'r':
              c = '\r';
              doFlat(state, c);
              break;
            case 't':
              c = '\t';
              doFlat(state, c);
              break;
            case 'v':
              c = '\013';
              doFlat(state, c);
              break;
            case 'c':
              if (state.cp < state.cpend && isControlLetter(src[state.cp])) {
                c = (char)(src[state.cp++] & 0x1F);
              } else {
                state.cp--;
                c = '\\';
              } 
              doFlat(state, c);
              break;
            case 'u':
              nDigits += 2;
            case 'x':
              n = 0;
              j = 0;
              for (; j < nDigits && state.cp < state.cpend; j++) {
                c = src[state.cp++];
                n = Kit.xDigitToInt(c, n);
                if (n < 0) {
                  state.cp -= j + 2;
                  n = src[state.cp++];
                  break;
                } 
              } 
              c = (char)n;
              doFlat(state, c);
              break;
            case 'd':
              state.result = new RENode((byte)7);
              state.progLength++;
              break;
            case 'D':
              state.result = new RENode((byte)8);
              state.progLength++;
              break;
            case 's':
              state.result = new RENode((byte)11);
              state.progLength++;
              break;
            case 'S':
              state.result = new RENode((byte)12);
              state.progLength++;
              break;
            case 'w':
              state.result = new RENode((byte)9);
              state.progLength++;
              break;
            case 'W':
              state.result = new RENode((byte)10);
              state.progLength++;
              break;
          } 
          state.result = new RENode((byte)14);
          state.result.chr = c;
          state.result.length = 1;
          state.result.flatIndex = state.cp - 1;
          state.progLength += 3;
          break;
        } 
        reportError("msg.trail.backslash", "");
        return false;
      case '(':
        result = null;
        i = state.cp;
        if (state.cp + 1 < state.cpend && src[state.cp] == '?' && ((c = src[state.cp + 1]) == '=' || c == '!' || c == ':')) {
          state.cp += 2;
          if (c == '=') {
            result = new RENode((byte)41);
            state.progLength += 4;
          } else if (c == '!') {
            result = new RENode((byte)42);
            state.progLength += 4;
          } 
        } else {
          result = new RENode((byte)29);
          state.progLength += 6;
          result.parenIndex = state.parenCount++;
        } 
        state.parenNesting++;
        if (!parseDisjunction(state))
          return false; 
        if (state.cp == state.cpend || src[state.cp] != ')') {
          reportError("msg.unterm.paren", "");
          return false;
        } 
        state.cp++;
        state.parenNesting--;
        if (result != null) {
          result.kid = state.result;
          state.result = result;
        } 
        break;
      case ')':
        reportError("msg.re.unmatched.right.paren", "");
        return false;
      case '[':
        state.result = new RENode((byte)22);
        i = state.cp;
        state.result.startIndex = i;
        while (true) {
          if (state.cp == state.cpend) {
            reportError("msg.unterm.class", "");
            return false;
          } 
          if (src[state.cp] == '\\') {
            state.cp++;
          } else if (src[state.cp] == ']') {
            state.result.kidlen = state.cp - i;
            break;
          } 
          state.cp++;
        } 
        state.result.index = state.classCount++;
        if (!calculateBitmapSize(state, state.result, src, i, state.cp++))
          return false; 
        state.progLength += 3;
        break;
      case '.':
        state.result = new RENode((byte)6);
        state.progLength++;
        break;
      case '*':
      case '+':
      case '?':
        reportError("msg.bad.quant", String.valueOf(src[state.cp - 1]));
        return false;
      default:
        state.result = new RENode((byte)14);
        state.result.chr = c;
        state.result.length = 1;
        state.result.flatIndex = state.cp - 1;
        state.progLength += 3;
        break;
    } 
    RENode term = state.result;
    if (state.cp == state.cpend)
      return true; 
    boolean hasQ = false;
    switch (src[state.cp]) {
      case '+':
        state.result = new RENode((byte)25);
        state.result.min = 1;
        state.result.max = -1;
        state.progLength += 8;
        hasQ = true;
        break;
      case '*':
        state.result = new RENode((byte)25);
        state.result.min = 0;
        state.result.max = -1;
        state.progLength += 8;
        hasQ = true;
        break;
      case '?':
        state.result = new RENode((byte)25);
        state.result.min = 0;
        state.result.max = 1;
        state.progLength += 8;
        hasQ = true;
        break;
      case '{':
        min = 0;
        max = -1;
        leftCurl = state.cp;
        if (++state.cp < src.length && isDigit(c = src[state.cp])) {
          state.cp++;
          min = getDecimalValue(c, state, 65535, "msg.overlarge.min");
          c = src[state.cp];
          if (c == ',') {
            c = src[++state.cp];
            if (isDigit(c)) {
              state.cp++;
              max = getDecimalValue(c, state, 65535, "msg.overlarge.max");
              c = src[state.cp];
              if (min > max) {
                reportError("msg.max.lt.min", String.valueOf(src[state.cp]));
                return false;
              } 
            } 
          } else {
            max = min;
          } 
          if (c == '}') {
            state.result = new RENode((byte)25);
            state.result.min = min;
            state.result.max = max;
            state.progLength += 12;
            hasQ = true;
          } 
        } 
        if (!hasQ)
          state.cp = leftCurl; 
        break;
    } 
    if (!hasQ)
      return true; 
    state.cp++;
    state.result.kid = term;
    state.result.parenIndex = parenBaseCount;
    state.result.parenCount = state.parenCount - parenBaseCount;
    if (state.cp < state.cpend && src[state.cp] == '?') {
      state.cp++;
      state.result.greedy = false;
    } else {
      state.result.greedy = true;
    } 
    return true;
  }
  
  private static void resolveForwardJump(byte[] array, int from, int pc) {
    if (from > pc)
      throw Kit.codeBug(); 
    addIndex(array, from, pc - from);
  }
  
  private static int getOffset(byte[] array, int pc) {
    return getIndex(array, pc);
  }
  
  private static int addIndex(byte[] array, int pc, int index) {
    if (index < 0)
      throw Kit.codeBug(); 
    if (index > 65535)
      throw Context.reportRuntimeError("Too complex regexp"); 
    array[pc] = (byte)(index >> 8);
    array[pc + 1] = (byte)index;
    return pc + 2;
  }
  
  private static int getIndex(byte[] array, int pc) {
    return (array[pc] & 0xFF) << 8 | array[pc + 1] & 0xFF;
  }
  
  private static int emitREBytecode(CompilerState state, RECompiled re, int pc, RENode t) {
    byte[] program = re.program;
    while (t != null) {
      RENode nextAlt;
      int nextAltFixup, nextTermFixup;
      boolean ignoreCase;
      program[pc++] = t.op;
      switch (t.op) {
        case 1:
          pc--;
          break;
        case 53:
        case 54:
        case 55:
          ignoreCase = (t.op == 54);
          addIndex(program, pc, ignoreCase ? upcase(t.chr) : t.chr);
          pc += 2;
          addIndex(program, pc, ignoreCase ? upcase((char)t.index) : t.index);
          pc += 2;
        case 31:
          nextAlt = t.kid2;
          nextAltFixup = pc;
          pc += 2;
          pc = emitREBytecode(state, re, pc, t.kid);
          program[pc++] = 32;
          nextTermFixup = pc;
          pc += 2;
          resolveForwardJump(program, nextAltFixup, pc);
          pc = emitREBytecode(state, re, pc, nextAlt);
          program[pc++] = 32;
          nextAltFixup = pc;
          pc += 2;
          resolveForwardJump(program, nextTermFixup, pc);
          resolveForwardJump(program, nextAltFixup, pc);
          break;
        case 14:
          if (t.flatIndex != -1)
            while (t.next != null && t.next.op == 14 && t.flatIndex + t.length == t.next.flatIndex) {
              t.length += t.next.length;
              t.next = t.next.next;
            }  
          if (t.flatIndex != -1 && t.length > 1) {
            if ((state.flags & 0x2) != 0) {
              program[pc - 1] = 16;
            } else {
              program[pc - 1] = 14;
            } 
            pc = addIndex(program, pc, t.flatIndex);
            pc = addIndex(program, pc, t.length);
            break;
          } 
          if (t.chr < 'Ā') {
            if ((state.flags & 0x2) != 0) {
              program[pc - 1] = 17;
            } else {
              program[pc - 1] = 15;
            } 
            program[pc++] = (byte)t.chr;
            break;
          } 
          if ((state.flags & 0x2) != 0) {
            program[pc - 1] = 19;
          } else {
            program[pc - 1] = 18;
          } 
          pc = addIndex(program, pc, t.chr);
          break;
        case 29:
          pc = addIndex(program, pc, t.parenIndex);
          pc = emitREBytecode(state, re, pc, t.kid);
          program[pc++] = 30;
          pc = addIndex(program, pc, t.parenIndex);
          break;
        case 13:
          pc = addIndex(program, pc, t.parenIndex);
          break;
        case 41:
          nextTermFixup = pc;
          pc += 2;
          pc = emitREBytecode(state, re, pc, t.kid);
          program[pc++] = 43;
          resolveForwardJump(program, nextTermFixup, pc);
          break;
        case 42:
          nextTermFixup = pc;
          pc += 2;
          pc = emitREBytecode(state, re, pc, t.kid);
          program[pc++] = 44;
          resolveForwardJump(program, nextTermFixup, pc);
          break;
        case 25:
          if (t.min == 0 && t.max == -1) {
            program[pc - 1] = t.greedy ? 26 : 45;
          } else if (t.min == 0 && t.max == 1) {
            program[pc - 1] = t.greedy ? 28 : 47;
          } else if (t.min == 1 && t.max == -1) {
            program[pc - 1] = t.greedy ? 27 : 46;
          } else {
            if (!t.greedy)
              program[pc - 1] = 48; 
            pc = addIndex(program, pc, t.min);
            pc = addIndex(program, pc, t.max + 1);
          } 
          pc = addIndex(program, pc, t.parenCount);
          pc = addIndex(program, pc, t.parenIndex);
          nextTermFixup = pc;
          pc += 2;
          pc = emitREBytecode(state, re, pc, t.kid);
          program[pc++] = 49;
          resolveForwardJump(program, nextTermFixup, pc);
          break;
        case 22:
          if (!t.sense)
            program[pc - 1] = 23; 
          pc = addIndex(program, pc, t.index);
          re.classList[t.index] = new RECharSet(t.bmsize, t.startIndex, t.kidlen, t.sense);
          break;
      } 
      t = t.next;
    } 
    return pc;
  }
  
  private static void pushProgState(REGlobalData gData, int min, int max, int cp, REBackTrackData backTrackLastToSave, int continuationOp, int continuationPc) {
    gData.stateStackTop = new REProgState(gData.stateStackTop, min, max, cp, backTrackLastToSave, continuationOp, continuationPc);
  }
  
  private static REProgState popProgState(REGlobalData gData) {
    REProgState state = gData.stateStackTop;
    gData.stateStackTop = state.previous;
    return state;
  }
  
  private static void pushBackTrackState(REGlobalData gData, byte op, int pc) {
    REProgState state = gData.stateStackTop;
    gData.backTrackStackTop = new REBackTrackData(gData, op, pc, gData.cp, state.continuationOp, state.continuationPc);
  }
  
  private static void pushBackTrackState(REGlobalData gData, byte op, int pc, int cp, int continuationOp, int continuationPc) {
    gData.backTrackStackTop = new REBackTrackData(gData, op, pc, cp, continuationOp, continuationPc);
  }
  
  private static boolean flatNMatcher(REGlobalData gData, int matchChars, int length, String input, int end) {
    if (gData.cp + length > end)
      return false; 
    for (int i = 0; i < length; i++) {
      if (gData.regexp.source[matchChars + i] != input.charAt(gData.cp + i))
        return false; 
    } 
    gData.cp += length;
    return true;
  }
  
  private static boolean flatNIMatcher(REGlobalData gData, int matchChars, int length, String input, int end) {
    if (gData.cp + length > end)
      return false; 
    char[] source = gData.regexp.source;
    for (int i = 0; i < length; i++) {
      char c1 = source[matchChars + i];
      char c2 = input.charAt(gData.cp + i);
      if (c1 != c2 && upcase(c1) != upcase(c2))
        return false; 
    } 
    gData.cp += length;
    return true;
  }
  
  private static boolean backrefMatcher(REGlobalData gData, int parenIndex, String input, int end) {
    if (gData.parens == null || parenIndex >= gData.parens.length)
      return false; 
    int parenContent = gData.parensIndex(parenIndex);
    if (parenContent == -1)
      return true; 
    int len = gData.parensLength(parenIndex);
    if (gData.cp + len > end)
      return false; 
    if ((gData.regexp.flags & 0x2) != 0) {
      for (int i = 0; i < len; i++) {
        char c1 = input.charAt(parenContent + i);
        char c2 = input.charAt(gData.cp + i);
        if (c1 != c2 && upcase(c1) != upcase(c2))
          return false; 
      } 
    } else if (!input.regionMatches(parenContent, input, gData.cp, len)) {
      return false;
    } 
    gData.cp += len;
    return true;
  }
  
  private static void addCharacterToCharSet(RECharSet cs, char c) {
    int byteIndex = c / 8;
    if (c >= cs.length)
      throw ScriptRuntime.constructError("SyntaxError", "invalid range in character class"); 
    cs.bits[byteIndex] = (byte)(cs.bits[byteIndex] | 1 << (c & 0x7));
  }
  
  private static void addCharacterRangeToCharSet(RECharSet cs, char c1, char c2) {
    int byteIndex1 = c1 / 8;
    int byteIndex2 = c2 / 8;
    if (c2 >= cs.length || c1 > c2)
      throw ScriptRuntime.constructError("SyntaxError", "invalid range in character class"); 
    c1 = (char)(c1 & 0x7);
    c2 = (char)(c2 & 0x7);
    if (byteIndex1 == byteIndex2) {
      cs.bits[byteIndex1] = (byte)(cs.bits[byteIndex1] | 255 >> 7 - c2 - c1 << c1);
    } else {
      cs.bits[byteIndex1] = (byte)(cs.bits[byteIndex1] | 255 << c1);
      for (int i = byteIndex1 + 1; i < byteIndex2; i++)
        cs.bits[i] = -1; 
      cs.bits[byteIndex2] = (byte)(cs.bits[byteIndex2] | 255 >> 7 - c2);
    } 
  }
  
  private static void processCharSet(REGlobalData gData, RECharSet charSet) {
    synchronized (charSet) {
      if (!charSet.converted) {
        processCharSetImpl(gData, charSet);
        charSet.converted = true;
      } 
    } 
  }
  
  private static void processCharSetImpl(REGlobalData gData, RECharSet charSet) {
    int src = charSet.startIndex;
    int end = src + charSet.strlength;
    char rangeStart = Character.MIN_VALUE;
    boolean inRange = false;
    int byteLength = (charSet.length + 7) / 8;
    charSet.bits = new byte[byteLength];
    if (src == end)
      return; 
    if (gData.regexp.source[src] == '^') {
      assert !charSet.sense;
      src++;
    } else {
      assert charSet.sense;
    } 
    while (src != end) {
      char thisCh, c;
      int n, i, nDigits = 2;
      switch (gData.regexp.source[src]) {
        case '\\':
          src++;
          c = gData.regexp.source[src++];
          switch (c) {
            case 'b':
              thisCh = '\b';
              break;
            case 'f':
              thisCh = '\f';
              break;
            case 'n':
              thisCh = '\n';
              break;
            case 'r':
              thisCh = '\r';
              break;
            case 't':
              thisCh = '\t';
              break;
            case 'v':
              thisCh = '\013';
              break;
            case 'c':
              if (src < end && isControlLetter(gData.regexp.source[src])) {
                thisCh = (char)(gData.regexp.source[src++] & 0x1F);
                break;
              } 
              src--;
              thisCh = '\\';
              break;
            case 'u':
              nDigits += 2;
            case 'x':
              n = 0;
              for (i = 0; i < nDigits && src < end; i++) {
                c = gData.regexp.source[src++];
                int digit = toASCIIHexDigit(c);
                if (digit < 0) {
                  src -= i + 1;
                  n = 92;
                  break;
                } 
                n = n << 4 | digit;
              } 
              thisCh = (char)n;
              break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
              n = c - 48;
              c = gData.regexp.source[src];
              if ('0' <= c && c <= '7') {
                src++;
                n = 8 * n + c - 48;
                c = gData.regexp.source[src];
                if ('0' <= c && c <= '7') {
                  src++;
                  i = 8 * n + c - 48;
                  if (i <= 255) {
                    n = i;
                  } else {
                    src--;
                  } 
                } 
              } 
              thisCh = (char)n;
              break;
            case 'd':
              addCharacterRangeToCharSet(charSet, '0', '9');
              continue;
            case 'D':
              addCharacterRangeToCharSet(charSet, false, '/');
              addCharacterRangeToCharSet(charSet, ':', (char)(charSet.length - 1));
              continue;
            case 's':
              for (i = charSet.length - 1; i >= 0; i--) {
                if (isREWhiteSpace(i))
                  addCharacterToCharSet(charSet, (char)i); 
              } 
              continue;
            case 'S':
              for (i = charSet.length - 1; i >= 0; i--) {
                if (!isREWhiteSpace(i))
                  addCharacterToCharSet(charSet, (char)i); 
              } 
              continue;
            case 'w':
              for (i = charSet.length - 1; i >= 0; i--) {
                if (isWord((char)i))
                  addCharacterToCharSet(charSet, (char)i); 
              } 
              continue;
            case 'W':
              for (i = charSet.length - 1; i >= 0; i--) {
                if (!isWord((char)i))
                  addCharacterToCharSet(charSet, (char)i); 
              } 
              continue;
          } 
          thisCh = c;
          break;
        default:
          thisCh = gData.regexp.source[src++];
          break;
      } 
      if (inRange) {
        if ((gData.regexp.flags & 0x2) != 0) {
          assert rangeStart <= thisCh;
          for (c = rangeStart; c <= thisCh; ) {
            addCharacterToCharSet(charSet, c);
            char uch = upcase(c);
            char dch = downcase(c);
            if (c != uch)
              addCharacterToCharSet(charSet, uch); 
            if (c != dch)
              addCharacterToCharSet(charSet, dch); 
            c = (char)(c + 1);
            if (c == '\000')
              break; 
          } 
        } else {
          addCharacterRangeToCharSet(charSet, rangeStart, thisCh);
        } 
        inRange = false;
        continue;
      } 
      if ((gData.regexp.flags & 0x2) != 0) {
        addCharacterToCharSet(charSet, upcase(thisCh));
        addCharacterToCharSet(charSet, downcase(thisCh));
      } else {
        addCharacterToCharSet(charSet, thisCh);
      } 
      if (src < end - 1 && gData.regexp.source[src] == '-') {
        src++;
        inRange = true;
        rangeStart = thisCh;
      } 
    } 
  }
  
  private static boolean classMatcher(REGlobalData gData, RECharSet charSet, char ch) {
    if (!charSet.converted)
      processCharSet(gData, charSet); 
    int byteIndex = ch >> 3;
    return ((charSet.length == 0 || ch >= charSet.length || (charSet.bits[byteIndex] & 1 << (ch & 0x7)) == 0)) ^ charSet.sense;
  }
  
  private static boolean reopIsSimple(int op) {
    return (op >= 1 && op <= 23);
  }
  
  private static int simpleMatch(REGlobalData gData, String input, int op, byte[] program, int pc, int end, boolean updatecp) {
    int i;
    boolean bool1;
    char matchCh;
    int parenIndex, offset, length, index;
    boolean result = false;
    int startcp = gData.cp;
    switch (op) {
      case 1:
        result = true;
        break;
      case 2:
        if (gData.cp != 0 && (!gData.multiline || !isLineTerm(input.charAt(gData.cp - 1))))
          break; 
        result = true;
        break;
      case 3:
        if (gData.cp != end && (!gData.multiline || !isLineTerm(input.charAt(gData.cp))))
          break; 
        result = true;
        break;
      case 4:
        i = ((gData.cp == 0 || !isWord(input.charAt(gData.cp - 1))) ? 1 : 0) ^ ((gData.cp >= end || !isWord(input.charAt(gData.cp))) ? 1 : 0);
        break;
      case 5:
        i = ((gData.cp == 0 || !isWord(input.charAt(gData.cp - 1))) ? 1 : 0) ^ ((gData.cp < end && isWord(input.charAt(gData.cp))) ? 1 : 0);
        break;
      case 6:
        if (gData.cp != end && !isLineTerm(input.charAt(gData.cp))) {
          i = 1;
          gData.cp++;
        } 
        break;
      case 7:
        if (gData.cp != end && isDigit(input.charAt(gData.cp))) {
          i = 1;
          gData.cp++;
        } 
        break;
      case 8:
        if (gData.cp != end && !isDigit(input.charAt(gData.cp))) {
          i = 1;
          gData.cp++;
        } 
        break;
      case 9:
        if (gData.cp != end && isWord(input.charAt(gData.cp))) {
          i = 1;
          gData.cp++;
        } 
        break;
      case 10:
        if (gData.cp != end && !isWord(input.charAt(gData.cp))) {
          i = 1;
          gData.cp++;
        } 
        break;
      case 11:
        if (gData.cp != end && isREWhiteSpace(input.charAt(gData.cp))) {
          i = 1;
          gData.cp++;
        } 
        break;
      case 12:
        if (gData.cp != end && !isREWhiteSpace(input.charAt(gData.cp))) {
          i = 1;
          gData.cp++;
        } 
        break;
      case 13:
        parenIndex = getIndex(program, pc);
        pc += 2;
        bool1 = backrefMatcher(gData, parenIndex, input, end);
        break;
      case 14:
        offset = getIndex(program, pc);
        pc += 2;
        length = getIndex(program, pc);
        pc += 2;
        bool1 = flatNMatcher(gData, offset, length, input, end);
        break;
      case 15:
        matchCh = (char)(program[pc++] & 0xFF);
        if (gData.cp != end && input.charAt(gData.cp) == matchCh) {
          bool1 = true;
          gData.cp++;
        } 
        break;
      case 16:
        offset = getIndex(program, pc);
        pc += 2;
        length = getIndex(program, pc);
        pc += 2;
        bool1 = flatNIMatcher(gData, offset, length, input, end);
        break;
      case 17:
        matchCh = (char)(program[pc++] & 0xFF);
        if (gData.cp != end) {
          char c = input.charAt(gData.cp);
          if (matchCh == c || upcase(matchCh) == upcase(c)) {
            bool1 = true;
            gData.cp++;
          } 
        } 
        break;
      case 18:
        matchCh = (char)getIndex(program, pc);
        pc += 2;
        if (gData.cp != end && input.charAt(gData.cp) == matchCh) {
          bool1 = true;
          gData.cp++;
        } 
        break;
      case 19:
        matchCh = (char)getIndex(program, pc);
        pc += 2;
        if (gData.cp != end) {
          char c = input.charAt(gData.cp);
          if (matchCh == c || upcase(matchCh) == upcase(c)) {
            bool1 = true;
            gData.cp++;
          } 
        } 
        break;
      case 22:
      case 23:
        index = getIndex(program, pc);
        pc += 2;
        if (gData.cp != end && classMatcher(gData, gData.regexp.classList[index], input.charAt(gData.cp))) {
          gData.cp++;
          bool1 = true;
        } 
        break;
      default:
        throw Kit.codeBug();
    } 
    if (bool1) {
      if (!updatecp)
        gData.cp = startcp; 
      return pc;
    } 
    gData.cp = startcp;
    return -1;
  }
  
  private static boolean executeREBytecode(REGlobalData gData, String input, int end) {
    int pc = 0;
    byte[] program = gData.regexp.program;
    int continuationOp = 57;
    int continuationPc = 0;
    boolean result = false;
    int op = program[pc++];
    if (gData.regexp.anchorCh < 0 && reopIsSimple(op)) {
      boolean anchor = false;
      while (gData.cp <= end) {
        int match = simpleMatch(gData, input, op, program, pc, end, true);
        if (match >= 0) {
          anchor = true;
          pc = match;
          op = program[pc++];
          break;
        } 
        gData.skipped++;
        gData.cp++;
      } 
      if (!anchor)
        return false; 
    } 
    while (true) {
      if (reopIsSimple(op)) {
        int match = simpleMatch(gData, input, op, program, pc, end, true);
        result = (match >= 0);
        if (result)
          pc = match; 
      } else {
        char matchCh1;
        int i;
        int offset;
        int parenIndex;
        int nextpc;
        REProgState rEProgState1;
        int min;
        REProgState state;
        char matchCh2;
        int startcp;
        int cap_index;
        int max;
        int new_min;
        char c;
        byte nextop;
        boolean greedy;
        int new_max;
        switch (op) {
          case 53:
          case 54:
          case 55:
            matchCh1 = (char)getIndex(program, pc);
            pc += 2;
            matchCh2 = (char)getIndex(program, pc);
            pc += 2;
            if (gData.cp == end) {
              result = false;
              break;
            } 
            c = input.charAt(gData.cp);
            if (op == 55) {
              if (c != matchCh1 && !classMatcher(gData, gData.regexp.classList[matchCh2], c)) {
                result = false;
                break;
              } 
            } else {
              if (op == 54)
                c = upcase(c); 
              if (c != matchCh1 && c != matchCh2) {
                result = false;
                break;
              } 
            } 
          case 31:
            i = pc + getOffset(program, pc);
            pc += 2;
            op = program[pc++];
            startcp = gData.cp;
            if (reopIsSimple(op)) {
              int match = simpleMatch(gData, input, op, program, pc, end, true);
              if (match < 0) {
                op = program[i++];
                pc = i;
                continue;
              } 
              result = true;
              pc = match;
              op = program[pc++];
            } 
            nextop = program[i++];
            pushBackTrackState(gData, nextop, i, startcp, continuationOp, continuationPc);
            continue;
          case 32:
            offset = getOffset(program, pc);
            pc += offset;
            op = program[pc++];
            continue;
          case 29:
            parenIndex = getIndex(program, pc);
            pc += 2;
            gData.setParens(parenIndex, gData.cp, 0);
            op = program[pc++];
            continue;
          case 30:
            parenIndex = getIndex(program, pc);
            pc += 2;
            cap_index = gData.parensIndex(parenIndex);
            gData.setParens(parenIndex, cap_index, gData.cp - cap_index);
            op = program[pc++];
            continue;
          case 41:
            nextpc = pc + getIndex(program, pc);
            pc += 2;
            op = program[pc++];
            if (reopIsSimple(op) && simpleMatch(gData, input, op, program, pc, end, false) < 0) {
              result = false;
              break;
            } 
            pushProgState(gData, 0, 0, gData.cp, gData.backTrackStackTop, continuationOp, continuationPc);
            pushBackTrackState(gData, (byte)43, nextpc);
            continue;
          case 42:
            nextpc = pc + getIndex(program, pc);
            pc += 2;
            op = program[pc++];
            if (reopIsSimple(op)) {
              int match = simpleMatch(gData, input, op, program, pc, end, false);
              if (match >= 0 && program[match] == 44) {
                result = false;
                break;
              } 
            } 
            pushProgState(gData, 0, 0, gData.cp, gData.backTrackStackTop, continuationOp, continuationPc);
            pushBackTrackState(gData, (byte)44, nextpc);
            continue;
          case 43:
          case 44:
            rEProgState1 = popProgState(gData);
            gData.cp = rEProgState1.index;
            gData.backTrackStackTop = rEProgState1.backTrack;
            continuationPc = rEProgState1.continuationPc;
            continuationOp = rEProgState1.continuationOp;
            if (op == 44)
              result = !result; 
            break;
          case 25:
          case 26:
          case 27:
          case 28:
          case 45:
          case 46:
          case 47:
          case 48:
            greedy = false;
            switch (op) {
              case 26:
                greedy = true;
              case 45:
                min = 0;
                max = -1;
                break;
              case 27:
                greedy = true;
              case 46:
                min = 1;
                max = -1;
                break;
              case 28:
                greedy = true;
              case 47:
                min = 0;
                max = 1;
                break;
              case 25:
                greedy = true;
              case 48:
                min = getOffset(program, pc);
                pc += 2;
                max = getOffset(program, pc) - 1;
                pc += 2;
                break;
              default:
                throw Kit.codeBug();
            } 
            pushProgState(gData, min, max, gData.cp, (REBackTrackData)null, continuationOp, continuationPc);
            if (greedy) {
              pushBackTrackState(gData, (byte)51, pc);
              continuationOp = 51;
              continuationPc = pc;
              pc += 6;
              op = program[pc++];
              continue;
            } 
            if (min != 0) {
              continuationOp = 52;
              continuationPc = pc;
              pc += 6;
              op = program[pc++];
              continue;
            } 
            pushBackTrackState(gData, (byte)52, pc);
            popProgState(gData);
            pc += 4;
            pc += getOffset(program, pc);
            op = program[pc++];
            continue;
          case 49:
            result = true;
            pc = continuationPc;
            op = continuationOp;
            continue;
          case 51:
            while (true) {
              REProgState rEProgState = popProgState(gData);
              if (!result) {
                if (rEProgState.min == 0)
                  result = true; 
                continuationPc = rEProgState.continuationPc;
                continuationOp = rEProgState.continuationOp;
                pc += 4;
                pc += getOffset(program, pc);
                break;
              } 
              if (rEProgState.min == 0 && gData.cp == rEProgState.index) {
                result = false;
                continuationPc = rEProgState.continuationPc;
                continuationOp = rEProgState.continuationOp;
                pc += 4;
                pc += getOffset(program, pc);
                break;
              } 
              int m = rEProgState.min, n = rEProgState.max;
              if (m != 0)
                m--; 
              if (n != -1)
                n--; 
              if (n == 0) {
                result = true;
                continuationPc = rEProgState.continuationPc;
                continuationOp = rEProgState.continuationOp;
                pc += 4;
                pc += getOffset(program, pc);
                break;
              } 
              int j = pc + 6;
              int k = program[j];
              int i1 = gData.cp;
              if (reopIsSimple(k)) {
                j++;
                int match = simpleMatch(gData, input, k, program, j, end, true);
                if (match < 0) {
                  result = (m == 0);
                  continuationPc = rEProgState.continuationPc;
                  continuationOp = rEProgState.continuationOp;
                  pc += 4;
                  pc += getOffset(program, pc);
                  break;
                } 
                result = true;
                j = match;
              } 
              continuationOp = 51;
              continuationPc = pc;
              pushProgState(gData, m, n, i1, (REBackTrackData)null, rEProgState.continuationOp, rEProgState.continuationPc);
              if (m == 0) {
                pushBackTrackState(gData, (byte)51, pc, i1, rEProgState.continuationOp, rEProgState.continuationPc);
                int parenCount = getIndex(program, pc);
                int i2 = getIndex(program, pc + 2);
                for (int i3 = 0; i3 < parenCount; i3++)
                  gData.setParens(i2 + i3, -1, 0); 
              } 
              if (program[j] != 49) {
                pc = j;
                op = program[pc++];
              } 
            } 
            break;
          case 52:
            state = popProgState(gData);
            if (!result) {
              if (state.max == -1 || state.max > 0) {
                pushProgState(gData, state.min, state.max, gData.cp, (REBackTrackData)null, state.continuationOp, state.continuationPc);
                continuationOp = 52;
                continuationPc = pc;
                int parenCount = getIndex(program, pc);
                pc += 2;
                int j = getIndex(program, pc);
                pc += 4;
                for (int k = 0; k < parenCount; k++)
                  gData.setParens(j + k, -1, 0); 
                op = program[pc++];
                continue;
              } 
              continuationPc = state.continuationPc;
              continuationOp = state.continuationOp;
              break;
            } 
            if (state.min == 0 && gData.cp == state.index) {
              result = false;
              continuationPc = state.continuationPc;
              continuationOp = state.continuationOp;
              break;
            } 
            new_min = state.min;
            new_max = state.max;
            if (new_min != 0)
              new_min--; 
            if (new_max != -1)
              new_max--; 
            pushProgState(gData, new_min, new_max, gData.cp, (REBackTrackData)null, state.continuationOp, state.continuationPc);
            if (new_min != 0) {
              continuationOp = 52;
              continuationPc = pc;
              int parenCount = getIndex(program, pc);
              pc += 2;
              int j = getIndex(program, pc);
              pc += 4;
              for (int k = 0; k < parenCount; k++)
                gData.setParens(j + k, -1, 0); 
              op = program[pc++];
              continue;
            } 
            continuationPc = state.continuationPc;
            continuationOp = state.continuationOp;
            pushBackTrackState(gData, (byte)52, pc);
            popProgState(gData);
            pc += 4;
            pc += getOffset(program, pc);
            op = program[pc++];
            continue;
          case 57:
            return true;
          default:
            throw Kit.codeBug("invalid bytecode");
        } 
      } 
      if (!result) {
        REBackTrackData backTrackData = gData.backTrackStackTop;
        if (backTrackData != null) {
          gData.backTrackStackTop = backTrackData.previous;
          gData.parens = backTrackData.parens;
          gData.cp = backTrackData.cp;
          gData.stateStackTop = backTrackData.stateStackTop;
          continuationOp = backTrackData.continuationOp;
          continuationPc = backTrackData.continuationPc;
          pc = backTrackData.pc;
          op = backTrackData.op;
          continue;
        } 
        return false;
      } 
      op = program[pc++];
    } 
  }
  
  private static boolean matchRegExp(REGlobalData gData, RECompiled re, String input, int start, int end, boolean multiline) {
    if (re.parenCount != 0) {
      gData.parens = new long[re.parenCount];
    } else {
      gData.parens = null;
    } 
    gData.backTrackStackTop = null;
    gData.stateStackTop = null;
    gData.multiline = (multiline || (re.flags & 0x4) != 0);
    gData.regexp = re;
    int anchorCh = gData.regexp.anchorCh;
    for (int i = start; i <= end; i++) {
      if (anchorCh >= 0)
        while (true) {
          if (i == end)
            return false; 
          char matchCh = input.charAt(i);
          if (matchCh == anchorCh || ((gData.regexp.flags & 0x2) != 0 && upcase(matchCh) == upcase((char)anchorCh)))
            break; 
          i++;
        }  
      gData.cp = i;
      gData.skipped = i - start;
      for (int j = 0; j < re.parenCount; j++)
        gData.parens[j] = -1L; 
      boolean result = executeREBytecode(gData, input, end);
      gData.backTrackStackTop = null;
      gData.stateStackTop = null;
      if (result)
        return true; 
      if (anchorCh == -2 && !gData.multiline) {
        gData.skipped = end;
        return false;
      } 
      i = start + gData.skipped;
    } 
    return false;
  }
  
  Object executeRegExp(Context cx, Scriptable scope, RegExpImpl res, String str, int[] indexp, int matchType) {
    Object result;
    Scriptable obj;
    REGlobalData gData = new REGlobalData();
    int start = indexp[0];
    int end = str.length();
    if (start > end)
      start = end; 
    boolean matches = matchRegExp(gData, this.re, str, start, end, res.multiline);
    if (!matches) {
      if (matchType != 2)
        return null; 
      return Undefined.instance;
    } 
    int index = gData.cp;
    int ep = indexp[0] = index;
    int matchlen = ep - start + gData.skipped;
    index -= matchlen;
    if (matchType == 0) {
      result = Boolean.TRUE;
      obj = null;
    } else {
      result = cx.newArray(scope, 0);
      obj = (Scriptable)result;
      String matchstr = str.substring(index, index + matchlen);
      obj.put(0, obj, matchstr);
    } 
    if (this.re.parenCount == 0) {
      res.parens = null;
      res.lastParen = SubString.emptySubString;
    } else {
      SubString parsub = null;
      res.parens = new SubString[this.re.parenCount];
      for (int num = 0; num < this.re.parenCount; num++) {
        int cap_index = gData.parensIndex(num);
        if (cap_index != -1) {
          int cap_length = gData.parensLength(num);
          parsub = new SubString(str, cap_index, cap_length);
          res.parens[num] = parsub;
          if (matchType != 0)
            obj.put(num + 1, obj, parsub.toString()); 
        } else if (matchType != 0) {
          obj.put(num + 1, obj, Undefined.instance);
        } 
      } 
      res.lastParen = parsub;
    } 
    if (matchType != 0) {
      obj.put("index", obj, Integer.valueOf(start + gData.skipped));
      obj.put("input", obj, str);
    } 
    if (res.lastMatch == null) {
      res.lastMatch = new SubString();
      res.leftContext = new SubString();
      res.rightContext = new SubString();
    } 
    res.lastMatch.str = str;
    res.lastMatch.index = index;
    res.lastMatch.length = matchlen;
    res.leftContext.str = str;
    if (cx.getLanguageVersion() == 120) {
      res.leftContext.index = start;
      res.leftContext.length = gData.skipped;
    } else {
      res.leftContext.index = 0;
      res.leftContext.length = start + gData.skipped;
    } 
    res.rightContext.str = str;
    res.rightContext.index = ep;
    res.rightContext.length = end - ep;
    return result;
  }
  
  int getFlags() {
    return this.re.flags;
  }
  
  private static void reportWarning(Context cx, String messageId, String arg) {
    if (cx.hasFeature(11)) {
      String msg = ScriptRuntime.getMessage1(messageId, arg);
      Context.reportWarning(msg);
    } 
  }
  
  private static void reportError(String messageId, String arg) {
    String msg = ScriptRuntime.getMessage1(messageId, arg);
    throw ScriptRuntime.constructError("SyntaxError", msg);
  }
  
  protected int getMaxInstanceId() {
    return 5;
  }
  
  protected int findInstanceIdInfo(String s) {
    int attr, id = 0;
    String X = null;
    int s_length = s.length();
    if (s_length == 6) {
      int c = s.charAt(0);
      if (c == 103) {
        X = "global";
        id = 3;
      } else if (c == 115) {
        X = "source";
        id = 2;
      } 
    } else if (s_length == 9) {
      int c = s.charAt(0);
      if (c == 108) {
        X = "lastIndex";
        id = 1;
      } else if (c == 109) {
        X = "multiline";
        id = 5;
      } 
    } else if (s_length == 10) {
      X = "ignoreCase";
      id = 4;
    } 
    if (X != null && X != s && !X.equals(s))
      id = 0; 
    if (id == 0)
      return super.findInstanceIdInfo(s); 
    switch (id) {
      case 1:
        attr = this.lastIndexAttr;
        return instanceIdInfo(attr, id);
      case 2:
      case 3:
      case 4:
      case 5:
        attr = 7;
        return instanceIdInfo(attr, id);
    } 
    throw new IllegalStateException();
  }
  
  protected String getInstanceIdName(int id) {
    switch (id) {
      case 1:
        return "lastIndex";
      case 2:
        return "source";
      case 3:
        return "global";
      case 4:
        return "ignoreCase";
      case 5:
        return "multiline";
    } 
    return super.getInstanceIdName(id);
  }
  
  protected Object getInstanceIdValue(int id) {
    switch (id) {
      case 1:
        return this.lastIndex;
      case 2:
        return new String(this.re.source);
      case 3:
        return ScriptRuntime.wrapBoolean(((this.re.flags & 0x1) != 0));
      case 4:
        return ScriptRuntime.wrapBoolean(((this.re.flags & 0x2) != 0));
      case 5:
        return ScriptRuntime.wrapBoolean(((this.re.flags & 0x4) != 0));
    } 
    return super.getInstanceIdValue(id);
  }
  
  protected void setInstanceIdValue(int id, Object value) {
    switch (id) {
      case 1:
        this.lastIndex = value;
        return;
      case 2:
      case 3:
      case 4:
      case 5:
        return;
    } 
    super.setInstanceIdValue(id, value);
  }
  
  protected void setInstanceIdAttributes(int id, int attr) {
    switch (id) {
      case 1:
        this.lastIndexAttr = attr;
        return;
    } 
    super.setInstanceIdAttributes(id, attr);
  }
  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) {
      case 1:
        arity = 2;
        s = "compile";
        break;
      case 2:
        arity = 0;
        s = "toString";
        break;
      case 3:
        arity = 0;
        s = "toSource";
        break;
      case 4:
        arity = 1;
        s = "exec";
        break;
      case 5:
        arity = 1;
        s = "test";
        break;
      case 6:
        arity = 1;
        s = "prefix";
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(id));
    } 
    initPrototypeMethod(REGEXP_TAG, id, s, arity);
  }
  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    Object x;
    if (!f.hasTag(REGEXP_TAG))
      return super.execIdCall(f, cx, scope, thisObj, args); 
    int id = f.methodId();
    switch (id) {
      case 1:
        return realThis(thisObj, f).compile(cx, scope, args);
      case 2:
      case 3:
        return realThis(thisObj, f).toString();
      case 4:
        return realThis(thisObj, f).execSub(cx, scope, args, 1);
      case 5:
        x = realThis(thisObj, f).execSub(cx, scope, args, 0);
        return Boolean.TRUE.equals(x) ? Boolean.TRUE : Boolean.FALSE;
      case 6:
        return realThis(thisObj, f).execSub(cx, scope, args, 2);
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }
  
  private static NativeRegExp realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeRegExp))
      throw incompatibleCallError(f); 
    return (NativeRegExp)thisObj;
  }
  
  protected int findPrototypeId(String s) {
    int c, id = 0;
    String X = null;
    switch (s.length()) {
      case 4:
        c = s.charAt(0);
        if (c == 101) {
          X = "exec";
          id = 4;
          break;
        } 
        if (c == 116) {
          X = "test";
          id = 5;
        } 
        break;
      case 6:
        X = "prefix";
        id = 6;
        break;
      case 7:
        X = "compile";
        id = 1;
        break;
      case 8:
        c = s.charAt(3);
        if (c == 111) {
          X = "toSource";
          id = 3;
          break;
        } 
        if (c == 116) {
          X = "toString";
          id = 2;
        } 
        break;
    } 
    if (X != null && X != s && !X.equals(s))
      id = 0; 
    return id;
  }
}
