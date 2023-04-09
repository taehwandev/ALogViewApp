package tech.thdev.android.logview.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import tech.thdev.android.log.view.ALogViewService
import tech.thdev.android.logview.example.compose.MainScreen
import tech.thdev.android.logview.example.ui.theme.MainTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(it)
                    ) {
                        MainScreen {
                            if (Settings.canDrawOverlays(this@MainActivity).not()) {
                                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                            } else {
                                startService(ALogViewService.newInstance(this@MainActivity))
                                Timber.i("Sample start!!!")
                            }
                        }
                    }
                }
            }
        }
    }
}