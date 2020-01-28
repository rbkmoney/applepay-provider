package com.rbkmoney.provider.applepay.store;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class APCertStore {
    private final Path identityCerDir;
    private final Path processingCerDir;
    private final String SUFFIX_P12 = ".p12";
    private final String SUFFIX_CER = ".cer";

    public APCertStore(String identityCerDir, String processingCerDir) {
        this.identityCerDir = Paths.get(identityCerDir);
        this.processingCerDir = Paths.get(processingCerDir);
    }

    public APCertStore(String baseDir) {
        this(Paths.get(baseDir, "identity").toString(), Paths.get(baseDir, "processing").toString());
    }

    public byte[] getIdentityCert(String merchantId) {
        return getCert(identityCerDir, merchantId, null, SUFFIX_P12);
    }

    public byte[] getProcessingKeyCert(String merchantId, String cerHash) {
        return getCert(processingCerDir, merchantId, cerHash, SUFFIX_P12);
    }

    public byte[] getProcessingCert(String merchantId, String cerHash) {
        return getCert(processingCerDir, merchantId, cerHash, SUFFIX_CER);
    }

    public File[] getMerchantCertsList() {
        return new File(processingCerDir.toString()).listFiles((folder1, name) -> name.endsWith(SUFFIX_P12));
    }

    private byte[] getCert(Path baseDir, String merchantId, String certHash, String suffix) {
        Path certPath = baseDir.resolve(buildCertFileName(merchantId, certHash, suffix));
        try {
            if (!Files.isRegularFile(certPath)) {
                certPath = baseDir.resolve(buildCertFileName(null, certHash, suffix));
                if (!Files.isRegularFile(certPath)) {
                    certPath = baseDir.resolve(buildCertFileName(merchantId, null, suffix));
                    if (!Files.isRegularFile(certPath)) {
                        certPath = baseDir.resolve(buildCertFileName(null, null, suffix));
                        if (!Files.isRegularFile(certPath)) {
                            return null;
                        }
                    }
                }
            }

            return Files.readAllBytes(certPath);
        } catch (IOException e) {
            throw new CertStoreException(e);
        }
    }

    private String buildCertFileName(String merchantId, String certHash, String suffix) {
        return (merchantId != null ? merchantId + (certHash != null ? "_" : "") : "") + (certHash != null ? certHash : "") + suffix;
    }

}
