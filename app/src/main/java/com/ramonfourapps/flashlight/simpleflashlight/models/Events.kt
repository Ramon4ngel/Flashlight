package com.ramonfourapps.flashlight.simpleflashlight.models

class Events {
    class StateChanged(val isEnabled: Boolean)

    class CameraUnavailable

    class StopStroboscope

    class StopSOS
}
