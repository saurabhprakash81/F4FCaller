package monsanto.b2b.F4FCaller.model;

public class PricePushRequest {

    private String type = null;
    private String typeChoice = null;
    private String push = "";
    private String partnerType = "";
    private String businessId = "";
    private String validateZones = "";
    private String dealer = "";
    private String zoneID = "";
    private String year = "";
    private String productId = "";
    private String specieId = "";
    private String updateNeeded = "false";
    private String priceDate = "";
    private String acronymName = "";

    public String getUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(String updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public String getAcronymName() { return acronymName; }

    public void setAcronymName(String acronymName) {
        this.acronymName = acronymName;
    }

    public String getTypeChoice() {
        return typeChoice;
    }

    public void setTypeChoice(String typeChoice) {
        this.typeChoice = typeChoice;
    }

    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(String partnerType) {
        this.partnerType = partnerType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getValidateZones() {
        return validateZones;
    }

    public void setValidateZones(String validateZones) {
        this.validateZones = validateZones;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSpecieId() {
        return specieId;
    }

    public void setSpecieId(String specieId) {
        this.specieId = specieId;
    }

    public String getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
