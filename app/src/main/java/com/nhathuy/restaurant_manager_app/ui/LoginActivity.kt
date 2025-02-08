package com.nhathuy.restaurant_manager_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.databinding.ActivityLoginBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.serverAuthCode?.let {
                code ->
                viewModel.handleOAuthCallback("GOOGLE",code)
            }
        }
        catch (e:ApiException){
            Toast.makeText(this,"Google sign in failed: ${e.message}",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupGoogleSignIn()
        setupClickListeners()
        observeViewModel()
    }
    private fun setupGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.google_client_id))
            .requestEmail()
            .build()
        googleSignInClient  = GoogleSignIn.getClient(this, gso)
    }
    private fun setupClickListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = textfieldEmail.text.toString()
                val password = textfieldPassword.text.toString()

                if(validateInput(email,password)){
                    viewModel.login(email, password)
                }
            }
            textView2.setOnClickListener {
                startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            }
            btnSignGoogle.setOnClickListener {
                startGoogleSignIn()
            }
            binding.btnSignGithub.setOnClickListener {

            }
        }
    }
    private fun startGoogleSignIn(){
        val signInClient = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInClient)
    }
    private fun observeViewModel() {
        viewModel.loginResult.observe(this) {
            result ->
            when(result){
                is Resource.Success -> {
                    startActivity(Intent(this,MainActivity::class.java))
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Error ->{
                    Toast.makeText(this,result.message,Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility=View.VISIBLE
                }
            }

        }
        viewModel.oauthResult.observe(this) {
                result ->
            when(result){
                is Resource.Success -> {
                    startActivity(Intent(this,MainActivity::class.java))
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Error ->{
                    Toast.makeText(this,result.message,Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility=View.VISIBLE
                }
            }

        }
    }

    private fun validateInput(email:String,password:String):Boolean{
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.textInputLayoutEmail.error = "Please enter a valid email"
            isValid = false
        }
        else{
            binding.textInputLayoutEmail.error = null
        }

        if(password.isEmpty() || password.length < 8){
            binding.textInputLayoutPassword.error = "Password must be least 8 characters"
            isValid=false
        }
        else{
            binding.textInputLayoutPassword.error = null
        }

        return isValid
    }
}