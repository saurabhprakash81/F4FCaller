package monsanto.b2b.F4FCaller.model;

public class PricePushResponse {

    private String messageId = null;

    private boolean updateAvailable = true;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }


}
