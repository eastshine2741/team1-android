package com.waffle22.wafflytime.ui.login


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import android.content.Intent.getIntent
import androidx.compose.ui.focus.FocusDirection.Companion.In
import org.json.JSONObject
import java.net.MalformedURLException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var alertDialog: AlertDialog

    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLogin.setOnClickListener { login() }
            btnSignup.setOnClickListener { signUp() }
            kakaoLoginButton.setOnClickListener { kakaoLogin() }
            naverLoginButton.setOnClickListener { naverLogin() }
            googleLoginButton.setOnClickListener { googleLogin() }
            githubLoginButton.setOnClickListener { githubLogin() }

        }

        lifecycleScope.launch {
            viewModel.loginState.collect {
                loginLogic(it)
            }
        }

    }

    private fun login() {
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        viewModel.login(
            binding.idEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )

    }

    private fun kakaoLogin() {

        val CLIENT_ID = "14e86042a3842d295c4ef5af422fac3d"
        val REDIRECT_URI = "http://localhost:3000/api/auth/social/login/kakao"
        val KAKAO_AUTH_URL =
            URL("https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code")
        val thread = Thread(){
            val connection = KAKAO_AUTH_URL.openConnection()
            BufferedReader(InputStreamReader(connection.getInputStream())).use { inp ->
                var line: String?
                while (inp.readLine().also { line = it } != null) {
                    println(line)
                }
            }
        }
        thread.start()
    }




    private fun loginLogic(status: SlackState<Nothing>) {
        when (status.status) {
            "0" -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when (status.status) {
                    "200" -> {
                        findNavController().navigate(LoginFragmentDirections.actionGlobalAuthCheckFragment())
                    }
                    else -> {
                        Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetAuthState()
            }
        }
    }

    private fun signUp() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }
}
