package com.example.data

import androidx.room.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val mobileNumber: String,
    val passwordHash: String,
    val age: Int,
    val gender: String,
    val skinType: String = "Normal",
    val hairType: String = "Normal",
    val allergies: String = "",
    val healthConditions: String = "",
    val currentMedicines: String = "",
    val preferences: String = "",
    val profileImageUri: String? = null
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val brand: String,
    val category: String, // "Skincare", "Haircare", "Medicine"
    val ingredients: String, // Comma separated list
    val benefits: String,
    val warnings: String,
    val safetyScore: Int, // 1 - 100
    val imageUrl: String = "",
    val suitabilityTags: String = "" // Comma separated tags
)

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val name: String,
    val function: String,
    val benefits: String,
    val sideEffects: String,
    val riskLevel: String, // "Safe", "Moderate", "High Risk"
    val scientificDescription: String,
    val comedogenicRating: Int, // 0 - 5
    val pregnancySafetyStatus: String // "Safe", "Avoid", "Consult Doctor"
)

@Entity(tableName = "cabinet_items")
data class CabinetItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val name: String,
    val dosage: String,
    val expiryDate: String, // YYYY-MM-DD
    val quantity: Int,
    val refillReminder: Boolean = true
)

@Entity(tableName = "journey_entries")
data class JourneyEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val date: String, // YYYY-MM-DD
    val description: String,
    val routine: String,
    val photoPath: String? = null,
    val improvementRating: Int // 1 to 10
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorEmail: String,
    val title: String,
    val content: String,
    val category: String, // "Skincare", "Routine", "Medicinal Query", "General"
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val likedByEmails: String = "" // Comma-separated user emails
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorEmail: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
