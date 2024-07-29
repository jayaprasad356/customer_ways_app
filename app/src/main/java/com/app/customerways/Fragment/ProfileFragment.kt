package com.app.customerways.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.customerways.Activity.CustomerSupportActivity
import com.app.customerways.Activity.DeactivateActivity
import com.app.customerways.Activity.EditProfileActivity
import com.app.customerways.Activity.FreePointsActivity
import com.app.customerways.Activity.GoogleLoginActivity
import com.app.customerways.Activity.HomeActivity
import com.app.customerways.Activity.IdverficationActivity
import com.app.customerways.Activity.InterestActivity
import com.app.customerways.Activity.InviteFriendsActivity
import com.app.customerways.Activity.MytripsActivity
import com.app.customerways.Activity.NotificationActivity
import com.app.customerways.Activity.PrivacypolicyActivity
import com.app.customerways.Activity.PurchasepointActivity
import com.app.customerways.Activity.SeetingActivity
import com.app.customerways.Activity.Stage4Activity
import com.app.customerways.Activity.TermsconditionActivity
import com.app.customerways.R
import com.app.customerways.databinding.FragmentProfileBinding
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var activity:Activity
    private lateinit var session: Session
    private lateinit var googleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private var filePath1: String? = null
    private var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2
    private val REQUEST_IMAGE_CAMERA = 3
    private var isCameraRequest = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity = requireActivity()
        session = Session(activity)

        val sellerStatus = session.getData(Constant.SELLER_STATUS)

        if (sellerStatus == "1") {
            binding.rlSellersOption.visibility = View.VISIBLE
        } else {
            binding.rlSellersOption.visibility = View.GONE
        }

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE

        binding.ivAddProfile.setOnClickListener {
            isCameraRequest = false
            pickImageFromGallery()
        }

        binding.ivEdit.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivCamera.setOnClickListener {
            isCameraRequest = true
            pickImageFromGallery()
        }

        binding.rlNotifications.setOnClickListener {
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.rlPrivacy.setOnClickListener {
            val intent = Intent(activity, PrivacypolicyActivity::class.java)
            startActivity(intent)
        }

        binding.rlSetting.setOnClickListener {
            val intent = Intent(activity, SeetingActivity::class.java)
            startActivity(intent)
        }

        binding.rlTermscondition.setOnClickListener {
            val intent = Intent(activity, TermsconditionActivity::class.java)
            startActivity(intent)
        }

        binding.rlInviteFriends.setOnClickListener {
            val intent = Intent(activity, InviteFriendsActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        val profile = session.getData(Constant.PROFILE)
        Glide.with(this).load(profile).placeholder(R.drawable.profile_placeholder)
            .into(binding.civProfile)
        Glide.with(this).load(session.getData(Constant.COVER_IMG)).placeholder(R.drawable.placeholder_bg)
            .into(binding.ivCover)

        binding.tvName.text = session.getData(Constant.NAME)
        binding.tvUsername.text = "@${session.getData(Constant.UNIQUE_NAME)}"



        binding.rlMytrips.setOnClickListener {
            val intent = Intent(activity, MytripsActivity::class.java)
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.rlDeactiveaccount.setOnClickListener {
            val intent = Intent(activity, DeactivateActivity::class.java)
            startActivity(intent)
        }

        binding.rlCustomerSupport.setOnClickListener {
            val intent = Intent(activity, CustomerSupportActivity::class.java)
            startActivity(intent)
        }

        binding.rlLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                if (isCameraRequest) {
                    CropImage.activity(imageUri)
                        .setAspectRatio(4, 2)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(activity, this)
                } else {
                    CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(activity, this)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        if (isCameraRequest) {
                            binding.ivCover.setImageBitmap(myBitmap)
                            uploadCover()
                        } else {
                            binding.civProfile.setImageBitmap(myBitmap)
                            uploadProfile()
                        }
                    }
                }
            }
        }
    }

    private fun uploadProfile() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        val fileParams: MutableMap<String, String> = HashMap()
        fileParams[Constant.PROFILE] = filePath1!!
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonobj = jsonObject.getJSONObject(Constant.DATA)

                        session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                        session.setData(Constant.UNIQUE_NAME, jsonobj.getString(Constant.UNIQUE_NAME))
                        session.setData(Constant.EMAIL, jsonobj.getString(Constant.EMAIL))
                        session.setData(Constant.AGE, jsonobj.getString(Constant.AGE))
                        session.setData(Constant.GENDER, jsonobj.getString(Constant.GENDER))
                        session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                        session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                        session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                        session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
                        session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                        session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                        session.setData(Constant.REFERRED_BY, jsonobj.getString(Constant.REFERRED_BY))
                        Glide.with(this).load(session.getData(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder)
                            .into(binding.civProfile)

                        // Call resume() in HomeActivity if needed
                        

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.UPDATE_IMAGE, params, fileParams)
    }

    private fun uploadCover() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        val fileParams: MutableMap<String, String> = HashMap()
        fileParams[Constant.COVER_IMG] = filePath1!!
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonobj = jsonObject.getJSONObject(Constant.DATA)

                        session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))
                        Glide.with(this).load(session.getData(Constant.COVER_IMG))
                            .placeholder(R.drawable.placeholder_bg).into(binding.ivCover)


                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.UPDATE_COVER_IMG, params, fileParams)
    }

    private fun showPointsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)

        val dialogBuilder = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()
        val title = dialogView.findViewById<TextView>(R.id.dialog_title)
        val btnPurchase = dialogView.findViewById<LinearLayout>(R.id.btnPurchase)
        val btnFreePoints = dialogView.findViewById<LinearLayout>(R.id.btnFreePoints)

        title.text = "You have ${session.getData(Constant.POINTS)} Points"

        btnPurchase.setOnClickListener {
            val intent = Intent(activity, PurchasepointActivity::class.java)
            startActivity(intent)
            dialogBuilder.dismiss()
        }

        btnFreePoints.setOnClickListener {
            val intent = Intent(activity, FreePointsActivity::class.java)
            startActivity(intent)
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }

    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
            .setMessage("Are you sure you want to logout?")
            .setCancelable(true)
            .setPositiveButton("Logout") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogBuilder.show()

        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
            ContextCompat.getColor(activity, R.color.primary)
        )
        dialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
            ContextCompat.getColor(activity, R.color.text_grey)
        )
    }

    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            clearSessionData(activity)
            redirectToLogin(activity)
        }
    }

    private fun clearSessionData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun redirectToLogin(context: Context) {
        session.setBoolean("is_logged_in", false)
        val intent = Intent(context, GoogleLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        requireActivity().finish()
    }
}
