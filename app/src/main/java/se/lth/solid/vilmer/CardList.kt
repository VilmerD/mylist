package se.lth.solid.vilmer

import java.io.Serializable

class CardList(var name: String = "My List") : Serializable {
    var cards: ArrayList<CardDataModel> = arrayListOf()
    var tags: ArrayList<String> = arrayListOf()
}