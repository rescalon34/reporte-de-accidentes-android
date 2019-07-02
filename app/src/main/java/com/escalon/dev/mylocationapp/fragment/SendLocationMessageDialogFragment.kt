package com.escalon.dev.mylocationapp.fragment

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.escalon.dev.mylocationapp.MyLocationApplication
import com.escalon.dev.mylocationapp.R
import com.escalon.dev.mylocationapp.network.NetworkManager
import com.escalon.dev.mylocationapp.network.model.AccessToken
import com.escalon.dev.mylocationapp.network.model.SendLocationMessage
import com.escalon.dev.mylocationapp.network.repository.db.AppLocationDb
import com.escalon.dev.mylocationapp.network.repository.repo.AccessTokenRepository
import com.escalon.dev.mylocationapp.network.repository.repo.SendLocationMessageRepository
import com.fisherman.core.repository.util.ApiErrorResponse
import com.fisherman.core.repository.util.ApiResponse
import com.fisherman.core.repository.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_send_location_message.*

class SendLocationMessageDialogFragment : DialogFragment() {

    private var currentLocation: String? = null
    private var sendLocationMessageRepository: SendLocationMessageRepository? = null
    private var progressBar: ProgressDialog? = null

    var accessTokenRepository = AccessTokenRepository(
        AppLocationDb.getInstance(MyLocationApplication.instance).accessTokenDao(),
        NetworkManager.getNetworkManager()
    )

    companion object {
        const val CURRENT_LOCATION_ARG = "current_location_arg"

        fun getInstance(currentLocation: String?): SendLocationMessageDialogFragment {
            val fragment = SendLocationMessageDialogFragment()
            val bundle = Bundle()
            bundle.putString(CURRENT_LOCATION_ARG, currentLocation ?: "")
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialogFragmentStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_send_location_message, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentLocation = arguments?.getString(CURRENT_LOCATION_ARG)
        Log.d("CURRENTLOCATION", "current location: $currentLocation")

        btn_send_message.setOnClickListener {
            progressBar = ProgressDialog(activity)
            progressBar?.setMessage("Cargando...")
            progressBar?.setCancelable(false)
            progressBar?.show()
            progressBar?.isIndeterminate = true
            if (edt_send_message.text.isNotEmpty()) {
                accessTokenRepository.queryAccessToken().observe(activity, onAccessTokenObserved())
            } else {
                edt_send_message.error = "Por favor ingrese un mensaje"
            }
        }
    }

    private fun onAccessTokenObserved() = Observer<AccessToken> { accessToken ->
        sendLocationMessageRepository = SendLocationMessageRepository(NetworkManager.getNetworkManager(accessToken))
        val sendLocationMessage = currentLocation?.let { SendLocationMessage(it, edt_send_message.text.toString()) }
        sendLocationMessage?.let {
            sendLocationMessageRepository?.sendLocationMessage(sendLocationMessage)
                ?.observe(this, onSendLocationMessageObserved())
        }
    }

    /**
     * Observe result when sending Location message
     */
    private fun onSendLocationMessageObserved() = Observer<ApiResponse<Any>> { messageResponse ->
        when (messageResponse) {
            is ApiSuccessResponse -> {
                Toast.makeText(activity, "Mensaje enviado exitosamente!", Toast.LENGTH_SHORT).show()
                progressBar?.isIndeterminate = false
                progressBar?.dismiss()
                //close dialog after sending data
                dismiss()
            }
            is ApiErrorResponse -> {
                progressBar?.isIndeterminate = false
                progressBar?.dismiss()
                Toast.makeText(activity, "Error! intente de nuevo por favor.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}