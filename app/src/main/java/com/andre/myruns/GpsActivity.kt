package com.andre.myruns

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GpsActivity: AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initButtons()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_cancel_map -> finish()
            R.id.button_save_map -> {
                // TODO: Save
            }
        }
    }

    private fun initButtons() {
        findViewById<Button>(R.id.button_cancel_map)
            .setOnClickListener(this)
        findViewById<Button>(R.id.button_save_map)
            .setOnClickListener(this)
    }
}
