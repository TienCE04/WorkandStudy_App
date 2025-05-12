package com.example.workandstudy_app.document.Document_Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.workandstudy_app.databinding.FragmentOtherBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragDocOther.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragDocOther : Fragment() {
    private lateinit var binding: FragmentOtherBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOtherBinding.inflate(layoutInflater)
        return binding.root
    }
}