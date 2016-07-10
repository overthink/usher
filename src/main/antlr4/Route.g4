grammar Route;

// --------------------------------------------------------------------------
// Parser rules (start with lowercase)
// --------------------------------------------------------------------------

route
  : (literal | param | wildcard)+
  ;

literal
  : PathFragment
  ;

inlineRegex
  : InlineRe
  ;

param
  : Param inlineRegex?
  ;

wildcard
  : Star
  ;

// --------------------------------------------------------------------------
// Lexer rules (start with uppercase)
// --------------------------------------------------------------------------

Colon:  ':';
Star:   '*';
LBrace: '{';
RBrace: '}';

fragment Hex
  : [a-fA-F0-9]
  ;

fragment PctEnc
  : '%' Hex Hex
  ;

// Woefully incomplete, no unicode, but good enough for simple useful cases like \d+
fragment ReChar
  : [\\a-zA-Z0-9+*.~\[\]()$^&?]+
  ;

InlineRe
  : LBrace ReChar+ RBrace { setText(getText().substring(1, getText().length() - 1)); } // trim braces
  ;

// via Java8 grammar https://github.com/antlr/grammars-v4/blob/5315a661fe0e18164ae69335c2acba13288ec910/java8/Java8.g4#L1749
fragment IdLetterOrDigit
  : [a-zA-Z0-9_.-] // these are the "java letters or digits" below 0x7F
  | // covers all characters above 0x7F which are not a surrogate
    ~[\u0000-\u007F\uD800-\uDBFF]
    {Character.isJavaIdentifierPart(_input.LA(-1))}?
  | // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    [\uD800-\uDBFF] [\uDC00-\uDFFF]
    {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
  ;

Identifier
  : IdLetterOrDigit+
  ;

// Allowed in URL path component (after domain, before query). Vaguely based
// on RFC-3986, definitely not correct, but good enough.
fragment PathChar
  : [a-zA-Z0-9-._~/]
  | PctEnc
  ;

PathFragment
  : PathChar+
  ;

Param
  : Colon Identifier { setText(getText().substring(1)); } // trim leading ':'
  ;
