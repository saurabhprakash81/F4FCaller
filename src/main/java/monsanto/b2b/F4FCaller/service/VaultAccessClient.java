package monsanto.b2b.F4FCaller.service;


//import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.vault.config.EnvironmentVaultConfiguration;

//import org.springframework.vault.authentication.AppRoleAuthentication;
//import org.springframework.vault.authentication.AppRoleAuthenticationOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.config.EnvironmentVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

/**
 * Created by evwob on 7/18/18.
 */
@PropertySource("vault.properties")
@Import(EnvironmentVaultConfiguration.class)
@Configuration
public class VaultAccessClient {

    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private EnvironmentVaultConfiguration configuration;

    private AppRoleAuthentication clientAuthentication;
    private VaultTemplate vaultTemplate;
    private VaultResponse vaultResponse;
    private String user;
    private String password;
    private String url;

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public void initializeClient(String oauthPath) {
        configuration.setApplicationContext(appContext);
        clientAuthentication = (AppRoleAuthentication)configuration.clientAuthentication();
        clientAuthentication.login();
        vaultTemplate = configuration.vaultTemplate();
        vaultResponse = vaultTemplate.read(oauthPath);
        user = (String)vaultResponse.getData().get("user");
        password = (String)vaultResponse.getData().get("password");
        url = (String)vaultResponse.getData().get("url");
    }




}
