package com.backup.tests;

import com.backup.Scanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScannerTest {

    @Test
    void hashFile() {
        String hash = Scanner.hashFile("_test_database.sqlite");
        assertEquals("d0616a263cc0fe4bb866eced2b0b60d3", hash);
    }
}