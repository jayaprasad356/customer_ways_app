package com.app.customerways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.customerways.R
import com.app.customerways.databinding.ActivityOtpBinding
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.cli.Options

import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OtpActivity : BaseActivity() {

    lateinit var binding: ActivityOtpBinding
    lateinit var activity: Activity
    lateinit var session: Session

    private var countDownTimer: CountDownTimer? = null
    private val COUNTDOWN_TIME = 45000L // 45 seconds in milliseconds

    // Firebase Auth instance
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_otp)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)
        auth = FirebaseAuth.getInstance()

        startCountdown()
        otp()

        binding.btnVerifyOTP.setOnClickListener {
            val otp = binding.otpview.getOTP()
            if (otp.length == 6) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnResendOTP.setOnClickListener {
            resetCountdown()
            startCountdown()
            resendOtp()
            Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            binding.btnResendOTP.isEnabled = false
        }
    }

    private fun verifyOtp(otp: String) {
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI
                        val user = auth.currentUser
                        // Continue with your login logic here
                        login()
                    } else {
                        // Sign in failed, handle error
                        Toast.makeText(this, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun resendOtp() {
        val phoneNumber = session.getData(Constant.MOBILE)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval or instant validation
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Handle error
                    Toast.makeText(this@OtpActivity, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    this@OtpActivity.verificationId = verificationId
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Continue with your login logic here
                    login()
                } else {
                    Toast.makeText(this, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun login() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.MOBILE] = session.getData(Constant.MOBILE)
        params["otp"] = binding.otpview.getOTP().toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonobj = jsonObject.getJSONObject(Constant.DATA)
                        session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                        session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))
                        session.setData(Constant.UNIQUE_NAME, jsonobj.getString(Constant.UNIQUE_NAME))
                        session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                        session.setBoolean("is_logged_in", true)
                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.CHECK_MOBILE, params, true, 1)
    }

    private fun otp() {
        val params = HashMap<String, String>()
        params[Constant.MOBILE] = session.getData(Constant.MOBILE)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    Log.d("SIGNUP_RES", response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val otp = jsonArray.getJSONObject(0).getString("otp")
                        sendotp(otp)
                    } else {
                        Toast.makeText(this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, this, Constant.OTP, params, true)
    }

    private fun sendotp(otp: String) {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                Toast.makeText(this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "OTP Failed", Toast.LENGTH_SHORT).show()
            }
        }, this, Constant.getOTPUrl("b45c58db6d261f2a", session.getData(Constant.MOBILE), otp), params, true)
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.btnResendOTP.text = "Resend in $secondsLeft seconds"
            }

            override fun onFinish() {
                binding.btnResendOTP.isEnabled = true
                binding.btnResendOTP.text = "Resend OTP"
            }
        }.start()
    }

    private fun resetCountdown() {
        countDownTimer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetCountdown()
    }
}
