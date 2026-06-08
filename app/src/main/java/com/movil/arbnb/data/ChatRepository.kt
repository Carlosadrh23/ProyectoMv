package com.movil.arbnb.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.movil.arbnb.Chat
import com.movil.arbnb.Message

object ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    fun getOrCreateChat(myId: String, otherId: String, myName: String, otherName: String, onResult: (String) -> Unit) {
        chatsCollection
            .whereArrayContains("participantIds", myId)
            .get()
            .addOnSuccessListener { snapshot ->
                val existingChat = snapshot.documents.find { doc ->
                    val ids = doc.get("participantIds") as? List<*>
                    ids?.contains(otherId) == true
                }

                if (existingChat != null) {
                    onResult(existingChat.id)
                } else {
                    val newChat = hashMapOf(
                        "participantIds" to listOf(myId, otherId),
                        "participantNames" to mapOf(myId to myName, otherId to otherName),
                        "lastMessage" to "",
                        "lastTimestamp" to System.currentTimeMillis()
                    )
                    chatsCollection.add(newChat).addOnSuccessListener { onResult(it.id) }
                }
            }
    }

    fun getMyChats(userId: String, onResult: (List<Chat>) -> Unit) {
        chatsCollection
            .whereArrayContains("participantIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Chat::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onResult(list.sortedByDescending { it.lastTimestamp })
            }
    }

    fun getMessages(chatId: String, onResult: (List<Message>) -> Unit) {
        chatsCollection.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onResult(messages)
            }
    }

    fun sendMessage(chatId: String, senderId: String, text: String) {
        val message = hashMapOf(
            "senderId" to senderId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        chatsCollection.document(chatId).collection("messages").add(message)
        
        chatsCollection.document(chatId).update(
            "lastMessage", text,
            "lastTimestamp", System.currentTimeMillis()
        )
    }
}
