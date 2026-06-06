package com.unreal.medisageai.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.unreal.medisageai.BuildConfig
import com.unreal.medisageai.data.MediSageRepository
import com.unreal.medisageai.data.MediSageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val MEDICAL_SYSTEM_INSTRUCTION = """
You are MediSage, a professional medical assistant.
Provide clear, evidence-informed health information in plain language.
You are NOT a substitute for a licensed clinician. Do not provide a definitive diagnosis,
prescribe medication, or interpret personal test results as medical advice.
Always end responses with this disclaimer on its own line:
"Disclaimer: This information is for educational purposes only and is not a substitute for professional medical advice, diagnosis, or treatment. Consult a qualified healthcare provider for any medical concerns."
If a user describes symptoms of a medical emergency (chest pain, stroke signs, severe bleeding,
suicidal ideation, anaphylaxis, etc.), instruct them to contact local emergency services immediately.
"""

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content { text(MEDICAL_SYSTEM_INSTRUCTION.trimIndent()) },
    )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMediSageRepository(impl: MediSageRepositoryImpl): MediSageRepository
}
