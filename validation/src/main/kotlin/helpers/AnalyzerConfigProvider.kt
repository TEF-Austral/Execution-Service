package helpers

import config.AnalyzerConfig

class UserConfigProvider {
    private val userConfigs = mutableMapOf<String, AnalyzerConfig>()

    fun getUserConfig(userId: String?): AnalyzerConfig {
        return userConfigs[userId] ?: AnalyzerConfig() // Return default config if userId not found
    }

    fun setUserConfig(
        userId: String,
        config: AnalyzerConfig,
    ) {
        userConfigs[userId] = config
    }
}
