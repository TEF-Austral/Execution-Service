package result

import string.streaming.StreamingSplitter
import coordinates.Coordinates

data class StreamSplitterResult(
    val string: String,
    val coordinates: Coordinates,
    val splitter: StreamingSplitter,
)
