package com.example.track

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dao = AppDatabase.get(this).userDao()

        val buttonRegister = findViewById<Button>(R.id.buttonReg)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        val login = findViewById<EditText>(R.id.login)
        val password = findViewById<EditText>(R.id.password)

        buttonLogin.setOnClickListener {
            val loginText = login.text.toString().trim()
            val passwordText = password.text.toString().trim()

            lifecycleScope.launch {
                val existing = dao.findByLogin(loginText)

                if (loginText.isBlank() || passwordText.isBlank()){
                    Toast.makeText(this@LoginActivity, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (existing == null){
                    Toast.makeText(this@LoginActivity, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (existing.password != passwordText){
                    Toast.makeText(this@LoginActivity, "Неверный пароль", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                Toast.makeText(this@LoginActivity, "Добро пожаловать, ${existing.login}!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java).apply {
                    putExtra("USER_LOGIN", existing.login)
                    putExtra("USER_ID", existing.id)
                })
                finish()
            }


        }
        buttonRegister.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}