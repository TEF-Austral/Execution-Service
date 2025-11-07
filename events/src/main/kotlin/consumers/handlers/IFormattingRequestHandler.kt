package consumers.handlers

import requests.FormattingRequestEvent

interface IFormattingRequestHandler {
    fun handle(request: FormattingRequestEvent)
}
