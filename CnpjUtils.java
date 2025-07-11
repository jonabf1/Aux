
/**
 * Utility class for validating Brazilian CNPJ numbers.
 *
 * <p>Compatible with JDK 17.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * boolean ok = CnpjUtils.isValid("04.252.011/0001-10");
 * }</pre>
 *
 * <p>Technical terms:</p>
 * <ul>
 *   <li>checksum – soma de verificação</li>
 *   <li>weight – peso</li>
 * </ul>
 */
public final class CnpjUtils {

    private CnpjUtils() {
        // Utility class; do not instantiate.
    }

    /**
     * Validates a CNPJ string.
     *
     * @param cnpj the CNPJ to validate (may contain punctuation)
     * @return {@code true} if the CNPJ is structurally valid, {@code false} otherwise
     */
    public static boolean isValid(String cnpj) {
        if (cnpj == null) return false;

        // 1) Remove everything that is not a digit (sanitização)
        String digits = cnpj.replaceAll("\\D", "");
        if (digits.length() != 14) return false;

        // 2) Reject trivial sequences like 00000000000000
        if (digits.chars().distinct().count() == 1) return false;

        // 3) Calculate check digits (dígitos verificadores)
        int dv1 = calcDigit(digits.substring(0, 12));
        int dv2 = calcDigit(digits.substring(0, 12) + dv1);

        // 4) Compare with the original DV
        return digits.equals(digits.substring(0, 12) + dv1 + dv2);
    }

    /**
     * Calculates a single check digit for CNPJ.
     *
     * @param base 12 or 13 digits without the DV(s)
     * @return the calculated check digit (0‑9)
     */
    private static int calcDigit(String base) {
        int[] weights = base.length() == 12
                ? new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
                : new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            int num = Character.getNumericValue(base.charAt(i));
            sum += num * weights[i];
        }
        int mod = sum % 11;
        return (mod < 2) ? 0 : 11 - mod;
    }

    // -------------------------------------------------------------
    // Optional helper methods
    // -------------------------------------------------------------

    /**
     * Removes any punctuation and pads with leading zeros until 14 digits.
     *
     * @param cnpj raw CNPJ string
     * @return a numeric string with exactly 14 digits
     */
    public static String normalize(String cnpj) {
        if (cnpj == null) return null;
        String digits = cnpj.replaceAll("\\D", "");
        return String.format("%014d", Long.parseUnsignedLong(digits));
    }

    /**
     * Quick CLI for manual testing:
     * {@code java CnpjUtils 04252011000110}
     */
    public static void main(String[] args) {
        String value = args.length > 0 ? args[0] : "04.252.011/0001-10";
        System.out.println(value + " -> " + isValid(value));
    }
}
