package com.ramonfourapps.flashlight

import android.content.pm.ActivityInfo
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramonfourapps.flashlight.simpleflashlight.extensions.config
import com.ramonfourapps.flashlight.simpleflashlight.helpers.CameraTorchListener
import com.ramonfourapps.flashlight.simpleflashlight.helpers.MIN_BRIGHTNESS_LEVEL
import com.ramonfourapps.flashlight.simpleflashlight.helpers.MyCameraImpl
import com.ramonfourapps.flashlight.ui.theme.FlashlightTheme
import com.ramonfourapps.flashlight.simpleflashlight.models.Events
import com.ramonfourapps.flashlight.ui.theme.Gray50_500
import com.ramonfourapps.flashlight.ui.theme.Gray50_700
import com.ramonfourapps.flashlight.ui.theme.Pink80
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : ComponentActivity() {

    private val MAX_STROBO_DELAY = 2000L
    private val MIN_STROBO_DELAY = 10L
    private val FLASHLIGHT_STATE = "flashlight_state"
    private val STROBOSCOPE_STATE = "stroboscope_state"

    private var mBus: EventBus? = null
    private var mCameraImpl: MyCameraImpl? = null
    private var mIsFlashlightOn = false
    private var reTurnFlashlightOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBus = EventBus.getDefault()

        setContent {
            setContent {
                FlashlightTheme() {
                    mCameraImpl?.let { MyApp(modifier = Modifier.fillMaxSize(), it) }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        mCameraImpl!!.handleCameraSetup()
        checkState(MyCameraImpl.isFlashlightOn)

        if (config.turnFlashlightOn && reTurnFlashlightOn) {
            mCameraImpl!!.enableFlashlight()
        }

        reTurnFlashlightOn = true


    }

    override fun onStart() {
        super.onStart()
        mBus!!.register(this)

        if (mCameraImpl == null) {
            setupCameraImpl()
        }
    }

    override fun onStop() {
        super.onStop()
        mBus!!.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    private fun releaseCamera() {
        mCameraImpl?.releaseCamera()
        mCameraImpl = null
    }

    @Subscribe
    fun stateChangedEvent(event: Events.StateChanged) {
        checkState(event.isEnabled)
    }

    @Subscribe
    fun stopStroboscope(event: Events.StopStroboscope) {
        //stroboscope_bar.beInvisible()
        //changeIconColor(getContrastColor(), stroboscope_btn)
    }

    @Subscribe
    fun stopSOS(event: Events.StopSOS) {
        //sos_btn.setTextColor(getContrastColor())
    }

    private fun checkState(isEnabled: Boolean) {
        if (isEnabled) {
            enableFlashlight()
        } else {
            disableFlashlight()
        }
    }

    private fun enableFlashlight() {
        //changeIconColor(getProperPrimaryColor(), flashlight_btn)
        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = true

        //sos_btn.setTextColor(getContrastColor())

        //changeIconColor(getContrastColor(), stroboscope_btn)
        //stroboscope_bar.beInvisible()
    }

    private fun disableFlashlight() {
        //changeIconColor(getContrastColor(), flashlight_btn)
        //window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = false
    }

    private fun changeIconColor(color: Int, imageView: ImageView?) {
        //imageView!!.background.applyColorFilter(color)
    }

    private fun setupCameraImpl() {
        mCameraImpl = MyCameraImpl.newInstance(this, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {
                if (mCameraImpl!!.supportsBrightnessControl()) {
                    //brightness_bar.beVisibleIf(isEnabled)
                }
            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        })
        if (config.turnFlashlightOn) {
            mCameraImpl!!.enableFlashlight()
        }
        setupBrightness()
    }

    private fun setupBrightness() {
        /*brightness_bar.max = mCameraImpl?.getMaximumBrightnessLevel() ?: MIN_BRIGHTNESS_LEVEL
        brightness_bar.progress = mCameraImpl?.getCurrentBrightnessLevel() ?: MIN_BRIGHTNESS_LEVEL
        brightness_bar.onSeekBarChangeListener { level ->
            val newLevel = level.coerceAtLeast(MIN_BRIGHTNESS_LEVEL)
            mCameraImpl?.updateBrightnessLevel(newLevel)
            config.brightnessLevel = newLevel
        }*/
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier, mCameraImpl: MyCameraImpl) {

            //FlashOnOff()
    FlashOnOff(modifier = Modifier.fillMaxSize(), mCameraImpl)

}



@Composable
private fun FlashOnOff(
    modifier: Modifier = Modifier,
    mCameraImpl: MyCameraImpl
) {

    val state = remember{
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        color = Color(0xFF4F5657)

    ){
        Column(modifier = Modifier.background(color = Gray50_700),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {

            Button(onClick = {
                //state.value=!state.value
                if (state.value) {
                    //TODO: camera flash on
                    mCameraImpl!!.toggleFlashlight()

                } else {
                    //TODO: camera flash off
                    mCameraImpl!!.toggleFlashlight()
                }
                state.value=!state.value

            }, modifier = Modifier.size(150.dp),colors= ButtonDefaults.buttonColors(Gray50_500), shape = CircleShape,) {
                if(state.value){
                    Text(text = "ON", color = Color.White
                        , fontSize = 25.sp, fontWeight = FontWeight.Bold )
                    Icon(painter = painterResource(id = R.drawable.ic_power_24),
                         contentDescription = "Power",
                         tint= Color.White)
                }else{
                    Text(text = "OFF", color = Color.Black
                        , fontSize = 25.sp, fontWeight = FontWeight.Bold )
                    Icon(painter = painterResource(id = R.drawable.ic_power_24),
                        contentDescription = "Power",
                        tint= Color.Black)
                }
            }
        }
    }
}




@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    FlashlightTheme {
        //FlashOnOff()
        var mCameraImpl: MyCameraImpl? = null
        mCameraImpl = MyCameraImpl.newInstance(LocalContext.current, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {
                if (mCameraImpl!!.supportsBrightnessControl()) {
                    //brightness_bar.beVisibleIf(isEnabled)
                }
            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        })
        mCameraImpl?.let { FlashOnOff(modifier = Modifier.fillMaxSize(), it) }
        //Default Preview fail with: java.lang.AssertionError: Unsupported Service: camera

    }
}


@Preview
@Composable
fun MyAppPreview() {
    FlashlightTheme {
        //MyApp(Modifier.fillMaxSize())
        var mCameraImpl: MyCameraImpl? = null
        mCameraImpl = MyCameraImpl.newInstance(LocalContext.current, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {
                if (mCameraImpl!!.supportsBrightnessControl()) {
                    //brightness_bar.beVisibleIf(isEnabled)
                }
            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        })
        mCameraImpl?.let { MyApp(modifier = Modifier.fillMaxSize(), it) }
        //Default Preview fail with: java.lang.AssertionError: Unsupported Service: camera
    }
}


