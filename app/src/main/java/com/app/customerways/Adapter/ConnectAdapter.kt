package com.app.customerways.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.customerways.Activity.ChatsActivity
import com.app.customerways.Activity.FreePointsActivity
import com.app.customerways.Activity.ProfileinfoActivity
import com.app.customerways.Activity.PurchasepointActivity
import com.app.customerways.Model.Connect
import com.app.customerways.R
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.bumptech.glide.Glide

class ConnectAdapter(
    val activity: Activity,
    connect: java.util.ArrayList<Connect>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val connect: ArrayList<Connect>
    val activitys: Activity

    init {
        this.connect = connect
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_home_connect, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: Connect = connect[position]

        val session = Session(activity)

        if (report.online_status == "1") {
            holder.IV_online_status.visibility = View.VISIBLE
        } else {
            holder.IV_online_status.visibility = View.GONE
        }

        val online_status = report.online_status
        var status = ""
        if (online_status == "1") {
             status = "Online"
        } else {
             status = ""
        }

        holder.tvAge.text = report.age
//        holder.tvDistance.text = report.distance + "" + status


        val gender = report.gender

        if(gender == "male") {
            holder.ivGender.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.male_ic))
        }
        else {
            holder.ivGender.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.female_ic))
        }

        if (gender == "male") {
            holder.ivGenderColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.blue_200))
        } else {
            holder.ivGenderColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.primary))
        }


//        holder.itemView.setOnClickListener{
//            val intent = Intent(activity, ProfileinfoActivity::class.java)
//            activity.startActivity(intent)
//        }



        val point = session.getData(Constant.POINTS)


        holder.itemView.setOnClickListener {


           if (report.friend_user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.friend_user_id)
                activity.startActivity(intent)
            }



        }

        holder.ivProfile.setOnClickListener {
            val intent = Intent(activity, ProfileinfoActivity::class.java)
            intent.putExtra("name", report.name)
            intent.putExtra("chat_user_id", report.friend_user_id)
            intent.putExtra("id", report.id)
            session.setData("reciver_profile", report.profile)
            intent.putExtra("friend", report.friend)
            activity.startActivity(intent)

        }


       holder.tvName.text = report.name


        //holder.tvLatestseen.text = report.introduction is more than one line mean en with dot
        if (report.introduction!!.length > 45) {
            holder.tvLatestseen.text = report.introduction!!.substring(0, 45) + ".."
        } else {
            holder.tvLatestseen.text = report.introduction
        }



        Glide.with(activitys)
            .load(report.profile)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.ivProfile)


    }


    override fun getItemCount(): Int {
        return connect.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
       val tvName: TextView = itemView.findViewById(R.id.tvName)
       val tvLatestseen: TextView = itemView.findViewById(R.id.tvLatestseen)
        val ivProfile:ImageView = itemView.findViewById(R.id.ivProfile)
        val IV_online_status:ImageView = itemView.findViewById(R.id.IV_online_status)
        val  ivGender:ImageView = itemView.findViewById(R.id.ivGender)
        val ivGenderColor:LinearLayout = itemView.findViewById(R.id.ivGenderColor)
        val  tvAge :TextView = itemView.findViewById(R.id.tvAge)
//        val tvDistance  :TextView = itemView.findViewById(R.id.tvDistance)


    }


}