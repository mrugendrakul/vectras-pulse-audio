package com.mrugendra.pulsedroid.ui

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrugendra.pulsedroid.Data.uiState
import com.mrugendra.pulsedroid.soundThread.SoundThread
import com.mrugendra.pulsedroid.soundThread.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.Socket
import java.net.UnknownHostException

open class PulseViewModel:ViewModel(){

    protected var _uiState = MutableStateFlow(uiState())
        private set
    var extUiState : StateFlow<uiState> = _uiState.asStateFlow()

    var playThread : SoundThread? = null

    fun errorDisplay(error:Exception){
        _uiState.update { it->it.copy(
            error = "$error"
        ) }
    }

    fun playAudio( playState:Boolean){
        if (!playState) {
//            playState = true
            _uiState.update { it ->
                it.copy(
                    playButton = true
                )
            }
//            playButton.setText("Stop")
//            if (null != playThread) {
//
//                playThread.Terminate()
////                playThread = null
//            }
//            val server = findViewById<View>(R.id.EditTextServer) as EditText
//            val port = findViewById<View>(R.id.EditTextPort) as EditText
            viewModelScope.launch {
                try {
                    val playThread = SoundThread(
                        _uiState.value.server, _uiState.value.port,
                        callback = { errorDisplay(it) }
                    )
                    Thread(playThread).start()

//                    startAudio()
                } catch (e: Exception) {
                    Log.d(TAG, "Error in viewmodel : $e")
                    _uiState.update { it ->
                        it.copy(
                            playButton = false,
                            error = "$e : ${e.message}"
                        )
                    }

                }
            }
        }

         else {
//            payState = false
//            playButton.setText("Play!")l
            _uiState.update { it->it.copy(
                playButton = false
            ) }
            try
            { playThread?.Terminate() }
            catch (e:Exception){
                Log.d(TAG,"Error in viewmodel : $e")
                _uiState.update { it->it.copy(
                    playButton = true,
                    error = "$e : ${e.message}"
                ) }
            }
        }
    }


    fun ServerChange(server:String){
        _uiState.update { it->it.copy(
            server = server
        ) }
    }

    fun PortChange(port:String){
        _uiState.update { it->it.copy(
            port = port
        ) }
    }

}
