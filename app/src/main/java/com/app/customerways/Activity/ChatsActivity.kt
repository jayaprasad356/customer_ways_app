package com.app.customerways.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.customerways.Adapter.ChatAdapter
import com.app.customerways.Model.ChatModel
import com.app.customerways.R
import com.app.customerways.databinding.ActivityChatsBinding
import com.app.customerways.extentions.fetchMessages
import com.app.customerways.extentions.logError
import com.app.customerways.extentions.logInfo
import com.app.customerways.extentions.makeToast
import com.app.customerways.extentions.observeTypingStatus
import com.app.customerways.extentions.observeUserStatus
import com.app.customerways.extentions.playReceiveTone
import com.app.customerways.extentions.popUpMenu
import com.app.customerways.extentions.setUserStatus
import com.app.customerways.extentions.updateMessagesForSender
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.app.customerways.listeners.OnMessagesFetchedListener
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class ChatsActivity : BaseActivity(), OnMessagesFetchedListener {
    lateinit var binding: ActivityChatsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private val firebaseDatabase: FirebaseDatabase =
        Firebase.database("https://customerways-default-rtdb.firebaseio.com/")
    private val databaseReference: DatabaseReference = firebaseDatabase.reference
    private var chatReference: DatabaseReference? = null
    private var chatAdapter: ChatAdapter? = null
    private var messages = mutableListOf<ChatModel?>()

    private var senderId = ""
    private var receiverId = ""
    private var senderName: String? = null
    private var receiverName: String? = null

    private lateinit var soundPool: SoundPool
    private var sentTone: Int = 0
    private var receiveTone: Int = 0

    private var isConversationsFetching: Boolean = true

    private lateinit var typingStatusReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        senderId = session.getData(Constant.USER_ID)
        receiverId = intent.getStringExtra("chat_user_id").toString()
        senderName = session.getData(Constant.NAME)
        receiverName = intent.getStringExtra("name")
        binding.tvName.text = receiverName
        chatReference =
            databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
        typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")


        Glide.with(this)
            .load(session.getData("reciver_profile"))
            .placeholder(R.drawable.profile_placeholder)
            .into(binding.ivProfile)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        sentTone = soundPool.load(this, R.raw.sent_new, 1)
     //  receiveTone = soundPool.load(this, R.raw.recieve_tone, 1)

        binding.sendButton.setOnClickListener {
            val message = binding.messageEdittext.text.toString()
            if (message.isNotEmpty()) {
                isBlocked(senderId, receiverId) { isBlocked ->
                    if (isBlocked) {
                        makeToast("You cannot send messages to this user blocked.")
                    } else {
                        senderName?.let { sName ->
                            receiverName?.let { rName ->
                                updateMessagesForSender(
                                    databaseReference = databaseReference,
                                    senderID = senderId,
                                    receiverID = receiverId,
                                    senderName = senderName!!,
                                    receiverName = receiverName!!,
                                    message = message,
                                    soundPool = soundPool,
                                    sentTone = sentTone
                                )
                                binding.messageEdittext.text.clear()
                            } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")
                        } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")
                    }
                }
            } else {
                makeToast("Enter text to send")
            }
        }


        // Inside your onCreate method
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) { // keyboard is opened
                binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
            }
        }

        binding.messageEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingStatusReference.setValue(s.toString().isNotEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivMore.setOnClickListener {
            showPopupMenu()
        }

        fetchMessages(chatReference, this@ChatsActivity) {
            isConversationsFetching = it
        }
        chatReference?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                logInfo(CHATS_ACTIVITY, "from firebase child added - $chatModel")
                onMessageAdded(chatModel)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                onMessageRemoved(chatModel)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                // Implement error handling here
                logError(CHATS_ACTIVITY, "Firebase child event listener was cancelled: ${error.message}")
                makeToast("An error occurred: ${error.message}")
            }
        })



        observeTypingStatus(firebaseDatabase, receiverId)
        setUserStatus(firebaseDatabase, senderId, true)
        observeUserStatus(firebaseDatabase, receiverId)
    }


    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.ivMore)
        popupMenu.inflate(R.menu.chat_options_menu)

        // Check current block status
        isBlocked(senderId, receiverId) { blocked ->
            val blockMenuItem = popupMenu.menu.findItem(R.id.menu_block_chat)
            blockMenuItem.title = if (blocked) "Unblock User" else "Block User"

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_block_chat -> {
                        updateBlockStatus(senderId, receiverId, !blocked)
                        true
                    }
                    R.id.menu_clear_chat -> {
                        clearChatInFirebase(senderName ?: "", receiverName ?: "",receiverId)
                        true
                    }
                    R.id.menu_report -> {
                        add_freind()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }


    private fun add_freind() {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.FRIEND_USER_ID] = receiverId!!
        params[Constant.FRIEND] = "2"

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(
                            activity,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.ADD_FRIENDS, params, true, 1)
    }



    private fun updateBlockStatus(senderId: String, receiverId: String, isBlocked: Boolean) {
        val blockReference = databaseReference.child("blocked_users").child(senderId).child(receiverId)
        blockReference.setValue(isBlocked).addOnSuccessListener {
            val message = if (isBlocked) {
                "User has been blocked"
            } else {
                "User has been unblocked"
            }
            makeToast(message)
        }.addOnFailureListener {
            makeToast("Failed to update block status")
        }
    }



    private fun isBlocked(senderId: String, receiverId: String, callback: (Boolean) -> Unit) {
        val blockReference = databaseReference.child("blocked_users").child(senderId).child(receiverId)
        blockReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isBlocked = snapshot.getValue(Boolean::class.java) ?: false
                callback(isBlocked)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }


    private fun clearChatInFirebase(senderName: String, receiverName: String, receiverID: String) {
        val senderChatReference = databaseReference.child("CHATS_V2").child(senderName).child(receiverName)
        val receiverChatReference = databaseReference.child("CHATS_V2").child(receiverName).child(senderName)

        senderChatReference.removeValue().addOnSuccessListener {
            deletechat(receiverID)
            makeToast("Chat cleared successfully")
            messages.clear()
            chatAdapter?.notifyDataSetChanged()
        }.addOnFailureListener {
            makeToast("Failed to clear chat")
        }

        receiverChatReference.removeValue().addOnFailureListener {
            makeToast("Failed to clear chat for receiver")
        }
    }


    fun deletechat(
        receiverID: String
    ) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.CHAT_USER_ID] = receiverID
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                   //     Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                        logError(CHATS_ACTIVITY, "Message failed to update to the API.")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.DELETE_CHAT, params, false, 1)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onMessagesFetched(conversations: MutableList<ChatModel?>) {
        var lastDisplayedDateTime: Long? = null
        messages = conversations
        if (messages.isNotEmpty()) {
            messages.sortBy { it?.dateTime }
            initializeRecyclerView(messages)
            binding.RVChats.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                        val chatModel = chatAdapter?.getItemInfo(firstVisibleItemPosition)
                        val dateTime = chatModel?.dateTime

                        if (dateTime != lastDisplayedDateTime) {
                            lastDisplayedDateTime = dateTime
                            val actualDate = dateTime?.let { Date(it) }
                            val formattedDate = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
                            binding.tvDate.visibility = View.VISIBLE
                            binding.tvDate.text = actualDate?.let { formattedDate.format(it) }
                        }
                    }
                }
            })
            binding.RVChats.scrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
        } else {
            //Display empty conversation placeholder.
            logInfo("conversations", "Conversations are empty.")
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageAdded(chatModel: ChatModel?) {
        chatModel?.let { nonEmptyChatModel ->
            when {
                messages.isEmpty() -> {
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Message added are empty")
                    messages.add(nonEmptyChatModel)
                    initializeRecyclerView(messages)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {
                        playReceiveTone(
                            soundPool,
                            receiveTone
                        )
                    } else {

                    }
                }

                messages.none { existingChatModel -> existingChatModel?.chatID == chatModel.chatID } -> {
                    Log
                        .e("ADDED_NON_EMPTY_CHAT_MODEL", nonEmptyChatModel.toString())
                    messages.add(nonEmptyChatModel)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {
                        playReceiveTone(
                            soundPool,
                            receiveTone
                        )
                    }
                    messages.sortBy { it?.dateTime }
                    initializeRecyclerView(messages)
                    receiverName?.let { nonEmptyReceiverName ->
                        senderName?.let { nonEmptySenderName ->
                            nonEmptyChatModel.chatID?.let { nonEmptyChatID ->
                                //Todo : This causing the bug in the app. Fix this to enable tick functionality
//                                updateMessageSeenStatus(
//                                    receiverName = nonEmptyReceiverName,
//                                    senderName = nonEmptySenderName,
//                                    chatID = nonEmptyChatID
//                                )
                            }
                        }
                    }
                    binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Message added: $chatModel")
                }

                else -> {
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Duplicate message ignored")
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageChanged(chatModel: ChatModel?) {
        logInfo(CHATS_ACTIVITY, "Message changed - $chatModel")
        chatAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageRemoved(chatModel: ChatModel?) {
        messages.remove(chatModel)
        chatAdapter?.notifyDataSetChanged()
        logInfo("$CHATS_ACTIVITY onMessageRemoved", "Message removed: $chatModel")
    }

    override fun onError(errorMessage: String) {
        makeToast("Error: $errorMessage")
        logError("$CHATS_ACTIVITY onError", "Error: $errorMessage")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeRecyclerView(conversations: MutableList<ChatModel?>) {
        chatAdapter = ChatAdapter(conversations, onClick = {}, session)
        chatAdapter?.notifyDataSetChanged()
        binding.RVChats.apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = chatAdapter
            scrollToPosition(
                chatAdapter?.itemCount?.minus(1) ?: 0
            ) // Ensure scrolling to the last message
            invalidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Set user offline status with last seen time
        setUserStatus(
            firebaseDatabase,
            senderId,
            false
        )
    }

    override fun onStop() {
        super.onStop()
        // Set user offline status with last seen time
        setUserStatus(firebaseDatabase, senderId, false)
    }

    override fun onStart() {
        super.onStart()
        // Set user online status
        setUserStatus(firebaseDatabase, senderId, true)
    }


}

const val CHATS_ACTIVITY = "ChatsActivity"
