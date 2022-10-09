package eu.wodrobina.hibernatecompedium;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import eu.wodrobina.hibernatecompedium.product.LazyProduct;
import eu.wodrobina.hibernatecompedium.product.LazyProductRepository;
import eu.wodrobina.hibernatecompedium.review.Review;
import eu.wodrobina.hibernatecompedium.review.ReviewRepository;

@SpringBootTest
class SingleLazyProductTests {

    final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Autowired
    private LazyProductRepository lazyProductRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @Test
    @Transactional
    void test01() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        LazyProduct product = createLazyProduct(savedReviews);

        //WHEN
        Optional<LazyProduct> byId = lazyProductRepository.findById(product.getId());

        //THEN
        byId.get().getReviews()
            .forEach(Review::getText);
    }

    @Test
    @Transactional
    void test02() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        LazyProduct product = createLazyProduct(savedReviews);

        //WHEN //THEN
        Optional<LazyProduct> byId = lazyProductRepository.findById(product.getId());

    }

    @Test
    @Transactional
    void test03() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        LazyProduct product = createLazyProduct(savedReviews);

        //WHEN
        Iterable<LazyProduct> all = lazyProductRepository.findAll();

        //THEN
        all.forEach(lazyProduct->lazyProduct.getReviews()
            .forEach(Review::getText));
    }

    @Test
    @Transactional
    void test04() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        LazyProduct product = createLazyProduct(savedReviews);

        //WHEN//THEN
        Iterable<LazyProduct> all = lazyProductRepository.findAll();
    }

    private LazyProduct createLazyProduct(Iterable<Review> savedReviews) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
                LazyProduct item = new LazyProduct("item");
                savedReviews.forEach(item::addReview);
                return lazyProductRepository.save(item);
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
