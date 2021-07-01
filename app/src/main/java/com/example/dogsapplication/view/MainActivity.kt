package com.example.dogsapplication.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.dogsapplication.R
import com.example.dogsapplication.util.PERMISSION_SEND_SMS
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.navController

    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun checkSmsPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            )!= PackageManager.PERMISSION_GRANTED ){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.SEND_SMS)){

                AlertDialog.Builder(this)
                    .setTitle("Send SMS permission")
                    .setMessage("this app requires access to send SMS")
                    .setPositiveButton("Ask Me"){dialog,which ->

                        requestSmsPermission()
                    }
                    .setNegativeButton("No"){dialog, which ->

                        notifyDetailFragment(false)
                    }

                    .show()
            }else{
                requestSmsPermission()
            }

        }else{
            notifyDetailFragment(true)
        }
    }

    private fun notifyDetailFragment(permissionGranted: Boolean) {


        val activeFragment = fragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DetailFragment){

            activeFragment.onPermissionResult(permissionGranted)
        }

    }

    private fun requestSmsPermission() {

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS)
           , PERMISSION_SEND_SMS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){

            PERMISSION_SEND_SMS -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    notifyDetailFragment(true)

                }else{

                    notifyDetailFragment(false)

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}