package uk.ac.bham.teamproject.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.bham.teamproject.IntegrationTest;

/**
 * Test class for the FinanceResource REST controller.
 *
 * @see FinanceResource
 */
@IntegrationTest
class FinanceResourceIT {

    private MockMvc restMockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        FinanceResource financeResource = new FinanceResource(null);
        restMockMvc = MockMvcBuilders.standaloneSetup(financeResource).build();
    }

    /**
     * Test countByProduct
     */
    @Test
    void testCountByProduct() throws Exception {
        restMockMvc.perform(get("/api/finance-resource/count-by-product")).andExpect(status().isOk());
    }
}
