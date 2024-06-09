package com.guroomsoft.icms.sap;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;

@Slf4j
@Component
public class JcoInMemoryDestinationDataProvider implements DestinationDataProvider {
    private DestinationDataEventListener eL;
    private HashMap<String, Properties> secureDBStorage=new HashMap<String, Properties>();

    @Override
    public Properties getDestinationProperties(String destinationName) throws DataProviderException {
        try
        {
            // read the destination from DB
            Properties p = secureDBStorage.get(destinationName);

            // check if all is correct
            if (p != null && p.isEmpty())
                throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION,
                        "destination configuration is incorrect", null);

            return p;
        } catch ( RuntimeException re ) {
            throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
        }
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        this.eL = eventListener;
    }

    /** implementation that saves the properties in memory */
    public void changeProperties(String destName, Properties properties)
    {
        synchronized (secureDBStorage)
        {
            if (properties==null)
            {
                if (secureDBStorage.remove(destName)!=null)
                    eL.deleted(destName);
            }
            else
            {
                secureDBStorage.put(destName, properties);
                eL.updated(destName);
            }
        }
    }

}
