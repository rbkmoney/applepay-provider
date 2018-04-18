package com.rbkmoney.provider.applepay.iface.decrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.payment_tool_provider.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.provider.applepay.domain.PaymentData;
import com.rbkmoney.provider.applepay.domain.PaymentDataKeys;
import com.rbkmoney.provider.applepay.domain.PaymentToken;
import com.rbkmoney.provider.applepay.domain.ValidationException;
import com.rbkmoney.provider.applepay.service.CertNotFoundException;
import com.rbkmoney.provider.applepay.service.CryptoException;
import com.rbkmoney.provider.applepay.service.DecryptionService;
import com.rbkmoney.provider.applepay.service.SignatureValidator;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vpankrashkin on 03.04.18.
 */
public class ProviderHandler implements PaymentToolProviderSrv.Iface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SignatureValidator validator;
    private final DecryptionService decryptionService;
    private final ObjectReader inReader = new ObjectMapper().readerFor(PaymentToken.class);
    private final ObjectReader outReader = new ObjectMapper().readerFor(PaymentDataKeys.class);

    public ProviderHandler(SignatureValidator validator, DecryptionService decryptionService) {
        this.validator = validator;
        this.decryptionService = decryptionService;
    }

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

            validator.validate(paymentData.getHeader(), paymentData.getData(), paymentData.getSignature(), paymentData.getVersion());
            log.info("Request successfully validated");

            String tokenData = decryptionService.decryptToken(payment_tool.getRequest().getApple().getMerchantId(), paymentToken);
            log.info("Payment data decrypted");
            PaymentDataKeys dataKeys = outReader.readValue(tokenData);

            UnwrappedPaymentTool result = new UnwrappedPaymentTool();
            result.setCardInfo(extractCardInfo(paymentToken, dataKeys));
            result.setPaymentData(extractPaymentData(dataKeys));
            result.setDetails(extractPaymentDetails(paymentToken, dataKeys));

            UnwrappedPaymentTool logResult = new UnwrappedPaymentTool(result);

            logResult.getPaymentData().setTokenizedCard(new TokenizedCard());
            log.info("Unwrap result: {}", logResult);
            return result;
        } catch (IOException e) {
            log.error("Failed to read json data", e);
            throw new InvalidRequest(Arrays.asList("Failed to read json data"));
        } catch (ValidationException e) {
            log.error("Failed to validate request", e);
            throw new InvalidRequest(Arrays.asList(e.getMessage()));
        } catch (CertNotFoundException e) {
            String message = String.format("Not found cert data for merchant: %s", payment_tool.getRequest().getApple().getMerchantId());
          log.warn(message, e);
          throw new InvalidRequest(Arrays.asList(message));
        } catch (CryptoException e) {
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
        cardInfo.setCardClass(TypeUtil.toEnumField(paymentToken.getPaymentMethod().getPaymentMethodType(), CardClass.class));
        cardInfo.setPaymentSystem(TypeUtil.toEnumField(paymentToken.getPaymentMethod().getPaymentNetwork(), BankCardPaymentSystem.class));
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
        } else throw new IllegalArgumentException("Unsupported auth type: " + paymentDataKeys.getAuthData().getClass().getName());

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
}
