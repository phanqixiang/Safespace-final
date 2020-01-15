package com.example.safespace.user


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.safespace.R
import com.example.safespace.databinding.FragmentReportBinding



/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {
    val TAG = "ReportFragment"

    private lateinit var binding : FragmentReportBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.setContentView(activity!!, R.layout.fragment_report)
        binding.addReportButton.setOnClickListener{
            val i = Intent(activity, ReportActivity::class.java)
            startActivity(i)
        }
        return inflater!!.inflate(R.layout.fragment_report, container, false)
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

    }
    }



