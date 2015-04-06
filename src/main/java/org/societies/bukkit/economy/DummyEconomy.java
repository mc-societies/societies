package org.societies.bukkit.economy;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

/**
 * Represents a DummyEconomy
 */
@SuppressWarnings("deprecation")
public class DummyEconomy extends AbstractEconomy {

    private NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "Dummy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return currencyInstance.format(v);
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public double getBalance(String s) {
        return 0;
    }

    @Override
    public double getBalance(String s, String s1) {
        return 0;
    }

    @Override
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return response();
    }

    private EconomyResponse response() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "Dummy!");
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return response();
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return response();
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return response();
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return response();
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return response();
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return response();
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return response();
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return response();
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return response();
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return response();
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return response();
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }
}
