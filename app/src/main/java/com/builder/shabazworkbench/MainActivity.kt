package com.builder.shabazworkbench

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.builder.shabazworkbench.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        Timber.d("before Coroutine ${Thread.currentThread().name}")
        lifecycleScope.launch {
            Timber.d("launch Coroutine ${Thread.currentThread().name}")
            async {  test1("1") }
            async {  test1("2") }
            async {  test1("3") }
            Timber.d("After test1 ${Thread.currentThread().name}")
            test2()
            Timber.d("After test2 ${Thread.currentThread().name}")
        }
        Timber.d("After Coroutine ${Thread.currentThread().name}")

        binding.fab.setOnClickListener { view ->
            run {
                val normal  = measureTime {
                val pref = getSharedPreferences("shabaz_normal", MODE_PRIVATE)
                pref.edit().putString("test", "shabaz").commit()
                pref.getString("test","")
                }
                Log.d("shabaz", "Normal took: $normal ms")
                val ec  = measureTime {
                    val masterKey = MasterKey.Builder(this)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()
                    val ePrefs = EncryptedSharedPreferences.create(
                        this,
                        "secret_shabaz_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                    ePrefs.edit().putString("test", "shabaz").commit()
                    ePrefs.getString("test", "")
                }
                Log.d("shabaz", "encrypted took: $ec ms")



                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    private suspend fun test2() = withContext(Dispatchers.Default) {
        Timber.d("test2 ${Thread.currentThread().name}")
        Thread.sleep(5000)
        Timber.d("test2 Returning ${Thread.currentThread().name}")

    }

    private suspend fun test1(s: String) = withContext(Dispatchers.IO) {
        Timber.d("test1 call #$s on ${Thread.currentThread().name}")
        delay(3000)
        Timber.d("test1 Returning #$s on ${Thread.currentThread().name}")

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}