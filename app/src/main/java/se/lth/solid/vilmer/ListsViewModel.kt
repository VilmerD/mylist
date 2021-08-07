package se.lth.solid.vilmer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ListsViewModel(private val handle: SavedStateHandle) : ViewModel() {
    lateinit var myLists: ArrayList<CardList>
    var displaying = 0

    fun size(): Int {
        return myLists.size
    }

    fun addList(list: CardList) {
        myLists.add(list)
    }

    fun addCard(card: CardDataModel) {
        myLists[displaying].cards.add(card)
    }

    fun getCurrent(): CardList {
        return myLists[displaying]
    }

    fun safeDeleteList(position: Int) : Boolean {
        return if (myLists.size > 1 && position >= 0) {
            myLists.removeAt(position)
            true
        } else {
            false
        }
    }

    fun safeDeleteCard(position: Int) : Boolean {
        val cards = myLists[displaying].cards
        return if (position >= 0 && position < cards.size) {
            cards.removeAt(position)
            true
        } else {
            false
        }
    }
}