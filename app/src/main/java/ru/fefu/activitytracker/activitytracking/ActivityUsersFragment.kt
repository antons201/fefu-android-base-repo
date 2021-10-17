package ru.fefu.activitytracker.activitytracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.fefu.activitytracker.R

class ActivityUsersFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity_users, container, false)
    }

    companion object {

        fun newInstance() : ActivityUsersFragment {
            val bundle = Bundle()
            val fragment = ActivityUsersFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}