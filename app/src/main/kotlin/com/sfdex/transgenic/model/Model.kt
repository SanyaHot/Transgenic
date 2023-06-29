package com.sfdex.transgenic.model

data class Model(
    val brand: String,              //HUAWEI
    val manufacturer: String,       //HUAWEI
    val model: String,              //MNA-AL00
    val device: String,             //Mona
    val board: String,              //Mona
    val product: String,            //Mona
    val specificPkg: String = "",   //com.host.name
    val fullModelName: String = ""  //HUAWEI P60 Pro
)
