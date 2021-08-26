package se.lth.solid.vilmer

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.io.Serializable

/***
 * A simple object that holds the data for a card
 */
class CardDataModel(var file: File?, var name: String, var tags: ArrayList<String>) : Serializable {
    var grade: Int = 0
}