package com.sfdex.transgenic.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sfdex.transgenic.R
import com.sfdex.transgenic.ui.theme.TransgenicTheme

private const val TAG = "MainActivity"
private var context: Context? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContent {
            TransgenicTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    //LocalContext.current.startActivity(Intent(context, ModelActivity::class.java))
    Column(
        modifier = Modifier
            .padding(30.dp)
    ) {
        Text(text = deviceInfo(), fontSize = 18.sp)

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                //Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
                context?.startActivity(Intent(context, ModelActivity::class.java))
            }) {
            Text(text = stringResource(id = R.string.change_model))
        }
    }
}

private fun deviceInfo(): String {
    val manufacturer = Build.MANUFACTURER
    val brand = Build.BRAND
    val model = Build.MODEL
    val device = Build.DEVICE
    val product = Build.PRODUCT
    val board = Build.BOARD
    val codename = Build.VERSION.CODENAME
    val sdkInt = Build.VERSION.SDK_INT
    val release = Build.VERSION.RELEASE

    return buildString {
        append("manufacturer = $manufacturer\n")
        append("brand = $brand\n")
        append("model = $model\n")
        append("device = $device\n")
        append("product = $product\n")
        append("board = $board\n")
        append("codename = $codename\n")
        append("sdkInt = $sdkInt\n")
        append("release = $release\n")
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TransgenicTheme {
        Greeting()
    }
}