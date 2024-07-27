package com.app.customerways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.app.customerways.R
import com.app.customerways.databinding.ActivitySellerStatusBinding
import com.app.customerways.helper.Session

class SellerStatusActivity : BaseActivity() {
    lateinit var binding: ActivitySellerStatusBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_status)
        binding = ActivitySellerStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        val extras = intent.extras
        if (extras != null) {
            val sellerStatus = extras.getString("sellerStatus")
            if (sellerStatus != null) {
                if (sellerStatus == "2") {
                    val statusMessage = "pending for approval"
                    binding.tvStatusMessage.text = statusMessage
                } else if (sellerStatus == "3") {
                    val statusMessage = "approval rejected"
                    binding.tvStatusMessage.text = statusMessage
                }
            }
        }

        binding.ibBack.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}