const puppeteer = require('puppeteer');
const cas = require('../../cas.js');
const assert = require("assert");

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.goto(page, "https://localhost:8443/cas/login?service=https://apereo.github.io");
    await cas.loginWith(page, "casuser", "Mellon");
    const url = await page.url();
    console.log(`Page url: ${url}`);
    await cas.assertTicketParameter(page);
    await browser.close();

    const baseUrl = "https://localhost:8443/cas/actuator/registeredServices";
    await cas.doGet(baseUrl, res => {
        assert(res.status === 200);
        const length = res.data[1].length;
        console.log(`Services found: ${length}`);
        assert(length === 1);
        res.data[1].forEach(service => {
            assert(service.id === 1);
            assert(service.name === "Sample");
        });
    }, err => {
        throw err;
    }, {
        'Content-Type': 'application/json'
    });
})();
