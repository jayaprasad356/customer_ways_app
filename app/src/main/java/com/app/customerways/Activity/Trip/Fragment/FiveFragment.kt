package com.app.customerways.Activity.Trip.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.customerways.Activity.Trip.StarttripActivity
import com.app.customerways.Activity.Trip.TripCompletedActivity
import com.app.customerways.databinding.FragmentFiveBinding
import com.app.customerways.helper.ApiConfig
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class FiveFragment : Fragment() {


    lateinit var binding: FragmentFiveBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var filePath1: String? = null
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFiveBinding.inflate(layoutInflater)

        activity = requireActivity()
        session = Session(activity)

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.tvTitle.visibility = View.INVISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"

        binding.ivProof1.setOnClickListener {
            pickImageFromGallery()
        }


//        binding.etDescription.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }

//            override fun afterTextChanged(s: Editable?) {
//                if (binding.etDescription.lineCount > 5) {
//                    val text = s.toString().substring(0, binding.etDescription.selectionEnd - 1)
//                    binding.etDescription.setText(text)
//                    binding.etDescription.setSelection(binding.etDescription.text!!.length)
//                }
//            }
//        })

//        if (session.getData(Constant.TRIP_TITLE) != null) {
//            binding.etTripName.setText(session.getData(Constant.TRIP_TITLE))
//        }

//        if (session.getData(Constant.TRIP_DESCRIPTION) != null) {
//            binding.etDescription.setText(session.getData(Constant.TRIP_DESCRIPTION))
//        }

        return binding.root
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                CropImage.activity(imageUri)
                    .setAspectRatio(5, 3)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(requireContext(), this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.ivProof1.setImageBitmap(myBitmap)
                        binding.ivAddProof1.visibility = View.GONE
                        //    binding.rlProfile.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun addtripImage(id: String) {
        if (filePath1.isNullOrEmpty()) {
            Toast.makeText(activity, "Please select an image to upload.", Toast.LENGTH_SHORT).show()
            return
        }
        val params: MutableMap<String, String> = HashMap()
        params[Constant.PRODUCT_ID] = id
        val fileParams: MutableMap<String, String> = HashMap()
        fileParams[Constant.PRODUCT_IMAGE] = filePath1!!

        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val intent = Intent(activity, TripCompletedActivity::class.java)
                        startActivity(intent)
                        activity.finish()
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON Parsing Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "$result", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.UPDATE_PRODUCT_IMAGE, params, fileParams)
    }

}