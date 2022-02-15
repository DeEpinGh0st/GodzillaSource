package org.fife.ui.rsyntaxtextarea;




















class DefaultTokenMakerFactory
  extends AbstractTokenMakerFactory
  implements SyntaxConstants
{
  protected void initTokenMakerMap() {
    String pkg = "org.fife.ui.rsyntaxtextarea.modes.";
    
    putMapping("text/plain", pkg + "PlainTextTokenMaker");
    putMapping("text/actionscript", pkg + "ActionScriptTokenMaker");
    putMapping("text/asm", pkg + "AssemblerX86TokenMaker");
    putMapping("text/asm6502", pkg + "Assembler6502TokenMaker");
    putMapping("text/bbcode", pkg + "BBCodeTokenMaker");
    putMapping("text/c", pkg + "CTokenMaker");
    putMapping("text/clojure", pkg + "ClojureTokenMaker");
    putMapping("text/cpp", pkg + "CPlusPlusTokenMaker");
    putMapping("text/cs", pkg + "CSharpTokenMaker");
    putMapping("text/css", pkg + "CSSTokenMaker");
    putMapping("text/csv", pkg + "CsvTokenMaker");
    putMapping("text/d", pkg + "DTokenMaker");
    putMapping("text/dart", pkg + "DartTokenMaker");
    putMapping("text/delphi", pkg + "DelphiTokenMaker");
    putMapping("text/dockerfile", pkg + "DockerTokenMaker");
    putMapping("text/dtd", pkg + "DtdTokenMaker");
    putMapping("text/fortran", pkg + "FortranTokenMaker");
    putMapping("text/golang", pkg + "GoTokenMaker");
    putMapping("text/groovy", pkg + "GroovyTokenMaker");
    putMapping("text/hosts", pkg + "HostsTokenMaker");
    putMapping("text/htaccess", pkg + "HtaccessTokenMaker");
    putMapping("text/html", pkg + "HTMLTokenMaker");
    putMapping("text/ini", pkg + "IniTokenMaker");
    putMapping("text/java", pkg + "JavaTokenMaker");
    putMapping("text/javascript", pkg + "JavaScriptTokenMaker");
    putMapping("text/jshintrc", pkg + "JshintrcTokenMaker");
    putMapping("text/json", pkg + "JsonTokenMaker");
    putMapping("text/jsp", pkg + "JSPTokenMaker");
    putMapping("text/kotlin", pkg + "KotlinTokenMaker");
    putMapping("text/latex", pkg + "LatexTokenMaker");
    putMapping("text/less", pkg + "LessTokenMaker");
    putMapping("text/lisp", pkg + "LispTokenMaker");
    putMapping("text/lua", pkg + "LuaTokenMaker");
    putMapping("text/makefile", pkg + "MakefileTokenMaker");
    putMapping("text/markdown", pkg + "MarkdownTokenMaker");
    putMapping("text/mxml", pkg + "MxmlTokenMaker");
    putMapping("text/nsis", pkg + "NSISTokenMaker");
    putMapping("text/perl", pkg + "PerlTokenMaker");
    putMapping("text/php", pkg + "PHPTokenMaker");
    putMapping("text/properties", pkg + "PropertiesFileTokenMaker");
    putMapping("text/python", pkg + "PythonTokenMaker");
    putMapping("text/ruby", pkg + "RubyTokenMaker");
    putMapping("text/sas", pkg + "SASTokenMaker");
    putMapping("text/scala", pkg + "ScalaTokenMaker");
    putMapping("text/sql", pkg + "SQLTokenMaker");
    putMapping("text/tcl", pkg + "TclTokenMaker");
    putMapping("text/typescript", pkg + "TypeScriptTokenMaker");
    putMapping("text/unix", pkg + "UnixShellTokenMaker");
    putMapping("text/vb", pkg + "VisualBasicTokenMaker");
    putMapping("text/bat", pkg + "WindowsBatchTokenMaker");
    putMapping("text/xml", pkg + "XMLTokenMaker");
    putMapping("text/yaml", pkg + "YamlTokenMaker");
  }
}
