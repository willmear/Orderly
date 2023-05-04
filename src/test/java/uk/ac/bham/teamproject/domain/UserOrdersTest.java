package uk.ac.bham.teamproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.ac.bham.teamproject.web.rest.TestUtil;

class UserOrdersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserOrders.class);
        UserOrders userOrders1 = new UserOrders();
        userOrders1.setId(1L);
        UserOrders userOrders2 = new UserOrders();
        userOrders2.setId(userOrders1.getId());
        assertThat(userOrders1).isEqualTo(userOrders2);
        userOrders2.setId(2L);
        assertThat(userOrders1).isNotEqualTo(userOrders2);
        userOrders1.setId(null);
        assertThat(userOrders1).isNotEqualTo(userOrders2);
    }
}
