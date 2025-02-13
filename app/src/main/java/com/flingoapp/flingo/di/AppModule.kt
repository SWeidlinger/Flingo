package com.flingoapp.flingo.di

import android.app.Application
import com.flingoapp.flingo.data.network.AndroidConnectivityObserver
import com.flingoapp.flingo.data.network.ConnectivityObserver

class MainApplication : Application() {
    companion object {
        lateinit var appModule: AppModule
        lateinit var connectivityObserver: ConnectivityObserver
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
        connectivityObserver = AndroidConnectivityObserver(this)
    }
}

interface AppModule {
    val genAiModule: GenAiModule
}

class AppModuleImpl(
    private val application: Application
) : AppModule {
    override val genAiModule: GenAiModule by lazy {
        GenAiModuleImpl(application)
    }
}