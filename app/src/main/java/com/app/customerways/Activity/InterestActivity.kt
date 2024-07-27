package com.app.customerways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.customerways.Adapter.ConnectAdapter
import com.app.customerways.Fragment.InterestFragment
import com.app.customerways.Model.Connect
import com.app.customerways.R
import com.app.customerways.databinding.ActivityIdverficationBinding
import com.app.customerways.databinding.ActivityInterestBinding
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class InterestActivity : AppCompatActivity() {

    lateinit var binding: ActivityInterestBinding
    lateinit var activity: Activity
    private lateinit var session: Session

    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val connectList = ArrayList<Connect>()
    private lateinit var connectAdapter: ConnectAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        binding = ActivityInterestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        setupRecyclerView()
        setupSwipeRefreshLayout()

        if (connectList.isEmpty()) {
            NotificationList()
        }


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }


    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvConnectList.layoutManager = linearLayoutManager

        connectAdapter = ConnectAdapter(activity, connectList)
        binding.rvConnectList.adapter = connectAdapter

        binding.rvConnectList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    NotificationList()
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            NotificationList()
        }
    }

    private fun NotificationList() {
        if (isLoading) return
        isLoading = true

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.OFFSET] = offset.toString()
        params[Constant.LIMIT] = limit.toString()

        ApiConfig.RequestToVolley({ result, response ->
            isLoading = false
            binding.swipeRefreshLayout.isRefreshing = false

            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        total = jsonObject.getInt(Constant.TOTAL)
                        if (offset == 0) {
                            connectList.clear()
                        }

                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val connect = gson.fromJson(jsonObject1.toString(), Connect::class.java)
                            connectList.add(connect)
                        }

                        connectAdapter.notifyDataSetChanged()
                        offset += limit
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.FREINDS_LIST, params, true, 1)
    }
}
