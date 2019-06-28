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
import com.escalon.dev.mylocationapp.network.model.CreateUserRequest
import com.escalon.dev.mylocationapp.network.repository.db.AppLocationDb
import com.escalon.dev.mylocationapp.network.repository.repo.AccessTokenRepository
import com.escalon.dev.mylocationapp.util.SingleLiveEvent

class SignUpViewModel : ViewModel() {

    companion object {
        const val SIGN_UP_EVENT = 6
        const val SIGN_UP_USER_ARG = "sign_up_user_name_arg"
        const val SIGN_UP_PASSWORD_ARG = "sign_up_user_password_arg"
    }

    // Observables Values
    val userEmail = ObservableField<String>("")
    val password = ObservableField<String>("")
    val userEmailError = ObservableField<String>("")
    val passwordError = ObservableField<String>("")
    val progressBarVisibility = ObservableInt(View.GONE)
    val signUpButtonEnabled: ObservableBoolean = ObservableBoolean(true)

    private val signUpActionEvent = SingleLiveEvent<Int>()
    var createUserRequest = CreateUserRequest("", "")

    var accessTokenRepository = AccessTokenRepository(
        AppLocationDb.getInstance(MyLocationApplication.instance).accessTokenDao(),
        NetworkManager.getNetworkManager()
    )

    fun signUpButtonClicked() {
        if (isValidForm()) {
            createUserRequest = CreateUserRequest(userEmail.get(), password.get())
            signUpActionEvent.value = SIGN_UP_EVENT
        }
    }

    fun getSignUpActionEvent(): LiveData<Int> = signUpActionEvent

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