package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.util.Base64
import android.graphics.Bitmap
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.*

@Composable
fun CareSureApp(
    viewModel: CareSureViewModel,
    modifier: Modifier = Modifier
) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedIngredient by viewModel.selectedIngredient.collectAsStateWithLifecycle()
    
    // Gradient dark background mimicking high-end Stripe/Linear design
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkslateBg,
                        Color(0xFF020617)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Standard App Top Bar
            AppTopHeader(
                activeScreen = activeScreen,
                currentUser = currentUser,
                onBack = {
                    when (activeScreen) {
                        AppScreen.ProductDetails -> viewModel.setNavigation(AppScreen.ProductSearch)
                        AppScreen.SignUp -> viewModel.setNavigation(AppScreen.Landing)
                        AppScreen.SignIn -> viewModel.setNavigation(AppScreen.Landing)
                        AppScreen.EditProfile -> viewModel.setNavigation(AppScreen.Dashboard)
                        else -> viewModel.setNavigation(AppScreen.Dashboard)
                    }
                },
                onLogout = { viewModel.logout() },
                onProfileClick = { viewModel.setNavigation(AppScreen.EditProfile) },
                onAdminClick = { viewModel.setNavigation(AppScreen.AdminDashboard) }
            )
            
            // Screens content with transitions
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeScreen) {
                    AppScreen.Landing -> LandingScreen(
                        viewModel = viewModel,
                        onEnterSignIn = { viewModel.setNavigation(AppScreen.SignIn) },
                        onEnterSignUp = { viewModel.setNavigation(AppScreen.SignUp) }
                    )
                    AppScreen.SignIn -> SignInScreen(
                        viewModel = viewModel,
                        onSignUpNow = { viewModel.setNavigation(AppScreen.SignUp) }
                    )
                    AppScreen.SignUp -> SignUpScreen(
                        viewModel = viewModel,
                        onSignInNow = { viewModel.setNavigation(AppScreen.SignIn) }
                    )
                    AppScreen.Dashboard -> DashboardScreen(viewModel = viewModel)
                    AppScreen.EditProfile -> EditProfileScreen(viewModel = viewModel)
                    AppScreen.ProductSearch -> ProductSearchScreen(viewModel = viewModel)
                    AppScreen.ProductDetails -> ProductDetailsScreen(viewModel = viewModel)
                    AppScreen.SkinJourney -> SkinJourneyScreen(viewModel = viewModel)
                    AppScreen.MedicineCabinet -> MedicineCabinetScreen(viewModel = viewModel)
                    AppScreen.Community -> CommunityScreen(viewModel = viewModel)
                    AppScreen.CareBot -> CareBotScreen(viewModel = viewModel)
                    AppScreen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
                }
            }
            
            // Premium Bottom Navigation Bar (Shown only when User is Logged In)
            if (currentUser != null && activeScreen != AppScreen.Landing && activeScreen != AppScreen.SignIn && activeScreen != AppScreen.SignUp) {
                AppBottomNavBar(
                    activeScreen = activeScreen,
                    onNavigate = { viewModel.setNavigation(it) }
                )
            }
        }
        
        // Modal Sheet Dialog for Ingredient Intelligence
        selectedIngredient?.let { ingredient ->
            IngredientIntelligenceDialog(
                ingredient = ingredient,
                onDismiss = { viewModel.closeIngredientIntelligence() }
            )
        }
    }
}

// --- App Headers & Bars ---

@Composable
fun AppTopHeader(
    activeScreen: AppScreen,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    val showBack = activeScreen != AppScreen.Landing && activeScreen != AppScreen.Dashboard && activeScreen != AppScreen.SignIn && activeScreen != AppScreen.SignUp
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0x1F2A3F), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    tint = TextWhite
                )
            }
        } else {
            // Elegant brand logo with emerald pulse
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(HospitalEmerald, RoundedCornerShape(8.dp))
                        .drawBehind {
                            drawCircle(
                                color = AccentMint,
                                radius = 6.dp.toPx(),
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                )
                Text(
                    text = "CareSure AI+",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            }
        }
        
        // Profile quick slot or Sign in buttons
        if (currentUser != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Admin dashboard button for demonstration
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0x0E2EA8), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Admin Area",
                        tint = AccentMint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0x1F2A3F), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Edit Profile",
                        tint = AccentMint
                    )
                }
                
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF2E1A1A), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        tint = ClinicalRed
                    )
                }
            }
        } else if (activeScreen == AppScreen.Landing) {
            Button(
                onClick = onBack, // placeholder inside Landing
                colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Portal", color = TextWhite)
            }
        }
    }
}

@Composable
fun AppBottomNavBar(
    activeScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF0F172A),
        tonalElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        val items = listOf(
            Triple(AppScreen.Dashboard, Icons.Default.Home, "Home"),
            Triple(AppScreen.ProductSearch, Icons.Default.Search, "Scan/Find"),
            Triple(AppScreen.MedicineCabinet, Icons.Default.MedicalServices, "Cabinet"),
            Triple(AppScreen.SkinJourney, Icons.Default.Face, "Journey"),
            Triple(AppScreen.Community, Icons.Default.Forum, "Forum"),
            Triple(AppScreen.CareBot, Icons.Default.SmartToy, "CareBot")
        )
        
        items.forEach { (screen, icon, label) ->
            val selected = activeScreen == screen || (screen == AppScreen.ProductSearch && activeScreen == AppScreen.ProductDetails)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) AccentMint else TextGray
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (selected) TextWhite else TextGray,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0x1E10B981)
                )
            )
        }
    }
}

// --- High Fidelity Landing Screen ---

