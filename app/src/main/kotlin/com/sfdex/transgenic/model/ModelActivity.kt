package com.sfdex.transgenic.model

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.sfdex.transgenic.R
import com.sfdex.transgenic.ui.theme.Pink80
import com.sfdex.transgenic.ui.theme.Purple40
import com.sfdex.transgenic.ui.theme.TransgenicTheme
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

//import androidx.compose.runtime.remember
//import below to use 'by remember'
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue

private const val TAG = "ModelActivity"

class ModelActivity : ComponentActivity() {
    private val brands = ArrayList<String>()

    private val allBrandsModels = ArrayList<Model>()

    private var models = mutableStateListOf<Model>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readModelsFromAssets()
        setContent {
            TransgenicTheme {
                //MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(
                            title = {
                                Text(text = getString(R.string.app_name))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Pink80,
                                titleContentColor = Color.Red,
                                actionIconContentColor = Purple40
                            ),
                            actions = {
                                Icon(Icons.Filled.Favorite, contentDescription = "")
                            })
                    }) {
                        it.toString()
                        ChooseModels()
                    }
                }
            }
        }
    }

    @Composable
    fun ChooseModels() {
        models.addAll(allBrandsModels)
        Row {
            //var isExpanded by remember { mutableStateOf(false) }
            LazyColumn(
                modifier = Modifier.weight(1.0f, false),
                contentPadding = PaddingValues(10.dp, 5.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(brands) { brand ->
                    Text(text = brand, Modifier.clickable {
                        models.clear()
                        models.addAll(
                            allBrandsModels.filter { it.brand == brand }
                        )
                    })
                    Divider()
                }
            }
            DeviceList(models = models, modifier = Modifier.weight(2.0f, false))
        }
    }

    @Composable
    fun DeviceList(models: MutableList<Model>, modifier: Modifier) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(10.dp, 5.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(models) { model ->
                Text(text = model.fullModelName, Modifier.clickable {
                    val ret = saveModel(model)
                    Toast.makeText(
                        this@ModelActivity,
                        if (ret) "Success" else "failure",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                Divider()
            }
        }
    }

    private fun saveModel(model: Model): Boolean {
        val filePath = "/data/local/tmp/genetically-modified-phone.json"
        //val model = Model("HUAWEI", "HUAWEI", "DCO-AL00", "Draco", "Draco", "Draco", "", "")
        try {
            val modelInfoStr = Gson().toJson(model)
            File(filePath).writeText(modelInfoStr)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun readModelsFromAssets() {
        //#model,dtype,brand,brand_title,code,code_alias,model_name,ver_name
        val ins = assets.open("models.csv")
        fun parse(index: Int, s: String, info: MutableList<String>) {
            if (index > 0) {
                if (s.contains("\"") && info[index - 1].contains("\"")) {
                    if (index > 2 && info[index - 2] == "{") {
                        info[index - 2] =
                            "${info[index - 1].removePrefix("\"")},${s.removeSuffix("\"")}"
                        info[index - 1] = "{"
                        info[index] = "{"
                    } else {
                        info[index - 1] = "${info[index - 1]},$s"
                        info[index] = "{"
                    }
                } else if (((index > 2 && info[index - 2] == "{") || info[index - 1] == "{")
                    && !s.contains("\"")
                ) {
                    info[index - 1] = s
                    info[index] = "{"
                }
            }
        }

        val bufferReader = BufferedReader(InputStreamReader(ins))
        bufferReader.useLines {
            it.forEach { line ->
                //Log.d(TAG, "readModelsFromAssets: $line")
                if (!line.startsWith("#")) {
                    val info = line.split(",").toMutableList()
                    info.forEachIndexed { index, s ->
                        parse(index, s, info)
                    }
                    if (!brands.contains(info[2])) {
                        brands.add(info[2])
                    }
                    allBrandsModels.add(
                        Model(
                            brand = info[2],
                            manufacturer = info[2],
                            model = info[0],
                            device = info[5],
                            board = info[5],
                            product = info[5],
                            fullModelName = info[6]
                        )
                    )
                }
            }
        }
        bufferReader.close()
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceListPreview() {
    TransgenicTheme {
        //DeviceList()
    }
}