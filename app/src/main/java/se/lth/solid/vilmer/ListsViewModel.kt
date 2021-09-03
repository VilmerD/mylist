package se.lth.solid.vilmer

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ListsViewModel : ViewModel() {
    var myLists: ArrayList<CardList> = arrayListOf()
    var tagFilters: ArrayList<String> = arrayListOf()
    var filterGrade: Int = 0

    var displayingIndex = 0

    val size: Int
        get() = myLists.size

    val list: CardList
        get() = myLists[displayingIndex]

    /***
     * Filters and sorts the cards
     */
    fun getCardsFiltered(): ArrayList<CardDataModel> {
        Log.d("ListsViewModel", "Filtering cards.")
        var filteredCards = list.cards

        // Filters out the cards that don't contain all tags in the users filter
        if (tagFilters.isNotEmpty()) {
            filteredCards = ArrayList(filteredCards.filter { card: CardDataModel ->
                card.tags.containsAll(tagFilters)
            })
        }
        // Sorting the cards by rising or falling grade
        filteredCards.sortWith { c1: CardDataModel, c2: CardDataModel ->
            (c1.grade - c2.grade) * filterGrade
        }
        return filteredCards
    }

    /***
     * Deletes the list at the position, if there is a list there. If there is only 1 list, it is
     * replaced by a new list.
     */
    fun safeDeleteList(position: Int): Boolean {
        return if (position in 0 until size) {
            if (size > 1) {
                myLists.removeAt(position)
                displayingIndex = displayingIndex.coerceAtMost(myLists.size - 1)
            } else {
                myLists[0] = CardList()
            }
            true
        } else {
            false
        }
    }

    /***
     * Deletes the card at the position, if there is a card there.
     */
    fun safeDeleteCard(position: Int): Boolean {
        val cards = list.cards
        return if (position in 0 until cards.size) {
            cards.removeAt(position)
            true
        } else {
            false
        }
    }

}