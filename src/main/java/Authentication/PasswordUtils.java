package Authentication;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class PasswordUtils {

    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtils() {
    }

    public static String hashPassword(String password) {

        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        byte[] hash = generateHash(
                password.toCharArray(),
                salt,
                ITERATIONS
        );

        return ITERATIONS
                + ":"
                + Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(
            String password,
            String storedPassword
    ) {

        try {
            String[] parts = storedPassword.split(":");

            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);

            byte[] salt =
                    Base64.getDecoder().decode(parts[1]);

            byte[] storedHash =
                    Base64.getDecoder().decode(parts[2]);

            byte[] enteredHash =
                    generateHash(
                            password.toCharArray(),
                            salt,
                            iterations
                    );

            if (storedHash.length != enteredHash.length) {
                return false;
            }

            int difference = 0;

            for (int i = 0; i < storedHash.length; i++) {
                difference |= storedHash[i] ^ enteredHash[i];
            }

            return difference == 0;

        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] generateHash(
            char[] password,
            byte[] salt,
            int iterations
    ) {

        try {
            PBEKeySpec specification =
                    new PBEKeySpec(
                            password,
                            salt,
                            iterations,
                            KEY_LENGTH
                    );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return factory
                    .generateSecret(specification)
                    .getEncoded();

        } catch (NoSuchAlgorithmException
                 | InvalidKeySpecException e) {

            throw new IllegalStateException(
                    "Unable to hash password",
                    e
            );
        }
    }
}