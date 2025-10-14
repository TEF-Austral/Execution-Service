package string.streaming

import result.StreamSplitterResult
import string.splitter.extractor.TokenExtractor
import java.io.Reader

class StreamingSplitter {

    private val state: SplitterState
    private val bufferManager: BufferManager
    private val tokenProcessor: TokenProcessor

    constructor(
        reader: Reader,
        extractors: List<TokenExtractor>,
        bufferSize: Int = 65536,
        logicalUnit: List<Char> =
            listOf(
                ';',
                '\n',
            ),
    ) {
        this.state = SplitterState(logicalUnit = logicalUnit)
        this.bufferManager = BufferManager(reader, bufferSize)
        this.tokenProcessor = TokenProcessor(extractors)
    }

    private constructor(
        state: SplitterState,
        bufferManager: BufferManager,
        tokenProcessor: TokenProcessor,
    ) {
        this.state = state
        this.bufferManager = bufferManager
        this.tokenProcessor = tokenProcessor
    }

    fun next(): StreamSplitterResult? {
        val currentState = bufferManager.ensureSufficientBuffer(state)

        if (!currentState.hasData) {
            return null
        }

        return when (val processingResult = tokenProcessor.process(currentState)) {
            is Success -> {
                return buildSplitterResult(processingResult, currentState)
            }

            is Skip -> createNewSplitter(processingResult.newState).next()

            is Retry -> createNewSplitter(processingResult.newState).next()

            null -> {
                if (shouldConsumeWhenFinished(currentState)) {
                    createNewSplitter(currentState.consume(1)).next()
                } else {
                    null
                }
            }
        }
    }

    private fun buildSplitterResult(
        processingResult: Success,
        currentState: SplitterState,
    ): StreamSplitterResult {
        val nextSplitter = createNewSplitter(processingResult.newState)
        return StreamSplitterResult(
            string = processingResult.token,
            coordinates = currentState.cursor.position,
            splitter = nextSplitter,
        )
    }

    private fun shouldConsumeWhenFinished(state: SplitterState): Boolean =
        state.isStreamFinished && state.hasData

    private fun createNewSplitter(newState: SplitterState): StreamingSplitter =
        StreamingSplitter(newState, this.bufferManager, this.tokenProcessor)
}
