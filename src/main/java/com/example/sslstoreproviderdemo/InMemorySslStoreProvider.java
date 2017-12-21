package com.example.sslstoreproviderdemo;

import org.springframework.boot.context.embedded.Ssl;
import org.springframework.boot.context.embedded.SslStoreProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;

public class InMemorySslStoreProvider implements SslStoreProvider {
    private final Ssl ssl;

    public InMemorySslStoreProvider(Ssl ssl) {
        this.ssl = ssl;
    }

    @Override
    public KeyStore getKeyStore() throws Exception {
        String encodedKeystore = "MIIKEwIBAzCCCcwGCSqGSIb3DQEHAaCCCb0Eggm5MIIJtTCCBWkGCSqGSIb3DQEHAaCCBVoEggVWMIIFUjCCBU4GCyqGSIb3DQEMCgECoIIE+zCCBPcwKQYKKoZIhvcNAQwBAzAbBBSap+UvIul6hCV32U2jSZElRHrK4QIDAMNQBIIEyIoH6HbkIwfWFmpdvrsMUrdaqDbEY9/8ZrPpjKZFyWs8gZ+SSv0LqkakkFZXkzOT+TO7CTPtuGTjQpV5OQx9g9LPbCqW7QrJYUFxs4CIBeza7kUpRiaBiDEA6TDoSDUKaDzenWezYgoymZ/OKwQCtwJ8khuzEFc+24qdE5qQA85Jeb4M5pazMsJfnlosrU+hb0/pdnnLK6khokUDT7Gu+5nHfDiEPKp3+2wkyRxIZAmQaKHuvhfGRYN1ptqdV3C1tZ0MjlWeZiK4PQi/FY2up84ggFkJCYkFfjEVA47zo8gOHfjDXfT8kCSCounZ1bCpCgfOJHezBheg1P8TLjLYacHCRIyFGpn/Nh2m4Yi2xhEK2AoZ3pvBogrRz+P/J49bc5i2lPDR/AUnsjyW3KraPd5UYfKvLavNpCTEHIOfjhPag4k96v3C8a4GJimAz8ksnkKsRg9gOUFLBdodjELdoP9cgHy86EDx3BUeN/PGjJHShDEe1MVKuZYevbb5bZ+uEAtQ0sGw2Wu31s67qLnKyycNYX4m9vS/+Reg7EmuTE5hkW+zsOI9xJw5Ch0AHScznOhGpF6Y+Grt/250OkXcOL8oKNGT79ZSpp7a0ypswJccU2L3l9PQ/X9XEC5P5BbB6dH8V1FEVokVmY+7NISSNkhFPkG+Fp8mgTHa0f9dLgeaW2GEo8OozL887lBpKW/N/Gvrk95Wd6r14/8qbYJLZGAuxcGVSBOWMKjOX/f+PnrZWinQhcupXWCn7w0mP2kexJ8lCT4y+5M393G+Gv+qG0dw9i9tJNBF8+H7TZMVVUas584luFvFh5xVQUuLjEb2FSjkiGH14lUWR6O4rUwrla7lNmBBeMYf/XsnIFNKJDHGbDkDRPtA8QZ5UJyToa5voT/FMAZHnmk5tQv/vOPp9yPKwKOHVfZSXwIg9cxi/Mh0lDZgM03eE+wxbZZQcWjzIwyHlKf5tviQvEG4aoNK01VJQy+uH9curYFosUDeo7k1XPSK7JHGvYH7EgJ/E91Foc3TnwVfY8F03E+5pM1tvSubJ6gUTZQFW5tWlmLVswkRCEZiY7aqrM3JqD5Z8RPD295/QJgJ4kQjNmAmOp9OGXZOxHlJmnmZZpwcZv4CBqsB4dvj9GfRQ0XVU2CQ0LEAcrtw863RmsEwCiTP07KRCtnVdteDWOD/4CxDZBuihVNYXlDsB5FWYyxSH6pUkcTX0x70KEJPMEUj2rzydLmX46luiHAPZqYwZNo8YbpGmelRjZnCJKkX8WvME93ovs+choXwXKb+8croe+Xr4w1qxO5DiRj2Ad10HsmXW4h2cFeWXMmuROCYbvFQdq9J1Ie4askZbkV9MveTvhglR1ot3lE7QfkqiwnvnZlmLVek5XDWWcoM2CzaJjQx557Z67Uk23wPaynnIbFUSmePQ0dTZ1b0t3DuzDHPSpY6IZnL6eOTciC7wkCHO+SvVi6tMOoprYCgHGycZci6tXfHAxMEUHil6at2JNASddKzPo56wT6phjLoqdedNAjArq3fK/pcxokOSA2uJ2RsSslHQhlHdb9CyDyQPjst0Q2I3gNuXVaZTQvxDlLFtOYE/o3In6k4TcCfNTYxsJEQwB1mOaxZysP9ejbkKT+0QjFAMBsGCSqGSIb3DQEJFDEOHgwAdABvAG0AYwBhAHQwIQYJKoZIhvcNAQkVMRQEElRpbWUgMTUxMzcyNDg3NDQ0NTCCBEQGCSqGSIb3DQEHBqCCBDUwggQxAgEAMIIEKgYJKoZIhvcNAQcBMCkGCiqGSIb3DQEMAQYwGwQUBMTY/M6VptD60fP0+gUsEmPSD2cCAwDDUICCA/Ba+BzCl7XmAlI33JSzidiHh9AHZwT581LTg7i7bwDV75xZ56h8PbeTeNKWdo13CquKc+rEXAH/8jolEqjMqBZPsYD424vsokvTFXaPd0z4cfM4inttqf+qrfCtQpdXHkKg2e4sFCvrOphZ8n2f0dVcuEgCp6KSbrm+osQa1TU4RWVhpR5sODCe+9L1s4UOvZhDWnFT38qj5zqvCa4poBdYWZ8TiijdB60UdclkxRoEJg8AxeFLMytkp3OLifaz9Q6lI8iorMBhiFsNOXnBTAkOb72aJUgVJ9EEM3yFeTG/7Co2/disWUW6VvlCO2J9IeNtWPf2DBSfbtIQKPdA0HM90W/MrpwuwtVZIMqjPVZiUVvyrjHjMuXsRhs18mDwBgFu8JY/Yy6lcq0Zof2O0Xf2+2ajSoHrbEfwwFF5zstCSaJpHf0JWTKis4Czw9wWG53Jkt2bUTNZaQWeMtnkIZ/OBc7dIRQmRKZZrr95l+X49QL+b6/f+Bp0HtIqmB5Ac8OPqrG9/MmSoJHAcUa/klc9/0GqRF3Fe/6wbwdkEsFmyB0zLpG2EfUMLEsSygprZcCav/fn0vZZR9V8vIlSfv/OlsQRX1xxciYZu9V3tlbOaIv+MapeuDSCoumv98Wur+9kr61WzO/rJbImp26IMRcRwQ8NMtqLSnBhE1aKt5I8HkEZ7opaGVWODFRLuhCElAa9XbXhy4RwGVFa1OxAwhqdAk0c5cdITU9rvhKH1TsJ6itU741/tFdTFEazqZRu7y9wIMD1L2g8p5z8I+aav9p+ZW84P92AAkA/0/5y6HqFBemtylvpjHi9ONwUCka5DfijRfwOL7JjqgMXhtVhro8aE0WPFO7MekcyxSO3UgMcrGvs9vI4xNNjT88ooDlNLA6T9c6pLaB8+QfXzGdz4DMn7wFCixCC4A2w1sD+gudak7ebDipjehLDKMtECddj+oF8c4EVUUXKt3dLgkY9CZ4irrSrFQaKr7bQqmTbcO7V6A0zl5hdGqQM+kDRnZDJCCSlTQIKYlTkZEblnYfbB9jqfs6uCf1hQ8p2+epkc/amBGhAIieF+QdgeRalRqr4LXuLk4WEaRvqX5bcJB6KDazVhLRLvY8Y+OCqJA1c+4ci0nv6fxljDia5kEb2NWE8We9u9ZOKd+LRvKUbrA7WUAAjq9ZqvMCLe9ob69Ftal8OwotUw05Z6bwaYZmoRZY5QUFrXnNxU9mL8ufQed2Snck2drnVURjOoKqQK6nUs/GCg/Nc4Pfh6ULXGSsdcFJArycP6/CXjnocXjoAzFGNOro3F9l6Rt7KzZAqwzBKv53kqvx/KLYa4Ssu6XxJp+cxlj0wPjAhMAkGBSsOAwIaBQAEFIF/iXzzEzY4RZXJO+5+Q4JZI0QkBBS/A0q2bHyh/tI5nSuhKAxdk6w9IAIDAYag";
        byte[] bytes = Base64.getDecoder().decode(encodedKeystore);

        return load(bytes, ssl.getKeyStoreType(), ssl.getKeyStorePassword());
    }

    @Override
    public KeyStore getTrustStore() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(System.getenv("JAVA_HOME"),"/jre/lib/security/cacerts"));
        return load(bytes, ssl.getTrustStoreType(), ssl.getTrustStorePassword());
    }

    private KeyStore load(byte[] bytes, String type, String password) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            KeyStore keyStore = KeyStore.getInstance(type);
            keyStore.load(stream, password.toCharArray());

            return keyStore;
        }
    }
}
