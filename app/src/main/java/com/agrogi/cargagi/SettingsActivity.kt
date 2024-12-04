package com.agrogi.cargagi

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var userIdEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val editTextIp = findViewById<EditText>(R.id.editTextIp)
        userIdEditText = findViewById(R.id.user_id_edit_text)


        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        editTextIp.setText(sharedPreferences.getString("server_ip", ""))
        val userId = sharedPreferences.getString("user_id", "")
        userIdEditText.setText(userId)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val newUserId = userIdEditText.text.toString()

        buttonSave.setOnClickListener {
            val ip = editTextIp.text.toString()
            with(sharedPreferences.edit()) {
                putString("server_ip", ip)
                putString("user_id", newUserId)
                apply()
            }
            finish()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}