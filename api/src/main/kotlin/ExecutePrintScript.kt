//package api
//
//import TokenStream
//import api.dtos.ExecutionResult
//import builder.DefaultNodeBuilder
//import factory.DefaultInterpreterFactory.createInterpreter
//import factory.DefaultLexerFactory
//import factory.StringSplitterFactory
//import factory.StringToTokenConverterFactory
//import parser.factory.DefaultParserFactory
//import parser.result.FinalResult
//import parser.stream.ParserAstStream
//import stream.token.LexerTokenStream
//import transformer.StringToPrintScriptVersion
//import type.Version
//import java.io.InputStream
//import java.io.InputStreamReader
//import java.nio.charset.StandardCharsets
//
//class ExecutePrintScript : ExecuteLanguage {
//    override fun execute(
//        code: String,
//        version: String,
//    ): ExecutionResult = handleExecution(code, transform(version))
//
//    fun handleExecution(
//        code: String,
//        version: Version,
//    ): ExecutionResult {
//        val interpreter = createInterpreter(version)
//        val astStream = ParserAstStream(parseCode(code, version).getParser())
//        val result = interpreter.interpret(astStream)
//        return if (result.interpretedCorrectly) {
//            ExecutionResult(true, "Program executed successfully")
//        } else {
//            ExecutionResult(false, "Program executed with errors: ${result.message}")
//        }
//    }
//
//    fun parseCode(
//        code: String,
//        version: Version,
//    ): FinalResult {
//        val tokenStream = tokeniseCode(code, version)
//        val parser =
//            DefaultParserFactory.createWithVersion(
//                version,
//                DefaultNodeBuilder(),
//                tokenStream,
//            )
//        return parser.parse()
//    }
//
//    fun tokeniseCode(
//        code: String,
//        version: Version,
//    ): TokenStream {
//        val reader = InputStreamReader(stringToInputStream(code), StandardCharsets.UTF_8)
//        val lexerFactory = DefaultLexerFactory(StringSplitterFactory, StringToTokenConverterFactory)
//        val lexer = lexerFactory.createLexerWithVersion(version, reader)
//        return LexerTokenStream(lexer)
//    }
//
//    private fun stringToInputStream(code: String): InputStream =
//        code.byteInputStream(StandardCharsets.UTF_8)
//
//    private fun transform(version: String): Version =
//        StringToPrintScriptVersion().transform(version)
//}
