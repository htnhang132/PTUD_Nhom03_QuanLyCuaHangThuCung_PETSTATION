package com.petstation.gui.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class MenuModelTest {
    @Test
    void hasFiveGroupsInOrder() {
        assertThat(MenuModel.groups().stream().map(MenuModel.Group::label).toList())
                .containsExactly("Hệ thống", "Danh mục", "Tìm kiếm", "Xử lý", "Thống kê");
    }

    @Test
    void mnemonicsAreUnique() {
        List<Character> keys = MenuModel.groups().stream().map(MenuModel.Group::mnemonic).toList();
        assertThat(keys).doesNotHaveDuplicates();
    }

    @Test
    void everyMenuItemIsRegisteredAndNoScreenAppearsInTwoGroups() {
        Set<ScreenId> seen = EnumSet.noneOf(ScreenId.class);
        for (MenuModel.Group g : MenuModel.groups()) {
            for (MenuModel.Item item : g.items()) {
                if (item.isSeparator()) continue;
                assertThat(seen.add(item.screen())).as("trùng %s", item.screen()).isTrue();
            }
        }
        ScreenRegistry registry = new ScreenRegistry(null, ignored -> {});
        for (ScreenId id : seen) assertThat(registry.definition(id)).as(id.name()).isNotNull();
    }

    @Test
    void everyScreenIsRegistered() {
        ScreenRegistry registry = new ScreenRegistry(null, ignored -> {});
        for (ScreenId id : ScreenId.values())
            assertThat(registry.definition(id)).as(id.name()).isNotNull();
    }

    @Test
    void searchGroupHasNoCatalogOrProcessScreens() {
        Set<ScreenId> search =
                MenuModel.groups().get(2).items().stream()
                        .filter(i -> !i.isSeparator())
                        .map(MenuModel.Item::screen)
                        .collect(Collectors.toSet());
        assertThat(search)
                .contains(
                        ScreenId.ORDERS,
                        ScreenId.BATCH_STOCK,
                        ScreenId.SPA_DAILY,
                        ScreenId.HOTEL_MAP)
                .doesNotContain(ScreenId.POS, ScreenId.CUSTOMERS, ScreenId.SPA, ScreenId.HOTEL);
    }

    @Test
    void productCatalogHostsPriceHistoryTab() {
        var labels = MenuModel.groups().get(1).items().stream().map(MenuModel.Item::label).toList();
        assertThat(labels).anyMatch(l -> l.startsWith("Sản phẩm"));
        assertThat(labels).noneMatch(l -> l.contains("▸ Lịch sử giá"));
    }
}
