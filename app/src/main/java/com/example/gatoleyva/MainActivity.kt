package com.example.gatoleyva

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gatoleyva.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // DataBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}