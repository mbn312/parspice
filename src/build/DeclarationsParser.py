# Generated from Declarations.g4 by ANTLR 4.9.1
# encoding: utf-8
from antlr4 import *
from io import StringIO
import sys
if sys.version_info[1] > 5:
	from typing import TextIO
else:
	from typing.io import TextIO


from parse_tree import *
import manual_info

manual_info.load_info()


def serializedATN():
    with StringIO() as buf:
        buf.write("\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%")
        buf.write("\u0088\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2")
        buf.write("\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3")
        buf.write("\2\3\2\7\2\36\n\2\f\2\16\2!\13\2\3\2\3\2\3\3\3\3\3\3\3")
        buf.write("\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3")
        buf.write("\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3")
        buf.write("\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4M\n\4\f\4\16")
        buf.write("\4P\13\4\3\4\3\4\3\4\5\4U\n\4\3\4\3\4\3\4\3\4\3\4\3\4")
        buf.write("\3\4\3\4\7\4_\n\4\f\4\16\4b\13\4\5\4d\n\4\3\4\3\4\3\4")
        buf.write("\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3")
        buf.write("\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6}\n\6\3\6\3\6\3\6\3\6\7")
        buf.write("\6\u0083\n\6\f\6\16\6\u0086\13\6\3\6\2\3\n\7\2\4\6\b\n")
        buf.write("\2\2\2\u008e\2\f\3\2\2\2\4$\3\2\2\2\6@\3\2\2\2\bi\3\2")
        buf.write("\2\2\n|\3\2\2\2\f\r\7\3\2\2\r\16\7\4\2\2\16\17\7\5\2\2")
        buf.write("\17\20\7\6\2\2\20\21\7\7\2\2\21\22\7\5\2\2\22\23\7\b\2")
        buf.write("\2\23\24\7\t\2\2\24\25\7\n\2\2\25\26\7\13\2\2\26\27\7")
        buf.write("\f\2\2\27\30\7\r\2\2\30\31\5\4\3\2\31\37\b\2\1\2\32\33")
        buf.write("\5\6\4\2\33\34\b\2\1\2\34\36\3\2\2\2\35\32\3\2\2\2\36")
        buf.write("!\3\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 \"\3\2\2\2!\37\3\2")
        buf.write("\2\2\"#\7\16\2\2#\3\3\2\2\2$%\7\17\2\2%&\7\r\2\2&\'\7")
        buf.write("\"\2\2\'(\7\r\2\2()\7\"\2\2)*\7\20\2\2*+\7\21\2\2+,\7")
        buf.write("\22\2\2,-\7\23\2\2-.\7\5\2\2./\7\"\2\2/\60\7\20\2\2\60")
        buf.write("\61\7\21\2\2\61\62\7\24\2\2\62\63\7\23\2\2\63\64\7\5\2")
        buf.write("\2\64\65\7\16\2\2\65\66\7\"\2\2\66\67\7\20\2\2\678\7\"")
        buf.write("\2\289\7\"\2\29:\7\23\2\2:;\7\r\2\2;<\7\25\2\2<=\7\5\2")
        buf.write("\2=>\7\16\2\2>?\7\16\2\2?\5\3\2\2\2@A\7\b\2\2AB\7\26\2")
        buf.write("\2BC\7\27\2\2CD\7\17\2\2DE\5\n\6\2EF\7\"\2\2FG\7\20\2")
        buf.write("\2GT\b\4\1\2HI\5\b\5\2IJ\7\30\2\2JK\b\4\1\2KM\3\2\2\2")
        buf.write("LH\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OQ\3\2\2\2PN\3")
        buf.write("\2\2\2QR\5\b\5\2RS\b\4\1\2SU\3\2\2\2TN\3\2\2\2TU\3\2\2")
        buf.write("\2UV\3\2\2\2VW\7\23\2\2Wc\b\4\1\2XY\7\31\2\2YZ\7\"\2\2")
        buf.write("Z`\b\4\1\2[\\\7\30\2\2\\]\7\"\2\2]_\b\4\1\2^[\3\2\2\2")
        buf.write("_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2ad\3\2\2\2b`\3\2\2\2cX\3")
        buf.write("\2\2\2cd\3\2\2\2de\3\2\2\2ef\7\5\2\2fg\b\4\1\2gh\b\4\1")
        buf.write("\2h\7\3\2\2\2ij\5\n\6\2jk\7\"\2\2kl\b\5\1\2l\t\3\2\2\2")
        buf.write("mn\b\6\1\2no\7\32\2\2o}\b\6\1\2pq\7\33\2\2q}\b\6\1\2r")
        buf.write("s\7\34\2\2s}\b\6\1\2tu\7\35\2\2u}\b\6\1\2vw\7\36\2\2w")
        buf.write("}\b\6\1\2xy\7\37\2\2y}\b\6\1\2z{\7 \2\2{}\b\6\1\2|m\3")
        buf.write("\2\2\2|p\3\2\2\2|r\3\2\2\2|t\3\2\2\2|v\3\2\2\2|x\3\2\2")
        buf.write("\2|z\3\2\2\2}\u0084\3\2\2\2~\177\f\3\2\2\177\u0080\7!")
        buf.write("\2\2\u0080\u0081\b\6\1\2\u0081\u0083\b\6\1\2\u0082~\3")
        buf.write("\2\2\2\u0083\u0086\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085")
        buf.write("\3\2\2\2\u0085\13\3\2\2\2\u0086\u0084\3\2\2\2\t\37NT`")
        buf.write("c|\u0084")
        return buf.getvalue()


