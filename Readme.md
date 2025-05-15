# Integración BCI API - Manual de Ejecución y Pruebas

Este documento proporciona instrucciones detalladas para configurar, ejecutar y probar la API de integración BCI.

## Prerequisitos

Asegúrate de tener instalados los siguientes elementos:

- **Java Development Kit (JDK) 21**: Esta aplicación requiere JDK 21 para ser ejecutada.
    - Verifica tu instalación con: `java -version`
    - Descarga desde: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://adoptium.net/)

- **Gradle 8.x o superior**: Se recomienda usar Gradle 8.5 o superior.
    - Verifica tu instalación con: `gradle -v`
    - Descarga desde: [Gradle Releases](https://gradle.org/releases/)
    - Alternativamente, puedes usar el Gradle Wrapper incluido en el proyecto: `./gradlew` (Linux/Mac) o `gradlew.bat` (Windows)

- **Git**: Para clonar el repositorio.
    - Verifica tu instalación con: `git --version`
    - Descarga desde: [Git SCM](https://git-scm.com/downloads)

- **Postman** (opcional): Para probar la API manualmente.
    - Descarga desde: [Postman](https://www.postman.com/downloads/)

## Configuración del Proyecto

1. **Clonar el Repositorio**:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd integracion_bci
   ```

2. **Configuración de Base de Datos**:
   La aplicación utiliza H2 (base de datos en memoria) por defecto. No se requiere configuración adicional para entornos de desarrollo.

## Comandos de Gradle

### Construir el Proyecto
```bash
  ./gradlew build
```

### Ejecutar la Aplicación
```bash
  ./gradlew bootRun
```
La aplicación iniciará en: http://localhost:8080

### Ejecutar Tests
```bash 
  ./gradlew test 
```

### Limpiar la Construcción
```bash
  ./gradlew clean
```

### Construir sin Ejecutar Tests
```bash
  ./gradlew build -x test
```

## Pruebas de API

### Usando Swagger UI
La API incluye documentación interactiva con Swagger UI:
1. Inicia la aplicación usando `./gradlew bootRun`
2. Abre en tu navegador: http://localhost:8080/swagger-ui/index.html
3. Puedes explorar y probar todos los endpoints desde la interfaz de Swagger

### Usando cURL
Se incluye un script `user.sh` para probar la creación de usuarios:

```bash
# Dar permisos de ejecución al script (solo sistemas Unix/Linux/Mac)
chmod +x user.sh

# Ejecutar el script
./user.sh
```

Contenido del script:
```bash
  curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@rodriguez.org",
    "password": "Hunter123",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'
```

## Estructura del Proyecto

La aplicación sigue una arquitectura estándar de Spring Boot:

- `controller`: Contiene los controlleres REST
- `service`: Contiene la lógica de negocio
- `repository`: Interfaces para acceso a datos
- `entity`: Entidades JPA
- `dto`: Objetos de transferencia de datos
- `util`: Utilidades (como JwtUtil)

## Tests Unitarios

El proyecto contiene tests unitarios solo para el controller.:

- `UserControllerTest`: Pruebas para el controller de usuarios

Para ejecutar solo los tests de una clase específica:
```bash
 ./gradlew test --tests com.ejercicios.integracion_bci.controller.UserControllerTest
```

Para ejecutar un método de test específico:
```bash
 ./gradlew test --tests com.ejercicios.integracion_bci.controller.UserControllerTest.createUser_Success
```

## Notas Importantes

- La aplicación utiliza H2 como base de datos en memoria, pero se esta usando modo archivo y estos archivos estan en la carpeta /data (los crea al iniciar la aplicacion si no existen)
- Flyway se utiliza para la migración de base de datos (Solo se tiene que iniciar la aplicacion para que la db y su estructura se creen).
- JWT se utiliza para la autenticación, y se genera un token para cada usuario registrado.
- La contraseña debe cumplir con un formato específico (al menos 8 caracteres, contener al menos una letra y un número).
- El correo electrónico debe cumplir con un formato válido.