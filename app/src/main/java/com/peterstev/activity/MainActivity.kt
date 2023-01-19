package com.peterstev.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.peterstev.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.main_nav_host).navigateUp()
}
