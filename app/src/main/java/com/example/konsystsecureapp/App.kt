package com.example.konsystsecureapp

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("23a8989f-b546-4c32-b6d7-0a8b73679d25")
    }
}
