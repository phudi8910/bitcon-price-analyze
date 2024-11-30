package myapp.bitcoin_analyze.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import myapp.bitcoin_analyze.service.dto.RequestDTO;
import myapp.bitcoin_analyze.service.impl.BitcoinAnalyzeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Base64;

@Service
public class Encryptor {
    private final Logger log = LoggerFactory.getLogger(BitcoinAnalyzeServiceImpl.class);
    @Value("${aes.secretkey}")
    public String SECRET_KEY;

    @Autowired
    WebConfigurer webConfigurer;

    public String aesDecrypt(String encryptedData) throws Exception {
        byte[] cipherData = Base64.getDecoder().decode(encryptedData);
        byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        final byte[][] keyAndIV = aesGenerateKeyAndIV(32, 16, 1, saltData, SECRET_KEY.getBytes(StandardCharsets.UTF_8), md5);
        SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
        IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

        byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = aesCBC.doFinal(encrypted);
        return  new String(decryptedData, StandardCharsets.UTF_8);
    }

    public RequestDTO isAesMessageValid(String encryptedData){
        try{
            String[] decryptedMessages= aesDecrypt(encryptedData).split("\\|");
            LocalDate requestDate= LocalDate.parse(decryptedMessages[1]);
            LocalTime requestTime = LocalTime.parse(decryptedMessages[2]);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // Tùy chọn: Tắt chuyển đổi tự động sang timestamps
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Tùy chọn: Loại trừ các trường null
            JavaTimeModule module = new JavaTimeModule();
            mapper.registerModule(module);

            RequestDTO requestDTO = mapper.readValue(decryptedMessages[3], RequestDTO.class);
            if(requestDTO.getRequestDate().isEqual(requestDate) && requestDTO.getRequestTime().equals(requestTime)){
                return requestDTO;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
        return null;
    }

    public String trimStringByString(String text, String trimBy) {
        int beginIndex = 0;
        int endIndex = text.length();

        while (text.substring(beginIndex, endIndex).startsWith(trimBy)) {
            beginIndex += trimBy.length();
        }

        while (text.substring(beginIndex, endIndex).endsWith(trimBy)) {
            endIndex -= trimBy.length();
        }

        return text.substring(beginIndex, endIndex);
    }

    public byte[][] aesGenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md)  {
        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();
            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte)0);
        }
    }
}
