package uk.ac.bham.teamproject.web.rest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.bham.teamproject.orders.OrderRepository;

/**
 * FinanceResource controller
 */
@RestController
@RequestMapping("/api")
@AutoConfiguration
public class FinanceResource {

    private final OrderRepository orderRepository;
    private final Logger log = LoggerFactory.getLogger(FinanceResource.class);

    public FinanceResource(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * GET countByProduct
     */
    @GetMapping("/count-by-product")
    public Map<String, BigInteger> countByProduct() {
        Map<String, BigInteger> result = new HashMap<>();
        List<Object[]> counts = orderRepository.countByProduct();
        for (Object[] row : counts) {
            result.put((String) row[0], (BigInteger) row[1]);
        }
        return result;
    }
}
