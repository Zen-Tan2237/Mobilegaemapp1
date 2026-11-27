package com.nyp.scaffoldingapp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ExternalURLHelper {
	fun openURL(context : Context, url : String)
	{
		// Convert URL to android URI
		val uri = Uri.parse(url.trim())
		// 2. Only allow web links
		if (uri.scheme != "http" && uri.scheme != "https") {
			Toast.makeText(
				context,
				"Invalid URL",
				Toast.LENGTH_SHORT
			).show()
			return
		}

		// 3. Ensure the URL has a hostname
		if (uri.host.isNullOrBlank()) {
			Toast.makeText(
				context,
				"Invalid web address",
				Toast.LENGTH_SHORT
			).show()
			return
		}

		// 4. Create an Intent to open the URL
		val intent = Intent(
			Intent.ACTION_VIEW,
			uri
		)

		// 5. Ask Android to launch a compatible application
		try {
			context.startActivity(intent)
		}
		catch (e: ActivityNotFoundException) {
			Toast.makeText(
				context,
				"No app available to open this link",
				Toast.LENGTH_LONG
			).show()
		}
		catch (e: SecurityException) {
			Toast.makeText(
				context,
				"Unable to open this link",
				Toast.LENGTH_LONG
			).show()
		}
	}
}