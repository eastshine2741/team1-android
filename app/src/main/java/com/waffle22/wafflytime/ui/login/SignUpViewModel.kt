package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.network.dto.SignUpRequest
import com.waffle22.wafflytime.network.dto.TokenContainer
import com.waffle22.wafflytime.util.AuthStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

// TODO: Add StateFlow Enum
// Todo: Add Response Code Enum

class SignUpViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage
): ViewModel() {

    // TODO: Change String type to Enum Class!!!
    private val _signUpState = MutableStateFlow<SignUpStatus>(SignUpStatus.StandBy)
    val signUpState: StateFlow<SignUpStatus> = _signUpState

    fun resetSignUpState(){
        _signUpState.value = SignUpStatus.StandBy
    }

    fun signUp(id: String, password: String){
        viewModelScope.launch {
            try {
                val response: Response<TokenContainer> = wafflyApiService.signUp(SignUpRequest(id, password))
                when (response.code().toString()){
                    "200" -> {
                        authStorage.setAuthInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                        _signUpState.value = SignUpStatus.SignUpOk
                    }
                    "409" -> {
                        _signUpState.value = SignUpStatus.SignUpConflict
                    }
                    "500" -> {
                        Log.d("debug","ami?")
                        _signUpState.value = SignUpStatus.Error_500
                    }
                }
            } catch (e:java.lang.Exception) {
                _signUpState.value = SignUpStatus.Corruption
            }
        }
    }
}