package com.neoguri

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neoguri.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)

    }

}