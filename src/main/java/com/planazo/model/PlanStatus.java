package com.planazo.model;

public enum PlanStatus {
    ACTIVE,      // Plan activo, se puede unir gente
    CANCELLED,   // Plan cancelado por el creador
    COMPLETED,   // Plan ya realizado
    FULL         // Plan lleno (max participantes alcanzado)
}