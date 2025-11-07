package consumers.handlers

import requests.LintingRequestEvent

interface ILintingRequestHandler {
    fun handle(request: LintingRequestEvent)
}
