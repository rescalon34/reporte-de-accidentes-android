package com.escalon.dev.mylocationapp.activity.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.escalon.dev.mylocationapp.MyLocationApplication.Companion.preferences
import com.escalon.dev.mylocationapp.R
import com.escalon.dev.mylocationapp.activity.MapActivity
import com.escalon.dev.mylocationapp.activity.login.LoginViewModel.Companion.LOGIN_EVENT
import com.escalon.dev.mylocationapp.activity.login.LoginViewModel.Companion.SIGN_UP_EVENT
import com.escalon.dev.mylocationapp.databinding.ActivityLoginBinding
import com.escalon.dev.mylocationapp.network.model.AccessToken
import com.escalon.dev.mylocationapp.network.model.LoginRequest
import com.escalon.dev.mylocationapp.util.PrefUtils
import com.fisherman.core.repository.util.ApiErrorResponse
import com.fisherman.core.repository.util.ApiResponse
import com.fisherman.core.repository.util.ApiSuccessResponse

class LoginActivity : AppCompatActivity() {

    private var loginViewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Iniciar Sesión"

        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.loginViewModel = this.loginViewModel

        preferences?.currentScreen = PrefUtils.LOGIN_SCREEN

        val bundle = intent.extras
        val userNameArg = bundle?.getString(SignUpViewModel.SIGN_UP_USER_ARG)
        val passwordArg = bundle?.getString(SignUpViewModel.SIGN_UP_PASSWORD_ARG)

        // receive parameters and log user in
        if (userNameArg != null && passwordArg != null) {
            loginViewModel?.userEmail?.set(userNameArg)
            loginViewModel?.password?.set(passwordArg)
            loginViewModel?.loginRequest = LoginRequest(userNameArg, passwordArg)
            performLoginAction()
        }

        loginViewModel?.getLoginActionEvent()?.observe(this, onLoginActionObserved())
    }

    private fun onLoginActionObserved() = Observer<Int> { event ->
        when (event) {
            LOGIN_EVENT -> performLoginAction()
            SIGN_UP_EVENT -> routeUserToSignUpScreen()
        }
    }

    private fun performLoginAction() {
        loginViewModel?.let {
            it.progressBarVisibility.set(View.VISIBLE)
            it.loginButtonEnabled.set(false)
            it.accessTokenRepository.getAccessToken(it.loginRequest).observe(this, getAccessTokenFromApiObserved())
        }
    }

    private fun getAccessTokenFromApiObserved() = Observer<ApiResponse<AccessToken>> { responseLogin ->
        when (responseLogin) {
            is ApiSuccessResponse -> {
                //Toast.makeText(this, "login success: ", Toast.LENGTH_SHORT).show()
                Log.d("RESPONSELOGIN", "response login: " + responseLogin.body)
                loginViewModel?.progressBarVisibility?.set(View.GONE)
                loginViewModel?.loginButtonEnabled?.set(true)

                // save token into databse
                loginViewModel?.accessTokenRepository?.insertAccessTokenRepo(responseLogin.body)
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                finish()
            }
            is ApiErrorResponse -> {
                Toast.makeText(this, "There was an error trying to log in", Toast.LENGTH_SHORT).show()
                Log.d("RESPONSELOGIN", "response code: " + responseLogin.responseCode)
                loginViewModel?.progressBarVisibility?.set(View.GONE)
                loginViewModel?.loginButtonEnabled?.set(true)
            }
        }
    }

    private fun routeUserToSignUpScreen() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}
