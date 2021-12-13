package ru.fefu.activitytracker.activitytracking.train

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.database.ActivityMy
import ru.fefu.activitytracker.databinding.FragmentNewTrainBinding
import java.time.LocalDateTime

import android.graphics.BitmapFactory
import android.location.Location
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.Exception


class NewTrainFragment : Fragment(R.layout.fragment_new_train) {
    private var _binding: FragmentNewTrainBinding? = null
    private val binding: FragmentNewTrainBinding
        get() = _binding!!

    private val trainTypeCardsRepository = TrainTypeCardsRepository()

    private val newTrainFragmentAdapter = NewTrainFragmentAdapter(trainTypeCardsRepository.getTrainTypeCards())


    private var activityId: Int = -1

    companion object {
        const val TAG = "change_train_fragment"

        private const val REQUEST_CODE_RESOLVE_GOOGLE_API_ERROR = 1337
        private const val REQUEST_CODE_RESOLVE_GPS_ERROR = 1338

        fun newInstance(): NewTrainFragment {
            val bundle = Bundle()
            val fragment = NewTrainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RESOLVE_GOOGLE_API_ERROR || requestCode == REQUEST_CODE_RESOLVE_GPS_ERROR) {
            if (resultCode == Activity.RESULT_OK) startLocationService()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _binding = view?.let { FragmentNewTrainBinding.bind(it)}

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.trainTypesList) {
            adapter = newTrainFragmentAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.startTrainButton.setOnClickListener {
            requestLocationPermission()
        }
    }

    private fun startTrain() {
        activityId = App.INSTANCE.db.activityMyDao().insert(
            ActivityMy(
                0,
                LocalDateTime.now(),
                LocalDateTime.now(),
                TrainTypes.values()[newTrainFragmentAdapter.getSelectedPosition()],
                listOf()
            )
        ).toInt()
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.train_info, ActiveTrainFragment.newInstance(
                TrainTypes.values()[newTrainFragmentAdapter.getSelectedPosition()],
                activityId,
                false
            ), ActiveTrainFragment.TAG)
            commit()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
            granted -> if (granted) {
                startLocationService()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionDeniedDialog()
                } else {
                    showRationaleDialog()
                }
            }
    }

    private fun requestLocationPermission() {
        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                startLocationService()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showRationaleDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Требуется разрешение")
                .setMessage("Вы не сможете тренироваться, пока не будет выдано разрешение на " +
                        "определение местоположения")
                .setPositiveButton("Разрешения") { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                .setNegativeButton("Отмена") { _, _ -> }
                .show()
        }
    }

    private fun showPermissionDeniedDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Разрешение заблокировано")
                .setMessage("Перейдите в настройки, чтобы выдать разрешение на определение " +
                        "местоположения")
                .setPositiveButton("Настройки") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity?.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                .setNegativeButton("Отмена") { _, _ -> }
                .show()
        }
    }

    private fun startLocationService() {
        if (isGooglePlayServicesAvailable()) {
            checkIfGpsEnabled(
                {
                    startTrain()
                    context?.let { TrainForegroundService.startForeground(it, activityId) }
                },
                {
                    if (it is ResolvableApiException) {
                        it.startResolutionForResult(requireActivity(), REQUEST_CODE_RESOLVE_GPS_ERROR)
                    } else {
                        Toast.makeText(requireContext(), "GPS Unavailable", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val result = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (result == ConnectionResult.SUCCESS) return true
        if (googleApiAvailability.isUserResolvableError(result)) {
            googleApiAvailability.getErrorDialog(
                this,
                result,
                REQUEST_CODE_RESOLVE_GOOGLE_API_ERROR
            )?.show()
        } else {
            Toast.makeText(
                requireContext(),
                "Google services unavailable",
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    private fun checkIfGpsEnabled(success: () -> Unit, error: (Exception) -> Unit) {
        LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                    .addLocationRequest(TrainForegroundService.locationRequest)
                    .build()
            )
            .addOnSuccessListener { success.invoke() }
            .addOnFailureListener { error.invoke(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}