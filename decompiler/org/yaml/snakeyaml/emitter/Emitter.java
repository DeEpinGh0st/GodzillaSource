package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentEventsCollector;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Constant;
import org.yaml.snakeyaml.util.ArrayStack;




































public final class Emitter
  implements Emitable
{
  public static final int MIN_INDENT = 1;
  public static final int MAX_INDENT = 10;
  private static final char[] SPACE = new char[] { ' ' };
  
  private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");
  private static final Set<Character> INVALID_ANCHOR = new HashSet<>();
  static {
    INVALID_ANCHOR.add(Character.valueOf('['));
    INVALID_ANCHOR.add(Character.valueOf(']'));
    INVALID_ANCHOR.add(Character.valueOf('{'));
    INVALID_ANCHOR.add(Character.valueOf('}'));
    INVALID_ANCHOR.add(Character.valueOf(','));
    INVALID_ANCHOR.add(Character.valueOf('*'));
    INVALID_ANCHOR.add(Character.valueOf('&'));
  }
  
  private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<>();
  static {
    ESCAPE_REPLACEMENTS.put(Character.valueOf(false), "0");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\007'), "a");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\b'), "b");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\t'), "t");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\n'), "n");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\013'), "v");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\f'), "f");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\r'), "r");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\033'), "e");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(''), "N");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "_");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "L");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "P");
  }
  
  private static final Map<String, String> DEFAULT_TAG_PREFIXES = new LinkedHashMap<>();
  static {
    DEFAULT_TAG_PREFIXES.put("!", "!");
    DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
  }


  
  private final Writer stream;

  
  private final ArrayStack<EmitterState> states;
  
  private EmitterState state;
  
  private final Queue<Event> events;
  
  private Event event;
  
  private final ArrayStack<Integer> indents;
  
  private Integer indent;
  
  private int flowLevel;
  
  private boolean rootContext;
  
  private boolean mappingContext;
  
  private boolean simpleKeyContext;
  
  private int column;
  
  private boolean whitespace;
  
  private boolean indention;
  
  private boolean openEnded;
  
  private final Boolean canonical;
  
  private final Boolean prettyFlow;
  
  private final boolean allowUnicode;
  
  private int bestIndent;
  
  private final int indicatorIndent;
  
  private final boolean indentWithIndicator;
  
  private int bestWidth;
  
  private final char[] bestLineBreak;
  
  private final boolean splitLines;
  
  private final int maxSimpleKeyLength;
  
  private final boolean emitComments;
  
  private Map<String, String> tagPrefixes;
  
  private String preparedAnchor;
  
  private String preparedTag;
  
  private ScalarAnalysis analysis;
  
  private DumperOptions.ScalarStyle style;
  
  private final CommentEventsCollector blockCommentsCollector;
  
  private final CommentEventsCollector inlineCommentsCollector;

  
  public Emitter(Writer stream, DumperOptions opts) {
    this.stream = stream;

    
    this.states = new ArrayStack(100);
    this.state = new ExpectStreamStart();
    
    this.events = new ArrayBlockingQueue<>(100);
    this.event = null;
    
    this.indents = new ArrayStack(10);
    this.indent = null;
    
    this.flowLevel = 0;
    
    this.mappingContext = false;
    this.simpleKeyContext = false;






    
    this.column = 0;
    this.whitespace = true;
    this.indention = true;

    
    this.openEnded = false;

    
    this.canonical = Boolean.valueOf(opts.isCanonical());
    this.prettyFlow = Boolean.valueOf(opts.isPrettyFlow());
    this.allowUnicode = opts.isAllowUnicode();
    this.bestIndent = 2;
    if (opts.getIndent() > 1 && opts.getIndent() < 10) {
      this.bestIndent = opts.getIndent();
    }
    this.indicatorIndent = opts.getIndicatorIndent();
    this.indentWithIndicator = opts.getIndentWithIndicator();
    this.bestWidth = 80;
    if (opts.getWidth() > this.bestIndent * 2) {
      this.bestWidth = opts.getWidth();
    }
    this.bestLineBreak = opts.getLineBreak().getString().toCharArray();
    this.splitLines = opts.getSplitLines();
    this.maxSimpleKeyLength = opts.getMaxSimpleKeyLength();
    this.emitComments = opts.isProcessComments();

    
    this.tagPrefixes = new LinkedHashMap<>();

    
    this.preparedAnchor = null;
    this.preparedTag = null;

    
    this.analysis = null;
    this.style = null;

    
    this.blockCommentsCollector = new CommentEventsCollector(this.events, new CommentType[] { CommentType.BLANK_LINE, CommentType.BLOCK });
    
    this.inlineCommentsCollector = new CommentEventsCollector(this.events, new CommentType[] { CommentType.IN_LINE });
  }

  
  public void emit(Event event) throws IOException {
    this.events.add(event);
    while (!needMoreEvents()) {
      this.event = this.events.poll();
      this.state.expect();
      this.event = null;
    } 
  }


  
  private boolean needMoreEvents() {
    if (this.events.isEmpty()) {
      return true;
    }
    
    Iterator<Event> iter = this.events.iterator();
    Event event = iter.next();
    while (event instanceof org.yaml.snakeyaml.events.CommentEvent) {
      if (!iter.hasNext()) {
        return true;
      }
      event = iter.next();
    } 
    
    if (event instanceof DocumentStartEvent)
      return needEvents(iter, 1); 
    if (event instanceof SequenceStartEvent)
      return needEvents(iter, 2); 
    if (event instanceof MappingStartEvent)
      return needEvents(iter, 3); 
    if (event instanceof org.yaml.snakeyaml.events.StreamStartEvent)
      return needEvents(iter, 2); 
    if (event instanceof org.yaml.snakeyaml.events.StreamEndEvent)
      return false; 
    if (this.emitComments) {
      return needEvents(iter, 1);
    }
    return false;
  }
  
  private boolean needEvents(Iterator<Event> iter, int count) {
    int level = 0;
    int actualCount = 0;
    while (iter.hasNext()) {
      Event event = iter.next();
      if (event instanceof org.yaml.snakeyaml.events.CommentEvent) {
        continue;
      }
      actualCount++;
      if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
        level++;
      } else if (event instanceof DocumentEndEvent || event instanceof org.yaml.snakeyaml.events.CollectionEndEvent) {
        level--;
      } else if (event instanceof org.yaml.snakeyaml.events.StreamEndEvent) {
        level = -1;
      } 
      if (level < 0) {
        return false;
      }
    } 
    return (actualCount < count);
  }
  
  private void increaseIndent(boolean flow, boolean indentless) {
    this.indents.push(this.indent);
    if (this.indent == null) {
      if (flow) {
        this.indent = Integer.valueOf(this.bestIndent);
      } else {
        this.indent = Integer.valueOf(0);
      } 
    } else if (!indentless) {
      Emitter emitter = this; emitter.indent = Integer.valueOf(emitter.indent.intValue() + this.bestIndent);
    } 
  }
  
  private class ExpectStreamStart
    implements EmitterState
  {
    private ExpectStreamStart() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.StreamStartEvent) {
        Emitter.this.writeStreamStart();
        Emitter.this.state = new Emitter.ExpectFirstDocumentStart();
      } else {
        throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
      } 
    }
  }
  
  private class ExpectNothing implements EmitterState {
    public void expect() throws IOException {
      throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
    }
    
    private ExpectNothing() {} }
  
  private class ExpectFirstDocumentStart implements EmitterState { private ExpectFirstDocumentStart() {}
    
    public void expect() throws IOException {
      (new Emitter.ExpectDocumentStart(true)).expect();
    } }

  
  private class ExpectDocumentStart implements EmitterState {
    private final boolean first;
    
    public ExpectDocumentStart(boolean first) {
      this.first = first;
    }
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof DocumentStartEvent) {
        DocumentStartEvent ev = (DocumentStartEvent)Emitter.this.event;
        if ((ev.getVersion() != null || ev.getTags() != null) && Emitter.this.openEnded) {
          Emitter.this.writeIndicator("...", true, false, false);
          Emitter.this.writeIndent();
        } 
        if (ev.getVersion() != null) {
          String versionText = Emitter.this.prepareVersion(ev.getVersion());
          Emitter.this.writeVersionDirective(versionText);
        } 
        Emitter.this.tagPrefixes = (Map)new LinkedHashMap<>(Emitter.DEFAULT_TAG_PREFIXES);
        if (ev.getTags() != null) {
          Set<String> handles = new TreeSet<>(ev.getTags().keySet());
          for (String handle : handles) {
            String prefix = (String)ev.getTags().get(handle);
            Emitter.this.tagPrefixes.put(prefix, handle);
            String handleText = Emitter.this.prepareTagHandle(handle);
            String prefixText = Emitter.this.prepareTagPrefix(prefix);
            Emitter.this.writeTagDirective(handleText, prefixText);
          } 
        } 


        
        boolean implicit = (this.first && !ev.getExplicit() && !Emitter.this.canonical.booleanValue() && ev.getVersion() == null && (ev.getTags() == null || ev.getTags().isEmpty()) && !Emitter.this.checkEmptyDocument());
        if (!implicit) {
          Emitter.this.writeIndent();
          Emitter.this.writeIndicator("---", true, false, false);
          if (Emitter.this.canonical.booleanValue()) {
            Emitter.this.writeIndent();
          }
        } 
        Emitter.this.state = new Emitter.ExpectDocumentRoot();
      } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.StreamEndEvent) {




        
        Emitter.this.writeStreamEnd();
        Emitter.this.state = new Emitter.ExpectNothing();
      } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
        Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
        Emitter.this.writeBlockComment();
      } else {
        
        throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
      } 
    } }
  
  private class ExpectDocumentEnd implements EmitterState { private ExpectDocumentEnd() {}
    
    public void expect() throws IOException {
      Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      Emitter.this.writeBlockComment();
      if (Emitter.this.event instanceof DocumentEndEvent) {
        Emitter.this.writeIndent();
        if (((DocumentEndEvent)Emitter.this.event).getExplicit()) {
          Emitter.this.writeIndicator("...", true, false, false);
          Emitter.this.writeIndent();
        } 
        Emitter.this.flushStream();
        Emitter.this.state = new Emitter.ExpectDocumentStart(false);
      } else {
        throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
      } 
    } }
  
  private class ExpectDocumentRoot implements EmitterState { private ExpectDocumentRoot() {}
    
    public void expect() throws IOException {
      Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      if (!Emitter.this.blockCommentsCollector.isEmpty()) {
        Emitter.this.writeBlockComment();
        if (Emitter.this.event instanceof DocumentEndEvent) {
          (new Emitter.ExpectDocumentEnd()).expect();
          return;
        } 
      } 
      Emitter.this.states.push(new Emitter.ExpectDocumentEnd());
      Emitter.this.expectNode(true, false, false);
    } }



  
  private void expectNode(boolean root, boolean mapping, boolean simpleKey) throws IOException {
    this.rootContext = root;
    this.mappingContext = mapping;
    this.simpleKeyContext = simpleKey;
    if (this.event instanceof org.yaml.snakeyaml.events.AliasEvent) {
      expectAlias();
    } else if (this.event instanceof ScalarEvent || this.event instanceof CollectionStartEvent) {
      processAnchor("&");
      processTag();
      if (this.event instanceof ScalarEvent) {
        expectScalar();
      } else if (this.event instanceof SequenceStartEvent) {
        if (this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent)this.event).isFlow() || 
          checkEmptySequence()) {
          expectFlowSequence();
        } else {
          expectBlockSequence();
        }
      
      } else if (this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent)this.event).isFlow() || 
        checkEmptyMapping()) {
        expectFlowMapping();
      } else {
        expectBlockMapping();
      } 
    } else {
      
      throw new EmitterException("expected NodeEvent, but got " + this.event);
    } 
  }
  
  private void expectAlias() throws IOException {
    if (!(this.event instanceof org.yaml.snakeyaml.events.AliasEvent)) {
      throw new EmitterException("Alias must be provided");
    }
    processAnchor("*");
    this.state = (EmitterState)this.states.pop();
  }
  
  private void expectScalar() throws IOException {
    increaseIndent(true, false);
    processScalar();
    this.indent = (Integer)this.indents.pop();
    this.state = (EmitterState)this.states.pop();
  }


  
  private void expectFlowSequence() throws IOException {
    writeIndicator("[", true, true, false);
    this.flowLevel++;
    increaseIndent(true, false);
    if (this.prettyFlow.booleanValue()) {
      writeIndent();
    }
    this.state = new ExpectFirstFlowSequenceItem();
  }
  private class ExpectFirstFlowSequenceItem implements EmitterState { private ExpectFirstFlowSequenceItem() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        Emitter.this.writeIndicator("]", false, false, false);
        Emitter.this.inlineCommentsCollector.collectEvents();
        Emitter.this.writeInlineComments();
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
        Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
        Emitter.this.writeBlockComment();
      } else {
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
          Emitter.this.writeIndent();
        }
        Emitter.this.states.push(new Emitter.ExpectFlowSequenceItem());
        Emitter.this.expectNode(false, false, false);
        Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
        Emitter.this.writeInlineComments();
      } 
    } }
  
  private class ExpectFlowSequenceItem implements EmitterState { private ExpectFlowSequenceItem() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        if (Emitter.this.canonical.booleanValue()) {
          Emitter.this.writeIndicator(",", false, false, false);
          Emitter.this.writeIndent();
        } else if (Emitter.this.prettyFlow.booleanValue()) {
          Emitter.this.writeIndent();
        } 
        Emitter.this.writeIndicator("]", false, false, false);
        Emitter.this.inlineCommentsCollector.collectEvents();
        Emitter.this.writeInlineComments();
        if (Emitter.this.prettyFlow.booleanValue()) {
          Emitter.this.writeIndent();
        }
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
        Emitter.this.event = Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
      } else {
        Emitter.this.writeIndicator(",", false, false, false);
        Emitter.this.writeBlockComment();
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
          Emitter.this.writeIndent();
        }
        Emitter.this.states.push(new ExpectFlowSequenceItem());
        Emitter.this.expectNode(false, false, false);
        Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
        Emitter.this.writeInlineComments();
      } 
    } }



  
  private void expectFlowMapping() throws IOException {
    writeIndicator("{", true, true, false);
    this.flowLevel++;
    increaseIndent(true, false);
    if (this.prettyFlow.booleanValue()) {
      writeIndent();
    }
    this.state = new ExpectFirstFlowMappingKey();
  }
  private class ExpectFirstFlowMappingKey implements EmitterState { private ExpectFirstFlowMappingKey() {}
    
    public void expect() throws IOException {
      Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      Emitter.this.writeBlockComment();
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        Emitter.this.writeIndicator("}", false, false, false);
        Emitter.this.inlineCommentsCollector.collectEvents();
        Emitter.this.writeInlineComments();
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
          Emitter.this.writeIndent();
        }
        if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
          Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
          Emitter.this.expectNode(false, true, true);
        } else {
          Emitter.this.writeIndicator("?", true, false, false);
          Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
          Emitter.this.expectNode(false, true, false);
        } 
      } 
    } }
  
  private class ExpectFlowMappingKey implements EmitterState { private ExpectFlowMappingKey() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        if (Emitter.this.canonical.booleanValue()) {
          Emitter.this.writeIndicator(",", false, false, false);
          Emitter.this.writeIndent();
        } 
        if (Emitter.this.prettyFlow.booleanValue()) {
          Emitter.this.writeIndent();
        }
        Emitter.this.writeIndicator("}", false, false, false);
        Emitter.this.inlineCommentsCollector.collectEvents();
        Emitter.this.writeInlineComments();
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        Emitter.this.writeIndicator(",", false, false, false);
        Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
        Emitter.this.writeBlockComment();
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
          Emitter.this.writeIndent();
        }
        if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
          Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
          Emitter.this.expectNode(false, true, true);
        } else {
          Emitter.this.writeIndicator("?", true, false, false);
          Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
          Emitter.this.expectNode(false, true, false);
        } 
      } 
    } }
  
  private class ExpectFlowMappingSimpleValue implements EmitterState { private ExpectFlowMappingSimpleValue() {}
    
    public void expect() throws IOException {
      Emitter.this.writeIndicator(":", false, false, false);
      Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      Emitter.this.writeInlineComments();
      Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
      Emitter.this.expectNode(false, true, false);
      Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
      Emitter.this.writeInlineComments();
    } }
  
  private class ExpectFlowMappingValue implements EmitterState { private ExpectFlowMappingValue() {}
    
    public void expect() throws IOException {
      if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow.booleanValue()) {
        Emitter.this.writeIndent();
      }
      Emitter.this.writeIndicator(":", true, false, false);
      Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      Emitter.this.writeInlineComments();
      Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
      Emitter.this.expectNode(false, true, false);
      Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
      Emitter.this.writeInlineComments();
    } }



  
  private void expectBlockSequence() throws IOException {
    boolean indentless = (this.mappingContext && !this.indention);
    increaseIndent(false, indentless);
    this.state = new ExpectFirstBlockSequenceItem();
  }
  private class ExpectFirstBlockSequenceItem implements EmitterState { private ExpectFirstBlockSequenceItem() {}
    
    public void expect() throws IOException {
      (new Emitter.ExpectBlockSequenceItem(true)).expect();
    } }

  
  private class ExpectBlockSequenceItem implements EmitterState {
    private final boolean first;
    
    public ExpectBlockSequenceItem(boolean first) {
      this.first = first;
    }
    
    public void expect() throws IOException {
      if (!this.first && Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.CommentEvent) {
        Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
      } else {
        Emitter.this.writeIndent();
        if (!Emitter.this.indentWithIndicator || this.first) {
          Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
        }
        Emitter.this.writeIndicator("-", true, false, true);
        if (Emitter.this.indentWithIndicator && this.first) {
          Emitter.this.indent = Integer.valueOf(Emitter.this.indent.intValue() + Emitter.this.indicatorIndent);
        }
        if (!Emitter.this.blockCommentsCollector.isEmpty()) {
          Emitter.this.increaseIndent(false, false);
          Emitter.this.writeBlockComment();
          if (Emitter.this.event instanceof ScalarEvent) {
            Emitter.this.analysis = Emitter.this.analyzeScalar(((ScalarEvent)Emitter.this.event).getValue());
            if (!Emitter.this.analysis.isEmpty()) {
              Emitter.this.writeIndent();
            }
          } 
          Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        } 
        Emitter.this.states.push(new ExpectBlockSequenceItem(false));
        Emitter.this.expectNode(false, false, false);
        Emitter.this.inlineCommentsCollector.collectEvents();
        Emitter.this.writeInlineComments();
      } 
    }
  }

  
  private void expectBlockMapping() throws IOException {
    increaseIndent(false, false);
    this.state = new ExpectFirstBlockMappingKey();
  }
  private class ExpectFirstBlockMappingKey implements EmitterState { private ExpectFirstBlockMappingKey() {}
    
    public void expect() throws IOException {
      (new Emitter.ExpectBlockMappingKey(true)).expect();
    } }

  
  private class ExpectBlockMappingKey implements EmitterState {
    private final boolean first;
    
    public ExpectBlockMappingKey(boolean first) {
      this.first = first;
    }
    
    public void expect() throws IOException {
      Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      Emitter.this.writeBlockComment();
      if (!this.first && Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        Emitter.this.writeIndent();
        if (Emitter.this.checkSimpleKey()) {
          Emitter.this.states.push(new Emitter.ExpectBlockMappingSimpleValue());
          Emitter.this.expectNode(false, true, true);
        } else {
          Emitter.this.writeIndicator("?", true, false, true);
          Emitter.this.states.push(new Emitter.ExpectBlockMappingValue());
          Emitter.this.expectNode(false, true, false);
        } 
      } 
    }
  }
  
  private boolean isFoldedOrLiteral(Event event) {
    if (!event.is(Event.ID.Scalar)) {
      return false;
    }
    ScalarEvent scalarEvent = (ScalarEvent)event;
    DumperOptions.ScalarStyle style = scalarEvent.getScalarStyle();
    return (style == DumperOptions.ScalarStyle.FOLDED || style == DumperOptions.ScalarStyle.LITERAL);
  }
  private class ExpectBlockMappingSimpleValue implements EmitterState { private ExpectBlockMappingSimpleValue() {}
    
    public void expect() throws IOException {
      Emitter.this.writeIndicator(":", false, false, false);
      Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      if (!Emitter.this.isFoldedOrLiteral(Emitter.this.event) && 
        Emitter.this.writeInlineComments()) {
        Emitter.this.increaseIndent(true, false);
        Emitter.this.writeIndent();
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
      } 
      
      Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      if (!Emitter.this.blockCommentsCollector.isEmpty()) {
        Emitter.this.increaseIndent(true, false);
        Emitter.this.writeBlockComment();
        Emitter.this.writeIndent();
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
      } 
      Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
      Emitter.this.expectNode(false, true, false);
      Emitter.this.inlineCommentsCollector.collectEvents();
      Emitter.this.writeInlineComments();
    } }
  
  private class ExpectBlockMappingValue implements EmitterState { private ExpectBlockMappingValue() {}
    
    public void expect() throws IOException {
      Emitter.this.writeIndent();
      Emitter.this.writeIndicator(":", true, false, true);
      Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      Emitter.this.writeInlineComments();
      Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
      Emitter.this.writeBlockComment();
      Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
      Emitter.this.expectNode(false, true, false);
      Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
      Emitter.this.writeInlineComments();
    } }



  
  private boolean checkEmptySequence() {
    return (this.event instanceof SequenceStartEvent && !this.events.isEmpty() && this.events.peek() instanceof org.yaml.snakeyaml.events.SequenceEndEvent);
  }
  
  private boolean checkEmptyMapping() {
    return (this.event instanceof MappingStartEvent && !this.events.isEmpty() && this.events.peek() instanceof org.yaml.snakeyaml.events.MappingEndEvent);
  }
  
  private boolean checkEmptyDocument() {
    if (!(this.event instanceof DocumentStartEvent) || this.events.isEmpty()) {
      return false;
    }
    Event event = this.events.peek();
    if (event instanceof ScalarEvent) {
      ScalarEvent e = (ScalarEvent)event;
      return (e.getAnchor() == null && e.getTag() == null && e.getImplicit() != null && e
        .getValue().length() == 0);
    } 
    return false;
  }
  
  private boolean checkSimpleKey() {
    int length = 0;
    if (this.event instanceof NodeEvent && ((NodeEvent)this.event).getAnchor() != null) {
      if (this.preparedAnchor == null) {
        this.preparedAnchor = prepareAnchor(((NodeEvent)this.event).getAnchor());
      }
      length += this.preparedAnchor.length();
    } 
    String tag = null;
    if (this.event instanceof ScalarEvent) {
      tag = ((ScalarEvent)this.event).getTag();
    } else if (this.event instanceof CollectionStartEvent) {
      tag = ((CollectionStartEvent)this.event).getTag();
    } 
    if (tag != null) {
      if (this.preparedTag == null) {
        this.preparedTag = prepareTag(tag);
      }
      length += this.preparedTag.length();
    } 
    if (this.event instanceof ScalarEvent) {
      if (this.analysis == null) {
        this.analysis = analyzeScalar(((ScalarEvent)this.event).getValue());
      }
      length += this.analysis.getScalar().length();
    } 
    return (length < this.maxSimpleKeyLength && (this.event instanceof org.yaml.snakeyaml.events.AliasEvent || (this.event instanceof ScalarEvent && 
      !this.analysis.isEmpty() && !this.analysis.isMultiline()) || 
      checkEmptySequence() || checkEmptyMapping()));
  }


  
  private void processAnchor(String indicator) throws IOException {
    NodeEvent ev = (NodeEvent)this.event;
    if (ev.getAnchor() == null) {
      this.preparedAnchor = null;
      return;
    } 
    if (this.preparedAnchor == null) {
      this.preparedAnchor = prepareAnchor(ev.getAnchor());
    }
    writeIndicator(indicator + this.preparedAnchor, true, false, false);
    this.preparedAnchor = null;
  }
  
  private void processTag() throws IOException {
    String tag = null;
    if (this.event instanceof ScalarEvent) {
      ScalarEvent ev = (ScalarEvent)this.event;
      tag = ev.getTag();
      if (this.style == null) {
        this.style = chooseScalarStyle();
      }
      if ((!this.canonical.booleanValue() || tag == null) && ((this.style == null && ev.getImplicit()
        .canOmitTagInPlainScalar()) || (this.style != null && ev.getImplicit()
        .canOmitTagInNonPlainScalar()))) {
        this.preparedTag = null;
        return;
      } 
      if (ev.getImplicit().canOmitTagInPlainScalar() && tag == null) {
        tag = "!";
        this.preparedTag = null;
      } 
    } else {
      CollectionStartEvent ev = (CollectionStartEvent)this.event;
      tag = ev.getTag();
      if ((!this.canonical.booleanValue() || tag == null) && ev.getImplicit()) {
        this.preparedTag = null;
        return;
      } 
    } 
    if (tag == null) {
      throw new EmitterException("tag is not specified");
    }
    if (this.preparedTag == null) {
      this.preparedTag = prepareTag(tag);
    }
    writeIndicator(this.preparedTag, true, false, false);
    this.preparedTag = null;
  }
  
  private DumperOptions.ScalarStyle chooseScalarStyle() {
    ScalarEvent ev = (ScalarEvent)this.event;
    if (this.analysis == null) {
      this.analysis = analyzeScalar(ev.getValue());
    }
    if ((!ev.isPlain() && ev.getScalarStyle() == DumperOptions.ScalarStyle.DOUBLE_QUOTED) || this.canonical.booleanValue()) {
      return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
    }
    if (ev.isPlain() && ev.getImplicit().canOmitTagInPlainScalar() && (
      !this.simpleKeyContext || (!this.analysis.isEmpty() && !this.analysis.isMultiline())) && ((this.flowLevel != 0 && this.analysis
      .isAllowFlowPlain()) || (this.flowLevel == 0 && this.analysis.isAllowBlockPlain()))) {
      return null;
    }
    
    if (!ev.isPlain() && (ev.getScalarStyle() == DumperOptions.ScalarStyle.LITERAL || ev.getScalarStyle() == DumperOptions.ScalarStyle.FOLDED) && 
      this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.isAllowBlock()) {
      return ev.getScalarStyle();
    }
    
    if ((ev.isPlain() || ev.getScalarStyle() == DumperOptions.ScalarStyle.SINGLE_QUOTED) && 
      this.analysis.isAllowSingleQuoted() && (!this.simpleKeyContext || !this.analysis.isMultiline())) {
      return DumperOptions.ScalarStyle.SINGLE_QUOTED;
    }
    
    return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
  }
  
  private void processScalar() throws IOException {
    ScalarEvent ev = (ScalarEvent)this.event;
    if (this.analysis == null) {
      this.analysis = analyzeScalar(ev.getValue());
    }
    if (this.style == null) {
      this.style = chooseScalarStyle();
    }
    boolean split = (!this.simpleKeyContext && this.splitLines);
    if (this.style == null) {
      writePlain(this.analysis.getScalar(), split);
    } else {
      switch (this.style) {
        case DOUBLE_QUOTED:
          writeDoubleQuoted(this.analysis.getScalar(), split);
          break;
        case SINGLE_QUOTED:
          writeSingleQuoted(this.analysis.getScalar(), split);
          break;
        case FOLDED:
          writeFolded(this.analysis.getScalar(), split);
          break;
        case LITERAL:
          writeLiteral(this.analysis.getScalar());
          break;
        default:
          throw new YAMLException("Unexpected style: " + this.style);
      } 
    } 
    this.analysis = null;
    this.style = null;
  }


  
  private String prepareVersion(DumperOptions.Version version) {
    if (version.major() != 1) {
      throw new EmitterException("unsupported YAML version: " + version);
    }
    return version.getRepresentation();
  }
  
  private static final Pattern HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
  
  private String prepareTagHandle(String handle) {
    if (handle.length() == 0)
      throw new EmitterException("tag handle must not be empty"); 
    if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!')
      throw new EmitterException("tag handle must start and end with '!': " + handle); 
    if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches()) {
      throw new EmitterException("invalid character in the tag handle: " + handle);
    }
    return handle;
  }
  
  private String prepareTagPrefix(String prefix) {
    if (prefix.length() == 0) {
      throw new EmitterException("tag prefix must not be empty");
    }
    StringBuilder chunks = new StringBuilder();
    int start = 0;
    int end = 0;
    if (prefix.charAt(0) == '!') {
      end = 1;
    }
    while (end < prefix.length()) {
      end++;
    }
    if (start < end) {
      chunks.append(prefix, start, end);
    }
    return chunks.toString();
  }
  
  private String prepareTag(String tag) {
    if (tag.length() == 0) {
      throw new EmitterException("tag must not be empty");
    }
    if ("!".equals(tag)) {
      return tag;
    }
    String handle = null;
    String suffix = tag;
    
    for (String prefix : this.tagPrefixes.keySet()) {
      if (tag.startsWith(prefix) && ("!".equals(prefix) || prefix.length() < tag.length())) {
        handle = prefix;
      }
    } 
    if (handle != null) {
      suffix = tag.substring(handle.length());
      handle = this.tagPrefixes.get(handle);
    } 
    
    int end = suffix.length();
    String suffixText = (end > 0) ? suffix.substring(0, end) : "";
    
    if (handle != null) {
      return handle + suffixText;
    }
    return "!<" + suffixText + ">";
  }
  
  static String prepareAnchor(String anchor) {
    if (anchor.length() == 0) {
      throw new EmitterException("anchor must not be empty");
    }
    for (Character invalid : INVALID_ANCHOR) {
      if (anchor.indexOf(invalid.charValue()) > -1) {
        throw new EmitterException("Invalid character '" + invalid + "' in the anchor: " + anchor);
      }
    } 
    Matcher matcher = SPACES_PATTERN.matcher(anchor);
    if (matcher.find()) {
      throw new EmitterException("Anchor may not contain spaces: " + anchor);
    }
    return anchor;
  }

  
  private ScalarAnalysis analyzeScalar(String scalar) {
    if (scalar.length() == 0) {
      return new ScalarAnalysis(scalar, true, false, false, true, true, false);
    }
    
    boolean blockIndicators = false;
    boolean flowIndicators = false;
    boolean lineBreaks = false;
    boolean specialCharacters = false;

    
    boolean leadingSpace = false;
    boolean leadingBreak = false;
    boolean trailingSpace = false;
    boolean trailingBreak = false;
    boolean breakSpace = false;
    boolean spaceBreak = false;

    
    if (scalar.startsWith("---") || scalar.startsWith("...")) {
      blockIndicators = true;
      flowIndicators = true;
    } 
    
    boolean preceededByWhitespace = true;
    boolean followedByWhitespace = (scalar.length() == 1 || Constant.NULL_BL_T_LINEBR.has(scalar.codePointAt(1)));
    
    boolean previousSpace = false;

    
    boolean previousBreak = false;
    
    int index = 0;
    
    while (index < scalar.length()) {
      int c = scalar.codePointAt(index);
      
      if (index == 0) {
        
        if ("#,[]{}&*!|>'\"%@`".indexOf(c) != -1) {
          flowIndicators = true;
          blockIndicators = true;
        } 
        if (c == 63 || c == 58) {
          flowIndicators = true;
          if (followedByWhitespace) {
            blockIndicators = true;
          }
        } 
        if (c == 45 && followedByWhitespace) {
          flowIndicators = true;
          blockIndicators = true;
        } 
      } else {
        
        if (",?[]{}".indexOf(c) != -1) {
          flowIndicators = true;
        }
        if (c == 58) {
          flowIndicators = true;
          if (followedByWhitespace) {
            blockIndicators = true;
          }
        } 
        if (c == 35 && preceededByWhitespace) {
          flowIndicators = true;
          blockIndicators = true;
        } 
      } 
      
      boolean isLineBreak = Constant.LINEBR.has(c);
      if (isLineBreak) {
        lineBreaks = true;
      }
      if (c != 10 && (32 > c || c > 126)) {
        if (c == 133 || (c >= 160 && c <= 55295) || (c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111)) {


          
          if (!this.allowUnicode) {
            specialCharacters = true;
          }
        } else {
          specialCharacters = true;
        } 
      }
      
      if (c == 32) {
        if (index == 0) {
          leadingSpace = true;
        }
        if (index == scalar.length() - 1) {
          trailingSpace = true;
        }
        if (previousBreak) {
          breakSpace = true;
        }
        previousSpace = true;
        previousBreak = false;
      } else if (isLineBreak) {
        if (index == 0) {
          leadingBreak = true;
        }
        if (index == scalar.length() - 1) {
          trailingBreak = true;
        }
        if (previousSpace) {
          spaceBreak = true;
        }
        previousSpace = false;
        previousBreak = true;
      } else {
        previousSpace = false;
        previousBreak = false;
      } 

      
      index += Character.charCount(c);
      preceededByWhitespace = (Constant.NULL_BL_T.has(c) || isLineBreak);
      followedByWhitespace = true;
      if (index + 1 < scalar.length()) {
        int nextIndex = index + Character.charCount(scalar.codePointAt(index));
        if (nextIndex < scalar.length()) {
          followedByWhitespace = (Constant.NULL_BL_T.has(scalar.codePointAt(nextIndex)) || isLineBreak);
        }
      } 
    } 
    
    boolean allowFlowPlain = true;
    boolean allowBlockPlain = true;
    boolean allowSingleQuoted = true;
    boolean allowBlock = true;
    
    if (leadingSpace || leadingBreak || trailingSpace || trailingBreak) {
      allowFlowPlain = allowBlockPlain = false;
    }
    
    if (trailingSpace) {
      allowBlock = false;
    }

    
    if (breakSpace) {
      allowFlowPlain = allowBlockPlain = allowSingleQuoted = false;
    }

    
    if (spaceBreak || specialCharacters) {
      allowFlowPlain = allowBlockPlain = allowSingleQuoted = allowBlock = false;
    }

    
    if (lineBreaks) {
      allowFlowPlain = false;
    }
    
    if (flowIndicators) {
      allowFlowPlain = false;
    }
    
    if (blockIndicators) {
      allowBlockPlain = false;
    }
    
    return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowBlock);
  }



  
  void flushStream() throws IOException {
    this.stream.flush();
  }

  
  void writeStreamStart() {}

  
  void writeStreamEnd() throws IOException {
    flushStream();
  }

  
  void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) throws IOException {
    if (!this.whitespace && needWhitespace) {
      this.column++;
      this.stream.write(SPACE);
    } 
    this.whitespace = whitespace;
    this.indention = (this.indention && indentation);
    this.column += indicator.length();
    this.openEnded = false;
    this.stream.write(indicator);
  }
  
  void writeIndent() throws IOException {
    int indent;
    if (this.indent != null) {
      indent = this.indent.intValue();
    } else {
      indent = 0;
    } 
    
    if (!this.indention || this.column > indent || (this.column == indent && !this.whitespace)) {
      writeLineBreak(null);
    }
    
    writeWhitespace(indent - this.column);
  }
  
  private void writeWhitespace(int length) throws IOException {
    if (length <= 0) {
      return;
    }
    this.whitespace = true;
    char[] data = new char[length];
    for (int i = 0; i < data.length; i++) {
      data[i] = ' ';
    }
    this.column += length;
    this.stream.write(data);
  }
  
  private void writeLineBreak(String data) throws IOException {
    this.whitespace = true;
    this.indention = true;
    this.column = 0;
    if (data == null) {
      this.stream.write(this.bestLineBreak);
    } else {
      this.stream.write(data);
    } 
  }
  
  void writeVersionDirective(String versionText) throws IOException {
    this.stream.write("%YAML ");
    this.stream.write(versionText);
    writeLineBreak(null);
  }


  
  void writeTagDirective(String handleText, String prefixText) throws IOException {
    this.stream.write("%TAG ");
    this.stream.write(handleText);
    this.stream.write(SPACE);
    this.stream.write(prefixText);
    writeLineBreak(null);
  }

  
  private void writeSingleQuoted(String text, boolean split) throws IOException {
    writeIndicator("'", true, false, false);
    boolean spaces = false;
    boolean breaks = false;
    int start = 0, end = 0;
    
    while (end <= text.length()) {
      char ch = Character.MIN_VALUE;
      if (end < text.length()) {
        ch = text.charAt(end);
      }
      if (spaces) {
        if (ch == '\000' || ch != ' ') {
          if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text
            .length()) {
            writeIndent();
          } else {
            int len = end - start;
            this.column += len;
            this.stream.write(text, start, len);
          } 
          start = end;
        } 
      } else if (breaks) {
        if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
          if (text.charAt(start) == '\n') {
            writeLineBreak(null);
          }
          String data = text.substring(start, end);
          for (char br : data.toCharArray()) {
            if (br == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(br));
            } 
          } 
          writeIndent();
          start = end;
        }
      
      } else if (Constant.LINEBR.has(ch, "\000 '") && 
        start < end) {
        int len = end - start;
        this.column += len;
        this.stream.write(text, start, len);
        start = end;
      } 

      
      if (ch == '\'') {
        this.column += 2;
        this.stream.write("''");
        start = end + 1;
      } 
      if (ch != '\000') {
        spaces = (ch == ' ');
        breaks = Constant.LINEBR.has(ch);
      } 
      end++;
    } 
    writeIndicator("'", false, false, false);
  }
  
  private void writeDoubleQuoted(String text, boolean split) throws IOException {
    writeIndicator("\"", true, false, false);
    int start = 0;
    int end = 0;
    while (end <= text.length()) {
      Character ch = null;
      if (end < text.length()) {
        ch = Character.valueOf(text.charAt(end));
      }
      if (ch == null || "\"\\  ﻿".indexOf(ch.charValue()) != -1 || ' ' > ch
        .charValue() || ch.charValue() > '~') {
        if (start < end) {
          int len = end - start;
          this.column += len;
          this.stream.write(text, start, len);
          start = end;
        } 
        if (ch != null) {
          String data;
          if (ESCAPE_REPLACEMENTS.containsKey(ch)) {
            data = "\\" + (String)ESCAPE_REPLACEMENTS.get(ch);
          } else if (!this.allowUnicode || !StreamReader.isPrintable(ch.charValue())) {

            
            if (ch.charValue() <= 'ÿ') {
              String s = "0" + Integer.toString(ch.charValue(), 16);
              data = "\\x" + s.substring(s.length() - 2);
            } else if (ch.charValue() >= '?' && ch.charValue() <= '?') {
              if (end + 1 < text.length()) {
                Character ch2 = Character.valueOf(text.charAt(++end));
                String s = "000" + Long.toHexString(Character.toCodePoint(ch.charValue(), ch2.charValue()));
                data = "\\U" + s.substring(s.length() - 8);
              } else {
                String s = "000" + Integer.toString(ch.charValue(), 16);
                data = "\\u" + s.substring(s.length() - 4);
              } 
            } else {
              String s = "000" + Integer.toString(ch.charValue(), 16);
              data = "\\u" + s.substring(s.length() - 4);
            } 
          } else {
            data = String.valueOf(ch);
          } 
          this.column += data.length();
          this.stream.write(data);
          start = end + 1;
        } 
      } 
      if (0 < end && end < text.length() - 1 && (ch.charValue() == ' ' || start >= end) && this.column + end - start > this.bestWidth && split) {
        String data;
        
        if (start >= end) {
          data = "\\";
        } else {
          data = text.substring(start, end) + "\\";
        } 
        if (start < end) {
          start = end;
        }
        this.column += data.length();
        this.stream.write(data);
        writeIndent();
        this.whitespace = false;
        this.indention = false;
        if (text.charAt(start) == ' ') {
          data = "\\";
          this.column += data.length();
          this.stream.write(data);
        } 
      } 
      end++;
    } 
    writeIndicator("\"", false, false, false);
  }
  
  private boolean writeCommentLines(List<CommentLine> commentLines) throws IOException {
    boolean wroteComment = false;
    if (this.emitComments) {
      int indentColumns = 0;
      boolean firstComment = true;
      for (CommentLine commentLine : commentLines) {
        if (commentLine.getCommentType() != CommentType.BLANK_LINE) {
          if (firstComment) {
            firstComment = false;
            writeIndicator("#", (commentLine.getCommentType() == CommentType.IN_LINE), false, false);
            indentColumns = (this.column > 0) ? (this.column - 1) : 0;
          } else {
            writeWhitespace(indentColumns);
            writeIndicator("#", false, false, false);
          } 
          this.stream.write(commentLine.getValue());
          writeLineBreak(null);
        } else {
          writeLineBreak(null);
          writeIndent();
        } 
        wroteComment = true;
      } 
    } 
    return wroteComment;
  }
  
  private void writeBlockComment() throws IOException {
    if (!this.blockCommentsCollector.isEmpty()) {
      writeIndent();
      writeCommentLines(this.blockCommentsCollector.consume());
    } 
  }
  
  private boolean writeInlineComments() throws IOException {
    return writeCommentLines(this.inlineCommentsCollector.consume());
  }
  
  private String determineBlockHints(String text) {
    StringBuilder hints = new StringBuilder();
    if (Constant.LINEBR.has(text.charAt(0), " ")) {
      hints.append(this.bestIndent);
    }
    char ch1 = text.charAt(text.length() - 1);
    if (Constant.LINEBR.hasNo(ch1)) {
      hints.append("-");
    } else if (text.length() == 1 || Constant.LINEBR.has(text.charAt(text.length() - 2))) {
      hints.append("+");
    } 
    return hints.toString();
  }
  
  void writeFolded(String text, boolean split) throws IOException {
    String hints = determineBlockHints(text);
    writeIndicator(">" + hints, true, false, false);
    if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
      this.openEnded = true;
    }
    if (!writeInlineComments()) {
      writeLineBreak(null);
    }
    boolean leadingSpace = true;
    boolean spaces = false;
    boolean breaks = true;
    int start = 0, end = 0;
    while (end <= text.length()) {
      char ch = Character.MIN_VALUE;
      if (end < text.length()) {
        ch = text.charAt(end);
      }
      if (breaks) {
        if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
          if (!leadingSpace && ch != '\000' && ch != ' ' && text.charAt(start) == '\n') {
            writeLineBreak(null);
          }
          leadingSpace = (ch == ' ');
          String data = text.substring(start, end);
          for (char br : data.toCharArray()) {
            if (br == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(br));
            } 
          } 
          if (ch != '\000') {
            writeIndent();
          }
          start = end;
        } 
      } else if (spaces) {
        if (ch != ' ') {
          if (start + 1 == end && this.column > this.bestWidth && split) {
            writeIndent();
          } else {
            int len = end - start;
            this.column += len;
            this.stream.write(text, start, len);
          } 
          start = end;
        }
      
      } else if (Constant.LINEBR.has(ch, "\000 ")) {
        int len = end - start;
        this.column += len;
        this.stream.write(text, start, len);
        if (ch == '\000') {
          writeLineBreak(null);
        }
        start = end;
      } 
      
      if (ch != '\000') {
        breaks = Constant.LINEBR.has(ch);
        spaces = (ch == ' ');
      } 
      end++;
    } 
  }
  
  void writeLiteral(String text) throws IOException {
    String hints = determineBlockHints(text);
    writeIndicator("|" + hints, true, false, false);
    if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
      this.openEnded = true;
    }
    if (!writeInlineComments()) {
      writeLineBreak(null);
    }
    boolean breaks = true;
    int start = 0, end = 0;
    while (end <= text.length()) {
      char ch = Character.MIN_VALUE;
      if (end < text.length()) {
        ch = text.charAt(end);
      }
      if (breaks) {
        if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
          String data = text.substring(start, end);
          for (char br : data.toCharArray()) {
            if (br == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(br));
            } 
          } 
          if (ch != '\000') {
            writeIndent();
          }
          start = end;
        }
      
      } else if (ch == '\000' || Constant.LINEBR.has(ch)) {
        this.stream.write(text, start, end - start);
        if (ch == '\000') {
          writeLineBreak(null);
        }
        start = end;
      } 
      
      if (ch != '\000') {
        breaks = Constant.LINEBR.has(ch);
      }
      end++;
    } 
  }
  
  void writePlain(String text, boolean split) throws IOException {
    if (this.rootContext) {
      this.openEnded = true;
    }
    if (text.length() == 0) {
      return;
    }
    if (!this.whitespace) {
      this.column++;
      this.stream.write(SPACE);
    } 
    this.whitespace = false;
    this.indention = false;
    boolean spaces = false;
    boolean breaks = false;
    int start = 0, end = 0;
    while (end <= text.length()) {
      char ch = Character.MIN_VALUE;
      if (end < text.length()) {
        ch = text.charAt(end);
      }
      if (spaces) {
        if (ch != ' ') {
          if (start + 1 == end && this.column > this.bestWidth && split) {
            writeIndent();
            this.whitespace = false;
            this.indention = false;
          } else {
            int len = end - start;
            this.column += len;
            this.stream.write(text, start, len);
          } 
          start = end;
        } 
      } else if (breaks) {
        if (Constant.LINEBR.hasNo(ch)) {
          if (text.charAt(start) == '\n') {
            writeLineBreak(null);
          }
          String data = text.substring(start, end);
          for (char br : data.toCharArray()) {
            if (br == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(br));
            } 
          } 
          writeIndent();
          this.whitespace = false;
          this.indention = false;
          start = end;
        }
      
      } else if (Constant.LINEBR.has(ch, "\000 ")) {
        int len = end - start;
        this.column += len;
        this.stream.write(text, start, len);
        start = end;
      } 
      
      if (ch != '\000') {
        spaces = (ch == ' ');
        breaks = Constant.LINEBR.has(ch);
      } 
      end++;
    } 
  }
}
