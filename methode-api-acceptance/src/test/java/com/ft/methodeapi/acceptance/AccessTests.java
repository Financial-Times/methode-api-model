package com.ft.methodeapi.acceptance;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome=true, tags = "@Testing", format = { "pretty", "html:target/cucumber-html-report", "json:target/cucumber-json-report/access.json" })
public class AccessTests {
}
