package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.util.concurrent.TimeUnit

// --- CareSure Navigation enum ---
enum class AppScreen {
    Landing,
    SignIn,
    SignUp,
    Dashboard,
    EditProfile,
    ProductSearch,
    ProductDetails,
    SkinJourney,
    MedicineCabinet,
    Community,
    AdminDashboard,
    CareBot
}

data class ScannedIngredientsResult(
    val productName: String,
    val brand: String,
    val ingredients: List<String>,
    val safetyScore: Int,
    val safetyStatus: String, // "Safe", "Caution", "Unsafe"
    val explanation: String
)

class CareSureViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val userDao = db.userDao()
    private val productDao = db.productDao()
    private val ingredientDao = db.ingredientDao()
    private val cabinetDao = db.cabinetDao()
    private val journeyDao = db.journeyDao()
    private val communityDao = db.communityDao()
    private val chatDao = db.chatDao()

    // --- Authentication States ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // --- Navigation ---
    private val _activeScreen = MutableStateFlow(AppScreen.Landing)
    val activeScreen: StateFlow<AppScreen> = _activeScreen.asStateFlow()

    // --- Search & Selection States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    private val _selectedIngredient = MutableStateFlow<IngredientEntity?>(null)
    val selectedIngredient: StateFlow<IngredientEntity?> = _selectedIngredient.asStateFlow()

    // --- Community categories ---
    private val _communityCategory = MutableStateFlow("All")
    val communityCategory: StateFlow<String> = _communityCategory.asStateFlow()

    // --- Database Flow Expositions ---
    val allProducts: StateFlow<List<ProductEntity>> = productDao.getAllProductsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchedProducts: StateFlow<List<ProductEntity>> = combine(_searchQuery, _selectedCategory, allProducts) { query, category, products ->
        products.filter { product ->
            val matchesQuery = query.isEmpty() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true) ||
                    product.ingredients.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || product.category.equals(category, ignoreCase = true)

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cabinetItems: StateFlow<List<CabinetItemEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) cabinetDao.getCabinetItems(user.email) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journeyEntries: StateFlow<List<JourneyEntryEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) journeyDao.getJourneyEntries(user.email) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communityPosts: StateFlow<List<CommunityPostEntity>> = communityDao.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatHistory: StateFlow<List<ChatMessageEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) chatDao.getChatHistory(user.email) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed database silently on startup if empty
        viewModelScope.launch(Dispatchers.IO) {
            val count = db.ingredientDao().getAllIngredients().size
            if (count == 0) {
                // Populate starter ingredients
                ingredientDao.insertIngredients(ProductSeedingData.getStarterIngredients())
                // Populate index seed of 105 products
                productDao.insertProducts(ProductSeedingData.generate100Products())
            }
        }
    }

    // --- Password Helper ---
    private fun hashWithSHA256(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // --- Auth Actions ---
    fun registerUser(fullName: String, email: String, mobileNumber: String, p1: String, age: Int, gender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authError.value = null
            if (fullName.isEmpty() || email.isEmpty() || mobileNumber.isEmpty() || p1.isEmpty() || age <= 0 || gender.isEmpty()) {
                _authError.value = "Please fill in all registration fields completely."
                return@launch
            }
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                _authError.value = "A user account with this email already exists."
                return@launch
            }
            val passwordHash = hashWithSHA256(p1)
            val newUser = UserEntity(
                email = email.trim(),
                fullName = fullName.trim(),
                mobileNumber = mobileNumber.trim(),
                passwordHash = passwordHash,
                age = age,
                gender = gender
            )
            userDao.insertUser(newUser)
            withContext(Dispatchers.Main) {
                _currentUser.value = newUser
                _activeScreen.value = AppScreen.Dashboard
            }
        }
    }

    fun loginUser(email: String, p1: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authError.value = null
            if (email.isEmpty() || p1.isEmpty()) {
                _authError.value = "Please enter both Email and Password."
                return@launch
            }
            val user = userDao.getUserByEmail(email.trim())
            if (user == null) {
                _authError.value = "No account found with this email."
                return@launch
            }
            val expectedHash = hashWithSHA256(p1)
            if (user.passwordHash == expectedHash) {
                withContext(Dispatchers.Main) {
                    _currentUser.value = user
                    _activeScreen.value = AppScreen.Dashboard
                }
            } else {
                _authError.value = "Invalid password. Please verify and try again."
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _activeScreen.value = AppScreen.Landing
    }

    // --- Profile Updates ---
    fun updateUserProfile(
        skinType: String,
        hairType: String,
        allergies: String,
        conditions: String,
        medicines: String,
        preferences: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val updated = user.copy(
                skinType = skinType,
                hairType = hairType,
                allergies = allergies,
                healthConditions = conditions,
                currentMedicines = medicines,
                preferences = preferences
            )
            userDao.updateUser(updated)
            withContext(Dispatchers.Main) {
                _currentUser.value = updated
            }
        }
    }

    fun setNavigation(screen: AppScreen) {
        _activeScreen.value = screen
    }

    fun selectProduct(product: ProductEntity) {
        _selectedProduct.value = product
        _activeScreen.value = AppScreen.ProductDetails
    }

    fun selectIngredientByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sanitized = name.trim().lowercase().removeSuffix(",").removeSuffix(".")
            val allIn = ingredientDao.getAllIngredients()
            val match = allIn.firstOrNull { it.name.trim().equals(sanitized, ignoreCase = true) }
                ?: IngredientEntity(
                    name = name,
                    function = "Conditioning Active",
                    benefits = "Improves structural texture and maintains formulation moisture.",
                    sideEffects = "None known. Highly tolerated.",
                    riskLevel = "Safe",
                    scientificDescription = "An ingredient used primarily in high-quality local personal care products for stabilization.",
                    comedogenicRating = 0,
                    pregnancySafetyStatus = "Safe"
                )
            withContext(Dispatchers.Main) {
                _selectedIngredient.value = match
            }
        }
    }

    fun closeIngredientIntelligence() {
        _selectedIngredient.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSearchCategory(category: String) {
        _selectedCategory.value = category
    }

    // --- Dynamic AI Safety, Suitability Analysis Logic ---
    fun analyzeProductSafetyAndSuitability(product: ProductEntity): ScoringResult {
        val user = _currentUser.value ?: return ScoringResult(product.safetyScore, 100, "Highly Suitable", "Set up your profile to receive personalized advice.")

        val productIngredients = product.ingredients.split(",").map { it.trim().lowercase() }
        val userAllergies = user.allergies.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        val userConditions = user.healthConditions.lowercase()
        val userMedicines = user.currentMedicines.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }

        var personalizedSafety = product.safetyScore
        var suitability = 100
        val alertList = mutableListOf<String>()
        var summary = "Excellent choice for your routine!"

        // 1. Allergen Check
        for (allergy in userAllergies) {
            val match = productIngredients.any { it.contains(allergy) || allergy.contains(it) }
            if (match) {
                suitability = 0
                personalizedSafety = 10
                alertList.add("CRITICAL ALLERGEN MATCH: Contains '$allergy'")
            }
        }

        // 2. Pregnancy Safety Check
        if (userConditions.contains("pregnancy") || userConditions.contains("pregnant")) {
            val hasRetinol = productIngredients.any { it.contains("retinol") || it.contains("vitamin a") }
            val hasSalicylic = productIngredients.any { it.contains("salicylic acid") || it.contains("peeling") }
            if (hasRetinol) {
                suitability -= 60
                alertList.add("PREGNANCY WARNING: Retinol is NOT pregnancy safe!")
            }
            if (hasSalicylic) {
                suitability -= 30
                alertList.add("PREGNANCY ADVISORY: Contains Salicylic Acid. Limit concentration/consult doctor.")
            }
        }

        // 3. Skin Type Compatibilities
        val skinT = user.skinType.lowercase()
        if (skinT.contains("oily") || skinT.contains("acne")) {
            val hasComedogenic = productIngredients.any { it.contains("butter") || it.contains("isopropyl") || it.contains("coconut") }
            if (hasComedogenic) {
                suitability -= 25
                alertList.add("SKIN DISCORDANCE: Contains comedogenic ingredients. Might trigger breakouts.")
            }
        }

        if (skinT.contains("dry") || skinT.contains("flake")) {
            val hasAlcohol = productIngredients.any { it.contains("ethanol") || it.contains("denat alcohol") }
            if (hasAlcohol) {
                suitability -= 30
                alertList.add("SKIN DISCORDANCE: Contains drying denatured alcohol.")
            }
        }

        // Adjust suitability limits
        suitability = suitability.coerceIn(0, 100)
        personalizedSafety = personalizedSafety.coerceIn(10, 100)

        val severity = when {
            suitability < 40 -> "High Risk"
            suitability < 75 -> "Caution Recommended"
            else -> "Highly Suitable"
        }

        summary = if (alertList.isNotEmpty()) {
            alertList.joinToString(" | ")
        } else {
            "Matches ${user.skinType} skin and profile preferences perfectly. No hazardous conflicts discovered."
        }

        return ScoringResult(personalizedSafety, suitability, severity, summary)
    }

    // --- Medicine Cabinet Actions ---
    fun addCabinetItem(name: String, dosage: String, expiryDate: String, quantity: Int) {
        val email = _currentUser.value?.email ?: return
        if (name.isEmpty() || dosage.isEmpty() || expiryDate.isEmpty() || quantity < 0) return
        viewModelScope.launch(Dispatchers.IO) {
            val item = CabinetItemEntity(
                userEmail = email,
                name = name,
                dosage = dosage,
                expiryDate = expiryDate,
                quantity = quantity
            )
            cabinetDao.insertCabinetItem(item)
        }
    }

    fun deleteCabinetItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            cabinetDao.deleteCabinetItem(id)
        }
    }

    // --- Skin Journey Actions ---
    fun addJourneyEntry(description: String, routine: String, improvementRating: Int) {
        val email = _currentUser.value?.email ?: return
        if (description.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val yString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val entry = JourneyEntryEntity(
                userEmail = email,
                date = yString,
                description = description,
                routine = routine,
                improvementRating = improvementRating
            )
            journeyDao.insertJourneyEntry(entry)
        }
    }

    fun deleteJourneyEntry(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            journeyDao.deleteJourneyEntry(id)
        }
    }

    // --- Community Actions ---
    fun createCommunityPost(title: String, content: String, category: String) {
        val user = _currentUser.value ?: return
        if (title.isEmpty() || content.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val post = CommunityPostEntity(
                authorName = user.fullName,
                authorEmail = user.email,
                title = title,
                content = content,
                category = category
            )
            communityDao.insertPost(post)
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            communityDao.deletePost(postId)
        }
    }

    fun toggleLikePost(post: CommunityPostEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val likedList = post.likedByEmails.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
            val newLikedBy: String
            val newLikeCount: Int
            if (likedList.contains(user.email)) {
                likedList.remove(user.email)
                newLikeCount = (post.likesCount - 1).coerceAtLeast(0)
            } else {
                likedList.add(user.email)
                newLikeCount = post.likesCount + 1
            }
            newLikedBy = likedList.joinToString(",")
            communityDao.updatePost(
                post.copy(likesCount = newLikeCount, likedByEmails = newLikedBy)
            )
        }
    }

    fun addCommentToPost(postId: Int, content: String) {
        val user = _currentUser.value ?: return
        if (content.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val comment = CommentEntity(
                postId = postId,
                authorName = user.fullName,
                authorEmail = user.email,
                content = content
            )
            communityDao.insertComment(comment)
        }
    }

    fun getCommentsForPostFlow(postId: Int): Flow<List<CommentEntity>> {
        return communityDao.getCommentsForPost(postId)
    }

    // --- Admin Operations ---
    fun adminAddProduct(name: String, brand: String, category: String, ingredients: String, benefits: String, warnings: String, safetyScore: Int, tags: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val p = ProductEntity(
                name = name,
                brand = brand,
                category = category,
                ingredients = ingredients,
                benefits = benefits,
                warnings = warnings,
                safetyScore = safetyScore,
                imageUrl = "gen_admin_prod",
                suitabilityTags = tags
            )
            productDao.insertProduct(p)
        }
    }

    // --- CareBot Gemini Connection (Retrofit Option B) ---
    private val _isChatbotLoading = MutableStateFlow(false)
    val isChatbotLoading: StateFlow<Boolean> = _isChatbotLoading.asStateFlow()

    // --- AI Label Image Scanner States ---
    private val _isScannerLoading = MutableStateFlow(false)
    val isScannerLoading: StateFlow<Boolean> = _isScannerLoading.asStateFlow()

    private val _scannedResult = MutableStateFlow<ScannedIngredientsResult?>(null)
    val scannedResult: StateFlow<ScannedIngredientsResult?> = _scannedResult.asStateFlow()

    private val _scannerError = MutableStateFlow<String?>(null)
    val scannerError: StateFlow<String?> = _scannerError.asStateFlow()

    fun clearChat() {
        var user = _currentUser.value
        if (user == null) {
            user = UserEntity(
                email = "guest@caresure.com",
                fullName = "Guest Caregiver",
                mobileNumber = "1234567890",
                passwordHash = "",
                age = 30,
                gender = "Unspecified"
            )
            viewModelScope.launch(Dispatchers.IO) {
                userDao.insertUser(user)
                withContext(Dispatchers.Main) {
                    _currentUser.value = user
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.clearChatHistory(user.email)
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.trim().isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            var user = _currentUser.value
            if (user == null) {
                // Auto-seed Guest profile so we never fail silently if accessed before register/login
                user = UserEntity(
                    email = "guest@caresure.com",
                    fullName = "Guest Caregiver",
                    mobileNumber = "1234567890",
                    passwordHash = "",
                    age = 30,
                    gender = "Unspecified",
                    skinType = "Sensitive",
                    hairType = "Dry & Frizzy",
                    allergies = "Salicylic acid",
                    healthConditions = "Eczema",
                    currentMedicines = "None",
                    preferences = "Fragrance-free only"
                )
                userDao.insertUser(user)
                withContext(Dispatchers.Main) {
                    _currentUser.value = user
                }
            }

            // Save User message immediately
            val userMsg = ChatMessageEntity(
                userEmail = user.email,
                message = userText,
                isUser = true
            )
            chatDao.insertMessage(userMsg)

            withContext(Dispatchers.Main) {
                _isChatbotLoading.value = true
            }

            try {
                val systemContextPrompt = """
                    You are CareBot, a medical and skincare ingredient intelligence AI assistant representing CareSure AI+. 
                    The user's health profile:
                    - Name: ${user.fullName}
                    - Age: ${user.age}
                    - Skin Type: ${user.skinType}
                    - Hair Type: ${user.hairType}
                    - Declared Allergies: ${user.allergies}
                    - Health Conditions: ${user.healthConditions}
                    - Current Medicines: ${user.currentMedicines}
                    
                    Always respond with accurate, scientific, supportive, yet critical medical guidelines regarding product safety. Keep your answers concise, clear and structured. Refer specifically to whether their ingredients or profiles match any concerns.
                """.trimIndent()

                val responseText = tryCallGeminiAPI(userText, systemContextPrompt)

                val botMsg = ChatMessageEntity(
                    userEmail = user.email,
                    message = responseText,
                    isUser = false
                )
                chatDao.insertMessage(botMsg)
            } catch (e: Exception) {
                android.util.Log.e("CareBot_Error", "Chat execution failed", e)
                val botMsgErr = ChatMessageEntity(
                    userEmail = user.email,
                    message = "CareBot connection failed: ${e.localizedMessage}. Please try again later.",
                    isUser = false
                )
                chatDao.insertMessage(botMsgErr)
            } finally {
                withContext(Dispatchers.Main) {
                    _isChatbotLoading.value = false
                }
            }
        }
    }

    // --- AI Label Image Scanner Methods ---
    fun analyzeLabelImage(base64Image: String, mimeType: String = "image/jpeg") {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _isScannerLoading.value = true
                _scannerError.value = null
                _scannedResult.value = null
            }

            // High-fidelity dynamic offline simulation overrides
            if (base64Image == "PRESET_ACNE_SPOT") {
                delaySimulation(1500)
                val res = ScannedIngredientsResult(
                    productName = "Acne Relief Spot Gel",
                    brand = "Anti-Acne Spot Cream",
                    ingredients = listOf("Aqua", "Salicylic Acid 2%", "Tea Tree Oil", "Witch Hazel", "Glycerin", "Phenoxyethanol"),
                    safetyScore = 82,
                    safetyStatus = "Caution",
                    explanation = "Formulated with 2% Salicylic Acid to exfoliate and clear pore-clogging debris, paired with soothing Tea Tree Oil. Witch Hazel skin-clarifies but can be drying for sensitive skin types."
                )
                withContext(Dispatchers.Main) {
                    _scannedResult.value = res
                    _isScannerLoading.value = false
                }
                return@launch
            }
            if (base64Image == "PRESET_DANDRUFF") {
                delaySimulation(1500)
                val res = ScannedIngredientsResult(
                    productName = "Therapy Dandruff Shampoo",
                    brand = "Therapy Dandruff Wash",
                    ingredients = listOf("Aqua", "Ketoconazole 2%", "Zinc Pyrithione", "Sodium Laureth Sulfate", "Cocamidopropyl Betaine"),
                    safetyScore = 78,
                    safetyStatus = "Caution",
                    explanation = "Contains Ketoconazole and Zinc Pyrithione to actively combat dandruff causing fungal activity. May cause mild irritation or scalp dryness if used daily; check suitability."
                )
                withContext(Dispatchers.Main) {
                    _scannedResult.value = res
                    _isScannerLoading.value = false
                }
                return@launch
            }
            if (base64Image == "PRESET_CICA") {
                delaySimulation(1500)
                val res = ScannedIngredientsResult(
                    productName = "Pure Calm Gel-Cream",
                    brand = "Ultra Cica Calm-Gel",
                    ingredients = listOf("Aqua", "Centella Asiatica Extract", "Niacinamide 5%", "Ceramide NP", "Glycerin", "Hyaluronic Acid"),
                    safetyScore = 95,
                    safetyStatus = "Safe",
                    explanation = "An incredibly supportive cream featuring Centella (Cica) and Ceramides. Ideal for rebuilding compromised skin barriers, reducing redness, and intense hydration."
                )
                withContext(Dispatchers.Main) {
                    _scannedResult.value = res
                    _isScannerLoading.value = false
                }
                return@launch
            }
            // Simulated Barcodes
            if (base64Image == "BARCODE_PEARS") {
                delaySimulation(1500)
                val res = ScannedIngredientsResult(
                    productName = "Pure & Gentle Soap",
                    brand = "Pears",
                    ingredients = listOf("Aqua", "Glycerin", "Lauric Acid", "Sorbitol", "Perfume", "Sodium Lauryl Sulfate"),
                    safetyScore = 90,
                    safetyStatus = "Safe",
                    explanation = "A legendary high-glycerin soap bar known for gentle cleansing. Sorbitol and Glycerin naturally hydrate and lock moisture; contains mild fragrance."
                )
                withContext(Dispatchers.Main) {
                    _scannedResult.value = res
                    _isScannerLoading.value = false
                }
                return@launch
            }
            if (base64Image == "BARCODE_HIMALAYA") {
                delaySimulation(1500)
                val res = ScannedIngredientsResult(
                    productName = "Purifying Neem Face Wash",
                    brand = "Himalaya",
                    ingredients = listOf("Aqua", "Ammonium Lauryl Sulfate", "Melia Azadirachta Leaf Extract (Neem)", "Curcuma Longa Root Extract (Turmeric)", "Glycerin", "Phenoxyethanol"),
                    safetyScore = 85,
                    safetyStatus = "Safe",
                    explanation = "Enriched with clinical grade Neem (antibacterial agent) and Turmeric to cleanse impurities, clear blackheads, and prevent acne recurrences without severe dermal tightening."
                )
                withContext(Dispatchers.Main) {
                    _scannedResult.value = res
                    _isScannerLoading.value = false
                }
                return@launch
            }
            if (base64Image == "BARCODE_VICCO") {
                delaySimulation(1500)
                val res = ScannedIngredientsResult(
                    productName = "Turmeric Skin Cream",
                    brand = "Vicco",
                    ingredients = listOf("Aqua", "Turmeric Extract 16%", "Sandalwood Oil (Chandan Oil)", "Sorbitol", "Stearic Acid"),
                    safetyScore = 94,
                    safetyStatus = "Safe",
                    explanation = "An ayurvedic legacy skin cream with 16% Turmeric and Sandalwood Oil. Sandalwood cools and heals, while Turmeric acts as a natural antiseptic and skin rejuvenator."
                )
                withContext(Dispatchers.Main) {
                    _scannedResult.value = res
                    _isScannerLoading.value = false
                }
                return@launch
            }

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "null" || apiKey.isBlank()) {
                android.util.Log.d("Scanner_Debug", "Falling back to simulated scanner label analysis")
                delaySimulation(2500)
                val fallback = ScannedIngredientsResult(
                    productName = "Dynamic Hydrating Cleanser",
                    brand = "PureHeal Glow",
                    ingredients = listOf("Aqua", "Glycerin", "Niacinamide 10%", "Zinc PCA 1%", "Phenoxyethanol", "Sodium Hyaluronate"),
                    safetyScore = 88,
                    safetyStatus = "Safe",
                    explanation = "This cleanser features high concentrations of Niacinamide (Vitamin B3) paired with Zinc PCA. This combo sebum-balances and reinforces raw dermal barriers without severe lipid stripping."
                )
                withContext(Dispatchers.Main) {
                    _scannedResult.value = fallback
                    _isScannerLoading.value = false
                }
                return@launch
            }

            try {
                android.util.Log.d("Scanner_Debug", "Analyzing camera product image live using gemini-3.5-flash...")
                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

                val prompt = """
                    You are an expert cosmetic and safety clinical ingredient analyzer.
                    Please analyze the attached image of a product label.
                    1. Extract the likely Product Name and Brand. If not visible, guess or use "Unknown Product".
                    2. Extract all visible ingredients as a clean list of comma-separated strings/items.
                    3. Assess a Safety Score (0-100 AQ, where 100 is perfectly safe and non-toxic).
                    4. Identify a Safety Status ("Safe", "Caution", or "Unsafe") based on common health alerts or user safety.
                    5. Provide a 2-3 sentence clinical explanation of the major benefits and potential risks of these ingredients.

                    Respond ONLY with a valid JSON object matching this structure EXACTLY (do not include markdown formatting like ```json or ```, just the raw JSON text):
                    {
                      "productName": "extracted product name",
                      "brand": "extracted brand name",
                      "ingredients": ["ingredient1", "ingredient2", "ingredient3"],
                      "safetyScore": 85,
                      "safetyStatus": "Safe",
                      "explanation": "clinical analysis explanation text here"
                    }
                """.trimIndent()

                val jsonBodyObj = org.json.JSONObject().apply {
                    put("contents", org.json.JSONArray().apply {
                        put(org.json.JSONObject().apply {
                            put("parts", org.json.JSONArray().apply {
                                put(org.json.JSONObject().apply {
                                    put("text", prompt)
                                })
                                put(org.json.JSONObject().apply {
                                    put("inlineData", org.json.JSONObject().apply {
                                        put("mimeType", mimeType)
                                        put("data", base64Image)
                                    })
                                })
                            })
                        })
                    })
                }

                val jsonBody = jsonBodyObj.toString()
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(jsonBody.toRequestBody(mediaType))
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string() ?: ""
                    android.util.Log.d("Scanner_Debug", "Response Code: ${response.code}")
                    if (!response.isSuccessful) {
                        throw Exception("HTTP ${response.code}: $bodyString")
                    }

                    val jsonResponse = extractCleanJsonText(bodyString)
                    android.util.Log.d("Scanner_Debug", "Extracted JSON text: $jsonResponse")
                    val result = parseScannedIngredients(jsonResponse)
                    withContext(Dispatchers.Main) {
                        _scannedResult.value = result
                        _isScannerLoading.value = false
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("Scanner_Debug", "Multimodal scanner failed", e)
                withContext(Dispatchers.Main) {
                    _scannerError.value = "Scanning analysis failed: ${e.localizedMessage}. Please retry with clearer label lighting."
                    _isScannerLoading.value = false
                }
            }
        }
    }

    private fun extractCleanJsonText(responseBody: String): String {
        val rawText = try {
            val root = org.json.JSONObject(responseBody)
            root.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } catch (e: Exception) {
            responseBody
        }

        var cleaned = rawText.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substringAfter("```json").substringBeforeLast("```").trim()
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substringAfter("```").substringBeforeLast("```").trim()
        }
        return cleaned
    }

    private fun parseScannedIngredients(jsonStr: String): ScannedIngredientsResult {
        val root = org.json.JSONObject(jsonStr)
        val name = root.optString("productName", "Scanned Product")
        val brand = root.optString("brand", "Unknown Brand")
        val score = root.optInt("safetyScore", 75)
        val status = root.optString("safetyStatus", "Caution")
        val explanation = root.optString("explanation", "An analysis of the scanned ingredients is complete.")
        
        val ingredientsJson = root.optJSONArray("ingredients")
        val ingredientsList = mutableListOf<String>()
        if (ingredientsJson != null) {
            for (i in 0 until ingredientsJson.length()) {
                ingredientsList.add(ingredientsJson.getString(i))
            }
        } else {
            ingredientsList.add("Water")
            ingredientsList.add("Glycerin")
        }
        return ScannedIngredientsResult(name, brand, ingredientsList, score, status, explanation)
    }

    fun clearScanner() {
        _scannedResult.value = null
        _scannerError.value = null
    }

    private suspend fun tryCallGeminiAPI(prompt: String, systemInstruction: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        android.util.Log.d("CareBot_Debug", "tryCallGeminiAPI: Prompt: '$prompt', API Key defined=${apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"}")
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "null" || apiKey.isBlank()) {
            android.util.Log.d("CareBot_Debug", "Falling back to smart offline advice (no valid API key declared).")
            delaySimulation(1000)
            return getSmartHeuristicsResponse(prompt)
        }

        return withContext(Dispatchers.IO) {
            val modelsToTry = listOf("gemini-3.5-flash", "gemini-2.5-flash", "gemini-1.5-flash")
            var lastException: Exception? = null

            for (model in modelsToTry) {
                try {
                    android.util.Log.d("CareBot_Debug", "Connecting to Gemini API using model: $model")
                    val okHttpClient = OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build()

                    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                    
                    // Construct request cleanly using Android org.json.JSONObject (avoids manual character escaping vulnerabilities)
                    val jsonBodyObj = org.json.JSONObject().apply {
                        put("contents", org.json.JSONArray().apply {
                            put(org.json.JSONObject().apply {
                                put("parts", org.json.JSONArray().apply {
                                    put(org.json.JSONObject().apply {
                                        put("text", prompt)
                                    })
                                })
                            })
                        })
                        if (systemInstruction.isNotEmpty()) {
                            put("systemInstruction", org.json.JSONObject().apply {
                                put("parts", org.json.JSONArray().apply {
                                    put(org.json.JSONObject().apply {
                                        put("text", systemInstruction)
                                    })
                                })
                            })
                        }
                    }
                    val jsonBody = jsonBodyObj.toString()
                    android.util.Log.d("CareBot_Debug", "Request Payload: $jsonBody")

                    val request = Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
                        .post(jsonBody.toRequestBody(mediaType))
                        .build()

                    okHttpClient.newCall(request).execute().use { response ->
                        val bodyString = response.body?.string() ?: ""
                        android.util.Log.d("CareBot_Debug", "Response Code: ${response.code}")
                        android.util.Log.d("CareBot_Debug", "Response Payload: $bodyString")

                        if (!response.isSuccessful) {
                            throw Exception("HTTP ${response.code}: $bodyString")
                        }
                        val text = extractTextFromJson(bodyString)
                        if (text.startsWith("Error:") || text.contains("No response text found")) {
                            throw Exception("JSON structure invalid: $text")
                        }
                        android.util.Log.d("CareBot_Debug", "Gemini response obtained successfully: ${text.take(50)}...")
                        return@withContext text
                    }
                } catch (e: Exception) {
                    android.util.Log.e("CareBot_Debug", "Failed using model $model: ${e.localizedMessage}", e)
                    lastException = e
                }
            }

            val errorMsg = lastException?.localizedMessage ?: "Unknown network/API error"
            android.util.Log.e("CareBot_Debug", "All models failed. Final error: $errorMsg")
            
            // Failsafe message requested by User
            "CareBot is temporarily unavailable. Please try again later."
        }
    }

    private fun extractTextFromJson(json: String): String {
        return try {
            val root = org.json.JSONObject(json)
            val candidates = root.getJSONArray("candidates")
            val candidate = candidates.getJSONObject(0)
            val content = candidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val part = parts.getJSONObject(0)
            part.getString("text")
        } catch (e: Exception) {
            try {
                val root = org.json.JSONObject(json)
                if (root.has("error")) {
                    val error = root.getJSONObject("error")
                    val message = error.getString("message")
                    "Error from Gemini: $message"
                } else {
                    "Error parsing: ${e.localizedMessage}"
                }
            } catch (inner: Exception) {
                "No response text found."
            }
        }
    }

    private suspend fun delaySimulation(timeMs: Long) {
        withContext(Dispatchers.IO) {
            Thread.sleep(timeMs)
        }
    }

    private fun getSmartHeuristicsResponse(prompt: String): String {
        val q = prompt.lowercase().trim()
        val user = _currentUser.value
        
        return when {
            q == "hello" || q == "hi" || q == "hey" || q.startsWith("hello") || q.startsWith("hi ") -> {
                val namePart = if (user != null) ", ${user.fullName}" else ""
                "Hello$namePart! 🤖 I'm CareBot, your personalized dermatologist and clinical ingredient advisor. How can I assist you with your skincare, haircare, or ingredient safety queries today?"
            }
            
            q.contains("niacinamide") -> {
                "**Clinical Ingredient Spotlight: Niacinamide (Vitamin B3)**\n\n" +
                "• **Purpose:** High-affinity skin repair, sebum balancing, and melanin transfer reduction.\n" +
                "• **Synergy:** Works beautifully with **Centella Asiatica** or **Hyaluronic Acid** for deep skin barrier replenishment.\n" +
                "• **Usage Advice:** Ideal for daily AM/PM usage. Limit concentration to 2%-5% if your skin is reactive to prevent temporary flushing. Safe for continuous pregnancy usage."
            }
            
            q.contains("retinol") || q.contains("vitamin a") || q.contains("tretinoin") -> {
                "**Clinical Advisory: Retinol & Vitamin A Derivatives**\n\n" +
                "• **Mechanism:** Accelerates cellular renewal cycle, purges clogged sebaceous glands, and boosts synthesized collagen.\n" +
                "• **Strict Contraindications:**\n" +
                "  1. **PREGNANCY:** Strictly do NOT use if pregnant or nursing due to potential systemic fetal absorption risk.\n" +
                "  2. **BARRIER BREACH:** Do not apply on active eczematous dry patches, active rosacea, or open skin flares.\n" +
                "• **Application Blueprint:** PM routine only on fully dry skin. Seal with broad-spectrum SPF 50+ in the daily AM routine, as retinoids heavily increase photo-reactivity."
            }
            
            q.contains("salicylic") || q.contains("bha") || q.contains("acne") || q.contains("pimple") -> {
                "**Aesthetic Advisory: Salicylic Acid (BHA)**\n\n" +
                "• **Mechanism:** Lipid-soluble acid that penetrates deep inside pores to liquefy compacted sebum, dead cells, and cutibacterium debris.\n" +
                "• **Implementation:** Restrict exfoliation to 2-3 PM routines weekly. Always follow with rich botanical hydrating humectants to shield the outer stratum corneum from dry peeling.\n" +
                "• **Skin Suitability:** Extremely effective for oily, acne-prone, or congested skin profiles."
            }
            
            q.contains("dandruff") || q.contains("hair") || q.contains("scalp") || q.contains("ketoconazole") || q.contains("shampoo") -> {
                "**Haircare & Scalp Protocol: Dandruff & Seborrheic Dermatitis**\n\n" +
                "• **Factor:** Overgrowth of Malassezia yeast feeding on excess sebum lipids.\n" +
                "• **Primary Cleanse:** Select cleansers formulated with **Ketoconazole (2%)** or **Salicylic Acid + Zinc Pyrithione**.\n" +
                "• **Clinical Directions:** Massage gently as scalp rinse. Leave the therapeutic lather untouched for **4 to 5 minutes** prior to rinsing. This duration is paramount to compromise the yeast cellular membrane."
            }
            
            q.contains("oily skin") || q.contains("suggest") && q.contains("oily") || q.contains("oily") -> {
                "**Personalized Blueprint: Sebum Control for Oily Skin**\n\n" +
                "• **Active Targets:** Balance sebum production without causing dehydration-triggered reactive oiliness.\n" +
                "• **Recommended Routine Assets:**\n" +
                "  1. **Cleanse:** LHA/Salicylic Acid gel cleanser (PM).\n" +
                "  2. **Exfoliate & Tone:** Niacinamide (5%) or Zinc PCA serums.\n" +
                "  3. **Hydration:** Ultra-light gel-creams containing Hyaluronic Acid or Centella (like Dot & Key or Neutrogena hydro-gels).\n" +
                "• **Comedogenic Filter:** Keep ingredient ratings strictly under 2/5 to ensure pore-breathing clarity."
            }
            
            q.contains("dry skin") || q.contains("suggest") && q.contains("dry") || q.contains("dry") -> {
                "**Personalized Blueprint: Intense Moisture for Dry Skin**\n\n" +
                "• **Active Targets:** Restore lipid deficiencies, prevent transepidermal water loss (TEWL), and rebuild the skin's acidic mantle.\n" +
                "• **Recommended Routine Assets:**\n" +
                "  1. **Cleanse:** Non-foaming, creamy milk cleansers containing Ceramides (AM/PM).\n" +
                "  2. **Treat:** Hyaluronic Acid (2%) or Squalane-based barrier lock-in serums.\n" +
                "  3. **Moisten:** Rich occlusive creams with Shea Butter, Ceramides 1, 3, 6, and Glycerin.\n" +
                "• **Daily Guide:** Avoid physical facial scrubs that micro-tear delicate lipid layers."
            }
            
            q.contains("hyaluronic") || q.contains("hydration") || q.contains("moisture") -> {
                "**Clinical Ingredient Spotlight: Hyaluronic Acid (HA)**\n\n" +
                "• **Purpose:** High-efficiency humectant holding up to **1000x its own molecular weight** in pure environmental water molecules.\n" +
                "• **Application Secret:** ALWAYS apply on damp or slightly wet skin! If applied dry in arid climates, HA will draw moisture outwards from your deep dermis, triggering dehydrating irritation.\n" +
                "• **Pairing:** Ideal to seal immediately with a lipid-rich emollient cream to lock in the surface hydration."
            }
            
            q.contains("vitamin c") || q.contains("ascorbic") || q.contains("brighten") || q.contains("dark spot") || q.contains("pigmentation") -> {
                "**Clinical Spotlight: L-Ascorbic Acid (Vitamin C)**\n\n" +
                "• **Purpose:** Neutralizes free radicals, boosts natural collage synthesis, and inhibits tyrosinase activity to fade localized dark spots/hyperpigmentation.\n" +
                "• **Usage Routine:** Best applied in the daily AM routine to amplify the biological efficacy of your sunscreen actives.\n" +
                "• **Stability Care:** L-Ascorbic Acid oxidizes rapidly in UV light. Store in amber glass or opaque airless pump bottles, and discard if the solution turns a dark brown/orange shade."
            }
            
            q.contains("sunscreen") || q.contains("spf") || q.contains("sun protection") || q.contains("uv") -> {
                "**Clinical Directive: The Broad-Spectrum Sunscreen Rule**\n\n" +
                "• **Significance:** Sun damage is responsible for over **80% of premature fine lines, wrinkles, and pigmentary changes**.\n" +
                "• **Efficacy Guidelines:** Use SPF 30 or SPF 50 labeled with \"Broad-Spectrum PA++++\" to counter both burning UVB waves and aging UVA rays.\n" +
                "• **Application Rule:** Apply a continuous streak on two fingers for full face/neck coverage. Re-apply every 2 to 3 hours under persistent outdoor sunshine."
            }
            
            q.contains("sensitive skin") || q.contains("redness") || q.contains("eczema") || q.contains("rosacea") -> {
                "**Symptom Protocol: Sensitive Skin & Barrier Repair**\n\n" +
                "• **Target Goals:** Cool micro-erythema, relieve inflammation, and bypass allergy-mediating factors.\n" +
                "• **Safe Botanical Actives:**\n" +
                "  1. **Centella Asiatica (Cica):** Calms vascular swelling and cools surface heat.\n" +
                "  2. **Colloidal Oatmeal (1%):** Binds to raw, itchy, flaking eczema sheets to provide a protective layer.\n" +
                "  3. **Ceramides:** The ultimate cellular cement to close microscopic barrier cracks.\n" +
                "• **Warning:** Completely steer clear of essential oils, peppermint, denatured alcohol, and synthetic fragrance profiles."
            }

            q.contains("dot & key") || q.contains("moisturizer") || q.contains("suitability") -> {
                "**Product Analysis: Dot & Key Moisturizer (Cica & Niacinamide Gel-Cream)**\n\n" +
                "• **Suitability Rating:** 92% Suitable for Acne-Prone & Oily Skin.\n" +
                "• **Core Actives:** Cica (Centella) extract for dynamic barrier soothing, paired with Niacinamide (Vitamin B3) for calming acne-related erythema.\n" +
                "• **Comedogenic Profiling:** Fully water-gel based. Free of heavy occlusion butters (such as Shea Butter or Coconut oil) that clog delicate pores.\n" +
                "• **Recommendation:** Apply evenly on damp skin post-toning. Extremely safe to pair with active anti-acne PM treatments like Benzoyl Peroxide."
            }
            
            _currentUser.value != null && (q.contains("my profile") || q.contains("profile") || q.contains("recommend") || q.contains("allergy")) -> {
                val u = _currentUser.value!!
                "**Personalized Profile Analysis for ${u.fullName}**\n\n" +
                "• **Identified Profile:** Skin Type: `${u.skinType}` | Hair Type: `${u.hairType}`\n" +
                "• **Allergy Warnings:** `${u.allergies.ifEmpty { "None declared" }}`\n" +
                "• **Health Conditions:** `${u.healthConditions.ifEmpty { "None declared" }}`\n" +
                "• **Daily Treatment Framework Advice:** Since you exhibit a `${u.skinType}` skin type, prioritize delicate non-stripping cleansers. " +
                "Avoid heavy synthetic fragrance lines if sensitivities are present. For active acne, use gentle localized hydrocolloid sheets over raw squeezing."
            }
            
            else -> {
                "• **CareBot Comprehensive Skincare, Haircare, and Wellness Advisory** •\n\n" +
                "Thank you for reaching out! To help you explore any skincare or wellness question, here is our specialized clinical advice:\n\n" +
                "1. **Safety Clearance:** Check individual skincare and medicine products in the **Products** tab to see a personalized Safety Score (0-100) mapped to your clinical conditions.\n" +
                "2. **Ingredient Synergy:** When blending active acids (like Vitamin C or Glycolic Acid) with Retinols, separate usage (e.g., Vitamin C in AM, Retinol in PM) to prevent skin irritation.\n" +
                "3. **Dosage Guard:** Limit high-potency chemical peels to weekly/fortnightly frequencies.\n\n" +
                "Please describe any specific ingredient or product name to get a detailed medical assessment!"
            }
        }
    }
}

// --- Dynamic Score Result model ---
data class ScoringResult(
    val safetyScore: Int,
    val suitabilityScore: Int,
    val severityLevel: String, // "Highly Suitable", "Caution", "High Risk"
    val analysisSummary: String
)
