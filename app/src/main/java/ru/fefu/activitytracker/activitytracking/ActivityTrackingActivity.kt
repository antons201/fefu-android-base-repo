package ru.fefu.activitytracker.activitytracking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.databinding.ActivityTrackingBinding

class ActivityTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingBinding

    private var lastHiddenActivityFragment : Fragment? = null
    private var lastHiddenProfileFragment : Fragment? = null
    private var lastitemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.activity_tracker, ActivityFragment.newInstance(), ActivityFragment.TAG)
                commit()
            }
        }

        binding.activityNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_profile -> {
                    if (lastitemId != R.id.navigation_profile) {
                        val current_fragment =
                            supportFragmentManager.fragments.firstOrNull { !it.isHidden }
                        val switched_fragment = lastHiddenProfileFragment
                        setFragment(current_fragment, switched_fragment)
                        if (current_fragment!!::class != ProfileFragment::class)
                            lastHiddenActivityFragment = current_fragment
                        lastitemId = R.id.navigation_profile
                    }
                    true
                }
                R.id.navigation_activity -> {
                    if (lastitemId != R.id.navigation_activity) {
                        val current_fragment =
                            supportFragmentManager.fragments.firstOrNull { !it.isHidden }
                        val switched_fragment = lastHiddenActivityFragment
                        setFragment(current_fragment, switched_fragment)
                        if (current_fragment!!::class == ProfileFragment::class)
                            lastHiddenProfileFragment = current_fragment
                        lastitemId = R.id.navigation_activity
                    }
                    true
                }

                else -> false
            }
        }

    }

    fun setFragment(current_fragment: Fragment?, switched_fragment: Fragment?) {
        if (current_fragment != null) {
            supportFragmentManager.beginTransaction().apply {
                hide(current_fragment)
                commit()
            }
        }
        if (switched_fragment != null) {
            supportFragmentManager.beginTransaction().apply {
                show(switched_fragment)
                commit()
            }
        } else {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.activity_tracker, ProfileFragment.newInstance(), ProfileFragment.TAG)
                commit()
            }
        }
    }
}