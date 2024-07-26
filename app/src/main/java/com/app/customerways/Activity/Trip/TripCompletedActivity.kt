package com.app.customerways.Activity.Trip

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.app.customerways.Activity.BaseActivity
import com.app.customerways.Activity.HomeActivity
import com.app.customerways.Activity.MytripsActivity
import com.app.customerways.R
import com.app.customerways.databinding.ActivityTripCompletedBinding
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session

class TripCompletedActivity : BaseActivity() {


    lateinit var binding: ActivityTripCompletedBinding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_completed)

        binding = ActivityTripCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        session.setData(Constant.PRODUCT_LOCATION, "")
        session.setData(Constant.PRODUCT_TITLE, "")
        session.setData(Constant.PRODUCT_DESCRIPTION, "")
        session.setData(Constant.PRODUCT_FROM_DATE,"")
        session.setData(Constant.PRODUCT_TO_DATE,"")

        binding.btnMytrip.setOnClickListener {
            val intent = Intent(activity, MytripsActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
        // Handler to move to the next activity after 3 seconds

}
