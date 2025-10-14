import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class Stream(
    private val filePath: String,
) : Reader {
    override fun read(): String =
        BufferedReader(
            InputStreamReader(
                FileInputStream(filePath),
                StandardCharsets.UTF_8,
            ),
        ).use { reader ->
            reader.readLines().joinToString("\n")
        }
}
