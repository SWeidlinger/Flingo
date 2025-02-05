package com.flingoapp.flingo.di

import android.app.Application
import android.content.Context

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
    private val appContext: Context
) : AppModule {
    override val genAiModule: GenAiModule by lazy {
        GenAiModuleImpl()
    }
}