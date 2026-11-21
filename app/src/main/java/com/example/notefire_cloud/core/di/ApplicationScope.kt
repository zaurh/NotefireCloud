package com.example.notefire_cloud.core.di

import javax.inject.Qualifier

/** A CoroutineScope that lives as long as the app — for work that must outlive a screen. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
