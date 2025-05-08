package com.nhathuy.restaurant_manager_app.service

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.data.model.WebSocketConnectionState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantWebSocketClient @Inject constructor(private val tokenManager: TokenManager) {
    private var webSocketClient: WebSocketClient? = null
    private val TAG = "WebSocketClient"

    //flows to emit notificationFlow
    private val _notificationFlow = MutableSharedFlow<AdminNotification>(replay = 0)
    val notificationFlow: SharedFlow<AdminNotification> = _notificationFlow

    //connect state
    private val _connectionState = MutableSharedFlow<WebSocketConnectionState>(replay = 1)
    val connectionState: SharedFlow<WebSocketConnectionState> = _connectionState

    private val gson = Gson()

    /**
     * connects to websocket server
     */
    fun connect(baseUrl: String) {
        val token = tokenManager.getAccessToken() ?: return

        try {
            val uri = URI("$baseUrl/restaurant-websocket/websocket?token=$token")

            webSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d(TAG, "Websocket connection opened")
                    _connectionState.tryEmit(WebSocketConnectionState.CONNECTED)

                    // Subscribe to admin notifications topic
                    subscribe("/topic/admin-notifications")
                }

                override fun onMessage(message: String?) {
                    Log.d(TAG,"WebSocket message received: $message")

                    message?.let {
                        try{
                            val notification = gson.fromJson(it,AdminNotification::class.java)
                            _notificationFlow.tryEmit(notification)
                        }
                        catch(e:Exception){
                            Log.e(TAG,"Error parsing notification ${e.message}")
                        }
                    }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d(TAG,"WebSocket connect closed. Code: $code, reason: $reason")
                    _connectionState.tryEmit(WebSocketConnectionState.DISCONNECTED)
                }

                override fun onError(ex: java.lang.Exception?) {
                    Log.e(TAG, "WebSocket error: ${ex?.message}")
                    _connectionState.tryEmit(WebSocketConnectionState.ERROR)
                }

            }

            webSocketClient?.connect()

        } catch (e: Exception) {
            Log.e(TAG, "Error creating WebSocket client: ${e.message}")
            _connectionState.tryEmit(WebSocketConnectionState.ERROR)
        }
    }

    /**
     * Subscribes to a specific WebSocket topic
     */
    fun subscribe(topic: String) {
        val subscribeFrame = """
            SUBSCRIBE
            id:sub-0
            destination:$topic
        """.trimIndent()

        webSocketClient?.send(subscribeFrame)
        Log.d(TAG,"Subscribed to topic: $topic")
    }

    /**
     * Sends a message to the WebSocket server
     */
    fun sendMessage(destination: String, message: String) {
        val messageFrame = """
            SEND
            destination:$destination
            content-type:application/json
            
            $message
        """.trimIndent()

        webSocketClient?.send(messageFrame)
    }

    /**
     * Disconnects from the WebSocket server
     */
    fun disconnect() {
        webSocketClient?.close()
        webSocketClient = null
        _connectionState.tryEmit(WebSocketConnectionState.DISCONNECTED)
    }

}