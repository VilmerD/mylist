package se.lth.solid.vilmer

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.io.Serializable

class CardDataModel(var file: File?, var name: String, var tags: ArrayList<String>) : Serializable {
    var grade: Int = 0
}