@Composable
fun LandingScreen(
    viewModel: CareSureViewModel,
    onEnterSignIn: () -> Unit,
    onEnterSignUp: () -> Unit
) {
    val scrollState = rememberScrollState()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    var landingSearchText by remember { mutableStateOf("") }
    
    // Quick demonstration items
    val demoList = remember(landingSearchText, allProducts) {
        allProducts.filter {
            landingSearchText.isNotEmpty() && (
                it.name.contains(landingSearchText, ignoreCase = true) ||
                it.brand.contains(landingSearchText, ignoreCase = true)
            )
        }.take(3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0x1110B981), RoundedCornerShape(30.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "✨ NEW: AI 3.5 Personal suitabilities unlocked",
                    color = AccentMint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Verify Ingredients.\nSecure Your Wellness.",
                color = TextWhite,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif
            )
            
            Text(
                text = "CareSure AI+ computes safety ratios, decodes complex chemical compounds, detects allergens, and validates Suitability Indexes across 100+ Indian medical and cosmetic lines.",
                color = TextGray,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = onEnterSignUp,
                    colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).testTag("get_started_landing")
                ) {
                    Text("Register Free", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
                
                OutlinedButton(
                    onClick = onEnterSignIn,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, HospitalEmerald),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Account Login", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AccentMint)
                }
            }
        }
        
        // Fast Interactive Search Demo (No login needed)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(18.dp))
                .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "⚡ Real-time Smart Intelligence Sandbox",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Input any Indian brand (e.g. Pilgrim, Minimalist, Nivea) below to simulate safety screening instantly:",
                color = TextGray,
                fontSize = 13.sp
            )
            
            OutlinedTextField(
                value = landingSearchText,
                onValueChange = { landingSearchText = it },
                placeholder = { Text("Search Pilgrim, Cetaphil, Himalaya...", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            if (demoList.isNotEmpty()) {
                demoList.forEach { prod ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x0F000000), RoundedCornerShape(10.dp))
                            .clickable {
                                // Guest details notice
                                landingSearchText = ""
                                viewModel.selectProduct(prod)
                            }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(prod.brand + " | " + prod.category, color = TextGray, fontSize = 12.sp)
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0x1F10B981), RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        ) {
                            Text("${prod.safetyScore} AQ", color = AccentMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (landingSearchText.isNotEmpty()) {
                Text("No match found. Complete registration to analyze your specific product.", color = AccentMint, fontSize = 13.sp)
            }
        }
        
        // Visual core features section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Scientific Care Safeguards", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            
            FeatureRow(
                icon = Icons.Default.Science,
                title = "Ingredient Intelligence",
                description = "Decodes toxic comedogenic ratings, side effects, and pregnancy compatibility in simple medical summaries."
            )
            
            FeatureRow(
                icon = Icons.Default.HistoryEdu,
                title = "User Suitability Engine",
                description = "Calculates compatibility relative to skin triggers, chronic eczema conditions, and your medication allergies."
            )
            
            FeatureRow(
                icon = Icons.Default.MedicalServices,
                title = "Medicine cabinet control",
                description = "Logs active healthcare dosages. Triggers localized expiration alerts to protect your family."
            )
        }
        
        // Testimonials / Social Proof
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Trusted by Indian Families", color = AccentMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                "\"CareSure AI+ immediately flagged Cetaphil cleanser matches for my dry rosacea profile and warned me about Retinol during postpartum care! Simply indispensable.\"",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text("- Dr. Ananya Sharma, MD Dermatology (Mumbai)", color = TextGray, fontSize = 12.sp)
        }
        
        // FAQ Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Frequently Asked Questions", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            FAQItem(q = "Can I trust CareSure for critical medical warnings?", a = "Yes, our engine integrates official WHO classifications. However, always consult a licensed doctor for severe ailments.")
            FAQItem(q = "How does Personal Suitability work?", a = "By cross-matching active elements with your specific profile (e.g. pregnancy, dry skin type) to rate suitability dynamically out of 100.")
        }
        
        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "© 2026 CareSure AI+ Healthcare. Secure & fully offline compilation.",
                color = TextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FeatureRow(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0x1F10B981), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = AccentMint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(description, color = TextGray, fontSize = 13.sp)
        }
    }
}

@Composable
fun FAQItem(q: String, a: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(q, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = AccentMint
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(a, color = TextGray, fontSize = 13.sp)
        }
    }
}

// --- SignIn & SignUp Screens ---

