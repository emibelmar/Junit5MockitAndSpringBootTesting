package org.ebmdev.junitapp.ejemplos.models;

import org.ebmdev.junitapp.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class CuentaTest {
    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeAll //Añade cierta funcionalidad antes de ejecutar cualquier método de la clase
    static void beforeAll() {
        System.out.println("Inicializando el test.");
    }

    @AfterAll //Añade cierta funcionalidad después de ejecutar todos los métodos de la clase
    static void afterAll() {
        System.out.println("Finalizando el test.");
    }

    @BeforeEach
        //Añade cierta funcionalidad antes de ejecutar cada uno de los métodos de la clase test.
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Ebmdev", new BigDecimal("1000.00"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Iniciando el test.");
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName() + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
        //Añade cierta funcionalidad antes de ejecutar cada uno de los métodos de la clases test.
    void tearDown() {
        System.out.println("Finalizando el test.");
    }

    @Nested
    class CuentaBancoTest {
        @Test
        @DisplayName("Test para el nombre de la cuenta")
        void testNombreCuenta() {
            assertEquals("Ebmdev", cuenta.getPersona(), () -> "El nombre de la cuenta no es el esperado, se esperaba: Ebmdev, sin embargo fue: " + cuenta.getPersona());
        }

        @Test
        @DisplayName("Test para el saldo de la cuenta")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(cuenta.getSaldo().doubleValue(), 1000.00);
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Test para la referencia entre dos cuentas")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("1200.00"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("1200.00"));

            assertEquals(cuenta2, cuenta);
        }

        @Test
        @DisplayName("Test para el método debito() de la cuenta")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.00", cuenta.getSaldo().toPlainString());
        }

        @Test
        @DisplayName("Test para el método credito() de la cuenta")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.00", cuenta.getSaldo().toPlainString());
        }

        @Test
        @DisplayName("Test para la excepción DineroInsuficienteException")
        void testDineroInsuficienteExceptionCuenta() {
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.debito(new BigDecimal(1500));
            });
            assertEquals(exception.getMessage(), "Dinero Insuficiente");
        }

        @Test
        @DisplayName("Test para el método transferir() de la cuenta")
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
        @DisplayName("Test para la relación entre Cuenta y Banco")
            //@Disabled --  Esta anotación nos permite evitar la ejecución de algunos test de una clase si fuera necesario.
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

    @Nested
    class SOTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.MAC, OS.LINUX})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJDK8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_16)
        void testSoloJDK16() {
        }
    }

    @Nested
    class SystemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + " : " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
        void test64BitsArch() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "Emilio")
        void testUsernameProperty() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }

    }

    @Nested
    class EnvVariableTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> env = System.getenv();
            env.forEach((k, v) -> System.out.println(k + " : " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-16.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "4")
        void testNumeroProcesadores() {
        }

    }

    @Nested
    class CuentaNombreSaldoTest {
        @Test
        void testSaldoCuentaDev() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumeTrue(esDev);
            assertNotNull(cuenta.getSaldo());
            assertEquals(cuenta.getSaldo().doubleValue(), 1000.00);
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        void testSaldoCuentaDev2() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumingThat(esDev, () -> {
                        assertNotNull(cuenta.getSaldo());
                        assertEquals(cuenta.getSaldo().doubleValue(), 1000.00);
                        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
                        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
                    }
            );

        }
    }

    @Nested
    class RepeatedTests {
        @RepeatedTest(value = 5, name = "repetición {currentRepetition}/{totalRepetitions}")
        //Con esta anotación se repite el test tantas veces como se le pase por parámetros, esto solo tendría sentido cuando se generara algún parámetro de forma aleatoria dentro del metodo, como podría ser un Math.random();
        @DisplayName("Test para el método debito() de la cuenta")
        void testDebitoCuentaRepetir(RepetitionInfo info) {
            if (info.getCurrentRepetition() == 3) {
                System.out.println("Es la 3ª repetición");
            }
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.00", cuenta.getSaldo().toPlainString());
        }
    }

    @Tag("param")
    @Nested
    class ParameterizedTests {
        @ParameterizedTest(name = "nº {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "700", "900"})
        void testDebitoCuentaValueSource(String cantidad) {
            cuenta.debito(new BigDecimal(cantidad));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "nº {index} ejecutando con valor {0}")
        @CsvSource({"1,100", "2,200", "3,300", "4,700", "5,900"})
        void testDebitoCuentaCsvSource(String index, String cantidad) {
            System.out.println(index + " -> " + cantidad);
            cuenta.debito(new BigDecimal(cantidad));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "nº {index} ejecutando con valor {0}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String cantidad) {
            cuenta.debito(new BigDecimal(cantidad));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "nº {index} ejecutando con valor {0}")
        @MethodSource("cantidadList")
        void testDebitoCuentaMethodSource(String cantidad) {
            cuenta.debito(new BigDecimal(cantidad));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        private static List<String> cantidadList() {
            return Arrays.asList("100", "200", "300", "700", "900");
        }

        @ParameterizedTest(name = "nº {index} ejecutando con valor {0}")
        @CsvSource({"150,100", "250,200", "320,300", "780,700", "980,900"})
        void testDebitoCuentaCsvSource2(String saldo, String cantidad) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(cantidad));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "nº {index} ejecutando con valor {0}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String cantidad) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(cantidad));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Nested
    class timeOutTests {
        @Test
        @Timeout(1)
        void timeOutTest() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(950);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void timeOutTest2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(950);
        }

        @Test
        void timeOutAssertionsTest() {
            assertTimeout(Duration.ofMillis(1000l),() -> {
                TimeUnit.MILLISECONDS.sleep(950);
            } );
        }
    }
}