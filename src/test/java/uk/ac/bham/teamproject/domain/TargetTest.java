package uk.ac.bham.teamproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.ac.bham.teamproject.web.rest.TestUtil;

class TargetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Target.class);
        Target target1 = new Target();
        target1.setId(1L);
        Target target2 = new Target();
        target2.setId(target1.getId());
        assertThat(target1).isEqualTo(target2);
        target2.setId(2L);
        assertThat(target1).isNotEqualTo(target2);
        target1.setId(null);
        assertThat(target1).isNotEqualTo(target2);
    }
}
