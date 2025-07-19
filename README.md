# Registro de Actividades - App Android

Aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para registrar, listar, editar y eliminar actividades realizadas por los usuarios. La app utiliza **Firebase** como backend.

## ✨ Características

- 📋 Registro de actividades con campos de:
    - Actividad
    - Subactividad
    - Fecha
    - Duración (horas y minutos)
    - Comentarios

- 📄 Listado de actividades registradas
    - Visualización en tabla
    - Visualización de detalles de actividad
    - Edición y eliminación de registros

- ✅ Gestión de estados con `ViewModel` y `StateFlow`
- 🎨 Interfaz 100% Jetpack Compose Material 3
- 🔐 Autenticación con Firebase
- ☁️ Almacenamiento de registros en Firestore

---

## 🛠️ Tecnologías usadas

- Kotlin
- Jetpack Compose (Material 3)
- Android Architecture Components
    - ViewModel
    - StateFlow
- Firebase (Auth + Firestore)
- Navigation Compose
- DatePicker Material
- Coroutines

---

## 🔧 Instalación y ejecución

1. Clona el repositorio:

```bash
git clone https://github.com/tu-usuario/registro-actividades-app.git
cd registro-actividades-app