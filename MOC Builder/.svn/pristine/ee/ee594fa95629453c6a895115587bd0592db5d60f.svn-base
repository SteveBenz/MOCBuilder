/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.data;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link PaymentDT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class PaymentDT {

    /**
     * The payment method for this order
     */
    private String method;

    /**
     * Currency code of the payment
     */
    private CurrencyT currency;

    /**
     * The time the buyer paid
     */
    private Date paid;

    /**
     * Payment status
     */
    private String status;

    public PaymentDT() {
        super();
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method;
    }

    @JsonProperty("currency_code")
    public CurrencyT getCurrency() {
        return currency;
    }

    @JsonProperty("currency_code")
    public void setCurrency(CurrencyT currency) {
        this.currency = currency;
    }

    @JsonProperty("date_paid")
    public Date getPaid() {
        return paid;
    }

    @JsonProperty("date_paid")
    public void setPaid(Date paid) {
        this.paid = paid;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }
}
