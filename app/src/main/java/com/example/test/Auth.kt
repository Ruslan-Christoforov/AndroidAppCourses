package com.example.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Auth : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)


        emailInput.addTextChangedListener(inputWatcher)
        passwordInput.addTextChangedListener(inputWatcher)

        validateInputs()

        loginButton.setOnClickListener {
            // Создание Intent для перехода на MainnActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonOpenLink: Button = findViewById(R.id.button1)

        buttonOpenLink.setOnClickListener {
            openLink("https://vk.com/")
        }

        val buttonOpenLink2: Button = findViewById(R.id.button2)

        buttonOpenLink2.setOnClickListener {
            openLink("https://ok.ru/")
        }
    }

    private val inputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateInputs()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun validateInputs() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Проверка на корректность email (маска текст@текст.текст)
        val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordNotEmpty = password.isNotEmpty()

        loginButton.isEnabled = isEmailValid && isPasswordNotEmpty
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}