package monsanto.b2b.F4FCaller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monsanto.b2b.F4FCaller.model.PricePushRequest;
import monsanto.b2b.F4FCaller.model.PricePushResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.*;

@Component
public class PricePushService {

    @Autowired
    Environment environment;

    @Autowired
    VaultAccessClient vaultAccessClient;

    Connection db = null;
    PreparedStatement preparedStatement = null;

    private static final Logger logger = Logger.getLogger(PricePushService.class);


    public PricePushResponse processRequest(PricePushRequest pricePushRequest ){
        PricePushResponse pricePushResponse = new PricePushResponse();
        logger.debug("**logging ***");
        pricePushResponse = callAWSPostgress(pricePushRequest);
        return pricePushResponse;
    }

    public PricePushResponse callAWSPostgress(PricePushRequest pricePushRequest) {

        db = getDatabaseConnection();

        PricePushResponse pricePushResponse = new PricePushResponse();
        String result = null;
        //get the price requested date for the partner
        String filename = pricePushRequest.getDealer() + new java.sql.Timestamp((new java.util.Date()).getTime()).toString();
        String lastRequestedDate = null;
        String selectQuery = "select \"PRICE_REQUESTED_DATE\" from pricepush.\"PRICE_REQUEST_HISTORY\" where \"PARTNER_ID\" = '" + pricePushRequest.getDealer()+"'";
        String updatQuery = "Update pricepush.\"PRICE_REQUEST_HISTORY\" set \"PRICE_REQUESTED_DATE\" ='" + new java.sql.Timestamp((new java.util.Date()).getTime()) + "' , filename='" + filename  +"' where \"PARTNER_ID\" ='" +pricePushRequest.getDealer() + "'";
        logger.debug("*** updated Query whe check box checked *** " + updatQuery);
        String insertQuery = "INSERT INTO pricepush.\"PRICE_REQUEST_HISTORY\" (\"PARTNER_ID\",\"PRICE_REQUESTED_DATE\",\"filename\") VALUES (?,?,?)";
        try {
            preparedStatement = db.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                lastRequestedDate = resultSet.getTimestamp("PRICE_REQUESTED_DATE").toString();
                logger.debug("******* lastRequestedDate ********* " + lastRequestedDate);
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        //if user has requested for Update price in the UI , ie updateNeeded checkbox in the UI is checked
        if("true".equals(pricePushRequest.getUpdateNeeded())){
            logger.debug("******* Update Needed check box is checked ********* ");
            //if lastRequestedDate does not exist, return error Message to the UI, do not call F4F
            if(lastRequestedDate == null){
                logger.debug("******* error message back to UI ********* " );
                pricePushResponse.setUpdateAvailable(false);
                return pricePushResponse;
            }
            //If lastRequestedDate exist in the database, updated priceDate in pricePushRequest with this date and push to F4F
            pricePushRequest.setPriceDate(lastRequestedDate);
            //invoke the F4F serivce pushing the price UI data
            result = pushToF4F(pricePushRequest);
            //if the request is successfully received by F4F, update the table with current date time
            if(result != null){
                updatePriceHistoryTable(pricePushRequest,updatQuery,false);
                pricePushResponse.setMessageId(result);
            }
            //if user has not checked the updateNeeded checkbox in the UI , we need to push the request to F4F
        } else {
            result = pushToF4F(pricePushRequest);
            //if the request is successfully received by F4F, update the table with current date time
            if(result != null){
                if(lastRequestedDate == null){ // insert current date time for the partner as it does not exist yet
                    updatePriceHistoryTable(pricePushRequest,insertQuery,true);
                } else { //update price request date with current date time for the partner Id
                    updatePriceHistoryTable(pricePushRequest,updatQuery,false);
                }
                pricePushResponse.setMessageId(result);
            }
        }
        return pricePushResponse;

    }

    //method to get the databae connection to AWS Postgress
    private Connection getDatabaseConnection() {
        vaultAccessClient.initializeClient(environment.getProperty("dbCredentialsPath"));
        logger.debug("***Vault successfully connected and initalized");
        final String DB_NAME = "pricepush";
        final String DB_HOST_LOCAL = "localhost";
        final String DB_PORT_LOCAL = "6000";
        final String DB_URL_LOCAL = "jdbc:postgresql://" + DB_HOST_LOCAL + ":" + DB_PORT_LOCAL + "/" + DB_NAME + "?sslmode=require";
        final String DB_PASSWORD = vaultAccessClient.getPassword();
        final String DB_URL = vaultAccessClient.getUrl();
        final String DB_USER = vaultAccessClient.getUser();
        final String DB_PASSWD = new String(Base64.decodeBase64(DB_PASSWORD));

        try {
            Class.forName("org.postgresql.Driver");
            db = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            //db = DriverManager.getConnection(DB_URL_LOCAL, DB_USER, DB_PASSWD);
            logger.debug("***Connection successful ***");
        } catch (ClassNotFoundException | SQLException ex) {
            logger.debug(ex.getMessage());
        }

        return db;
    }

    //method to update or insert records into the price history table
    protected void updatePriceHistoryTable(PricePushRequest pricePushRequest,String query, boolean isInsert) {
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        String dealer = pricePushRequest.getDealer();
        String filename = dealer + timestamp.toString();
        try (PreparedStatement preparedStatement = db.prepareStatement(query)) {
            if(isInsert){
                preparedStatement.setString(1, dealer);
                preparedStatement.setTimestamp(2, timestamp);
                preparedStatement.setString(3,filename);
            }
            preparedStatement.executeUpdate();
        } catch (Exception sqle) {
            logger.debug("error is "+ sqle.getMessage());
        }
    }

    //method to push the price push message to F4F
    protected String pushToF4F(PricePushRequest pricePushRequest) {
        String url = environment.getProperty("f4f.pushservice");
        RestTemplate restTemplate = new RestTemplate();
        String result = null;
        ObjectMapper mapper = new ObjectMapper();
        if(pricePushRequest.getAcronymName() == null){
            pricePushRequest.setAcronymName("");
        }
        if(pricePushRequest.getPriceDate() == null){
            pricePushRequest.setPriceDate("");
        }
        if(pricePushRequest.getProductId() == null){
            pricePushRequest.setProductId("");
        }
        try {
            String jsonString = mapper.writeValueAsString(pricePushRequest);
            logger.debug("***** json request to F4F ***** " + jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        result = restTemplate.postForObject( url, pricePushRequest, String.class);
        return result;
    }
}

