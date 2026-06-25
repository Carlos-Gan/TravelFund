package com.gamo.travelfund.data.repository

import com.gamo.travelfund.BuildConfig
import com.gamo.travelfund.data.remote.GeminiRecommendationResponse
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson


class GeminiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateTravelRecommendation(
        destination: String,
        totalBudget: Double,
        baseCurrency: String,
        destinationCurrency: String,
        categories: List<BudgetCategoryWithStats>,
        interests: String
    ): GeminiRecommendationResponse {


        val categoriesText = if (categories.isEmpty()) {
            "No hay categorías registradas."

        } else {
            categories.joinToString("\n") { categoryStats ->
                val category = categoryStats.category
                val remaining = category.plannedAmount - categoryStats.spentAmount

                """
                - ${category.emoji} ${category.name}
                  Presupuesto: ${category.plannedAmount} $baseCurrency
                  Gastado: ${categoryStats.spentAmount} $baseCurrency
                  Restante: $remaining $baseCurrency
                """.trimIndent()
            }
        }

        val prompt = """
            Eres un asistente experto en viajes y presupuesto.

            Destino: $destination
            Presupuesto total: $totalBudget $baseCurrency
            Moneda destino: $destinationCurrency
            Gustos del usuario: $interests

            Categorías actuales:
            $categoriesText
            
            Responde ÚNICAMENTE con JSON válido, sin markdown, sin explicación extra.
            {
              "summary": "Resumen corto del destino",
              "recommendedPlaces": [
                {
                  "name": "Nombre del lugar",
                  "reason": "Por qué lo recomiendas",
                  "estimatedCost": "Rango aproximado de costo"
                }
              ],
              "budgetTips": [
                "Consejo de ahorro 1",
                "Consejo de ahorro 2"
              ],
              "suggestedCategories": [
                {
                  "name": "Nombre de categoría",
                  "emoji": "Emoji",
                  "reason": "Por qué conviene agregarla"
                }
              ],
              "budgetAnalysis": [
                {
                  "category": "Nombre de categoría",
                  "comment": "Comentario sobre si el presupuesto parece alto, bajo o adecuado"
                }
              ]
            }
            Reglas:
            - No inventes precios exactos.
            - Usa rangos aproximados.
            - Recomienda cosas que puedan entrar dentro del presupuesto restante.
            - Devuelve máximo 5 lugares recomendados.
            - Devuelve máximo 4 consejos.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)

            val json = response.text
                ?.removePrefix("```json")
                ?.removePrefix("```")
                ?.removeSuffix("```")
                ?.trim()

            Gson().fromJson(
                json,
                GeminiRecommendationResponse::class.java
            )
        } catch (e: Exception) {
            GeminiRecommendationResponse(
                summary = "Error Gemini: ${e.message}"
            )
        }


    }
}