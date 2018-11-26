package main;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShopTest {

    private Shop shop = new Shop();
    private UserDataObject user = new UserDataObject("123", "abc", 200, 0);

    @Test
    public void testBuy() {
        assertEquals("Данного товара нет в магазине!", shop.buy(user, "abc"));
        assertEquals("Покупка совершена!", shop.buy(user, "extralife"));
        assertEquals("Недостаточно очков для совершения покупки!", shop.buy(user, "extralife"));
    }
}