@Composable
fun SignInScreen(
    viewModel: CareSureViewModel,
    onSignUpNow: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text("Welcome back", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Provide your credentials to access CareSure safe intelligence dashboards.", color = TextGray, fontSize = 14.sp)
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Patient Email Address", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Email, "Email", tint = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("username_input")
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Secured Password", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Lock, "Password", tint = TextGray) },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("password_input")
        )
        
        if (authError != null) {
            Text(authError!!, color = ClinicalRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        
        Button(
            onClick = { viewModel.loginUser(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_button")
        ) {
            Text("Unlock Identity Session", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
        
        TextButton(
            onClick = onSignUpNow,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("New to CareSure? Register a profile", color = AccentMint)
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel: CareSureViewModel,
    onSignInNow: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Create Medical Profile", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Enter your baseline measurements to calculate tailored suitability indices.", color = TextGray, fontSize = 14.sp)
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name", color = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email (Used to secure account)", color = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("Mobile Number", color = TextGray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Create strong password", color = TextGray) },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age", color = TextGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Gender", color = TextGray, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Male", "Female", "Other").forEach { g ->
                        val selected = gender == g
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (selected) HospitalEmerald else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .border(BorderStroke(1.dp, if (selected) AccentMint else Color(0x3FFFFFFF)), RoundedCornerShape(8.dp))
                                .clickable { gender = g }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(g, color = if (selected) TextWhite else TextGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        
        if (authError != null) {
            Text(authError!!, color = ClinicalRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        
        Button(
            onClick = {
                val ageVal = age.toIntOrNull() ?: 0
                viewModel.registerUser(name, email, mobile, password, ageVal, gender)
            },
            colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Acknowledge & Register", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
        
        TextButton(
            onClick = onSignInNow,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already registered? SignIn here", color = AccentMint)
        }
    }
}

// --- Active Patient Dashboard ---

@Composable
fun DashboardScreen(viewModel: CareSureViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val cabinet by viewModel.cabinetItems.collectAsStateWithLifecycle()
    val posts by viewModel.communityPosts.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(HospitalEmerald, Color(0xFF0F172A))),
                    RoundedCornerShape(18.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Welcome, ${currentUser?.fullName}!",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Physical Profile: Age: ${currentUser?.age} | Skin: ${currentUser?.skinType} | Hair: ${currentUser?.hairType}",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
            
            Box(
                modifier = Modifier
                    .background(Color(0x3FFFFFFF), RoundedCornerShape(8.dp))
                    .clickable { viewModel.setNavigation(AppScreen.EditProfile) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Edit Physical Suitabilities ⚙️", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        // Alerts Section
        val closeExpiryItem = cabinet.firstOrNull { it.quantity < 3 }
        if (closeExpiryItem != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, ClinicalRed)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, "Alert", tint = ClinicalRed, modifier = Modifier.size(24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Medicine Refill Alert!", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("'${closeExpiryItem.name}' is running low (Current Qty: ${closeExpiryItem.quantity}).", color = TextGray, fontSize = 12.sp)
                    }
                }
            }
        }
        
        // Quick Action Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardQuickAction(
                title = "Verify Product",
                icon = Icons.Default.Search,
                desc = "Search 100+ Indian assets",
                color = Color(0xFF0284C7),
                onClick = { viewModel.setNavigation(AppScreen.ProductSearch) },
                modifier = Modifier.weight(1f)
            )
            DashboardQuickAction(
                title = "Ask CareBot",
                icon = Icons.Default.SmartToy,
                desc = "AI Ingredient advice",
                color = HospitalEmerald,
                onClick = { viewModel.setNavigation(AppScreen.CareBot) },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Safety Guidelines Carousel/Item
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("💡 Dynamic Safety Tip of the Day", color = AccentMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                "Avoid layering Vitamin C and Retinol together in the same session. Retinol breaks down if mixed with highly acidic Vitamin C. Keep Vitamin C for daylight defense and Retinol for nighttime cellular repair.",
                color = TextWhite,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        
        // Community Snippet
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hot Discussions", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { viewModel.setNavigation(AppScreen.Community) }) {
                    Text("See forum", color = AccentMint)
                }
            }
            
            val trPost = posts.firstOrNull()
            if (trPost != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(trPost.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(trPost.content, color = TextGray, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("by ${trPost.authorName}", color = AccentMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("❤️ ${trPost.likesCount} endorsement", color = TextGray, fontSize = 11.sp)
                    }
                }
            } else {
                Text("No community posts logged yet. Be the first to open a question!", color = TextGray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DashboardQuickAction(
    title: String,
    icon: ImageVector,
    desc: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
        }
        
        Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(desc, color = TextGray, fontSize = 11.sp)
    }
}

// --- Edit Profile Screen ---

@Composable
fun EditProfileScreen(viewModel: CareSureViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    // Manage fields locally and save back
    var skinType by remember { mutableStateOf(currentUser?.skinType ?: "Normal") }
    var hairType by remember { mutableStateOf(currentUser?.hairType ?: "Normal") }
    var allergies by remember { mutableStateOf(currentUser?.allergies ?: "") }
    var conditions by remember { mutableStateOf(currentUser?.healthConditions ?: "") }
    var medicines by remember { mutableStateOf(currentUser?.currentMedicines ?: "") }
    var preferences by remember { mutableStateOf(currentUser?.preferences ?: "") }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Personal Intelligence Matrix", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Your profile answers are evaluated locally using safe algorithm heuristics mapping ingredient chemical listings.", color = TextGray, fontSize = 13.sp)
        
        // Skin Type Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Primary Skin Type", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Normal", "Oily", "Dry", "Combination", "Sensitive", "Acne-prone").forEach { t ->
                    val isS = skinType == t
                    Box(
                        modifier = Modifier
                            .background(if (isS) HospitalEmerald else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, if (isS) AccentMint else Color(0x3FFFFFFF)), RoundedCornerShape(8.dp))
                            .clickable { skinType = t }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(t, color = if (isS) TextWhite else TextGray, fontSize = 13.sp)
                    }
                }
            }
        }
        
        // Hair Type Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Hair Texture Type", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Normal", "Straight", "Wavy", "Curly", "Coily", "Dry Flaky / Dandruff").forEach { t ->
                    val isS = hairType == t
                    Box(
                        modifier = Modifier
                            .background(if (isS) HospitalEmerald else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, if (isS) AccentMint else Color(0x3FFFFFFF)), RoundedCornerShape(8.dp))
                            .clickable { hairType = t }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(t, color = if (isS) TextWhite else TextGray, fontSize = 13.sp)
                    }
                }
            }
        }
        
        // Allergies
        OutlinedTextField(
            value = allergies,
            onValueChange = { allergies = it },
            label = { Text("Declared Allergies (e.g. Parabens, Fragrance)", color = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Health Conditions
        OutlinedTextField(
            value = conditions,
            onValueChange = { conditions = it },
            label = { Text("Health Conditions (e.g. Pregnancy, Rosacea, Eczema)", color = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Medicines
        OutlinedTextField(
            value = medicines,
            onValueChange = { medicines = it },
            label = { Text("Associated Medicines (e.g. Retinol, Benzoyl Peroxide)", color = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Preferences
        OutlinedTextField(
            value = preferences,
            onValueChange = { preferences = it },
            label = { Text("Preferences (e.g. Vegan, Cruelty-free)", color = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = HospitalEmerald,
                unfocusedBorderColor = Color(0x3FFFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = {
                viewModel.updateUserProfile(skinType, hairType, allergies, conditions, medicines, preferences)
                viewModel.setNavigation(AppScreen.Dashboard)
            },
            colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Update Local Matrix Schema", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
    }
}

// --- Product Search Screen ---

@Composable
fun ProductSearchScreen(viewModel: CareSureViewModel) {
    var searchModeTab by remember { mutableStateOf(0) } // 0 = Catalog, 1 = AI Scanner
    
    // Catalog states
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchedProducts by viewModel.searchedProducts.collectAsStateWithLifecycle()
    
    // Scanner states
    val isScannerLoading by viewModel.isScannerLoading.collectAsStateWithLifecycle()
    val scannedResult by viewModel.scannedResult.collectAsStateWithLifecycle()
    val scannerError by viewModel.scannerError.collectAsStateWithLifecycle()
    
    var showBarcodeSimulator by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                if (bytes != null) {
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    viewModel.analyzeLabelImage(base64)
                }
            } catch (e: Exception) {
                android.util.Log.e("Scanner_Error", "Failed to process selected file", e)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            try {
                val outputStream = java.io.ByteArrayOutputStream()
                it.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, outputStream)
                val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                viewModel.analyzeLabelImage(base64)
            } catch (e: Exception) {
                android.util.Log.e("Scanner_Error", "Failed to process captured photo", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High-end Segmented Control Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (searchModeTab == 0) Color(0xFF1E293B) else Color.Transparent)
                    .clickable { searchModeTab = 0 }
                    .padding(vertical = 10.dp)
                    .testTag("catalog_search_tab"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Catalog Search", 
                    color = if (searchModeTab == 0) AccentMint else TextGray, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 13.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (searchModeTab == 1) Color(0xFF1E293B) else Color.Transparent)
                    .clickable { searchModeTab = 1 }
                    .padding(vertical = 10.dp)
                    .testTag("ai_label_scanner_tab"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "AI Label Scanner 📸", 
                    color = if (searchModeTab == 1) AccentMint else TextGray, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 13.sp
                )
            }
        }

        if (searchModeTab == 0) {
            // DATABASE CATALOG TAB
            Text("Safeguard Database Search", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            // Search TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search 100+ Indian brand catalogs...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("product_search_input")
            )
            
            // Categories list
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("All", "Skincare", "Haircare", "Medicine")
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) HospitalEmerald else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, if (isSelected) AccentMint else Color(0x3FFFFFFF)), RoundedCornerShape(8.dp))
                            .clickable { viewModel.updateSearchCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(cat, color = if (isSelected) TextWhite else TextGray, fontSize = 13.sp)
                    }
                }
            }
            
            // Results count
            Text("${searchedProducts.size} diagnostic records found", color = TextGray, fontSize = 12.sp)
            
            // Results
            if (searchedProducts.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(searchedProducts) { product ->
                        ProductItemCard(
                            product = product,
                            onSelect = { viewModel.selectProduct(product) }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Info, "Empty", tint = TextGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No scanned items match your search. Verify naming syntax.", color = TextWhite, fontSize = 14.sp)
                }
            }
        } else {
            // MULTIMODAL AI SCANNER TAB
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("AI Multimodal Ingredient Analyser", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Capture a photo of clinical ingredients or container label to screen, categorize, and cross-reference with medical safety indices.",
                    color = TextGray,
                    fontSize = 13.sp
                )

                if (scannedResult == null && !isScannerLoading) {
                    // MULTIMODAL CONSOLE PANEL
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0x1FFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Sensor capture modes:", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Live Camera button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                        .clickable { cameraLauncher.launch(null) }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.PhotoCamera, "Take Photo", tint = AccentMint, modifier = Modifier.size(24.dp))
                                        Text("Take Photo 📸", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                
                                // Choose from library button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                        .clickable { galleryLauncher.launch("image/*") }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Info, "Library", tint = AccentMint, modifier = Modifier.size(24.dp))
                                        Text("Upload Image 🖼️", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                
                                // Barcode Simulator button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                        .clickable { showBarcodeSimulator = true }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.DocumentScanner, "Barcode Scanner", tint = AccentMint, modifier = Modifier.size(24.dp))
                                        Text("Scan Barcode 🏷️", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    // SIMULATED PRESET TRIGGERS (Requested for seamless offline/preset testing!)
                    Text("Simulate Quick Scan Presets", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    
                    val presets = listOf(
                        Triple("Anti-Acne Spot Cream", "PRESET_ACNE_SPOT", "Active ingredient Spot Cream with 2% Salicylic Acid & Tea Tree Oil."),
                        Triple("Therapy Dandruff Wash", "PRESET_DANDRUFF", "Anti-dandruff shampoo container with Ketoconazole 2%."),
                        Triple("Ultra Cica Calm-Gel", "PRESET_CICA", "Barrier reparative lotion with Centella Asiatica & Niacinamide.")
                    )

                    presets.forEach { (name, keyword, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, Color(0x0FFFFFFF)), RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.analyzeLabelImage(keyword)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0x3310B981), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DocumentScanner, "Scan", tint = AccentMint, modifier = Modifier.size(18.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = TextGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Icon(Icons.Default.ArrowForwardIos, "Go", tint = TextGray, modifier = Modifier.size(12.dp))
                        }
                    }
                }

                // BARCODE SIMULATOR MODAL
                if (showBarcodeSimulator) {
                    AlertDialog(
                        onDismissRequest = { showBarcodeSimulator = false },
                        confirmButton = {
                            Button(
                                onClick = { showBarcodeSimulator = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                            ) {
                                Text("Cancel", color = TextWhite)
                            }
                        },
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.DocumentScanner, "Scanner", tint = AccentMint)
                                Text("Laser Barcode Sensor", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text("Hold standard clinical barcode sticker within the focus lens frame to decode.", color = TextGray, fontSize = 12.sp)
                                
                                // Simulated focus box with static red matching scanning line
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                                        .border(BorderStroke(1.dp, HospitalEmerald.copy(alpha = 0.5f)), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            repeat(18) { index ->
                                                Box(
                                                    modifier = Modifier
                                                        .width(if (index % 3 == 0) 3.dp else if (index % 5 == 0) 5.dp else 1.dp)
                                                        .height(45.dp)
                                                        .background(TextGray)
                                                )
                                            }
                                        }
                                        Text("UPC-A: 8 901030 704900", color = TextGray, fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                                    }
                                    
                                    // Animated red scanner indicator bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(Brush.horizontalGradient(listOf(Color.Transparent, ClinicalRed, Color.Transparent)))
                                    )
                                }
                                
                                Text("Decoded Med-Brand Catalogs (Tap to simulate read):", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                
                                val customBarcodes = listOf(
                                    Triple("Pears Pure & Gentle Soap Mini", "8901030704900", "BARCODE_PEARS"),
                                    Triple("Himalaya Neem Acne Wash", "8901396388417", "BARCODE_HIMALAYA"),
                                    Triple("Vicco Legacy Turmeric Cream", "8901207040116", "BARCODE_VICCO")
                                )
                                
                                customBarcodes.forEach { (label, code, actionKey) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                            .clickable {
                                                showBarcodeSimulator = false
                                                viewModel.analyzeLabelImage(actionKey)
                                            }
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(Color(0x1C10B981), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.QrCode, "Code", tint = AccentMint, modifier = Modifier.size(16.dp))
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(label, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("UPC: $code", color = TextGray, fontSize = 10.sp)
                                        }
                                        Icon(Icons.Default.ArrowForwardIos, "Select", tint = TextGray, modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        },
                        containerColor = Color(0xFF111827),
                        titleContentColor = TextWhite,
                        textContentColor = TextGray,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // SCANNING METERS / LOADING
                if (isScannerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = AccentMint)
                            Text("CareBot AI is decoding chemical formula details...", color = TextWhite, fontSize = 14.sp)
                            Text("Extracting active agents and calculating toxic thresholds", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }

                // ERROR SCREEN
                scannerError?.let { err ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1FCE1212), RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, ClinicalRed), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(err, color = ClinicalRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Button(
                                onClick = { viewModel.clearScanner() },
                                colors = ButtonDefaults.buttonColors(containerColor = ClinicalRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Retry Scanning Capture", color = TextWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // PARSED RESULT DISPLAY (Highly styled!)
                scannedResult?.let { result ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(18.dp))
                            .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(18.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header with scan verification badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(result.productName, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Brand: ${result.brand}", color = TextGray, fontSize = 13.sp)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(Color(0x2210B981), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Scan Verified 🛡️", color = AccentMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(color = Color(0x0FFFFFFF))

                        // Score meters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Clinical Safety AQ", color = TextGray, fontSize = 11.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("${result.safetyScore}", color = AccentMint, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("/ 100", color = TextGray, fontSize = 13.sp)
                                }
                            }

                            val badgeColor = when (result.safetyStatus.lowercase()) {
                                "safe" -> HospitalEmerald
                                "caution" -> Color(0xFFD97706)
                                else -> ClinicalRed
                            }

                            Box(
                                modifier = Modifier
                                    .background(badgeColor, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    result.safetyStatus.uppercase(), 
                                    color = TextWhite, 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Ingredients list
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Dermal Active Ingredients (Tap for Intell-Details 💡)", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                result.ingredients.forEach { ing ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                            .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(8.dp))
                                            .clickable { viewModel.selectIngredientByName(ing) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(ing, color = AccentMint, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Explanation
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Clinical Safety Analysis & Risks", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(result.explanation, color = TextGray, fontSize = 13.sp, lineHeight = 18.sp)
                        }

                        // Action Buttons: Add directly to cabinet or clear scanner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.addCabinetItem(
                                        name = result.productName,
                                        dosage = "1 daily application - Scanned",
                                        expiryDate = "2027-12-31",
                                        quantity = 1
                                    )
                                    // Navigate to Cabinet to see it!
                                    viewModel.setNavigation(AppScreen.MedicineCabinet)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Text("Add to Cabinet 💊", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearScanner() },
                                border = BorderStroke(1.dp, Color(0x3FFFFFFF)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Scan New", color = TextWhite, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: ProductEntity,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dynamic simulated image box with brand initials
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E3F5F),
                            Color(0xFF10192A)
                        )
                    ),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                product.brand.take(1),
                color = AccentMint,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Brand: " + product.brand + " | " + product.category, color = TextGray, fontSize = 12.sp)
            
            // Suitability indicator
            Spacer(modifier = Modifier.height(4.dp))
            Text(product.suitabilityTags, color = AccentMint, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        
        // Safety score block
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .background(Color(0x1F10B981), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${product.safetyScore}% Safe",
                    color = AccentMint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- Product Details Screen ---

@Composable
fun ProductDetailsScreen(viewModel: CareSureViewModel) {
    val product by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No product selected", color = TextWhite)
        }
        return
    }
    
    val p = product!!
    // Evaluate personalized scoring parameters relative to this logged-in account
    val result = viewModel.analyzeProductSafetyAndSuitability(p)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main detail card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(p.brand.take(1), color = AccentMint, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                }
                Column {
                    Text(p.name, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Brand: " + p.brand + " | Category: " + p.category, color = TextGray, fontSize = 13.sp)
                }
            }
            
            Divider(color = Color(0x1FCDCDCD))
            
            // Dynamic Scoring Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Safety Score", color = TextGray, fontSize = 11.sp)
                    Text("${result.safetyScore}%", color = if (result.safetyScore < 40) ClinicalRed else AccentMint, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your Suitability", color = TextGray, fontSize = 11.sp)
                    Text("${result.suitabilityScore}%", color = if (result.suitabilityScore < 40) ClinicalRed else AccentMint, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            
            // Safety badge severity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (result.suitabilityScore < 45) Color(0xFF450A0A) else Color(0x1E10B981),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Risk Matrix: " + result.severityLevel,
                    color = if (result.suitabilityScore < 45) ClinicalRed else AccentMint,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            
            // Warnings summary description
            Text("Personal Impact Analysis:", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(result.analysisSummary, color = TextGray, fontSize = 13.sp)
        }
        
        // Active Ingredients Clickable Catalog
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🔬 Decoded Chemical Ingredients", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Select any ingredient node below to pull scientific descriptions and comedogenic ratings offline:", color = TextGray, fontSize = 12.sp)
            
            // Wrap in flow row block simulation
            FlowRowLayout(
                items = p.ingredients.split(",")
            ) { ingredientName ->
                Box(
                    modifier = Modifier
                        .background(Color(0x1F10B981), RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, HospitalEmerald.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectIngredientByName(ingredientName) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(ingredientName.trim(), color = AccentMint, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        
        // Benefits Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("✨ Expected Benefits", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(p.benefits, color = TextGray, fontSize = 13.sp)
        }
        
        // Warnings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x1FEF4444), RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, ClinicalRed.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("⚠️ Safety Alert / Medical Disclaimer", color = ClinicalRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(p.warnings, color = TextGray, fontSize = 13.sp)
        }
    }
}

// FlowRow layout simulator
@Composable
fun FlowRowLayout(
    items: List<String>,
    modifier: Modifier = Modifier,
    content: @Composable (String) -> Unit
) {
    // Basic wrapper
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val chunked = items.chunked(3)
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f, fill = false)) {
                        content(item)
                    }
                }
            }
        }
    }
}

// --- Ingredient Intelligence Modal Dialog ---

@Composable
fun IngredientIntelligenceDialog(
    ingredient: IngredientEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald)
            ) {
                Text("Dismiss Assessment", color = TextWhite)
            }
        },
        title = {
            Column {
                Text("Intelligence: " + ingredient.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .background(Color(0x2210B981), RoundedCornerShape(4.dp))
                        .padding(4.dp)
                ) {
                    Text(ingredient.function, color = AccentMint, fontSize = 12.sp)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(ingredient.scientificDescription, color = TextGray, fontSize = 13.sp)
                
                Divider(color = Color(0x3FFFFFFF))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Risk Level:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(ingredient.riskLevel, color = if (ingredient.riskLevel == "Safe") AccentMint else ClinicalOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Comedogenic Rating:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${ingredient.comedogenicRating} / 5", color = TextWhite, fontSize = 13.sp)
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pregnancy Safety:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(ingredient.pregnancySafetyStatus, color = if (ingredient.pregnancySafetyStatus == "Safe") AccentMint else ClinicalRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text("Proven Benefits:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(ingredient.benefits, color = TextGray, fontSize = 13.sp)
                
                Text("Possible Side Effects:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(ingredient.sideEffects, color = TextGray, fontSize = 13.sp)
            }
        },
        containerColor = Color(0xFF1E293B)
    )
}

// --- AI CareBot Screen ---

@Composable
fun CareBotScreen(viewModel: CareSureViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val isBotLoading by viewModel.isChatbotLoading.collectAsStateWithLifecycle()
    var userText by remember { mutableStateOf("") }
    val lazyListState = rememberScrollState()
    
    androidx.compose.runtime.LaunchedEffect(chatHistory.size, isBotLoading) {
        if (chatHistory.isNotEmpty() || isBotLoading) {
            lazyListState.animateScrollTo(lazyListState.maxValue)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Chat screen top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("CareBot Advisor 🤖", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Real-time Gemini personal health companion.", color = TextGray, fontSize = 12.sp)
            }
            TextButton(onClick = { viewModel.clearChat() }) {
                Text("Clear History", color = ClinicalRed)
            }
        }
        
        // Chat listing
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(lazyListState)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (chatHistory.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.SmartToy, "CareBot", tint = AccentMint, modifier = Modifier.size(48.dp))
                            Text("No chats logged yet. Ask questions like:", color = TextWhite, fontSize = 14.sp)
                            
                            // Templates suggestion list
                            listOf(
                                "Retinol & pregnancy warnings",
                                "Soothe Niacinamide breakouts?",
                                "Combating oily dandruff scale roots"
                            ).forEach { temp ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                        .clickable { userText = temp }
                                        .padding(8.dp)
                                ) {
                                    Text(temp, color = AccentMint, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    chatHistory.forEach { chat ->
                        val isUser = chat.isUser
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isUser) HospitalEmerald else Color(0xFF1E293B),
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (isUser) 12.dp else 0.dp,
                                            bottomEnd = if (isUser) 0.dp else 12.dp
                                        )
                                    )
                                    .padding(12.dp)
                                    .widthIn(max = 260.dp)
                            ) {
                                Text(
                                    chat.message,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                    if (isBotLoading) {
                        Text("typing...", color = TextGray, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                    }
                }
            }
        }
        
        // Input controller box
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userText,
                onValueChange = { userText = it },
                placeholder = { Text("Consult ingredients, dosages...", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).testTag("carebot_input")
            )
            
            IconButton(
                onClick = {
                    if (userText.isNotEmpty()) {
                        viewModel.sendChatMessage(userText)
                        userText = ""
                    }
                },
                enabled = userText.isNotEmpty() && !isBotLoading,
                modifier = Modifier
                    .size(48.dp)
                    .background(HospitalEmerald, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Send, "Send Message", tint = TextWhite)
            }
        }
    }
}

// --- Skin Journey Module ---

@Composable
fun SkinJourneyScreen(viewModel: CareSureViewModel) {
    val entries by viewModel.journeyEntries.collectAsStateWithLifecycle()
    var description by remember { mutableStateOf("") }
    var routine by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5f) }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Personal Skin Journey Logs", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Upload daily visual progress, details, routines, and track weekly/monthly improvement indices.", color = TextGray, fontSize = 12.sp)
        
        // Entry Creater Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("➕ Log Daily Progress Card", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("How is your skin feeling today?", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = routine,
                onValueChange = { routine = it },
                label = { Text("What routines did you execute? (e.g. BHA Cleansing)", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Slider
            Column {
                Text("Subjective Improvement Rating: ${rating.toInt()} / 10", color = TextWhite, fontSize = 12.sp)
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 1f..10f,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentMint,
                        activeTrackColor = HospitalEmerald
                    )
                )
            }
            
            Button(
                onClick = {
                    viewModel.addJourneyEntry(description, routine, rating.toInt())
                    description = ""
                    routine = ""
                    rating = 5f
                },
                colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Entry Session", color = TextWhite)
            }
        }
        
        // List entries
        Text("Your Logged Timeline", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        if (entries.isNotEmpty()) {
            entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("📅 Date: " + entry.date, color = AccentMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(entry.description, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Routine: " + entry.routine, color = TextGray, fontSize = 12.sp)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .background(Color(0x3F10B981), CircleShape)
                                .size(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${entry.improvementRating}", color = AccentMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        IconButton(onClick = { viewModel.deleteJourneyEntry(entry.id) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = ClinicalRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        } else {
            Text("No diary files logged yet. Write above to verify improvement trends.", color = TextGray, fontSize = 13.sp)
        }
    }
}

// --- Medicine Cabinet Screen ---

@Composable
fun MedicineCabinetScreen(viewModel: CareSureViewModel) {
    val cabinetItems by viewModel.cabinetItems.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Medicine Cabinet & Refills", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Save active pharmacy items, current counts, and safety expiration periods locally.", color = TextGray, fontSize = 12.sp)
        
        // Add form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("➕ Add Medicine Element", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name (e.g. Crocin)", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("medicine_name_input")
            )
            
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage Schedule (e.g. 1 Tablet after dinner)", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("dosage_input")
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry (YYYY-MM-DD)", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = HospitalEmerald,
                        unfocusedBorderColor = Color(0x3FFFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.3f).testTag("expiry_input")
                )
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Qty", color = TextGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = HospitalEmerald,
                        unfocusedBorderColor = Color(0x3FFFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(0.7f).testTag("quantity_input")
                )
            }
            
            Button(
                onClick = {
                    val qNum = quantity.toIntOrNull() ?: 1
                    viewModel.addCabinetItem(name, dosage, expiryDate, qNum)
                    name = ""
                    dosage = ""
                    expiryDate = ""
                    quantity = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("add_medicine_button")
            ) {
                Text("Save to Cabinet Inventory", color = TextWhite)
            }
        }
        
        // List entries
        Text("Your Cabinet Inventory", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        if (cabinetItems.isNotEmpty()) {
            cabinetItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Dosage: " + item.dosage, color = TextGray, fontSize = 12.sp)
                        Text("Expires: " + item.expiryDate, color = ClinicalOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Available Qty: " + item.quantity, color = AccentMint, fontSize = 12.sp)
                    }
                    
                    IconButton(onClick = { viewModel.deleteCabinetItem(item.id) }) {
                        Icon(Icons.Default.Delete, "Delete", tint = ClinicalRed)
                    }
                }
            }
        } else {
            Text("No active elements found inside your medicine shelf. Create above.", color = TextGray, fontSize = 13.sp)
        }
    }
}

// --- Community Board Module ---

@Composable
fun CommunityScreen(viewModel: CareSureViewModel) {
    val posts by viewModel.communityPosts.collectAsStateWithLifecycle()
    var categorySelection by remember { mutableStateOf("All") }
    
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Skincare") }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("CareSure Peer Support Forum", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Discuss brand formulas and routine improvements anonymously with other patient profiles.", color = TextGray, fontSize = 12.sp)
        
        // Add post form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("📝 Share Experience / Open Question", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Topic Title (e.g. Niacinamide purging help)", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Describe your chemical trigger or advice details...", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Cat selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Skincare", "Haircare", "Medicine").forEach { c ->
                    val isSel = cat == c
                    Box(
                        modifier = Modifier
                            .background(if (isSel) HospitalEmerald else Color(0x11000000), RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, if (isSel) AccentMint else Color(0x3FFFFFFF)), RoundedCornerShape(8.dp))
                            .clickable { cat = c }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(c, color = if (isSel) TextWhite else TextGray, fontSize = 12.sp)
                    }
                }
            }
            
            Button(
                onClick = {
                    viewModel.createCommunityPost(title, content, cat)
                    title = ""
                    content = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Broadcast Anonymously", color = TextWhite)
            }
        }
        
        // Topic Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Skincare", "Haircare", "Medicine").forEach { option ->
                val active = categorySelection == option
                Box(
                    modifier = Modifier
                        .background(if (active) HospitalEmerald else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .clickable { categorySelection = option }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(option, color = if (active) TextWhite else TextGray, fontSize = 12.sp)
                }
            }
        }
        
        // Render posts
        val filteredList = if (categorySelection == "All") posts else posts.filter { it.category == categorySelection }
        if (filteredList.isNotEmpty()) {
            filteredList.forEach { post ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Category: " + post.category, color = AccentMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Author: " + post.authorName, color = TextGray, fontSize = 11.sp)
                    }
                    
                    Text(post.title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(post.content, color = TextGray, fontSize = 13.sp)
                    
                    Divider(color = Color(0x11FFFFFF))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleLikePost(post) }) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ThumbUp, "Like", tint = AccentMint, modifier = Modifier.size(16.dp))
                                Text(" ${post.likesCount} endorsement", color = TextWhite, fontSize = 12.sp)
                            }
                        }
                        
                        IconButton(onClick = { viewModel.deletePost(post.id) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = ClinicalRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        } else {
            Text("No current topics published for this filter category.", color = TextGray, fontSize = 13.sp)
        }
    }
}

