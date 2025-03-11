// src/main/java/com/siga/TestConexion.java

package com.siga;

import com.siga.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestConexion {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexion a la base de datos...");
        System.out.println("===============================================");

        try {
            // Prueba 1: Verificar conexión
            probarConexion();

            // Prueba 2: Verificar tablas existentes
            listarTablas();

            // Prueba 3: Insertar y consultar datos de prueba
            probarOperacionesCRUD();

        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarConexion() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            System.out.println("[OK] Conexion establecida exitosamente");
            System.out.println("URL: " + conn.getMetaData().getURL());
            System.out.println("Usuario: " + conn.getMetaData().getUserName());
            System.out.println("Version DB: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("===============================================");
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al conectar: " + e.getMessage());
        }
    }

    private static void listarTablas() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            System.out.println("Tablas encontradas en la base de datos:");
            int count = 0;
            while (rs.next()) {
                System.out.println("- " + rs.getString("TABLE_NAME"));
                count++;
            }
            System.out.println("Total de tablas: " + count);
            System.out.println("===============================================");
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al listar tablas: " + e.getMessage());
        }
    }

    private static void probarOperacionesCRUD() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Insertar un doctor de prueba
            String insertSQL = "INSERT INTO doctores (dni, nombre, apellido, matricula) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, "12345678");
                pstmt.setString(2, "Juan");
                pstmt.setString(3, "Prueba");
                pstmt.setString(4, "MAT123");
                pstmt.executeUpdate();
                System.out.println("[OK] Doctor de prueba insertado correctamente");
            }

            // Consultar el doctor insertado
            String selectSQL = "SELECT * FROM doctores WHERE dni = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, "12345678");
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Doctor encontrado:");
                    System.out.println("   DNI: " + rs.getString("dni"));
                    System.out.println("   Nombre: " + rs.getString("nombre"));
                    System.out.println("   Apellido: " + rs.getString("apellido"));
                    System.out.println("   Matricula: " + rs.getString("matricula"));
                }
            }

            // Eliminar el doctor de prueba
            String deleteSQL = "DELETE FROM doctores WHERE dni = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                pstmt.setString(1, "12345678");
                pstmt.executeUpdate();
                System.out.println("[OK] Doctor de prueba eliminado correctamente");
            }

            System.out.println("===============================================");
            System.out.println("[OK] Todas las pruebas CRUD completadas exitosamente");

        } catch (SQLException e) {
            System.err.println("[ERROR] Error en operaciones CRUD: " + e.getMessage());
        }
    }
}