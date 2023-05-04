package uk.ac.bham.teamproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.ac.bham.teamproject.web.rest.TestUtil;

class BusinessesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Businesses.class);
        Businesses businesses1 = new Businesses();
        businesses1.setId(1L);
        Businesses businesses2 = new Businesses();
        businesses2.setId(businesses1.getId());
        assertThat(businesses1).isEqualTo(businesses2);
        businesses2.setId(2L);
        assertThat(businesses1).isNotEqualTo(businesses2);
        businesses1.setId(null);
        assertThat(businesses1).isNotEqualTo(businesses2);
    }
}
