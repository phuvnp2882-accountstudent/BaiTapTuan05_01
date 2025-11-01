package com.example.baitaptuan05_01

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneLoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phone Login", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("login") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isCodeSent) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Nhập số điện thoại (VD: +84xxxx)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(context as Activity)
                            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    auth.signInWithCredential(credential).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("profile")
                                        }
                                    }
                                }

                                override fun onVerificationFailed(e: FirebaseException) {
                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                                }

                                override fun onCodeSent(vid: String, token: PhoneAuthProvider.ForceResendingToken) {
                                    verificationId = vid
                                    isCodeSent = true
                                    Toast.makeText(context, "Mã OTP đã được gửi!", Toast.LENGTH_SHORT).show()
                                }
                            })
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2342A4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GỬI MÃ OTP", color = Color.White)
                }
            } else {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("Nhập mã OTP") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                        auth.signInWithCredential(credential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                navController.navigate("profile")
                            } else {
                                Toast.makeText(context, "Sai mã OTP!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2342A4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("XÁC NHẬN", color = Color.White)
                }
            }
        }
    }
}
