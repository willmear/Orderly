package uk.ac.bham.teamproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.ac.bham.teamproject.web.rest.TestUtil;

class GoogleCalendarAccessTokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GoogleCalendarAccessToken.class);
        GoogleCalendarAccessToken googleCalendarAccessToken1 = new GoogleCalendarAccessToken();
        googleCalendarAccessToken1.setId(1L);
        GoogleCalendarAccessToken googleCalendarAccessToken2 = new GoogleCalendarAccessToken();
        googleCalendarAccessToken2.setId(googleCalendarAccessToken1.getId());
        assertThat(googleCalendarAccessToken1).isEqualTo(googleCalendarAccessToken2);
        googleCalendarAccessToken2.setId(2L);
        assertThat(googleCalendarAccessToken1).isNotEqualTo(googleCalendarAccessToken2);
        googleCalendarAccessToken1.setId(null);
        assertThat(googleCalendarAccessToken1).isNotEqualTo(googleCalendarAccessToken2);
    }
}
