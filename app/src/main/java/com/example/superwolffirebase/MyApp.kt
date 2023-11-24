package com.example.superwolffirebase

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.superwolffirebase.DebuggingActivityLifecycleCallback.Companion.DEBUG_FRAGMENT
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(DebuggingActivityLifecycleCallback())
    }
}

class DebuggingActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {
    companion object {
        const val DEBUG_ACTIVITY = "DEBUG_ACTIVITY"
        const val DEBUG_FRAGMENT = "DEBUG_FRAGMENT"
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(DEBUG_ACTIVITY, "${activity::class.java.simpleName} --> onCreated")
        activity.registerFragmentLifecycleCallbacks()
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(DEBUG_ACTIVITY, "${activity::class.java.simpleName} --> onStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(DEBUG_ACTIVITY, "${activity::class.java.simpleName} --> onResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(DEBUG_ACTIVITY, "${activity::class.java.simpleName} --> onPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(DEBUG_ACTIVITY, "${activity::class.java.simpleName} --> onStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(DEBUG_ACTIVITY, "${activity::class.java.simpleName} --> onSaveState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(DEBUG_ACTIVITY, "${activity::class.java.simpleName} --> onDestroyed")
    }
}

fun Activity.registerFragmentLifecycleCallbacks() {
    if (this is FragmentActivity) {
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {

                override fun onFragmentCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    savedInstanceState: Bundle?
                ) {
                    super.onFragmentCreated(fm, f, savedInstanceState)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onCreated")
                }

                override fun onFragmentViewCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    v: View,
                    savedInstanceState: Bundle?
                ) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onViewCreated")
                }

                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onResumed")
                }

                override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                    super.onFragmentPaused(fm, f)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onPaused")
                }

                override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                    super.onFragmentStopped(fm, f)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onStopped")
                }

                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentViewDestroyed(fm, f)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onViewDestroyed")
                }

                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDestroyed(fm, f)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onDestroyed")
                }

                override fun onFragmentSaveInstanceState(
                    fm: FragmentManager,
                    f: Fragment,
                    outState: Bundle
                ) {
                    super.onFragmentSaveInstanceState(fm, f, outState)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onSaveInstanceState")
                }

                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    super.onFragmentStarted(fm, f)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onFragmentStarted")
                }

                override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDetached(fm, f)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onFragmentDetached")
                }

                override fun onFragmentAttached(
                    fm: FragmentManager,
                    f: Fragment,
                    context: Context
                ) {
                    super.onFragmentAttached(fm, f, context)
                    Log.d(DEBUG_FRAGMENT, "${f::class.java.simpleName} --> onFragmentAttached")
                }
            }, true
        )
    }
}