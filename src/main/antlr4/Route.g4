grammar Route;

// --------------------------------------------------------------------------
// Parser rules (start with lowercase)
// --------------------------------------------------------------------------

route
  : (literal | param | wildcard)+
  ;

literal
  : PATH_FRAGMENT
  ;

inlineRegex
  : '{' INLINE_RE '}'
  ;

param
  : PARAM inlineRegex?
  ;

wildcard
  : '*'
  ;

// --------------------------------------------------------------------------
// Lexer rules (start with uppercase)
// --------------------------------------------------------------------------

fragment HEX
  : [a-fA-F0-9]
  ;

fragment PCT_ENC
  : '%' HEX HEX
  ;

// Woefully incomplete, no unicode, but good enough for simple useful cases like \d+
INLINE_RE
  : [\\a-zA-Z0-9+*.~\[\]()$^&?]+
  ;

// Allowed in URL path component (after domain, before query). Vaguely based
// on RFC-3986, definitely not correct, but good enough.
PATH_FRAGMENT
  : ([a-zA-Z0-9-._~/] | PCT_ENC)+
  ;

fragment LATIN1_SUPP
  : '\u00A0' .. '\u00FF'
  ;

// Arbitrarily support some high ASCII chars.  I'd like to support all
// printable, non-whitespace unicode, but I haven't figured out how to do that
// yet.
IDENTIFIER
  : ([a-zA-Z0-9-_.] | LATIN1_SUPP)+
  ;

// TODO: pretty sure this shouldn't be a lexer rule
PARAM
  : ':' IDENTIFIER { setText(getText().substring(1)); } // trim leading ':'
  ;
