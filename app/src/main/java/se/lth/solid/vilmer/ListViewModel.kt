package se.lth.solid.vilmer

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ListViewModel(private val handle: SavedStateHandle) : ViewModel() {
    var myLists: ArrayList<CardList> = if (handle.contains(myListsKey)) {
        handle.get<ArrayList<CardList>>(myListsKey)!!
    } else {
        arrayListOf()
    }

    fun addList(name: String) {
        myLists.add(CardList(name, arrayListOf()))
    }

    fun addCard(image: Bitmap?, name: String, tags: Array<String>?) {
        myLists[0].cards.add(CardDataModel(image, name, tags))
    }

    companion object {
        const val myListsKey = "LISTS_KEY"
    }

}