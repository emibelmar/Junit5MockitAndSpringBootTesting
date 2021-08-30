package org.ebmdev.junitapp.ejemplos.models;

import org.ebmdev.junitapp.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Ebmdev", new BigDecimal("1000.00"));

        assertEquals("Ebmdev", cuenta.getPersona(), () -> "El nombre de la cuenta no es el esperado, se esperaba: Ebmdev, sin embargo fue: " + cuenta.getPersona());
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Ebmdev", new BigDecimal("1000.00"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(cuenta.getSaldo().doubleValue(), 1000.00);
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1200.00"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("1200.00"));

        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Ebmdev", new BigDecimal("1000.00"));
        cuenta.debito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.00", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Ebmdev", new BigDecimal("1000.00"));
        cuenta.credito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.00", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Ebmdev", new BigDecimal("1000.00"));

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        assertEquals(exception.getMessage(), "Dinero Insuficiente");
    }

    @Test
    void testTransferirDineroCuenta() {
        Cuenta cuentaOrigen = new Cuenta("Empresa", new BigDecimal("1200.00"));
        Cuenta cuentaDestino = new Cuenta("Ebmdev", new BigDecimal("1000.00"));

        Banco banco = new Banco();
        banco.setNombre("La Caixa");
        banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal(500));

        assertEquals("700.00", cuentaOrigen.getSaldo().toPlainString());
        assertEquals("1500.00", cuentaDestino.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuentaOrigen = new Cuenta("Empresa", new BigDecimal("1200.00"));
        Cuenta cuentaDestino = new Cuenta("Ebmdev", new BigDecimal("1000.00"));

        Banco banco = new Banco();
        banco.anadirCuenta(cuentaOrigen);
        banco.anadirCuenta(cuentaDestino);
        banco.setNombre("La Caixa");

        assertAll(() -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("La Caixa", cuentaOrigen.getBanco().getNombre()),
                () -> assertEquals("Ebmdev", banco.getCuentas().stream().filter(c -> c.getPersona().equals("Ebmdev")).findFirst().get().getPersona()),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Ebmdev")))
        );
    }
}