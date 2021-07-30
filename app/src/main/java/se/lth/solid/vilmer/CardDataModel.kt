package se.lth.solid.vilmer

import java.io.File
import java.io.Serializable

class CardDataModel(var file: File, var name: String, var tags: Array<String>?) : Serializable {
    private val serialVersionUID: Long = -29238982928391L
    var counts: Int = 0

    fun iterateCounts() : Int{
        counts++
        return counts
    }
}