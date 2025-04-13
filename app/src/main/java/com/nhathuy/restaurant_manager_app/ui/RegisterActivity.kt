package com.nhathuy.restaurant_manager_app.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.databinding.ActivityRegisterBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.util.Constants
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject
/**
 * Handles user registration via email, Google, and Github OAuth2.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authWebView:WebView

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
                viewModel.handleOAuthCallback("GOOGLE", code)
            }
        }
        catch (e:ApiException){
            Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private val mapPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val address = data.getStringExtra(MapPickerActivity.EXTRA_SELECTED_ADDRESS)
                address?.let {
                    binding.textfieldAddress.setText(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupGoogleSignIn()
        setupClickListeners()
        observeViewModel()
        setupWebView()
    }
    private fun setupGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.google_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun setupClickListeners() {
        binding.apply {
            btnSignUp.setOnClickListener {
                val email = textfieldEmail.text.toString()
                val name = textfieldName.text.toString()
                val phoneNumber = textfieldPhonenumber.text.toString()
                val password = textfieldPassword.text.toString()
                val address = textfieldAddress.text.toString()

                if(validateInput(email, password,name,phoneNumber,address)){
                    viewModel.register(name, email, password, phoneNumber, address)
                }
            }
            textLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
            }
            btnSignGoogle.setOnClickListener {
                startGoogleSignIn()
            }
            btnSignGithub.setOnClickListener {
                startGithubSignIn()
            }

            chooseLocation.setOnClickListener {
                val intent = Intent(this@RegisterActivity, MapPickerActivity::class.java)
                mapPickerLauncher.launch(intent)
            }

        }
    }
    private fun setupWebView() {
        authWebView = WebView(this).apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    url?.let { urlString ->
                        if (urlString.startsWith(Constants.GITHUB_REDIRECT_URI)) {
                            val uri = Uri.parse(urlString)
                            uri.getQueryParameter("code")?.let { code ->
                                viewModel.handleOAuthCallback("GITHUB", code)
                                runOnUiThread {
                                    (authWebView.parent as? ViewGroup)?.removeView(authWebView)
                                }
                            }
                            return true
                        }
                    }
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun startGithubSignIn() {
        binding.progressBar.visibility = View.VISIBLE

        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addContentView(authWebView, params)

        val authUrl = Uri.parse(Constants.GITHUB_AUTH_URL)
            .buildUpon()
            .appendQueryParameter("client_id", getString(R.string.github_client_id))
            .appendQueryParameter("scope", Constants.GITHUB_SCOPE)
            .appendQueryParameter("redirect_uri", Constants.GITHUB_REDIRECT_URI)
            .build()
            .toString()

        authWebView.loadUrl(authUrl)
    }

    private fun startGoogleSignIn(){
        val signInClient = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInClient)
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) {
                result ->
            when(result){
                is Resource.Success -> {
                    startActivity(Intent(this,LoginActivity::class.java))
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Error ->{
                    Toast.makeText(this,result.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility= View.VISIBLE
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
                    Toast.makeText(this,result.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility= View.VISIBLE
                }
            }

        }
    }

    private fun validateInput(email:String,password:String,name:String,phoneNumber:String, address:String):Boolean{
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

        if(name.isEmpty()){
            binding.textInputLayoutName.error = "Name not empty"
            isValid = false
        }
        else{
            binding.textInputLayoutName.error = null
        }

        if(phoneNumber.isEmpty() || phoneNumber.length != 10){
            binding.textInputLayoutPhonenumber.error = "Phone number not empty and must 10 numbers"
            isValid = false
        }
        else{
            binding.textInputLayoutPhonenumber.error = null
        }

        if(address.isEmpty()){
            binding.textInputLayoutAddress.error = "Address not empty"
            isValid = false
        }
        else{
            binding.textInputLayoutAddress.error = null
        }
        return isValid
    }
}