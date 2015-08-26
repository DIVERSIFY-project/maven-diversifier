package fr.inria.diversify

import spoon.Launcher
import spoon.processing.AbstractProcessor
import spoon.processing.Severity
import spoon.reflect.code.CtCase
import spoon.reflect.code.CtExpression
import spoon.reflect.code.CtLiteral
import spoon.reflect.declaration.*
import spoon.support.compiler.VirtualFile
import spoon.support.reflect.code.CtCodeSnippetExpressionImpl

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.Key
import java.security.SecureRandom

/**
 * Created by nicolas on 25/08/2015.
 */
public class EncryptLiteralProcessor extends AbstractProcessor<CtLiteral<String>> {


    private SecureRandom random = new SecureRandom();

    private String generateKey() {
        // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

        String pw = "";
        for (int i = 0; i < 16; i++) {
            int index = (int) (random.nextDouble() * letters.length());
            pw += letters.substring(index, index + 1);
        }
        return pw;
    }

    private static String crypt(String value, String key) throws Exception {
        Key aesKey = null;
        Cipher cipher = null;

        if (key == null || key.length() != 16) {
            throw new Exception("bad aes key configured");
        }
        if (aesKey == null) {
            aesKey = new SecretKeySpec(key.getBytes(), "AES");
            cipher = Cipher.getInstance("AES");
        }

        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return toHexString(cipher.doFinal(value.getBytes()));
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private String getDecryptMethod(Class type) {
        switch (type) {
            case byte: return "decryptByte";
            case short: return "decryptShort";
            case int: return "decryptInteger";
            case long: return "decryptLong";
            case float: return "decryptFloat";
            case double: return "decryptDouble";
            case boolean: return "decryptBoolean";
            case char: return "decryptChar";
            default: return "decrypt";
        }
    }

    @Override
    boolean isToBeProcessed(CtLiteral<String> candidate) {
        boolean toprocess = super.isToBeProcessed(candidate);

        // doesn't process null
        toprocess &= candidate.getValue() != null;
        // doesn't process literal without type (class reference)
        toprocess &= candidate.type != null

        // skip static in a non-static inner type
        try {
            toprocess &=
                    !(candidate.getParent(CtField.class).hasModifier(ModifierKind.STATIC) &&
                            !candidate.getParent(CtClass.class).hasModifier(ModifierKind.STATIC) &&
                            candidate.getParent(CtClass.class).getParent(CtClass.class) != null)
        } catch (Exception ex) {
        }

        // skip value for annotation type attribute
        try {
            toprocess &=
                    candidate.getParent(CtField.class) == null ||
                            candidate.getParent(CtAnnotationType.class) == null
        } catch (Exception ex) {
        }

        // skip for annotation attribute
        toprocess &= candidate.getParent(CtAnnotation.class) == null

        // skip for case expression
        try {
            toprocess &= candidate.getParent(CtCase.class).getCaseExpression() != candidate
        } catch (Exception ex) {
        }

        return toprocess;
    }

    @Override
    public void process(CtLiteral<String> ctLiteral) {
        try {
            String value = ctLiteral.getValue();
            String key = generateKey();

            String crypted = crypt(value, key);

            CtExpression replacement = new CtCodeSnippetExpressionImpl<>(value:
                    """new utils.Decryptor("$key").${getDecryptMethod(ctLiteral.type.actualClass)}("$crypted")""");

            // replace literal by crypted
            ctLiteral.replace(replacement);
        } catch (Exception ex) {
            getEnvironment().report(this, Severity.ERROR, ex.getClass().toString() + " in " + ctLiteral.getParent().getPosition().toString());
        }
    }

    @Override
    void processingDone() {
        String decryptor = getClass().getClassLoader().getResourceAsStream("utils/Decryptor.java").getText();

        Launcher launcher = new Launcher();
        launcher.addInputResource(new VirtualFile(decryptor, "utils/Decryptor.java"));
        launcher.buildModel();

        CtClass clazz = launcher.getFactory().Type().get("utils.Decryptor");
        getFactory().Package().getOrCreate("utils").addType(clazz);
    }
}
