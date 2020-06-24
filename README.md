# GoogleDrivePermissions
Herramienta para la gestión de permisos en Google Drive.
## Prerrequisitos
- Java
- Gradle
- Credenciales de desarrollador para la API de Google Drive

## Puesta en marcha
- Clonar el repositorio. `git clone https://github.com/koldou98/GoogleDrivePermissions`
- Añadir las credenciales (credentials.json) en la carpeta src/main/resources
- Configurar el usuario y contraseña de la base de datos, src/main/resources/META-INF/persistence.xml. Estos usuarios por defecto son user y pass.
- Ejecutar `gradle run` en la el directorio donde se encuentra el proyecto.

## Javadoc
El javadoc del proyecto se encuentra en la página asociada a este proyecto: [https://koldou98.github.io/GoogleDrivePermissions/](https://koldou98.github.io/GoogleDrivePermissions/)
## Información adicional
### Versión de Java utilizada
Java 1.8.0_251.\
Link: [https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
### Versión de Gradle utilizada
Gradle 6.3.\
Link: [https://gradle.org/releases/](https://gradle.org/releases/)
### LaF utilizado
FlatLaf 0.26 ha sido el look and feel utilizado.\
Link: [https://github.com/JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf)
### Motor de base de datos
H2 ha sido la base de datos utilizada. Para el desarrollo se utiliza una base de datos embebida, mientras que para las pruebas unitarias una base de datos en memoria.\
Link: [https://github.com/h2database/h2database](https://github.com/h2database/h2database)
### Motor de persistencia 
Hibernate 5.4.\
Link: [https://hibernate.org/orm/releases/5.4/](https://hibernate.org/orm/releases/5.4/)
### Guías de diseño empleadas para los iconos
IntelliJ Platform UI Guidelines, las mismas utilizadas en el LaF.\
Link: [https://jetbrains.design/intellij/](https://jetbrains.design/intellij/)

