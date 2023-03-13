package uz.gita.my_taxi_test.utils.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

// Created by Jamshid Isoqov on 3/9/2023


fun Fragment.bitmapFromDrawableRes(@DrawableRes resourceId: Int) =
    convertDrawableToBitmap(AppCompatResources.getDrawable(requireContext(), resourceId))

private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
    if (sourceDrawable == null) {
        return null
    }
    return if (sourceDrawable is BitmapDrawable) {
        sourceDrawable.bitmap
    } else {
        val constantState = sourceDrawable.constantState ?: return null
        val drawable = constantState.newDrawable().mutate()
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    }
}

fun Fragment.hasPermission(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    Dexter.withContext(requireContext())
        .withPermission(permission).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                onPermissionGranted.invoke()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                onPermissionDenied.invoke()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }
        }).check()
}

fun Fragment.hasPermission(
    permissions: List<String>,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    Dexter.withContext(requireContext())
        .withPermissions(permissions).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if (permissions.size == p0?.grantedPermissionResponses?.size) {
                    onPermissionGranted.invoke()
                } else {
                    onPermissionDenied.invoke()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }

        })
}

fun Context.checkLocation(onCancelListener: (Boolean) -> Unit) {
    val manager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    if (manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        onCancelListener.invoke(true)
    } else {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                onCancelListener.invoke(false)
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}
