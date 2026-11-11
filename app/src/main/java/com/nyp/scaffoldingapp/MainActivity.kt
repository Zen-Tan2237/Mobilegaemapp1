package com.nyp.scaffoldingapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyp.scaffoldingapp.ui.pages.Page1
import com.nyp.scaffoldingapp.ui.pages.Page2
import com.nyp.scaffoldingapp.ui.pages.Page3
import com.nyp.scaffoldingapp.ui.pages.Page4
import com.nyp.scaffoldingapp.ui.theme.GlassBackground
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme
import com.nyp.scaffoldingapp.ui.theme.glassSurface

/** Preserved four-tab navigation from the original MainActivity. */
enum class Tab(val label: String, @DrawableRes val icon: Int) {
    PAGE1("Page 1", R.drawable.ic_home),
    PAGE2("Page 2", R.drawable.ic_email),
    PAGE3("Page 3", R.drawable.ic_list),
    PAGE4("Page 4", R.drawable.ic_star)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val prefs = remember(context) { context.getSharedPreferences("football_appearance", Context.MODE_PRIVATE) }
            // null means follow phone preference, while the first manual toggle saves an explicit choice.
            var darkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", defaultDarkMode(context))) }
            ScaffoldingTheme(darkTheme = darkMode) {
                AppScaffold(darkMode = darkMode, onToggleTheme = {
                    darkMode = !darkMode
                    prefs.edit().putBoolean("dark_mode", darkMode).apply()
                })
            }
        }
    }
}

private fun defaultDarkMode(context: Context): Boolean =
    (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES

@Composable
fun AppScaffold(darkMode: Boolean, onToggleTheme: () -> Unit) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.PAGE1) }
    GlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                        .glassSurface(30).padding(horizontal = 6.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Tab.entries.forEach { tab ->
                        val active = tab == selectedTab
                        val ink by animateColorAsState(
                            if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            animationSpec = tween(250), label = "tabTint"
                        )
                        Column(
                            Modifier.weight(1f).clip(CircleShape).clickable { selectedTab = tab }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(painterResource(tab.icon), contentDescription = tab.label,
                                tint = ink, modifier = Modifier.size(23.dp))
                            Spacer(Modifier.height(3.dp))
                            Text(tab.label, color = ink, style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                Crossfade(targetState = selectedTab, animationSpec = tween(230), label = "pageTransition") { tab ->
                    when (tab) {
                        Tab.PAGE1 -> Page1()
                        Tab.PAGE2 -> Page2()
                        Tab.PAGE3 -> Page3()
                        Tab.PAGE4 -> Page4()
                    }
                }
            }
        }
    }
}
