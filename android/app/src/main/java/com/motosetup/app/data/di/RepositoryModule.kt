package com.motosetup.app.data.di

import com.motosetup.app.data.repository.AuthRepository
import com.motosetup.app.data.repository.BikeRepository
import com.motosetup.app.data.repository.ChecklistRepository
import com.motosetup.app.data.repository.EntitlementStore
import com.motosetup.app.data.repository.FirebaseAuthRepository
import com.motosetup.app.data.repository.FirebaseBikeRepository
import com.motosetup.app.data.repository.FirebaseChecklistRepository
import com.motosetup.app.data.repository.FirebaseEntitlementStore
import com.motosetup.app.data.repository.FirebaseMaintenanceRepository
import com.motosetup.app.data.repository.FirebaseSessionRepository
import com.motosetup.app.data.repository.FirebaseTrackRepository
import com.motosetup.app.data.repository.MaintenanceRepository
import com.motosetup.app.data.repository.SessionRepository
import com.motosetup.app.data.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBikeRepository(impl: FirebaseBikeRepository): BikeRepository

    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(impl: FirebaseMaintenanceRepository): MaintenanceRepository

    @Binds
    @Singleton
    abstract fun bindChecklistRepository(impl: FirebaseChecklistRepository): ChecklistRepository

    @Binds
    @Singleton
    abstract fun bindEntitlementStore(impl: FirebaseEntitlementStore): EntitlementStore

    @Binds
    @Singleton
    abstract fun bindTrackRepository(impl: FirebaseTrackRepository): TrackRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: FirebaseSessionRepository): SessionRepository
}
