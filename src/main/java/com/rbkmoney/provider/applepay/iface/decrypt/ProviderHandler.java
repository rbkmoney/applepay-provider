package com.rbkmoney.provider.applepay.iface.decrypt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.payment_tool_provider.Auth3DS;
import com.rbkmoney.damsel.payment_tool_provider.AuthData;
import com.rbkmoney.damsel.payment_tool_provider.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.provider.applepay.domain.*;
import com.rbkmoney.provider.applepay.service.CertNotFoundException;
import com.rbkmoney.provider.applepay.service.CryptoException;
import com.rbkmoney.provider.applepay.service.DecryptionService;
import com.rbkmoney.provider.applepay.service.SignatureValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class ProviderHandler implements PaymentToolProviderSrv.Iface {

    private final SignatureValidator validator;
    private final DecryptionService decryptionService;
    private final ObjectReader inReader = new ObjectMapper().readerFor(PaymentToken.class).with(DeserializationFeature.UNWRAP_ROOT_VALUE);
    private final ObjectReader outReader = new ObjectMapper() {{
        registerModule(new SimpleModule() {{
            addDeserializer(com.rbkmoney.provider.applepay.domain.AuthData.class, new AuthDeserializer());
        }});
    }}.registerModule(new JavaTimeModule()).readerFor(PaymentDataKeys.class);
    private final boolean enableValidation;

    @Override
    public UnwrappedPaymentTool unwrap(WrappedPaymentTool payment_tool) throws InvalidRequest, TException {
        log.info("New unwrap request: {}", payment_tool);
        Content content = payment_tool.getRequest().getApple().getPaymentToken();
        if (!content.getType().equalsIgnoreCase("application/json")) {
            throw new InvalidRequest(Arrays.asList("Wrong content type"));
        }

        try {
            PaymentToken paymentToken = inReader.readValue(content.getData());
            PaymentData paymentData = paymentToken.getPaymentData();

            if (enableValidation) {
                validator.validate(paymentData.getHeader(), paymentData.getData(), paymentData.getSignature(), paymentData.getVersion());
                log.info("Request successfully validated");
            } else {
                log.info("Request validation skipped");
            }

            String tokenData = decryptionService.decryptToken(payment_tool.getRequest().getApple().getMerchantId(), paymentToken);
            log.info("Payment data decrypted");
            PaymentDataKeys dataKeys = outReader.readValue(tokenData);

            validate(dataKeys);

            UnwrappedPaymentTool result = new UnwrappedPaymentTool();
            result.setCardInfo(extractCardInfo(paymentToken, dataKeys));
            result.setPaymentData(extractPaymentData(dataKeys));
            result.setDetails(extractPaymentDetails(paymentToken, dataKeys));

            UnwrappedPaymentTool logResult = new UnwrappedPaymentTool(result);

            logResult.getPaymentData().setTokenizedCard(new TokenizedCard("***", null, result.getPaymentData().getTokenizedCard().getAuthData()));
            log.info("Unwrap result: {}", logResult);
            return result;
        } catch (IOException e) {
            log.error("Failed to read json data: {}", e.getMessage().replaceAll("([^\\d])\\d{8,19}([^\\d])", "$1***$2"));
            throw new InvalidRequest(Arrays.asList("Failed to read json data"));
        } catch (ValidationException e) {
            log.error("Failed to validate request", e);
            throw new InvalidRequest(Arrays.asList(e.getMessage()));
        } catch (CertNotFoundException e) {
            String message = String.format("Not found cert data for merchant: %s", payment_tool.getRequest().getApple().getMerchantId());
            log.warn(message, e);
            throw new InvalidRequest(Arrays.asList(message));
        } catch (CryptoException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException | KeyStoreException e) {
            log.error("Decryption error", e);
            throw new RuntimeException(e);
        }
    }

    private CardInfo extractCardInfo(PaymentToken paymentToken, PaymentDataKeys paymentDataKeys) {
        CardInfo cardInfo = new CardInfo();

        cardInfo.setDisplayName(paymentToken.getPaymentMethod().getDisplayName());
        cardInfo.setCardholderName(paymentDataKeys.getCardholderName());
        if (cardInfo.isSetDisplayName()) {
            Matcher matcher = Pattern.compile(".+\\s+(\\d{4})").matcher(cardInfo.getDisplayName());
            if (matcher.find()) {
                cardInfo.setLast4Digits(matcher.group(1));
            }
        }
        cardInfo.setCardClass(TypeUtil.toEnumField(
                Optional.ofNullable(paymentToken.getPaymentMethod().getPaymentMethodType()).map(s -> s.toLowerCase()).orElse(null),
                CardClass.class, CardClass.unknown));
        cardInfo.setPaymentSystem(TypeUtil.toEnumField(
                Optional.ofNullable(paymentToken.getPaymentMethod().getPaymentNetwork()).map(s -> s.toLowerCase()).orElse(null),
                BankCardPaymentSystem.class, null));
        return cardInfo;
    }

    private CardPaymentData extractPaymentData(PaymentDataKeys paymentDataKeys) {
        CardPaymentData cardPaymentData = new CardPaymentData();
        TokenizedCard tokenizedCard = new TokenizedCard();
        tokenizedCard.setDpan(paymentDataKeys.getAppPrimaryAccountNumber());
        tokenizedCard.setExpDate(new ExpDate(
                (byte) paymentDataKeys.getAppExpirationDate().getMonth().getValue(),
                (short) paymentDataKeys.getAppExpirationDate().getYear()
        ));
        if (paymentDataKeys.getAuthData() instanceof com.rbkmoney.provider.applepay.domain.Auth3DS) {
            com.rbkmoney.provider.applepay.domain.Auth3DS auth3DSData = (com.rbkmoney.provider.applepay.domain.Auth3DS) paymentDataKeys.getAuthData();
            Auth3DS auth3DS = new Auth3DS(auth3DSData.getCryptogram());
            auth3DS.setEci(auth3DSData.getEci());
            tokenizedCard.setAuthData(AuthData.auth_3ds(auth3DS));
        } else
            throw new IllegalArgumentException("Unsupported auth type: " + paymentDataKeys.getAuthData().getClass().getName());

        cardPaymentData.setTokenizedCard(tokenizedCard);
        return cardPaymentData;
    }

    private PaymentDetails extractPaymentDetails(PaymentToken paymentToken, PaymentDataKeys paymentDataKeys) {
        ApplePayDetails payDetails = new ApplePayDetails(
                paymentToken.getTransactionId(),
                paymentDataKeys.getTransactionAmount(),
                Short.parseShort(paymentDataKeys.getCurrencyCode()),
                paymentDataKeys.getDevManufacturerIdentifier()
        );
        return PaymentDetails.apple(payDetails);
    }

    private void validate(PaymentDataKeys dataKeys) throws IOException {
        switch (dataKeys.getAuthType()) {
            case Auth3DS:
                if (!(dataKeys.getAuthData() instanceof com.rbkmoney.provider.applepay.domain.Auth3DS)) {
                    throw new IOException("Wrong json data type:" + dataKeys.getAuthData());
                }
                break;
            case AuthEMV:
                if (!(dataKeys.getAuthData() instanceof AuthEMV)) {
                    throw new IOException("Wrong json data type:" + dataKeys.getAuthData());
                }
        }
    }

    private static class AuthDeserializer extends StdDeserializer<com.rbkmoney.provider.applepay.domain.AuthData> {
        public AuthDeserializer() {
            super(AuthData.class);
        }

        @Override
        public com.rbkmoney.provider.applepay.domain.AuthData deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            TreeNode node = jp.readValueAsTree();

            JsonParser tdsParser = node.traverse(ctxt.getParser().getCodec());

            JavaType tdsType = ctxt.getTypeFactory().constructType(com.rbkmoney.provider.applepay.domain.Auth3DS.class);
            JsonDeserializer tdsDes = ctxt.findRootValueDeserializer(tdsType);

            try {
                tdsParser.nextToken();
                return (com.rbkmoney.provider.applepay.domain.Auth3DS) tdsDes.deserialize(tdsParser, ctxt);
            } catch (IOException e) {
                JsonParser emvParser = node.traverse(ctxt.getParser().getCodec());
                emvParser.nextToken();
                JavaType emvType = ctxt.getTypeFactory().constructType(com.rbkmoney.provider.applepay.domain.AuthEMV.class);
                JsonDeserializer emvDes = ctxt.findRootValueDeserializer(emvType);
                return (com.rbkmoney.provider.applepay.domain.AuthEMV) emvDes.deserialize(emvParser, ctxt);

            }
        }
    }
}
