package com.gne.groceries.retrofit

import android.os.Looper
import android.util.Log
import com.gne.groceries.BuildConfig
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit


class ApiInterceptor @JvmOverloads constructor(private val logger: HttpLoggingInterceptor.Logger = Logger.DEFAULT) :
    Interceptor {
    enum class Level {
        /** No logs.  */
        NONE,

        /**
         * Logs request and response lines.
         */
        BASIC,

        /**
         * Logs request and response lines and their respective headers.
         */
        HEADERS,

        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         */
        BODY
    }

    interface Logger {
        @Throws(IOException::class)
        fun log(message: String?)

        companion object {
            val DEFAULT: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger { message: String ->
                if (BuildConfig.DEBUG) {
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    //                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
                    try {
                        Log.d("Interceptor", message)
                    } catch (e: Exception) {
//                        Log.d("Interceptor", e.message!!)
                    }
                    //                    }
//                });
                }
            }
        }
    }

    @Volatile
    private var headersToRedact: Set<String> = Collections.emptySet()
    fun redactHeader(name: String) {
        val newHeadersToRedact: MutableSet<String> =
            TreeSet(java.lang.String.CASE_INSENSITIVE_ORDER)
        newHeadersToRedact.addAll(headersToRedact)
        newHeadersToRedact.add(name)
        headersToRedact = newHeadersToRedact
    }

    @Volatile
    var level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
        private set

    /** Change the level at which this interceptor logs.  */
    fun setLevel(level: Level?): ApiInterceptor {
        if (level == null) throw NullPointerException("level == null. Use Level.NONE instead.")
        this.level = level
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = level
//        val request: Request = chain.request()
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()
        val url = originalUrl.newBuilder()
            .addQueryParameter("api-key", BuildConfig.API_KEY)
            .addQueryParameter("format", "json")
            .build()

        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS
        val requestBody: RequestBody? = request.body()
        val hasRequestBody = requestBody != null
        val connection: Connection? = chain.connection()
        var requestStartMessage: String = ("--> "
                + request.method()
                + ' ' + request.url()
            .toString() + if (connection != null) " " + connection.protocol() else "")
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody?.contentLength() + "-byte body)"
        }
        logger.log(requestStartMessage)
        if (logHeaders) {
            if (hasRequestBody) {
                if (requestBody?.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType())
                }
                if (requestBody?.contentLength() != -1L) {
                    logger.log("Content-Length: " + requestBody?.contentLength())
                }
            }
            val headers: Headers = request.headers()
            var i = 0
            val count: Int = headers.size()
            while (i < count) {
                val name: String = headers.name(i)
                if (!"Content-Type".equals(
                        name,
                        ignoreCase = true
                    ) && !"Content-Length".equals(name, ignoreCase = true)
                ) {
                    logHeader(headers, i)
                }
                i++
            }
            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method())
            } else if (bodyHasUnknownEncoding(request.headers())) {
                logger.log("--> END " + request.method().toString() + " (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody?.writeTo(buffer)
                var charset: Charset? = UTF8
                val contentType: MediaType? = requestBody?.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                logger.log("")
                if (isPlaintext(buffer)) {
                    logger.log(buffer.readString(charset))
                    logger.log(
                        "--> END " + request.method()
                            .toString() + " (" + requestBody?.contentLength()
                            .toString() + "-byte body)"
                    )
                } else {
                    logger.log(
                        ("--> END " + request.method().toString() + " (binary "
                                + requestBody?.contentLength().toString() + "-byte body omitted)")
                    )
                }
            }
        }
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs: Long = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log(
            ("<-- "
                    + response.code()
                    + (if (response.message()
                    .isEmpty()
            ) "" else ' '.toString() + response.message())
                    + ' ' + response.request().url()
                    + " (" + tookMs + "ms" + (if (!logHeaders) ", $bodySize body" else "") + ')')
        )
        if (logHeaders) {
            val headers: Headers = response.headers()
            var i = 0
            val count: Int = headers.size()
            while (i < count) {
                logHeader(headers, i)
                i++
            }
            if (!logBody || !HttpHeaders.hasBody(response)) {
                logger.log("<-- END HTTP")
            } else if (bodyHasUnknownEncoding(response.headers())) {
                logger.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer: Buffer = source.buffer()
                var gzippedLength: Long? = null
                if ("gzip".equals(headers.get("Content-Encoding"), ignoreCase = true)) {
                    gzippedLength = buffer.size()
                    var gzippedResponseBody: GzipSource? = null
                    try {
                        gzippedResponseBody = GzipSource(buffer.clone())
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    } finally {
                        gzippedResponseBody?.close()
                    }
                }
                var charset: Charset? = UTF8
                val contentType: MediaType? = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                if (!isPlaintext(buffer)) {
                    logger.log("")
                    logger.log(
                        "<-- END HTTP (binary " + buffer.size().toString() + "-byte body omitted)"
                    )
                    return response
                }
                if (contentLength != 0L) {
                    logger.log("")
                    logger.log(buffer.clone().readString(charset))
                }
                if (gzippedLength != null) {
                    logger.log(
                        ("<-- END HTTP (" + buffer.size().toString() + "-byte, "
                                + gzippedLength.toString() + "-gzipped-byte body)")
                    )
                } else {
                    logger.log("<-- END HTTP (" + buffer.size().toString() + "-byte body)")
                }
            }
        }
        return response
    }

    @Throws(IOException::class)
    private fun logHeader(headers: Headers, i: Int) {
        val value = if (headersToRedact.contains(headers.name(i))) "â–ˆâ–ˆ" else headers.value(i)
        logger.log(headers.name(i).toString() + ": " + value)
    }

    companion object {
        private val UTF8: Charset = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size() < 64) buffer.size() else 64.toLong()
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint: Int = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }
        }

        private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
            val contentEncoding: String? = headers.get("Content-Encoding")
            return ((contentEncoding != null
                    ) && !contentEncoding.equals("identity", ignoreCase = true)
                    && !contentEncoding.equals("gzip", ignoreCase = true))
        }
    }
}
