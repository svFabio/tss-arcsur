package Vista;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Validador de entradas de texto basado en PlainDocument.
 * Controla caracteres permitidos y límites de longitud para campos Swing.
 * Compatible al 100% con tildes (á, é, í, ó, ú, ü) y eñe (ñ, Ñ) usando escapes Unicode.
 */
public class ValidadorDocumento extends PlainDocument {

    public enum Tipo {
        SOLO_NUMEROS,
        SOLO_LETRAS,
        ALFANUMERICO,
        EMAIL,
        TELEFONO,
        ENLACE,
        TEXTO_LIBRE
    }

    private final Tipo tipo;
    private final int limiteMaximo;

    public ValidadorDocumento(Tipo tipo, int limiteMaximo) {
        this.tipo = tipo;
        this.limiteMaximo = limiteMaximo;
    }

    public ValidadorDocumento(Tipo tipo) {
        this(tipo, 0);
    }

    // Métodos fábrica para facilidad de uso
    public static ValidadorDocumento soloNumeros(int limite) {
        return new ValidadorDocumento(Tipo.SOLO_NUMEROS, limite);
    }

    public static ValidadorDocumento soloLetras(int limite) {
        return new ValidadorDocumento(Tipo.SOLO_LETRAS, limite);
    }

    public static ValidadorDocumento alfanumerico(int limite) {
        return new ValidadorDocumento(Tipo.ALFANUMERICO, limite);
    }

    public static ValidadorDocumento email(int limite) {
        return new ValidadorDocumento(Tipo.EMAIL, limite);
    }

    public static ValidadorDocumento telefono(int limite) {
        return new ValidadorDocumento(Tipo.TELEFONO, limite);
    }

    public static ValidadorDocumento enlace(int limite) {
        return new ValidadorDocumento(Tipo.ENLACE, limite);
    }

    public static ValidadorDocumento textoLibre(int limite) {
        return new ValidadorDocumento(Tipo.TEXTO_LIBRE, limite);
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null || str.isEmpty()) {
            return;
        }

        // Filtrado estricto de caracteres según la regla establecida
        StringBuilder filtrado = new StringBuilder();
        int posActual = offset;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (esPermitido(c, posActual)) {
                filtrado.append(c);
                posActual++;
            }
        }

        if (filtrado.length() == 0) {
            return;
        }

        // Control de límite máximo sobre la longitud de caracteres válidos
        if (limiteMaximo > 0) {
            int disponible = limiteMaximo - getLength();
            if (disponible <= 0) {
                return;
            }
            if (filtrado.length() > disponible) {
                filtrado.setLength(disponible);
            }
        }

        super.insertString(offset, filtrado.toString(), attr);
    }

    private boolean esPermitido(char c, int pos) {
        switch (tipo) {
            case SOLO_NUMEROS:
                return (c >= '0' && c <= '9');

            case SOLO_LETRAS:
                return esLetraEspanol(c) || c == ' ';

            case TELEFONO:
                // Permite dígitos, espacios, guiones y '+' solo al inicio
                return (c >= '0' && c <= '9') || c == ' ' || c == '-' || (c == '+' && pos == 0);

            case ALFANUMERICO:
                // Letras (con tildes/ñ), números, espacios y puntuación empresarial usual
                return esLetraEspanol(c) || (c >= '0' && c <= '9') || c == ' '
                        || c == '.' || c == ',' || c == '-' || c == '/' || c == '&'
                        || c == '(' || c == ')' || c == '\'' || c == '"';

            case EMAIL:
                // Letras ASCII, números y símbolos permitidos en emails
                return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                        || (c >= '0' && c <= '9') || c == '@' || c == '.'
                        || c == '_' || c == '-' || c == '+';

            case ENLACE:
                // Para perfiles de LinkedIn o URLs completas
                return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                        || (c >= '0' && c <= '9') || c == ':' || c == '/'
                        || c == '.' || c == '-' || c == '_' || c == '~'
                        || c == '?' || c == '=' || c == '&' || c == '%' || c == '#';

            case TEXTO_LIBRE:
                // Para áreas de texto: letras (tildes/ñ), números, saltos y signos comunes
                if (c == '\n' || c == '\r' || c == '\t' || c == ' ') return true;
                if (esLetraEspanol(c) || (c >= '0' && c <= '9')) return true;
                String puntuacion = ".,;:!?()[]{}-_\"'/+#%*$&";
                return puntuacion.indexOf(c) >= 0;

            default:
                return true;
        }
    }

    /**
     * Valida si un carácter es una letra del español o una letra válida en Unicode.
     * Utiliza escapes Unicode para garantizar compatibilidad sin importar la codificación del archivo.
     */
    public static boolean esLetraEspanol(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        }
        // á, é, í, ó, ú, ü, ñ y sus mayúsculas en Unicode:
        switch (c) {
            case '\u00E1': case '\u00C1': // á, Á
            case '\u00E9': case '\u00C9': // é, É
            case '\u00ED': case '\u00CD': // í, Í
            case '\u00F3': case '\u00D3': // ó, Ó
            case '\u00FA': case '\u00DA': // ú, Ú
            case '\u00FC': case '\u00DC': // ü, Ü
            case '\u00F1': case '\u00D1': // ñ, Ñ
                return true;
            default:
                // Soporte para marcas combinadas (NFD) o variantes de letras
                return Character.isLetter(c) || Character.getType(c) == Character.NON_SPACING_MARK;
        }
    }
}
