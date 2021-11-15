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
import ru.fefu.activitytracker.activitytracking.ActivityMyCardsRepository
import ru.fefu.activitytracker.databinding.FragmentActivityMyBinding
import ru.fefu.activitytracker.database.ActivityMy

class ActivityMyFragment : Fragment (R.layout.fragment_activity_my) {

    private var _binding: FragmentActivityMyBinding? = null
    private val binding: FragmentActivityMyBinding
        get() = _binding!!

    private val activityMyCardsRepository = ActivityMyCardsRepository()

    private val activityMyCardListAdapter = ActivityMyCardListAdapter(activityMyCardsRepository.getActivityMyCards())

    private var countAdded : Int = 0


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
                        (activityMyCardListAdapter.mutableCards[it] as ActivityMyCard).sport_type
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
            for (i in countAdded until activityMyCardListAdapter.itemCount) {
                activityMyCardListAdapter.mutableCards.add(
                    ActivityMyCard(
                        "14.32 км",
                        cardsList[i].start_time,
                        cardsList[i].end_time,
                        cardsList[i].sport_type
                    )
                )
                countAdded++
                activityMyCardListAdapter.notifyItemInserted(activityMyCardListAdapter.itemCount - 1)
            }
        }
    }
}