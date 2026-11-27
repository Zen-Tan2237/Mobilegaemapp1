
package com.nyp.scaffoldingapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.net.HttpURLConnection
import java.net.URL

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Extract the video ID from a YouTube URL
fun getYouTubeVideoId(videoUrl: String): String? {
	val uri = Uri.parse(videoUrl)
	val host = uri.host?.lowercase()?.removePrefix("www.")
		?: return null

	val id = when (host) {
		"youtube.com", "m.youtube.com" ->
			if (uri.path == "/watch")
				uri.getQueryParameter("v")
			else
				uri.pathSegments.getOrNull(1)

		"youtu.be" ->
			uri.pathSegments.firstOrNull()

		else -> null
	}

	return id?.takeIf {
		it.matches(Regex("[A-Za-z0-9_-]{11}"))
	}
}

// Download the thumbnail on the IO dispatcher
suspend fun downloadThumbnail(url: String): Bitmap? =
	withContext(Dispatchers.IO) {
		var connection: HttpURLConnection? = null

		try {
			connection = URL(url)
				.openConnection() as HttpURLConnection

			connection.connectTimeout = 5000
			connection.readTimeout = 5000

			connection.inputStream.use { stream ->
				BitmapFactory.decodeStream(stream)
			}
		} catch (e: Exception) {
			null
		} finally {
			connection?.disconnect()
		}
	}

@Composable
fun YouTubeThumbnail(videoUrl: String) {
	val videoId = remember(videoUrl) {
		getYouTubeVideoId(videoUrl)
	}

	var bitmap by remember(videoId) {
		mutableStateOf<Bitmap?>(null)
	}

	var loading by remember(videoId) {
		mutableStateOf(videoId != null)
	}

	LaunchedEffect(videoId) {
		if (videoId != null) {
			val thumbnailUrl =
				"https://img.youtube.com/vi/$videoId/mqdefault.jpg"

			bitmap = downloadThumbnail(thumbnailUrl)
			loading = false
		}
	}

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.aspectRatio(16f / 9f)
			.background(Color.DarkGray)
	) {
		when {
			bitmap != null -> Image(
				bitmap = bitmap!!.asImageBitmap(),
				contentDescription = "YouTube thumbnail",
				modifier = Modifier.fillMaxSize(),
				contentScale = ContentScale.Crop
			)

			loading -> CircularProgressIndicator()

			else -> Text(
				text = "Thumbnail unavailable",
				color = Color.White
			)
		}
	}
}
