package se.lth.solid.vilmer

import java.io.File
import java.io.Serializable

class CardDataModel(var file: File?, var name: String, var tags: ArrayList<String>) : Serializable {
    var grade: Int = 0
}