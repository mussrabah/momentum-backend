package com.muss_coding.momentum_backend.project

import com.muss_coding.momentum_backend.auth.User
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "projects")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true)
    var description: String? = null,

    // As per your "Momentum" spec
    @Column(nullable = true)
    var color: String? = null,

    @Column(nullable = true)
    var deadline: Instant? = null,

    // This is the critical link
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: User,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)