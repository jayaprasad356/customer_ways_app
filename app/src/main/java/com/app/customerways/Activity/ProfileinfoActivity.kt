package com.app.customerways.Activity

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.customerways.R
import com.app.customerways.databinding.ActivityProfileinfoBinding
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.json.JSONException
import org.json.JSONObject

class ProfileinfoActivity : BaseActivity() {
    lateinit var binding: ActivityProfileinfoBinding
    lateinit var activity: Activity
    lateinit var session: Session
    var user_id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileinfo)
        binding = ActivityProfileinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener{
            onBackPressed()
        }



        activity = this
        session = Session(activity)
         user_id = intent.getStringExtra("chat_user_id")


        val id = intent.getStringExtra("id")
        val friend = intent.getStringExtra("friend")
        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("profile")


         var friend_data = "" + friend

        if (friend_data == "0") {
            binding.ivaddFriend.setBackgroundResource(R.drawable.add_account)
            binding.tvAddFriend.text = "Add to Friend"
        } else if (friend_data == "1") {
            binding.ivaddFriend.setBackgroundResource(R.drawable.added_frd)
            binding.tvAddFriend.text = "Friend Added"
        }


        binding.rlAddFriend.setOnClickListener {
            // Change the background of rlAddFriend
            if (friend_data == "0") {
                val friend = "1"
                friend_data = "1"
                add_freind(binding.ivaddFriend, binding.tvAddFriend, user_id,friend)
            } else if (friend_data == "1"   ) {
                val friend = "2"
                friend_data = "0"
                add_freind(binding.ivaddFriend, binding.tvAddFriend, user_id,friend)
            }




        }

        binding.rlChat.setOnClickListener {
            if (user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                session.setData("reciver_profile", profile )
                intent.putExtra("chat_user_id", user_id)
                activity.startActivity(intent)
            }
        }



        userdetails(user_id)
        profile_view(user_id)


    }


    private fun userdetails(user_id: String?) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = user_id.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)


                        Glide.with(activity).load(jsonobj.getString(Constant.COVER_IMG)).placeholder(R.drawable.placeholder_bg).into(binding.ivCover)
                        Glide.with(activity).load(jsonobj.getString(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder).into(binding.civProfile)





                        binding.tvName.text = jsonobj.getString(Constant.NAME)
                        binding.tvUsername.text = "@"+jsonobj.getString(Constant.UNIQUE_NAME)
                        binding.tvPlace.text = jsonobj.getString(Constant.CITY) + ", " + jsonobj.getString(Constant.STATE)





                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.OTHER_USER_DETAILS, params, true, 1)
    }


    private fun add_freind(ivaddFriend: ImageView, tvAddFriend: TextView, user_id: String?, friend: String) {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.FRIEND_USER_ID] = user_id!!
        params[Constant.FRIEND] = friend

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                        if (friend == "1") {
                            ivaddFriend.setBackgroundResource(R.drawable.added_frd)
                            tvAddFriend.text = "Friend Added"

                        } else if (friend == "2") {
                            ivaddFriend.setBackgroundResource(R.drawable.add_account)
                            tvAddFriend.text = "Add to Friend"
                        }

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()


                    } else {
                       Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.ADD_FRIENDS, params, true, 1)
    }
    private fun profile_view(user_id: String?) {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.PROFILE_USER_ID] = user_id!!
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {



                     //   Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()


                    } else {
                       // Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.PROFILE_VIEW, params, true, 1)
    }
}