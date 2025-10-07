import java.nio.file.Files
import java.nio.file.Path

class JsonReader(
    private val path: String,
) {
    fun read(): String = Files.readString(Path.of(path))
}
