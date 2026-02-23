# Eliminar Constraint productos_tipo_check

## Problema
La base de datos tiene un constraint `productos_tipo_check` que limita los valores del campo `tipo` a los valores del enum antiguo. Esto impide insertar nuevos tipos dinámicos.

## Solución

### Opción 1: Ejecutar SQL directamente en Supabase

1. Ve a tu proyecto en Supabase Dashboard
2. Navega a **SQL Editor**
3. Ejecuta el siguiente comando:

```sql
ALTER TABLE productos DROP CONSTRAINT IF EXISTS productos_tipo_check;
```

### Opción 2: Ejecutar desde terminal (si tienes acceso directo)

```bash
psql -h db.amjsltygrftkcjoekczc.supabase.co -U postgres -d postgres -c "ALTER TABLE productos DROP CONSTRAINT IF EXISTS productos_tipo_check;"
```

### Opción 3: Usar el archivo SQL

El archivo `migration_remove_tipo_constraint.sql` contiene el comando necesario.

## Verificación

Después de ejecutar el comando, verifica que el constraint fue eliminado:

```sql
SELECT conname, conrelid::regclass, contype 
FROM pg_constraint 
WHERE conrelid = 'productos'::regclass 
AND conname = 'productos_tipo_check';
```

Si no devuelve resultados, el constraint fue eliminado correctamente.

## Importante

- Este cambio es necesario para permitir tipos dinámicos
- No afecta los datos existentes
- Es seguro ejecutarlo en producción

