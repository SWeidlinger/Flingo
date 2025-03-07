package com.flingoapp.flingo.di

import android.app.Application
import android.content.Context
import com.flingoapp.flingo.data.datasource.BookDataSource
import com.flingoapp.flingo.data.datasource.BookDataSourceJsonImpl
import com.flingoapp.flingo.data.network.AndroidConnectivityObserver
import com.flingoapp.flingo.data.network.ConnectivityObserver
import com.flingoapp.flingo.data.repository.BookRepository
import com.flingoapp.flingo.data.repository.BookRepositoryImpl
import com.flingoapp.flingo.data.repository.PersonalizationRepository
import com.flingoapp.flingo.data.repository.PersonalizationRepositoryImpl

/**
 * Main application
 *
 * @constructor Create empty Main application
 */
class MainApplication : Application() {
    companion object {
        lateinit var appModule: AppModule
        lateinit var connectivityObserver: ConnectivityObserver
        lateinit var bookDataSource: BookDataSource
        lateinit var bookRepository: BookRepository
        lateinit var personalizationRepository: PersonalizationRepository
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
        connectivityObserver = AndroidConnectivityObserver(this)
        bookDataSource = BookDataSourceJsonImpl(this)
        bookRepository = BookRepositoryImpl(bookDataSource)

        personalizationRepository = PersonalizationRepositoryImpl(
            genAiModule = appModule.genAiModule,
            bookRepository = bookRepository
        )
    }
}

/**
 * App module
 *
 * @constructor Create empty App module
 */
interface AppModule {
    val genAiModule: GenAiModule
}

/**
 * App module impl
 *
 * @property context
 * @constructor Create empty App module impl
 */
class AppModuleImpl(
    val context: Context
) : AppModule {
    override val genAiModule: GenAiModule by lazy {
        GenAiModuleImpl(context)
    }
}