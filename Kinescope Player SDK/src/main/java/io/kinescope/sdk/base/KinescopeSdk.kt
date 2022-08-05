package io.kinescope.sdk.base

class KinescopeSdk {
    companion object {
        private var KinescopeSdkKey:String? = null
        fun initialize(key:String) {
            KinescopeSdkKey = key
        }


    }

}