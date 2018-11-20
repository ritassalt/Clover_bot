package main;

import java.util.HashMap;

public class Shop {
    private static HashMap<String, Integer> items;

    public Shop() { fillShop(); }

    private void fillShop() {
        if (items == null) {
            items = new HashMap<String, Integer>();
            items.put("extralife", 200);
        }
    }

    public String buy(UserDataObject user, String item) {
        if (!items.containsKey(item)) {
            return "Данного товара нет в магазине!";
        }
        int price = items.get(item);
        if (user.getScores() < price) {
            return "Недостаточно очков для совершения покупки!";
        }
        user.updateBonuses(item, user.getBonusCount(item) + 1, price);
        return "Покупка совершена!";
    }
}
