package com.app.customerways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.customerways.R
import com.app.customerways.databinding.ActivityBecomeSellerBinding
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class BecomeSellerActivity : BaseActivity() {
    lateinit var binding: ActivityBecomeSellerBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var professions = listOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_become_seller)
        binding = ActivityBecomeSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        binding.etMobileNumber.setText(session.getData(Constant.MOBILE))
        binding.etName.setText(session.getData(Constant.YOUR_NAME))
        binding.etEmail.setText(session.getData(Constant.EMAIL))
        binding.etStoreName.setText(session.getData(Constant.STORE_NAME))
        binding.etCategory.setText(session.getData(Constant.CATEGORY))
        binding.etStoreAddress.setText(session.getData(Constant.STORE_ADDRESS))


        binding.tvSkip.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ibBack.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (binding.etName.text.toString().isEmpty()) {
                binding.etName.error = "Please enter name"
                return@setOnClickListener
            } else if (binding.etName.text.toString().length < 4) {
                binding.etName.error = "Name should be at least 4 characters"
                return@setOnClickListener
            } else if (binding.etStoreName.text.toString().isEmpty()) {
                binding.etStoreName.error = "Please enter store name"
                return@setOnClickListener
            } else if (binding.etStoreName.text.toString().length < 4) {
                binding.etStoreName.error = "Store name should be at least 8 characters"
                return@setOnClickListener
            } else if (binding.etMobileNumber.text.toString().isEmpty()) {
                binding.etMobileNumber.error = "Please enter mobile number"
                return@setOnClickListener
            } else if (binding.etMobileNumber.text.toString().length < 4) {
                binding.etMobileNumber.error = "Mobile mumber should be at least 10 characters"
                return@setOnClickListener
            } else if (binding.etEmail.text.toString().isEmpty()) {
                binding.etEmail.error = "Please enter email"
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
                binding.etEmail.error = "Enter a valid Email address"
                return@setOnClickListener
            }  else if (binding.etCategory.text.toString().isEmpty()) {
                binding.etCategory.error = "Please select Category"
                return@setOnClickListener
            }
            else {
                saveDetails()
            }
        }

        binding.etCategory.setOnClickListener {
            binding.cardCategory.visibility = View.VISIBLE
            showProfessionDialogcategory(binding.etCategory)
        }
    }

    private fun showProfessionDialogcategory(etCategory: EditText) {
        val Category = listOf(
            "Clothing", "Electronic", "Jewellery", "Shoes",
        )
        val adapter = ProfessionAdapter(Category) { selectedProfession ->
            binding.etCategory.setText(selectedProfession)
            binding.cardCategory.visibility = View.GONE
            binding.etCategory.error = null
        }
        binding.rvCategory.adapter = adapter
        binding.rvCategory.layoutManager = LinearLayoutManager(this)
    }

    private fun saveDetails() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.MOBILE] = session.getData(Constant.MOBILE)
        params[Constant.YOUR_NAME] = binding.etName.text.toString()
        params[Constant.EMAIL] = binding.etEmail.text.toString()
        params[Constant.STORE_NAME] = binding.etStoreName.text.toString()
        params[Constant.ADDRESS] = binding.etStoreAddress.text.toString()
        params[Constant.CATEGORY] = binding.etCategory.text.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, SellerStatusActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            activity,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.ADD_SELLERS, params, true, 1)
    }
}