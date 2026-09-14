package com.innovature.castnow.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient
) {

    private var webSocket: WebSocket? = null

    private val _messages = MutableStateFlow<String?>(null)
    val messages: StateFlow<String?> = _messages

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected

    fun connect() {

        val request = Request.Builder()
            .url("wss://your-websocket-server.com/ws")
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    _connected.value = true
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    _messages.value = text
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    bytes: ByteString
                ) {
                    // Handle binary data if required
                }

                override fun onClosing(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    _connected.value = false
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    _connected.value = false
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    _connected.value = false
                }
            }
        )
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(
            1000,
            "Client disconnected"
        )

        webSocket = null
        _connected.value = false
    }
}