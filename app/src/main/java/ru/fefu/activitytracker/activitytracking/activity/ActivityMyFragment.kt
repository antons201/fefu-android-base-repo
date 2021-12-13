package ru.fefu.activitytracker.activitytracking.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.activitytracking.ActivityMyCard
import ru.fefu.activitytracker.activitytracking.ActivityMyCardListAdapter
import ru.fefu.activitytracker.activitytracking.ActivityPeriod
import ru.fefu.activitytracker.activitytracking.Card
import ru.fefu.activitytracker.activitytracking.cards.utils.DistanceUtils.getDistanceByString
import ru.fefu.activitytracker.activitytracking.cards.utils.DistanceUtils.getDistanceUsingCoordinatesList
import ru.fefu.activitytracker.databinding.FragmentActivityMyBinding
import ru.fefu.activitytracker.database.ActivityMy
import java.time.LocalDate

class ActivityMyFragment : Fragment (R.layout.fragment_activity_my) {

    private var _binding: FragmentActivityMyBinding? = null
    private val binding: FragmentActivityMyBinding
        get() = _binding!!

    private val activityMyCardListAdapter = ActivityMyCardListAdapter()


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _binding = view?.let { FragmentActivityMyBinding.bind(it)}

        return view
    }

    companion object {

        fun newInstance() : ActivityMyFragment {
            val bundle = Bundle()
            val fragment = ActivityMyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.activityMyCardsList) {
            adapter = activityMyCardListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        activityMyCardListAdapter.setItemClickListener {

            val fragmentManager = parentFragment?.parentFragmentManager

            val currentFragment = fragmentManager?.findFragmentByTag(ActivityListFragment.TAG)

            fragmentManager?.beginTransaction()?.apply {
                if (currentFragment != null) {
                    hide(currentFragment)
                }
                add(R.id.activity_info,
                    ActivityMyDetailsFragment.newInstance(
                        (activityMyCardListAdapter.currentList[it] as ActivityMyCard).id
                    ),
                    ActivityMyDetailsFragment.TAG
                )
                addToBackStack(ActivityMyDetailsFragment.TAG)
                commit()
            }
        }

        App.INSTANCE.db.activityMyDao().getAll().observe(viewLifecycleOwner) {
            addCardsToDB(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addCardsToDB(cardsList: List<ActivityMy>) {
        if (cardsList.isNotEmpty()) {
            binding.activityWelcomeHead.visibility = View.GONE
            binding.activityWelcomeText.visibility = View.GONE

            val activityList = mutableListOf<Card>()
            val activityMap = mutableMapOf<LocalDate, MutableList<ActivityMyCard>>()

            for (i in cardsList.indices) {
                val card = ActivityMyCard(
                    cardsList[i].id,
                    getDistanceByString(
                        getDistanceUsingCoordinatesList(cardsList[i].coordinates_list)
                    ),
                    cardsList[i].start_time,
                    cardsList[i].end_time,
                    cardsList[i].sport_type
                )

                if (activityMap.containsKey(cardsList[i].start_time.toLocalDate())) {
                    activityMap[cardsList[i].start_time.toLocalDate()]?.add(
                        card
                    )
                } else {
                    activityMap[cardsList[i].start_time.toLocalDate()] = mutableListOf(card)
                }
            }

            activityMap.toSortedMap()

            for ((key, value) in activityMap) {
                val period = key.dayOfMonth.toString() + "." + key.monthValue.toString() + "." +
                        key.year.toString()
                activityList.add(
                    ActivityPeriod(period)
                )
                value.sortBy { it.start_time }
                for (elem in value) {
                    activityList.add(elem)
                }
            }

            activityMyCardListAdapter.submitList(activityList)
        } else {
            binding.activityWelcomeHead.visibility = View.VISIBLE
            binding.activityWelcomeText.visibility = View.VISIBLE
        }
    }
}