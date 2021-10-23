package ru.fefu.activitytracker.activitytracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.fefu.activitytracker.R


class ActivityMyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity_my, container, false)
    }

    companion object {
        fun newInstance() : ActivityMyFragment {
            val bundle = Bundle()
            val fragment = ActivityMyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}