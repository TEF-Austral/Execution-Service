
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ReaderTest {
    @Test
    fun `FileReader should read content from a valid file`() {
        val filePath = "src/test/resources/script.txt"
        val expectedContent = File(filePath).readText()

        val reader = FileReader(filePath)
        val actualContent = reader.read()
        print(expectedContent)

        Assertions.assertEquals(expectedContent, actualContent)
    }

    @Test
    fun `FileReader should return an empty String for a non-existent file`() {
        val nonExistentFilePath = "non/existent/file.txt"
        val reader = FileReader(nonExistentFilePath)

        Assertions.assertEquals("", reader.read())
    }

    @Test
    fun `Stream should read content from a valid file`() {
        val testFile = File("test_stream.txt")
        val testContent =
            """
            let x = 42;
            if (x > 10) {
                println("Hello World");
            }
            """.trimIndent()

        testFile.writeText(testContent)

        try {
            val stream = Stream("test_stream.txt")
            val actualContent = stream.read()

            Assertions.assertEquals(testContent, actualContent)
        } finally {
            testFile.delete()
        }
    }

    @Test
    fun `JsonReader should read JSON content from a valid file`() {
        val testFile = File("test.json")
        val jsonContent =
            """
            {
                "name": "John",
                "age": 30,
                "city": "New York"
            }
            """.trimIndent()

        testFile.writeText(jsonContent)

        try {
            val jsonReader = JsonReader("test.json")
            val actualContent = jsonReader.read()

            Assertions.assertEquals(jsonContent, actualContent)
        } finally {
            testFile.delete()
        }
    }

    @Test
    fun `JsonReader should throw exception for non-existent file`() {
        val jsonReader = JsonReader("non/existent/file.json")

        Assertions.assertThrows(Exception::class.java) {
            jsonReader.read()
        }
    }

    @Test
    fun `YamlReader should read YAML content from a valid file`() {
        val testFile = File("test.yaml")
        val yamlContent =
            """
            name: John
            age: 30
            city: New York
            skills:
              - Java
              - Kotlin
              - Python
            """.trimIndent()

        testFile.writeText(yamlContent)

        try {
            val yamlReader = YamlReader("test.yaml")
            val actualContent = yamlReader.read()

            Assertions.assertEquals(yamlContent, actualContent)
        } finally {
            testFile.delete()
        }
    }

    @Test
    fun `YamlReader should throw exception for non-existent file`() {
        val yamlReader = YamlReader("non/existent/file.yaml")

        Assertions.assertThrows(Exception::class.java) {
            yamlReader.read()
        }
    }

    @Test
    fun `JsonReader and YamlReader should work with different file extensions`() {
        val jsonFile = File("config.json")
        val yamlFile = File("config.yml")

        val jsonContent = """{"version": "1.0", "debug": true}"""
        val yamlContent =
            """version: "1.0"
            |debug: true
            """.trimMargin()

        jsonFile.writeText(jsonContent)
        yamlFile.writeText(yamlContent)

        try {
            val jsonReader = JsonReader("config.json")
            val yamlReader = YamlReader("config.yml")

            Assertions.assertEquals(jsonContent, jsonReader.read())
            Assertions.assertEquals(yamlContent, yamlReader.read())
        } finally {
            jsonFile.delete()
            yamlFile.delete()
        }
    }

    @Test
    fun `Large ReaderTest`() {
        val filePath = "src/test/resources/largerScript.txt"
        val expectedContent = File(filePath).readText()
        val reader = FileReader(filePath)
        val actualContent = reader.read()
        Assertions.assertEquals(expectedContent, actualContent)
        println("Actual content:$actualContent")
        println("Expected content:$expectedContent")
        Assertions.assertEquals(expectedContent, actualContent)
    }
}
