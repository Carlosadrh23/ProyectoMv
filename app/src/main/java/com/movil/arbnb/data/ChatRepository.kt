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
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Chat::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                // Sort locally since index might be missing in Firestore
                onResult(list.sortedByDescending { it.lastTimestamp })
            }
    }

    fun getOrCreateChat(myId: String, myName: String, otherId: String, otherName: String, onResult: (String) -> Unit) {
        // We use a sorted list of IDs to check for existing chat
        val participants = listOf(myId, otherId).sorted()
        
        chatsCollection
            .whereEqualTo("participantIds", participants)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onResult(snapshot.documents[0].id)
                } else {
                    val newChat = Chat(
                        participantIds = participants,
                        participantNames = mapOf(myId to myName, otherId to otherName),
                        lastMessage = "Nuevo chat",
                        lastTimestamp = System.currentTimeMillis()
                    )
                    val newDoc = chatsCollection.document()
                    newDoc.set(newChat.copy(id = newDoc.id))
                        .addOnSuccessListener { onResult(newDoc.id) }
                }
            }
    }

    fun sendMessage(chatId: String, message: Message, onComplete: (Boolean) -> Unit) {
        val messagesCollection = chatsCollection.document(chatId).collection("messages")
        val newDoc = messagesCollection.document()
        val messageWithId = message.copy(id = newDoc.id)
        
        newDoc.set(messageWithId)
            .addOnSuccessListener {
                chatsCollection.document(chatId).update(
                    "lastMessage", message.text,
                    "lastTimestamp", message.timestamp
                )
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
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
}
