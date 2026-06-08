package com.movil.arbnb.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.movil.arbnb.Chat
import com.movil.arbnb.Message

object ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    fun getMyChats(userId: String, onResult: (List<Chat>) -> Unit) {
        chatsCollection
            .whereArrayContains("participantIds", userId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.toObjects(Chat::class.java) ?: emptyList()
                onResult(list)
            }
    }

    fun getOrCreateChat(user1: String, name1: String, user2: String, name2: String, onResult: (String) -> Unit) {
        val participants = listOf(user1, user2).sorted()
        chatsCollection
            .whereEqualTo("participantIds", participants)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onResult(snapshot.documents[0].id)
                } else {
                    val newChat = Chat(
                        participantIds = participants,
                        participantNames = mapOf(user1 to name1, user2 to name2),
                        lastMessage = "Nuevo chat",
                        lastTimestamp = System.currentTimeMillis()
                    )
                    val newDoc = chatsCollection.document()
                    newDoc.set(newChat.copy(id = newDoc.id))
                        .addOnSuccessListener { onResult(newDoc.id) }
                }
            }
    }

    fun sendMessage(chatId: String, message: Message, onResult: (Boolean) -> Unit) {
        val messagesCollection = chatsCollection.document(chatId).collection("messages")
        val newDoc = messagesCollection.document()
        val messageWithId = message.copy(id = newDoc.id, timestamp = System.currentTimeMillis())
        
        newDoc.set(messageWithId)
            .addOnSuccessListener {
                chatsCollection.document(chatId).update(
                    "lastMessage", message.text,
                    "lastTimestamp", messageWithId.timestamp
                )
                onResult(true)
            }
            .addOnFailureListener { onResult(false) }
    }

    fun getMessages(chatId: String, onResult: (List<Message>) -> Unit) {
        chatsCollection.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.toObjects(Message::class.java) ?: emptyList()
                onResult(list)
            }
    }
}
