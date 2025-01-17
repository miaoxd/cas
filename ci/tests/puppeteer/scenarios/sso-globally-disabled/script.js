const puppeteer = require('puppeteer');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.goto(page, "https://localhost:8443/cas/login");
    await cas.loginWith(page);
    await cas.assertCookie(page);

    await cas.goto(page, "https://localhost:8443/cas/login");
    await page.waitForTimeout(1000);
    await cas.assertVisibility(page, '#username');

    await cas.assertVisibility(page, 'li #CASServerOne');
    await cas.click(page, "li #CASServerOne");
    await page.waitForNavigation();

    await page.waitForTimeout(2000);
    await cas.screenshot(page);
    await cas.loginWith(page);
    await page.waitForTimeout(1000);

    let result = new URL(page.url());
    console.log(result.searchParams.toString());

    await cas.goto(page, "https://localhost:8443/cas/login");
    await page.waitForTimeout(1000);
    await cas.assertVisibility(page, '#username');
    
    await browser.close();
})();
