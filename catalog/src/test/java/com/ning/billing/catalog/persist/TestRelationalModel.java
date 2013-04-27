/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.catalog.persist;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.testng.annotations.Test;

import com.google.common.io.Resources;
import com.ning.billing.catalog.CatalogTestSuiteNoDB;
import com.ning.billing.catalog.DefaultPlan;
import com.ning.billing.catalog.StandaloneCatalog;
import com.ning.billing.catalog.VersionedCatalog;
import com.ning.billing.catalog.api.Plan;
import com.ning.billing.catalog.io.VersionedCatalogLoader;
import com.ning.billing.util.config.catalog.XMLLoader;

public class TestRelationalModel extends CatalogTestSuiteNoDB {

    @Test(enabled=true)
    public void testModel() throws Exception {
        
        // Start EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("helloworld");

        // First unit of work
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
       
        VersionedCatalog catalog = loader.load(Resources.getResource("versionedCatalog").toString());
        StandaloneCatalog cat2 = XMLLoader.getObjectFromString(Resources.getResource("SpyCarBasic.xml").toExternalForm(), StandaloneCatalog.class);
        StandaloneCatalog cat3 = XMLLoader.getObjectFromString(Resources.getResource("WeaponsHire.xml").toExternalForm(), StandaloneCatalog.class);
//        em.persist(catalog);
//        em.persist(cat2);
        em.persist(cat3);
        
        tx.commit();
        em.close();

        // Second unit of work
        EntityManager newEm = emf.createEntityManager();
        EntityTransaction newTx = newEm.getTransaction();
        newTx.begin();

        List plans = newEm.createQuery("select p from DefaultPlan p").getResultList();

        List catalogs = newEm.createQuery("select c from VersionedCatalog c").getResultList();

        System.out.println(plans.size() + " plan(s) found:");
        System.out.println(catalogs.size() + "versioned catalog(s) found:");
        
        

        for (Object p : plans) {
            DefaultPlan loadedPrice = (DefaultPlan) p;
            System.out.println(loadedPrice.getName());
        }

        newTx.commit();
        newEm.close();

        // Shutting down the application
        emf.close();
    }

}
