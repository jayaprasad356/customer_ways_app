package com.app.customerways.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.app.customerways.Model.ChatModel
import com.app.customerways.R
import com.app.customerways.databinding.ReceiverChatMessageBinding
import com.app.customerways.databinding.SenderChatMessageBinding
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class ChatAdapter(
    private val conversations: List<ChatModel?>,
    private val onClick: (ChatModel) -> Unit,
    private var session: Session
) : RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding: ViewBinding = when (viewType) {
            0 -> ReceiverChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            1 -> SenderChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val chatMessage = conversations[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount(): Int = conversations.size

    fun getItemInfo(position: Int): ChatModel? = conversations[position]

    override fun getItemViewType(position: Int): Int {
        val chatMessage = conversations[position]
        val currentUser = session.getData(Constant.NAME)
        return if (chatMessage?.sentBy == currentUser) 1 else 0
    }

    inner class ItemHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatModel?) {
            chatMessage?.let {
                when (binding) {
                    is SenderChatMessageBinding -> {
                        binding.tvMessage.text = it.message
                        binding.tvTime.text = it.dateTime?.run {
                            formatDateTime(toLong())
                        } ?: ""
                        Glide.with(binding.root.context).load(session.getData(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)
                    }

                    is ReceiverChatMessageBinding -> {
                        binding.tvMessage.text = it.message
                        binding.tvTime.text =
                            it.dateTime?.run {
                                formatDateTime(toLong())
                            } ?: ""

                        Glide.with(binding.root.context).load(session.getData("reciver_profile")).placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)

                    }

                    else -> {}
                }
            }
        }
    }

    @Throws(TypeCastException::class)
    private fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
