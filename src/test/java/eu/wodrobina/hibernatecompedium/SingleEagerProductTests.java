package eu.wodrobina.hibernatecompedium;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import eu.wodrobina.hibernatecompedium.product.EagerProduct;
import eu.wodrobina.hibernatecompedium.product.EagerProductRepository;
import eu.wodrobina.hibernatecompedium.review.Review;
import eu.wodrobina.hibernatecompedium.review.ReviewRepository;

@SpringBootTest
class SingleEagerProductTests {

    final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Autowired
    private EagerProductRepository eagerProductRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @Test
    @Transactional
    @DisplayName("Create product and then search by its ID. Retrieve linked objects data")
    void test01() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        EagerProduct product = createEagerProduct(savedReviews);

        //WHEN
        Optional<EagerProduct> byId = eagerProductRepository.findById(product.getId());

        //THEN
        byId.get().getReviews()
            .forEach(Review::getText);
    }

    @Test
    @Transactional
    @DisplayName("Create product and then search by its ID.")
    void test02() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        EagerProduct product = createEagerProduct(savedReviews);

        //WHEN //THEN
        Optional<EagerProduct> byId = eagerProductRepository.findById(product.getId());

    }

    @Test
    @Transactional
    @DisplayName("Create product and then search all objects. Retrieve linked objects data")
    void test03() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        EagerProduct product = createEagerProduct(savedReviews);

        //WHEN
        Iterable<EagerProduct> all = eagerProductRepository.findAll();

        //THEN
        all.forEach(eagerProduct->eagerProduct.getReviews()
            .forEach(Review::getText));
    }

    @Test
    @Transactional
    @DisplayName("Create product and then search all objects")
    void test04() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        EagerProduct product = createEagerProduct(savedReviews);

        //WHEN//THEN
        Iterable<EagerProduct> all = eagerProductRepository.findAll();
    }

    private EagerProduct createEagerProduct(Iterable<Review> savedReviews) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
                EagerProduct item = new EagerProduct("item");
                savedReviews.forEach(item::addReview);
                return eagerProductRepository.save(item);
            }, executor)
            .get();
    }

    private Iterable<Review> createReviews() throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> reviewRepository.saveAll(List.of(
                new Review("first review"),
                new Review("second review"),
                new Review("third review")
            )), executor)
            .get();
    }

}
