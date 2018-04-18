package com.rbkmoney.provider.applepay.service;

import com.rbkmoney.provider.applepay.domain.PaymentHeader;
import com.rbkmoney.provider.applepay.domain.ValidationException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.*;
import java.util.*;

import static org.bouncycastle.util.encoders.Base64.decode;

public class SignatureValidator {

    private static final String APPLE_ROOT_CA_G3_CER = "AppleRootCA-G3.cer";
    private static final String LEAF_OID = "1.2.840.113635.100.6.29";
    private static final String INTERMEDIATE_OID = "1.2.840.113635.100.6.2.14";
    private static final String PKIX = "PKIX";
    private static final String COLLECTION = "Collection";
    private static final String X_509 = "X.509";
    private static final String ECC_TYPE = "EC_v1";
    private static final String RSA_TYPE = "RSA_v1";
    private static final String BC = "BC";

    static {
        if (Security.getProvider(BC) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final long expirationInMs;
    private final byte[] appleRootCACert;

    public SignatureValidator(byte[] appleRootCACert, long expirationInMs) {
        this.appleRootCACert = appleRootCACert;
        this.expirationInMs = expirationInMs;
    }

    public void validate(PaymentHeader applePayHeader, String applePayData,
                                String applePaySignature, String version) throws ValidationException {
        try {
            boolean eccSign = isECC(version);

            byte[] signedData = eccSign ? getECCSignedData(applePayData, applePayHeader) : getRSASignedData(applePayData, applePayHeader);

            CMSProcessable signedDataPrc = new CMSProcessableByteArray(signedData);
            CMSSignedData cmsSignedData = new CMSSignedData(signedDataPrc, decode(applePaySignature));

            Store store = cmsSignedData.getCertificates();
            ArrayList<X509CertificateHolder> allCertificates = (ArrayList<X509CertificateHolder>) store.getMatches(null);
            SignerInformation signerInformation = (SignerInformation) cmsSignedData.getSignerInfos().getSigners().stream().findFirst().orElseThrow(() -> new ValidationException("Cannot extract siner info"));

            List<X509Certificate> x509Certificates = new ArrayList<>();
            for (X509CertificateHolder certificate : allCertificates) {
                x509Certificates.add(new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificate));
            }

            // step 1:
            // Ensure that the certificates contain the correct custom OIDs: 1.2.840.113635.100.6.29
            // for the leaf certificate and 1.2.840.113635.100.6.2.14 for the intermediate CA. The value for these marker OIDs doesn’t matter, only their presence.
            validateCustomData(allCertificates);

            X509Certificate appleRootCertificate;
            // step 2:
            // Ensure that the root CA is the Apple Root CA - G3. This certificate is available from apple.com/certificateauthority.
            try (InputStream inputStream = getRootCA()) {
                CertificateFactory certificateFactory = CertificateFactory.getInstance(X_509);
                appleRootCertificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            }

            // step 3:
            // Ensure that there is a valid X.509 chain of trust from the signature to the root CA. Specifically,
            // ensure that the signature was created using the private key corresponding to the leaf certificate,
            // that the leaf certificate is signed by the intermediate CA, and that the intermediate CA is signed by the Apple Root CA - G3.
            validateCertificate(x509Certificates.get(0), appleRootCertificate, x509Certificates);

            // step 4:
            // Ensure that the signature is:
            // - a valid ECDSA signature (ecdsa-with-SHA256 1.2.840.10045.4.3.2) of the
            // concatenated values of the ephemeralPublicKey, data, transactionId, and applicationData keys.
            // - a valid RSA signature (RSA-with-SHA256 1.2.840.113549.1.1.11) of the
            //concatenated values of the wrappedKey, data, transactionId, and applicationData keys.
            validateECCSignature(signerInformation, store);

            // step 5:
            // Inspect the CMS signing time of the signature, as defined by section 11.3 of RFC 5652.
            // If the time signature and the transaction time differ by more than a few minutes, it's possible that the token is a replay attack.
            if (expirationInMs >= 0) {
                validateSignatureTime(expirationInMs, signerInformation);
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ValidationException("Unknown validation error:"+t.getMessage(), t);
        }
    }

    private InputStream getRootCA() {
        return new ByteArrayInputStream(appleRootCACert);
    }

    private static boolean isECC(String type) {
        if (ECC_TYPE.equals(type)) {
            return true;
        } else if (RSA_TYPE.equals(type)) {
            return false;
        } else {
            throw new IllegalArgumentException("Unknown signature type: " + type);
        }
    }

    private static void validateCustomData(ArrayList<X509CertificateHolder> allCertificates) throws ValidationException {
        if (allCertificates.size() != 2) {
            throw new ValidationException("Signature certificates count expected 2, but it's :" + allCertificates.size());
        }
        if (allCertificates.get(0).getExtension(new ASN1ObjectIdentifier(LEAF_OID)) == null) {
            throw new ValidationException("Leaf certificate doesn't have extension: " + LEAF_OID);
        }
        if (allCertificates.get(1).getExtension(new ASN1ObjectIdentifier(INTERMEDIATE_OID)) == null) {
            throw new ValidationException("Intermediate certificate doesn't have extension: " + INTERMEDIATE_OID);
        }
    }

    private static void validateECCSignature(SignerInformation signerInformation, Store store) throws ValidationException {
        try {
            ArrayList certCollection = (ArrayList) store.getMatches(signerInformation.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
            X509Certificate cert = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certHolder);
            signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(BC).build(cert));
        } catch (Exception e) {
            throw new ValidationException("Failed to verify apple pay ECC signature.", e);
        }
    }

    private static void validateSignatureTime(long applePaySignatureExpirationInMs, SignerInformation signerInformation) throws ValidationException {
        long signDate = 0;
        AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        Attribute signingTime = signedAttributes.get(CMSAttributes.signingTime);
        Enumeration signingTimeObjects = signingTime.getAttrValues().getObjects();
        try {
            if (signingTimeObjects.hasMoreElements()) {
                Object signingTimeObject = signingTimeObjects.nextElement();
                if (signingTimeObject instanceof ASN1UTCTime) {
                    ASN1UTCTime asn1Time = (ASN1UTCTime) signingTimeObject;
                    signDate = asn1Time.getDate().getTime();
                }
            }
        } catch (Exception e) {
            throw new ValidationException("Failed to extract sign time from apple pay signature.", e);
        }
        if (signDate == 0) {
            throw new ValidationException("Failed to extract sign time from apple pay signature [ASN1UTCTime not found].");
        }
        long expiration = System.currentTimeMillis() - applePaySignatureExpirationInMs;
        if (expiration > signDate) {
            throw new ValidationException(String.format("Apple pay signature is too old: %s, the expiration time is: %s ms.", signDate, applePaySignatureExpirationInMs));
        }
    }

    private static void validateCertificate(X509Certificate leafCertificate, X509Certificate trustedRootCert,
                                            List<X509Certificate> intermediateCerts) throws ValidationException {
        try {
            // Create the selector that specifies the starting certificate
            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(leafCertificate);

            // Create the trust anchors (set of root CA certificates)
            Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
            trustAnchors.add(new TrustAnchor(trustedRootCert, null));

            // Configure the PKIX certificate builder algorithm parameters
            PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAnchors, selector);

            // Disable CRL checks (this is done manually as additional step)
            pkixParams.setRevocationEnabled(false);

            // Specify a list of intermediate certificates
            CertStore intermediateAPCertStore = CertStore.getInstance(COLLECTION, new CollectionCertStoreParameters(intermediateCerts), BC);
            pkixParams.addCertStore(intermediateAPCertStore);

            // Build and verify the certification chain
            CertPathBuilder builder = CertPathBuilder.getInstance(PKIX, BC);

            //If no exception thrown, it means the validation passed.
            PKIXCertPathBuilderResult pkixCertPathBuilderResult = (PKIXCertPathBuilderResult) builder.build(pkixParams);

        } catch (Exception e) {
            throw new ValidationException("Failed to validate chain of trust for apple certificates.", e);
        }
    }

