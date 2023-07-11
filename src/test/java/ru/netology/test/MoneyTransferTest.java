package ru.netology.test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        var loginPage= open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldTransferBetweenOwnCards() {

        var firstCard = DataHelper.getFirstCardInfo();
        var secondCard = DataHelper.getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCard);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCard);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCard);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }



    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {

            var firstCard = DataHelper.getFirstCardInfo();
            var secondCard = DataHelper.getSecondCardInfo();
            var firstCardBalance =  dashboardPage.getCardBalance(firstCard);
            var secondCardBalance = dashboardPage.getCardBalance(secondCard);
            var amount = DataHelper.generateInvalidAmount(secondCardBalance);
            var transferPage = dashboardPage.selectCardToTransfer(firstCard);
            transferPage.makeTransfer(String.valueOf(amount),secondCard);
            transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
            var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCard);
            var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCard);
            assertEquals(firstCardBalance, actualBalanceFirstCard);
            assertEquals(secondCardBalance, actualBalanceSecondCard);

    }

}
