package eu.wodrobina.hibernatecompedium.product;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import eu.wodrobina.hibernatecompedium.review.Review;

@Entity
public class EagerProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "eager_product_id")
    private Set<Review> reviews = new HashSet<>();

    protected EagerProduct() {
    }

    public EagerProduct(String name) {
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
        if (!(o instanceof EagerProduct)) {
            return false;
        }
        return id != null && id.equals(((EagerProduct) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
