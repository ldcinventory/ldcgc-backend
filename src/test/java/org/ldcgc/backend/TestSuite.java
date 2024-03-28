package org.ldcgc.backend;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("LDCGC Test Suite")
@SelectPackages({"org.ldcgc.backend.controller", "org.ldcgc.backend.service"})
public class TestSuite {

    /* this is just a piece of code to check content when using mockMvc.perform:
       mockMvc.perform(postRequest(requestRoot, ERole.ROLE_ADMIN) <<- this line can vary depending on the endpoint called
            .andReturn().getResponse().getContentAsString()
     */

}
