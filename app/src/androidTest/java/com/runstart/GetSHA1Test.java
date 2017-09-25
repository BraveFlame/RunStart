package com.runstart;

import android.support.test.InstrumentationRegistry;

import com.runstart.help.GetSHA1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by user on 17-9-6.
 */
public class GetSHA1Test {

    @Before
    public void setUp() throws Exception {


    }

    @After
    public void tearDown() throws Exception {


    }


    //debug--SHA1
    @Test
    public void getCertificateSHA1Fingerprint02() throws Exception {
        String SHA1= GetSHA1.getCertificateSHA1Fingerprint(InstrumentationRegistry.getContext());
        assertEquals(SHA1,"80:02:E3:70:C9:E0:9C:58:76:1D:0D:7C:4E:6D:AB:70:91:99:94:06");
    }

    //SHA1
    @Test
    public void getCertificateSHA1Fingerprint() throws Exception {
        String SHA1=GetSHA1.getCertificateSHA1Fingerprint(InstrumentationRegistry.getContext());
        assertEquals(SHA1," B7:58:C9:08:F7:36:43:F2:EE:E7:B8:0F:12:5F:4F:9C:4B:F6:D7:4B");
    }


}