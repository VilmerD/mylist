package se.lth.solid.vilmer

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

    fun addCard(card: CardDataModel) {
        myLists[0].cards.add(card)
    }

    companion object {
        const val myListsKey = "LISTS_KEY"
    }

}