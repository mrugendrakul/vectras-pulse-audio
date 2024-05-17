package com.mrugendra.pulsedroid.soundThread

import android.media.AudioDeviceCallback
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.mrugendra.pulsedroid.ui.PulseViewModel
import kotlinx.coroutines.flow.update
import java.io.BufferedInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.Socket
import java.net.UnknownHostException

val TAG = "SoundThread"

class SoundThread(private val Server: String, Port: String, private  val callback :(String)->Unit) : Runnable{
    private var mTerminate = false
    private var mServer = Server
    private val mPort = Port.toInt()

    fun Terminate() {
        mTerminate = true
    }

    override fun run() {
        var sock: Socket? = null
        var audioData: BufferedInputStream? = null
        try {
            sock = Socket(mServer, mPort)
            callback("Connected")
        } catch (e: UnknownHostException) {
            // TODO if the host name could not be resolved into an IP address.
            Terminate()
            e.printStackTrace()
            Log.e(TAG,"Unknown Host here  : $e")
            callback("Unknown Host : $e")
//            throw e
        } catch (e: IOException) {
            // TODO if an error occurs while creating the socket
            Terminate()
            e.printStackTrace()
            Log.e(TAG,"IOException : $e")
            callback(e.message.toString())
//            sock = null
//            throw e
        } catch (e: SecurityException) {
            // TODO if a security manager exists and it denies the permission to
            // connect to the given address and port.
            Terminate()
            e.printStackTrace()
            Log.e(TAG,"Security : $e")
            callback(e.message.toString())
//            sock = null
//            throw e
        }

        if (!mTerminate) {
            try {
                audioData = BufferedInputStream(sock!!.getInputStream())
            } catch (e: UnsupportedEncodingException) {
                // TODO Auto-generated catch block
                Terminate()
                e.printStackTrace()
//                throw e
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                Terminate()
                e.printStackTrace()
                callback(e.message.toString())
//                throw e
            }
        }

        // Create AudioPlayer
        /*
		 * final int sampleRate = AudioTrack
		 * .getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
		 */
        // TODO native audio?
        val sampleRate = 48000

        val musicLength = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_CONFIGURATION_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
            AudioFormat.ENCODING_PCM_16BIT, musicLength,
            AudioTrack.MODE_STREAM
        )
        audioTrack.play()

        // TODO buffer size computation
        val audioBuffer = ByteArray(musicLength * 8)

        while (!mTerminate) {
            try {
                val sizeRead = audioData!!.read(audioBuffer, 0, musicLength * 8)
                var sizeWrite = audioTrack.write(audioBuffer, 0, sizeRead)
                if (sizeWrite == AudioTrack.ERROR_INVALID_OPERATION) {
                    sizeWrite = 0
                }
                if (sizeWrite == AudioTrack.ERROR_BAD_VALUE) {
                    sizeWrite = 0
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                callback(e.message.toString())
//                throw e
            }
        }

        audioTrack.stop()
        sock = null
        audioData = null
    }

}