class DeclarationsParser ( Parser ):

    grammarFileName = "Declarations.g4"

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    sharedContextCache = PredictionContextCache()

    literalNames = [ "<INVALID>", "'package'", "'spice.basic'", "';'", "'import'", 
                     "'spice.basic.*'", "'public'", "'class'", "'CSPICE'", 
                     "'extends'", "'Object'", "'{'", "'}'", "'static'", 
                     "'('", "'\"SET\",'", "'\"RETURN\"'", "')'", "'\"NULL\"'", 
                     "'exc.printStackTrace()'", "'native'", "'synchronized'", 
                     "','", "'throws'", "'void'", "'double'", "'String'", 
                     "'int'", "'boolean'", "'GFSearchUtils'", "'GFScalarQuantity'", 
                     "'[]'" ]

    symbolicNames = [ "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "NAME", "WS", "COMMENT", "LINE_COMMENT" ]

    RULE_cspice = 0
    RULE_static_decl = 1
    RULE_function_decl = 2
    RULE_arg = 3
    RULE_data_type = 4

    ruleNames =  [ "cspice", "static_decl", "function_decl", "arg", "data_type" ]

    EOF = Token.EOF
    T__0=1
    T__1=2
    T__2=3
    T__3=4
    T__4=5
    T__5=6
    T__6=7
    T__7=8
    T__8=9
    T__9=10
    T__10=11
    T__11=12
    T__12=13
    T__13=14
    T__14=15
    T__15=16
    T__16=17
    T__17=18
    T__18=19
    T__19=20
    T__20=21
    T__21=22
    T__22=23
    T__23=24
    T__24=25
    T__25=26
    T__26=27
    T__27=28
    T__28=29
    T__29=30
    T__30=31
    NAME=32
    WS=33
    COMMENT=34
    LINE_COMMENT=35

    def __init__(self, input:TokenStream, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.9.1")
        self._interp = ParserATNSimulator(self, self.atn, self.decisionsToDFA, self.sharedContextCache)
        self._predicates = None




    class CspiceContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser
            self.result = None
            self.fn = None # Function_declContext

        def static_decl(self):
            return self.getTypedRuleContext(DeclarationsParser.Static_declContext,0)


        def function_decl(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(DeclarationsParser.Function_declContext)
            else:
                return self.getTypedRuleContext(DeclarationsParser.Function_declContext,i)


        def getRuleIndex(self):
            return DeclarationsParser.RULE_cspice




    def cspice(self):

        localctx = DeclarationsParser.CspiceContext(self, self._ctx, self.state)
        self.enterRule(localctx, 0, self.RULE_cspice)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 10
            self.match(DeclarationsParser.T__0)
            self.state = 11
            self.match(DeclarationsParser.T__1)
            self.state = 12
            self.match(DeclarationsParser.T__2)
            self.state = 13
            self.match(DeclarationsParser.T__3)
            self.state = 14
            self.match(DeclarationsParser.T__4)
            self.state = 15
            self.match(DeclarationsParser.T__2)
            self.state = 16
            self.match(DeclarationsParser.T__5)
            self.state = 17
            self.match(DeclarationsParser.T__6)
            self.state = 18
            self.match(DeclarationsParser.T__7)
            self.state = 19
            self.match(DeclarationsParser.T__8)
            self.state = 20
            self.match(DeclarationsParser.T__9)
            self.state = 21
            self.match(DeclarationsParser.T__10)
            self.state = 22
            self.static_decl()
            localctx.result = []
            self.state = 29
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while _la==DeclarationsParser.T__5:
                self.state = 24
                localctx.fn = self.function_decl()
                localctx.result.append(localctx.fn.result)
                self.state = 31
                self._errHandler.sync(self)
                _la = self._input.LA(1)

            self.state = 32
            self.match(DeclarationsParser.T__11)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class Static_declContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def NAME(self, i:int=None):
            if i is None:
                return self.getTokens(DeclarationsParser.NAME)
            else:
                return self.getToken(DeclarationsParser.NAME, i)

        def getRuleIndex(self):
            return DeclarationsParser.RULE_static_decl




    def static_decl(self):

        localctx = DeclarationsParser.Static_declContext(self, self._ctx, self.state)
        self.enterRule(localctx, 2, self.RULE_static_decl)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 34
            self.match(DeclarationsParser.T__12)
            self.state = 35
            self.match(DeclarationsParser.T__10)
            self.state = 36
            self.match(DeclarationsParser.NAME)
            self.state = 37
            self.match(DeclarationsParser.T__10)
            self.state = 38
            self.match(DeclarationsParser.NAME)
            self.state = 39
            self.match(DeclarationsParser.T__13)
            self.state = 40
            self.match(DeclarationsParser.T__14)
            self.state = 41
            self.match(DeclarationsParser.T__15)
            self.state = 42
            self.match(DeclarationsParser.T__16)
            self.state = 43
            self.match(DeclarationsParser.T__2)
            self.state = 44
            self.match(DeclarationsParser.NAME)
            self.state = 45
            self.match(DeclarationsParser.T__13)
            self.state = 46
            self.match(DeclarationsParser.T__14)
            self.state = 47
            self.match(DeclarationsParser.T__17)
            self.state = 48
            self.match(DeclarationsParser.T__16)
            self.state = 49
            self.match(DeclarationsParser.T__2)
            self.state = 50
            self.match(DeclarationsParser.T__11)
            self.state = 51
            self.match(DeclarationsParser.NAME)
            self.state = 52
            self.match(DeclarationsParser.T__13)
            self.state = 53
            self.match(DeclarationsParser.NAME)
            self.state = 54
            self.match(DeclarationsParser.NAME)
            self.state = 55
            self.match(DeclarationsParser.T__16)
            self.state = 56
            self.match(DeclarationsParser.T__10)
            self.state = 57
            self.match(DeclarationsParser.T__18)
            self.state = 58
            self.match(DeclarationsParser.T__2)
            self.state = 59
            self.match(DeclarationsParser.T__11)
            self.state = 60
            self.match(DeclarationsParser.T__11)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class Function_declContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser
            self.result = None
            self.ret = None # Data_typeContext
            self.name = None # Token
            self._arg = None # ArgContext
            self.n = None # Token

        def data_type(self):
            return self.getTypedRuleContext(DeclarationsParser.Data_typeContext,0)


        def NAME(self, i:int=None):
            if i is None:
                return self.getTokens(DeclarationsParser.NAME)
            else:
                return self.getToken(DeclarationsParser.NAME, i)

        def arg(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(DeclarationsParser.ArgContext)
            else:
                return self.getTypedRuleContext(DeclarationsParser.ArgContext,i)


        def getRuleIndex(self):
            return DeclarationsParser.RULE_function_decl




    def function_decl(self):

        localctx = DeclarationsParser.Function_declContext(self, self._ctx, self.state)
        self.enterRule(localctx, 4, self.RULE_function_decl)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 62
            self.match(DeclarationsParser.T__5)
            self.state = 63
            self.match(DeclarationsParser.T__19)
            self.state = 64
            self.match(DeclarationsParser.T__20)
            self.state = 65
            self.match(DeclarationsParser.T__12)
            self.state = 66
            localctx.ret = self.data_type(0)
            self.state = 67
            localctx.name = self.match(DeclarationsParser.NAME)
            self.state = 68
            self.match(DeclarationsParser.T__13)
            args = []
            self.state = 82
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            if (((_la) & ~0x3f) == 0 and ((1 << _la) & ((1 << DeclarationsParser.T__23) | (1 << DeclarationsParser.T__24) | (1 << DeclarationsParser.T__25) | (1 << DeclarationsParser.T__26) | (1 << DeclarationsParser.T__27) | (1 << DeclarationsParser.T__28) | (1 << DeclarationsParser.T__29))) != 0):
                self.state = 76
                self._errHandler.sync(self)
                _alt = self._interp.adaptivePredict(self._input,1,self._ctx)
                while _alt!=2 and _alt!=ATN.INVALID_ALT_NUMBER:
                    if _alt==1:
                        self.state = 70
                        localctx._arg = self.arg()
                        self.state = 71
                        self.match(DeclarationsParser.T__21)
                        args.append(localctx._arg.result) 
                    self.state = 78
                    self._errHandler.sync(self)
                    _alt = self._interp.adaptivePredict(self._input,1,self._ctx)

                self.state = 79
                localctx._arg = self.arg()
                args.append(localctx._arg.result)


            self.state = 84
            self.match(DeclarationsParser.T__16)
            throws = []
            self.state = 97
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            if _la==DeclarationsParser.T__22:
                self.state = 86
                self.match(DeclarationsParser.T__22)
                self.state = 87
                localctx.n = self.match(DeclarationsParser.NAME)
                throws.append((None if localctx.n is None else localctx.n.text))
                self.state = 94
                self._errHandler.sync(self)
                _la = self._input.LA(1)
                while _la==DeclarationsParser.T__21:
                    self.state = 89
                    self.match(DeclarationsParser.T__21)
                    self.state = 90
                    localctx.n = self.match(DeclarationsParser.NAME)
                    throws.append((None if localctx.n is None else localctx.n.text))
                    self.state = 96
                    self._errHandler.sync(self)
                    _la = self._input.LA(1)



            self.state = 99
            self.match(DeclarationsParser.T__2)
            for (arg,io) in zip(args, manual_info.io((None if localctx.name is None else localctx.name.text))): arg.set_io(io)
            localctx.result = FunctionDecl((None if localctx.name is None else localctx.name.text), manual_info.classification((None if localctx.name is None else localctx.name.text)), localctx.ret.result, args, throws)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class ArgContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser
            self.result = None
            self.t = None # Data_typeContext
            self.n = None # Token

        def data_type(self):
            return self.getTypedRuleContext(DeclarationsParser.Data_typeContext,0)


        def NAME(self):
            return self.getToken(DeclarationsParser.NAME, 0)

        def getRuleIndex(self):
            return DeclarationsParser.RULE_arg




    def arg(self):

        localctx = DeclarationsParser.ArgContext(self, self._ctx, self.state)
        self.enterRule(localctx, 6, self.RULE_arg)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 103
            localctx.t = self.data_type(0)
            self.state = 104
            localctx.n = self.match(DeclarationsParser.NAME)
            localctx.result = Argument((None if localctx.n is None else localctx.n.text), localctx.t.result)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class Data_typeContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser
            self.result = None
            self.t = None # Data_typeContext

        def data_type(self):
            return self.getTypedRuleContext(DeclarationsParser.Data_typeContext,0)


        def getRuleIndex(self):
            return DeclarationsParser.RULE_data_type



    def data_type(self, _p:int=0):
        _parentctx = self._ctx
        _parentState = self.state
        localctx = DeclarationsParser.Data_typeContext(self, self._ctx, _parentState)
        _prevctx = localctx
        _startState = 8
        self.enterRecursionRule(localctx, 8, self.RULE_data_type, _p)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 122
            self._errHandler.sync(self)
            token = self._input.LA(1)
            if token in [DeclarationsParser.T__23]:
                self.state = 108
                self.match(DeclarationsParser.T__23)
                localctx.result = DataType(DataType.VOID, 0)
                pass
            elif token in [DeclarationsParser.T__24]:
                self.state = 110
                self.match(DeclarationsParser.T__24)
                localctx.result = DataType(DataType.DOUBLE, 0)
                pass
            elif token in [DeclarationsParser.T__25]:
                self.state = 112
                self.match(DeclarationsParser.T__25)
                localctx.result = DataType(DataType.STRING, 0)
                pass
            elif token in [DeclarationsParser.T__26]:
                self.state = 114
                self.match(DeclarationsParser.T__26)
                localctx.result = DataType(DataType.INT, 0)
                pass
            elif token in [DeclarationsParser.T__27]:
                self.state = 116
                self.match(DeclarationsParser.T__27)
                localctx.result = DataType(DataType.BOOLEAN, 0)
                pass
            elif token in [DeclarationsParser.T__28]:
                self.state = 118
                self.match(DeclarationsParser.T__28)
                localctx.result = DataType(DataType.GFSEARCHUTILS, 0)
                pass
            elif token in [DeclarationsParser.T__29]:
                self.state = 120
                self.match(DeclarationsParser.T__29)
                localctx.result = DataType(DataType.GFSCALARQUANTITY, 0)
                pass
            else:
                raise NoViableAltException(self)

            self._ctx.stop = self._input.LT(-1)
            self.state = 130
            self._errHandler.sync(self)
            _alt = self._interp.adaptivePredict(self._input,6,self._ctx)
            while _alt!=2 and _alt!=ATN.INVALID_ALT_NUMBER:
                if _alt==1:
                    if self._parseListeners is not None:
                        self.triggerExitRuleEvent()
                    _prevctx = localctx
                    localctx = DeclarationsParser.Data_typeContext(self, _parentctx, _parentState)
                    localctx.t = _prevctx
                    self.pushNewRecursionContext(localctx, _startState, self.RULE_data_type)
                    self.state = 124
                    if not self.precpred(self._ctx, 1):
                        from antlr4.error.Errors import FailedPredicateException
                        raise FailedPredicateException(self, "self.precpred(self._ctx, 1)")
                    self.state = 125
                    self.match(DeclarationsParser.T__30)
                    localctx.result = localctx.t.result
                    localctx.result.array_depth += 1 
                self.state = 132
                self._errHandler.sync(self)
                _alt = self._interp.adaptivePredict(self._input,6,self._ctx)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.unrollRecursionContexts(_parentctx)
        return localctx



    def sempred(self, localctx:RuleContext, ruleIndex:int, predIndex:int):
        if self._predicates == None:
            self._predicates = dict()
        self._predicates[4] = self.data_type_sempred
        pred = self._predicates.get(ruleIndex, None)
        if pred is None:
            raise Exception("No predicate with index:" + str(ruleIndex))
        else:
            return pred(localctx, predIndex)

    def data_type_sempred(self, localctx:Data_typeContext, predIndex:int):
            if predIndex == 0:
                return self.precpred(self._ctx, 1)
         




