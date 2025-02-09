package com.flingoapp.flingo.di

import android.app.Application

class MainApplication : Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
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