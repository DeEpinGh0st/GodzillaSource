package com.kitfox.svg.animation.parser;
import com.kitfox.svg.animation.TimeBase;
import com.kitfox.svg.animation.TimeCompound;
import com.kitfox.svg.animation.TimeDiscrete;
import com.kitfox.svg.animation.TimeIndefinite;
import com.kitfox.svg.animation.TimeLookup;
import com.kitfox.svg.animation.TimeSum;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnimTimeParser implements AnimTimeParserTreeConstants, AnimTimeParserConstants {
  protected JJTAnimTimeParserState jjtree = new JJTAnimTimeParserState(); public AnimTimeParserTokenManager token_source; SimpleCharStream jj_input_stream; public Token token; public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos;
  private Token jj_lastpos;
  private int jj_la;
  private int jj_gen;
  
  public static void main(String[] args) throws ParseException {
    StringReader reader = new StringReader("1:30 + 5ms");
    AnimTimeParser parser = new AnimTimeParser(reader);

    
    TimeBase tc = parser.Expr();
    System.err.println("AnimTimeParser eval to " + tc.evalTime());
    
    reader = new StringReader("19");
    parser.ReInit(reader);
    tc = parser.Expr();
    System.err.println("AnimTimeParser eval to " + tc.evalTime());
  }





  
  public final TimeBase Expr() throws ParseException {
    ASTExpr jjtn000 = new ASTExpr(0);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000);
    ArrayList<TimeBase> list = new ArrayList(); try {
      TimeBase term;
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 14:
          term = Sum();
          list.add(term);
          break;
        
        default:
          this.jj_la1[0] = this.jj_gen;
          break;
      } 

      
      while (jj_2_1(2)) {


        
        jj_consume_token(15);
        term = Sum();
        list.add(term);
      } 
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 15:
          jj_consume_token(15);
          break;
        
        default:
          this.jj_la1[1] = this.jj_gen;
          break;
      } 
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      switch (list.size()) {
        
        case 0:
          if ("" != null) return (TimeBase)new TimeIndefinite(); 
        case 1:
          if ("" != null) return list.get(0);  break;
      } 
      if ("" != null) return (TimeBase)new TimeCompound(list);
    
    } catch (Throwable jjte000) {
      if (jjtc000) {
        this.jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        this.jjtree.popNode();
      } 
      if (jjte000 instanceof ParseException) {
        throw (ParseException)jjte000;
      }
      if (jjte000 instanceof RuntimeException) {
        throw (RuntimeException)jjte000;
      }
      throw (Error)jjte000;
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final TimeBase Sum() throws ParseException {
    ASTSum jjtn000 = new ASTSum(1);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000); Token t = null;
    
    TimeBase t2 = null;
    try {
      TimeBase t1 = Term();
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 16:
        case 17:
          switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
            case 16:
              t = jj_consume_token(16);
              break;
            
            case 17:
              t = jj_consume_token(17);
              break;
            
            default:
              this.jj_la1[2] = this.jj_gen;
              jj_consume_token(-1);
              throw new ParseException();
          } 
          t2 = Term();
          break;
        
        default:
          this.jj_la1[3] = this.jj_gen;
          break;
      } 
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      if (t2 == null && "" != null) return t1;
      
      if (t.image.equals("-"))
      
      { if ("" != null) return (TimeBase)new TimeSum(t1, t2, false);
        
         }
      
      else if ("" != null) { return (TimeBase)new TimeSum(t1, t2, true); }
    
    } catch (Throwable jjte000) {
      if (jjtc000) {
        this.jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        this.jjtree.popNode();
      } 
      if (jjte000 instanceof ParseException) {
        throw (ParseException)jjte000;
      }
      if (jjte000 instanceof RuntimeException) {
        throw (RuntimeException)jjte000;
      }
      throw (Error)jjte000;
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final TimeBase Term() throws ParseException {
    ASTTerm jjtn000 = new ASTTerm(2);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000); try {
      TimeIndefinite timeIndefinite; TimeDiscrete timeDiscrete2; TimeLookup timeLookup; TimeDiscrete timeDiscrete1;
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 10:
          timeIndefinite = IndefiniteTime();
          this.jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          if ("" != null) return (TimeBase)timeIndefinite;
          
          break;
        case 8:
        case 9:
          timeDiscrete2 = LiteralTime();
          this.jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          if ("" != null) return (TimeBase)timeDiscrete2;
          
          break;
        case 14:
          timeLookup = LookupTime();
          this.jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          if ("" != null) return (TimeBase)timeLookup;
          
          break;
        case 11:
        case 12:
          timeDiscrete1 = EventTime();
          this.jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          if ("" != null) return (TimeBase)timeDiscrete1;
          
          break;
        default:
          this.jj_la1[4] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      } 
    } catch (Throwable jjte000) {
      if (jjtc000) {
        this.jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        this.jjtree.popNode();
      } 
      if (jjte000 instanceof ParseException) {
        throw (ParseException)jjte000;
      }
      if (jjte000 instanceof RuntimeException) {
        throw (RuntimeException)jjte000;
      }
      throw (Error)jjte000;
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final TimeIndefinite IndefiniteTime() throws ParseException {
    ASTIndefiniteTime jjtn000 = new ASTIndefiniteTime(3);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(10);
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      if ("" != null) return new TimeIndefinite(); 
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final TimeDiscrete EventTime() throws ParseException {
    ASTEventTime jjtn000 = new ASTEventTime(4);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000);
    try {
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 11:
          jj_consume_token(11);
          break;
        
        case 12:
          jj_consume_token(12);
          break;
        
        default:
          this.jj_la1[5] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      } 
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      
      if ("" != null) return new TimeDiscrete(0.0D); 
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final TimeDiscrete LiteralTime() throws ParseException {
    ASTLiteralTime jjtn000 = new ASTLiteralTime(5);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000); double t3 = Double.NaN; try {
      double t2;
      Token t;
      double t1 = Number();
      double value = t1;
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 13:
        case 18:
          switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
            case 18:
              jj_consume_token(18);
              t2 = Number();
              switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
                case 18:
                  jj_consume_token(18);
                  t3 = Number();
                  break;
                
                default:
                  this.jj_la1[6] = this.jj_gen;
                  break;
              } 
              
              if (Double.isNaN(t3)) {
                
                value = t1 * 60.0D + t2;
                
                break;
              } 
              value = t1 * 3600.0D + t2 * 60.0D + t3;
              break;

            
            case 13:
              t = jj_consume_token(13);
              
              if (t.image.equals("ms")) value = t1 / 1000.0D; 
              if (t.image.equals("min")) value = t1 * 60.0D; 
              if (t.image.equals("h")) value = t1 * 3600.0D;
              
              break;
          } 
          this.jj_la1[7] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();


        
        default:
          this.jj_la1[8] = this.jj_gen;
          break;
      } 
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      if ("" != null) return new TimeDiscrete(value); 
    } catch (Throwable jjte000) {
      if (jjtc000) {
        this.jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        this.jjtree.popNode();
      } 
      if (jjte000 instanceof ParseException) {
        throw (ParseException)jjte000;
      }
      if (jjte000 instanceof RuntimeException) {
        throw (RuntimeException)jjte000;
      }
      throw (Error)jjte000;
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final TimeLookup LookupTime() throws ParseException {
    ASTLookupTime jjtn000 = new ASTLookupTime(6);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000); double paramNum = 0.0D;
    
    try {
      Token node = jj_consume_token(14);
      jj_consume_token(19);
      Token event = jj_consume_token(14);
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 20:
          paramNum = ParamList();
          break;
        
        default:
          this.jj_la1[9] = this.jj_gen;
          break;
      } 
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      if ("" != null) return new TimeLookup(null, node.image, event.image, "" + paramNum); 
    } catch (Throwable jjte000) {
      if (jjtc000) {
        this.jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        this.jjtree.popNode();
      } 
      if (jjte000 instanceof ParseException) {
        throw (ParseException)jjte000;
      }
      if (jjte000 instanceof RuntimeException) {
        throw (RuntimeException)jjte000;
      }
      throw (Error)jjte000;
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final double ParamList() throws ParseException {
    ASTParamList jjtn000 = new ASTParamList(7);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(20);
      double num = Number();
      jj_consume_token(21);
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      if ("" != null) return num; 
    } catch (Throwable jjte000) {
      if (jjtc000) {
        this.jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        this.jjtree.popNode();
      } 
      if (jjte000 instanceof ParseException) {
        throw (ParseException)jjte000;
      }
      if (jjte000 instanceof RuntimeException) {
        throw (RuntimeException)jjte000;
      }
      throw (Error)jjte000;
    } finally {
      if (jjtc000) {
        this.jjtree.closeNodeScope(jjtn000, true);
      }
    } 
    throw new IllegalStateException("Missing return statement in function");
  }
  
  public final double Number() throws ParseException {
    ASTNumber jjtn000 = new ASTNumber(8);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000); 
    try { Token t;
      switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
        case 9:
          t = jj_consume_token(9);
          this.jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          try {
            if ("" != null) return Double.parseDouble(t.image);
          
          } catch (Exception e) {
            Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Could not parse double '" + t.image + "'", e);
          } 

          
          if ("" != null) return 0.0D;
          
          break;
        case 8:
          t = jj_consume_token(8);
          this.jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          try {
            if ("" != null) return Double.parseDouble(t.image);
          
          } catch (Exception e) {
            Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Could not parse double '" + t.image + "'", e);
          } 

          
          if ("" != null) return 0.0D;
          
          break;
        default:
          this.jj_la1[10] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      } 
      
      if (jjtc000)
        this.jjtree.closeNodeScope(jjtn000, true);  } finally { if (jjtc000) this.jjtree.closeNodeScope(jjtn000, true);
       }
  
  }

  
  public final int Integer() throws ParseException {
    ASTInteger jjtn000 = new ASTInteger(9);
    boolean jjtc000 = true;
    this.jjtree.openNodeScope(jjtn000);
    
    try { Token t = jj_consume_token(8);
      this.jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      try {
        if ("" != null) return Integer.parseInt(t.image);
      
      } catch (Exception e) {
        Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Could not parse int '" + t.image + "'", e);
      } 

      
      if ("" != null) return 0;
      
      if (jjtc000)
        this.jjtree.closeNodeScope(jjtn000, true);  } finally { if (jjtc000) this.jjtree.closeNodeScope(jjtn000, true);
       }
  
  }


  
  private boolean jj_2_1(int xla) {
    this.jj_la = xla;
    this.jj_scanpos = this.token;
    this.jj_lastpos = this.token; 
    try { return !jj_3_1(); }
    catch (LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  
  }

  
  private boolean jj_3R_3() {
    Token xsp = this.jj_scanpos;
    if (jj_3R_4()) {
      this.jj_scanpos = xsp;
      if (jj_3R_5()) {
        this.jj_scanpos = xsp;
        if (jj_3R_6()) {
          this.jj_scanpos = xsp;
          if (jj_3R_7()) return true; 
        } 
      } 
    } 
    return false;
  }

  
  private boolean jj_3R_4() {
    if (jj_3R_8()) return true; 
    return false;
  }

  
  private boolean jj_3R_9() {
    if (jj_3R_12()) return true; 
    return false;
  }

  
  private boolean jj_3R_5() {
    if (jj_3R_9()) return true; 
    return false;
  }


  
  private boolean jj_3R_12() {
    Token xsp = this.jj_scanpos;
    if (jj_3R_13()) {
      this.jj_scanpos = xsp;
      if (jj_3R_14()) return true; 
    } 
    return false;
  }

  
  private boolean jj_3R_13() {
    if (jj_scan_token(9)) return true; 
    return false;
  }

  
  private boolean jj_3R_6() {
    if (jj_3R_10()) return true; 
    return false;
  }

  
  private boolean jj_3R_7() {
    if (jj_3R_11()) return true; 
    return false;
  }

  
  private boolean jj_3R_2() {
    if (jj_3R_3()) return true; 
    return false;
  }

  
  private boolean jj_3R_8() {
    if (jj_scan_token(10)) return true; 
    return false;
  }

  
  private boolean jj_3R_10() {
    if (jj_scan_token(14)) return true; 
    return false;
  }

  
  private boolean jj_3_1() {
    if (jj_scan_token(15)) return true; 
    if (jj_3R_2()) return true; 
    return false;
  }

  
  private boolean jj_3R_14() {
    if (jj_scan_token(8)) return true; 
    return false;
  }


  
  private boolean jj_3R_11() {
    Token xsp = this.jj_scanpos;
    if (jj_scan_token(11)) {
      this.jj_scanpos = xsp;
      if (jj_scan_token(12)) return true; 
    } 
    return false;
  }











  
  private final int[] jj_la1 = new int[11]; private static int[] jj_la1_0;
  
  static {
    jj_la1_init_0();
  }
  private static void jj_la1_init_0() {
    jj_la1_0 = new int[] { 24320, 32768, 196608, 196608, 24320, 6144, 262144, 270336, 270336, 1048576, 768 };
  }
  private final JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0; private final LookaheadSuccess jj_ls; private List<int[]> jj_expentries;
  private int[] jj_expentry;
  private int jj_kind;
  private int[] jj_lasttokens;
  private int jj_endpos;
  
  public AnimTimeParser(InputStream stream) {
    this(stream, null);
  }



















  
  public void ReInit(InputStream stream) {
    ReInit(stream, null);
  }




  
  public void ReInit(InputStream stream, Charset encoding) {
    this.jj_input_stream.reInit(stream, encoding, 1, 1);
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0; int i;
    for (i = 0; i < 11; ) { this.jj_la1[i] = -1; i++; }
     for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
  
  }



















  
  public void ReInit(Reader stream) {
    if (this.jj_input_stream == null) {
      this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
    } else {
      this.jj_input_stream.reInit(stream, 1, 1);
    } 
    if (this.token_source == null) {
      this.token_source = new AnimTimeParserTokenManager(this.jj_input_stream);
    }
    
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0; int i;
    for (i = 0; i < 11; i++)
      this.jj_la1[i] = -1; 
    for (i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
















  
  public void ReInit(AnimTimeParserTokenManager tm) {
    this.token_source = tm;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0; int i;
    for (i = 0; i < 11; ) { this.jj_la1[i] = -1; i++; }
     for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
  
  }
  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken = this.token;
    if (this.token.next != null) {
      this.token = this.token.next;
    } else {
      this.token.next = this.token_source.getNextToken();
      this.token = this.token.next;
    } 
    this.jj_ntk = -1;
    if (this.token.kind == kind) {
      this.jj_gen++;
      if (++this.jj_gc > 100) {
        this.jj_gc = 0;
        for (int i = 0; i < this.jj_2_rtns.length; i++) {
          JJCalls c = this.jj_2_rtns[i];
          while (c != null) {
            if (c.gen < this.jj_gen)
              c.first = null; 
            c = c.next;
          } 
        } 
      } 
      return this.token;
    } 
    this.token = oldToken;
    this.jj_kind = kind;
    throw generateParseException();
  }
  private static final class LookaheadSuccess extends IllegalStateException {
    private LookaheadSuccess() {} }
  
  public AnimTimeParser(InputStream stream, Charset encoding) { this.jj_ls = new LookaheadSuccess();



























































    
    this.jj_expentries = (List)new ArrayList<int>();
    
    this.jj_kind = -1;
    this.jj_lasttokens = new int[100]; this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); this.token_source = new AnimTimeParserTokenManager(this.jj_input_stream); this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 11; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  } public AnimTimeParser(Reader stream) { this.jj_ls = new LookaheadSuccess(); this.jj_expentries = (List)new ArrayList<int>(); this.jj_kind = -1; this.jj_lasttokens = new int[100]; this.jj_input_stream = new SimpleCharStream(stream, 1, 1); this.token_source = new AnimTimeParserTokenManager(this.jj_input_stream); this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 11; i++) this.jj_la1[i] = -1;  for (i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls();  } public AnimTimeParser(AnimTimeParserTokenManager tm) { this.jj_ls = new LookaheadSuccess(); this.jj_expentries = (List)new ArrayList<int>(); this.jj_kind = -1; this.jj_lasttokens = new int[100]; this.token_source = tm; this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 11; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  }
  private boolean jj_scan_token(int kind) { if (this.jj_scanpos == this.jj_lastpos) { this.jj_la--; if (this.jj_scanpos.next == null) { this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken(); } else { this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next; }  } else { this.jj_scanpos = this.jj_scanpos.next; }  if (this.jj_rescan) { int i = 0; Token tok = this.token; while (tok != null && tok != this.jj_scanpos) { i++; tok = tok.next; }  if (tok != null)
        jj_add_error_token(kind, i);  }  if (this.jj_scanpos.kind != kind)
      return true;  if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
      throw this.jj_ls;  return false; } private void jj_add_error_token(int kind, int pos) { if (pos >= 100) {
      return;
    }
    
    if (pos == this.jj_endpos + 1) {
      this.jj_lasttokens[this.jj_endpos++] = kind;
    } else if (this.jj_endpos != 0) {
      this.jj_expentry = new int[this.jj_endpos];
      
      for (int i = 0; i < this.jj_endpos; i++) {
        this.jj_expentry[i] = this.jj_lasttokens[i];
      }
      
      for (int[] oldentry : this.jj_expentries) {
        if (oldentry.length == this.jj_expentry.length) {
          boolean isMatched = true;
          for (int j = 0; j < this.jj_expentry.length; j++) {
            if (oldentry[j] != this.jj_expentry[j]) {
              isMatched = false;
              break;
            } 
          } 
          if (isMatched) {
            this.jj_expentries.add(this.jj_expentry);
            
            break;
          } 
        } 
      } 
      if (pos != 0) {
        this.jj_endpos = pos;
        this.jj_lasttokens[this.jj_endpos - 1] = kind;
      } 
    }  }
  public final Token getNextToken() { if (this.token.next != null) { this.token = this.token.next; }
    else { this.token = this.token.next = this.token_source.getNextToken(); }
     this.jj_ntk = -1; this.jj_gen++; return this.token; }
  public final Token getToken(int index) { Token t = this.token; for (int i = 0; i < index; i++) { if (t.next == null)
        t.next = this.token_source.getNextToken();  t = t.next; }
     return t; }
  private int jj_ntk_f() { this.jj_nt = this.token.next; if (this.jj_nt == null) { this.token.next = this.token_source.getNextToken(); this.jj_ntk = this.token.next.kind; return this.jj_ntk; }
     this.jj_ntk = this.jj_nt.kind; return this.jj_ntk; } public ParseException generateParseException() { this.jj_expentries.clear();
    boolean[] la1tokens = new boolean[22];
    if (this.jj_kind >= 0) {
      la1tokens[this.jj_kind] = true;
      this.jj_kind = -1;
    }  int i;
    for (i = 0; i < 11; i++) {
      if (this.jj_la1[i] == this.jj_gen) {
        for (int k = 0; k < 32; k++) {
          if ((jj_la1_0[i] & 1 << k) != 0) {
            la1tokens[k] = true;
          }
        } 
      }
    } 
    for (i = 0; i < 22; i++) {
      if (la1tokens[i]) {
        this.jj_expentry = new int[1];
        this.jj_expentry[0] = i;
        this.jj_expentries.add(this.jj_expentry);
      } 
    } 
    this.jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[this.jj_expentries.size()][];
    for (int j = 0; j < this.jj_expentries.size(); j++) {
      exptokseq[j] = this.jj_expentries.get(j);
    }
    return new ParseException(this.token, exptokseq, tokenImage); }




  
  public final boolean trace_enabled() {
    return false;
  }

  
  public final void enable_tracing() {}

  
  public final void disable_tracing() {}
  
  private void jj_rescan_token() {
    this.jj_rescan = true;
    for (int i = 0; i < 1; i++) {
      try {
        JJCalls p = this.jj_2_rtns[i];
        do {
          if (p.gen > this.jj_gen) {
            this.jj_la = p.arg;
            this.jj_scanpos = p.first;
            this.jj_lastpos = p.first;
            switch (i) { case 0:
                jj_3_1(); break; }
          
          } 
          p = p.next;
        } while (p != null);
      } catch (LookaheadSuccess lookaheadSuccess) {}
    } 
    this.jj_rescan = false;
  }
  
  private void jj_save(int index, int xla) {
    JJCalls p = this.jj_2_rtns[index];
    while (p.gen > this.jj_gen) {
      if (p.next == null) {
        p.next = new JJCalls();
        p = p.next;
        break;
      } 
      p = p.next;
    } 
    p.gen = this.jj_gen + xla - this.jj_la;
    p.first = this.token;
    p.arg = xla;
  }
  
  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }
}
