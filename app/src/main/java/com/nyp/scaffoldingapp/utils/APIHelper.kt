package com.nyp.scaffoldingapp.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

object ApiHelper {

	private const val TIMEOUT_MS = 10_000

	suspend fun getArray(endpoint: String): JSONArray =
		withContext(Dispatchers.IO) {

			val connection = URL(endpoint)
				.openConnection() as HttpURLConnection

			connection.requestMethod = "GET"
			connection.connectTimeout = TIMEOUT_MS
			connection.readTimeout = TIMEOUT_MS

			try {
				val json = connection.inputStream
					.bufferedReader()
					.use { it.readText() }

				JSONArray(json)
			} finally {
				connection.disconnect()
			}
		}

	fun relativeDate(
		daysFromToday: Int,
		hour: Int,
		minute: Int
	): Long {
		return Calendar.getInstance().apply {
			add(Calendar.DAY_OF_YEAR, daysFromToday)
			set(Calendar.HOUR_OF_DAY, hour)
			set(Calendar.MINUTE, minute)
			set(Calendar.SECOND, 0)
			set(Calendar.MILLISECOND, 0)
		}.timeInMillis
	}
}