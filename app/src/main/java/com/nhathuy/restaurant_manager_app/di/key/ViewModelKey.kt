package com.nhathuy.restaurant_manager_app.di.key

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * This annotation is used to define the ViewModelKey annotation.
 * The ViewModelKey annotation is used to define the key for the ViewModel.
 *
 * @version 0.1
 * @since 07-02-2025
 * @author TravisHuy
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)