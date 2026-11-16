package com.example.track

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = AppDatabase.get(this).userDao()

        val buttonRegister = findViewById<Button>(R.id.button)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        val login = findViewById<EditText>(R.id.login)
        val password = findViewById<EditText>(R.id.password)
        val age = findViewById<EditText>(R.id.age)


        buttonRegister.setOnClickListener {
            val loginText = login.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val age = age.text.toString().toIntOrNull()

            if (loginText.isBlank() || loginText.length < 6){
                Toast.makeText(this, "Логин должен состоять минимум из 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordText.isBlank() || passwordText.length < 6){
                Toast.makeText(this, "Пароль должен состоять минимум из 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (age == null || age in 0..17){
                Toast.makeText(this, "Использование приложения начинается с 18 лет", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existing = dao.findByLogin(login.text.toString())
                if (existing != null) {
                    Toast.makeText(this@MainActivity, "Этот логин уже используется", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                dao.insert(User(login = login.text.toString(), password = password.text.toString(), age = age))
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }

        buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}