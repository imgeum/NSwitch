package com.neoguri

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.neoguri.databinding.ActivityMainBinding
import com.neoguri.nswitch.NSwitch

class MainActivity : AppCompatActivity(), NSwitch.SwitchEventListener {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)

        mBinding.mySwitch.setOnSwitchEvent(this)
        mBinding.mySwitch.setCheck(true)

    }

    override fun onOnOff(view: View, boolean: Boolean) {
        if(boolean){
            Toast.makeText(this, "on", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "off", Toast.LENGTH_SHORT).show()
        }
    }

}