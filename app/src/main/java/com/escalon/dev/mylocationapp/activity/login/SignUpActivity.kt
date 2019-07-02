package com.escalon.dev.mylocationapp.activity.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.escalon.dev.mylocationapp.R
import com.escalon.dev.mylocationapp.activity.login.SignUpViewModel.Companion.SIGN_UP_EVENT
import com.escalon.dev.mylocationapp.activity.login.SignUpViewModel.Companion.SIGN_UP_PASSWORD_ARG
import com.escalon.dev.mylocationapp.activity.login.SignUpViewModel.Companion.SIGN_UP_USER_ARG
import com.escalon.dev.mylocationapp.databinding.ActivitySignUpBinding
import com.fisherman.core.repository.util.ApiErrorResponse
import com.fisherman.core.repository.util.ApiResponse
import com.fisherman.core.repository.util.ApiSuccessResponse

class SignUpActivity : AppCompatActivity() {

    private var signUpViewModel: SignUpViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        title = "Registrarse"

        val binding = DataBindingUtil.setContentView<ActivitySignUpBinding>(this, R.layout.activity_sign_up)
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        binding.signUpViewModel = this.signUpViewModel

        signUpViewModel?.getSignUpActionEvent()?.observe(this, onSignUpActionObserved())
    }

    private fun onSignUpActionObserved() = Observer<Int> { signUpEvent ->
        when (signUpEvent) {
            SIGN_UP_EVENT -> performRegisterUserAction()
        }
    }

    private fun performRegisterUserAction() {
        signUpViewModel?.let {
            it.progressBarVisibility.set(View.VISIBLE)
            it.signUpButtonEnabled.set(false)

            it.accessTokenRepository.registerUser(it.createUserRequest).observe(this, onRegisterUserObserved())
        }
    }

    private fun onRegisterUserObserved() = Observer<ApiResponse<Any>> { responseSignUp ->
        when (responseSignUp) {
            is ApiSuccessResponse -> {
                Toast.makeText(this, "Registro exitoso!", Toast.LENGTH_SHORT).show()
                signUpViewModel?.progressBarVisibility?.set(View.GONE)
                signUpViewModel?.signUpButtonEnabled?.set(true)

                val userNameArg = signUpViewModel?.createUserRequest?.userName
                val userPasswordArg = signUpViewModel?.createUserRequest?.password

                // take user to login using the obtained credentials
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra(SIGN_UP_USER_ARG, userNameArg)
                intent.putExtra(SIGN_UP_PASSWORD_ARG, userPasswordArg)
                intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            is ApiErrorResponse -> {
                Toast.makeText(this, "Error! " + responseSignUp.defaultErrorMessages(), Toast.LENGTH_SHORT).show()
                signUpViewModel?.progressBarVisibility?.set(View.GONE)
                signUpViewModel?.signUpButtonEnabled?.set(true)
            }
        }

    }
}
