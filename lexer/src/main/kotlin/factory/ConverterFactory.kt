package factory

import converter.TokenConverter
import converter.specific.StringToTokenConverter

interface ConverterFactory {
    fun createVersionOne(): TokenConverter

    fun createVersionOnePointOne(): TokenConverter

    fun createCustom(customConverters: List<StringToTokenConverter>): TokenConverter
}
