package com.escalon.dev.mylocationapp.activity.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.v4.util.PatternsCompat
import android.text.TextUtils
import android.view.View
import com.escalon.dev.mylocationapp.MyLocationApplication
import com.escalon.dev.mylocationapp.network.NetworkManager
import com.escalon.dev.mylocationapp.network.model.LoginRequest
import com.escalon.dev.mylocationapp.network.repository.db.AppLocationDb
import com.escalon.dev.mylocationapp.network.repository.repo.AccessTokenRepository
import com.escalon.dev.mylocationapp.util.SingleLiveEvent

class LoginViewModel : ViewModel() {

    companion object {
        const val LOGIN_EVENT = 4
        const val SIGN_UP_EVENT = 5
    }

    // Observables Values
    val userEmail = ObservableField<String>("")
    val password = ObservableField<String>("")
    val userEmailError = ObservableField<String>("")
    val passwordError = ObservableField<String>("")
    val progressBarVisibility = ObservableInt(View.GONE)
    val loginButtonEnabled: ObservableBoolean = ObservableBoolean(true)
    var loginRequest = LoginRequest("", "")

    private val loginActionEvent = SingleLiveEvent<Int>()

    var accessTokenRepository = AccessTokenRepository(
        AppLocationDb.getInstance(MyLocationApplication.instance).accessTokenDao(),
        NetworkManager.getNetworkManager()
    )

    fun loginButtonClicked() {
        if (isValidForm()) {
            loginRequest = LoginRequest(userEmail.get(), password.get())
            loginActionEvent.value = LOGIN_EVENT
        }
    }

    fun registerButtonClicked() {
        loginActionEvent.value = SIGN_UP_EVENT
    }

    fun getLoginActionEvent(): LiveData<Int> = loginActionEvent

    private fun isValidForm(): Boolean {
        userEmailError.set(
            if (userEmail.get()?.isEmpty() == false)
                if (PatternsCompat.EMAIL_ADDRESS.matcher(userEmail.get()).matches()) ""
                else "Campo debe ser valido"
            else "Campo requerido"
        )

        passwordError.set(
            if (password.get()?.isEmpty() == false) ""
            else "Campo requerido"
        )

        return TextUtils.isEmpty(userEmailError.get()) && TextUtils.isEmpty(passwordError.get())
    }
}