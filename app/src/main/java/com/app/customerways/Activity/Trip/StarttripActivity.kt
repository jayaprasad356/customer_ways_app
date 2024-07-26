package com.app.customerways.Activity.Trip

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.app.customerways.Activity.BaseActivity
import com.app.customerways.Activity.Trip.Fragment.FiveFragment
import com.app.customerways.Activity.Trip.Fragment.FourFragment
import com.app.customerways.Activity.Trip.Fragment.SixFragment
import com.app.customerways.Activity.Trip.Fragment.oneFragment
import com.app.customerways.Activity.Trip.Fragment.threeFragment
import com.app.customerways.R
import com.app.customerways.databinding.ActivityStarttripBinding
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session


class StarttripActivity : BaseActivity() {

    lateinit var binding:ActivityStarttripBinding
    lateinit var activity: Activity
    lateinit var fm: FragmentManager
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starttrip)

        binding = ActivityStarttripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)


        fm = supportFragmentManager
        val fragment_one = oneFragment()


        fm.beginTransaction().replace(R.id.frameLayout, fragment_one).commit()



        binding.btnNext.setOnClickListener {
            btnNext(it)
        }

        binding.ivBack.setOnClickListener{
            btnBack(it)
        }




    }

    // resume

    public override fun onResume() {
        super.onResume()
        // check the current fragment
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {


            }
            is threeFragment -> {


            }
//            is FourFragment -> {
//
//            }
            is FiveFragment -> {


            }
            is SixFragment -> {

            }
        }

    }



    private fun btnBack(it: View?) {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {
                onBackPressed()
                onResume()
            }

            is threeFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, oneFragment()).commit()
                onResume()

            }
//            is FourFragment -> {
//                fm.beginTransaction().replace(R.id.frameLayout, threeFragment()).commit()
//                onResume()
//
//            }
            is FiveFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, threeFragment()).commit()
                onResume()

            }
            is SixFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FiveFragment()).commit()
                onResume()

            }
        }
    }

    fun btnNext(view: View) {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {

                if (fragment.isItemSelected()) {
                    fm.beginTransaction().replace(R.id.frameLayout, threeFragment()).commit()
                    onResume()
                } else {
                    Toast.makeText(this, "Please select a trip plan", Toast.LENGTH_SHORT).show()
                }
            }

            is threeFragment -> {
                // call fragment etLocation
                if ((fragment as threeFragment).binding.etTitle.text.toString().isEmpty() && (fragment as threeFragment).binding.etDescription.text.toString().isEmpty()) {
                    (fragment as threeFragment).binding.etTitle.error = "Please enter Title"
                    (fragment as threeFragment).binding.etDescription.error = "Please enter Description"
                }
                else {
                    session.setData(Constant.PRODUCT_TITLE, (fragment as threeFragment).binding.etTitle.text.toString())
                  session.setData(Constant.PRODUCT_DESCRIPTION, (fragment as threeFragment).binding.etDescription.text.toString())

                    fm.beginTransaction().replace(R.id.frameLayout, SixFragment()).commit()
                    onResume()
                }



            }
//            is FourFragment -> {
//                if ((fragment as FourFragment).binding.edStartDate.text.toString().isEmpty()) {
//                    (fragment as FourFragment).binding.edStartDate.error = "Please enter start date"
//                } else if ((fragment as FourFragment).binding.edEndDate.text.toString().isEmpty()) {
//                    (fragment as FourFragment).binding.edEndDate.error = "Please enter end date"
//                } else {
//                    session.setData(Constant.TRIP_FROM_DATE, (fragment as FourFragment).binding.edStartDate.text.toString())
//                    session.setData(Constant.TRIP_TO_DATE, (fragment as FourFragment).binding.edEndDate.text.toString())
//                    fm.beginTransaction().replace(R.id.frameLayout, FiveFragment()).commit()
//                    onResume()
//                }
//
//            }
            is FiveFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, SixFragment()).commit()
                onResume()
//                if ((fragment as FiveFragment).binding.etTripName.text.toString().isEmpty()) {
//                    (fragment as FiveFragment).binding.etTripName.error = "Please enter trip name"
//                }else if ((fragment as FiveFragment).binding.etDescription.text.toString().isEmpty()) {
//                    (fragment as FiveFragment).binding.etDescription.error = "Please enter description"
//                }
//                else {
//                    session.setData(Constant.TRIP_TITLE, (fragment as FiveFragment).binding.etTripName.text.toString())
//                    session.setData(Constant.TRIP_DESCRIPTION, (fragment as FiveFragment).binding.etDescription.text.toString())
//                    fm.beginTransaction().replace(R.id.frameLayout, SixFragment()).commit()
//                    onResume()
//                }


            }
            is SixFragment -> {


                       // fragment as SixFragment call function addtrip
                          (fragment as SixFragment).addtrip()

            }

        } }





    // back press
    override fun onBackPressed() {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {
                super.onBackPressed()
            }
            is threeFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, oneFragment()).commit()
                onResume()
            }
            is SixFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, threeFragment()).commit()
                onResume()
            }
        }
    }





}