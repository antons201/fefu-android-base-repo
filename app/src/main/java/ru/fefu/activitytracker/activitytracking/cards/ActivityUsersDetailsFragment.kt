package ru.fefu.activitytracker.activitytracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.databinding.FragmentUsersDetailsBinding

class ActivityUsersDetailsFragment : Fragment(R.layout.fragment_users_details) {

    private var _binding: FragmentUsersDetailsBinding? = null
    private val binding: FragmentUsersDetailsBinding
        get() = _binding!!


    companion object {

        const val TAG = "activity_users_details_fragment"

        fun newInstance() : ActivityUsersDetailsFragment {
            val bundle = Bundle()
            val fragment = ActivityUsersDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _binding = view?.let { FragmentUsersDetailsBinding.bind(it)}

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.activityUsersDetailsToolbar

        toolbar.setNavigationOnClickListener {
            val currentFragment = parentFragmentManager.findFragmentByTag(TAG)
            val switchedFragment = parentFragmentManager.findFragmentByTag(ActivityFragment.TAG)

            parentFragmentManager.beginTransaction().apply {
                if (currentFragment != null) {
                    hide(currentFragment)
                }
                if (switchedFragment != null) {
                    show(switchedFragment)
                }
                commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}