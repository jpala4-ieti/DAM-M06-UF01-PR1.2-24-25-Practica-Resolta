package com.project;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import static org.junit.jupiter.api.Assertions.*;

class PR124mainTest {

    @TempDir
    File tempDir;

    private PR124main gestor;

    @BeforeEach
    void setUp() {
        gestor = new PR124main();
        gestor.setFilePath(new File(tempDir, "PR124estudiants.dat").getAbsolutePath());
    }

    @Test
    void testAfegirIConsultarEstudiant() throws IOException {
        // Afegir un estudiant
        gestor.afegirEstudiantFitxer(1, "Estudiant Test", 8.5f);
        
        // Consultar l'estudiant afegit
        assertDoesNotThrow(() -> gestor.consultarNotaFitxer(1));
    }

    @Test
    void testLlistarEstudiants() throws IOException {
        // Afegir alguns estudiants
        gestor.afegirEstudiantFitxer(1, "Joan", 7.0f);
        gestor.afegirEstudiantFitxer(2, "Marta", 9.0f);

        // Capturar la sortida estàndard (System.out)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Llistar estudiants
            gestor.llistarEstudiantsFitxer();
        } finally {
            // Restaurar la sortida estàndard
            System.setOut(originalOut);
        }

        // Obtenir la sortida capturada com a String
        String output = outputStream.toString();

        // Verificar que la sortida conté els estudiants esperats
        assertTrue(output.contains("Registre: 1, Nom: Joan, Nota: 7.0"));
        assertTrue(output.contains("Registre: 2, Nom: Marta, Nota: 9.0"));
    }

    @Test
    void testConsultarEstudiantNoExistent() throws IOException {
        // Intentar consultar un estudiant que no existeix
        gestor.afegirEstudiantFitxer(1, "Joan", 7.0f);
        gestor.afegirEstudiantFitxer(2, "Marta", 9.0f);
        
        // Consultar un registre no existent
        assertDoesNotThrow(() -> gestor.consultarNotaFitxer(3));
    }

    @Test
    void testActualitzarNotaEstudiant() throws IOException {
        // Afegir un estudiant i actualitzar la seva nota
        gestor.afegirEstudiantFitxer(1, "Anna", 6.0f);
        gestor.actualitzarNotaFitxer(1, 9.5f);
        
        // Consultar per verificar l'actualització
        assertDoesNotThrow(() -> gestor.consultarNotaFitxer(1));
    }
    
    @Test
    void testMidaRegistreCorrecte() throws IOException {
        // Verificar que la mida de cada registre sigui correcta (48 bytes)
        File file = new File(gestor.getFilePath());
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            gestor.afegirEstudiantFitxer(1, "Test", 5.0f);
            assertEquals(48, raf.length());  // Verifica la mida del fitxer
        }
    }
}
