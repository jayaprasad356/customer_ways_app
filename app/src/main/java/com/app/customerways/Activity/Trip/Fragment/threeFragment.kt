package com.app.customerways.Activity.Trip.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.customerways.Activity.Trip.StarttripActivity
import com.app.customerways.databinding.FragmentThreeBinding
import com.app.customerways.helper.Constant
import com.app.customerways.helper.Session


class threeFragment : Fragment() {

    lateinit var binding: FragmentThreeBinding
    lateinit var activity: Activity
    lateinit var session: Session


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThreeBinding.inflate(layoutInflater)

        activity = requireActivity()
        session = Session(activity)

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.tvTitle.visibility = View.INVISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"


//        if (session.getData(Constant.TRIP_LOCATION) != null) {
//            binding.etLocation.setText(session.getData(Constant.TRIP_LOCATION))
//        }



        return binding.root
    }


}