package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }


    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 3;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("span[data-test-id=city] input").setValue(validUser.getCity());
        $("span[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("span[data-test-id=date] input").setValue(firstMeetingDate);
        $("span[data-test-id=name] input").setValue(validUser.getName());
        $("span[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Запланировать")).click();
        $(byText( "Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=\"success-notification\"] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate))
                .shouldBe(visible);
        $("span[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("span[data-test-id=date] input").setValue(secondMeetingDate);
        $$("button").find(exactText("Запланировать")).click();
        $("[data-test-id=\"replan-notification\"] .notification__content")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"))
                .shouldBe(visible);
        $$("button").find(exactText("Перепланировать")).click();
        $("[data-test-id= 'success-notification']")
                .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Invalid phone number")
    void shouldNotSuccessfulPlanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 3;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("span[data-test-id=city] input").setValue(validUser.getCity());
        $("span[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("span[data-test-id=date] input").setValue(firstMeetingDate);
        $("span[data-test-id=name] input").setValue(validUser.getName());
        $("span[data-test-id=phone] input").setValue("0 000 000 00 00");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Запланировать")).click();
        $(byText( "Неверный формат номера телефона!")).shouldBe(visible);
    }
}

