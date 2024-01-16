package org.ldcgc.backend;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("LDCGC Test Suite")
@SelectPackages({"org.ldcgc.backend.controller", "org.ldcgc.backend.service"})
public class TestSuite {
}
