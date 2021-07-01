package com.example.dogsapplication.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dogsapplication.R
import com.example.dogsapplication.databinding.FragmentDetailBinding
import com.example.dogsapplication.databinding.SendSmsDialogBinding
import com.example.dogsapplication.model.DogBreed
import com.example.dogsapplication.model.DogPalette
import com.example.dogsapplication.model.SmsInfo
import com.example.dogsapplication.viewmodel.DetailViewModel


class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private var dogUuid = 0

    private lateinit var dataBinding: FragmentDetailBinding
    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {

            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid

        }
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUuid)

        observeViewModel()
    }

    private fun observeViewModel() {

        viewModel.dogLiveData.observe(viewLifecycleOwner, Observer { dog ->

            currentDog = dog
            dog?.let { it ->

                dataBinding.dog = dog

                it.imageUrl?.let { colorSting ->

                    setupBackgroundColor(colorSting)

                }
            }
        })
    }

    private fun setupBackgroundColor(url: String) {

        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    Palette.from(resource)
                        .generate {

                            val intColor = it?.lightMutedSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            dataBinding.palette = myPalette

                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_send_sms -> {

                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "check out this dog breed")
                intent.putExtra(
                    Intent.EXTRA_TEXT,"${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM ,currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent,"share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {

        if (sendSmsStarted && permissionGranted) {

            context?.let {

                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",
                    currentDog?.imageUrl
                )

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog, null, false
                )

                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send Sms") { dialog, which ->

                        if (!dialogBinding.smsDestination.text.isNullOrEmpty()) {

                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which ->

                    }
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {

        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)
    }

}