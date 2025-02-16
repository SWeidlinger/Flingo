package com.flingoapp.flingo.di

import android.app.Application
import com.flingoapp.flingo.data.network.AndroidConnectivityObserver
import com.flingoapp.flingo.data.network.ConnectivityObserver
import com.flingoapp.flingo.data.repository.BookDataSource
import com.flingoapp.flingo.data.repository.BookDataSourceJsonImpl
import com.flingoapp.flingo.data.repository.BookRepository
import com.flingoapp.flingo.data.repository.BookRepositoryImpl

class MainApplication : Application() {
    companion object {
        lateinit var appModule: AppModule
        lateinit var connectivityObserver: ConnectivityObserver
        lateinit var bookDataSource: BookDataSource
        lateinit var bookRepository: BookRepository
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
        connectivityObserver = AndroidConnectivityObserver(this)
        bookDataSource = BookDataSourceJsonImpl(this)
        bookRepository = BookRepositoryImpl(bookDataSource)
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