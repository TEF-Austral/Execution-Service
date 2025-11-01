package utils

import factory.DefaultInterpreterFactory
import parser.stream.ParserAstStream
import transformer.StringToPrintScriptVersion
import utils.ParserInitializer.parse
import java.io.InputStream

object InterpreterInitializer {
    fun execute(
        src: InputStream,
        version: String,
        emitter: PrintEmitter,
        handler: ErrorHandler,
        provider: InputReceiver,
    ) {
        val adaptedVersion = StringToPrintScriptVersion().transform(version)
        val astStream = ParserAstStream(parse(src, version))
        val adaptedEmitter = PrintEmitterAdapter(emitter)
        val adaptedInput = InputProviderAdapter(provider, emitter)
        val interpreter =
            DefaultInterpreterFactory.createWithVersionAndEmitterAndInputProvider(
                adaptedVersion,
                adaptedEmitter,
                adaptedInput,
            )
        val result = interpreter.interpret(astStream)
        if (!result.interpretedCorrectly) {
            handler.reportError(result.message)
        }
    }
}