    private static byte[] getRSASignedData(String applePayData, PaymentHeader applePayHeader) throws IOException {
        byte[] wrappedKeyBytes = decode(applePayHeader.getWrappedKey());
        byte[] applePayDataBytes = decode(applePayData);
        byte[] transactionIdBytes = Hex.decode(applePayHeader.getTransactionId());
        byte[] applicationDataBytes = null;
        if (!(applePayHeader.getApplicationData() == null || applePayHeader.getApplicationData().length() == 0)) {
            applicationDataBytes = decode(applePayHeader.getApplicationData());
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(wrappedKeyBytes);
        byteArrayOutputStream.write(applePayDataBytes);
        byteArrayOutputStream.write(transactionIdBytes);
        if (applicationDataBytes != null) {
            byteArrayOutputStream.write(applicationDataBytes);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] getECCSignedData(String applePayData, PaymentHeader applePayHeader) throws IOException {
        byte[] ephemeralPublicKeyBytes = decode(applePayHeader.getEphemeralPublicKey());
        byte[] applePayDataBytes = decode(applePayData);
        byte[] transactionIdBytes = Hex.decode(applePayHeader.getTransactionId());
        byte[] applicationDataBytes = null;
        if (!(applePayHeader.getApplicationData() == null || applePayHeader.getApplicationData().length() == 0)) {
            applicationDataBytes = decode(applePayHeader.getApplicationData());
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(ephemeralPublicKeyBytes);
        byteArrayOutputStream.write(applePayDataBytes);
        byteArrayOutputStream.write(transactionIdBytes);
        if (applicationDataBytes != null) {
            byteArrayOutputStream.write(applicationDataBytes);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
