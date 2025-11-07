package consumers.handlers

import requests.TestingRequestEvent

interface ITestingRequestHandler {
    fun handle(request: TestingRequestEvent)
}
