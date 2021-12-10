package ru.fefu.activitytracker.activitytracking.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.activitytracking.train.TrainTypes
import ru.fefu.activitytracker.databinding.FragmentMyDetailsBinding

class ActivityMyDetailsFragment : Fragment(R.layout.fragment_my_details) {

    private var _binding: FragmentMyDetailsBinding? = null
    private val binding: FragmentMyDetailsBinding
        get() = _binding!!

    companion object {

        const val TAG = "activity_my_details_fragment"

        fun newInstance(sport_type: TrainTypes, start_time: String, stop_time: String,
                        distance: String, train_duration: String, spent_time: String) : ActivityMyDetailsFragment {
            val bundle = Bundle()
            val fragment = ActivityMyDetailsFragment()
            bundle.putString("sport_type", sport_type.type)
            bundle.putString("start_time", start_time)
            bundle.putString("stop_time", stop_time)
            bundle.putString("distance", distance)
            bundle.putString("train_duration", train_duration)
            bundle.putString("spent_time", spent_time)
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
        _binding = view?.let { FragmentMyDetailsBinding.bind(it)}

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.activityMyDetailsToolbar

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        toolbar.setTitle(arguments?.get("sport_type") as String)
        binding.distanceActivityMyDetails.text = arguments?.get("distance") as String
        binding.dateActivityMyDetails.text = arguments?.get("spent_time") as String
        binding.timeActivityMyDetails.text = arguments?.get("train_duration") as String
        binding.startTimeActivityMyDetails.text = arguments?.get("start_time") as String
        binding.endTimeActivityMyDetails.text = arguments?.get("stop_time") as String
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}