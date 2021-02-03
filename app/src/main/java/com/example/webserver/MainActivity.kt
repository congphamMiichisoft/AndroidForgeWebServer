package com.example.webserver

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import fi.iki.elonen.NanoHTTPD
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val server: FileServer = FileServer(context = this,port = 8080)
//        server.start()

        val myHttp = MyHTTPD(this)
        myHttp.start()
        val webview = findViewById<WebView>(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.loadUrl("http://localhost:8080/")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}


class MyHTTPD(val ctx : Context) : NanoHTTPD(PORT) {
    override fun serve(session: IHTTPSession): Response? {
//        val uri = session.uri
//        if (uri == "/wwww") {
//            val response = "HelloWorld"
//            return newFixedLengthResponse(response)
//        }
//        return null
        
        val uri = session.uri.removePrefix("/").ifEmpty { "index.html" }
        println("Loading $uri")
        try {
            val mime = when (uri.substringAfterLast(".")) {
                "ico" -> "image/x-icon"
                "css" -> "text/css"
                "htm" -> "text/html"
                "html" -> "text/html"
                else -> "application/javascript"
            }
            var filepath = ctx.filesDir
            return NanoHTTPD.newChunkedResponse(
                Response.Status.OK,
                mime,
                ctx.assets.open("$uri") // prefix with www because your files are not in the root folder in assets

            )
        } catch (e: Exception) {
            val message = "Failed to load asset $uri because $e"
            println(message)
            e.printStackTrace()
            return NanoHTTPD.newFixedLengthResponse(message)
        }
    }

    companion object {
        const val PORT = 8080
    }
}