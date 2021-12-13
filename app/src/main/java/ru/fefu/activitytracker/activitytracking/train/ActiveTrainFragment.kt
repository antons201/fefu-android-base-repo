package ru.fefu.activitytracker.activitytracking.train

import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.activitytracking.cards.utils.DistanceUtils.getDistanceByString
import ru.fefu.activitytracker.activitytracking.cards.utils.DistanceUtils.getDistanceUsingCoordinatesList
import ru.fefu.activitytracker.databinding.FragmentActiveTrainBinding
import java.time.LocalDateTime

class ActiveTrainFragment : Fragment(R.layout.fragment_active_train) {
    private var _binding: FragmentActiveTrainBinding? = null
    private val binding: FragmentActiveTrainBinding
        get() = _binding!!

    private var mapView: org.osmdroid.views.MapView? = null

    companion object {
        const val TAG = "active_train_fragment"

        fun newInstance(sport_type: TrainTypes, id: Int, restore: Boolean): ActiveTrainFragment {
            val bundle = Bundle()
            val fragment = ActiveTrainFragment()
            bundle.putString("sport_type", sport_type.type)
            bundle.putInt("id", id)
            bundle.putBoolean("restore", restore)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val polyline by lazy {
        Polyline().apply {
            outlinePaint.color = ContextCompat.getColor(
                requireContext(),
                R.color.purple_700
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _binding = view?.let { FragmentActiveTrainBinding.bind(it)}

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = activity?.findViewById(R.id.mapView)

        showUserLocation()

        binding.trainType.text = arguments?.get("sport_type") as String

        binding.stopTrainButton.setOnClickListener{

            mapView?.overlays?.clear()
            mapView?.invalidate()


            val cancelIntent = Intent(requireContext(), TrainForegroundService::class.java).apply {
                action = TrainForegroundService.ACTION_CANCEL
            }

            cancelIntent.putExtra("activity_id", arguments?.get("id") as Int)

            requireActivity().startService(cancelIntent)

            requireActivity().finish()
        }

        mapView = activity?.findViewById(R.id.mapView)
        checkLocation()
    }

    private fun showUserLocation() {
        val locationOverlay = MyLocationNewOverlay(
            object : GpsMyLocationProvider(requireContext()) {
                private var mapMoved = false
                override fun onLocationChanged(location: Location) {
                    location.removeBearing()
                    super.onLocationChanged(location)
                    if (mapMoved) return
                    mapMoved = true
                    mapView?.controller?.animateTo(
                        GeoPoint(
                            location.latitude,
                            location.longitude
                        ),
                        16.0,
                        1000
                    )
                }
            },
            mapView
        )
        val locationIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_user_location)
        locationOverlay.setDirectionArrow(locationIcon, locationIcon)
        locationOverlay.setPersonHotspot(locationIcon.width / 2f, locationIcon.height.toFloat())
        locationOverlay.enableMyLocation()
        mapView?.overlays?.add(locationOverlay)
    }

    private fun checkLocation() {

        var restore: Boolean = arguments?.get("restore") as Boolean

        var distance = 0.0

        App.INSTANCE.db.activityMyDao().getLiveData(arguments?.get("id") as Int)
            .observe(viewLifecycleOwner) {
                if (it.coordinates_list.isNotEmpty() && !restore) {
                    val lastCoordinate = it.coordinates_list.last()
                    polyline.addPoint(GeoPoint(lastCoordinate.first, lastCoordinate.second))
                    binding.trainDistance.text = getDistanceByString(
                        TrainForegroundService.distance
                    )
                }
                if (it.coordinates_list.isNotEmpty() && restore) {
                    for (coordinate in it.coordinates_list) {
                        polyline.addPoint(GeoPoint(coordinate.first, coordinate.second))
                    }
                    binding.trainDistance.text = getDistanceByString(
                        getDistanceUsingCoordinatesList(it.coordinates_list)
                    )
                    restore = false
                }
            }

        mapView?.overlayManager?.add(polyline)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}