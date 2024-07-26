package com.app.customerways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.customerways.Fragment.InterestFragment
import com.app.customerways.R
import com.app.customerways.databinding.ActivityIdverficationBinding
import com.app.customerways.databinding.ActivityInterestBinding

class InterestActivity : AppCompatActivity() {

    lateinit var binding: ActivityInterestBinding
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        binding = ActivityInterestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
}
