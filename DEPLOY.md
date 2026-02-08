# Guía de Despliegue

## ⚠️ IMPORTANTE: Seguridad

**NUNCA incluyas credenciales en el código fuente.** Todas las credenciales deben configurarse como variables de entorno en Render Dashboard.

## Opción Recomendada: Render + Supabase

### 📋 Instrucciones Paso a Paso

Para una guía detallada, consulta: **[RENDER_SETUP.md](./RENDER_SETUP.md)**

### Resumen Rápido:

1. **Crear Servicio Web en Render**
   - Tipo: Web Service
   - Runtime: Java
   - Build Command: `cd backend && mvn clean package -DskipTests`
   - Start Command: `cd backend && java -jar target/bot-planilla-backend-1.0.0.jar --spring.profiles.active=prod`

2. **Configurar Variables de Entorno en Render Dashboard:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=jdbc:postgresql://db.amjsltygrftkcjoekczc.supabase.co:6543/postgres?sslmode=require
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=1913FacultadesUriel
   ```

3. **⚠️ Compatibilidad IPv4:**
   - Supabase no es compatible con IPv4 por defecto
   - Render es solo IPv4
   - **Solución**: Usar Session Pooler (puerto `6543`)
   - Habilitar en Supabase: Settings → Database → Connection Pooling

### Base de Datos en Supabase

- **Host**: `db.amjsltygrftkcjoekczc.supabase.co`
- **Puerto directo**: `5432` (no compatible con IPv4)
- **Puerto Session Pooler**: `6543` (✅ usar este para Render)
- **Base de datos**: `postgres`
- **Usuario**: `postgres`

### Frontend

1. Actualizar `frontend/lib/config/app_config.dart`:
   ```dart
   static const String baseUrl = 'https://tu-servicio.onrender.com/api';
   ```

2. Desplegar en Firebase Hosting, Netlify, o Vercel

