Jaime López Díaz

LINK REPO: https://github.com/Jaime1999l/Jaime_Lopez_Diaz_Gestion_de_Novelas_con_Almacenamiento_de_Datos_del_Usuario.git

# Gestión de Novelas con Almacenamiento de Datos del Usuario

Esta es una aplicación Android desarrollada para gestionar una biblioteca personal de novelas. La aplicación permite al usuario agregar novelas, gestionar favoritos, generar reseñas y configurar el modo claro/oscuro de la interfaz de usuario y guardar las preferencias en SharedPreferences. Los datos de la aplicación se almacenan tanto localmente (SQLite) como en la nube (Firebase Firestore).

## Estructura de la Aplicación

### Actividades Principales

1. **PantallaPrincipalActivity**
   - Esta es la actividad principal de la aplicación, donde se muestra la lista de novelas. 
   - Se cargan las novelas desde la base de datos SQLite y Firebase Firestore, y se presentan en un `CardView`.
   - Funcionalidades principales:
     - Mostrar las novelas.
     - Añadir o eliminar novelas de favoritos.
     - Generar reseñas.
     - Navegar a otras actividades mediante un menú lateral (`DrawerLayout`).
   - La actividad también realiza una sincronización en segundo plano con Firestore para asegurarse de que los datos estén actualizados y se almacenan localmente en SQLite.

2. **AddEditNovelActivity**
   - Esta actividad permite al usuario agregar nuevas novelas o editar las existentes.
   - El usuario puede introducir el título, autor, año, sinopsis y seleccionar una imagen para la novela.
   - Los datos de la novela se guardan tanto en Firebase Firestore como en la base de datos local SQLite.

3. **AddReviewActivity**
   - Esta actividad permite al usuario generar reseñas para una novela.
   - El usuario puede agregar el nombre del revisor, un comentario y una puntuación para la novela.
   - Las reseñas se almacenan tanto en Firestore como en SQLite, asegurando que los datos persistan incluso sin conexión.

4. **FavoritesActivity**
   - Muestra una lista de las novelas que el usuario ha marcado como favoritas.
   - Los datos se cargan desde Firestore y SQLite, con la funcionalidad de sincronización para garantizar que los favoritos estén actualizados.

5. **ReviewActivity**
   - Muestra todas las reseñas de las novelas que han sido almacenadas en la base de datos (SQLite y Firestore).
   - Permite al usuario ver más detalles sobre una novela desde la lista de reseñas.

6. **SettingsActivity**
   - La actividad de configuración permite al usuario cambiar el tema de la aplicación (claro u oscuro).
   - El estado del tema seleccionado (modo claro/oscuro) se guarda en `SharedPreferences` y se aplica dinámicamente en toda la aplicación.
   - La aplicación se reinicia automáticamente cuando se cambia el tema para que el cambio se aplique de inmediato.

## Bases de Datos Utilizadas

### SQLite
SQLite es utilizado para almacenar los datos de las novelas y las reseñas localmente. Esto permite que los datos estén disponibles de manera offline.

- **Novelas (`novelasDB`)**:
  - ID de la novela.
  - Título.
  - Autor.
  - Año.
  - Sinopsis.
  - URI de la imagen.
  - Estado de favorito.

- **Reseñas (`reseñas`)**:
  - ID de la reseña.
  - ID de la novela asociada.
  - Revisor.
  - Comentario.
  - Puntuación.
  - Nombre de la novela.

### Firebase Firestore
Firestore se utiliza para almacenar los datos de manera remota, lo que permite que los usuarios accedan a su biblioteca de novelas desde múltiples dispositivos.

- **Colección de Novelas (`novelas`)**:
  - ID de la novela (generado por Firestore).
  - Título.
  - Autor.
  - Año.
  - Sinopsis.
  - URI de la imagen.
  - Estado de favorito.

- **Colección de Reseñas (`reviews`)**:
  - ID de la reseña.
  - ID de la novela asociada.
  - Revisor.
  - Comentario.
  - Puntuación.

### Sincronización entre SQLite y Firestore
La aplicación se sincroniza periódicamente con Firestore para mantener los datos locales actualizados:

- Las novelas se cargan inicialmente desde SQLite.
- Luego, se actualizan desde Firestore para asegurarse de que los datos estén sincronizados.
- Cada vez que se obtiene una nueva novela o se genera una reseña en Firestore, también se almacena localmente en SQLite.

## Funcionalidades de Configuración

### Modo Claro/Oscuro
La aplicación incluye una funcionalidad para cambiar entre el modo claro y oscuro:

- El usuario puede activar o desactivar el modo oscuro desde la actividad de `Settings`.
- El estado del tema se guarda en `SharedPreferences`, lo que permite recordar la preferencia incluso después de cerrar la aplicación.
- Se utiliza `AppCompatDelegate` para cambiar dinámicamente el tema de la aplicación.
- El fondo, los textos y los botones de la interfaz de usuario se adaptan automáticamente al modo claro u oscuro.

#### Implementación de `SharedPreferences` en `SettingsActivity`:
```java
SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

// Aplicar el tema basado en la preferencia
if (isDarkMode) {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
} else {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
}
