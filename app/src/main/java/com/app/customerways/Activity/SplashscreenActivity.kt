package com.app.customerways.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.customerways.R
import com.app.customerways.databinding.ActivitySplashscreenBinding
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.json.JSONException
import org.json.JSONObject

class SplashscreenActivity : BaseActivity() {
    lateinit var binding: ActivitySplashscreenBinding
    private var handler: Handler? = null
    private var activity: Activity? = null
   lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





        activity = this
        session = Session(activity)
        handler = Handler()
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)

        setContentView(binding.root)


            GotoActivity()
            // Do something when the video ends


        handleIncomingIntent(intent)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val data: Uri? = intent.data
        data?.let {
            if (it.isHierarchical) {
                val referralCode = it.getQueryParameter("referralCode")
                referralCode?.let {
                    Log.d("Referral Code", "referralCode = $referralCode")
//                    Toast.makeText(this, "Referral Code: $referralCode", Toast.LENGTH_LONG).show()
                    // Handle the referral code (e.g., store it in shared preferences, use it to fetch referral-specific data, etc.)
                }
            }
        }
    }


    private fun GotoActivity() {
        handler?.postDelayed({
            if (session!!.getBoolean("is_logged_in")) {
                val intent = Intent(activity,HomeActivity::class.java)
                startActivity(intent)
                finish()

            } else {
             //   Toast.makeText(activity, "Please login", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 100)
    }


    override fun onStart() {
        super.onStart()

    }

    private fun login() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.EMAIL] = session.getData(Constant.EMAIL)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val registered = jsonObject.getString("registered")
                        if (registered == "true") {
                            val `object` = JSONObject(response)
                            val jsonobj = `object`.getJSONObject(Constant.DATA)
                            session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))
                            session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                            session.setData(Constant.UNIQUE_NAME, jsonobj.getString(Constant.UNIQUE_NAME))
                            session.setData(Constant.EMAIL, jsonobj.getString(Constant.EMAIL))
                            session.setData(Constant.AGE, jsonobj.getString(Constant.AGE))
                            session.setData(Constant.GENDER, jsonobj.getString (Constant.GENDER))
                            session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                            session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                            session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                            session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                            session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                            session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))
                            session.setData(Constant.POINTS, jsonobj.getString(Constant.POINTS))

                            if (GoogleSignIn.getLastSignedInAccount(this) != null) {
                                val intent = Intent(activity,HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        }

                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.CHECK_EMAIL, params, true, 1)
    }
}