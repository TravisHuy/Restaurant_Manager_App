package com.nhathuy.restaurant_manager_app.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.databinding.ActivityLoginBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.service.RestaurantFirebaseMessagingService
import com.nhathuy.restaurant_manager_app.util.Constants
import com.nhathuy.restaurant_manager_app.util.Constants.KEY_EMAIL
import com.nhathuy.restaurant_manager_app.util.Constants.KEY_PASSWORD
import com.nhathuy.restaurant_manager_app.util.Constants.KEY_REMEMBER_ME
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 * Handles user authentication via email, Google , and Github OAuth2.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authWebView:WebView

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }


    private lateinit var sharedPreferences: SharedPreferences
    private var currentEmail:String = ""
    private var currentPassword:String = ""
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

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        loadSaveCredentials()


        setupGoogleSignIn()
        setupClickListeners()
        observeViewModel()
        setupWebView()
    }

    /**
     * Load saved email and password from shared preferences
     */
    private fun loadSaveCredentials(){
        val email = sharedPreferences.getString(Constants.KEY_EMAIL,"")
        val password = sharedPreferences.getString(Constants.KEY_PASSWORD,"")
        val rememberMe = sharedPreferences.getBoolean(Constants.KEY_REMEMBER_ME,false)

        binding.textfieldEmail.setText(email)
        binding.textfieldPassword.setText(password)
        binding.checkBoxRememberMe.isChecked = rememberMe
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
                currentEmail= textfieldEmail.text.toString()
                currentPassword = textfieldPassword.text.toString()

                if(validateInput(currentEmail,currentPassword)){
                    viewModel.login(currentEmail,currentPassword)
                    registerFcmToken()
                }
            }

            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            btnSignGoogle.setOnClickListener {
                startGoogleSignIn()
            }

            btnSignGithub.setOnClickListener {
                startGithubSignIn()
            }
        }
    }
    private fun setupWebView(){
        authWebView = WebView(this).apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?
                ): Boolean {
                    url?.let { urlString ->
                        if (urlString.startsWith(Constants.GITHUB_REDIRECT_URI)) {
                            val uri = Uri.parse(urlString)
                            uri.getQueryParameter("code")?.let { code ->
                                viewModel.handleOAuthCallback("GITHUB", code)
                                authWebView.visibility = View.GONE
                            }
                            return true
                        }
                    }
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        binding.root.addView(authWebView)
        authWebView.visibility = View.GONE
    }

    private fun startGoogleSignIn(){
        val signInClient = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInClient)
    }
    private fun startGithubSignIn(){
        binding.progressBar.visibility = View.VISIBLE
        authWebView.visibility = View.VISIBLE

        val authUrl = Uri.parse(Constants.GITHUB_AUTH_URL)
            .buildUpon()
            .appendQueryParameter("client_id", getString(R.string.github_client_id))
            .appendQueryParameter("scope", Constants.GITHUB_SCOPE)
            .appendQueryParameter("redirect_uri", Constants.GITHUB_REDIRECT_URI)
            .build()
            .toString()

        authWebView.loadUrl(authUrl)
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) {
            result ->
            when(result){
                is Resource.Success -> {
                    if(binding.checkBoxRememberMe.isChecked){
                        saveCredentials(currentEmail,currentPassword)
                    }
                    else{
                        clearCredentials()
                    }
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

    private fun saveCredentials(email: String, password: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_REMEMBER_ME, true)
            apply()
        }
    }

    private fun clearCredentials() {
        with(sharedPreferences.edit()) {
            remove(KEY_EMAIL)
            remove(KEY_PASSWORD)
            putBoolean(KEY_REMEMBER_ME, false)
            apply()
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
    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                RestaurantFirebaseMessagingService.registerToken(this, token)
            }
        }
    }
}