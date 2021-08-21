package se.lth.solid.vilmer

import android.util.Log
import androidx.lifecycle.ViewModel

class ListsViewModel : ViewModel() {
    lateinit var myLists: ArrayList<CardList>
    var tagFilters: ArrayList<String> = arrayListOf()
    var filterGrade: Int = 0
    var filterName: Int = 0

    var displayingIndex = 0

    val size: Int
        get() = myLists.size

    val list: CardList
        get() = myLists[displayingIndex]

    fun getCardsFiltered(): ArrayList<CardDataModel> {
        Log.d("ListsViewModel", "Filtering cards.")
        var filteredCards = myLists[displayingIndex].cards
        if (tagFilters.isNotEmpty()) {
            filteredCards = ArrayList(filteredCards.filter { card: CardDataModel ->
                card.tags.containsAll(tagFilters)
            })
        }
        filteredCards.sortWith { c1: CardDataModel, c2: CardDataModel ->
            (c1.grade - c2.grade) * filterGrade
        }
        return filteredCards
    }

    fun safeDeleteList(position: Int): Boolean {
        return if (size > 1) {
            myLists.removeAt(position)
            displayingIndex = displayingIndex.coerceAtMost(myLists.size - 1)
            true
        } else {
            myLists[0] = CardList()
            true
        }
    }

    fun safeDeleteCard(position: Int): Boolean {
        val cards = myLists[displayingIndex].cards
        return if (position >= 0 && position < cards.size) {
            cards.removeAt(position)
            true
        } else {
            false
        }
    }
}