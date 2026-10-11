import java.util.Scanner;

public class FactorizadorDeMatrices {
    // Umbral para considerar un pivote como cero (evita divisiones por valores muy pequeños)
    private static final double EPSILON = 1e-12;

    // main coordina lectura, cálculo LU y presentación; args admite argumentos de inicio, aquí no utilizados.
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        double[][] A = new double[3][3];
        double[][] L = {{1,0,0},{0,1,0},{0,0,1}};
        double[][] U = new double[3][3];

        System.out.println("FACTORIZACIÓN LU 3x3 (Doolittle)\nIngrese matriz A:");

        // Los bucles visitan filas y columnas; se comparte el lector para capturar los nueve valores.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                A[i][j] = leerNumero(sc, "  a" + (i+1) + (j+1) + " = ");
            }
        }

        for (int j = 0; j < 3; j++) U[0][j] = A[0][j];
        // El pivote se usa como divisor; si es cero (o casi cero), el cálculo se detiene para evitar divisiones inválidas.
        if (Math.abs(U[0][0]) < EPSILON) { System.out.println("Pivote 1 es cero (o casi cero)."); return; }

        // Doolittle calcula multiplicadores de L dividiendo elementos de A entre el pivote actual de U.
        L[1][0] = A[1][0] / U[0][0];
        L[2][0] = A[2][0] / U[0][0];
        // Se resta la contribución ya conocida de la primera fila para obtener los valores restantes de U.
        U[1][1] = A[1][1] - L[1][0] * U[0][1];
        U[1][2] = A[1][2] - L[1][0] * U[0][2];
        // El segundo pivote también será divisor; si vale cero (o casi cero), esta factorización no puede continuar.
        if (Math.abs(U[1][1]) < EPSILON) { System.out.println("Pivote 2 es cero (o casi cero)."); return; }

        // Estas fórmulas despejan posiciones desconocidas de LU=A usando los valores calculados previamente.
        L[2][1] = (A[2][1] - L[2][0] * U[0][1]) / U[1][1];
        U[2][2] = A[2][2] - L[2][0] * U[0][2] - L[2][1] * U[1][2];

        // El producto reconstruye A y permite comprobar visualmente si la factorización obtenida es correcta.
        double[][] C = multiplicar(L, U);
        imprimir("A", A); imprimir("L", L); imprimir("U", U); imprimir("L×U", C);

        sc.close(); // Cierra el Scanner para liberar recursos
    }

    // sc reutiliza la entrada del teclado; prompt determina el mensaje mostrado antes de leer.
    static double leerNumero(Scanner sc, String prompt) {
        // El bucle insiste hasta que una entrada válida ejecuta return; ante error, vuelve a pedir el número.
        while (true) {
            System.out.print(prompt);
            // Se lee la línea completa, se quitan espacios y se aceptan comas como separadores decimales.
            String s = sc.nextLine().replace(" ", "").replace(',', '.');
            try {
                // Si aparece una barra, separa numerador y denominador, los convierte y devuelve su división.
                if (s.contains("/")) {
                    String[] p = s.split("/");
                    return Double.parseDouble(p[0]) / Double.parseDouble(p[1]);
                }
                // Sin barra, convierte directamente el texto a número decimal y termina el método.
                return Double.parseDouble(s);
            // Si la conversión falla, muestra el aviso y el bucle permite intentarlo nuevamente.
            } catch (Exception e) {
                System.out.println("  Error: use 5, 2.5, 3/4, -7/3");
            }
        }
    }

    // A y B son matrices recibidas como parámetros; C almacena y devuelve su producto.
    static double[][] multiplicar(double[][] A, double[][] B) {
        double[][] C = new double[3][3];
        // i, j y k recorren filas, columnas y términos que se suman para cada posición del producto.
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }

    // nombre etiqueta la matriz; M es la matriz recibida y void indica que el método no devuelve valor.
    static void imprimir(String nombre, double[][] M) {
        System.out.println("\n" + nombre + ":");
        // Este for-each obtiene cada fila completa de M sin manejar manualmente su índice.
        for (double[] fila : M) {
            System.out.print("  [");
            // Cada valor se convierte a texto y printf reserva ocho espacios mínimos para alinear columnas.
            for (double v : fila) System.out.printf(" %8s", formatear(v));
            System.out.println(" ]");
        }
    }

    // Busca una fracción aproximada con denominador máximo 100 y devuelve texto listo para imprimir.
    static String formatear(double v) {
        for (int d = 1; d <= 100; d++) {
            // n aproxima el numerador; la tolerancia acepta pequeñas diferencias propias de cálculos decimales.
            long n = Math.round(v * d);
            if (Math.abs(v * d - n) < 1e-4)
                // Para denominador uno imprime entero; en los demás casos representa el valor como fracción.
                return d == 1 ? String.valueOf(n) : n + "/" + d;
        }
        // Si no encuentra una fracción cercana, muestra el valor redondeado a cuatro decimales.
        return String.format("%.4f", v);
    }
}