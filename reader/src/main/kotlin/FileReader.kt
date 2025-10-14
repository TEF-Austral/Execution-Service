import java.io.File
import java.io.IOException

class FileReader(
    private val path: String,
) : Reader {
    override fun read(): String =
        try {
            File(path).readText()
        } catch (e: IOException) {
            ""
        }
}
