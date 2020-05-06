import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebClientAPI {

    private WebClient webClient;

    WebClientAPI() {
        this.webClient = WebClient.builder().baseUrl("http://localhost:8080/products")
                .build();
    }

    private Mono<ResponseEntity<Product>> postNewProduct() {
        return webClient
                .post()
                .body(Mono.just(new Product(null, "Chai Latte", 35.0)), Product.class)
                .exchange()
                .flatMap(response -> response.toEntity(Product.class))
                .doOnSuccess(success -> System.out.println("yayayayyaya " + success));
    }

    private Flux<Product> getAllProducts() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(product -> System.out.println("WebClientAPI.gettAllProducts " + product));
    }


    private Mono<Product> getProduct(String id) {
        return webClient
                .get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnSuccess(product -> System.out.println(product));

    }

    private Mono<Void> deleteProduct(String id) {
        return webClient
                .delete()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(product -> System.out.println(product));

    }

    private Flux<ProductEvent> getAllEvents() {
        return webClient
                .get()
                .uri("/events")
                .retrieve()
                .bodyToFlux(ProductEvent.class);
    }

    public static void main(String[] args) {
        WebClientAPI webClientAPI = new WebClientAPI();
        webClientAPI.postNewProduct().
                thenMany(webClientAPI.getAllProducts())
                .take(1)
                .flatMap(p -> webClientAPI.deleteProduct(p.getId()))
        .thenMany(webClientAPI.getAllProducts())
        .thenMany(webClientAPI.getAllEvents())
        .subscribe(System.out::println);
    }


}
