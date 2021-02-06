grammar Declarations;

@header {
from parse_tree import *
}

cspice returns [result]
	: 'package' 'spice.basic' ';' 'import' 'spice.basic.*' ';'
	'public' 'class' 'CSPICE' 'extends' 'Object' '{'
	static_decl
	{$result = []}
	(fn=function_decl {$result.append($fn.result)} )* '}';

// I don't want to make a modal lexer to get rid of this block,
// and I don't want to make a general enough parser to deal with it,
// so I'm hardcoding it like a pleb.
static_decl
	: 'static' '{'
	NAME '{' NAME '(' '"SET",' '"RETURN"' ')' ';'
	NAME '(' '"SET",' '"NULL"' ')' ';' '}'
	NAME '(' NAME NAME ')' '{'
	'exc.printStackTrace()' ';' '}' '}';

function_decl returns [result]
	: 'public' 'native' 'synchronized' 'static'
	ret=data_type name=NAME
	'('
	{args = []}
	((arg ',' {args.append($arg.result)})* arg {args.append($arg.result)})? ')'
	{throws = []}
	('throws' n=NAME {throws.append($n.text)} (',' n=NAME {throws.append($n.text)})*)? ';'
	{$result = FunctionDecl($name.text, $ret.result, args, throws)};

arg returns [result]
	: t=data_type n=NAME {$result = Argument($n.text, $t.result)};

data_type returns [result]
	: 'void' {$result = DataType(DataType.VOID, 0)}
	| 'double' {$result = DataType(DataType.DOUBLE, 0)}
	| 'String' {$result = DataType(DataType.STRING, 0)}
	| 'int' {$result = DataType(DataType.INT, 0)}
	| 'boolean' {$result = DataType(DataType.BOOLEAN, 0)}
	| 'GFSearchUtils' {$result = DataType(DataType.GFSEARCHUTILS, 0)}
	| 'GFScalarQuantity' {$result = DataType(DataType.GFSCALARQUANTITY, 0)}
	| t=data_type '[]' {$result = $t.result}{$result.array_depth += 1};

NAME : [a-zA-Z][a-zA-Z0-9]*;

WS : [ \t\r\n]+ -> skip;

COMMENT : '/*' .*? '*/' -> skip;

LINE_COMMENT : '//' ~[\r\n]* -> skip;
