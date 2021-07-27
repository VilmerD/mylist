package se.lth.solid.vilmer

import android.graphics.Bitmap
import java.io.Serializable

class CardDataModel(val image: Bitmap?, val name: String, val tags: Array<String>?) : Serializable {
    private val serialVersionUID: Long = -29238982928391L
    var counts: Int = 0

    fun iterateCounts() : Int{
        counts++
        return counts
    }
}