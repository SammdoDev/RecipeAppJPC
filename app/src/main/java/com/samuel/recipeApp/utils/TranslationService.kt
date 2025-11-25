package com.samuel.recipeApp.utils

import android.content.Context
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Translation Service menggunakan Google ML Kit
 * GRATIS dan OFFLINE setelah model didownload
 */
class TranslationService(private val context: Context) {

    private var translator: Translator? = null
    private var isModelDownloaded = false

    init {
        initializeTranslator()
    }

    private fun initializeTranslator() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()

        translator = Translation.getClient(options)

        // Download model jika belum ada
        val conditions = DownloadConditions.Builder()
            .requireWifi() // Download hanya dengan WiFi
            .build()

        translator?.downloadModelIfNeeded(conditions)
            ?.addOnSuccessListener {
                isModelDownloaded = true
            }
            ?.addOnFailureListener {
                isModelDownloaded = false
            }
    }

    /**
     * Translate single text
     */
    suspend fun translate(text: String): String = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext text

        try {
            translator?.translate(text)?.await() ?: text
        } catch (e: Exception) {
            text // Return original if translation fails
        }
    }

    /**
     * Translate list of texts
     */
    suspend fun translateList(texts: List<String>): List<String> = withContext(Dispatchers.IO) {
        texts.map { text ->
            if (text.isBlank()) {
                text
            } else {
                try {
                    translator?.translate(text)?.await() ?: text
                } catch (e: Exception) {
                    text
                }
            }
        }
    }

    /**
     * Translate ingredients (Pair of ingredient and measure)
     */
    suspend fun translateIngredients(
        ingredients: List<Pair<String, String>>
    ): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        ingredients.map { (ingredient, measure) ->
            val translatedIngredient = try {
                translator?.translate(ingredient)?.await() ?: ingredient
            } catch (e: Exception) {
                ingredient
            }

            val translatedMeasure = try {
                translator?.translate(measure)?.await() ?: measure
            } catch (e: Exception) {
                measure
            }

            translatedIngredient to translatedMeasure
        }
    }

    /**
     * Translate instructions (split by line)
     */
    suspend fun translateInstructions(instructions: String): String = withContext(Dispatchers.IO) {
        if (instructions.isBlank()) return@withContext instructions

        try {
            // Split by newline and translate each step
            val steps = instructions.split("\r\n", "\n").filter { it.isNotBlank() }
            val translatedSteps = steps.map { step ->
                translator?.translate(step.trim())?.await() ?: step
            }
            translatedSteps.joinToString("\n")
        } catch (e: Exception) {
            instructions
        }
    }

    fun isReady(): Boolean = isModelDownloaded

    fun cleanup() {
        translator?.close()
    }
}

/**
 * Singleton instance untuk easy access
 */
object TranslationManager {
    private var translationService: TranslationService? = null

    fun initialize(context: Context) {
        if (translationService == null) {
            translationService = TranslationService(context.applicationContext)
        }
    }

    fun getService(): TranslationService? = translationService
}