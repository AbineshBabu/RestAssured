package config;

import java.util.Map;

public final class ServiceRegistry {

    private ServiceRegistry(){}

    public static String getBaseUrl(String serviceName){

        String serviceNameNormalizied = serviceName.toLowerCase();
        String key = serviceNameNormalizied+".baseUrl";

        try{
            return  ConfigManager.get(key);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No baseUrl configured for "+serviceName+" in "+
                    ConfigManager.getEnv());
        }

    }
}
