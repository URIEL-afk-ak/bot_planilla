# Guía de Configuración en Render

## Paso 1: Crear el Servicio Web

1. En el Dashboard de Render, haz clic en **"Nuevo servicio web →"** (Web Service)
2. Conecta tu repositorio de GitHub:
   - Selecciona el repositorio `bot_planilla`
   - O conecta manualmente con la URL del repositorio

## Paso 2: Configuración del Servicio

### Información Básica:
- **Nombre**: `bot-planilla-backend` (o el que prefieras)
- **Región**: Elige la más cercana a tus usuarios
- **Rama**: `main` (o la rama que uses)

### Build & Deploy:
- **Runtime**: `Java`
- **Build Command**: 
  ```bash
  cd backend && mvn clean package -DskipTests
  ```
- **Start Command**: 
  ```bash
  cd backend && java -jar target/bot-planilla-backend-1.0.0.jar --spring.profiles.active=prod
  ```

## Paso 3: Variables de Entorno

En la sección **"Environment"** del servicio, agrega las siguientes variables:

### Variables Requeridas:

1. **SPRING_PROFILES_ACTIVE**
   - Valor: `prod`

2. **SPRING_DATASOURCE_URL**
   - Valor: `jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require`
   - ⚠️ **IMPORTANTE**: Usa el puerto `6543` (Session Pooler) para compatibilidad IPv4
   - Modo Pool: Transacción

3. **SPRING_DATASOURCE_USERNAME**
   - Valor: `postgres.amjsltygrftkcjoekczc`

4. **SPRING_DATASOURCE_PASSWORD**
   - Valor: `1913FacultadesUriel`

### Nota sobre Session Pooler:
- Supabase no es compatible con IPv4 por defecto
- Render es solo IPv4, por lo que necesitas usar el **Session Pooler**
- El puerto del Session Pooler es `6543` (en lugar de `5432`)
- Asegúrate de que el Session Pooler esté habilitado en tu proyecto de Supabase

## Paso 4: Desplegar

1. Haz clic en **"Create Web Service"**
2. Render comenzará a construir y desplegar tu aplicación
3. Una vez completado, obtendrás una URL como: `https://bot-planilla-backend.onrender.com`

## Paso 5: Actualizar Frontend

Después de desplegar, actualiza la URL del backend en el frontend:

1. Abre `frontend/lib/config/app_config.dart`
2. Cambia la URL:
   ```dart
   static const String baseUrl = 'https://bot-planilla-backend.onrender.com/api';
   ```

## Verificación

Para verificar que todo funciona:
1. Visita: `https://tu-servicio.onrender.com/api/productos`
2. Deberías ver una respuesta JSON (probablemente vacía si no hay productos)

## Troubleshooting

### Error de conexión a la base de datos:
- Verifica que el Session Pooler esté habilitado en Supabase
- Asegúrate de usar el puerto `6543` en la URL
- Verifica que las credenciales sean correctas

### Error de compilación:
- Verifica que el `Build Command` esté correcto
- Revisa los logs en Render para ver el error específico

### La aplicación no inicia:
- Verifica que el `Start Command` sea correcto
- Asegúrate de que el archivo JAR se genere correctamente
- Revisa los logs en Render

