package utils

import emitter.Emitter
import factory.DefaultInterpreterFactory
import parser.stream.ParserAstStream
import transformer.StringToPrintScriptVersion
import utils.ParserInitializer.parse
import java.io.InputStream

object InterpreterInitializer {
    fun execute(
        src: InputStream,
        version: String,
        printEmitter: Emitter,
        inputEmitter: Emitter,
        handler: ErrorHandler,
        provider: InputReceiver,
    ) {
        val adaptedVersion = StringToPrintScriptVersion().transform(version)
        val astStream = ParserAstStream(parse(src, version))
        val adaptedInput = InputProviderAdapter(provider, inputEmitter)
        val interpreter =
            DefaultInterpreterFactory.createWithVersionAndEmitterAndInputProvider(
                adaptedVersion,
                printEmitter,
                adaptedInput,
            )
        val result = interpreter.interpret(astStream)
        if (!result.interpretedCorrectly) {
            handler.reportError(result.message)
        }
    }
}
