package com.app.customerways.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.customerways.R
import com.app.customerways.databinding.ActivityStorepointsBinding

class StorepointsActivity : BaseActivity() {


    lateinit var binding: ActivityStorepointsBinding
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storepoints)

        binding = ActivityStorepointsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}