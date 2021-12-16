package ru.fefu.activitytracker.activitytracking.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.activitytracking.cards.utils.DistanceUtils
import ru.fefu.activitytracker.activitytracking.cards.utils.TimeUtils
import ru.fefu.activitytracker.databinding.FragmentMyDetailsBinding

class ActivityMyDetailsFragment : Fragment(R.layout.fragment_my_details) {

    private var _binding: FragmentMyDetailsBinding? = null
    private val binding: FragmentMyDetailsBinding
        get() = _binding!!

    companion object {

        const val TAG = "activity_my_details_fragment"

        fun newInstance(activity_id : Int) : ActivityMyDetailsFragment {
            val bundle = Bundle()
            val fragment = ActivityMyDetailsFragment()
            bundle.putInt("activity_id", activity_id)
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

        val activity = App.INSTANCE.db.activityMyDao().getById(arguments?.get("activity_id") as Int)

        toolbar.setTitle(activity.sport_type.type)
        binding.distanceActivityMyDetails.text = DistanceUtils.getDistanceByString(
            DistanceUtils.getDistanceUsingCoordinatesList(activity.coordinates_list)
        )
        binding.dateActivityMyDetails.text = TimeUtils.getSpentTime(activity.end_time)
        binding.timeActivityMyDetails.text = TimeUtils.getDuration(activity.start_time, activity.end_time)
        binding.startTimeActivityMyDetails.text = TimeUtils.getTimeByStr(activity.start_time)
        binding.endTimeActivityMyDetails.text = TimeUtils.getTimeByStr(activity.end_time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}