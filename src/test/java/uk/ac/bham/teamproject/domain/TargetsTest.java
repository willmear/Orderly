package uk.ac.bham.teamproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.ac.bham.teamproject.web.rest.TestUtil;

class TargetsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Targets.class);
        Targets targets1 = new Targets();
        targets1.setId(1L);
        Targets targets2 = new Targets();
        targets2.setId(targets1.getId());
        assertThat(targets1).isEqualTo(targets2);
        targets2.setId(2L);
        assertThat(targets1).isNotEqualTo(targets2);
        targets1.setId(null);
        assertThat(targets1).isNotEqualTo(targets2);
    }
}
