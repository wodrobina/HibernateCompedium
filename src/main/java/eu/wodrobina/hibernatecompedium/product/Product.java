package eu.wodrobina.hibernatecompedium.product;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import eu.wodrobina.hibernatecompedium.review.Review;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    protected Product() {
    }

    public Product(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addReview(Review review){
        reviews.add(review);
    }

    public void removeReview(Review review){
        reviews.remove(review);
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
