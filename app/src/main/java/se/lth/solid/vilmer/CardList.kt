package se.lth.solid.vilmer
import java.io.Serializable

class CardList(var name: String, var cards: ArrayList<CardDataModel>, var tags: ArrayList<String>) : Serializable {
}