// --- Admin Dashboard Screen ---

@Composable
fun AdminDashboardScreen(viewModel: CareSureViewModel) {
    val products by viewModel.searchedProducts.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    // Product insert matrix parameters
    var pName by remember { mutableStateOf("") }
    var pBrand by remember { mutableStateOf("") }
    var pCategory by remember { mutableStateOf("Skincare") }
    var pIngredients by remember { mutableStateOf("") }
    var pBenefits by remember { mutableStateOf("") }
    var pWarnings by remember { mutableStateOf("") }
    var pScore by remember { mutableStateOf("") }
    var pTags by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Admin Security & Registry Control", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        
        // System Health Statistics
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("📈 System Metrics & Storage Ratios", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f).background(Color(0xFF0F172A), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    Text("Total Assets", color = TextGray, fontSize = 10.sp)
                    Text("${products.size}", color = AccentMint, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(modifier = Modifier.weight(1f).background(Color(0xFF0F172A), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    Text("Active DB Index", color = TextGray, fontSize = 10.sp)
                    Text("Room-SQLite", color = AccentMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f).background(Color(0xFF0F172A), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    Text("API Service", color = TextGray, fontSize = 10.sp)
                    Text("Gemini 3.5", color = AccentMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            // Custom Bar Chart demonstrating Product counts per category
            Spacer(modifier = Modifier.height(8.dp))
            Text("Asset Share Distribution (Visual Canvas Chart)", color = TextWhite, fontSize = 12.sp)
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // Skincare, Haircare, Medicine counts
                val skincareCount = products.count { it.category.equals("skincare", ignoreCase = true) }
                val haircareCount = products.count { it.category.equals("haircare", ignoreCase = true) }
                val medicineCount = products.count { it.category.equals("medicine", ignoreCase = true) }
                
                val maxVal = maxOf(skincareCount, haircareCount, medicineCount, 1)
                
                val w = size.width / 5
                val h = size.height
                
                // Draw Skincare
                drawRect(
                    color = AccentMint,
                    topLeft = Offset(w, h - (skincareCount.toFloat() / maxVal * h)),
                    size = androidx.compose.ui.geometry.Size(w - 10.dp.toPx(), skincareCount.toFloat() / maxVal * h)
                )
                // Draw Haircare
                drawRect(
                    color = HospitalJade,
                    topLeft = Offset(w * 2, h - (haircareCount.toFloat() / maxVal * h)),
                    size = androidx.compose.ui.geometry.Size(w - 10.dp.toPx(), haircareCount.toFloat() / maxVal * h)
                )
                // Draw Medicine
                drawRect(
                    color = ClinicalOrange,
                    topLeft = Offset(w * 3, h - (medicineCount.toFloat() / maxVal * h)),
                    size = androidx.compose.ui.geometry.Size(w - 10.dp.toPx(), medicineCount.toFloat() / maxVal * h)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Text("🟢 Skincare", color = AccentMint, fontSize = 10.sp)
                Text("🔵 Haircare", color = HospitalJade, fontSize = 10.sp)
                Text("🟠 Medicine", color = ClinicalOrange, fontSize = 10.sp)
            }
        }
        
        // Add new Indian Product Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("📦 Add Custom Product Registry Item", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            OutlinedTextField(
                value = pName,
                onValueChange = { pName = it },
                label = { Text("Product Name", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = pBrand,
                onValueChange = { pBrand = it },
                label = { Text("Brand (Dot & Key, Nivea, Plum...)", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = pIngredients,
                onValueChange = { pIngredients = it },
                label = { Text("Comma Separated Ingredients", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = pBenefits,
                onValueChange = { pBenefits = it },
                label = { Text("Benefits summary", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = HospitalEmerald,
                    unfocusedBorderColor = Color(0x3FFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = pScore,
                    onValueChange = { pScore = it },
                    label = { Text("Score (1-100)", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = HospitalEmerald,
                        unfocusedBorderColor = Color(0x3FFFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = pTags,
                    onValueChange = { pTags = it },
                    label = { Text("Tags (Dry Skin...)", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = HospitalEmerald,
                        unfocusedBorderColor = Color(0x3FFFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Skincare", "Haircare", "Medicine").forEach { optionCat ->
                    val sel = pCategory == optionCat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (sel) HospitalEmerald else Color(0x11000000), RoundedCornerShape(8.dp))
                            .clickable { pCategory = optionCat }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(optionCat, color = if (sel) TextWhite else TextGray, fontSize = 12.sp)
                    }
                }
            }
            
            Button(
                onClick = {
                    val sc = pScore.toIntOrNull() ?: 80
                    viewModel.adminAddProduct(pName, pBrand, pCategory, pIngredients, pBenefits, pWarnings, sc, pTags)
                    pName = ""
                    pBrand = ""
                    pIngredients = ""
                    pBenefits = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = HospitalEmerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Broadcast Admin Registry", color = TextWhite)
            }
        }
    }
}
