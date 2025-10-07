package transformer

import type.Version

class StringToPrintScriptVersion(
    private val versionMap: Map<String, Version> =
        mapOf(
            "1.0" to Version.VERSION_1_0,
            "1.1" to Version.VERSION_1_1,
        ),
) {
    fun transform(version: String): Version = versionMap[version] ?: Version.VERSION_1_0
}
