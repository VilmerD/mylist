package se.lth.solid.vilmer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ListsViewModel(private val handle: SavedStateHandle) : ViewModel() {
    lateinit var myLists: ArrayList<CardList>
    var filters: ArrayList<String>? = null
    var displaying = 0

    val size: Int
        get() = myLists.size

    fun addList(list: CardList) {
        myLists.add(list)
    }

    fun addCard(card: CardDataModel) {
        myLists[displaying].cards.add(card)
    }

    fun getListName(): String {
        return myLists[displaying].name
    }

    fun getTags(): ArrayList<String> {
        return myLists[displaying].tags
    }

    fun getCardsFiltered(): ArrayList<CardDataModel> {
        return if (filters != null && filters!!.isNotEmpty()) {
            ArrayList(myLists[displaying].cards.filter { card: CardDataModel ->
                for (tag in card.tags) {
                    if (filters!!.contains(tag)) return@filter true
                }
                false
            })
        } else {
            myLists[displaying].cards
        }
    }

    fun safeDeleteList(position: Int) : Boolean {
        return if (size > 1 && position >= 0) {
            myLists.removeAt(position)
            displaying = displaying.coerceAtMost(myLists.size - 1)
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