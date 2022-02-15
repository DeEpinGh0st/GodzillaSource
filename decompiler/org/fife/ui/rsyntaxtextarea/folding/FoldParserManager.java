package org.fife.ui.rsyntaxtextarea.folding;

import java.util.HashMap;
import java.util.Map;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


























public final class FoldParserManager
  implements SyntaxConstants
{
  private static final FoldParserManager INSTANCE = new FoldParserManager();





  
  private Map<String, FoldParser> foldParserMap = createFoldParserMap();














  
  public void addFoldParserMapping(String syntaxStyle, FoldParser parser) {
    this.foldParserMap.put(syntaxStyle, parser);
  }







  
  private Map<String, FoldParser> createFoldParserMap() {
    Map<String, FoldParser> map = new HashMap<>();
    
    map.put("text/actionscript", new CurlyFoldParser());
    map.put("text/asm6502", new LinesWithContentFoldParser());
    map.put("text/asm", new LinesWithContentFoldParser());
    map.put("text/c", new CurlyFoldParser());
    map.put("text/cpp", new CurlyFoldParser());
    map.put("text/cs", new CurlyFoldParser());
    map.put("text/clojure", new LispFoldParser());
    map.put("text/css", new CurlyFoldParser());
    map.put("text/d", new CurlyFoldParser());
    map.put("text/dart", new CurlyFoldParser());
    map.put("text/golang", new CurlyFoldParser());
    map.put("text/groovy", new CurlyFoldParser());
    map.put("text/htaccess", new XmlFoldParser());
    map.put("text/html", new HtmlFoldParser(-1));
    map.put("text/java", new CurlyFoldParser(true, true));
    map.put("text/javascript", new CurlyFoldParser());
    map.put("text/json", new JsonFoldParser());
    map.put("text/jshintrc", new JsonFoldParser());
    map.put("text/jsp", new HtmlFoldParser(1));
    map.put("text/kotlin", new CurlyFoldParser(true, true));
    map.put("text/latex", new LatexFoldParser());
    map.put("text/less", new CurlyFoldParser());
    map.put("text/lisp", new LispFoldParser());
    map.put("text/mxml", new XmlFoldParser());
    map.put("text/nsis", new NsisFoldParser());
    map.put("text/perl", new CurlyFoldParser());
    map.put("text/php", new HtmlFoldParser(0));
    map.put("text/python", new PythonFoldParser());
    map.put("text/scala", new CurlyFoldParser());
    map.put("text/typescript", new CurlyFoldParser());
    map.put("text/xml", new XmlFoldParser());
    map.put("text/yaml", new YamlFoldParser());
    
    return map;
  }







  
  public static FoldParserManager get() {
    return INSTANCE;
  }










  
  public FoldParser getFoldParser(String syntaxStyle) {
    return this.foldParserMap.get(syntaxStyle);
  }
}
