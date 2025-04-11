package com.example.librarybooklendingsystem.utils

import android.util.Log
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory
import javax.net.ssl.SSLSocketFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EmailValidator {
    suspend fun isValidEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Split email to get domain
            val parts = email.split("@")
            if (parts.size != 2) return@withContext false
            
            val domain = parts[1]
            
            // Try to resolve MX records for the domain
            val mxRecords = try {
                InetAddress.getAllByName(domain)
            } catch (e: Exception) {
                Log.e("EmailValidator", "Error resolving MX records: ${e.message}")
                return@withContext false
            }
            
            if (mxRecords.isEmpty()) return@withContext false
            
            // Try to connect to the mail server
            val socketFactory: SocketFactory = SSLSocketFactory.getDefault()
            var socket: Socket? = null
            
            try {
                // Try to connect to port 25 (SMTP) or 587 (SMTP with TLS)
                socket = socketFactory.createSocket(domain, 587)
                if (socket.isConnected) {
                    return@withContext true
                }
            } catch (e: Exception) {
                Log.e("EmailValidator", "Error connecting to mail server: ${e.message}")
            } finally {
                socket?.close()
            }
            
            false
        } catch (e: Exception) {
            Log.e("EmailValidator", "Error validating email: ${e.message}")
            false
        }
    }
} 