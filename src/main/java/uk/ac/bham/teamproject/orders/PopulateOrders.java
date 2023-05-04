package uk.ac.bham.teamproject.orders;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// THIS IS A THROWAWAY CLASS
// ONLY REQUIRED FOR THE MVP TO CREATE CHARTS

@Configuration
@AutoConfiguration
public class PopulateOrders {

    private static final Logger log = LoggerFactory.getLogger(PopulateOrders.class);

    @Bean
    CommandLineRunner initDatabase(OrderRepository repository) {
        return args -> {
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(1),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(20),
                        Float.valueOf(50),
                        "Product 1"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(2),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(20),
                        Float.valueOf(50),
                        "Product 1"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(3),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(20),
                        Float.valueOf(50),
                        "Product 1"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(4),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(5),
                        Float.valueOf(7),
                        "Product 2"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(5),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(5),
                        Float.valueOf(7),
                        "Product 2"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(6),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(5),
                        Float.valueOf(7),
                        "Product 2"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(7),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(5),
                        Float.valueOf(7),
                        "Product 2"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(8),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(5),
                        Float.valueOf(7),
                        "Product 2"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(9),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(10),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(11),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(12),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(13),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(14),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(15),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(16),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(6),
                        Float.valueOf(15),
                        "Product 3"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(17),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(3),
                        Float.valueOf(20),
                        "Product 4"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(18),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(3),
                        Float.valueOf(20),
                        "Product 4"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(19),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(3),
                        Float.valueOf(20),
                        "Product 4"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(20),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(17),
                        Float.valueOf(32),
                        "Product 5"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(21),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(17),
                        Float.valueOf(32),
                        "Product 5"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(22),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(17),
                        Float.valueOf(32),
                        "Product 5"
                    )
                )
            );
            log.info(
                "Preloading " +
                repository.save(
                    new Order(
                        Long.valueOf(23),
                        ".",
                        ".",
                        LocalDate.parse("10-10-10"),
                        LocalDate.parse("10-10-10"),
                        Long.valueOf(5),
                        Integer.valueOf(5),
                        Float.valueOf(17),
                        Float.valueOf(32),
                        "Product 5"
                    )
                )
            );
        };
    }
}
